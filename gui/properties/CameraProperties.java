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

import gui.EditorFrame;
import utils.Editor;
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
	public CameraProperties(EditorFrame frame)
	{
		super(frame);
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
			addCameraButton.setBounds(10, 230, 100, 25);
			addCameraButton.setText("Add Camera");
			addCameraButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					String name = "camera" + (tabbedPane.getTabCount() + 1);
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
			deleteCameraButton.setBounds(130, 230, 120, 25);
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
			tabbedPane.setBounds(10, 10, 460, 210);

			Vector<Camera> cameras = Editor.getProperties().getCameras();

			for (int i = 0; i < cameras.size(); i++)
	        {
				Camera camera = cameras.elementAt(i);
				tabbedPane.addTab(camera.getName(), null, new CameraPanel(camera), null);
			}
		}
		return tabbedPane;
	}

	private class CameraPanel extends JPanel
	{
		private JLabel				nameLabel			= null;
		private JTextField 			nameTextField		= null;
		private JLabel				segmentLabel		= null;
		private JTextField			segmentTextField	= null;
		private JLabel				toRightLabel		= null;
		private JTextField			toRightTextField	= null;
		private JLabel				toStartLabel		= null;
		private JTextField			toStartTextField	= null;
		private JLabel				heightLabel			= null;
		private JTextField			heightTextField		= null;
		private JLabel				fovStartLabel		= null;
		private JTextField			fovStartTextField	= null;
		private JLabel				fovEndLabel			= null;
		private JTextField			fovEndTextField		= null;

		/**
		 *
		 */
		public CameraPanel(Camera camera)
		{
			super();
			initialize(camera);
		}

		/**
		 *
		 */
		private void initialize(Camera camera)
		{
			nameLabel = new JLabel();
			segmentLabel = new JLabel();
			toRightLabel = new JLabel();
			toStartLabel = new JLabel();
			heightLabel = new JLabel();
			fovStartLabel = new JLabel();
			fovEndLabel = new JLabel();
			nameLabel.setBounds(10, 10, 160, 20);
			nameLabel.setText("Name");
			segmentLabel.setBounds(10, 35, 160, 20);
			segmentLabel.setText("Segment");
			toRightLabel.setBounds(10, 60, 160, 20);
			toRightLabel.setText("To Right");
			toStartLabel.setBounds(10, 85, 160, 20);
			toStartLabel.setText("To Start");
			heightLabel.setBounds(10, 110, 160, 20);
			heightLabel.setText("Height");
			fovStartLabel.setBounds(10, 135, 160, 20);
			fovStartLabel.setText("Fov Start");
			fovEndLabel.setBounds(10, 160, 160, 20);
			fovEndLabel.setText("Fov End");
			add(nameLabel);
			add(segmentLabel);
			add(toRightLabel);
			add(toStartLabel);
			add(heightLabel);
			add(fovStartLabel);
			add(fovEndLabel);
			setLayout(null);
			add(getNameTextField(), null);
			add(getSegmentTextField(), null);
			add(getToRightTextField(), null);
			add(getToStartTextField(), null);
			add(getHeightTextField(), null);
			add(getFovStartTextField(), null);
			add(getFovEndTextField(), null);

			getNameTextField().setText(camera.getName());
			getSegmentTextField().setText(camera.getSegment());
			getToRightTextField().setText(getString(camera.getToRight()));
			getToStartTextField().setText(getString(camera.getToStart()));
			getHeightTextField().setText(getString(camera.getHeight()));
			getFovStartTextField().setText(camera.getFovStart());
			getFovEndTextField().setText(camera.getFovEnd());
		}

		private String getString(double value)
		{
			if (!Double.isNaN(value))
				return String.valueOf(value);

			return null;
		}

		/**
		 * This method initializes nameTextField
		 *
		 * @return javax.swing.JTextField
		 */
		public JTextField getNameTextField()
		{
			if (nameTextField == null)
			{
				nameTextField = new JTextField();
				nameTextField.setBounds(120, 10, 100, 20);
			}
			return nameTextField;
		}

		public JTextField getSegmentTextField()
		{
			if (segmentTextField == null)
			{
				segmentTextField = new JTextField();
				segmentTextField.setBounds(120, 35, 100, 20);
			}
			return segmentTextField;
		}

		public JTextField getToRightTextField()
		{
			if (toRightTextField == null)
			{
				toRightTextField = new JTextField();
				toRightTextField.setBounds(120, 60, 100, 20);
			}
			return toRightTextField;
		}

		public JTextField getToStartTextField()
		{
			if (toStartTextField == null)
			{
				toStartTextField = new JTextField();
				toStartTextField.setBounds(120, 85, 100, 20);
			}
			return toStartTextField;
		}

		public JTextField getHeightTextField()
		{
			if (heightTextField == null)
			{
				heightTextField = new JTextField();
				heightTextField.setBounds(120, 110, 100, 20);
			}
			return heightTextField;
		}

		public JTextField getFovStartTextField()
		{
			if (fovStartTextField == null)
			{
				fovStartTextField = new JTextField();
				fovStartTextField.setBounds(120, 135, 100, 20);
			}
			return fovStartTextField;
		}

		public JTextField getFovEndTextField()
		{
			if (fovEndTextField == null)
			{
				fovEndTextField = new JTextField();
				fovEndTextField.setBounds(120, 160, 100, 20);
			}
			return fovEndTextField;
		}
	}

	/**
	 *
	 */
	public void exit()
	{
		String stringResult = null;
		MutableDouble doubleResult = new MutableDouble(Double.NaN);
		Vector<Camera> cameras = Editor.getProperties().getCameras();
		int minCount = Integer.min(cameras.size(), tabbedPane.getTabCount());
		if (cameras.size() != tabbedPane.getTabCount())
		{
			frame.documentIsModified = true;
		}
		for (int i = 0; i < minCount; i++)
        {
            Camera camera = cameras.elementAt(i);
            CameraPanel panel = (CameraPanel) tabbedPane.getComponentAt(i);

            if (isDifferent(panel.getNameTextField().getText(), camera.getName(), stringResult))
            {
                camera.setName(panel.getNameTextField().getText());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getSegmentTextField().getText(), camera.getSegment(), stringResult))
            {
                camera.setSegment(panel.getSegmentTextField().getText());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getToRightTextField().getText(), camera.getToRight(), doubleResult))
            {
                camera.setToRight(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getToStartTextField().getText(), camera.getToStart(), doubleResult))
            {
                camera.setToStart(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getHeightTextField().getText(), camera.getHeight(), doubleResult))
            {
                camera.setHeight(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getFovStartTextField().getText(), camera.getFovStart(), stringResult))
            {
                camera.setFovStart(stringResult);
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getFovEndTextField().getText(), camera.getFovEnd(), stringResult))
            {
                camera.setFovEnd(stringResult);
                frame.documentIsModified = true;
            }
		}
		if (cameras.size() > tabbedPane.getTabCount())
		{
			// need to trim envMaps
			while (cameras.size() > tabbedPane.getTabCount())
			{
				cameras.remove(cameras.size() - 1);
			}
		}
		else if (cameras.size() < tabbedPane.getTabCount())
		{
			// need to add to environmentMaps
			while (cameras.size() < tabbedPane.getTabCount())
			{
	            CameraPanel panel = (CameraPanel) tabbedPane.getComponentAt(cameras.size());
				Camera camera = new Camera();

				camera.setName(panel.getNameTextField().getText());
				camera.setSegment(panel.getSegmentTextField().getText());
				camera.setToRight(getDouble(panel.getToRightTextField().getText()));
				camera.setToStart(getDouble(panel.getToStartTextField().getText()));
				camera.setHeight(getDouble(panel.getHeightTextField().getText()));
				camera.setFovStart(panel.getFovStartTextField().getText());
				camera.setFovEnd(panel.getFovEndTextField().getText());

				cameras.add(camera);
			}
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
