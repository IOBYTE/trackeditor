/*
 *   PitProperties.java
 *   Created on 27 ??? 2005
 *
 *    The PitProperties.java is part of TrackEditor-0.3.1.
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
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.DoubleValue;
import utils.MutableDouble;
import utils.MutableInteger;
import utils.MutableString;
import utils.SegmentVector;
import utils.SurfaceComboBox;
import utils.circuit.Pits;
import utils.circuit.Segment;
import utils.circuit.SegmentSide;
import utils.circuit.Surface;

/**
 * @author babis
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class PitProperties extends PropertyPanel
{
	private class PitInfo
	{
		public String	name			= null;
		public String	surface			= null;
		public String	borderType		= null;
		public String	borderSurface	= null;
		public double	borderWidth		= Double.NaN;
		public double	borderHeight	= Double.NaN;
		
		PitInfo(String name)
		{
			this.name = name;
		}
	}
	
	private JLabel				styleLabel				= new JLabel();
	private JComboBox<String>	styleComboBox			= null;
	private JLabel				sideLabel				= new JLabel();
	private JComboBox<String>	sideComboBox			= null;
	private JLabel				entryLabel				= new JLabel();
	private JTextField			entryTextField			= new JTextField();
	private JLabel				startLabel				= new JLabel();
	private JTextField			startTextField			= new JTextField();
	private JLabel				startBuildingsLabel		= new JLabel();
	private JTextField			startBuildingsTextField	= new JTextField();
	private JLabel				stopBuildingsLabel		= new JLabel();
	private JTextField			stopBuildingsTextField	= new JTextField();
	private JLabel				maxPitsLabel			= new JLabel();
	private JTextField			maxPitsTextField		= new JTextField();
	private JLabel				endLabel				= new JLabel();
	private JTextField			endTextField			= new JTextField();
	private JLabel				exitLabel				= new JLabel();
	private JTextField			exitTextField			= new JTextField();
	private JLabel				widthLabel				= new JLabel();
	private JTextField			widthTextField			= new JTextField();
	private JLabel				lengthLabel				= new JLabel();
	private JTextField			lengthTextField			= new JTextField();
	private JLabel				indicatorLabel			= new JLabel();
	private JComboBox<String>	indicatorComboBox		= null;
	private JLabel				speedLimitLabel			= new JLabel();
	private JTextField			speedLimitTextField		= new JTextField();
	private JLabel				speedLimitUnitsLabel	= new JLabel();
	private JComboBox<String>	speedLimitUnitsComboBox	= null;
	private JCheckBox 			generatePitsCheckBox 	= null;
	private JLabel 				generatePitsLabel 		= new JLabel();
	private JTabbedPane			tabbedPane				= null;

	private PitInfo				entryPitInfo			= new PitInfo("Entry");
	private PitInfo				pitsPitInfo				= new PitInfo("Pit");
	private PitInfo				exitPitInfo				= new PitInfo("Exit");

	private String[]			sideSurfaceItems		=
														{"grass", "grass3", "grass5", "grass6", "grass7", "gravel",
			"sand3", "sand", "asphalt-lines", "asphalt-l-left", "asphalt-l-right", "asphalt-l-both", "asphalt-pits",
			"asphalt", "dirt", "dirt-b", "asphalt2", "road1", "road1-pits", "road1-asphalt", "asphalt-road1",
			"b-road1", "b-road1-l2", "b-road1-l2p", "concrete", "concrete2", "concrete3", "b-asphalt", "b-asphalt-l1",
			"b-asphalt-l1p", "asphalt2-lines", "asphalt2-l-left", "asphalt2-l-right", "asphalt2-l-both", "curb-5cm-r",
			"curb-5cm-l", "curb-l", "tar-grass3-l", "tar-grass3-r", "tar-sand", "b-road1-grass6", "b-road1-grass6-l2",
			"b-road1-gravel-l2", "b-road1-sand3", "b-road1-sand3-l2", "b-asphalt-grass7", "b-asphalt-grass7-l1",
			"b-asphalt-grass6", "b-asphalt-grass6-l1", "b-asphalt-sand3", "b-asphalt-sand3-l1", "barrier", "barrier2",
			"barrier-turn", "barrier-grille", "wall", "wall2", "tire-wall"};
	private Vector<String>		sideSurfaceVector		= new Vector<String>(Arrays.asList(sideSurfaceItems));
	
	/**
	 *
	 */
	public PitProperties(EditorFrame editorFrame)
	{
		super(editorFrame);
		addDefaultSurfaces(sideSurfaceVector);
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

		addLabel(this, 0, styleLabel, "Type", 110);
		addLabel(this, 1, sideLabel, "Side", 110);
		addLabel(this, 2, entryLabel, "Entry", 110);
		addLabel(this, 3, startLabel, "Start", 110);
		addLabel(this, 4, startBuildingsLabel, "Start Buildings", 110);
		addLabel(this, 5, stopBuildingsLabel, "Stop Buildings", 110);
		addLabel(this, 6, maxPitsLabel, "Max Pits", 110);
		addLabel(this, 7, endLabel, "End", 110);
		addLabel(this, 8, exitLabel, "Exit", 110);
		addLabel(this, 9, widthLabel, "Width", 110);
		addLabel(this, 10, lengthLabel, "Length", 110);
		addLabel(this, 11, indicatorLabel, "Indicator", 110);
		addLabel(this, 12, speedLimitLabel, "Speed Limit", 110);
		addLabel(this, 14, generatePitsLabel, "Generate Pits", 110);

		add(getStyleComboBox(), null);
		add(getSideComboBox(), null);

		Pits pits = getEditorFrame().getPits();

		addTextField(this, 2, entryTextField, pits.getEntry(), 120, 125);
		addTextField(this, 3, startTextField, pits.getStart(), 120, 125);
		addTextField(this, 4, startBuildingsTextField, pits.getStartBuildings(), 120, 125);
		addTextField(this, 5, stopBuildingsTextField, pits.getStopBuildings(), 120, 125);
		addTextField(this, 6, maxPitsTextField, pits.getMaxPits(), 120, 125);
		addTextField(this, 7, endTextField, pits.getEnd(), 120, 125);
		addTextField(this, 8, exitTextField, pits.getExit(), 120, 125);
		addTextField(this, 9, widthTextField, pits.getWidth(), 120, 125);
		addTextField(this, 10, lengthTextField, pits.getLength(), 120, 125);

		add(getIndicatorComboBox(), null);

		addTextField(this, 12, speedLimitTextField, pits.getSpeedLimitValue(), 120, 125);
		add(getSpeedLimitUnitsLabel(), null);
		add(getSpeedLimitUnitsComboBox(), null);

		add(getGeneratePitsCheckBox(), null);
		add(getTabbedPane(), null);		
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
	
	/**
	 * This method initializes styleComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getStyleComboBox()
	{
		if (styleComboBox == null)
		{
			String[] items = {"none", "no pits", "on track side", "on separate path", "no building"};
			styleComboBox = new JComboBox<String>(items);
			styleComboBox.setBounds(120, 10, 125, 23);
			int style = getEditorFrame().getPits().getStyle();
			if (style == Integer.MAX_VALUE)
				style = 0;
			else
				style++;
			styleComboBox.setSelectedIndex(style);
		}
		return styleComboBox;
	}

	/**
	 * This method initializes sideComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getSideComboBox()
	{
		if (sideComboBox == null)
		{
			String[] items = {"none", "right", "left"};
			sideComboBox = new JComboBox<String>(items);
			sideComboBox.setBounds(120, 37, 125, 23);
			String side = getEditorFrame().getPits().getSide();
			if (side == null || side.isEmpty())
				side = "none";
			selectItem(sideComboBox, side);
		}
		return sideComboBox;
	}

	/**
	 * This method initializes indicatorComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getIndicatorComboBox()
	{
		if (indicatorComboBox == null)
		{
			String[] items = {"none", "0  normal_pit_indicator.ac", "1  pit_indicator.ac"};
			indicatorComboBox = new JComboBox<String>(items);
			indicatorComboBox.setBounds(120, 307, 175, 23);
			int indicator = getEditorFrame().getPits().getIndicator();
			if (indicator == Integer.MAX_VALUE)
				indicator = 0;
			else
				indicator++;
			indicatorComboBox.setSelectedIndex(indicator);
		}
		return indicatorComboBox;
	}

	/**
	 * This method initializes speedLimitUnitsComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JLabel getSpeedLimitUnitsLabel()
	{
		speedLimitUnitsLabel.setText("Units");;
		speedLimitUnitsLabel.setBounds(260, 334, 40, 23);

		return speedLimitUnitsLabel;
	}

	/**
	 * This method initializes speedLimitUnitsComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getSpeedLimitUnitsComboBox()
	{
		if (speedLimitUnitsComboBox == null)
		{
			String[] items = {"none", "meters per second", "kilometers per hour", "miles per hour"};
			speedLimitUnitsComboBox = new JComboBox<String>(items);
			speedLimitUnitsComboBox.setBounds(300, 334, 140, 23);
			DoubleValue speedLimit = getEditorFrame().getPits().getSpeedLimit();
			int indicator = -1;

			if (speedLimit == null || speedLimit.units == null || Double.isNaN(speedLimit.value))
				indicator = 0;
			else if (speedLimit.units.equals("m") || speedLimit.units.equalsIgnoreCase("mps"))
				indicator = 1;
			else if (speedLimit.units.equalsIgnoreCase("kph"))
				indicator = 2;
			else if (speedLimit.units.equalsIgnoreCase("mph"))
				indicator = 3;
			speedLimitUnitsComboBox.setSelectedIndex(indicator);
			speedLimitUnitsComboBox.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					// Should we convert the speed limit to the new units?
				}
			});
		}
		return speedLimitUnitsComboBox;
	}

 	/**
	 * This method initializes generatePitsCheckBox
 	 *
	 * @return javax.swing.JCheckBox
 	 */
	private JCheckBox getGeneratePitsCheckBox()
	{
		if (generatePitsCheckBox == null)
		{
			generatePitsCheckBox = new JCheckBox();
			generatePitsCheckBox.setBounds(120, 388, 20, 20);
			generatePitsCheckBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					generatePits();
				}
			});
		}
		return generatePitsCheckBox;
	}
	
	private void generatePits()
	{
		if (generatePitsCheckBox.isSelected())
		{
			Pits pits = getEditorFrame().getPits();
			SegmentVector data = getEditorFrame().getTrackData().getSegments();
			Segment pitEntry = null;
			Segment pitStart = null;
			Segment pitEnd = null;
			Segment pitExit = null;

			Iterator<Segment> it = data.iterator();
			while (it.hasNext())
			{
				Segment obj = it.next();
				String name = obj.getName();
				if (name.equals(pits.getEntry()))
				{
					pitEntry = obj;
				}
				if (name.equals(pits.getStart()))
				{
					pitStart = obj;
				}
				if (name.equals(pits.getEnd()))
				{
					pitEnd = obj;
				}
				if (name.equals(pits.getExit()))
				{
					pitExit = obj;
				}
			}

			if (pitEntry != null)
			{
				// pits already exists so use it
				SegmentSide side = null;
				SegmentSide nextSide = null;
				Segment next = pitEntry.getNextShape();
				if (pits.getSide().equals("left"))
				{
					side = pitEntry.getLeft();
					if (next == null)
						next = data.elementAt(0);
					nextSide = next.getLeft();
				}
				else
				{
					side = pitEntry.getRight();
					if (next == null)
						next = data.elementAt(0);
					nextSide = next.getRight();
				}
				
				if (next != pitStart && nextSide.getHasBorder())
				{
					// entry has more than one segment
					entryPitInfo.borderType = nextSide.getBorderStyle();
					entryPitInfo.borderSurface = nextSide.getBorderSurface();
					entryPitInfo.borderHeight = nextSide.getBorderHeight();
					entryPitInfo.borderWidth = nextSide.getBorderWidth();					
				}
				else
				{
					entryPitInfo.borderType = SegmentSide.DEFAULT_PIT_ENTRY_BORDER_STYLE;
					entryPitInfo.borderSurface = SegmentSide.DEFAULT_PIT_ENTRY_BORDER_SURFACE;
					entryPitInfo.borderHeight = SegmentSide.DEFAULT_PIT_ENTRY_BORDER_HEIGHT;
					entryPitInfo.borderWidth = SegmentSide.DEFAULT_PIT_ENTRY_BORDER_WIDTH;
				}
				// the surface the game uses there: often it is inherited, not set on the segment
				entryPitInfo.surface = pits.getSide().equals("left") ? pitEntry.getValidLeftSideSurface(getEditorFrame()) : pitEntry.getValidRightSideSurface(getEditorFrame());
			}
			else
			{
				// set default values
				entryPitInfo.surface = SegmentSide.DEFAULT_PIT_ENTRY_SURFACE;
				entryPitInfo.borderType = SegmentSide.DEFAULT_PIT_ENTRY_BORDER_STYLE;
				entryPitInfo.borderSurface = SegmentSide.DEFAULT_PIT_ENTRY_BORDER_SURFACE;
				entryPitInfo.borderHeight = SegmentSide.DEFAULT_PIT_ENTRY_BORDER_HEIGHT;
				entryPitInfo.borderWidth = SegmentSide.DEFAULT_PIT_ENTRY_BORDER_WIDTH;
			}
			PitPanel entryPitPanel = new PitPanel(entryPitInfo);
			tabbedPane.addTab(entryPitInfo.name, null, entryPitPanel, null);
			
			if (pitStart != null)
			{
				SegmentSide side = null;
				if (pits.getSide().equals("left"))
				{
					side = pitStart.getLeft();
				}
				else
				{
					side = pitStart.getRight();
				}
				
				if (side.getHasBorder())
				{
					// use existing border
					pitsPitInfo.borderType = side.getBorderStyle();
					pitsPitInfo.borderSurface = side.getBorderSurface();
					pitsPitInfo.borderHeight = side.getBorderHeight();
					pitsPitInfo.borderWidth = side.getBorderWidth();					
				}
				else
				{
					pitsPitInfo.borderType = SegmentSide.DEFAULT_PIT_PITS_BORDER_STYLE;
					pitsPitInfo.borderSurface = SegmentSide.DEFAULT_PIT_PITS_BORDER_SURFACE;
					pitsPitInfo.borderHeight = SegmentSide.DEFAULT_PIT_PITS_BORDER_HEIGHT;
					pitsPitInfo.borderWidth = SegmentSide.DEFAULT_PIT_PITS_BORDER_WIDTH;
				}
				// the surface the game uses there: often it is inherited, not set on the segment
				pitsPitInfo.surface = pits.getSide().equals("left") ? pitStart.getValidLeftSideSurface(getEditorFrame()) : pitStart.getValidRightSideSurface(getEditorFrame());
			}
			else
			{
				// set default values
				pitsPitInfo.surface = SegmentSide.DEFAULT_PIT_PITS_SURFACE;
				pitsPitInfo.borderType = SegmentSide.DEFAULT_PIT_PITS_BORDER_STYLE;
				pitsPitInfo.borderSurface = SegmentSide.DEFAULT_PIT_PITS_BORDER_SURFACE;
				pitsPitInfo.borderHeight = SegmentSide.DEFAULT_PIT_PITS_BORDER_HEIGHT;
				pitsPitInfo.borderWidth = SegmentSide.DEFAULT_PIT_PITS_BORDER_WIDTH;
			}
			PitPanel pitsPitPanel = new PitPanel(pitsPitInfo);
			tabbedPane.addTab(pitsPitInfo.name, null, pitsPitPanel, null);
			
			if (pitExit != null)
			{
				// exit already exists so use it
				SegmentSide side = null;
				SegmentSide previousSide = null;
				Segment previous = pitExit.getPreviousShape();
				if (pits.getSide().equals("left"))
				{
					side = pitExit.getLeft();
					if (previous == null)
						previous = data.elementAt(data.size() - 1);
					previousSide = previous.getLeft();
				}
				else
				{
					side = pitExit.getRight();
					if (previous == null)
						previous = data.elementAt(data.size() - 1);
					previousSide = previous.getRight();
				}

				if (previous != pitEnd && previousSide.getHasBorder())
				{
					// exit has more than one segment
					exitPitInfo.borderType = previousSide.getBorderStyle();
					exitPitInfo.borderSurface = previousSide.getBorderSurface();
					exitPitInfo.borderHeight = previousSide.getBorderHeight();
					exitPitInfo.borderWidth = previousSide.getBorderWidth();					
				}
				else
				{
					exitPitInfo.borderType = SegmentSide.DEFAULT_PIT_EXIT_BORDER_STYLE;
					exitPitInfo.borderSurface = SegmentSide.DEFAULT_PIT_EXIT_BORDER_SURFACE;
					exitPitInfo.borderHeight = SegmentSide.DEFAULT_PIT_EXIT_BORDER_HEIGHT;
					exitPitInfo.borderWidth = SegmentSide.DEFAULT_PIT_EXIT_BORDER_WIDTH;
				}
				// the surface the game uses there: often it is inherited, not set on the segment
				exitPitInfo.surface = pits.getSide().equals("left") ? pitExit.getValidLeftSideSurface(getEditorFrame()) : pitExit.getValidRightSideSurface(getEditorFrame());
			}
			else
			{
				exitPitInfo.surface = SegmentSide.DEFAULT_PIT_EXIT_SURFACE;
				exitPitInfo.borderType = SegmentSide.DEFAULT_PIT_EXIT_BORDER_STYLE;
				exitPitInfo.borderSurface = SegmentSide.DEFAULT_PIT_EXIT_BORDER_SURFACE;
				exitPitInfo.borderHeight = SegmentSide.DEFAULT_PIT_EXIT_BORDER_HEIGHT;
				exitPitInfo.borderWidth = SegmentSide.DEFAULT_PIT_EXIT_BORDER_WIDTH;
			}
			
			PitPanel exitPitPanel = new PitPanel(exitPitInfo);
			tabbedPane.addTab(exitPitInfo.name, null, exitPitPanel, null);
		}
		else
		{
			tabbedPane.removeAll();
		}	
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
			tabbedPane.setBounds(10, 442, 510, 200);
		}
		return tabbedPane;
	}

	private class PitPanel extends JPanel
	{
		private PitInfo				pitInfo					= null;
		private JLabel				surfaceLabel			= new JLabel();
		private SurfaceComboBox		surfaceComboBox			= null;
		private JLabel				borderTypeLabel			= new JLabel();
		private JComboBox<String>	borderTypeComboBox		= null;
		private JLabel				borderSurfaceLabel		= new JLabel();
		private SurfaceComboBox		borderSurfaceComboBox	= null;
		private JLabel				borderHeightLabel		= new JLabel();
		private JTextField			borderHeightTextField	= new JTextField();
		private JLabel				borderWidthLabel		= new JLabel();
		private JTextField			borderWidthTextField	= new JTextField();
		
		/**
		 *
		 */
		public PitPanel(PitInfo pitInfo)
		{
			super();
			initialize(pitInfo);
		}

		/**
		 *
		 */
		private void initialize(PitInfo pitInfo)
		{
			setLayout(null);

			addLabel(this, 0, surfaceLabel, "Surface", 120);
			addLabel(this, 1, borderTypeLabel, "Border Type", 120);
			addLabel(this, 2, borderSurfaceLabel, "Border Surface", 120);
			addLabel(this, 3, borderHeightLabel, "Border Height", 120);
			addLabel(this, 4, borderWidthLabel, "Border Width", 120);

			add(getSurfaceComboBox(), null);
			add(getBorderTypeComboBox(), null);
			add(getBorderSurfaceComboBox(), null);
			addTextField(this, 3, borderHeightTextField, Double.NaN, 130, 125);
			addTextField(this, 4, borderWidthTextField, Double.NaN, 130, 125);
			
			setPitInfo(pitInfo);
		}
		
		private void setPitInfo(PitInfo pitInfo)
		{
			this.pitInfo = pitInfo;
			
			surfaceComboBox.setSelectedItem(this.pitInfo.surface);
			
			String borderType = this.pitInfo.borderType;
			if (borderType == null || borderType.isEmpty())
				borderType = "none";
			selectItem(borderTypeComboBox, borderType);
			borderTypeChanged(true);
		}

		/**
		 * This method initializes entrySurfaceComboBox
		 *
		 * @return javax.swing.JComboBox
		 */
		public SurfaceComboBox getSurfaceComboBox()
		{
			if (surfaceComboBox == null)
			{
				surfaceComboBox = new SurfaceComboBox(getEditorFrame(), sideSurfaceVector);
				surfaceComboBox.setBounds(130, 10, 200, 23);
			}
			return surfaceComboBox;
		}

		/**
		 * This method initializes borderTypeComboBox
		 *
		 * @return javax.swing.JComboBox
		 */
		public JComboBox<String> getBorderTypeComboBox()
		{
			if (borderTypeComboBox == null)
			{
				String[] types = { "none", "curb", "wall" };
				borderTypeComboBox = new JComboBox<String>(types);
				borderTypeComboBox.setBounds(130, 38, 125, 23);
				borderTypeComboBox.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (pitInfo != null)
							borderTypeChanged(false);
					}
				});
			}
			return borderTypeComboBox;
		}
		
		/**
		 * Enables the border fields for the border type chosen in the combo box.
		 * fromPitInfo fills them from the track; otherwise (the user picked a type) empty
		 * fields get the track's border, or the default pit border if it had none.
		 */
		private void borderTypeChanged(boolean fromPitInfo)
		{
			String type = (String) borderTypeComboBox.getSelectedItem();
			if (type == null || type.isEmpty() || type.equals("none"))
			{
				borderSurfaceComboBox.setEnabled(false);
				borderSurfaceComboBox.setSelectedIndex(-1);
				borderHeightTextField.setEnabled(false);
				setTextField(borderHeightTextField, Double.NaN);
				borderWidthTextField.setEnabled(false);
				setTextField(borderWidthTextField, Double.NaN);
			}
			else if (fromPitInfo)
			{
				borderSurfaceComboBox.setEnabled(true);
				borderSurfaceComboBox.setSelectedItem(pitInfo.borderSurface);
				borderHeightTextField.setEnabled(true);				
				setTextField(borderHeightTextField, pitInfo.borderHeight);
				borderWidthTextField.setEnabled(true);
				setTextField(borderWidthTextField, pitInfo.borderWidth);
			}
			else
			{
				boolean hadBorder = pitInfo.borderType != null && !pitInfo.borderType.isEmpty() && !pitInfo.borderType.equals("none");
				borderSurfaceComboBox.setEnabled(true);
				if (borderSurfaceComboBox.getSelectedItem() == null)
					borderSurfaceComboBox.setSelectedItem(hadBorder && pitInfo.borderSurface != null ? pitInfo.borderSurface : SegmentSide.DEFAULT_PIT_PITS_BORDER_SURFACE);
				borderHeightTextField.setEnabled(true);
				if (borderHeightTextField.getText().trim().isEmpty())
					setTextField(borderHeightTextField, hadBorder && !Double.isNaN(pitInfo.borderHeight) ? pitInfo.borderHeight : SegmentSide.DEFAULT_PIT_PITS_BORDER_HEIGHT);
				borderWidthTextField.setEnabled(true);
				if (borderWidthTextField.getText().trim().isEmpty())
					setTextField(borderWidthTextField, hadBorder && !Double.isNaN(pitInfo.borderWidth) ? pitInfo.borderWidth : SegmentSide.DEFAULT_PIT_PITS_BORDER_WIDTH);
			}
		}
		
		/**
		 * This method initializes borderSurfaceComboBox
		 *
		 * @return javax.swing.JComboBox
		 */
		public JComboBox<String> getBorderSurfaceComboBox()
		{
			if (borderSurfaceComboBox == null)
			{
				borderSurfaceComboBox = new SurfaceComboBox(getEditorFrame(), sideSurfaceVector);
				borderSurfaceComboBox.setBounds(130, 65, 180, 23);
			}
			return borderSurfaceComboBox;
		}
		
		/**
		 * This method initializes borderSurfaceComboBox
		 *
		 * @return javax.swing.JComboBox
		 */
		public JTextField getBorderHeightTextField()
		{
			return borderHeightTextField;
		}
		
		/**
		 * This method initializes borderSurfaceComboBox
		 *
		 * @return javax.swing.JComboBox
		 */
		public JTextField getBorderWidthTextField()
		{
			return borderWidthTextField;
		}
	}
	
	/**
	 * Before anything is applied: when pits are to be generated, each part (entry,
	 * pit, exit) with a border needs its border surface, height and width.
	 *
	 * @return what is missing, with its tab selected, or null
	 */
	public String generateProblem()
	{
		if (!getGeneratePitsCheckBox().isSelected())
			return null;

		for (int i = 0; i < tabbedPane.getTabCount(); i++)
		{
			PitPanel panel = (PitPanel) tabbedPane.getComponentAt(i);
			String problem = null;
			String type = (String) panel.getBorderTypeComboBox().getSelectedItem();
			if (panel.getSurfaceComboBox().getSelectedItem() == null)
				problem = "choose a surface";
			else if (type != null && !type.equals("none"))
			{
				if (panel.getBorderSurfaceComboBox().getSelectedItem() == null)
					problem = "choose a border surface";
				else if (Double.isNaN(getDouble(panel.getBorderHeightTextField().getText())))
					problem = "the border needs a height";
				else if (Double.isNaN(getDouble(panel.getBorderWidthTextField().getText())))
					problem = "the border needs a width";
			}
			if (problem != null)
			{
				tabbedPane.setSelectedIndex(i);
				return "Pit > " + tabbedPane.getTitleAt(i) + ": to generate pits, " + problem + ".";
			}
		}
		return null;
	}

	/**
	 *
	 */
	public void exit()
	{
		int index = getStyleComboBox().getSelectedIndex();
		int style = getEditorFrame().getPits().getStyle();
		if (index == 0)
		{
			if (style != Integer.MAX_VALUE)
			{
				getEditorFrame().getPits().setStyle(Integer.MAX_VALUE);
				getEditorFrame().documentIsModified = true;
			}
		}
		else if (style == Integer.MAX_VALUE || style != index - 1)
		{
			getEditorFrame().getPits().setStyle(index - 1);
			getEditorFrame().documentIsModified = true;
		}

		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();
		MutableInteger integerResult = new MutableInteger();

		if (isDifferent((String) getSideComboBox().getSelectedItem(),
			getEditorFrame().getPits().getSide(), stringResult))
		{
			getEditorFrame().getPits().setSide(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(entryTextField.getText(),
			getEditorFrame().getPits().getEntry(), stringResult))
		{
			getEditorFrame().getPits().setEntry(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(startTextField.getText(),
			getEditorFrame().getPits().getStart(), stringResult))
		{
			getEditorFrame().getPits().setStart(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(startBuildingsTextField.getText(),
			getEditorFrame().getPits().getStartBuildings(), stringResult))
		{
			getEditorFrame().getPits().setStartBuildings(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(stopBuildingsTextField.getText(),
			getEditorFrame().getPits().getStopBuildings(), stringResult))
		{
			getEditorFrame().getPits().setStopBuildings(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(maxPitsTextField.getText(),
			getEditorFrame().getPits().getMaxPits(), integerResult))
		{
			getEditorFrame().getPits().setMaxPits(integerResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(endTextField.getText(),
			getEditorFrame().getPits().getEnd(), stringResult))
		{
			getEditorFrame().getPits().setEnd(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(exitTextField.getText(),
			getEditorFrame().getPits().getExit(), stringResult))
		{
			getEditorFrame().getPits().setExit(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(widthTextField.getText(),
			getEditorFrame().getPits().getWidth(), doubleResult))
		{
			getEditorFrame().getPits().setWidth(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(lengthTextField.getText(),
			getEditorFrame().getPits().getLength(), doubleResult))
		{
			getEditorFrame().getPits().setLength(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		index = getIndicatorComboBox().getSelectedIndex();
		int indicator = getEditorFrame().getPits().getIndicator();
		if (index == 0)
		{
			if (indicator != Integer.MAX_VALUE)
			{
				getEditorFrame().getPits().setIndicator(Integer.MAX_VALUE);
				getEditorFrame().documentIsModified = true;
			}
		}
		else if (indicator == Integer.MAX_VALUE || indicator != index - 1)
		{
			getEditorFrame().getPits().setIndicator(index - 1);
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(speedLimitTextField.getText(),
			getEditorFrame().getPits().getSpeedLimitValue(), doubleResult))
		{
			getEditorFrame().getPits().setSpeedLimitValue(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		index = getSpeedLimitUnitsComboBox().getSelectedIndex();
		String	units = getEditorFrame().getPits().getSpeedLimitUnits();
		String[] items = {null, "m", "kph", "mph"};
		if (index == 0)
		{
			if (units != null)
			{
				getEditorFrame().getPits().setSpeedLimitUnits(null);
				getEditorFrame().documentIsModified = true;
			}
		}
		else if (index != -1 && !items[index].equalsIgnoreCase(units))
		{
			getEditorFrame().getPits().setSpeedLimitUnits(items[index]);
			getEditorFrame().documentIsModified = true;
		}

		if (getGeneratePitsCheckBox().isSelected())
		{
			createPits();
			getEditorFrame().documentIsModified = true;
		}
	}

	/**
	 *
	 */
	private void createPits()
	{
		Pits pits = getEditorFrame().getPits();
		SegmentVector data = getEditorFrame().getTrackData().getSegments();
		Segment pitEntry = null;
		Segment pitStart = null;
		Segment pitEnd = null;
		Segment pitExit = null;

		Iterator<Segment> it = data.iterator();
		while (it.hasNext())
		{
			Segment obj = it.next();
			String name = obj.getName();
			if (name.equals(pits.getEntry()))
			{
				pitEntry = obj;
			}
			if (name.equals(pits.getStart()))
			{
				pitStart = obj;
			}
			if (name.equals(pits.getEnd()))
			{
				pitEnd = obj;
			}
			if (name.equals(pits.getExit()))
			{
				pitExit = obj;
			}
		}

		if (pits.getSide() == null)
 		{
			JOptionPane.showMessageDialog(this, "No pit side specified", "Pit Side", JOptionPane.ERROR_MESSAGE);
			return;
 		}
		
		if (pitEntry == null)
 		{
			if (pits.getEntry() == null || pits.getEntry().isEmpty())
			{
				JOptionPane.showMessageDialog(this, "No pit entry specified.", "Pit Entry", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Pit entry " + pits.getEntry() + " not found.", "Pit Entry", JOptionPane.ERROR_MESSAGE);
			}
 			return;
 		}

		if (pitStart == null)
 		{
			if (pits.getStart() == null || pits.getStart().isEmpty())
			{
				JOptionPane.showMessageDialog(this, "No pit start specified.", "Pit Start", JOptionPane.WARNING_MESSAGE);
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Pit start " + pits.getStart() + " not found.", "Pit Start", JOptionPane.WARNING_MESSAGE);
			}
			return;
		}
		
		if (pitEnd == null)
 		{
			if (pits.getEnd() == null || pits.getEnd().isEmpty())
			{
				JOptionPane.showMessageDialog(this, "No pit end specified.", "Pit End", JOptionPane.WARNING_MESSAGE);
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Pit end " + pits.getEnd() + " not found.", "Pit End", JOptionPane.WARNING_MESSAGE);
			}
			return;
 		}

		if (pitExit == null)
 		{
			if (pits.getExit() == null || pits.getExit().isEmpty())
			{
				JOptionPane.showMessageDialog(this, "No pit exit specified.", "Pit Exit", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Pit exit " + pits.getExit() + " not found.", "Pit Exit", JOptionPane.ERROR_MESSAGE);
			}
 			return;
 		}	

		SegmentSide side = null;
		double borderWidth = 0;
		double sideWidth = 0;
		Segment nextSegment = pitExit.getNextShape();
		if (nextSegment == null)
			nextSegment = data.get(0);
		if (pits.getSide().equals("left"))
		{
			nextSegment.getLeft().setBorderWidth(nextSegment.getValidLeftBorderWidth(getEditorFrame()));
			nextSegment.getLeft().setBorderHeight(nextSegment.getValidLeftBorderHeight(getEditorFrame()));
			nextSegment.getLeft().setBorderSurface(nextSegment.getValidLeftBorderSurface(getEditorFrame()));
			nextSegment.getLeft().setBorderStyle(nextSegment.getValidLeftBorderStyle(getEditorFrame()));

			nextSegment.getLeft().setSideStartWidth(nextSegment.getValidLeftSideStartWidth(getEditorFrame()));
			nextSegment.getLeft().setSideSurface(nextSegment.getValidLeftSideSurface(getEditorFrame()));
			nextSegment.getLeft().setSideBankingType(nextSegment.getValidLeftSideBankingType(getEditorFrame()));

			nextSegment.getLeft().setBarrierWidth(nextSegment.getValidLeftBarrierWidth(getEditorFrame()));
			nextSegment.getLeft().setBarrierHeight(nextSegment.getValidLeftBarrierHeight(getEditorFrame()));
			nextSegment.getLeft().setBarrierSurface(nextSegment.getValidLeftBarrierSurface(getEditorFrame()));
			nextSegment.getLeft().setBarrierStyle(nextSegment.getValidLeftBarrierStyle(getEditorFrame()));

			side = pitExit.getLeft();
			borderWidth = nextSegment.getLeft().getBorderWidth();
			sideWidth = nextSegment.getLeft().getSideStartWidth();
		}
		else
		{
			nextSegment.getRight().setBorderWidth(nextSegment.getValidRightBorderWidth(getEditorFrame()));
			nextSegment.getRight().setBorderHeight(nextSegment.getValidRightBorderHeight(getEditorFrame()));
			nextSegment.getRight().setBorderSurface(nextSegment.getValidRightBorderSurface(getEditorFrame()));
			nextSegment.getRight().setBorderStyle(nextSegment.getValidRightBorderStyle(getEditorFrame()));

			nextSegment.getRight().setSideStartWidth(nextSegment.getValidRightSideStartWidth(getEditorFrame()));
			nextSegment.getRight().setSideSurface(nextSegment.getValidRightSideSurface(getEditorFrame()));
			nextSegment.getRight().setSideBankingType(nextSegment.getValidRightSideBankingType(getEditorFrame()));

			nextSegment.getRight().setBarrierWidth(nextSegment.getValidRightBarrierWidth(getEditorFrame()));
			nextSegment.getRight().setBarrierHeight(nextSegment.getValidRightBarrierHeight(getEditorFrame()));
			nextSegment.getRight().setBarrierSurface(nextSegment.getValidRightBarrierSurface(getEditorFrame()));
			nextSegment.getRight().setBarrierStyle(nextSegment.getValidRightBarrierStyle(getEditorFrame()));

			side = pitExit.getRight();
			borderWidth = nextSegment.getRight().getBorderWidth();
			sideWidth = nextSegment.getRight().getSideStartWidth();
		}

		PitPanel exitPanel = (PitPanel) tabbedPane.getComponentAt(2);
		int start = data.indexOf(pitExit);
		int end = data.indexOf(pitEnd);
		int count;
		if (start < end)
			count = start + data.size() - end;
		else
			count = start - end;

		for (int i = 0; i < count; i++)
		{
			// the exit can be after the start line and the end before it
			int index = Math.floorMod(start - i, data.size());
			if (pits.getSide().equals("left"))
			{
				side = data.get(index).getLeft();
			}
			else
			{
				side = data.get(index).getRight();
			}
			String borderType = exitPanel.getBorderTypeComboBox().getSelectedItem().toString();
			if (i == 0 || borderType.equals("none"))
			{
				// no border on last segment
				side.setBorderStyle(SegmentSide.DEFAULT_PIT_EXIT_BORDER_STYLE);
				side.setBorderSurface(SegmentSide.DEFAULT_PIT_EXIT_BORDER_SURFACE);
				side.setBorderHeight(SegmentSide.DEFAULT_PIT_EXIT_BORDER_HEIGHT);
				side.setBorderWidth(SegmentSide.DEFAULT_PIT_EXIT_BORDER_WIDTH);
				if (i == 0)
				{
					side.setSideStartWidth(pits.getWidth() * 3);
					side.setSideEndWidth(borderWidth + sideWidth);
				}
				else
				{
					side.setSideStartWidth((pits.getWidth() * 3));				
					side.setSideEndWidth(side.getSideStartWidth());
				}
			}
			else
			{
				side.setBorderStyle(borderType);
				side.setBorderSurface(exitPanel.getBorderSurfaceComboBox().getSelectedItem().toString());			
				side.setBorderHeight(getDouble(exitPanel.getBorderHeightTextField().getText()));
				side.setBorderWidth(getDouble(exitPanel.getBorderWidthTextField().getText()));
				side.setSideStartWidth((pits.getWidth() * 3) - side.getBorderWidth());				
				side.setSideEndWidth(side.getSideStartWidth());
			}
			side.setSideSurface(exitPanel.getSurfaceComboBox().getSelectedItem().toString());
			side.setBarrierHeight(1);
			side.setBarrierWidth(0.1);
		}

		Segment previousSegment = pitEntry.getPreviousShape();
		if (previousSegment == null)
			previousSegment = data.get(data.size() - 1);

		if (pits.getSide().equals("left"))
		{
			previousSegment.getLeft().setBorderWidth(previousSegment.getValidLeftBorderWidth(getEditorFrame()));
			previousSegment.getLeft().setBorderHeight(previousSegment.getValidLeftBorderHeight(getEditorFrame()));
			previousSegment.getLeft().setBorderSurface(previousSegment.getValidLeftBorderSurface(getEditorFrame()));
			previousSegment.getLeft().setBorderStyle(previousSegment.getValidLeftBorderStyle(getEditorFrame()));

			previousSegment.getLeft().setSideStartWidth(previousSegment.getValidLeftSideStartWidth(getEditorFrame()));
			previousSegment.getLeft().setSideSurface(previousSegment.getValidLeftSideSurface(getEditorFrame()));
			previousSegment.getLeft().setSideBankingType(previousSegment.getValidLeftSideBankingType(getEditorFrame()));

			previousSegment.getLeft().setBarrierWidth(previousSegment.getValidLeftBarrierWidth(getEditorFrame()));
			previousSegment.getLeft().setBarrierHeight(previousSegment.getValidLeftBarrierHeight(getEditorFrame()));
			previousSegment.getLeft().setBarrierSurface(previousSegment.getValidLeftBarrierSurface(getEditorFrame()));
			previousSegment.getLeft().setBarrierStyle(previousSegment.getValidLeftBarrierStyle(getEditorFrame()));

			side = pitEntry.getLeft();
			borderWidth = previousSegment.getLeft().getBorderWidth();
			sideWidth = previousSegment.getLeft().getSideStartWidth();
		}
		else
		{
			previousSegment.getRight().setBorderWidth(previousSegment.getValidRightBorderWidth(getEditorFrame()));
			previousSegment.getRight().setBorderHeight(previousSegment.getValidRightBorderHeight(getEditorFrame()));
			previousSegment.getRight().setBorderSurface(previousSegment.getValidRightBorderSurface(getEditorFrame()));
			previousSegment.getRight().setBorderStyle(previousSegment.getValidRightBorderStyle(getEditorFrame()));

			previousSegment.getRight().setSideStartWidth(previousSegment.getValidRightSideStartWidth(getEditorFrame()));
			previousSegment.getRight().setSideSurface(previousSegment.getValidRightSideSurface(getEditorFrame()));
			previousSegment.getRight().setSideBankingType(previousSegment.getValidRightSideBankingType(getEditorFrame()));

			previousSegment.getRight().setBarrierWidth(previousSegment.getValidRightBarrierWidth(getEditorFrame()));
			previousSegment.getRight().setBarrierHeight(previousSegment.getValidRightBarrierHeight(getEditorFrame()));
			previousSegment.getRight().setBarrierSurface(previousSegment.getValidRightBarrierSurface(getEditorFrame()));
			previousSegment.getRight().setBarrierStyle(previousSegment.getValidRightBarrierStyle(getEditorFrame()));

			side = pitEntry.getRight();
			borderWidth = previousSegment.getRight().getBorderWidth();
			sideWidth = previousSegment.getRight().getSideStartWidth();
		}

		PitPanel entryPanel = (PitPanel) tabbedPane.getComponentAt(0);
		start = data.indexOf(pitEntry);
		end = data.indexOf(pitStart);
		if (start > end)
			count = end + data.size() - start;
		else
			count = end - start;

		for (int i = 0; i < count; i++)
		{
			int index = (start + i) % data.size();
			if (pits.getSide().equals("left"))
			{
				side = data.get(index).getLeft();
			}
			else
			{
				side = data.get(index).getRight();
			}
			String borderType = entryPanel.getBorderTypeComboBox().getSelectedItem().toString();
			if (i == 0 || borderType.equals("none"))
			{
				// no border on first segment
				side.setBorderStyle(SegmentSide.DEFAULT_PIT_ENTRY_BORDER_STYLE);
				side.setBorderSurface(SegmentSide.DEFAULT_PIT_ENTRY_BORDER_SURFACE);
				side.setBorderHeight(SegmentSide.DEFAULT_PIT_ENTRY_BORDER_HEIGHT);
				side.setBorderWidth(SegmentSide.DEFAULT_PIT_ENTRY_BORDER_WIDTH);
				if (i == 0)
				{
					side.setSideStartWidth(borderWidth + sideWidth);
					side.setSideEndWidth(pits.getWidth() * 3);
				}
				else
				{
					side.setSideStartWidth((pits.getWidth() * 3));				
					side.setSideEndWidth(side.getSideEndWidth());
				}
			}
			else
			{
				side.setBorderStyle(borderType);
				side.setBorderSurface(entryPanel.getBorderSurfaceComboBox().getSelectedItem().toString());			
				side.setBorderHeight(getDouble(entryPanel.getBorderHeightTextField().getText()));
				side.setBorderWidth(getDouble(entryPanel.getBorderWidthTextField().getText()));
				side.setSideStartWidth((pits.getWidth() * 3) - side.getBorderWidth());				
				side.setSideEndWidth(side.getSideStartWidth());
			}
			side.setSideSurface(entryPanel.getSurfaceComboBox().getSelectedItem().toString());
			side.setBarrierHeight(1);
			side.setBarrierWidth(0.1);
		}

		PitPanel pitPanel = (PitPanel) tabbedPane.getComponentAt(1);
		start = data.indexOf(pitStart);
		end = data.indexOf(pitEnd);
		if (start > end)
			count = end + data.size() - start + 1;
		else
			count = end - start + 1;

		for (int i = 0; i < count; i++)
		{
			int index = (start + i) % data.size();
			if (pits.getSide().equals("left"))
			{
				side = data.get(index).getLeft();
			}
			else
			{
				side = data.get(index).getRight();
			}
			double width = pits.getWidth()*3;
			String borderType = pitPanel.getBorderTypeComboBox().getSelectedItem().toString();
			if (borderType.equals("none"))
			{
				side.setBorderHeight(SegmentSide.DEFAULT_PIT_PITS_BORDER_HEIGHT);
				side.setBorderWidth(SegmentSide.DEFAULT_PIT_PITS_BORDER_WIDTH);
				side.setBorderSurface(SegmentSide.DEFAULT_PIT_PITS_BORDER_SURFACE);
				side.setBorderStyle(SegmentSide.DEFAULT_PIT_PITS_BORDER_STYLE);
			}
			else
			{
				side.setBorderHeight(getDouble(pitPanel.getBorderHeightTextField().getText()));
				side.setBorderWidth(getDouble(pitPanel.getBorderWidthTextField().getText()));
				side.setBorderSurface(pitPanel.getBorderSurfaceComboBox().getSelectedItem().toString());
				side.setBorderStyle(borderType);
				width -= side.getBorderWidth();
			}
			side.setSideStartWidth(width);
			side.setSideEndWidth(width);
			side.setSideSurface(pitPanel.getSurfaceComboBox().getSelectedItem().toString());
			side.setBarrierHeight(1);
			side.setBarrierWidth(0.1);
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
