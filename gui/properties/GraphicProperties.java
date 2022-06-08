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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

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
	private JLabel				descriptionLabel				= null;
	private JTextField			descriptionTextField			= null;
	private JLabel				descriptionNightLabel			= null;
	private JTextField			descriptionNightTextField		= null;
	private JLabel				descriptionRainNightLabel		= null;
	private JTextField			descriptionRainNightTextField	= null;
	private JLabel				backgroundImageLabel			= null;
	private JTextField			backgroundImageTextField		= null;
	private JLabel				backgroundTypeLabel				= null;
	private JComboBox<String>	backgroundTypeComboBox			= null;
    private JLabel				backgroundColorRLabel			= null;
    private JTextField			backgroundColorRTextField		= null;
    private JLabel				backgroundColorGLabel			= null;
    private JTextField			backgroundColorGTextField		= null;
    private JLabel				backgroundColorBLabel			= null;
    private JTextField			backgroundColorBTextField		= null;
    private JLabel				ambientColorRLabel				= null;
    private JTextField			ambientColorRTextField			= null;
    private JLabel				ambientColorGLabel				= null;
    private JTextField			ambientColorGTextField			= null;
    private JLabel				ambientColorBLabel				= null;
    private JTextField			ambientColorBTextField			= null;
    private JLabel				diffuseColorRLabel				= null;
    private JTextField			diffuseColorRTextField			= null;
    private JLabel				diffuseColorGLabel				= null;
    private JTextField			diffuseColorGTextField			= null;
    private JLabel				diffuseColorBLabel				= null;
    private JTextField			diffuseColorBTextField			= null;
    private JLabel				specularColorRLabel				= null;
    private JTextField			specularColorRTextField			= null;
    private JLabel				specularColorGLabel				= null;
    private JTextField			specularColorGTextField			= null;
    private JLabel				specularColorBLabel				= null;
    private JTextField			specularColorBTextField			= null;
    private JLabel				lightPositionXLabel				= null;
    private JTextField			lightPositionXTextField			= null;
    private JLabel				lightPositionYLabel				= null;
    private JTextField			lightPositionYTextField			= null;
    private JLabel				lightPositionZLabel				= null;
    private JTextField			lightPositionZTextField			= null;
    private JLabel				shininessLabel					= null;
    private JTextField			shininessTextField				= null;
    private JLabel				fovFactorLabel					= null;
    private JTextField			fovFactorTextField				= null;

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
		descriptionLabel = new JLabel();
		descriptionNightLabel = new JLabel();
		descriptionRainNightLabel = new JLabel();
		backgroundImageLabel = new JLabel();
		backgroundTypeLabel = new JLabel();
		backgroundColorRLabel = new JLabel();
		backgroundColorGLabel = new JLabel();
		backgroundColorBLabel = new JLabel();
		ambientColorRLabel = new JLabel();
		ambientColorGLabel = new JLabel();
		ambientColorBLabel = new JLabel();
		diffuseColorRLabel = new JLabel();
		diffuseColorGLabel = new JLabel();
		diffuseColorBLabel = new JLabel();
		specularColorRLabel = new JLabel();
		specularColorGLabel = new JLabel();
		specularColorBLabel = new JLabel();
		lightPositionXLabel = new JLabel();
		lightPositionYLabel = new JLabel();
		lightPositionZLabel = new JLabel();
		shininessLabel = new JLabel();
		fovFactorLabel = new JLabel();
		this.setLayout(null);
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		this.setSize(362, 251);
		descriptionLabel.setBounds(10, 10, 70, 20);
		descriptionLabel.setText("Description");
		descriptionNightLabel.setBounds(10, 35, 120, 20);
		descriptionNightLabel.setText("Description Night");
		descriptionRainNightLabel.setBounds(10, 60, 150, 20);
		descriptionRainNightLabel.setText("Description Rain Night");
		backgroundImageLabel.setBounds(10, 85, 120, 20);
		backgroundImageLabel.setText("Background Image");
		backgroundTypeLabel.setBounds(10, 110, 120, 20);
		backgroundTypeLabel.setText("Background Type");
		backgroundColorRLabel.setBounds(10, 135, 120, 20);
		backgroundColorRLabel.setText("Background Bolor R");
		backgroundColorGLabel.setBounds(10, 160, 120, 20);
		backgroundColorGLabel.setText("Background Color G");
		backgroundColorBLabel.setBounds(10, 185, 120, 20);
		backgroundColorBLabel.setText("Background Color B");
		ambientColorRLabel.setBounds(10, 210, 120, 20);
		ambientColorRLabel.setText("Ambient Color R");
		ambientColorGLabel.setBounds(10, 235, 120, 20);
		ambientColorGLabel.setText("Ambient Color G");
		ambientColorBLabel.setBounds(10, 260, 120, 20);
		ambientColorBLabel.setText("Ambient Color B");
		diffuseColorRLabel.setBounds(10, 285, 120, 20);
		diffuseColorRLabel.setText("Diffuse Color R");
		diffuseColorGLabel.setBounds(10, 310, 120, 20);
		diffuseColorGLabel.setText("Diffuse Color G");
		diffuseColorBLabel.setBounds(10, 335, 120, 20);
		diffuseColorBLabel.setText("Diffuse Color B");
		specularColorRLabel.setBounds(10, 360, 120, 20);
		specularColorRLabel.setText("Specular Color R");
		specularColorGLabel.setBounds(10, 385, 120, 20);
		specularColorGLabel.setText("Specular Color G");
		specularColorBLabel.setBounds(10, 410, 120, 20);
		specularColorBLabel.setText("Specular Color B");
		lightPositionXLabel.setBounds(10, 435, 120, 20);
		lightPositionXLabel.setText("Light Position X");
		lightPositionYLabel.setBounds(10, 460, 120, 20);
		lightPositionYLabel.setText("Light Position Y");
		lightPositionZLabel.setBounds(10, 485, 120, 20);
		lightPositionZLabel.setText("Light Position Z");
		shininessLabel.setBounds(10, 510, 120, 20);
		shininessLabel.setText("Shininess");
		fovFactorLabel.setBounds(10, 535, 120, 20);
		fovFactorLabel.setText("Fov Factor");
		this.add(descriptionLabel, null);
		this.add(descriptionNightLabel, null);
		this.add(descriptionRainNightLabel, null);
		this.add(backgroundImageLabel, null);
		this.add(backgroundTypeLabel, null);
		this.add(backgroundColorRLabel, null);
		this.add(backgroundColorGLabel, null);
		this.add(backgroundColorBLabel, null);
		this.add(ambientColorRLabel, null);
		this.add(ambientColorGLabel, null);
		this.add(ambientColorBLabel, null);
		this.add(diffuseColorRLabel, null);
		this.add(diffuseColorGLabel, null);
		this.add(diffuseColorBLabel, null);
		this.add(specularColorRLabel, null);
		this.add(specularColorGLabel, null);
		this.add(specularColorBLabel, null);
		this.add(lightPositionXLabel, null);
		this.add(lightPositionYLabel, null);
		this.add(lightPositionZLabel, null);
		this.add(shininessLabel, null);
		this.add(fovFactorLabel, null);
		this.add(getDescriptionTextField(), null);
		this.add(getDescriptionNightTextField(), null);
		this.add(getDescriptionRainNightTextField(), null);
		this.add(getBackgroundImageTextField(), null);
		this.add(getBackgroundTypeComboBox(), null);
		this.add(getBackgroundColorRTextField(), null);
		this.add(getBackgroundColorGTextField(), null);
		this.add(getBackgroundColorBTextField(), null);
		this.add(getAmbientColorRTextField(), null);
		this.add(getAmbientColorGTextField(), null);
		this.add(getAmbientColorBTextField(), null);
		this.add(getDiffuseColorRTextField(), null);
		this.add(getDiffuseColorGTextField(), null);
		this.add(getDiffuseColorBTextField(), null);
		this.add(getSpecularColorRTextField(), null);
		this.add(getSpecularColorGTextField(), null);
		this.add(getSpecularColorBTextField(), null);
		this.add(getLightPositionXTextField(), null);
		this.add(getLightPositionYTextField(), null);
		this.add(getLightPositionZTextField(), null);
		this.add(getShininessTextField(), null);
		this.add(getFovFactorTextField(), null);
	}

	/**
	 * This method initializes descriptionTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getDescriptionTextField()
	{
		if (descriptionTextField == null)
		{
			descriptionTextField = new JTextField();
			descriptionTextField.setBounds(130, 10, 150, 20);
			setTextField(descriptionTextField, Editor.getProperties().getGraphic().getDescription());
		}
		return descriptionTextField;
	}

	/**
	 * This method initializes descriptionNightTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getDescriptionNightTextField()
	{
		if (descriptionNightTextField == null)
		{
			descriptionNightTextField = new JTextField();
			descriptionNightTextField.setBounds(130, 35, 150, 20);
			setTextField(descriptionNightTextField, Editor.getProperties().getGraphic().getDescriptionNight());
		}
		return descriptionNightTextField;
	}

	/**
	 * This method initializes descriptionRainNightTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getDescriptionRainNightTextField()
	{
		if (descriptionRainNightTextField == null)
		{
			descriptionRainNightTextField = new JTextField();
			descriptionRainNightTextField.setBounds(130, 60, 150, 20);
			setTextField(descriptionRainNightTextField, Editor.getProperties().getGraphic().getDescriptionRainNight());
		}
		return descriptionRainNightTextField;
	}

	/**
	 * This method initializes backgroundImageTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getBackgroundImageTextField()
	{
		if (backgroundImageTextField == null)
		{
			backgroundImageTextField = new JTextField();
			backgroundImageTextField.setBounds(130, 85, 150, 20);
			setTextField(backgroundImageTextField, Editor.getProperties().getGraphic().getBackgroundImage());
		}
		return backgroundImageTextField;
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
			backgroundTypeComboBox.setBounds(130, 110, 120, 20);
			int value = Editor.getProperties().getGraphic().getBackgroundType();
			if (value != Integer.MAX_VALUE)
				backgroundTypeComboBox.setSelectedItem(String.valueOf(value));
		}
		return backgroundTypeComboBox;
	}

	/**
	 * This method initializes backgroundColorRTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getBackgroundColorRTextField()
	{
		if (backgroundColorRTextField == null)
		{
			backgroundColorRTextField = new JTextField();
			backgroundColorRTextField.setBounds(130, 135, 150, 20);
			setTextField(backgroundColorRTextField, Editor.getProperties().getGraphic().getBackgroundColorR());
		}
		return backgroundColorRTextField;
	}

	/**
	 * This method initializes backgroundColorGTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getBackgroundColorGTextField()
	{
		if (backgroundColorGTextField == null)
		{
			backgroundColorGTextField = new JTextField();
			backgroundColorGTextField.setBounds(130, 160, 150, 20);
			setTextField(backgroundColorGTextField, Editor.getProperties().getGraphic().getBackgroundColorG());
		}
		return backgroundColorGTextField;
	}

	/**
	 * This method initializes backgroundColorBTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getBackgroundColorBTextField()
	{
		if (backgroundColorBTextField == null)
		{
			backgroundColorBTextField = new JTextField();
			backgroundColorBTextField.setBounds(130, 185, 150, 20);
			setTextField(backgroundColorBTextField, Editor.getProperties().getGraphic().getBackgroundColorB());
		}
		return backgroundColorBTextField;
	}

	/**
	 * This method initializes ambientColorRTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getAmbientColorRTextField()
	{
		if (ambientColorRTextField == null)
		{
			ambientColorRTextField = new JTextField();
			ambientColorRTextField.setBounds(130, 210, 150, 20);
			setTextField(ambientColorRTextField, Editor.getProperties().getGraphic().getAmbientColorR());
		}
		return ambientColorRTextField;
	}

	/**
	 * This method initializes ambientColorGTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getAmbientColorGTextField()
	{
		if (ambientColorGTextField == null)
		{
			ambientColorGTextField = new JTextField();
			ambientColorGTextField.setBounds(130, 235, 150, 20);
			setTextField(ambientColorGTextField, Editor.getProperties().getGraphic().getAmbientColorG());
		}
		return ambientColorGTextField;
	}

	/**
	 * This method initializes ambientColorBTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getAmbientColorBTextField()
	{
		if (ambientColorBTextField == null)
		{
			ambientColorBTextField = new JTextField();
			ambientColorBTextField.setBounds(130, 260, 150, 20);
			setTextField(ambientColorBTextField, Editor.getProperties().getGraphic().getAmbientColorB());
		}
		return ambientColorBTextField;
	}

	/**
	 * This method initializes diffuseColorRTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getDiffuseColorRTextField()
	{
		if (diffuseColorRTextField == null)
		{
			diffuseColorRTextField = new JTextField();
			diffuseColorRTextField.setBounds(130, 285, 150, 20);
			setTextField(diffuseColorRTextField, Editor.getProperties().getGraphic().getDiffuseColorR());
		}
		return diffuseColorRTextField;
	}

	/**
	 * This method initializes diffuseColorGTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getDiffuseColorGTextField()
	{
		if (diffuseColorGTextField == null)
		{
			diffuseColorGTextField = new JTextField();
			diffuseColorGTextField.setBounds(130, 310, 150, 20);
			setTextField(diffuseColorGTextField, Editor.getProperties().getGraphic().getDiffuseColorG());
		}
		return diffuseColorGTextField;
	}

	/**
	 * This method initializes diffuseColorBTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getDiffuseColorBTextField()
	{
		if (diffuseColorBTextField == null)
		{
			diffuseColorBTextField = new JTextField();
			diffuseColorBTextField.setBounds(130, 335, 150, 20);
			setTextField(diffuseColorBTextField, Editor.getProperties().getGraphic().getDiffuseColorB());
		}
		return diffuseColorBTextField;
	}

	/**
	 * This method initializes specularColorRTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getSpecularColorRTextField()
	{
		if (specularColorRTextField == null)
		{
			specularColorRTextField = new JTextField();
			specularColorRTextField.setBounds(130, 360, 150, 20);
			setTextField(specularColorRTextField, Editor.getProperties().getGraphic().getSpecularColorR());
		}
		return specularColorRTextField;
	}

	/**
	 * This method initializes specularColorGTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getSpecularColorGTextField()
	{
		if (specularColorGTextField == null)
		{
			specularColorGTextField = new JTextField();
			specularColorGTextField.setBounds(130, 385, 150, 20);
			setTextField(specularColorGTextField, Editor.getProperties().getGraphic().getSpecularColorG());
		}
		return specularColorGTextField;
	}

	/**
	 * This method initializes specularColorBTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getSpecularColorBTextField()
	{
		if (specularColorBTextField == null)
		{
			specularColorBTextField = new JTextField();
			specularColorBTextField.setBounds(130, 410, 150, 20);
			setTextField(specularColorBTextField, Editor.getProperties().getGraphic().getSpecularColorB());
		}
		return specularColorBTextField;
	}

	/**
	 * This method initializes lightPositionXTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getLightPositionXTextField()
	{
		if (lightPositionXTextField == null)
		{
			lightPositionXTextField = new JTextField();
			lightPositionXTextField.setBounds(130, 435, 150, 20);
			setTextField(lightPositionXTextField, Editor.getProperties().getGraphic().getLightPositionX());
		}
		return lightPositionXTextField;
	}

	/**
	 * This method initializes lightPositionYTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getLightPositionYTextField()
	{
		if (lightPositionYTextField == null)
		{
			lightPositionYTextField = new JTextField();
			lightPositionYTextField.setBounds(130, 460, 150, 20);
			setTextField(lightPositionYTextField, Editor.getProperties().getGraphic().getLightPositionY());
		}
		return lightPositionYTextField;
	}

	/**
	 * This method initializes lightPositionZTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getLightPositionZTextField()
	{
		if (lightPositionZTextField == null)
		{
			lightPositionZTextField = new JTextField();
			lightPositionZTextField.setBounds(130, 485, 150, 20);
			setTextField(lightPositionZTextField, Editor.getProperties().getGraphic().getLightPositionZ());
		}
		return lightPositionZTextField;
	}

	/**
	 * This method initializes shininessTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getShininessTextField()
	{
		if (shininessTextField == null)
		{
			shininessTextField = new JTextField();
			shininessTextField.setBounds(130, 510, 150, 20);
			setTextField(shininessTextField, Editor.getProperties().getGraphic().getShininess());
		}
		return shininessTextField;
	}

	/**
	 * This method initializes fovFactorTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getFovFactorTextField()
	{
		if (fovFactorTextField == null)
		{
			fovFactorTextField = new JTextField();
			fovFactorTextField.setBounds(130, 535, 150, 20);
			setTextField(fovFactorTextField, Editor.getProperties().getGraphic().getFovFactor());
		}
		return fovFactorTextField;
	}

	/**
	 *
	 */
	public void exit()
	{
		String result = null;
		if (isDifferent(getDescriptionTextField().getText(),
			Editor.getProperties().getGraphic().getDescription(), result))
		{
			Editor.getProperties().getGraphic().setDescription(result);
			frame.documentIsModified = true;
		}

		if (isDifferent(getDescriptionNightTextField().getText(),
			Editor.getProperties().getGraphic().getDescriptionNight(), result))
		{
			Editor.getProperties().getGraphic().setDescriptionNight(result);
			frame.documentIsModified = true;
		}

		if (isDifferent(getDescriptionRainNightTextField().getText(),
			Editor.getProperties().getGraphic().getDescriptionRainNight(), result))
		{
			Editor.getProperties().getGraphic().setDescriptionRainNight(result);
			frame.documentIsModified = true;
		}

		if (isDifferent(getBackgroundImageTextField().getText(),
			Editor.getProperties().getGraphic().getBackgroundImage(), result))
		{
			Editor.getProperties().getGraphic().setBackgroundImage(result);
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

		try
		{
			double value = Double.parseDouble(getBackgroundColorRTextField().getText());
			if (value != Editor.getProperties().getGraphic().getBackgroundColorR())
			{
				Editor.getProperties().getGraphic().setBackgroundColorR(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getBackgroundColorR()))
			{
				Editor.getProperties().getGraphic().setBackgroundColorR(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getBackgroundColorGTextField().getText());
			if (value != Editor.getProperties().getGraphic().getBackgroundColorG())
			{
				Editor.getProperties().getGraphic().setBackgroundColorG(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getBackgroundColorG()))
			{
				Editor.getProperties().getGraphic().setBackgroundColorG(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getBackgroundColorBTextField().getText());
			if (value != Editor.getProperties().getGraphic().getBackgroundColorB())
			{
				Editor.getProperties().getGraphic().setBackgroundColorB(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getBackgroundColorB()))
			{
				Editor.getProperties().getGraphic().setBackgroundColorB(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getAmbientColorRTextField().getText());
			if (value != Editor.getProperties().getGraphic().getAmbientColorR())
			{
				Editor.getProperties().getGraphic().setAmbientColorR(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getAmbientColorR()))
			{
				Editor.getProperties().getGraphic().setAmbientColorR(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getAmbientColorGTextField().getText());
			if (value != Editor.getProperties().getGraphic().getAmbientColorG())
			{
				Editor.getProperties().getGraphic().setAmbientColorG(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getAmbientColorG()))
			{
				Editor.getProperties().getGraphic().setAmbientColorG(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getAmbientColorBTextField().getText());
			if (value != Editor.getProperties().getGraphic().getAmbientColorB())
			{
				Editor.getProperties().getGraphic().setAmbientColorB(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getAmbientColorB()))
			{
				Editor.getProperties().getGraphic().setAmbientColorB(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getDiffuseColorRTextField().getText());
			if (value != Editor.getProperties().getGraphic().getDiffuseColorR())
			{
				Editor.getProperties().getGraphic().setDiffuseColorR(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getDiffuseColorR()))
			{
				Editor.getProperties().getGraphic().setDiffuseColorR(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getDiffuseColorGTextField().getText());
			if (value != Editor.getProperties().getGraphic().getDiffuseColorG())
			{
				Editor.getProperties().getGraphic().setDiffuseColorG(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getDiffuseColorG()))
			{
				Editor.getProperties().getGraphic().setDiffuseColorG(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getDiffuseColorBTextField().getText());
			if (value != Editor.getProperties().getGraphic().getDiffuseColorB())
			{
				Editor.getProperties().getGraphic().setDiffuseColorB(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getDiffuseColorB()))
			{
				Editor.getProperties().getGraphic().setDiffuseColorB(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getLightPositionXTextField().getText());
			if (value != Editor.getProperties().getGraphic().getLightPositionX())
			{
				Editor.getProperties().getGraphic().setLightPositionX(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getLightPositionX()))
			{
				Editor.getProperties().getGraphic().setLightPositionX(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getLightPositionYTextField().getText());
			if (value != Editor.getProperties().getGraphic().getLightPositionY())
			{
				Editor.getProperties().getGraphic().setLightPositionY(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getLightPositionY()))
			{
				Editor.getProperties().getGraphic().setLightPositionY(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getLightPositionZTextField().getText());
			if (value != Editor.getProperties().getGraphic().getLightPositionZ())
			{
				Editor.getProperties().getGraphic().setLightPositionZ(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getLightPositionZ()))
			{
				Editor.getProperties().getGraphic().setLightPositionZ(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getShininessTextField().getText());
			if (value != Editor.getProperties().getGraphic().getShininess())
			{
				Editor.getProperties().getGraphic().setShininess(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getShininess()))
			{
				Editor.getProperties().getGraphic().setShininess(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getFovFactorTextField().getText());
			if (value != Editor.getProperties().getGraphic().getFovFactor())
			{
				Editor.getProperties().getGraphic().setFovFactor(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getFovFactor()))
			{
				Editor.getProperties().getGraphic().setFovFactor(Double.NaN);
				frame.documentIsModified = true;
			}
		}
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
