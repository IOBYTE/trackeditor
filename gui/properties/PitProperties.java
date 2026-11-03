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
	private JTextField			widthTextField			= new JTextField();
	private JLabel				lengthLabel				= new JLabel();
	private JTextField			lengthTextField			= new JTextField();
	private JLabel				indicatorLabel			= new JLabel();
	private JComboBox<String>	indicatorComboBox		= null;
	private JLabel				speedLimitLabel			= new JLabel();
	private JTextField			speedLimitTextField		= new JTextField();
	private JLabel				speedLimitUnitsLabel	= new JLabel();
	private JComboBox<String>	speedLimitUnitsComboBox	= null;
	private JCheckBox			addPitsCheckBox		= null;
	private JLabel				addPitsLabel		= new JLabel();
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
			addTextField(this, row++, startBuildingsTextField, pits.getStartBuildings(), 120, 125);
			addLabel(this, row, stopBuildingsLabel, "Stop Buildings", 110);
			addTextField(this, row++, stopBuildingsTextField, pits.getStopBuildings(), 120, 125);
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
		getAddPitsCheckBox().setBounds(120, rowY(row) + 2, 20, 20);
		row++;

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

	private static int rowY(int row)
	{
		return 10 + row * 27;
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
			int style = getEditorFrame().getPits().getStyle();
			int index = style == Integer.MAX_VALUE ? 0 : style + 1;
			String[] items = STYLE_ITEMS;
			if (isVersion3())
			{
				// the version 3 loaders only build track side pits; another type the
				// track already has stays listed, so looking doesn't change it
				items = java.util.Arrays.copyOf(STYLE_ITEMS, 3);
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
				items = java.util.Arrays.copyOf(STYLE_ITEMS, STYLE_ITEMS.length - 1);
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
			styleComboBox.setSelectedItem(STYLE_ITEMS[index]);
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
			selectSide();
		}
		return sideComboBox;
	}

	private void selectSide()
	{
		String side = getEditorFrame().getPits().getSide();
		if (side == null || side.isEmpty())
			side = "none";
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
			String[] items = {"none", "meters per second", "kilometers per hour", "miles per hour"};
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
			addPitsCheckBox = new JCheckBox();
			addPitsCheckBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (addPitsCheckBox.isSelected())
						addTabs();
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
				if ("left".equals(pits.getSide()))
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
				entryPitInfo.surface = "left".equals(pits.getSide()) ? pitEntry.getValidLeftSideSurface(getEditorFrame()) : pitEntry.getValidRightSideSurface(getEditorFrame());
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
				if ("left".equals(pits.getSide()))
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
				pitsPitInfo.surface = "left".equals(pits.getSide()) ? pitStart.getValidLeftSideSurface(getEditorFrame()) : pitStart.getValidRightSideSurface(getEditorFrame());
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
				if ("left".equals(pits.getSide()))
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
				exitPitInfo.surface = "left".equals(pits.getSide()) ? pitExit.getValidLeftSideSurface(getEditorFrame()) : pitExit.getValidRightSideSurface(getEditorFrame());
			}
			else
			{
				exitPitInfo.surface = SegmentSide.DEFAULT_PIT_EXIT_SURFACE;
				exitPitInfo.borderType = SegmentSide.DEFAULT_PIT_EXIT_BORDER_STYLE;
				exitPitInfo.borderSurface = SegmentSide.DEFAULT_PIT_EXIT_BORDER_SURFACE;
				exitPitInfo.borderHeight = SegmentSide.DEFAULT_PIT_EXIT_BORDER_HEIGHT;
				exitPitInfo.borderWidth = SegmentSide.DEFAULT_PIT_EXIT_BORDER_WIDTH;
			}
		}
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
		boolean left = "left".equals(pits.getSide());
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
		if (!getAddPitsCheckBox().isSelected() || tabbedPane == null || tabbedPane.getTabCount() == 0)
			return null;

		for (int i = 0; i < tabbedPane.getTabCount(); i++)
		{
			PitPanel panel = (PitPanel) tabbedPane.getComponentAt(i);
			String problem = null;
			if (panel.getSurfaceComboBox().getSelectedItem() == null)
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
		int index = java.util.Arrays.asList(STYLE_ITEMS).indexOf(getStyleComboBox().getSelectedItem());
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

		if (pits.getSide() == null)
		{
			JOptionPane.showMessageDialog(this, "No pit side specified", "Pit Side", JOptionPane.ERROR_MESSAGE);
			return false;
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

		SegmentSide side = null;
		double borderWidth = 0;
		double sideWidth = 0;
		Segment nextSegment = pitExit.getNextShape();
		if (nextSegment == null && !data.isEmpty())
			nextSegment = data.get(0);
		if (nextSegment == null)
			return false;
		if (pits.getSide().equals("left"))
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
			// exit border is always "plan" with height 0 (driveable surface)
			side.setBorderStyle("plan");
			side.setBorderSurface(exitPanel.getBorderSurfaceComboBox().getSelectedItem().toString());
			side.setBorderHeight(0);
			side.setBorderWidth(getDouble(exitPanel.getBorderWidthTextField().getText()));
			if (i == 0)
			{
				side.setSideStartWidth(pits.getValidWidth() * 3);
				side.setSideEndWidth(borderWidth + sideWidth);
			}
			else
			{
				side.setSideStartWidth((pits.getValidWidth() * 3) - side.getBorderWidth());
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
			// entry border is always "plan" with height 0 (driveable surface)
			side.setBorderStyle("plan");
			side.setBorderSurface(entryPanel.getBorderSurfaceComboBox().getSelectedItem().toString());
			side.setBorderHeight(0);
			side.setBorderWidth(getDouble(entryPanel.getBorderWidthTextField().getText()));
			if (i == 0)
			{
				side.setSideStartWidth(borderWidth + sideWidth);
				side.setSideEndWidth(pits.getValidWidth() * 3);
			}
			else
			{
				side.setSideStartWidth((pits.getValidWidth() * 3) - side.getBorderWidth());
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
			side.setSideSurface(pitPanel.getSurfaceComboBox().getSelectedItem().toString());
			side.setBarrierHeight(1);
			side.setBarrierWidth(0.1);
		}
		return true;
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
