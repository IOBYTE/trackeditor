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
	private JLabel		turnMarksWidthLabel			= null;
	private JTextField	turnMarksWidthTextField		= null;
	private JLabel		turnMarksHeightLabel		= null;
	private JTextField	turnMarksHeightTextField	= null;
	private JLabel		turnMarksVerticalSpaceLabel			= null;
	private JTextField	turnMarksVerticalSpaceTextField		= null;
	private JLabel		turnMarksHorizontalSpaceLabel		= null;
	private JTextField	turnMarksHorizontalSpaceTextField	= null;
	
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
		turnMarksWidthLabel = new JLabel();
		turnMarksHeightLabel = new JLabel();
		turnMarksVerticalSpaceLabel = new JLabel();
		turnMarksHorizontalSpaceLabel = new JLabel();
		this.setLayout(null);
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		this.setSize(362, 251);		
		turnMarksWidthLabel.setBounds(10, 10, 70, 20);
		turnMarksWidthLabel.setText("Track Step");
		turnMarksHeightLabel.setBounds(10, 35, 70, 20);
		turnMarksHeightLabel.setText("Border margin");
		turnMarksVerticalSpaceLabel.setBounds(10, 60, 120, 20);
		turnMarksVerticalSpaceLabel.setText("Border Step");
		turnMarksHorizontalSpaceLabel.setBounds(10, 85, 120, 20);
		turnMarksHorizontalSpaceLabel.setText("Border Height");
		this.add(turnMarksWidthLabel, null);
		this.add(turnMarksHeightLabel, null);
		this.add(turnMarksVerticalSpaceLabel, null);
		this.add(turnMarksHorizontalSpaceLabel, null);
		this.add(getTurnMarksWidthTextField(), null);
		this.add(getTurnMarksHeightTextField(), null);
		this.add(getTurnMarksVerticalSpaceTextField(), null);
		this.add(getTurnMarksHorizontalSpaceTextField(), null);
	}
	/**
	 * This method initializes turnMarksWidthTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTurnMarksWidthTextField()
	{
		if (turnMarksWidthTextField == null)
		{
			turnMarksWidthTextField = new JTextField();
			turnMarksWidthTextField.setText(Editor.getProperties().getTurnMarksWidth() + "");
			turnMarksWidthTextField.setBounds(100, 10, 100, 20);
		}
		return turnMarksWidthTextField;
	}
	/**
	 * This method initializes turnMarksHeightTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTurnMarksHeightTextField()
	{
		if (turnMarksHeightTextField == null)
		{
			turnMarksHeightTextField = new JTextField();
			turnMarksHeightTextField.setText(Editor.getProperties().getTurnMarksHeight() + "");
			turnMarksHeightTextField.setBounds(100, 35, 100, 20);
		}
		return turnMarksHeightTextField;
	}
	/**
	 * This method initializes turnMarksVerticalSpaceTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTurnMarksVerticalSpaceTextField()
	{
		if (turnMarksVerticalSpaceTextField == null)
		{
			turnMarksVerticalSpaceTextField = new JTextField();
			turnMarksVerticalSpaceTextField.setText(Editor.getProperties().getTurnMarksVerticalSpace() + "");
			turnMarksVerticalSpaceTextField.setBounds(100, 60, 100, 20);
		}
		return turnMarksVerticalSpaceTextField;
	}
	/**
	 * This method initializes turnMarksHorizontalSpaceTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTurnMarksHorizontalSpaceTextField()
	{
		if (turnMarksHorizontalSpaceTextField == null)
		{
			turnMarksHorizontalSpaceTextField = new JTextField();
			turnMarksHorizontalSpaceTextField.setText(Editor.getProperties().getTurnMarksHorizontalSpace() + "");
			turnMarksHorizontalSpaceTextField.setBounds(100, 85, 100, 20);
		}
		return turnMarksHorizontalSpaceTextField;
	}	

	/**
	 *  
	 */
	public void exit()
	{
		try
		{
			Editor.getProperties().setTurnMarksWidth(Double.parseDouble(this.getTurnMarksWidthTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().setTurnMarksWidth(Double.NaN);
		}
		try
		{
			Editor.getProperties().setTurnMarksHeight(Double.parseDouble(this.getTurnMarksHeightTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().setTurnMarksHeight(Double.NaN);
		}
		try
		{
			Editor.getProperties().setTurnMarksVerticalSpace(Double.parseDouble(this.getTurnMarksVerticalSpaceTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().setTurnMarksVerticalSpace(Double.NaN);
		}
		try
		{
			Editor.getProperties().setTurnMarksHorizontalSpace(Double.parseDouble(this.getTurnMarksHorizontalSpaceTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().setTurnMarksHorizontalSpace(Double.NaN);
		}
		
		Editor.getProperties().valueChanged();
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
