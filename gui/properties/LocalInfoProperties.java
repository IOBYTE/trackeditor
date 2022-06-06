/*
 *   LocalInfoProperties.java
 *   Created on 3 June 2022
 *
 *    The LocalInfoProperties.java is part of TrackEditor-0.7.0.
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
public class LocalInfoProperties extends JPanel
{
	private JLabel		stationLabel						= null;
	private JTextField	stationTextField					= null;
	private JLabel		timezoneLabel						= null;
	private JTextField	timezoneTextField					= null;
	private JLabel		overallRainLikelyhoodSpaceLabel		= null;
	private JTextField	overallRainLikelyhoodSpaceTextField	= null;
	private JLabel		littleRainLikelyhoodLabel			= null;
	private JTextField	littleRainLikelyhoodTextField		= null;
	private JLabel		mediumRainLikelyhoodLabel			= null;
	private JTextField	mediumRainLikelyhoodTextField		= null;
	private JLabel		timeOfDayLabel						= null;
	private JTextField	timeOfDayTextField					= null;
	private JLabel		sunAscensionLabel					= null;
	private JTextField	sunAscensionTextField				= null;
	private JLabel		altitudeLabel						= null;
	private JTextField	altitudeTextField					= null;
	
	/**
	 *  
	 */
	public LocalInfoProperties()
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
		stationLabel = new JLabel();
		timezoneLabel = new JLabel();
		overallRainLikelyhoodSpaceLabel = new JLabel();
		littleRainLikelyhoodLabel = new JLabel();
		mediumRainLikelyhoodLabel = new JLabel();
		timeOfDayLabel = new JLabel();
		sunAscensionLabel = new JLabel();
		altitudeLabel = new JLabel();
		this.setLayout(null);
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		this.setSize(362, 251);		
		stationLabel.setBounds(10, 10, 70, 20);
		stationLabel.setText("Station");
		timezoneLabel.setBounds(10, 35, 70, 20);
		timezoneLabel.setText("Timezone");
		overallRainLikelyhoodSpaceLabel.setBounds(10, 60, 140, 20);
		overallRainLikelyhoodSpaceLabel.setText("Overall Rain Likelyhood");
		littleRainLikelyhoodLabel.setBounds(10, 85, 140, 20);
		littleRainLikelyhoodLabel.setText("Little Rain Likelyhood");
		mediumRainLikelyhoodLabel.setBounds(10, 110, 140, 20);
		mediumRainLikelyhoodLabel.setText("Medium Rain Likelyhood");
		timeOfDayLabel.setBounds(10, 135, 140, 20);
		timeOfDayLabel.setText("Time Of Day");
		sunAscensionLabel.setBounds(10, 160, 140, 20);
		sunAscensionLabel.setText("Sun Ascension");
		altitudeLabel.setBounds(10, 185, 140, 20);
		altitudeLabel.setText("Altitude");
		this.add(stationLabel, null);
		this.add(timezoneLabel, null);
		this.add(overallRainLikelyhoodSpaceLabel, null);
		this.add(littleRainLikelyhoodLabel, null);
		this.add(mediumRainLikelyhoodLabel, null);
		this.add(timeOfDayLabel, null);
		this.add(sunAscensionLabel, null);
		this.add(altitudeLabel, null);
		this.add(getStationTextField(), null);
		this.add(getTimezoneTextField(), null);
		this.add(getOverallRainLikelyhoodTextField(), null);
		this.add(getLittleRainLikelyhoodTextField(), null);
		this.add(getMediumRainLikelyhoodTextField(), null);
		this.add(getTimeOfDayTextField(), null);
		this.add(getSunAscensionTextField(), null);
		this.add(getAltitudeTextField(), null);
	}
	/**
	 * This method initializes stationTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getStationTextField()
	{
		if (stationTextField == null)
		{
			stationTextField = new JTextField();
			stationTextField.setText(Editor.getProperties().getLocalInfo().getStation());
			stationTextField.setBounds(150, 10, 100, 20);
			stationTextField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent le) {
				}
			});
		}
		return stationTextField;
	}
	/**
	 * This method initializes timezoneTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTimezoneTextField()
	{
		if (timezoneTextField == null)
		{
			timezoneTextField = new JTextField();
			double timezone = Editor.getProperties().getLocalInfo().getTimezone();
			if (!Double.isNaN(timezone))
				timezoneTextField.setText(timezone + "");
			timezoneTextField.setBounds(150, 35, 100, 20);
		}
		return timezoneTextField;
	}
	/**
	 * This method initializes overallRainLikelyhoodTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getOverallRainLikelyhoodTextField()
	{
		if (overallRainLikelyhoodSpaceTextField == null)
		{
			overallRainLikelyhoodSpaceTextField = new JTextField();
			double overall = Editor.getProperties().getLocalInfo().getOverallRainLikelyhood();
			if (!Double.isNaN(overall))
				overallRainLikelyhoodSpaceTextField.setText(overall + "");
			overallRainLikelyhoodSpaceTextField.setBounds(150, 60, 100, 20);
		}
		return overallRainLikelyhoodSpaceTextField;
	}
	/**
	 * This method initializes littleRainLikelyhoodTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getLittleRainLikelyhoodTextField()
	{
		if (littleRainLikelyhoodTextField == null)
		{
			littleRainLikelyhoodTextField = new JTextField();
			double little = Editor.getProperties().getLocalInfo().getLittleRainLikelyhood();
			if (!Double.isNaN(little))
				littleRainLikelyhoodTextField.setText(little + "");
			littleRainLikelyhoodTextField.setBounds(150, 85, 100, 20);
		}
		return littleRainLikelyhoodTextField;
	}	
	/**
	 * This method initializes mediumRainLikelyhoodTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getMediumRainLikelyhoodTextField()
	{
		if (mediumRainLikelyhoodTextField == null)
		{
			mediumRainLikelyhoodTextField = new JTextField();
			double medium = Editor.getProperties().getLocalInfo().getMediumRainLikelyhood();
			if (!Double.isNaN(medium))
				mediumRainLikelyhoodTextField.setText(medium + "");
			mediumRainLikelyhoodTextField.setBounds(150, 110, 100, 20);
		}
		return mediumRainLikelyhoodTextField;
	}	

	/**
	 * This method initializes timeOfDayTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTimeOfDayTextField()
	{
		if (timeOfDayTextField == null)
		{
			timeOfDayTextField = new JTextField();
			double timeOfDay = Editor.getProperties().getLocalInfo().getTimeOfDay();
			if (!Double.isNaN(timeOfDay))
				timeOfDayTextField.setText(timeOfDay + "");
			timeOfDayTextField.setBounds(150, 135, 100, 20);
		}
		return timeOfDayTextField;
	}	

	/**
	 * This method initializes sunAscensionTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getSunAscensionTextField()
	{
		if (sunAscensionTextField == null)
		{
			sunAscensionTextField = new JTextField();
			double sunAscension = Editor.getProperties().getLocalInfo().getSunAscension();
			if (!Double.isNaN(sunAscension))
				sunAscensionTextField.setText(sunAscension + "");
			sunAscensionTextField.setBounds(150, 160, 100, 20);
		}
		return sunAscensionTextField;
	}	

	/**
	 * This method initializes altitudeTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getAltitudeTextField()
	{
		if (altitudeTextField == null)
		{
			altitudeTextField = new JTextField();
			double altitude = Editor.getProperties().getLocalInfo().getAltitude();
			if (!Double.isNaN(altitude))
				altitudeTextField.setText(altitude + "");
			altitudeTextField.setBounds(150, 185, 100, 20);
		}
		return altitudeTextField;
	}	

	/**
	 *  
	 */
	public void exit()
	{
		Editor.getProperties().getLocalInfo().setStation(this.getStationTextField().getText());
		try
		{
			Editor.getProperties().getLocalInfo().setTimezone(Double.parseDouble(this.getTimezoneTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getLocalInfo().setTimezone(Double.NaN);
		}
		try
		{
			Editor.getProperties().getLocalInfo().setOverallRainLikelyhood(Double.parseDouble(this.getOverallRainLikelyhoodTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getLocalInfo().setOverallRainLikelyhood(Double.NaN);
		}
		try
		{
			Editor.getProperties().getLocalInfo().setLittleRainLikelyhood(Double.parseDouble(this.getLittleRainLikelyhoodTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getLocalInfo().setLittleRainLikelyhood(Double.NaN);
		}
		try
		{
			Editor.getProperties().getLocalInfo().setMediumRainLikelyhood(Double.parseDouble(this.getMediumRainLikelyhoodTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getLocalInfo().setMediumRainLikelyhood(Double.NaN);
		}
		try
		{
			Editor.getProperties().getLocalInfo().setTimeOfDay(Double.parseDouble(this.getTimeOfDayTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getLocalInfo().setTimeOfDay(Double.NaN);
		}
		try
		{
			Editor.getProperties().getLocalInfo().setSunAscension(Double.parseDouble(this.getSunAscensionTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getLocalInfo().setSunAscension(Double.NaN);
		}
		try
		{
			Editor.getProperties().getLocalInfo().setAltitude(Double.parseDouble(this.getAltitudeTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getLocalInfo().setAltitude(Double.NaN);
		}

		Editor.getProperties().valueChanged();
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
