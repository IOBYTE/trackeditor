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
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.MutableDouble;
import utils.MutableInteger;
import utils.MutableString;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class LocalInfoProperties extends PropertyPanel
{
	private JLabel		stationLabel					= new JLabel();
	private JTextField	stationTextField				= new JTextField();
	private JLabel		timezoneLabel					= new JLabel();
	private JTextField	timezoneTextField				= new JTextField();
	private JLabel		timeOfDayLabel					= new JLabel();
	private JTextField	timeOfDayTextField				= new JTextField();
	private JLabel		sunAscensionLabel				= new JLabel();
	private JTextField	sunAscensionTextField			= new JTextField();
	private JLabel		altitudeLabel					= new JLabel();
	private JTextField	altitudeTextField				= new JTextField();
	private JLabel		latitudeLabel					= new JLabel();
	private JTextField	latitudeTextField				= new JTextField();
	private JLabel		longitudeLabel					= new JLabel();
	private JTextField	longitudeTextField				= new JTextField();
	private JLabel		climatLabel						= new JLabel();
	private JTextField	climatTextField					= new JTextField();
	private JLabel		precipitationLabel				= new JLabel();
	private JTextField	precipitationTextField			= new JTextField();
	private JLabel		bestLapLabel					= new JLabel();
	private JTextField	bestLapTextField				= new JTextField();

	/**
	 *
	 */
	public LocalInfoProperties(EditorFrame frame)
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
		setLayout(null);
		setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));

		addLabel(this, 0, stationLabel, "Station", 150);
		addLabel(this, 1, timezoneLabel, "Timezone", 150);
		addLabel(this, 2, timeOfDayLabel, "Time Of Day", 150);
		addLabel(this, 3, sunAscensionLabel, "Sun Ascension", 150);
		addLabel(this, 4, altitudeLabel, "Altitude", 150);
		addLabel(this, 5, latitudeLabel, "Latitude", 150);
		addLabel(this, 6, longitudeLabel, "Longitude", 150);
		addLabel(this, 7, climatLabel, "Climate", 150);
		addLabel(this, 8, precipitationLabel, "Precipitation", 150);

		addTextField(this, 0, stationTextField, getEditorFrame().getTrackData().getLocalInfo().getStation(), 170, 125);
		addTextField(this, 1, timezoneTextField, getEditorFrame().getTrackData().getLocalInfo().getTimezone(), 170, 125);
		addTextField(this, 2, timeOfDayTextField, getEditorFrame().getTrackData().getLocalInfo().getTimeOfDay(), 170, 125);
		addTextField(this, 3, sunAscensionTextField, getEditorFrame().getTrackData().getLocalInfo().getSunAscension(), 170, 125);
		addTextField(this, 4, altitudeTextField, getEditorFrame().getTrackData().getLocalInfo().getAltitude(), 170, 125);
		addTextField(this, 5, latitudeTextField, getEditorFrame().getTrackData().getLocalInfo().getLatitude(), 170, 125);
		addTextField(this, 6, longitudeTextField, getEditorFrame().getTrackData().getLocalInfo().getLongitude(), 170, 125);
		addTextField(this, 7, climatTextField, getEditorFrame().getTrackData().getLocalInfo().getClimat(), 170, 125);
		addTextField(this, 8, precipitationTextField, getEditorFrame().getTrackData().getLocalInfo().getPrecipitation(), 170, 125);
		
		if (getEditorFrame().getGameType().hasBestLap())
		{
			addLabel(this, 9, bestLapLabel, "Best Lap", 150);
			addTextField(this, 9, bestLapTextField, getEditorFrame().getTrackData().getLocalInfo().getBestLap(), 170, 125);
		}
	}

	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();
		MutableInteger integerResult = new MutableInteger();

		if (isDifferent(stationTextField.getText(),
			getEditorFrame().getTrackData().getLocalInfo().getStation(), stringResult))
		{
			getEditorFrame().getTrackData().getLocalInfo().setStation(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(timezoneTextField.getText(),
			getEditorFrame().getTrackData().getLocalInfo().getTimezone(), doubleResult))
		{
			getEditorFrame().getTrackData().getLocalInfo().setTimezone(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(timeOfDayTextField.getText(),
			getEditorFrame().getTrackData().getLocalInfo().getTimeOfDay(), doubleResult))
		{
			getEditorFrame().getTrackData().getLocalInfo().setTimeOfDay(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(sunAscensionTextField.getText(),
			getEditorFrame().getTrackData().getLocalInfo().getSunAscension(), doubleResult))
		{
			getEditorFrame().getTrackData().getLocalInfo().setSunAscension(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(altitudeTextField.getText(),
			getEditorFrame().getTrackData().getLocalInfo().getAltitude(), doubleResult))
		{
			getEditorFrame().getTrackData().getLocalInfo().setAltitude(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(latitudeTextField.getText(),
			getEditorFrame().getTrackData().getLocalInfo().getLatitude(), doubleResult))
		{
			getEditorFrame().getTrackData().getLocalInfo().setLatitude(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(longitudeTextField.getText(),
			getEditorFrame().getTrackData().getLocalInfo().getLongitude(), doubleResult))
		{
			getEditorFrame().getTrackData().getLocalInfo().setLongitude(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(climatTextField.getText(),
			getEditorFrame().getTrackData().getLocalInfo().getClimat(), integerResult))
		{
			getEditorFrame().getTrackData().getLocalInfo().setClimat(integerResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(precipitationTextField.getText(),
			getEditorFrame().getTrackData().getLocalInfo().getPrecipitation(), doubleResult))
		{
			getEditorFrame().getTrackData().getLocalInfo().setPrecipitation(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (getEditorFrame().getGameType().hasBestLap())
		{
			if (isDifferent(bestLapTextField.getText(),
				getEditorFrame().getTrackData().getLocalInfo().getBestLap(), doubleResult))
			{
				getEditorFrame().getTrackData().getLocalInfo().setBestLap(doubleResult.getValue());
				getEditorFrame().documentIsModified = true;
			}
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
