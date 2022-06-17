/*
 *   TrackLightProperties.java
 *   Created on 15 June 2022
 *
 *    The TrackLightProperties.java is part of TrackEditor-0.7.0.
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

import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.EditorFrame;
import utils.Editor;
import utils.circuit.TrackLight;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TrackLightProperties extends PropertyPanel
{
	private JButton				addTrackLightButton		= null;
	private JButton				deleteTrackLightButton	= null;
	private JTabbedPane			tabbedPane				= null;

	/**
	 *
	 */
	public TrackLightProperties(EditorFrame frame)
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
		this.add(getAddTrackLightButton(), null);
		this.add(getDeleteTrackLightButton(), null);
	}

	/**
	 * This method initializes addTrackLightpingButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getAddTrackLightButton()
	{
		if (addTrackLightButton == null)
		{
			addTrackLightButton = new JButton();
			addTrackLightButton.setBounds(10, 410, 120, 25);
			addTrackLightButton.setText("Add Track Light");
			addTrackLightButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					String name = "light" + (tabbedPane.getTabCount() + 1);
					TrackLight light = new TrackLight();

					light.setName(name);

					tabbedPane.addTab(name, null, new TrackLightPanel(light), null);
					tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
				}
			});
		}
		return addTrackLightButton;
	}

	/**
	 * This method initializes deleteTrackLightButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteTrackLightButton()
	{
		if (deleteTrackLightButton == null)
		{
			deleteTrackLightButton = new JButton();
			deleteTrackLightButton.setBounds(170, 410, 120, 25);
			deleteTrackLightButton.setText("Delete Track Light");
			deleteTrackLightButton.addActionListener(new java.awt.event.ActionListener()
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
		return deleteTrackLightButton;
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
			tabbedPane.setBounds(10, 10, 460, 390);

			Vector<TrackLight> lights = Editor.getProperties().getTrackLights();

			for (int i = 0; i < lights.size(); i++)
	        {
				TrackLight light = lights.elementAt(i);
				tabbedPane.addTab(light.getName(), null, new TrackLightPanel(light), null);
			}
		}
		return tabbedPane;
	}

	private class TrackLightPanel extends JPanel
	{
		private JLabel				nameLabel				= null;
		private JTextField 			nameTextField			= null;
		private JLabel				roleLabel				= null;
		private JTextField			roleTextField			= null;
		private JLabel				topLeftXLabel			= null;
		private JTextField			topLeftXTextField		= null;
		private JLabel				topLeftYLabel			= null;
		private JTextField			topLeftYTextField		= null;
		private JLabel				topLeftZLabel			= null;
		private JTextField			topLeftZTextField		= null;
		private JLabel				bottomRightXLabel		= null;
		private JTextField			bottomRightXTextField	= null;
		private JLabel				bottomRightYLabel		= null;
		private JTextField			bottomRightYTextField	= null;
		private JLabel				bottomRightZLabel		= null;
		private JTextField			bottomRightZTextField	= null;
		private JLabel				textureOnLabel			= null;
		private JTextField			textureOnTextField		= null;
		private JButton				textureOnButton			= null;
		private JLabel				textureOffLabel			= null;
		private JTextField			textureOffTextField		= null;
		private JButton				textureOffButton		= null;
		private JLabel				indexLabel				= null;
		private JTextField			indexTextField			= null;
		private JLabel				redLabel				= null;
		private JTextField			redTextField			= null;
		private JLabel				greenLabel				= null;
		private JTextField			greenTextField			= null;
		private JLabel				blueLabel				= null;
		private JTextField			blueTextField			= null;

		private final String sep = System.getProperty("file.separator");

		/**
		 *
		 */
		public TrackLightPanel(TrackLight light)
		{
			super();
			initialize(light);
		}

		/**
		 *
		 */
		private void initialize(TrackLight light)
		{
			nameLabel = new JLabel();
			roleLabel = new JLabel();
			topLeftXLabel = new JLabel();
			topLeftYLabel = new JLabel();
			topLeftZLabel = new JLabel();
			bottomRightXLabel = new JLabel();
			bottomRightYLabel = new JLabel();
			bottomRightZLabel = new JLabel();
			textureOnLabel = new JLabel();
			textureOffLabel = new JLabel();
			indexLabel = new JLabel();
			redLabel = new JLabel();
			greenLabel = new JLabel();
			blueLabel = new JLabel();
			nameLabel.setBounds(10, 10, 160, 20);
			nameLabel.setText("Name");
			roleLabel.setBounds(10, 35, 160, 20);
			roleLabel.setText("Role");
			topLeftXLabel.setBounds(10, 60, 160, 20);
			topLeftXLabel.setText("Top Lext X");
			topLeftYLabel.setBounds(10, 85, 160, 20);
			topLeftYLabel.setText("Top Left Right");
			topLeftZLabel.setBounds(10, 110, 160, 20);
			topLeftZLabel.setText("Top Left Z");
			bottomRightXLabel.setBounds(10, 135, 160, 20);
			bottomRightXLabel.setText("Bottom Right X");
			bottomRightYLabel.setBounds(10, 160, 160, 20);
			bottomRightYLabel.setText("Bottom Right Y");
			bottomRightZLabel.setBounds(10, 185, 160, 20);
			bottomRightZLabel.setText("Bottom Right Z");
			textureOnLabel.setBounds(10, 210, 160, 20);
			textureOnLabel.setText("Texture On");
			textureOffLabel.setBounds(10, 235, 160, 20);
			textureOffLabel.setText("Texture Off");
			indexLabel.setBounds(10, 260, 160, 20);
			indexLabel.setText("Index");
			redLabel.setBounds(10, 285, 160, 20);
			redLabel.setText("Red");
			greenLabel.setBounds(10, 310, 160, 20);
			greenLabel.setText("Green");
			blueLabel.setBounds(10, 335, 160, 20);
			blueLabel.setText("Blue");
			add(nameLabel);
			add(roleLabel);
			add(topLeftXLabel);
			add(topLeftYLabel);
			add(topLeftZLabel);
			add(bottomRightXLabel);
			add(bottomRightYLabel);
			add(bottomRightZLabel);
			add(textureOnLabel);
			add(textureOffLabel);
			add(indexLabel);
			add(redLabel);
			add(greenLabel);
			add(blueLabel);
			setLayout(null);
			add(getNameTextField(), null);
			add(getRoleTextField(), null);
			add(getTopLeftXTextField(), null);
			add(getTopLeftYTextField(), null);
			add(getTopLeftZTextField(), null);
			add(getBottomRightXTextField(), null);
			add(getBottomRightYTextField(), null);
			add(getBottomRightZTextField(), null);
			add(getTextureOnTextField(), null);
			add(getTextureOffTextField(), null);
			add(getIndexTextField(), null);
			add(getRedTextField(), null);
			add(getGreenTextField(), null);
			add(getBlueTextField(), null);
			add(getTextureOnButton(), null);
			add(getTextureOffButton(), null);

			getNameTextField().setText(light.getName());
			getRoleTextField().setText(light.getRole());
			getTopLeftXTextField().setText(getString(light.getTopLeftX()));
			getTopLeftYTextField().setText(getString(light.getTopLeftY()));
			getTopLeftZTextField().setText(getString(light.getTopLeftZ()));
			getBottomRightXTextField().setText(getString(light.getBottomRightX()));
			getBottomRightYTextField().setText(getString(light.getBottomRightY()));
			getBottomRightZTextField().setText(getString(light.getBottomRightZ()));
			getTextureOnTextField().setText(light.getTextureOn());
			getTextureOffTextField().setText(light.getTextureOff());
			getIndexTextField().setText(getString(light.getIndex()));
			getRedTextField().setText(getString(light.getRed()));
			getGreenTextField().setText(getString(light.getGreen()));
			getBlueTextField().setText(getString(light.getBlue()));
		}

		private String getString(double value)
		{
			if (!Double.isNaN(value))
				return String.valueOf(value);

			return null;
		}
		private String getString(int value)
		{
			if (value != Integer.MAX_VALUE)
				return Integer.toString(value);

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

		public JTextField getRoleTextField()
		{
			if (roleTextField == null)
			{
				roleTextField = new JTextField();
				roleTextField.setBounds(120, 35, 100, 20);
			}
			return roleTextField;
		}

		public JTextField getTopLeftXTextField()
		{
			if (topLeftXTextField == null)
			{
				topLeftXTextField = new JTextField();
				topLeftXTextField.setBounds(120, 60, 100, 20);
			}
			return topLeftXTextField;
		}

		public JTextField getTopLeftYTextField()
		{
			if (topLeftYTextField == null)
			{
				topLeftYTextField = new JTextField();
				topLeftYTextField.setBounds(120, 85, 100, 20);
			}
			return topLeftYTextField;
		}

		public JTextField getTopLeftZTextField()
		{
			if (topLeftZTextField == null)
			{
				topLeftZTextField = new JTextField();
				topLeftZTextField.setBounds(120, 110, 100, 20);
			}
			return topLeftZTextField;
		}

		public JTextField getBottomRightXTextField()
		{
			if (bottomRightXTextField == null)
			{
				bottomRightXTextField = new JTextField();
				bottomRightXTextField.setBounds(120, 135, 100, 20);
			}
			return bottomRightXTextField;
		}

		public JTextField getBottomRightYTextField()
		{
			if (bottomRightYTextField == null)
			{
				bottomRightYTextField = new JTextField();
				bottomRightYTextField.setBounds(120, 160, 100, 20);
			}
			return bottomRightYTextField;
		}

		public JTextField getBottomRightZTextField()
		{
			if (bottomRightZTextField == null)
			{
				bottomRightZTextField = new JTextField();
				bottomRightZTextField.setBounds(120, 185, 100, 20);
			}
			return bottomRightZTextField;
		}

		public JTextField getTextureOnTextField()
		{
			if (textureOnTextField == null)
			{
				textureOnTextField = new JTextField();
				textureOnTextField.setBounds(120, 210, 240, 20);
			}
			return textureOnTextField;
		}

		private JButton getTextureOnButton()
		{
			if (textureOnButton == null)
			{
				textureOnButton = new JButton();
				textureOnButton.setBounds(370, 208, 80, 25);
				textureOnButton.setText("Browse");
				textureOnButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						textureOnFile();
					}
				});
			}
			return textureOnButton;
		}

		public JTextField getTextureOffTextField()
		{
			if (textureOffTextField == null)
			{
				textureOffTextField = new JTextField();
				textureOffTextField.setBounds(120, 235, 240, 20);
			}
			return textureOffTextField;
		}

		private JButton getTextureOffButton()
		{
			if (textureOffButton == null)
			{
				textureOffButton = new JButton();
				textureOffButton.setBounds(370, 233, 80, 25);
				textureOffButton.setText("Browse");
				textureOffButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						textureOffFile();
					}
				});
			}
			return textureOffButton;
		}

		public JTextField getIndexTextField()
		{
			if (indexTextField == null)
			{
				indexTextField = new JTextField();
				indexTextField.setBounds(120, 260, 100, 20);
			}
			return indexTextField;
		}

		public JTextField getRedTextField()
		{
			if (redTextField == null)
			{
				redTextField = new JTextField();
				redTextField.setBounds(120, 285, 100, 20);
			}
			return redTextField;
		}

		public JTextField getGreenTextField()
		{
			if (greenTextField == null)
			{
				greenTextField = new JTextField();
				greenTextField.setBounds(120, 310, 100, 20);
			}
			return greenTextField;
		}

		public JTextField getBlueTextField()
		{
			if (blueTextField == null)
			{
				blueTextField = new JTextField();
				blueTextField.setBounds(120, 335, 100, 20);
			}
			return blueTextField;
		}

		protected void textureOnFile()
		{
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFiles(null);
			fc.setSelectedFile(null);
			fc.rescanCurrentDirectory();
			fc.setApproveButtonMnemonic(0);
			fc.setDialogTitle("Texture On image file selection");
			fc.setVisible(true);
			fc.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("RGB and PNG images", "rgb", "png");
			fc.addChoosableFileFilter(filter);
			fc.setCurrentDirectory(new File(Editor.getProperties().getPath()));
			int result = fc.showDialog(this, "Ok");
			if (result == JFileChooser.APPROVE_OPTION)
			{
				String fileName = fc.getSelectedFile().toString();
				int index = fileName.lastIndexOf(sep);
				String pathToFile = fileName.substring(0, index);
				if (pathToFile.equals(Editor.getProperties().getPath()))
					fileName = fileName.substring(index + 1);
				getTextureOnTextField().setText(fileName);
			}
		}

		protected void textureOffFile()
		{
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFiles(null);
			fc.setSelectedFile(null);
			fc.rescanCurrentDirectory();
			fc.setApproveButtonMnemonic(0);
			fc.setDialogTitle("Texture Off image file selection");
			fc.setVisible(true);
			fc.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("RGB and PNG images", "rgb", "png");
			fc.addChoosableFileFilter(filter);
			fc.setCurrentDirectory(new File(Editor.getProperties().getPath()));
			int result = fc.showDialog(this, "Ok");
			if (result == JFileChooser.APPROVE_OPTION)
			{
				String fileName = fc.getSelectedFile().toString();
				int index = fileName.lastIndexOf(sep);
				String pathToFile = fileName.substring(0, index);
				if (pathToFile.equals(Editor.getProperties().getPath()))
					fileName = fileName.substring(index + 1);
				getTextureOffTextField().setText(fileName);
			}
		}
	}

	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();
		MutableInteger integerResult = new MutableInteger();

		Vector<TrackLight> lights = Editor.getProperties().getTrackLights();
		int minCount = Math.min(lights.size(), tabbedPane.getTabCount());
		if (lights.size() != tabbedPane.getTabCount())
		{
			frame.documentIsModified = true;
		}
		for (int i = 0; i < minCount; i++)
        {
            TrackLight light = lights.elementAt(i);
            TrackLightPanel panel = (TrackLightPanel) tabbedPane.getComponentAt(i);

            if (isDifferent(panel.getNameTextField().getText(), light.getName(), stringResult))
            {
                light.setName(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getRoleTextField().getText(), light.getRole(), stringResult))
            {
                light.setRole(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getTopLeftXTextField().getText(), light.getTopLeftX(), doubleResult))
            {
                light.setTopLeftX(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getTopLeftYTextField().getText(), light.getTopLeftY(), doubleResult))
            {
                light.setTopLeftY(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getTopLeftZTextField().getText(), light.getTopLeftZ(), doubleResult))
            {
                light.setTopLeftZ(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getTextureOnTextField().getText(), light.getTextureOn(), stringResult))
            {
                light.setTextureOn(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getTextureOffTextField().getText(), light.getTextureOff(), stringResult))
            {
                light.setTextureOff(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getIndexTextField().getText(), light.getIndex(), integerResult))
            {
                light.setIndex(integerResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getRedTextField().getText(), light.getRed(), doubleResult))
            {
                light.setRed(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getGreenTextField().getText(), light.getGreen(), doubleResult))
            {
                light.setGreen(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getBlueTextField().getText(), light.getBlue(), doubleResult))
            {
                light.setBlue(doubleResult.getValue());
                frame.documentIsModified = true;
            }
		}
		if (lights.size() > tabbedPane.getTabCount())
		{
			// need to trim envMaps
			while (lights.size() > tabbedPane.getTabCount())
			{
				lights.remove(lights.size() - 1);
			}
		}
		else if (lights.size() < tabbedPane.getTabCount())
		{
			// need to add to environmentMaps
			while (lights.size() < tabbedPane.getTabCount())
			{
	            TrackLightPanel panel = (TrackLightPanel) tabbedPane.getComponentAt(lights.size());
				TrackLight light = new TrackLight();

				light.setName(panel.getNameTextField().getText());
				light.setRole(panel.getRoleTextField().getText());
				light.setTopLeftX(getDouble(panel.getTopLeftXTextField().getText()));
				light.setTopLeftY(getDouble(panel.getTopLeftYTextField().getText()));
				light.setTopLeftZ(getDouble(panel.getTopLeftZTextField().getText()));
				light.setBottomRightX(getDouble(panel.getBottomRightXTextField().getText()));
				light.setBottomRightY(getDouble(panel.getBottomRightYTextField().getText()));
				light.setBottomRightZ(getDouble(panel.getBottomRightZTextField().getText()));
				light.setTextureOn(panel.getTextureOnTextField().getText());
				light.setTextureOff(panel.getTextureOffTextField().getText());
				light.setIndex(getInteger(panel.getIndexTextField().getText()));
				light.setRed(getDouble(panel.getRedTextField().getText()));
				light.setGreen(getDouble(panel.getGreenTextField().getText()));
				light.setBlue(getDouble(panel.getBlueTextField().getText()));

				lights.add(light);
			}
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
