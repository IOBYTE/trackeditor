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
public class LocalInfoProperties extends JPanel
{
	private EditorFrame	frame;
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
	public LocalInfoProperties(EditorFrame frame)
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
		update();
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
			stationTextField.setBounds(150, 10, 100, 20);
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
			altitudeTextField.setBounds(150, 185, 100, 20);
		}
		return altitudeTextField;
	}

	private void update()
	{
		stationTextField.setText(Editor.getProperties().getLocalInfo().getStation());

		double value = Editor.getProperties().getLocalInfo().getTimezone();
		if (!Double.isNaN(value))
			timezoneTextField.setText(value + "");
		else
			timezoneTextField.setText("");

		value = Editor.getProperties().getLocalInfo().getOverallRainLikelyhood();
		if (!Double.isNaN(value))
			overallRainLikelyhoodSpaceTextField.setText(value + "");
		else
			overallRainLikelyhoodSpaceTextField.setText("");

		value = Editor.getProperties().getLocalInfo().getLittleRainLikelyhood();
		if (!Double.isNaN(value))
			littleRainLikelyhoodTextField.setText(value + "");
		else
			littleRainLikelyhoodTextField.setText("");

		value = Editor.getProperties().getLocalInfo().getMediumRainLikelyhood();
		if (!Double.isNaN(value))
			mediumRainLikelyhoodTextField.setText(value + "");
		else
			mediumRainLikelyhoodTextField.setText("");

		value = Editor.getProperties().getLocalInfo().getTimeOfDay();
		if (!Double.isNaN(value))
			timeOfDayTextField.setText(value + "");
		else
			timeOfDayTextField.setText("");

		value = Editor.getProperties().getLocalInfo().getSunAscension();
		if (!Double.isNaN(value))
			sunAscensionTextField.setText(value + "");
		else
			sunAscensionTextField.setText("");

		value = Editor.getProperties().getLocalInfo().getAltitude();
		if (!Double.isNaN(value))
			altitudeTextField.setText(value + "");
		else
			altitudeTextField.setText("");
	}

	/**
	 *
	 */
	public void exit()
	{
		String station = getStationTextField().getText();
		if (!station.equals(Editor.getProperties().getLocalInfo().getStation()))
		{
			Editor.getProperties().getLocalInfo().setStation(station);
			frame.documentIsModified = true;
		}

		try
		{
			double value = Double.parseDouble(this.getTimezoneTextField().getText());
			if (value != Editor.getProperties().getLocalInfo().getTimezone())
			{
				Editor.getProperties().getLocalInfo().setTimezone(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getLocalInfo().getTimezone()))
			{
				Editor.getProperties().getLocalInfo().setTimezone(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(this.getOverallRainLikelyhoodTextField().getText());
			if (value != Editor.getProperties().getLocalInfo().getOverallRainLikelyhood())
			{
				Editor.getProperties().getLocalInfo().setOverallRainLikelyhood(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getLocalInfo().getOverallRainLikelyhood()))
			{
				Editor.getProperties().getLocalInfo().setOverallRainLikelyhood(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(this.getLittleRainLikelyhoodTextField().getText());
			if (value != Editor.getProperties().getLocalInfo().getLittleRainLikelyhood())
			{
				Editor.getProperties().getLocalInfo().setLittleRainLikelyhood(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getLocalInfo().getLittleRainLikelyhood()))
			{
				Editor.getProperties().getLocalInfo().setLittleRainLikelyhood(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(this.getMediumRainLikelyhoodTextField().getText());
			if (value != Editor.getProperties().getLocalInfo().getMediumRainLikelyhood())
			{
				Editor.getProperties().getLocalInfo().setMediumRainLikelyhood(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getLocalInfo().getMediumRainLikelyhood()))
			{
				Editor.getProperties().getLocalInfo().setMediumRainLikelyhood(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(this.getTimeOfDayTextField().getText());
			if (value != Editor.getProperties().getLocalInfo().getTimeOfDay())
			{
				Editor.getProperties().getLocalInfo().setTimeOfDay(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getLocalInfo().getTimeOfDay()))
			{
				Editor.getProperties().getLocalInfo().setTimeOfDay(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(this.getSunAscensionTextField().getText());
			if (value != Editor.getProperties().getLocalInfo().getSunAscension())
			{
				Editor.getProperties().getLocalInfo().setSunAscension(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getLocalInfo().getSunAscension()))
			{
				Editor.getProperties().getLocalInfo().setSunAscension(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(this.getAltitudeTextField().getText());
			if (value != Editor.getProperties().getLocalInfo().getAltitude())
			{
				Editor.getProperties().getLocalInfo().setAltitude(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getLocalInfo().getAltitude()))
			{
				Editor.getProperties().getLocalInfo().setAltitude(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		Editor.getProperties().valueChanged();
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
