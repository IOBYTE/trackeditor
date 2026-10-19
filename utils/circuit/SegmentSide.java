/*
 *   SegmentSide.java
 *   Created on 24 ??? 2005
 *
 *    The SegmentSide.java is part of TrackEditor-0.6.0.
 *
 *    TrackEditor-0.6.0 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.6.0 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.6.0; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package utils.circuit;

import java.awt.event.ActionEvent;
import java.util.Vector;

/**
 * @author Charalampos Alexopoulos
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SegmentSide implements Cloneable
{
	public static final double	DEFAULT_SIDE_START_WIDTH			= 4.0;
	public static final double	DEFAULT_SIDE_END_WIDTH				= 4.0;
	public static final String	DEFAULT_SIDE_SURFACE				= "grass";
	public static final String	DEFAULT_SIDE_BANKING_TYPE			= null;

	public static final double	DEFAULT_BARRIER_FENCE_HEIGHT		= 2.0;
	public static final double	DEFAULT_BARRIER_WALL_HEIGHT			= 1.0;
	public static final double	DEFAULT_BARRIER_FENCE_WIDTH			= 0.0;
	public static final double	DEFAULT_BARRIER_WALL_WIDTH			= 0.25;
	public static final String	DEFAULT_BARRIER_FENCE_SURFACE		= "barrier-grille";
	public static final String	DEFAULT_BARRIER_WALL_SURFACE		= "barrier";
	public static final String	DEFAULT_BARRIER_STYLE				= "wall";

	public static final double	DEFAULT_BORDER_PLAN_WIDTH			= 0.5;
	public static final double	DEFAULT_BORDER_CURB_WIDTH			= 0.5;
	public static final double	DEFAULT_BORDER_WALL_WIDTH			= 0.25;
	public static final double	DEFAULT_BORDER_PLAN_HEIGHT			= 0.0;
	public static final double	DEFAULT_BORDER_CURB_HEIGHT			= 0.05;
	public static final double	DEFAULT_BORDER_WALL_HEIGHT			= 1.0;
	public static final String	DEFAULT_BORDER_PLAN_LEFT_SURFACE	= "curb-l";
	public static final String	DEFAULT_BORDER_PLAN_RIGHT_SURFACE	= "curb-r";
	public static final String	DEFAULT_BORDER_CURB_LEFT_SURFACE	= "curb-5cm-l";
	public static final String	DEFAULT_BORDER_CURB_RIGHT_SURFACE	= "curb-5cm-r";
	public static final String	DEFAULT_BORDER_WALL_SURFACE			= "concrete";
	public static final String	DEFAULT_BORDER_STYLE				= "curb";
	
	public static final String	DEFAULT_PIT_ENTRY_SURFACE			= "asphalt";
	public static final String	DEFAULT_PIT_ENTRY_BORDER_STYLE		= null;
	public static final String	DEFAULT_PIT_ENTRY_BORDER_SURFACE	= null;
	public static final double	DEFAULT_PIT_ENTRY_BORDER_HEIGHT		= 0.0;
	public static final double	DEFAULT_PIT_ENTRY_BORDER_WIDTH		= 0.0;
	
	public static final String	DEFAULT_PIT_PITS_SURFACE			= "asphalt-pits";
	public static final double	DEFAULT_PIT_PITS_BORDER_WIDTH		= 0.5;
	public static final double	DEFAULT_PIT_PITS_BORDER_HEIGHT		= 1.0;
	public static final String	DEFAULT_PIT_PITS_BORDER_SURFACE		= "wall";
	public static final String	DEFAULT_PIT_PITS_BORDER_STYLE		= "wall";
	
	public static final String	DEFAULT_PIT_EXIT_SURFACE			= "asphalt";
	public static final String	DEFAULT_PIT_EXIT_BORDER_STYLE		= null;
	public static final String	DEFAULT_PIT_EXIT_BORDER_SURFACE		= null;
	public static final double	DEFAULT_PIT_EXIT_BORDER_HEIGHT		= 0.0;
	public static final double	DEFAULT_PIT_EXIT_BORDER_WIDTH		= 0.0;

	private Vector<SegmentSideListener>	sideListeners		= new Vector<SegmentSideListener>();
	private SegmentSide			prev						= null;
	private SegmentSide			props						= null;

	//	Side
	private boolean				hasSide						= false;
	protected double			sideStartWidth				= Double.NaN;
	private boolean				prevSideStartWidthChanged	= false;
	private boolean				thisSideStartWidthChanged	= false;
	protected double			sideEndWidth				= Double.NaN;
	private boolean				prevSideEndWidthChanged		= false;
	private boolean				thisSideEndWidthChanged		= false;
	protected String			sideSurface					= null;
	private boolean				prevSideSurfaceChanged		= false;
	private boolean				thisSideSurfaceChanged		= false;
	protected String			sideBankingType				= null;
	private boolean				prevSideBankingTypeChanged	= false;
	private boolean				thisSideBankingTypeChanged	= false;

	//	Barrier
	private boolean				hasBarrier					= false;
	protected double			barrierHeight				= Double.NaN;
	private boolean				prevBarrierHeightChanged	= false;
	private boolean				thisBarrierHeightChanged	= false;
	protected double			barrierWidth				= Double.NaN;
	private boolean				prevBarrierWidthChanged		= false;
	private boolean				thisBarrierWidthChanged		= false;
	protected String			barrierSurface				= null;
	private boolean				prevBarrierSurfaceChanged	= false;
	private boolean				thisBarrierSurfaceChanged	= false;
	protected String			barrierStyle				= null;
	private boolean				prevBarrierStyleChanged		= false;
	private boolean				thisBarrierStyleChanged		= false;

	// Border
	private boolean				hasBorder					= false;
	protected double			borderWidth					= Double.NaN;
	private boolean				prevBorderWidthChanged		= false;
	private boolean				thisBorderWidthChanged		= false;
	protected double			borderHeight				= Double.NaN;
	private boolean				prevBorderHeightChanged		= false;
	private boolean				thisBorderHeightChanged		= false;
	protected String			borderSurface				= null;
	private boolean				prevBorderSurfaceChanged	= false;
	private boolean				thisBorderSurfaceChanged	= false;
	protected String			borderStyle					= null;
	private boolean				prevBorderStyleChanged		= false;
	private boolean				thisBorderStyleChanged		= false;

	private boolean				isRight;
	/**
	 *  
	 */
	public SegmentSide(boolean isRight)
	{
		this.isRight = isRight;
	}

	public void set(SegmentSide side)
	{
		barrierHeight = side.barrierHeight;
		barrierStyle = side.barrierStyle;
		barrierSurface = side.barrierSurface;
		barrierWidth = side.barrierWidth;
		borderHeight = side.borderHeight;
		borderStyle = side.borderStyle;
		borderSurface = side.borderSurface;
		borderWidth = side.borderWidth;
		sideBankingType = side.sideBankingType;
		sideEndWidth = side.sideEndWidth;
		sideStartWidth = side.sideStartWidth;
		sideSurface = side.sideSurface;
//		s.sideListeners = (Vector<SegmentSideListener>) this.sideListeners.clone();
		hasSide = side.hasSide;
		hasBorder = side.hasBorder;
		hasBarrier = side.hasBarrier;
	}

	/**
	 */
	public void setNewTrackDefaults()
	{
		setSideStartWidth(DEFAULT_SIDE_START_WIDTH);
		setSideEndWidth(DEFAULT_SIDE_END_WIDTH);
		setSideSurface(DEFAULT_SIDE_SURFACE);

		setBarrierHeight(DEFAULT_BARRIER_WALL_HEIGHT);
		setBarrierWidth(DEFAULT_BARRIER_WALL_WIDTH);
		setBarrierSurface(DEFAULT_BARRIER_WALL_SURFACE);
		setBarrierStyle(DEFAULT_BARRIER_STYLE);

		setBorderWidth(DEFAULT_BORDER_CURB_WIDTH);
		setBorderHeight(DEFAULT_BORDER_CURB_HEIGHT);
		if (isRight)
		{
			setBorderSurface(DEFAULT_BORDER_CURB_RIGHT_SURFACE);
		}
		else
		{
			setBorderSurface(DEFAULT_BORDER_CURB_LEFT_SURFACE);
		}
		setBorderStyle(DEFAULT_BORDER_STYLE);
	}

	public boolean isRight()
	{
		return isRight;
	}

	public String getName()
	{
		if (isRight)
		{
			return new String("right");
		}

		return new String("left");
	}

	/**
	 * @return Returns the hasBarrier.
	 */
	public boolean getHasBarrier()
	{
		return hasBarrier;
	}

	private void checkHasBarrier()
	{
		hasBarrier = !Double.isNaN(barrierHeight) ||
				     !Double.isNaN(barrierWidth) ||
				     (barrierSurface != null && !barrierSurface.isEmpty()) ||
				     (barrierStyle != null && !barrierStyle.isEmpty());
	}

	/**
	 * @return Returns the barrierHeight.
	 */
	public double getBarrierHeight()
	{
		return barrierHeight;
	}
	/**
	 * @param barrierHeight
	 *            The barrierHeight to set.
	 */
	public void setBarrierHeight(double barrierHeight)
	{
		this.barrierHeight = barrierHeight;
		barrierHeightChanged();
		checkHasBarrier();
	}
	/**
	 *  
	 */
	private void barrierHeightChanged()
	{
		if (sideListeners != null)
		{
			Vector<SegmentSideListener> listeners = sideListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).barrierHeightChanged();
			}
		}
	}

	/**
	 * @return Returns the barrierStyle.
	 */
	public String getBarrierStyle()
	{
		return barrierStyle;
	}
	/**
	 * @param barrierStyle
	 *            The barrierStyle to set.
	 */
	public void setBarrierStyle(String barrierStyle)
	{
		this.barrierStyle = barrierStyle;
		barrierStyleChanged();
		checkHasBarrier();
	}
	/**
	 *  
	 */
	private void barrierStyleChanged()
	{
		if (sideListeners != null)
		{
			Vector<SegmentSideListener> listeners = sideListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).barrierStyleChanged();
			}
		}
	}

	/**
	 * @return Returns the barrierSurface.
	 */
	public String getBarrierSurface()
	{
		return barrierSurface;
	}
	/**
	 * @param barrierSurface
	 *            The barrierSurface to set.
	 */
	public void setBarrierSurface(String barrierSurface)
	{
		this.barrierSurface = barrierSurface;
		barrierSurfaceChanged();
		checkHasBarrier();
	}
	/**
	 *  
	 */
	private void barrierSurfaceChanged()
	{
		if (sideListeners != null)
		{
			Vector<SegmentSideListener> listeners = sideListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).barrierSurfaceChanged();
			}
		}
	}

	/**
	 * @return Returns the barrierWidth.
	 */
	public double getBarrierWidth()
	{
		return barrierWidth;
	}
	/**
	 * @param barrierWidth
	 *            The barrierWidth to set.
	 */
	public void setBarrierWidth(double barrierWidth)
	{
		this.barrierWidth = barrierWidth;
		barrierWidthChanged();
		checkHasBarrier();
	}
	/**
	 *  
	 */
	private void barrierWidthChanged()
	{
		if (sideListeners != null)
		{
			Vector<SegmentSideListener> listeners = sideListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).barrierWidthChanged();
			}
		}
	}

	/**
	 * @return Returns the hasBorder.
	 */
	public boolean getHasBorder()
	{
		return hasBorder;
	}

	private void checkHasBorder()
	{
		hasBorder = !Double.isNaN(borderHeight) ||
				    !Double.isNaN(borderWidth) ||
				    (borderSurface != null && !borderSurface.isEmpty()) ||
				    (borderStyle != null && !borderStyle.isEmpty());
	}

	/**
	 * @return Returns the borderHeight.
	 */
	public double getBorderHeight()
	{
		return borderHeight;
	}
	/**
	 * @param borderHeight
	 *            The borderHeight to set.
	 */
	public void setBorderHeight(double borderHeight)
	{
		this.borderHeight = borderHeight;
		borderHeightChanged();
		checkHasBorder();
	}
	/**
	 *  
	 */
	private void borderHeightChanged()
	{
		if (sideListeners != null)
		{
			Vector<SegmentSideListener> listeners = sideListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).borderHeightChanged();
			}
		}
	}

	/**
	 * @return Returns the borderStyle.
	 */
	public String getBorderStyle()
	{
		return borderStyle;
	}
	/**
	 * @param borderStyle
	 *            The borderStyle to set.
	 */
	public void setBorderStyle(String borderStyle)
	{
		this.borderStyle = borderStyle;
		borderStyleChanged();
		checkHasBorder();
	}
	/**
	 *  
	 */
	private void borderStyleChanged()
	{
		if (sideListeners != null)
		{
			Vector<SegmentSideListener> listeners = sideListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).borderStyleChanged();
			}
		}
	}

	/**
	 * @return Returns the borderSurface.
	 */
	public String getBorderSurface()
	{
		return borderSurface;
	}
	/**
	 * @param borderSurface
	 *            The borderSurface to set.
	 */
	public void setBorderSurface(String borderSurface)
	{
		this.borderSurface = borderSurface;
		borderSurfaceChanged();
		checkHasBorder();
	}
	/**
	 *  
	 */
	private void borderSurfaceChanged()
	{
		if (sideListeners != null)
		{
			Vector<SegmentSideListener> listeners = sideListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).borderSurfaceChanged();
			}
		}
	}

	/**
	 * @return Returns the borderWidth.
	 */
	public double getBorderWidth()
	{
		return borderWidth;
	}
	/**
	 * @param borderWidth
	 *            The borderWidth to set.
	 */
	public void setBorderWidth(double borderWidth)
	{
		this.borderWidth = borderWidth;
		borderWidthChanged();
		checkHasBorder();
	}
	/**
	 *  
	 */
	private void borderWidthChanged()
	{
		if (sideListeners != null)
		{
			Vector<SegmentSideListener> listeners = sideListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).borderWidthChanged();
			}
		}
	}

	/**
	 * @return Returns the hasSide.
	 */
	public boolean getHasSide()
	{
		return hasSide;
	}

	private void checkHasSide()
	{
		hasSide = !Double.isNaN(sideStartWidth) ||
				  !Double.isNaN(sideEndWidth) ||
				  (sideSurface != null && !sideSurface.isEmpty()) ||
				  (sideBankingType != null && !sideBankingType.isEmpty());
	}

	/**
	 * @return Returns the sideEndWidth.
	 */
	public double getSideEndWidth()
	{
		return sideEndWidth;
	}
	/**
	 * @param sideEndWidth
	 *            The sideEndWidth to set.
	 */
	public void setSideEndWidth(double sideEndWidth)
	{
		this.sideEndWidth = sideEndWidth;
		sideEndWidthChanged();
		checkHasSide();
	}
	/**
	 *  
	 */
	private void sideEndWidthChanged()
	{
		if (sideListeners != null)
		{
			Vector<SegmentSideListener> listeners = sideListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).sideEndWidthChanged();
			}
		}
	}

	/**
	 * @return Returns the sideStartWidth.
	 */
	public double getSideStartWidth()
	{
		return sideStartWidth;
	}
	/**
	 * @param sideStartWidth
	 *            The sideStartWidth to set.
	 */
	public void setSideStartWidth(double sideStartWidth)
	{
		this.sideStartWidth = sideStartWidth;
		sideStartWidthChanged();
		checkHasSide();
	}
	/**
	 *  
	 */
	private void sideStartWidthChanged()
	{
		if (sideListeners != null)
		{
			Vector<SegmentSideListener> listeners = sideListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).sideStartWidthChanged();
			}
		}
	}

	/**
	 * @return Returns the sideSurface.
	 */
	public String getSideSurface()
	{
		return sideSurface;
	}
	/**
	 * @param sideSurface
	 *            The sideSurface to set.
	 */
	public void setSideSurface(String sideSurface)
	{
		this.sideSurface = sideSurface;
		sideSurfaceChanged();
		checkHasSide();
	}

	/**
	 *  
	 */
	private void sideSurfaceChanged()
	{
		if (sideListeners != null)
		{
			Vector<SegmentSideListener> listeners = sideListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).sideSurfaceChanged();
			}
		}
	}

	/**
	 * @return Returns the sideBankingType.
	 */
	public String getSideBankingType()
	{
		return sideBankingType;
	}
	/**
	 * @param sideBankingType
	 *            The sideBankingType to set.
	 */
	public void setSideBankingType(String sideBankingType)
	{
		this.sideBankingType = sideBankingType;
		sideBankingTypeChanged();
		checkHasSide();
	}

	/**
	 *
	 */
	private void sideBankingTypeChanged()
	{
		if (sideListeners != null)
		{
			Vector<SegmentSideListener> listeners = sideListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).sideBankingTypeChanged();
			}
		}
	}

	public synchronized void removeSideListener(SegmentSideListener l)
	{

	}

	public synchronized void addSideListener(SegmentSideListener l)
	{
		Vector<SegmentSideListener> v = sideListeners == null ? new Vector<SegmentSideListener>(2) : (Vector<SegmentSideListener>) sideListeners.clone();
		if (!v.contains(l))
		{
			v.addElement(l);
			sideListeners = v;
		}
	}

	public void valueChanged()
	{
		if (sideListeners != null)
		{
			Vector<SegmentSideListener> listeners = sideListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).actionPerformed(null);
			}
		}
	}
	/**
	 * @return Returns the prev.
	 */
	public SegmentSide getPrev()
	{
		return prev;
	}
	/**
	 * @param prev
	 *            The prev to set.
	 */
	public void setPrevius(SegmentSide previus)
	{
		this.prev = previus;

		/**
		 * TODO : I have to wrie some kind of interaction when previus change,
		 * but for the moment i can figure out what. eg. If the side end of
		 * previus segment change then the side start of this segment must
		 * change, except in the case that user have already change the side
		 * start.
		 */
		//		prev.addSideListener(new SegmentSideListener()
		//				{
		//			public void barrierHeightChanged()
		//			{
		//				if(prev != null && !thisBarrierHeightChanged)
		//				{
		//					barrierHeight = prev.barrierHeight;
		//					thisBarrierHeightChanged = true;
		//				}
		//			}
		//
		//			public void barrierStyleChanged()
		//			{
		//				if(prev != null && !thisBarrierStyleChanged)
		//				{
		//					barrierStyle = prev.barrierStyle;
		//					thisBarrierStyleChanged = true;
		//				}
		//			}
		//
		//			public void barrierSurfaceChanged()
		//			{
		//				if(prev != null && !thisBarrierSurfaceChanged)
		//				{
		//					barrierSurface = prev.barrierSurface;
		//					thisBarrierSurfaceChanged = true;
		//				}
		//			}
		//
		//			public void barrierWidthChanged()
		//			{
		//				if(prev != null && !thisBarrierWidthChanged)
		//				{
		//					barrierWidth = prev.barrierWidth;
		//					thisBarrierWidthChanged = true;
		//				}
		//			}
		//
		//			public void borderHeightChanged()
		//			{
		//				if(prev != null && !thisBorderHeightChanged)
		//				{
		//					borderHeight = prev.borderHeight;
		//				}
		//			}
		//
		//			public void borderStyleChanged()
		//			{
		//				if(prev != null && !thisBorderStyleChanged)
		//				{
		//					borderStyle = prev.borderStyle;
		//				}
		//			}
		//
		//			public void borderSurfaceChanged()
		//			{
		//				if(prev != null && !thisBorderSurfaceChanged)
		//				{
		//					borderSurface = prev.borderSurface;
		//				}
		//			}
		//
		//			public void borderWidthChanged()
		//			{
		//				if(prev != null && !thisBorderWidthChanged)
		//				{
		//					borderWidth = prev.borderWidth;
		//				}
		//			}
		//
		//			public void sideEndWidthChanged()
		//			{
		//				if(prev != null && !thisSideStartWidthChanged)
		//				{
		//					sideStartWidth = prev.sideEndWidth;
		//					prevSideEndWidthChanged = true;
		//				}
		//			}
		//
		//			public void sideStartWidthChanged()
		//			{
		//				// Do nothing when previus side start width
		//				// change.
		//			}
		//
		//			public void sideSurfaceChanged()
		//			{
		//				if(prev != null && !thisSideSurfaceChanged)
		//				{
		//					sideSurface = prev.sideSurface;
		//				}
		//			}
		//
		//			public void actionPerformed(ActionEvent arg0)
		//			{
		//				
		//			}
		//				});
	}
	/**
	 * @return Returns the props.
	 */
	public SegmentSide getProps()
	{
		return props;
	}
	/**
	 * @param props
	 *            The props to set.
	 */
	public void setProperties(SegmentSide properties)
	{
		this.props = properties;
		props.addSideListener(new SegmentSideListener()
		{

			public void barrierHeightChanged()
			{
				if (!prevBarrierHeightChanged && !thisBarrierHeightChanged)
				{
					barrierHeight = props.barrierHeight;
				}
			}

			public void barrierStyleChanged()
			{
				if (!prevBarrierStyleChanged && !thisBarrierStyleChanged)
				{
					barrierStyle = props.barrierStyle;
				}
			}

			public void barrierSurfaceChanged()
			{
				if (!prevBarrierSurfaceChanged && !thisBarrierSurfaceChanged)
				{
					barrierSurface = props.barrierSurface;
				}
			}

			public void barrierWidthChanged()
			{
				if (!prevBarrierWidthChanged && !thisBarrierWidthChanged)
				{
					barrierWidth = props.barrierWidth;
				}
			}

			public void borderHeightChanged()
			{
				if (!prevBorderHeightChanged && !thisBorderHeightChanged)
				{
					borderHeight = props.borderHeight;
				}
			}

			public void borderStyleChanged()
			{
				if (!prevBorderStyleChanged && !thisBorderStyleChanged)
				{
					borderStyle = props.borderStyle;
				}
			}

			public void borderSurfaceChanged()
			{
				if (!prevBorderSurfaceChanged && !thisBorderSurfaceChanged)
				{
					borderSurface = props.borderSurface;
				}
			}

			public void borderWidthChanged()
			{
				if (!prevBorderWidthChanged && !thisBorderWidthChanged)
				{
					borderWidth = props.borderWidth;
				}
			}

			public void sideEndWidthChanged()
			{
				if (!prevSideEndWidthChanged && !thisSideEndWidthChanged)
				{
					sideEndWidth = props.sideEndWidth;
				}
			}

			public void sideStartWidthChanged()
			{
				if (!prevSideStartWidthChanged && !thisSideStartWidthChanged)
				{
					sideStartWidth = props.sideStartWidth;
				}
			}

			public void sideSurfaceChanged()
			{
				if (!prevSideSurfaceChanged && !thisSideSurfaceChanged)
				{
					sideSurface = props.sideSurface;
				}
			}

			public void sideBankingTypeChanged()
			{
				if (!prevSideBankingTypeChanged && !thisSideBankingTypeChanged)
				{
					sideBankingType = props.sideBankingType;
				}
			}

			public void actionPerformed(ActionEvent arg0)
			{

			}

		});
	}
	
	protected Object clone()
	{
		SegmentSide s = null;
		try
		{
			s = (SegmentSide) super.clone();
			s.barrierHeight = this.barrierHeight;
			s.barrierStyle = this.barrierStyle;
			s.barrierSurface = this.barrierSurface;
			s.barrierWidth = this.barrierWidth;
			s.borderHeight = this.borderHeight;
			s.borderStyle = this.borderStyle;
			s.borderSurface = this.borderSurface;
			s.borderWidth = this.borderWidth;
			s.sideBankingType = this.sideBankingType;
			s.sideEndWidth = this.sideEndWidth;
			s.sideStartWidth = this.sideStartWidth;
			s.sideSurface = this.sideSurface;
			s.sideListeners = (Vector<SegmentSideListener>) this.sideListeners.clone();
			s.hasSide = this.hasSide;
			s.hasBorder = this.hasBorder;
			s.hasBarrier = this.hasBarrier;
			s.isRight = this.isRight;
			
		} catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s; // return the clone
	}
}
