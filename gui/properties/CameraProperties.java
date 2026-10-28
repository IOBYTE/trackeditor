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
	 * Keeps the selected tab for the next time, also when the dialog is cancelled or
	 * closed
	 */
	public void rememberTab()
	{
		if (tabbedPane != null)
			getEditorFrame().getProject().setPropertiesEditorCameraTab(tabbedPane.getSelectedIndex());
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
