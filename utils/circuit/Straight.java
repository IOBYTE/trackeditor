/*
 *   Straight.java
 *   Created on 9 ??? 2005
 *
 *    The Straight.java is part of TrackEditor-0.6.0.
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


/**
 * @author Patrice Espie , Charalampos Alexopoulos
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

public class Straight extends Segment
{
	public Straight()
	{
		this(null);
	}

	public Straight(Segment prev)
	{
		super("str");
		this.previousShape = prev;
	}

	@Override
	public void set(Segment segment)
	{
		super.set(segment);
		Straight straight = (Straight) segment;
	}

	public boolean contains(Point2D.Double point)
	{
		if (points == null)
			return false;

		// barriers count too
		if (!selectionRectangle.contains(point.x, point.y))
			return false;

		int count = 0;
		int stride = 4 * (7 + (Editor.getProperties().getShowArrows() > 0.0 ? 1 : 0));
		int last = stride * (nbSteps - 1);

		// offset to 4 corners of straight
		int offset[] = { LEFT_BARRIER_START_LEFT,
						 last + LEFT_BARRIER_END_LEFT,
						 last + RIGHT_BARRIER_END_RIGHT,
						 RIGHT_BARRIER_START_RIGHT };

		for (int j = 0; j < 4; j++)
		{
			int start = offset[j];
			int next = offset[(j + 1) % 4];

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

		return count != 0;
	}

	protected void setBounds()
	{
		if (points == null || points.length == 0)
			return;

		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;

		int stride = 4 * (7 + (Editor.getProperties().getShowArrows() > 0.0 ? 1 : 0));
		int last = stride * (nbSteps - 1);

		// like the game's track loader: the track, borders and sides, not the
		// barriers (reliefs and object maps are placed relative to this box)
		int offset[] = { TRACK_START_LEFT, last + TRACK_END_LEFT, last + TRACK_END_RIGHT, TRACK_START_RIGHT,
						 LEFT_BORDER_START_LEFT, last + LEFT_BORDER_END_LEFT,
						 LEFT_SIDE_START_LEFT, last + LEFT_SIDE_END_LEFT,
						 RIGHT_BORDER_START_RIGHT, last + RIGHT_BORDER_END_RIGHT,
						 RIGHT_SIDE_START_RIGHT, last + RIGHT_SIDE_END_RIGHT };

		for (int i = 0; i < offset.length; i++)
		{
			if (minX > points[offset[i]].x)
				minX = points[offset[i]].x;
			if (maxX < points[offset[i]].x)
				maxX = points[offset[i]].x;
			if (minY > points[offset[i]].y)
				minY = points[offset[i]].y;
			if (maxY < points[offset[i]].y)
				maxY = points[offset[i]].y;
		}

		boundingRectangle.setRect(minX, minY, maxX - minX, maxY - minY);
		setSelectionBounds();
	}

	public void calcShape(EditorFrame editorFrame) throws Exception
	{
		double currentX = Editor.getProperties().getCurrentX();
		double currentY = Editor.getProperties().getCurrentY();
		double currentA = Editor.getProperties().getCurrentA();
		double showArrows = Editor.getProperties().getShowArrows();
		double trackWidth = editorFrame.getTrackData().getMainTrack().getWidth();
		double leftBorderWidth = getValidLeftBorderWidth(editorFrame);
		double rightBorderWidth = getValidRightBorderWidth(editorFrame);
		double leftSideStartWidth = getValidLeftSideStartWidth(editorFrame);
		double leftSideEndWidth = getValidLeftSideEndWidth(editorFrame);
		double rightSideStartWidth = getValidRightSideStartWidth(editorFrame);
		double rightSideEndWidth = getValidRightSideEndWidth(editorFrame);
		double leftBarrierWidth = getValidLeftBarrierWidth(editorFrame);
		double rightBarrierWidth = getValidRightBarrierWidth(editorFrame);
		double leftStartHeight = this.getCalculatedHeightStartLeft();
		double rightStartHeight = this.getCalculatedHeightStartRight();
		double leftEndHeight = this.getCalculatedHeightEndLeft();
		double rightEndHeight = this.getCalculatedHeightEndRight();

		// same step count as the game's track loader
		nbSteps = getValidSteps(editorFrame, length);
		stepLength = length / nbSteps;

		if (points == null || points.length != 4 * (7 + (showArrows > 0.0 ? 1 : 0)) * nbSteps)
		{
			points = new Point3D[4 * (7 + (showArrows > 0.0 ? 1 : 0)) * nbSteps];

			for (int i = 0; i < points.length; i++)
				points[i] = new Point3D();

			trPoints = new Point2D.Double[4 * (7 + (showArrows > 0.0 ? 1 : 0)) * nbSteps];
			for (int i = 0; i < trPoints.length; i++)
				trPoints[i] = new Point2D.Double();
		}

		int currentSubSeg = 0;
		double leftSideDeltaStep = (leftSideEndWidth - leftSideStartWidth) / nbSteps;
		double rightSideDeltaStep = (rightSideEndWidth - rightSideStartWidth) / nbSteps;

		double leftHeightDeltaStep = (leftEndHeight - leftStartHeight) / nbSteps;
		double rightHeightDeltaStep = (rightEndHeight - rightStartHeight) / nbSteps;

		double cos = Math.cos(currentA) * stepLength;
		double sin = Math.sin(currentA) * stepLength;

		double cosTransLeft = Math.cos(currentA + Math.PI / 2);
		double sinTransLeft = Math.sin(currentA + Math.PI / 2);

		boolean linear = getValidProfil(editorFrame).equals("linear");

		double T1l = (getCalculatedStartTangentLeft() / 100.0)  * getLength();
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

		dx = 0;
		dy = 0;

		for (int nStep = 0; nStep < nbSteps; nStep++)
		{
			// track
			points[currentSubSeg + TRACK_START_LEFT].x = currentX + cosTransLeft * trackWidth / 2;
			points[currentSubSeg + TRACK_START_LEFT].y = currentY + sinTransLeft * trackWidth / 2;

			points[currentSubSeg + TRACK_END_LEFT].x = points[currentSubSeg + TRACK_START_LEFT].x + cos;
			points[currentSubSeg + TRACK_END_LEFT].y = points[currentSubSeg + TRACK_START_LEFT].y + sin;

			points[currentSubSeg + TRACK_START_RIGHT].x = currentX - cosTransLeft * trackWidth / 2;
			points[currentSubSeg + TRACK_START_RIGHT].y = currentY - sinTransLeft * trackWidth / 2;

			points[currentSubSeg + TRACK_END_RIGHT].x = points[currentSubSeg + TRACK_START_RIGHT].x + cos;
			points[currentSubSeg + TRACK_END_RIGHT].y = points[currentSubSeg + TRACK_START_RIGHT].y + sin;

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

			points[currentSubSeg + LEFT_BORDER_END_LEFT].x = points[currentSubSeg + LEFT_BORDER_START_LEFT].x + cos;
			points[currentSubSeg + LEFT_BORDER_END_LEFT].y = points[currentSubSeg + LEFT_BORDER_START_LEFT].y + sin;

			points[currentSubSeg + LEFT_BORDER_START_RIGHT].x = points[currentSubSeg + TRACK_START_LEFT].x;
			points[currentSubSeg + LEFT_BORDER_START_RIGHT].y = points[currentSubSeg + TRACK_START_LEFT].y;

			points[currentSubSeg + LEFT_BORDER_END_RIGHT].x = points[currentSubSeg + TRACK_END_LEFT].x;
			points[currentSubSeg + LEFT_BORDER_END_RIGHT].y = points[currentSubSeg + TRACK_END_LEFT].y;

			// left side

			points[currentSubSeg + LEFT_SIDE_START_LEFT].x = currentX + cosTransLeft * (trackWidth / 2 + leftBorderWidth + leftSideStartWidth + (leftSideDeltaStep * nStep));
			points[currentSubSeg + LEFT_SIDE_START_LEFT].y = currentY + sinTransLeft * (trackWidth / 2 + leftBorderWidth + leftSideStartWidth + (leftSideDeltaStep * nStep));

			points[currentSubSeg + LEFT_SIDE_END_LEFT].x = currentX + cos + cosTransLeft * (trackWidth / 2 + leftBorderWidth + leftSideStartWidth + (leftSideDeltaStep * (nStep + 1)));
			points[currentSubSeg + LEFT_SIDE_END_LEFT].y = currentY + sin + sinTransLeft * (trackWidth / 2 + leftBorderWidth + leftSideStartWidth + (leftSideDeltaStep * (nStep + 1)));

			points[currentSubSeg + LEFT_SIDE_END_RIGHT].x = points[currentSubSeg + LEFT_BORDER_END_LEFT].x;
			points[currentSubSeg + LEFT_SIDE_END_RIGHT].y = points[currentSubSeg + LEFT_BORDER_END_LEFT].y;

			points[currentSubSeg + LEFT_SIDE_START_RIGHT].x = points[currentSubSeg + LEFT_BORDER_START_LEFT].x;
			points[currentSubSeg + LEFT_SIDE_START_RIGHT].y = points[currentSubSeg + LEFT_BORDER_START_LEFT].y;

			// left barrier

			points[currentSubSeg + LEFT_BARRIER_START_LEFT].x = currentX + cosTransLeft * (trackWidth / 2 + leftBorderWidth + leftSideStartWidth + (leftSideDeltaStep * nStep) + leftBarrierWidth);
			points[currentSubSeg + LEFT_BARRIER_START_LEFT].y = currentY + sinTransLeft * (trackWidth / 2 + leftBorderWidth + leftSideStartWidth + (leftSideDeltaStep * nStep) + leftBarrierWidth);

			points[currentSubSeg + LEFT_BARRIER_END_LEFT].x = currentX + cos + cosTransLeft * (trackWidth / 2 + leftBorderWidth + leftSideStartWidth + (leftSideDeltaStep * (nStep + 1)) + leftBarrierWidth);
			points[currentSubSeg + LEFT_BARRIER_END_LEFT].y = currentY + sin + sinTransLeft * (trackWidth / 2 + leftBorderWidth + leftSideStartWidth + (leftSideDeltaStep * (nStep + 1)) + leftBarrierWidth);

			points[currentSubSeg + LEFT_BARRIER_START_RIGHT].x = points[currentSubSeg + LEFT_SIDE_START_LEFT].x;
			points[currentSubSeg + LEFT_BARRIER_START_RIGHT].y = points[currentSubSeg + LEFT_SIDE_START_LEFT].y;

			points[currentSubSeg + LEFT_BARRIER_END_RIGHT].x = points[currentSubSeg + LEFT_SIDE_END_LEFT].x;
			points[currentSubSeg + LEFT_BARRIER_END_RIGHT].y = points[currentSubSeg + LEFT_SIDE_END_LEFT].y;

			// right border

			points[currentSubSeg + RIGHT_BORDER_START_RIGHT].x = currentX - cosTransLeft * (trackWidth / 2 + rightBorderWidth);
			points[currentSubSeg + RIGHT_BORDER_START_RIGHT].y = currentY - sinTransLeft * (trackWidth / 2 + rightBorderWidth);

			points[currentSubSeg + RIGHT_BORDER_END_RIGHT].x = points[currentSubSeg + RIGHT_BORDER_START_RIGHT].x + cos;
			points[currentSubSeg + RIGHT_BORDER_END_RIGHT].y = points[currentSubSeg + RIGHT_BORDER_START_RIGHT].y + sin;

			points[currentSubSeg + RIGHT_BORDER_START_LEFT].x = points[currentSubSeg + TRACK_START_RIGHT].x;
			points[currentSubSeg + RIGHT_BORDER_START_LEFT].y = points[currentSubSeg + TRACK_START_RIGHT].y;

			points[currentSubSeg + RIGHT_BORDER_END_LEFT].x = points[currentSubSeg + TRACK_END_RIGHT].x;
			points[currentSubSeg + RIGHT_BORDER_END_LEFT].y = points[currentSubSeg + TRACK_END_RIGHT].y;

			// right side

			points[currentSubSeg + RIGHT_SIDE_START_RIGHT].x = currentX - cosTransLeft * (trackWidth / 2 + rightBorderWidth + rightSideStartWidth + (rightSideDeltaStep * nStep));
			points[currentSubSeg + RIGHT_SIDE_START_RIGHT].y = currentY - sinTransLeft * (trackWidth / 2 + rightBorderWidth + rightSideStartWidth + (rightSideDeltaStep * nStep));

			points[currentSubSeg + RIGHT_SIDE_END_RIGHT].x = currentX + cos - cosTransLeft * (trackWidth / 2 + rightBorderWidth + rightSideStartWidth + (rightSideDeltaStep * (nStep + 1)));
			points[currentSubSeg + RIGHT_SIDE_END_RIGHT].y = currentY + sin - sinTransLeft * (trackWidth / 2 + rightBorderWidth + rightSideStartWidth + (rightSideDeltaStep * (nStep + 1)));

			points[currentSubSeg + RIGHT_SIDE_END_LEFT].x = points[currentSubSeg + RIGHT_BORDER_END_RIGHT].x;
			points[currentSubSeg + RIGHT_SIDE_END_LEFT].y = points[currentSubSeg + RIGHT_BORDER_END_RIGHT].y;

			points[currentSubSeg + RIGHT_SIDE_START_LEFT].x = points[currentSubSeg + RIGHT_BORDER_START_RIGHT].x;
			points[currentSubSeg + RIGHT_SIDE_START_LEFT].y = points[currentSubSeg + RIGHT_BORDER_START_RIGHT].y;

			// right barrier

			points[currentSubSeg + RIGHT_BARRIER_START_RIGHT].x = currentX - cosTransLeft * (trackWidth / 2 + rightBorderWidth + rightSideStartWidth + (rightSideDeltaStep * nStep) + rightBarrierWidth);
			points[currentSubSeg + RIGHT_BARRIER_START_RIGHT].y = currentY - sinTransLeft * (trackWidth / 2 + rightBorderWidth + rightSideStartWidth + (rightSideDeltaStep * nStep) + rightBarrierWidth);

			points[currentSubSeg + RIGHT_BARRIER_END_RIGHT].x = currentX + cos - cosTransLeft * (trackWidth / 2 + rightBorderWidth + rightSideStartWidth + (rightSideDeltaStep * (nStep + 1)) + rightBarrierWidth);
			points[currentSubSeg + RIGHT_BARRIER_END_RIGHT].y = currentY + sin - sinTransLeft * (trackWidth / 2 + rightBorderWidth + rightSideStartWidth + (rightSideDeltaStep * (nStep + 1)) + rightBarrierWidth);

			points[currentSubSeg + RIGHT_BARRIER_START_LEFT].x = points[currentSubSeg + RIGHT_SIDE_START_RIGHT].x;
			points[currentSubSeg + RIGHT_BARRIER_START_LEFT].y = points[currentSubSeg + RIGHT_SIDE_START_RIGHT].y;

			points[currentSubSeg + RIGHT_BARRIER_END_LEFT].x = points[currentSubSeg + RIGHT_SIDE_END_RIGHT].x;
			points[currentSubSeg + RIGHT_BARRIER_END_LEFT].y = points[currentSubSeg + RIGHT_SIDE_END_RIGHT].y;

			if (showArrows > 0.0)
			{
				// arrow
				points[currentSubSeg + ARROW_START_LEFT].x = points[currentSubSeg + TRACK_START_LEFT].x;
				points[currentSubSeg + ARROW_START_LEFT].y = points[currentSubSeg + TRACK_START_LEFT].y;

				points[currentSubSeg + ARROW_END_LEFT].x = points[currentSubSeg + ARROW_START_LEFT].x + cos - (cosTransLeft * trackWidth / 2) * 0.99999;
				points[currentSubSeg + ARROW_END_LEFT].y = points[currentSubSeg + ARROW_START_LEFT].y + sin - (sinTransLeft * trackWidth / 2) * 0.99999;

				points[currentSubSeg + ARROW_START_RIGHT].x = points[currentSubSeg + TRACK_START_RIGHT].x;
				points[currentSubSeg + ARROW_START_RIGHT].y = points[currentSubSeg + TRACK_START_RIGHT].y;

				points[currentSubSeg + ARROW_END_RIGHT].x = points[currentSubSeg + ARROW_START_RIGHT].x + cos + (cosTransLeft * trackWidth / 2) * 0.99999;
				points[currentSubSeg + ARROW_END_RIGHT].y = points[currentSubSeg + ARROW_START_RIGHT].y + sin + (sinTransLeft * trackWidth / 2) * 0.99999;

				currentSubSeg += 32;
			}
			else
			{
				currentSubSeg += 28;
			}

			// move track center
			dx += cos;
			dy += sin;
			currentX += cos;
			currentY += sin;
		}

		endTrackCenter.setLocation(currentX, currentY);
		endTrackAlpha = startTrackAlpha;

		setBounds();

		Editor.getProperties().setCurrentA(currentA);
		Editor.getProperties().setCurrentX(currentX);
		Editor.getProperties().setCurrentY(currentY);
	}

	public void drag(Point2D.Double dragDelta)
	{
	}

	public Object clone()
	{
		Straight s;
		s = (Straight) super.clone();

		return s; // return the clone
	}

	public void dump(PrintStream printStream, String indent, boolean dumpCalculated, boolean dumpPoints, boolean dumpTrPoints, boolean dumpToDraw)
	{
		printStream.println(indent + "Straight");
		super.dump(printStream, indent, dumpCalculated, dumpPoints, dumpTrPoints, dumpToDraw);
	}
}
