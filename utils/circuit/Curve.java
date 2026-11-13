/*
 *   Curve.java
 *   Created on 9 ??? 2005
 *
 *    The Curve.java is part of TrackEditor-0.6.0.
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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.PrintStream;

import utils.Properties;
import utils.check.TrackContext;

import miscel.EPMath;

/**
 * @author Patrice Espie , Charalampos Alexopoulos
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class Curve extends Segment
{
	protected double		arcDeg;
	protected double		radiusStart;
	// NaN when the curve has no end radius: its radius is then constant
	protected double 		radiusEnd	= Double.NaN;

	public Point2D.Double	center = new Point2D.Double();
	// rotation center of each step, for the bounds and drawing
	private double[]		stepCenters = new double[0];
	// each step's turn in radians, to the left positive: it can be more than half a turn
	private double[]		stepArcs = new double[0];
	// the most degrees of a step drawn as one straight line
	private static final double	DEGREES_PER_PIECE	= 2;
	// the quads of a step that follow its arc: the track, borders, sides and barriers, not the arrow
	private static final int	ARC_QUADS			= 7;

	protected String		marks;

	public Curve()
	{
		this("rgt",null);
	}

	public Curve(String type, Segment prev)
	{
		super(type);
		this.previousShape = prev;
	}

	@Override
	public void set(Segment segment)
	{
		super.set(segment);
		Curve curve = (Curve) segment;
		arcDeg = curve.arcDeg;
		radiusStart = curve.radiusStart;
		radiusEnd = curve.radiusEnd;
		marks = curve.marks;
	}

	public Segment copyTo(Segment _shape)
	{
		super.copyTo(_shape);

		Curve shape = (Curve) _shape;

		return shape;
	}

	public boolean contains(Point2D.Double point)
	{
		// barriers count too
		if (!selectionRectangle.contains(point.x, point.y))
			return false;

		if (super.contains(point))
			return true;
		// and the bulge of each step's arc beyond the straight line between its ends
		if (!hasStepArcs())
			return false;
		int stride = points.length / nbSteps;
		for (int i = 0; i < points.length; i += 4)
		{
			if ((i % stride) / 4 < ARC_QUADS && quadPath(i, i / stride).contains(point.x, point.y))
				return true;
		}
		return false;
	}

	/** @return whether the steps' centers and arcs are known for the points */
	private boolean hasStepArcs()
	{
		return points != null && nbSteps > 0 && points.length % nbSteps == 0
			&& stepCenters.length >= 2 * nbSteps && stepArcs.length >= nbSteps;
	}

	/**
	 * Draws each step as the game builds it: its long sides are arcs around the
	 * step's center (or spirals where a side's width changes), not the straight
	 * lines between its ends, which a step of a whole curve would show as a chord.
	 */
	@Override
	public void draw(Graphics g, AffineTransform affineTransform, boolean roadOnly)
	{
		if (!hasStepArcs() || !(g instanceof Graphics2D))
		{
			super.draw(g, affineTransform, roadOnly);
			return;
		}
		affineTransform.transform(points, 0, trPoints, 0, points.length);
		Graphics2D g2 = (Graphics2D) g;
		Rectangle clipBound = g.getClipBounds();
		int stride = points.length / nbSteps;
		for (int i = 0; i < points.length; i += 4)
		{
			if (!drawsQuad(i, roadOnly))
				continue;
			Shape shape = affineTransform.createTransformedShape((i % stride) / 4 < ARC_QUADS ? quadPath(i, i / stride) : quadPath(i, -1));
			if (clipBound == null || shape.getBounds().intersects(clipBound))
				g2.draw(shape);
		}
	}

	/**
	 * @return the quad of points from first: start, end, end, start, with the
	 *         lines along the step following its arc, or straight when step is -1
	 */
	private Path2D.Double quadPath(int first, int step)
	{
		Path2D.Double path = new Path2D.Double();
		path.moveTo(points[first].x, points[first].y);
		if (step < 0)
		{
			for (int j = 1; j < 4; j++)
				path.lineTo(points[first + j].x, points[first + j].y);
		}
		else
		{
			arcTo(path, step, points[first], points[first + 1], false);
			path.lineTo(points[first + 2].x, points[first + 2].y);
			arcTo(path, step, points[first + 3], points[first + 2], true);
		}
		path.closePath();
		return path;
	}

	/** Adds the step's arc from start to end, or back from end to start, without its first point */
	private void arcTo(Path2D.Double path, int step, Point3D start, Point3D end, boolean back)
	{
		double cx = stepCenters[2 * step], cy = stepCenters[2 * step + 1];
		double arc = stepArcs[step];
		double r0 = Math.hypot(start.x - cx, start.y - cy), r1 = Math.hypot(end.x - cx, end.y - cy);
		int pieces = (int) Math.min(3600, Math.max(1, Math.ceil(Math.abs(Math.toDegrees(arc)) / DEGREES_PER_PIECE)));
		if (pieces == 1 || r0 < 1e-9 || r1 < 1e-9)
		{
			Point3D to = back ? start : end;
			path.lineTo(to.x, to.y);
			return;
		}
		double a0 = Math.atan2(start.y - cy, start.x - cx);
		for (int k = 1; k <= pieces; k++)
		{
			double t = back ? 1 - (double) k / pieces : (double) k / pieces;
			if (k == pieces)
			{
				// exactly the end point
				Point3D to = back ? start : end;
				path.lineTo(to.x, to.y);
				break;
			}
			double a = a0 + arc * t, r = r0 + (r1 - r0) * t;
			path.lineTo(cx + r * Math.cos(a), cy + r * Math.sin(a));
		}
	}

	protected void setBounds()
	{
		if (points == null || points.length == 0)
			return;

		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;

		// like the game's track loader: the track, borders and sides (not the
		// barriers), following each step's arc in 36 pieces so the bulge of the
		// curve between step ends counts; reliefs and object maps are placed
		// relative to this box
		int stride = points.length / nbSteps;
		int[][] edges = { { TRACK_START_LEFT, TRACK_END_LEFT }, { TRACK_START_RIGHT, TRACK_END_RIGHT },
						  { LEFT_BORDER_START_LEFT, LEFT_BORDER_END_LEFT }, { LEFT_BORDER_START_RIGHT, LEFT_BORDER_END_RIGHT },
						  { LEFT_SIDE_START_LEFT, LEFT_SIDE_END_LEFT }, { LEFT_SIDE_START_RIGHT, LEFT_SIDE_END_RIGHT },
						  { RIGHT_BORDER_START_RIGHT, RIGHT_BORDER_END_RIGHT }, { RIGHT_BORDER_START_LEFT, RIGHT_BORDER_END_LEFT },
						  { RIGHT_SIDE_START_RIGHT, RIGHT_SIDE_END_RIGHT }, { RIGHT_SIDE_START_LEFT, RIGHT_SIDE_END_LEFT } };

		for (int n = 0; n < nbSteps; n++)
		{
			int base = n * stride;
			boolean haveCenter = stepCenters.length >= 2 * (n + 1);
			double cx = haveCenter ? stepCenters[2 * n] : 0;
			double cy = haveCenter ? stepCenters[2 * n + 1] : 0;

			for (int[] edge : edges)
			{
				Point3D start = points[base + edge[0]];
				Point3D end = points[base + edge[1]];

				minX = Math.min(minX, Math.min(start.x, end.x));
				maxX = Math.max(maxX, Math.max(start.x, end.x));
				minY = Math.min(minY, Math.min(start.y, end.y));
				maxY = Math.max(maxY, Math.max(start.y, end.y));

				if (!haveCenter)
					continue;

				double radius = Math.hypot(start.x - cx, start.y - cy);
				double a0 = Math.atan2(start.y - cy, start.x - cx);
				double da = Math.atan2(end.y - cy, end.x - cx) - a0;
				while (da > Math.PI)
					da -= 2 * Math.PI;
				while (da < -Math.PI)
					da += 2 * Math.PI;
				// a step of more than half a turn
				if (stepArcs.length > n)
					da = stepArcs[n];

				for (int j = 1; j < 36; j++)
				{
					double a = a0 + da * j / 36;
					double x = cx + radius * Math.cos(a);
					double y = cy + radius * Math.sin(a);
					minX = Math.min(minX, x);
					maxX = Math.max(maxX, x);
					minY = Math.min(minY, y);
					maxY = Math.max(maxY, y);
				}
			}
		}

		boundingRectangle.setRect(minX, minY, maxX - minX, maxY - minY);
		setSelectionBounds();
	}

	public void calcShape(TrackContext trackContext) throws Exception
	{
		double	currentX	= Properties.getInstance().getCurrentX();
		double	currentY	= Properties.getInstance().getCurrentY();
		double	currentA	= Properties.getInstance().getCurrentA();
		double 	showArrows = Properties.getInstance().getShowArrows();
		double	trackStartDist = Properties.getInstance().getTrackStartDist();
		double	trackWidth = trackContext.getTrackData().getMainTrack().getValidWidth();
		double	leftBorderWidth = getValidLeftBorderWidth(trackContext);
		double	rightBorderWidth = getValidRightBorderWidth(trackContext);
		double	leftSideStartWidth = getValidLeftSideStartWidth(trackContext);
		double	leftSideEndWidth = getValidLeftSideEndWidth(trackContext);
		double	rightSideStartWidth = getValidRightSideStartWidth(trackContext);
		double	rightSideEndWidth = getValidRightSideEndWidth(trackContext);
		double	leftBarrierWidth = getValidLeftBarrierWidth(trackContext);
		double	rightBarrierWidth = getValidRightBarrierWidth(trackContext);
		double	leftStartHeight = this.getCalculatedHeightStartLeft();
		double	rightStartHeight = this.getCalculatedHeightStartRight();
		double	leftEndHeight = this.getCalculatedHeightEndLeft();
		double	rightEndHeight = this.getCalculatedHeightEndRight();

		// calc turn length
		double arc = getArcRad();
		double endRadius = getValidRadiusEnd();
		length = arc * (radiusStart + endRadius) / 2;
		// same step count as the game's track loader
		nbSteps = getBuiltSteps(trackContext, length);

		trackStartDist += length;
		stepLength = length / nbSteps;

		double deltaRadiusStep;

		if (endRadius != radiusStart)
		{
			if (nbSteps != 1)
			{
				deltaRadiusStep = (endRadius - radiusStart) / (nbSteps - 1);
				double tmpAngle = 0;
				double tmpRadius = radiusStart;
				for (int curStep = 0; curStep < nbSteps; curStep++)
				{
					tmpAngle += stepLength / tmpRadius;
					tmpRadius += deltaRadiusStep;
				}
				stepLength *= arc / tmpAngle;
			}
			else
			{
				deltaRadiusStep = (endRadius - radiusStart) / nbSteps;
			}
		}
		else
		{
			deltaRadiusStep = 0;
		}

		if (points == null || points.length != 4 * (7 + (showArrows > 0.0 ? 1 : 0)) * nbSteps)
		{
			points = new Point3D[4 * (7 + (showArrows > 0.0 ? 1 : 0)) * nbSteps];

			for (int i = 0; i < points.length; i++)
				points[i] = new Point3D();

			trPoints = new Point2D.Double[4 * (7 + (showArrows > 0.0 ? 1 : 0)) * nbSteps];
			for (int i = 0; i < trPoints.length; i++)
				trPoints[i] = new Point2D.Double();
		}

		boolean dir = type.equals("rgt");

		double curRadius = radiusStart;

		double leftSideDeltaStep = (leftSideEndWidth - leftSideStartWidth) / nbSteps;
		double rightSideDeltaStep = (rightSideEndWidth - rightSideStartWidth) / nbSteps;

		double leftHeightDeltaStep = (leftEndHeight - leftStartHeight) / nbSteps;
		double rightHeightDeltaStep = (rightEndHeight - rightStartHeight) / nbSteps;

		boolean linear = getValidProfil(trackContext).equals("linear");

		double T1l = (getCalculatedStartTangentLeft() / 100.0) * getLength();
		double T2l = (getCalculatedEndTangentLeft() / 100.0) * getLength();
		double tl = 0.0;
		double dtl = 1.0 / nbSteps;
		double T1r = (getCalculatedStartTangentRight() / 100.0) * getLength();
		double T2r = (getCalculatedEndTangentRight() / 100.0) * getLength();
		double tr = 0.0;
		double dtr = 1.0 / nbSteps;
		double curzsl = leftStartHeight;
		double curzsr = rightStartHeight;
		double curzel = leftStartHeight;
		double curzer = rightStartHeight;

		int currentSubSeg = 0;

		for (int nStep = 0; nStep < nbSteps; nStep++)
		{
			double cosTrans = Math.cos(currentA + Math.PI / 2);
			double sinTrans = Math.sin(currentA + Math.PI / 2);

			double thisStepArc;
			double xCenter;
			double yCenter;

			if (dir)
			{
				xCenter = currentX - cosTrans * curRadius;
				yCenter = currentY - sinTrans * curRadius;
				thisStepArc = -stepLength / curRadius;
			}
			else
			{
				xCenter = currentX + cosTrans * curRadius;
				yCenter = currentY + sinTrans * curRadius;
				thisStepArc = stepLength / curRadius;
			}

			if (nStep == 0)
			{
				center.setLocation(xCenter, yCenter);
				if (stepCenters.length != 2 * nbSteps)
					stepCenters = new double[2 * nbSteps];
			}
			stepCenters[2 * nStep] = xCenter;
			stepCenters[2 * nStep + 1] = yCenter;
			if (stepArcs.length != nbSteps)
				stepArcs = new double[nbSteps];
			stepArcs[nStep] = thisStepArc;

			double cos = Math.cos(thisStepArc);
			double sin = Math.sin(thisStepArc);

			// update radiusStart
			curRadius += deltaRadiusStep;

			double cosTransLeft = cosTrans;
			double sinTransLeft = sinTrans;

			// track
			points[currentSubSeg + TRACK_START_LEFT].x = currentX + cosTransLeft * trackWidth / 2;
			points[currentSubSeg + TRACK_START_LEFT].y = currentY + sinTransLeft * trackWidth / 2;

			double x = points[currentSubSeg + TRACK_START_LEFT].x - xCenter;
			double y = points[currentSubSeg + TRACK_START_LEFT].y - yCenter;
			points[currentSubSeg + TRACK_END_LEFT].x = x * cos - y * sin + xCenter;
			points[currentSubSeg + TRACK_END_LEFT].y = y * cos + x * sin + yCenter;

			points[currentSubSeg + TRACK_START_RIGHT].x = currentX - cosTransLeft * trackWidth / 2;
			points[currentSubSeg + TRACK_START_RIGHT].y = currentY - sinTransLeft * trackWidth / 2;

			x = points[currentSubSeg + TRACK_START_RIGHT].x - xCenter;
			y = points[currentSubSeg + TRACK_START_RIGHT].y - yCenter;
			points[currentSubSeg + TRACK_END_RIGHT].x = x * cos - y * sin + xCenter;
			points[currentSubSeg + TRACK_END_RIGHT].y = y * cos + x * sin + yCenter;

			if (linear)
			{
				points[currentSubSeg + TRACK_START_LEFT].z = leftStartHeight + leftHeightDeltaStep * nStep;
				points[currentSubSeg + TRACK_END_LEFT].z = leftStartHeight + leftHeightDeltaStep * (nStep + 1);
				points[currentSubSeg + TRACK_END_RIGHT].z = rightStartHeight + rightHeightDeltaStep * (nStep + 1);
				points[currentSubSeg + TRACK_START_RIGHT].z = rightStartHeight + rightHeightDeltaStep * nStep;
			}
			else
			{
				tl += dtl;
				tr += dtr;

				curzsl = curzel;
				curzel = trackSpline(leftStartHeight, leftEndHeight, T1l, T2l, tl);

				curzsr = curzer;
				curzer = trackSpline(rightStartHeight, rightEndHeight, T1r, T2r, tr);

				points[currentSubSeg + TRACK_START_LEFT].z = curzsl;
				points[currentSubSeg + TRACK_END_LEFT].z = curzel;
				points[currentSubSeg + TRACK_END_RIGHT].z = curzer;
				points[currentSubSeg + TRACK_START_RIGHT].z = curzsr;
			}

			// left border

			points[currentSubSeg + LEFT_BORDER_START_LEFT].x = currentX + cosTransLeft * (trackWidth / 2 + leftBorderWidth);
			points[currentSubSeg + LEFT_BORDER_START_LEFT].y = currentY + sinTransLeft * (trackWidth / 2 + leftBorderWidth);

			x = points[currentSubSeg + LEFT_BORDER_START_LEFT].x - xCenter;
			y = points[currentSubSeg + LEFT_BORDER_START_LEFT].y - yCenter;
			points[currentSubSeg + LEFT_BORDER_END_LEFT].x = x * cos - y * sin + xCenter;
			points[currentSubSeg + LEFT_BORDER_END_LEFT].y = y * cos + x * sin + yCenter;

			points[currentSubSeg + LEFT_BORDER_START_RIGHT].x = points[currentSubSeg + TRACK_START_LEFT].x;
			points[currentSubSeg + LEFT_BORDER_START_RIGHT].y = points[currentSubSeg + TRACK_START_LEFT].y;

			points[currentSubSeg + LEFT_BORDER_END_RIGHT].x = points[currentSubSeg + TRACK_END_LEFT].x;
			points[currentSubSeg + LEFT_BORDER_END_RIGHT].y = points[currentSubSeg + TRACK_END_LEFT].y;

			// left side

			points[currentSubSeg + LEFT_SIDE_START_LEFT].x = currentX
					+ cosTransLeft
					* (trackWidth / 2 + leftBorderWidth + leftSideStartWidth + leftSideDeltaStep
							* nStep);
			points[currentSubSeg + LEFT_SIDE_START_LEFT].y = currentY
					+ sinTransLeft
					* (trackWidth / 2 + leftBorderWidth + leftSideStartWidth + leftSideDeltaStep
							* nStep);

			x = points[currentSubSeg + LEFT_SIDE_START_LEFT].x + cosTransLeft * leftSideDeltaStep - xCenter;
			y = points[currentSubSeg + LEFT_SIDE_START_LEFT].y + sinTransLeft * leftSideDeltaStep - yCenter;
			points[currentSubSeg + LEFT_SIDE_END_LEFT].x = x * cos - y * sin + xCenter;
			points[currentSubSeg + LEFT_SIDE_END_LEFT].y = y * cos + x * sin + yCenter;

			points[currentSubSeg + LEFT_SIDE_END_RIGHT].x = points[currentSubSeg + LEFT_BORDER_END_LEFT].x;
			points[currentSubSeg + LEFT_SIDE_END_RIGHT].y = points[currentSubSeg + LEFT_BORDER_END_LEFT].y;

			points[currentSubSeg + LEFT_SIDE_START_RIGHT].x = points[currentSubSeg + LEFT_BORDER_START_LEFT].x;
			points[currentSubSeg + LEFT_SIDE_START_RIGHT].y = points[currentSubSeg + LEFT_BORDER_START_LEFT].y;

			// left barrier

			points[currentSubSeg + LEFT_BARRIER_START_LEFT].x = currentX + cosTransLeft
					* ((trackWidth / 2) + leftBorderWidth + leftSideStartWidth + leftBarrierWidth + (leftSideDeltaStep * nStep));
			points[currentSubSeg + LEFT_BARRIER_START_LEFT].y = currentY + sinTransLeft
					* ((trackWidth / 2) + leftBorderWidth + leftSideStartWidth + leftBarrierWidth + (leftSideDeltaStep * nStep));

			x = points[currentSubSeg + LEFT_BARRIER_START_LEFT].x + cosTransLeft * leftSideDeltaStep - xCenter;
			y = points[currentSubSeg + LEFT_BARRIER_START_LEFT].y + sinTransLeft * leftSideDeltaStep - yCenter;
			points[currentSubSeg + LEFT_BARRIER_END_LEFT].x = x * cos - y * sin + xCenter;
			points[currentSubSeg + LEFT_BARRIER_END_LEFT].y = y * cos + x * sin + yCenter;

			points[currentSubSeg + LEFT_BARRIER_END_RIGHT].x = points[currentSubSeg + LEFT_SIDE_END_LEFT].x;
			points[currentSubSeg + LEFT_BARRIER_END_RIGHT].y = points[currentSubSeg + LEFT_SIDE_END_LEFT].y;

			points[currentSubSeg + LEFT_BARRIER_START_RIGHT].x = points[currentSubSeg + LEFT_SIDE_START_LEFT].x;
			points[currentSubSeg + LEFT_BARRIER_START_RIGHT].y = points[currentSubSeg + LEFT_SIDE_START_LEFT].y;

			// right border

			points[currentSubSeg + RIGHT_BORDER_START_RIGHT].x = currentX - cosTransLeft * (trackWidth / 2 + rightBorderWidth);
			points[currentSubSeg + RIGHT_BORDER_START_RIGHT].y = currentY - sinTransLeft * (trackWidth / 2 + rightBorderWidth);

			x = points[currentSubSeg + RIGHT_BORDER_START_RIGHT].x - xCenter;
			y = points[currentSubSeg + RIGHT_BORDER_START_RIGHT].y - yCenter;
			points[currentSubSeg + RIGHT_BORDER_END_RIGHT].x = x * cos - y * sin + xCenter;
			points[currentSubSeg + RIGHT_BORDER_END_RIGHT].y = y * cos + x * sin + yCenter;

			points[currentSubSeg + RIGHT_BORDER_END_LEFT].x = points[currentSubSeg + TRACK_END_RIGHT].x;
			points[currentSubSeg + RIGHT_BORDER_END_LEFT].y = points[currentSubSeg + TRACK_END_RIGHT].y;

			points[currentSubSeg + RIGHT_BORDER_START_LEFT].x = points[currentSubSeg + TRACK_START_RIGHT].x;
			points[currentSubSeg + RIGHT_BORDER_START_LEFT].y = points[currentSubSeg + TRACK_START_RIGHT].y;

			// right side

			points[currentSubSeg + RIGHT_SIDE_START_RIGHT].x = currentX
					- cosTransLeft
					* (trackWidth / 2 + rightBorderWidth + rightSideStartWidth + rightSideDeltaStep
							* nStep);
			points[currentSubSeg + RIGHT_SIDE_START_RIGHT].y = currentY
					- sinTransLeft
					* (trackWidth / 2 + rightBorderWidth + rightSideStartWidth + rightSideDeltaStep
							* nStep);

			x = points[currentSubSeg + RIGHT_SIDE_START_RIGHT].x - cosTransLeft * rightSideDeltaStep - xCenter;
			y = points[currentSubSeg + RIGHT_SIDE_START_RIGHT].y - sinTransLeft * rightSideDeltaStep - yCenter;
			points[currentSubSeg + RIGHT_SIDE_END_RIGHT].x = x * cos - y * sin + xCenter;
			points[currentSubSeg + RIGHT_SIDE_END_RIGHT].y = y * cos + x * sin + yCenter;

			points[currentSubSeg + RIGHT_SIDE_END_LEFT].x = points[currentSubSeg + RIGHT_BORDER_END_RIGHT].x;
			points[currentSubSeg + RIGHT_SIDE_END_LEFT].y = points[currentSubSeg + RIGHT_BORDER_END_RIGHT].y;

			points[currentSubSeg + RIGHT_SIDE_START_LEFT].x = points[currentSubSeg + RIGHT_BORDER_START_RIGHT].x;
			points[currentSubSeg + RIGHT_SIDE_START_LEFT].y = points[currentSubSeg + RIGHT_BORDER_START_RIGHT].y;

			// right barrier

			points[currentSubSeg + RIGHT_BARRIER_START_RIGHT].x = currentX - cosTransLeft
					* ((trackWidth / 2) + rightBorderWidth + rightSideStartWidth + rightBarrierWidth + (rightSideDeltaStep * nStep));
			points[currentSubSeg + RIGHT_BARRIER_START_RIGHT].y = currentY - sinTransLeft
					* (trackWidth / 2 + rightBorderWidth + rightSideStartWidth + rightBarrierWidth + (rightSideDeltaStep * nStep));

			x = points[currentSubSeg + RIGHT_BARRIER_START_RIGHT].x - cosTransLeft * rightSideDeltaStep - xCenter;
			y = points[currentSubSeg + RIGHT_BARRIER_START_RIGHT].y - sinTransLeft * rightSideDeltaStep - yCenter;
			points[currentSubSeg + RIGHT_BARRIER_END_RIGHT].x = x * cos - y * sin + xCenter;
			points[currentSubSeg + RIGHT_BARRIER_END_RIGHT].y = y * cos + x * sin + yCenter;

			points[currentSubSeg + RIGHT_BARRIER_END_LEFT].x = points[currentSubSeg + RIGHT_SIDE_END_RIGHT].x;
			points[currentSubSeg + RIGHT_BARRIER_END_LEFT].y = points[currentSubSeg + RIGHT_SIDE_END_RIGHT].y;

			points[currentSubSeg + RIGHT_BARRIER_START_LEFT].x = points[currentSubSeg + RIGHT_SIDE_START_RIGHT].x;
			points[currentSubSeg + RIGHT_BARRIER_START_LEFT].y = points[currentSubSeg + RIGHT_SIDE_START_RIGHT].y;

			if (showArrows > 0.0)
			{
				// arrow
				points[currentSubSeg + ARROW_START_LEFT].x = points[currentSubSeg + TRACK_START_LEFT].x;
				points[currentSubSeg + ARROW_START_LEFT].y = points[currentSubSeg + TRACK_START_LEFT].y;

				x = points[currentSubSeg + ARROW_START_LEFT].x - xCenter - (cosTransLeft * trackWidth / 2) * 0.99999;
				y = points[currentSubSeg + ARROW_START_LEFT].y - yCenter - (sinTransLeft * trackWidth / 2) * 0.99999;
				points[currentSubSeg + ARROW_END_LEFT].x = x * cos - y * sin + xCenter;
				points[currentSubSeg + ARROW_END_LEFT].y = y * cos + x * sin + yCenter;

				points[currentSubSeg + ARROW_START_RIGHT].x = points[currentSubSeg + TRACK_START_RIGHT].x;
				points[currentSubSeg + ARROW_START_RIGHT].y = points[currentSubSeg + TRACK_START_RIGHT].y;

				x = points[currentSubSeg + ARROW_START_RIGHT].x - xCenter + (cosTransLeft * trackWidth / 2) * 0.99999;
				y = points[currentSubSeg + ARROW_START_RIGHT].y - yCenter + (sinTransLeft * trackWidth / 2) * 0.99999;
				points[currentSubSeg + ARROW_END_RIGHT].x = x * cos - y * sin + xCenter;
				points[currentSubSeg + ARROW_END_RIGHT].y = y * cos + x * sin + yCenter;

				currentSubSeg += 32;
			}
			else
			{
				currentSubSeg += 28;
			}

			// move track center

			x = currentX - xCenter;
			y = currentY - yCenter;
			currentX = x * cos - y * sin + xCenter;
			currentY = y * cos + x * sin + yCenter;

			// inc track angle
			currentA += thisStepArc;
		}
		/*
		 * // return along the X axis for ( int i = 0; i < points.length; i++ )
		 * points[ i ].y = -points[ i ].y;
		 */
		//        endTrackCenter.setLocation( datas[ 0 ], -datas[ 1 ] );
		endTrackCenter.setLocation(currentX, currentY);
		endTrackAlpha = currentA % EPMath.PI_MUL_2;
		while (endTrackAlpha < -Math.PI)
			endTrackAlpha += EPMath.PI_MUL_2;
		while (endTrackAlpha > Math.PI)
			endTrackAlpha -= EPMath.PI_MUL_2;

		setBounds();

		Properties.getInstance().setCurrentA(currentA);
		Properties.getInstance().setCurrentX(currentX);
		Properties.getInstance().setCurrentY(currentY);
	}

	public void drag(Point2D.Double dragDelta)
	{
	}

	/**
	 * @return Returns the arc.
	 */
	public double getArcRad()
	{
		return Math.toRadians(arcDeg);
	}
	/**
	 * @param arc The arc to set.
	 */
	public void setArcRad(double arcRad)
	{
		this.arcDeg = Math.toDegrees(arcRad);
	}
	/**
	 * @return Returns the arc.
	 */
	public double getArcDeg()
	{
		return arcDeg;
	}
	/**
	 * @param arc The arc to set.
	 */
	public void setArcDeg(double arcDeg)
	{
		this.arcDeg = arcDeg;
	}
	/**
	 * @return Returns the radiusEnd.
	 */
	public double getRadiusEnd()
	{
		return radiusEnd;
	}
	public boolean hasRadiusEnd()
	{
		return !Double.isNaN(radiusEnd);
	}

	/**
	 * The end radius the game uses: a curve without one has a constant radius,
	 * so it ends at the radius it starts at. This is not inherited.
	 */
	public double getValidRadiusEnd()
	{
		return hasRadiusEnd() ? radiusEnd : radiusStart;
	}

	/**
	 * @param radiusEnd The radiusEnd to set.
	 */
	public void setRadiusEnd(double radiusEnd)
	{
		this.radiusEnd = radiusEnd;
	}
	/**
	 * @return Returns the radiusStart.
	 */
	public double getRadiusStart()
	{
		return radiusStart;
	}
	/**
	 * @param radiusStart The radiusStart to set.
	 */
	public void setRadiusStart(double radiusStart)
	{
		this.radiusStart = radiusStart;
	}
	public String getMarks()
	{
		return marks;
	}
	/**
	 * @param arc The arc to set.
	 */
	public void setMarks(String marks)
	{
		this.marks = marks;
	}

	public Object clone()
	{
		Curve s;
		s = (Curve) super.clone();
		s.arcDeg = this.arcDeg;
		s.radiusStart = this.radiusStart;
		s.radiusEnd = this.radiusEnd;
		s.marks = this.marks;
		s.stepCenters = this.stepCenters.clone();
		s.stepArcs = this.stepArcs.clone();
		s.center = (Point2D.Double) this.center.clone();

		return s; // return the clone
	}

	public void dump(PrintStream printStream, String indent, boolean dumpCalculated, boolean dumpPoints, boolean dumpTrPoints, boolean dumpToDraw)
	{
		printStream.println(indent + "Curve");
		printStream.println(indent + "  arcDeg                      : " + arcDeg);
		printStream.println(indent + "  radiusStart                 : " + radiusStart);
		printStream.println(indent + "  radiusEnd                   : " + radiusEnd);
		printStream.println(indent + "  center                      : " + center.x + ", " + center.y);
		printStream.println(indent + "  marks                       : " + marks);
		super.dump(printStream, indent, dumpCalculated, dumpPoints, dumpTrPoints, dumpToDraw);
	}
}
