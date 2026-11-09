/*
 *   CameraProperties.java
 *   Created on 15 June 2022
 *
 *    The CameraProperties.java is part of TrackEditor-0.7.0.
 *
 *    TrackEditor-0.7.0 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.7.0 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.7.0; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package gui.properties;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gui.EditorFrame;
import utils.MutableDouble;
import utils.MutableString;
import utils.SegmentVector;
import utils.circuit.Camera;
import utils.circuit.Curve;
import utils.circuit.Segment;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class CameraProperties extends PropertyPanel
{
	private JButton				addCameraButton		= null;
	private JButton				deleteCameraButton	= null;
	private JTabbedPane			tabbedPane			= null;

	/**
	 *
	 */
	public CameraProperties(EditorFrame editorFrame)
	{
		super(editorFrame);
		initialize();
    }

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize()
	{
		this.setLayout(null);
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		this.setSize(362, 251);
		this.add(getTabbedPane(), null);
		this.add(getAddCameraButton(), null);
		this.add(getDeleteCameraButton(), null);
	}

	/**
	 * This method initializes addCamerapingButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getAddCameraButton()
	{
		if (addCameraButton == null)
		{
			addCameraButton = new JButton();
			addCameraButton.setBounds(10, 277, 120, 25);
			addCameraButton.setText("Add Camera");
			addCameraButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					String name = uniqueName(tabbedPane, "camera");
					Camera camera = new Camera();

					camera.setName(name);

					tabbedPane.addTab(name, null, new CameraPanel(camera), null);
					tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
				}
			});
		}
		return addCameraButton;
	}

	/**
	 * This method initializes deleteCameraButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteCameraButton()
	{
		if (deleteCameraButton == null)
		{
			deleteCameraButton = new JButton();
			deleteCameraButton.setBounds(150, 277, 120, 25);
			deleteCameraButton.setText("Delete Camera");
			deleteCameraButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (tabbedPane.getTabCount() > 0)
					{
						tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
					}
				}
			});
		}
		return deleteCameraButton;
	}

	/**
	 * This method initializes tabbedPane
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getTabbedPane()
	{
		if (tabbedPane == null)
		{
			tabbedPane = new JTabbedPane();
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			tabbedPane.setBounds(10, 10, 510, 257);

			Vector<Camera> cameras = getEditorFrame().getTrackData().getCameras();

			for (int i = 0; i < cameras.size(); i++)
	        {
				Camera camera = cameras.elementAt(i);
				tabbedPane.addTab(camera.getName(), null, new CameraPanel(camera), null);
			}
			SwingUtilities.invokeLater( new Runnable()
			{
				public void run()
				{
					int lastTab = getEditorFrame().getProject().getPropertiesEditorCameraTab();
					if (lastTab == -1 && tabbedPane.getTabCount() > 0)
					{
						lastTab = 0;
					}
					if (lastTab < tabbedPane.getTabCount())
					{
						tabbedPane.setSelectedIndex(lastTab);
					}
				}
			});
		}
		return tabbedPane;
	}

	private class CameraPanel extends JPanel
	{
		private final Camera	item;	// what this tab shows and Ok changes
		private JLabel				nameLabel			= new JLabel();
		private JTextField 			nameTextField		= new JTextField();
		private JLabel				commentLabel		= new JLabel();
		private JTextField 			commentTextField	= new JTextField();
		private JLabel				segmentLabel		= new JLabel();
		private JTextField			segmentTextField	= hintTextField("required");
		private JLabel				toRightLabel		= new JLabel();
		private JTextField			toRightTextField	= hintTextField("0.0 (right edge)");
		private JLabel				toStartLabel		= new JLabel();
		private JTextField			toStartTextField	= hintTextField("0.0 (start of the segment)");
		private JButton				fixButton			= new JButton("Fix");
		// the unit To Start is saved in, which Fix can change
		private boolean				unitsKnown;
		private String				units;
		// what is wrong with To Start's unit, and with the segment names
		private boolean				toStartProblem;
		private boolean				namesProblem;
		private String[]			segmentTip, fovStartTip, fovEndTip;
		private JLabel				heightLabel			= new JLabel();
		private JTextField			heightTextField		= hintTextField("0.0 (on the road)");
		private JLabel				fovStartLabel		= new JLabel();
		private JTextField			fovStartTextField	= hintTextField("required");
		private JLabel				fovEndLabel			= new JLabel();
		private JTextField			fovEndTextField		= hintTextField("required");

		/**
		 *
		 */
		public CameraPanel(Camera camera)
		{
			super();
			this.item = camera;
			unitsKnown = camera.isToStartUnitsKnown();
			units = camera.getToStartUnits();
			initialize(camera);
		}

		/**
		 *
		 */
		private void initialize(Camera camera)
		{
			setLayout(null);

			addLabel(this, 0, nameLabel, "Name", 130);
			addLabel(this, 1, commentLabel, "Comment", 130);
			addLabel(this, 2, segmentLabel, "Segment", 130);
			addLabel(this, 3, toRightLabel, "To Right (m)", 130);
			addLabel(this, 4, toStartLabel, "To Start", 130);
			addLabel(this, 5, heightLabel, "Height (m)", 130);
			addLabel(this, 6, fovStartLabel, "Fov Start", 130);
			addLabel(this, 7, fovEndLabel, "Fov End", 130);

			addTextField(this, 0, nameTextField, camera.getName(), 150, 125);
			markItemName(nameTextField);
			addTextField(this, 1, commentTextField, camera.getComment(), 150, 335);
			addTextField(this, 2, segmentTextField, camera.getSegment(), 150, 185);
			segmentChooser(segmentTextField, 0);
			addTextField(this, 3, toRightTextField, camera.getToRight(), 150, 185);
			addTextField(this, 4, toStartTextField, camera.getToStart(), 150, 185);
			addTextField(this, 5, heightTextField, camera.getHeight(), 150, 185);
			addTextField(this, 6, fovStartTextField, camera.getFovStart(), 150, 185);
			addTextField(this, 7, fovEndTextField, camera.getFovEnd(), 150, 185);
			segmentChooser(fovStartTextField, 1);
			segmentChooser(fovEndTextField, 2);

			tip(nameLabel, nameTextField, "The camera's name, different from the other cameras'.",
				"The game doesn't show it.");
			tip(commentLabel, commentTextField, "A note for whoever edits the track. The game doesn't read it.");
			segmentTip = new String[] { "The segment the camera stands by: To Start and To Right",
				"are measured from it. Required: without it the game won't load the track." };
			tip(toRightLabel, toRightTextField, "How far across from the road's right edge, in meters, looking along the track:",
				"0 is on the right edge, the road's width is on the left edge,",
				"less than 0 is off the road on the right, more than the width is off it on the left.",
				"Empty is 0, the right edge.");
			tip(heightLabel, heightTextField, "How high the camera is, in meters, above the ground under it",
				"(the road, or a border or side when it is off the road).",
				"Empty is 0, on the ground.");
			fovStartTip = new String[] { "The first segment this camera films.",
				"Fov is the stretch of track the camera covers, not a lens angle: the game zooms by itself.",
				"The camera films from the start of Fov Start up to the start of Fov End.",
				"Where cameras' stretches overlap, the one later in the list films.",
				"Required: without it the game won't load the track." };
			fovEndTip = new String[] { "The segment after the last one this camera films: it stops where this one starts.",
				"The same segment as Fov Start films the whole lap.",
				"Required: without it the game won't load the track." };

			fixButton.setBounds(toStartTextField.getX() + toStartTextField.getWidth() + 5, toStartTextField.getY(), 60,
				toStartTextField.getHeight());
			fixButton.setToolTipText("Put To Start in a unit that fits the segment");
			fixButton.addActionListener(e -> fixToStart());
			add(fixButton);

			// the segment says how To Start is read; the names have to be segments
			showProblems();
			DocumentListener changed = new DocumentListener()
			{
				public void insertUpdate(DocumentEvent e)
				{
					showProblems();
				}

				public void removeUpdate(DocumentEvent e)
				{
					showProblems();
				}

				public void changedUpdate(DocumentEvent e)
				{
					showProblems();
				}
			};
			for (JTextField field : new JTextField[] { segmentTextField, toStartTextField, fovStartTextField, fovEndTextField })
				field.getDocument().addDocumentListener(changed);
		}

		/** Shows what the game would make of the camera as it is now, as soon as the tab shows. */
		private void showProblems()
		{
			showNameProblems();
			showToStartUnits();
		}

		/**
		 * Marks a segment name that is missing or isn't a segment in red, with what
		 * the game does then; Ok offers to pick the right one.
		 */
		private void showNameProblems()
		{
			JLabel[] labels = { segmentLabel, fovStartLabel, fovEndLabel };
			JTextField[] fields = { segmentTextField, fovStartTextField, fovEndTextField };
			String[][] tips = { segmentTip, fovStartTip, fovEndTip };
			String[] instead = { "the game puts the camera by the first segment instead",
				"the game films from the first segment instead", "the game films up to the first segment instead" };
			namesProblem = false;
			for (int i = 0; i < fields.length; i++)
			{
				String name = fields[i].getText().trim();
				String problem = null;
				if (name.isEmpty())
					problem = "Missing: the game won't load the track without it.";
				else if (getEditorFrame().getSegment(name) == null)
					problem = "\"" + name + "\" isn't a segment: " + instead[i] + ".";
				String[] lines = tips[i];
				if (problem != null)
				{
					namesProblem = true;
					lines = java.util.Arrays.copyOf(lines, lines.length + 2);
					lines[lines.length - 2] = "<font color=red>" + problem.replace("<", "&lt;") + "</font>";
					lines[lines.length - 1] = "<font color=red>Pick the segment from the list; Ok also offers to.</font>";
				}
				labels[i].setForeground(problem != null ? java.awt.Color.RED : UIManager.getColor("Label.foreground"));
				tip(labels[i], fields[i], lines);
			}
		}

		/** Puts a list of the track's segments around a name's field, in its place. */
		private void segmentChooser(JTextField field, int which)
		{
			java.awt.Rectangle bounds = field.getBounds();
			remove(field);
			SegmentChooser chooser = new SegmentChooser(field, false, () -> getEditorFrame().getTrackData().getSegments(),
				() -> {
					SegmentVector segments = getEditorFrame().getTrackData().getSegments();
					String name = field.getText().trim();
					return name.isEmpty() ? missingGuess(this, which, segments) : closestSegmentName(name, segments);
				});
			chooser.setBounds(bounds);
			add(chooser);
		}

		@Override
		public void addNotify()
		{
			super.addNotify();
			showTabProblem();
		}

		/** Shows the camera's tab in red while it has a problem. */
		private void showTabProblem()
		{
			int i = tabbedPane == null ? -1 : tabbedPane.indexOfComponent(this);
			if (i < 0)
				return;
			boolean problem = namesProblem || toStartProblem;
			tabbedPane.setForegroundAt(i, problem ? java.awt.Color.RED : null);
			tabbedPane.setToolTipTextAt(i, problem ? "This camera has a problem: see the red labels" : null);
		}

		/** @return the unit To Start is saved in: as the file has it, or as a new camera is saved */
		private String savedUnits(Segment segment)
		{
			if (unitsKnown)
				return units == null || units.isEmpty() ? null : units;
			return segment != null && !segment.getType().equals("str") ? "deg" : null;
		}

		private double toStart()
		{
			try
			{
				return Double.parseDouble(toStartTextField.getText().trim());
			}
			catch (NumberFormatException e)
			{
				return Double.NaN;
			}
		}

		/** @return the number, in its unit, as the game reads it: rad for deg, m for ft */
		private double read(double value, String units)
		{
			return units == null ? value : plugin.torcs.GfParmNumbers.toSI(units, value, getEditorFrame().getGameType(), null);
		}

		/**
		 * Offers To Start in a unit that fits its segment: what was most likely
		 * meant, or the same place the game puts it now.
		 */
		private void fixToStart()
		{
			Segment segment = getEditorFrame().getSegment(segmentTextField.getText().trim());
			double value = toStart();
			if (segment == null || Double.isNaN(value))
				return;
			String units = savedUnits(segment);
			double read = read(value, units);
			String given = text(value) + (units == null ? "" : " " + units);
			String message;
			String[] options;
			double[] values;
			String[] newUnits;
			if (segment.getType().equals("str"))
			{
				message = "On a straight the game reads To Start as meters: it turns " + given + " into " + text(read)
					+ " and puts the camera " + text(read) + " m along " + segment.getName() + ".";
				options = new String[] { "Meant " + text(value) + " m", "Keep it at " + text(read) + " m", "Cancel" };
				values = new double[] { value, read };
				newUnits = new String[] { null, null };
			}
			else
			{
				double radius = ((Curve) segment).getRadiusStart();
				boolean length = units != null && !"deg".equals(units) && !"rad".equals(units);
				double turned = Math.toDegrees(read) % 360;
				if (turned < 0)
					turned += 360;
				// as meters along the middle of the road
				double meters = length ? read : value;
				// a value in degrees was meant as an angle
				double meant = Double.isNaN(radius) || radius <= 0 || "deg".equals(units) ? Double.NaN : Math.toDegrees(meters / radius);
				message = "On a curve the game reads To Start as an angle in radians: it reads " + given + " as "
					+ text(read) + " radians and puts the camera " + text(Math.toDegrees(read)) + " deg round " + segment.getName()
					+ (Math.abs(read) >= 2 * Math.PI ? ", more than a full turn, so " + text(turned) + " deg round its circle." : ".");
				java.util.List<String> o = new java.util.ArrayList<String>();
				java.util.List<Double> v = new java.util.ArrayList<Double>();
				if (!Double.isNaN(meant))
				{
					o.add("Meant " + text(meters) + " m along it: " + text(meant) + " deg");
					v.add(meant);
				}
				o.add("Keep it at " + text(Math.abs(read) >= 2 * Math.PI ? turned : Math.toDegrees(read)) + " deg");
				v.add(Math.abs(read) >= 2 * Math.PI ? turned : Math.toDegrees(read));
				o.add("Cancel");
				options = o.toArray(new String[0]);
				values = new double[v.size()];
				for (int i = 0; i < values.length; i++)
					values[i] = v.get(i);
				newUnits = new String[values.length];
				java.util.Arrays.fill(newUnits, "deg");
			}
			int choice = javax.swing.JOptionPane.showOptionDialog(this, "<html><body style='width: 380px'>" + message + "</body></html>", "Fix To Start",
				javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if (choice < 0 || choice >= values.length)
				return;
			unitsKnown = true;
			this.units = newUnits[choice];
			// close enough to the same place
			String precise = String.format(java.util.Locale.US, "%.4f", values[choice]).replaceAll("0+$", "").replaceAll("\\.$", "");
			toStartTextField.setText("-0".equals(precise) ? "0" : precise);
			showToStartUnits();
		}

		private String text(double value)
		{
			String text = String.format(java.util.Locale.US, "%.2f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
			return "-0".equals(text) ? "0" : text;
		}

		/** Explains a field, on it and on its label. */
		private void tip(JLabel label, JTextField field, String... lines)
		{
			String text = "<html>" + String.join("<br>", lines) + "</html>";
			for (JComponent c : new JComponent[] { label, field })
				c.setToolTipText(text);
		}

		/**
		 * Shows To Start's unit, and what the game makes of it. The game reads the
		 * number in its unit (deg becomes rad, ft becomes m) and then uses it as a
		 * length from the segment's start on a straight, and as an angle round it,
		 * in radians, on a curve. Without a unit it is meters on a straight and
		 * radians on a curve; a new camera is saved without one on a straight and
		 * in degrees on a curve. A unit that doesn't fit the segment is in red,
		 * with what the game does with it.
		 */
		private void showToStartUnits()
		{
			Segment segment = getEditorFrame().getSegment(segmentTextField.getText().trim());
			String units = savedUnits(segment);
			String text, tip = null;
			if (segment == null)
				text = unitsKnown && units != null ? units : "m or deg";
			else
			{
				boolean curve = !segment.getType().equals("str");
				boolean angle = "deg".equals(units) || "rad".equals(units);
				double value = toStart();
				text = units == null ? curve ? "rad" : "m" : units;
				if (curve && units != null && !angle)
					tip = "On a curve the game reads To Start as an angle: this " + units
						+ " length is read as radians, so the camera is far from where it should be";
				else if (!curve && angle && value != 0)
					tip = "On a straight the game reads To Start as a length: this angle in " + units
						+ " is turned into radians and read as meters";
				else if (curve && Math.abs(read(value, units)) >= 2 * Math.PI)
					tip = "This is more than a full turn round the curve: the game puts the camera round and round its circle";
			}
			toStartLabel.setText("To Start (" + text + ")");
			toStartLabel.setForeground(tip != null ? java.awt.Color.RED : UIManager.getColor("Label.foreground"));
			String[] lines;
			if (segment == null)
				lines = new String[] { "How far along the segment from its start.",
					"On a straight a length, in meters; on a curve an angle round the curve,",
					"in degrees when saved in degrees (as a new camera is), otherwise in radians.",
					"Empty is 0, the segment's start." };
			else if (segment.getType().equals("str"))
				lines = new String[] { "How far along the straight from its start, in meters.",
					"More than the segment's length puts the camera past its end.",
					"Empty is 0, the segment's start." };
			else
				lines = new String[] { "How far round the curve from the segment's start, as an angle (1 rad = 57.3 deg):",
					"in degrees when saved in degrees (as a new camera is), otherwise in radians.",
					"It is an angle, not a length: the bigger the curve's radius,",
					"the further along the road the same angle is.",
					"More than the curve's arc puts the camera past its end, on the same circle.",
					"Empty is 0, the segment's start." };
			if (tip != null)
			{
				lines = java.util.Arrays.copyOf(lines, lines.length + 2);
				lines[lines.length - 2] = "<font color=red>" + tip + ".</font>";
				lines[lines.length - 1] = "<font color=red>Fix offers what was most likely meant, or the same place in a unit that fits.</font>";
			}
			tip(toStartLabel, toStartTextField, lines);
			fixButton.setVisible(tip != null);
			toStartProblem = tip != null;
			showTabProblem();
		}
	}

	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();

		Vector<Camera> cameras = getEditorFrame().getTrackData().getCameras();
		// each tab keeps the item it shows, so deleting a tab can't move values the
		// tab doesn't show (like the unit of a camera's "to start") to another item
		Vector<Camera> tabItems = new Vector<Camera>();
		for (int i = 0; i < tabbedPane.getTabCount(); i++)
        {
            CameraPanel panel = (CameraPanel) tabbedPane.getComponentAt(i);
            Camera camera = panel.item;

            if (isDifferent(panel.nameTextField.getText(), camera.getName(), stringResult))
            {
                camera.setName(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.commentTextField.getText(), camera.getComment(), stringResult))
            {
                camera.setComment(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.segmentTextField.getText(), camera.getSegment(), stringResult))
            {
                camera.setSegment(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.toRightTextField.getText(), camera.getToRight(), doubleResult))
            {
                camera.setToRight(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.toStartTextField.getText(), camera.getToStart(), doubleResult))
            {
                camera.setToStart(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            // a unit Fix chose
            if (panel.unitsKnown && (!camera.isToStartUnitsKnown() || !java.util.Objects.equals(panel.units, camera.getToStartUnits())))
            {
                camera.setToStartUnits(panel.units);
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.heightTextField.getText(), camera.getHeight(), doubleResult))
            {
                camera.setHeight(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.fovStartTextField.getText(), camera.getFovStart(), stringResult))
            {
                camera.setFovStart(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.fovEndTextField.getText(), camera.getFovEnd(), stringResult))
            {
                camera.setFovEnd(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }
            tabItems.add(camera);
		}
		if (replaceItems(cameras, tabItems))
		{
			getEditorFrame().documentIsModified = true;
		}

		rememberTab();
	}

	/**
	 * Shows the cameras as they are now, after they were changed outside this tab
	 * (the Pit tab renames segments they use, or undoes it). What was typed here
	 * and not yet accepted is lost.
	 */
	public void reload()
	{
		int selected = tabbedPane.getSelectedIndex();
		tabbedPane.removeAll();
		for (Camera camera : getEditorFrame().getTrackData().getCameras())
			tabbedPane.addTab(camera.getName(), null, new CameraPanel(camera), null);
		if (selected >= 0 && selected < tabbedPane.getTabCount())
			tabbedPane.setSelectedIndex(selected);
	}

	/**
	 * @return what's wrong with the segments a camera refers to: the game finds
	 *         them by name, stops loading the track when one isn't set, and uses
	 *         the first segment for a name no segment has
	 */
	public static java.util.List<String> segmentProblems(SegmentVector segments, String camera, String segment,
		String fovStart, String fovEnd)
	{
		java.util.List<String> problems = new java.util.ArrayList<String>();
		String[] names = { segment, fovStart, fovEnd };
		String[] labels = { "segment", "fov start", "fov end" };
		String[] results = { "so the game puts the camera on the first segment",
			"so the game uses the camera from the first segment", "so the game uses the camera up to the first segment" };
		for (int i = 0; i < names.length; i++)
		{
			String name = names[i] == null ? "" : names[i].trim();
			if (name.isEmpty())
				problems.add("Camera " + camera + ": missing " + labels[i] + ": the game won't load the track without it");
			else if (segments.getSegmentFromName(name) == null)
				problems.add("Camera " + camera + ": " + labels[i] + " \"" + name + "\" isn't a segment, " + results[i]);
		}
		return problems;
	}

	/**
	 * Checks the segments the cameras in the dialog refer to, and if some are
	 * missing or aren't segments, asks for the right ones: for each, a list of
	 * the track's segments, the closest name picked, or none.
	 *
	 * @return true to go on (none wrong, fixed, or ignored), false to fix them
	 */
	public boolean checkSegments(java.awt.Component parent, JTabbedPane tabs)
	{
		SegmentVector segments = getEditorFrame().getTrackData().getSegments();
		java.util.List<JTextField> bad = new java.util.ArrayList<JTextField>();
		java.util.List<String> problems = new java.util.ArrayList<String>();
		java.util.List<String> guesses = new java.util.ArrayList<String>();
		int first = -1;
		for (int i = 0; i < tabbedPane.getTabCount(); i++)
		{
			CameraPanel panel = (CameraPanel) tabbedPane.getComponentAt(i);
			JTextField[] fields = { panel.segmentTextField, panel.fovStartTextField, panel.fovEndTextField };
			String[] labels = { "segment", "fov start", "fov end" };
			for (int j = 0; j < fields.length; j++)
			{
				String name = fields[j].getText().trim();
				String camera = "Camera " + panel.nameTextField.getText().trim() + " " + labels[j];
				if (name.isEmpty())
				{
					problems.add(camera + ": missing, the game won't load the track without it");
					guesses.add(missingGuess(panel, j, segments));
				}
				else if (segments.getSegmentFromName(name) == null)
				{
					problems.add(camera + ": \"" + name + "\" isn't a segment");
					guesses.add(closestSegmentName(name, segments));
				}
				else
					continue;
				bad.add(fields[j]);
				if (first < 0)
					first = i;
			}
		}
		if (bad.isEmpty())
			return true;

		tabs.setSelectedComponent(this);
		tabbedPane.setSelectedIndex(first);
		String[] picks = pickSegments(parent, "Camera Segments", problems, guesses, segments, true);
		if (picks == null)
			return false;
		// ignored
		if (picks.length == 0)
			return true;
		for (int i = 0; i < picks.length; i++)
			bad.get(i).setText(picks[i]);
		// none picked for some
		return checkSegments(parent, tabs);
	}

	/**
	 * @return a segment for a camera's missing segment (where it's used from),
	 *         fov start (its segment) or fov end (the segment after its
	 *         segment), or null
	 */
	private static String missingGuess(CameraPanel panel, int field, SegmentVector segments)
	{
		utils.circuit.Segment segment = segments.getSegmentFromName(panel.segmentTextField.getText().trim());
		utils.circuit.Segment fovStart = segments.getSegmentFromName(panel.fovStartTextField.getText().trim());
		if (field == 0)
			return fovStart == null ? null : fovStart.getName();
		if (segment == null)
			segment = fovStart;
		if (segment == null)
			return null;
		if (field == 1)
			return segment.getName();
		return segments.get((segments.indexOf(segment) + 1) % segments.size()).getName();
	}

	/**
	 * Keeps the selected tab for the next time, also when the dialog is canceled or
	 * closed
	 */
	public void rememberTab()
	{
		if (tabbedPane != null)
			getEditorFrame().getProject().setPropertiesEditorCameraTab(tabbedPane.getSelectedIndex());
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
