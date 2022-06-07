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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.Editor;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class GraphicProperties extends JPanel
{
	private EditorFrame		frame;
	private JLabel			descriptionLabel				= null;
	private JTextField		descriptionTextField			= null;
	private JLabel			descriptionNightLabel			= null;
	private JTextField		descriptionNightTextField		= null;
	private JLabel			descriptionRainNightLabel		= null;
	private JTextField		descriptionRainNightTextField	= null;
	private JLabel			backgroundImageLabel			= null;
	private JTextField		backgroundImageTextField		= null;

	/**
	 *
	 */
	public GraphicProperties(EditorFrame frame)
	{
		super();
		this.frame = frame;
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
		this.add(descriptionLabel, null);
		this.add(descriptionNightLabel, null);
		this.add(descriptionRainNightLabel, null);
		this.add(backgroundImageLabel, null);
		this.add(getDescriptionTextField(), null);
		this.add(getDescriptionNightTextField(), null);
		this.add(getDescriptionRainNightTextField(), null);
		this.add(getBackgroundImageTextField(), null);
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
			String value = Editor.getProperties().getGraphic().getDescription();
			if (value != null && !value.isEmpty())
				descriptionTextField.setText(value);
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
			String value = Editor.getProperties().getGraphic().getDescriptionNight();
			if (value != null && !value.isEmpty())
				descriptionNightTextField.setText(value);
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
			String value = Editor.getProperties().getGraphic().getDescriptionRainNight();
			if (value != null && !value.isEmpty())
				descriptionRainNightTextField.setText(value);
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
			String value = Editor.getProperties().getGraphic().getBackgroundImage();
			if (value != null && !value.isEmpty())
				backgroundImageTextField.setText(value);
		}
		return backgroundImageTextField;
	}

	/**
	 *
	 */
	public void exit()
	{
		String description = getDescriptionTextField().getText();
		if (!description.equals(Editor.getProperties().getGraphic().getDescription()))
		{
			Editor.getProperties().getGraphic().setDescription(description);
			frame.documentIsModified = true;
		}

		String descriptionNight = getDescriptionNightTextField().getText();
		if (!descriptionNight.equals(Editor.getProperties().getGraphic().getDescriptionNight()))
		{
			Editor.getProperties().getGraphic().setDescriptionNight(descriptionNight);
			frame.documentIsModified = true;
		}

		String descriptionRainNight = getDescriptionRainNightTextField().getText();
		if (!descriptionRainNight.equals(Editor.getProperties().getGraphic().getDescriptionRainNight()))
		{
			Editor.getProperties().getGraphic().setDescriptionRainNight(descriptionRainNight);
			frame.documentIsModified = true;
		}

		String backgroundImage = getBackgroundImageTextField().getText();
		if (!backgroundImage.equals(Editor.getProperties().getGraphic().getBackgroundImage()))
		{
			Editor.getProperties().getGraphic().setBackgroundImage(backgroundImage);
			frame.documentIsModified = true;
		}

		Editor.getProperties().valueChanged();
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
