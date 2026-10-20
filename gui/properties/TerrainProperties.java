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

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import gui.EditorFrame;
import utils.Editor;
import utils.MutableDouble;
import utils.MutableInteger;
import utils.MutableString;
import utils.SurfaceComboBox;
import utils.ac3d.Ac3dException;
import utils.circuit.Reliefs;
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
	private JLabel				reliefBorderLabel			= new JLabel();
	private JComboBox<String>	reliefBorderComboBox		= null;
	private JLabel				surfaceLabel				= new JLabel();
	private SurfaceComboBox		surfaceComboBox				= null;
	private JLabel				randomSeedLabel				= new JLabel();
	private JTextField			randomSeedTextField			= new JTextField();
	private JLabel				useObjectMaterialsLabel		= new JLabel();
	private JComboBox<String>	useObjectMaterialsComboBox	= null;	
	private JLabel				tiledFileLabel				= new JLabel();
	private JTextField			tiledFileTextField			= new JTextField();	
	private JButton				defaultButton				= null;
	private JButton				deleteButton				= null;
	private JTabbedPane			tabbedPane					= null;
	private JButton				elevationMapButton			= null;
	private JButton				reliefFileButton			= null;
	private JButton				tiledFileButton			= null;

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
	public TerrainProperties(EditorFrame editorFrame)
	{
		super(editorFrame);
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize()
	{
		addDefaultSurfaces(roadSurfaceVector);

		setLayout(null);
		setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));

		addLabel(this, 0, trackStepLabel, "Track Step", 120);
		addLabel(this, 1, borderMarginLabel, "Border Margin", 120);
		addLabel(this, 2, borderStepLabel, "Border Step", 120);
		addLabel(this, 3, borderHeightLabel, "Border Height", 120);
		addLabel(this, 4, orientationLabel, "Orientation", 120);
		addLabel(this, 5, maximumAltitudeLabel, "Maximum Altitude", 120);
		addLabel(this, 6, minimumAltitudeLabel, "Minimum Altitude", 120);
		addLabel(this, 7, groupSizeLabel, "Group Size", 120);
		addLabel(this, 8, elevationMapLabel, "Elevation Map", 120);
		addLabel(this, 9, reliefFileLabel, "Relief File", 120);
		addLabel(this, 10, reliefBorderLabel, "Relief Border", 120);
		addLabel(this, 11, surfaceLabel, "Surface", 120);
		addLabel(this, 12, randomSeedLabel, "Random Seed", 120);
		addLabel(this, 13, useObjectMaterialsLabel, "Use Object Materials", 120);

		addTextField(this, 0, trackStepTextField, getEditorFrame().getTerrainGeneration().getTrackStep(), 140, 125);
		addTextField(this, 1, borderMarginTextField, getEditorFrame().getTerrainGeneration().getBorderMargin(), 140, 125);
		addTextField(this, 2, borderStepTextField, getEditorFrame().getTerrainGeneration().getBorderStep(), 140, 125);
		addTextField(this, 3, borderHeightTextField, getEditorFrame().getTerrainGeneration().getBorderHeight(), 140, 125);

		this.add(getOrientationComboBox(), null);

		addTextField(this, 5, maximumAltitudeTextField, getEditorFrame().getTerrainGeneration().getMaximumAltitude(), 140, 125);
		addTextField(this, 6, minimumAltitudeTextField, getEditorFrame().getTerrainGeneration().getMinimumAltitude(), 140, 125);
		addTextField(this, 7, groupSizeTextField, getEditorFrame().getTerrainGeneration().getGroupSize(), 140, 125);
		addTextField(this, 8, elevationMapTextField, getEditorFrame().getTerrainGeneration().getElevationMap(), 140, 295);
		addTextField(this, 9, reliefFileTextField, getEditorFrame().getTerrainGeneration().getReliefFile(), 140, 295);
		addTextField(this, 12, randomSeedTextField, getEditorFrame().getTerrainGeneration().getRandomSeed(), 140, 125);

		this.add(getSurfaceComboBox(), null);
		this.add(getDefaultButton(), null);
		this.add(getDeleteButton(), null);
		this.add(getTabbedPane(), null);
		this.add(getElevationMapButton(), null);
		this.add(getReliefFileButton(), null);
		this.add(getReliefBorderComboBox(), null);
		this.add(getUseObjectMaterialsComboBox(), null);
		// only Speed Dreams trackgen reads the tiled file
		if (!getEditorFrame().getCarsSportsRacing())
		{
			addLabel(this, 14, tiledFileLabel, "Tiled File", 120);
			addTextField(this, 14, tiledFileTextField, getEditorFrame().getTerrainGeneration().getTiledFile(), 140, 295);
			this.add(getTiledFileButton(), null);
		}
		getReliefBorderComboBox().setSelectedItem(toNoneString(getEditorFrame().getTerrainGeneration().getReliefBorder()));
		getUseObjectMaterialsComboBox().setSelectedItem(toNoneString(getEditorFrame().getTerrainGeneration().getUseObjectMaterials()));
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
			orientationComboBox.setBounds(140, 118, 125, 23);
			String orientation = getEditorFrame().getTerrainGeneration().getOrientation();
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
	private SurfaceComboBox getSurfaceComboBox()
	{
		if (surfaceComboBox == null)
		{
			String surface = getEditorFrame().getTerrainGeneration().getSurface();
			addSurface(roadSurfaceVector, surface);
			surfaceComboBox = new SurfaceComboBox(getEditorFrame(), roadSurfaceVector);
			surfaceComboBox.setBounds(140, 307, 200, 23);
			surfaceComboBox.setSelectedItem(surface);
		}
		return surfaceComboBox;
	}

	/**
	 * This method initializes reliefBorderComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getReliefBorderComboBox()
	{
		if (reliefBorderComboBox == null)
		{
			String[] items = {"none", "yes", "no"};
			reliefBorderComboBox = new JComboBox<String>(items);
			reliefBorderComboBox.setBounds(140, 280, 125, 23);
		}
		return reliefBorderComboBox;
	}

	/**
	 * This method initializes useObjectMaterialsComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getUseObjectMaterialsComboBox()
	{
		if (useObjectMaterialsComboBox == null)
		{
			String[] items = {"none", "yes", "no"};
			useObjectMaterialsComboBox = new JComboBox<String>(items);
			useObjectMaterialsComboBox.setBounds(140, 361, 125, 23);
		}
		return useObjectMaterialsComboBox;
	}

	private void addDefaultSurfaces(Vector<String> surfaceVector)
	{
		Vector<Surface> surfaces = getEditorFrame().getTrackData().getSurfaces();
		for (int i = 0; i < surfaces.size(); i++)
		{
			String surface = surfaces.elementAt(i).getName();
			if (surface != null)
			{
				boolean found = false;
				for (int j = 0; j < surfaceVector.size(); j++)
				{
					if (surfaceVector.elementAt(i).equals(surfaces.elementAt(i).getName()))
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					surfaceVector.add(surface);
				}
			}
		}
		Collections.sort(surfaceVector);
	}

	private void addSurface(Vector<String> surfaceVector, String surface)
	{
		// add this surface if it's not found in default list
		if (surface != null)
		{
			boolean found = false;
			for (int i = 0; i < surfaceVector.size(); i++)
			{
				if (surfaceVector.elementAt(i).equals(surface))
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				surfaceVector.add(surface);
				Collections.sort(surfaceVector);
			}
		}
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
			elevationMapButton.setBounds(440, 225, 80, 25);
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
			reliefFileButton.setBounds(440, 252, 80, 25);
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
		Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle("Elevation map image file selection");
		fc.setFileHidingEnabled(false);
		fc.setVisible(true);
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG images", "png");
		fc.addChoosableFileFilter(filter);
		fc.setCurrentDirectory(new File(Editor.getProperties().getPath()));
		int result = fc.showOpenDialog(this);
		UIManager.put("FileChooser.readOnly", old);
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
		Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle("Relief file selection");
		fc.setFileHidingEnabled(false);
		fc.setVisible(true);
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("AC", "ac");
		fc.addChoosableFileFilter(filter);
		fc.setCurrentDirectory(new File(Editor.getProperties().getPath()));
		int result = fc.showOpenDialog(this);
		UIManager.put("FileChooser.readOnly", old);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			String fileName = fc.getSelectedFile().toString();
			int index = fileName.lastIndexOf(sep);
			String pathToFile = fileName.substring(0, index);
			if (pathToFile.equals(Editor.getProperties().getPath()))
				fileName = fileName.substring(index + 1);
			reliefFileTextField.setText(fileName);

			Reliefs reliefs = new Reliefs();

			try
			{
				reliefs.setFileName(fileName);
				tabbedPane.remove(2);
				tabbedPane.addTab("Reliefs", null, new ReliefProperties(getEditorFrame(), reliefs));
				tabbedPane.setSelectedIndex(2);
			}
			catch (IOException e)
			{
				String msg = fileName + " : Can't read input file!";
				JOptionPane.showMessageDialog(getEditorFrame(), msg, "Relief File", JOptionPane.ERROR_MESSAGE);
			}
			catch (Ac3dException e)
			{
				String msg = fileName + " : " + e.getLocalizedMessage();
				JOptionPane.showMessageDialog(getEditorFrame(), msg, "Relief File", JOptionPane.ERROR_MESSAGE);
			}
			catch (Exception e)
			{
				String msg = fileName + " : " + e.getLocalizedMessage();
				JOptionPane.showMessageDialog(getEditorFrame(), msg, "Relief File", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * This method initializes tiledFileButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getTiledFileButton()
	{
		if (tiledFileButton == null)
		{
			tiledFileButton = new JButton();
			tiledFileButton.setBounds(440, 387, 80, 25);
			tiledFileButton.setText("Browse");
			tiledFileButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					tiledFile();
				}
			});
		}
		return tiledFileButton;
	}

	protected void tiledFile()
	{
		Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle("Tiled file selection");
		fc.setFileHidingEnabled(false);
		fc.setVisible(true);
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG images", "jpg");
		fc.addChoosableFileFilter(filter);
		fc.setCurrentDirectory(new File(Editor.getProperties().getPath()));
		int result = fc.showOpenDialog(this);
		UIManager.put("FileChooser.readOnly", old);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			String fileName = fc.getSelectedFile().toString();
			int index = fileName.lastIndexOf(sep);
			String pathToFile = fileName.substring(0, index);
			if (pathToFile.equals(Editor.getProperties().getPath()))
				fileName = fileName.substring(index + 1);
			tiledFileTextField.setText(fileName);
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
					setTextField(randomSeedTextField, TerrainGeneration.DEFAULT_RANDOM_SEED);
					reliefBorderComboBox.setSelectedItem(TerrainGeneration.DEFAULT_RELIEF_BORDER);
					useObjectMaterialsComboBox.setSelectedItem(TerrainGeneration.DEFAULT_USE_OBJECT_MATERIALS);
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
					randomSeedTextField.setText(null);
					reliefBorderComboBox.setSelectedItem(null);
					useObjectMaterialsComboBox.setSelectedItem(null);
				}
			});
		}
		return deleteButton;
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
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			tabbedPane.setBounds(10, 420, 510, 362);

			// only Speed Dreams trackgen places individual graphic objects
			if (!getEditorFrame().getCarsSportsRacing())
				tabbedPane.addTab("Objects", null, new GraphicObjectProperties(getEditorFrame()));
			tabbedPane.addTab("Object Maps", null, new ObjectMapProperties(getEditorFrame()));
			tabbedPane.addTab("Reliefs", null, new ReliefProperties(getEditorFrame()));
			SwingUtilities.invokeLater( new Runnable()
			{
				public void run()
				{
					int lastTab = getEditorFrame().getProject().getPropertiesEditorTerrainTab();
					if (lastTab == -1 && tabbedPane.getTabCount() > 0)
					{
						lastTab = 0;
					}
					if (lastTab < tabbedPane.getTabCount())
					{
						tabbedPane.setSelectedIndex(lastTab);
					}
				}
			});
		}
		return tabbedPane;
	}

	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();
		MutableInteger integerResult = new MutableInteger();

		if (isDifferent(trackStepTextField.getText(),
			getEditorFrame().getTerrainGeneration().getTrackStep(), doubleResult))
		{
			getEditorFrame().getTerrainGeneration().setTrackStep(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(borderMarginTextField.getText(),
			getEditorFrame().getTerrainGeneration().getBorderMargin(), doubleResult))
		{
			getEditorFrame().getTerrainGeneration().setBorderMargin(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(borderStepTextField.getText(),
			getEditorFrame().getTerrainGeneration().getBorderStep(), doubleResult))
		{
			getEditorFrame().getTerrainGeneration().setBorderStep(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(borderHeightTextField.getText(),
			getEditorFrame().getTerrainGeneration().getBorderHeight(), doubleResult))
		{
			getEditorFrame().getTerrainGeneration().setBorderHeight(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(maximumAltitudeTextField.getText(),
			getEditorFrame().getTerrainGeneration().getMaximumAltitude(), doubleResult))
		{
			getEditorFrame().getTerrainGeneration().setMaximumAltitude(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(minimumAltitudeTextField.getText(),
			getEditorFrame().getTerrainGeneration().getMinimumAltitude(), doubleResult))
		{
			getEditorFrame().getTerrainGeneration().setMinimumAltitude(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(groupSizeTextField.getText(),
			getEditorFrame().getTerrainGeneration().getGroupSize(), doubleResult))
		{
			getEditorFrame().getTerrainGeneration().setGroupSize(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent((String) getOrientationComboBox().getSelectedItem(),
			getEditorFrame().getTerrainGeneration().getOrientation(), stringResult))
		{
			getEditorFrame().getTerrainGeneration().setOrientation(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(elevationMapTextField.getText(),
			getEditorFrame().getTerrainGeneration().getElevationMap(), stringResult))
		{
			getEditorFrame().getTerrainGeneration().setElevationMap(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(reliefFileTextField.getText(),
			getEditorFrame().getTerrainGeneration().getReliefFile(), stringResult))
		{
			try
			{
				getEditorFrame().getTerrainGeneration().setReliefFile(stringResult.getValue());
			}
			catch (IOException e)
			{
				String msg = stringResult.getValue() + " : Can't read input file!";
				JOptionPane.showMessageDialog(getEditorFrame(), msg, "Relief File", JOptionPane.ERROR_MESSAGE);
			}
			catch (Ac3dException e)
			{
				String msg = stringResult.getValue() + " : " + e.getLocalizedMessage();
				JOptionPane.showMessageDialog(getEditorFrame(), msg, "Relief File", JOptionPane.ERROR_MESSAGE);
			}
			catch (Exception e)
			{
				String msg = stringResult.getValue() + " : " + e.getLocalizedMessage();
				JOptionPane.showMessageDialog(getEditorFrame(), msg, "Relief File", JOptionPane.ERROR_MESSAGE);
			}
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(reliefBorderComboBox.getSelectedItem().toString(),
			getEditorFrame().getTerrainGeneration().getReliefBorder(), stringResult))
		{
			getEditorFrame().getTerrainGeneration().setReliefBorder(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent((String) surfaceComboBox.getSelectedItem(),
			getEditorFrame().getTerrainGeneration().getSurface(), stringResult))
		{
			getEditorFrame().getTerrainGeneration().setSurface(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(randomSeedTextField.getText(),
			getEditorFrame().getTerrainGeneration().getRandomSeed(), integerResult))
		{
			getEditorFrame().getTerrainGeneration().setRandomSeed(integerResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(useObjectMaterialsComboBox.getSelectedItem().toString(),
			getEditorFrame().getTerrainGeneration().getUseObjectMaterials(), stringResult))
		{
			getEditorFrame().getTerrainGeneration().setUseObjectMaterials(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (!getEditorFrame().getCarsSportsRacing() && isDifferent(tiledFileTextField.getText(),
			getEditorFrame().getTerrainGeneration().getTiledFile(), stringResult))
		{
			getEditorFrame().getTerrainGeneration().setTiledFile(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		// the Objects tab only exists for Speed Dreams
		for (int i = 0; i < tabbedPane.getTabCount(); i++)
		{
			Component component = tabbedPane.getComponentAt(i);
			if (component instanceof GraphicObjectProperties)
				((GraphicObjectProperties)component).exit();
			else if (component instanceof ObjectMapProperties)
				((ObjectMapProperties)component).exit();
			else if (component instanceof ReliefProperties)
				((ReliefProperties)component).exit();
		}

		getEditorFrame().getProject().setPropertiesEditorTerrainTab(this.tabbedPane.getSelectedIndex());
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
