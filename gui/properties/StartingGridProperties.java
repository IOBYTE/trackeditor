/*
 *   StartingGridProperties.java
 *   Created on 31 May 2022
 *
 *    The StartingGridProperties.java is part of TrackEditor-0.7.0.
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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.MutableDouble;
import utils.MutableInteger;
import utils.MutableString;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class StartingGridProperties extends PropertyPanel
{
	private JLabel				rowsLabel						= new JLabel();
	private JTextField			rowsTextField					= new JTextField();
	private JLabel				polePositionSideLabel			= new JLabel();
	private JComboBox<String>	polePositionSideComboBox		= null;
	private JLabel				distanceToStartLabel			= new JLabel();
	private JTextField			distanceToStartTextField		= new JTextField();
	private JLabel				distanceBetweenColumnsLabel		= new JLabel();
	private JTextField			distanceBetweenColumnsTextField	= new JTextField();
	private JLabel				offsetWithinAColumnLabel		= new JLabel();
	private JTextField			offsetWithinAColumnTextField	= new JTextField();
	private JLabel				initialHeightLabel				= new JLabel();
	private JTextField			initialHeightTextField			= new JTextField();

	/**
	 *
	 */
	public StartingGridProperties(EditorFrame editorFrame)
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

		addLabel(this, 0, rowsLabel, "Rows", 180);
		addLabel(this, 1, polePositionSideLabel, "Pole Position Side", 180);
		addLabel(this, 2, distanceToStartLabel, "Distance To Start", 180);
		addLabel(this, 3, distanceBetweenColumnsLabel, "Distance Between Columns", 180);
		addLabel(this, 4, offsetWithinAColumnLabel, "Offset Within A Column", 180);
		addLabel(this, 5, initialHeightLabel, "Initial Height", 180);

		addTextField(this, 0, rowsTextField, getEditorFrame().getTrackData().getStartingGrid().getRows(), 190, 125);

		add(getPolePositionSideComboBox(), null);

		addTextField(this, 2, distanceToStartTextField, getEditorFrame().getTrackData().getStartingGrid().getDistanceToStart(), 190, 125);
		addTextField(this, 3, distanceBetweenColumnsTextField, getEditorFrame().getTrackData().getStartingGrid().getDistanceBetweenColumns(), 190, 125);
		addTextField(this, 4, offsetWithinAColumnTextField, getEditorFrame().getTrackData().getStartingGrid().getOffsetWithinAColumn(), 190, 125);
		addTextField(this, 5, initialHeightTextField, getEditorFrame().getTrackData().getStartingGrid().getInitialHeight(), 190, 125);
	}

	/**
	 * This method initializes polePositionSideComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getPolePositionSideComboBox()
	{
		if (polePositionSideComboBox == null)
		{
			String[] items = {"none", "right", "left"};
			polePositionSideComboBox = new JComboBox<String>(items);
			polePositionSideComboBox.setBounds(190, 37, 125, 23);
			String side = getEditorFrame().getTrackData().getStartingGrid().getPolePositionSide();
			if (side == null || side.isEmpty())
				side = "none";
			polePositionSideComboBox.setSelectedItem(side);
		}
		return polePositionSideComboBox;
	}

	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();
		MutableInteger integerResult = new MutableInteger();

		if (isDifferent(rowsTextField.getText(),
			getEditorFrame().getTrackData().getStartingGrid().getRows(), integerResult))
		{
			getEditorFrame().getTrackData().getStartingGrid().setRows(integerResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent((String) getPolePositionSideComboBox().getSelectedItem(),
			getEditorFrame().getTrackData().getStartingGrid().getPolePositionSide(), stringResult))
		{
			getEditorFrame().getTrackData().getStartingGrid().setPolePositionSide(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(distanceToStartTextField.getText(),
			getEditorFrame().getTrackData().getStartingGrid().getDistanceToStart(), doubleResult))
		{
			getEditorFrame().getTrackData().getStartingGrid().setDistanceToStart(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(distanceBetweenColumnsTextField.getText(),
			getEditorFrame().getTrackData().getStartingGrid().getDistanceBetweenColumns(), doubleResult))
		{
			getEditorFrame().getTrackData().getStartingGrid().setDistanceBetweenColumns(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(offsetWithinAColumnTextField.getText(),
			getEditorFrame().getTrackData().getStartingGrid().getOffsetWithinAColumn(), doubleResult))
		{
			getEditorFrame().getTrackData().getStartingGrid().setOffsetWithinAColumn(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(initialHeightTextField.getText(),
			getEditorFrame().getTrackData().getStartingGrid().getInitialHeight(), doubleResult))
		{
			getEditorFrame().getTrackData().getStartingGrid().setInitialHeight(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
