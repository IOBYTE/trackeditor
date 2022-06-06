/*
 *   PitProperties.java
 *   Created on 27 ��� 2005
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

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utils.Editor;
import utils.TrackData;
import utils.circuit.Segment;
import utils.circuit.SegmentSide;

/**
 * @author babis
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class PitProperties extends JPanel
{
	//private Properties	properties		= Properties.getInstance();
	private JLabel				styleLabel				= null;
	private JComboBox<String>	styleComboBox			= null;
	private JLabel				sideLabel				= null;
	private JComboBox<String>	sideComboBox			= null;
	private JLabel				entryLabel				= null;
	private JTextField			entryTextField			= null;
	private JLabel				startLabel				= null;
	private JTextField			startTextField			= null;
	private JLabel				startBuildingsLabel		= null;
	private JTextField			startBuildingsTextField	= null;
	private JLabel				stopBuildingsLabel		= null;
	private JTextField			stopBuildingsTextField	= null;
	private JLabel				maxPitsLabel			= null;
	private JTextField			maxPitsTextField		= null;
	private JLabel				endLabel				= null;
	private JTextField			endTextField			= null;
	private JLabel				exitLabel				= null;
	private JTextField			exitTextField			= null;
	private JLabel				widthLabel				= null;
	private JTextField			widthTextField			= null;
	private JLabel				lengthLabel				= null;
	private JTextField			lengthTextField			= null;
	private JLabel				indicatorLabel			= null;
	private JTextField			indicatorTextField		= null;	// TODO replace with JComboBox?
	private JLabel				speedLimitLabel			= null;
	private JTextField			speedLimitTextField		= null;
	private JLabel 				generatePitsLabel 		= null;
	private JCheckBox 			generatePitsCheckBox 	= null;
	private boolean 			generatePits			= false;
	/**
	 *  
	 */
	public PitProperties()
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
		generatePitsLabel = new JLabel();
		sideLabel = new JLabel();
		entryLabel = new JLabel();
		startLabel = new JLabel();
		endLabel = new JLabel();
		exitLabel = new JLabel();
		widthLabel = new JLabel();
		lengthLabel = new JLabel();
		startBuildingsLabel = new JLabel();
		stopBuildingsLabel = new JLabel();
		styleLabel = new JLabel();
		maxPitsLabel = new JLabel();
		indicatorLabel = new JLabel();
		speedLimitLabel = new JLabel();
		this.setLayout(null);
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		this.setSize(362, 251);
		styleLabel.setBounds(10, 10, 70, 20);
		styleLabel.setText("Pit type");
		sideLabel.setBounds(10, 35, 70, 20);
		sideLabel.setText("Pits side");
		entryLabel.setBounds(10, 60, 70, 20);
		entryLabel.setText("Pit entry");
		startLabel.setBounds(10, 85, 70, 20);
		startLabel.setText("Pit start");
		startBuildingsLabel.setBounds(10, 110, 120, 20);
		startBuildingsLabel.setText("Pit start buildings");
		stopBuildingsLabel.setBounds(10, 135, 120, 20);
		stopBuildingsLabel.setText("Pit stop buildings");
		maxPitsLabel.setBounds(10, 160, 120, 20);
		maxPitsLabel.setText("Pit max pits");
		endLabel.setBounds(10, 185, 70, 20);
		endLabel.setText("Pit end");
		exitLabel.setBounds(10, 210, 70, 20);
		exitLabel.setText("Pit exit");
		widthLabel.setBounds(10, 235, 70, 20);
		widthLabel.setText("Pit width");
		lengthLabel.setBounds(10, 260, 70, 20);
		lengthLabel.setText("Pit length");
		indicatorLabel.setBounds(10, 285, 70, 20);
		indicatorLabel.setText("Pit indicator");
		speedLimitLabel.setBounds(10, 310, 70, 20);
		speedLimitLabel.setText("Pit speed limit");
		generatePitsLabel.setBounds(240, 10, 99, 20);
		generatePitsLabel.setText("Generate Pits");
		this.add(getLengthTextField(), null);
		this.add(getWidthTextField(), null);
		this.add(lengthLabel, null);
		this.add(getExitTextField(), null);
		this.add(widthLabel, null);
		this.add(getEntryTextField(), null);
		this.add(startLabel, null);
		this.add(getStartTextField(), null);
		this.add(endLabel, null);
		this.add(getEndTextField(), null);
		this.add(exitLabel, null);
		this.add(sideLabel, null);
		this.add(getSideComboBox(), null);
		this.add(entryLabel, null);
		this.add(getStyleComboBox(), null);
		this.add(styleLabel, null);
		this.add(generatePitsLabel, null);
		this.add(getGeneratePitsCheckBox(), null);
		this.add(startBuildingsLabel, null);
		this.add(getStartBuildingsTextField(), null);
		this.add(stopBuildingsLabel, null);
		this.add(getStopBuildingsTextField(), null);
		this.add(getMaxPitsTextField(), null);
		this.add(maxPitsLabel, null);
		this.add(getIndicatorTextField(), null);
		this.add(indicatorLabel, null);
		this.add(getSpeedLimitTextField(), null);
		this.add(speedLimitLabel, null);
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
			String[] items = {"none", "on track side", "on separate path", "no building"};
			styleComboBox = new JComboBox<String>(items);
			int style = Editor.getProperties().getPits().getStyle();
			if (style == Integer.MAX_VALUE)
				style = 0;
			styleComboBox.setSelectedIndex(style);
			styleComboBox.setBounds(100, 10, 100, 20);
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
			String side = Editor.getProperties().getPits().getSide();
			if (side == null || side.isEmpty())
				side = "none";
			sideComboBox.setSelectedItem(side);
			sideComboBox.setBounds(100, 35, 80, 20);
		}
		return sideComboBox;
	}
	/**
	 * This method initializes entryTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getEntryTextField()
	{
		if (entryTextField == null)
		{
			entryTextField = new JTextField();
			entryTextField.setText(Editor.getProperties().getPits().getEntry());
			entryTextField.setBounds(100, 60, 100, 20);
		}
		return entryTextField;
	}
	/**
	 * This method initializes startTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getStartTextField()
	{
		if (startTextField == null)
		{
			startTextField = new JTextField();
			startTextField.setText(Editor.getProperties().getPits().getStart());
			startTextField.setBounds(100, 85, 100, 20);
		}
		return startTextField;
	}
	/**
	 * This method initializes startBuildingsTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getStartBuildingsTextField()
	{
		if (startBuildingsTextField == null)
		{
			startBuildingsTextField = new JTextField();
			startBuildingsTextField.setText(Editor.getProperties().getPits().getStartBuildings());
			startBuildingsTextField.setBounds(100, 110, 100, 20);
		}
		return startBuildingsTextField;
	}
	/**
	 * This method initializes stopBuildingsTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getStopBuildingsTextField()
	{
		if (stopBuildingsTextField == null)
		{
			stopBuildingsTextField = new JTextField();
			stopBuildingsTextField.setText(Editor.getProperties().getPits().getStopBuildings());
			stopBuildingsTextField.setBounds(100, 135, 100, 20);
		}
		return stopBuildingsTextField;
	}
	/**
	 * This method initializes maxPitsTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getMaxPitsTextField()
	{
		if (maxPitsTextField == null)
		{
			maxPitsTextField = new JTextField();
			if (Editor.getProperties().getPits().getMaxPits() != Integer.MAX_VALUE)
				maxPitsTextField.setText(Editor.getProperties().getPits().getMaxPits() + "");
			maxPitsTextField.setBounds(100, 160, 100, 20);
		}
		return maxPitsTextField;
	}
	/**
	 * This method initializes endTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getEndTextField()
	{
		if (endTextField == null)
		{
			endTextField = new JTextField();
			endTextField.setText(Editor.getProperties().getPits().getEnd());
			endTextField.setBounds(100, 185, 100, 20);
		}
		return endTextField;
	}
	/**
	 * This method initializes exitTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getExitTextField()
	{
		if (exitTextField == null)
		{
			exitTextField = new JTextField();
			exitTextField.setText(Editor.getProperties().getPits().getExit());
			exitTextField.setBounds(100, 210, 100, 20);
		}
		return exitTextField;
	}
	/**
	 * This method initializes widthTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getWidthTextField()
	{
		if (widthTextField == null)
		{
			widthTextField = new JTextField();
			double val = Editor.getProperties().getPits().getWidth();
			if (!Double.isNaN(val))
				widthTextField.setText(Double.toString(val));
			widthTextField.setBounds(100, 235, 40, 20);
		}
		return widthTextField;
	}
	/**
	 * This method initializes lengthTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getLengthTextField()
	{
		if (lengthTextField == null)
		{
			lengthTextField = new JTextField();
			double val = Editor.getProperties().getPits().getLength();
			if (!Double.isNaN(val))
				lengthTextField.setText(Double.toString(val));
			lengthTextField.setBounds(100, 260, 40, 20);
		}
		return lengthTextField;
	}
	/**
	 * This method initializes indicatorTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getIndicatorTextField()
	{
		if (indicatorTextField == null)
		{
			indicatorTextField = new JTextField();
			int val = Editor.getProperties().getPits().getIndicator();
			if (val != Integer.MAX_VALUE)
				indicatorTextField.setText(Integer.toString(val));
			indicatorTextField.setBounds(100, 285, 40, 20);
		}
		return indicatorTextField;
	}
	/**
	 * This method initializes speedLimitTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getSpeedLimitTextField()
	{
		if (speedLimitTextField == null)
		{
			speedLimitTextField = new JTextField();
			double val = Editor.getProperties().getPits().getSpeedLimit();
			if (!Double.isNaN(val))
				speedLimitTextField.setText(Double.toString(val));
			speedLimitTextField.setBounds(100, 310, 40, 20);
		}
		return speedLimitTextField;
	}

	/**
	 *  
	 */
	public void exit()
	{
		Editor.getProperties().getPits().setStyle(getStyleComboBox().getSelectedIndex());	// TODO handle missing
		String side = (String) getSideComboBox().getSelectedItem();
		if (side == "none")
			side = null;
		Editor.getProperties().getPits().setSide(side);
		Editor.getProperties().getPits().setEntry(this.getEntryTextField().getText());
		Editor.getProperties().getPits().setStart(this.getStartTextField().getText());
		Editor.getProperties().getPits().setStartBuildings(this.getStartBuildingsTextField().getText());
		Editor.getProperties().getPits().setStopBuildings(this.getStopBuildingsTextField().getText());
		try
		{
			int maxPits = Integer.parseInt(this.getMaxPitsTextField().getText());
			if (maxPits != Integer.MAX_VALUE)
				Editor.getProperties().getPits().setMaxPits(maxPits);
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getPits().setMaxPits(Integer.MAX_VALUE);
		}
		Editor.getProperties().getPits().setEnd(this.getEndTextField().getText());
		Editor.getProperties().getPits().setExit(this.getExitTextField().getText());
		try
		{
			Editor.getProperties().getPits().setWidth(Double.parseDouble(this.getWidthTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getPits().setWidth(Double.NaN);
		}
		try
		{
			Editor.getProperties().getPits().setLength(Double.parseDouble(this.getLengthTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getPits().setLength(Double.NaN);
		}
		if(this.getGeneratePitsCheckBox().isSelected())
		{
			createPits();
		}
		try
		{
			int indicator = Integer.parseInt(this.getIndicatorTextField().getText());
			if (indicator != Integer.MAX_VALUE)
				Editor.getProperties().getPits().setIndicator(indicator);
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getPits().setIndicator(Integer.MAX_VALUE);
		}
		try
		{
			Editor.getProperties().getPits().setSpeedLimit(Double.parseDouble(this.getSpeedLimitTextField().getText()));
		} catch (NumberFormatException e)
		{
			Editor.getProperties().getPits().setSpeedLimit(Double.NaN);
		}
		Editor.getProperties().valueChanged();
	}
	/**
	 * 
	 */
	private void createPits()
	{
		Vector<Segment> data = TrackData.getTrackData();
		Segment pitEntry = null;
		Segment pitStart = null;
		Segment pitStartBuildings = null;
		Segment pitStopBuildings = null;
		Segment pitEnd = null;
		Segment pitExit = null;
		
		Iterator it = data.iterator();
		while (it.hasNext())
		{
			Segment obj = (Segment) it.next();
			String name = obj.getName();
			if (name.equals(Editor.getProperties().getPits().getEntry()))
			{
				pitEntry = obj;
			}else if (name.equals(Editor.getProperties().getPits().getStart()))
			{
				pitStart = obj;
			}else if (name.equals(Editor.getProperties().getPits().getStartBuildings()))
			{
				pitStartBuildings = obj;
			}else if (name.equals(Editor.getProperties().getPits().getStopBuildings()))
			{
				pitStopBuildings = obj;
			}else if (name.equals(Editor.getProperties().getPits().getEnd()))
			{
				pitEnd = obj;
			}else if (name.equals(Editor.getProperties().getPits().getExit()))
			{
				pitExit = obj;
			}
		}
		SegmentSide side = null;
		if(pitEntry == null)
		{
			System.out.println("No pit entry");
			return;
		}
		if(Editor.getProperties().getPits().getSide().equals("left"))
		{
			side = pitEntry.getLeft();
		}else
		{
			side = pitEntry.getRight();
		}
		side.setBorderHeight(0);
		side.setBorderWidth(0);
		side.setSideEndWidth(Editor.getProperties().getPits().getWidth()*3);
		side.setSideSurface("road1");
		side.setBarrierHeight(1);
		side.setBarrierWidth(0.1);
		
		if(pitExit == null)
		{
			System.out.println("No pit exit");
			return;
		}
		if(Editor.getProperties().getPits().getSide().equals("left"))
		{
			side = pitExit.getLeft();
		}else
		{
			side = pitExit.getRight();
		}
		side.setBorderHeight(0);
		side.setBorderWidth(0);
		side.setSideStartWidth(Editor.getProperties().getPits().getWidth()*3);
		side.setSideSurface("road1");
		side.setBarrierHeight(1);
		side.setBarrierWidth(0.1);
		
		if(pitStart == null || pitEnd == null)
		{
			System.out.println("No pit start or end");
			return;
		}
		
		int start = data.indexOf(pitEntry);
		int end = data.indexOf(pitExit);
		
		for(int i=start+1; i<data.size(); i++)
		{
			if(Editor.getProperties().getPits().getSide().equals("left"))
			{
				side = ((Segment) data.get(i)).getLeft();
			}else
			{
				side = ((Segment) data.get(i)).getRight();
			}
			side.setBorderHeight(1);
			side.setBorderWidth(0.1);
			side.setSideStartWidth(Editor.getProperties().getPits().getWidth()*3);
			side.setSideEndWidth(Editor.getProperties().getPits().getWidth()*3);
			side.setSideSurface("road1-pits");
			side.setBarrierHeight(1);
			side.setBarrierWidth(0.1);
		}
		
		for(int i=0; i<end; i++)
		{
			if(Editor.getProperties().getPits().getSide().equals("left"))
			{
				side = ((Segment) data.get(i)).getLeft();
			}else
			{
				side = ((Segment) data.get(i)).getRight();
			}
			side.setBorderHeight(1);
			side.setBorderWidth(0.1);
			side.setSideStartWidth(Editor.getProperties().getPits().getWidth()*3);
			side.setSideEndWidth(Editor.getProperties().getPits().getWidth()*3);
			side.setSideSurface("road1-pits");
			side.setBarrierHeight(1);
			side.setBarrierWidth(0.1);
		}
	}

	/**
	 * This method initializes generatePitsCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getGeneratePitsCheckBox() {
		if (generatePitsCheckBox == null) {
			generatePitsCheckBox = new JCheckBox();
			generatePitsCheckBox.setBounds(328, 10, 20, 20);
			generatePitsCheckBox.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) 
				{    
					if(generatePitsCheckBox.isSelected())
					{
						generatePits = true;
					}else
					{
						generatePits = false;
					}
				}
			});
		}
		return generatePitsCheckBox;
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
