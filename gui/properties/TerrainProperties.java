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

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.Editor;
import utils.circuit.Surface;
import utils.circuit.TerrainGeneration;

/**
 * @author Robert Reif
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TerrainProperties extends PropertyPanel
{
	private JLabel				trackStepLabel				= null;
	private JTextField			trackStepTextField			= null;
	private JLabel				borderMarginLabel			= null;
	private JTextField			borderMarginTextField		= null;
	private JLabel				borderStepLabel				= null;
	private JTextField			borderStepTextField			= null;
	private JLabel				borderHeightLabel			= null;
	private JTextField			borderHeightTextField		= null;
	private JLabel				orientationLabel			= null;
	private JComboBox<String>	orientationComboBox			= null;
	private JLabel				maximumAltitudeLabel		= null;
	private JTextField			maximumAltitudeTextField	= null;
	private JLabel				minimumAltitudeLabel		= null;
	private JTextField			minimumAltitudeTextField	= null;
	private JLabel				groupSizeLabel				= null;
	private JTextField			groupSizeTextField			= null;
	private JLabel				elevationMapLabel			= null;
	private JTextField			elevationMapTextField		= null;		// TODO add button to get file
	private JLabel				reliefFileLabel				= null;
	private JTextField			reliefFileTextField			= null;		// TODO add button to get file
	private JLabel				surfaceLabel				= null;
	private JComboBox<String>	surfaceComboBox				= null;
	private JButton				defaultButton				= null;
	private JButton				deleteButton				= null;
	
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
	public TerrainProperties(EditorFrame frame)
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
		this.add(getSurfaceComboBox(), null);
		this.add(getDefaultButton(), null);
		this.add(getDeleteButton(), null);

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
	 * This method initializes trackStepTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTrackStepTextField()
	{
		if (trackStepTextField == null)
		{
			trackStepTextField = new JTextField();
			trackStepTextField.setBounds(120, 10, 100, 20);
			setTextField(trackStepTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getTrackStep());
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
			borderMarginTextField.setBounds(120, 35, 100, 20);
			setTextField(borderMarginTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getBorderMargin());
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
			borderStepTextField.setBounds(120, 60, 100, 20);
			setTextField(borderStepTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getBorderStep());
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
			borderHeightTextField.setBounds(120, 85, 100, 20);
			setTextField(borderHeightTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getBorderHeight());
		}
		return borderHeightTextField;
	}

	/**
	 * This method initializes orientationComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getOrientationComboBox()
	{
		if (orientationComboBox == null)
		{
			String[] items =
			{"none", "clockwise", "counter-clockwise"};
			orientationComboBox = new JComboBox<String>(items);
			orientationComboBox.setBounds(120, 110, 120, 20);
			String orientation = Editor.getProperties().getGraphic().getTerrainGeneration().getOrientation();
			if (orientation == null || orientation.isEmpty())
				orientation = "none";
			orientationComboBox.setSelectedItem(orientation);
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
			maximumAltitudeTextField.setBounds(120, 135, 100, 20);
			setTextField(maximumAltitudeTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getMaximumAltitude());
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
			minimumAltitudeTextField.setBounds(120, 160, 100, 20);
			setTextField(minimumAltitudeTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getMinimumAltitude());
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
			groupSizeTextField.setBounds(120, 185, 100, 20);
			setTextField(groupSizeTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getGroupSize());
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
			elevationMapTextField.setBounds(120, 210, 100, 20);
			setTextField(elevationMapTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getElevationMap());
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
			reliefFileTextField.setBounds(120, 235, 100, 20);
			setTextField(reliefFileTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getReliefFile());
		}
		return reliefFileTextField;
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
			surfaceComboBox.setBounds(120, 260, 140, 20);
			String surface = Editor.getProperties().getGraphic().getTerrainGeneration().getSurface();
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
	 * This method initializes defaultButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDefaultButton()
	{
		if (defaultButton == null)
		{
			defaultButton = new JButton();
			defaultButton.setBounds(300, 15, 80, 25);
			defaultButton.setText("Default");
			defaultButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					setTextField(trackStepTextField, TerrainGeneration.DEFAULT_TRACK_STEP);
					setTextField(borderMarginTextField, TerrainGeneration.DEFAULT_BORDER_MARGIN);
					setTextField(borderStepTextField, TerrainGeneration.DEFAULT_BORDER_STEP);
					setTextField(borderHeightTextField, TerrainGeneration.DEFAULT_BORDER_HEIGHT);
					orientationComboBox.setSelectedItem(TerrainGeneration.DEFAULT_ORIENTATION);
					setTextField(maximumAltitudeTextField, TerrainGeneration.DEFAULT_MAXIMUM_ALTITUDE);
					setTextField(minimumAltitudeTextField, TerrainGeneration.DEFAULT_MINIMUM_ALTITUDE);
					setTextField(groupSizeTextField, TerrainGeneration.DEFAULT_GROUP_SIZE);
					setTextField(elevationMapTextField, TerrainGeneration.DEFAULT_ELEVATION_MAP);
					setTextField(reliefFileTextField, TerrainGeneration.DEFAULT_RELIEF_FILE);
					surfaceComboBox.setSelectedItem(TerrainGeneration.DEFAULT_SURFACE);
				}
			});
		}
		return defaultButton;
	}

	/**
	 * This method initializes deleteButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteButton()
	{
		if (deleteButton == null)
		{
			deleteButton = new JButton();
			deleteButton.setBounds(300, 50, 80, 25);
			deleteButton.setText("Delete");
			deleteButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					trackStepTextField.setText(null);
					borderMarginTextField.setText(null);
					borderStepTextField.setText(null);
					borderHeightTextField.setText(null);
					orientationComboBox.setSelectedItem(null);
					maximumAltitudeTextField.setText(null);
					minimumAltitudeTextField.setText(null);
					groupSizeTextField.setText(null);
					elevationMapTextField.setText(null);
					reliefFileTextField.setText(null);
					surfaceComboBox.setSelectedItem(null);
				}
			});
		}
		return deleteButton;
	}

	/**
	 *  
	 */
	public void exit()
	{
		try
		{
			double value = Double.parseDouble(getTrackStepTextField().getText());
			if (value != Editor.getProperties().getGraphic().getTerrainGeneration().getTrackStep())
			{
				Editor.getProperties().getGraphic().getTerrainGeneration().setTrackStep(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getTerrainGeneration().getTrackStep()))
			{
				Editor.getProperties().getGraphic().getTerrainGeneration().setTrackStep(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getBorderMarginTextField().getText());
			if (value != Editor.getProperties().getGraphic().getTerrainGeneration().getBorderMargin())
			{
				Editor.getProperties().getGraphic().getTerrainGeneration().setBorderMargin(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getTerrainGeneration().getBorderMargin()))
			{
				Editor.getProperties().getGraphic().getTerrainGeneration().setBorderMargin(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getBorderStepTextField().getText());
			if (value != Editor.getProperties().getGraphic().getTerrainGeneration().getBorderStep())
			{
				Editor.getProperties().getGraphic().getTerrainGeneration().setBorderStep(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getTerrainGeneration().getBorderStep()))
			{
				Editor.getProperties().getGraphic().getTerrainGeneration().setBorderStep(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getBorderHeightTextField().getText());
			if (value != Editor.getProperties().getGraphic().getTerrainGeneration().getBorderHeight())
			{
				Editor.getProperties().getGraphic().getTerrainGeneration().setBorderHeight(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getTerrainGeneration().getBorderHeight()))
			{
				Editor.getProperties().getGraphic().getTerrainGeneration().setBorderHeight(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getMaximumAltitudeTextField().getText());
			if (value != Editor.getProperties().getGraphic().getTerrainGeneration().getMaximumAltitude())
			{
				Editor.getProperties().getGraphic().getTerrainGeneration().setMaximumAltitude(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getTerrainGeneration().getMaximumAltitude()))
			{
				Editor.getProperties().getGraphic().getTerrainGeneration().setMaximumAltitude(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getMinimumAltitudeTextField().getText());
			if (value != Editor.getProperties().getGraphic().getTerrainGeneration().getMinimumAltitude())
			{
				Editor.getProperties().getGraphic().getTerrainGeneration().setMinimumAltitude(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getTerrainGeneration().getMinimumAltitude()))
			{
				Editor.getProperties().getGraphic().getTerrainGeneration().setMinimumAltitude(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		try
		{
			double value = Double.parseDouble(getGroupSizeTextField().getText());
			if (value != Editor.getProperties().getGraphic().getTerrainGeneration().getGroupSize())
			{
				Editor.getProperties().getGraphic().getTerrainGeneration().setGroupSize(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getGraphic().getTerrainGeneration().getGroupSize()))
			{
				Editor.getProperties().getGraphic().getTerrainGeneration().setGroupSize(Double.NaN);
				frame.documentIsModified = true;
			}
		}

		String orientation = (String) getOrientationComboBox().getSelectedItem();
		if (orientation == "none")
			orientation = null;
		if (!orientation.equals(Editor.getProperties().getGraphic().getTerrainGeneration().getOrientation()))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setOrientation(orientation);
			frame.documentIsModified = true;
		}

		String elevationMap = getElevationMapTextField().getText();
		if (!elevationMap.equals(Editor.getProperties().getGraphic().getTerrainGeneration().getElevationMap()))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setElevationMap(elevationMap);
			frame.documentIsModified = true;
		}

		String reliefFile = getReliefFileTextField().getText();
		if (!reliefFile.equals(Editor.getProperties().getGraphic().getTerrainGeneration().getReliefFile()))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setReliefFile(reliefFile);
			frame.documentIsModified = true;
		}

		String surface = (String) surfaceComboBox.getSelectedItem();
		if (!surface.equals(Editor.getProperties().getGraphic().getTerrainGeneration().getSurface()))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setSurface(surface);
			frame.documentIsModified = true;
		}

		Editor.getProperties().valueChanged();
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
