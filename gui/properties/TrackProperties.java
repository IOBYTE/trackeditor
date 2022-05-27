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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utils.Editor;
/**
 * @author babis
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TrackProperties extends JPanel
{
	//private Properties properties = Properties.getInstance();
	private JLabel		widthLabel					= null;
	private JTextField	widthTextField				= null;
	private JLabel		surfaceLabel				= null;
	private JComboBox	surfaceComboBox				= null;
	private JLabel		profileStepsLengthLabel		= null;
	private JTextField	profileStepsLengthTextField	= null;

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

	/**
	 * 
	 */
	public TrackProperties()
	{
		super();
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
	}
	/**
	 * This method initializes widthTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getWidthTextField() {
		if (widthTextField == null) {
			widthTextField = new JTextField();
			widthTextField.setText(Double.toString(Editor.getProperties().getTrackWidth()));
			widthTextField.setBounds(120, 10, 50, 20);
			widthTextField.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
				}
			});
		}
		return widthTextField;
	}

	/**
	 * This method initializes surfaceComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getSurfaceComboBox()
	{
		if (surfaceComboBox == null)
		{
			surfaceComboBox = new JComboBox();
			surfaceComboBox.setModel(new DefaultComboBoxModel(roadSurfaceItems));
			surfaceComboBox.setBounds(120, 35, 120, 20);
			surfaceComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Editor.getProperties().setSurface((String) surfaceComboBox.getSelectedItem());
				}
			});

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
			profileStepsLengthTextField.setText(Double.toString(Editor.getProperties().getProfileStepLength()));
			profileStepsLengthTextField.setBounds(120, 60, 50, 20);
			widthTextField.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
				}
			});
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
			Editor.getProperties().setTrackWidth(Double.parseDouble(this.getWidthTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().setTrackWidth(Double.NaN);
		}

		Editor.getProperties().setSurface((String) surfaceComboBox.getSelectedItem());

		try
		{
			Editor.getProperties().setProfileStepLength(Double.parseDouble(this.getProfileStepsLengthTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().setProfileStepLength(Double.NaN);
		}

	}
 }  //  @jve:decl-index=0:visual-constraint="10,10"
