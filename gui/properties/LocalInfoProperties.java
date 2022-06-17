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
import utils.Editor;

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
	private JLabel		overallRainLikelyhoodLabel		= new JLabel();
	private JTextField	overallRainLikelyhoodTextField	= new JTextField();
	private JLabel		littleRainLikelyhoodLabel		= new JLabel();
	private JTextField	littleRainLikelyhoodTextField	= new JTextField();
	private JLabel		mediumRainLikelyhoodLabel		= new JLabel();
	private JTextField	mediumRainLikelyhoodTextField	= new JTextField();
	private JLabel		timeOfDayLabel					= new JLabel();
	private JTextField	timeOfDayTextField				= new JTextField();
	private JLabel		sunAscensionLabel				= new JLabel();
	private JTextField	sunAscensionTextField			= new JTextField();
	private JLabel		altitudeLabel					= new JLabel();
	private JTextField	altitudeTextField				= new JTextField();

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

		addLabel(this, 0, stationLabel, "Station", 130);
		addLabel(this, 1, timezoneLabel, "Timezone", 130);
		addLabel(this, 2, overallRainLikelyhoodLabel, "Overall Rain Likelyhood", 130);
		addLabel(this, 3, littleRainLikelyhoodLabel, "Little Rain Likelyhood", 130);
		addLabel(this, 4, mediumRainLikelyhoodLabel, "Medium Rain Likelyhood", 130);
		addLabel(this, 5, timeOfDayLabel, "Time Of Day", 130);
		addLabel(this, 6, sunAscensionLabel, "Sun Ascension", 130);
		addLabel(this, 7, altitudeLabel, "Altitude", 130);

		addTextField(this, 0, stationTextField, Editor.getProperties().getLocalInfo().getStation(), 150, 100);
		addTextField(this, 1, timezoneTextField, Editor.getProperties().getLocalInfo().getTimezone(), 150, 100);
		addTextField(this, 2, overallRainLikelyhoodTextField, Editor.getProperties().getLocalInfo().getOverallRainLikelyhood(), 150, 100);
		addTextField(this, 3, littleRainLikelyhoodTextField, Editor.getProperties().getLocalInfo().getLittleRainLikelyhood(), 150, 100);
		addTextField(this, 4, mediumRainLikelyhoodTextField, Editor.getProperties().getLocalInfo().getMediumRainLikelyhood(), 150, 100);
		addTextField(this, 5, timeOfDayTextField, Editor.getProperties().getLocalInfo().getTimeOfDay(), 150, 100);
		addTextField(this, 6, sunAscensionTextField, Editor.getProperties().getLocalInfo().getSunAscension(), 150, 100);
		addTextField(this, 7, altitudeTextField, Editor.getProperties().getLocalInfo().getAltitude(), 150, 100);
	}

	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();

		if (isDifferent(stationTextField.getText(),
			Editor.getProperties().getLocalInfo().getStation(), stringResult))
		{
			Editor.getProperties().getLocalInfo().setStation(stringResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(timezoneTextField.getText(),
			Editor.getProperties().getLocalInfo().getTimezone(), doubleResult))
		{
			Editor.getProperties().getLocalInfo().setTimezone(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(overallRainLikelyhoodTextField.getText(),
			Editor.getProperties().getLocalInfo().getOverallRainLikelyhood(), doubleResult))
		{
			Editor.getProperties().getLocalInfo().setOverallRainLikelyhood(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(littleRainLikelyhoodTextField.getText(),
			Editor.getProperties().getLocalInfo().getLittleRainLikelyhood(), doubleResult))
		{
			Editor.getProperties().getLocalInfo().setLittleRainLikelyhood(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(mediumRainLikelyhoodTextField.getText(),
			Editor.getProperties().getLocalInfo().getMediumRainLikelyhood(), doubleResult))
		{
			Editor.getProperties().getLocalInfo().setMediumRainLikelyhood(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(timeOfDayTextField.getText(),
			Editor.getProperties().getLocalInfo().getTimeOfDay(), doubleResult))
		{
			Editor.getProperties().getLocalInfo().setTimeOfDay(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(sunAscensionTextField.getText(),
			Editor.getProperties().getLocalInfo().getSunAscension(), doubleResult))
		{
			Editor.getProperties().getLocalInfo().setSunAscension(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(altitudeTextField.getText(),
			Editor.getProperties().getLocalInfo().getAltitude(), doubleResult))
		{
			Editor.getProperties().getLocalInfo().setAltitude(doubleResult.getValue());
			frame.documentIsModified = true;
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
