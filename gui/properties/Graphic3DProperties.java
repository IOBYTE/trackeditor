package gui.properties;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.EditorFrame;
import utils.Editor;
import utils.MutableDouble;
import utils.MutableString;

public class Graphic3DProperties extends PropertyPanel
{
	private JLabel				descriptionLabel				= new JLabel();
	private JTextField			descriptionTextField			= new JTextField();
	private JLabel				descriptionNightLabel			= new JLabel();
	private JTextField			descriptionNightTextField		= new JTextField();
	private JLabel				descriptionRainNightLabel		= new JLabel();
	private JTextField			descriptionRainNightTextField	= new JTextField();

	private JLabel				separateObjectsLabel			= new JLabel();
	private JTextField			separateObjectsTextField		= new JTextField();
	private JLabel				terrainObjectsLabel				= new JLabel();
	private JTextField			terrainObjectsTextField			= new JTextField();
	private JButton				terrainObjectsButton			= null;
	private JLabel				roadObjectsLabel				= new JLabel();
	private JTextField			roadObjectsTextField			= new JTextField();
	private JButton				roadObjectsButton				= null;
	private JLabel				road2ObjectsLabel				= new JLabel();
	private JTextField			road2ObjectsTextField			= new JTextField();
	private JButton				road2ObjectsButton				= null;
	private JLabel				treesObjectsLabel				= new JLabel();
	private JTextField			treesObjectsTextField			= new JTextField();
	private JButton				treesObjectsButton				= null;
	private JLabel				buildingObjectsLabel			= new JLabel();
	private JTextField			buildingObjectsTextField		= new JTextField();
	private JButton				buildingObjectsButton			= null;
	private JLabel				houseObjectsLabel				= new JLabel();
	private JTextField			houseObjectsTextField			= new JTextField();
	private JButton				houseObjectsButton				= null;
	private JLabel				carsObjectsLabel				= new JLabel();
	private JTextField			carsObjectsTextField			= new JTextField();
	private JButton				carsObjectsButton				= null;
	private JLabel				truckObjectsLabel				= new JLabel();
	private JTextField			truckObjectsTextField			= new JTextField();
	private JButton				truckObjectsButton				= null;
	private JLabel				tribunesObjectsLabel			= new JLabel();
	private JTextField			tribunesObjectsTextField		= new JTextField();
	private JButton				tribunesObjectsButton			= null;
	private JLabel				pubObjectsLabel					= new JLabel();
	private JTextField			pubObjectsTextField				= new JTextField();
	private JButton				pubObjectsButton				= null;
	private JLabel				decorsObjectsLabel				= new JLabel();
	private JTextField			decorsObjectsTextField			= new JTextField();
	private JButton				decorsObjectsButton				= null;
	private JLabel				waterObjectsLabel				= new JLabel();
	private JTextField			waterObjectsTextField			= new JTextField();
	private JButton				waterObjectsButton				= null;
	private JLabel				grassObjectsLabel				= new JLabel();
	private JTextField			grassObjectsTextField			= new JTextField();
	private JButton				grassObjectsButton				= null;
	private JLabel				sandObjectsLabel				= new JLabel();
	private JTextField			sandObjectsTextField			= new JTextField();
	private JButton				sandObjectsButton				= null;
	private JLabel				rockObjectsLabel				= new JLabel();
	private JTextField			rockObjectsTextField			= new JTextField();
	private JButton				rockObjectsButton				= null;
	private JLabel				curbObjectsLabel				= new JLabel();
	private JTextField			curbObjectsTextField			= new JTextField();
	private JButton				curbObjectsButton				= null;
	private JLabel				fenceObjectsLabel				= new JLabel();
	private JTextField			fenceObjectsTextField			= new JTextField();
	private JButton				fenceObjectsButton				= null;
	private JLabel				barrierObjectsLabel				= new JLabel();
	private JTextField			barrierObjectsTextField			= new JTextField();
	private JButton				barrierObjectsButton			= null;
	private JLabel				wallObjectsLabel				= new JLabel();
	private JTextField			wallObjectsTextField			= new JTextField();
	private JButton				wallObjectsButton				= null;
	private JLabel				environmentObjectsLabel			= new JLabel();
	private JTextField			environmentObjectsTextField		= new JTextField();
	private JButton				environmentObjectsButton		= null;
	
	private final String sep = System.getProperty("file.separator");

	/**
	 *
	 */
	public Graphic3DProperties(EditorFrame editorFrame)
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
		setLayout(null);
		setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));

		addLabel(this, 0, descriptionLabel, "Description", 150);
		addLabel(this, 1, descriptionNightLabel, "Description Night", 150);
		addLabel(this, 2, descriptionRainNightLabel, "Description Rain Night", 150);

		if (getEditorFrame().getCarsSportsRacing())
		{
			addLabel(this, 3, separateObjectsLabel, "Seperate Objects", 150);
			addLabel(this, 4, terrainObjectsLabel, "Terrain", 150);
			addLabel(this, 5, roadObjectsLabel, "Road", 150);
			addLabel(this, 6, road2ObjectsLabel, "Road2", 150);
			addLabel(this, 7, treesObjectsLabel, "Trees", 150);
			addLabel(this, 8, buildingObjectsLabel, "Building", 150);
			addLabel(this, 9, houseObjectsLabel, "House", 150);
			addLabel(this, 10, carsObjectsLabel, "Cars", 150);
			addLabel(this, 11, truckObjectsLabel, "Truck", 150);
			addLabel(this, 12, tribunesObjectsLabel, "Tribunes", 150);
			addLabel(this, 13, pubObjectsLabel, "Pub", 150);
			addLabel(this, 14, decorsObjectsLabel, "Decors", 150);
			addLabel(this, 15, waterObjectsLabel, "Water", 150);
			addLabel(this, 16, grassObjectsLabel, "Grass", 150);
			addLabel(this, 17, sandObjectsLabel, "Sand", 150);
			addLabel(this, 18, rockObjectsLabel, "Rock", 150);
			addLabel(this, 19, curbObjectsLabel, "Curb", 150);
			addLabel(this, 20, fenceObjectsLabel, "Fence", 150);
			addLabel(this, 21, barrierObjectsLabel, "Barrier", 150);
			addLabel(this, 22, wallObjectsLabel, "Wall", 150);
			addLabel(this, 23, environmentObjectsLabel, "Environment", 150);
		}

		addTextField(this, 0, descriptionTextField, getEditorFrame().getTrackData().getGraphic().getDescription(), 160, 200);
		addTextField(this, 1, descriptionNightTextField, getEditorFrame().getTrackData().getGraphic().getDescriptionNight(), 160, 200);
		addTextField(this, 2, descriptionRainNightTextField, getEditorFrame().getTrackData().getGraphic().getDescriptionRainNight(), 160, 200);

		if (getEditorFrame().getCarsSportsRacing())
		{
			addTextField(this, 3, separateObjectsTextField, getEditorFrame().getTrackData().getGraphic().getSeparateObjects(), 160, 200);
			addTextField(this, 4, terrainObjectsTextField, getEditorFrame().getTrackData().getGraphic().getTerrainObjects(), 160, 275);
			addTextField(this, 5, roadObjectsTextField, getEditorFrame().getTrackData().getGraphic().getRoadObjects(), 160, 275);
			addTextField(this, 6, road2ObjectsTextField, getEditorFrame().getTrackData().getGraphic().getRoad2Objects(), 160, 275);
			addTextField(this, 7, treesObjectsTextField, getEditorFrame().getTrackData().getGraphic().getTreesObjects(), 160, 275);
			addTextField(this, 8, buildingObjectsTextField, getEditorFrame().getTrackData().getGraphic().getBuildingObjects(), 160, 275);
			addTextField(this, 9, houseObjectsTextField, getEditorFrame().getTrackData().getGraphic().getHouseObjects(), 160, 275);
			addTextField(this, 10, carsObjectsTextField, getEditorFrame().getTrackData().getGraphic().getCarsObjects(), 160, 275);
			addTextField(this, 11, truckObjectsTextField, getEditorFrame().getTrackData().getGraphic().getTruckObjects(), 160, 275);
			addTextField(this, 12, tribunesObjectsTextField, getEditorFrame().getTrackData().getGraphic().getTribunesObjects(), 160, 275);
			addTextField(this, 13, pubObjectsTextField, getEditorFrame().getTrackData().getGraphic().getPubObjects(), 160, 275);
			addTextField(this, 14, decorsObjectsTextField, getEditorFrame().getTrackData().getGraphic().getDecorsObjects(), 160, 275);
			addTextField(this, 15, waterObjectsTextField, getEditorFrame().getTrackData().getGraphic().getWaterObjects(), 160, 275);
			addTextField(this, 16, grassObjectsTextField, getEditorFrame().getTrackData().getGraphic().getGrassObjects(), 160, 275);
			addTextField(this, 17, sandObjectsTextField, getEditorFrame().getTrackData().getGraphic().getSandObjects(), 160, 275);
			addTextField(this, 18, rockObjectsTextField, getEditorFrame().getTrackData().getGraphic().getRockObjects(), 160, 275);
			addTextField(this, 19, curbObjectsTextField, getEditorFrame().getTrackData().getGraphic().getCurbObjects(), 160, 275);
			addTextField(this, 20, fenceObjectsTextField, getEditorFrame().getTrackData().getGraphic().getFenceObjects(), 160, 275);
			addTextField(this, 21, barrierObjectsTextField, getEditorFrame().getTrackData().getGraphic().getBarrierObjects(), 160, 275);
			addTextField(this, 22, wallObjectsTextField, getEditorFrame().getTrackData().getGraphic().getWallObjects(), 160, 275);
			addTextField(this, 23, environmentObjectsTextField, getEditorFrame().getTrackData().getGraphic().getEnvironmentObjects(), 160, 275);
		}

		if (getEditorFrame().getCarsSportsRacing())
		{
			add(getTerrainObjectsButton(), null);
			add(getRoadObjectsButton(), null);
			add(getRoad2ObjectsButton(), null);
			add(getTreesObjectsButton(), null);
			add(getBuildingObjectsButton(), null);
			add(getHouseObjectsButton(), null);
			add(getCarsObjectsButton(), null);
			add(getTruckObjectsButton(), null);
			add(getTribunesObjectsButton(), null);
			add(getPubObjectsButton(), null);
			add(getDecorsObjectsButton(), null);
			add(getWaterObjectsButton(), null);
			add(getGrassObjectsButton(), null);
			add(getSandObjectsButton(), null);
			add(getRockObjectsButton(), null);
			add(getCurbObjectsButton(), null);
			add(getFenceObjectsButton(), null);
			add(getBarrierObjectsButton(), null);
			add(getWallObjectsButton(), null);
			add(getEnvironmentObjectsButton(), null);
		}
	}

	/**
	 * This method initializes backgroundImageButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getTerrainObjectsButton()
	{
		if (terrainObjectsButton == null)
		{
			terrainObjectsButton = new JButton();
			terrainObjectsButton.setBounds(440, 117, 80, 25);
			terrainObjectsButton.setText("Browse");
			terrainObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(terrainObjectsTextField);
				}
			});
		}
		return terrainObjectsButton;
	}

	/**
	 * This method initializes roadObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getRoadObjectsButton()
	{
		if (roadObjectsButton == null)
		{
			roadObjectsButton = new JButton();
			roadObjectsButton.setBounds(440, 144, 80, 25);
			roadObjectsButton.setText("Browse");
			roadObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(roadObjectsTextField);
				}
			});
		}
		return roadObjectsButton;
	}

	/**
	 * This method initializes road2ObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getRoad2ObjectsButton()
	{
		if (road2ObjectsButton == null)
		{
			road2ObjectsButton = new JButton();
			road2ObjectsButton.setBounds(440, 171, 80, 25);
			road2ObjectsButton.setText("Browse");
			road2ObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(road2ObjectsTextField);
				}
			});
		}
		return road2ObjectsButton;
	}

	/**
	 * This method initializes treesObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getTreesObjectsButton()
	{
		if (treesObjectsButton == null)
		{
			treesObjectsButton = new JButton();
			treesObjectsButton.setBounds(440, 198, 80, 25);
			treesObjectsButton.setText("Browse");
			treesObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(treesObjectsTextField);
				}
			});
		}
		return treesObjectsButton;
	}

	/**
	 * This method initializes buildingObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBuildingObjectsButton()
	{
		if (buildingObjectsButton == null)
		{
			buildingObjectsButton = new JButton();
			buildingObjectsButton.setBounds(440, 225, 80, 25);
			buildingObjectsButton.setText("Browse");
			buildingObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(buildingObjectsTextField);
				}
			});
		}
		return buildingObjectsButton;
	}

	/**
	 * This method initializes houseObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getHouseObjectsButton()
	{
		if (houseObjectsButton == null)
		{
			houseObjectsButton = new JButton();
			houseObjectsButton.setBounds(440, 252, 80, 25);
			houseObjectsButton.setText("Browse");
			houseObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(houseObjectsTextField);
				}
			});
		}
		return houseObjectsButton;
	}

	/**
	 * This method initializes backgroundImageButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCarsObjectsButton()
	{
		if (carsObjectsButton == null)
		{
			carsObjectsButton = new JButton();
			carsObjectsButton.setBounds(440, 279, 80, 25);
			carsObjectsButton.setText("Browse");
			carsObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(carsObjectsTextField);
				}
			});
		}
		return carsObjectsButton;
	}

	/**
	 * This method initializes truckObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getTruckObjectsButton()
	{
		if (truckObjectsButton == null)
		{
			truckObjectsButton = new JButton();
			truckObjectsButton.setBounds(440, 306, 80, 25);
			truckObjectsButton.setText("Browse");
			truckObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(truckObjectsTextField);
				}
			});
		}
		return truckObjectsButton;
	}

	/**
	 * This method initializes tribunesObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getTribunesObjectsButton()
	{
		if (tribunesObjectsButton == null)
		{
			tribunesObjectsButton = new JButton();
			tribunesObjectsButton.setBounds(440, 333, 80, 25);
			tribunesObjectsButton.setText("Browse");
			tribunesObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(tribunesObjectsTextField);
				}
			});
		}
		return tribunesObjectsButton;
	}

	/**
	 * This method initializes pubObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getPubObjectsButton()
	{
		if (pubObjectsButton == null)
		{
			pubObjectsButton = new JButton();
			pubObjectsButton.setBounds(440, 360, 80, 25);
			pubObjectsButton.setText("Browse");
			pubObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(pubObjectsTextField);
				}
			});
		}
		return pubObjectsButton;
	}

	/**
	 * This method initializes decorsObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDecorsObjectsButton()
	{
		if (decorsObjectsButton == null)
		{
			decorsObjectsButton = new JButton();
			decorsObjectsButton.setBounds(440, 387, 80, 25);
			decorsObjectsButton.setText("Browse");
			decorsObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(decorsObjectsTextField);
				}
			});
		}
		return decorsObjectsButton;
	}

	/**
	 * This method initializes waterObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getWaterObjectsButton()
	{
		if (waterObjectsButton == null)
		{
			waterObjectsButton = new JButton();
			waterObjectsButton.setBounds(440, 414, 80, 25);
			waterObjectsButton.setText("Browse");
			waterObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(waterObjectsTextField);
				}
			});
		}
		return waterObjectsButton;
	}

	/**
	 * This method initializes grassObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getGrassObjectsButton()
	{
		if (grassObjectsButton == null)
		{
			grassObjectsButton = new JButton();
			grassObjectsButton.setBounds(440, 441, 80, 25);
			grassObjectsButton.setText("Browse");
			grassObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(grassObjectsTextField);
				}
			});
		}
		return grassObjectsButton;
	}

	/**
	 * This method initializes sandObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getSandObjectsButton()
	{
		if (sandObjectsButton == null)
		{
			sandObjectsButton = new JButton();
			sandObjectsButton.setBounds(440, 468, 80, 25);
			sandObjectsButton.setText("Browse");
			sandObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(sandObjectsTextField);
				}
			});
		}
		return sandObjectsButton;
	}

	/**
	 * This method initializes rockObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getRockObjectsButton()
	{
		if (rockObjectsButton == null)
		{
			rockObjectsButton = new JButton();
			rockObjectsButton.setBounds(440, 495, 80, 25);
			rockObjectsButton.setText("Browse");
			rockObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(rockObjectsTextField);
				}
			});
		}
		return rockObjectsButton;
	}

	/**
	 * This method initializes curbObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCurbObjectsButton()
	{
		if (curbObjectsButton == null)
		{
			curbObjectsButton = new JButton();
			curbObjectsButton.setBounds(440, 522, 80, 25);
			curbObjectsButton.setText("Browse");
			curbObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(curbObjectsTextField);
				}
			});
		}
		return curbObjectsButton;
	}

	/**
	 * This method initializes fenceObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getFenceObjectsButton()
	{
		if (fenceObjectsButton == null)
		{
			fenceObjectsButton = new JButton();
			fenceObjectsButton.setBounds(440, 549, 80, 25);
			fenceObjectsButton.setText("Browse");
			fenceObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(fenceObjectsTextField);
				}
			});
		}
		return fenceObjectsButton;
	}

	/**
	 * This method initializes backgroundImageButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBarrierObjectsButton()
	{
		if (barrierObjectsButton == null)
		{
			barrierObjectsButton = new JButton();
			barrierObjectsButton.setBounds(440, 576, 80, 25);
			barrierObjectsButton.setText("Browse");
			barrierObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(barrierObjectsTextField);
				}
			});
		}
		return barrierObjectsButton;
	}

	/**
	 * This method initializes wallObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getWallObjectsButton()
	{
		if (wallObjectsButton == null)
		{
			wallObjectsButton = new JButton();
			wallObjectsButton.setBounds(440, 603, 80, 25);
			wallObjectsButton.setText("Browse");
			wallObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(wallObjectsTextField);
				}
			});
		}
		return wallObjectsButton;
	}

	/**
	 * This method initializes environmentObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getEnvironmentObjectsButton()
	{
		if (environmentObjectsButton == null)
		{
			environmentObjectsButton = new JButton();
			environmentObjectsButton.setBounds(440, 630, 80, 25);
			environmentObjectsButton.setText("Browse");
			environmentObjectsButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectsFile(environmentObjectsTextField);
				}
			});
		}
		return environmentObjectsButton;
	}

	protected void objectsFile(JTextField textField)
	{
		Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle("3D objects file selection");
		fc.setFileHidingEnabled(false);
		fc.setVisible(true);
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("ac and acc", "ac", "acc");
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
			textField.setText(fileName);
		}
	}	
	
	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();

		if (isDifferent(descriptionTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getDescription(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setDescription(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(descriptionNightTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getDescriptionNight(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setDescriptionNight(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(descriptionRainNightTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getDescriptionRainNight(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setDescriptionRainNight(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(separateObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getSeparateObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setSeparateObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(terrainObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getTerrainObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setTerrainObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(roadObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getRoadObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setRoadObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(road2ObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getRoad2Objects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setRoad2Objects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(treesObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getTreesObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setTreesObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(buildingObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getBuildingObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setBuildingObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(houseObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getHouseObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setHouseObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(carsObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getCarsObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setCarsObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(truckObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getTruckObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setTruckObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(tribunesObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getTribunesObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setTribunesObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(pubObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getPubObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setPubObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
		if (isDifferent(decorsObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getDecorsObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setDecorsObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(waterObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getWaterObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setWaterObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(grassObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getGrassObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setGrassObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(sandObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getSandObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setSandObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(rockObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getRockObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setRockObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
		if (isDifferent(curbObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getCurbObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setCurbObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(fenceObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getFenceObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setFenceObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(barrierObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getBarrierObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setBarrierObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(wallObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getWallObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setWallObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(environmentObjectsTextField.getText(),
			getEditorFrame().getTrackData().getGraphic().getEnvironmentObjects(), stringResult))
		{
			getEditorFrame().getTrackData().getGraphic().setEnvironmentObjects(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
	}
}
