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

import java.awt.geom.Point2D;
import java.io.PrintStream;

import gui.EditorFrame;
import utils.Editor;

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
	protected double 		radiusEnd;

	public Point2D.Double	center = new Point2D.Double();

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
	}

	public Segment copyTo(Segment _shape)
	{
		super.copyTo(_shape);

		Curve shape = (Curve) _shape;

		return shape;
	}

	public boolean contains(Point2D.Double point)
	{
		if (!boundingRectangle.contains(point.x, point.y))
			return false;

		return super.contains(point);
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
			// don't use barrier points
			if ((i >= 12 && i <= 15) || (i >= 24 && i <= 27))
				continue;

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
		double	currentX	= Editor.getProperties().getCurrentX();
		double	currentY	= Editor.getProperties().getCurrentY();
		double	currentA	= Editor.getProperties().getCurrentA();
		double 	showArrows = Editor.getProperties().getShowArrows();
		double	trackStartDist = Editor.getProperties().getTrackStartDist();
		double	profilStepsLength = getValidProfilStepsLength(editorFrame);
		double	trackWidth = editorFrame.getTrackData().getMainTrack().getWidth();
		double	leftBorderWidth = getValidLeftBorderWidth(editorFrame);
		double	rightBorderWidth = getValidRightBorderWidth(editorFrame);
		double	leftSideStartWidth = getValidLeftSideStartWidth(editorFrame);
		double	leftSideEndWidth = getValidLeftSideEndWidth(editorFrame);
		double	rightSideStartWidth = getValidRightSideStartWidth(editorFrame);
		double	rightSideEndWidth = getValidRightSideEndWidth(editorFrame);
		double	leftBarrierWidth = getValidLeftBarrierWidth(editorFrame);
		double	rightBarrierWidth = getValidRightBarrierWidth(editorFrame);
		double	leftStartHeight = this.getCalculatedHeightStartLeft();
		double	rightStartHeight = this.getCalculatedHeightStartRight();
		double	leftEndHeight = this.getCalculatedHeightEndLeft();
		double	rightEndHeight = this.getCalculatedHeightEndRight();

		// calc turn length
		double arc = getArcRad();
		length = arc * (radiusStart + radiusEnd) / 2;
		if (hasProfilSteps())
		{
			nbSteps = getProfilSteps();
		}
		else
		{
			nbSteps = (int) (length / profilStepsLength + 0.5) + 1;
		}

		trackStartDist += length;
		stepLength = length / nbSteps;

		double deltaRadiusStep;

		if (radiusEnd != radiusStart)
		{
			if (nbSteps != 1)
			{
				deltaRadiusStep = (radiusEnd - radiusStart) / (nbSteps - 1);
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
				deltaRadiusStep = (radiusEnd - radiusStart) / nbSteps;
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

		boolean linear = getValidProfil(editorFrame).equals("linear");

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
			}

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

		Editor.getProperties().setCurrentA(currentA);
		Editor.getProperties().setCurrentX(currentX);
		Editor.getProperties().setCurrentY(currentY);
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
