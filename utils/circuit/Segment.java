/*
 *   Segment.java
 *   Created on 9 ??? 2005
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
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Vector;

import gui.EditorFrame;
import utils.Editor;
import utils.MutableDouble;

/**
 * @author Patrice Espie , Charalampos Alexopoulos
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

public class Segment implements Cloneable
{
	public enum Where { LEFT, CENTER, RIGHT }

	private Vector<SegmentSideListener>	segmentListeners	= new Vector<SegmentSideListener>();

	// neighbours
	public Segment			previousShape;
	public Segment			nextShape;

	protected String		name					= "";
	protected String		comment					= null;

	protected SegmentSide	left					= new SegmentSide(false);
	protected SegmentSide	right					= new SegmentSide(true);
	//	 type
	protected String		type;
	protected int			count;
	public double			startTrackAlpha;
	public double			endTrackAlpha;
	public Point2D.Double	startTrackCenter		= new Point2D.Double();
	public Point2D.Double	endTrackCenter			= new Point2D.Double();
	public double			distFromCircuitStart;
	public int				nbSteps					= 0;
	public double			stepLength;

	// All datas
	protected double		length					= Double.NaN;
	protected String		surface					= null;

	protected double		heightStart				= Double.NaN;
	protected double		heightStartLeft			= Double.NaN;
	protected double		heightStartRight		= Double.NaN;

	protected double		heightEnd				= Double.NaN;
	protected double		heightEndLeft			= Double.NaN;
	protected double		heightEndRight			= Double.NaN;

	protected double		grade					= Double.NaN;
	protected double		bankingStart			= Double.NaN;
	protected double		bankingEnd				= Double.NaN;

	protected String		profil					= null;
	protected int			profilSteps				= Integer.MAX_VALUE;
	protected double		profilStepsLength		= Double.NaN;
	protected double		profilStartTangent		= Double.NaN;
	protected double		profilEndTangent		= Double.NaN;
	protected double		profilStartTangentLeft	= Double.NaN;
	protected double		profilEndTangentLeft	= Double.NaN;
	protected double		profilStartTangentRight	= Double.NaN;
	protected double		profilEndTangentRight	= Double.NaN;

	// Speed Dreams attributes the editor doesn't edit but must preserve
	protected int			penalty					= Integer.MAX_VALUE;
	protected int			envMapIndex				= Integer.MAX_VALUE;
	protected double		dovFactor				= Double.NaN;

	// calculated values
	protected double		calculatedHeightStart 		= Double.NaN;
	protected double		calculatedHeightStartLeft 	= Double.NaN;
	protected double		calculatedHeightStartRight 	= Double.NaN;
	protected double		calculatedHeightEnd			= Double.NaN;
	protected double		calculatedHeightEndLeft		= Double.NaN;
	protected double		calculatedHeightEndRight 	= Double.NaN;
	protected double		calculatedGrade 			= Double.NaN;
	protected double		calculatedBankingStart 		= Double.NaN;
	protected double		calculatedBankingEnd 		= Double.NaN;
	protected double		calculatedStartTangent		= Double.NaN;
	protected double		calculatedEndTangent		= Double.NaN;
	protected double		calculatedStartTangentLeft	= Double.NaN;
	protected double		calculatedEndTangentLeft	= Double.NaN;
	protected double		calculatedStartTangentRight	= Double.NaN;
	protected double		calculatedEndTangentRight	= Double.NaN;

	// shape to be drawn
	public Point3D			points[];
	public Point2D.Double	trPoints[];

	// datas for fast 'draw' process
	int						xToDraw[]			= new int[4];
	int						yToDraw[]			= new int[4];

	double dx;
	double dy;

	protected Rectangle2D.Double	boundingRectangle	= new Rectangle2D.Double(0, 0, 0, 0);

	protected static final int	TRACK_START_LEFT			= 0;
	protected static final int	TRACK_END_LEFT				= 1;
	protected static final int	TRACK_END_RIGHT				= 2;
	protected static final int	TRACK_START_RIGHT			= 3;
	protected static final int	LEFT_BORDER_START_LEFT		= 4;
	protected static final int	LEFT_BORDER_END_LEFT		= 5;
	protected static final int	LEFT_BORDER_END_RIGHT		= 6;
	protected static final int	LEFT_BORDER_START_RIGHT		= 7;
	protected static final int	LEFT_SIDE_START_LEFT		= 8;
	protected static final int	LEFT_SIDE_END_LEFT			= 9;
	protected static final int	LEFT_SIDE_END_RIGHT			= 10;
	protected static final int	LEFT_SIDE_START_RIGHT		= 11;
	protected static final int	LEFT_BARRIER_START_LEFT		= 12;
	protected static final int	LEFT_BARRIER_END_LEFT		= 13;
	protected static final int	LEFT_BARRIER_END_RIGHT		= 14;
	protected static final int	LEFT_BARRIER_START_RIGHT	= 15;
	protected static final int	RIGHT_BORDER_START_RIGHT	= 16;
	protected static final int	RIGHT_BORDER_END_RIGHT		= 17;
	protected static final int	RIGHT_BORDER_END_LEFT		= 18;
	protected static final int	RIGHT_BORDER_START_LEFT		= 19;
	protected static final int	RIGHT_SIDE_START_RIGHT		= 20;
	protected static final int	RIGHT_SIDE_END_RIGHT		= 21;
	protected static final int	RIGHT_SIDE_END_LEFT			= 22;
	protected static final int	RIGHT_SIDE_START_LEFT		= 23;
	protected static final int	RIGHT_BARRIER_START_RIGHT	= 24;
	protected static final int	RIGHT_BARRIER_END_RIGHT		= 25;
	protected static final int	RIGHT_BARRIER_END_LEFT		= 26;
	protected static final int	RIGHT_BARRIER_START_LEFT	= 27;
	protected static final int	ARROW_START_LEFT			= 28;
	protected static final int	ARROW_END_LEFT				= 29;
	protected static final int	ARROW_END_RIGHT				= 30;
	protected static final int	ARROW_START_RIGHT			= 31;

	public Segment()
	{
		this(null);
	}

	public Segment(String type)
	{
		this.type = type;
		setProperties();
	}

	public Segment(String name, String type)
	{
		this.name = name;
		this.type = type;
		setProperties();
	}

	public void set(Segment segment)
	{
		left.set(segment.left);
		right.set(segment.right);
		name = segment.name;
		type = segment.type;
		comment = segment.comment;
		length = segment.length;
		nbSteps = segment.nbSteps;
		stepLength = segment.stepLength;
		surface = segment.surface;
		heightStart = segment.heightStart;
		heightStartLeft = segment.heightStartLeft;
		heightStartRight = segment.heightStartRight;
		heightEnd = segment.heightEnd;
		heightEndLeft = segment.heightEndLeft;
		heightEndRight = segment.heightEndRight;
		grade = segment.grade;
		bankingStart = segment.bankingStart;
		bankingEnd = segment.bankingEnd;
		profil = segment.profil;
		profilSteps = segment.profilSteps;
		profilStepsLength = segment.profilStepsLength;
		profilStartTangent = segment.profilStartTangent;
		profilEndTangent = segment.profilEndTangent;
		profilStartTangentLeft = segment.profilStartTangentLeft;
		profilEndTangentLeft = segment.profilEndTangentLeft;
		profilStartTangentRight = segment.profilStartTangentRight;
		profilEndTangentRight = segment.profilEndTangentRight;
		penalty = segment.penalty;
		envMapIndex = segment.envMapIndex;
		dovFactor = segment.dovFactor;
		calculatedHeightStart = segment.calculatedHeightStart;
		calculatedHeightStartLeft = segment.calculatedHeightStartLeft;
		calculatedHeightStartRight = segment.calculatedHeightStartRight;
		calculatedHeightEnd = segment.calculatedHeightEnd;
		calculatedHeightEndLeft = segment.calculatedHeightEndLeft;
		calculatedHeightEndRight = segment.calculatedHeightEndRight;
		calculatedGrade = segment.calculatedGrade;
		calculatedBankingStart = segment.calculatedBankingStart;
		calculatedBankingEnd = segment.calculatedBankingEnd;
		calculatedStartTangent = segment.calculatedStartTangent;
		calculatedEndTangent = segment.calculatedEndTangent;
		calculatedStartTangentLeft = segment.calculatedStartTangentLeft;
		calculatedEndTangentLeft = segment.calculatedEndTangentLeft;
		calculatedStartTangentRight = segment.calculatedStartTangentRight;
		calculatedEndTangentRight = segment.calculatedEndTangentRight;
		points = Arrays.copyOf(segment.points, segment.points.length);
		trPoints = Arrays.copyOf(segment.trPoints, segment.trPoints.length);
		xToDraw = Arrays.copyOf(segment.xToDraw, segment.xToDraw.length);
		yToDraw = Arrays.copyOf(segment.yToDraw, segment.yToDraw.length);
		dx = segment.dx;
		dy = segment.dy;
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

		if (name == null || name.equals("")) // || i > 0)
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
	
	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	/**
	 * @return Returns the surface.
	 */
	public String getSurface()
	{
		return surface;
	}
	/**
	 * @param surface
	 *            The surface to set.
	 */
	public void setSurface(String surface)
	{
		this.surface = surface;
	}
	public Segment copyTo(Segment shape)
	{
		shape.points = points.clone();
		shape.trPoints = trPoints.clone();
		shape.type = new String(type);
		shape.xToDraw = xToDraw.clone();
		shape.yToDraw = yToDraw.clone();

		return shape;
	}

	// adapted from: https://web.archive.org/web/20130126163405/http://geomalgorithms.com/a03-_inclusion.html
	// Copyright 2000 softSurfer, 2012 Dan Sunday
	protected double isLeft(Point2D.Double P0, Point2D.Double P1, Point2D.Double P2)
	{
		return ((P1.x - P0.x) * (P2.y - P0.y) - (P2.x -  P0.x) * (P1.y - P0.y));
	}
	
	public boolean contains(Point2D.Double point)
	{
		if (points == null)
			return false;
		
		for (int i = 0; i < points.length; i += 4)
		{
			int count = 0;

			for (int j = 0; j < 4; j++)
			{
				int start = i + j;
				int next = i + ((j + 1) % 4);

				if (points[start].y <= point.y)
				{
					if (points[next].y > point.y)
					{
						if (isLeft(points[start], points[next], point) > 0)
							++count;
					}
				}
				else
				{
					if (points[next].y <= point.y)
					{
						if (isLeft(points[start], points[next], point) < 0)
							--count;
					}
				}
			}

			if (count != 0)
				return true;
		}

		return false;
	}

	public double trackSpline(double p0, double p1, double t0, double t1, double t)
	{
	    double t2, t3;
	    double h0, h1, h2, h3;

	    t2 = t * t;
	    t3 = t * t2;
	    h1 = 3 * t2 - 2 * t3;
	    h0 = 1 - h1;
	    h2 = t3 - 2 * t2 + t;
	    h3 = t3 - t2;

	    return h0 * p0 + h1 * p1 + h2 * t0 + h3 * t1;
	}

	public Rectangle2D.Double getBounds()
	{
		return boundingRectangle;
	}

	protected void setBounds()
	{
		if (points == null || points.length == 0)
			return;

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

		boundingRectangle.setRect(minX, minY, maxX - minX, maxY - minY);
	}

	public void calcShape(EditorFrame editorFrame) throws Exception
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

	public double getHeightStart()
	{
		return heightStart;
	}
	public void setHeightStart(double heightStart)
	{
		this.heightStart = heightStart;
	}
	public boolean hasHeightStart()
	{
		return !Double.isNaN(heightStart);
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
	public boolean hasHeightStartLeft()
	{
		return !Double.isNaN(heightStartLeft);
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
	public boolean hasHeightStartRight()
	{
		return !Double.isNaN(heightStartRight);
	}

	public double getHeightEnd()
	{
		return heightEnd;
	}
	public void setHeightEnd(double heightEnd)
	{
		this.heightEnd = heightEnd;
	}
	public boolean hasHeightEnd()
	{
		return !Double.isNaN(heightEnd);
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
	public boolean hasHeightEndLeft()
	{
		return !Double.isNaN(heightEndLeft);
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
	public boolean hasHeightEndRight()
	{
		return !Double.isNaN(heightEndRight);
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
	public boolean hasGrade()
	{
		return !Double.isNaN(grade);
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
	public boolean hasBankingStart()
	{
		return !Double.isNaN(bankingStart);
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
	public boolean hasBankingEnd()
	{
		return !Double.isNaN(bankingEnd);
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
	public boolean hasProfil()
	{
		return profil != null;
	}

	/**
	 * @return Returns the profilSteps.
	 */
	public int getProfilSteps()
	{
		return profilSteps;
	}
	/**
	 * @param profilSteps
	 *            The profilSteps to set.
	 */
	public void setProfilSteps(int profilSteps)
	{
		this.profilSteps = profilSteps;
	}
	public boolean hasProfilSteps()
	{
		return profilSteps != Integer.MAX_VALUE;
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
	public boolean hasProfilStepsLength()
	{
		return !Double.isNaN(profilStepsLength);
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
	public boolean hasProfilStartTangent()
	{
		return !Double.isNaN(profilStartTangent);
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
	public boolean hasProfilEndTangent()
	{
		return !Double.isNaN(profilEndTangent);
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
	public boolean hasProfilStartTangentLeft()
	{
		return !Double.isNaN(profilStartTangentLeft);
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
	public boolean hasProfilEndTangentLeft()
	{
		return !Double.isNaN(profilEndTangentLeft);
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
	public boolean hasProfilStartTangentRight()
	{
		return !Double.isNaN(profilStartTangentRight);
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
	public boolean hasProfilEndTangentRight()
	{
		return !Double.isNaN(profilEndTangentRight);
	}

	public int getPenalty()
	{
		return penalty;
	}
	public void setPenalty(int penalty)
	{
		this.penalty = penalty;
	}

	public int getEnvMapIndex()
	{
		return envMapIndex;
	}
	public void setEnvMapIndex(int envMapIndex)
	{
		this.envMapIndex = envMapIndex;
	}

	public double getDovFactor()
	{
		return dovFactor;
	}
	public void setDovFactor(double dovFactor)
	{
		this.dovFactor = dovFactor;
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
	 * @return Returns the nextShape.
	 */
	public Segment getNextShape()
	{
		return nextShape;
	}
	/**
	 * @param previousShape
	 *            The nextShape to set.
	 */
	public void setNextShape(Segment next)
	{
		this.nextShape = next;
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
		Vector<SegmentSideListener> v = segmentListeners == null ? new Vector<SegmentSideListener>(2) : (Vector<SegmentSideListener>) segmentListeners.clone();
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
			s.comment = this.comment;
			s.length = this.length;
			s.nbSteps = this.nbSteps;
			s.stepLength = this.stepLength;
			s.surface = this.surface;
			s.heightStart = this.heightStart;
			s.heightStartLeft = this.heightStartLeft;
			s.heightStartRight = this.heightStartRight;
			s.heightEnd = this.heightEnd;
			s.heightEndLeft = this.heightEndLeft;
			s.heightEndRight = this.heightEndRight;
			s.grade = this.grade;
			s.bankingStart = this.bankingStart;
			s.bankingEnd = this.bankingEnd;
			s.profil = this.profil;
			s.profilSteps = this.profilSteps;
			s.profilStepsLength = this.profilStepsLength;
			s.profilStartTangent = this.profilStartTangent;
			s.profilEndTangent = this.profilEndTangent;
			s.profilStartTangentLeft = this.profilStartTangentLeft;
			s.profilEndTangentLeft = this.profilEndTangentLeft;
			s.profilStartTangentRight = this.profilStartTangentRight;
			s.profilEndTangentRight = this.profilEndTangentRight;
			s.penalty = this.penalty;
			s.envMapIndex = this.envMapIndex;
			s.dovFactor = this.dovFactor;
			s.calculatedHeightStart = this.calculatedHeightStart;
			s.calculatedHeightStartLeft = this.calculatedHeightStartLeft;
			s.calculatedHeightStartRight = this.calculatedHeightStartRight;
			s.calculatedHeightEnd = this.calculatedHeightEnd;
			s.calculatedHeightEndLeft = this.calculatedHeightEndLeft;
			s.calculatedHeightEndRight = this.calculatedHeightEndRight;
			s.calculatedGrade = this.calculatedGrade;
			s.calculatedBankingStart = this.calculatedBankingStart;
			s.calculatedBankingEnd = this.calculatedBankingEnd;
			s.calculatedStartTangent = this.calculatedStartTangent;
			s.calculatedEndTangent = this.calculatedEndTangent;
			s.calculatedStartTangentLeft = this.calculatedStartTangentLeft;
			s.calculatedEndTangentLeft = this.calculatedEndTangentLeft;
			s.calculatedStartTangentRight = this.calculatedStartTangentRight;
			s.calculatedEndTangentRight = this.calculatedEndTangentRight;
			s.points = Arrays.copyOf(points, points.length);
			s.trPoints = Arrays.copyOf(trPoints, trPoints.length);
			s.xToDraw = xToDraw.clone();
			s.yToDraw = yToDraw.clone();
			s.dx = dx;
			s.dy = dy;
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
    public Point3D[] getPoints()
    {
        return points;
    }
    /**
     * @param points The points to set.
     */
    public void setPoints(Point3D[] points)
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

	public double getCalculatedHeightStart()
	{
		return calculatedHeightStart;
	}
	public void setCalculatedHeightStart(double calculatedHeightStart)
	{
		this.calculatedHeightStart = calculatedHeightStart;
	}

	public double getCalculatedHeightStartLeft()
	{
		return calculatedHeightStartLeft;
	}
	public void setCalculatedHeightStartLeft(double calculatedHeightStartLeft)
	{
		this.calculatedHeightStartLeft = calculatedHeightStartLeft;
	}

	public double getCalculatedHeightStartRight()
	{
		return calculatedHeightStartRight;
	}
	public void setCalculatedHeightStartRight(double calculatedHeightStartRight)
	{
		this.calculatedHeightStartRight = calculatedHeightStartRight;
	}

	public double getCalculatedHeightEnd()
	{
		return calculatedHeightEnd;
	}
	public void setCalculatedHeightEnd(double calculatedHeightEnd)
	{
		this.calculatedHeightEnd = calculatedHeightEnd;
	}

	public double getCalculatedHeightEndLeft()
	{
		return calculatedHeightEndLeft;
	}
	public void setCalculatedHeightEndLeft(double calculatedHeightEndLeft)
	{
		this.calculatedHeightEndLeft = calculatedHeightEndLeft;
	}

	public double getCalculatedHeightEndRight()
	{
		return calculatedHeightEndRight;
	}
	public void setCalculatedHeightEndRight(double calculatedHeightEndRight)
	{
		this.calculatedHeightEndRight = calculatedHeightEndRight;
	}

	public double getCalculatedGrade()
	{
		return calculatedGrade;
	}
	public void setCalculatedGrade(double calculatedGrade)
	{
		this.calculatedGrade = calculatedGrade;
	}

	public double getCalculatedBankingStart()
	{
		return calculatedBankingStart;
	}
	public void setCalculatedBankingStart(double calculatedBankingStart)
	{
		this.calculatedBankingStart = calculatedBankingStart;
	}

	public double getCalculatedBankingEnd()
	{
		return calculatedBankingEnd;
	}
	public void setCalculatedBankingEnd(double calculatedBankingEnd)
	{
		this.calculatedBankingEnd = calculatedBankingEnd;
	}

	public double getCalculatedStartTangent()
	{
		return calculatedStartTangent;
	}
	public void setCalculatedStartTangent(double calculatedStartTangent)
	{
		this.calculatedStartTangent = calculatedStartTangent;
	}

	public double getCalculatedEndTangent()
	{
		return calculatedEndTangent;
	}
	public void setCalculatedEndTangent(double calculatedEndTangent)
	{
		this.calculatedEndTangent = calculatedEndTangent;
	}

	public double getCalculatedStartTangentLeft()
	{
		return calculatedStartTangentLeft;
	}
	public void setCalculatedStartTangentLeft(double calculatedStartTangentLeft)
	{
		this.calculatedStartTangentLeft = calculatedStartTangentLeft;
	}

	public double getCalculatedEndTangentLeft()
	{
		return calculatedEndTangentLeft;
	}
	public void setCalculatedEndTangentLeft(double calculatedEndTangentLeft)
	{
		this.calculatedEndTangentLeft = calculatedEndTangentLeft;
	}

	public double getCalculatedStartTangentRight()
	{
		return calculatedStartTangentRight;
	}
	public void setCalculatedStartTangentRight(double calculatedStartTangentRight)
	{
		this.calculatedStartTangentRight = calculatedStartTangentRight;
	}

	public double getCalculatedEndTangentRight()
	{
		return calculatedEndTangentRight;
	}
	public void setCalculatedEndTangentRight(double calculatedEndTangentRight)
	{
		this.calculatedEndTangentRight = calculatedEndTangentRight;
	}

	public String getValidSurface(EditorFrame editorFrame)
	{
		Segment previous = this;
		String surf;
		while (previous != null)
		{
			surf = surface;
			if (surf != null)
			{
				return surf;
			}
			previous = previous.previousShape;
		}

		surf = editorFrame.getTrackData().getMainTrack().getSurface();
		if (surf == null)
		{
			surf = MainTrack.DEFAULT_SURFACE;
		}
		return surf;
	}

	// this is not inherited, and like the game's track loader the Main Track
	// "profil" isn't used: a segment without its own profil is a spline
	public String getValidProfil(EditorFrame editorFrame)
	{
		if (hasProfil())
		{
			return getProfil();
		}

		return "spline";
	}

	/**
	 * @return true if the game builds this segment's profile as a spline
	 */
	public boolean isSpline()
	{
		return !hasProfil() || getProfil().equals("spline");
	}

	/**
	 * Number of steps the game's track loader builds this segment with: one for
	 * a linear profile; otherwise "profil steps" (unless it is 1), else from the
	 * segment's or Main Track's "profil steps length", else one.
	 *
	 * @param length the segment's length ((start + end radius) / 2 * arc for a curve)
	 */
	public int getValidSteps(EditorFrame editorFrame, double length)
	{
		if (!isSpline())
			return 1;

		if (hasProfilSteps() && getProfilSteps() != 1)
			return getProfilSteps();

		double profilStepsLength = getValidProfilStepsLength(editorFrame);
		if (profilStepsLength > 0)
			return (int) (length / profilStepsLength) + 1;

		return 1;
	}

	// this is not inherited
	public double getValidProfilStepsLength(EditorFrame editorFrame)
	{
		double	length = profilStepsLength;
		if (hasProfilStepsLength())
		{
			return length;
		}

		// NaN when neither the segment nor the Main Track has one: the game
		// then builds the segment as a single step
		return editorFrame.getTrackData().getMainTrack().getProfilStepsLength();
	}

    public double getValidLeftBarrierWidth(EditorFrame editorFrame)
    {
        Segment previous = this;
        double	value;

        // try to get missing attribute from previous segments first
        while (previous != null)
        {
            value = previous.left.barrierWidth;
            if (!Double.isNaN(value))
            {
                return value;
            }
            else
            {
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getLeft().getBarrierWidth();
        if (Double.isNaN(value))
    	{
        	String style = getValidLeftBarrierStyle(editorFrame);

        	switch (style)
        	{
        	case "fence":
        		value = SegmentSide.DEFAULT_BARRIER_FENCE_WIDTH;
        		break;
        	case "wall":
        		value = SegmentSide.DEFAULT_BARRIER_WALL_WIDTH;
        		break;
        	default:
        		value = Double.NaN;
        		break;
        	}
    	}

        return value;
    }

    public double getValidRightBarrierWidth(EditorFrame editorFrame)
    {
    	Segment previous = this;
        double	value;

    	// try to get missing attribute from previous segments first
        while (previous != null)
    	{
            value = previous.right.barrierWidth;
            if (!Double.isNaN(value))
    		{
                return value;
    		}
    		else
    		{
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getRight().getBarrierWidth();
        if (Double.isNaN(value))
    	{
        	String style = getValidRightBarrierStyle(editorFrame);

        	switch (style)
        	{
        	case "fence":
        		value = SegmentSide.DEFAULT_BARRIER_FENCE_WIDTH;
        		break;
        	case "wall":
        		value = SegmentSide.DEFAULT_BARRIER_WALL_WIDTH;
        		break;
        	default:
        		value = Double.NaN;
        		break;
        	}
    	}

        return value;
    }

    public double getValidLeftBarrierHeight(EditorFrame editorFrame)
    {
        Segment previous = this;
        double	value;

        // try to get missing attribute from previous segments first
        while (previous != null)
        {
            value = previous.left.barrierHeight;
            if (!Double.isNaN(value))
            {
                return value;
            }
            else
            {
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getLeft().getBarrierHeight();
        if (Double.isNaN(value))
    	{
        	String style = getValidLeftBarrierStyle(editorFrame);

        	switch (style)
        	{
        	case "fence":
        		value = SegmentSide.DEFAULT_BARRIER_FENCE_HEIGHT;
        		break;
        	case "wall":
        		value = SegmentSide.DEFAULT_BARRIER_WALL_HEIGHT;
        		break;
        	default:
        		value = Double.NaN;
        		break;
        	}
    	}

        return value;
    }

    public double getValidRightBarrierHeight(EditorFrame editorFrame)
    {
    	Segment previous = this;
        double	value;

    	// try to get missing attribute from previous segments first
        while (previous != null)
    	{
            value = previous.right.barrierHeight;
            if (!Double.isNaN(value))
    		{
                return value;
    		}
    		else
    		{
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getRight().getBarrierHeight();
        if (Double.isNaN(value))
    	{
        	String style = getValidRightBarrierStyle(editorFrame);

        	switch (style)
        	{
        	case "fence":
        		value = SegmentSide.DEFAULT_BARRIER_FENCE_HEIGHT;
        		break;
        	case "wall":
        		value = SegmentSide.DEFAULT_BARRIER_WALL_HEIGHT;
        		break;
        	default:
        		value = Double.NaN;
        		break;
        	}
    	}

        return value;
    }

    public String getValidLeftBarrierSurface(EditorFrame editorFrame)
    {
        Segment previous = this;
        String	value;

        // try to get missing attribute from previous segments first
        while (previous != null)
        {
            value = previous.left.barrierSurface;
            if (value != null && !value.isEmpty())
            {
                return value;
            }
            else
            {
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getLeft().getBarrierSurface();
        if (value == null || value.isEmpty())
    	{
        	String style = getValidLeftBarrierStyle(editorFrame);

        	switch (style)
        	{
        	case "fence":
        		value = SegmentSide.DEFAULT_BARRIER_FENCE_SURFACE;
        		break;
        	case "wall":
        		value = SegmentSide.DEFAULT_BARRIER_WALL_SURFACE;
        		break;
        	default:
        		value = null;
        		break;
        	}
    	}

        return value;
    }

    public String getValidRightBarrierSurface(EditorFrame editorFrame)
    {
    	Segment previous = this;
        String	value;

    	// try to get missing attribute from previous segments first
        while (previous != null)
    	{
            value = previous.right.barrierSurface;
            if (value != null && !value.isEmpty())
    		{
                return value;
    		}
    		else
    		{
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getRight().getBarrierSurface();
        if (value == null || value.isEmpty())
    	{
        	String style = getValidRightBarrierStyle(editorFrame);

        	switch (style)
        	{
        	case "fence":
        		value = SegmentSide.DEFAULT_BARRIER_FENCE_SURFACE;
        		break;
        	case "wall":
        		value = SegmentSide.DEFAULT_BARRIER_WALL_SURFACE;
        		break;
        	default:
        		value = null;
        		break;
        	}
    	}

        return value;
    }

    public String getValidLeftBarrierStyle(EditorFrame editorFrame)
    {
        Segment previous = this;
        String	value;

        // try to get missing attribute from previous segments first
        while (previous != null)
        {
            value = previous.left.barrierStyle;
            if (value != null && !value.isEmpty())
            {
                return value;
            }
            else
            {
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getLeft().getBarrierStyle();
        if (value == null || value.isEmpty())
    	{
            value = SegmentSide.DEFAULT_BARRIER_STYLE;
    	}

        return value;
    }

    public String getValidRightBarrierStyle(EditorFrame editorFrame)
    {
    	Segment previous = this;
        String	value;

    	// try to get missing attribute from previous segments first
        while (previous != null)
    	{
            value = previous.right.barrierStyle;
            if (value != null && !value.isEmpty())
    		{
                return value;
    		}
    		else
    		{
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getRight().getBarrierStyle();
        if (value == null || value.isEmpty())
    	{
            value = SegmentSide.DEFAULT_BARRIER_STYLE;
    	}

        return value;
    }

    public double getValidLeftBorderWidth(EditorFrame editorFrame)
    {
        Segment previous = this;
        double	value;

        // try to get missing attribute from previous segments first
        while (previous != null)
        {
            value = previous.left.borderWidth;
            if (!Double.isNaN(value))
            {
                return value;
            }
            else
            {
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getLeft().getBorderWidth();
        if (Double.isNaN(value))
    	{
        	String style = getValidLeftBorderStyle(editorFrame);

        	switch (style)
        	{
        	case "plan":
        		value = SegmentSide.DEFAULT_BORDER_PLAN_WIDTH;
        		break;
        	case "curb":
        		value = SegmentSide.DEFAULT_BORDER_CURB_WIDTH;
        		break;
        	case "wall":
        		value = SegmentSide.DEFAULT_BORDER_WALL_WIDTH;
        		break;
        	default:
        		value = Double.NaN;
        		break;
        	}
    	}

        return value;
    }

    public double getValidRightBorderWidth(EditorFrame editorFrame)
    {
    	Segment previous = this;
        double	value;

    	// try to get missing attribute from previous segments first
        while (previous != null)
    	{
            value = previous.right.borderWidth;
            if (!Double.isNaN(value))
    		{
                return value;
    		}
    		else
    		{
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getRight().getBorderWidth();
        if (Double.isNaN(value))
    	{
        	String style = getValidRightBorderStyle(editorFrame);

        	switch (style)
        	{
        	case "plan":
        		value = SegmentSide.DEFAULT_BORDER_PLAN_WIDTH;
        		break;
        	case "curb":
        		value = SegmentSide.DEFAULT_BORDER_CURB_WIDTH;
        		break;
        	case "wall":
        		value = SegmentSide.DEFAULT_BORDER_WALL_WIDTH;
        		break;
        	default:
        		value = Double.NaN;
        		break;
        	}
    	}

        return value;
    }

    public double getValidLeftBorderHeight(EditorFrame editorFrame)
    {
        Segment previous = this;
        double	value;

        // try to get missing attribute from previous segments first
        while (previous != null)
        {
            value = previous.left.borderHeight;
            if (!Double.isNaN(value))
            {
                return value;
            }
            else
            {
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getLeft().getBorderHeight();
        if (Double.isNaN(value))
    	{
        	String style = getValidLeftBorderStyle(editorFrame);

        	switch (style)
        	{
        	case "plan":
        		value = SegmentSide.DEFAULT_BORDER_PLAN_HEIGHT;
        		break;
        	case "curb":
        		value = SegmentSide.DEFAULT_BORDER_CURB_HEIGHT;
        		break;
        	case "wall":
        		value = SegmentSide.DEFAULT_BORDER_WALL_HEIGHT;
        		break;
        	default:
        		value = Double.NaN;
        		break;
        	}
    	}

        return value;
    }

    public double getValidRightBorderHeight(EditorFrame editorFrame)
    {
    	Segment previous = this;
        double	value;

    	// try to get missing attribute from previous segments first
        while (previous != null)
    	{
            value = previous.right.borderHeight;
            if (!Double.isNaN(value))
    		{
                return value;
    		}
    		else
    		{
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getRight().getBorderHeight();
        if (Double.isNaN(value))
    	{
        	String style = getValidRightBorderStyle(editorFrame);

        	switch (style)
        	{
        	case "plan":
        		value = SegmentSide.DEFAULT_BORDER_PLAN_HEIGHT;
        		break;
        	case "curb":
        		value = SegmentSide.DEFAULT_BORDER_CURB_HEIGHT;
        		break;
        	case "wall":
        		value = SegmentSide.DEFAULT_BORDER_WALL_HEIGHT;
        		break;
        	default:
        		value = Double.NaN;
        		break;
        	}
    	}

        return value;
    }

    public String getValidLeftBorderSurface(EditorFrame editorFrame)
    {
        Segment previous = this;
        String	value;

        // try to get missing attribute from previous segments first
        while (previous != null)
        {
            value = previous.left.borderSurface;
            if (value != null && !value.isEmpty())
            {
                return value;
            }
            else
            {
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getLeft().getBorderSurface();
        if (value == null || value.isEmpty())
    	{
        	String style = getValidLeftBorderStyle(editorFrame);

        	switch (style)
        	{
        	case "plan":
        		value = SegmentSide.DEFAULT_BORDER_PLAN_LEFT_SURFACE;
        		break;
        	case "curb":
        		value = SegmentSide.DEFAULT_BORDER_CURB_LEFT_SURFACE;
        		break;
        	case "wall":
        		value = SegmentSide.DEFAULT_BORDER_WALL_SURFACE;
        		break;
        	default:
        		value = null;
        		break;
        	}
    	}

        return value;
    }

    public String getValidRightBorderSurface(EditorFrame editorFrame)
    {
    	Segment previous = this;
        String	value;

    	// try to get missing attribute from previous segments first
        while (previous != null)
    	{
            value = previous.right.borderSurface;
            if (value != null && !value.isEmpty())
    		{
                return value;
    		}
    		else
    		{
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getRight().getBorderSurface();
        if (value == null || value.isEmpty())
    	{
        	String style = getValidRightBorderStyle(editorFrame);

        	switch (style)
        	{
        	case "plan":
        		value = SegmentSide.DEFAULT_BORDER_PLAN_RIGHT_SURFACE;
        		break;
        	case "curb":
        		value = SegmentSide.DEFAULT_BORDER_CURB_RIGHT_SURFACE;
        		break;
        	case "wall":
        		value = SegmentSide.DEFAULT_BORDER_WALL_SURFACE;
        		break;
        	default:
        		value = null;
        		break;
        	}
    	}

        return value;
    }

    public String getValidLeftBorderStyle(EditorFrame editorFrame)
    {
        Segment previous = this;
        String	value;

        // try to get missing attribute from previous segments first
        while (previous != null)
        {
            value = previous.left.borderStyle;
            if (value != null && !value.isEmpty())
            {
                return value;
            }
            else
            {
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getLeft().getBorderStyle();
        if (value == null || value.isEmpty())
    	{
            value = SegmentSide.DEFAULT_BORDER_STYLE;
    	}

        return value;
    }

    public String getValidRightBorderStyle(EditorFrame editorFrame)
    {
    	Segment previous = this;
        String	value;

    	// try to get missing attribute from previous segments first
        while (previous != null)
    	{
            value = previous.right.borderStyle;
            if (value != null && !value.isEmpty())
    		{
                return value;
    		}
    		else
    		{
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getRight().getBorderStyle();
        if (value == null || value.isEmpty())
    	{
            value = SegmentSide.DEFAULT_BORDER_STYLE;
    	}

        return value;
    }

    public double getValidLeftSideStartWidth(EditorFrame editorFrame)
    {
    	Segment previous = this;
		double	value;

    	// try to get missing attribute from previous segments first
        while (true)
    	{
            value = previous.left.sideStartWidth;
            if (!Double.isNaN(value))
    		{
                return value;
    		}
            previous = previous.previousShape;
            if (previous == null)
    		{
                break;
            }
            value = previous.left.sideEndWidth;
            if (!Double.isNaN(value))
            {
                return value;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getLeft().getSideStartWidth();
        if (Double.isNaN(value))
    	{
            value = SegmentSide.DEFAULT_SIDE_START_WIDTH;
    	}

        return value;
    }

    public double getValidLeftSideEndWidth(EditorFrame editorFrame)
    {
    	Segment previous = this;
		double	value;

    	// try to get missing attribute from previous segments first
        while (previous != null)
    	{
            value = previous.left.sideEndWidth;
            if (!Double.isNaN(value))
    		{
                return value;
            }
            value = previous.left.sideStartWidth;
            if (!Double.isNaN(value))
    		{
                return value;
    		}
            previous = previous.previousShape;
        }

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getLeft().getSideEndWidth();
        if (Double.isNaN(value))
    	{
            value = SegmentSide.DEFAULT_SIDE_END_WIDTH;
    	}

        return value;
    }

    public double getValidRightSideStartWidth(EditorFrame editorFrame)
    {
    	Segment previous = this;
		double	value;

    	// try to get missing attribute from previous segments first
        while (true)
    	{
            value = previous.right.sideStartWidth;
            if (!Double.isNaN(value))
    		{
                return value;
    		}
            previous = previous.previousShape;
            if (previous == null)
    		{
                break;
            }
            value = previous.right.sideEndWidth;
            if (!Double.isNaN(value))
            {
                return value;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getRight().getSideStartWidth();
        if (Double.isNaN(value))
        {
            value = SegmentSide.DEFAULT_SIDE_START_WIDTH;
    	}

        return value;
    }

    public double getValidRightSideEndWidth(EditorFrame editorFrame)
    {
    	Segment previous = this;
		double	value;

    	// try to get missing attribute from previous segments first
        while (previous != null)
    	{
            value = previous.right.sideEndWidth;
            if (!Double.isNaN(value))
    		{
                return value;
    		}
            value = previous.right.sideStartWidth;
            if (!Double.isNaN(value))
    		{
                return value;
    		}
            previous = previous.previousShape;
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getRight().getSideEndWidth();
        if (Double.isNaN(value))
    	{
            value = SegmentSide.DEFAULT_SIDE_END_WIDTH;
    	}

        return value;
	}
    
    public String getValidLeftSideSurface(EditorFrame editorFrame)
    {
        Segment previous = this;
        String	value;

        // try to get missing attribute from previous segments first
        while (previous != null)
        {
            value = previous.left.sideSurface;
            if (value != null && !value.isEmpty())
            {
                return value;
            }
            else
            {
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getLeft().getSideSurface();
        if (value == null || value.isEmpty())
    	{
            value = SegmentSide.DEFAULT_SIDE_SURFACE;
    	}

        return value;
    }

    public String getValidRightSideSurface(EditorFrame editorFrame)
    {
    	Segment previous = this;
        String	value;

    	// try to get missing attribute from previous segments first
        while (previous != null)
    	{
            value = previous.right.sideSurface;
            if (value != null && !value.isEmpty())
    		{
                return value;
    		}
    		else
    		{
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getRight().getSideSurface();
        if (value == null || value.isEmpty())
    	{
            value = SegmentSide.DEFAULT_SIDE_SURFACE;
    	}

        return value;
    }

    public String getValidLeftSideBankingType(EditorFrame editorFrame)
    {
        Segment previous = this;
        String	value;

        // try to get missing attribute from previous segments first
        while (previous != null)
        {
            value = previous.left.sideBankingType;
            if (value != null && !value.isEmpty())
            {
                return value;
            }
            else
            {
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getLeft().getSideBankingType();
        if (value == null || value.isEmpty())
    	{
            value = SegmentSide.DEFAULT_SIDE_BANKING_TYPE;
    	}

        return value;
    }

    public String getValidRightSideBankingType(EditorFrame editorFrame)
    {
    	Segment previous = this;
        String	value;

    	// try to get missing attribute from previous segments first
        while (previous != null)
    	{
            value = previous.right.sideBankingType;
            if (value != null && !value.isEmpty())
    		{
                return value;
    		}
    		else
    		{
                previous = previous.previousShape;
    		}
    	}

    	// get it from main track when all else fails
        value = editorFrame.getTrackData().getMainTrack().getRight().getSideBankingType();
        if (value == null || value.isEmpty())
    	{
            value = SegmentSide.DEFAULT_SIDE_BANKING_TYPE;
    	}

        return value;
    }

    public double getValidHeightStart()
    {
    	Segment previous = this;

    	// try to get missing attribute from previous segments
        while (previous != null)
    	{
            if (previous.hasHeightStart())
    		{
                return previous.heightStart;
    		}
            else if (previous.hasHeightStartLeft() && 
            		 previous.hasHeightStartRight() && 
            		 previous.heightStartLeft == previous.heightStartRight)
            {
            	return previous.heightStartLeft;
            }
            previous = previous.previousShape;
            if (previous == null)
        	{
                break;
            }
            if (previous.hasHeightEnd())
    		{
                return previous.heightEnd;
    		}
            else if (previous.hasHeightEndLeft() && 
            		 previous.hasHeightEndRight() && 
            		 previous.heightEndLeft == previous.heightEndRight)
            {
            	return previous.heightEnd;
            }
    	}

        return Double.NaN;
    }

    public double getValidHeightStartLeft()
    {
    	Segment previous = this;

    	// try to get missing attribute from previous segments
        while (previous != null)
    	{
            if (previous.hasHeightStartLeft())
    		{
                return previous.heightStartLeft;
    		}
            else if (previous.hasHeightStart())
            {
            	return previous.heightStart;
            }
            previous = previous.previousShape;
            if (previous == null)
        	{
                break;
            }
            if (previous.hasHeightEndLeft())
    		{
                return previous.heightEndLeft;
    		}
            else if (previous.hasHeightEnd())
            {
            	return previous.heightEnd;
            }
    	}

        return Double.NaN;
    }

    public double getValidHeightStartRight()
    {
    	Segment previous = this;

    	// try to get missing attribute from previous segments
        while (previous != null)
    	{
            if (previous.hasHeightStartRight())
    		{
                return previous.heightStartRight;
    		}
            else if (previous.hasHeightStart())
            {
            	return previous.heightStart;
            }
            previous = previous.previousShape;
            if (previous == null)
        	{
                break;
            }
            if (previous.hasHeightEndRight())
    		{
                return previous.heightEndRight;
    		}
            else if (previous.hasHeightEnd())
            {
            	return previous.heightEnd;
            }
    	}

        return Double.NaN;
    }

    public double getValidBankingStart(EditorFrame editorFrame)
    {
    	Segment previous = this;

    	// try to get missing attribute from previous segments
        while (previous != null)
    	{
            if (previous.hasBankingStart())
    		{
                return previous.bankingStart;
    		}
            else if (previous.hasHeightStart())
    		{
                return 0; // flat
    		}
            else if (previous.hasHeightStartLeft() && previous.hasHeightStartRight())
            {
            	if (previous.heightStartLeft == previous.heightStartRight)
            	{
            		return 0; // flat
            	}
            	else
            	{
        			return Math.toDegrees(Math.atan2(previous.heightStartLeft - previous.heightStartRight, editorFrame.getTrackData().getMainTrack().getWidth()));
            	}
            }
            previous = previous.previousShape;
            if (previous == null)
        	{
                break;
            }
            if (previous.hasBankingEnd())
    		{
                return previous.bankingEnd;
    		}
            else if (previous.hasHeightEnd())
    		{
                return 0; // flat
    		}
            else if (previous.hasHeightEndLeft() && previous.hasHeightEndRight())
            {
            	if (previous.heightEndLeft == previous.heightEndRight)
            	{
            		return 0; // flat
            	}
            	else
            	{
        			return Math.toDegrees(Math.atan2(previous.heightEndLeft - previous.heightEndRight, editorFrame.getTrackData().getMainTrack().getWidth()));
            	}
            }
    	}

        return Double.NaN;
    }

    public void inheritProperties(Segment previousShape)
    {
		setSurface(previousShape.getSurface());
		
		if (!Double.isNaN(previousShape.getHeightEnd()))
		{
			setHeightStart(previousShape.getHeightEnd());
			setHeightEnd(previousShape.getHeightEnd());
		}

		if (!Double.isNaN(previousShape.getHeightEndLeft()))
		{
			setHeightStartLeft(previousShape.getHeightEndLeft());
			setHeightEndLeft(previousShape.getHeightEndLeft());
		}

		if (!Double.isNaN(previousShape.getHeightEndRight()))
		{
			setHeightStartRight(previousShape.getHeightEndRight());
			setHeightEndRight(previousShape.getHeightEndRight());
		}

		setGrade(previousShape.getGrade());
		if (!Double.isNaN(bankingEnd))
		{
			setBankingStart(previousShape.getBankingEnd());
			setBankingEnd(previousShape.getBankingEnd());
		}

		setProfil(previousShape.getProfil());

		getLeft().setSideStartWidth(previousShape.getLeft().getSideEndWidth());
		getLeft().setSideEndWidth(previousShape.getLeft().getSideEndWidth());
		getLeft().setSideSurface(previousShape.getLeft().getSideSurface());
		getLeft().setSideBankingType(previousShape.getLeft().getSideBankingType());

		getLeft().setBarrierWidth(previousShape.getLeft().getBarrierWidth());
		getLeft().setBarrierHeight(previousShape.getLeft().getBarrierHeight());
		getLeft().setBarrierSurface(previousShape.getLeft().getBarrierSurface());
		getLeft().setBarrierStyle(previousShape.getLeft().getBarrierStyle());

		getLeft().setBorderWidth(previousShape.getLeft().getBorderWidth());
		getLeft().setBorderHeight(previousShape.getLeft().getBorderHeight());
		getLeft().setBorderSurface(previousShape.getLeft().getBorderSurface());
		getLeft().setBorderStyle(previousShape.getLeft().getBorderStyle());

		getRight().setSideStartWidth(previousShape.getRight().getSideEndWidth());
		getRight().setSideEndWidth(previousShape.getRight().getSideEndWidth());
		getRight().setSideSurface(previousShape.getRight().getSideSurface());
		getRight().setSideBankingType(previousShape.getRight().getSideBankingType());

		getRight().setBarrierWidth(previousShape.getRight().getBarrierWidth());
		getRight().setBarrierHeight(previousShape.getRight().getBarrierHeight());
		getRight().setBarrierSurface(previousShape.getRight().getBarrierSurface());
		getRight().setBarrierStyle(previousShape.getRight().getBarrierStyle());

		getRight().setBorderWidth(previousShape.getRight().getBorderWidth());
		getRight().setBorderHeight(previousShape.getRight().getBorderHeight());
		getRight().setBorderSurface(previousShape.getRight().getBorderSurface());
		getRight().setBorderStyle(previousShape.getRight().getBorderStyle());
    }

	public void unoptimize(EditorFrame editorFrame)
	{
		surface = getValidSurface(editorFrame);
		if (!Double.isNaN(getValidProfilStepsLength(editorFrame)))
			profilStepsLength = getValidProfilStepsLength(editorFrame);

		// TODO: elevation, banking and other stuff needs to be implemented

		left.setBarrierWidth(getValidLeftBarrierWidth(editorFrame));
		left.setBarrierHeight(getValidLeftBarrierHeight(editorFrame));
		left.setBarrierSurface(getValidLeftBarrierSurface(editorFrame));
		left.setBarrierStyle(getValidLeftBarrierStyle(editorFrame));

		left.setBorderWidth(getValidLeftBorderWidth(editorFrame));
		left.setBorderHeight(getValidLeftBorderHeight(editorFrame));
		left.setBorderSurface(getValidLeftBorderSurface(editorFrame));
		left.setBorderStyle(getValidLeftBorderStyle(editorFrame));

		left.setSideStartWidth(getValidLeftSideStartWidth(editorFrame));
		left.setSideEndWidth(getValidLeftSideEndWidth(editorFrame));
		left.setSideSurface(getValidLeftSideSurface(editorFrame));
		left.setSideBankingType(getValidLeftSideBankingType(editorFrame));

		right.setBarrierWidth(getValidRightBarrierWidth(editorFrame));
		right.setBarrierHeight(getValidRightBarrierHeight(editorFrame));
		right.setBarrierSurface(getValidRightBarrierSurface(editorFrame));
		right.setBarrierStyle(getValidRightBarrierStyle(editorFrame));

		right.setBorderWidth(getValidRightBorderWidth(editorFrame));
		right.setBorderHeight(getValidRightBorderHeight(editorFrame));
		right.setBorderSurface(getValidRightBorderSurface(editorFrame));
		right.setBorderStyle(getValidRightBorderStyle(editorFrame));

		right.setSideStartWidth(getValidRightSideStartWidth(editorFrame));
		right.setSideEndWidth(getValidRightSideEndWidth(editorFrame));
		right.setSideSurface(getValidRightSideSurface(editorFrame));
		right.setSideBankingType(getValidRightSideBankingType(editorFrame));
	}

	// from https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm
	private static final double EPSILON = 0.0000001;

	public static boolean rayIntersectsTriangle(Point3D rayOrigin,
												Point3D rayVector,
												Point3D vertex0,
												Point3D vertex1,
												Point3D vertex2,
												Point3D outIntersectionPoint)
	{
		Point3D edge1 = new Point3D();
		Point3D edge2 = new Point3D();
		Point3D h = new Point3D();
		Point3D s = new Point3D();
		Point3D q = new Point3D();
		double a, f, u, v;
		edge1.sub(vertex1, vertex0);
		edge2.sub(vertex2, vertex0);
		h.cross(rayVector, edge2);
		a = edge1.dot(h);

		if (a > -EPSILON && a < EPSILON)
		{
			return false;    // This ray is parallel to this triangle.
		}

		f = 1.0 / a;
		s.sub(rayOrigin, vertex0);
		u = f * (s.dot(h));

		if (u < 0.0 || u > 1.0)
		{
			return false;
		}

		q.cross(s, edge1);
		v = f * rayVector.dot(q);

		if (v < 0.0 || u + v > 1.0)
		{
			return false;
		}

		// At this stage we can compute t to find out where the intersection point is on the line.
		double t = f * edge2.dot(q);
		if (t > EPSILON) // ray intersection
		{
			outIntersectionPoint.set(0.0, 0.0, 0.0);
			outIntersectionPoint.scaleAdd(t, rayVector, rayOrigin);
			return true;
		}
		else // This means that there is a line intersection but not a ray intersection.
		{
			return false;
		}
	}

	public boolean getHeightAndSlopeAt(double x, double y, MutableDouble height, MutableDouble slopeLeft, MutableDouble slopeRight)
	{
		if (points == null)
			return false;

		Point3D rayOrigin = new Point3D(x, y, -1000.0);
		Point3D rayVector = new Point3D(0.0, 0.0, 1.0);
		Point3D intersectionPoint = new Point3D();

		int stride = 4 * (7 + (Editor.getProperties().getShowArrows() > 0.0 ? 1 : 0));

		for (int i = 0; i < points.length; i += stride)
		{
			if (rayIntersectsTriangle(rayOrigin, rayVector, points[i + 0], points[i + 1], points[i + 2], intersectionPoint))
			{
				height.setValue(intersectionPoint.z);
				double at = (((i / stride) * stepLength) + (stepLength / 2)) / length;
				slopeLeft.setValue(getTangentAt(at, Segment.Where.LEFT));
				slopeRight.setValue(getTangentAt(at, Segment.Where.RIGHT));
				return true;
			}
			if (rayIntersectsTriangle(rayOrigin, rayVector, points[i + 0], points[i + 2], points[i + 3], intersectionPoint))
			{
				height.setValue(intersectionPoint.z);
				double at = (((i / stride) * stepLength) + (stepLength / 2)) / length;
				slopeLeft.setValue(getTangentAt(at, Segment.Where.LEFT));
				slopeRight.setValue(getTangentAt(at, Segment.Where.RIGHT));
				return true;
			}
		}

		return false;
	}

	public double getHeightAt(double splitPoint, Where where)
	{
		double zStart = 0.0;
		double zEnd = 0.0;
		double tanStart = 0.0;
		double tanEnd = 0.0;

		switch (where)
		{
		case LEFT:
			zStart = getCalculatedHeightStartLeft();
			zEnd = getCalculatedHeightEndLeft();
			tanStart = getCalculatedStartTangentLeft() / 100.0;
			tanEnd = getCalculatedEndTangentLeft() / 100.0;
			break;
		case CENTER:
			zStart = getCalculatedHeightStart();
			zEnd = getCalculatedHeightEnd();
			tanStart = getCalculatedStartTangent() / 100.0;
			tanEnd = getCalculatedEndTangent() / 100.0;
			break;
		case RIGHT:
			zStart = getCalculatedHeightStartRight();
			zEnd = getCalculatedHeightEndRight();
			tanStart = getCalculatedStartTangentRight() / 100.0;
			tanEnd = getCalculatedEndTangentRight() / 100.0;
			break;
		}

		// the spline's tangents are slopes times the segment length
		return trackSpline(zStart, zEnd, tanStart * length, tanEnd * length, splitPoint);
	}

	public double getTangentAt(double splitPoint, Where where)
	{
		double t2 = splitPoint * splitPoint;
		double zStart = 0.0;
		double zEnd = 0.0;
		double tanStart = 0.0;
		double tanEnd = 0.0;

		switch (where)
		{
		case LEFT:
			zStart = getCalculatedHeightStartLeft();
			zEnd = getCalculatedHeightEndLeft();
			tanStart = getCalculatedStartTangentLeft() / 100.0;
			tanEnd = getCalculatedEndTangentLeft() / 100.0;
			break;
		case CENTER:
			zStart = getCalculatedHeightStart();
			zEnd = getCalculatedHeightEnd();
			tanStart = getCalculatedStartTangent() / 100.0;
			tanEnd = getCalculatedEndTangent() / 100.0;
			break;
		case RIGHT:
			zStart = getCalculatedHeightStartRight();
			zEnd = getCalculatedHeightEndRight();
			tanStart = getCalculatedStartTangentRight() / 100.0;
			tanEnd = getCalculatedEndTangentRight() / 100.0;
			break;
		}

		return 100 * (3 * t2 * (2 * zStart - 2 * zEnd + tanStart * length + tanEnd * length) + 2 * splitPoint * (-3 * zStart + 3 * zEnd - 2 * tanStart * length - tanEnd * length) + (tanStart * length)) / length;
	}

	public void dump(String indent)
	{
		dump(indent, true, true, true, true);
	}

	public void dump(String indent, boolean dumpCalculated, boolean dumpPoints, boolean dumpTrPoints, boolean dumpToDraw)
	{
		dump(System.out, indent, dumpCalculated, dumpPoints, dumpTrPoints, dumpToDraw);
	}

	public void dump(PrintStream printStream, String indent, boolean dumpCalculated, boolean dumpPoints, boolean dumpTrPoints, boolean dumpToDraw)
	{
		printStream.println(indent + "  previousShape               : " + (previousShape != null ? previousShape.name : "null"));
		printStream.println(indent + "  nextShape                   : " + (nextShape != null ? nextShape.name : "null"));
		printStream.println(indent + "  name                        : " + name);
		printStream.println(indent + "  type                        : " + type);
		printStream.println(indent + "  comment                     : " + comment);
		printStream.println(indent + "  count                       : " + count);
		printStream.println(indent + "  length                      : " + length);
		printStream.println(indent + "  startTrackAlpha             : " + startTrackAlpha);
		printStream.println(indent + "  endTrackAlpha               : " + endTrackAlpha);
		printStream.println(indent + "  startTrackCenter            : " + startTrackCenter.x + " " + startTrackCenter.y);
		printStream.println(indent + "  endTrackCenter              : " + endTrackCenter.x + " " + endTrackCenter.y);
		printStream.println(indent + "  distFromCircuitStart        : " + distFromCircuitStart);
		printStream.println(indent + "  nbSteps                     : " + nbSteps);
		printStream.println(indent + "  stepLength                  : " + stepLength);
		printStream.println(indent + "  surface                     : " + surface);
		printStream.println(indent + "  heightStart                 : " + heightStart);
		printStream.println(indent + "  heightStartLeft             : " + heightStartLeft);
		printStream.println(indent + "  heightStartRight            : " + heightStartRight);
		printStream.println(indent + "  heightEnd                   : " + heightEnd);
		printStream.println(indent + "  heightEndLeft               : " + heightEndLeft);
		printStream.println(indent + "  heightEndRight              : " + heightEndRight);
		printStream.println(indent + "  grade                       : " + grade);
		printStream.println(indent + "  bankingStart                : " + bankingStart);
		printStream.println(indent + "  bankingEnd                  : " + bankingEnd);
		printStream.println(indent + "  profil                      : " + profil);
		printStream.println(indent + "  profilSteps                 : " + profilSteps);
		printStream.println(indent + "  profilStepsLength           : " + profilStepsLength);
		printStream.println(indent + "  profilStartTangent          : " + profilStartTangent);
		printStream.println(indent + "  profilEndTangent            : " + profilEndTangent);
		printStream.println(indent + "  profilStartTangentLeft      : " + profilStartTangentLeft);
		printStream.println(indent + "  profilEndTangentLeft        : " + profilEndTangentLeft);
		printStream.println(indent + "  profilStartTangentRight     : " + profilStartTangentRight);
		printStream.println(indent + "  profilEndTangentRight       : " + profilEndTangentRight);
		if (dumpCalculated)
		{
			printStream.println(indent + "  calculatedHeightStart       : " + calculatedHeightStart);
			printStream.println(indent + "  calculatedHeightStartLeft   : " + calculatedHeightStartLeft);
			printStream.println(indent + "  calculatedHeightStartRight  : " + calculatedHeightStartRight);
			printStream.println(indent + "  calculatedHeightEnd         : " + calculatedHeightEndLeft);
			printStream.println(indent + "  calculatedHeightEndLeft     : " + calculatedHeightEndLeft);
			printStream.println(indent + "  calculatedHeightEndRight    : " + calculatedHeightEndRight);
			printStream.println(indent + "  calculatedGrade             : " + calculatedGrade);
			printStream.println(indent + "  calculatedBankingStart      : " + calculatedBankingStart);
			printStream.println(indent + "  calculatedBankingEnd        : " + calculatedBankingEnd);
			printStream.println(indent + "  calculatedStartTangent      : " + calculatedStartTangent);
			printStream.println(indent + "  calculatedStartTangentLeft  : " + calculatedStartTangentLeft);
			printStream.println(indent + "  calculatedStartTangentRight : " + calculatedStartTangentRight);
			printStream.println(indent + "  calculatedEndTangent        : " + calculatedEndTangent);
			printStream.println(indent + "  calculatedEndTangentLeft    : " + calculatedEndTangentLeft);
			printStream.println(indent + "  calculatedEndTangentRight   : " + calculatedEndTangentRight);
		}

		if (points != null)
		{
			printStream.println(indent + "  points                      : " + points.length);
			if (dumpPoints)
			{
				for (int i = 0; i < points.length; i++)
				{
					printStream.println(indent + "    points[" + i + "] " +
										String.format("%12.7f", points[i].x) + ", " +
										String.format("%12.7f", points[i].y) + ", " +
										String.format("%12.7f", points[i].z));
				}
			}
		}
		else
		{
			printStream.println(indent + "  points                      : null");
		}
		if (trPoints != null)
		{
			printStream.println(indent + "  trPoints                    : " + trPoints.length);
			if (dumpTrPoints)
			{
				for (int i = 0; i < trPoints.length; i++)
				{
					printStream.println(indent + "    trPoints[" + i + "] " + trPoints[i].x + ", " + trPoints[i].y);
				}
			}
		}
		else
		{
			printStream.println(indent + "  trPoints                    : null");
		}
		if (dumpToDraw)
		{
			if (xToDraw != null)
			{
				printStream.println(indent + "  xToDraw                     : " + xToDraw.length);
				for (int i = 0; i < xToDraw.length; i++)
				{
					printStream.println(indent + "    xToDraw[" + i + "] " + xToDraw[i]);
				}
			}
			else
			{
				printStream.println(indent + "  xToDraw                     : null");
			}
			if (yToDraw != null)
			{
				printStream.println(indent + "  yToDraw                     : " + yToDraw.length);
				for (int i = 0; i < yToDraw.length; i++)
				{
					printStream.println(indent + "    yToDraw[" + i + "] " + yToDraw[i]);
				}
			}
			else
			{
				printStream.println(indent + "  yToDraw                     : null");
			}
		}
		printStream.println(indent + "  dx                          : " + dx);
		printStream.println(indent + "  dy                          : " + dy);
	}
}
