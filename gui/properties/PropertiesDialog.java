/*
 *   PropertiesDialog.java
 *   Created on 27 ???2005
 *
 *    The PropertiesDialog.java is part of TrackEditor-0.3.1.
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import gui.EditorFrame;
import utils.GameType;
import utils.Properties;
import utils.undo.Undo;
import utils.undo.UndoProperties;
/**
 * @author babis
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class PropertiesDialog extends JDialog
{
	private EditorFrame				editorFrame;
	public static boolean			APPROVE					= false;
	// Ok or Cancel is closing the dialog, or the pit segments are being checked
	private boolean					closing					= false;
	private boolean					checkingPits			= false;
	private JTabbedPane				tabbedPane				= null;
	private GeneralProperties		generalProperties		= null;
	private JPanel					pane					= null;
	private JPanel					footerPanel				= null;
	private JButton					okButton				= null;
	private JButton					cancelButton			= null;
	private TrackProperties			trackProperties			= null;
	private PitProperties			pitProperties			= null;
	private ImageProperties			imageProperties			= null;
	private TerrainProperties		terrainProperties		= null;
	private TurnMarksProperties		turnMarksProperties		= null;
	private LocalInfoProperties		localInfoProperties		= null;
	private StartingGridProperties	startingGridProperties	= null;
	private GraphicProperties		graphicProperties		= null;
	private Graphic3DProperties		graphic3DProperties		= null;
	private SurfaceProperties		surfaceProperties		= null;
	private EnvMapProperties		envMapProperties		= null;
	private ObjectProperties		objectProperties		= null;
	private CameraProperties		cameraProperties		= null;
	private TrackLightProperties	trackLightProperties	= null;
	private SectorProperties		sectorProperties		= null;
	// for each tab, in order, whether this game mode shows it
	private java.util.List<Boolean>	tabShown				= new java.util.ArrayList<Boolean>();
	// TORCS only
	private GeoreferenceProperties	georeferenceProperties	= null;
	// keeps the tabs with something in red in red, while the dialog is open
	private javax.swing.Timer		tabProblemsTimer		= null;
	private static final String		TAB_PROBLEM_TIP			= "Something on this tab has a problem: see the red text";

	/**
	 *
	 */
	public PropertiesDialog(EditorFrame editorFrame)
	{
		super();
		this.editorFrame = editorFrame;
		initialize();
	}
	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize()
	{
		this.setContentPane(getPane());
		this.setSize(550, 926);
		Point p = editorFrame.getLocation();
		p.x = editorFrame.getProject().getPropertiesEditorX();
		p.y = editorFrame.getProject().getPropertiesEditorY();
		this.setLocation(p);
		this.setModal(true);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		// closing the window is a cancel: pit generation has already changed the track
		this.addWindowListener(new java.awt.event.WindowAdapter()
		{
			@Override
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				cancel();
			}
		});
		this.setResizable(false);
		this.setTitle("Properties");

		// a field turns red, or back, as it is typed in: checked a few times a second
		showTabProblems(getTabbedPane());
		tabProblemsTimer = new javax.swing.Timer(300, e -> showTabProblems(getTabbedPane()));
		tabProblemsTimer.start();
	}

	@Override
	public void dispose()
	{
		if (tabProblemsTimer != null)
			tabProblemsTimer.stop();
		super.dispose();
	}

	/**
	 * Shows each tab, and each tab in a tab, in red while something on it is
	 * red, the way a field, label or list shows a problem.
	 * @return whether something in component is red
	 */
	private static boolean showTabProblems(java.awt.Component component)
	{
		if (component instanceof JTabbedPane)
		{
			JTabbedPane tabs = (JTabbedPane) component;
			boolean any = false;
			for (int i = 0; i < tabs.getTabCount(); i++)
			{
				// a tab that isn't selected is hidden, but its problems are there
				java.awt.Component tab = tabs.getComponentAt(i);
				boolean problem = tab != null && hasProblem(tab);
				any |= problem;
				java.awt.Color color = problem ? java.awt.Color.RED : null;
				if (!java.util.Objects.equals(tabs.getForegroundAt(i), color))
					tabs.setForegroundAt(i, color);
				// a tip of the tab's own, like a camera's, is kept
				String tip = tabs.getToolTipTextAt(i);
				if (problem && tip == null)
					tabs.setToolTipTextAt(i, TAB_PROBLEM_TIP);
				else if (!problem && TAB_PROBLEM_TIP.equals(tip))
					tabs.setToolTipTextAt(i, null);
			}
			return any;
		}
		return component.isVisible() && hasProblem(component);
	}

	/** @return whether component, shown or not, or something in it is red */
	private static boolean hasProblem(java.awt.Component component)
	{
		if (component instanceof JTabbedPane)
			return showTabProblems(component);
		boolean any = java.awt.Color.RED.equals(component.getForeground()) && !(component instanceof javax.swing.JPanel);
		if (component instanceof java.awt.Container)
		{
			// every tab in it is shown, so none stops at the first problem
			for (java.awt.Component child : ((java.awt.Container) component).getComponents())
				any |= showTabProblems(child);
		}
		return any;
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
			// TORCS has no Local Info, Lights or Sectors; only TORCS has a Georeference
			boolean extensions = editorFrame.getGameType().hasSpeedDreamsExtensions();
			addTab("General", getGeneralProperties());
			addTab("Track", getTrackProperties());
			addTab("Pit", getPitProperties());
			addTab("Terrain", getTerrainProperties());
			addTab("Turn Marks", getTurnMarksProperties());
			addTab("Local Info", extensions ? getLocalInfoProperties() : null);
			addTab("Grid", getStartingGridProperties());
			addTab("Graphic", getGraphicProperties());
			addTab("Graphic 3D", getGraphic3DProperties());
			addTab("Env Map", getEnvMapProperties());
			addTab("Surfaces", getSurfaceProperties());
			addTab("Objects", getObjectProperties());
			addTab("Cameras", getCameraProperties());
			// the Pit tab renames segments, and the cameras that use them follow
			getPitProperties().setCamerasChanged(() -> getCameraProperties().reload());
			addTab("Lights", extensions ? getTrackLightProperties() : null);
			addTab("Sectors", extensions ? getSectorProperties() : null);
			addTab("Image", getImageProperties());
			addTab("Georeference", editorFrame.getGameType() == GameType.TORCS ? getGeoreferenceProperties() : null);
			// leaving the Pit tab: its segments have to exist
			tabbedPane.addChangeListener(new javax.swing.event.ChangeListener()
			{
				private java.awt.Component previous = null;

				public void stateChanged(javax.swing.event.ChangeEvent e)
				{
					java.awt.Component selected = tabbedPane.getSelectedComponent();
					if (!closing && !checkingPits && previous == pitProperties && selected != pitProperties)
					{
						checkingPits = true;
						try
						{
							if (!pitProperties.checkSegmentNames(PropertiesDialog.this))
								tabbedPane.setSelectedComponent(pitProperties);
						}
						finally
						{
							checkingPits = false;
						}
					}
					previous = tabbedPane.getSelectedComponent();
				}
			});
			SwingUtilities.invokeLater( new Runnable()
			{
				public void run()
				{
					// the tab last shown may be one this game mode doesn't have
					int tab = toShownTab(editorFrame.getProject().getPropertiesEditorTab());
					if (tab >= 0)
						tabbedPane.setSelectedIndex(tab);
				}
			});
		}
		return tabbedPane;
	}
	/**
	 * Shows the tab with the first title, then in it the tab with the next title and
	 * so on: a tab of the dialog, then a tab inside it (a Terrain tab, a camera, a
	 * surface...). A title that isn't found ends it there.
	 */
	public void showTabs(final String... titles)
	{
		// after the tab last shown has been selected (see getTabbedPane)
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				java.awt.Container container = getContentPane();
				for (String title : titles)
				{
					JTabbedPane tabs = findTabs(container, title);
					if (tabs == null)
						return;
					tabs.setSelectedIndex(tabs.indexOfTab(title));
					if (!(tabs.getSelectedComponent() instanceof java.awt.Container))
						return;
					container = (java.awt.Container) tabs.getSelectedComponent();
				}
			}
		});
	}

	/** @return the tabs in container, the nearest first, that have a tab with the title */
	private static JTabbedPane findTabs(java.awt.Container container, String title)
	{
		java.util.ArrayDeque<java.awt.Component> queue = new java.util.ArrayDeque<java.awt.Component>();
		queue.add(container);
		while (!queue.isEmpty())
		{
			java.awt.Component component = queue.poll();
			if (component instanceof JTabbedPane && ((JTabbedPane) component).indexOfTab(title) >= 0)
				return (JTabbedPane) component;
			if (component instanceof java.awt.Container)
				queue.addAll(java.util.Arrays.asList(((java.awt.Container) component).getComponents()));
		}
		return null;
	}

	/**
	 * This method initializes generalProperties
	 *
	 * @return gui.properties.GeneralProperties
	 */
	private GeneralProperties getGeneralProperties()
	{
		if (generalProperties == null)
		{
			generalProperties = new GeneralProperties(editorFrame);
		}
		return generalProperties;
	}
	/**
	 * This method initializes pane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPane()
	{
		if (pane == null)
		{
			pane = new JPanel();
			pane.setLayout(new BorderLayout());
			pane.add(getTabbedPane(), BorderLayout.CENTER);
			pane.add(getFooterPanel(), BorderLayout.SOUTH);
		}
		return pane;
	}
	/**
	 * This method initializes footerPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getFooterPanel()
	{
		if (footerPanel == null)
		{
			footerPanel = new JPanel();
			footerPanel.setLayout(null);
			footerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
			footerPanel.setPreferredSize(new Dimension(14, 35));
			footerPanel.add(getOkButton(), null);
			footerPanel.add(getCancelButton(), null);
		}
		return footerPanel;
	}
	/**
	 * This method initializes okButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton()
	{
		if (okButton == null)
		{
			okButton = new JButton();
			okButton.setBounds(140, 5, 80, 25);
			okButton.setText("Ok");
			okButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					exit();
				}
			});
		}
		return okButton;
	}
	/**
	 * This method initializes cancelButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton()
	{
		if (cancelButton == null)
		{
			cancelButton = new JButton();
			cancelButton.setBounds(315, 5, 80, 25);
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					cancel();
				}
			});
		}
		return cancelButton;
	}
	/**
	 * This method initializes trackProperties
	 *
	 * @return gui.properties.TrackProperties
	 */
	private TrackProperties getTrackProperties() {
		if (trackProperties == null) {
			trackProperties = new TrackProperties(editorFrame);
		}
		return trackProperties;
	}
	/**
	 * This method initializes pitProperties
	 *
	 * @return gui.properties.PitProperties
	 */
	private PitProperties getPitProperties() {
		if (pitProperties == null) {
			pitProperties = new PitProperties(editorFrame);
		}
		return pitProperties;
	}
	/**
	 * This method initializes imageProperties
	 *
	 * @return gui.properties.ImageProperties
	 */
	private ImageProperties getImageProperties() {
		if (imageProperties == null) {
			imageProperties = new ImageProperties(editorFrame);
		}
		return imageProperties;
	}
	/**
	 * This method initializes turnMarksProperties
	 *
	 * @return gui.properties.TurnMarksProperties
	 */
	private TurnMarksProperties getTurnMarksProperties() {
		if (turnMarksProperties == null) {
			turnMarksProperties = new TurnMarksProperties(editorFrame);
		}
		return turnMarksProperties;
	}
	/**
	 * This method initializes terrainProperties
	 *
	 * @return gui.properties.TerrainProperties
	 */
	private TerrainProperties getTerrainProperties() {
		if (terrainProperties == null) {
			terrainProperties = new TerrainProperties(editorFrame);
		}
		return terrainProperties;
	}
	/**
	 * This method initializes localInfoProperties
	 *
	 * @return gui.properties.LocalInfoProperties
	 */
	private GeoreferenceProperties getGeoreferenceProperties()
	{
		if (georeferenceProperties == null)
			georeferenceProperties = new GeoreferenceProperties(editorFrame);
		return georeferenceProperties;
	}

	private LocalInfoProperties getLocalInfoProperties() {
		if (localInfoProperties == null) {
			localInfoProperties = new LocalInfoProperties(editorFrame);
		}
		return localInfoProperties;
	}
	/**
	 * This method initializes startingGridProperties
	 *
	 * @return gui.properties.StartingGridProperties
	 */
	private StartingGridProperties getStartingGridProperties() {
		if (startingGridProperties == null) {
			startingGridProperties = new StartingGridProperties(editorFrame);
		}
		return startingGridProperties;
	}
	/**
	 * This method initializes graphicProperties
	 *
	 * @return gui.properties.GraphicProperties
	 */
	private GraphicProperties getGraphicProperties() {
		if (graphicProperties == null) {
			graphicProperties = new GraphicProperties(editorFrame);
		}
		return graphicProperties;
	}
	/**
	 * This method initializes graphic3DProperties
	 *
	 * @return gui.properties.GraphiceDProperties
	 */
	private Graphic3DProperties getGraphic3DProperties() {
		if (graphic3DProperties == null) {
			graphic3DProperties = new Graphic3DProperties(editorFrame);
		}
		return graphic3DProperties;
	}
	/**
	 * This method initializes envMapProperties
	 *
	 * @return gui.properties.EnvMapProperties
	 */
	private EnvMapProperties getEnvMapProperties() {
		if (envMapProperties == null) {
			envMapProperties = new EnvMapProperties(editorFrame);
		}
		return envMapProperties;
	}

	/**
	 * This method initializes surfaceProperties
	 *
	 * @return gui.properties.SurfaceProperties
	 */
	private SurfaceProperties getSurfaceProperties() {
		if (surfaceProperties == null) {
			surfaceProperties = new SurfaceProperties(editorFrame, false);
		}
		return surfaceProperties;
	}

	/**
	 * This method initializes objectProperties
	 *
	 * @return gui.properties.ObjectProperties
	 */
	private ObjectProperties getObjectProperties() {
		if (objectProperties == null) {
			objectProperties = new ObjectProperties(editorFrame, false);
		}
		return objectProperties;
	}

	/**
	 * This method initializes cameraProperties
	 *
	 * @return gui.properties.CameraProperties
	 */
	private CameraProperties getCameraProperties() {
		if (cameraProperties == null) {
			cameraProperties = new CameraProperties(editorFrame);
		}
		return cameraProperties;
	}

	/**
	 * This method initializes trackLightProperties
	 *
	 * @return gui.propertiesTraclLightProperties
	 */
	private TrackLightProperties getTrackLightProperties() {
		if (trackLightProperties == null) {
			trackLightProperties = new TrackLightProperties(editorFrame);
		}
		return trackLightProperties;
	}

	/**
	 * This method initializes sectorProperties
	 *
	 * @return gui.properties.SectorProperties
	 */
	private SectorProperties getSectorProperties() {
		if (sectorProperties == null) {
			sectorProperties = new SectorProperties(editorFrame);
		}
		return sectorProperties;
	}

	//	 Exit when window close

	/**
	 * Adds a tab, or only notes its place when the game mode doesn't have it (panel is
	 * null). The tab remembered in the project is its place among all the tabs, so it
	 * stays the same tab in every game mode.
	 */
	private void addTab(String title, javax.swing.JPanel panel)
	{
		tabShown.add(panel != null);
		if (panel != null)
			tabbedPane.addTab(title, null, panel, null);
	}

	/** @return the tab's place among all the tabs */
	private int toAllTabs(int shownTab)
	{
		int shown = -1;
		for (int i = 0; i < tabShown.size(); i++)
		{
			if (tabShown.get(i) && ++shown == shownTab)
				return i;
		}
		return -1;
	}

	/** @return the shown tab at that place among all the tabs, or -1 when it isn't shown */
	private int toShownTab(int tab)
	{
		if (tab < 0 || tab >= tabShown.size() || !tabShown.get(tab))
			return -1;
		int shown = 0;
		for (int i = 0; i < tab; i++)
		{
			if (tabShown.get(i))
				shown++;
		}
		return shown;
	}

	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			editorFrame.getProject().setPropertiesEditorX(this.getX());
			editorFrame.getProject().setPropertiesEditorY(this.getY());
			editorFrame.getProject().setPropertiesEditorTab(toAllTabs(this.tabbedPane.getSelectedIndex()));
			rememberTabs();
		}
	}

	/**
	 *
	 */
	protected void exit()
	{
		// the pit segments have to exist, and the pits should end where they should:
		// fitting the segments is an interactive fix, offered only when those are
		// on in Preferences (Check reports the problem either way)
		if (!this.pitProperties.checkSegmentNames(this) || !this.pitProperties.checkOrder(this)
			|| (editorFrame.getInteractiveFixes() && !this.pitProperties.checkBuildingsEnd(this)))
		{
			getTabbedPane().setSelectedComponent(this.pitProperties);
			return;
		}
		// the checks below can show other tabs
		closing = true;
		try
		{
			exitChecked();
		}
		finally
		{
			closing = false;
		}
	}

	private void exitChecked()
	{
		// a value that isn't a number would be lost, so don't apply anything until it's fixed
		if (!PropertyPanel.checkNumbers(this, getTabbedPane()))
			return;
		// names of cameras, lights, surfaces...
		if (!PropertyPanel.checkNames(this, getTabbedPane()))
			return;
		String pitProblem = this.pitProperties.generateProblem();
		if (pitProblem != null)
		{
			getTabbedPane().setSelectedComponent(this.pitProperties);
			javax.swing.JOptionPane.showMessageDialog(this, pitProblem, "Properties", javax.swing.JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!this.imageProperties.checkScale(this, getTabbedPane()))
			return;
		// cameras on segments that aren't there: picking them is an interactive fix,
		// offered only when those are on in Preferences (Check reports them either way)
		if (editorFrame.getInteractiveFixes() && !this.cameraProperties.checkSegments(this, getTabbedPane()))
			return;
		String georeferenceProblem = this.georeferenceProperties == null ? null : this.georeferenceProperties.problem();
		if (georeferenceProblem != null)
		{
			getTabbedPane().setSelectedComponent(this.georeferenceProperties);
			javax.swing.JOptionPane.showMessageDialog(this, georeferenceProblem, "Properties", javax.swing.JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Ok is one undo step; converting the track's version clears the undo list
		UndoProperties undo = new UndoProperties(editorFrame);
		int version = editorFrame.getTrackData().getHeader().getVersion();

		this.generalProperties.exit();
		this.trackProperties.exit();
		this.pitProperties.exit();
		this.imageProperties.exit();
		this.terrainProperties.exit();
		this.turnMarksProperties.exit();
		if (this.localInfoProperties != null)
			this.localInfoProperties.exit();
		this.startingGridProperties.exit();
		this.graphicProperties.exit();
		this.graphic3DProperties.exit();
		this.envMapProperties.exit();
		this.surfaceProperties.exit();
		this.objectProperties.exit();
		this.cameraProperties.exit();
		if (this.trackLightProperties != null)
			this.trackLightProperties.exit();
		if (this.sectorProperties != null)
			this.sectorProperties.exit();
		if (this.georeferenceProperties != null)
			this.georeferenceProperties.exit();

		undo.captureAfter();
		if (version == editorFrame.getTrackData().getHeader().getVersion() && undo.changed())
			Undo.add(undo);

		Properties.getInstance().valueChanged();
		APPROVE = true;
		cancel();
	}

	/**
	 *
	 */
	protected void cancel()
	{
		closing = true;
		// the Pit panel changes the track as soon as Apply is pressed, so it has
		// to put it back. On Ok this does nothing: exit() has already accepted
		// the changes by clearing the state cancel restores.
		this.pitProperties.cancel();
		editorFrame.setGraphicObjectProperties(null);
		editorFrame.getProject().setPropertiesEditorX(this.getX());
		editorFrame.getProject().setPropertiesEditorY(this.getY());
		editorFrame.getProject().setPropertiesEditorTab(toAllTabs(this.tabbedPane.getSelectedIndex()));
		rememberTabs();
		this.dispose();
	}

	/**
	 * Keeps the tabs selected inside the tabs for the next time
	 */
	private void rememberTabs()
	{
		this.terrainProperties.rememberTab();
		this.surfaceProperties.rememberTab();
		this.objectProperties.rememberTab();
		this.cameraProperties.rememberTab();
		// not in TORCS
		if (this.trackLightProperties != null)
			this.trackLightProperties.rememberTab();
	}
} //  @jve:decl-index=0:visual-constraint="6,6"
