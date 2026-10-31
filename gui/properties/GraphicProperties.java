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
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.EditorFrame;
import utils.Properties;
import utils.MutableDouble;
import utils.MutableString;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class GraphicProperties extends PropertyPanel
{
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
	private JButton				backgroundImageButton			= null;

	private final String sep = System.getProperty("file.separator");

	/**
	 *
	 */
	public GraphicProperties(EditorFrame editorFrame)
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
		setLayout(null);
		setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));

		addLabel(this, 0, backgroundImageLabel, "Background Image", 150);
		addLabel(this, 1, backgroundTypeLabel, "Background Type", 150);
		addLabel(this, 2, backgroundColorRLabel, "Background Color R", 150);
		addLabel(this, 3, backgroundColorGLabel, "Background Color G", 150);
		addLabel(this, 4, backgroundColorBLabel, "Background Color B", 150);
		addLabel(this, 5, ambientColorRLabel, "Ambient Color R", 150);
		addLabel(this, 6, ambientColorGLabel, "Ambient Color G", 150);
		addLabel(this, 7, ambientColorBLabel, "Ambient Color B", 150);
		addLabel(this, 8, diffuseColorRLabel, "Diffuse Color R", 150);
		addLabel(this, 9, diffuseColorGLabel, "Diffuse Color G", 150);
		addLabel(this, 10, diffuseColorBLabel, "Diffuse Color B", 150);
		addLabel(this, 11, specularColorRLabel, "Specular Color R", 150);
		addLabel(this, 12, specularColorGLabel, "Specular Color G", 150);
		addLabel(this, 13, specularColorBLabel, "Specular Color B", 150);
		addLabel(this, 14, lightPositionXLabel, "Light Position X", 150);
		addLabel(this, 15, lightPositionYLabel, "Light Position Y", 150);
		addLabel(this, 16, lightPositionZLabel, "Light Position Z", 150);
		addLabel(this, 17, shininessLabel, "Shininess", 150);
		addLabel(this, 18, fovFactorLabel, "Fov Factor", 150);

		addTextField(this, 0, backgroundImageTextField, getEditorFrame().getTrackData().getGraphic().getBackgroundImage(), 160, 275);

		add(getBackgroundTypeComboBox(), null);

		addTextField(this, 2, backgroundColorRTextField, getEditorFrame().getTrackData().getGraphic().getBackgroundColorR(), 160, 125);
		addTextField(this, 3, backgroundColorGTextField, getEditorFrame().getTrackData().getGraphic().getBackgroundColorG(), 160, 125);
		addTextField(this, 4, backgroundColorBTextField, getEditorFrame().getTrackData().getGraphic().getBackgroundColorB(), 160, 125);
		addTextField(this, 5, ambientColorRTextField, getEditorFrame().getTrackData().getGraphic().getAmbientColorR(), 160, 125);
		addTextField(this, 6, ambientColorGTextField, getEditorFrame().getTrackData().getGraphic().getAmbientColorG(), 160, 125);
		addTextField(this, 7, ambientColorBTextField, getEditorFrame().getTrackData().getGraphic().getAmbientColorB(), 160, 125);
		addTextField(this, 8, diffuseColorRTextField, getEditorFrame().getTrackData().getGraphic().getDiffuseColorR(), 160, 125);
		addTextField(this, 9, diffuseColorGTextField, getEditorFrame().getTrackData().getGraphic().getDiffuseColorG(), 160, 125);
		addTextField(this, 10, diffuseColorBTextField, getEditorFrame().getTrackData().getGraphic().getDiffuseColorB(), 160, 125);
		addTextField(this, 11, specularColorRTextField, getEditorFrame().getTrackData().getGraphic().getSpecularColorR(), 160, 125);
		addTextField(this, 12, specularColorGTextField, getEditorFrame().getTrackData().getGraphic().getSpecularColorG(), 160, 125);
		addTextField(this, 13, specularColorBTextField, getEditorFrame().getTrackData().getGraphic().getSpecularColorB(), 160, 125);
		addTextField(this, 14, lightPositionXTextField, getEditorFrame().getTrackData().getGraphic().getLightPositionX(), 160, 125);
		addTextField(this, 15, lightPositionYTextField, getEditorFrame().getTrackData().getGraphic().getLightPositionY(), 160, 125);
		addTextField(this, 16, lightPositionZTextField, getEditorFrame().getTrackData().getGraphic().getLightPositionZ(), 160, 125);
		addTextField(this, 17, shininessTextField, getEditorFrame().getTrackData().getGraphic().getShininess(), 160, 125);
		addTextField(this, 18, fovFactorTextField, getEditorFrame().getTrackData().getGraphic().getFovFactor(), 160, 125);

		add(getBackgroundImageButton(), null);
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
			backgroundTypeComboBox.setBounds(160, 37, 125, 23);
			int value = getEditorFrame().getTrackData().getGraphic().getBackgroundType();
			if (value != Integer.MAX_VALUE)
				selectItem(backgroundTypeComboBox, String.valueOf(value));
		}
		return backgroundTypeComboBox;
	}

	/**
	 * This method initializes backgroundImageButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBackgroundImageButton()
	{
		if (backgroundImageButton == null)
		{
			backgroundImageButton = new JButton();
			backgroundImageButton.setBounds(440, 9, 80, 25);
			backgroundImageButton.setText("Browse");
			backgroundImageButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					backgroundImageFile();
				}
			});
		}
		return backgroundImageButton;
	}

	protected void backgroundImageFile()
	{
		Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle("Background image file selection");
		fc.setFileHidingEnabled(false);
		fc.setVisible(true);
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("RGB and PNG images", "rgb", "png");
		fc.addChoosableFileFilter(filter);
		fc.setCurrentDirectory(new File(Properties.getInstance().getPath()));
		int result = fc.showOpenDialog(this);
		UIManager.put("FileChooser.readOnly", old);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			String fileName = fc.getSelectedFile().toString();
			int index = fileName.lastIndexOf(sep);
			String pathToFile = fileName.substring(0, index);
			if (pathToFile.equals(Properties.getInstance().getPath()))
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

		if (isDifferent(backgroundImageTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getBackgroundImage(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setBackgroundImage(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		int index = getBackgroundTypeComboBox().getSelectedIndex();
		int type = getEditorFrame().getTrackData().getGraphic().getBackgroundType();
		if (index == 0)
		{
			if (type != Integer.MAX_VALUE)
			{
				getEditorFrame().getTrackData().getGraphic().setBackgroundType(Integer.MAX_VALUE);
				getEditorFrame().documentIsModified = true;
			}
		}
		else if (index != -1)
		{
			// also when the track had no background type yet
			int value = Integer.parseInt((String) getBackgroundTypeComboBox().getSelectedItem());
			if (value != type)
			{
				getEditorFrame().getTrackData().getGraphic().setBackgroundType(value);
				getEditorFrame().documentIsModified = true;
			}
		}

		if (isDifferent(backgroundColorRTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getBackgroundColorR(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setBackgroundColorR(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(backgroundColorGTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getBackgroundColorG(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setBackgroundColorG(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(backgroundColorBTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getBackgroundColorB(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setBackgroundColorB(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(ambientColorRTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getAmbientColorR(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setAmbientColorR(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(ambientColorGTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getAmbientColorG(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setAmbientColorG(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(ambientColorBTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getAmbientColorB(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setAmbientColorB(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(diffuseColorRTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getDiffuseColorR(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setDiffuseColorR(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(diffuseColorGTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getDiffuseColorG(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setDiffuseColorG(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(diffuseColorBTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getDiffuseColorB(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setDiffuseColorB(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(specularColorRTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getSpecularColorR(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setSpecularColorR(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(specularColorGTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getSpecularColorG(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setSpecularColorG(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(specularColorBTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getSpecularColorB(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setSpecularColorB(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(lightPositionXTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getLightPositionX(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setLightPositionX(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(lightPositionYTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getLightPositionY(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setLightPositionY(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(lightPositionZTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getLightPositionZ(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setLightPositionZ(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(shininessTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getShininess(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setShininess(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(fovFactorTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getFovFactor(), doubleResult))
		{
			getEditorFrame().getTrackData().getGraphic().setFovFactor(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
