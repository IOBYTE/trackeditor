/*
 *   Segment.java
 *   Created on 9 ��� 2005
 *
 *    The Segment.java is part of TrackEditor-0.6.0.
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

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import utils.Editor;

/**
 * @author Patrice Espie , Charalampos Alexopoulos
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

public class Segment implements Cloneable
{
	//protected Properties	properties			= Properties.getInstance();
	private Vector			segmentListeners	= new Vector();

	// neighbours
	public Segment			previousShape;
	public Segment			nextShape;

	protected String		name				= "";

	protected SegmentSide	left				= new SegmentSide();
	protected SegmentSide	right				= new SegmentSide();
	//	 type
	protected String		type;
	protected int			count;
	public double			startTrackAlpha;
	public double			endTrackAlpha;
	public Point2D.Double	startTrackCenter	= new Point2D.Double();
	public Point2D.Double	endTrackCenter		= new Point2D.Double();
	public double			distFromCircuitStart;

	// All datas
	protected double		profilStepLength;
	protected double		length;
	protected String		surface;

	protected double		heightStart;
	protected double		heightEnd;

	protected double		heightStartLeft;
	protected double		heightStartRight;
	protected double		heightEndLeft;
	protected double		heightEndRight;

	protected double		grade;
	protected double		bankingStart;
	protected double		bankingEnd;

	protected String		profil;
	protected double		profilSteps;
	protected double		profilStepsLength;
	protected double		profilStartTangent;
	protected double		profilEndTangent;
	protected double		profilStartTangentLeft;
	protected double		profilEndTangentLeft;
	protected double		profilStartTangentRight;
	protected double		profilEndTangentRight;

	// shape to be drawn
	public Point2D.Double	points[];
	public Point2D.Double	trPoints[];

	// datas for fast 'draw' process
	int						xToDraw[]			= new int[4];
	int						yToDraw[]			= new int[4];
	
	double dx;
	double dy;

	public Segment()
	{
		this(null);
	}

	public Segment(String type)
	{
		this.type = type;
		setProperties();
	}

	/**
	 * @return Returns the left.
	 */
	public SegmentSide getLeft()
	{
		return left;
	}
	/**
	 * @param left
	 *            The left to set.
	 */
	public void setLeft(SegmentSide left)
	{
		this.left = left;
	}
	/**
	 * @return Returns the right.
	 */
	public SegmentSide getRight()
	{
		return right;
	}
	/**
	 * @param right
	 *            The right to set.
	 */
	public void setRight(SegmentSide right)
	{
		this.right = right;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		int i = -1;
		try
		{
			i = Integer.parseInt(name);
		} catch (NumberFormatException e)
		{

		}

		if (name == null || name.equals("") || i > 0)
		{
			name = String.valueOf(count);
		}
		return name;
	}
	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	/**
	 * @return Returns the surface.
	 */
	public String getSurface()
	{
		String tmp = Editor.getProperties().getSurface();
		if (surface != null)
		{
			tmp = surface;
		}
		return tmp;
	}
	/**
	 * @param surface
	 *            The surface to set.
	 */
	public void setSurface(String surface)
	{
		this.surface = surface;
	}
	public Segment copyTo(Segment shape) throws CloneNotSupportedException
	{
		shape.points = (Point2D.Double[]) points.clone();
		shape.trPoints = (Point2D.Double[]) trPoints.clone();
		shape.type = new String(type);
		shape.xToDraw = (int[]) xToDraw.clone();
		shape.yToDraw = (int[]) yToDraw.clone();

		return (shape);
	}

	public boolean contains(double x, double y)
	{
		for (int i = 0; i < points.length; i += 4)
		{
			boolean found = true;

			for (int j = 0; j < 4; j++)
			{
				int idxA = i + j;
				int idxB = i + (j + 1) % 4;
				int idxC = i + (j + 2) % 4;
				int idxD = i + (j + 3) % 4;

				if (points[idxA].equals(points[idxB]) || points[idxB].equals(points[idxC])
						|| points[idxC].equals(points[idxD]) || points[idxD].equals(points[idxA]))
				{
					found = false;
					break;
				}

				if (points[idxA].x == points[idxB].x)
				{
					// vertical line
					int localSign = (x > points[idxA].x ? 1 : -1);
					int localSign2 = (points[idxC].x > points[idxA].x ? 1 : -1);

					if (localSign != localSign2)
					{
						found = false;
						break;
					}
				} else
				{
					double a, b;

					a = (points[idxB].y - points[idxA].y) / (points[idxB].x - points[idxA].x);
					b = points[idxA].y - a * points[idxA].x;

					int localSign = (y > a * x + b ? 1 : -1);
					int localSign2 = (points[idxC].y > a * points[idxC].x + b ? 1 : -1);

					if (localSign != localSign2)
					{
						found = false;
						break;
					}
				}
			}

			if (found)
				return (true);
		}

		return (false);
	}

	public Rectangle2D.Double getBounds()
	{
		if (points == null || points.length == 0)
			return (new Rectangle2D.Double(0, 0, 0, 0));

		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;

		for (int i = 0; i < points.length; i++)
		{
			if (minX > points[i].x)
				minX = points[i].x;
			if (maxX < points[i].x)
				maxX = points[i].x;
			if (minY > points[i].y)
				minY = points[i].y;
			if (maxY < points[i].y)
				maxY = points[i].y;
		}

		return (new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY));
	}

	public void calcShape(Segment previousShape) throws Exception
	{
		System.out.println("Segment.calcShape : Start ...");
	}

	public void draw(Graphics g, AffineTransform affineTransform)
	{
		if (points == null || points.length < 4)
			return;
		affineTransform.transform(points, 0, trPoints, 0, points.length);

		Rectangle clipBound = g.getClipBounds();
		int minX, maxX, minY, maxY;

		for (int i = 0; i < points.length; i += 4)
		{
			// clip calcul
			minX = Integer.MAX_VALUE;
			maxX = Integer.MIN_VALUE;
			minY = Integer.MAX_VALUE;
			maxY = Integer.MIN_VALUE;

			for (int j = 0; j < 4; j++)
			{
				if (minX > trPoints[i + j].x)
					minX = (int) (trPoints[i + j].x);

				if (maxX < trPoints[i + j].x)
					maxX = (int) (trPoints[i + j].x);

				if (minY > trPoints[i + j].y)
					minY = (int) (trPoints[i + j].y);

				if (maxY < trPoints[i + j].y)
					maxY = (int) (trPoints[i + j].y);
			}

			if (clipBound.intersects(minX, minY, maxX - minX, maxY - minY))
			{
				for (int j = 0; j < 4; j++)
				{
					xToDraw[j] = (int) (trPoints[i + j].x);
					yToDraw[j] = (int) (trPoints[i + j].y);
				}
				g.drawPolygon(xToDraw, yToDraw, 4);
			}
		}
	}

	public void drag(Point2D.Double dragDelta)
	{
	}
	/**
	 * @return Returns the heightEnd.
	 */
	public double getHeightEnd()
	{
		return heightEnd;
	}
	/**
	 * @param heightEnd
	 *            The heightEnd to set.
	 */
	public void setHeightEnd(double heightEnd)
	{
		this.heightEnd = heightEnd;
	}
	/**
	 * @return Returns the heightStart.
	 */
	public double getHeightStart()
	{
		return heightStart;
	}
	/**
	 * @param heightStart
	 *            The heightStart to set.
	 */
	public void setHeightStart(double heightStart)
	{
		this.heightStart = heightStart;
	}

	/**
	 * @return Returns the heightStartLeft.
	 */
	public double getHeightStartLeft()
	{
		return heightStartLeft;
	}
	/**
	 * @param heightStartLeft
	 *            The heightStartLeft to set.
	 */
	public void setHeightStartLeft(double heightStartLeft)
	{
		this.heightStartLeft = heightStartLeft;
	}
	/**
	 * @return Returns the heightStartRight.
	 */
	public double getHeightStartRight()
	{
		return heightStartRight;
	}
	/**
	 * @param heightStart
	 *            The heightStartRight to set.
	 */
	public void setHeightStartRight(double heightStartRight)
	{
		this.heightStartRight = heightStartRight;
	}
	/**
	 * @return Returns the heightEndLeft.
	 */
	public double getHeightEndLeft()
	{
		return heightEndLeft;
	}
	/**
	 * @param heightEndLeft
	 *            The heightEndLeft to set.
	 */
	public void setHeightEndLeft(double heightEndLeft)
	{
		this.heightEndLeft = heightEndLeft;
	}
	/**
	 * @return Returns the heightEndRight.
	 */
	public double getHeightEndRight()
	{
		return heightEndRight;
	}
	/**
	 * @param heightEndRight
	 *            The heightEndRight to set.
	 */
	public void setHeightEndRight(double heightEndRight)
	{
		this.heightEndRight = heightEndRight;
	}
	/**
	 * @return Returns the grade.
	 */
	public double getGrade()
	{
		return grade;
	}
	/**
	 * @param grade
	 *            The grade to set.
	 */
	public void setGrade(double grade)
	{
		this.grade = grade;
	}
	/**
	 * @return Returns the bankingStart.
	 */
	public double getBankingStart()
	{
		return bankingStart;
	}
	/**
	 * @param bankingStart
	 *            The bankingStart to set.
	 */
	public void setBankingStart(double bankingStart)
	{
		this.bankingStart = bankingStart;
	}
	/**
	 * @return Returns the bankingEnd.
	 */
	public double getBankingEnd()
	{
		return bankingEnd;
	}
	/**
	 * @param bankingEnd
	 *            The bankingEnd to set.
	 */
	public void setBankingEnd(double bankingEnd)
	{
		this.bankingEnd = bankingEnd;
	}
	/**
	 * @return Returns the profil.
	 */
	public String getProfil()
	{
		return profil;
	}
	/**
	 * @param profil
	 *            The profil to set.
	 */
	public void setProfil(String profil)
	{
		this.profil = profil;
	}
	/**
	 * @return Returns the profilSteps.
	 */
	public double getProfilSteps()
	{
		return profilSteps;
	}
	/**
	 * @param profilSteps
	 *            The profilSteps to set.
	 */
	public void setProfilSteps(double profilSteps)
	{
		this.profilSteps = profilSteps;
	}
	/**
	 * @return Returns the profilStepsLength.
	 */
	public double getProfilStepsLength()
	{
		return profilStepsLength;
	}
	/**
	 * @param profilStepsLength
	 *            The profilStepsLength to set.
	 */
	public void setProfilStepsLength(double profilStepsLength)
	{
		this.profilStepsLength = profilStepsLength;
	}
	/**
	 * @return Returns the profilStartTangent.
	 */
	public double getProfilStartTangent()
	{
		return profilStartTangent;
	}
	/**
	 * @param profilStartTangent
	 *            The profilStartTangent to set.
	 */
	public void setProfilStartTangent(double profilStartTangent)
	{
		this.profilStartTangent = profilStartTangent;
	}
	/**
	 * @return Returns the profilEndTangent.
	 */
	public double getProfilEndTangent()
	{
		return profilEndTangent;
	}
	/**
	 * @param profilEndTangent
	 *            The profilEndTangent to set.
	 */
	public void setProfilEndTangent(double profilEndTangent)
	{
		this.profilEndTangent = profilEndTangent;
	}
	/**
	 * @return Returns the profilStartTangentLeft.
	 */
	public double getProfilStartTangentLeft()
	{
		return profilStartTangentLeft;
	}
	/**
	 * @param profilStartTangentLeft
	 *            The profilStartTangentLeft to set.
	 */
	public void setProfilStartTangentLeft(double profilStartTangentLeft)
	{
		this.profilStartTangentLeft = profilStartTangentLeft;
	}
	/**
	 * @return Returns the profilEndTangentLeft.
	 */
	public double getProfilEndTangentLeft()
	{
		return profilEndTangentLeft;
	}
	/**
	 * @param profilEndTangentLeft
	 *            The profilEndTangentLeft to set.
	 */
	public void setProfilEndTangentLeft(double profilEndTangentLeft)
	{
		this.profilEndTangentLeft = profilEndTangentLeft;
	}
	/**
	 * @return Returns the profilStartTangentRight.
	 */
	public double getProfilStartTangentRight()
	{
		return profilStartTangentRight;
	}
	/**
	 * @param profilStartTangentRight
	 *            The profilStartTangentRight to set.
	 */
	public void setProfilStartTangentRight(double profilStartTangentRight)
	{
		this.profilStartTangentRight = profilStartTangentRight;
	}
	/**
	 * @return Returns the profilEndTangentRight.
	 */
	public double getProfilEndTangentRight()
	{
		return profilEndTangentRight;
	}
	/**
	 * @param profilEndTangentRight
	 *            The profilEndTangentRight to set.
	 */
	public void setProfilEndTangentRight(double profilEndTangentRight)
	{
		this.profilEndTangentRight = profilEndTangentRight;
	}

	/**
	 * @return Returns the length.
	 */
	public double getLength()
	{
		return length;
	}
	/**
	 * @param length
	 *            The length to set.
	 */
	public void setLength(double length)
	{
		this.length = length;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType()
	{
		return type;
	}
	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	/**
	 * @return Returns the profilStepLength.
	 */
	public double getProfilStepLength()
	{
		return profilStepLength;
	}
	/**
	 * @param profilStepLength
	 *            The profilStepLength to set.
	 */
	public void setProfilStepLength(double profilStepLength)
	{
		this.profilStepLength = profilStepLength;
	}
	/**
	 * @return Returns the count.
	 */
	public int getCount()
	{
		return count;
	}
	/**
	 * @param count
	 *            The count to set.
	 */
	public void setCount(int count)
	{
		this.count = count;
	}
	/**
	 * @return Returns the previousShape.
	 */
	public Segment getPreviousShape()
	{
		return previousShape;
	}
	/**
	 * @param previousShape
	 *            The previousShape to set.
	 */
	public void setPreviousShape(Segment prev)
	{
		this.previousShape = prev;
	}
	/**
	 *  
	 */
	private void setProperties()
	{

	}

	public synchronized void removeSideListener(ActionListener l)
	{

	}

	public synchronized void addSideListener(SegmentSideListener l)
	{
		Vector v = segmentListeners == null ? new Vector(2) : (Vector) segmentListeners.clone();
		if (!v.contains(l))
		{
			v.addElement(l);
			segmentListeners = v;
		}
	}

	public Object clone()
	{
		Segment s = null;
		try
		{
			s = (Segment) super.clone();
			s.left = (SegmentSide) this.left.clone();
			s.right = (SegmentSide) this.right.clone();
			s.name = this.name;
			s.type = this.type;
			s.heightEnd = this.heightEnd;
			s.heightStart = this.heightStart;
			s.length = this.length;
			s.surface = this.surface;
			
		} catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s; // return the clone
	}
    /**
     * @return Returns the points.
     */
    public Point2D.Double[] getPoints()
    {
        return points;
    }
    /**
     * @param points The points to set.
     */
    public void setPoints(Point2D.Double[] points)
    {
        this.points = points;
    }
    /**
     * @return Returns the dx.
     */
    public double getDx()
    {
        return dx;
    }
    /**
     * @return Returns the dy.
     */
    public double getDy()
    {
        return dy;
    }
}