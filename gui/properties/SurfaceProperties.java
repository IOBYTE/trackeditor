/*
 *   SurfaceProperties.java
 *   Created on 14 June 2022
 *
 *    The SurfaceProperties.java is part of TrackEditor-0.7.0.
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
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.EditorFrame;
import utils.Editor;
import utils.circuit.Surface;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SurfaceProperties extends PropertyPanel
{
	private JButton				addSurfaceButton	= null;
	private JButton				deleteSurfaceButton	= null;
	private JTabbedPane			tabbedPane			= null;

	/**
	 *
	 */
	public SurfaceProperties(EditorFrame frame)
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
		this.add(getAddSurfaceButton(), null);
		this.add(getDeleteSurfaceButton(), null);
	}

	/**
	 * This method initializes addSurfacepingButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getAddSurfaceButton()
	{
		if (addSurfaceButton == null)
		{
			addSurfaceButton = new JButton();
			addSurfaceButton.setBounds(10, 610, 100, 25);
			addSurfaceButton.setText("Add Surface");
			addSurfaceButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					String name = "surface" + (tabbedPane.getTabCount() + 1);
					Surface surface = new Surface();

					surface.setName(name);

					tabbedPane.addTab(name, null, new SurfacePanel(surface), null);
					tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
				}
			});
		}
		return addSurfaceButton;
	}

	/**
	 * This method initializes deleteSurfaceButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteSurfaceButton()
	{
		if (deleteSurfaceButton == null)
		{
			deleteSurfaceButton = new JButton();
			deleteSurfaceButton.setBounds(130, 610, 120, 25);
			deleteSurfaceButton.setText("Delete Surface");
			deleteSurfaceButton.addActionListener(new java.awt.event.ActionListener()
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
		return deleteSurfaceButton;
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
			tabbedPane.setBounds(10, 10, 460, 590);

			Vector<Surface> surfaces = Editor.getProperties().getSurfaces();

			for (int i = 0; i < surfaces.size(); i++)
	        {
				Surface surface = surfaces.elementAt(i);
				tabbedPane.addTab(surface.getName(), null, new SurfacePanel(surface), null);
			}
		}
		return tabbedPane;
	}

	private class SurfacePanel extends JPanel
	{
		private JLabel				nameLabel						= null;
		private JTextField 			nameTextField					= null;
		private JLabel				colorR1Label					= null;
		private JTextField			colorR1TextField				= null;
		private JLabel				colorG1Label					= null;
		private JTextField			colorG1TextField				= null;
		private JLabel				colorB1Label					= null;
		private JTextField			colorB1TextField				= null;
		private JLabel				colorR2Label					= null;
		private JTextField			colorR2TextField				= null;
		private JLabel				colorG2Label					= null;
		private JTextField			colorG2TextField				= null;
		private JLabel				colorB2Label					= null;
		private JTextField			colorB2TextField				= null;
		private JLabel				textureNameLabel				= null;
		private JTextField			textureNameTextField			= null;
		private JLabel				textureTypeLabel				= null;
		private JComboBox<String>	textureTypeComboBox				= null;
		private JLabel				textureSizeLabel				= null;
		private JTextField			textureSizeTextField			= null;
		private JLabel				textureLinkWithPreviousLabel	= null;
		private JComboBox<String>	textureLinkWithPreviousComboBox	= null;
		private JLabel				textureStartOnBoundaryLabel		= null;
		private JComboBox<String>	textureStartOnBoundaryComboBox	= null;
		private JLabel				textureMipMapLabel				= null;
		private JTextField			textureMipMapTextField			= null;
		private JLabel				frictionLabel					= null;
		private JTextField 			frictionTextField				= null;
		private JLabel				rollingResistanceLabel			= null;
		private JTextField 			rollingResistanceTextField		= null;
		private JLabel				bumpNameLabel					= null;
		private JTextField 			bumpNameTextField				= null;
		private JLabel				bumpSizeLabel					= null;
		private JTextField 			bumpSizeTextField				= null;
		private JLabel				roughnessLabel					= null;
		private JTextField 			roughnessTextField				= null;
		private JLabel				roughnessWavelengthLabel		= null;
		private JTextField 			roughnessWavelengthTextField	= null;
		private JLabel				racelineNameLabel				= null;
		private JTextField 			racelineNameTextField			= null;
		private JLabel				damageLabel						= null;
		private JTextField 			damageTextField					= null;
		private JLabel				reboundLabel					= null;
		private JTextField 			reboundTextField				= null;
		private JButton				browseButton					= null;

		private final String sep = System.getProperty("file.separator");

		/**
		 *
		 */
		public SurfacePanel(Surface surface)
		{
			super();
			initialize(surface);
		}

		/**
		 *
		 */
		private void initialize(Surface surface)
		{
			nameLabel = new JLabel();
			colorR1Label = new JLabel();
			colorG1Label = new JLabel();
			colorB1Label = new JLabel();
			colorR2Label = new JLabel();
			colorG2Label = new JLabel();
			colorB2Label = new JLabel();
			textureNameLabel = new JLabel();
			textureTypeLabel = new JLabel();
			textureSizeLabel = new JLabel();
			textureLinkWithPreviousLabel = new JLabel();
			textureStartOnBoundaryLabel = new JLabel();
			textureMipMapLabel = new JLabel();
			frictionLabel = new JLabel();
			rollingResistanceLabel = new JLabel();
			bumpNameLabel = new JLabel();
			bumpSizeLabel = new JLabel();
			roughnessLabel = new JLabel();
			roughnessWavelengthLabel = new JLabel();
			racelineNameLabel = new JLabel();
			damageLabel = new JLabel();
			reboundLabel = new JLabel();

			nameLabel.setBounds(10, 10, 160, 20);
			nameLabel.setText("Name");
			colorR1Label.setBounds(10, 35, 160, 20);
			colorR1Label.setText("Color R1");
			colorG1Label.setBounds(10, 60, 160, 20);
			colorG1Label.setText("Color G1");
			colorB1Label.setBounds(10, 85, 160, 20);
			colorB1Label.setText("Color B1");
			colorR2Label.setBounds(10, 110, 160, 20);
			colorR2Label.setText("Color R2");
			colorG2Label.setBounds(10, 135, 160, 20);
			colorG2Label.setText("Color G2");
			colorB2Label.setBounds(10, 160, 160, 20);
			colorB2Label.setText("Color B2");
			textureNameLabel.setBounds(10, 185, 160, 20);
			textureNameLabel.setText("Texture Name");
			textureTypeLabel.setBounds(10, 210, 160, 20);
			textureTypeLabel.setText("Texture Type");
			textureSizeLabel.setBounds(10, 235, 160, 20);
			textureSizeLabel.setText("Texture Size");
			textureLinkWithPreviousLabel.setBounds(10, 260, 160, 20);
			textureLinkWithPreviousLabel.setText("Texture Link With Previous");
			textureStartOnBoundaryLabel.setBounds(10, 285, 160, 20);
			textureStartOnBoundaryLabel.setText("Texture Start On Boundary");
			textureMipMapLabel.setBounds(10, 310, 160, 20);
			textureMipMapLabel.setText("Texture MipMap");
			frictionLabel.setBounds(10, 335, 160, 20);
			frictionLabel.setText("Friction");
			rollingResistanceLabel.setBounds(10, 360, 160, 20);
			rollingResistanceLabel.setText("Rolling Resistance");
			bumpNameLabel.setBounds(10, 385, 160, 20);
			bumpNameLabel.setText("Bump Name");
			bumpSizeLabel.setBounds(10, 410, 160, 20);
			bumpSizeLabel.setText("Bump Size");
			roughnessLabel.setBounds(10, 435, 160, 20);
			roughnessLabel.setText("Roughness");
			roughnessWavelengthLabel.setBounds(10, 460, 160, 20);
			roughnessWavelengthLabel.setText("Roughness Wavelength");
			racelineNameLabel.setBounds(10, 485, 160, 20);
			racelineNameLabel.setText("Raceline Name");
			damageLabel.setBounds(10, 510, 160, 20);
			damageLabel.setText("Damage");
			reboundLabel.setBounds(10, 535, 160, 20);
			reboundLabel.setText("Rebound");

			add(nameLabel);
			add(colorR1Label);
			add(colorG1Label);
			add(colorB1Label);
			add(colorR2Label);
			add(colorG2Label);
			add(colorB2Label);
			add(textureNameLabel);
			add(textureTypeLabel);
			add(textureSizeLabel);
			add(textureLinkWithPreviousLabel);
			add(textureStartOnBoundaryLabel);
			add(textureMipMapLabel);
			add(frictionLabel);
			add(rollingResistanceLabel);
			add(bumpNameLabel);
			add(bumpSizeLabel);
			add(roughnessLabel);
			add(roughnessWavelengthLabel);
			add(racelineNameLabel);
			add(damageLabel);
			add(reboundLabel);
			setLayout(null);
			add(getNameTextField(), null);
			add(getColorR1TextField(), null);
			add(getColorG1TextField(), null);
			add(getColorB1TextField(), null);
			add(getColorR2TextField(), null);
			add(getColorG2TextField(), null);
			add(getColorB2TextField(), null);
			add(getTextureNameTextField(), null);
			add(getTextureTypeComboBox(), null);
			add(getTextureSizeTextField(), null);
			add(getTextureLinkWithPreviousComboBox(), null);
			add(getTextureStartOnBoundaryComboBox(), null);
			add(getTextureMipMapTextField(), null);
			add(getFrictionTextField(), null);
			add(getRollingResistanceTextField(), null);
			add(getBumpNameTextField(), null);
			add(getBumpSizeTextField(), null);
			add(getRoughnessTextField(), null);
			add(getRoughnessWavelengthTextField(), null);
			add(getRacelineNameTextField(), null);
			add(getDamageTextField(), null);
			add(getReboundTextField(), null);
			add(getBrowseButton(), null);

			getNameTextField().setText(surface.getName());
			getColorR1TextField().setText(getString(surface.getColorR1()));
			getColorG1TextField().setText(getString(surface.getColorG1()));
			getColorB1TextField().setText(getString(surface.getColorB1()));
			getColorR2TextField().setText(getString(surface.getColorR2()));
			getColorG2TextField().setText(getString(surface.getColorG2()));
			getColorB2TextField().setText(getString(surface.getColorB2()));
			getTextureNameTextField().setText(surface.getTextureName());
			getTextureTypeComboBox().setSelectedItem(getString(surface.getTextureType()));
			getTextureSizeTextField().setText(getString(surface.getTextureSize()));
			getTextureLinkWithPreviousComboBox().setSelectedItem(getString(surface.getTextureLinkWithPrevious()));
			getTextureStartOnBoundaryComboBox().setSelectedItem(getString(surface.getTextureStartOnBoundary()));
			getTextureMipMapTextField().setText(getString(surface.getTextureMipMap()));
			getFrictionTextField().setText(getString(surface.getFriction()));
			getRollingResistanceTextField().setText(getString(surface.getRollingResistance()));
			getBumpNameTextField().setText(surface.getBumpName());
			getBumpSizeTextField().setText(getString(surface.getBumpSize()));
			getRoughnessTextField().setText(getString(surface.getRoughness()));
			getRoughnessWavelengthTextField().setText(getString(surface.getRoughnessWavelength()));
			getRacelineNameTextField().setText(surface.getRacelineName());
			getDamageTextField().setText(getString(surface.getDammage()));
			getReboundTextField().setText(getString(surface.getRebound()));
		}

		private String getString(double value)
		{
			if (!Double.isNaN(value))
				return String.valueOf(value);

			return null;
		}
		private String getString(String string)
		{
			if (string == null || string.isEmpty())
				return "none";

			return string;
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
				nameTextField.setBounds(180, 10, 100, 20);
			}
			return nameTextField;
		}

		/**
		 * This method initializes browseButton
		 *
		 * @return javax.swing.JButton
		 */
		private JButton getBrowseButton()
		{
			if (browseButton == null)
			{
				browseButton = new JButton();
				browseButton.setBounds(370, 183, 80, 25);
				browseButton.setText("Browse");
				browseButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						selectFile();
					}
				});
			}
			return browseButton;
		}

		public JTextField getColorR1TextField()
		{
			if (colorR1TextField == null)
			{
				colorR1TextField = new JTextField();
				colorR1TextField.setBounds(180, 35, 100, 20);
			}
			return colorR1TextField;
		}

		public JTextField getColorG1TextField()
		{
			if (colorG1TextField == null)
			{
				colorG1TextField = new JTextField();
				colorG1TextField.setBounds(180, 60, 100, 20);
			}
			return colorG1TextField;
		}

		public JTextField getColorB1TextField()
		{
			if (colorB1TextField == null)
			{
				colorB1TextField = new JTextField();
				colorB1TextField.setBounds(180, 85, 100, 20);
			}
			return colorB1TextField;
		}

		public JTextField getColorR2TextField()
		{
			if (colorR2TextField == null)
			{
				colorR2TextField = new JTextField();
				colorR2TextField.setBounds(180, 110, 100, 20);
			}
			return colorR2TextField;
		}

		public JTextField getColorG2TextField()
		{
			if (colorG2TextField == null)
			{
				colorG2TextField = new JTextField();
				colorG2TextField.setBounds(180, 135, 100, 20);
			}
			return colorG2TextField;
		}

		public JTextField getColorB2TextField()
		{
			if (colorB2TextField == null)
			{
				colorB2TextField = new JTextField();
				colorB2TextField.setBounds(180, 160, 100, 20);
			}
			return colorB2TextField;
		}

		public JTextField getTextureNameTextField()
		{
			if (textureNameTextField == null)
			{
				textureNameTextField = new JTextField();
				textureNameTextField.setBounds(180, 185, 180, 20);
			}
			return textureNameTextField;
		}

		public JComboBox<String> getTextureTypeComboBox()
		{
			if (textureTypeComboBox == null)
			{
				String[] items = {"none", "discrete", "continuous"};
				textureTypeComboBox = new JComboBox<String>(items);
				textureTypeComboBox.setBounds(180, 210, 120, 20);
			}
			return textureTypeComboBox;
		}

		public JTextField getTextureSizeTextField()
		{
			if (textureSizeTextField == null)
			{
				textureSizeTextField = new JTextField();
				textureSizeTextField.setBounds(180, 235, 100, 20);
			}
			return textureSizeTextField;
		}

		public JComboBox<String> getTextureLinkWithPreviousComboBox()
		{
			if (textureLinkWithPreviousComboBox == null)
			{
				String[] items = {"none", "yes", "no"};
				textureLinkWithPreviousComboBox = new JComboBox<String>(items);
				textureLinkWithPreviousComboBox.setBounds(180, 260, 120, 20);
			}
			return textureLinkWithPreviousComboBox;
		}

		public JComboBox<String> getTextureStartOnBoundaryComboBox()
		{
			if (textureStartOnBoundaryComboBox == null)
			{
				String[] items = {"none", "yes", "no"};
				textureStartOnBoundaryComboBox = new JComboBox<String>(items);
				textureStartOnBoundaryComboBox.setBounds(180, 285, 120, 20);
			}
			return textureStartOnBoundaryComboBox;
		}

		public JTextField getTextureMipMapTextField()
		{
			if (textureMipMapTextField == null)
			{
				textureMipMapTextField = new JTextField();
				textureMipMapTextField.setBounds(180, 310, 100, 20);
			}
			return textureMipMapTextField;
		}

		public JTextField getFrictionTextField()
		{
			if (frictionTextField == null)
			{
				frictionTextField = new JTextField();
				frictionTextField.setBounds(180, 335, 100, 20);
			}
			return frictionTextField;
		}

		public JTextField getRollingResistanceTextField()
		{
			if (rollingResistanceTextField == null)
			{
				rollingResistanceTextField = new JTextField();
				rollingResistanceTextField.setBounds(180, 360, 100, 20);
			}
			return rollingResistanceTextField;
		}

		public JTextField getBumpNameTextField()
		{
			if (bumpNameTextField == null)
			{
				bumpNameTextField = new JTextField();
				bumpNameTextField.setBounds(180, 385, 100, 20);
			}
			return bumpNameTextField;
		}

		public JTextField getBumpSizeTextField()
		{
			if (bumpSizeTextField == null)
			{
				bumpSizeTextField = new JTextField();
				bumpSizeTextField.setBounds(180, 410, 100, 20);
			}
			return bumpSizeTextField;
		}

		public JTextField getRoughnessTextField()
		{
			if (roughnessTextField == null)
			{
				roughnessTextField = new JTextField();
				roughnessTextField.setBounds(180, 435, 100, 20);
			}
			return roughnessTextField;
		}

		public JTextField getRoughnessWavelengthTextField()
		{
			if (roughnessWavelengthTextField == null)
			{
				roughnessWavelengthTextField = new JTextField();
				roughnessWavelengthTextField.setBounds(180, 460, 100, 20);
			}
			return roughnessWavelengthTextField;
		}

		public JTextField getRacelineNameTextField()
		{
			if (racelineNameTextField == null)
			{
				racelineNameTextField = new JTextField();
				racelineNameTextField.setBounds(180, 485, 100, 20);
			}
			return racelineNameTextField;
		}

		public JTextField getDamageTextField()
		{
			if (damageTextField == null)
			{
				damageTextField = new JTextField();
				damageTextField.setBounds(180, 510, 100, 20);
			}
			return damageTextField;
		}

		public JTextField getReboundTextField()
		{
			if (reboundTextField == null)
			{
				reboundTextField = new JTextField();
				reboundTextField.setBounds(180, 535, 100, 20);
			}
			return reboundTextField;
		}

		protected void selectFile()
		{
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFiles(null);
			fc.setSelectedFile(null);
			fc.rescanCurrentDirectory();
			fc.setApproveButtonMnemonic(0);
			fc.setDialogTitle("Surface texture image file selection");
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
				getTextureNameTextField().setText(fileName);
			}
		}
	}

	/**
	 *
	 */
	public void exit()
	{
		String stringResult = null;
		MutableDouble doubleResult = new MutableDouble(Double.NaN);
		Vector<Surface> surfaces = Editor.getProperties().getSurfaces();
		int minCount = Integer.min(surfaces.size(), tabbedPane.getTabCount());
		if (surfaces.size() != tabbedPane.getTabCount())
		{
			frame.documentIsModified = true;
		}
		for (int i = 0; i < minCount; i++)
        {
            Surface surface = surfaces.elementAt(i);
            SurfacePanel panel = (SurfacePanel) tabbedPane.getComponentAt(i);

            if (isDifferent(panel.getNameTextField().getText(), surface.getName(), stringResult))
            {
                surface.setName(panel.getNameTextField().getText());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getColorR1TextField().getText(), surface.getColorR1(), doubleResult))
            {
                surface.setColorR1(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getColorG1TextField().getText(), surface.getColorR1(), doubleResult))
            {
                surface.setColorG1(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getColorB1TextField().getText(), surface.getColorR1(), doubleResult))
            {
                surface.setColorB1(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getColorR2TextField().getText(), surface.getColorR1(), doubleResult))
            {
                surface.setColorR2(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getColorG2TextField().getText(), surface.getColorR1(), doubleResult))
            {
                surface.setColorG2(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getColorB2TextField().getText(), surface.getColorR1(), doubleResult))
            {
                surface.setColorB2(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getTextureNameTextField().getText(), surface.getTextureName(), stringResult))
            {
                surface.setTextureName(panel.getTextureNameTextField().getText());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getTextureTypeComboBox().getSelectedItem().toString(), surface.getTextureType(), stringResult))
            {
                surface.setTextureName(stringResult);
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getTextureSizeTextField().getText(), surface.getTextureSize(), doubleResult))
            {
                surface.setTextureSize(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getTextureLinkWithPreviousComboBox().getSelectedItem().toString(), surface.getTextureLinkWithPrevious(), stringResult))
            {
                surface.setTextureLinkWithPrevious(stringResult);
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getTextureStartOnBoundaryComboBox().getSelectedItem().toString(), surface.getTextureStartOnBoundary(), stringResult))
            {
                surface.setTextureStartOnBoundary(stringResult);
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getTextureMipMapTextField().getText(), surface.getTextureMipMap(), doubleResult))
            {
                surface.setTextureMipMap(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getFrictionTextField().getText(), surface.getFriction(), doubleResult))
            {
                surface.setFriction(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getRollingResistanceTextField().getText(), surface.getRollingResistance(), doubleResult))
            {
                surface.setRollingResistance(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getBumpNameTextField().getText(), surface.getBumpName(), stringResult))
            {
                surface.setBumpName(panel.getBumpNameTextField().getText());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getBumpSizeTextField().getText(), surface.getBumpSize(), doubleResult))
            {
                surface.setBumpSize(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getRoughnessTextField().getText(), surface.getRoughness(), doubleResult))
            {
                surface.setRoughness(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getRoughnessWavelengthTextField().getText(), surface.getRoughnessWavelength(), doubleResult))
            {
                surface.setRoughnessWavelength(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getRacelineNameTextField().getText(), surface.getRacelineName(), stringResult))
            {
                surface.setRacelineName(panel.getRacelineNameTextField().getText());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getDamageTextField().getText(), surface.getDammage(), doubleResult))
            {
                surface.setDammage(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getReboundTextField().getText(), surface.getRebound(), doubleResult))
            {
                surface.setRebound(doubleResult.getValue());
                frame.documentIsModified = true;
            }
		}
		if (surfaces.size() > tabbedPane.getTabCount())
		{
			// need to trim envMaps
			while (surfaces.size() > tabbedPane.getTabCount())
			{
				surfaces.remove(surfaces.size() - 1);
			}
		}
		else if (surfaces.size() < tabbedPane.getTabCount())
		{
			// need to add to environmentMaps
			while (surfaces.size() < tabbedPane.getTabCount())
			{
	            SurfacePanel panel = (SurfacePanel) tabbedPane.getComponentAt(surfaces.size());
				Surface surface = new Surface();

				surface.setName(panel.getNameTextField().getText());
				surface.setColorR1(getDouble(panel.getColorR1TextField().getText()));
				surface.setColorG1(getDouble(panel.getColorG1TextField().getText()));
				surface.setColorB1(getDouble(panel.getColorB1TextField().getText()));
				surface.setColorR2(getDouble(panel.getColorR2TextField().getText()));
				surface.setColorG2(getDouble(panel.getColorG2TextField().getText()));
				surface.setColorB2(getDouble(panel.getColorB2TextField().getText()));
				surface.setTextureName(panel.getTextureNameTextField().getText());
				surface.setTextureType(getString(panel.getTextureTypeComboBox().getSelectedItem().toString()));
				surface.setTextureSize(getDouble(panel.getTextureSizeTextField().getText()));
				surface.setTextureLinkWithPrevious(getString(panel.getTextureLinkWithPreviousComboBox().getSelectedItem().toString()));
				surface.setTextureStartOnBoundary(getString(panel.getTextureStartOnBoundaryComboBox().getSelectedItem().toString()));
				surface.setTextureMipMap(getDouble(panel.getTextureMipMapTextField().getText()));
				surface.setFriction(getDouble(panel.getFrictionTextField().getText()));
				surface.setRollingResistance(getDouble(panel.getRollingResistanceTextField().getText()));
				surface.setBumpName(panel.getBumpNameTextField().getText());
				surface.setBumpSize(getDouble(panel.getBumpSizeTextField().getText()));
				surface.setRoughness(getDouble(panel.getRoughnessTextField().getText()));
				surface.setRoughnessWavelength(getDouble(panel.getRoughnessWavelengthTextField().getText()));
				surface.setRacelineName(panel.getRacelineNameTextField().getText());
				surface.setDammage(getDouble(panel.getDamageTextField().getText()));
				surface.setRebound(getDouble(panel.getReboundTextField().getText()));

				surfaces.add(surface);
			}
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
