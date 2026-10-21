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
import utils.Editor;
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
		this.setSize(550, 893);
		Point p = editorFrame.getLocation();
		p.x = editorFrame.getProject().getPropertiesEditorX();
		p.y = editorFrame.getProject().getPropertiesEditorY();
		this.setLocation(p);
		this.setModal(true);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setTitle("Properties");
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
			tabbedPane.addTab("General", null, getGeneralProperties(), null);
			tabbedPane.addTab("Track", null, getTrackProperties(), null);
			tabbedPane.addTab("Pit", null, getPitProperties(), null);
			tabbedPane.addTab("Terrain", null, getTerrainProperties(), null);
			tabbedPane.addTab("Turn Marks", null, getTurnMarksProperties(), null);
			tabbedPane.addTab("Local Info", null, getLocalInfoProperties(), null);
			tabbedPane.addTab("Grid", null, getStartingGridProperties(), null);
			tabbedPane.addTab("Graphic", null, getGraphicProperties(), null);
			tabbedPane.addTab("Graphic 3D", null, getGraphic3DProperties(), null);
			tabbedPane.addTab("Env Map", null, getEnvMapProperties(), null);
			tabbedPane.addTab("Surfaces", null, getSurfaceProperties(), null);
			tabbedPane.addTab("Objects", null, getObjectProperties(), null);
			tabbedPane.addTab("Cameras", null, getCameraProperties(), null);
			tabbedPane.addTab("Lights", null, getTrackLightProperties(), null);
			tabbedPane.addTab("Sectors", null, getSectorProperties(), null);			
			tabbedPane.addTab("Image", null, getImageProperties(), null);			
			SwingUtilities.invokeLater( new Runnable()
			{
				public void run()
				{
					tabbedPane.setSelectedIndex(editorFrame.getProject().getPropertiesEditorTab());
				}
			});
		}
		return tabbedPane;
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

	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			editorFrame.getProject().setPropertiesEditorX(this.getX());
			editorFrame.getProject().setPropertiesEditorY(this.getY());
			editorFrame.getProject().setPropertiesEditorTab(this.tabbedPane.getSelectedIndex());
		}
	}

	/**
	 *
	 */
	protected void exit()
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

		this.generalProperties.exit();
		this.trackProperties.exit();
		this.pitProperties.exit();
		this.imageProperties.exit();
		this.terrainProperties.exit();
		this.turnMarksProperties.exit();
		this.localInfoProperties.exit();
		this.startingGridProperties.exit();
		this.graphicProperties.exit();
		this.graphic3DProperties.exit();
		this.envMapProperties.exit();
		this.surfaceProperties.exit();
		this.objectProperties.exit();
		this.cameraProperties.exit();
		this.trackLightProperties.exit();
		this.sectorProperties.exit();
		Editor.getProperties().valueChanged();
		APPROVE = true;
		cancel();
	}

	/**
	 *
	 */
	protected void cancel()
	{
		editorFrame.setGraphicObjectProperties(null);
		editorFrame.getProject().setPropertiesEditorX(this.getX());
		editorFrame.getProject().setPropertiesEditorY(this.getY());
		editorFrame.getProject().setPropertiesEditorTab(this.tabbedPane.getSelectedIndex());
		this.dispose();
	}
} //  @jve:decl-index=0:visual-constraint="6,6"
