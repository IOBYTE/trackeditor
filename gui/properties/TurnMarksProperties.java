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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
		this.add(getTurnMarksWidthTextField(), null);
		this.add(getTurnMarksHeightTextField(), null);
		this.add(getTurnMarksVerticalSpaceTextField(), null);
		this.add(getTurnMarksHorizontalSpaceTextField(), null);
	}
	/**
	 * This method initializes widthTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTurnMarksWidthTextField()
	{
		if (widthTextField == null)
		{
			widthTextField = new JTextField();
			if (Editor.getProperties().getTurnMarks() != null)
				widthTextField.setText(Editor.getProperties().getTurnMarks().getWidth() + "");
			widthTextField.setBounds(100, 10, 100, 20);
			widthTextField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent le) {
				}
			});
		}
		return widthTextField;
	}
	/**
	 * This method initializes heightTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTurnMarksHeightTextField()
	{
		if (heightTextField == null)
		{
			heightTextField = new JTextField();
			if (Editor.getProperties().getTurnMarks() != null)
				heightTextField.setText(Editor.getProperties().getTurnMarks().getHeight() + "");
			heightTextField.setBounds(100, 35, 100, 20);
		}
		return heightTextField;
	}
	/**
	 * This method initializes verticalSpaceTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTurnMarksVerticalSpaceTextField()
	{
		if (verticalSpaceTextField == null)
		{
			verticalSpaceTextField = new JTextField();
			if (Editor.getProperties().getTurnMarks() != null)
				verticalSpaceTextField.setText(Editor.getProperties().getTurnMarks().getVerticalSpace() + "");
			verticalSpaceTextField.setBounds(100, 60, 100, 20);
		}
		return verticalSpaceTextField;
	}
	/**
	 * This method initializes horizontalSpaceTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTurnMarksHorizontalSpaceTextField()
	{
		if (horizontalSpaceTextField == null)
		{
			horizontalSpaceTextField = new JTextField();
			if (Editor.getProperties().getTurnMarks() != null)
				horizontalSpaceTextField.setText(Editor.getProperties().getTurnMarks().getHorizontalSpace() + "");
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
			Editor.getProperties().getTurnMarks().setWidth(Double.parseDouble(this.getTurnMarksWidthTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getTurnMarks().setWidth(Double.NaN);
		}
		try
		{
			Editor.getProperties().getTurnMarks().setHeight(Double.parseDouble(this.getTurnMarksHeightTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getTurnMarks().setHeight(Double.NaN);
		}
		try
		{
			Editor.getProperties().getTurnMarks().setVerticalSpace(Double.parseDouble(this.getTurnMarksVerticalSpaceTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getTurnMarks().setVerticalSpace(Double.NaN);
		}
		try
		{
			Editor.getProperties().getTurnMarks().setHorizontalSpace(Double.parseDouble(this.getTurnMarksHorizontalSpaceTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getTurnMarks().setHorizontalSpace(Double.NaN);
		}
		
		Editor.getProperties().valueChanged();
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
