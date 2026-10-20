/*
 *   EditorFrame.java
 *   Created on 9 ??? 2005
 *
 *    The EditorFrame.java is part of TrackEditor-0.6.0.
 *
 *    TrackEditor-0.6.0 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.6.0 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.6.0; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package gui;

import gui.properties.GraphicObjectProperties;
import gui.properties.PropertiesDialog;
import gui.splash.SplashScreen;
import gui.view.CircuitView;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import plugin.torcs.XmlReader;
import plugin.torcs.XmlWriter;
import utils.CustomFileFilter;
import utils.Editor;
import utils.Project;
import utils.Properties;
import utils.SegmentVector;
import utils.TrackData;
import utils.TrackEditor;
import utils.ac3d.Ac3d;
import utils.ac3d.Ac3dMaterial;
import utils.ac3d.Ac3dObject;
import utils.ac3d.Ac3dSurface;
import utils.circuit.Curve;
import utils.circuit.GraphicObject;
import utils.circuit.MainTrack;
import utils.circuit.ObjShapeObject;
import utils.circuit.ObjectMap;
import utils.circuit.Pits;
import utils.circuit.Reliefs;
import utils.circuit.Segment;
import utils.circuit.Straight;
import utils.circuit.Surface;
import utils.circuit.TerrainGeneration;
import utils.circuit.TrackObject;
import utils.undo.Undo;

/**
 * @author Patrice Espie , Charalampos Alexopoulos
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class EditorFrame extends JFrame
{
	UndoAction					undoAction							= null;
	RedoAction					redoAction							= null;
	DeleteAction				deleteAction						= null;
	ZoomPlusAction				zoomPlusAction						= null;
	ZoomOneAction				zoomOneAction						= null;
	ZoomMinusAction				zoomMinusAction						= null;
	StraightAction				straightAction						= null;
	RightAction					rightAction							= null;
	LeftAction					leftAction							= null;
	NewAction					newAction							= null;
	OpenAction					openAction							= null;
	RecentAction				recentAction						= null;
	SaveAction					saveAction							= null;
	ShowArrowsAction			showArrowsAction					= null;
	ShowBackgroundAction		showBackgroundAction				= null;
	ShowObjectsAction			showObjectsAction					= null;
	ShowReliefsAction			showReliefsAction					= null;
	MoveAction					moveAction							= null;
	SubdivideAction 			subdivideAction						= null;
	DumpTextAction 				dumpTextAction						= null;
	DumpAC3DAction 				dumpAC3DAction						= null;	
	HelpAction					helpAction							= null;
	ImportAction				importAction						= null;
	ExportAction				exportAction						= null;
	ExportAllAction				allAction							= null;
	ExportAC3Action				ac3Action							= null;
	ExportAC3E0Action			ac3E0Action							= null;
	ExportAC3E1Action			ac3E1Action							= null;
	ExportAC3E2Action			ac3E2Action							= null;
	ExportAC3E3Action			ac3E3Action							= null;
	ExportAC3E4Action			ac3E4Action							= null;
	ExportAC3RacelineAction		ac3RacelineAction					= null;
	PropertiesAction			propertiesAction					= null;
	CalcDeltaAction				calcDeltaAction						= null;
	CheckAction					checkAction							= null;
	FinishLineAction			finishLineAction					= null;

	private JPanel				jContentPane						= null;

	public boolean				documentIsModified;

	CircuitView					view;

	// UI
	JMenuBar					mainMenuBar							= new JMenuBar();
	JMenu						menuFile							= new JMenu();
	JMenuItem					itemSaveCircuit						= null;
	JMenuItem					itemCloseCircuit					= new JMenuItem();
	JScrollPane					mainScrollPane						= new JScrollPane();
	GridBagLayout				gridBagLayout1						= new GridBagLayout();
	JButton						buttonZoomPlus						= null;
	JButton						buttonZoomMinus						= null;
	JButton						buttonZoomOne						= null;
	public JToggleButton		toggleButtonCreateStraightSegment	= null;
	public JToggleButton		toggleButtonCreateLeftSegment		= null;
	public JToggleButton		toggleButtonCreateRightSegment		= null;
	public JToggleButton		toggleButtonDelete					= null;
	public JToggleButton 		toggleButtonSubdivide				= null;
	JButton						undoButton							= null;
	JButton						redoButton							= null;
	JMenu						viewMenu							= new JMenu();
	JMenuItem					menuItemAddBackground				= new JMenuItem();
	JMenuItem					menuItemShoStartPoint				= new JMenuItem();

	private JToolBar			jToolBar							= null;
	private JMenuItem			newMenuItem							= null;
	private JMenuItem			openMenuItem						= null;
	private JMenu				recentFilesMenu						= null;
	private JButton				saveButton							= null;
	private JButton				openButton							= null;
	private JButton				helpButton							= null;
	private JButton				checkButton							= null;
	public JToggleButton		finishLineToggleButton				= null;
	private JButton				propertiesButton					= null;
	private JMenuItem			zoomPlusMenuItem					= null;
	private JMenuItem			zoomMinusMenuItem					= null;
	private JMenuItem			zoomOneMenuItem						= null;
	private JMenu				segmentMenu							= null;
	private JMenu				helpMenu							= null;
	private JMenuItem			aboutMenuItem						= null;
	private JMenuItem			addStraightMenuItem					= null;
	private JMenuItem			addRightMenuItem					= null;
	private JMenuItem			addLeftMenuItem						= null;
	private JMenuItem			moveMenuItem						= null;
	private JMenuItem			deleteMenuItem						= null;
	private JMenuItem 			subdivideMenuItem					= null;	
	private JMenuItem 			dumpTextMenuItem					= null;
	private JMenuItem 			dumpAC3DMenuItem					= null;	
	private JMenuItem			showArrowsMenuItem					= null;
	private JMenuItem			showBackgroundMenuItem				= null;
	private JMenuItem			showObjectsMenuItem					= null;
	private JMenuItem			showReliefsMenuItem					= null;
	private JMenuItem			defaultSurfacesItem					= null;
	private JMenuItem			defaultObjectsItem					= null;
	private JToggleButton		moveButton							= null;
	private JToggleButton		showArrowsButton					= null;
	private JToggleButton		showBackgroundButton				= null;
	private JToggleButton		showObjectsButton					= null;
	private JToggleButton		showReliefsButton					= null;
	private JButton				newButton							= null;
	//private DeltaPanel			deltaPanel							= null;

	/**
	 * The splash screen shown at startup
	 */
	private SplashScreen		_splash;

	private JMenu				editMenu							= null;
	private JMenuItem			undoMenuItem						= null;
	private JMenuItem			redoMenuItem						= null;
	private JMenu				importMenu							= null;
	private JMenuItem			importMenuItem						= null;
	private JMenu				exportMenu							= null;
	private JMenuItem			exportMenuItem						= null;
	private JMenuItem			exportAllMenuItem					= null;
	private JMenuItem			exportAC3MenuItem					= null;
	private JMenuItem			exportAC3E0MenuItem					= null;
	private JMenuItem			exportAC3E1MenuItem					= null;
	private JMenuItem			exportAC3E2MenuItem					= null;
	private JMenuItem			exportAC3E3MenuItem					= null;
	private JMenuItem			exportAC3E4MenuItem					= null;
	private JMenuItem			exportAC3RacelineMenuItem			= null;
	private JMenuItem			propertiesMenuItem					= null;

	private Project				prj;

	private String 				sep									= System.getProperty("file.separator");

	private JButton 			calculateDeltaButton				= null;
	
	private Preferences			preferences							= Preferences.userNodeForPackage(EditorFrame.class);
	private List<String>		recentFiles							= new ArrayList<String>();
	private final static String	RECENT_FILES_STRING					= "recent.files.";
	private int					recentFilesMax						= 10;
	private final static String	RECENT_FILES_MAX					= "RecentFilesMax";
	private boolean				interactiveFixes					= false;
	private final static String	INTERACTIVE_FIXES					= "InteractiveFixes";
	private boolean				cursorCoordinates					= false;
	private final static String	CURSOR_COORDINATES					= "CursorCoordinates";
	private boolean				cursorNames							= false;
	private final static String	CURSOR_NAMES						= "CursorNames";
	private boolean				checkDefaultObjects					= false;
	private final static String	CHECK_DEFAULT_OBJECTS				= "CheckDefaultObjects";
	private boolean				carsSportsRacing					= false;
	private final static String	CARS_SPORTS_RACING					= "CarsSportsRacing";
	
	private TrackData			trackData							= null;
	private Vector<Surface>		defaultSurfaces						= new Vector<Surface>();
	private Vector<TrackObject>	defaultObjects						= new Vector<TrackObject>();
	private String				dataDirectory						= null;
	private String				binDirectory						= null;
	private String				libDirectory						= null;
	private final static String	SD_DATA_DIRECTORY					= "DataDirectory";
	private final static String	SD_BIN_DIRECTORY					= "BinDirectory";
	private final static String	SD_LIB_DIRECTORY					= "LibDirectory";

	private DefaultSurfacesDialog	defaultSurfacesDialog			= null;
	private DefaultObjectsDialog	defaultObjectsDialog			= null;
	
	private String				originalTitle						= null;

	private boolean				pasteObject							= false;
	private int 				currentObjectMap					= -1;
	private int					currentObjectColor					= 0;
	private boolean				currentObjectGraphic				= false;

	private GraphicObjectProperties	graphicObjectProperties			= null;
	
	public GraphicObjectProperties getGraphicObjectProperties() {
		return graphicObjectProperties;
	}

	public void setGraphicObjectProperties(GraphicObjectProperties graphicObjectProperties) {
		this.graphicObjectProperties = graphicObjectProperties;
	}

	public boolean isPasteObject() {
		return pasteObject;
	}

	public void setPasteObject(boolean pasteObject) {
		this.pasteObject = pasteObject;
	}

	public boolean isCurrentObjectGraphic() {
		return currentObjectGraphic;
	}

	public void setCurrentObjectGraphic(boolean currentObjectGraphic) {
		this.currentObjectGraphic = currentObjectGraphic;
	}

	public int getCurrentObjectMap() {
		return currentObjectMap;
	}

	public void setCurrentObjectMap(int currentObjectMap) {
		this.currentObjectMap = currentObjectMap;
	}

	public int getCurrentObjectColor() {
		return currentObjectColor;
	}

	public void setCurrentObjectColor(int currentObjectColor) {
		this.currentObjectColor = currentObjectColor;
	}

	public CircuitView getCircuitView() {
		return view;
	}

	public class NewProjectInfo
	{
		public String	name;
		public String	category;
		public String	subcategory;
		public int		version;
		public String	path;
		public String	author;
		public String	email;
		public String	copyright;
		public String	description;
	}
	
	public Rectangle2D.Double getBoundingRectangle()
	{
		return view.getBoundingRectangle();
	}
	
	public TrackData getTrackData()
	{
		return trackData;
	}
	
	public void setTrackData(TrackData trackData)
	{
		this.trackData = trackData;
	}

	public EditorFrame()
	{
		boolean doSplash = true;
		SplashScreen.setDoSplash(doSplash);
		if (doSplash)
		{
			_splash = SplashScreen.getInstance();
			_splash.setStatus("Initializing gui ...");
			_splash.incProgress(20);
			_splash.setVisible(true);
			try
			{
				Thread.sleep(100);
			} catch (InterruptedException ex)
			{
			}
		}

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		createActions();
		try
		{
			view = new CircuitView(this);
			_splash.incProgress(20);
			Editor.getProperties().addPropertiesListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					//view.setBackgroundImage(properties.getImage());
					//documentIsModified = true;

				}
			});
			initialize();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		_splash.dispose();

		dataDirectory = preferences.get(SD_DATA_DIRECTORY, null);
		binDirectory = preferences.get(SD_BIN_DIRECTORY, null);
		libDirectory = preferences.get(SD_LIB_DIRECTORY, null);
		recentFilesMax = Integer.parseInt(preferences.get(RECENT_FILES_MAX, "10"));
		interactiveFixes = preferences.getBoolean(INTERACTIVE_FIXES, false);
		cursorCoordinates = preferences.getBoolean(CURSOR_COORDINATES, false);
		cursorNames = preferences.getBoolean(CURSOR_NAMES, false);
		checkDefaultObjects = preferences.getBoolean(CHECK_DEFAULT_OBJECTS, false);
		// the game type is a preference; the app name only supplies the default
		carsSportsRacing = preferences.getBoolean(carsSportsRacingKey(), TrackEditor.executableName.equals("csr-trackeditor"));

		if (dataDirectory == null)
		{
			JOptionPane.showMessageDialog(this,
					"You need to specify the Speed Dreams data directory in order to\n" +
					"load the default surfaces, textures and objects.\n" +
					"Make sure you have write permission if you intend to edit or add\n" +
					"tracks in this directory.\n\n" +
					"You may need to specify the bin directory where " + 
					(getCarsSportsRacing() ? "csr-trackgen" : "sd2-trackgen") + " is\n" +
					"located if it is not in your executable path.",
					"First Time Setup",
					JOptionPane.INFORMATION_MESSAGE);
			newPreferencesDialog();
		}

		readDefaultSurfaces();
		readDefaultObjects();
		
		originalTitle = getTitle();
	}
	
	private void readDefaultSurfaces()
	{
		if (dataDirectory != null && !dataDirectory.isEmpty())
		{
			String defaultSurfaceFile = dataDirectory + sep + "data" + sep + "tracks" + sep + "surfaces.xml";
			try
			{
				XmlReader xmlreader = new XmlReader(this);
				xmlreader.readDefaultSurfaces(defaultSurfaceFile, defaultSurfaces);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Opening Default Surfaces", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public Vector<Surface> getDefaultSurfaces()
	{
		return defaultSurfaces;
	}

	private void readDefaultObjects()
	{
		if (dataDirectory != null && !dataDirectory.isEmpty())
		{
			String defaultSurfaceFile = dataDirectory + sep + "data" + sep + "tracks" + sep + "objects.xml";
			try
			{
				XmlReader xmlreader = new XmlReader(this);
				xmlreader.readDefaultObjects(defaultSurfaceFile, defaultObjects);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Opening Default Objects", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public Vector<TrackObject> getDefaultObjects()
	{
		return defaultObjects;
	}

	public String getDataDirectory()
	{
		return dataDirectory;
	}
	
	public void setDataDirectory(String dataDirectory)
	{
		this.dataDirectory = dataDirectory;
		
		preferences.put(SD_DATA_DIRECTORY, this.dataDirectory);
	}

	public String getBinDirectory()
	{
		return binDirectory;
	}
	
	public void setBinDirectory(String binDirectory)
	{
		this.binDirectory = binDirectory;
		
		preferences.put(SD_BIN_DIRECTORY, this.binDirectory);
	}

	public String getLibDirectory()
	{
		return libDirectory;
	}
	
	public void setLibDirectory(String libDirectory)
	{
		this.libDirectory = libDirectory;
		
		preferences.put(SD_LIB_DIRECTORY, this.libDirectory);
	}

	public int getRecentFilesMax()
	{
		return recentFilesMax;
	}
	
	public void setRecentFilesMax(int recentFilesMax)
	{
		this.recentFilesMax = recentFilesMax;
		
		while (recentFiles.size() > recentFilesMax)
		{
			int index = recentFiles.size() - 1;
			System.out.println("removing " + index);
			recentFiles.remove(index);
			recentFilesMenu.remove(index);
			preferences.remove(RECENT_FILES_STRING+index);
		}

		preferences.put(RECENT_FILES_MAX, this.recentFilesMax+"");
	}

	public boolean getInteractiveFixes()
	{
		return interactiveFixes;
	}

	public void setInteractiveFixes(boolean value)
	{
		interactiveFixes = value;

		preferences.putBoolean(INTERACTIVE_FIXES, interactiveFixes);
	}

	public boolean getCursorCoordinates()
	{
		return cursorCoordinates;
	}

	public void setCursorCoordinates(boolean value)
	{
		cursorCoordinates = value;

		preferences.putBoolean(CURSOR_COORDINATES, cursorCoordinates);
	}

	public boolean getCursorNames()
	{
		return cursorNames;
	}

	public void setCursorNames(boolean value)
	{
		cursorNames = value;

		preferences.putBoolean(CURSOR_NAMES, cursorNames);
	}

	public boolean getCheckDefaultObjects()
	{
		return checkDefaultObjects;
	}

	public void setCheckDefaultObjects(boolean value)
	{
		checkDefaultObjects = value;

		preferences.putBoolean(CHECK_DEFAULT_OBJECTS, checkDefaultObjects);
	}

	public boolean getCarsSportsRacing()
	{
		return carsSportsRacing;
	}

	public void setCarsSportsRacing(boolean value)
	{
		if (value == carsSportsRacing)
			return;

		carsSportsRacing = value;

		preferences.putBoolean(carsSportsRacingKey(), carsSportsRacing);
	}

	/**
	 * sd2-trackeditor and csr-trackeditor share one preferences node, so the game
	 * type is stored per app name. Otherwise choosing a type in one app would change
	 * the default of the other.
	 */
	private static String carsSportsRacingKey()
	{
		return CARS_SPORTS_RACING + "." + TrackEditor.executableName;
	}

	private void updateRecentFiles(String filename)
	{
		recentFiles.remove(filename);
		recentFiles.add(0, filename);
		
		if (recentFiles.size() > recentFilesMax)
		{
			recentFiles.remove(recentFiles.size() - 1);
		}
    
		for (int i = 0; i < recentFilesMax; i++)
		{
			if (i < recentFiles.size())
			{
				preferences.put(RECENT_FILES_STRING+i, recentFiles.get(i));
			}
			else
			{
				preferences.remove(RECENT_FILES_STRING+i);
			}
		}
		
		for (int i = 0; i < recentFiles.size(); i++)
		{
			if (i < (recentFilesMenu.getItemCount() - 2))
			{
				if (recentFilesMenu.getItem(i).getText() != recentFiles.get(i))
				{
					recentFilesMenu.getItem(i).setText(recentFiles.get(i));
				}
			}
			else
			{
				addRecentFilesMenuItem(recentFiles.get(i));
			}
		}
	}

	private void addRecentFilesMenuItem(String filename)
	{
		JMenuItem recentFileMenuItem = new JMenuItem(filename);
		recentFileMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				openProject(((JMenuItem) e.getSource()).getText());
			}
		});
		recentFilesMenu.insert(recentFileMenuItem, recentFilesMenu.getItemCount() - 2);
	}

	/**
	 *
	 */
	protected void openProject()
	{
		Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle("Project path selection");
		fc.setVisible(true);
		String trackDir = preferences.get(SD_DATA_DIRECTORY, null);
		if (trackDir == null)
			trackDir = System.getProperty("user.dir");
		fc.setCurrentDirectory(new File(trackDir + sep + "tracks"));
		CustomFileFilter filter = new CustomFileFilter();
		filter.addValid(".prj.xml");
		filter.setDescription("*.prj.xml");
		fc.setFileFilter(filter);
		int result = fc.showOpenDialog(this);
		UIManager.put("FileChooser.readOnly", old);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			openProject(fc.getSelectedFile().toString());
		}
	}
	
	private void openProject(String projectFileName)
	{
		//System.out.println("openProject reading : " + projectFileName);
		
		try
		{
			XMLDecoder decoder = new XMLDecoder(new FileInputStream(projectFileName));
			Editor.setProperties((Properties)decoder.readObject());
			decoder.close();
			
			// update path from project file location
			Editor.getProperties().setPath(projectFileName.substring(0, projectFileName.lastIndexOf(sep)));
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(this, "Opening project file : " + projectFileName + "\n\n" + ex.getLocalizedMessage(), "Project Open", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String trackFileName = projectFileName.replaceAll(".prj.xml", ".xml");
		File file = new File(trackFileName);
		if (!file.exists())
		{
			JOptionPane.showMessageDialog(this, "File not found : " + trackFileName, "Project Open", JOptionPane.ERROR_MESSAGE);				
			return;
		}
		trackData = null;
		trackData = new TrackData();		
		readFile(file);
		updateRecentFiles(projectFileName);
		
		setTitle(originalTitle + " - Project: " + projectFileName);
	}

	/**
	 *
	 */
	/**
	 * Saves the project file and the track xml.
	 *
	 * @return true only if both were written; on failure the user has been
	 *         shown the error and the document stays marked as modified
	 */
	protected boolean saveProject()
	{
		if (getTrackData() == null)
		{
			message("No track", "Nothing to save");
			return false;
		}

		String filename = Editor.getProperties().getPath();
		String trackname = filename.substring(filename.lastIndexOf(sep) + 1);
		filename = filename + sep + trackname + ".prj.xml";

		//System.out.println("saveProject writing : " + filename);

		boolean projectSaved = true;
		try (FileOutputStream out = new FileOutputStream(filename))
		{
			// XMLEncoder reports problems through this listener instead of throwing
			final Exception[] encoderError = { null };
			XMLEncoder encoder = new XMLEncoder(out);
			encoder.setExceptionListener(new ExceptionListener()
			{
				public void exceptionThrown(Exception e)
				{
					if (encoderError[0] == null)
						encoderError[0] = e;
				}
			});
			encoder.writeObject(Editor.getProperties());
			encoder.close();

			if (encoderError[0] != null)
				throw encoderError[0];
		} catch (Exception e)
		{
			projectSaved = false;
			JOptionPane.showMessageDialog(this,
				"Couldn't write : " + filename + "\n\n" + e.getLocalizedMessage(),
				"Save Project", JOptionPane.ERROR_MESSAGE);
		}

		// write the track even if the project file failed, so as much as possible is saved
		boolean trackSaved = exportTrack();

		if (!projectSaved || !trackSaved)
			return false;

		documentIsModified = false;
		updateRecentFiles(filename);
		return true;
	}

	void itemCloseCircuit_actionPerformed(ActionEvent e)
	{
		if (canClose())
		{
			System.exit(0);
		}
	}
	/**
	 *
	 */
	protected void newProjectDialog()
	{
		NewProjectInfo	newProjectInfo = new NewProjectInfo();
		
		NewProjectDialog newProject = new NewProjectDialog(this, newProjectInfo);
		newProject.setVisible(true);
		if (NewProjectDialog.APPROVE)
		{
			setTitle(originalTitle);
			try
			{
				createNewCircuit(newProjectInfo);
				String filename = Editor.getProperties().getPath();
				String trackname = filename.substring(filename.lastIndexOf(sep) + 1);
				filename = filename + sep + trackname + ".prj.xml";
				setTitle(originalTitle + " - Project: " + filename);
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
			refresh();
		}
	}

	/**
     *
     */
    private void createNewCircuit(NewProjectInfo newProjectInfo)
    {
        Segment shape;
        SegmentVector track = new SegmentVector();

        shape = new Straight();
        shape.setLength(100);
        shape.setHeightStart(0);
        shape.setSurface(MainTrack.DEFAULT_SURFACE);
        shape.getLeft().setNewTrackDefaults();
        shape.getRight().setNewTrackDefaults();
        track.add(shape);

        shape = new Curve();
        ((Curve)shape).setRadiusStart(100);
        ((Curve)shape).setRadiusEnd(100);
        ((Curve)shape).setArcDeg(180);
        shape.setSurface(MainTrack.DEFAULT_SURFACE);
        shape.getLeft().setNewTrackDefaults();
        shape.getRight().setNewTrackDefaults();
        track.add(shape);

        shape = new Straight();
        shape.setLength(100);
        shape.setSurface(MainTrack.DEFAULT_SURFACE);
        shape.getLeft().setNewTrackDefaults();
        shape.getRight().setNewTrackDefaults();
        track.add(shape);

        shape = new Curve();
        ((Curve)shape).setRadiusStart(100);
        ((Curve)shape).setRadiusEnd(100);
        ((Curve)shape).setArcDeg(180);
        shape.setHeightEnd(0);
        shape.setSurface(MainTrack.DEFAULT_SURFACE);
        shape.getLeft().setNewTrackDefaults();
        shape.getRight().setNewTrackDefaults();
        track.add(shape);

        trackData = null;
        trackData = new TrackData();
        
        trackData.getHeader().setName(newProjectInfo.name);
        trackData.getHeader().setCategory(newProjectInfo.category);
        trackData.getHeader().setSubcategory(newProjectInfo.subcategory);
        trackData.getHeader().setVersion(newProjectInfo.version);
        Editor.getProperties().setPath(newProjectInfo.path);
        trackData.getHeader().setAuthor(newProjectInfo.author);
        trackData.getHeader().setEmail(newProjectInfo.email);
        trackData.getHeader().setCopyright(newProjectInfo.copyright);
        trackData.getHeader().setDescription(newProjectInfo.description);

        trackData.getMainTrack().setProfilStepsLength(MainTrack.DEFAULT_PROFIL_STEPS_LENGTH);
        trackData.getMainTrack().setWidth(MainTrack.DEFAULT_WIDTH);
        trackData.getMainTrack().setSurface(MainTrack.DEFAULT_SURFACE);
        trackData.getMainTrack().getLeft().setNewTrackDefaults();
        trackData.getMainTrack().getRight().setNewTrackDefaults();

        trackData.setSegments(track);
        
		documentIsModified = true;
   }

    /**
	 * @return Returns the prj.
	 */
	public Project getProject()
	{
		if(prj == null)
		{
			prj = new Project();
		}
		return prj;
	}

	private void initialize() throws Exception
	{
		String title = Editor.getProperties().title + " " + Editor.getProperties().version;
		this.setTitle(title);
		Image image = new ImageIcon(getClass().getResource("/icon.png")).getImage();
		this.setIconImage(image);
		setSize(new Dimension(preferences.getInt("Width", 800), preferences.getInt("Height", 600)));
		Point point = new Point(preferences.getInt("X", 0), preferences.getInt("Y", 0));
		if (point.x == 0 && point.y == 0)
		{
			setLocationRelativeTo(null);
		}
		else
		{
			setLocation(point);		
		}
		menuFile.setText("File");
		itemCloseCircuit.setText("Exit");
		itemCloseCircuit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				itemCloseCircuit_actionPerformed(e);
			}
		});
		this.setJMenuBar(mainMenuBar);
		this.setContentPane(getJContentPane());
		mainScrollPane.setMaximumSize(new Dimension(100, 100));
		mainScrollPane.setMinimumSize(new Dimension(10, 10));
		mainScrollPane.setPreferredSize(new Dimension(10, 10));
		view.setMaximumSize(new Dimension(100, 100));
		view.setMinimumSize(new Dimension(10, 10));
		view.setPreferredSize(new Dimension(10, 10));
		view.setSize(new Dimension(32767, 32767));
		viewMenu.setText("View");
		_splash.incProgress(20);
		try
		{
			Thread.sleep(100);
		} catch (InterruptedException ex)
		{
		}
		menuItemAddBackground.setText("Add background image");
		menuItemAddBackground.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				menuItemAddBackground_actionPerformed(e);
			}
		});
		menuItemShoStartPoint.setText("Show start point");
		menuItemShoStartPoint.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				menuItemShoStartPoint_actionPerformed(e);
			}
		});

		mainScrollPane.getViewport().add(view, null);
		mainMenuBar.add(menuFile);
		mainMenuBar.add(getEditMenu());
		mainMenuBar.add(viewMenu);
		mainMenuBar.add(getSegmentMenu());
		mainMenuBar.add(getHelpMenu());
		menuFile.add(getNewMenuItem());
		menuFile.add(getOpenMenuItem());
		menuFile.add(getRecentMenu());
		menuFile.add(getItemSaveCircuit());
		menuFile.addSeparator();
		menuFile.add(getImportMenu());
		menuFile.add(getExportMenu());
		menuFile.addSeparator();
		menuFile.add(getPropertiesMenuItem());
		menuFile.addSeparator();
		menuFile.add(itemCloseCircuit);

		viewMenu.add(getZoomPlusMenuItem());
		viewMenu.add(getZoomMinusMenuItem());
		viewMenu.add(getZoomOneMenuItem());
		viewMenu.addSeparator();
		viewMenu.add(getShowArrowsMenuItem());
		viewMenu.add(getShowBackgroundMenuItem());
		viewMenu.add(getShowObjectsMenuItem());
		viewMenu.add(getShowReliefsMenuItem());
		viewMenu.add(menuItemShoStartPoint);
		viewMenu.add(menuItemAddBackground);
		viewMenu.addSeparator();
		viewMenu.add(getDefaultSurfacesMenuItem());
		viewMenu.add(getDefaultObjectsMenuItem());
		_splash.incProgress(20);
		try
		{
			Thread.sleep(1000);
		} catch (InterruptedException ex)
		{
		}
		this.setVisible(true);
		
		getProject().setSegmentEditorX(preferences.getInt("SegmentEditorX", 0));
		getProject().setSegmentEditorY(preferences.getInt("SegmentEditorY", 0));
		getProject().setPropertiesEditorX(preferences.getInt("PropertiesEditorX", 0));
		getProject().setPropertiesEditorY(preferences.getInt("PropertiesEditorY", 0));
		getProject().setPropertiesEditorTab(preferences.getInt("PropertiesEditorTab", 0));
		getProject().setPropertiesEditorSurfaceTab(preferences.getInt("PropertiesEditorSurfaceTab", 0));
		getProject().setPropertiesEditorObjectTab(preferences.getInt("PropertiesEditorObjectTab", 0));
		getProject().setPropertiesEditorCameraTab(preferences.getInt("PropertiesEditorCameraTab", 0));
		getProject().setPropertiesEditorLightTab(preferences.getInt("PropertiesEditorLightTab", 0));
		getProject().setPropertiesEditorTerrainTab(preferences.getInt("PropertiesEditorTerrainTab", 0));
		getProject().setDefaultSurfacesDialogX(preferences.getInt("DefaultSurfacesDialogX", 0));
		getProject().setDefaultSurfacesDialogY(preferences.getInt("DefaultSurfacesDialogY", 0));
		getProject().setDefaultObjectsDialogX(preferences.getInt("DefaultObjectsDialogX", 0));
		getProject().setDefaultObjectsDialogY(preferences.getInt("DefaultObjectsDialogY", 0));
		getProject().setTrackgenDialogX(preferences.getInt("TrackgenDialogX", 0));
		getProject().setTrackgenDialogY(preferences.getInt("TrackgenDialogY", 0));
		getProject().setPreferencesDialogX(preferences.getInt("PreferencesDialogX", 0));
		getProject().setPreferencesDialogY(preferences.getInt("PreferencesDialogY", 0));
		getProject().setNewProjectDialogX(preferences.getInt("NewProjectDialogX", 0));
		getProject().setNewProjectDialogY(preferences.getInt("NewProjectDialogY", 0));		
		getProject().setCheckDialogX(preferences.getInt("CheckDialogX", 0));
		getProject().setCheckDialogY(preferences.getInt("CheckDialogY", 0));
		getProject().setCheckDialogWidth(preferences.getInt("CheckDialogWidth", 500));
		getProject().setCheckDialogHeight(preferences.getInt("CheckDialogHeight", 500));
	}

	/**
	 * This method initializes newMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getNewMenuItem()
	{
		if (newMenuItem == null)
		{
			newMenuItem = new JMenuItem();
			newMenuItem.setAction(newAction);
			newMenuItem.setIcon(null);
			newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		}
		return newMenuItem;
	}
	/**
	 * This method initializes openMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getOpenMenuItem()
	{
		if (openMenuItem == null)
		{
			openMenuItem = new JMenuItem();
			openMenuItem.setAction(openAction);
			openMenuItem.setIcon(null);
			openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		}
		return openMenuItem;
	}
	/**
	 * This method initializes recentFilesMenu
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getRecentMenu()
	{
		if (recentFilesMenu == null)
		{
			recentFilesMenu = new JMenu();
			recentFilesMenu.setAction(recentAction);
			recentFilesMenu.setIcon(null);
			recentFilesMenu.addSeparator();
			JMenuItem clearHistory = new JMenuItem("Clear History");
			clearHistory.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int count = recentFilesMenu.getItemCount() - 2;
					for (int i = 0; i < count; i++)
					{
						preferences.remove(RECENT_FILES_STRING + i);
						recentFilesMenu.remove(0);
					}
					recentFiles.clear();
				}
			});
			recentFilesMenu.add(clearHistory);

			int recentFilesMax = Integer.parseInt(preferences.get(RECENT_FILES_MAX, "10"));

			for (int i = 0; i < recentFilesMax; i++)
			{
				String file = preferences.get(RECENT_FILES_STRING + i, "");

				if (!file.equals(""))
				{
					recentFiles.add(file);
					addRecentFilesMenuItem(file);
				}
				else
				{
					break;
				}
			}
		}
		return recentFilesMenu;
	}

	/**
	 * This method initializes itemSaveCircuit
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getItemSaveCircuit()
	{
		if (itemSaveCircuit == null)
		{
			itemSaveCircuit = new JMenuItem();
			itemSaveCircuit.setAction(saveAction);
			itemSaveCircuit.setIcon(null);
			itemSaveCircuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		}
		return itemSaveCircuit;
	}

	/**
	 * This method initializes zoomPlusMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getZoomPlusMenuItem()
	{
		if (zoomPlusMenuItem == null)
		{
			zoomPlusMenuItem = new JMenuItem();
			zoomPlusMenuItem.setAction(zoomPlusAction);
			zoomPlusMenuItem.setIcon(null);
		}
		return zoomPlusMenuItem;
	}
	/**
	 * This method initializes zoomMinusMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getZoomMinusMenuItem()
	{
		if (zoomMinusMenuItem == null)
		{
			zoomMinusMenuItem = new JMenuItem();
			zoomMinusMenuItem.setAction(zoomMinusAction);
			zoomMinusMenuItem.setIcon(null);
		}
		return zoomMinusMenuItem;
	}
	/**
	 * This method initializes zoomOneMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getZoomOneMenuItem()
	{
		if (zoomOneMenuItem == null)
		{
			zoomOneMenuItem = new JMenuItem();
			zoomOneMenuItem.setAction(zoomOneAction);
			zoomOneMenuItem.setIcon(null);
		}
		return zoomOneMenuItem;
	}
	/**
	 * This method initializes segmentMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getSegmentMenu()
	{
		if (segmentMenu == null)
		{
			segmentMenu = new JMenu();
			segmentMenu.setText("Segment");
			segmentMenu.add(getAddStraightMenuItem());
			segmentMenu.add(getAddRightMenuItem());
			segmentMenu.add(getAddLeftMenuItem());
			segmentMenu.addSeparator();
			segmentMenu.add(getMoveMenuItem());
			segmentMenu.add(getDeleteMenuItem());
			segmentMenu.add(getSubdivideMenuItem());
			segmentMenu.addSeparator();
			segmentMenu.add(getDumpTextMenuItem());
			segmentMenu.add(getDumpAC3DMenuItem());
		}
		return segmentMenu;
	}
	/**
	 * This method initializes helpMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getHelpMenu()
	{
		if (helpMenu == null)
		{
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}
	/**
	 * This method initializes aboutMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getAboutMenuItem()
	{
		if (aboutMenuItem == null)
		{
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.setAction(helpAction);
		}
		return aboutMenuItem;
	}
	/**
	 * This method initializes addStraightMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getAddStraightMenuItem()
	{
		if (addStraightMenuItem == null)
		{
			addStraightMenuItem = new JMenuItem();
			addStraightMenuItem.setAction(straightAction);
			addStraightMenuItem.setIcon(null);
		}
		return addStraightMenuItem;
	}
	/**
	 * This method initializes addRightMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getAddRightMenuItem()
	{
		if (addRightMenuItem == null)
		{
			addRightMenuItem = new JMenuItem();
			addRightMenuItem.setAction(rightAction);
			addRightMenuItem.setIcon(null);
		}
		return addRightMenuItem;
	}
	/**
	 * This method initializes addLeftMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getAddLeftMenuItem()
	{
		if (addLeftMenuItem == null)
		{
			addLeftMenuItem = new JMenuItem();
			addLeftMenuItem.setAction(leftAction);
			addLeftMenuItem.setIcon(null);
		}
		return addLeftMenuItem;
	}
	/**
	 * This method initializes moveMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMoveMenuItem()
	{
		if (moveMenuItem == null)
		{
			moveMenuItem = new JMenuItem();
			moveMenuItem.setAction(moveAction);
			moveMenuItem.setIcon(null);
		}
		return moveMenuItem;
	}
	/**
	 * This method initializes subdivideMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
    private JMenuItem getSubdivideMenuItem()
    {
        if (subdivideMenuItem == null)
        {
            subdivideMenuItem = new JMenuItem();
            subdivideMenuItem.setAction(subdivideAction);
            subdivideMenuItem.setIcon(null);
        }
        return subdivideMenuItem;
    }

    /**
     * This method initializes dumpTextMenuItem
     *
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getDumpTextMenuItem()
    {
    	if (dumpTextMenuItem == null)
    	{
    		dumpTextMenuItem = new JMenuItem();
    		dumpTextMenuItem.setAction(dumpTextAction);
    		dumpTextMenuItem.setIcon(null);
    	}
    	return dumpTextMenuItem;
    }
    /**
     * This method initializes dumpAC3DMenuItem
     *
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getDumpAC3DMenuItem()
    {
    	if (dumpAC3DMenuItem == null)
    	{
    		dumpAC3DMenuItem = new JMenuItem();
    		dumpAC3DMenuItem.setAction(dumpAC3DAction);
    		dumpAC3DMenuItem.setIcon(null);
    	}
    	return dumpAC3DMenuItem;
    }

    /**
	 * This method initializes deleteMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getDeleteMenuItem()
	{
		if (deleteMenuItem == null)
		{
			deleteMenuItem = new JMenuItem();
			deleteMenuItem.setAction(deleteAction);
			deleteMenuItem.setIcon(null);
		}
		return deleteMenuItem;
	}
	/**
	 * This method initializes showArrowsMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getShowArrowsMenuItem()
	{
		if (showArrowsMenuItem == null)
		{
			showArrowsMenuItem = new JMenuItem();
			showArrowsMenuItem.setAction(showArrowsAction);
			showArrowsMenuItem.setIcon(null);
		}
		return showArrowsMenuItem;
	}

	/**
	 * This method initializes showBackgroundMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getShowBackgroundMenuItem()
	{
		if (showBackgroundMenuItem == null)
		{
			showBackgroundMenuItem = new JMenuItem();
			showBackgroundMenuItem.setAction(showBackgroundAction);
			showBackgroundMenuItem.setIcon(null);
		}
		return showBackgroundMenuItem;
	}

	/**
	 * This method initializes showObjectsMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getShowObjectsMenuItem()
	{
		if (showObjectsMenuItem == null)
		{
			showObjectsMenuItem = new JMenuItem();
			showObjectsMenuItem.setAction(showObjectsAction);
			showObjectsMenuItem.setIcon(null);
		}
		return showObjectsMenuItem;
	}

	/**
	 * This method initializes showReliefsMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getShowReliefsMenuItem()
	{
		if (showReliefsMenuItem == null)
		{
			showReliefsMenuItem = new JMenuItem();
			showReliefsMenuItem.setAction(showReliefsAction);
			showReliefsMenuItem.setIcon(null);
		}
		return showReliefsMenuItem;
	}

	private JMenuItem getDefaultSurfacesMenuItem()
	{
		if (defaultSurfacesItem == null)
		{
			defaultSurfacesItem = new JMenuItem("Default Surfaces");
			defaultSurfacesItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					defaultSurfacesDialog();
				}
			});
		}
		return defaultSurfacesItem;
	}
		
	private void defaultSurfacesDialog()
	{
		if (defaultSurfacesDialog == null)
		{
			defaultSurfacesDialog = new DefaultSurfacesDialog(this);
			defaultSurfacesDialog.setVisible(true);
		}
	}
	
	private JMenuItem getDefaultObjectsMenuItem()
	{
		if (defaultObjectsItem == null)
		{
			defaultObjectsItem = new JMenuItem("Default Objects");
			defaultObjectsItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					defaultObjectsDialog();
				}
			});
		}
		return defaultObjectsItem;
	}
		
	private void defaultObjectsDialog()
	{
		if (defaultObjectsDialog == null)
		{
			defaultObjectsDialog = new DefaultObjectsDialog(this);
			defaultObjectsDialog.setVisible(true);
		}
	}
	
	/**
	 * This method initializes editMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getEditMenu()
	{
		if (editMenu == null)
		{
			editMenu = new JMenu();
			editMenu.setText("Edit");
			editMenu.add(getUndoMenuItem());
			editMenu.add(getRedoMenuItem());
			editMenu.addSeparator();
			JMenuItem preferenceItem = new JMenuItem("Preferences");
			preferenceItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					newPreferencesDialog();
				}
			});
			editMenu.add(preferenceItem);
		}
		return editMenu;
	}
	
	private void newPreferencesDialog()
	{
		PreferencesDialog preferencesDialog = new PreferencesDialog(this);
		preferencesDialog.setVisible(true);
		if (PreferencesDialog.APPROVE)
		{
			setDataDirectory(preferencesDialog.getDataDirectory());
			setBinDirectory(preferencesDialog.getBinDirectory());
			setLibDirectory(preferencesDialog.getLibDirectory());
			setRecentFilesMax(preferencesDialog.getRecentFilesMax());
			setInteractiveFixes(preferencesDialog.getInteractiveFixes());
			setCursorCoordinates(preferencesDialog.getCursorCoordinates());
			setCursorNames(preferencesDialog.getCursorNames());
			setCheckDefaultObjects(preferencesDialog.getCheckDefaultObjects());
			setCarsSportsRacing(preferencesDialog.getCarsSportsRacing());
			readDefaultSurfaces();
			readDefaultObjects();
			view.redrawCircuit();
			if (view.segmentParamDialog != null)
				view.segmentParamDialog.refresh();
		}
	}
	
	/**
	 * This method initializes undoMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getUndoMenuItem()
	{
		if (undoMenuItem == null)
		{
			undoMenuItem = new JMenuItem();
			undoMenuItem.setAction(undoAction);
			undoMenuItem.setIcon(null);
			undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
		}
		return undoMenuItem;
	}
	/**
	 * This method initializes redoMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getRedoMenuItem()
	{
		if (redoMenuItem == null)
		{
			redoMenuItem = new JMenuItem();
			redoMenuItem.setAction(redoAction);
			redoMenuItem.setIcon(null);
			redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
		}
		return redoMenuItem;
	}
	/**
	 * This method initializes importMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenu getImportMenu()
	{
		if (importMenu == null)
		{
			importMenu = new JMenu();
			importMenu.setText("Import");
			importMenu.add(getImportMenuItem());
		}
		return importMenu;
	}
	/**
	 * This method initializes importMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	public JMenuItem getImportMenuItem()
	{
		if (importMenuItem == null)
		{
			importMenuItem = new JMenuItem();
			importMenuItem.setAction(importAction);
			importMenuItem.setIcon(null);
		}
		return importMenuItem;
	}
	/**
	 * This method initializes exportMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getExportMenu()
	{
		if (exportMenu == null)
		{
			exportMenu = new JMenu();
			exportMenu.setText("Export");
			exportMenu.add(getExportAllMenuItem());
			exportMenu.addSeparator();
			exportMenu.add(getExportMenuItem());
			exportMenu.add(getExportAC3MenuItem());
			exportMenu.add(getExportAC3E0MenuItem());
			exportMenu.add(getExportAC3E1MenuItem());
			exportMenu.add(getExportAC3E2MenuItem());
			exportMenu.add(getExportAC3E3MenuItem());
			exportMenu.add(getExportAC3E4MenuItem());
			exportMenu.add(getExportAC3RacelineMenuItem());
		}
		return exportMenu;
	}
	/**
	 * This method initializes exportMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	public JMenuItem getExportMenuItem()
	{
		if (exportMenuItem == null)
		{
			exportMenuItem = new JMenuItem();
			exportMenuItem.setAction(exportAction);
			exportMenuItem.setIcon(null);
		}
		return exportMenuItem;
	}
	/**
	 * This method initializes exportAllMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExportAllMenuItem()
	{
		if (exportAllMenuItem == null)
		{
			exportAllMenuItem = new JMenuItem();
			exportAllMenuItem.setAction(allAction);
			exportAllMenuItem.setIcon(null);
		}
		return exportAllMenuItem;
	}
	/**
	 * This method initializes exportAC3MenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExportAC3MenuItem()
	{
		if (exportAC3MenuItem == null)
		{
			exportAC3MenuItem = new JMenuItem();
			exportAC3MenuItem.setAction(ac3Action);
			exportAC3MenuItem.setIcon(null);
		}
		return exportAC3MenuItem;
	}
	/**
	 * This method initializes exportAC3E0MenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExportAC3E0MenuItem()
	{
		if (exportAC3E0MenuItem == null)
		{
			exportAC3E0MenuItem = new JMenuItem();
			exportAC3E0MenuItem.setAction(ac3E0Action);
			exportAC3E0MenuItem.setIcon(null);
		}
		return exportAC3E0MenuItem;
	}
	
	/**
	 * This method initializes exportAC3E1MenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExportAC3E1MenuItem()
	{
		if (exportAC3E1MenuItem == null)
		{
			exportAC3E1MenuItem = new JMenuItem();
			exportAC3E1MenuItem.setAction(ac3E1Action);
			exportAC3E1MenuItem.setIcon(null);
		}
		return exportAC3E1MenuItem;
	}
	
	/**
	 * This method initializes exportAC3E2MenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExportAC3E2MenuItem()
	{
		if (exportAC3E2MenuItem == null)
		{
			exportAC3E2MenuItem = new JMenuItem();
			exportAC3E2MenuItem.setAction(ac3E2Action);
			exportAC3E2MenuItem.setIcon(null);
		}
		return exportAC3E2MenuItem;
	}
	
	/**
	 * This method initializes exportAC3E3MenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExportAC3E3MenuItem()
	{
		if (exportAC3E3MenuItem == null)
		{
			exportAC3E3MenuItem = new JMenuItem();
			exportAC3E3MenuItem.setAction(ac3E3Action);
			exportAC3E3MenuItem.setIcon(null);
		}
		return exportAC3E3MenuItem;
	}
	
	/**
	 * This method initializes exportAC3E4MenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExportAC3E4MenuItem()
	{
		if (exportAC3E4MenuItem == null)
		{
			exportAC3E4MenuItem = new JMenuItem();
			exportAC3E4MenuItem.setAction(ac3E4Action);
			exportAC3E4MenuItem.setIcon(null);
		}
		return exportAC3E4MenuItem;
	}
	
	/**
	 * This method initializes exportAC3RacelineMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExportAC3RacelineMenuItem()
	{
		if (exportAC3RacelineMenuItem == null)
		{
			exportAC3RacelineMenuItem = new JMenuItem();
			exportAC3RacelineMenuItem.setAction(ac3RacelineAction);
			exportAC3RacelineMenuItem.setIcon(null);
		}
		return exportAC3RacelineMenuItem;
	}
	
	/**
	 * This method initializes propertiesMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getPropertiesMenuItem()
	{
		if (propertiesMenuItem == null)
		{
			propertiesMenuItem = new JMenuItem();
			propertiesMenuItem.setAction(propertiesAction);
			propertiesMenuItem.setIcon(null);
		}
		return propertiesMenuItem;
	}

	/**
	 * This method initializes undoButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getUndoButton()
	{
		if (undoButton == null)
		{
			undoButton = new JButton();
			undoButton.setAction(undoAction);
			if (undoButton.getIcon() != null)
			{
				undoButton.setText("");
			}
		}
		return undoButton;
	}

	/**
	 * This method initializes redoButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getRedoButton()
	{
		if (redoButton == null)
		{
			redoButton = new JButton();
			redoButton.setAction(redoAction);
			if (redoButton.getIcon() != null)
			{
				redoButton.setText("");
			}
		}
		return redoButton;
	}

	/**
	 * This method initializes deleteButton
	 *
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getToggleButtonDelete()
	{
		if (toggleButtonDelete == null)
		{
			toggleButtonDelete = new JToggleButton();
			toggleButtonDelete.setAction(deleteAction);
			if (toggleButtonDelete.getIcon() != null)
			{
				toggleButtonDelete.setText("");
			}
		}
		return toggleButtonDelete;
	}

	/**
	 * This method initializes toggleButtonSubdivide
	 *
	 * @return javax.swing.JToggleButton
	 */
    private JToggleButton getToggleButtonSubdivide()
    {
        if (toggleButtonSubdivide == null)
        {
            toggleButtonSubdivide = new JToggleButton();
            toggleButtonSubdivide.setAction(subdivideAction);
            if (toggleButtonSubdivide.getIcon() != null)
            {
                toggleButtonSubdivide.setText("");
            }
        }
        return toggleButtonSubdivide;
    }

	/**
	 * This method initializes buttonZoomPlus
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getButtonZoomPlus()
	{
		if (buttonZoomPlus == null)
		{
			buttonZoomPlus = new JButton();
			buttonZoomPlus.setAction(zoomPlusAction);
			if (buttonZoomPlus.getIcon() != null)
			{
				buttonZoomPlus.setText("");
			}
		}
		return buttonZoomPlus;
	}

	/**
	 * This method initializes buttonZoomPlus
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getButtonZoomOne()
	{
		if (buttonZoomOne == null)
		{
			buttonZoomOne = new JButton();
			buttonZoomOne.setAction(zoomOneAction);
			if (buttonZoomOne.getIcon() != null)
			{
				buttonZoomOne.setText("");
			}
		}
		return buttonZoomOne;
	}

	private JButton getButtonZoomMinus()
	{
		if (buttonZoomMinus == null)
		{
			buttonZoomMinus = new JButton();
			buttonZoomMinus.setAction(zoomMinusAction);
			if (buttonZoomMinus.getIcon() != null)
			{
				buttonZoomMinus.setText("");
			}
		}
		return buttonZoomMinus;
	}

	private JToggleButton getToggleButtonCreateStraightSegment()
	{
		if (toggleButtonCreateStraightSegment == null)
		{
			toggleButtonCreateStraightSegment = new JToggleButton();
			toggleButtonCreateStraightSegment.setAction(straightAction);
			if (toggleButtonCreateStraightSegment.getIcon() != null)
			{
				toggleButtonCreateStraightSegment.setText("");
			}
		}
		return toggleButtonCreateStraightSegment;
	}

	private JToggleButton getToggleButtonCreateRightSegment()
	{
		if (toggleButtonCreateRightSegment == null)
		{
			toggleButtonCreateRightSegment = new JToggleButton();
			toggleButtonCreateRightSegment.setAction(rightAction);
			if (toggleButtonCreateRightSegment.getIcon() != null)
			{
				toggleButtonCreateRightSegment.setText("");
			}
		}
		return toggleButtonCreateRightSegment;
	}

	private JToggleButton getToggleButtonCreateLeftSegment()
	{
		if (toggleButtonCreateLeftSegment == null)
		{
			toggleButtonCreateLeftSegment = new JToggleButton();
			toggleButtonCreateLeftSegment.setAction(leftAction);
			if (toggleButtonCreateLeftSegment.getIcon() != null)
			{
				toggleButtonCreateLeftSegment.setText("");
			}
		}
		return toggleButtonCreateLeftSegment;
	}
	/**
	 * This method initializes moveButton
	 *
	 * @return javax.swing.JButton
	 */
	private JToggleButton getMoveButton()
	{
		if (moveButton == null)
		{
			moveButton = new JToggleButton();
			moveButton.setAction(moveAction);
			if (moveButton.getIcon() != null)
			{
				moveButton.setText("");
			}
		}
		return moveButton;
	}
	/**
	 * This method initializes showArrowsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JToggleButton getShowArrowsButton()
	{
		if (showArrowsButton == null)
		{
			showArrowsButton = new JToggleButton();
			showArrowsButton.setAction(showArrowsAction);
			if (showArrowsButton.getIcon() != null)
			{
				showArrowsButton.setText("");
			}
		}
		return showArrowsButton;
	}
	/**
	 * This method initializes showBackgroundButton
	 *
	 * @return javax.swing.JButton
	 */
	private JToggleButton getShowBackgroundButton()
	{
		if (showBackgroundButton == null)
		{
			showBackgroundButton = new JToggleButton();
			showBackgroundButton.setAction(showBackgroundAction);
			if (showBackgroundButton.getIcon() != null)
			{
				showBackgroundButton.setText("");
			}
		}
		return showBackgroundButton;
	}
	/**
	 * This method initializes showObjectsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JToggleButton getShowObjectsButton()
	{
		if (showObjectsButton == null)
		{
			showObjectsButton = new JToggleButton();
			showObjectsButton.setAction(showObjectsAction);
			if (showObjectsButton.getIcon() != null)
			{
				showObjectsButton.setText("");
			}
		}
		return showObjectsButton;
	}
	/**
	 * This method initializes showReliefsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JToggleButton getShowReliefsButton()
	{
		if (showReliefsButton == null)
		{
			showReliefsButton = new JToggleButton();
			showReliefsButton.setAction(showReliefsAction);
			if (showReliefsButton.getIcon() != null)
			{
				showReliefsButton.setText("");
			}
		}
		return showReliefsButton;
	}
	/**
	 * This method initializes newButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getNewButton()
	{
		if (newButton == null)
		{
			newButton = new JButton();
			newButton.setAction(newAction);
			if (newButton.getIcon() != null)
			{
				newButton.setText("");
			}
		}
		return newButton;
	}
	/**
	 * This method initializes saveButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getSaveButton()
	{
		if (saveButton == null)
		{
			saveButton = new JButton();
			saveButton.setAction(saveAction);
			if (saveButton.getIcon() != null)
			{
				saveButton.setText("");
			}
		}
		return saveButton;
	}
	/**
	 * This method initializes openButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getOpenButton()
	{
		if (openButton == null)
		{
			openButton = new JButton();
			openButton.setAction(openAction);
			if (openButton.getIcon() != null)
			{
				openButton.setText("");
			}
		}
		return openButton;
	}
	/**
	 * This method initializes helpButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getHelpButton()
	{
		if (helpButton == null)
		{
			helpButton = new JButton();
			helpButton.setAction(helpAction);
			if (helpButton.getIcon() != null)
			{
				helpButton.setText("");
			}
		}
		return helpButton;
	}
	/**
	 * This method initializes checkButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCheckButton()
	{
		if (checkButton == null)
		{
			checkButton = new JButton();
			checkButton.setAction(checkAction);
			if (checkButton.getIcon() != null)
			{
				checkButton.setText("");
			}
		}
		return checkButton;
	}
	/**
	 * This method initializes finishLineToggleButton
	 *
	 * @return javax.swing.JButton
	 */
	private JToggleButton getFinishLineToggleButton()
	{
		if (finishLineToggleButton == null)
		{
			finishLineToggleButton = new JToggleButton();
			finishLineToggleButton.setAction(finishLineAction);
			if (finishLineToggleButton.getIcon() != null)
			{
				finishLineToggleButton.setText("");
			}
		}
		return finishLineToggleButton;
	}
	/**
	 * This method initializes propertiesButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getPropertiesButton()
	{
		if (propertiesButton == null)
		{
			propertiesButton = new JButton();
			propertiesButton.setAction(propertiesAction);
			if (propertiesButton.getIcon() != null)
			{
				propertiesButton.setText("");
			}
		}
		return propertiesButton;
	}
	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane()
	{
		if (jContentPane == null)
		{
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(mainScrollPane, BorderLayout.CENTER);
			jContentPane.add(getJToolBar(), BorderLayout.NORTH);
			//jContentPane.add(getDeltaPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

//	private DeltaPanel getDeltaPanel()
//	{
//	    if (deltaPanel == null)
//		{
//	        deltaPanel = new DeltaPanel();
//	        //deltaPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(BevelBorder.LOWERED));
//	        //deltaPanel.setBackground(new Color(0,204,204));
//	        deltaPanel.setPreferredSize(new Dimension(140,24));
//		}
//		return deltaPanel;
//	}

	protected boolean canClose()
	{
		if (documentIsModified)
		{
			// ask whether to save or not
			int Res = JOptionPane.showConfirmDialog(this, "The circuit was modified. Do you want to save it ?",
					"Closing circuit", JOptionPane.YES_NO_CANCEL_OPTION);
			if (Res == JOptionPane.CANCEL_OPTION)
			{
				// can't close
				return false;
			} else if (Res == JOptionPane.YES_OPTION)
			{
				// save, and only close if the save worked
				return saveProject();
			} else
			{
				// don't save, but can close
				return true;
			}
		} else
		{
			// can close
			return true;
		}
	}

	void buttonZoomPlus_actionPerformed(ActionEvent e)
	{
		view.incZoomFactor();
		view.redrawCircuit();
	}

	void buttonZoomMinus_actionPerformed(ActionEvent e)
	{
		view.decZoomFactor();
		view.redrawCircuit();
	}

	void buttonZoomOne_actionPerformed(ActionEvent e)
	{
		view.setZoomFactor(1.0);
		view.redrawCircuit();
	}

	void checkButtons(JToggleButton button, int state)
	{
		if (button == null)
			return;

		if (button.isSelected())
		{
			view.setState(state);

			if (toggleButtonCreateStraightSegment != button)
				toggleButtonCreateStraightSegment.setSelected(false);
			if (toggleButtonCreateLeftSegment != button)
				toggleButtonCreateLeftSegment.setSelected(false);
			if (toggleButtonCreateRightSegment != button)
				toggleButtonCreateRightSegment.setSelected(false);
			if (getMoveButton() != button)
				getMoveButton().setSelected(false);
			if (toggleButtonDelete != button)
				toggleButtonDelete.setSelected(false);
			if (toggleButtonSubdivide != button)
				toggleButtonSubdivide.setSelected(false);
		} else
		{
			view.setState(CircuitView.STATE_NONE);
		}
	}

	void toggleButtonCreateStraightSegment_actionPerformed(ActionEvent e)
	{
		checkButtons(toggleButtonCreateStraightSegment, CircuitView.STATE_CREATE_STRAIGHT);
	}

	void toggleButtonCreateLeftSegment_actionPerformed(ActionEvent e)
	{
		checkButtons(toggleButtonCreateLeftSegment, CircuitView.STATE_CREATE_LEFT_SEGMENT);
	}

	void toggleButtonCreateRightSegment_actionPerformed(ActionEvent e)
	{
		checkButtons(toggleButtonCreateRightSegment, CircuitView.STATE_CREATE_RIGHT_SEGMENT);
	}

	void toggleButtonMoveSegments_actionPerformed(ActionEvent e)
	{
		checkButtons(getMoveButton(), CircuitView.STATE_MOVE_SEGMENTS);
	}

	void toggleButtonDelete_actionPerformed(ActionEvent e)
	{
		checkButtons(toggleButtonDelete, CircuitView.STATE_DELETE);
	}

	void toggleButtonShowArrow_actionPerformed(ActionEvent e)
	{
		//view.showArrows(getShowArrowsButton().isSelected());
	}

	void buttonUndo_actionPerformed(ActionEvent e)
	{
		Undo.undo();
		view.redrawCircuit();
	}

	void buttonRedo_actionPerformed(ActionEvent e)
	{
		Undo.redo();
		view.redrawCircuit();
	}

    void toggleButtonSubdivide_actionPerformed(ActionEvent e)
    {
        checkButtons(toggleButtonSubdivide, CircuitView.STATE_SUBDIVIDE);
    }

    void dumpText_actionPerformed(ActionEvent e)
    {
    	if (trackData == null)
    	{
    		message("No track", "Nothing to dump.");
    		return;
    	}

    	String fileName = Editor.getProperties().getPath();
    	String trackName = fileName.substring(fileName.lastIndexOf(sep) + 1);
    	fileName = fileName + sep + trackName + "-track.txt";
    	try
    	{
    		FileOutputStream stream = new FileOutputStream(fileName);

    		PrintStream printStream = new PrintStream(stream);
    		trackData.getSegments().dump(printStream);
    	}
    	catch (Exception ex)
    	{
    		JOptionPane.showMessageDialog(this,
    				"Couldn't write : " + fileName + "\n\n" + ex.getLocalizedMessage(),
    				"Dump Segment Text", JOptionPane.ERROR_MESSAGE);
    	}
    }

    void dumpAC3D_actionPerformed(ActionEvent e)
    {
    	writeTrack();
    }

    void menuItemAddBackground_actionPerformed(ActionEvent e)
	{
		if (trackData == null)
		{
			message("No track", "Nothing to add to.");
			return;
		}

		Boolean old = UIManager.getBoolean("FileChooser.readOnly");
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle("Background image file selection");
		fc.setVisible(true);
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG images", "png");
		fc.addChoosableFileFilter(filter);
		fc.setCurrentDirectory(new File(Editor.getProperties().getPath()));
		int result = fc.showOpenDialog(this);
		UIManager.put("FileChooser.readOnly", old);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			Path selectedFile = Paths.get(fc.getSelectedFile().toString());
			Path trackPath = Paths.get(Editor.getProperties().getPath());

			// remove directory if same as track directory
			if (selectedFile.getParent().toString().equals(trackPath.toString()))
			{
				selectedFile = selectedFile.getFileName();
			}
			view.setBackgroundImage(selectedFile.toString());
		}
	}

	void menuItemShoStartPoint_actionPerformed(ActionEvent e)
	{
		checkButtons(null, CircuitView.STATE_SHOW_BGRD_START_POSITION);
	}

	void toggleButtonShowBackground_actionPerformed(ActionEvent e)
	{
		view.setShowBackground(getShowBackgroundButton().isSelected());
		view.invalidate();
		view.repaint();
	}

	void toggleButtonShowObjects_actionPerformed(ActionEvent e)
	{
		view.setShowObjects(getShowObjectsButton().isSelected());
		view.invalidate();
		view.repaint();
	}

	void toggleButtonShowReliefs_actionPerformed(ActionEvent e)
	{
		view.setShowReliefs(getShowReliefsButton().isSelected());
		view.invalidate();
		view.repaint();
	}
	/**
	 * This method initializes jToolBar
	 *
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getJToolBar()
	{
		if (jToolBar == null)
		{
			jToolBar = new JToolBar();

			jToolBar.add(getNewButton());
			jToolBar.add(getOpenButton());
			jToolBar.add(getSaveButton());
			jToolBar.add(getPropertiesButton());
			jToolBar.add(getUndoButton());
			jToolBar.add(getRedoButton());
			jToolBar.add(getToggleButtonDelete());
			jToolBar.add(getToggleButtonSubdivide());
			jToolBar.add(getButtonZoomPlus());
			jToolBar.add(getButtonZoomOne());
			jToolBar.add(getButtonZoomMinus());
			jToolBar.add(getToggleButtonCreateStraightSegment());
			jToolBar.add(getToggleButtonCreateRightSegment());
			jToolBar.add(getToggleButtonCreateLeftSegment());
			jToolBar.add(getMoveButton());
			jToolBar.add(getShowArrowsButton());
			jToolBar.add(getShowBackgroundButton());
			jToolBar.add(getShowObjectsButton());
			jToolBar.add(getShowReliefsButton());
			jToolBar.add(getCalculateDeltaButton());
			jToolBar.add(getCheckButton());
			jToolBar.add(getFinishLineToggleButton());
			jToolBar.add(getHelpButton());
		}
		return jToolBar;
	}

	private void exportAc3d(String additionalArgs)
	{
		//check for graphic objects with same name
		Vector<GraphicObject> graphicObjects = trackData.getGraphicObjects();
		for (int i = 0; i < graphicObjects.size() - 1; i++)
		{
			for (int j = i + 1; j < graphicObjects.size(); j++)
			{
				if (graphicObjects.get(i).getName().equals(graphicObjects.get(j).getName()))
				{
					JOptionPane.showMessageDialog(this, "Graphic Objects has duplicate name: " + graphicObjects.get(i).getName(), "Exporting AC3", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		}

		String reliefBorder = getTrackData().getGraphic().getTerrainGeneration().getReliefBorder();
		String newArgs = additionalArgs;
		if (reliefBorder !=  null && reliefBorder.equals("yes"))
		{
			if (newArgs == null)
				newArgs = new String(" -B");
			else
				newArgs += " -B";
		}
		TrackgenPanel tg = new TrackgenPanel(this, newArgs);
		tg.setModal(true);
		tg.setVisible(true);
	}
	/**
	 *
	 */
	protected void propertiesDialog()
	{
		if (trackData != null)
		{
			PropertiesDialog properties = new PropertiesDialog(this);
			properties.setVisible(true);
			if (view.segmentParamDialog != null)
				view.segmentParamDialog.refresh();

			refresh();
		} else
		{
			message("No track", "Properties are not available");
		}
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createNavigationIcon(String imageName)
	{
		String imgLocation = "icons/" + imageName + ".gif";
		java.net.URL imageURL = EditorFrame.class.getResource(imgLocation);

		if (imageURL == null)
		{
			System.err.println("Resource not found: " + imgLocation);
			return null;
		} else
		{
			return new ImageIcon(imageURL);
		}
	}

	/**
	 *
	 */
	private void createActions()
	{
		propertiesAction = new PropertiesAction("Properties", createNavigationIcon("Properties24"), "Properties dialog.", KeyEvent.VK_S);
		undoAction = new UndoAction("Undo", createNavigationIcon("Undo24"), "Undo.", KeyEvent.VK_Z);
		redoAction = new RedoAction("Redo", createNavigationIcon("Redo24"), "Redo.", KeyEvent.VK_R);
		deleteAction = new DeleteAction("Delete", createNavigationIcon("Cut24"), "Delete.", KeyEvent.VK_L);
		subdivideAction = new SubdivideAction("Subdivide", createNavigationIcon("Subdivide24"), "Subdivide.", KeyEvent.VK_Q);
		dumpTextAction = new DumpTextAction("Dump Text", null, "Dump segments to file.", null);
		dumpAC3DAction = new DumpAC3DAction("Dump Ac3d", null, "Dumpsegements to AC3d file.", null);
		zoomPlusAction = new ZoomPlusAction("Zoom in", createNavigationIcon("ZoomIn24"), "Zoom in.", KeyEvent.VK_M);
		zoomOneAction = new ZoomOneAction("Zoom 1:1", createNavigationIcon("Zoom24"), "Zoom 1:1.", KeyEvent.VK_N);
		zoomMinusAction = new ZoomMinusAction("Zoom out", createNavigationIcon("ZoomOut24"), "Zoom out.", KeyEvent.VK_O);
		straightAction = new StraightAction("Add straight", createNavigationIcon("Straight24"), "Add a straight segment.", KeyEvent.VK_P);
		rightAction = new RightAction("Add right", createNavigationIcon("TurnRight24"), "Add a right turn segment.", KeyEvent.VK_Q);
		leftAction = new LeftAction("Add left", createNavigationIcon("TurnLeft24"), "Add a left turn segment.",	KeyEvent.VK_S);
		newAction = new NewAction("New", createNavigationIcon("New24"), "New circuit.", KeyEvent.VK_S);
		openAction = new OpenAction("Open", createNavigationIcon("Open24"), "Open existing circuit.", KeyEvent.VK_S);
		recentAction = new RecentAction("Recent", null, "Recently opened circuits.", KeyEvent.VK_S);
		saveAction = new SaveAction("Save", createNavigationIcon("Save24"), "Save the circuit.", KeyEvent.VK_S);
		moveAction = new MoveAction("Move", createNavigationIcon("Export24"), "Move.", KeyEvent.VK_S);
		showArrowsAction = new ShowArrowsAction("Show arrows", createNavigationIcon("FindAgain24"), "Show arrows.", KeyEvent.VK_S);
		showBackgroundAction = new ShowBackgroundAction("Show background", createNavigationIcon("Search24"), "Show background image.", KeyEvent.VK_S);
		showObjectsAction = new ShowObjectsAction("Show objects", createNavigationIcon("Object24"), "Show objects.", KeyEvent.VK_S);
		showReliefsAction = new ShowReliefsAction("Show reliefs", createNavigationIcon("Relief24"), "Show reliefs.", KeyEvent.VK_S);
		checkAction = new CheckAction("Check", createNavigationIcon("Check24"), "Check.", KeyEvent.VK_S);
		finishLineAction = new FinishLineAction("Finish Line", createNavigationIcon("Finish24"), "Finish Line.", KeyEvent.VK_S);
		helpAction = new HelpAction("Help", createNavigationIcon("Help24"), "Help.", KeyEvent.VK_S);
		/** ******************************************************************* */
		allAction = new ExportAllAction("All", null, "Export both XML file and AC3 file.", null);
		ac3Action = new ExportAC3Action("AC3", null, "Create AC3 file.", null);
		ac3E0Action = new ExportAC3E0Action("AC3 with all elevation maps", null, "Create AC3 file and all elevation maps.", null);
		ac3E1Action = new ExportAC3E1Action("AC3 with terrain and track elevation map", null, "Create AC3 file and terrain and track elevation map.", null);
		ac3E2Action = new ExportAC3E2Action("AC3 with terrain and white track elevation map", null, "Create AC3 file and terrain and white track elevation map.", null);
		ac3E3Action = new ExportAC3E3Action("AC3 with track elevation map", null, "Create AC3 file and track elevation map.", null);
		ac3E4Action = new ExportAC3E4Action("AC3 with track with height steps elevation map", null, "Create AC3 file and track with height steps elevation map.", null);
		ac3RacelineAction = new ExportAC3RacelineAction("AC3 race line", null, "Create AC3 race line file.", null);
		calcDeltaAction = new CalcDeltaAction("Delta's", createNavigationIcon("Calc24"), "Calculate Delta's for x,y,z and angle.", KeyEvent.VK_S);
		importAction = new ImportAction("Speed Dreams", null, "Speed Dreams xml file", null);
		exportAction = new ExportAction("Speed Dreams", null, "Speed Dreams xml file", null);
	}

	public class UndoAction extends AbstractAction
	{
		public UndoAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			buttonUndo_actionPerformed(e);
		}
	}
	public class RedoAction extends AbstractAction
	{
		public RedoAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			buttonRedo_actionPerformed(e);
		}
	}
	public class DeleteAction extends AbstractAction
	{
		public DeleteAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			toggleButtonDelete_actionPerformed(e);
		}
	}
    public class SubdivideAction extends AbstractAction
    {
        public SubdivideAction(String text, ImageIcon icon, String desc, Integer mnemonic)
        {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        public void actionPerformed(final ActionEvent e) {
            toggleButtonSubdivide_actionPerformed(e);
        }
    }

    public class DumpTextAction extends AbstractAction
    {
    	public DumpTextAction(String text, ImageIcon icon, String desc, Integer mnemonic)
    	{
    		super(text, icon);
    		putValue(SHORT_DESCRIPTION, desc);
    		putValue(MNEMONIC_KEY, mnemonic);
    	}

    	public void actionPerformed(final ActionEvent e) {
    		dumpText_actionPerformed(e);
    	}
    }
    public class DumpAC3DAction extends AbstractAction
    {
    	public DumpAC3DAction(String text, ImageIcon icon, String desc, Integer mnemonic)
    	{
    		super(text, icon);
    		putValue(SHORT_DESCRIPTION, desc);
    		putValue(MNEMONIC_KEY, mnemonic);
    	}

    	public void actionPerformed(final ActionEvent e) {
    		dumpAC3D_actionPerformed(e);
    	}
    }

    public class ZoomPlusAction extends AbstractAction
	{
		public ZoomPlusAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			buttonZoomPlus_actionPerformed(e);
		}
	}
	public class ZoomOneAction extends AbstractAction
	{
		public ZoomOneAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			buttonZoomOne_actionPerformed(e);
		}
	}
	public class ZoomMinusAction extends AbstractAction
	{
		public ZoomMinusAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			buttonZoomMinus_actionPerformed(e);
		}
	}
	public class StraightAction extends AbstractAction
	{
		public StraightAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			toggleButtonCreateStraightSegment_actionPerformed(e);
		}
	}
	public class RightAction extends AbstractAction
	{
		public RightAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			toggleButtonCreateRightSegment_actionPerformed(e);
		}
	}
	public class LeftAction extends AbstractAction
	{
		public LeftAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			toggleButtonCreateLeftSegment_actionPerformed(e);
		}
	}
	public class NewAction extends AbstractAction
	{
		public NewAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			newProjectDialog();
		}
	}
	public class OpenAction extends AbstractAction
	{
		public OpenAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			openProject();
		}
	}
	public class RecentAction extends AbstractAction
	{
		public RecentAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
		}
	}

	public class SaveAction extends AbstractAction
	{
		public SaveAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			saveProject();
		}
	}
	public class MoveAction extends AbstractAction
	{
		public MoveAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			toggleButtonMoveSegments_actionPerformed(e);
		}
	}
	public class ShowArrowsAction extends AbstractAction
	{
		public ShowArrowsAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			toggleButtonShowArrow_actionPerformed(e);
		}
	}
	public class ShowBackgroundAction extends AbstractAction
	{
		public ShowBackgroundAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			if (trackData == null)
			{
				if (showBackgroundButton.isSelected())
				{
					showBackgroundButton.setSelected(false);
				}

				message("No track", "Nothing to show.");
				return;
			}
			toggleButtonShowBackground_actionPerformed(e);
		}
	}
	public class ShowObjectsAction extends AbstractAction
	{
		public ShowObjectsAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			if (trackData == null)
			{
				if (showObjectsButton.isSelected())
					showObjectsButton.setSelected(false);

				message("No track", "Nothing to show.");
				return;
			}
			toggleButtonShowObjects_actionPerformed(e);
		}
	}
	public class ShowReliefsAction extends AbstractAction
	{
		public ShowReliefsAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			if (trackData == null)
			{
				if (showReliefsButton.isSelected())
					showReliefsButton.setSelected(false);

				message("No track", "Nothing to show.");
				return;
			}
			toggleButtonShowReliefs_actionPerformed(e);
		}
	}
	public class HelpAction extends AbstractAction
	{
		public HelpAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
		    int type = JOptionPane.PLAIN_MESSAGE;
		    String msg = Editor.getProperties().title + " " + Editor.getProperties().version + "\n\n"
                + "Copyright Charalampos Alexopoulos\n"
                + "Copyright (2022-2023) Robert Reif\n";
		    JOptionPane.showMessageDialog(null,msg,"About",type);
		}
	}
	public class CheckAction extends AbstractAction
	{
		public CheckAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			checkTrack();
		}
	}
	private void checkTrack()
	{
		if (trackData == null)
		{
			message("No track", "Nothing to check");
			return;
		}
		
		CheckDialog checkDialog = new CheckDialog(this);
		checkDialog.setModal(true);
		checkDialog.setVisible(true);
	}

	public class FinishLineAction extends AbstractAction
	{
		public FinishLineAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			finishLine();
		}
	}
	private void finishLine()
	{
		if (trackData == null)
		{
			message("No track", "No track");
			return;
		}
		checkButtons(finishLineToggleButton, CircuitView.STATE_FINISH_LINE);
	}

	private class ExportAction extends AbstractAction
	{
		public ExportAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			exportTrack();
		}
	}
	/**
	 * Writes the track xml.
	 *
	 * @return true if it was written; on failure the user has been shown the error
	 */
	public boolean exportTrack()
	{
		if (trackData == null)
		{
			message("No track", "Nothing to export");
			return false;
		}
		String fileName = Editor.getProperties().getPath();
		String trackName = fileName.substring(fileName.lastIndexOf(sep) + 1);
		fileName = fileName + sep + trackName + ".xml";
		
		//System.out.println("exportTrack writing : " + fileName);
	
		XmlWriter xmlWriter = new XmlWriter(this);
		
		try
		{
			xmlWriter.writeXml(fileName, trackName);
			return true;
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this,
				"Couldn't write : " + fileName + "\n\n" + e.getLocalizedMessage(),
				"Export track", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	public class ExportAllAction extends AbstractAction
	{
		public ExportAllAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			if (trackData == null)
			{
				message("No track", "Nothing to export");
				return;
			}			
			if (exportTrack())
				exportAc3d(null);
		}
	}
	public class ExportAC3Action extends AbstractAction
	{
		public ExportAC3Action(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			if (trackData == null)
			{
				message("No track", "Nothing to export");
				return;
			}			
			exportAc3d(null);
		}
	}
	public class ExportAC3E0Action extends AbstractAction
	{
		public ExportAC3E0Action(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			if (trackData == null)
			{
				message("No track", "Nothing to export");
				return;
			}			
			exportAc3d(" -E 0");
		}
	}
	public class ExportAC3E1Action extends AbstractAction
	{
		public ExportAC3E1Action(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			if (trackData == null)
			{
				message("No track", "Nothing to export");
				return;
			}			
			exportAc3d(" -E 1");
		}
	}
	public class ExportAC3E2Action extends AbstractAction
	{
		public ExportAC3E2Action(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			if (trackData == null)
			{
				message("No track", "Nothing to export");
				return;
			}			
			exportAc3d(" -E 2");
		}
	}
	public class ExportAC3E3Action extends AbstractAction
	{
		public ExportAC3E3Action(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			if (trackData == null)
			{
				message("No track", "Nothing to export");
				return;
			}			
			exportAc3d(" -E 3");
		}
	}
	public class ExportAC3E4Action extends AbstractAction
	{
		public ExportAC3E4Action(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			if (trackData == null)
			{
				message("No track", "Nothing to export");
				return;
			}			
			exportAc3d(" -E 4");
		}
	}
	public class ExportAC3RacelineAction extends AbstractAction
	{
		public ExportAC3RacelineAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			if (trackData == null)
			{
				message("No track", "Nothing to export");
				return;
			}			
			exportAc3d(" -r");
		}
	}
	public class PropertiesAction extends AbstractAction
	{
		public PropertiesAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			propertiesDialog();
		}
	}
	public class CalcDeltaAction extends AbstractAction
	{
		public CalcDeltaAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
		    calculateDeltas();
		}
	}

	public class ImportAction extends AbstractAction
	{
		public ImportAction(String text, ImageIcon icon, String desc, Integer mnemonic)
		{
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e)
		{
			importTrack();
		}
	}

	public void importTrack()
	{
		if (trackData != null)
		{
			if (canClose())
				trackData = null;
		}
		
		Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle("Import track from Xml");
		fc.setVisible(true);
		String trackDir = preferences.get(SD_DATA_DIRECTORY, null);
		if (trackDir == null)
			trackDir = System.getProperty("user.dir");
		fc.setCurrentDirectory(new File(trackDir + sep + "tracks"));
		CustomFileFilter filter = new CustomFileFilter();
		
		filter.addValid(".xml");
		filter.addInvalid(".prj.xml");
		filter.setDescription("*.xml");
		fc.setFileFilter(filter);
		int result = fc.showOpenDialog(this);
		UIManager.put("FileChooser.readOnly", old);
		if (result ==JFileChooser.APPROVE_OPTION)
		{
			String xmlFileName = fc.getSelectedFile().toString();
			String path = xmlFileName;
			path = path.substring(0, path.lastIndexOf(sep));
			Editor.getProperties().setPath(path);
			File file = new File(xmlFileName);
			trackData = null;
			trackData = new TrackData();
			readFile(file);
		}
	}
	
	public void readFile(File file)
	{
		//System.out.println("readFile : " + file.getAbsolutePath());
		
		try
		{
			XmlReader xmlReader = new XmlReader(this);
			
			xmlReader.readXml(file.getAbsolutePath());
			
			Vector<ObjectMap> objectMaps = trackData.getObjectMaps();
			for (int i = 0; i < objectMaps.size(); i++)
		 	{
				Vector<ObjShapeObject> objects = objectMaps.get(i).getObjects();

				for (int j = 0; j < objects.size(); j++)
				{
					objects.get(j).setName(getObjectColorName(objects.get(j).getRGB()));
				}
		 	}			
			
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this,
				"Read file : " + file.getAbsolutePath() + "\n\n" + e.getLocalizedMessage(),
				"Read Track", JOptionPane.ERROR_MESSAGE);				
		}
		refresh();
		
		setTitle(originalTitle + " - Project: " + file.getAbsolutePath());
	}

    /**
     *
     */
    private void calculateDeltas()
    {
		if (trackData == null)
		{
			message("No track", "Can't calculate deltas.");
			return;
		}
		DeltaPanel tg = new DeltaPanel(this, "", false);
		//tg.setModal(false);
		tg.setVisible(true);
    }


	public void refresh()
	{
		view.redrawCircuit();
		this.validate();
		this.repaint();
	}

	public void writeTrack()
	{
		if (trackData == null)
		{
			message("No track", "Can't write track.");
			return;
		}
		String filePath = Editor.getProperties().getPath();
		String trackName = filePath.substring(filePath.lastIndexOf(sep) + 1);
		String filename = Editor.getProperties().getPath() + sep + trackName + "-track.ac";

		Ac3d track = new Ac3d();
		Ac3dMaterial material = new Ac3dMaterial("");

		material.setRgb(new double[] { 0.4, 0.4, 0.4} );
		material.setAmb(new double[] { 0.8, 0.8, 0.8 } );
		material.setEmis(new double[] { 0.4, 0.4, 0.4 } );
		material.setSpec(new double[] { 0.5, 0.5, 0.5 } );
		material.setShi(50);
		material.setTrans(0);

		Ac3dObject world = new Ac3dObject("world", 3);

		track.getMaterials().add(material);
		track.setRoot(world);

		Ac3dObject group = new Ac3dObject("group", 5);
		group.setName("track");
		world.addKid(group);

		for (Segment segment : trackData.getSegments())
		{
			Ac3dObject object = new Ac3dObject("poly", 0);

			object.setName(segment.getName());

			Surface trackSurface = null;

			for (Surface surface : trackData.getSurfaces())
			{
				if (surface.getName().equals(segment.getSurface()))
				{
					trackSurface = surface;
					break;
				}
			}

			if (trackSurface == null)
			{
				for (Surface surface : defaultSurfaces)
				{
					trackSurface = surface;
					break;
				}
			}

			if (trackSurface != null) 
			{
				object.setTexture(trackSurface.getTextureName());
			}

			int steps;
			if (segment.hasProfilSteps())
			{
				steps = segment.getProfilSteps();
			}
			else
			{
				steps = (int) (segment.getLength() / segment.getValidProfilStepsLength(this) + 0.5) + 1;
			}

			int stride = segment.points.length / steps;

			for (int i = 0; i < steps; i++)
			{
				int offset = i * stride;

				if (i == 0)
				{
					object.getVertices().add(new double[] { segment.getPoints()[0].x, segment.getPoints()[0].z, -segment.getPoints()[0].y } );
					object.getVertices().add(new double[] { segment.getPoints()[3].x, segment.getPoints()[3].z, -segment.getPoints()[3].y } );
				}

				object.getVertices().add(new double[] { segment.getPoints()[offset + 1].x, segment.getPoints()[offset + 1].z, -segment.getPoints()[offset + 1].y } );
				object.getVertices().add(new double[] { segment.getPoints()[offset + 2].x, segment.getPoints()[offset + 2].z, -segment.getPoints()[offset + 2].y } );
			}

			for (int i = 0; i < steps; i++)
			{
				for (int j = 0; j < 2; j++)
				{
					Ac3dSurface surface = new Ac3dSurface();

					surface.setPolygon();
					surface.setSingleSided();
					surface.setSmoothShaded();
					surface.setMat(0);

					if (j == 0)
					{
						surface.addRef((i * 2) + 0, 0.0, 0.0);
						surface.addRef((i * 2) + 1, 0.0, 0.0);
						surface.addRef((i * 2) + 2, 0.0, 0.0);
					}
					else
					{
						surface.addRef((i * 2) + 2, 0.0, 0.0);
						surface.addRef((i * 2) + 1, 0.0, 0.0);
						surface.addRef((i * 2) + 3, 0.0, 0.0);
					}
					object.addSurface(surface);
				}
			}

			group.addKid(object);
		}

		try
		{
			track.write(filename);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	//	 Exit when window close

	protected void processWindowEvent(WindowEvent e)
	{
		if (e.getID() == WindowEvent.WINDOW_CLOSING && documentIsModified)
		{
			// close request
			if (!canClose())
			{
				// cancel close request
				return;
			}
		}
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			exit();
		}
	}

	public void message(String title, String msg)
	{
		if (title == null)
		{
			title = "Message";
		}
		JOptionPane.showMessageDialog(this, msg, title, JOptionPane.PLAIN_MESSAGE);
	}

	public void exit()
	{
		if (defaultSurfacesDialog != null)
		{
			getProject().setDefaultSurfacesDialogX(defaultSurfacesDialog.getX());
			getProject().setDefaultSurfacesDialogY(defaultSurfacesDialog.getY());
			defaultSurfacesDialog.setVisible(false);
		}
		
		if (defaultObjectsDialog != null)
		{
			getProject().setDefaultObjectsDialogX(defaultObjectsDialog.getX());
			getProject().setDefaultObjectsDialogY(defaultObjectsDialog.getY());
			defaultObjectsDialog.setVisible(false);
		}
		
		getProject().setFrameX(this.getX());
		getProject().setFrameY(this.getY());
		
		preferences.putInt("X", this.getX());
		preferences.putInt("Y", this.getY());
		preferences.putInt("Width", this.getWidth());
		preferences.putInt("Height", this.getHeight());
		preferences.putInt("SegmentEditorX", getProject().getSegmentEditorX());
		preferences.putInt("SegmentEditorY", getProject().getSegmentEditorY());
		preferences.putInt("PropertiesEditorX", getProject().getPropertiesEditorX());
		preferences.putInt("PropertiesEditorY", getProject().getPropertiesEditorY());
		preferences.putInt("PropertiesEditorTab", getProject().getPropertiesEditorTab());
		preferences.putInt("PropertiesEditorSurfaceTab", getProject().getPropertiesEditorSurfaceTab());
		preferences.putInt("PropertiesEditorObjectTab", getProject().getPropertiesEditorObjectTab());
		preferences.putInt("PropertiesEditorCameraTab", getProject().getPropertiesEditorCameraTab());
		preferences.putInt("PropertiesEditorLightTab", getProject().getPropertiesEditorLightTab());
		preferences.putInt("PropertiesEditorTerrainTab", getProject().getPropertiesEditorTerrainTab());
		preferences.putInt("DefaultSurfacesDialogX", getProject().getDefaultSurfacesDialogX());
		preferences.putInt("DefaultSurfacesDialogY", getProject().getDefaultSurfacesDialogY());
		preferences.putInt("DefaultObjectsDialogX", getProject().getDefaultObjectsDialogX());
		preferences.putInt("DefaultObjectsDialogY", getProject().getDefaultObjectsDialogY());
		preferences.putInt("TrackgenDialogX", getProject().getTrackgenDialogX());
		preferences.putInt("TrackgenDialogY", getProject().getTrackgenDialogY());
		preferences.putInt("PreferencesDialogX", getProject().getPreferencesDialogX());
		preferences.putInt("PreferencesDialogY", getProject().getPreferencesDialogY());
		preferences.putInt("NewProjectDialogX", getProject().getNewProjectDialogX());
		preferences.putInt("NewProjectDialogY", getProject().getNewProjectDialogY());		
		preferences.putInt("CheckDialogX", getProject().getCheckDialogX());
		preferences.putInt("CheckDialogY", getProject().getCheckDialogY());
		preferences.putInt("CheckDialogWidth", getProject().getCheckDialogWidth());
		preferences.putInt("CheckDialogHeight", getProject().getCheckDialogHeight());
		
		System.exit(0);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e)
	{
		System.out.println("Mouse released");
		getProject().setFrameX(this.getX());
		getProject().setFrameY(this.getY());
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowStateListener#windowStateChanged(java.awt.event.WindowEvent)
	 */
	public void windowStateChanged(WindowEvent e)
	{
		// TODO Auto-generated method stub
		System.out.println("windowStateChanged");
	}


	/**
	 * This method initializes calculateDeltaButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCalculateDeltaButton() {
		if (calculateDeltaButton == null) {
			calculateDeltaButton = new JButton();
			calculateDeltaButton.setAction(calcDeltaAction);
			if (calculateDeltaButton.getIcon() != null)
			{
			    calculateDeltaButton.setText("");
			}
		}
		return calculateDeltaButton;
	}
	
	public void clearDefaultSurfacesDialog()
	{
		defaultSurfacesDialog = null;
	}

	public void clearDefaultObjectsDialog()
	{
		defaultObjectsDialog = null;
	}

	public String getObjectColorName(int rgb)
	{
		Vector<TrackObject>	trackObjects = trackData.getTrackObjects();

		for (int i = 0; i < trackObjects.size(); i++)
		{
			if (trackObjects.get(i).getColor() == rgb)
			{
				return trackObjects.get(i).getName();
			}
		}

		for (int i = 0; i < defaultObjects.size(); i++)
		{
			if (defaultObjects.get(i).getColor() == rgb)
			{
				return defaultObjects.get(i).getName();
			}
		}

		return null;
	}

	public Set<String> getObjectColorNames()
	{
		Vector<TrackObject>	trackObjects = trackData.getTrackObjects();
		Set<String> set = new HashSet<String>();

		for (int i = 0; i < trackObjects.size(); i++)
		{
			set.add(trackObjects.get(i).getName());
		}

		for (int i = 0; i < defaultObjects.size(); i++)
		{
			set.add(defaultObjects.get(i).getName());
		}

		return set;
	}

	public int getObjectColor(String name)
	{
		Vector<TrackObject>	trackObjects = trackData.getTrackObjects();

		for (int i = 0; i < trackObjects.size(); i++)
		{
			if (trackObjects.get(i).getName() == name)
			{
				return trackObjects.get(i).getColor();
			}
		}

		for (int i = 0; i < defaultObjects.size(); i++)
		{
			if (defaultObjects.get(i).getName() == name)
			{
				return defaultObjects.get(i).getColor();
			}
		}

		return 0;
	}

	public Segment getSegment(String name)
	{
		return trackData.getSegments().getSegment(name);
	}
	
	public TerrainGeneration getTerrainGeneration()
	{
		return trackData.getTerrainGeneration();
	}
	
	public Vector<ObjectMap> getObjectMaps()
	{
		return trackData.getObjectMaps();
	}
	
	public Vector<GraphicObject> getGraphicObjects()
	{
		return trackData.getGraphicObjects();
	}

	public Reliefs getReliefs()
	{
		return trackData.getReliefs();
	}
	
	public Pits getPits()
	{
		return trackData.getMainTrack().getPits();
	}
}
