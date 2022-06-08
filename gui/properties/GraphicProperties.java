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
		this.add(descriptionLabel, null);
		this.add(descriptionNightLabel, null);
		this.add(descriptionRainNightLabel, null);
		this.add(backgroundImageLabel, null);
		this.add(backgroundTypeLabel, null);
		this.add(getDescriptionTextField(), null);
		this.add(getDescriptionNightTextField(), null);
		this.add(getDescriptionRainNightTextField(), null);
		this.add(getBackgroundImageTextField(), null);
		this.add(getBackgroundTypeComboBox(), null);
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
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
