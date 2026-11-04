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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import gui.EditorFrame;
import utils.MutableDouble;
import utils.MutableString;
import utils.SegmentVector;
import utils.circuit.Camera;

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
		private JTextField			segmentTextField	= new JTextField();
		private JLabel				toRightLabel		= new JLabel();
		private JTextField			toRightTextField	= new JTextField();
		private JLabel				toStartLabel		= new JLabel();
		private JTextField			toStartTextField	= new JTextField();
		private JLabel				heightLabel			= new JLabel();
		private JTextField			heightTextField		= new JTextField();
		private JLabel				fovStartLabel		= new JLabel();
		private JTextField			fovStartTextField	= new JTextField();
		private JLabel				fovEndLabel			= new JLabel();
		private JTextField			fovEndTextField		= new JTextField();

		/**
		 *
		 */
		public CameraPanel(Camera camera)
		{
			super();
			this.item = camera;
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
			addLabel(this, 3, toRightLabel, "To Right", 130);
			addLabel(this, 4, toStartLabel, "To Start", 130);
			addLabel(this, 5, heightLabel, "Height", 130);
			addLabel(this, 6, fovStartLabel, "Fov Start", 130);
			addLabel(this, 7, fovEndLabel, "Fov End", 130);

			addTextField(this, 0, nameTextField, camera.getName(), 150, 125);
			markItemName(nameTextField);
			addTextField(this, 1, commentTextField, camera.getComment(), 150, 335);
			addTextField(this, 2, segmentTextField, camera.getSegment(), 150, 125);
			addTextField(this, 3, toRightTextField, camera.getToRight(), 150, 125);
			addTextField(this, 4, toStartTextField, camera.getToStart(), 150, 125);
			addTextField(this, 5, heightTextField, camera.getHeight(), 150, 125);
			addTextField(this, 6, fovStartTextField, camera.getFovStart(), 150, 125);
			addTextField(this, 7, fovEndTextField, camera.getFovEnd(), 150, 125);
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
