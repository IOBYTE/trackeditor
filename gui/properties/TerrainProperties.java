/*
 *   TerrainProperties.java
 *   Created on 31 May 2022
 *
 *    The PitProperties.java is part of TrackEditor-0.7.0.
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

import javax.swing.JComboBox;
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
public class TerrainProperties extends JPanel
{
	private JLabel		trackStepLabel			= null;
	private JTextField	trackStepTextField		= null;
	private JLabel		borderMarginLabel		= null;
	private JTextField	borderMarginTextField	= null;
	private JLabel		borderStepLabel			= null;
	private JTextField	borderStepTextField		= null;
	private JLabel		borderHeightLabel		= null;
	private JTextField	borderHeightTextField	= null;
	private JLabel		orientationLabel		= null;
	private JComboBox	orientationComboBox		= null;
	
	/**
	 *  
	 */
	public TerrainProperties()
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
		trackStepLabel = new JLabel();
		borderMarginLabel = new JLabel();
		borderStepLabel = new JLabel();
		borderHeightLabel = new JLabel();
		orientationLabel = new JLabel();		
		this.setLayout(null);
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		this.setSize(362, 251);		
		trackStepLabel.setBounds(10, 10, 70, 20);
		trackStepLabel.setText("Track Step");
		borderMarginLabel.setBounds(10, 35, 70, 20);
		borderMarginLabel.setText("Border margin");
		borderStepLabel.setBounds(10, 60, 120, 20);
		borderStepLabel.setText("Border Step");
		borderHeightLabel.setBounds(10, 85, 120, 20);
		borderHeightLabel.setText("Border Height");
		orientationLabel.setBounds(10, 110, 70, 20);
		orientationLabel.setText("Orientation");	
		this.add(trackStepLabel, null);
		this.add(borderMarginLabel, null);
		this.add(borderStepLabel, null);
		this.add(borderHeightLabel, null);
		this.add(orientationLabel, null);
		this.add(getTrackStepTextField(), null);
		this.add(getBorderMarginTextField(), null);
		this.add(getBorderStepTextField(), null);
		this.add(getBorderHeightTextField(), null);
		this.add(getOrientationComboBox(), null);
	}
	/**
	 * This method initializes trackStepTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTrackStepTextField()
	{
		if (trackStepTextField == null)
		{
			trackStepTextField = new JTextField();
			trackStepTextField.setText(Editor.getProperties().getTerrainTrackStep() + "");
			trackStepTextField.setBounds(100, 10, 100, 20);
		}
		return trackStepTextField;
	}
	/**
	 * This method initializes borderMarginTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getBorderMarginTextField()
	{
		if (borderMarginTextField == null)
		{
			borderMarginTextField = new JTextField();
			borderMarginTextField.setText(Editor.getProperties().getTerrainBorderMargin() + "");
			borderMarginTextField.setBounds(100, 35, 100, 20);
		}
		return borderMarginTextField;
	}
	/**
	 * This method initializes borderStepTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getBorderStepTextField()
	{
		if (borderStepTextField == null)
		{
			borderStepTextField = new JTextField();
			borderStepTextField.setText(Editor.getProperties().getTerrainBorderStep() + "");
			borderStepTextField.setBounds(100, 60, 100, 20);
		}
		return borderStepTextField;
	}
	/**
	 * This method initializes borderHeightTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getBorderHeightTextField()
	{
		if (borderHeightTextField == null)
		{
			borderHeightTextField = new JTextField();
			borderHeightTextField.setText(Editor.getProperties().getTerrainBorderHeight() + "");
			borderHeightTextField.setBounds(100, 85, 100, 20);
		}
		return borderHeightTextField;
	}	
	/**
	 * This method initializes orientationComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getOrientationComboBox()
	{
		if (orientationComboBox == null)
		{
			String[] items =
			{"clockwise", "counter-clockwise"};
			orientationComboBox = new JComboBox(items);
			orientationComboBox.setSelectedItem(Editor.getProperties().getTerrainOrientation());
			orientationComboBox.setBounds(100, 110, 100, 20);
		}
		return orientationComboBox;
	}

	/**
	 *  
	 */
	public void exit()
	{
		try
		{
			Editor.getProperties().setTerrainTrackStep(Double.parseDouble(this.getTrackStepTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().setTerrainTrackStep(Double.NaN);
		}
		try
		{
			Editor.getProperties().setTerrainBorderMargin(Double.parseDouble(this.getBorderMarginTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().setTerrainBorderMargin(Double.NaN);
		}
		try
		{
			Editor.getProperties().setTerrainBorderStep(Double.parseDouble(this.getBorderStepTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().setTerrainBorderStep(Double.NaN);
		}
		try
		{
			Editor.getProperties().setTerrainBorderHeight(Double.parseDouble(this.getBorderHeightTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().setTerrainBorderHeight(Double.NaN);
		}
		
		Editor.getProperties().setTerrainOrientation((String) getOrientationComboBox().getSelectedItem());
		Editor.getProperties().valueChanged();
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
