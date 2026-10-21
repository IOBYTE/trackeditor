/*
 *   TrackProperties.java
 *   Created on 27 ??? 2005
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.MutableDouble;
import utils.MutableString;
import utils.SurfaceComboBox;
import utils.circuit.SegmentSide;
import utils.circuit.Surface;

/**
 * @author babis
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TrackProperties extends PropertyPanel
{
	private JLabel				widthLabel					= new JLabel();
	private JTextField			widthTextField				= new JTextField();
	private JLabel				surfaceLabel				= new JLabel();
	private SurfaceComboBox		surfaceComboBox				= null;
	private JLabel				profilLabel					= new JLabel();
	private JComboBox<String>	profilComboBox				= null;
	private JLabel				profilStepsLengthLabel		= new JLabel();
	private JTextField			profilStepsLengthTextField	= new JTextField();
	private JLabel				racelineWidthscaleLabel		= new JLabel();
	private JTextField			racelineWidthscaleTextField	= new JTextField();
	private JLabel				racelineIntLabel			= new JLabel();
	private JTextField			racelineIntTextField		= new JTextField();
	private JLabel				racelineExtLabel			= new JLabel();
	private JTextField			racelineExtTextField		= new JTextField();
	private JTabbedPane			tabbedPane					= null;

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
	private Vector<String>		roadSurfaceVector			= new Vector<String>(Arrays.asList(roadSurfaceItems));

	private String[]			borderStyleItems			= {"none", "plan", "curb", "wall"};
	
	private String[]			borderSurfaceItems			=
	{"curb-5cm-r", "curb-5cm-l", "curb-l", "tar-grass3-l",
	 "tar-grass3-r", "tar-sand", "b-road1-grass6", "b-road1-grass6-l2", "b-road1-gravel-l2", "b-road1-sand3",
	 "b-road1-sand3-l2", "b-asphalt-grass7", "b-asphalt-grass7-l1", "b-asphalt-grass6", "b-asphalt-grass6-l1",
	 "b-asphalt-sand3", "b-asphalt-sand3-l1", "grass", "grass3", "grass5", "grass6", "grass7", "gravel",
	 "sand3", "sand", "asphalt-lines", "asphalt-l-left", "asphalt-l-right", "asphalt-l-both", "asphalt-pits",
	 "asphalt", "dirt", "dirt-b", "asphalt2", "road1", "road1-pits", "road1-asphalt", "asphalt-road1",
	 "b-road1", "b-road1-l2", "b-road1-l2p", "concrete", "concrete2", "concrete3", "b-asphalt", "b-asphalt-l1",
	 "b-asphalt-l1p", "asphalt2-lines", "asphalt2-l-left", "asphalt2-l-right", "asphalt2-l-both", "barrier",
	 "barrier2", "barrier-turn", "barrier-grille", "wall", "wall2", "tire-wall"};
	private Vector<String>		borderSurfaceVector			= new Vector<String>(Arrays.asList(borderSurfaceItems));
	private String[]			sideSurfaceItems			=
	{"grass", "grass3", "grass5", "grass6", "grass7", "gravel",
	 "sand3", "sand", "asphalt-lines", "asphalt-l-left", "asphalt-l-right", "asphalt-l-both", "asphalt-pits",
	 "asphalt", "dirt", "dirt-b", "asphalt2", "road1", "road1-pits", "road1-asphalt", "asphalt-road1",
	 "b-road1", "b-road1-l2", "b-road1-l2p", "concrete", "concrete2", "concrete3", "b-asphalt", "b-asphalt-l1",
	 "b-asphalt-l1p", "asphalt2-lines", "asphalt2-l-left", "asphalt2-l-right", "asphalt2-l-both", "curb-5cm-r",
	 "curb-5cm-l", "curb-l", "tar-grass3-l", "tar-grass3-r", "tar-sand", "b-road1-grass6", "b-road1-grass6-l2",
	 "b-road1-gravel-l2", "b-road1-sand3", "b-road1-sand3-l2", "b-asphalt-grass7", "b-asphalt-grass7-l1",
	 "b-asphalt-grass6", "b-asphalt-grass6-l1", "b-asphalt-sand3", "b-asphalt-sand3-l1", "barrier", "barrier2",
	 "barrier-turn", "barrier-grille", "wall", "wall2", "tire-wall"};
	private Vector<String>		sideSurfaceVector			= new Vector<String>(Arrays.asList(sideSurfaceItems));
	private String[]			barrierStyleItems			= {"none", "fence", "fence1", "fence2", "wall"};
	// CSR has no fence1/fence2 (they are written as fence)
	private String[]			csrBarrierStyleItems		= {"none", "fence", "wall"};
	private String[]			barrierSurfaceItems			=
	{"barrier", "barrier2", "barrier-turn", "barrier-grille",
	 "wall", "wall2", "tire-wall", "asphalt-lines", "asphalt-l-left", "asphalt-l-right", "asphalt-l-both",
	 "asphalt-pits", "asphalt", "dirt", "dirt-b", "asphalt2", "road1", "road1-pits", "road1-asphalt",
	 "asphalt-road1", "b-road1", "b-road1-l2", "b-road1-l2p", "concrete", "concrete2", "concrete3", "b-asphalt",
	 "b-asphalt-l1", "b-asphalt-l1p", "asphalt2-lines", "asphalt2-l-left", "asphalt2-l-right",
	 "asphalt2-l-both", "curb-5cm-r", "curb-5cm-l", "curb-l", "tar-grass3-l", "tar-grass3-r", "tar-sand",
	 "b-road1-grass6", "b-road1-grass6-l2", "b-road1-gravel-l2", "b-road1-sand3", "b-road1-sand3-l2",
	 "b-asphalt-grass7", "b-asphalt-grass7-l1", "b-asphalt-grass6", "b-asphalt-grass6-l1", "b-asphalt-sand3",
	 "b-asphalt-sand3-l1", "grass", "grass3", "grass5", "grass6", "grass7", "gravel", "sand3", "sand"};
	private Vector<String>		barrierSurfaceVector		= new Vector<String>(Arrays.asList(barrierSurfaceItems));

	/**
	 *
	 */
	/**
	 * @return the barrier styles for the current game type. A CSR track never gets
	 * fence1/fence2, but a style already set (read from a Speed Dreams track) stays
	 * listed so opening the dialog doesn't change it.
	 */
	private String[] getBarrierStyleItems(String currentStyle)
	{
		if (!getEditorFrame().getCarsSportsRacing())
			return barrierStyleItems;

		if (currentStyle != null && (currentStyle.equals("fence1") || currentStyle.equals("fence2")))
		{
			String[] items = java.util.Arrays.copyOf(csrBarrierStyleItems, csrBarrierStyleItems.length + 1);
			items[items.length - 1] = currentStyle;
			return items;
		}

		return csrBarrierStyleItems;
	}

	public TrackProperties(EditorFrame editorFrame)
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
		addDefaultSurfaces(sideSurfaceVector);
		addDefaultSurfaces(borderSurfaceVector);
		addDefaultSurfaces(barrierSurfaceVector);
		
		this.setLayout(null);
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));

		addLabel(this, 0, widthLabel, "Width", 140);
		addLabel(this, 1, surfaceLabel, "Surface", 140);
		addLabel(this, 2, profilLabel, "Profil", 140);
		addLabel(this, 3, profilStepsLengthLabel, "Profil Steps Length", 140);
		addLabel(this, 4, racelineWidthscaleLabel, "Raceline Width Scale", 140);
		addLabel(this, 5, racelineIntLabel, "Raceline Int", 140);
		addLabel(this, 6, racelineExtLabel, "Raceline Ext", 140);

		addTextField(this, 0, widthTextField, getEditorFrame().getTrackData().getMainTrack().getWidth(), 150, 125);

		this.add(getSurfaceComboBox(), null);
		this.add(getProfilComboBox(), null);

		addTextField(this, 3, profilStepsLengthTextField, getEditorFrame().getTrackData().getMainTrack().getProfilStepsLength(), 150, 125);
		addTextField(this, 4, racelineWidthscaleTextField, getEditorFrame().getTrackData().getMainTrack().getRacelineWidthscale(), 150, 125);
		addTextField(this, 5, racelineIntTextField, getEditorFrame().getTrackData().getMainTrack().getRacelineInt(), 150, 125);
		addTextField(this, 6, racelineExtTextField, getEditorFrame().getTrackData().getMainTrack().getRacelineExt(), 150, 125);

		this.add(getTabbedPane(), null);
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
	 * This method initializes surfaceComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private SurfaceComboBox getSurfaceComboBox()
	{
		if (surfaceComboBox == null)
		{
			String newSurface = getEditorFrame().getTrackData().getMainTrack().getSurface();
			addSurface(roadSurfaceVector, newSurface);			
			surfaceComboBox = new SurfaceComboBox(getEditorFrame(), roadSurfaceVector);
			surfaceComboBox.setBounds(150, 37, 200, 23);
			surfaceComboBox.setSelectedItem(newSurface);
		}
		return surfaceComboBox;
	}

	/**
	 * This method initializes profilComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getProfilComboBox()
	{
		if (profilComboBox == null)
		{
			String[] items = {"none", "linear", "spline"};
			profilComboBox = new JComboBox<String>(items);
			profilComboBox.setBounds(150, 64, 125, 23);						
			String profil = getEditorFrame().getTrackData().getMainTrack().getProfil();
			if (profil == null || profil.isEmpty())
				profil = "none";
			selectItem(profilComboBox, profil);
		}
		return profilComboBox;
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
			tabbedPane.setBounds(10, 205, 510, 370);

			tabbedPane.addTab("Left", null, new SidePanel(getEditorFrame().getTrackData().getMainTrack().getLeft()), null);
			tabbedPane.addTab("Right", null, new SidePanel(getEditorFrame().getTrackData().getMainTrack().getRight()), null);
		}
		return tabbedPane;
	}

	private class SidePanel extends JPanel
	{
		private SegmentSide 		side;
		private JLabel				sideStartWidthLabel			= new JLabel();
		private JTextField 			sideStartWidthTextField		= new JTextField();
		private JLabel				sideEndWidthLabel			= new JLabel();
		private JTextField 			sideEndWidthTextField		= new JTextField();
		private JLabel				sideSurfaceLabel			= new JLabel();
		private SurfaceComboBox		sideSurfaceComboBox			= null;
		private JLabel				sideBankingTypeLabel		= new JLabel();
		private JComboBox<String>	sideBankingTypeComboBox		= null;
		private JLabel				borderWidthLabel			= new JLabel();
		private JTextField			borderWidthTextField		= new JTextField();
		private JLabel				borderHeightLabel			= new JLabel();
		private JTextField			borderHeightTextField		= new JTextField();
		private JLabel				borderSurfaceLabel			= new JLabel();
		private SurfaceComboBox		borderSurfaceComboBox		= null;
		private JLabel				borderStyleLabel			= new JLabel();
		private JComboBox<String>	borderStyleComboBox			= null;
		private JLabel				barrierWidthLabel			= new JLabel();
		private JTextField			barrierWidthTextField		= new JTextField();
		private JLabel				barrierHeightLabel			= new JLabel();
		private JTextField			barrierHeightTextField		= new JTextField();
		private JLabel				barrierSurfaceLabel			= new JLabel();
		private SurfaceComboBox		barrierSurfaceComboBox		= null;
		private JLabel				barrierStyleLabel			= new JLabel();
		private JComboBox<String>	barrierStyleComboBox		= null;
		private boolean				initialized					= false;

		/**
		 *
		 */
		public SidePanel(SegmentSide side)
		{
			super();
			this.side = side;
			initialize();
		}

		/**
		 *
		 */
		private void initialize()
		{
			setLayout(null);
			
			addLabel(this, 0, sideStartWidthLabel, "Side Start Width", 140);
			addLabel(this, 1, sideEndWidthLabel, "Side End Width", 140);
			addLabel(this, 2, sideSurfaceLabel, "Side Banking Surface", 140);
			addLabel(this, 3, sideBankingTypeLabel, "Side Type", 140);
			addLabel(this, 4, borderWidthLabel, "Border Width", 140);
			addLabel(this, 5, borderHeightLabel, "Border Height", 140);
			addLabel(this, 6, borderSurfaceLabel, "Border Surface", 140);
			addLabel(this, 7, borderStyleLabel, "Border Style", 140);
			addLabel(this, 8, barrierWidthLabel, "Barrier Width", 140);
			addLabel(this, 9, barrierHeightLabel, "Barrier Height", 140);
			addLabel(this, 10, barrierSurfaceLabel, "Barrier Surface", 140);
			addLabel(this, 11, barrierStyleLabel, "Barrier Style", 140);
			
			addTextField(this, 0, sideStartWidthTextField, side.getSideStartWidth(), 150, 125);
			addTextField(this, 1, sideEndWidthTextField, side.getSideEndWidth(), 150, 125);

			add(getSideSurfaceComboBox(), null);
			add(getSideBankingTypeComboBox(), null);

			addTextField(this, 4, borderWidthTextField, side.getBorderWidth(), 150, 125);
			addTextField(this, 5, borderHeightTextField, side.getBorderHeight(), 150, 125);

			add(getBorderSurfaceComboBox(), null);
			add(getBorderStyleComboBox(), null);

			addTextField(this, 8, barrierWidthTextField, side.getBarrierWidth(), 150, 125);
			addTextField(this, 9, barrierHeightTextField, side.getBarrierHeight(), 150, 125);

			add(getBarrierSurfaceComboBox(), null);
			add(getBarrierStyleComboBox(), null);

			switch (borderStyleComboBox.getSelectedItem().toString())
			{
			case "none":
				borderWidthTextField.setEnabled(false);
				borderHeightTextField.setEnabled(false);
				borderSurfaceComboBox.setEnabled(false);
				break;
			case "plan":
				borderWidthTextField.setEnabled(true);
				borderHeightTextField.setEnabled(false);
				borderSurfaceComboBox.setEnabled(true);
				break;
			case "wall":
				borderWidthTextField.setEnabled(true);
				borderHeightTextField.setEnabled(true);
				borderSurfaceComboBox.setEnabled(true);
				break;
			case "curb":
				borderWidthTextField.setEnabled(true);
				borderHeightTextField.setEnabled(true);
				borderSurfaceComboBox.setEnabled(true);
				break;
			}
			switch (barrierStyleComboBox.getSelectedItem().toString())
			{
			case "none":
				barrierWidthTextField.setEnabled(false);
				barrierHeightTextField.setEnabled(false);
				barrierSurfaceComboBox.setEnabled(false);
				break;
			case "fence":
			case "fence1":
			case "fence2":
				barrierWidthTextField.setEnabled(false);
				barrierHeightTextField.setEnabled(true);
				barrierSurfaceComboBox.setEnabled(true);
				break;
			case "wall":
				barrierWidthTextField.setEnabled(true);
				barrierHeightTextField.setEnabled(true);
				barrierSurfaceComboBox.setEnabled(true);
				break;
			}
			
			initialized = true;
		}
		
		/**
		 * This method initializes sideSurfaceComboBox
		 *
		 * @return utils.SurfaceComboBox
		 */
		private SurfaceComboBox getSideSurfaceComboBox()
		{
			if (sideSurfaceComboBox == null)
			{
				addSurface(sideSurfaceVector, side.getSideSurface());
				sideSurfaceComboBox = new SurfaceComboBox(getEditorFrame(), sideSurfaceVector);
				sideSurfaceComboBox.setBounds(150, 64, 200, 23);
				sideSurfaceComboBox.setSelectedItem(side.getSideSurface());
				sideSurfaceComboBox.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						// the track changes on Ok (exit), so Cancel leaves it alone
					}
				});
			}
			return sideSurfaceComboBox;
		}

		/**
		 * This method initializes sideBankingTypeComboBox
		 *
		 * @return javax.swing.JComboBox
		 */
		private JComboBox<String> getSideBankingTypeComboBox()
		{
			if (sideBankingTypeComboBox == null)
			{
				String[] items = {"none", "level", "tangent"};
				sideBankingTypeComboBox = new JComboBox<String>();
				sideBankingTypeComboBox.setBounds(150, 91, 125, 23);
				sideBankingTypeComboBox.setModel(new DefaultComboBoxModel<String>(items));
				String type = side.getSideBankingType();
				if (type == null || type.isEmpty())
					type = "none";
				selectItem(sideBankingTypeComboBox, type);
				sideBankingTypeComboBox.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						// the track changes on Ok (exit), so Cancel leaves it alone
					}
				});
			}
			return sideBankingTypeComboBox;
		}

		/**
		 * This method initializes borderSurfaceComboBox
		 *
		 * @return utils.SurfaceComboBox
		 */
		private SurfaceComboBox getBorderSurfaceComboBox()
		{
			if (borderSurfaceComboBox == null)
			{
				addSurface(borderSurfaceVector, side.getBorderSurface());
				borderSurfaceComboBox = new SurfaceComboBox(getEditorFrame(), borderSurfaceVector);
				borderSurfaceComboBox.setBounds(150, 172, 200, 23);
				borderSurfaceComboBox.setSelectedItem(side.getBorderSurface());
				borderSurfaceComboBox.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						// the track changes on Ok (exit), so Cancel leaves it alone
					}
				});
			}
			return borderSurfaceComboBox;
		}

		/**
		 * This method initializes borderStyleComboBox
		 *
		 * @return javax.swing.JComboBox
		 */
		private JComboBox<String> getBorderStyleComboBox()
		{
			if (borderStyleComboBox == null)
			{
				borderStyleComboBox = new JComboBox<String>();
				borderStyleComboBox.setBounds(150, 199, 125, 23);
				borderStyleComboBox.setModel(new DefaultComboBoxModel<String>(borderStyleItems));
				String style = side.getBorderStyle();
				selectItem(borderStyleComboBox, style);
				borderStyleComboBox.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						// the track changes on Ok (exit), so Cancel leaves it alone
						
						if (!initialized)
							return;

						switch (borderStyleComboBox.getSelectedItem().toString())
						{
						case "none":
							borderWidthTextField.setText(null);
							borderWidthTextField.setEnabled(false);
							borderHeightTextField.setText(null);
							borderHeightTextField.setEnabled(false);
							borderSurfaceComboBox.setEnabled(false);
							borderSurfaceComboBox.setSelectedIndex(-1);
							break;
						case "plan":
							borderWidthTextField.setEnabled(true);
							borderWidthTextField.setText(Double.toString(SegmentSide.DEFAULT_BORDER_PLAN_WIDTH));
							borderHeightTextField.setEnabled(false);
							borderHeightTextField.setText(Double.toString(SegmentSide.DEFAULT_BORDER_PLAN_HEIGHT));
							borderSurfaceComboBox.setEnabled(true);
							if (side.isRight())
							{
								borderSurfaceComboBox.setSelectedItem(SegmentSide.DEFAULT_BORDER_PLAN_RIGHT_SURFACE);
							}
							else
							{
								borderSurfaceComboBox.setSelectedItem(SegmentSide.DEFAULT_BORDER_PLAN_LEFT_SURFACE);
							}
							break;
						case "wall":
							borderWidthTextField.setEnabled(true);
							borderWidthTextField.setText(Double.toString(SegmentSide.DEFAULT_BORDER_WALL_WIDTH));
							borderHeightTextField.setEnabled(true);
							borderHeightTextField.setText(Double.toString(SegmentSide.DEFAULT_BORDER_WALL_HEIGHT));
							borderSurfaceComboBox.setEnabled(true);
							borderSurfaceComboBox.setSelectedItem(SegmentSide.DEFAULT_BORDER_WALL_SURFACE);
							break;
						case "curb":
							borderWidthTextField.setEnabled(true);
							borderWidthTextField.setText(Double.toString(SegmentSide.DEFAULT_BORDER_CURB_WIDTH));
							borderHeightTextField.setEnabled(true);
							borderHeightTextField.setText(Double.toString(SegmentSide.DEFAULT_BORDER_CURB_HEIGHT));
							borderSurfaceComboBox.setEnabled(true);
							if (side.isRight())
							{
								borderSurfaceComboBox.setSelectedItem(SegmentSide.DEFAULT_BORDER_CURB_RIGHT_SURFACE);
							}
							else
							{
								borderSurfaceComboBox.setSelectedItem(SegmentSide.DEFAULT_BORDER_CURB_LEFT_SURFACE);
							}
							break;
						}
					}
				});
			}
			return borderStyleComboBox;
		}

		/**
		 * This method initializes barrierSurfaceComboBox
		 *
		 * @return utils.SurfaceComboBox
		 */
		private SurfaceComboBox getBarrierSurfaceComboBox()
		{
			if (barrierSurfaceComboBox == null)
			{
				addSurface(barrierSurfaceVector, side.getBarrierSurface());
				barrierSurfaceComboBox = new SurfaceComboBox(getEditorFrame(), barrierSurfaceVector);
				barrierSurfaceComboBox.setBounds(150, 280, 200, 23);
				barrierSurfaceComboBox.setSelectedItem(side.getBarrierSurface());
				barrierSurfaceComboBox.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						// the track changes on Ok (exit), so Cancel leaves it alone
					}
				});
			}
			return barrierSurfaceComboBox;
		}

		/**
		 * This method initializes barrierStyleComboBox
		 *
		 * @return javax.swing.JComboBox
		 */
		private JComboBox<String> getBarrierStyleComboBox()
		{
			if (barrierStyleComboBox == null)
			{
				barrierStyleComboBox = new JComboBox<String>();
				barrierStyleComboBox.setBounds(150, 307, 125, 23);
				String style = side.getBarrierStyle();
				if (style == null || style.isEmpty())
					style = "none";
				barrierStyleComboBox.setModel(new DefaultComboBoxModel<String>(getBarrierStyleItems(style)));
				selectItem(barrierStyleComboBox, style);
				barrierStyleComboBox.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						// the track changes on Ok (exit), so Cancel leaves it alone
						
						if (!initialized)
							return;

						switch (barrierStyleComboBox.getSelectedItem().toString())
						{
						case "none":
							barrierWidthTextField.setEnabled(false);
							barrierWidthTextField.setText(null);
							barrierHeightTextField.setText(null);
							barrierHeightTextField.setEnabled(false);
							barrierSurfaceComboBox.setEnabled(false);
							barrierSurfaceComboBox.setSelectedIndex(-1);
							break;
						case "fence":
						case "fence1":
						case "fence2":
							barrierWidthTextField.setEnabled(false);
							barrierWidthTextField.setText(Double.toString(SegmentSide.DEFAULT_BARRIER_FENCE_WIDTH));
							barrierHeightTextField.setEnabled(true);
							barrierHeightTextField.setText(Double.toString(SegmentSide.DEFAULT_BARRIER_FENCE_HEIGHT));
							barrierSurfaceComboBox.setEnabled(true);
							barrierSurfaceComboBox.setSelectedItem(SegmentSide.DEFAULT_BARRIER_FENCE_SURFACE);
							break;
						case "wall":
							barrierWidthTextField.setEnabled(true);
							barrierWidthTextField.setText(Double.toString(SegmentSide.DEFAULT_BARRIER_WALL_WIDTH));
							barrierHeightTextField.setEnabled(true);
							barrierHeightTextField.setText(Double.toString(SegmentSide.DEFAULT_BARRIER_WALL_HEIGHT));
							barrierSurfaceComboBox.setEnabled(true);
							barrierSurfaceComboBox.setSelectedItem(SegmentSide.DEFAULT_BARRIER_WALL_SURFACE);
							break;
						}
					}
				});
			}
			return barrierStyleComboBox;
		}
	}
	
	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();

        if (isDifferent(widthTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getWidth(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().setWidth(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;
        }

        if (isDifferent((String) surfaceComboBox.getSelectedItem(),
        	getEditorFrame().getTrackData().getMainTrack().getSurface(), stringResult))
        {
        	getEditorFrame().getTrackData().getMainTrack().setSurface(stringResult.getValue());
        	getEditorFrame().documentIsModified = true;
        }

		if (isDifferent((String) profilComboBox.getSelectedItem(),
	        getEditorFrame().getTrackData().getMainTrack().getProfil(), stringResult))
		{
			getEditorFrame().getTrackData().getMainTrack().setProfil(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(profilStepsLengthTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getProfilStepsLength(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().setProfilStepsLength(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;
        }

        if (isDifferent(racelineWidthscaleTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getRacelineWidthscale(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().setRacelineWidthscale(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;
        }

        if (isDifferent(racelineIntTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getRacelineInt(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().setRacelineInt(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;
        }

        if (isDifferent(racelineExtTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getRacelineExt(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().setRacelineExt(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;
        }

        SidePanel left = (SidePanel) tabbedPane.getComponentAt(0);

        if (isDifferent(left.sideStartWidthTextField.getText(),
        	getEditorFrame().getTrackData().getMainTrack().getLeft().getSideStartWidth(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().getLeft().setSideStartWidth(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;        	
        }
        if (isDifferent(left.sideEndWidthTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getLeft().getSideEndWidth(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().getLeft().setSideEndWidth(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;        	
        }
		if (isDifferent((String) left.sideSurfaceComboBox.getSelectedItem(),
            getEditorFrame().getTrackData().getMainTrack().getLeft().getSideSurface(), stringResult))
		{
			getEditorFrame().getTrackData().getMainTrack().getLeft().setSideSurface(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
		if (isDifferent((String) left.sideBankingTypeComboBox.getSelectedItem(),
            getEditorFrame().getTrackData().getMainTrack().getLeft().getSideBankingType(), stringResult))
		{
			getEditorFrame().getTrackData().getMainTrack().getLeft().setSideBankingType(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
        if (isDifferent(left.borderWidthTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getLeft().getBorderWidth(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().getLeft().setBorderWidth(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;        	
        }
        if (isDifferent(left.borderHeightTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getLeft().getBorderHeight(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().getLeft().setBorderHeight(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;        	
        }
		if (isDifferent((String) left.borderSurfaceComboBox.getSelectedItem(),
            getEditorFrame().getTrackData().getMainTrack().getLeft().getBorderSurface(), stringResult))
		{
			getEditorFrame().getTrackData().getMainTrack().getLeft().setBorderSurface(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
		if (isDifferent((String) left.borderStyleComboBox.getSelectedItem(),
            getEditorFrame().getTrackData().getMainTrack().getLeft().getBorderStyle(), stringResult))
		{
			getEditorFrame().getTrackData().getMainTrack().getLeft().setBorderStyle(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
        if (isDifferent(left.barrierWidthTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getLeft().getBarrierWidth(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().getLeft().setBarrierWidth(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;        	
        }
        if (isDifferent(left.barrierHeightTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getLeft().getBarrierHeight(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().getLeft().setBarrierHeight(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;        	
        }
		if (isDifferent((String) left.barrierSurfaceComboBox.getSelectedItem(),
            getEditorFrame().getTrackData().getMainTrack().getLeft().getBarrierSurface(), stringResult))
		{
			getEditorFrame().getTrackData().getMainTrack().getLeft().setBarrierSurface(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
		if (isDifferent((String) left.barrierStyleComboBox.getSelectedItem(),
            getEditorFrame().getTrackData().getMainTrack().getLeft().getBarrierStyle(), stringResult))
		{
			getEditorFrame().getTrackData().getMainTrack().getLeft().setBarrierStyle(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

        SidePanel right = (SidePanel) tabbedPane.getComponentAt(1);

        if (isDifferent(right.sideStartWidthTextField.getText(),
        	getEditorFrame().getTrackData().getMainTrack().getRight().getSideStartWidth(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().getRight().setSideStartWidth(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;        	
        }
        if (isDifferent(right.sideEndWidthTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getRight().getSideEndWidth(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().getRight().setSideEndWidth(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;        	
        }
		if (isDifferent((String) right.sideSurfaceComboBox.getSelectedItem(),
            getEditorFrame().getTrackData().getMainTrack().getRight().getSideSurface(), stringResult))
		{
			getEditorFrame().getTrackData().getMainTrack().getRight().setSideSurface(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
		if (isDifferent((String) right.sideBankingTypeComboBox.getSelectedItem(),
            getEditorFrame().getTrackData().getMainTrack().getRight().getSideBankingType(), stringResult))
		{
			getEditorFrame().getTrackData().getMainTrack().getRight().setSideBankingType(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
        if (isDifferent(right.borderWidthTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getRight().getBorderWidth(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().getRight().setBorderWidth(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;        	
        }
        if (isDifferent(right.borderHeightTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getRight().getBorderHeight(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().getRight().setBorderHeight(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;        	
        }
		if (isDifferent((String) right.borderSurfaceComboBox.getSelectedItem(),
            getEditorFrame().getTrackData().getMainTrack().getRight().getBorderSurface(), stringResult))
		{
			getEditorFrame().getTrackData().getMainTrack().getRight().setBorderSurface(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
		if (isDifferent((String) right.borderStyleComboBox.getSelectedItem(),
            getEditorFrame().getTrackData().getMainTrack().getRight().getBorderStyle(), stringResult))
		{
			getEditorFrame().getTrackData().getMainTrack().getRight().setBorderStyle(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
        if (isDifferent(right.barrierWidthTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getRight().getBarrierWidth(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().getRight().setBarrierWidth(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;        	
        }
        if (isDifferent(right.barrierHeightTextField.getText(),
            getEditorFrame().getTrackData().getMainTrack().getRight().getBarrierHeight(), doubleResult))
        {
            getEditorFrame().getTrackData().getMainTrack().getRight().setBarrierHeight(doubleResult.getValue());
            getEditorFrame().documentIsModified = true;        	
        }
		if (isDifferent((String) right.barrierSurfaceComboBox.getSelectedItem(),
            getEditorFrame().getTrackData().getMainTrack().getRight().getBarrierSurface(), stringResult))
		{
			getEditorFrame().getTrackData().getMainTrack().getRight().setBarrierSurface(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
		if (isDifferent((String) right.barrierStyleComboBox.getSelectedItem(),
            getEditorFrame().getTrackData().getMainTrack().getRight().getBarrierStyle(), stringResult))
		{
			getEditorFrame().getTrackData().getMainTrack().getRight().setBarrierStyle(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
