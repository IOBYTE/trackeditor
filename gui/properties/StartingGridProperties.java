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
import javax.swing.JPanel;
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
	private JLabel				rowsLabel						= null;
	private JTextField			rowsTextField					= null;
	private JLabel				polePositionSideLabel			= null;
	private JComboBox<String>	polePositionSideComboBox		= null;
	private JLabel				distanceToStartLabel			= null;
	private JTextField			distanceToStartTextField		= null;
	private JLabel				distanceBetweenColumnsLabel		= null;
	private JTextField			distanceBetweenColumnsTextField	= null;
	private JLabel				offsetWithinAColumnLabel		= null;
	private JTextField			offsetWithinAColumnTextField	= null;
	private JLabel				initialHeightLabel				= null;
	private JTextField			initialHeightTextField			= null;

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
		rowsLabel = new JLabel();
		polePositionSideLabel = new JLabel();
		distanceToStartLabel = new JLabel();
		distanceBetweenColumnsLabel = new JLabel();
		offsetWithinAColumnLabel = new JLabel();
		initialHeightLabel = new JLabel();
		this.setLayout(null);
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		this.setSize(362, 251);
		rowsLabel.setBounds(10, 10, 70, 20);
		rowsLabel.setText("Rows");
		polePositionSideLabel.setBounds(10, 35, 120, 20);
		polePositionSideLabel.setText("Pole Position Side");
		distanceToStartLabel.setBounds(10, 60, 120, 20);
		distanceToStartLabel.setText("Distance To Start");
		distanceBetweenColumnsLabel.setBounds(10, 85, 140, 20);
		distanceBetweenColumnsLabel.setText("Distance Between Columns");
		offsetWithinAColumnLabel.setBounds(10, 110, 140, 20);
		offsetWithinAColumnLabel.setText("Offset Within A Column");
		initialHeightLabel.setBounds(10, 135, 140, 20);
		initialHeightLabel.setText("Initial Height");
		this.add(rowsLabel, null);
		this.add(polePositionSideLabel, null);
		this.add(distanceToStartLabel, null);
		this.add(distanceBetweenColumnsLabel, null);
		this.add(offsetWithinAColumnLabel, null);
		this.add(initialHeightLabel, null);
		this.add(getRowsTextField(), null);
		this.add(getPolePositionSideComboBox(), null);
		this.add(getDistanceToStartTextField(), null);
		this.add(getDistanceBetweenColumnsTextField(), null);
		this.add(getOffsetWithinAColumnTextField(), null);
		this.add(getInitialHeightTextField(), null);
	}
	/**
	 * This method initializes rowsTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getRowsTextField()
	{
		if (rowsTextField == null)
		{
			rowsTextField = new JTextField();
			rowsTextField.setBounds(150, 10, 100, 20);
			setTextField(rowsTextField, Editor.getProperties().getStartingGrid().getRows());
		}
		return rowsTextField;
	}
	/**
	 * This method initializes sideComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getPolePositionSideComboBox()
	{
		if (polePositionSideComboBox == null)
		{
			String[] items = {"none", "right", "left"};
			polePositionSideComboBox = new JComboBox<String>(items);
			polePositionSideComboBox.setBounds(150, 35, 80, 20);
			String side = Editor.getProperties().getStartingGrid().getPolePositionSide();
			if (side == null || side.isEmpty())
				side = "none";
			polePositionSideComboBox.setSelectedItem(side);
		}
		return polePositionSideComboBox;
	}
	/**
	 * This method initializes distanceToStartTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getDistanceToStartTextField()
	{
		if (distanceToStartTextField == null)
		{
			distanceToStartTextField = new JTextField();
			distanceToStartTextField.setBounds(150, 60, 100, 20);
			setTextField(distanceToStartTextField, Editor.getProperties().getStartingGrid().getDistanceToStart());
		}
		return distanceToStartTextField;
	}
	/**
	 * This method initializes distanceBetweenColumnsTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getDistanceBetweenColumnsTextField()
	{
		if (distanceBetweenColumnsTextField == null)
		{
			distanceBetweenColumnsTextField = new JTextField();
			distanceBetweenColumnsTextField.setBounds(150, 85, 100, 20);
			setTextField(distanceBetweenColumnsTextField, Editor.getProperties().getStartingGrid().getDistanceBetweenColumns());
		}
		return distanceBetweenColumnsTextField;
	}

	/**
	 * This method initializes offsetWithinAColumnTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getOffsetWithinAColumnTextField()
	{
		if (offsetWithinAColumnTextField == null)
		{
			offsetWithinAColumnTextField = new JTextField();
			offsetWithinAColumnTextField.setBounds(150, 110, 100, 20);
			setTextField(offsetWithinAColumnTextField, Editor.getProperties().getStartingGrid().getOffsetWithinAColumn());
		}
		return offsetWithinAColumnTextField;
	}

	/**
	 * This method initializes initialHeightTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getInitialHeightTextField()
	{
		if (initialHeightTextField == null)
		{
			initialHeightTextField = new JTextField();
			initialHeightTextField.setBounds(150, 135, 100, 20);
			setTextField(initialHeightTextField, Editor.getProperties().getStartingGrid().getInitialHeight());
		}
		return initialHeightTextField;
	}

	/**
	 *
	 */
	public void exit()
	{
		try
		{
			int value = Integer.parseInt(this.getRowsTextField().getText());
			if (value != Editor.getProperties().getStartingGrid().getRows())
			{
				Editor.getProperties().getStartingGrid().setRows(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getStartingGrid().getRows()))
			{
				Editor.getProperties().getStartingGrid().setRows(Integer.MAX_VALUE);
				frame.documentIsModified = true;
			}
		}

		String subcategory = (String) getPolePositionSideComboBox().getSelectedItem();
		if (!subcategory.equals(Editor.getProperties().getHeader().getSubcategory()))
		{
			if (subcategory != "none")
				Editor.getProperties().getStartingGrid().setPolePositionSide(subcategory);
			else
				Editor.getProperties().getStartingGrid().setPolePositionSide(null);
			frame.documentIsModified = true;
		}

		try
		{
			int value = Integer.parseInt(this.getDistanceToStartTextField().getText());
			if (value != Editor.getProperties().getStartingGrid().getDistanceToStart())
			{
				Editor.getProperties().getStartingGrid().setDistanceToStart(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getStartingGrid().getDistanceToStart()))
			{
				Editor.getProperties().getStartingGrid().setDistanceToStart(Integer.MAX_VALUE);
				frame.documentIsModified = true;
			}
		}

		try
		{
			int value = Integer.parseInt(this.getDistanceBetweenColumnsTextField().getText());
			if (value != Editor.getProperties().getStartingGrid().getDistanceBetweenColumns())
			{
				Editor.getProperties().getStartingGrid().setDistanceBetweenColumns(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getStartingGrid().getDistanceBetweenColumns()))
			{
				Editor.getProperties().getStartingGrid().setDistanceBetweenColumns(Integer.MAX_VALUE);
				frame.documentIsModified = true;
			}
		}

		try
		{
			int value = Integer.parseInt(this.getOffsetWithinAColumnTextField().getText());
			if (value != Editor.getProperties().getStartingGrid().getOffsetWithinAColumn())
			{
				Editor.getProperties().getStartingGrid().setOffsetWithinAColumn(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getStartingGrid().getOffsetWithinAColumn()))
			{
				Editor.getProperties().getStartingGrid().setOffsetWithinAColumn(Integer.MAX_VALUE);
				frame.documentIsModified = true;
			}
		}

		try
		{
			int value = Integer.parseInt(this.getInitialHeightTextField().getText());
			if (value != Editor.getProperties().getStartingGrid().getInitialHeight())
			{
				Editor.getProperties().getStartingGrid().setInitialHeight(value);
				frame.documentIsModified = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(Editor.getProperties().getStartingGrid().getInitialHeight()))
			{
				Editor.getProperties().getStartingGrid().setInitialHeight(Integer.MAX_VALUE);
				frame.documentIsModified = true;
			}
		}

		Editor.getProperties().valueChanged();
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
