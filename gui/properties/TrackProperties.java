/*
 *   TrackProperties.java
 *   Created on 27 ��� 2005
 *
 *    The TrackProperties.java is part of TrackEditor-0.3.1.
 *
 *    TrackEditor-0.3.1 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.3.1 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.3.1; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package gui.properties;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.Editor;
import utils.circuit.Surface;
/**
 * @author babis
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TrackProperties extends PropertyPanel
{
	private JLabel				widthLabel					= new JLabel();
	private JTextField			widthTextField				= new JTextField();
	private JLabel				surfaceLabel				= new JLabel();
	private JComboBox<String>	surfaceComboBox				= null;
	private JLabel				profilStepsLengthLabel		= new JLabel();
	private JTextField			profilStepsLengthTextField	= new JTextField();
	private JLabel				racelineWidthscaleLabel		= new JLabel();
	private JTextField			racelineWidthscaleTextField	= new JTextField();
	private JLabel				racelineIntLabel			= new JLabel();
	private JTextField			racelineIntTextField		= new JTextField();
	private JLabel				racelineExtLabel			= new JLabel();
	private JTextField			racelineExtTextField		= new JTextField();

	private String[]			roadSurfaceItems		=
	{"asphalt-lines", "asphalt-l-left", "asphalt-l-right",
     "asphalt-l-both", "asphalt-pits", "asphalt", "dirt", "dirt-b", "asphalt2", "road1", "road1-pits",
     "road1-asphalt", "asphalt-road1", "b-road1", "b-road1-l2", "b-road1-l2p", "concrete", "concrete2",
     "concrete3", "b-asphalt", "b-asphalt-l1", "b-asphalt-l1p", "asphalt2-lines", "asphalt2-l-left",
     "asphalt2-l-right", "asphalt2-l-both", "grass", "grass3", "grass5", "grass6", "grass7", "gravel", "sand3",
     "sand", "curb-5cm-r", "curb-5cm-l", "curb-l", "tar-grass3-l", "tar-grass3-r", "tar-sand", "b-road1-grass6",
     "b-road1-grass6-l2", "b-road1-gravel-l2", "b-road1-sand3", "b-road1-sand3-l2", "b-asphalt-grass7",
     "b-asphalt-grass7-l1", "b-asphalt-grass6", "b-asphalt-grass6-l1", "b-asphalt-sand3", "b-asphalt-sand3-l1",
     "barrier", "barrier2", "barrier-turn", "barrier-grille", "wall", "wall2", "tire-wall"};
	private Vector<String>		roadSurfaceVector			= new Vector<String>(Arrays.asList(roadSurfaceItems));

	/**
	 *
	 */
	public TrackProperties(EditorFrame frame)
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
        this.setLayout(null);
        this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));

		addLabel(this, 0, widthLabel, "Width", 130);
		addLabel(this, 1, surfaceLabel, "Surface", 130);
		addLabel(this, 2, profilStepsLengthLabel, "Profil Steps Length", 130);
		addLabel(this, 3, racelineWidthscaleLabel, "Raceline Width Scale", 130);
		addLabel(this, 4, racelineIntLabel, "Raceline Int", 130);
		addLabel(this, 5, racelineExtLabel, "Racelin Ext", 130);

		addTextField(this, 0, widthTextField, Editor.getProperties().getMainTrack().getWidth(), 120, 50);

        this.add(getSurfaceComboBox(), null);

		addTextField(this, 2, profilStepsLengthTextField, Editor.getProperties().getMainTrack().getProfilStepsLength(), 120, 50);
		addTextField(this, 3, racelineWidthscaleTextField, Editor.getProperties().getMainTrack().getRacelineWidthscale(), 120, 50);
		addTextField(this, 4, racelineIntTextField, Editor.getProperties().getMainTrack().getRacelineInt(), 120, 50);
		addTextField(this, 5, racelineExtTextField, Editor.getProperties().getMainTrack().getRacelineExt(), 120, 50);

        Vector<Surface> surfaces = Editor.getProperties().getSurfaces();
        for (int i = 0; i < surfaces.size(); i++)
        {
			String surface = surfaces.elementAt(i).getName();
			boolean found = false;
			for (int j = 0; j < roadSurfaceVector.size(); j++)
			{
				if (roadSurfaceVector.elementAt(i).equals(surfaces.elementAt(i).getName()))
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				roadSurfaceVector.add(surface);
			}
        }
		Collections.sort(roadSurfaceVector);
	}

	/**
	 * This method initializes surfaceComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getSurfaceComboBox()
	{
		if (surfaceComboBox == null)
		{
			surfaceComboBox = new JComboBox<String>();
			surfaceComboBox.setBounds(120, 35, 140, 20);
			String surface = Editor.getProperties().getMainTrack().getSurface();
			if (surface != null)
			{
				boolean found = false;
				for (int i = 0; i < roadSurfaceVector.size(); i++)
				{
					if (roadSurfaceVector.elementAt(i).equals(surface))
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					roadSurfaceVector.add(surface);
				}
			}
			surfaceComboBox.setModel(new DefaultComboBoxModel<String>(roadSurfaceVector));
			surfaceComboBox.setSelectedItem(surface);
		}
		return surfaceComboBox;
	}

	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();

        if (isDifferent(widthTextField.getText(),
            Editor.getProperties().getMainTrack().getWidth(), doubleResult))
        {
            Editor.getProperties().getMainTrack().setWidth(doubleResult.getValue());
            frame.documentIsModified = true;
        }

		if (isDifferent((String) surfaceComboBox.getSelectedItem(),
            Editor.getProperties().getMainTrack().getSurface(), stringResult))
		{
			Editor.getProperties().getMainTrack().setSurface(stringResult.getValue());
			frame.documentIsModified = true;
		}

        if (isDifferent(profilStepsLengthTextField.getText(),
            Editor.getProperties().getMainTrack().getProfilStepsLength(), doubleResult))
        {
            Editor.getProperties().getMainTrack().setProfilStepsLength(doubleResult.getValue());
            frame.documentIsModified = true;
        }

        if (isDifferent(racelineWidthscaleTextField.getText(),
            Editor.getProperties().getMainTrack().getRacelineWidthscale(), doubleResult))
        {
            Editor.getProperties().getMainTrack().setRacelineWidthscale(doubleResult.getValue());
            frame.documentIsModified = true;
        }

        if (isDifferent(racelineIntTextField.getText(),
            Editor.getProperties().getMainTrack().getRacelineInt(), doubleResult))
        {
            Editor.getProperties().getMainTrack().setRacelineInt(doubleResult.getValue());
            frame.documentIsModified = true;
        }

        if (isDifferent(racelineExtTextField.getText(),
            Editor.getProperties().getMainTrack().getRacelineExt(), doubleResult))
        {
            Editor.getProperties().getMainTrack().setRacelineExt(doubleResult.getValue());
            frame.documentIsModified = true;
        }
	}
 }  //  @jve:decl-index=0:visual-constraint="10,10"
