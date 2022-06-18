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
		private JLabel				nameLabel						= new JLabel();
		private JTextField 			nameTextField					= new JTextField();
		private JLabel				colorR1Label					= new JLabel();
		private JTextField			colorR1TextField				= new JTextField();
		private JLabel				colorG1Label					= new JLabel();
		private JTextField			colorG1TextField				= new JTextField();
		private JLabel				colorB1Label					= new JLabel();
		private JTextField			colorB1TextField				= new JTextField();
		private JLabel				colorR2Label					= new JLabel();
		private JTextField			colorR2TextField				= new JTextField();
		private JLabel				colorG2Label					= new JLabel();
		private JTextField			colorG2TextField				= new JTextField();
		private JLabel				colorB2Label					= new JLabel();
		private JTextField			colorB2TextField				= new JTextField();
		private JLabel				textureNameLabel				= new JLabel();
		private JTextField			textureNameTextField			= new JTextField();
		private JLabel				textureTypeLabel				= new JLabel();
		private JComboBox<String>	textureTypeComboBox				= null;
		private JLabel				textureSizeLabel				= new JLabel();
		private JTextField			textureSizeTextField			= new JTextField();
		private JLabel				textureLinkWithPreviousLabel	= new JLabel();
		private JComboBox<String>	textureLinkWithPreviousComboBox	= null;
		private JLabel				textureStartOnBoundaryLabel		= new JLabel();
		private JComboBox<String>	textureStartOnBoundaryComboBox	= null;
		private JLabel				textureMipMapLabel				= new JLabel();
		private JTextField			textureMipMapTextField			= new JTextField();
		private JLabel				frictionLabel					= new JLabel();
		private JTextField 			frictionTextField				= new JTextField();
		private JLabel				rollingResistanceLabel			= new JLabel();
		private JTextField 			rollingResistanceTextField		= new JTextField();
		private JLabel				bumpNameLabel					= new JLabel();
		private JTextField 			bumpNameTextField				= new JTextField();
		private JLabel				bumpSizeLabel					= new JLabel();
		private JTextField 			bumpSizeTextField				= new JTextField();
		private JLabel				roughnessLabel					= new JLabel();
		private JTextField 			roughnessTextField				= new JTextField();
		private JLabel				roughnessWavelengthLabel		= new JLabel();
		private JTextField 			roughnessWavelengthTextField	= new JTextField();
		private JLabel				racelineNameLabel				= new JLabel();
		private JTextField 			racelineNameTextField			= new JTextField();
		private JLabel				damageLabel						= new JLabel();
		private JTextField 			damageTextField					= new JTextField();
		private JLabel				reboundLabel					= new JLabel();
		private JTextField 			reboundTextField				= new JTextField();
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
			setLayout(null);

			addLabel(this, 0, nameLabel, "Name", 160);
			addLabel(this, 1, colorR1Label, "Color R1", 160);
			addLabel(this, 2, colorG1Label, "Color G1", 160);
			addLabel(this, 3, colorB1Label, "Color B1", 160);
			addLabel(this, 4, colorR2Label, "Color R2", 160);
			addLabel(this, 5, colorG2Label, "Color G2", 160);
			addLabel(this, 6, colorB2Label, "Color B2", 160);
			addLabel(this, 7, textureNameLabel, "Texture Name", 160);
			addLabel(this, 8, textureTypeLabel, "Texture Type", 160);
			addLabel(this, 9, textureSizeLabel, "Texture Size", 160);
			addLabel(this, 10, textureLinkWithPreviousLabel, "Texture Link With Previous", 160);
			addLabel(this, 11, textureStartOnBoundaryLabel, "Texture Start On Boundary", 160);
			addLabel(this, 12, textureMipMapLabel, "Texture MipMap", 160);
			addLabel(this, 13, frictionLabel, "Friction", 160);
			addLabel(this, 14, rollingResistanceLabel, "Rolling Resistance", 160);
			addLabel(this, 15, bumpNameLabel, "Bump Name", 160);
			addLabel(this, 16, bumpSizeLabel, "Bump Size", 160);
			addLabel(this, 17, roughnessLabel, "Roughness", 160);
			addLabel(this, 18, roughnessWavelengthLabel, "Roughness Wavelength", 160);
			addLabel(this, 19, racelineNameLabel, "Raceline Name", 160);
			addLabel(this, 20, damageLabel, "Damage", 160);
			addLabel(this, 21, reboundLabel, "Rebound", 160);

			addTextField(this, 0, nameTextField, surface.getName(), 180, 100);
			addTextField(this, 1, colorR1TextField, getString(surface.getColorR1()), 180, 100);
			addTextField(this, 2, colorG1TextField, getString(surface.getColorG1()), 180, 100);
			addTextField(this, 3, colorB1TextField, getString(surface.getColorB1()), 180, 100);
			addTextField(this, 4, colorR2TextField, getString(surface.getColorR2()), 180, 100);
			addTextField(this, 5, colorG2TextField, getString(surface.getColorG2()), 180, 100);
			addTextField(this, 6, colorB2TextField, getString(surface.getColorB2()), 180, 100);
			addTextField(this, 7, textureNameTextField, surface.getTextureName(), 180, 180);

			add(getTextureTypeComboBox(), null);
			getTextureTypeComboBox().setSelectedItem(getString(surface.getTextureType()));

			addTextField(this, 9, textureSizeTextField, getString(surface.getTextureSize()), 180, 100);

			add(getTextureLinkWithPreviousComboBox(), null);
			getTextureLinkWithPreviousComboBox().setSelectedItem(getString(surface.getTextureLinkWithPrevious()));
			add(getTextureStartOnBoundaryComboBox(), null);
			getTextureStartOnBoundaryComboBox().setSelectedItem(getString(surface.getTextureStartOnBoundary()));

			addTextField(this, 12, textureMipMapTextField, getString(surface.getTextureMipMap()), 180, 100);
			addTextField(this, 13, frictionTextField, getString(surface.getFriction()), 180, 100);
			addTextField(this, 14, rollingResistanceTextField, getString(surface.getRollingResistance()), 180, 100);
			addTextField(this, 15, bumpNameTextField, surface.getBumpName(), 180, 100);
			addTextField(this, 16, bumpSizeTextField, getString(surface.getBumpSize()), 180, 100);
			addTextField(this, 17, roughnessTextField, getString(surface.getRoughness()), 180, 100);
			addTextField(this, 18, roughnessWavelengthTextField, getString(surface.getRoughnessWavelength()), 180, 100);
			addTextField(this, 19, racelineNameTextField, surface.getRacelineName(), 180, 100);
			addTextField(this, 20, damageTextField, surface.getDammage(), 180, 100);
			addTextField(this, 21, reboundTextField, getString(surface.getRebound()), 180, 100);

			add(getBrowseButton(), null);
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
				textureNameTextField.setText(fileName);
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

		Vector<Surface> surfaces = Editor.getProperties().getSurfaces();
		int minCount = Math.min(surfaces.size(), tabbedPane.getTabCount());
		if (surfaces.size() != tabbedPane.getTabCount())
		{
			frame.documentIsModified = true;
		}
		for (int i = 0; i < minCount; i++)
        {
            Surface surface = surfaces.elementAt(i);
            SurfacePanel panel = (SurfacePanel) tabbedPane.getComponentAt(i);

            if (isDifferent(panel.nameTextField.getText(), surface.getName(), stringResult))
            {
                surface.setName(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.colorR1TextField.getText(), surface.getColorR1(), doubleResult))
            {
                surface.setColorR1(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.colorG1TextField.getText(), surface.getColorG1(), doubleResult))
            {
                surface.setColorG1(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.colorB1TextField.getText(), surface.getColorB1(), doubleResult))
            {
                surface.setColorB1(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.colorR2TextField.getText(), surface.getColorR2(), doubleResult))
            {
                surface.setColorR2(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.colorG2TextField.getText(), surface.getColorG2(), doubleResult))
            {
                surface.setColorG2(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.colorB2TextField.getText(), surface.getColorB2(), doubleResult))
            {
                surface.setColorB2(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.textureNameTextField.getText(), surface.getTextureName(), stringResult))
            {
                surface.setTextureName(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getTextureTypeComboBox().getSelectedItem().toString(), surface.getTextureType(), stringResult))
            {
                surface.setTextureName(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.textureSizeTextField.getText(), surface.getTextureSize(), doubleResult))
            {
                surface.setTextureSize(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getTextureLinkWithPreviousComboBox().getSelectedItem().toString(), surface.getTextureLinkWithPrevious(), stringResult))
            {
                surface.setTextureLinkWithPrevious(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getTextureStartOnBoundaryComboBox().getSelectedItem().toString(), surface.getTextureStartOnBoundary(), stringResult))
            {
                surface.setTextureStartOnBoundary(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.textureMipMapTextField.getText(), surface.getTextureMipMap(), doubleResult))
            {
                surface.setTextureMipMap(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.frictionTextField.getText(), surface.getFriction(), doubleResult))
            {
                surface.setFriction(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.rollingResistanceTextField.getText(), surface.getRollingResistance(), doubleResult))
            {
                surface.setRollingResistance(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.bumpNameTextField.getText(), surface.getBumpName(), stringResult))
            {
                surface.setBumpName(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.bumpSizeTextField.getText(), surface.getBumpSize(), doubleResult))
            {
                surface.setBumpSize(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.roughnessTextField.getText(), surface.getRoughness(), doubleResult))
            {
                surface.setRoughness(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.roughnessWavelengthTextField.getText(), surface.getRoughnessWavelength(), doubleResult))
            {
                surface.setRoughnessWavelength(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.racelineNameTextField.getText(), surface.getRacelineName(), stringResult))
            {
                surface.setRacelineName(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.damageTextField.getText(), surface.getDammage(), doubleResult))
            {
                surface.setDammage(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.reboundTextField.getText(), surface.getRebound(), doubleResult))
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

				surface.setName(panel.nameTextField.getText());
				surface.setColorR1(getDouble(panel.colorR1TextField.getText()));
				surface.setColorG1(getDouble(panel.colorG1TextField.getText()));
				surface.setColorB1(getDouble(panel.colorB1TextField.getText()));
				surface.setColorR2(getDouble(panel.colorR2TextField.getText()));
				surface.setColorG2(getDouble(panel.colorG2TextField.getText()));
				surface.setColorB2(getDouble(panel.colorB2TextField.getText()));
				surface.setTextureName(panel.textureNameTextField.getText());
				surface.setTextureType(getString(panel.getTextureTypeComboBox().getSelectedItem().toString()));
				surface.setTextureSize(getDouble(panel.textureSizeTextField.getText()));
				surface.setTextureLinkWithPrevious(getString(panel.getTextureLinkWithPreviousComboBox().getSelectedItem().toString()));
				surface.setTextureStartOnBoundary(getString(panel.getTextureStartOnBoundaryComboBox().getSelectedItem().toString()));
				surface.setTextureMipMap(getDouble(panel.textureMipMapTextField.getText()));
				surface.setFriction(getDouble(panel.frictionTextField.getText()));
				surface.setRollingResistance(getDouble(panel.rollingResistanceTextField.getText()));
				surface.setBumpName(panel.bumpNameTextField.getText());
				surface.setBumpSize(getDouble(panel.bumpSizeTextField.getText()));
				surface.setRoughness(getDouble(panel.roughnessTextField.getText()));
				surface.setRoughnessWavelength(getDouble(panel.roughnessWavelengthTextField.getText()));
				surface.setRacelineName(panel.racelineNameTextField.getText());
				surface.setDammage(getDouble(panel.damageTextField.getText()));
				surface.setRebound(getDouble(panel.reboundTextField.getText()));

				surfaces.add(surface);
			}
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
