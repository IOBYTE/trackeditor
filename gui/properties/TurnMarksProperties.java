/*
 *   TurnMarksProperties.java
 *   Created on 31 May 2022
 *
 *    The TurnMarksProperties.java is part of TrackEditor-0.7.0.
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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.Editor;
import utils.circuit.TurnMarks;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TurnMarksProperties extends PropertyPanel
{
	private JLabel		widthLabel					= null;
	private JTextField	widthTextField				= null;
	private JLabel		heightLabel					= null;
	private JTextField	heightTextField				= null;
	private JLabel		verticalSpaceLabel			= null;
	private JTextField	verticalSpaceTextField		= null;
	private JLabel		horizontalSpaceLabel		= null;
	private JTextField	horizontalSpaceTextField	= null;
	private JButton		defaultButton				= null;
	private JButton		deleteButton				= null;

	/**
	 *
	 */
	public TurnMarksProperties(EditorFrame frame)
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
		widthLabel = new JLabel();
		heightLabel = new JLabel();
		verticalSpaceLabel = new JLabel();
		horizontalSpaceLabel = new JLabel();
		this.setLayout(null);
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		this.setSize(362, 251);
		widthLabel.setBounds(10, 10, 70, 20);
		widthLabel.setText("Width");
		heightLabel.setBounds(10, 35, 70, 20);
		heightLabel.setText("Height");
		verticalSpaceLabel.setBounds(10, 60, 120, 20);
		verticalSpaceLabel.setText("Vertical Space");
		horizontalSpaceLabel.setBounds(10, 85, 120, 20);
		horizontalSpaceLabel.setText("Horizontal Space");
		this.add(widthLabel, null);
		this.add(heightLabel, null);
		this.add(verticalSpaceLabel, null);
		this.add(horizontalSpaceLabel, null);
		this.add(getWidthTextField(), null);
		this.add(getHeightTextField(), null);
		this.add(getVerticalSpaceTextField(), null);
		this.add(getHorizontalSpaceTextField(), null);
		this.add(getDefaultButton(), null);
		this.add(getDeleteButton(), null);
	}

	/**
	 * This method initializes widthTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getWidthTextField()
	{
		if (widthTextField == null)
		{
			widthTextField = new JTextField();
			widthTextField.setBounds(100, 10, 100, 20);
			setTextField(widthTextField, Editor.getProperties().getGraphic().getTurnMarks().getWidth());
		}
		return widthTextField;
	}
	/**
	 * This method initializes heightTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getHeightTextField()
	{
		if (heightTextField == null)
		{
			heightTextField = new JTextField();
			heightTextField.setBounds(100, 35, 100, 20);
			setTextField(heightTextField, Editor.getProperties().getGraphic().getTurnMarks().getHeight());
		}
		return heightTextField;
	}
	/**
	 * This method initializes verticalSpaceTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getVerticalSpaceTextField()
	{
		if (verticalSpaceTextField == null)
		{
			verticalSpaceTextField = new JTextField();
			verticalSpaceTextField.setBounds(100, 60, 100, 20);
			setTextField(verticalSpaceTextField, Editor.getProperties().getGraphic().getTurnMarks().getVerticalSpace());
		}
		return verticalSpaceTextField;
	}
	/**
	 * This method initializes horizontalSpaceTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getHorizontalSpaceTextField()
	{
		if (horizontalSpaceTextField == null)
		{
			horizontalSpaceTextField = new JTextField();
			horizontalSpaceTextField.setBounds(100, 85, 100, 20);
			setTextField(horizontalSpaceTextField, Editor.getProperties().getGraphic().getTurnMarks().getHorizontalSpace());
		}
		return horizontalSpaceTextField;
	}
	/**
	 * This method initializes defaultButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDefaultButton()
	{
		if (defaultButton == null)
		{
			defaultButton = new JButton();
			defaultButton.setBounds(300, 15, 80, 25);
			defaultButton.setText("Default");
			defaultButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					widthTextField.setText(TurnMarks.DEFAULT_WIDTH + "");
					heightTextField.setText(TurnMarks.DEFAULT_HEIGHT + "");
					verticalSpaceTextField.setText(TurnMarks.DEFAULT_VERTICAL_SPACE + "");
					horizontalSpaceTextField.setText(TurnMarks.DEFAULT_HORIZONTAL_SPACE + "");
				}
			});
		}
		return defaultButton;
	}
	/**
	 * This method initializes deleteButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteButton()
	{
		if (deleteButton == null)
		{
			deleteButton = new JButton();
			deleteButton.setBounds(300, 50, 80, 25);
			deleteButton.setText("Delete");
			deleteButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					widthTextField.setText("");
					heightTextField.setText("");
					verticalSpaceTextField.setText("");
					horizontalSpaceTextField.setText("");
				}
			});
		}
		return deleteButton;
	}

	/**
	 *
	 */
	public void exit()
	{
		try
		{
			double value = Double.parseDouble(getWidthTextField().getText());
			if (value != Editor.getProperties().getGraphic().getTurnMarks().getWidth())
			{
				Editor.getProperties().getGraphic().getTurnMarks().setWidth(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getTurnMarks().getWidth()))
			{
				Editor.getProperties().getGraphic().getTurnMarks().setWidth(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getHeightTextField().getText());
			if (value != Editor.getProperties().getGraphic().getTurnMarks().getHeight())
			{
				Editor.getProperties().getGraphic().getTurnMarks().setHeight(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getTurnMarks().getHeight()))
			{
				Editor.getProperties().getGraphic().getTurnMarks().setHeight(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getVerticalSpaceTextField().getText());
			if (value != Editor.getProperties().getGraphic().getTurnMarks().getVerticalSpace())
			{
				Editor.getProperties().getGraphic().getTurnMarks().setVerticalSpace(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getTurnMarks().getVerticalSpace()))
			{
				Editor.getProperties().getGraphic().getTurnMarks().setVerticalSpace(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getHorizontalSpaceTextField().getText());
			if (value != Editor.getProperties().getGraphic().getTurnMarks().getHorizontalSpace())
			{
				Editor.getProperties().getGraphic().getTurnMarks().setHorizontalSpace(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getTurnMarks().getHorizontalSpace()))
			{
				Editor.getProperties().getGraphic().getTurnMarks().setHorizontalSpace(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		Editor.getProperties().valueChanged();
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
