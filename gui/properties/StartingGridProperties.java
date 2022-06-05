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

import utils.Editor;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class StartingGridProperties extends JPanel
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
	public StartingGridProperties()
	{
		super();
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
			double rows = Editor.getProperties().getStartingGrid().getRows();
			if (rows != Integer.MAX_VALUE)
				rowsTextField.setText(rows + "");
			rowsTextField.setBounds(150, 10, 100, 20);
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
			String[] items = {"right", "left"};
			polePositionSideComboBox = new JComboBox<String>(items);
			String side = Editor.getProperties().getStartingGrid().getPolePositionSide();
			if (side == null || side.isEmpty())
				side = "right";
			polePositionSideComboBox.setSelectedItem(side);
			polePositionSideComboBox.setBounds(150, 35, 80, 20);
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
			double space = Editor.getProperties().getStartingGrid().getDistanceToStart();
			if (!Double.isNaN(space))
				distanceToStartTextField.setText(space + "");
			distanceToStartTextField.setBounds(150, 60, 100, 20);
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
			double space = Editor.getProperties().getStartingGrid().getDistanceBetweenColumns();
			if (!Double.isNaN(space))
				distanceBetweenColumnsTextField.setText(space + "");
			distanceBetweenColumnsTextField.setBounds(150, 85, 100, 20);
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
			double space = Editor.getProperties().getStartingGrid().getOffsetWithinAColumn();
			if (!Double.isNaN(space))
				offsetWithinAColumnTextField.setText(space + "");
			offsetWithinAColumnTextField.setBounds(150, 110, 100, 20);
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
			double space = Editor.getProperties().getStartingGrid().getInitialHeight();
			if (!Double.isNaN(space))
				initialHeightTextField.setText(space + "");
			initialHeightTextField.setBounds(150, 135, 100, 20);
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
			Editor.getProperties().getStartingGrid().setRows(Integer.parseInt(this.getRowsTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getStartingGrid().setRows(Integer.MAX_VALUE);
		}
		Editor.getProperties().getStartingGrid().setPolePositionSide((String) getPolePositionSideComboBox().getSelectedItem());
		try
		{
			Editor.getProperties().getStartingGrid().setDistanceToStart(Double.parseDouble(this.getDistanceToStartTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getStartingGrid().setDistanceToStart(Double.NaN);
		}
		try
		{
			Editor.getProperties().getStartingGrid().setDistanceBetweenColumns(Double.parseDouble(this.getDistanceBetweenColumnsTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getStartingGrid().setDistanceBetweenColumns(Double.NaN);
		}
		try
		{
			Editor.getProperties().getStartingGrid().setOffsetWithinAColumn(Double.parseDouble(this.getOffsetWithinAColumnTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getStartingGrid().setOffsetWithinAColumn(Double.NaN);
		}
		try
		{
			Editor.getProperties().getStartingGrid().setInitialHeight(Double.parseDouble(this.getInitialHeightTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getStartingGrid().setInitialHeight(Double.NaN);
		}

		Editor.getProperties().valueChanged();
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
