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
import utils.Editor;

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
	public StartingGridProperties(EditorFrame frame)
	{
		super(frame);
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

		addLabel(this, 0, rowsLabel, "Rows", 170);
		addLabel(this, 1, polePositionSideLabel, "Pole Position Side", 170);
		addLabel(this, 2, distanceToStartLabel, "Distance To Start", 170);
		addLabel(this, 3, distanceBetweenColumnsLabel, "Distance Between Columns", 170);
		addLabel(this, 4, offsetWithinAColumnLabel, "Offset Within A Column", 170);
		addLabel(this, 5, initialHeightLabel, "Initial Height", 170);

		addTextField(this, 0, rowsTextField, Editor.getProperties().getStartingGrid().getRows(), 180, 100);

		add(getPolePositionSideComboBox(), null);

		addTextField(this, 2, distanceToStartTextField, Editor.getProperties().getStartingGrid().getDistanceToStart(), 180, 100);
		addTextField(this, 3, distanceBetweenColumnsTextField, Editor.getProperties().getStartingGrid().getDistanceBetweenColumns(), 180, 100);
		addTextField(this, 4, offsetWithinAColumnTextField, Editor.getProperties().getStartingGrid().getOffsetWithinAColumn(), 180, 100);
		addTextField(this, 5, initialHeightTextField, Editor.getProperties().getStartingGrid().getInitialHeight(), 180, 100);
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
			polePositionSideComboBox.setBounds(180, 35, 80, 20);
			String side = Editor.getProperties().getStartingGrid().getPolePositionSide();
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
			Editor.getProperties().getStartingGrid().getRows(), integerResult))
		{
			Editor.getProperties().getStartingGrid().setRows(integerResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent((String) getPolePositionSideComboBox().getSelectedItem(),
			Editor.getProperties().getStartingGrid().getPolePositionSide(), stringResult))
		{
			Editor.getProperties().getStartingGrid().setPolePositionSide(stringResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(distanceToStartTextField.getText(),
			Editor.getProperties().getStartingGrid().getDistanceToStart(), doubleResult))
		{
			Editor.getProperties().getStartingGrid().setDistanceToStart(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(distanceBetweenColumnsTextField.getText(),
			Editor.getProperties().getStartingGrid().getDistanceBetweenColumns(), doubleResult))
		{
			Editor.getProperties().getStartingGrid().setDistanceBetweenColumns(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(offsetWithinAColumnTextField.getText(),
			Editor.getProperties().getStartingGrid().getOffsetWithinAColumn(), doubleResult))
		{
			Editor.getProperties().getStartingGrid().setOffsetWithinAColumn(doubleResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(initialHeightTextField.getText(),
			Editor.getProperties().getStartingGrid().getInitialHeight(), doubleResult))
		{
			Editor.getProperties().getStartingGrid().setInitialHeight(doubleResult.getValue());
			frame.documentIsModified = true;
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
