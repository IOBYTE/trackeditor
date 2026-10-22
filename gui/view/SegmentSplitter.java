package gui.view;

import java.awt.geom.Point2D;

import gui.EditorFrame;
import utils.Editor;
import utils.SegmentVector;
import utils.circuit.Curve;
import utils.circuit.Segment;
import utils.circuit.SegmentSide;
import utils.circuit.Straight;
import utils.undo.Undo;
import utils.undo.UndoSplitSegment;

/**
 * Splits a segment in two at the point clicked, so that the game builds the
 * same track from the two pieces as it did from the original.
 *
 * Straights and constant radius curves are split exactly where clicked. A
 * spiral (start radius != end radius) is built by the game as a series of
 * steps, each a circular arc with its own radius, so it is split at the step
 * boundary nearest the click and each piece is given the steps it covered;
 * the pieces then have exactly the original's radii and angles.
 *
 * Heights, slopes, banking and side widths at the split point are taken from
 * the original segment, before anything is changed, using the same spline the
 * game uses for the profile.
 */
class SegmentSplitter
{
	private final EditorFrame	editorFrame;

	SegmentSplitter(EditorFrame editorFrame)
	{
		this.editorFrame = editorFrame;
	}

	/**
	 * @return null if the segment was split, otherwise why it wasn't
	 */
	String split(Segment shape, Point2D.Double point)
	{
		SegmentVector data = editorFrame.getTrackData().getSegments();
		int pos = data.indexOf(shape);
		if (pos < 0)
			return "Segment not found";

		boolean isCurve = shape instanceof Curve;
		double length = formulaLength(shape);
		if (!(length > 0))
			return "The segment has no length";

		int steps = steps(shape, length);

		// where the segment starts: the end of the previous one
		double x0 = 0, y0 = 0, a0 = 0;
		if (pos > 0)
		{
			Segment previous = data.get(pos - 1);
			x0 = previous.endTrackCenter.x;
			y0 = previous.endTrackCenter.y;
			a0 = previous.endTrackAlpha;
		}

		// spline parameter (0..1) of the split, and the curve geometry of the pieces
		double t;
		double arc1 = 0, arc2 = 0, radiusEnd1 = 0, radiusStart2 = 0;
		int steps1 = 0, steps2 = 0;
		boolean spiral = false;

		if (!isCurve)
		{
			double dx = Math.cos(a0), dy = Math.sin(a0);
			t = ((point.x - x0) * dx + (point.y - y0) * dy) / length;
		}
		else
		{
			Curve curve = (Curve) shape;
			double rs = curve.getRadiusStart();
			double re = curve.hasRadiusEnd() ? curve.getRadiusEnd() : rs;
			double arc = curve.getArcRad();
			double sign = curve.getType().equals("rgt") ? -1 : 1;
			spiral = re != rs;

			if (spiral && steps < 2)
				return "The game builds this spiral as a single step, so it can't be split.\n" +
					   "A linear profile is always one step; for a spline give it a profil steps length or profil steps.";

			// the game's steps: equal length, radius changing by a fixed amount each step
			double dr = spiral ? (re - rs) / (steps - 1) : 0;
			double stepLength = length / steps;
			if (spiral)
			{
				double angle = 0;
				for (int i = 0; i < steps; i++)
					angle += stepLength / (rs + i * dr);
				stepLength *= arc / angle;
			}

			// find the distance along the centre line closest to the click
			double best = Double.MAX_VALUE, bestDist = 0;
			double x = x0, y = y0, a = a0, dist = 0;
			int samples = 64;
			for (int i = 0; i < steps; i++)
			{
				double k = sign / (rs + i * dr);
				for (int j = 1; j <= samples; j++)
				{
					double s = stepLength * j / samples;
					double px = x + (Math.sin(a + k * s) - Math.sin(a)) / k;
					double py = y - (Math.cos(a + k * s) - Math.cos(a)) / k;
					double d2 = (px - point.x) * (px - point.x) + (py - point.y) * (py - point.y);
					if (d2 < best)
					{
						best = d2;
						bestDist = dist + s;
					}
				}
				x += (Math.sin(a + k * stepLength) - Math.sin(a)) / k;
				y -= (Math.cos(a + k * stepLength) - Math.cos(a)) / k;
				a += k * stepLength;
				dist += stepLength;
			}

			if (spiral)
			{
				// split on a step boundary
				steps1 = (int) Math.round(bestDist / stepLength);
				steps1 = Math.max(1, Math.min(steps - 1, steps1));
				steps2 = steps - steps1;
				t = (double) steps1 / steps;
				for (int i = 0; i < steps1; i++)
					arc1 += stepLength / (rs + i * dr);
				arc2 = arc - arc1;
				radiusEnd1 = rs + (steps1 - 1) * dr;
				radiusStart2 = rs + steps1 * dr;
			}
			else
			{
				t = bestDist / dist;
				arc1 = arc * t;
				arc2 = arc - arc1;
				radiusEnd1 = radiusStart2 = rs;
			}
		}

		// don't make pieces shorter than a step (or a quarter of the segment)
		double minimum = Math.min(length / Math.max(steps, 1), length / 4);
		if (!spiral && (t * length < minimum || (1 - t) * length < minimum))
			return "Too close to the end of the segment to split it";

		Segment original = (Segment) shape.clone();
		Segment piece2;

		if (isCurve)
		{
			Curve curve = (Curve) shape;
			Curve newCurve = new Curve(curve.getType(), curve);
			double re = curve.hasRadiusEnd() ? curve.getRadiusEnd() : curve.getRadiusStart();

			curve.setArcDeg(Math.toDegrees(arc1));
			newCurve.setArcDeg(Math.toDegrees(arc2));
			newCurve.setRadiusStart(radiusStart2);
			if (spiral)
			{
				curve.setRadiusEnd(radiusEnd1);
				newCurve.setRadiusEnd(re);
			}
			else
			{
				newCurve.setRadiusEnd(curve.hasRadiusEnd() ? re : Double.NaN);
			}
			piece2 = newCurve;
		}
		else
		{
			Straight newStraight = new Straight();
			shape.setLength(length * t);
			newStraight.setLength(length * (1 - t));
			piece2 = newStraight;
		}

		Segment previous = pos > 0 ? data.get(pos - 1) : null;
		splitAttributes(original, previous, shape, piece2, t, length, steps, spiral, steps1, steps2);

		if (isCurve)
		{
			int count = Editor.getProperties().getCurveNameCount() + 1;
			Editor.getProperties().setCurveNameCount(count);
			piece2.setName("curve " + count);
		}
		else
		{
			int count = Editor.getProperties().getStraightNameCount() + 1;
			Editor.getProperties().setStraightNameCount(count);
			piece2.setName("straight " + count);
		}

		data.insertElementAt(piece2, pos + 1);

		// make sure the pieces end at the original's heights: a piece without an
		// explicit end height gets one from the grade, which needn't follow the spline
		editorFrame.getTrackData().calculateSegmentValues();
		fixHeight(shape.getCalculatedHeightEnd(), splitHeights[0], () -> shape.setHeightEnd(splitHeights[0]));
		fixHeight(piece2.getCalculatedHeightEnd(), original.getCalculatedHeightEnd(), () -> piece2.setHeightEnd(original.getCalculatedHeightEnd()));
		editorFrame.getTrackData().calculateSegmentValues();

		Undo.add(new UndoSplitSegment(editorFrame, shape, original, piece2));

		return null;
	}

	/**
	 * Length the game uses for the profile (for a spiral this is not the
	 * length actually driven).
	 */
	static double formulaLength(Segment segment)
	{
		if (segment instanceof Curve)
		{
			Curve curve = (Curve) segment;
			double re = curve.hasRadiusEnd() ? curve.getRadiusEnd() : curve.getRadiusStart();
			return (curve.getRadiusStart() + re) / 2 * curve.getArcRad();
		}
		return segment.getLength();
	}

	private int steps(Segment segment, double length)
	{
		return segment.getValidSteps(editorFrame, length);
	}

	private boolean hasSpline(Segment segment)
	{
		return segment.isSpline();
	}

	/** The game's profile spline: tangents are slopes in % of the segment length. */
	private static double height(double z0, double z1, double tan0, double tan1, double length, double t)
	{
		double t2 = t * t, t3 = t2 * t;
		return (2 * t3 - 3 * t2 + 1) * z0 + (3 * t2 - 2 * t3) * z1 +
			   (t3 - 2 * t2 + t) * tan0 / 100 * length + (t3 - t2) * tan1 / 100 * length;
	}

	/** d(height)/dt of the spline above. */
	private static double heightRate(double z0, double z1, double tan0, double tan1, double length, double t)
	{
		double t2 = t * t;
		return (6 * t2 - 6 * t) * z0 + (6 * t - 6 * t2) * z1 +
			   (3 * t2 - 4 * t + 1) * tan0 / 100 * length + (3 * t2 - 2 * t) * tan1 / 100 * length;
	}

	/** heights at the split: centre, left, right */
	private final double[]	splitHeights	= new double[3];

	private void fixHeight(double actual, double wanted, Runnable set)
	{
		if (!Double.isNaN(wanted) && Math.abs(actual - wanted) > 0.0005)
			set.run();
	}

	private static boolean differ(double a, double b)
	{
		return Math.abs(a - b) > 1e-9;
	}

	private void splitAttributes(Segment original, Segment previous, Segment piece1, Segment piece2, double t, double length,
								 int steps, boolean spiral, int steps1, int steps2)
	{
		double length1 = formulaLength(piece1);
		double length2 = formulaLength(piece2);

		// settings the second piece shares with the original
		piece2.setSurface(original.getSurface());
		piece2.setProfil(original.getProfil());
		piece2.setProfilStepsLength(original.getProfilStepsLength());
		piece2.setGrade(original.getGrade());
		piece2.setPenalty(original.getPenalty());
		piece2.setEnvMapIndex(original.getEnvMapIndex());
		piece2.setDovFactor(original.getDovFactor());

		// steps: a spiral keeps the steps each piece covered; otherwise keep an explicit count roughly in proportion
		if (spiral)
		{
			if (original.hasProfilSteps() || steps(piece1, length1) != steps1)
				piece1.setProfilSteps(steps1);
			if (original.hasProfilSteps() || steps(piece2, length2) != steps2)
				piece2.setProfilSteps(steps2);
		}
		else if (original.hasProfilSteps())
		{
			int count1 = Math.max(1, Math.min(steps - 1, (int) Math.round(steps * t)));
			piece1.setProfilSteps(count1);
			piece2.setProfilSteps(Math.max(1, steps - count1));
		}

		splitSide(original, original.getLeft(), piece1.getLeft(), piece2.getLeft(),
				original.getValidLeftSideStartWidth(editorFrame), original.getValidLeftSideEndWidth(editorFrame), t);
		splitSide(original, original.getRight(), piece1.getRight(), piece2.getRight(),
				original.getValidRightSideStartWidth(editorFrame), original.getValidRightSideEndWidth(editorFrame), t);

		// banking changes linearly over the steps
		double bankingStart = original.getValidBankingStart(editorFrame);
		if (!Double.isNaN(bankingStart) && original.hasBankingEnd())
		{
			double banking = bankingStart + (original.getBankingEnd() - bankingStart) * t;
			piece1.setBankingEnd(banking);
			piece2.setBankingStart(banking);
			piece2.setBankingEnd(original.getBankingEnd());
		}

		// heights
		double zc = height(original.getCalculatedHeightStart(), original.getCalculatedHeightEnd(),
				original.getCalculatedStartTangent(), original.getCalculatedEndTangent(), length, t);
		double zl = height(original.getCalculatedHeightStartLeft(), original.getCalculatedHeightEndLeft(),
				original.getCalculatedStartTangentLeft(), original.getCalculatedEndTangentLeft(), length, t);
		double zr = height(original.getCalculatedHeightStartRight(), original.getCalculatedHeightEndRight(),
				original.getCalculatedStartTangentRight(), original.getCalculatedEndTangentRight(), length, t);

		boolean spline = hasSpline(original);
		if (!spline)
		{
			// linear profile
			zc = original.getCalculatedHeightStart() + (original.getCalculatedHeightEnd() - original.getCalculatedHeightStart()) * t;
			zl = original.getCalculatedHeightStartLeft() + (original.getCalculatedHeightEndLeft() - original.getCalculatedHeightStartLeft()) * t;
			zr = original.getCalculatedHeightStartRight() + (original.getCalculatedHeightEndRight() - original.getCalculatedHeightStartRight()) * t;
		}

		splitHeights[0] = zc;
		splitHeights[1] = zl;
		splitHeights[2] = zr;

		if (!Double.isNaN(original.getValidHeightStart()))
		{
			if (original.hasHeightEnd())
			{
				piece1.setHeightEnd(zc);
				piece2.setHeightStart(zc);
				piece2.setHeightEnd(original.getHeightEnd());
			}
			if (original.hasHeightEndLeft())
			{
				piece1.setHeightEndLeft(zl);
				piece2.setHeightStartLeft(zl);
				piece2.setHeightEndLeft(original.getHeightEndLeft());
			}
			if (original.hasHeightEndRight())
			{
				piece1.setHeightEndRight(zr);
				piece2.setHeightStartRight(zr);
				piece2.setHeightEndRight(original.getHeightEndRight());
			}
		}

		if (!spline)
			return;

		// the split point gets new tangents; anything explicit at the original's
		// end now belongs to the second piece
		piece1.setProfilEndTangent(Double.NaN);
		piece1.setProfilEndTangentLeft(Double.NaN);
		piece1.setProfilEndTangentRight(Double.NaN);

		// slopes at the split, as tangents of each piece (equal to the slope
		// for straights and arcs; scaled for a spiral, whose profile length
		// isn't the sum of its pieces')
		boolean separate = differ(original.getCalculatedStartTangentLeft(), original.getCalculatedStartTangentRight()) ||
						   differ(original.getCalculatedEndTangentLeft(), original.getCalculatedEndTangentRight()) ||
						   original.hasProfilStartTangentLeft() || original.hasProfilStartTangentRight() ||
						   original.hasProfilEndTangentLeft() || original.hasProfilEndTangentRight();

		if (!separate)
		{
			double rate = heightRate(original.getCalculatedHeightStart(), original.getCalculatedHeightEnd(),
					original.getCalculatedStartTangent(), original.getCalculatedEndTangent(), length, t);
			double end1 = 100 * t * rate / length1;
			double start2 = 100 * (1 - t) * rate / length2;
			double end2 = original.getCalculatedEndTangent();

			// a segment's start and end tangents both default to the previous
			// segment's end tangent, so only write what differs from that
			double inherited = previous != null ? previous.getCalculatedEndTangent() : 0;
			if (differ(end1, inherited))
				piece1.setProfilEndTangent(end1);
			else
				end1 = inherited;
			if (differ(start2, end1))
				piece2.setProfilStartTangent(start2);
			if (original.hasProfilEndTangent() || differ(end2, end1))
				piece2.setProfilEndTangent(end2);
		}
		else
		{
			splitTangents(original, piece1, piece2, t, length, length1, length2, true);
			splitTangents(original, piece1, piece2, t, length, length1, length2, false);
		}
	}

	private void splitTangents(Segment original, Segment piece1, Segment piece2, double t,
							   double length, double length1, double length2, boolean left)
	{
		double z0 = left ? original.getCalculatedHeightStartLeft() : original.getCalculatedHeightStartRight();
		double z1 = left ? original.getCalculatedHeightEndLeft() : original.getCalculatedHeightEndRight();
		double start1 = left ? original.getCalculatedStartTangentLeft() : original.getCalculatedStartTangentRight();
		double end2 = left ? original.getCalculatedEndTangentLeft() : original.getCalculatedEndTangentRight();
		double rate = heightRate(z0, z1, start1, end2, length, t);
		double end1 = 100 * t * rate / length1;
		double start2 = 100 * (1 - t) * rate / length2;

		if (left)
		{
			piece1.setProfilEndTangentLeft(end1);
			piece2.setProfilStartTangentLeft(start2);
			piece2.setProfilEndTangentLeft(end2);
		}
		else
		{
			piece1.setProfilEndTangentRight(end1);
			piece2.setProfilStartTangentRight(start2);
			piece2.setProfilEndTangentRight(end2);
		}

	}

	private void splitSide(Segment original, SegmentSide side, SegmentSide side1, SegmentSide side2,
						   double startWidth, double endWidth, double t)
	{
		if (!Double.isNaN(startWidth) && !Double.isNaN(endWidth) && differ(startWidth, endWidth))
		{
			double width = startWidth + (endWidth - startWidth) * t;
			side1.setSideEndWidth(width);
			side2.setSideStartWidth(width);
			side2.setSideEndWidth(endWidth);
		}

		side2.setSideSurface(side.getSideSurface());

		side2.setBorderWidth(side.getBorderWidth());
		side2.setBorderStyle(side.getBorderStyle());
		side2.setBorderSurface(side.getBorderSurface());
		side2.setBorderHeight(side.getBorderHeight());

		side2.setBarrierStyle(side.getBarrierStyle());
		side2.setBarrierSurface(side.getBarrierSurface());
		side2.setBarrierHeight(side.getBarrierHeight());
		side2.setBarrierWidth(side.getBarrierWidth());
	}
}
