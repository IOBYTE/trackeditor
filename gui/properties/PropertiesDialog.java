/*
 *   PropertiesDialog.java
 *   Created on 27 ��� 2005
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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import gui.EditorFrame;
/**
 * @author babis
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class PropertiesDialog extends JDialog
{
	//private Properties properties = Properties.getInstance();
	private EditorFrame				frame;
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

	/**
	 *  
	 */
	public PropertiesDialog(EditorFrame frame)
	{
		super();
		this.frame = frame;
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
		this.setSize(440, 455);
		this.setModal(true);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setTitle("Properties");
		this.getPane().setSize(447, 321);
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
			tabbedPane.addTab("General", null, getGeneralProperties(), null);
			tabbedPane.addTab("Track", null, getTrackProperties(), null);
			tabbedPane.addTab("Pit", null, getPitProperties(), null);
			tabbedPane.addTab("Terrain", null, getTerrainProperties(), null);
			tabbedPane.addTab("Turn Marks", null, getTurnMarksProperties(), null);
			tabbedPane.addTab("Local Info", null, getLocalInfoProperties(), null);
			tabbedPane.addTab("Grid", null, getStartingGridProperties(), null);
			tabbedPane.addTab("Graphic", null, getGraphicProperties(), null);
			tabbedPane.addTab("Image", null, getImageProperties(), null);
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
			generalProperties = new GeneralProperties(frame);
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
			pane.add(getTabbedPane(), java.awt.BorderLayout.CENTER);
			pane.add(getFooterPanel(), java.awt.BorderLayout.SOUTH);
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
			footerPanel.setPreferredSize(new java.awt.Dimension(14, 35));
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
			okButton.setBounds(120, 5, 80, 25);
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
			cancelButton.setBounds(250, 5, 80, 25);
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
			trackProperties = new TrackProperties();
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
			pitProperties = new PitProperties();
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
			imageProperties = new ImageProperties();
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
			turnMarksProperties = new TurnMarksProperties();
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
			terrainProperties = new TerrainProperties();
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
			localInfoProperties = new LocalInfoProperties();
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
			startingGridProperties = new StartingGridProperties();
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
			graphicProperties = new GraphicProperties(frame);
		}
		return graphicProperties;
	}
	
	/**
	 *  
	 */
	protected void exit()
	{
		this.generalProperties.exit();
		this.trackProperties.exit();
		this.pitProperties.exit();
		this.imageProperties.exit();
		this.terrainProperties.exit();
		this.turnMarksProperties.exit();
		this.localInfoProperties.exit();
		this.startingGridProperties.exit();
		this.graphicProperties.exit();
		APPROVE = true;
		cancel();
	}
	
	/**
	 *  
	 */
	protected void cancel()
	{
		this.dispose();
	}
   } //  @jve:decl-index=0:visual-constraint="6,6"
