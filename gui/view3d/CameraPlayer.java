/*
 *   CameraPlayer.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package gui.view3d;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gui.EditorFrame;
import gui.view3d.GameTrack.Seg;
import utils.SegmentVector;
import utils.circuit.Camera;
import utils.circuit.MainTrack;
import utils.circuit.StartingGrid;
import utils.undo.Undo;
import utils.undo.UndoCameras;

/**
 * Plays a lap as the game's road views show it: a car (not drawn) drives round
 * the track at a steady speed, and the view looks from the track camera the game
 * uses for the segment it's on (the last one in the list whose "fov start" to "fov end"
 * covers it), turning to follow it. Where no camera covers the track, the game
 * looks from high above it. A strip shows which camera covers each part of the
 * lap, and the camera showing can be edited as it plays.
 *
 * Driving is the same lap seen from the driver's seat, or from behind the car.
 * It starts as a race does: from pole position on the grid, standing until the
 * start lights go out, then pulling away to the path.
 */
class CameraPlayer
{
	private static final String[]	VIEWS		= { "zoom (TV director)", "no zoom" };
	private static final String[]	PATHS		= { "racing line", "center line" };
	private static final String[]	DRIVE_VIEWS	= { "in car", "chase" };
	private static final String		NONE		= "(none)";
	// the game's road views: the zoom keeps the car this size (locfovy), the other a fixed field of view
	private static final double		ZOOM_SIZE	= 9;
	private static final double		NO_ZOOM_FOV	= 30;
	// where the game looks from when no camera films (grWrldX * 0.5, grWrldY * 0.6, 120 m up)
	private static final double		ABOVE		= 120;
	// driving: the driver's eyes above the road, how far ahead they look, and the field of view
	private static final double		EYE_HEIGHT	= 1.1;
	private static final double		LOOK_AHEAD	= 15;
	private static final double		DRIVER_FOV	= 60;
	// the chase view: how far behind the car and how high, and how far ahead of it it looks
	private static final double		CHASE_BACK	= 8;
	private static final double		CHASE_UP	= 2.8;
	private static final double		CHASE_AHEAD	= 12;
	// the start: the game's race clock starts at -2 s, after a pause while it waits
	// for the player (here this long); then the car pulls away this hard, and
	// reaches its path this far from its grid place
	private static final double		START_CLOCK	= -2;
	private static final double		START_WAIT	= 1;
	private static final double		ACCELERATION	= 7;
	private static final double		MERGE		= 150;
	// the grid when neither the track nor the race says: Quick Race's
	private static final int		GRID_ROWS	= 2;
	private static final double		GRID_TO_START	= 25;
	private static final Color[]	COLORS		= { new Color(0x378ADD), new Color(0x1D9E75), new Color(0xD85A30),
		new Color(0x7F77DD), new Color(0xBA7517), new Color(0xD4537E), new Color(0x639922), new Color(0x888780) };

	private final EditorFrame		editorFrame;
	private final Track3DWindow		window;
	private final Track3DRenderer	renderer;

	/** the playback bar, under the view */
	final JPanel					bar				= new JPanel(new BorderLayout());
	/** the camera's fields, beside the view */
	final JPanel					editPanel		= new JPanel(new GridBagLayout());

	private final JButton			pauseButton		= new JButton("Pause");
	private final JButton			restartButton	= new JButton("Restart");
	private final JButton			nextButton		= new JButton("Next camera");
	private final JComboBox<String>	viewBox			= new JComboBox<String>(VIEWS);
	private final JComboBox<String>	driveViewBox	= new JComboBox<String>(DRIVE_VIEWS);
	private final JComboBox<String>	pathBox			= new JComboBox<String>(PATHS);
	private final JSlider			speedSlider		= new JSlider(20, 300, 160);
	private final JLabel			speedLabel		= new JLabel();
	private final JCheckBox			editBox			= new JCheckBox("Edit camera");
	private final Strip				strip			= new Strip();
	private final JLabel			statusLabel		= new JLabel(" ");

	private final JLabel			titleLabel		= new JLabel();
	private final JComboBox<String>	segmentBox		= new JComboBox<String>();
	private final JTextField		toStartField	= new JTextField(7);
	private final JComboBox<String>	unitBox			= new JComboBox<String>();
	private final JTextField		toRightField	= new JTextField(7);
	private final JTextField		heightField		= new JTextField(7);
	private final JComboBox<String>	fovStartBox		= new JComboBox<String>();
	private final JComboBox<String>	fovEndBox		= new JComboBox<String>();
	private final JButton			moveButton		= new JButton("Move here");
	private final JButton			startButton		= new JButton("Start here");
	private final JButton			endButton		= new JButton("End here");
	private final JButton			addButton		= new JButton("Add camera here");
	private final JButton			deleteButton	= new JButton("Delete");
	private final JButton			undoButton		= new JButton("Undo");
	private final JButton			redoButton		= new JButton("Redo");

	private final Timer				timer			= new Timer(30, e -> tick());
	private boolean					active;
	// from the driver's seat, not the track's cameras
	private boolean					drive;
	private boolean					playing;
	private long					lastTick;
	// how far round the lap the car is
	private double					distance;
	// starting from the grid: the race's clock (0 is the start), NaN when not
	private double					clock			= Double.NaN;
	private double					waited;
	// the car's speed, in m/s, while it pulls away
	private double					speed;
	// how far it has driven from the grid, and its lane there (0 on the left edge, 1 on the right)
	private double					driven;
	private double					gridLane		= Double.NaN;
	private double					gridToStart;
	private GameTrack				track;
	// the camera filming each main segment
	private Camera[]				owners			= new Camera[0];
	private Camera					lastOwner;
	private RaceLine				raceLine;
	private GameTrack				raceLineTrack;
	// how the view was before playing, to go back to
	private double[]				savedView;
	// the camera in the edit panel, and the unit its "to start" shows in
	private Camera					edited;
	private String					shownUnit		= "m";
	private boolean					filling;
	private UndoCameras				pending;

	CameraPlayer(EditorFrame editorFrame, Track3DWindow window, Track3DRenderer renderer)
	{
		this.editorFrame = editorFrame;
		this.window = window;
		this.renderer = renderer;
		createBar();
		createEditPanel();
		bar.setVisible(false);
		editPanel.setVisible(false);
	}

	private void createBar()
	{
		viewBox.setLightWeightPopupEnabled(false);
		pathBox.setLightWeightPopupEnabled(false);
		pauseButton.setToolTipText("Pause or play (space)");
		pauseButton.addActionListener(e -> setPlaying(!playing));
		restartButton.setToolTipText("Back to the start line, or to the grid when driving");
		restartButton.addActionListener(e -> restart());
		nextButton.setToolTipText("Skip to where the next camera takes over");
		nextButton.addActionListener(e -> nextCamera());
		viewBox.setToolTipText("<html>Zoom: the TV director's view, zoomed so the car stays the same size.<br>No zoom: a fixed field of view.</html>");
		viewBox.addActionListener(e -> show());
		driveViewBox.setLightWeightPopupEnabled(false);
		driveViewBox.setToolTipText("From the driver's seat, or from behind the car");
		driveViewBox.addActionListener(e -> show());
		pathBox.setToolTipText("Where the car drives");
		pathBox.addActionListener(e -> show());
		speedSlider.setToolTipText("The car's speed, the same all round the lap");
		speedSlider.setPreferredSize(new Dimension(120, speedSlider.getPreferredSize().height));
		speedSlider.addChangeListener(e -> speedLabel.setText(speedSlider.getValue() + " km/h"));
		speedLabel.setText(speedSlider.getValue() + " km/h");
		editBox.setToolTipText("Edit the camera showing; editing pauses");
		editBox.addActionListener(e -> {
			endEdit();
			editPanel.setVisible(editBox.isSelected());
			if (editBox.isSelected())
				fillEdit(ownerAt(distance));
			window.layoutChanged();
		});

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
		controls.add(pauseButton);
		controls.add(restartButton);
		controls.add(nextButton);
		controls.add(new JLabel("View"));
		controls.add(viewBox);
		controls.add(driveViewBox);
		controls.add(new JLabel("Path"));
		controls.add(pathBox);
		controls.add(new JLabel("Speed"));
		controls.add(speedSlider);
		controls.add(speedLabel);
		controls.add(editBox);

		strip.setPreferredSize(new Dimension(100, 22));
		strip.setToolTipText("");
		statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 2, 6));

		bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
		bar.add(controls, BorderLayout.NORTH);
		JPanel stripPanel = new JPanel(new BorderLayout());
		stripPanel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
		stripPanel.add(strip, BorderLayout.CENTER);
		bar.add(stripPanel, BorderLayout.CENTER);
		bar.add(statusLabel, BorderLayout.SOUTH);
	}

	private void createEditPanel()
	{
		editPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY),
			BorderFactory.createEmptyBorder(6, 8, 6, 8)));
		for (JComboBox<String> box : java.util.Arrays.asList(segmentBox, unitBox, fovStartBox, fovEndBox))
			box.setLightWeightPopupEnabled(false);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		titleLabel.setFont(titleLabel.getFont().deriveFont(java.awt.Font.BOLD));
		editPanel.add(titleLabel, c);
		c.gridwidth = 1;
		row(c, "Segment", segmentBox);
		JPanel toStart = new JPanel(new BorderLayout(4, 0));
		toStart.add(toStartField, BorderLayout.CENTER);
		toStart.add(unitBox, BorderLayout.EAST);
		row(c, "To Start", toStart);
		row(c, "To Right", withUnit(toRightField));
		row(c, "Height", withUnit(heightField));
		row(c, "Fov Start", fovStartBox);
		row(c, "Fov End", fovEndBox);

		JPanel here = new JPanel(new java.awt.GridLayout(0, 1, 0, 3));
		here.add(moveButton);
		here.add(startButton);
		here.add(endButton);
		here.add(addButton);
		here.add(deleteButton);
		JPanel undo = new JPanel(new java.awt.GridLayout(1, 2, 4, 0));
		undo.add(undoButton);
		undo.add(redoButton);
		here.add(undo);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		c.insets = new Insets(8, 2, 2, 2);
		editPanel.add(here, c);
		// the rest of the height below
		c.gridy++;
		c.weighty = 1;
		editPanel.add(new JPanel(), c);

		segmentBox.setToolTipText("The segment the camera stands by");
		toStartField.setToolTipText("<html>How far along the segment from its start: in a curve an angle,<br>"
			+ "or in meters along the middle of the road (saved as an angle in degrees)</html>");
		unitBox.setToolTipText("The unit of To Start");
		toRightField.setToolTipText("From the right edge of the road: less than 0 is off the road on the right");
		heightField.setToolTipText("Above the ground");
		fovStartBox.setToolTipText("The first segment the camera films");
		fovEndBox.setToolTipText("The segment after the last one it films (the same as Fov Start films all the lap)");
		moveButton.setToolTipText("Put the camera by the car, keeping To Right and Height");
		startButton.setToolTipText("Make the camera film from the car's segment");
		endButton.setToolTipText("Make the camera film up to the end of the car's segment");
		addButton.setToolTipText("A new camera by the car, filming from its segment to where the next camera takes over");
		deleteButton.setToolTipText("Delete the camera");
		undoButton.setToolTipText("Undo the last change, as the editor's Undo does");
		redoButton.setToolTipText("Redo the last change undone, as the editor's Redo does");

		segmentBox.addActionListener(e -> segmentChanged());
		unitBox.addActionListener(e -> unitChanged());
		fovStartBox.addActionListener(e -> fovChanged());
		fovEndBox.addActionListener(e -> fovChanged());
		for (JTextField field : java.util.Arrays.asList(toStartField, toRightField, heightField))
		{
			field.getDocument().addDocumentListener(new DocumentListener()
			{
				public void insertUpdate(DocumentEvent e)
				{
					fieldChanged(field);
				}

				public void removeUpdate(DocumentEvent e)
				{
					fieldChanged(field);
				}

				public void changedUpdate(DocumentEvent e)
				{
					fieldChanged(field);
				}
			});
			field.addFocusListener(new FocusAdapter()
			{
				public void focusLost(FocusEvent e)
				{
					endEdit();
				}
			});
			field.addActionListener(e -> endEdit());
		}
		moveButton.addActionListener(e -> moveHere());
		startButton.addActionListener(e -> startHere());
		endButton.addActionListener(e -> endHere());
		addButton.addActionListener(e -> addHere());
		deleteButton.addActionListener(e -> delete());
		undoButton.addActionListener(e -> {
			endEdit();
			editorFrame.undo();
		});
		redoButton.addActionListener(e -> {
			endEdit();
			editorFrame.redo();
		});
	}

	private void row(GridBagConstraints c, String label, JComponent field)
	{
		c.gridy++;
		c.gridx = 0;
		c.weightx = 0;
		editPanel.add(new JLabel(label), c);
		c.gridx = 1;
		c.weightx = 1;
		editPanel.add(field, c);
	}

	private static JPanel withUnit(JTextField field)
	{
		JPanel panel = new JPanel(new BorderLayout(4, 0));
		panel.add(field, BorderLayout.CENTER);
		panel.add(new JLabel("m"), BorderLayout.EAST);
		return panel;
	}

	boolean isActive()
	{
		return active;
	}

	/**
	 * Drives (from the driver's seat) or plays the cameras, the next time it
	 * starts: the cameras' view, their editing and the next camera are only for
	 * the cameras.
	 */
	void setDrive(boolean drive)
	{
		this.drive = drive;
		for (JComponent c : java.util.Arrays.asList(viewBox, nextButton, editBox))
			c.setVisible(!drive);
		driveViewBox.setVisible(drive);
	}

	/** Starts playing from the start line, or stops and puts the view back as it was. */
	void setActive(boolean on)
	{
		if (on == active)
			return;
		RoadMesh mesh = renderer.getMesh();
		if (on)
		{
			savedView = mesh == null || mesh.isEmpty() ? null : renderer.camera.state(renderer.getHeightScale());
			active = true;
			distance = 0;
			lastOwner = null;
			trackChanged();
			restart();
			bar.setVisible(true);
			editPanel.setVisible(editBox.isSelected() && !drive);
			setPlaying(true);
			timer.start();
		}
		else
		{
			endEdit();
			timer.stop();
			active = false;
			clock = Double.NaN;
			gridLane = Double.NaN;
			renderer.setStartClock(Double.NaN);
			playing = false;
			bar.setVisible(false);
			editPanel.setVisible(false);
			if (savedView != null && mesh != null && !mesh.isEmpty())
				renderer.camera.setState(savedView, mesh, renderer.getHeightScale());
			savedView = null;
			window.requestRedraw();
		}
		window.layoutChanged();
	}

	void setPlaying(boolean play)
	{
		playing = play && track != null;
		pauseButton.setText(playing ? "Pause" : "Play");
		lastTick = System.nanoTime();
	}

	boolean isPlaying()
	{
		return playing;
	}

	/** The track changed (an edit, an undo, another track): follows it. */
	void trackChanged()
	{
		if (!active)
			return;
		track = GameTrack.build(editorFrame);
		raceLine = null;
		if (track == null)
		{
			owners = new Camera[0];
			setPlaying(false);
			statusLabel.setText("No track");
			strip.repaint();
			return;
		}
		distance = Math.max(0, Math.min(distance, track.length() - 0.01));
		owners = track.cameraOwners(cameras());
		fillSegmentNames();
		// the same camera, or one with its name after an undo, which puts back copies
		Camera camera = edited;
		if (camera != null && !cameras().contains(camera))
		{
			camera = null;
			for (Camera c : cameras())
				if (c.getName() != null && c.getName().equals(edited.getName()))
					camera = c;
		}
		lastOwner = ownerAt(distance);
		if (editBox.isSelected() && !drive)
			fillEdit(camera != null ? camera : lastOwner);
		show();
	}

	private Vector<Camera> cameras()
	{
		return editorFrame.getTrackData().getCameras();
	}

	private void tick()
	{
		long now = System.nanoTime();
		double seconds = Math.min(0.2, (now - lastTick) / 1e9);
		lastTick = now;
		if (!playing || track == null)
			return;
		double total = track.length();
		double target = speedSlider.getValue() / 3.6;
		if (!Double.isNaN(clock))
		{
			// waiting, then the lights, then pulling away
			if (waited < START_WAIT)
				waited += seconds;
			else
				clock += seconds;
			if (clock >= 0)
			{
				speed = Math.min(target, speed + ACCELERATION * seconds);
				distance = (distance + speed * seconds) % total;
				driven += speed * seconds;
			}
			renderer.setStartClock(clock);
		}
		else
			distance = (distance + target * seconds) % total;
		show();
	}

	/** Back to the start line, or, when driving, to pole position on the grid with the lights to come. */
	private void restart()
	{
		if (track == null)
			return;
		if (!drive)
		{
			clock = Double.NaN;
			gridLane = Double.NaN;
			renderer.setStartClock(Double.NaN);
			moveTo(0);
			return;
		}
		// as the game places the first car: the track's grid, or the race's
		StartingGrid grid = editorFrame.getTrackData().getStartingGrid();
		int rows = grid.getRows() == Integer.MAX_VALUE ? GRID_ROWS : Math.max(1, grid.getRows());
		gridToStart = Double.isNaN(grid.getDistanceToStart()) ? GRID_TO_START : grid.getDistanceToStart();
		String pole = grid.getPolePositionSide();
		if (pole == null || pole.isEmpty())
		{
			// the inside of the first turn
			pole = "right";
			for (Seg seg : track.segs)
				if (seg.type != GameTrack.TR_STR)
				{
					pole = seg.type == GameTrack.TR_LFT ? "left" : "right";
					break;
				}
		}
		// the first of the row's places, counted from the pole side
		double fromRight = 1.0 / (rows + 1);
		gridLane = pole.equals("left") ? fromRight : 1 - fromRight;
		double total = track.length();
		distance = ((total - gridToStart) % total + total) % total;
		clock = START_CLOCK;
		waited = 0;
		speed = 0;
		driven = 0;
		renderer.setStartClock(clock);
		show();
	}

	private void moveTo(double where)
	{
		if (track == null)
			return;
		double total = track.length();
		distance = (where % total + total) % total;
		show();
	}

	private void nextCamera()
	{
		if (track == null || owners.length == 0)
			return;
		Seg seg = track.segAt(distance);
		Camera now = owners[seg.id];
		for (int i = 1; i <= owners.length; i++)
		{
			int j = (seg.id + i) % owners.length;
			if (owners[j] != now)
			{
				moveTo(track.segs.get(j).lgfromstart);
				return;
			}
		}
	}

	private Camera ownerAt(double where)
	{
		return track == null || owners.length == 0 ? null : owners[track.segAt(where).id];
	}

	/** @return the racing line, worked out once for the track as the raceline layer does */
	private RaceLine raceLine()
	{
		if (raceLine == null || raceLineTrack != track)
		{
			MainTrack main = editorFrame.getTrackData().getMainTrack();
			double ext = Double.isNaN(main.getRacelineExt()) ? 2.0 : main.getRacelineExt();
			double in = Double.isNaN(main.getRacelineInt()) ? 2.0 : main.getRacelineInt();
			raceLine = new RaceLine(track, ext, in);
			raceLineTrack = track;
		}
		return raceLine;
	}

	/** @return where the car is this far round: x, y and the ground's height */
	private float[] place(double where)
	{
		Seg seg = track.segAt(where);
		float along = (float) Math.max(0, Math.min(where - seg.lgfromstart, seg.length));
		float toStart = seg.type == GameTrack.TR_STR ? along : along / seg.radius;
		// the racing line's lane: 0 on the left edge, 1 on the right
		double lane = pathBox.getSelectedIndex() == 0 ? raceLine().textureOffset(where) + 0.5 : 0.5;
		if (!Double.isNaN(gridLane))
		{
			// from the grid place to the path, smoothly over the first meters
			double total = track.length();
			double ahead = ((where - distance) % total + total * 1.5) % total - total / 2;
			double t = Math.max(0, Math.min(1, (driven + ahead) / MERGE));
			t = t * t * (3 - 2 * t);
			lane = gridLane + (lane - gridLane) * t;
		}
		float toRight = (float) (seg.width * (1 - lane));
		float[] xy = GameTrack.local2Global(seg, toStart, toRight);
		return new float[] { xy[0], xy[1], track.heightL(seg, toStart, toRight) };
	}

	/** Puts the car where it is, and looks at it from the camera the game uses there. */
	private void show()
	{
		RoadMesh mesh = renderer.getMesh();
		if (!active || track == null || mesh == null || mesh.isEmpty())
			return;
		double total = track.length();
		double scale = renderer.getHeightScale();
		float[] car = place(distance);
		Seg seg = track.segAt(distance);
		if (drive && driveViewBox.getSelectedIndex() == 1)
		{
			// behind the car on its path, a little up, looking down the road past it
			float[] back = place(((distance - CHASE_BACK) % total + total) % total);
			float[] far = place((distance + CHASE_AHEAD) % total);
			double[] eye = { back[0], back[1], back[2] * scale + CHASE_UP };
			double dx = far[0] - eye[0], dy = far[1] - eye[1], dz = far[2] * scale + EYE_HEIGHT - eye[2];
			renderer.camera.lookFrom(eye, Math.atan2(dy, dx), Math.atan2(dz, Math.hypot(dx, dy)),
				Math.sqrt(dx * dx + dy * dy + dz * dz), DRIVER_FOV, mesh);
		}
		else if (drive)
		{
			// from the driver's seat, looking down the road ahead
			float[] far = place((distance + LOOK_AHEAD) % total);
			double[] eye = { car[0], car[1], car[2] * scale + EYE_HEIGHT };
			double dx = far[0] - eye[0], dy = far[1] - eye[1], dz = far[2] * scale + EYE_HEIGHT - eye[2];
			renderer.camera.lookFrom(eye, Math.atan2(dy, dx), Math.atan2(dz, Math.hypot(dx, dy)),
				Math.sqrt(dx * dx + dy * dy + dz * dz), DRIVER_FOV, mesh);
		}
		if (drive)
		{
			String start = Double.isNaN(clock) || clock >= 1 ? "" : waited < START_WAIT ? "On pole, " + text(gridToStart) + " m before the line     "
				: clock < 0 ? String.format(Locale.US, "Start in %.1f s     ", -clock) : "Go!     ";
			statusLabel.setText(String.format(Locale.US, "%sSegment: %s     %,.0f m of %,.0f m", start, seg.name, distance, total));
			strip.repaint();
			window.requestRedraw();
			return;
		}
		Camera camera = owners[seg.id];
		double[] eye;
		if (camera != null)
		{
			float[] e = track.cameraEye(camera);
			// the exaggeration lifts the ground under it, not its height above the ground
			eye = new double[] { e[0], e[1], e[2] * scale + e[3] };
		}
		else
			eye = new double[] { mesh.minX + 0.5 * (mesh.maxX - mesh.minX), mesh.minY + 0.6 * (mesh.maxY - mesh.minY),
				mesh.minZ * scale + ABOVE };
		boolean zoom = viewBox.getSelectedIndex() == 0;
		// the game's no zoom view looks level from the camera, at the car's place
		double[] target = { car[0], car[1], zoom || camera == null ? car[2] * scale + 0.6 : eye[2] };
		double dx = target[0] - eye[0], dy = target[1] - eye[1], dz = target[2] - eye[2];
		double dd = Math.sqrt(dx * dx + dy * dy + dz * dz);
		double fov = zoom ? Math.max(1, Math.min(90, Math.toDegrees(Math.atan2(ZOOM_SIZE, dd)))) : NO_ZOOM_FOV;
		renderer.camera.lookFrom(eye, Math.atan2(dy, dx), Math.atan2(dz, Math.hypot(dx, dy)), Math.max(1, dd), fov, mesh);

		statusLabel.setText(String.format(Locale.US, "Camera: %s     Segment: %s     %,.0f m of %,.0f m",
			camera == null ? "none, looking from above" : camera.getName(), seg.name, distance, total));
		// the next camera, in the panel
		if (camera != lastOwner)
		{
			lastOwner = camera;
			if (editBox.isSelected() && pending == null)
				fillEdit(camera);
		}
		strip.repaint();
		window.requestRedraw();
	}

	private void fillSegmentNames()
	{
		SegmentVector segments = editorFrame.getTrackData().getSegments();
		filling = true;
		for (JComboBox<String> box : java.util.Arrays.asList(segmentBox, fovStartBox, fovEndBox))
		{
			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
			model.addElement(NONE);
			for (int i = 0; i < segments.size(); i++)
				model.addElement(segments.get(i).getName());
			box.setModel(model);
		}
		filling = false;
	}

	/** Shows a camera in the edit panel, or that there's none here. */
	private void fillEdit(Camera camera)
	{
		endEdit();
		edited = camera;
		refreshEdit();
	}

	private void refreshEdit()
	{
		filling = true;
		Camera camera = edited;
		boolean has = camera != null && track != null;
		titleLabel.setText(camera == null ? "No camera here" : "Camera " + camera.getName());
		for (JComponent field : java.util.Arrays.asList(segmentBox, toStartField, unitBox, toRightField, heightField, fovStartBox,
			fovEndBox, moveButton, startButton, endButton, deleteButton))
			field.setEnabled(has);
		addButton.setEnabled(track != null);
		if (has)
		{
			select(segmentBox, camera.getSegment());
			select(fovStartBox, camera.getFovStart());
			select(fovEndBox, camera.getFovEnd());
			toRightField.setText(text(camera.getToRight()));
			heightField.setText(text(camera.getHeight()));
			Seg seg = cameraSeg(camera);
			if (seg.type == GameTrack.TR_STR)
			{
				unitBox.setModel(new DefaultComboBoxModel<String>(new String[] { "m" }));
				unitBox.setEnabled(false);
				shownUnit = "m";
				toStartField.setText(text(camera.getToStart()));
			}
			else
			{
				unitBox.setModel(new DefaultComboBoxModel<String>(new String[] { "deg", "rad", "m" }));
				// as the game reads it: degrees when saved in degrees, otherwise radians
				boolean degrees = !camera.isToStartUnitsKnown() || "deg".equals(camera.getToStartUnits());
				shownUnit = degrees ? "deg" : "rad";
				unitBox.setSelectedItem(shownUnit);
				double angle = track.cameraToStart(camera, seg);
				toStartField.setText(Double.isNaN(camera.getToStart()) ? "" : text(degrees ? Math.toDegrees(angle) : angle));
			}
		}
		else
		{
			for (JTextField field : java.util.Arrays.asList(toStartField, toRightField, heightField))
				field.setText("");
		}
		for (JTextField field : java.util.Arrays.asList(toStartField, toRightField, heightField))
			field.setBackground(UIManager.getColor("TextField.background"));
		filling = false;
		strip.repaint();
	}

	private static void select(JComboBox<String> box, String name)
	{
		box.setSelectedItem(name);
		if (name == null || !name.equals(box.getSelectedItem()))
			box.setSelectedIndex(0);
	}

	private static String text(double value)
	{
		if (Double.isNaN(value))
			return "";
		String text = String.format(Locale.US, "%.3f", value);
		text = text.replaceAll("0+$", "").replaceAll("\\.$", "");
		return "-0".equals(text) ? "0" : text;
	}

	/** @return the segment a camera stands by, as the game finds it: the first one when it isn't in the track */
	private Seg cameraSeg(Camera camera)
	{
		Seg seg = track.firstStep(camera.getSegment());
		return seg != null ? seg : track.segs.get(0);
	}

	private static String picked(JComboBox<String> box)
	{
		Object item = box.getSelectedItem();
		return item == null || NONE.equals(item) ? null : item.toString();
	}

	/** Starts an undo step for the changes from here, and pauses. */
	private void beginEdit()
	{
		if (pending == null)
			pending = new UndoCameras(editorFrame.getTrackData());
		if (playing)
			setPlaying(false);
	}

	/** Ends the undo step: the changes since beginEdit are one step. */
	private void endEdit()
	{
		if (pending == null)
			return;
		UndoCameras step = pending;
		pending = null;
		step.captureAfter();
		if (step.changed())
		{
			Undo.add(step);
			editorFrame.documentIsModified = true;
			// the Cameras menu and the 3D view's cameras
			window.cameraEdited();
		}
	}

	/** A camera changed: the view and the strip follow, as it is. */
	private void cameraChanged()
	{
		owners = track.cameraOwners(cameras());
		// still the camera edited, wherever it films now
		lastOwner = ownerAt(distance);
		show();
	}

	private void segmentChanged()
	{
		if (filling || edited == null || track == null)
			return;
		Seg before = cameraSeg(edited);
		beginEdit();
		edited.setSegment(picked(segmentBox));
		Seg after = cameraSeg(edited);
		// a straight's "to start" is a length, a curve's an angle: start at its start
		if ((before.type == GameTrack.TR_STR) != (after.type == GameTrack.TR_STR))
		{
			edited.setToStart(0);
			edited.setToStartUnits(after.type == GameTrack.TR_STR ? null : "deg");
		}
		refreshEdit();
		cameraChanged();
		endEdit();
	}

	private void unitChanged()
	{
		if (filling || edited == null || track == null)
			return;
		String unit = (String) unitBox.getSelectedItem();
		Seg seg = cameraSeg(edited);
		double value = parse(toStartField.getText());
		if (!Double.isNaN(value))
		{
			// the same place in the other unit
			double angle = "deg".equals(shownUnit) ? Math.toRadians(value) : "m".equals(shownUnit) ? value / seg.radius : value;
			double shown = "deg".equals(unit) ? Math.toDegrees(angle) : "m".equals(unit) ? angle * seg.radius : angle;
			filling = true;
			toStartField.setText(text(shown));
			filling = false;
		}
		shownUnit = unit;
		fieldChanged(toStartField);
		endEdit();
	}

	private void fovChanged()
	{
		if (filling || edited == null || track == null)
			return;
		beginEdit();
		edited.setFovStart(picked(fovStartBox));
		edited.setFovEnd(picked(fovEndBox));
		cameraChanged();
		endEdit();
	}

	private static double parse(String text)
	{
		text = text.trim();
		if (text.isEmpty())
			return Double.NaN;
		try
		{
			return Double.parseDouble(text);
		}
		catch (NumberFormatException e)
		{
			return Double.NaN;
		}
	}

	/** A field was typed in: the camera changes as it's typed, when it's a number (or empty, for To Right and Height). */
	private void fieldChanged(JTextField field)
	{
		if (filling || edited == null || track == null)
			return;
		String text = field.getText().trim();
		double value = parse(text);
		boolean ok = !Double.isNaN(value) || text.isEmpty() && field != toStartField;
		field.setBackground(ok ? UIManager.getColor("TextField.background") : new Color(255, 200, 200));
		if (!ok)
			return;
		beginEdit();
		if (field == toRightField)
			edited.setToRight(value);
		else if (field == heightField)
			edited.setHeight(value);
		else
		{
			Seg seg = cameraSeg(edited);
			if (seg.type == GameTrack.TR_STR)
			{
				edited.setToStart(value);
				edited.setToStartUnits(null);
			}
			else if ("rad".equals(shownUnit))
			{
				// without a unit the game reads radians
				edited.setToStart(value);
				edited.setToStartUnits(null);
			}
			else
			{
				edited.setToStart("m".equals(shownUnit) ? Math.toDegrees(value / seg.radius) : value);
				edited.setToStartUnits("deg");
			}
		}
		cameraChanged();
	}

	/** Puts the camera by the car: its segment and "to start" where the car is. */
	private void place(Camera camera)
	{
		Seg seg = track.segAt(distance);
		Seg first = track.firstStep(seg.name);
		if (first == null)
			first = seg;
		double along = Math.max(0, distance - first.lgfromstart);
		camera.setSegment(seg.name);
		if (first.type == GameTrack.TR_STR)
		{
			camera.setToStart(Math.round(along * 100) / 100.0);
			camera.setToStartUnits(null);
		}
		else
		{
			camera.setToStart(Math.round(Math.toDegrees(along / first.radius) * 100) / 100.0);
			camera.setToStartUnits("deg");
		}
		// off the road on the right, a little up
		if (Double.isNaN(camera.getToRight()))
			camera.setToRight(-5);
		if (Double.isNaN(camera.getHeight()))
			camera.setHeight(5);
	}

	private void moveHere()
	{
		if (edited == null || track == null)
			return;
		beginEdit();
		place(edited);
		refreshEdit();
		cameraChanged();
		endEdit();
	}

	private void startHere()
	{
		if (edited == null || track == null)
			return;
		beginEdit();
		edited.setFovStart(track.segAt(distance).name);
		refreshEdit();
		cameraChanged();
		endEdit();
	}

	/** @return the segment after the car's segment (after all of its steps) */
	private String nextSegment(Seg seg)
	{
		Seg s = seg.next;
		while (s != seg && s.name != null && s.name.equals(seg.name))
			s = s.next;
		return s.name;
	}

	private void endHere()
	{
		if (edited == null || track == null)
			return;
		beginEdit();
		edited.setFovEnd(nextSegment(track.segAt(distance)));
		refreshEdit();
		cameraChanged();
		endEdit();
	}

	/** A new camera by the car, filming from its segment to where the next camera takes over (it comes last, so it wins). */
	private void addHere()
	{
		if (track == null)
			return;
		endEdit();
		Seg seg = track.segAt(distance);
		String end = nextSegment(seg);
		Camera now = owners[seg.id];
		for (int i = 1; i < owners.length; i++)
		{
			Seg s = track.segs.get((seg.id + i) % owners.length);
			if (owners[s.id] != now)
			{
				end = s.name;
				break;
			}
		}
		Camera camera = new Camera();
		List<Camera> cameras = cameras();
		for (int n = cameras.size(); camera.getName() == null; n++)
		{
			String name = "cam " + n;
			boolean used = false;
			for (Camera c : cameras)
				used |= name.equals(c.getName());
			if (!used)
				camera.setName(name);
		}
		place(camera);
		camera.setFovStart(seg.name);
		camera.setFovEnd(end);
		beginEdit();
		cameras.add(camera);
		edited = camera;
		refreshEdit();
		cameraChanged();
		endEdit();
	}

	private void delete()
	{
		if (edited == null || track == null)
			return;
		beginEdit();
		cameras().remove(edited);
		cameraChanged();
		edited = ownerAt(distance);
		refreshEdit();
		endEdit();
	}

	/** Which camera films each part of the lap: a band each, dashed red where none does. */
	private class Strip extends JComponent
	{
		private static final long serialVersionUID = 1L;

		Strip()
		{
			MouseAdapter mouse = new MouseAdapter()
			{
				public void mousePressed(MouseEvent e)
				{
					seek(e.getX());
				}

				public void mouseDragged(MouseEvent e)
				{
					seek(e.getX());
				}
			};
			addMouseListener(mouse);
			addMouseMotionListener(mouse);
		}

		private void seek(int x)
		{
			if (track == null)
				return;
			endEdit();
			moveTo(Math.max(0, Math.min(getWidth() - 1, x)) * track.length() / Math.max(1, getWidth()));
		}

		@Override
		public String getToolTipText(MouseEvent e)
		{
			if (track == null || owners.length == 0)
				return null;
			if (drive)
				return track.segAt(Math.max(0, Math.min(getWidth() - 1, e.getX())) * track.length() / Math.max(1, getWidth())).name;
			Camera camera = ownerAt(Math.max(0, Math.min(getWidth() - 1, e.getX())) * track.length() / Math.max(1, getWidth()));
			return camera == null ? "No camera: the game looks from above" : camera.getName();
		}

		@Override
		protected void paintComponent(Graphics g0)
		{
			Graphics2D g = (Graphics2D) g0.create();
			int w = getWidth(), h = getHeight(), top = 4, bandHeight = h - 8;
			if (track != null && drive)
			{
				// where the segments start, on a plain band
				double total = track.length();
				g.setColor(new Color(0xB4B2A9));
				g.fillRect(0, top, w, bandHeight);
				g.setColor(new Color(0x5F5E5A));
				String name = null;
				for (Seg s : track.segs)
				{
					if (s.name != null && s.name.equals(name))
						continue;
					name = s.name;
					int x = (int) Math.round(s.lgfromstart / total * w);
					g.drawLine(x, top, x, top + bandHeight - 1);
				}
				int x = (int) Math.round(distance / total * w);
				g.setColor(Color.BLACK);
				g.setStroke(new BasicStroke(3));
				g.drawLine(x, 0, x, h);
			}
			else if (track != null && owners.length > 0)
			{
				double total = track.length();
				List<Camera> cameras = cameras();
				int n = owners.length;
				for (int i = 0; i < n;)
				{
					Camera camera = owners[i];
					int j = i;
					while (j + 1 < n && owners[j + 1] == camera)
						j++;
					Seg first = track.segs.get(i), last = track.segs.get(j);
					int x0 = (int) Math.round(first.lgfromstart / total * w);
					int x1 = (int) Math.round((last.lgfromstart + last.length) / total * w);
					if (camera == null)
					{
						g.setColor(Color.WHITE);
						g.fillRect(x0, top, Math.max(1, x1 - x0), bandHeight);
						g.setColor(new Color(0xE24B4A));
						g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[] { 4, 3 }, 0));
						g.drawRect(x0, top, Math.max(1, x1 - x0 - 1), bandHeight - 1);
					}
					else
					{
						g.setColor(COLORS[Math.max(0, cameras.indexOf(camera)) % COLORS.length]);
						g.fillRect(x0, top, Math.max(1, x1 - x0), bandHeight);
					}
					i = j + 1;
				}
				// the camera edited, wherever it films
				if (edited != null && editBox.isSelected())
				{
					g.setColor(Color.BLACK);
					g.setStroke(new BasicStroke(2));
					for (int i = 0; i < n; i++)
					{
						if (owners[i] != edited)
							continue;
						Seg s = track.segs.get(i);
						int x0 = (int) Math.round(s.lgfromstart / total * w);
						int x1 = (int) Math.round((s.lgfromstart + s.length) / total * w);
						g.drawLine(x0, top + 1, x1, top + 1);
						g.drawLine(x0, top + bandHeight - 1, x1, top + bandHeight - 1);
					}
				}
				int x = (int) Math.round(distance / total * w);
				g.setColor(Color.BLACK);
				g.setStroke(new BasicStroke(3));
				g.drawLine(x, 0, x, h);
			}
			g.dispose();
		}
	}
}
