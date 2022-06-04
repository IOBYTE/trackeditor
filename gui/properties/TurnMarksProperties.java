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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utils.Editor;

/**
 * @author Robert Reif
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TurnMarksProperties extends JPanel
{
	private JLabel		widthLabel					= null;
	private JTextField	widthTextField				= null;
	private JLabel		heightLabel					= null;
	private JTextField	heightTextField				= null;
	private JLabel		verticalSpaceLabel			= null;
	private JTextField	verticalSpaceTextField		= null;
	private JLabel		horizontalSpaceLabel		= null;
	private JTextField	horizontalSpaceTextField	= null;
	
	/**
	 *  
	 */
	public TurnMarksProperties()
	{
		super();
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
		widthLabel.setText("Track Step");
		heightLabel.setBounds(10, 35, 70, 20);
		heightLabel.setText("Border margin");
		verticalSpaceLabel.setBounds(10, 60, 120, 20);
		verticalSpaceLabel.setText("Border Step");
		horizontalSpaceLabel.setBounds(10, 85, 120, 20);
		horizontalSpaceLabel.setText("Border Height");
		this.add(widthLabel, null);
		this.add(heightLabel, null);
		this.add(verticalSpaceLabel, null);
		this.add(horizontalSpaceLabel, null);
		this.add(getWidthTextField(), null);
		this.add(getHeightTextField(), null);
		this.add(getVerticalSpaceTextField(), null);
		this.add(getHorizontalSpaceTextField(), null);
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
			double width = Editor.getProperties().getGraphic().getTurnMarks().getWidth();
			if (!Double.isNaN(width))
				widthTextField.setText(width + "");
			widthTextField.setBounds(100, 10, 100, 20);
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
			double height = Editor.getProperties().getGraphic().getTurnMarks().getHeight();
			if (!Double.isNaN(height))
				heightTextField.setText(height + "");
			heightTextField.setBounds(100, 35, 100, 20);
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
			double space = Editor.getProperties().getGraphic().getTurnMarks().getVerticalSpace();
			if (!Double.isNaN(space))
				verticalSpaceTextField.setText(space + "");
			verticalSpaceTextField.setBounds(100, 60, 100, 20);
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
			double space = Editor.getProperties().getGraphic().getTurnMarks().getHorizontalSpace();
			if (!Double.isNaN(space))
				horizontalSpaceTextField.setText(space + "");
			horizontalSpaceTextField.setBounds(100, 85, 100, 20);
		}
		return horizontalSpaceTextField;
	}	

	/**
	 *  
	 */
	public void exit()
	{
		try
		{
			Editor.getProperties().getGraphic().getTurnMarks().setWidth(Double.parseDouble(this.getWidthTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getGraphic().getTurnMarks().setWidth(Double.NaN);
		}
		try
		{
			Editor.getProperties().getGraphic().getTurnMarks().setHeight(Double.parseDouble(this.getHeightTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getGraphic().getTurnMarks().setHeight(Double.NaN);
		}
		try
		{
			Editor.getProperties().getGraphic().getTurnMarks().setVerticalSpace(Double.parseDouble(this.getVerticalSpaceTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getGraphic().getTurnMarks().setVerticalSpace(Double.NaN);
		}
		try
		{
			Editor.getProperties().getGraphic().getTurnMarks().setHorizontalSpace(Double.parseDouble(this.getHorizontalSpaceTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getGraphic().getTurnMarks().setHorizontalSpace(Double.NaN);
		}
		
		Editor.getProperties().valueChanged();
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
