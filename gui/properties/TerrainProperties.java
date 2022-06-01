/*
 *   TerrainProperties.java
 *   Created on 31 May 2022
 *
 *    The TerrainProperties.java is part of TrackEditor-0.7.0.
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
	private JLabel		trackStepLabel				= null;
	private JTextField	trackStepTextField			= null;
	private JLabel		borderMarginLabel			= null;
	private JTextField	borderMarginTextField		= null;
	private JLabel		borderStepLabel				= null;
	private JTextField	borderStepTextField			= null;
	private JLabel		borderHeightLabel			= null;
	private JTextField	borderHeightTextField		= null;
	private JLabel		orientationLabel			= null;
	private JComboBox	orientationComboBox			= null;
	private JLabel		maximumAltitudeLabel		= null;
	private JTextField	maximumAltitudeTextField	= null;
	private JLabel		minimumAltitudeLabel		= null;
	private JTextField	minimumAltitudeTextField	= null;
	private JLabel		groupSizeLabel				= null;
	private JTextField	groupSizeTextField			= null;
	private JLabel		elevationMapLabel			= null;
	private JTextField	elevationMapTextField		= null;		// TODO add button to get file
	private JLabel		reliefFileLabel				= null;
	private JTextField	reliefFileTextField			= null;		// TODO add button to get file
	private JLabel		surfaceLabel				= null;
	private JTextField	surfaceTextField			= null;		// TODO replace with JComboBox?
	
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
		maximumAltitudeLabel = new JLabel();
		minimumAltitudeLabel = new JLabel();
		groupSizeLabel = new JLabel();
		elevationMapLabel = new JLabel();
		reliefFileLabel = new JLabel();
		surfaceLabel = new JLabel();
		this.setLayout(null);
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		this.setSize(362, 251);		
		trackStepLabel.setBounds(10, 10, 90, 20);
		trackStepLabel.setText("Track Step");
		borderMarginLabel.setBounds(10, 35, 90, 20);
		borderMarginLabel.setText("Border Margin");
		borderStepLabel.setBounds(10, 60, 90, 20);
		borderStepLabel.setText("Border Step");
		borderHeightLabel.setBounds(10, 85, 90, 20);
		borderHeightLabel.setText("Border Height");
		orientationLabel.setBounds(10, 110, 90, 20);
		orientationLabel.setText("Orientation");
		maximumAltitudeLabel.setBounds(10, 135, 90, 20);
		maximumAltitudeLabel.setText("Maximum Altitude");
		minimumAltitudeLabel.setBounds(10, 160, 90, 20);
		minimumAltitudeLabel.setText("Minimum Altitude");
		groupSizeLabel.setBounds(10, 185, 90, 20);
		groupSizeLabel.setText("Group Size");
		elevationMapLabel.setBounds(10, 210, 90, 20);
		elevationMapLabel.setText("Elevation Map");
		reliefFileLabel.setBounds(10, 235, 90, 20);
		reliefFileLabel.setText("Reliefe File");
		surfaceLabel.setBounds(10, 260, 90, 20);
		surfaceLabel.setText("Surface");
		this.add(trackStepLabel, null);
		this.add(borderMarginLabel, null);
		this.add(borderStepLabel, null);
		this.add(borderHeightLabel, null);
		this.add(orientationLabel, null);
		this.add(maximumAltitudeLabel, null);
		this.add(minimumAltitudeLabel, null);
		this.add(groupSizeLabel, null);
		this.add(elevationMapLabel, null);
		this.add(reliefFileLabel, null);
		this.add(surfaceLabel, null);
		this.add(getTrackStepTextField(), null);
		this.add(getBorderMarginTextField(), null);
		this.add(getBorderStepTextField(), null);
		this.add(getBorderHeightTextField(), null);
		this.add(getOrientationComboBox(), null);
		this.add(getMaximumAltitudeTextField(), null);
		this.add(getMinimumAltitudeTextField(), null);
		this.add(getGroupSizeTextField(), null);
		this.add(getElevationMapTextField(), null);
		this.add(getReliefFileTextField(), null);
		this.add(getSurfaceTextField(), null);
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
			trackStepTextField.setBounds(120, 10, 100, 20);
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
			borderMarginTextField.setBounds(120, 35, 100, 20);
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
			borderStepTextField.setBounds(120, 60, 100, 20);
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
			borderHeightTextField.setBounds(120, 85, 100, 20);
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
			orientationComboBox.setBounds(120, 110, 100, 20);
		}
		return orientationComboBox;
	}
	/**
	 * This method initializes maximumAltitudeTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getMaximumAltitudeTextField()
	{
		if (maximumAltitudeTextField == null)
		{
			maximumAltitudeTextField = new JTextField();
			maximumAltitudeTextField.setText(Editor.getProperties().getTerrainMaximumAltitude() + "");
			maximumAltitudeTextField.setBounds(120, 135, 100, 20);
		}
		return maximumAltitudeTextField;
	}
	/**
	 * This method initializes minimumAltitudeTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getMinimumAltitudeTextField()
	{
		if (minimumAltitudeTextField == null)
		{
			minimumAltitudeTextField = new JTextField();
			minimumAltitudeTextField.setText(Editor.getProperties().getTerrainMinimumAltitude() + "");
			minimumAltitudeTextField.setBounds(120, 160, 100, 20);
		}
		return minimumAltitudeTextField;
	}
	/**
	 * This method initializes groupSizeTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getGroupSizeTextField()
	{
		if (groupSizeTextField == null)
		{
			groupSizeTextField = new JTextField();
			groupSizeTextField.setText(Editor.getProperties().getTerrainGroupSize() + "");
			groupSizeTextField.setBounds(120, 185, 100, 20);
		}
		return groupSizeTextField;
	}

	/**
	 * This method initializes elevationMapTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getElevationMapTextField()
	{
		if (elevationMapTextField == null)
		{
			elevationMapTextField = new JTextField();
			elevationMapTextField.setText(Editor.getProperties().getTerrainElevationMap());
			elevationMapTextField.setBounds(120, 210, 100, 20);
		}
		return elevationMapTextField;
	}
	/**
	 * This method initializes reliefFileTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getReliefFileTextField()
	{
		if (reliefFileTextField == null)
		{
			reliefFileTextField = new JTextField();
			reliefFileTextField.setText(Editor.getProperties().getTerrainReliefFile());
			reliefFileTextField.setBounds(120, 235, 100, 20);
		}
		return reliefFileTextField;
	}
	/**
	 * This method initializes surfaceTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getSurfaceTextField()
	{
		if (surfaceTextField == null)
		{
			surfaceTextField = new JTextField();
			surfaceTextField.setText(Editor.getProperties().getTerrainSurface());
			surfaceTextField.setBounds(120, 260, 100, 20);
		}
		return surfaceTextField;
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
