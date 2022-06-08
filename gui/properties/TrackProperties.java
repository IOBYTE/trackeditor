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
	private JLabel				widthLabel					= null;
	private JTextField			widthTextField				= null;
	private JLabel				surfaceLabel				= null;
	private JComboBox<String>	surfaceComboBox				= null;
	private JLabel				profileStepsLengthLabel		= null;
	private JTextField			profileStepsLengthTextField	= null;

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
	private Vector<String>	roadSurfaceVector				= new Vector<String>(Arrays.asList(roadSurfaceItems));

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
	private void initialize() {
        widthLabel = new JLabel();
        surfaceLabel = new JLabel();
        profileStepsLengthLabel = new JLabel();
        this.setLayout(null);
        this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
        this.setSize(356, 259);
        widthLabel.setText("Width");
        widthLabel.setBounds(10, 10, 110, 20);
        surfaceLabel.setText("Surface");
        surfaceLabel.setBounds(10, 35, 110, 20);
        profileStepsLengthLabel.setText("Profile Steps Length");
        profileStepsLengthLabel.setBounds(10, 60, 110, 20);

        this.add(widthLabel, null);
        this.add(getWidthTextField(), null);
        this.add(surfaceLabel, null);
        this.add(getSurfaceComboBox(), null);
        this.add(profileStepsLengthLabel, null);
        this.add(getProfileStepsLengthTextField(), null);

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
	 * This method initializes widthTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getWidthTextField() {
		if (widthTextField == null) {
			widthTextField = new JTextField();
			widthTextField.setBounds(120, 10, 50, 20);
			setTextField(widthTextField, Editor.getProperties().getMainTrack().getWidth());
		}
		return widthTextField;
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
	 * This method initializes profileStepsLengthTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getProfileStepsLengthTextField() {
		if (profileStepsLengthTextField == null) {
			profileStepsLengthTextField = new JTextField();
			profileStepsLengthTextField.setBounds(120, 60, 50, 20);
			setTextField(profileStepsLengthTextField, Editor.getProperties().getMainTrack().getProfileStepLength());
		}
		return profileStepsLengthTextField;
	}

	/**
	 *
	 */
	public void exit()
	{
		try
		{
			double value = Double.parseDouble(this.getWidthTextField().getText());
			if (value != Editor.getProperties().getMainTrack().getWidth())
			{
				Editor.getProperties().getMainTrack().setWidth(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getMainTrack().getWidth()))
			{
				Editor.getProperties().getMainTrack().setWidth(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		String result = null;
		if (isDifferent((String) surfaceComboBox.getSelectedItem(),
			Editor.getProperties().getMainTrack().getSurface(), result))
		{
			Editor.getProperties().getMainTrack().setSurface(result);
			frame.documentIsModified = true;
		}

		try
		{
			double value = Double.parseDouble(this.getProfileStepsLengthTextField().getText());
			if (value != Editor.getProperties().getMainTrack().getProfileStepLength())
			{
				Editor.getProperties().getMainTrack().setProfileStepLength(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getMainTrack().getProfileStepLength()))
			{
				Editor.getProperties().getMainTrack().setProfileStepLength(Double.NaN);
				frame.documentIsModified = true;
			}
		}
	}
 }  //  @jve:decl-index=0:visual-constraint="10,10"
