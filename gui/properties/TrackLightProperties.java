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
		private JLabel				nameLabel				= new JLabel();
		private JTextField 			nameTextField			= new JTextField();
		private JLabel				roleLabel				= new JLabel();
		private JTextField			roleTextField			= new JTextField();
		private JLabel				topLeftXLabel			= new JLabel();
		private JTextField			topLeftXTextField		= new JTextField();
		private JLabel				topLeftYLabel			= new JLabel();
		private JTextField			topLeftYTextField		= new JTextField();
		private JLabel				topLeftZLabel			= new JLabel();
		private JTextField			topLeftZTextField		= new JTextField();
		private JLabel				bottomRightXLabel		= new JLabel();
		private JTextField			bottomRightXTextField	= new JTextField();
		private JLabel				bottomRightYLabel		= new JLabel();
		private JTextField			bottomRightYTextField	= new JTextField();
		private JLabel				bottomRightZLabel		= new JLabel();
		private JTextField			bottomRightZTextField	= new JTextField();
		private JLabel				textureOnLabel			= new JLabel();
		private JTextField			textureOnTextField		= new JTextField();
		private JButton				textureOnButton			= null;
		private JLabel				textureOffLabel			= new JLabel();
		private JTextField			textureOffTextField		= new JTextField();
		private JButton				textureOffButton		= null;
		private JLabel				indexLabel				= new JLabel();
		private JTextField			indexTextField			= new JTextField();
		private JLabel				redLabel				= new JLabel();
		private JTextField			redTextField			= new JTextField();
		private JLabel				greenLabel				= new JLabel();
		private JTextField			greenTextField			= new JTextField();
		private JLabel				blueLabel				= new JLabel();
		private JTextField			blueTextField			= new JTextField();

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
			setLayout(null);

			addLabel(this, 0, nameLabel, "Name", 160);
			addLabel(this, 1, roleLabel, "Role", 160);
			addLabel(this, 2, topLeftXLabel, "Top Lext X", 160);
			addLabel(this, 3, topLeftYLabel, "Top Left RY", 160);
			addLabel(this, 4, topLeftZLabel, "Top Left Z", 160);
			addLabel(this, 5, bottomRightXLabel, "Bottom Right X", 160);
			addLabel(this, 6, bottomRightYLabel, "Bottom Right Y", 160);
			addLabel(this, 7, bottomRightZLabel, "Bottom Right Z", 160);
			addLabel(this, 8, textureOnLabel, "Texture On", 160);
			addLabel(this, 9, textureOffLabel, "Texture Off", 160);
			addLabel(this, 10, indexLabel, "Index", 160);
			addLabel(this, 11, redLabel, "Red", 160);
			addLabel(this, 12, greenLabel, "Green", 160);
			addLabel(this, 13, blueLabel, "Blue", 160);

			addTextField(this, 0, nameTextField, light.getName(), 120, 100);
			addTextField(this, 1, roleTextField, light.getRole(), 120, 100);
			addTextField(this, 2, topLeftXTextField, getString(light.getTopLeftX()), 120, 100);
			addTextField(this, 3, topLeftYTextField, getString(light.getTopLeftY()), 120, 100);
			addTextField(this, 4, topLeftZTextField, getString(light.getTopLeftZ()), 120, 100);
			addTextField(this, 5, bottomRightXTextField, getString(light.getBottomRightX()), 120, 100);
			addTextField(this, 6, bottomRightYTextField, getString(light.getBottomRightY()), 120, 100);
			addTextField(this, 7, bottomRightZTextField, getString(light.getBottomRightZ()), 120, 100);
			addTextField(this, 8, textureOnTextField, light.getTextureOn(), 120, 240);
			addTextField(this, 9, textureOffTextField, light.getTextureOff(), 120, 240);
			addTextField(this, 10, indexTextField, getString(light.getIndex()), 120, 100);
			addTextField(this, 11, redTextField, getString(light.getRed()), 120, 100);
			addTextField(this, 12, greenTextField, getString(light.getGreen()), 120, 100);
			addTextField(this, 13, blueTextField, getString(light.getBlue()), 120, 100);

			add(getTextureOnButton(), null);
			add(getTextureOffButton(), null);
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
				textureOnTextField.setText(fileName);
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
				textureOffTextField.setText(fileName);
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

            if (isDifferent(panel.nameTextField.getText(), light.getName(), stringResult))
            {
                light.setName(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.roleTextField.getText(), light.getRole(), stringResult))
            {
                light.setRole(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.topLeftXTextField.getText(), light.getTopLeftX(), doubleResult))
            {
                light.setTopLeftX(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.topLeftYTextField.getText(), light.getTopLeftY(), doubleResult))
            {
                light.setTopLeftY(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.topLeftZTextField.getText(), light.getTopLeftZ(), doubleResult))
            {
                light.setTopLeftZ(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.bottomRightXTextField.getText(), light.getBottomRightX(), doubleResult))
            {
                light.setBottomRightX(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.bottomRightYTextField.getText(), light.getBottomRightY(), doubleResult))
            {
                light.setBottomRightY(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.bottomRightZTextField.getText(), light.getBottomRightZ(), doubleResult))
            {
                light.setBottomRightZ(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.textureOnTextField.getText(), light.getTextureOn(), stringResult))
            {
                light.setTextureOn(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.textureOffTextField.getText(), light.getTextureOff(), stringResult))
            {
                light.setTextureOff(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.indexTextField.getText(), light.getIndex(), integerResult))
            {
                light.setIndex(integerResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.redTextField.getText(), light.getRed(), doubleResult))
            {
                light.setRed(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.greenTextField.getText(), light.getGreen(), doubleResult))
            {
                light.setGreen(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.blueTextField.getText(), light.getBlue(), doubleResult))
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

				light.setName(panel.nameTextField.getText());
				light.setRole(panel.roleTextField.getText());
				light.setTopLeftX(getDouble(panel.topLeftXTextField.getText()));
				light.setTopLeftY(getDouble(panel.topLeftYTextField.getText()));
				light.setTopLeftZ(getDouble(panel.topLeftZTextField.getText()));
				light.setBottomRightX(getDouble(panel.bottomRightXTextField.getText()));
				light.setBottomRightY(getDouble(panel.bottomRightYTextField.getText()));
				light.setBottomRightZ(getDouble(panel.bottomRightZTextField.getText()));
				light.setTextureOn(panel.textureOnTextField.getText());
				light.setTextureOff(panel.textureOffTextField.getText());
				light.setIndex(getInteger(panel.indexTextField.getText()));
				light.setRed(getDouble(panel.redTextField.getText()));
				light.setGreen(getDouble(panel.greenTextField.getText()));
				light.setBlue(getDouble(panel.blueTextField.getText()));

				lights.add(light);
			}
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
