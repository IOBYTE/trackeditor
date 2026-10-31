/*
 *   Track3DWindow.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package gui.view3d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import gui.EditorFrame;
import utils.Properties;
import utils.TrackData;

/**
 * A separate window that shows the open track's road in 3D and follows every edit
 * made in the main window. Left-drag rotates, right-drag (or Shift and left-drag)
 * pans, the wheel zooms toward the road under the cursor, a double-click fits the
 * track in the window.
 */
public class Track3DWindow extends JFrame
{
	private static Track3DWindow	instance;
	// some Windows drivers offer multisampling only in pixel formats AWT can't give a window
	private static boolean			multisampling	= true;

	private final EditorFrame		editorFrame;
	private final GLCanvas			canvas;
	private final Track3DRenderer	renderer;
	private final Runnable			trackListener;
	private final JLabel			trackLabel		= new JLabel();
	private final JLabel			frameLabel		= new JLabel();
	private final JLabel			textureLabel	= new JLabel();
	private final JToggleButton		wireframeButton	= new JToggleButton("Wireframe");
	private final JToggleButton		lightingButton	= new JToggleButton("Lighting", true);
	private final JToggleButton		cullingButton	= new JToggleButton("Cull");
	private final JToggleButton		racelineButton	= new JToggleButton("Raceline");
	private final JButton			camerasButton	= new JButton("Cameras ▾");
	private final JComboBox<String>	colorBox		= new JComboBox<String>(new String[] { "textures", "by surface", "by height", "plain" });
	private final JComboBox<String>	heightBox		= new JComboBox<String>(new String[] { "1", "2", "5", "10" });
	private TrackData				shownTrack;
	private boolean					rebuildPending;
	private String					rendererName	= "";
	private int						lastX, lastY;
	private boolean					panning;
	// the track camera last looked from
	private int						cameraIndex		= -1;
	// the size and position when neither maximized nor minimized, which is what is saved
	private Rectangle				normalBounds;
	// called once the first frame is drawn, to close the loading splash
	private Runnable				whenDrawn;
	// gives the editor the camera and raceline layer, to keep in the track's project
	private final Supplier<String>	viewSource		= this::viewState;

	/**
	 * Opens the window, or brings it to the front. Opening can take a while on a big
	 * track, so a splash shows until the first frame is drawn. Then done gets null,
	 * or the error when the window can't open: the JOGL jars or their native
	 * libraries are missing, or there is no OpenGL.
	 */
	public static void open(EditorFrame editorFrame, Consumer<Throwable> done)
	{
		if (instance != null && instance.isDisplayable())
		{
			instance.setExtendedState(instance.getExtendedState() & ~ICONIFIED);
			instance.setVisible(true);
			instance.toFront();
			done.accept(null);
			return;
		}
		LoadingSplash splash = new LoadingSplash(editorFrame);
		// building the window keeps the event thread busy: give the splash time to show first
		Timer start = new Timer(100, e -> {
			splash.paintNow();
			try
			{
				create(editorFrame);
			}
			catch (Throwable t)
			{
				if (instance != null)
					instance.dispose();
				instance = null;
				splash.dispose();
				done.accept(t);
				return;
			}
			instance.whenDrawn(() -> {
				splash.dispose();
				done.accept(null);
			});
		});
		start.setRepeats(false);
		start.start();
	}

	private static void create(EditorFrame editorFrame)
	{
		try
		{
			instance = new Track3DWindow(editorFrame, multisampling);
			instance.setVisible(true);
		}
		catch (GLException e)
		{
			if (!multisampling)
				throw e;
			if (instance != null)
				instance.dispose();
			multisampling = false;
			instance = new Track3DWindow(editorFrame, false);
			instance.setVisible(true);
		}
		instance.toFront();
	}

	/** Runs whenDrawn after the first frame, or after a while if nothing gets drawn. */
	private void whenDrawn(Runnable whenDrawn)
	{
		this.whenDrawn = whenDrawn;
		// an OpenGL error while drawing is only printed: don't keep the splash up for it
		Timer timeout = new Timer(20000, e -> drawn());
		timeout.setRepeats(false);
		timeout.start();
	}

	private void drawn()
	{
		Runnable run = whenDrawn;
		whenDrawn = null;
		if (run != null)
			run.run();
	}

	/**
	 * A small window that says the 3D view is loading, shown while the event thread
	 * is busy building it.
	 */
	private static class LoadingSplash extends JWindow
	{
		LoadingSplash(EditorFrame editorFrame)
		{
			super(editorFrame);
			JLabel label = new JLabel("Loading the 3D view...");
			label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
				BorderFactory.createEmptyBorder(20, 32, 20, 32)));
			panel.add(label, BorderLayout.CENTER);
			setContentPane(panel);
			pack();
			setLocationRelativeTo(editorFrame);
			setAlwaysOnTop(true);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			setVisible(true);
		}

		/** Paints it now, as it can't be painted while the window is built. */
		void paintNow()
		{
			getRootPane().paintImmediately(0, 0, getRootPane().getWidth(), getRootPane().getHeight());
		}
	}

	private Track3DWindow(EditorFrame editorFrame, boolean multisample)
	{
		super("3D View");
		this.editorFrame = editorFrame;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// the OpenGL canvas is drawn by the system: Swing's popups have to be windows to show over it
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

		GLProfile profile = GLProfile.getMaxFixedFunc(true);
		GLCapabilities capabilities = new GLCapabilities(profile);
		capabilities.setDepthBits(24);
		if (multisample)
		{
			capabilities.setSampleBuffers(true);
			capabilities.setNumSamples(4);
		}
		canvas = new GLCanvas(capabilities);
		renderer = new Track3DRenderer(this);
		canvas.addGLEventListener(renderer);
		canvas.setFocusable(true);

		add(createToolBar(), BorderLayout.NORTH);
		restoreSettings();
		add(canvas, BorderLayout.CENTER);
		add(createStatusBar(), BorderLayout.SOUTH);
		addMouseAndKeys();

		restoreBounds();
		trackListener = new Runnable()
		{
			public void run()
			{
				trackChanged();
			}
		};
		editorFrame.getCircuitView().addTrackChangeListener(trackListener);
		editorFrame.set3DViewSource(viewSource);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				editorFrame.getCircuitView().removeTrackChangeListener(trackListener);
				// the next window shows the track the same way
				String state = viewState();
				if (state != null)
					editorFrame.set3DView(state);
				editorFrame.remove3DViewSource(viewSource);
				saveBounds();
				drawn();
			}
		});
		// saved as it changes, so it is kept also when the editor quits with the window open
		addComponentListener(new ComponentAdapter()
		{
			public void componentMoved(ComponentEvent e)
			{
				boundsChanged();
			}

			public void componentResized(ComponentEvent e)
			{
				boundsChanged();
			}
		});
		addWindowStateListener(e -> saveBounds());

		rebuild();
	}

	private JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		colorBox.setLightWeightPopupEnabled(false);
		heightBox.setLightWeightPopupEnabled(false);

		wireframeButton.setToolTipText("Show the edges of the triangles (W)");
		wireframeButton.addActionListener(e -> setWireframe(wireframeButton.isSelected()));
		lightingButton.setToolTipText("Shade the track by a light from above (L); off shows flat colors");
		lightingButton.addActionListener(e -> setLighting(lightingButton.isSelected()));
		cullingButton.setToolTipText("Hide the back of one-sided surfaces, as the game does: the road's underside disappears (C)");
		cullingButton.addActionListener(e -> setCulling(cullingButton.isSelected()));
		racelineButton.setToolTipText("Show the raceline layer a raceline export adds to the road (R); on when the road's surfaces name a raceline texture");
		racelineButton.addActionListener(e -> setRaceline(racelineButton.isSelected()));
		JButton fit = new JButton("Fit");
		fit.setToolTipText("Show the whole track from the current direction (F, or double-click)");
		fit.addActionListener(e -> frame());
		JButton top = new JButton("Top");
		top.setToolTipText("Look straight down (T)");
		top.addActionListener(e -> fit(true));
		JButton angled = new JButton("Angled");
		angled.setToolTipText("Look at the track at an angle (P)");
		angled.addActionListener(e -> fit(false));
		camerasButton.setToolTipText("<html>Look from one of the track's cameras at all of the track it films (N for the next one).<br>" +
			"The game turns it to follow a car and zooms in on it.</html>");
		camerasButton.addActionListener(e -> showCameras());
		JButton zoomIn = new JButton("+");
		zoomIn.setToolTipText("Zoom in (+, or the wheel)");
		zoomIn.addActionListener(e -> zoom(0.8));
		JButton zoomOut = new JButton("−");
		zoomOut.setToolTipText("Zoom out (-, or the wheel)");
		zoomOut.addActionListener(e -> zoom(1.25));
		colorBox.setToolTipText("How the road is colored");
		colorBox.setMaximumSize(colorBox.getPreferredSize());
		colorBox.addActionListener(e -> {
			int i = colorBox.getSelectedIndex();
			renderer.setColoring(Track3DRenderer.Coloring.values()[i]);
			saveSettings();
			requestRedraw();
		});
		heightBox.setToolTipText("Exaggerate the heights, for flat tracks");
		heightBox.setMaximumSize(heightBox.getPreferredSize());
		heightBox.addActionListener(e -> {
			renderer.setHeightScale(Double.parseDouble((String) heightBox.getSelectedItem()));
			saveSettings();
			requestRedraw();
		});

		toolBar.add(wireframeButton);
		toolBar.add(lightingButton);
		toolBar.add(cullingButton);
		toolBar.add(racelineButton);
		toolBar.addSeparator();
		toolBar.add(fit);
		toolBar.add(top);
		toolBar.add(angled);
		toolBar.add(camerasButton);
		toolBar.add(zoomIn);
		toolBar.add(zoomOut);
		toolBar.addSeparator();
		toolBar.add(new JLabel("Color "));
		toolBar.add(colorBox);
		toolBar.add(Box.createHorizontalStrut(8));
		toolBar.add(new JLabel("Height × "));
		toolBar.add(heightBox);
		toolBar.add(Box.createHorizontalGlue());
		return toolBar;
	}

	private JPanel createStatusBar()
	{
		JPanel status = new JPanel(new BorderLayout());
		status.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
		status.add(trackLabel, BorderLayout.WEST);
		textureLabel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
		status.add(textureLabel, BorderLayout.CENTER);
		status.add(frameLabel, BorderLayout.EAST);
		return status;
	}

	private void addMouseAndKeys()
	{
		MouseAdapter mouse = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				canvas.requestFocusInWindow();
				lastX = e.getX();
				lastY = e.getY();
				panning = SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e) || e.isShiftDown();
			}

			public void mouseDragged(MouseEvent e)
			{
				double scale = surfaceScale();
				double dx = (e.getX() - lastX) * scale, dy = (e.getY() - lastY) * scale;
				lastX = e.getX();
				lastY = e.getY();
				if (panning)
					renderer.camera.pan(dx, dy, renderer.getHeight());
				else
					renderer.camera.orbit(dx, dy);
				requestRedraw();
			}

			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e))
					frame();
			}

			public void mouseWheelMoved(MouseWheelEvent e)
			{
				double scale = surfaceScale();
				renderer.zoomAt((int) (e.getX() * scale), (int) (e.getY() * scale), Math.pow(1.15, e.getPreciseWheelRotation()));
				requestRedraw();
			}
		};
		canvas.addMouseListener(mouse);
		canvas.addMouseMotionListener(mouse);
		canvas.addMouseWheelListener(mouse);

		canvas.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				double step = 20;
				switch (e.getKeyCode())
				{
				case KeyEvent.VK_W:
					wireframeButton.setSelected(!wireframeButton.isSelected());
					setWireframe(wireframeButton.isSelected());
					break;
				case KeyEvent.VK_L:
					lightingButton.setSelected(!lightingButton.isSelected());
					setLighting(lightingButton.isSelected());
					break;
				case KeyEvent.VK_C:
					cullingButton.setSelected(!cullingButton.isSelected());
					setCulling(cullingButton.isSelected());
					break;
				case KeyEvent.VK_R:
					racelineButton.setSelected(!racelineButton.isSelected());
					setRaceline(racelineButton.isSelected());
					break;
				case KeyEvent.VK_N:
					nextCamera();
					break;
				case KeyEvent.VK_F:
					frame();
					break;
				case KeyEvent.VK_P:
					fit(false);
					break;
				case KeyEvent.VK_T:
					fit(true);
					break;
				case KeyEvent.VK_PLUS:
				case KeyEvent.VK_EQUALS:
				case KeyEvent.VK_ADD:
					zoom(0.8);
					break;
				case KeyEvent.VK_MINUS:
				case KeyEvent.VK_SUBTRACT:
					zoom(1.25);
					break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_UP:
				case KeyEvent.VK_DOWN:
				{
					int dx = e.getKeyCode() == KeyEvent.VK_LEFT ? -1 : e.getKeyCode() == KeyEvent.VK_RIGHT ? 1 : 0;
					int dy = e.getKeyCode() == KeyEvent.VK_UP ? -1 : e.getKeyCode() == KeyEvent.VK_DOWN ? 1 : 0;
					if (e.isShiftDown())
						renderer.camera.pan(-dx * step, -dy * step, renderer.getHeight());
					else
						renderer.camera.orbit(dx * step / 2, dy * step / 2);
					requestRedraw();
					break;
				}
				default:
					return;
				}
				e.consume();
			}
		});
	}

	/** @return OpenGL pixels per mouse unit (above 1 on a scaled display) */
	private double surfaceScale()
	{
		float[] scale = canvas.getCurrentSurfaceScale(new float[2]);
		return scale[1] > 0 ? scale[1] : 1;
	}

	private void setWireframe(boolean on)
	{
		renderer.setWireframe(on);
		saveSettings();
		requestRedraw();
	}

	private void setCulling(boolean on)
	{
		renderer.setCulling(on);
		saveSettings();
		requestRedraw();
	}

	private void setRaceline(boolean on)
	{
		renderer.setRaceline(on);
		rebuild();
	}

	private void setLighting(boolean on)
	{
		renderer.setLighting(on);
		saveSettings();
		requestRedraw();
	}

	/**
	 * Shows the track as it was shown last time: wireframe, lighting, culling, color
	 * and height exaggeration are the same for every track.
	 */
	private void restoreSettings()
	{
		Preferences preferences = Preferences.userNodeForPackage(Track3DWindow.class);
		wireframeButton.setSelected(preferences.getBoolean("View3DWireframe", false));
		lightingButton.setSelected(preferences.getBoolean("View3DLighting", true));
		cullingButton.setSelected(preferences.getBoolean("View3DCull", false));
		renderer.setWireframe(wireframeButton.isSelected());
		renderer.setLighting(lightingButton.isSelected());
		renderer.setCulling(cullingButton.isSelected());
		// read both first: setting one saves the other
		int color = preferences.getInt("View3DColor", 0);
		String height = preferences.get("View3DHeightScale", "1");
		if (color >= 0 && color < colorBox.getItemCount())
			colorBox.setSelectedIndex(color);
		// not an item: stays at 1
		heightBox.setSelectedItem(height);
	}

	private void saveSettings()
	{
		Preferences preferences = Preferences.userNodeForPackage(Track3DWindow.class);
		preferences.putBoolean("View3DWireframe", wireframeButton.isSelected());
		preferences.putBoolean("View3DLighting", lightingButton.isSelected());
		preferences.putBoolean("View3DCull", cullingButton.isSelected());
		preferences.putInt("View3DColor", colorBox.getSelectedIndex());
		preferences.put("View3DHeightScale", (String) heightBox.getSelectedItem());
	}

	/**
	 * @return the camera and raceline layer, kept in the track's project: the
	 *         camera's {@link OrbitCamera#state} without its field of view, 1 when
	 *         the raceline layer shows, and the field of view. Null when no track
	 *         is shown, or not yet the open one.
	 */
	private String viewState()
	{
		RoadMesh mesh = renderer.getMesh();
		if (shownTrack == null || shownTrack != editorFrame.getTrackData() || mesh == null || mesh.isEmpty())
			return null;
		double[] c = renderer.camera.state(renderer.getHeightScale());
		return String.format(Locale.ROOT, "%.5f %.5f %.3f %.3f %.3f %.3f %d %.2f", c[0], c[1], c[2], c[3], c[4], c[5],
			racelineButton.isSelected() ? 1 : 0, c[6]);
	}

	/** @return a {@link #viewState}'s numbers, or null when there is none or it can't be read */
	private static double[] parseViewState(String state)
	{
		if (state == null)
			return null;
		String[] parts = state.trim().split("\\s+");
		// the field of view was added later
		if (parts.length != 7 && parts.length != 8)
			return null;
		double[] values = new double[8];
		values[7] = OrbitCamera.FOV_DEGREES;
		try
		{
			for (int i = 0; i < parts.length; i++)
			{
				values[i] = Double.parseDouble(parts[i]);
				if (Double.isNaN(values[i]) || Double.isInfinite(values[i]))
					return null;
			}
		}
		catch (NumberFormatException e)
		{
			return null;
		}
		return values;
	}

	private void fit(boolean top)
	{
		RoadMesh mesh = renderer.getMesh();
		if (mesh != null && !mesh.isEmpty())
			renderer.camera.fit(mesh, renderer.getHeightScale(), top);
		requestRedraw();
	}

	private void frame()
	{
		RoadMesh mesh = renderer.getMesh();
		if (mesh != null && !mesh.isEmpty())
			renderer.camera.frame(mesh, renderer.getHeightScale());
		requestRedraw();
	}

	/** The menu of the track's cameras. */
	private void showCameras()
	{
		RoadMesh mesh = renderer.getMesh();
		if (mesh == null || mesh.cameras.isEmpty())
			return;
		JPopupMenu menu = new JPopupMenu();
		for (int i = 0; i < mesh.cameras.size(); i++)
		{
			final int index = i;
			JMenuItem item = new JMenuItem(mesh.cameras.get(i).name);
			item.addActionListener(e -> lookFromCamera(index));
			menu.add(item);
		}
		menu.show(camerasButton, 0, camerasButton.getHeight());
	}

	private void nextCamera()
	{
		RoadMesh mesh = renderer.getMesh();
		if (mesh != null && !mesh.cameras.isEmpty())
			lookFromCamera((cameraIndex + 1) % mesh.cameras.size());
	}

	/** Looks from the track's camera, toward the middle of what it films. */
	private void lookFromCamera(int index)
	{
		RoadMesh mesh = renderer.getMesh();
		if (mesh == null || mesh.isEmpty() || index >= mesh.cameras.size())
			return;
		cameraIndex = index;
		GameTrack.CameraView view = mesh.cameras.get(index);
		double scale = renderer.getHeightScale();
		// the exaggeration lifts the ground under it, not its height above the ground
		double[] eye = { view.eye[0], view.eye[1], (view.eye[2] - view.height) * scale + view.height };

		// the game turns it to follow a car and zooms in on it: show all it films instead
		double[] middle = { view.target[0], view.target[1], view.target[2] * scale };
		double yaw0 = Math.atan2(middle[1] - eye[1], middle[0] - eye[0]);
		double minYaw = 0, maxYaw = 0, minPitch = Double.MAX_VALUE, maxPitch = -Double.MAX_VALUE;
		for (float[] p : view.filmed)
		{
			double dx = p[0] - eye[0], dy = p[1] - eye[1], dz = p[2] * scale - eye[2];
			double yaw = Math.IEEEremainder(Math.atan2(dy, dx) - yaw0, 2 * Math.PI);
			minYaw = Math.min(minYaw, yaw);
			maxYaw = Math.max(maxYaw, yaw);
			double pitch = Math.atan2(dz, Math.hypot(dx, dy));
			minPitch = Math.min(minPitch, pitch);
			maxPitch = Math.max(maxPitch, pitch);
		}
		double margin = Math.toRadians(3);
		double halfWidth = Math.min(Math.toRadians(80), (maxYaw - minYaw) / 2 + margin);
		double halfHeight = (maxPitch - minPitch) / 2 + margin;
		double aspect = (double) renderer.getWidth() / renderer.getHeight();
		double fov = Math.toDegrees(2 * Math.max(halfHeight, Math.atan(Math.tan(halfWidth) / aspect)));
		double distance = Math.sqrt(Math.pow(middle[0] - eye[0], 2) + Math.pow(middle[1] - eye[1], 2) + Math.pow(middle[2] - eye[2], 2));
		renderer.camera.lookFrom(eye, yaw0 + (minYaw + maxYaw) / 2, (minPitch + maxPitch) / 2, distance,
			Math.max(10, Math.min(120, fov)), mesh);
		trackLabel.setText("Camera: " + view.name);
		requestRedraw();
	}

	private void zoom(double factor)
	{
		renderer.camera.zoom(factor, null);
		requestRedraw();
	}

	void requestRedraw()
	{
		canvas.repaint();
	}

	/** The main window redrew the track: rebuild the road once the current events are done. */
	private void trackChanged()
	{
		if (rebuildPending)
			return;
		rebuildPending = true;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				rebuildPending = false;
				if (isDisplayable())
					rebuild();
			}
		});
	}

	private void rebuild()
	{
		// another track: its raceline layer when its road names one
		TrackData track = editorFrame.getTrackData();
		boolean newTrack = track != shownTrack;
		double[] saved = null;
		if (newTrack)
		{
			// as it was last shown, else with its raceline layer when its road names one
			saved = parseViewState(editorFrame.get3DView());
			racelineButton.setSelected(saved != null ? saved[6] != 0 : TrackScene.namesRaceline(editorFrame));
			renderer.setRaceline(racelineButton.isSelected());
		}
		RoadMesh mesh = RoadMesh.build(editorFrame, racelineButton.isSelected());
		renderer.setMesh(mesh);

		// from where it was last shown, else all of it in view
		if (newTrack && !mesh.isEmpty())
		{
			if (saved != null)
				renderer.camera.setState(new double[] { saved[0], saved[1], saved[2], saved[3], saved[4], saved[5], saved[7] }, mesh, renderer.getHeightScale());
			else
				renderer.camera.fit(mesh, renderer.getHeightScale(), false);
		}
		shownTrack = track;

		String name = trackName();
		setTitle(name == null ? "3D View" : "3D View — " + name);
		trackLabel.setText(mesh.isEmpty() ? "No track open" : String.format("%,d triangles in %,d segments", mesh.triangleCount, mesh.segmentCount));
		camerasButton.setEnabled(!mesh.cameras.isEmpty());
		if (newTrack)
			cameraIndex = -1;
		requestRedraw();
	}

	private String trackName()
	{
		if (editorFrame.getTrackData() == null)
			return null;
		String path = Properties.getInstance().getPath();
		if (path == null)
			return null;
		int sep = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
		return path.substring(sep + 1);
	}

	/** Called from the drawing thread once OpenGL is ready. */
	void rendererFound(String name)
	{
		SwingUtilities.invokeLater(() -> rendererName = name);
	}

	/** Called from the drawing thread with the textures that couldn't be shown, and why. */
	void texturesMissing(java.util.List<String> missing)
	{
		SwingUtilities.invokeLater(() -> {
			textureLabel.setText(missing.isEmpty() ? "" : missing.size() == 1 ? "1 texture missing" : missing.size() + " textures missing");
			textureLabel.setToolTipText(missing.isEmpty() ? null : "<html>" + escape(missing) + "</html>");
		});
	}

	private static String escape(java.util.List<String> lines)
	{
		StringBuilder text = new StringBuilder();
		for (String line : lines)
		{
			if (text.length() > 0)
				text.append("<br>");
			text.append(line.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
		}
		return text.toString();
	}

	/** Called from the drawing thread after each frame. */
	void frameDrawn(double ms, int triangles)
	{
		SwingUtilities.invokeLater(() -> {
			frameLabel.setText(String.format("%.1f ms    %s", ms, rendererName));
			drawn();
		});
	}

	/**
	 * Puts the window where it was last time, at the same size and maximized if it
	 * was. Next to the editor the first time, or when the saved place is no longer
	 * on any screen (a monitor was unplugged).
	 */
	private void restoreBounds()
	{
		Preferences preferences = Preferences.userNodeForPackage(Track3DWindow.class);
		Rectangle main = editorFrame.getBounds();
		int w = Math.max(400, preferences.getInt("View3DWidth", 900)), h = Math.max(300, preferences.getInt("View3DHeight", 620));
		int x = preferences.getInt("View3DX", main.x + 60), y = preferences.getInt("View3DY", main.y + 60);
		if (!onScreen(new Rectangle(x, y, w, h)))
		{
			x = main.x + 60;
			y = main.y + 60;
		}
		normalBounds = new Rectangle(x, y, w, h);
		setBounds(normalBounds);
		if (preferences.getBoolean("View3DMaximized", false))
			setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
	}

	/** @return whether enough of the title bar is on a screen to grab it */
	private static boolean onScreen(Rectangle r)
	{
		Rectangle titleBar = new Rectangle(r.x, r.y, r.width, 30);
		for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
		{
			Rectangle visible = device.getDefaultConfiguration().getBounds().intersection(titleBar);
			if (visible.width >= 100 && visible.height >= 10)
				return true;
		}
		return false;
	}

	private void boundsChanged()
	{
		if (isShowing() && getExtendedState() == NORMAL)
			normalBounds = getBounds();
		saveBounds();
	}

	private void saveBounds()
	{
		if (normalBounds == null)
			return;
		Preferences preferences = Preferences.userNodeForPackage(Track3DWindow.class);
		preferences.putInt("View3DX", normalBounds.x);
		preferences.putInt("View3DY", normalBounds.y);
		preferences.putInt("View3DWidth", normalBounds.width);
		preferences.putInt("View3DHeight", normalBounds.height);
		preferences.putBoolean("View3DMaximized", (getExtendedState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH);
	}
}
