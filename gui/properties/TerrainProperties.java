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

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.EditorFrame;
import utils.Editor;
import utils.circuit.ObjectMap;
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
	private JLabel				trackStepLabel				= new JLabel();
	private JTextField			trackStepTextField			= new JTextField();
	private JLabel				borderMarginLabel			= new JLabel();
	private JTextField			borderMarginTextField		= new JTextField();
	private JLabel				borderStepLabel				= new JLabel();
	private JTextField			borderStepTextField			= new JTextField();
	private JLabel				borderHeightLabel			= new JLabel();
	private JTextField			borderHeightTextField		= new JTextField();
	private JLabel				orientationLabel			= new JLabel();
	private JComboBox<String>	orientationComboBox			= null;
	private JLabel				maximumAltitudeLabel		= new JLabel();
	private JTextField			maximumAltitudeTextField	= new JTextField();
	private JLabel				minimumAltitudeLabel		= new JLabel();
	private JTextField			minimumAltitudeTextField	= new JTextField();
	private JLabel				groupSizeLabel				= new JLabel();
	private JTextField			groupSizeTextField			= new JTextField();
	private JLabel				elevationMapLabel			= new JLabel();
	private JTextField			elevationMapTextField		= new JTextField();
	private JLabel				reliefFileLabel				= new JLabel();
	private JTextField			reliefFileTextField			= new JTextField();
	private JLabel				surfaceLabel				= new JLabel();
	private JComboBox<String>	surfaceComboBox				= null;
	private JButton				defaultButton				= null;
	private JButton				deleteButton				= null;
	private JButton				addObjectMapButton			= null;
	private JButton				deleteObjectMapButton		= null;
	private JTabbedPane			tabbedPane					= null;
	private JButton				elevationMapButton			= null;
	private JButton				reliefFileButton			= null;

	private final String sep = System.getProperty("file.separator");

	private String[]			roadSurfaceItems			=
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
		setLayout(null);
		setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));

		addLabel(this, 0, trackStepLabel, "Track Step", 100);
		addLabel(this, 1, borderMarginLabel, "Border Margin", 100);
		addLabel(this, 2, borderStepLabel, "Border Step", 100);
		addLabel(this, 3, borderHeightLabel, "Border Height", 100);
		addLabel(this, 4, orientationLabel, "Orientation", 100);
		addLabel(this, 5, maximumAltitudeLabel, "Maximum Altitude", 100);
		addLabel(this, 6, minimumAltitudeLabel, "Minimum Altitude", 100);
		addLabel(this, 7, groupSizeLabel, "Group Size", 100);
		addLabel(this, 8, elevationMapLabel, "Elevation Map", 100);
		addLabel(this, 9, reliefFileLabel, "Reliefe File", 100);
		addLabel(this, 10, surfaceLabel, "Surface", 100);

		addTextField(this, 0, trackStepTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getTrackStep(), 120, 100);
		addTextField(this, 1, borderMarginTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getBorderMargin(), 120, 100);
		addTextField(this, 2, borderStepTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getBorderStep(), 120, 100);
		addTextField(this, 3, borderHeightTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getBorderHeight(), 120, 100);

		this.add(getOrientationComboBox(), null);

		addTextField(this, 5, maximumAltitudeTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getMaximumAltitude(), 120, 100);
		addTextField(this, 6, minimumAltitudeTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getMinimumAltitude(), 120, 100);
		addTextField(this, 7, groupSizeTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getGroupSize(), 120, 100);
		addTextField(this, 8, elevationMapTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getElevationMap(), 120, 250);
		addTextField(this, 9, reliefFileTextField, Editor.getProperties().getGraphic().getTerrainGeneration().getReliefFile(), 120, 250);

		this.add(getSurfaceComboBox(), null);
		this.add(getDefaultButton(), null);
		this.add(getDeleteButton(), null);
		this.add(getTabbedPane(), null);
		this.add(getAddObjectMapButton(), null);
		this.add(getDeleteObjectMapButton(), null);
		this.add(getElevationMapButton(), null);
		this.add(getReliefFileButton(), null);

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
	 * This method initializes elevationMapButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getElevationMapButton()
	{
		if (elevationMapButton == null)
		{
			elevationMapButton = new JButton();
			elevationMapButton.setBounds(390, 207, 80, 25);
			elevationMapButton.setText("Browse");
			elevationMapButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					elevationMapFile();
				}
			});
		}
		return elevationMapButton;
	}

	/**
	 * This method initializes reliefFileButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getReliefFileButton()
	{
		if (reliefFileButton == null)
		{
			reliefFileButton = new JButton();
			reliefFileButton.setBounds(390, 232, 80, 25);
			reliefFileButton.setText("Browse");
			reliefFileButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					reliefFile();
				}
			});
		}
		return reliefFileButton;
	}

	protected void elevationMapFile()
	{
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle("Surface texture image file selection");
		fc.setVisible(true);
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("RGB and PNG images", "rgb", "png");
		fc.addChoosableFileFilter(filter);
		fc.setCurrentDirectory(new File(Editor.getProperties().getPath()));
		int result = fc.showDialog(this, "Ok");
		if (result == JFileChooser.APPROVE_OPTION)
		{
			String fileName = fc.getSelectedFile().toString();
			int index = fileName.lastIndexOf(sep);
			String pathToFile = fileName.substring(0, index);
			if (pathToFile.equals(Editor.getProperties().getPath()))
				fileName = fileName.substring(index + 1);
			elevationMapTextField.setText(fileName);
		}
	}

	protected void reliefFile()
	{
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle("Surface texture image file selection");
		fc.setVisible(true);
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("RGB and PNG images", "rgb", "png");
		fc.addChoosableFileFilter(filter);
		fc.setCurrentDirectory(new File(Editor.getProperties().getPath()));
		int result = fc.showDialog(this, "Ok");
		if (result == JFileChooser.APPROVE_OPTION)
		{
			String fileName = fc.getSelectedFile().toString();
			int index = fileName.lastIndexOf(sep);
			String pathToFile = fileName.substring(0, index);
			if (pathToFile.equals(Editor.getProperties().getPath()))
				fileName = fileName.substring(index + 1);
			reliefFileTextField.setText(fileName);
		}
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
	 * This method initializes addObjectMapButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getAddObjectMapButton()
	{
		if (addObjectMapButton == null)
		{
			addObjectMapButton = new JButton();
			addObjectMapButton.setBounds(10, 400, 130, 25);
			addObjectMapButton.setText("Add Object Map");
			addObjectMapButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					String name = "map " + (tabbedPane.getTabCount() + 1);

					tabbedPane.addTab(name, null, new ObjectMapPanel(name, ""), null);
					tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
				}
			});
		}
		return addObjectMapButton;
	}

	/**
	 * This method initializes deleteObjectMapButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteObjectMapButton()
	{
		if (deleteObjectMapButton == null)
		{
			deleteObjectMapButton = new JButton();
			deleteObjectMapButton.setBounds(155, 400, 140, 25);
			deleteObjectMapButton.setText("Delete Object Map");
			deleteObjectMapButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (tabbedPane.getTabCount() > 0)
					{
						tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
					}
				}
			});
		}
		return deleteObjectMapButton;
	}

	/**
	 * This method initializes tabbedPane
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getTabbedPane()
	{
		if (tabbedPane == null)
		{
			tabbedPane = new JTabbedPane();
			tabbedPane.setBounds(10, 285, 460, 100);

			Vector<ObjectMap> objectMaps = Editor.getProperties().getGraphic().getTerrainGeneration().getObjectMaps();

			for (int i = 0; i < objectMaps.size(); i++)
	        {
                utils.circuit.ObjectMap objectMap = objectMaps.elementAt(i);
				tabbedPane.addTab(objectMap.getName(), null, new ObjectMapPanel(objectMap.getName(), objectMap.getObjectMap()), null);
			}
		}
		return tabbedPane;
	}

	private class ObjectMapPanel extends JPanel
	{
		private JLabel		nameLabel			= null;
		private JTextField 	nameTextField		= null;
		private JLabel		objectMapLabel		= null;
		private JTextField	objectMapTextField	= null;
		private JButton		browseButton		= null;

		/**
		 *
		 */
		public ObjectMapPanel(String name, String objectMap)
		{
			super();
			initialize(name, objectMap);
		}

		/**
		 *
		 */
		private void initialize(String name, String objectMap)
		{
			nameLabel = new JLabel();
			objectMapLabel = new JLabel();
			nameLabel.setBounds(15, 15, 90, 20);
			nameLabel.setText("Name");
			objectMapLabel.setBounds(15, 40, 90, 20);
			objectMapLabel.setText("Object Map");
			add(nameLabel);
			add(objectMapLabel);
			setLayout(null);
			add(getNameTextField(), null);
			add(getObjectMapTextField(), null);
			add(getBrowseButton(), null);

			getNameTextField().setText(name);
			getObjectMapTextField().setText(objectMap);
		}

		/**
		 * This method initializes nameTextField
		 *
		 * @return javax.swing.JTextField
		 */
		public JTextField getNameTextField()
		{
			if (nameTextField == null)
			{
				nameTextField = new JTextField();
				nameTextField.setBounds(105, 15, 60, 20);
			}
			return nameTextField;
		}

		/**
		 * This method initializes objectMapTextField
		 *
		 * @return javax.swing.JTextField
		 */
		public JTextField getObjectMapTextField()
		{
			if (objectMapTextField == null)
			{
				objectMapTextField = new JTextField();
				objectMapTextField.setBounds(105, 40, 260, 20);
			}
			return objectMapTextField;
		}
		/**
		 * This method initializes browseButton
		 *
		 * @return javax.swing.JButton
		 */
		private JButton getBrowseButton()
		{
			if (browseButton == null)
			{
				browseButton = new JButton();
				browseButton.setBounds(370, 37, 80, 25);
				browseButton.setText("Browse");
				browseButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						selectFile();
					}
				});
			}
			return browseButton;
		}

		protected void selectFile()
		{
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFiles(null);
			fc.setSelectedFile(null);
			fc.rescanCurrentDirectory();
			fc.setApproveButtonMnemonic(0);
			fc.setDialogTitle("Object Map image file selection");
			fc.setVisible(true);
			fc.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG images", "png");
			fc.addChoosableFileFilter(filter);
			fc.setCurrentDirectory(new File(Editor.getProperties().getPath()));
			int result = fc.showDialog(this, "Ok");
			if (result == JFileChooser.APPROVE_OPTION)
			{
				String fileName = fc.getSelectedFile().toString();
				int index = fileName.lastIndexOf(sep);
				String pathToFile = fileName.substring(0, index);
				if (pathToFile.equals(Editor.getProperties().getPath()))
					fileName = fileName.substring(index + 1);
				getObjectMapTextField().setText(fileName);
			}
		}
	}

	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();

		if (isDifferent(trackStepTextField.getText(),
			Editor.getProperties().getGraphic().getTerrainGeneration().getTrackStep(), doubleResult))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setTrackStep(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(borderMarginTextField.getText(),
			Editor.getProperties().getGraphic().getTerrainGeneration().getBorderMargin(), doubleResult))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setBorderMargin(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(borderStepTextField.getText(),
			Editor.getProperties().getGraphic().getTerrainGeneration().getBorderStep(), doubleResult))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setBorderStep(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(borderHeightTextField.getText(),
			Editor.getProperties().getGraphic().getTerrainGeneration().getBorderHeight(), doubleResult))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setBorderHeight(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(maximumAltitudeTextField.getText(),
			Editor.getProperties().getGraphic().getTerrainGeneration().getMaximumAltitude(), doubleResult))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setMaximumAltitude(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(minimumAltitudeTextField.getText(),
			Editor.getProperties().getGraphic().getTerrainGeneration().getMinimumAltitude(), doubleResult))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setMinimumAltitude(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(groupSizeTextField.getText(),
			Editor.getProperties().getGraphic().getTerrainGeneration().getGroupSize(), doubleResult))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setGroupSize(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent((String) getOrientationComboBox().getSelectedItem(),
			Editor.getProperties().getGraphic().getTerrainGeneration().getOrientation(), stringResult))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setOrientation(stringResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(elevationMapTextField.getText(),
			Editor.getProperties().getGraphic().getTerrainGeneration().getElevationMap(), stringResult))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setElevationMap(stringResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(reliefFileTextField.getText(),
			Editor.getProperties().getGraphic().getTerrainGeneration().getReliefFile(), stringResult))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setReliefFile(stringResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent((String) surfaceComboBox.getSelectedItem(),
			Editor.getProperties().getGraphic().getTerrainGeneration().getSurface(), stringResult))
		{
			Editor.getProperties().getGraphic().getTerrainGeneration().setSurface(stringResult.getValue());
			frame.documentIsModified = true;
		}

		Vector<ObjectMap> objectMaps = Editor.getProperties().getGraphic().getTerrainGeneration().getObjectMaps();
		int minCount = Math.min(objectMaps.size(), tabbedPane.getTabCount());
		if (objectMaps.size() != tabbedPane.getTabCount())
		{
			frame.documentIsModified = true;
		}
		for (int i = 0; i < minCount; i++)
        {
            utils.circuit.ObjectMap objectMap = objectMaps.elementAt(i);
            ObjectMapPanel panel = (ObjectMapPanel) tabbedPane.getComponentAt(i);
            if (isDifferent(panel.getNameTextField().getText(), objectMap.getName(), stringResult))
            {
                objectMap.setName(stringResult.getValue());
                frame.documentIsModified = true;
            }
            if (isDifferent(panel.getObjectMapTextField().getText(), objectMap.getObjectMap(), stringResult))
            {
                objectMap.setObjectMap(stringResult.getValue());
                frame.documentIsModified = true;
            }
		}
		if (objectMaps.size() > tabbedPane.getTabCount())
		{
			// need to trim objectMaps
			while (objectMaps.size() > tabbedPane.getTabCount())
			{
				objectMaps.remove(objectMaps.size() - 1);
			}
		}
		else if (objectMaps.size() < tabbedPane.getTabCount())
		{
			// need to add to objectMaps
			while (objectMaps.size() < tabbedPane.getTabCount())
			{
	            ObjectMapPanel panel = (ObjectMapPanel) tabbedPane.getComponentAt(objectMaps.size());
				ObjectMap objectMap = new ObjectMap();
				objectMap.setName(panel.getNameTextField().getText());
				objectMap.setObjectMap(panel.getObjectMapTextField().getText());
				objectMaps.add(objectMap);
			}
		}
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
