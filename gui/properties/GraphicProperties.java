/*
 *   GraphicProperties.java
 *   Created on 6 June 2022
 *
 *    The GraphicProperties.java is part of TrackEditor-0.7.0.
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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.EditorFrame;
import utils.Editor;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class GraphicProperties extends PropertyPanel
{
	private JLabel				descriptionLabel				= new JLabel();
	private JTextField			descriptionTextField			= new JTextField();
	private JLabel				descriptionNightLabel			= new JLabel();
	private JTextField			descriptionNightTextField		= new JTextField();
	private JLabel				descriptionRainNightLabel		= new JLabel();
	private JTextField			descriptionRainNightTextField	= new JTextField();
	private JLabel				backgroundImageLabel			= new JLabel();
	private JTextField			backgroundImageTextField		= new JTextField();
	private JLabel				backgroundTypeLabel				= new JLabel();
	private JComboBox<String>	backgroundTypeComboBox			= null;
    private JLabel				backgroundColorRLabel			= new JLabel();
    private JTextField			backgroundColorRTextField		= new JTextField();
    private JLabel				backgroundColorGLabel			= new JLabel();
    private JTextField			backgroundColorGTextField		= new JTextField();
    private JLabel				backgroundColorBLabel			= new JLabel();
    private JTextField			backgroundColorBTextField		= new JTextField();
    private JLabel				ambientColorRLabel				= new JLabel();
    private JTextField			ambientColorRTextField			= new JTextField();
    private JLabel				ambientColorGLabel				= new JLabel();
    private JTextField			ambientColorGTextField			= new JTextField();
    private JLabel				ambientColorBLabel				= new JLabel();
    private JTextField			ambientColorBTextField			= new JTextField();
    private JLabel				diffuseColorRLabel				= new JLabel();
    private JTextField			diffuseColorRTextField			= new JTextField();
    private JLabel				diffuseColorGLabel				= new JLabel();
    private JTextField			diffuseColorGTextField			= new JTextField();
    private JLabel				diffuseColorBLabel				= new JLabel();
    private JTextField			diffuseColorBTextField			= new JTextField();
    private JLabel				specularColorRLabel				= new JLabel();
    private JTextField			specularColorRTextField			= new JTextField();
    private JLabel				specularColorGLabel				= new JLabel();
    private JTextField			specularColorGTextField			= new JTextField();
    private JLabel				specularColorBLabel				= new JLabel();
    private JTextField			specularColorBTextField			= new JTextField();
    private JLabel				lightPositionXLabel				= new JLabel();
    private JTextField			lightPositionXTextField			= new JTextField();
    private JLabel				lightPositionYLabel				= new JLabel();
    private JTextField			lightPositionYTextField			= new JTextField();
    private JLabel				lightPositionZLabel				= new JLabel();
    private JTextField			lightPositionZTextField			= new JTextField();
    private JLabel				shininessLabel					= new JLabel();
    private JTextField			shininessTextField				= new JTextField();
    private JLabel				fovFactorLabel					= new JLabel();
    private JTextField			fovFactorTextField				= new JTextField();
	private JButton				browseButton					= null;

	private final String sep = System.getProperty("file.separator");

	/**
	 *
	 */
	public GraphicProperties(EditorFrame frame)
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
		setLayout(null);
		setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));

		addLabel(this, 0, descriptionLabel, "Description", 130);
		addLabel(this, 1, descriptionNightLabel, "Description Night", 130);
		addLabel(this, 2, descriptionRainNightLabel, "Description Rain Night", 130);
		addLabel(this, 3, backgroundImageLabel, "Background Image", 130);
		addLabel(this, 4, backgroundTypeLabel, "Background Type", 130);
		addLabel(this, 5, backgroundColorRLabel, "Background Color R", 130);
		addLabel(this, 6, backgroundColorGLabel, "Background Color G", 130);
		addLabel(this, 7, backgroundColorBLabel, "Background Color B", 130);
		addLabel(this, 8, ambientColorRLabel, "Ambient Color R", 130);
		addLabel(this, 9, ambientColorGLabel, "Ambient Color G", 130);
		addLabel(this, 10, ambientColorBLabel, "Ambient Color B", 130);
		addLabel(this, 11, diffuseColorRLabel, "Diffuse Color R", 130);
		addLabel(this, 12, diffuseColorGLabel, "Diffuse Color G", 130);
		addLabel(this, 13, diffuseColorBLabel, "Diffuse Color B", 130);
		addLabel(this, 14, specularColorRLabel, "Specular Color R", 130);
		addLabel(this, 15, specularColorGLabel, "Specular Color G", 130);
		addLabel(this, 16, specularColorBLabel, "Specular Color B", 130);
		addLabel(this, 17, lightPositionXLabel, "Light Position X", 130);
		addLabel(this, 18, lightPositionYLabel, "Light Position Y", 130);
		addLabel(this, 19, lightPositionZLabel, "Light Position Z", 130);
		addLabel(this, 20, shininessLabel, "Shininess", 130);
		addLabel(this, 21, fovFactorLabel, "Fov Factor", 130);

		addTextField(this, 0, descriptionTextField, Editor.getProperties().getGraphic().getDescription(), 140, 150);
		addTextField(this, 1, descriptionNightTextField, Editor.getProperties().getGraphic().getDescriptionNight(), 140, 150);
		addTextField(this, 2, descriptionRainNightTextField, Editor.getProperties().getGraphic().getDescriptionRainNight(), 140, 150);
		addTextField(this, 3, backgroundImageTextField, Editor.getProperties().getGraphic().getBackgroundImage(), 140, 255);

		add(getBackgroundTypeComboBox(), null);

		addTextField(this, 5, backgroundColorRTextField, Editor.getProperties().getGraphic().getBackgroundColorR(), 140, 80);
		addTextField(this, 6, backgroundColorGTextField, Editor.getProperties().getGraphic().getBackgroundColorG(), 140, 80);
		addTextField(this, 7, backgroundColorBTextField, Editor.getProperties().getGraphic().getBackgroundColorB(), 140, 80);
		addTextField(this, 8, ambientColorRTextField, Editor.getProperties().getGraphic().getAmbientColorR(), 140, 80);
		addTextField(this, 9, ambientColorGTextField, Editor.getProperties().getGraphic().getAmbientColorG(), 140, 80);
		addTextField(this, 10, ambientColorBTextField, Editor.getProperties().getGraphic().getAmbientColorB(), 140, 80);
		addTextField(this, 11, diffuseColorRTextField, Editor.getProperties().getGraphic().getDiffuseColorR(), 140, 80);
		addTextField(this, 12, diffuseColorGTextField, Editor.getProperties().getGraphic().getDiffuseColorG(), 140, 80);
		addTextField(this, 13, diffuseColorBTextField, Editor.getProperties().getGraphic().getDiffuseColorB(), 140, 80);
		addTextField(this, 14, specularColorRTextField, Editor.getProperties().getGraphic().getSpecularColorR(), 140, 80);
		addTextField(this, 15, specularColorGTextField, Editor.getProperties().getGraphic().getSpecularColorG(), 140, 80);
		addTextField(this, 16, specularColorBTextField, Editor.getProperties().getGraphic().getSpecularColorB(), 140, 80);
		addTextField(this, 17, lightPositionXTextField, Editor.getProperties().getGraphic().getLightPositionX(), 140, 80);
		addTextField(this, 18, lightPositionYTextField, Editor.getProperties().getGraphic().getLightPositionY(), 140, 80);
		addTextField(this, 19, lightPositionZTextField, Editor.getProperties().getGraphic().getLightPositionZ(), 140, 80);
		addTextField(this, 20, shininessTextField, Editor.getProperties().getGraphic().getShininess(), 140, 80);
		addTextField(this, 21, fovFactorTextField, Editor.getProperties().getGraphic().getFovFactor(), 140, 80);

		add(getBrowseButton(), null);
	}

	/**
	 * This method initializes backgroundTypeComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getBackgroundTypeComboBox()
	{
		if (backgroundTypeComboBox == null)
		{
			String[] types = {"none", "0", "2", "4"};
			backgroundTypeComboBox = new JComboBox<String>();
			backgroundTypeComboBox.setModel(new DefaultComboBoxModel<String>(types));
			backgroundTypeComboBox.setBounds(140, 110, 80, 20);
			int value = Editor.getProperties().getGraphic().getBackgroundType();
			if (value != Integer.MAX_VALUE)
				backgroundTypeComboBox.setSelectedItem(String.valueOf(value));
		}
		return backgroundTypeComboBox;
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
			browseButton.setBounds(400, 82, 80, 25);
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
			backgroundImageTextField.setText(fileName);
		}
	}

	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();

		if (isDifferent(descriptionTextField.getText(),
			Editor.getProperties().getGraphic().getDescription(), stringResult))
		{
			Editor.getProperties().getGraphic().setDescription(stringResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(descriptionNightTextField.getText(),
			Editor.getProperties().getGraphic().getDescriptionNight(), stringResult))
		{
			Editor.getProperties().getGraphic().setDescriptionNight(stringResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(descriptionRainNightTextField.getText(),
			Editor.getProperties().getGraphic().getDescriptionRainNight(), stringResult))
		{
			Editor.getProperties().getGraphic().setDescriptionRainNight(stringResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(backgroundImageTextField.getText(),
			Editor.getProperties().getGraphic().getBackgroundImage(), stringResult))
		{
			Editor.getProperties().getGraphic().setBackgroundImage(stringResult.getValue());
			frame.documentIsModified = true;
		}

		int index = getBackgroundTypeComboBox().getSelectedIndex();
		int type = Editor.getProperties().getGraphic().getBackgroundType();
		if (index == 0)
		{
			if (type != Integer.MAX_VALUE)
			{
				Editor.getProperties().getGraphic().setBackgroundType(Integer.MAX_VALUE);
				frame.documentIsModified = true;
			}
		}
		else
		{
			if (type == Integer.MAX_VALUE)
			{
				Editor.getProperties().getGraphic().setBackgroundType(Integer.MAX_VALUE);
				frame.documentIsModified = true;
			}
			else
			{
				int value = Integer.parseInt((String) getBackgroundTypeComboBox().getSelectedItem());
				if (value != type)
				{
					Editor.getProperties().getGraphic().setBackgroundType(value);
					frame.documentIsModified = true;
				}
			}
		}

		if (isDifferent(backgroundColorRTextField.getText(),
			Editor.getProperties().getGraphic().getBackgroundColorR(), doubleResult))
		{
			Editor.getProperties().getGraphic().setBackgroundColorR(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(backgroundColorGTextField.getText(),
			Editor.getProperties().getGraphic().getBackgroundColorG(), doubleResult))
		{
			Editor.getProperties().getGraphic().setBackgroundColorG(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(backgroundColorBTextField.getText(),
			Editor.getProperties().getGraphic().getBackgroundColorB(), doubleResult))
		{
			Editor.getProperties().getGraphic().setBackgroundColorB(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(ambientColorRTextField.getText(),
			Editor.getProperties().getGraphic().getAmbientColorR(), doubleResult))
		{
			Editor.getProperties().getGraphic().setAmbientColorR(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(ambientColorGTextField.getText(),
			Editor.getProperties().getGraphic().getAmbientColorG(), doubleResult))
		{
			Editor.getProperties().getGraphic().setAmbientColorG(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(ambientColorBTextField.getText(),
			Editor.getProperties().getGraphic().getAmbientColorB(), doubleResult))
		{
			Editor.getProperties().getGraphic().setAmbientColorB(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(diffuseColorRTextField.getText(),
			Editor.getProperties().getGraphic().getDiffuseColorR(), doubleResult))
		{
			Editor.getProperties().getGraphic().setDiffuseColorR(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(diffuseColorGTextField.getText(),
			Editor.getProperties().getGraphic().getDiffuseColorG(), doubleResult))
		{
			Editor.getProperties().getGraphic().setDiffuseColorG(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(diffuseColorBTextField.getText(),
			Editor.getProperties().getGraphic().getDiffuseColorB(), doubleResult))
		{
			Editor.getProperties().getGraphic().setDiffuseColorB(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(specularColorRTextField.getText(),
			Editor.getProperties().getGraphic().getSpecularColorR(), doubleResult))
		{
			Editor.getProperties().getGraphic().setSpecularColorR(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(specularColorGTextField.getText(),
			Editor.getProperties().getGraphic().getSpecularColorG(), doubleResult))
		{
			Editor.getProperties().getGraphic().setSpecularColorG(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(specularColorBTextField.getText(),
			Editor.getProperties().getGraphic().getSpecularColorB(), doubleResult))
		{
			Editor.getProperties().getGraphic().setSpecularColorB(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(lightPositionXTextField.getText(),
			Editor.getProperties().getGraphic().getLightPositionX(), doubleResult))
		{
			Editor.getProperties().getGraphic().setLightPositionX(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(lightPositionYTextField.getText(),
			Editor.getProperties().getGraphic().getLightPositionY(), doubleResult))
		{
			Editor.getProperties().getGraphic().setLightPositionY(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(lightPositionZTextField.getText(),
			Editor.getProperties().getGraphic().getLightPositionZ(), doubleResult))
		{
			Editor.getProperties().getGraphic().setLightPositionZ(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(shininessTextField.getText(),
			Editor.getProperties().getGraphic().getShininess(), doubleResult))
		{
			Editor.getProperties().getGraphic().setShininess(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(fovFactorTextField.getText(),
			Editor.getProperties().getGraphic().getFovFactor(), doubleResult))
		{
			Editor.getProperties().getGraphic().setFovFactor(doubleResult.getValue());
			frame.documentIsModified = true;
		}
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
