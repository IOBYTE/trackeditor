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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.DoubleValue;
import utils.undo.Undo;
import utils.undo.UndoPitGeneration;
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
	// empty: the game's defaults, shown in gray
	private JTextField			widthTextField			= hintTextField("5.0 (default)");
	private JLabel				lengthLabel				= new JLabel();
	private JTextField			lengthTextField			= hintTextField("15.0 (default)");
	private JLabel				indicatorLabel			= new JLabel();
	private JComboBox<String>	indicatorComboBox		= null;
	private JLabel				speedLimitLabel			= new JLabel();
	private JTextField			speedLimitTextField		= hintTextField("25.0 m/s (default)");
	private JLabel				speedLimitUnitsLabel	= new JLabel();
	private JComboBox<String>	speedLimitUnitsComboBox	= null;
	private JCheckBox			addPitsCheckBox		= null;
	private JLabel				addPitsLabel		= new JLabel();
	// adding pits using lengths: the segments are split or resized to fit them
	private JCheckBox			lengthsCheckBox		= null;
	private JLabel				entryLengthLabel	= new JLabel();
	private JTextField			entryLengthTextField	= new JTextField();
	private JLabel				entryLengthUnitsLabel	= new JLabel();
	private JLabel				pitsOffsetLabel		= new JLabel();
	private JComboBox<String>	pitsOffsetComboBox	= null;
	private JTextField			pitsOffsetTextField	= new JTextField();
	private JLabel				pitsOffsetUnitsLabel	= new JLabel();
	private JLabel				pitsLengthLabel		= new JLabel();
	private JTextField			pitsLengthTextField	= new JTextField();
	private JLabel				pitsLengthUnitsLabel	= new JLabel();
	private JLabel				buildingsOffsetLabel	= new JLabel();
	private JTextField			buildingsOffsetTextField	= new JTextField();
	private JLabel				buildingsOffsetUnitsLabel	= new JLabel();
	private JLabel				pitCountLabel		= new JLabel();
	private JTextField			pitCountTextField	= new JTextField();
	private JLabel				pitCountUnitsLabel	= new JLabel();
	private JLabel				exitLengthLabel		= new JLabel();
	private JTextField			exitLengthTextField	= new JTextField();
	private JLabel				exitLengthUnitsLabel	= new JLabel();
	private static final String	CENTER_ON_LINE		= "center on start line";
	private static final String	OFFSET_FROM_LINE	= "offset from start line";
	private JButton				applyButton			= null;
	private JButton				undoButton			= null;
	private JButton				redoButton			= null;
	// a "pit indicator" value the choices don't have, kept as it is
	private int					otherIndicator		= Integer.MAX_VALUE;
	private int					generateCount		= 0;
	private int					undoCount			= 0;
	private Vector<Segment>		initialSegments		= null;
	private Pits				initialPits			= null;
	private boolean				initialModified		= false;
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

		Pits pits = getEditorFrame().getPits();
		// TORCS only has track side pits, without buildings, max pits or an indicator
		boolean options = getEditorFrame().getGameType().hasPitOptions();
		int row = 0;

		addLabel(this, row, styleLabel, "Type", 110);
		add(getStyleComboBox(), null);
		getStyleComboBox().setBounds(120, rowY(row++), 125, 23);
		addLabel(this, row, sideLabel, "Side", 110);
		add(getSideComboBox(), null);
		getSideComboBox().setBounds(120, rowY(row++), 125, 23);
		addLabel(this, row, entryLabel, "Entry", 110);
		addTextField(this, row++, entryTextField, pits.getEntry(), 120, 200);
		addLabel(this, row, startLabel, "Start", 110);
		addTextField(this, row++, startTextField, pits.getStart(), 120, 200);
		if (options)
		{
			addLabel(this, row, startBuildingsLabel, "Start Buildings", 110);
			addTextField(this, row++, startBuildingsTextField, pits.getStartBuildings(), 120, 200);
			addLabel(this, row, stopBuildingsLabel, "Stop Buildings", 110);
			addTextField(this, row++, stopBuildingsTextField, pits.getStopBuildings(), 120, 200);
		}
		if (showMaxPitsAndIndicator())
		{
			addLabel(this, row, maxPitsLabel, "Max Pits", 110);
			addTextField(this, row++, maxPitsTextField, pits.getMaxPits(), 120, 125);
		}
		addLabel(this, row, endLabel, "End", 110);
		addTextField(this, row++, endTextField, pits.getEnd(), 120, 200);
		addLabel(this, row, exitLabel, "Exit", 110);
		addTextField(this, row++, exitTextField, pits.getExit(), 120, 200);
		addLabel(this, row, widthLabel, "Width", 110);
		addTextField(this, row++, widthTextField, pits.getWidth(), 120, 125);
		widthTextField.setToolTipText("Width of each pit (empty = 5 m)");
		addLabel(this, row, lengthLabel, "Length", 110);
		addTextField(this, row++, lengthTextField, pits.getLength(), 120, 125);
		lengthTextField.setToolTipText("Length of each pit (empty = 15 m)");
		if (showMaxPitsAndIndicator())
		{
			addLabel(this, row, indicatorLabel, "Indicator", 110);
			add(getIndicatorComboBox(), null);
			getIndicatorComboBox().setBounds(120, rowY(row++), 175, 23);
		}
		addLabel(this, row, speedLimitLabel, "Speed Limit", 110);
		addTextField(this, row, speedLimitTextField, pits.getSpeedLimitValue(), 120, 125);
		speedLimitTextField.setToolTipText("Pit lane speed limit (empty = 25 m/s, 90 km/h)");
		add(getSpeedLimitUnitsLabel(), null);
		getSpeedLimitUnitsLabel().setBounds(260, rowY(row), 40, 23);
		add(getSpeedLimitUnitsComboBox(), null);
		getSpeedLimitUnitsComboBox().setBounds(300, rowY(row), 140, 23);
		row += 2;

		addLabel(this, row, addPitsLabel, "Add Pits", 110);
		add(getAddPitsCheckBox(), null);
		getAddPitsCheckBox().setBounds(116, rowY(row) + 2, 185, 20);
		add(getLengthsCheckBox(), null);
		getLengthsCheckBox().setBounds(305, rowY(row) + 2, 150, 20);
		row++;
		// the lengths, in the order of the zones: entry, pits, buildings, exit
		addLabel(this, row, entryLengthLabel, "Entry Length", 110);
		addTextField(this, row, entryLengthTextField, "", 120, 125);
		addLabel(this, row++, entryLengthUnitsLabel, "m", 250, 30);
		entryLengthTextField.setToolTipText("Length of the pit entry, before the pits");
		addLabel(this, row, pitsOffsetLabel, "Pits Offset", 110);
		add(getPitsOffsetComboBox(), null);
		getPitsOffsetComboBox().setBounds(120, rowY(row), 175, 23);
		addTextField(this, row, pitsOffsetTextField, "", 300, 80);
		addLabel(this, row++, pitsOffsetUnitsLabel, "m", 385, 30);
		pitsOffsetTextField.setToolTipText("Where the pits start from the start line: less than 0 is before it");
		addLabel(this, row, pitsLengthLabel, "Pits Length", 110);
		addTextField(this, row, pitsLengthTextField, "", 120, 125);
		addLabel(this, row++, pitsLengthUnitsLabel, "m", 250, 200);
		pitsLengthTextField.setToolTipText("Length of the pits, from the pit start to the pit end");
		if (options)
		{
			addLabel(this, row, buildingsOffsetLabel, "Buildings Offset", 110);
			addTextField(this, row, buildingsOffsetTextField, "", 120, 125);
			addLabel(this, row++, buildingsOffsetUnitsLabel, "m from pit start", 250, 150);
			buildingsOffsetTextField.setToolTipText("Where the pit buildings start, from the pit start");
			addLabel(this, row, pitCountLabel, "Number of Pits", 110);
			addTextField(this, row, pitCountTextField, "", 120, 125);
			addLabel(this, row++, pitCountUnitsLabel, "", 250, 200);
			pitCountTextField.setToolTipText("How many pits: the buildings are this many pit lengths long");
		}
		addLabel(this, row, exitLengthLabel, "Exit Length", 110);
		addTextField(this, row, exitLengthTextField, "", 120, 125);
		addLabel(this, row++, exitLengthUnitsLabel, "m", 250, 30);
		exitLengthTextField.setToolTipText("Length of the pit exit, after the pits");
		fillLengthFields();
		javax.swing.event.DocumentListener update = new javax.swing.event.DocumentListener()
		{
			public void insertUpdate(javax.swing.event.DocumentEvent e)
			{
				updateLengthReadOuts();
			}

			public void removeUpdate(javax.swing.event.DocumentEvent e)
			{
				updateLengthReadOuts();
			}

			public void changedUpdate(javax.swing.event.DocumentEvent e)
			{
				updateLengthReadOuts();
			}
		};
		pitsLengthTextField.getDocument().addDocumentListener(update);
		pitCountTextField.getDocument().addDocumentListener(update);
		lengthTextField.getDocument().addDocumentListener(update);
		updateLengthReadOuts();
		setLengthsEnabled(false);

		add(getTabbedPane(), null);
		getTabbedPane().setLocation(10, rowY(row));

		int buttonY = rowY(row) + 205;
		int buttonWidth = 70;
		int totalWidth = buttonWidth * 3 + 20;
		int startX = (530 - totalWidth) / 2;
		add(getApplyButton(), null);
		getApplyButton().setBounds(startX, buttonY, buttonWidth, 25);
		add(getUndoButton(), null);
		getUndoButton().setBounds(startX + buttonWidth + 10, buttonY, buttonWidth, 25);
		add(getRedoButton(), null);
		getRedoButton().setBounds(startX + buttonWidth * 2 + 20, buttonY, buttonWidth, 25);

		setButtonsVisible(false);
	}

	private boolean isVersion3()
	{
		return getEditorFrame().getTrackData().getHeader().getVersion() == 3;
	}

	/** The pit type: TORCS version 4 has none, but its version 3 tracks do */
	/** The version 3 loaders don't read max pits or an indicator */
	private boolean showMaxPitsAndIndicator()
	{
		return getEditorFrame().getGameType().hasPitOptions() && !isVersion3();
	}

	// the pit type choices, in pit style order ("none" is no pit style)
	private static final String[] STYLE_ITEMS = {"none", "no pits", "on track side", "on separate path", "no building"};

	/**
	 * The pit type choices, "none" saying what the game does without one: track
	 * side pits, but no pits for a version 3 track
	 */
	private String[] styleItems()
	{
		String[] items = STYLE_ITEMS.clone();
		items[0] = isVersion3() ? "none (no pits)" : "none (track side)";
		return items;
	}

	/** @return whether the pits are on the left: the games take any side but "right" as left, and right when there's none */
	private static boolean onLeft(Pits pits)
	{
		return pits.getSide() != null && !pits.getSide().equals("right");
	}

	private static int rowY(int row)
	{
		return 10 + row * 27;
	}

	/**
	 * The surfaces the track can use: the default ones (the names it starts with,
	 * when the game's surfaces file wasn't found) only when it includes them, and
	 * its own
	 */
	private void addDefaultSurfaces(Vector<String> surfaceVector)
	{
		String[] fallback = surfaceVector.toArray(new String[0]);
		surfaceVector.clear();
		surfaceVector.addAll(getEditorFrame().getUsableSurfaceNames(fallback));
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
			int style = getEditorFrame().getPits().getStyle();
			int index = style == Integer.MAX_VALUE ? 0 : style + 1;
			String[] items = styleItems();
			if (isVersion3())
			{
				// the version 3 loaders only build track side pits; another type the
				// track already has stays listed, so looking doesn't change it
				items = java.util.Arrays.copyOf(styleItems(), 3);
				if (index >= 3 && index < STYLE_ITEMS.length)
				{
					items = java.util.Arrays.copyOf(items, 4);
					items[3] = STYLE_ITEMS[index];
				}
				styleComboBox = new JComboBox<String>(items);
				styleComboBox.setToolTipText("Version 3 tracks only get track side pits.");
			}
			else if (!getEditorFrame().getGameType().hasPitOptions())
			{
				// TORCS doesn't support buildings, so hide "no building"
				items = java.util.Arrays.copyOf(styleItems(), STYLE_ITEMS.length - 1);
				styleComboBox = new JComboBox<String>(items);
			}
			else
			{
				styleComboBox = new JComboBox<String>(items);
			}
			styleComboBox.setBounds(120, 10, 125, 23);
			selectStyle();
		}
		return styleComboBox;
	}

	private void selectStyle()
	{
		int style = getEditorFrame().getPits().getStyle();
		int index = style == Integer.MAX_VALUE ? 0 : style + 1;
		if (index >= 0 && index < STYLE_ITEMS.length)
			styleComboBox.setSelectedItem(styleItems()[index]);
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
			// the games use right when there's no side
			String[] items = {"none (right)", "right", "left"};
			sideComboBox = new JComboBox<String>(items);
			sideComboBox.setBounds(120, 37, 125, 23);
			selectSide();
		}
		return sideComboBox;
	}

	private void selectSide()
	{
		String side = getEditorFrame().getPits().getSide();
		if (side == null || side.isEmpty())
			sideComboBox.setSelectedIndex(0);
		else
			selectItem(sideComboBox, side);
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
			indicatorComboBox = new JComboBox<String>();
			indicatorComboBox.setBounds(120, 307, 175, 23);
			indicatorComboBox.setToolTipText("<html>The game only shows it when the pit type is \"no building\",<br>"
				+ "for pits with a car: 1 is pit_indicator.ac, 2 or more normal_pit_indicator.ac</html>");
			selectIndicator();
		}
		return indicatorComboBox;
	}

	/**
	 * The choices: none (no attribute), 1 (pit_indicator.ac) and 2
	 * (normal_pit_indicator.ac, which the game shows for any value above 1), and
	 * any other value the track has, kept as it is.
	 */
	private void selectIndicator()
	{
		int indicator = getEditorFrame().getPits().getIndicator();
		javax.swing.DefaultComboBoxModel<String> model = new javax.swing.DefaultComboBoxModel<String>(
			new String[] {"none", "1  pit_indicator.ac", "2  normal_pit_indicator.ac"});
		otherIndicator = Integer.MAX_VALUE;
		int index;
		if (indicator == Integer.MAX_VALUE)
			index = 0;
		else if (indicator == 1 || indicator == 2)
			index = indicator;
		else
		{
			otherIndicator = indicator;
			model.addElement(indicator + (indicator > 1 ? "  normal_pit_indicator.ac" : "  none in the game"));
			index = 3;
		}
		indicatorComboBox.setModel(model);
		indicatorComboBox.setSelectedIndex(index);
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
			// a speed limit without units is in meters per second
			String[] items = {"none (m/s)", "meters per second", "kilometers per hour", "miles per hour"};
			// TORCS track files use km/h
			speedLimitUnitsComboBox = new JComboBox<String>(items);
			speedLimitUnitsComboBox.setBounds(300, 334, 140, 23);
			selectSpeedLimitUnits();
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

	private void selectSpeedLimitUnits()
	{
		DoubleValue speedLimit = getEditorFrame().getPits().getSpeedLimit();
		int index = -1;

		if (speedLimit == null || speedLimit.units == null || Double.isNaN(speedLimit.value))
			index = 0;
		else if (speedLimit.units.equals("m") || speedLimit.units.equalsIgnoreCase("mps"))
			index = 1;
		else if (isKilometersPerHour(speedLimit.units))
			index = 2;
		else if (speedLimit.units.equalsIgnoreCase("mph"))
			index = 3;
		speedLimitUnitsComboBox.setSelectedIndex(index);
	}

	/**
	 * Shows the track's pit settings in the dialog's fields and generator
	 * tabs, after an undo or redo of a generation has put back other settings.
	 */
	private void refillFields()
	{
		Pits pits = getEditorFrame().getPits();
		boolean options = getEditorFrame().getGameType().hasPitOptions();

		selectStyle();
		selectSide();
		setTextField(entryTextField, pits.getEntry());
		setTextField(startTextField, pits.getStart());
		if (options)
		{
			setTextField(startBuildingsTextField, pits.getStartBuildings());
			setTextField(stopBuildingsTextField, pits.getStopBuildings());
		}
		if (showMaxPitsAndIndicator())
		{
			setTextField(maxPitsTextField, pits.getMaxPits());
			selectIndicator();
		}
		setTextField(endTextField, pits.getEnd());
		setTextField(exitTextField, pits.getExit());
		setTextField(widthTextField, pits.getWidth());
		setTextField(lengthTextField, pits.getLength());
		setTextField(speedLimitTextField, pits.getSpeedLimitValue());
		selectSpeedLimitUnits();
		refillTabs();
	}

	private JCheckBox getAddPitsCheckBox()
	{
		if (addPitsCheckBox == null)
		{
			addPitsCheckBox = new JCheckBox("using existing segments");
			addPitsCheckBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					// adding pits needs pit segments that exist
					// whether the pits fit their segments is checked on Apply and Ok
					if (addPitsCheckBox.isSelected() && (!checkSegmentNames(PitProperties.this) || !checkOrder(PitProperties.this)))
					{
						addPitsCheckBox.setSelected(false);
						return;
					}
					if (addPitsCheckBox.isSelected())
					{
						getLengthsCheckBox().setSelected(false);
						setLengthsEnabled(false);
						addTabs();
					}
					setButtonsVisible(addPitsCheckBox.isSelected());
				}
			});
		}
		return addPitsCheckBox;
	}

	private void setButtonsVisible(boolean visible)
	{
		getApplyButton().setVisible(visible);
		getUndoButton().setVisible(visible);
		getRedoButton().setVisible(visible);
		getTabbedPane().setVisible(visible);
	}

	private JButton getApplyButton()
	{
		if (applyButton == null)
		{
			applyButton = new JButton("Apply");
			applyButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					doGenerate();
				}
			});
		}
		return applyButton;
	}

	/** @return where the game places the pits, or null */
	private gui.view3d.GameTrack.PitLayout pitLayout()
	{
		try
		{
			gui.view3d.GameTrack track = gui.view3d.GameTrack.build(getEditorFrame());
			return track == null ? null : track.pitPlaces();
		}
		catch (RuntimeException e)
		{
			return null;
		}
	}

	/** The stop buildings segment (the pit end in TORCS): its name as the dialog calls it */
	private String stopLabel()
	{
		return getEditorFrame().getGameType().hasPitOptions() ? "Stop Buildings" : "End";
	}

	/**
	 * Checks that the pit segments typed in the dialog are segments of the track
	 * (the game finds them by their exact name), and if some aren't, asks for the
	 * right ones: for each, a list of the track's segments, the closest name
	 * picked, or none.
	 *
	 * @return true when they all exist or were fixed, false when the user cancelled
	 */
	public boolean checkSegmentNames(java.awt.Component parent)
	{
		boolean options = getEditorFrame().getGameType().hasPitOptions();
		JTextField[] fields = options
			? new JTextField[] { entryTextField, startTextField, startBuildingsTextField, stopBuildingsTextField, endTextField, exitTextField }
			: new JTextField[] { entryTextField, startTextField, endTextField, exitTextField };
		String[] labels = options
			? new String[] { "Entry", "Start", "Start Buildings", "Stop Buildings", "End", "Exit" }
			: new String[] { "Entry", "Start", "End", "Exit" };
		SegmentVector data = getEditorFrame().getTrackData().getSegments();
		String[] names = new String[data.size()];
		for (int i = 0; i < names.length; i++)
			names[i] = data.get(i).getName();

		JPanel panel = new JPanel(new java.awt.GridBagLayout());
		java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
		c.insets = new java.awt.Insets(3, 3, 3, 3);
		c.anchor = java.awt.GridBagConstraints.WEST;
		java.util.List<JTextField> bad = new java.util.ArrayList<JTextField>();
		java.util.List<JComboBox<String>> choices = new java.util.ArrayList<JComboBox<String>>();
		for (int i = 0; i < fields.length; i++)
		{
			String name = fields[i].getText().trim();
			if (name.isEmpty() || data.getSegmentFromName(name) != null)
				continue;
			JComboBox<String> choice = new JComboBox<String>();
			choice.addItem("(none)");
			for (String segment : names)
				choice.addItem(segment);
			choice.setSelectedItem(closestName(name, names));
			c.gridy = bad.size();
			c.gridx = 0;
			panel.add(new JLabel(labels[i] + ": \"" + name + "\" isn't a segment"), c);
			c.gridx = 1;
			panel.add(choice, c);
			bad.add(fields[i]);
			choices.add(choice);
		}
		if (bad.isEmpty())
			return true;

		c.gridy = bad.size();
		c.gridx = 0;
		c.gridwidth = 2;
		panel.add(new JLabel("Pick the segments to use instead:"), c);
		int answer = JOptionPane.showConfirmDialog(parent, panel, "Pit Segments", JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.WARNING_MESSAGE);
		if (answer != JOptionPane.OK_OPTION)
			return false;
		for (int i = 0; i < bad.size(); i++)
		{
			Object picked = choices.get(i).getSelectedItem();
			bad.get(i).setText(picked == null || "(none)".equals(picked) ? "" : picked.toString());
		}
		return true;
	}

	/** @return whether the pit segments typed in the dialog are in order round the track; says what isn't */
	public boolean checkOrder(java.awt.Component parent)
	{
		String problem = Pits.orderProblem(getEditorFrame().getTrackData().getSegments(), entryTextField.getText(),
			startTextField.getText(), startBuildingsTextField.getText(), stopBuildingsTextField.getText(),
			endTextField.getText(), exitTextField.getText(), getEditorFrame().getGameType().hasPitOptions());
		if (problem == null)
			return true;
		JOptionPane.showMessageDialog(parent, "<html><body style='width: 400px'>" + problem + ".</body></html>", "Pit Segments",
			JOptionPane.ERROR_MESSAGE);
		return false;
	}

	/**
	 * Tapers the pit side of segments evenly along their length: the outer edge
	 * (border and side) goes from one width at the start of the first segment to
	 * another at the end of the last, so the side is that less the border.
	 */
	private static void taper(java.util.List<Segment> segments, boolean left, double from, double to, double border)
	{
		if (Double.isNaN(border))
			border = 0;
		double total = 0;
		for (Segment segment : segments)
			total += gui.view.SegmentSplitter.formulaLength(segment);
		if (!(total > 0))
			return;
		double along = 0;
		for (Segment segment : segments)
		{
			SegmentSide side = left ? segment.getLeft() : segment.getRight();
			double startWidth = from + (to - from) * along / total - border;
			along += gui.view.SegmentSplitter.formulaLength(segment);
			double endWidth = from + (to - from) * along / total - border;
			side.setSideStartWidth(Math.max(0, startWidth));
			side.setSideEndWidth(Math.max(0, endWidth));
		}
	}

	/**
	 * @return where the game places the pits with the values typed in the dialog,
	 *         without changing the track: the dialog's values are tried on a copy
	 *         of the pits
	 */
	private gui.view3d.GameTrack.PitLayout pitLayoutOfDialog()
	{
		utils.circuit.MainTrack mainTrack = getEditorFrame().getTrackData().getMainTrack();
		Pits pits = mainTrack.getPits();
		boolean modified = getEditorFrame().documentIsModified;
		try
		{
			mainTrack.setPits(clonePits(pits));
			applyFields();
			return pitLayout();
		}
		finally
		{
			mainTrack.setPits(pits);
			getEditorFrame().documentIsModified = modified;
		}
	}

	/**
	 * Warns when the last pit doesn't end where it should (see fitSegments),
	 * with the values typed in the dialog, and offers to fit the segments.
	 *
	 * @return true to go on (fitted, or ignored), false when the user cancelled
	 *         or the fit didn't work
	 */
	public boolean checkBuildingsEnd(java.awt.Component parent)
	{
		gui.view3d.GameTrack.PitLayout layout = pitLayoutOfDialog();
		String problem = layout == null ? null : layout.problem();
		if (problem == null)
			return true;
		String[] options = { "Fit Segments", "Ignore", "Cancel" };
		int answer = JOptionPane.showOptionDialog(parent, "<html><body style='width: 400px'>" + problem
			+ ".<br><br>Fit Segments fits the segments to the pits; Ignore leaves them as they are.</body></html>",
			"Pit Buildings", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		if (answer == 1)
			return true;
		if (answer != 0)
			return false;
		fitSegments();
		// it may not have worked (a spiral), and says why
		layout = pitLayoutOfDialog();
		return layout == null || layout.problem() == null;
	}

	/** @return the segment name most like the name: the fewest letters to change, ignoring case */
	private static String closestName(String name, String[] names)
	{
		String best = "(none)";
		int bestDistance = Integer.MAX_VALUE;
		String a = name.toLowerCase();
		for (String candidate : names)
		{
			if (candidate == null)
				continue;
			String b = candidate.toLowerCase();
			int[] previous = new int[b.length() + 1], current = new int[b.length() + 1];
			for (int j = 0; j <= b.length(); j++)
				previous[j] = j;
			for (int i = 1; i <= a.length(); i++)
			{
				current[0] = i;
				for (int j = 1; j <= b.length(); j++)
					current[j] = Math.min(Math.min(current[j - 1] + 1, previous[j] + 1),
						previous[j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1));
				int[] swap = previous;
				previous = current;
				current = swap;
			}
			int distance = previous[b.length()];
			if (distance < bestDistance)
			{
				bestDistance = distance;
				best = candidate;
			}
		}
		// nothing like it: leave it to the user
		return bestDistance <= Math.max(2, name.length() / 2) ? best : "(none)";
	}

	/**
	 * Moves where the pits start, so the finish line is a whole number of pits
	 * from it: splits the segment there, and makes its second piece the start
	 * buildings segment (the pit start in TORCS). The pits start earlier when
	 * there's room in the pit lane, otherwise later.
	 *
	 * @return what was done, and what went wrong or null
	 */
	private String[] fitStart(gui.view3d.GameTrack.PitLayout layout, boolean torcs)
	{
		Pits pits = getEditorFrame().getPits();
		SegmentVector data = getEditorFrame().getTrackData().getSegments();
		int n = data.size();
		double[] pos = new double[n + 1];
		for (int i = 0; i < n; i++)
			pos[i + 1] = pos[i] + gui.view.SegmentSplitter.formulaLength(data.get(i));
		double trackLength = pos[n];
		Segment first = data.getSegmentFromName(layout.firstName);
		if (first == null)
			return new String[] { null, "The segment the pits start in, " + layout.firstName + ", wasn't found" };
		double len = layout.len;
		double from = pos[data.indexOf(first)];
		double toLine = trackLength - from;
		double rest = toLine % len;
		// the earliest the pits can start: the start of the pit start segment, or
		// in TORCS, where the pit start is, the segment after the pit entry
		Segment lowest = torcs ? data.getSegmentFromName(pits.getEntry()) : data.getSegmentFromName(pits.getStart());
		double room = 0;
		if (lowest != null)
		{
			int i = data.indexOf(lowest);
			double low = torcs ? pos[i + 1] : pos[i];
			room = ((from - low) % trackLength + trackLength) % trackLength;
		}
		double at = room >= len - rest - 0.005 ? from - (len - rest) : from + rest;
		if (at < 0)
			at += trackLength;
		if (at >= trackLength - 0.005)
			at = 0;

		int i = 0;
		while (i < n - 1 && pos[i + 1] <= at + 0.005)
			i++;
		Segment segment = data.get(i);
		Segment start;
		String message = "";
		if (Math.abs(at - pos[i]) < 0.005)
			start = segment;
		else
		{
			gui.view.SegmentSplitter splitter = new gui.view.SegmentSplitter(getEditorFrame());
			String name = segment.getName();
			start = splitter.splitAtFraction(segment, (at - pos[i]) / (pos[i + 1] - pos[i]));
			if (start == null)
				return new String[] { null, name + " couldn't be split where the pits should start: " + splitter.getProblem() };
			message = String.format("%s was split %.2f m from its start, and %s is its second piece.%n", name, at - pos[i], start.getName());
		}
		if (torcs)
			pits.setStart(start.getName());
		else
			pits.setStartBuildings(start.getName());
		double before = (trackLength - at) % trackLength;
		message += String.format("The pits now start at %s, %.2f m (%d pits) before the finish line, so the pit boxes line up across it.%n",
			start.getName(), before, Math.round(before / len));
		return new String[] { message, null };
	}

	/**
	 * Makes the pits fill whole segments: splits the segment the last pit ends
	 * in, there. In TORCS the first piece becomes the pit end. In Speed Dreams and
	 * CSR the segment after the pits becomes the stop buildings segment and max
	 * pits is set to the number of pits: the game leaves the stop buildings
	 * segment out of the pits' stretch, where it replaces the barrier with an
	 * invisible one, so the segments the pits stand on all have to come before
	 * it. The game places the pits from the start of the start buildings
	 * segment, so they always start at a segment's start.
	 */
	private void fitSegments()
	{
		// the pit segments have to exist, in order
		if (!checkSegmentNames(this) || !checkOrder(this))
			return;
		boolean wasModified = getEditorFrame().documentIsModified;
		saveInitialState();
		UndoPitGeneration undo = new UndoPitGeneration(getEditorFrame());
		// fit the pits as they are in the dialog
		applyFields();
		gui.view3d.GameTrack.PitLayout layout = pitLayout();
		if (layout == null || layout.endName == null)
		{
			undo.undo();
			getEditorFrame().documentIsModified = wasModified;
			JOptionPane.showMessageDialog(this, "There are no pits to fit: the pits need an entry, start, end, exit and side.",
				"Fit Segments", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		if (layout.endsOnStop() && layout.lineProblem() == null)
		{
			JOptionPane.showMessageDialog(this, "The pits already fit: the last pit ends at the end of " + layout.endName
				+ ", and the " + stopLabel() + " segment is " + layout.stopName + ".", "Fit Segments", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		Pits pits = getEditorFrame().getPits();
		boolean torcs = !getEditorFrame().getGameType().hasPitOptions();
		SegmentVector data = getEditorFrame().getTrackData().getSegments();
		String message = "";

		// first where the pits start, which moves where they end
		if (layout.lineProblem() != null)
		{
			String[] result = fitStart(layout, torcs);
			if (result[1] != null)
			{
				undo.undo();
				getEditorFrame().documentIsModified = wasModified;
				refillFields();
				refreshView();
				JOptionPane.showMessageDialog(this, result[1] + ", so nothing was changed.", "Fit Segments", JOptionPane.WARNING_MESSAGE);
				return;
			}
			message = result[0];
			refreshView();
			layout = pitLayout();
			if (layout == null || layout.endName == null)
			{
				undo.undo();
				getEditorFrame().documentIsModified = wasModified;
				refillFields();
				refreshView();
				JOptionPane.showMessageDialog(this, "Moving the start of the pits left no pits to fit, so nothing was changed.",
					"Fit Segments", JOptionPane.WARNING_MESSAGE);
				return;
			}
		}

		Segment segment = null;
		boolean spiral = false;
		if (!layout.endsOnStopSegment())
		{
			segment = data.getSegmentFromName(layout.endName);
			// before it is split
			spiral = segment instanceof utils.circuit.Curve
				&& ((utils.circuit.Curve) segment).getValidRadiusEnd() != ((utils.circuit.Curve) segment).getRadiusStart();
			String next = layout.nextName;
			if (Math.abs(layout.endInto - layout.endLength) >= 0.005f)
			{
				gui.view.SegmentSplitter splitter = new gui.view.SegmentSplitter(getEditorFrame());
				Segment piece2 = segment == null ? null : splitter.splitAtFraction(segment, layout.endInto / layout.endLength);
				if (piece2 == null)
				{
					undo.undo();
					getEditorFrame().documentIsModified = wasModified;
					JOptionPane.showMessageDialog(this, layout.endName + " couldn't be split: "
						+ (segment == null ? "it wasn't found" : splitter.getProblem()), "Fit Segments", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// the pit lane keeps its end and exit: the end of the second piece; but
				// when the pits went on past the pit end, the first piece becomes the
				// pit end below, so the pit lane reaches the last pit
				if (!torcs && layout.endName.equals(pits.getEnd()) && !layout.pastPitEnd)
					pits.setEnd(piece2.getName());
				if (layout.endName.equals(pits.getExit()))
					pits.setExit(piece2.getName());
				next = piece2.getName();
				message += String.format("%s was split %.2f m from its start, and %s is its second piece.%n", layout.endName,
					layout.endInto, piece2.getName());
			}
			int count = layout.pits.size();
			if (torcs)
			{
				pits.setEnd(layout.endName);
				message += "The pit end is now " + layout.endName + ", where the last pit ends.";
			}
			else
			{
				pits.setStopBuildings(next);
				pits.setMaxPits(count);
				message += String.format("The stop buildings segment is now %s, the one after the pits, and max pits is %d.%n"
					+ "So %s is part of the pits: the game doesn't draw its barrier through them, but cars still hit it.",
					next, count, layout.endName);
				// the pit lane goes on to the end of the last pit
				if (layout.pastPitEnd)
				{
					pits.setEnd(layout.endName);
					message += String.format("%nThe pits went past the pit end %s, so the pit end is now %s and the pit lane reaches the last pit.",
						layout.pitEndName, layout.endName);
					int exit = data.indexOf(data.getSegmentFromName(pits.getExit()));
					int start = data.indexOf(data.getSegmentFromName(pits.getStart()));
					int newEnd = data.indexOf(data.getSegmentFromName(layout.endName));
					int n = data.size();
					if (exit >= 0 && start >= 0 && newEnd >= 0 && (exit - start + n) % n <= (newEnd - start + n) % n)
						message += String.format("%nThe pit exit %s is now at or before the pit end: it has to come after it.", pits.getExit());
				}
			}
		}

		// Speed Dreams and CSR leave out the barrier along all but the last step of
		// the stop buildings segment: make it one step long, where the pits end
		if (!torcs)
		{
			refreshView();
			gui.view3d.GameTrack.PitLayout now = pitLayout();
			if (now != null && now.endsOnStopSegment() && now.stopSteps > 1)
			{
				Segment stop = data.getSegmentFromName(now.stopName);
				double length = stop == null ? 0 : gui.view.SegmentSplitter.formulaLength(stop);
				gui.view.SegmentSplitter splitter = new gui.view.SegmentSplitter(getEditorFrame());
				Segment piece2 = stop == null || !(length > 0) ? null : splitter.splitAtFraction(stop, now.stopStepLength / length);
				if (piece2 == null)
				{
					undo.undo();
					getEditorFrame().documentIsModified = wasModified;
					refillFields();
					refreshView();
					JOptionPane.showMessageDialog(this, now.stopName + " couldn't be split to make the stop buildings segment one step long: "
						+ (stop == null ? "it wasn't found" : splitter.getProblem()) + ", so nothing was changed.",
						"Fit Segments", JOptionPane.WARNING_MESSAGE);
					return;
				}
				// the pit lane and its exit keep their end
				if (now.stopName.equals(pits.getEnd()))
					pits.setEnd(piece2.getName());
				if (now.stopName.equals(pits.getExit()))
					pits.setExit(piece2.getName());
				message += String.format((message.isEmpty() ? "" : "%n") + "%s was split %.2f m from its start, and %s is its second piece: the stop buildings segment "
					+ "is one step long, as the game leaves out the barrier along all but its last step.", now.stopName,
					now.stopStepLength, piece2.getName());
			}
		}

		// a spiral is only split where one of the game's steps ends: not good enough
		// (the pieces' shapes are worked out when the track is drawn)
		refreshView();
		gui.view3d.GameTrack.PitLayout after = pitLayout();
		String problem = after == null ? "the pits are gone" : after.endProblem() != null ? after.endProblem() : after.lineProblem();
		if (problem != null)
		{
			double at = segment == null ? 0 : gui.view.SegmentSplitter.formulaLength(segment);
			undo.undo();
			getEditorFrame().documentIsModified = wasModified;
			refillFields();
			refreshView();
			JOptionPane.showMessageDialog(this, spiral && after != null && after.endProblem() != null
				? String.format("The last pit ends %.2f m into %s, a spiral the game builds in steps.%n"
					+ "It can only be split where a step ends, and the nearest is %.2f m from its start, so nothing was changed.%n"
					+ "Change the pit length or max pits, or the segment, so the last pit ends elsewhere.",
					layout.endInto, layout.endName, at)
				: "The pits still wouldn't fit (" + problem + "), so nothing was changed.",
				"Fit Segments", JOptionPane.WARNING_MESSAGE);
			return;
		}
		refillFields();

		undo.captureAfter();
		Undo.add(undo);
		generateCount++;
		undoCount = 0;
		updateGenerateButtons();
		refreshView();
		getEditorFrame().documentIsModified = true;
		JOptionPane.showMessageDialog(this, message, "Fit Segments", JOptionPane.INFORMATION_MESSAGE);
	}

	private JButton getUndoButton()
	{
		if (undoButton == null)
		{
			undoButton = new JButton("Undo");
			undoButton.setEnabled(false);
			undoButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					doUndoGenerate();
				}
			});
		}
		return undoButton;
	}

	private JButton getRedoButton()
	{
		if (redoButton == null)
		{
			redoButton = new JButton("Redo");
			redoButton.setEnabled(false);
			redoButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					doRedoGenerate();
				}
			});
		}
		return redoButton;
	}

	/**
	 * Saves the initial state when the first generate is done, so cancel
	 * can restore it. Each generate can be individually undone.
	 */
	private void saveInitialState()
	{
		if (initialSegments == null)
		{
			initialSegments = cloneSegments(getEditorFrame().getTrackData().getSegments());
			initialPits = clonePits(getEditorFrame().getPits());
			initialModified = getEditorFrame().documentIsModified;
		}
	}

	private static Vector<Segment> cloneSegments(SegmentVector data)
	{
		Vector<Segment> clones = new Vector<Segment>(data.size());
		for (int i = 0; i < data.size(); i++)
			clones.add((Segment) data.get(i).clone());
		return clones;
	}

	private static Pits clonePits(Pits original)
	{
		Pits copy = new Pits();
		copy.setSide(original.getSide());
		copy.setEntry(original.getEntry());
		copy.setStart(original.getStart());
		copy.setStartBuildings(original.getStartBuildings());
		copy.setStopBuildings(original.getStopBuildings());
		copy.setMaxPits(original.getMaxPits());
		copy.setEnd(original.getEnd());
		copy.setExit(original.getExit());
		copy.setWidth(original.getWidth());
		copy.setLength(original.getLength());
		copy.setStyle(original.getStyle());
		copy.setIndicator(original.getIndicator());
		copy.setSpeedLimitValue(original.getSpeedLimitValue());
		copy.setSpeedLimitUnits(original.getSpeedLimitUnits());
		copy.copyGeneratorFrom(original);
		return copy;
	}

	private void doGenerate()
	{
		if (getLengthsCheckBox().isSelected())
		{
			doGenerateFromLengths();
			return;
		}
		// the pit segments have to exist, be in order, and should fit the pits
		if (!checkSegmentNames(this) || !checkOrder(this) || !checkBuildingsEnd(this))
			return;
		// check the tabs first, so nothing is changed when something is missing
		String problem = generateProblem();
		if (problem != null)
		{
			JOptionPane.showMessageDialog(this, problem, "Add Pits", JOptionPane.ERROR_MESSAGE);
			return;
		}

		boolean wasModified = getEditorFrame().documentIsModified;
		saveInitialState();
		UndoPitGeneration undo = new UndoPitGeneration(getEditorFrame());
		// generate from what's in the dialog, not the last accepted values
		applyFields();
		storeTabs();
		boolean done;
		try
		{
			done = createPits();
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "The pits couldn't be added: " + e, "Add Pits", JOptionPane.ERROR_MESSAGE);
			done = false;
		}
		if (!done)
		{
			// put back the track as it was: no undo step, and not modified
			// if it wasn't; the dialog keeps what was typed so it can be fixed
			undo.undo();
			getEditorFrame().documentIsModified = wasModified;
			return;
		}
		// so a later change to the pit segments shows their settings, not these
		getEditorFrame().getPits().getGenerator().setSegmentsKey(pitSegmentsKey());
		undo.captureAfter();
		Undo.add(undo);
		generateCount++;
		undoCount = 0;
		updateGenerateButtons();
		refreshView();
		getEditorFrame().documentIsModified = true;
	}

	private JCheckBox getLengthsCheckBox()
	{
		if (lengthsCheckBox == null)
		{
			lengthsCheckBox = new JCheckBox("using lengths");
			lengthsCheckBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					// the pit segments are made on Apply, so they needn't exist yet
					if (lengthsCheckBox.isSelected())
					{
						getAddPitsCheckBox().setSelected(false);
						addTabs();
					}
					setLengthsEnabled(lengthsCheckBox.isSelected());
					setButtonsVisible(lengthsCheckBox.isSelected());
				}
			});
		}
		return lengthsCheckBox;
	}

	private JComboBox<String> getPitsOffsetComboBox()
	{
		if (pitsOffsetComboBox == null)
		{
			pitsOffsetComboBox = new JComboBox<String>(new String[] { CENTER_ON_LINE, OFFSET_FROM_LINE });
			pitsOffsetComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					// the offset starts from the centered one
					pitsOffsetTextField.setEditable(!centered());
					updateLengthReadOuts();
				}
			});
		}
		return pitsOffsetComboBox;
	}

	private boolean centered()
	{
		return CENTER_ON_LINE.equals(getPitsOffsetComboBox().getSelectedItem());
	}

	/** The lengths can only be changed when adding pits using lengths */
	private void setLengthsEnabled(boolean enabled)
	{
		JTextField[] fields = { entryLengthTextField, pitsOffsetTextField, pitsLengthTextField, buildingsOffsetTextField,
			pitCountTextField, exitLengthTextField };
		for (JTextField field : fields)
			field.setEnabled(enabled);
		getPitsOffsetComboBox().setEnabled(enabled);
		pitsOffsetTextField.setEditable(!centered());
	}

	/** @return the length of a pit: the dialog's, or the default */
	private double pitLength()
	{
		double length = getDouble(lengthTextField.getText());
		return length > 0 ? length : 15.0;
	}

	/** @return the value without trailing zeros, to the cm */
	private static String meters(double value)
	{
		String text = String.format(java.util.Locale.US, "%.2f", value);
		if (text.contains("."))
			text = text.replaceAll("0+$", "").replaceAll("\\.$", "");
		return "-0".equals(text) ? "0" : text;
	}

	/** Shows the centered offset, and how long the buildings are or how many pits fit */
	private void updateLengthReadOuts()
	{
		double pitsLength = getDouble(pitsLengthTextField.getText());
		if (centered())
			pitsOffsetTextField.setText(pitsLength > 0 ? meters(-pitsLength / 2) : "");
		if (getEditorFrame().getGameType().hasPitOptions())
		{
			int count = getInteger(pitCountTextField.getText());
			pitCountUnitsLabel.setText(count != Integer.MAX_VALUE && count > 0 ? "buildings " + meters(count * pitLength()) + " m" : "");
			pitsLengthUnitsLabel.setText("m");
		}
		else
		{
			// TORCS fills the pits with as many pits as fit
			int count = pitsLength > 0 ? (int) Math.floor(pitsLength / pitLength() + 1e-6) : 0;
			pitsLengthUnitsLabel.setText(pitsLength > 0 ? "m (" + count + (count == 1 ? " pit)" : " pits)") : "m");
		}
	}

	/** @return where each segment starts along the track from the start line, and at the end the track's length */
	private static double[] positions(SegmentVector data)
	{
		double[] pos = new double[data.size() + 1];
		for (int i = 0; i < data.size(); i++)
			pos[i + 1] = pos[i] + gui.view.SegmentSplitter.formulaLength(data.get(i));
		return pos;
	}

	/** @return the distance along the track, between 0 and its length */
	private static double wrap(double at, double trackLength)
	{
		double wrapped = (at % trackLength + trackLength) % trackLength;
		return wrapped > trackLength - 0.005 ? 0 : wrapped;
	}

	/**
	 * Fills the lengths from the track's pits, when it has them, otherwise with
	 * defaults: the pits centered on the start line.
	 */
	private void fillLengthFields()
	{
		Pits pits = getEditorFrame().getPits();
		SegmentVector data = getEditorFrame().getTrackData().getSegments();
		double entryLength = 120, offset = Double.NaN, pitsLength = 300, buildingsOffset = 15, exitLength = 120;
		int count = 10;
		double[] pos = positions(data);
		double trackLength = pos[data.size()];
		int start = indexOf(data, pits.getStart()), end = indexOf(data, pits.getEnd());
		if (trackLength > 0 && start >= 0 && end >= 0)
		{
			double from = pos[start], to = pos[end + 1];
			pitsLength = wrap(to - from, trackLength);
			offset = from > trackLength / 2 ? from - trackLength : from;
			int entry = indexOf(data, pits.getEntry()), exit = indexOf(data, pits.getExit());
			if (entry >= 0)
				entryLength = wrap(from - pos[entry], trackLength);
			if (exit >= 0)
				exitLength = wrap(pos[exit + 1] - to, trackLength);
			int startBuildings = indexOf(data, pits.getStartBuildings());
			if (startBuildings >= 0)
				buildingsOffset = wrap(pos[startBuildings] - from, trackLength);
			if (buildingsOffset >= pitsLength)
				buildingsOffset = 0;
			int stopBuildings = indexOf(data, pits.getStopBuildings());
			if (pits.getMaxPits() != Integer.MAX_VALUE)
				count = pits.getMaxPits();
			else if (stopBuildings >= 0)
				count = (int) Math.floor(wrap(pos[stopBuildings] - from - buildingsOffset, trackLength) / pitLength() + 1e-6);
		}
		entryLengthTextField.setText(meters(entryLength));
		pitsLengthTextField.setText(meters(pitsLength));
		buildingsOffsetTextField.setText(meters(buildingsOffset));
		pitCountTextField.setText(count + "");
		exitLengthTextField.setText(meters(exitLength));
		boolean center = Double.isNaN(offset) || Math.abs(offset + pitsLength / 2) < 0.01;
		getPitsOffsetComboBox().setSelectedItem(center ? CENTER_ON_LINE : OFFSET_FROM_LINE);
		if (!center)
			pitsOffsetTextField.setText(meters(offset));
	}

	private static int indexOf(SegmentVector data, String name)
	{
		if (name == null || name.trim().isEmpty())
			return -1;
		Segment segment = data.getSegmentFromName(name.trim());
		return segment == null ? -1 : data.indexOf(segment);
	}

	/** Where the pit segments go, along the track from the start line */
	private static class PitLengths
	{
		double	entryStart, pitStart, buildingsStart, buildingsEnd, pitEnd, exitEnd;
		int		count;
	}

	/** @return the lengths in the dialog, or null when one is wrong, after saying which */
	private PitLengths readLengths(boolean torcs, double trackLength)
	{
		double pitLength = pitLength();
		double entry = getDouble(entryLengthTextField.getText());
		double pitsLength = getDouble(pitsLengthTextField.getText());
		double offset = centered() ? -pitsLength / 2 : getDouble(pitsOffsetTextField.getText());
		double exit = getDouble(exitLengthTextField.getText());
		double buildings = torcs ? 0 : getDouble(buildingsOffsetTextField.getText());
		int count = torcs ? (pitsLength > 0 ? (int) Math.floor(pitsLength / pitLength + 1e-6) : 0) : getInteger(pitCountTextField.getText());
		String problem = null;
		if (!(entry > 0))
			problem = "Entry Length has to be more than 0";
		else if (!(pitsLength > 0))
			problem = "Pits Length has to be more than 0";
		else if (Double.isNaN(offset))
			problem = "Pits Offset has to be a number";
		else if (!(exit > 0))
			problem = "Exit Length has to be more than 0";
		else if (!(buildings >= 0))
			problem = "Buildings Offset can't be less than 0";
		else if (torcs && count < 1)
			problem = "Pits Length has to be at least one pit long (" + meters(pitLength) + " m)";
		else if (torcs && pitsLength - count * pitLength > 0.01)
			problem = String.format("TORCS fills the pits with pits from the pit start to the pit end, so Pits Length has to be "
				+ "a whole number of pits: %s or %s m", meters(count * pitLength), meters((count + 1) * pitLength));
		else if (count == Integer.MAX_VALUE || count < 1)
			problem = "Number of Pits has to be 1 or more";
		else if (entry + pitsLength + exit > trackLength - 0.01)
			problem = String.format("The entry, pits and exit are %s m long, but the track is only %s m long",
				meters(entry + pitsLength + exit), meters(trackLength));
		else if (!torcs && buildings + count * pitLength > pitsLength - 0.01)
			problem = String.format("The buildings end %s m from the pit start (Buildings Offset and %d pits of %s m), "
				+ "but they have to end before the pits do, at %s m: the stop buildings segment comes after them, in the pits",
				meters(buildings + count * pitLength), count, meters(pitLength), meters(pitsLength));
		if (problem != null)
		{
			JOptionPane.showMessageDialog(this, problem + ".", "Add Pits", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		PitLengths lengths = new PitLengths();
		lengths.count = count;
		lengths.entryStart = wrap(offset - entry, trackLength);
		lengths.pitStart = wrap(offset, trackLength);
		lengths.buildingsStart = wrap(offset + buildings, trackLength);
		lengths.buildingsEnd = wrap(offset + buildings + count * pitLength, trackLength);
		lengths.pitEnd = wrap(offset + pitsLength, trackLength);
		lengths.exitEnd = wrap(offset + pitsLength + exit, trackLength);
		return lengths;
	}

	/**
	 * Trackgen starts the pit side's texture again at the start line, so when the
	 * pits go across it, the pit boxes after it only line up when it's a whole
	 * number of pits from the first pit. Offers to move the buildings (the pits in
	 * TORCS) so it is.
	 *
	 * @return false when the user cancelled
	 */
	private boolean checkLengthsLine(PitLengths lengths, boolean torcs, double trackLength)
	{
		double pitLength = pitLength();
		// the first pit, and how far the start line is from it
		double first = torcs ? lengths.pitStart : lengths.buildingsStart;
		double toLine = first == 0 ? 0 : trackLength - first;
		double rest = toLine % pitLength;
		if (toLine >= lengths.count * pitLength - 0.005 || rest < 0.005 || pitLength - rest < 0.005)
			return true;

		// earlier or later, whichever moves them less and still fits
		double earlier = pitLength - rest, later = rest;
		double pitsLength = getDouble(pitsLengthTextField.getText());
		double offset = centered() ? -pitsLength / 2 : getDouble(pitsOffsetTextField.getText());
		double buildings = torcs ? 0 : getDouble(buildingsOffsetTextField.getText());
		boolean canEarlier = torcs || buildings - earlier >= -0.005;
		boolean canLater = torcs || buildings + later + lengths.count * pitLength <= pitsLength - 0.01;
		double shift = canLater && (later <= earlier || !canEarlier) ? later : canEarlier ? -earlier : Double.NaN;

		String message = String.format("The start line is %s m into the pits (%d pits and %s m): trackgen starts the pit box "
			+ "texture again at the line, so the boxes after it won't line up with the pits.", meters(toLine),
			(int) Math.floor(toLine / pitLength), meters(rest));
		String[] options;
		if (Double.isNaN(shift))
		{
			message += "<br><br>Change the lengths so the line is a whole number of pits from the first pit, or Ignore to go on.";
			options = new String[] { "Ignore", "Cancel" };
		}
		else
		{
			message += String.format("<br><br>Line Up moves the %s %s m %s, so %s.", torcs ? "pits" : "buildings",
				meters(Math.abs(shift)), shift < 0 ? "earlier" : "later",
				torcs ? "Pits Offset becomes " + meters(offset + shift) + " m" : "Buildings Offset becomes " + meters(buildings + shift) + " m");
			options = new String[] { "Line Up", "Ignore", "Cancel" };
		}
		int answer = JOptionPane.showOptionDialog(this, "<html><body style='width: 400px'>" + message + "</body></html>",
			"Pit Boxes", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		if (answer < 0 || options[answer].equals("Cancel"))
			return false;
		if (options[answer].equals("Ignore"))
			return true;
		if (torcs)
		{
			getPitsOffsetComboBox().setSelectedItem(OFFSET_FROM_LINE);
			pitsOffsetTextField.setText(meters(offset + shift));
		}
		else
			buildingsOffsetTextField.setText(meters(buildings + shift));
		return true;
	}

	/**
	 * Makes a segment start where given: uses a segment boundary that's already
	 * there, or moves one between two straights when it's near (so the track still
	 * closes), or splits the segment there.
	 *
	 * @param keep where boundaries are wanted: these aren't moved
	 * @return what was done, or null when nothing, and a problem or null
	 */
	private String[] placeBoundary(double at, java.util.List<Double> keep)
	{
		SegmentVector data = getEditorFrame().getTrackData().getSegments();
		int n = data.size();
		double[] pos = positions(data);
		for (int i = 0; i <= n; i++)
			if (Math.abs(pos[i] - at) < 0.01)
				return new String[] { null, null };

		int i = 0;
		while (i < n - 1 && pos[i + 1] <= at)
			i++;
		// the nearer end of the segment first; not the start line
		int[] ends = at - pos[i] <= pos[i + 1] - at ? new int[] { i, i + 1 } : new int[] { i + 1, i };
		for (int b : ends)
		{
			if (b == 0 || b == n || Math.abs(pos[b] - at) > pitLength() / 2)
				continue;
			boolean kept = false;
			for (double k : keep)
				kept |= Math.abs(pos[b] - k) < 0.01;
			Segment before = data.get(b - 1), after = data.get(b);
			if (kept || before instanceof utils.circuit.Curve || after instanceof utils.circuit.Curve)
				continue;
			double shift = at - pos[b];
			double lengthBefore = pos[b] - pos[b - 1] + shift, lengthAfter = pos[b + 1] - pos[b] - shift;
			if (lengthBefore < 1 || lengthAfter < 1)
				continue;
			before.setLength(lengthBefore);
			after.setLength(lengthAfter);
			getEditorFrame().getTrackData().calculateSegmentValues();
			return new String[] { String.format("The end of %s was moved %s m %s, so it's %s m long and %s %s m.", before.getName(),
				meters(Math.abs(shift)), shift < 0 ? "earlier" : "later", meters(lengthBefore), after.getName(), meters(lengthAfter)), null };
		}

		Segment segment = data.get(i);
		String name = segment.getName();
		gui.view.SegmentSplitter splitter = new gui.view.SegmentSplitter(getEditorFrame());
		Segment piece2 = splitter.splitAtFraction(segment, (at - pos[i]) / (pos[i + 1] - pos[i]));
		if (piece2 == null)
			return new String[] { null, String.format("%s couldn't be split %s m from its start: %s", name, meters(at - pos[i]),
				splitter.getProblem()) };
		double actual = pos[i] + gui.view.SegmentSplitter.formulaLength(segment);
		if (Math.abs(actual - at) >= 0.01)
			return new String[] { null, String.format("%s is a spiral, which the game builds in steps, so it can only be split "
				+ "where a step ends: %s m from where it's needed. Change the lengths so the pits' segments start elsewhere", name,
				meters(Math.abs(actual - at))) };
		return new String[] { String.format("%s was split %s m from its start, and %s is its second piece.", name,
			meters(at - pos[i]), piece2.getName()), null };
	}

	/** @return the name of the segment that starts there, or ends there, or null */
	private String segmentAt(double at, boolean starts)
	{
		SegmentVector data = getEditorFrame().getTrackData().getSegments();
		int n = data.size();
		double[] pos = positions(data);
		for (int i = 0; i < n; i++)
		{
			double boundary = starts ? pos[i] : pos[i + 1];
			if (Math.abs(boundary - at) < 0.01 || Math.abs(boundary - at - pos[n]) < 0.01 || Math.abs(boundary + pos[n] - at) < 0.01)
				return data.get(i).getName();
		}
		return null;
	}

	/**
	 * Makes the pit segments from the lengths: makes segments start where the
	 * pits' zones do, and fills in the pit segments' names.
	 *
	 * @return what was done, and a problem or null
	 */
	private String[] placePitSegments(PitLengths lengths, boolean torcs)
	{
		StringBuilder done = new StringBuilder();
		java.util.List<Double> keep = new java.util.ArrayList<Double>(java.util.Arrays.asList(lengths.entryStart, lengths.pitStart,
			lengths.pitEnd, lengths.exitEnd));
		if (!torcs)
		{
			keep.add(lengths.buildingsStart);
			keep.add(lengths.buildingsEnd);
		}
		for (double at : keep)
		{
			String[] result = placeBoundary(at, keep);
			if (result[1] != null)
				return new String[] { done.toString(), result[1] };
			if (result[0] != null)
				done.append(result[0]).append("\n");
		}

		String[] names = { segmentAt(lengths.entryStart, true), segmentAt(lengths.pitStart, true),
			torcs ? "" : segmentAt(lengths.buildingsStart, true), torcs ? "" : segmentAt(lengths.buildingsEnd, true),
			segmentAt(lengths.pitEnd, false), segmentAt(lengths.exitEnd, false) };
		SegmentVector data = getEditorFrame().getTrackData().getSegments();
		for (String name : names)
		{
			if (name == null)
				return new String[] { done.toString(), "A pit segment wasn't where it should be" };
			int uses = 0;
			for (int i = 0; i < data.size(); i++)
				if (name.equals(data.get(i).getName()))
					uses++;
			if (uses > 1)
				return new String[] { done.toString(), "More than one segment is called " + name
					+ ", so the game can't tell which is a pit segment: rename one of them" };
		}
		entryTextField.setText(names[0]);
		startTextField.setText(names[1]);
		if (!torcs)
		{
			startBuildingsTextField.setText(names[2]);
			stopBuildingsTextField.setText(names[3]);
			if (showMaxPitsAndIndicator())
				maxPitsTextField.setText(lengths.count + "");
		}
		endTextField.setText(names[4]);
		exitTextField.setText(names[5]);
		applyFields();

		// Speed Dreams and CSR leave out the barrier along all but the last step of
		// the stop buildings segment: make it one step long, where the pits end
		if (!torcs)
		{
			refreshView();
			gui.view3d.GameTrack.PitLayout now = pitLayout();
			if (now != null && now.endsOnStopSegment() && now.stopSteps > 1)
			{
				Segment stop = data.getSegmentFromName(now.stopName);
				double length = stop == null ? 0 : gui.view.SegmentSplitter.formulaLength(stop);
				gui.view.SegmentSplitter splitter = new gui.view.SegmentSplitter(getEditorFrame());
				Segment piece2 = stop == null || !(length > 0) ? null : splitter.splitAtFraction(stop, now.stopStepLength / length);
				if (piece2 == null)
					return new String[] { done.toString(), now.stopName + " couldn't be split to make the stop buildings segment one step long: "
						+ (stop == null ? "it wasn't found" : splitter.getProblem()) };
				// the pit lane keeps its end
				if (now.stopName.equals(endTextField.getText().trim()))
					endTextField.setText(piece2.getName());
				done.append(String.format("%s was split %s m from its start, and %s is its second piece: the stop buildings segment "
					+ "is one step long, as the game leaves out the barrier along all but its last step.%n", now.stopName,
					meters(now.stopStepLength), piece2.getName()));
				applyFields();
			}
		}

		refreshView();
		gui.view3d.GameTrack.PitLayout layout = pitLayout();
		if (layout == null || layout.pits.isEmpty())
			return new String[] { done.toString(), "The game wouldn't place any pits: check the Type and Side" };
		if (layout.endProblem() != null)
			return new String[] { done.toString(), layout.endProblem() };
		if (!torcs && layout.pits.size() != lengths.count)
			return new String[] { done.toString(), String.format("The game would place %d pits, not %d", layout.pits.size(), lengths.count) };
		return new String[] { done.toString(), null };
	}

	/**
	 * Adds the pits using lengths: makes the pit segments, then adds the pits to
	 * them as Add Pits using existing segments does, as one undo step.
	 */
	private void doGenerateFromLengths()
	{
		boolean torcs = !getEditorFrame().getGameType().hasPitOptions();
		double trackLength = positions(getEditorFrame().getTrackData().getSegments())[getEditorFrame().getTrackData().getSegments().size()];
		if (!(trackLength > 0))
			return;
		PitLengths lengths = readLengths(torcs, trackLength);
		if (lengths == null || !checkLengthsLine(lengths, torcs, trackLength))
			return;
		// lined up: read them again
		lengths = readLengths(torcs, trackLength);
		if (lengths == null)
			return;
		// check the tabs first, so nothing is changed when something is missing
		String problem = generateProblem();
		if (problem != null)
		{
			JOptionPane.showMessageDialog(this, problem, "Add Pits", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JTextField[] names = { entryTextField, startTextField, startBuildingsTextField, stopBuildingsTextField, maxPitsTextField,
			endTextField, exitTextField };
		String[] typed = new String[names.length];
		for (int i = 0; i < names.length; i++)
			typed[i] = names[i].getText();
		boolean wasModified = getEditorFrame().documentIsModified;
		saveInitialState();
		UndoPitGeneration undo = new UndoPitGeneration(getEditorFrame());
		String[] result;
		try
		{
			result = placePitSegments(lengths, torcs);
			if (result[1] == null)
			{
				storeTabs();
				if (!createPits())
					result[1] = "";
			}
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
			result = new String[] { "", "The pits couldn't be added: " + e };
		}
		if (result[1] != null)
		{
			// put back the track as it was, and the names typed
			undo.undo();
			getEditorFrame().documentIsModified = wasModified;
			for (int i = 0; i < names.length; i++)
				names[i].setText(typed[i]);
			refreshView();
			// createPits() has said what was wrong
			if (!result[1].isEmpty())
				JOptionPane.showMessageDialog(this, "<html><body style='width: 400px'>" + result[1]
					+ ", so nothing was changed.</body></html>", "Add Pits", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// so a later change to the pit segments shows their settings, not these
		getEditorFrame().getPits().getGenerator().setSegmentsKey(pitSegmentsKey());
		undo.captureAfter();
		Undo.add(undo);
		generateCount++;
		undoCount = 0;
		updateGenerateButtons();
		refreshView();
		getEditorFrame().documentIsModified = true;
		if (!result[0].isEmpty())
			JOptionPane.showMessageDialog(this, result[0].trim(), "Add Pits", JOptionPane.INFORMATION_MESSAGE);
	}

	private void doUndoGenerate()
	{
		if (generateCount > 0)
		{
			Undo.undo();
			refillFields();
			generateCount--;
			undoCount++;
			updateGenerateButtons();
			refreshView();
		}
	}

	private void doRedoGenerate()
	{
		if (undoCount > 0)
		{
			Undo.redo();
			refillFields();
			generateCount++;
			undoCount--;
			updateGenerateButtons();
			refreshView();
		}
	}

	private void updateGenerateButtons()
	{
		getUndoButton().setEnabled(generateCount > 0);
		getRedoButton().setEnabled(undoCount > 0);
	}

	private void refreshView()
	{
		getEditorFrame().getCircuitView().redrawCircuit();
		getEditorFrame().getCircuitView().invalidate();
		getEditorFrame().getCircuitView().repaint();
	}

	/**
	 * Called when the dialog is cancelled. Restores the state from before
	 * any generation was done in this dialog session.
	 */
	public void cancel()
	{
		if (initialSegments != null)
		{
			// remove every generate step from the global undo and redo stacks.
			// Undoing a step moves it to the redo stack, where the steps already
			// undone in this dialog are waiting, so all of them are popped there.
			int steps = generateCount + undoCount;
			while (generateCount > 0)
			{
				Undo.undo();
				generateCount--;
			}
			while (steps > 0)
			{
				Undo.popRedo();
				steps--;
			}
			undoCount = 0;
			updateGenerateButtons();
			// restore to the exact initial state
			getEditorFrame().getTrackData().getSegments().replaceAll(initialSegments);
			getEditorFrame().getTrackData().getMainTrack().setPits(initialPits);
			refreshView();
			getEditorFrame().documentIsModified = initialModified;
			initialSegments = null;
			initialPits = null;
		}
	}

	/**
	 * Fills the tabbed pane with the entry, pit and exit tabs the first time
	 * the Add Pits checkbox is selected. The tabs are built only once, so the
	 * values the user enters stay when the checkbox is cleared and selected
	 * again, or when a generation is undone.
	 */
	private void addTabs()
	{
		if (getTabbedPane().getTabCount() > 0)
			return;

		readPitInfos();
		tabbedPane.addTab(entryPitInfo.name, null, new PitPanel(entryPitInfo, false), null);
		tabbedPane.addTab(pitsPitInfo.name, null, new PitPanel(pitsPitInfo, true), null);
		tabbedPane.addTab(exitPitInfo.name, null, new PitPanel(exitPitInfo, false), null);
	}

	/**
	 * Fills the generator tabs' settings: the generator's last settings when the
	 * track's pits or its project have them, otherwise what the pit segments
	 * have, or defaults.
	 */
	private void readPitInfos()
	{
		{
			Pits pits = getEditorFrame().getPits();
			Pits.Generator generator = pits.getGenerator();
			if (generator == null)
				generator = utils.Properties.getInstance().getPitGenerator();
			// not when the pit segments were changed after it made them
			if (generator != null && generator.isComplete()
				&& (generator.getSegmentsKey() == null || generator.getSegmentsKey() == pitSegmentsKey()))
			{
				fromPart(generator.getEntry(), entryPitInfo);
				fromPart(generator.getPits(), pitsPitInfo);
				fromPart(generator.getExit(), exitPitInfo);
				pavePitInfos();
				return;
			}
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
				if (onLeft(pits))
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
					readBorderValues(nextSide, entryPitInfo);
				}
				else
				{
					entryPitInfo.borderType = SegmentSide.DEFAULT_PIT_ENTRY_BORDER_STYLE;
					entryPitInfo.borderSurface = SegmentSide.DEFAULT_PIT_ENTRY_BORDER_SURFACE;
					entryPitInfo.borderHeight = SegmentSide.DEFAULT_PIT_ENTRY_BORDER_HEIGHT;
					entryPitInfo.borderWidth = SegmentSide.DEFAULT_PIT_ENTRY_BORDER_WIDTH;
				}
				// the surface the game uses there: often it is inherited, not set on the segment
				entryPitInfo.surface = onLeft(pits) ? pitEntry.getValidLeftSideSurface(getEditorFrame()) : pitEntry.getValidRightSideSurface(getEditorFrame());
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

			if (pitStart != null)
			{
				SegmentSide side = null;
				if (onLeft(pits))
				{
					side = pitStart.getLeft();
				}
				else
				{
					side = pitStart.getRight();
				}

				if (side.getHasBorder())
				{
					readBorderValues(side, pitsPitInfo);
				}
				else
				{
					pitsPitInfo.borderType = SegmentSide.DEFAULT_PIT_PITS_BORDER_STYLE;
					pitsPitInfo.borderSurface = SegmentSide.DEFAULT_PIT_PITS_BORDER_SURFACE;
					pitsPitInfo.borderHeight = SegmentSide.DEFAULT_PIT_PITS_BORDER_HEIGHT;
					pitsPitInfo.borderWidth = SegmentSide.DEFAULT_PIT_PITS_BORDER_WIDTH;
				}
				// the surface the game uses there: often it is inherited, not set on the segment
				pitsPitInfo.surface = onLeft(pits) ? pitStart.getValidLeftSideSurface(getEditorFrame()) : pitStart.getValidRightSideSurface(getEditorFrame());
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

			if (pitExit != null)
			{
				// exit already exists so use it
				SegmentSide side = null;
				SegmentSide previousSide = null;
				Segment previous = pitExit.getPreviousShape();
				if (onLeft(pits))
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
					readBorderValues(previousSide, exitPitInfo);
				}
				else
				{
					exitPitInfo.borderType = SegmentSide.DEFAULT_PIT_EXIT_BORDER_STYLE;
					exitPitInfo.borderSurface = SegmentSide.DEFAULT_PIT_EXIT_BORDER_SURFACE;
					exitPitInfo.borderHeight = SegmentSide.DEFAULT_PIT_EXIT_BORDER_HEIGHT;
					exitPitInfo.borderWidth = SegmentSide.DEFAULT_PIT_EXIT_BORDER_WIDTH;
				}
				// the surface the game uses there: often it is inherited, not set on the segment
				exitPitInfo.surface = onLeft(pits) ? pitExit.getValidLeftSideSurface(getEditorFrame()) : pitExit.getValidRightSideSurface(getEditorFrame());
			}
			else
			{
				exitPitInfo.surface = SegmentSide.DEFAULT_PIT_EXIT_SURFACE;
				exitPitInfo.borderType = SegmentSide.DEFAULT_PIT_EXIT_BORDER_STYLE;
				exitPitInfo.borderSurface = SegmentSide.DEFAULT_PIT_EXIT_BORDER_SURFACE;
				exitPitInfo.borderHeight = SegmentSide.DEFAULT_PIT_EXIT_BORDER_HEIGHT;
				exitPitInfo.borderWidth = SegmentSide.DEFAULT_PIT_EXIT_BORDER_WIDTH;
			}

			// the pit segments often only have the sides of the track around them,
			// which aren't a pit lane's
			pavePitInfos();
			if (pitEntry != null)
			{
				Segment outside = pitEntry.getPreviousShape();
				if (outside == null)
					outside = data.elementAt(data.size() - 1);
				notTrackBorders(outside, onLeft(pits));
			}
		}
	}

	/**
	 * @return the names of the segments the pits are in, as the game places them,
	 *         or null when it places none (a separate pit path, or no pits)
	 */
	private java.util.Set<String> pitBoxSegments()
	{
		gui.view3d.GameTrack.PitLayout layout = pitLayout();
		if (layout == null)
			return null;
		java.util.Set<String> names = new java.util.HashSet<String>();
		for (gui.view3d.GameTrack.LocalPos pit : layout.pits)
			names.add(pit.seg.name);
		if (layout.endName != null)
			names.add(layout.endName);
		return names;
	}

	/**
	 * @return the pit lane's surface where there are no pits: the pit surface
	 *         without its pit box markings ("asphalt" for "asphalt-pits"), or else
	 *         the entry's surface
	 */
	private String laneSurface(String boxSurface, String entrySurface)
	{
		if (boxSurface != null && boxSurface.endsWith("-pits"))
		{
			String plain = boxSurface.substring(0, boxSurface.length() - "-pits".length());
			if (utils.circuit.RoadTexture.findSurface(getEditorFrame(), plain) != null)
				return plain;
		}
		return entrySurface;
	}

	/** A pit lane is paved: the default surfaces in place of grass, sand, gravel or dirt */
	private void pavePitInfos()
	{
		if (!roadSurface(entryPitInfo.surface))
			entryPitInfo.surface = SegmentSide.DEFAULT_PIT_ENTRY_SURFACE;
		if (!roadSurface(pitsPitInfo.surface))
			pitsPitInfo.surface = SegmentSide.DEFAULT_PIT_PITS_SURFACE;
		if (!roadSurface(exitPitInfo.surface))
			exitPitInfo.surface = SegmentSide.DEFAULT_PIT_EXIT_SURFACE;
	}

	/**
	 * @return whether cars can drive on a surface: it grips and rolls like
	 *         asphalt, concrete or a curb, not like grass (little grip), or sand,
	 *         gravel or dirt (much rolling resistance); the games' own surfaces
	 *         differ (TORCS' grass rolls as easily as Speed Dreams' asphalt), so
	 *         both count; a surface that isn't known is kept
	 */
	private boolean roadSurface(String name)
	{
		if (name == null || name.isEmpty())
			return false;
		Surface surface = utils.circuit.RoadTexture.findSurface(getEditorFrame(), name);
		if (surface == null)
			return true;
		// the games' defaults for a surface without them
		double friction = Double.isNaN(surface.getFriction()) ? 0.8 : surface.getFriction();
		double rollingResistance = Double.isNaN(surface.getRollingResistance()) ? 0.001 : surface.getRollingResistance();
		return friction >= 0.7 && rollingResistance <= 0.06;
	}

	/**
	 * The default pit borders in place of the track's own, which the pit segments
	 * only inherit: the curbs or walls along the track aren't the pit lane's
	 * driveable entry and exit edge, or the pit wall between the track and the pits.
	 * The entry and exit get the default pit lane edge, a flat border with a line,
	 * as do ones that have no width: the side is narrowed by the border's width,
	 * so the barrier goes on where it was.
	 *
	 * @param outside the segment before the pit entry
	 */
	private void notTrackBorders(Segment outside, boolean left)
	{
		String style = left ? outside.getValidLeftBorderStyle(getEditorFrame()) : outside.getValidRightBorderStyle(getEditorFrame());
		String surface = left ? outside.getValidLeftBorderSurface(getEditorFrame()) : outside.getValidRightBorderSurface(getEditorFrame());
		double width = left ? outside.getValidLeftBorderWidth(getEditorFrame()) : outside.getValidRightBorderWidth(getEditorFrame());
		if (sameBorder(entryPitInfo.borderSurface, entryPitInfo.borderWidth, surface, width) || !(entryPitInfo.borderWidth > 0))
		{
			entryPitInfo.borderSurface = SegmentSide.DEFAULT_PIT_ENTRY_BORDER_SURFACE;
			entryPitInfo.borderWidth = SegmentSide.DEFAULT_PIT_ENTRY_BORDER_WIDTH;
		}
		if (sameBorder(exitPitInfo.borderSurface, exitPitInfo.borderWidth, surface, width) || !(exitPitInfo.borderWidth > 0))
		{
			exitPitInfo.borderSurface = SegmentSide.DEFAULT_PIT_EXIT_BORDER_SURFACE;
			exitPitInfo.borderWidth = SegmentSide.DEFAULT_PIT_EXIT_BORDER_WIDTH;
		}
		if (style != null && style.equals(pitsPitInfo.borderType) && sameBorder(pitsPitInfo.borderSurface, pitsPitInfo.borderWidth, surface, width))
		{
			pitsPitInfo.borderType = SegmentSide.DEFAULT_PIT_PITS_BORDER_STYLE;
			pitsPitInfo.borderSurface = SegmentSide.DEFAULT_PIT_PITS_BORDER_SURFACE;
			pitsPitInfo.borderHeight = SegmentSide.DEFAULT_PIT_PITS_BORDER_HEIGHT;
			pitsPitInfo.borderWidth = SegmentSide.DEFAULT_PIT_PITS_BORDER_WIDTH;
		}
	}

	private static boolean sameBorder(String surface, double width, String trackSurface, double trackWidth)
	{
		return surface != null && surface.equals(trackSurface) && !Double.isNaN(width) && Math.abs(width - trackWidth) < 0.001;
	}

	/**
	 * @return a key of the pit side of the segments the generator changes, from
	 *         the one before the pit entry to the one after the pit exit
	 */
	private int pitSegmentsKey()
	{
		Pits pits = getEditorFrame().getPits();
		SegmentVector data = getEditorFrame().getTrackData().getSegments();
		Segment entry = data.getSegmentFromName(pits.getEntry());
		Segment exit = data.getSegmentFromName(pits.getExit());
		if (entry == null || exit == null || data.isEmpty())
			return 0;
		boolean left = onLeft(pits);
		int n = data.size();
		int from = (data.indexOf(entry) - 1 + n) % n, to = (data.indexOf(exit) + 1) % n;
		StringBuilder key = new StringBuilder();
		for (int i = from, count = 0; count < n; i = (i + 1) % n, count++)
		{
			Segment segment = data.get(i);
			SegmentSide side = left ? segment.getLeft() : segment.getRight();
			key.append(segment.getName()).append('|').append(side.getSideSurface()).append('|')
				.append(side.getSideStartWidth()).append('|').append(side.getSideEndWidth()).append('|')
				.append(side.getBorderStyle()).append('|').append(side.getBorderSurface()).append('|')
				.append(side.getBorderWidth()).append('|').append(side.getBorderHeight()).append('|')
				.append(side.getBarrierHeight()).append('|').append(side.getBarrierWidth()).append('\n');
			if (i == to)
				break;
		}
		return key.toString().hashCode();
	}

	private static void fromPart(Pits.GeneratorPart part, PitInfo info)
	{
		info.surface = part.getSurface();
		info.borderType = part.getBorderType();
		info.borderSurface = part.getBorderSurface();
		info.borderWidth = part.getBorderWidth();
		info.borderHeight = part.getBorderHeight();
	}

	/** The settings in a generator tab */
	private Pits.GeneratorPart toPart(PitPanel panel)
	{
		Pits.GeneratorPart part = new Pits.GeneratorPart();
		Object surface = panel.getSurfaceComboBox().getSelectedItem();
		part.setSurface(surface == null ? null : surface.toString());
		Object borderSurface = panel.getBorderSurfaceComboBox().getSelectedItem();
		part.setBorderSurface(borderSurface == null ? null : borderSurface.toString());
		part.setBorderWidth(getDouble(panel.getBorderWidthTextField().getText()));
		if (panel.isPitZone)
		{
			Object type = panel.getBorderTypeComboBox().getSelectedItem();
			part.setBorderType(type == null ? null : type.toString());
			part.setBorderHeight(getDouble(panel.getBorderHeightTextField().getText()));
		}
		else
		{
			// entry/exit: always "plan" with height 0 (driveable surface)
			part.setBorderType("plan");
			part.setBorderHeight(0);
		}
		return part;
	}

	/** Keeps the generator tabs' settings with the track's pits */
	private void storeTabs()
	{
		if (tabbedPane == null || tabbedPane.getTabCount() < 3)
			return;
		getEditorFrame().getPits().setGenerator(new Pits.Generator(
			toPart((PitPanel) tabbedPane.getComponentAt(0)),
			toPart((PitPanel) tabbedPane.getComponentAt(1)),
			toPart((PitPanel) tabbedPane.getComponentAt(2))));
	}

	/** Shows the generator settings the track's pits have, or those of its pit segments */
	private void refillTabs()
	{
		if (tabbedPane == null || tabbedPane.getTabCount() < 3)
			return;
		readPitInfos();
		PitInfo[] infos = {entryPitInfo, pitsPitInfo, exitPitInfo};
		for (int i = 0; i < 3; i++)
			((PitPanel) tabbedPane.getComponentAt(i)).refill(infos[i]);
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

		private final boolean isPitZone;

		/**
		 * @param pitInfo the pit zone info
		 * @param isPitZone true for the pits tab (border type and height are
		 *        editable), false for entry/exit tabs (border is always "plan"
		 *        with height 0 since the surface must be driveable)
		 */
		public PitPanel(PitInfo pitInfo, boolean isPitZone)
		{
			super();
			this.isPitZone = isPitZone;
			initialize(pitInfo);
		}

		/**
		 *
		 */
		private void initialize(PitInfo pitInfo)
		{
			setLayout(null);

			int row = 0;
			addLabel(this, row, surfaceLabel, "Surface", 120);
			row++;
			if (isPitZone)
			{
				addLabel(this, row, borderTypeLabel, "Border Type", 120);
				row++;
			}
			addLabel(this, row, borderSurfaceLabel, "Border Surface", 120);
			row++;
			if (isPitZone)
			{
				addLabel(this, row, borderHeightLabel, "Border Height", 120);
				row++;
			}
			addLabel(this, row, borderWidthLabel, "Border Width", 120);

			row = 0;
			add(getSurfaceComboBox(), null);
			getSurfaceComboBox().setBounds(130, rowY(row), 200, 23);
			row++;
			if (isPitZone)
			{
				add(getBorderTypeComboBox(), null);
				row++;
			}
			add(getBorderSurfaceComboBox(), null);
			getBorderSurfaceComboBox().setBounds(130, rowY(row), 200, 23);
			row++;
			if (isPitZone)
			{
				addTextField(this, row, borderHeightTextField, Double.NaN, 130, 125);
				row++;
			}
			addTextField(this, row, borderWidthTextField, Double.NaN, 130, 125);

			if (!isPitZone)
			{
				// entry/exit: always "plan" with height 0 (driveable surface)
				pitInfo.borderType = "plan";
				pitInfo.borderHeight = 0;
			}

			setPitInfo(pitInfo);
		}

		/** Shows other settings, after an undo or redo */
		private void refill(PitInfo pitInfo)
		{
			if (!isPitZone)
			{
				pitInfo.borderType = "plan";
				pitInfo.borderHeight = 0;
			}
			setPitInfo(pitInfo);
		}

		private void setPitInfo(PitInfo pitInfo)
		{
			this.pitInfo = pitInfo;

			surfaceComboBox.setSelectedItem(this.pitInfo.surface);

			if (isPitZone)
			{
				String borderType = this.pitInfo.borderType;
				if (borderType == null || borderType.isEmpty())
					borderType = "none";
				selectItem(borderTypeComboBox, borderType);
				borderTypeChanged(true);
			}
			else
			{
				// entry/exit: populate surface and width only
				borderSurfaceComboBox.setSelectedItem(this.pitInfo.borderSurface);
				setTextField(borderWidthTextField, this.pitInfo.borderWidth);
			}
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
				String[] types = { "none", "plan", "curb", "wall" };
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
		if (!getAddPitsCheckBox().isSelected() && !getLengthsCheckBox().isSelected() || tabbedPane == null
			|| tabbedPane.getTabCount() == 0)
			return null;

		for (int i = 0; i < tabbedPane.getTabCount(); i++)
		{
			PitPanel panel = (PitPanel) tabbedPane.getComponentAt(i);
			String problem = null;
			if (panel.getSurfaceComboBox().getItemCount() == 0)
				problem = "the track needs surfaces: tick Default Surfaces on the Surfaces tab, or add some there, then reopen the properties";
			else if (panel.getSurfaceComboBox().getSelectedItem() == null)
				problem = "choose a surface";
			else if (panel.isPitZone)
			{
				String type = (String) panel.getBorderTypeComboBox().getSelectedItem();
				if (type != null && !type.equals("none"))
				{
					if (panel.getBorderSurfaceComboBox().getSelectedItem() == null)
						problem = "choose a border surface";
					else if (Double.isNaN(getDouble(panel.getBorderHeightTextField().getText())))
						problem = "the border needs a height";
					else if (Double.isNaN(getDouble(panel.getBorderWidthTextField().getText())))
						problem = "the border needs a width";
				}
			}
			else
			{
				// entry/exit: just need a border surface and width
				if (panel.getBorderSurfaceComboBox().getSelectedItem() == null)
					problem = "choose a border surface";
				else if (Double.isNaN(getDouble(panel.getBorderWidthTextField().getText())))
					problem = "the border needs a width";
			}
			if (problem != null)
			{
				tabbedPane.setSelectedIndex(i);
				return "Pit > " + tabbedPane.getTitleAt(i) + ": to add pits, " + problem + ".";
			}
		}
		return null;
	}

	/**
	 *
	 */
	/** Returns the value if explicitly set (not NaN), otherwise the default. */
	private static double valueOrDefault(double value, double defaultValue)
	{
		return Double.isNaN(value) ? defaultValue : value;
	}

	/** Returns the value if explicitly set (not null/empty), otherwise the default. */
	private static String valueOrDefault(String value, String defaultValue)
	{
		return value != null && !value.isEmpty() ? value : defaultValue;
	}

	/**
	 * Returns pit-appropriate border values from a segment side.
	 * Uses the segment's own values when explicitly set, otherwise the
	 * pits defaults (height 1.0, width 0.5, wall surface) which are
	 * more appropriate for a pit border than the entry/exit defaults of 0.
	 */
	private static void readBorderValues(SegmentSide side, PitInfo info)
	{
		info.borderType = side.getBorderStyle();
		info.borderSurface = valueOrDefault(side.getBorderSurface(), SegmentSide.DEFAULT_PIT_PITS_BORDER_SURFACE);
		info.borderHeight = valueOrDefault(side.getBorderHeight(), SegmentSide.DEFAULT_PIT_PITS_BORDER_HEIGHT);
		info.borderWidth = valueOrDefault(side.getBorderWidth(), SegmentSide.DEFAULT_PIT_PITS_BORDER_WIDTH);
	}

	private static boolean isKilometersPerHour(String units)
	{
		return units != null && (units.equalsIgnoreCase("kph") || units.equalsIgnoreCase("km/h"));
	}

	public void exit()
	{
		applyFields();

		// generation is done via the Generate button, not on OK
		// clear the initial state so cancel doesn't undo accepted changes
		initialSegments = null;
		initialPits = null;
	}

	/**
	 * Copies the dialog's fields onto the track's pits.
	 */
	private void applyFields()
	{
		// TORCS doesn't support buildings, max pits or indicator
		boolean options = getEditorFrame().getGameType().hasPitOptions();
		// the item, not its position: a version 3 track has fewer choices
		int index = java.util.Arrays.asList(styleItems()).indexOf(getStyleComboBox().getSelectedItem());
		int style = getEditorFrame().getPits().getStyle();
		if (index >= 0)
		{
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
		}

		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();
		MutableInteger integerResult = new MutableInteger();

		// the first choice is no side
		String side = getSideComboBox().getSelectedIndex() <= 0 ? null : (String) getSideComboBox().getSelectedItem();
		if (isDifferent(side, getEditorFrame().getPits().getSide(), stringResult))
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

		if (options && isDifferent(startBuildingsTextField.getText(),
			getEditorFrame().getPits().getStartBuildings(), stringResult))
		{
			getEditorFrame().getPits().setStartBuildings(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (options && isDifferent(stopBuildingsTextField.getText(),
			getEditorFrame().getPits().getStopBuildings(), stringResult))
		{
			getEditorFrame().getPits().setStopBuildings(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (showMaxPitsAndIndicator() && isDifferent(maxPitsTextField.getText(),
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
		if (showMaxPitsAndIndicator() && index >= 0)
		{
			int value = index == 0 ? Integer.MAX_VALUE : index == 3 ? otherIndicator : index;
			if (value != indicator)
			{
				getEditorFrame().getPits().setIndicator(value);
				getEditorFrame().documentIsModified = true;
			}
		}

		if (isDifferent(speedLimitTextField.getText(),
			getEditorFrame().getPits().getSpeedLimitValue(), doubleResult))
		{
			getEditorFrame().getPits().setSpeedLimitValue(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		index = getSpeedLimitUnitsComboBox().getSelectedIndex();
		String	units = getEditorFrame().getPits().getSpeedLimitUnits();
		String[] items = {null, "m", options ? "kph" : "km/h", "mph"};
		if (index == 0)
		{
			if (units != null)
			{
				getEditorFrame().getPits().setSpeedLimitUnits(null);
				getEditorFrame().documentIsModified = true;
			}
		}
		else if (index != -1 && !items[index].equalsIgnoreCase(units) &&
			!(index == 2 && isKilometersPerHour(units)))
		{
			getEditorFrame().getPits().setSpeedLimitUnits(items[index]);
			getEditorFrame().documentIsModified = true;
		}
	}

	/**
	 *
	 */
	private boolean createPits()
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

		// no side is right, as in the games

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
			return false;
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
			return false;
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
			return false;
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
			return false;
		}

		// where the game puts the pits, from the segments as they are: the generator
		// only changes their sides
		java.util.Set<String> boxSegments = pitBoxSegments();

		SegmentSide side = null;
		double borderWidth = 0;
		double sideWidth = 0;
		Segment nextSegment = pitExit.getNextShape();
		if (nextSegment == null && !data.isEmpty())
			nextSegment = data.get(0);
		if (nextSegment == null)
			return false;
		if (onLeft(pits))
		{
			nextSegment.getLeft().setBorderWidth(nextSegment.getValidLeftBorderWidth(getEditorFrame()));
			nextSegment.getLeft().setBorderHeight(nextSegment.getValidLeftBorderHeight(getEditorFrame()));
			nextSegment.getLeft().setBorderSurface(nextSegment.getValidLeftBorderSurface(getEditorFrame()));
			nextSegment.getLeft().setBorderStyle(nextSegment.getValidLeftBorderStyle(getEditorFrame()));

			nextSegment.getLeft().setSideStartWidth(nextSegment.getValidLeftSideStartWidth(getEditorFrame()));
			nextSegment.getLeft().setSideSurface(nextSegment.getValidLeftSideSurface(getEditorFrame()));

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

			nextSegment.getRight().setBarrierWidth(nextSegment.getValidRightBarrierWidth(getEditorFrame()));
			nextSegment.getRight().setBarrierHeight(nextSegment.getValidRightBarrierHeight(getEditorFrame()));
			nextSegment.getRight().setBarrierSurface(nextSegment.getValidRightBarrierSurface(getEditorFrame()));
			nextSegment.getRight().setBarrierStyle(nextSegment.getValidRightBarrierStyle(getEditorFrame()));

			side = pitExit.getRight();
			borderWidth = nextSegment.getRight().getBorderWidth();
			sideWidth = nextSegment.getRight().getSideStartWidth();
		}

		PitPanel exitPanel = (PitPanel) tabbedPane.getComponentAt(2);
		// the exit's barrier is the one after it, so it goes on unchanged: the
		// segments only get a barrier from the one before them, the pit end
		SegmentSide after = onLeft(pits) ? nextSegment.getLeft() : nextSegment.getRight();
		double barrierWidthAfter = after.getBarrierWidth();
		double barrierHeightAfter = after.getBarrierHeight();
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
			if (onLeft(pits))
			{
				side = data.get(index).getLeft();
			}
			else
			{
				side = data.get(index).getRight();
			}
			// exit border is always "plan" with height 0 (driveable surface)
			side.setBorderStyle("plan");
			Object exitBorderSurface = exitPanel.getBorderSurfaceComboBox().getSelectedItem();
			side.setBorderSurface(exitBorderSurface == null ? null : exitBorderSurface.toString());
			side.setBorderHeight(0);
			side.setBorderWidth(getDouble(exitPanel.getBorderWidthTextField().getText()));
			side.setSideSurface(exitPanel.getSurfaceComboBox().getSelectedItem().toString());
			side.setBarrierHeight(barrierHeightAfter);
			side.setBarrierWidth(barrierWidthAfter);
		}
		// from the pit lane's width at the pit end down to the side after the exit,
		// evenly over all the exit's segments
		java.util.List<Segment> exitSegments = new java.util.ArrayList<Segment>();
		for (int i = count - 1; i >= 0; i--)
			exitSegments.add(data.get(Math.floorMod(start - i, data.size())));
		taper(exitSegments, onLeft(pits), pits.getValidWidth() * 3, borderWidth + sideWidth,
			getDouble(exitPanel.getBorderWidthTextField().getText()));

		Segment previousSegment = pitEntry.getPreviousShape();
		if (previousSegment == null)
			previousSegment = data.get(data.size() - 1);

		if (onLeft(pits))
		{
			previousSegment.getLeft().setBorderWidth(previousSegment.getValidLeftBorderWidth(getEditorFrame()));
			previousSegment.getLeft().setBorderHeight(previousSegment.getValidLeftBorderHeight(getEditorFrame()));
			previousSegment.getLeft().setBorderSurface(previousSegment.getValidLeftBorderSurface(getEditorFrame()));
			previousSegment.getLeft().setBorderStyle(previousSegment.getValidLeftBorderStyle(getEditorFrame()));

			previousSegment.getLeft().setSideStartWidth(previousSegment.getValidLeftSideStartWidth(getEditorFrame()));
			previousSegment.getLeft().setSideSurface(previousSegment.getValidLeftSideSurface(getEditorFrame()));

			previousSegment.getLeft().setBarrierWidth(previousSegment.getValidLeftBarrierWidth(getEditorFrame()));
			previousSegment.getLeft().setBarrierHeight(previousSegment.getValidLeftBarrierHeight(getEditorFrame()));
			previousSegment.getLeft().setBarrierSurface(previousSegment.getValidLeftBarrierSurface(getEditorFrame()));
			previousSegment.getLeft().setBarrierStyle(previousSegment.getValidLeftBarrierStyle(getEditorFrame()));

			side = pitEntry.getLeft();
			borderWidth = previousSegment.getLeft().getBorderWidth();
			sideWidth = previousSegment.getValidLeftSideEndWidth(getEditorFrame());
		}
		else
		{
			previousSegment.getRight().setBorderWidth(previousSegment.getValidRightBorderWidth(getEditorFrame()));
			previousSegment.getRight().setBorderHeight(previousSegment.getValidRightBorderHeight(getEditorFrame()));
			previousSegment.getRight().setBorderSurface(previousSegment.getValidRightBorderSurface(getEditorFrame()));
			previousSegment.getRight().setBorderStyle(previousSegment.getValidRightBorderStyle(getEditorFrame()));

			previousSegment.getRight().setSideStartWidth(previousSegment.getValidRightSideStartWidth(getEditorFrame()));
			previousSegment.getRight().setSideSurface(previousSegment.getValidRightSideSurface(getEditorFrame()));

			previousSegment.getRight().setBarrierWidth(previousSegment.getValidRightBarrierWidth(getEditorFrame()));
			previousSegment.getRight().setBarrierHeight(previousSegment.getValidRightBarrierHeight(getEditorFrame()));
			previousSegment.getRight().setBarrierSurface(previousSegment.getValidRightBarrierSurface(getEditorFrame()));
			previousSegment.getRight().setBarrierStyle(previousSegment.getValidRightBarrierStyle(getEditorFrame()));

			side = pitEntry.getRight();
			borderWidth = previousSegment.getRight().getBorderWidth();
			sideWidth = previousSegment.getValidRightSideEndWidth(getEditorFrame());
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
			if (onLeft(pits))
			{
				side = data.get(index).getLeft();
			}
			else
			{
				side = data.get(index).getRight();
			}
			// entry border is always "plan" with height 0 (driveable surface)
			side.setBorderStyle("plan");
			Object entryBorderSurface = entryPanel.getBorderSurfaceComboBox().getSelectedItem();
			side.setBorderSurface(entryBorderSurface == null ? null : entryBorderSurface.toString());
			side.setBorderHeight(0);
			side.setBorderWidth(getDouble(entryPanel.getBorderWidthTextField().getText()));
			side.setSideSurface(entryPanel.getSurfaceComboBox().getSelectedItem().toString());
			// the barrier goes on from the segment before the entry
			side.setBarrierHeight(Double.NaN);
			side.setBarrierWidth(Double.NaN);
		}
		// from the side before the entry up to the pit lane's width at the pit start,
		// evenly over all the entry's segments
		java.util.List<Segment> entrySegments = new java.util.ArrayList<Segment>();
		for (int i = 0; i < count; i++)
			entrySegments.add(data.get((start + i) % data.size()));
		taper(entrySegments, onLeft(pits), borderWidth + sideWidth, pits.getValidWidth() * 3,
			getDouble(entryPanel.getBorderWidthTextField().getText()));

		PitPanel pitPanel = (PitPanel) tabbedPane.getComponentAt(1);
		String boxSurface = pitPanel.getSurfaceComboBox().getSelectedItem().toString();
		String laneSurface = laneSurface(boxSurface, entryPanel.getSurfaceComboBox().getSelectedItem().toString());
		start = data.indexOf(pitStart);
		end = data.indexOf(pitEnd);
		if (start > end)
			count = end + data.size() - start + 1;
		else
			count = end - start + 1;

		for (int i = 0; i < count; i++)
		{
			int index = (start + i) % data.size();
			if (onLeft(pits))
			{
				side = data.get(index).getLeft();
			}
			else
			{
				side = data.get(index).getRight();
			}
			double width = pits.getValidWidth() * 3;
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
			// the pit boxes' markings only where there are pits
			Segment segment = data.get(index);
			side.setSideSurface(boxSegments == null || boxSegments.contains(segment.getName()) ? boxSurface : laneSurface);
			// the barrier goes on from the track: the game doesn't draw it along the
			// pit buildings, but cars still hit it there
			side.setBarrierHeight(Double.NaN);
			side.setBarrierWidth(Double.NaN);
		}
		return true;
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
