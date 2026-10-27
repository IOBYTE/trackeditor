/*
 *   SegmentSideProperties.java
 *   Created on Aug 25, 2004
 *
 *    The SegmentSideProperties.java is part of TrackEditor-0.2.0.
 *
 *    TrackEditor-0.2.0 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.2.0 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.2.0; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package gui.segment;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import gui.EditorFrame;
import utils.SurfaceComboBox;
import utils.circuit.Segment;
import utils.circuit.SegmentSide;
import utils.circuit.Surface;
import bsh.EvalError;
import bsh.Interpreter;
/**
 * @author babis
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SegmentSideProperties extends JPanel implements SliderListener
{
	private SegmentSide			side;
	private SegmentEditorDlg	parent;

	private String[]			borderStyleItems		= {"none", "plan", "wall", "curb"};
	private String[]			borderSurfaceItems		=
														{"curb-5cm-r", "curb-5cm-l", "curb-l", "tar-grass3-l",
			"tar-grass3-r", "tar-sand", "b-road1-grass6", "b-road1-grass6-l2", "b-road1-gravel-l2", "b-road1-sand3",
			"b-road1-sand3-l2", "b-asphalt-grass7", "b-asphalt-grass7-l1", "b-asphalt-grass6", "b-asphalt-grass6-l1",
			"b-asphalt-sand3", "b-asphalt-sand3-l1", "grass", "grass3", "grass5", "grass6", "grass7", "gravel",
			"sand3", "sand", "asphalt-lines", "asphalt-l-left", "asphalt-l-right", "asphalt-l-both", "asphalt-pits",
			"asphalt", "dirt", "dirt-b", "asphalt2", "road1", "road1-pits", "road1-asphalt", "asphalt-road1",
			"b-road1", "b-road1-l2", "b-road1-l2p", "concrete", "concrete2", "concrete3", "b-asphalt", "b-asphalt-l1",
			"b-asphalt-l1p", "asphalt2-lines", "asphalt2-l-left", "asphalt2-l-right", "asphalt2-l-both", "barrier",
			"barrier2", "barrier-turn", "barrier-grille", "wall", "wall2", "tire-wall"};
	private Vector<String>		borderSurfaceVector		= new Vector<String>();
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
	private Vector<String>		sideSurfaceVector		= new Vector<String>();
	private String[]			barrierStyleItems		= {"none", "no barrier", "wall", "fence", "fence1", "fence2"};
	// only Speed Dreams has fence1/fence2 (for the others they are written as fence) and
	// no barrier (CSR's track loader reads past the end of its style list for the segment
	// after a "no barrier" side, and TORCS builds a wall)
	private String[]			basicBarrierStyleItems	= {"none", "wall", "fence"};
	private boolean				updatingBarrierStyle	= false;
	// true while setSide fills in the controls: that isn't an edit
	private boolean				filling					= false;
	private String[]			barrierSurfaceItems		=
														{"barrier", "barrier2", "barrier-turn", "barrier-grille",
			"wall", "wall2", "tire-wall", "asphalt-lines", "asphalt-l-left", "asphalt-l-right", "asphalt-l-both",
			"asphalt-pits", "asphalt", "dirt", "dirt-b", "asphalt2", "road1", "road1-pits", "road1-asphalt",
			"asphalt-road1", "b-road1", "b-road1-l2", "b-road1-l2p", "concrete", "concrete2", "concrete3", "b-asphalt",
			"b-asphalt-l1", "b-asphalt-l1p", "asphalt2-lines", "asphalt2-l-left", "asphalt2-l-right",
			"asphalt2-l-both", "curb-5cm-r", "curb-5cm-l", "curb-l", "tar-grass3-l", "tar-grass3-r", "tar-sand",
			"b-road1-grass6", "b-road1-grass6-l2", "b-road1-gravel-l2", "b-road1-sand3", "b-road1-sand3-l2",
			"b-asphalt-grass7", "b-asphalt-grass7-l1", "b-asphalt-grass6", "b-asphalt-grass6-l1", "b-asphalt-sand3",
			"b-asphalt-sand3-l1", "grass", "grass3", "grass5", "grass6", "grass7", "gravel", "sand3", "sand"};
	private Vector<String>		barrierSurfaceVector	= new Vector<String>();

	public JPanel				panel					= null;
	private JLabel				borderLabel				= null;
	private JLabel				borderSurfaceLabel		= null;
	private JLabel				borderStyleLabel		= null;
	private JLabel				sideLabel				= null;
	private JLabel				sideSurfaceLabel		= null;
	private JLabel				barrierLabel			= null;
	private JLabel				barrierStyleLabel		= null;
	private JLabel				barrierSurfaceLabel		= null;
	public JLabel				titleLabel				= null;
	private SegmentSlider		barrierHeightSlider		= null;
	private SurfaceComboBox		borderSurfaceComboBox	= null;
	private JComboBox<String>	borderStyleComboBox		= null;
	private SurfaceComboBox		sideSurfaceComboBox		= null;
	private SurfaceComboBox		barrierSurfaceComboBox	= null;
	private JComboBox<String>	barrierStyleComboBox	= null;
	private SegmentSlider		sideStartWidthSlider	= null;
	private SegmentSlider		borderWidthSlider		= null;
	private SegmentSlider		sideEndWidthSlider		= null;
	private SegmentSlider		barrierWidthSlider		= null;
	private SegmentSlider		borderHeightSlider		= null;

	/**
	 *
	 */
	public SegmentSideProperties(SegmentEditorDlg parent, Segment segment, SegmentSide side)
	{
		this.parent = parent;
		
		// add new surfaces from Surfaces
		addDefaultSurfaces(sideSurfaceVector, sideSurfaceItems);
		addDefaultSurfaces(borderSurfaceVector, borderSurfaceItems);
		addDefaultSurfaces(barrierSurfaceVector, barrierSurfaceItems);
		
		setSide(segment, side);
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize()
	{
		titleLabel = new JLabel();
		this.setLayout(null);
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		this.setLocation(0, 0);
		titleLabel.setBounds(396, 5, 70, 23);
		titleLabel.setText("Right");
		titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
		this.add(getPanel(), null);
		this.add(titleLabel, null);
	}

	public void refresh()
	{
		sideSurfaceVector.clear();
		addDefaultSurfaces(sideSurfaceVector, sideSurfaceItems);
		panel.remove(sideSurfaceComboBox);
		sideSurfaceComboBox = null;
		panel.add(getSideSurfaceComboBox(), null);

		borderSurfaceVector.clear();
		addDefaultSurfaces(borderSurfaceVector, borderSurfaceItems);
		panel.remove(borderSurfaceComboBox);
		borderSurfaceComboBox = null;
		panel.add(getBorderSurfaceComboBox(), null);

		barrierSurfaceVector.clear();
		addDefaultSurfaces(barrierSurfaceVector, barrierSurfaceItems);
		panel.remove(barrierSurfaceComboBox);
		barrierSurfaceComboBox = null;
		panel.add(getBarrierSurfaceComboBox(), null);
	}

	private void addDefaultSurfaces(Vector<String> surfaceVector, String[] fallback)
	{
		// use the default surfaces if available
		Vector<Surface> defaultSurfaces = parent.editorFrame.getDefaultSurfaces();

		if (defaultSurfaces.isEmpty())
		{
			// add the ones found
			surfaceVector.addAll(Arrays.asList(fallback));
		}
		else
		{
			// add the ones that should be there
	        for (int i = 0; i < defaultSurfaces.size(); i++)
	        {
	        	surfaceVector.add(defaultSurfaces.get(i).getName());
	        }
		}

        Vector<Surface> surfaces = parent.editorFrame.getTrackData().getSurfaces();
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
	 * This method initializes panel
	 *
	 * @return javax.swing.JPanel
	 */
	public JPanel getPanel()
	{
		if (panel == null)
		{
			borderLabel = new JLabel();
			borderStyleLabel = new JLabel();
			borderSurfaceLabel = new JLabel();
			panel = new JPanel();
			sideSurfaceLabel = new JLabel();
			sideLabel = new JLabel();
			barrierSurfaceLabel = new JLabel();
			barrierStyleLabel = new JLabel();
			barrierLabel = new JLabel();
			panel.setLayout(null);
			panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			borderLabel.setBounds(698, 2, 50, 23);
			borderLabel.setText("Border");
			borderSurfaceLabel.setBounds(698, 325, 55, 23);
			borderSurfaceLabel.setText("Surface");
			borderStyleLabel.setBounds(703, 375, 55, 23);
			borderStyleLabel.setText("Style");
			panel.add(borderSurfaceLabel, null);
			panel.add(borderStyleLabel, null);
			panel.add(borderLabel, null);
			panel.add(sideLabel, null);
			panel.add(sideSurfaceLabel, null);
			panel.add(barrierLabel, null);
			panel.add(barrierStyleLabel, null);
			panel.add(barrierSurfaceLabel, null);
			panel.setLocation(3, 35);
			panel.setSize(823, 431);
			sideLabel.setText("Side");
			sideLabel.setSize(45, 23);
			sideLabel.setLocation(401, 2);
			sideSurfaceLabel.setText("Surface");
			sideSurfaceLabel.setBounds(391, 325, 55, 23);
			barrierLabel.setText("Barrier");
			barrierLabel.setBounds(85, 2, 50, 23);
			barrierStyleLabel.setText("Style");
			barrierStyleLabel.setBounds(90, 375, 55, 23);
			barrierSurfaceLabel.setText("Surface");
			barrierSurfaceLabel.setBounds(85, 325, 55, 23);
			panel.add(getBarrierHeightSlider(), null);
			panel.add(getBorderSurfaceComboBox(), null);
			panel.add(getBorderStyleComboBox(), null);
			panel.add(getSideSurfaceComboBox(), null);
			panel.add(getBarrierSurfaceComboBox(), null);
			panel.add(getBarrierStyleComboBox(), null);
			panel.add(getSideStartWidthSlider(), null);
			panel.add(getBorderWidthSlider(), null);
			panel.add(getSideEndWidthSlider(), null);
			panel.add(getBarrierWidthSlider(), null);
			panel.add(getBorderHeightSlider(), null);
		}
		return panel;
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
			borderSurfaceComboBox = new SurfaceComboBox(parent.editorFrame, borderSurfaceVector);
			borderSurfaceComboBox.setBounds(618, 350, 200, 23);
			borderSurfaceComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					// a choice is an edit (undo, save, redraw); filling in the shown value isn't
					if (filling || borderSurfaceComboBox.getSelectedItem() == null)
						return;
					String surface = borderSurfaceComboBox.getSelectedItem() + "";
					if (surface.equals(side.getBorderSurface()))
						return;
					side.setBorderSurface(surface);
					parent.sideChanged();
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
			borderStyleComboBox.setBounds(658, 400, 120, 23);
			borderStyleComboBox.setModel(new DefaultComboBoxModel<String>(borderStyleItems));
			borderStyleComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (filling)
						return;
					String oldStyle = side.getBorderStyle();
					if (oldStyle == null || oldStyle.isEmpty())
						oldStyle = "none";
					String newStyle = borderStyleComboBox.getSelectedItem().toString();
					boolean styleChanged = !oldStyle.equals(newStyle);
					String style = newStyle;
					if ("none".equals(style))
						style = null;
					side.setBorderStyle(style);
					if (!styleChanged)
					{
						return;
					}
					switch (newStyle)
					{
					case "none":
						side.setBorderStyle(null);
						side.setBorderSurface(null);
						side.setBorderHeight(Double.NaN);
						side.setBorderWidth(Double.NaN);
						getBorderSurfaceComboBox().setSelectedIndex(-1);
						getBorderSurfaceComboBox().setEnabled(false);
						getBorderHeightSlider().setValue(side.getBorderHeight());
						getBorderHeightSlider().setEnabled(false);
						getBorderWidthSlider().setValue(side.getBorderWidth());
						getBorderWidthSlider().setEnabled(false);
						break;
					case "plan":
						if (side.isRight())
						{
							side.setBorderSurface(SegmentSide.DEFAULT_BORDER_PLAN_RIGHT_SURFACE);
						}
						else
						{
							side.setBorderSurface(SegmentSide.DEFAULT_BORDER_PLAN_LEFT_SURFACE);
						}
						side.setBorderHeight(SegmentSide.DEFAULT_BORDER_PLAN_HEIGHT);
						side.setBorderWidth(SegmentSide.DEFAULT_BORDER_PLAN_WIDTH);
						getBorderSurfaceComboBox().setEnabled(true);
						getBorderSurfaceComboBox().setSelectedItem(side.getBorderSurface());
						getBorderHeightSlider().setEnabled(true);
						getBorderHeightSlider().setValueFrozen(side.getBorderHeight());
						getBorderWidthSlider().setEnabled(true);
						getBorderWidthSlider().setValue(side.getBorderWidth());
						break;
					case "wall":
						side.setBorderSurface(SegmentSide.DEFAULT_BORDER_WALL_SURFACE);
						side.setBorderHeight(SegmentSide.DEFAULT_BORDER_WALL_HEIGHT);
						side.setBorderWidth(SegmentSide.DEFAULT_BORDER_WALL_WIDTH);
						getBorderSurfaceComboBox().setEnabled(true);
						getBorderSurfaceComboBox().setSelectedItem(side.getBorderSurface());
						getBorderHeightSlider().setEnabled(true);
						getBorderHeightSlider().setValue(side.getBorderHeight());
						getBorderWidthSlider().setEnabled(true);
						getBorderWidthSlider().setValue(side.getBorderWidth());
						break;
					case "curb":
						if (side.isRight())
						{
							side.setBorderSurface(SegmentSide.DEFAULT_BORDER_CURB_RIGHT_SURFACE);
						}
						else
						{
							side.setBorderSurface(SegmentSide.DEFAULT_BORDER_CURB_LEFT_SURFACE);
						}
						side.setBorderHeight(SegmentSide.DEFAULT_BORDER_CURB_HEIGHT);
						side.setBorderWidth(SegmentSide.DEFAULT_BORDER_CURB_WIDTH);
						getBorderSurfaceComboBox().setEnabled(true);
						getBorderSurfaceComboBox().setSelectedItem(side.getBorderSurface());
						getBorderHeightSlider().setEnabled(true);
						getBorderHeightSlider().setValue(side.getBorderHeight());
						getBorderWidthSlider().setEnabled(true);
						getBorderWidthSlider().setValue(side.getBorderWidth());
						break;
					}
					parent.sideChanged();
				}
			});
		}
		return borderStyleComboBox;
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
			sideSurfaceComboBox = new SurfaceComboBox(parent.editorFrame, sideSurfaceVector);
			sideSurfaceComboBox.setBounds(311, 350, 200, 23);
			sideSurfaceComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					// a choice is an edit (undo, save, redraw); filling in the shown value isn't
					if (filling || sideSurfaceComboBox.getSelectedItem() == null)
						return;
					String surface = sideSurfaceComboBox.getSelectedItem() + "";
					if (surface.equals(side.getSideSurface()))
						return;
					side.setSideSurface(surface);
					parent.sideChanged();
				}
			});
		}
		return sideSurfaceComboBox;
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
			barrierSurfaceComboBox = new SurfaceComboBox(parent.editorFrame, barrierSurfaceVector)
			{
				public void setEnabled(boolean value)
				{
					super.setEnabled(value && !isVersion3());
					setToolTipText(isVersion3() ? VERSION3_BARRIER_TIP : null);
				}
			};
			barrierSurfaceComboBox.setEnabled(true);
			barrierSurfaceComboBox.setBounds(5, 350, 200, 23);
			barrierSurfaceComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					// a choice is an edit (undo, save, redraw); filling in the shown value isn't
					if (filling || barrierSurfaceComboBox.getSelectedItem() == null)
						return;
					String surface = barrierSurfaceComboBox.getSelectedItem() + "";
					if (surface.equals(side.getBarrierSurface()))
						return;
					side.setBarrierSurface(surface);
					parent.sideChanged();
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
	/**
	 * @return the barrier styles for the current game type. Only a Speed Dreams track
	 * gets fence1/fence2 or no barrier, but a style already set (read from a Speed Dreams
	 * track) stays listed so viewing the segment doesn't change it.
	 */
	private String[] barrierStyleItems(String currentStyle)
	{
		if (parent.editorFrame.getGameType().hasNoBarrier())
			return barrierStyleItems;

		if (currentStyle != null && (currentStyle.equals("fence1") || currentStyle.equals("fence2") || currentStyle.equals("no barrier")))
		{
			String[] items = java.util.Arrays.copyOf(basicBarrierStyleItems, basicBarrierStyleItems.length + 1);
			items[items.length - 1] = currentStyle;
			return items;
		}

		return basicBarrierStyleItems;
	}

	private JComboBox<String> getBarrierStyleComboBox()
	{
		if (barrierStyleComboBox == null)
		{
			barrierStyleComboBox = new JComboBox<String>()
			{
				public void setEnabled(boolean value)
				{
					super.setEnabled(value && !isVersion3());
					setToolTipText(isVersion3() ? VERSION3_BARRIER_TIP : null);
				}
			};
			barrierStyleComboBox.setEnabled(true);
			barrierStyleComboBox.setBounds(45, 400, 120, 23);
			barrierStyleComboBox.setModel(new DefaultComboBoxModel<String>(barrierStyleItems(null)));
			barrierStyleComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (updatingBarrierStyle || filling)
						return;
					String oldStyle = side.getBarrierStyle();
					if (oldStyle == null || oldStyle.isEmpty())
						oldStyle = "none";
					String newStyle = barrierStyleComboBox.getSelectedItem().toString();
					boolean styleChanged = !oldStyle.equals(newStyle);
					String style = newStyle;
					if ("none".equals(style))
						style = null;
					side.setBarrierStyle(style);
					if (!styleChanged)
					{
						return;
					}
					switch (newStyle)
					{
					case "none":
						side.setBarrierSurface(null);
						side.setBarrierHeight(Double.NaN);
						side.setBarrierWidth(Double.NaN);
						getBarrierSurfaceComboBox().setSelectedIndex(-1);
						getBarrierSurfaceComboBox().setEnabled(false);
						getBarrierHeightSlider().setValue(side.getBarrierHeight());
						getBarrierHeightSlider().setEnabled(false);
						getBarrierWidthSlider().setValue(side.getBarrierWidth());
						getBarrierWidthSlider().setEnabled(false);
						break;
					case "no barrier":
						side.setBarrierSurface(null);
						side.setBarrierHeight(0.0);
						side.setBarrierWidth(0.0);
						getBarrierSurfaceComboBox().setSelectedIndex(-1);
						getBarrierSurfaceComboBox().setEnabled(false);
						getBarrierHeightSlider().setEnabled(true);
						getBarrierHeightSlider().setValueFrozen(side.getBarrierHeight());
						getBarrierWidthSlider().setEnabled(true);
						getBarrierWidthSlider().setValueFrozen(side.getBarrierWidth());
						break;
					case "wall":
						side.setBarrierSurface(SegmentSide.DEFAULT_BARRIER_WALL_SURFACE);
						side.setBarrierHeight(SegmentSide.DEFAULT_BARRIER_WALL_HEIGHT);
						side.setBarrierWidth(SegmentSide.DEFAULT_BARRIER_WALL_WIDTH);
						getBarrierSurfaceComboBox().setEnabled(true);
						getBarrierSurfaceComboBox().setSelectedItem(side.getBarrierSurface());
						getBarrierHeightSlider().setEnabled(true);
						getBarrierHeightSlider().setValue(side.getBarrierHeight());
						getBarrierWidthSlider().setEnabled(true);
						getBarrierWidthSlider().setValue(side.getBarrierWidth());
						break;
					case "fence":
						if (!oldStyle.equals("fence1") && !oldStyle.equals("fence2"))
						{
							side.setBarrierSurface(SegmentSide.DEFAULT_BARRIER_FENCE_SURFACE);
							side.setBarrierHeight(SegmentSide.DEFAULT_BARRIER_FENCE_HEIGHT);
							side.setBarrierWidth(SegmentSide.DEFAULT_BARRIER_FENCE_WIDTH);
							getBarrierSurfaceComboBox().setEnabled(true);
							getBarrierSurfaceComboBox().setSelectedItem(side.getBarrierSurface());
							getBarrierHeightSlider().setEnabled(true);
							getBarrierHeightSlider().setValue(side.getBarrierHeight());
							getBarrierWidthSlider().setEnabled(true);
							getBarrierWidthSlider().setValueFrozen(side.getBarrierWidth());
						}
						break;
					case "fence1":
						if (!oldStyle.equals("fence") && !oldStyle.equals("fence2"))
						{
							side.setBarrierSurface(SegmentSide.DEFAULT_BARRIER_FENCE_SURFACE);
							side.setBarrierHeight(SegmentSide.DEFAULT_BARRIER_FENCE_HEIGHT);
							side.setBarrierWidth(SegmentSide.DEFAULT_BARRIER_FENCE_WIDTH);
							getBarrierSurfaceComboBox().setEnabled(true);
							getBarrierSurfaceComboBox().setSelectedItem(side.getBarrierSurface());
							getBarrierHeightSlider().setEnabled(true);
							getBarrierHeightSlider().setValue(side.getBarrierHeight());
							getBarrierWidthSlider().setEnabled(true);
							getBarrierWidthSlider().setValueFrozen(side.getBarrierWidth());
						}
						break;
					case "fence2":
						if (!oldStyle.equals("fence") && !oldStyle.equals("fence1"))
						{
							side.setBarrierSurface(SegmentSide.DEFAULT_BARRIER_FENCE_SURFACE);
							side.setBarrierHeight(SegmentSide.DEFAULT_BARRIER_FENCE_HEIGHT);
							side.setBarrierWidth(SegmentSide.DEFAULT_BARRIER_FENCE_WIDTH);
							getBarrierSurfaceComboBox().setEnabled(true);
							getBarrierSurfaceComboBox().setSelectedItem(side.getBarrierSurface());
							getBarrierHeightSlider().setEnabled(true);
							getBarrierHeightSlider().setValue(side.getBarrierHeight());
							getBarrierWidthSlider().setEnabled(true);
							getBarrierWidthSlider().setValueFrozen(side.getBarrierWidth());
						}
						break;
					}
					parent.sideChanged();
				}
			});
		}
		return barrierStyleComboBox;
	}
	/**
	 * This method initializes barrierHeightSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getBarrierHeightSlider()
	{
		if (barrierHeightSlider == null)
		{
			barrierHeightSlider = new Version3BarrierSlider(side.getBarrierHeight(), "Height", "BarrierHeight")
			{
				double getVersion3Value()
				{
					return getVersion3BarrierHeight();
				}
			};
			barrierHeightSlider.setBounds(52, 25, 50, 290);
			barrierHeightSlider.addSliderListener(this);
			barrierHeightSlider.setEnabled(barrierHeightSlider.isEnabled());
		}
		return barrierHeightSlider;
	}

	private static final String VERSION3_BARRIER_TIP = "Version 3 tracks use the Main Track barrier for every segment.";

	private boolean isVersion3()
	{
		return parent.editorFrame.getTrackData().getHeader().getVersion() == 3;
	}

	/** The Main Track side the barrier of every segment of a version 3 track comes from */
	private SegmentSide getVersion3MainSide()
	{
		return side.isRight() ? parent.editorFrame.getTrackData().getMainTrack().getRight()
			: parent.editorFrame.getTrackData().getMainTrack().getLeft();
	}

	/** The barrier style a version 3 track uses: the Main Track's (fence when it has none) */
	private String getVersion3BarrierStyle()
	{
		String style = getVersion3MainSide().getBarrierStyle();
		return style == null || style.isEmpty() ? "fence" : style;
	}

	/** The barrier height a version 3 track uses: the Main Track's (1 m when it has none) */
	private double getVersion3BarrierHeight()
	{
		double height = getVersion3MainSide().getBarrierHeight();
		return Double.isNaN(height) ? 1.0 : height;
	}

	/** The barrier width a version 3 track uses: 0 for a fence, else the Main Track's (0.5 m when it has none) */
	private double getVersion3BarrierWidth()
	{
		if (getVersion3BarrierStyle().equals("fence"))
			return 0.0;
		double width = getVersion3MainSide().getBarrierWidth();
		return Double.isNaN(width) ? 0.5 : width;
	}

	/** The barrier surface a version 3 track uses: the Main Track's (barrier when it has none) */
	private String getVersion3BarrierSurface()
	{
		String surface = getVersion3MainSide().getBarrierSurface();
		return surface == null || surface.isEmpty() ? "barrier" : surface;
	}

	/**
	 * A barrier slider that, in a version 3 track, shows the Main Track's value and
	 * can't be changed: the version 3 loaders read the barrier only from the Main Track.
	 */
	private abstract class Version3BarrierSlider extends SegmentSlider
	{
		Version3BarrierSlider(double value, String section, String method)
		{
			super(0, 50, 0, 0.001, value, section, "", method, true, false);
		}

		abstract double getVersion3Value();

		public void setEnabled(boolean value)
		{
			if (isVersion3())
			{
				super.setEnabled(true);
				super.setValueFrozen(getVersion3Value());
			}
			else
			{
				super.setEnabled(value);
			}
			setToolTip(this, isVersion3() ? VERSION3_BARRIER_TIP : null);
		}

		public void setValue(double value)
		{
			if (isVersion3())
				super.setValueFrozen(getVersion3Value());
			else
				super.setValue(value);
		}

		public void setValueFrozen(double value)
		{
			super.setValueFrozen(isVersion3() ? getVersion3Value() : value);
		}
	}

	/** Shows the Main Track barrier of a version 3 track; none of it can be changed here */
	private void showVersion3Barrier()
	{
		String surface = getVersion3BarrierSurface();
		addSurface(barrierSurfaceVector, surface);
		getBarrierSurfaceComboBox().setSelectedItem(surface);
		getBarrierSurfaceComboBox().setEnabled(false);

		String style = getVersion3BarrierStyle();
		updatingBarrierStyle = true;
		getBarrierStyleComboBox().setModel(new DefaultComboBoxModel<String>(barrierStyleItems(style)));
		selectStyle(getBarrierStyleComboBox(), style);
		updatingBarrierStyle = false;
		getBarrierStyleComboBox().setEnabled(false);

		getBarrierHeightSlider().setEnabled(false);
		getBarrierWidthSlider().setEnabled(false);
	}

	private static void setToolTip(javax.swing.JComponent component, String text)
	{
		component.setToolTipText(text);
		for (java.awt.Component child : component.getComponents())
		{
			if (child instanceof javax.swing.JComponent)
				setToolTip((javax.swing.JComponent) child, text);
		}
	}

	/**
	 * This method initializes sideStartWidthSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getSideStartWidthSlider()
	{
		if (sideStartWidthSlider == null)
		{
			sideStartWidthSlider = new SegmentSlider(0, 100, 0, 0.001, side.getSideStartWidth(), "Width", "Start", "SideStartWidth", true, false);
			sideStartWidthSlider.setBounds(358, 25, 50, 290);
			sideStartWidthSlider.addSliderListener(this);
		}
		return sideStartWidthSlider;
	}
	/**
	 * This method initializes borderWidthSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getBorderWidthSlider()
	{
		if (borderWidthSlider == null)
		{
			borderWidthSlider = new SegmentSlider(0, 5, 0, 0.001, side.getBorderWidth(), "Width", "", "BorderWidth", true, false);
			borderWidthSlider.setBounds(720, 25, 50, 290);
			borderWidthSlider.addSliderListener(this);
		}
		return borderWidthSlider;
	}
	/**
	 * This method initializes sideEndWidthSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getSideEndWidthSlider()
	{
		if (sideEndWidthSlider == null)
		{
			sideEndWidthSlider = new SegmentSlider(0, 100, 0, 0.001, side.getSideEndWidth(), "Width", "End", "SideEndWidth", true, false);
			sideEndWidthSlider.setBounds(413, 25, 50, 290);
			sideEndWidthSlider.addSliderListener(this);
		}
		return sideEndWidthSlider;
	}
	/**
	 * This method initializes barrierWidthSlider
	 *
	 * @return gui.segment.SegmentSlider
	 */
	private SegmentSlider getBarrierWidthSlider()
	{
		if (barrierWidthSlider == null)
		{
			barrierWidthSlider = new Version3BarrierSlider(side.getBarrierWidth(), "Width", "BarrierWidth")
			{
				double getVersion3Value()
				{
					return getVersion3BarrierWidth();
				}
			};
			barrierWidthSlider.setBounds(107, 25, 50, 290);
			barrierWidthSlider.addSliderListener(this);
			barrierWidthSlider.setEnabled(barrierWidthSlider.isEnabled());
		}
		return barrierWidthSlider;
	}
	/**
	 * This method initializes borderHeightSlider
	 *
	 * @return gui.segment.SegmentSlider
	 */
	private SegmentSlider getBorderHeightSlider()
	{
		if (borderHeightSlider == null)
		{
			borderHeightSlider = new SegmentSlider(0, 5, 0, 0.001, side.getBorderHeight(), "Height", "", "BorderHeight", true, false);
			borderHeightSlider.setBounds(665, 25, 50, 290);
			borderHeightSlider.addSliderListener(this);
		}
		return borderHeightSlider;
	}

	public void update()
	{
	}

	/**
	 * @return Returns the titleLabel.
	 */
	public String getTitle()
	{
		return titleLabel.getText();
	}
	/**
	 * @param titleLabel
	 *            The titleLabel to set.
	 */
	public void setTitle(String title)
	{
		this.titleLabel.setText(title);
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

	public void setSide(Segment segment, SegmentSide side)
	{
		filling = true;
		try
		{
			fillSide(segment, side);
		}
		finally
		{
			filling = false;
		}
	}

	/**
	 * Selects style, adding it to the list when the list doesn't have it (a style
	 * the game reads differently, like a "curb" barrier), so the segment's own style
	 * is shown instead of the previous segment's.
	 */
	private static void selectStyle(JComboBox<String> comboBox, String style)
	{
		DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboBox.getModel();
		if (model.getIndexOf(style) == -1)
			model.addElement(style);
		comboBox.setSelectedItem(style);
	}

	private void fillSide(Segment segment, SegmentSide side)
	{
		this.side = side;

		// update side
		String surface = this.side.getSideSurface();
		addSurface(sideSurfaceVector, surface);

		if (surface != null)
			this.getSideSurfaceComboBox().setSelectedItem(surface);
		else
			this.getSideSurfaceComboBox().setSelectedIndex(-1);

		this.getSideStartWidthSlider().setValue(side.getSideStartWidth());
		this.getSideEndWidthSlider().setValue(side.getSideEndWidth());

		// update border
		surface = this.side.getBorderSurface();
		addSurface(borderSurfaceVector, surface);	

		if (surface != null && !surface.isEmpty())
			this.getBorderSurfaceComboBox().setSelectedItem(surface);
		else
			this.getBorderSurfaceComboBox().setSelectedIndex(-1);

		String style = side.getBorderStyle();
		if (style == null || style.isEmpty())
			style = "none";
		// a fresh list each time, so an unlisted style shown for one segment isn't offered for the next
		getBorderStyleComboBox().setModel(new DefaultComboBoxModel<String>(borderStyleItems));
		selectStyle(getBorderStyleComboBox(), style);
		if (parent.editorFrame.getInteractiveFixes())
			fixBorder(segment, side);
		switch (style)
		{
		case "none":
			getBorderHeightSlider().setValue(side.getBorderHeight());
			getBorderHeightSlider().setEnabled(false);
			getBorderWidthSlider().setValue(side.getBorderWidth());
			getBorderWidthSlider().setEnabled(false);
			break;
		case "plan":
			getBorderHeightSlider().setEnabled(true);
			getBorderHeightSlider().setValueFrozen(side.getBorderHeight());
			getBorderWidthSlider().setEnabled(true);
			getBorderWidthSlider().setValue(side.getBorderWidth());
			break;
		case "wall":
			getBorderHeightSlider().setEnabled(true);
			getBorderHeightSlider().setValue(side.getBorderHeight());
			getBorderWidthSlider().setEnabled(true);
			getBorderWidthSlider().setValue(side.getBorderWidth());
			break;
		case "curb":
		default:
			// the game makes any other border style a wall
			getBorderHeightSlider().setEnabled(true);
			getBorderHeightSlider().setValue(side.getBorderHeight());
			getBorderWidthSlider().setEnabled(true);
			getBorderWidthSlider().setValue(side.getBorderWidth());
			break;
		}
		
		// update barrier
		if (isVersion3())
		{
			// no interactive fixes either: the games ignore a segment's barrier
			showVersion3Barrier();
			this.validate();
			this.repaint();
			return;
		}
		surface = this.side.getBarrierSurface();
		addSurface(barrierSurfaceVector, surface);

		if (surface != null)
			this.getBarrierSurfaceComboBox().setSelectedItem(surface);
		else
			this.getBarrierSurfaceComboBox().setSelectedIndex(-1);
		
		this.getBarrierHeightSlider().setValue(side.getBarrierHeight());
		this.getBarrierWidthSlider().setValue(side.getBarrierWidth());

		style = side.getBarrierStyle();
		if (style == null || style.isEmpty())
			style = "none";
		// the list depends on the game type and on the current style
		updatingBarrierStyle = true;
		getBarrierStyleComboBox().setModel(new DefaultComboBoxModel<String>(barrierStyleItems(style)));
		selectStyle(getBarrierStyleComboBox(), style);
		updatingBarrierStyle = false;
		if (parent.editorFrame.getInteractiveFixes())
			fixBarrier(segment, side);
		switch (style)
		{
		case "none":
			getBarrierSurfaceComboBox().setSelectedIndex(-1);
			getBarrierHeightSlider().setValue(side.getBarrierHeight());
			getBarrierHeightSlider().setEnabled(false);
			getBarrierWidthSlider().setValue(side.getBarrierWidth());
			getBarrierWidthSlider().setEnabled(false);
			break;
		case "no barrier":
			getBarrierHeightSlider().setEnabled(true);
			getBarrierHeightSlider().setValueFrozen(side.getBarrierHeight());
			getBarrierWidthSlider().setEnabled(true);
			getBarrierWidthSlider().setValueFrozen(side.getBarrierWidth());
			break;
		case "wall":
			getBarrierHeightSlider().setEnabled(true);
			getBarrierHeightSlider().setValue(side.getBarrierHeight());
			getBarrierWidthSlider().setEnabled(true);
			getBarrierWidthSlider().setValue(side.getBarrierWidth());
			break;
		case "fence":
		case "fence1":
		case "fence2":
			getBarrierHeightSlider().setEnabled(true);
			getBarrierHeightSlider().setValue(side.getBarrierHeight());
			getBarrierWidthSlider().setEnabled(true);
			getBarrierWidthSlider().setValueFrozen(side.getBarrierWidth());
			break;
		case "plan":
		case "curb":
		default:
			// a style the game doesn't know (see fixBarrier), but the values set here
			// are still edited
			getBarrierHeightSlider().setEnabled(true);
			getBarrierHeightSlider().setValue(side.getBarrierHeight());
			getBarrierWidthSlider().setEnabled(true);
			getBarrierWidthSlider().setValue(side.getBarrierWidth());
			break;
		}

		this.validate();
		this.repaint();
	}

	/**
	 * The interactive fix for a segment's border, from what the game makes of it. Only
	 * Speed Dreams and CSR ignore a plan border's height (they use 0): TORCS keeps it for
	 * the curb and wall borders after it, so it isn't wrong there.
	 */
	private void fixBorder(Segment segment, SegmentSide side)
	{
		if (parent.editorFrame.getGameType().planBorderResetsHeight() && "plan".equals(getBorderStyle(segment, side)))
			checkBorderPlan(segment, side);
	}

	/**
	 * @return whether the game knows a segment barrier style: wall and fence, fence1 and
	 *         fence2 only in Speed Dreams, and no barrier only in Speed Dreams
	 */
	private boolean isKnownBarrierStyle(String style)
	{
		EditorFrame.GameType game = parent.editorFrame.getGameType();
		return style.equals("wall") || style.equals("fence") ||
			(game.hasFence1Fence2() && (style.equals("fence1") || style.equals("fence2"))) ||
			(game.hasNoBarrier() && style.equals("no barrier"));
	}

	/**
	 * The interactive fix for a segment's barrier (version 4 and 5 tracks: version 3
	 * tracks only have the Main Track's). A style the game doesn't know is offered to be
	 * changed (TORCS makes it a wall, the others keep the barrier from before); otherwise
	 * the barrier the game uses, set here or from before, is checked for its style.
	 */
	private void fixBarrier(Segment segment, SegmentSide side)
	{
		String own = side.getBarrierStyle();
		if (own != null && !own.isEmpty() && !isKnownBarrierStyle(own))
		{
			checkBarrierInvalid(segment, side);
			return;
		}
		String style = side.isRight() ? segment.getValidRightBarrierStyle(parent.editorFrame) : segment.getValidLeftBarrierStyle(parent.editorFrame);
		if (style.equals("no barrier"))
			checkBarrierNoBarrier(segment, side);
		else if (style.equals("wall"))
			checkBarrierWall(segment, side);
		else if (style.startsWith("fence"))
			checkBarrierFence(segment, side);
	}

	private void checkBorderPlan(Segment segment, SegmentSide side)
	{
		// fix up bad value (height should be 0)
		if (getBorderHeight(segment, side) != 0)
		{
			String[] options = { "Set height to 0.0", "Set style to wall", "Set style to curb", "Ignore" };
			switch (JOptionPane.showOptionDialog(null, "Found " + side.getName() + getBorderStyleText(segment, side) + " with " +
					getBorderHeightText(segment, side) + " " + getBorderWidthText(segment, side) + " " + getBorderSurfaceText(segment, side),
					"Invalid Border Height", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[3]))
			{
			case 0: // set height to 0
				side.setBorderHeight(0);
				break;
			case 1: // set style to wall
				side.setBorderStyle("wall");
				getBorderStyleComboBox().setSelectedItem(side.getBorderStyle());
				break;
			case 2: // set style to curb
				side.setBorderStyle("curb");
				getBorderStyleComboBox().setSelectedItem(side.getBorderStyle());
				break;
			case 3: // ignore
				// nothing
				break;
			}
		}
	}

	private void checkBarrierInvalid(Segment segment, SegmentSide side)
	{
		// fix up bad value (a style the game doesn't know)
		// only Speed Dreams is offered no barrier (see basicBarrierStyleItems)
		boolean withoutNoBarrier = !parent.editorFrame.getGameType().hasNoBarrier();
		String[] options = withoutNoBarrier ?
			new String[] { "Set style to none", "Set style to fence", "Set style to wall", "Ignore" } :
			new String[] { "Set style to none", "Set style to no barrier", "Set style to fence", "Set style to wall", "Ignore" };
		String result = parent.editorFrame.getGameType().otherBarrierStylesAreWalls() ? "the game makes it a wall" : "the game keeps the barrier from before";
		int choice = JOptionPane.showOptionDialog(null, "Found " + side.getName() + getBarrierStyleText(segment, side) + " with" +
				getBarrierWidthText(segment, side) + getBarrierHeightText(segment, side) + getBarrierSurfaceText(segment, side) +
				"\n(" + result + ")",
				"Invalid Barrier Style", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[options.length - 1]);
		// the choices after none are one further on in the full list
		if (withoutNoBarrier && choice >= 1)
			choice++;
		switch (choice)
		{
		case 0: // none
			side.setBarrierStyle(null);
			side.setBarrierSurface(null);
			side.setBarrierHeight(Double.NaN);
			side.setBarrierWidth(Double.NaN);
			getBarrierSurfaceComboBox().setSelectedIndex(-1);
			getBarrierHeightSlider().setValue(side.getBarrierHeight());
			getBarrierHeightSlider().setEnabled(false);
			getBarrierWidthSlider().setValue(side.getBarrierWidth());
			getBarrierWidthSlider().setEnabled(false);
			break;
		case 1: // no barrier
			side.setBarrierStyle("no barrier");
			side.setBarrierSurface(null);
			side.setBarrierHeight(0.0);
			side.setBarrierWidth(0.0);
			getBarrierSurfaceComboBox().setSelectedIndex(-1);
			getBarrierHeightSlider().setEnabled(true);
			getBarrierHeightSlider().setValueFrozen(side.getBarrierHeight());
			getBarrierWidthSlider().setEnabled(true);
			getBarrierWidthSlider().setValueFrozen(side.getBarrierWidth());
			break;
		case 2: // fence
			side.setBarrierStyle("fence");
			// fix up bad value (width should be 0)
			getBarrierStyleComboBox().setSelectedItem(side.getBarrierStyle());
			checkBarrierFence(segment, side);
			getBarrierHeightSlider().setEnabled(true);
			getBarrierHeightSlider().setValue(side.getBarrierHeight());
			getBarrierWidthSlider().setEnabled(true);
			getBarrierWidthSlider().setValueFrozen(side.getBarrierWidth());
			break;
		case 3: // wall
			side.setBarrierStyle("wall");
			getBarrierStyleComboBox().setSelectedItem(side.getBarrierStyle());
			checkBarrierWall(segment, side);
			getBarrierHeightSlider().setEnabled(true);
			getBarrierHeightSlider().setValue(side.getBarrierHeight());
			getBarrierWidthSlider().setEnabled(true);
			getBarrierWidthSlider().setValueFrozen(side.getBarrierWidth());
			break;
		case 4: // ignore
			// nothing
			break;
		}
	}
	
	private void checkBarrierWall(Segment segment, SegmentSide side)
	{
		// fix up bad value (width should not be 0)
		if (getBarrierWidth(segment, side) == 0)
		{
			String[] options1 = { "Set width to " + SegmentSide.DEFAULT_BARRIER_WALL_WIDTH, "Set style to fence", "Ignore" };
			switch (JOptionPane.showOptionDialog(null, "Found " + side.getName() + getBarrierStyleText(segment, side) + " with" +
					getBarrierWidthText(segment, side) + getBarrierHeightText(segment, side) + getBarrierSurfaceText(segment, side),
					"Invalid Barrier Width", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options1, options1[2]))
			{
			case 0: // set width to default
				side.setBarrierWidth(SegmentSide.DEFAULT_BARRIER_WALL_WIDTH);
				break;
			case 1: // set style to fence
				side.setBarrierStyle("fence");
				getBarrierStyleComboBox().setSelectedItem(side.getBarrierStyle());
				break;
			case 2: // ignore
				// nothing
				break;
			}
		}
		if (getBarrierHeight(segment, side) == 0)
		{
			String[] options1 = { "Set height to " + SegmentSide.DEFAULT_BARRIER_WALL_HEIGHT, "Ignore" };
			switch (JOptionPane.showOptionDialog(null, "Found " + side.getName() + getBarrierStyleText(segment, side) + " with" +
					getBarrierHeightText(segment, side) + getBarrierWidthText(segment, side) + getBarrierSurfaceText(segment, side),
					"Invalid Barrier Height", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options1, options1[1]))
			{
			case 0: // set height
				side.setBarrierHeight(SegmentSide.DEFAULT_BARRIER_WALL_HEIGHT);
				break;
			case 1: // ignore
				// nothing
				break;
			}
		}
	}

	private void checkBarrierFence(Segment segment, SegmentSide side)
	{
		// fix up bad value (width should be 0)
		if (getBarrierWidth(segment, side) != 0)
		{
			String[] options = { "Set width to 0.0", "Set style to wall", "Ignore" };
			switch (JOptionPane.showOptionDialog(null, "Found " + side.getName() + getBarrierStyleText(segment, side) + " with" +
					getBarrierWidthText(segment, side) + getBarrierHeightText(segment, side) + getBarrierSurfaceText(segment, side),
					"Invalid Barrier Width", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]))
			{
			case 0: // set width to 0
				side.setBarrierWidth(0);
				break;
			case 1: // set style to wall
				side.setBarrierStyle("wall");
				getBarrierStyleComboBox().setSelectedItem(side.getBarrierStyle());
				break;
			case 2: // ignore
				// nothing
				break;
			}
		}
		// fix up bad value (width should not be 0)
		if (getBarrierHeight(segment, side) == 0)
		{
			// (plan isn't a barrier style: only Speed Dreams has no barrier)
			boolean noBarrier = parent.editorFrame.getGameType().hasNoBarrier();
			String[] options = noBarrier ?
				new String[] { "Set height to " + SegmentSide.DEFAULT_BARRIER_FENCE_HEIGHT, "Set style to no barrier", "Ignore" } :
				new String[] { "Set height to " + SegmentSide.DEFAULT_BARRIER_FENCE_HEIGHT, "Ignore" };
			int choice = JOptionPane.showOptionDialog(null, "Found " + side.getName() + getBarrierStyleText(segment, side) + " with" +
					getBarrierWidthText(segment, side) + getBarrierHeightText(segment, side) + getBarrierSurfaceText(segment, side),
					"Invalid Barrier Height", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[options.length - 1]);
			if (choice == 0)
			{
				side.setBarrierHeight(SegmentSide.DEFAULT_BARRIER_FENCE_HEIGHT);
			}
			else if (noBarrier && choice == 1)
			{
				side.setBarrierStyle("no barrier");
				side.setBarrierHeight(0.0);
				side.setBarrierWidth(0.0);
				getBarrierStyleComboBox().setSelectedItem(side.getBarrierStyle());
			}
		}
	}

	private void checkBarrierNoBarrier(Segment segment, SegmentSide side)
	{
		// fix up bad values (height should be 0)
		if (getBarrierHeight(segment, side) != 0)
		{
			String[] options = { "Set height to 0.0", "Ignore" };
			switch (JOptionPane.showOptionDialog(null, "Found " + side.getName() + getBarrierStyleText(segment, side) + " with" +
					getBarrierHeightText(segment, side) + getBarrierWidthText(segment, side) + getBarrierSurfaceText(segment, side),
					"Invalid Barrier Height", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]))
			{
			case 0: // set height to 0
				side.setBarrierHeight(0);
				break;
			case 1: // ignore
				// nothing
				break;
			}
		}
		// fix up bad values (width should be 0)
		if (getBarrierWidth(segment, side) != 0)
		{
			String[] options = { "Set width to 0.0", "Ignore" };
			switch (JOptionPane.showOptionDialog(null, "Found " + side.getName() + getBarrierStyleText(segment, side) + " with" +
					getBarrierWidthText(segment, side) + getBarrierHeightText(segment, side) + getBarrierSurfaceText(segment, side),
					"Invalid Barrier Width", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]))
			{
			case 0: // set width to 0
				side.setBarrierWidth(0);
				break;
			case 1: // ignore
				// nothing
				break;
			}
		}
	}

	private double getBarrierHeight(Segment segment, SegmentSide side)
	{
		if (!Double.isNaN(side.getBarrierHeight()))
		{
			return side.getBarrierHeight();
		}
		
		return side.isRight() ? segment.getValidRightBarrierHeight(parent.editorFrame) : segment.getValidLeftBarrierHeight(parent.editorFrame);
	}

	private String getBarrierHeightText(Segment segment, SegmentSide side)
	{
		if (!Double.isNaN(side.getBarrierHeight()))
		{
			return " height " + side.getBarrierHeight();
		}
		
		return " inherited height " + getBarrierHeight(segment, side);
	}

	private double getBarrierWidth(Segment segment, SegmentSide side)
	{
		if (!Double.isNaN(side.getBarrierWidth()))
		{
			return side.getBarrierWidth();
		}
		
		return side.isRight() ? segment.getValidRightBarrierWidth(parent.editorFrame) : segment.getValidLeftBarrierWidth(parent.editorFrame);
	}

	private String getBarrierWidthText(Segment segment, SegmentSide side)
	{
		if (!Double.isNaN(side.getBarrierWidth()))
		{
			return " width " + side.getBarrierWidth();
		}
		
		return " inherited width " + getBarrierWidth(segment, side);
	}

	private String getBarrierSurface(Segment segment, SegmentSide side)
	{
		if (side.getBarrierSurface() != null && !side.getBarrierSurface().isEmpty())
		{
			return side.getBarrierSurface();
		}
		
		return side.isRight() ? segment.getValidRightBarrierSurface(parent.editorFrame) : segment.getValidLeftBarrierSurface(parent.editorFrame);
	}

	private String getBarrierSurfaceText(Segment segment, SegmentSide side)
	{
		if (side.getBarrierSurface() != null && !side.getBarrierSurface().isEmpty())
		{
			return " surface \"" + side.getBarrierSurface() + "\"";
		}
		
		return " inherited surface \"" + getBarrierSurface(segment, side) + "\"";
	}

	private String getBarrierStyle(Segment segment, SegmentSide side)
	{
		if (side.getBarrierStyle() != null && !side.getBarrierStyle().isEmpty())
		{
			return side.getBarrierStyle();
		}

		return side.isRight() ? segment.getValidRightBarrierStyle(parent.editorFrame) : segment.getValidLeftBarrierStyle(parent.editorFrame);
	}

	private String getBarrierStyleText(Segment segment, SegmentSide side)
	{
		if (side.getBarrierStyle() != null && !side.getBarrierStyle().isEmpty())
		{
			return " barrier style \"" + side.getBarrierStyle() + "\"";
		}

		return " inherited barrier style \"" + getBarrierStyle(segment, side) + "\"";
	}

	private double getBorderHeight(Segment segment, SegmentSide side)
	{
		if (!Double.isNaN(side.getBorderHeight()))
		{
			return side.getBorderHeight();
		}
		
		return side.isRight() ? segment.getValidRightBorderHeight(parent.editorFrame) : segment.getValidLeftBorderHeight(parent.editorFrame);
	}

	private String getBorderHeightText(Segment segment, SegmentSide side)
	{
		if (!Double.isNaN(side.getBorderHeight()))
		{
			return " height " + side.getBorderHeight();
		}
		
		return " inherited height " + getBorderHeight(segment, side);
	}

	private double getBorderWidth(Segment segment, SegmentSide side)
	{
		if (!Double.isNaN(side.getBorderWidth()))
		{
			return side.getBorderWidth();
		}
		
		return side.isRight() ? segment.getValidRightBorderWidth(parent.editorFrame) : segment.getValidLeftBorderWidth(parent.editorFrame);
	}

	private String getBorderWidthText(Segment segment, SegmentSide side)
	{
		if (!Double.isNaN(side.getBorderWidth()))
		{
			return " width " + side.getBorderWidth();
		}
		
		return " inherited width " + getBorderWidth(segment, side);
	}

	private String getBorderSurface(Segment segment, SegmentSide side)
	{
		if (side.getBorderSurface() != null && !side.getBorderSurface().isEmpty())
		{
			return side.getBorderSurface();
		}
		
		return side.isRight() ? segment.getValidRightBorderSurface(parent.editorFrame) : segment.getValidLeftBorderSurface(parent.editorFrame);
	}

	private String getBorderSurfaceText(Segment segment, SegmentSide side)
	{
		if (side.getBorderSurface() != null && !side.getBorderSurface().isEmpty())
		{
			return " surface \"" + side.getBorderSurface() + "\"";
		}
		
		return " inherited surface \"" + getBorderSurface(segment, side) + "\"";
	}

	private String getBorderStyle(Segment segment, SegmentSide side)
	{
		if (side.getBorderStyle() != null && !side.getBorderStyle().isEmpty())
		{
			return side.getBorderStyle();
		}

		return side.isRight() ? segment.getValidRightBorderStyle(parent.editorFrame) : segment.getValidLeftBorderStyle(parent.editorFrame);
	}

	private String getBorderStyleText(Segment segment, SegmentSide side)
	{
		if (side.getBorderStyle() != null && !side.getBorderStyle().isEmpty())
		{
			return " border style \"" + side.getBorderStyle() + "\"";
		}

		return " inherited border style \"" + getBorderStyle(segment, side) + "\"";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gui.segment.SliderListener#valueChanged(gui.segment.SegmentSlider)
	 */
	public void sliderChanged(SegmentSlider slider)
	{
		Interpreter line = new Interpreter();
		String command = "";

		try
		{
			line.set("side", side);

			String method = slider.getMethod();

			if (Double.isNaN(slider.getValue()))
				command = "side.set" + method + "(Double.NaN)";
			else
				command = "side.set" + method + "(" + slider.getValue() + ")";

			line.eval(command);
			side = (SegmentSide) line.get("side");
		} catch (EvalError e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parent.update();
	}

	public void checkBoxChanged(SegmentSlider slider)
	{
	}	
} //  @jve:decl-index=0:visual-constraint="42,-30"
