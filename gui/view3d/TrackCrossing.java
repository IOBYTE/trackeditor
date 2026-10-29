/*
 *   TrackCrossing.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package gui.view3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gui.EditorFrame;
import gui.view3d.GameTrack.Seg;
import utils.circuit.RoadTexture;
import utils.circuit.Surface;
import utils.circuit.TerrainGeneration;

/**
 * The bridges TORCS's trackgen builds where the track crosses over itself
 * (tools/trackgen trackcrossing.cpp): it finds where the centerlines cross,
 * samples the outer edges of both parts around there, and builds the bridge's
 * underside and sides over the lower part, the walls (sockets) beside the lower
 * part with their returns at the portals, and the floors between the walls and
 * the lower part's edges. Each is a list of triangles, as trackgen writes them.
 */
public class TrackCrossing
{
	private static final double	CROSSING_PI							= 3.14159265358979323846;
	private static final double	CROSSING_PLAN_EPSILON				= 1e-7;
	private static final double	CROSSING_POINT_EPSILON				= 1e-4;
	private static final double	CROSSING_RENDERED_ENDPOINT_EPSILON	= 1e-3;
	private static final double	CROSSING_HEIGHT_EPSILON				= 1e-5;
	private static final double	CROSSING_TEXTURE_SIZE				= 15.0;
	private static final double	DBL_MAX								= Double.MAX_VALUE;

	private static final int	TR_SIDE_RGT							= GameTrack.TR_SIDE_RGT;
	private static final int	TR_SIDE_LFT							= GameTrack.TR_SIDE_LFT;

	/** One of a crossing's objects: its points (x, y, z, u, v) and triangles. */
	public static final class CrossingObject
	{
		public String				name		= "";
		public String				texture		= "";
		public final List<double[]>	vertices	= new ArrayList<double[]>();
		public final List<int[]>	triangles	= new ArrayList<int[]>();
	}

	/** One crossing: the bridge, the walls beside the lower part, their returns and floors. */
	public static final class Geometry
	{
		public int					upperSegmentId, lowerSegmentId;
		public double				crossingX, crossingY;
		public double				undersideZ;
		public double				minimumUpperZ;
		public double				minimumBridgeThickness;
		public final double[]		measuredSocketGap	= new double[2];
		public final CrossingObject	underside			= new CrossingObject();
		public final CrossingObject	bridgeRight			= new CrossingObject();
		public final CrossingObject	bridgeLeft			= new CrossingObject();
		public final CrossingObject	socketRight			= new CrossingObject();
		public final CrossingObject	socketLeft			= new CrossingObject();
		/** the portal returns: by the upper part's side, then the lower part's */
		public final CrossingObject[][]	filler			= { { new CrossingObject(), new CrossingObject() },
			{ new CrossingObject(), new CrossingObject() } };
		public final CrossingObject	floorRight			= new CrossingObject();
		public final CrossingObject	floorLeft			= new CrossingObject();
		/** the portals' footprints, which close the terrain and aren't drawn */
		public final CrossingObject	terrainMask			= new CrossingObject();

		/** @return the objects in the order trackgen writes them */
		public List<CrossingObject> objects()
		{
			List<CrossingObject> list = new ArrayList<CrossingObject>();
			list.add(underside);
			list.add(bridgeRight);
			list.add(bridgeLeft);
			list.add(socketRight);
			list.add(filler[TR_SIDE_RGT][TR_SIDE_RGT]);
			list.add(filler[TR_SIDE_LFT][TR_SIDE_RGT]);
			list.add(socketLeft);
			list.add(filler[TR_SIDE_RGT][TR_SIDE_LFT]);
			list.add(filler[TR_SIDE_LFT][TR_SIDE_LFT]);
			list.add(floorRight);
			list.add(floorLeft);
			return list;
		}
	}

	private static final class PlanPoint
	{
		double	x, y;

		PlanPoint(double x, double y)
		{
			this.x = x;
			this.y = y;
		}
	}

	private static final class CrossingSegment
	{
		Seg			segment;
		int			index;
		double		startDistance;
		// not trackgen's: the centerline's ends on a straight, worked out once, and the
		// box the centerline is in, to skip the pairs that can't cross
		PlanPoint	start, end;
		double		minX, minY, maxX, maxY;
	}

	private static final class Candidate
	{
		int		firstIndex, secondIndex;
		double	firstToStart, secondToStart;
		double	x, y;
	}

	private static final class BoundaryPoint
	{
		double	distance;
		double	x, y, z;
		double	minimumSurfaceZ, maximumSurfaceZ;
		double	barrierTopZ;
		boolean	renderedVertex;

		BoundaryPoint copy()
		{
			BoundaryPoint p = new BoundaryPoint();
			p.distance = distance;
			p.x = x;
			p.y = y;
			p.z = z;
			p.minimumSurfaceZ = minimumSurfaceZ;
			p.maximumSurfaceZ = maximumSurfaceZ;
			p.barrierTopZ = barrierTopZ;
			p.renderedVertex = renderedVertex;
			return p;
		}
	}

	private static final class BoundaryLine
	{
		final List<BoundaryPoint>	points	= new ArrayList<BoundaryPoint>();

		BoundaryPoint front()
		{
			return points.get(0);
		}

		BoundaryPoint back()
		{
			return points.get(points.size() - 1);
		}
	}

	private static final class SampleDistance
	{
		double	distance;
		boolean	renderedVertex;
		Seg		segment;
		double	toStart;

		void set(SampleDistance other)
		{
			distance = other.distance;
			renderedVertex = other.renderedVertex;
			segment = other.segment;
			toStart = other.toStart;
		}
	}

	private static final class LineIntersection
	{
		boolean	found;
		double	firstDistance, secondDistance;
		double	x, y;
		double	distanceSquared	= DBL_MAX;
	}

	private static final class CrossingTexture
	{
		String	name;
		double	sizeU, sizeV;
	}

	private final GameTrack		track;
	private final EditorFrame	editorFrame;
	private final double		trackStep;
	private final double		socketGap, bridgeThickness;
	/** how many places the centerlines cross, before the height and clearance checks */
	public int					candidateCount;
	public final List<Geometry>	crossings	= new ArrayList<Geometry>();

	/**
	 * BuildTrackCrossingModel, with ReadTrackCrossingSettings: finds the crossings
	 * and builds each one with enough clearance. Nothing when the settings are
	 * invalid (trackgen stops then).
	 */
	public TrackCrossing(GameTrack track, EditorFrame editorFrame)
	{
		this.track = track;
		this.editorFrame = editorFrame;
		trackStep = track.trackStep;
		TerrainGeneration terrain = editorFrame.getTrackData().getTerrainGeneration();
		socketGap = Double.isNaN(terrain.getCrossingBridgeSocketGap()) ? 0.5 : (float) terrain.getCrossingBridgeSocketGap();
		bridgeThickness = Double.isNaN(terrain.getCrossingBridgeThickness()) ? 1.0 : (float) terrain.getCrossingBridgeThickness();
		if (!finite(socketGap) || socketGap < 0.0 || !finite(bridgeThickness) || bridgeThickness <= 0.0)
			return;
		if (track.segs.isEmpty() || !finite(trackStep) || trackStep <= 0.0)
			return;

		List<CrossingSegment> segments = new ArrayList<CrossingSegment>();
		double trackLength = 0.0;
		for (int index = 0; index < track.segs.size(); index++)
		{
			CrossingSegment crossingSegment = new CrossingSegment();
			crossingSegment.segment = track.segs.get(index);
			crossingSegment.index = index;
			crossingSegment.startDistance = trackLength;
			segments.add(crossingSegment);
			trackLength += crossingSegment.segment.length;
		}
		if (trackLength <= CROSSING_PLAN_EPSILON)
			return;
		List<Candidate> candidates = new ArrayList<Candidate>();
		findCrossingCandidates(segments, candidates);
		candidateCount = candidates.size();
		for (Candidate candidate : candidates)
		{
			double[] firstHeight = new double[1], secondHeight = new double[1];
			getTrackCenterPoint(segments.get(candidate.firstIndex).segment, candidate.firstToStart, firstHeight);
			getTrackCenterPoint(segments.get(candidate.secondIndex).segment, candidate.secondToStart, secondHeight);
			if (Math.abs(firstHeight[0] - secondHeight[0]) <= CROSSING_HEIGHT_EPSILON)
				continue;
			boolean firstIsUpper = firstHeight[0] > secondHeight[0];
			Geometry geometry = new Geometry();
			if (buildCrossingGeometry(segments, trackLength, candidate, firstIsUpper, crossings.size(), geometry))
				crossings.add(geometry);
		}
	}

	private static double cross(double firstX, double firstY, double secondX, double secondY)
	{
		return firstX * secondY - firstY * secondX;
	}

	private static double distanceSquared(double firstX, double firstY, double secondX, double secondY)
	{
		double deltaX = firstX - secondX;
		double deltaY = firstY - secondY;
		return deltaX * deltaX + deltaY * deltaY;
	}

	private static double clamp(double value, double minimum, double maximum)
	{
		if (value < minimum)
			return minimum;
		if (value > maximum)
			return maximum;
		return value;
	}

	private static boolean finite(double value)
	{
		return !Double.isNaN(value) && !Double.isInfinite(value);
	}

	/** fmod: the remainder with the dividend's sign */
	private static double fmod(double a, double b)
	{
		return a % b;
	}

	private static double positiveAngle(double angle)
	{
		double normalized = fmod(angle, 2.0 * CROSSING_PI);
		if (normalized < 0.0)
			normalized += 2.0 * CROSSING_PI;
		return normalized;
	}

	private static double segmentEndToStart(Seg segment)
	{
		return segment.type == GameTrack.TR_STR ? segment.length : segment.arc;
	}

	private static double segmentPhysicalOffset(Seg segment, double toStart)
	{
		return segment.type == GameTrack.TR_STR ? toStart : toStart * segment.radius;
	}

	private static double segmentToStart(Seg segment, double physicalOffset)
	{
		if (segment.type == GameTrack.TR_STR)
			return physicalOffset;
		if (Math.abs(segment.radius) <= CROSSING_PLAN_EPSILON)
			return 0.0;
		return physicalOffset / segment.radius;
	}

	/** RtTrackLocal2Global with TR_TORIGHT, in the game's floats */
	private static PlanPoint localToGlobal(Seg segment, double toStart, double toRight)
	{
		float[] xy = GameTrack.local2Global(segment, (float) toStart, (float) toRight);
		return new PlanPoint(xy[0], xy[1]);
	}

	private double heightL(Seg segment, double toStart, double toRight)
	{
		return track.heightL(segment, (float) toStart, (float) toRight);
	}

	private PlanPoint getTrackCenterPoint(Seg segment, double toStart, double[] height)
	{
		float toRight = GameTrack.width(segment, (float) toStart) / 2.0f;
		PlanPoint point = localToGlobal(segment, toStart, toRight);
		if (height != null)
			height[0] = heightL(segment, (float) toStart, toRight);
		return point;
	}

	private static PlanPoint getTrackTangent(Seg segment, double toStart)
	{
		double angle = segment.angleZS;
		if (segment.type == GameTrack.TR_LFT)
			angle += toStart;
		else if (segment.type == GameTrack.TR_RGT)
			angle -= toStart;
		return new PlanPoint(Math.cos(angle), Math.sin(angle));
	}

	/** @return the curve's toStart at (x, y), or NaN when it's beyond the curve */
	private static double getCurveToStart(Seg segment, double x, double y)
	{
		double sign = segment.type == GameTrack.TR_LFT ? 1.0 : -1.0;
		double angle = Math.atan2(sign * (x - segment.centerX), -sign * (y - segment.centerY));
		double offset = segment.type == GameTrack.TR_LFT ? positiveAngle(angle - segment.angleZS) : positiveAngle(segment.angleZS - angle);
		if (offset > segment.arc + CROSSING_PLAN_EPSILON)
			return Double.NaN;
		return clamp(offset, 0.0, segment.arc);
	}

	private static void appendCandidate(List<Candidate> candidates, int firstIndex, int secondIndex,
		double firstToStart, double secondToStart, double x, double y)
	{
		Candidate candidate = new Candidate();
		candidate.firstIndex = firstIndex;
		candidate.secondIndex = secondIndex;
		candidate.firstToStart = firstToStart;
		candidate.secondToStart = secondToStart;
		candidate.x = x;
		candidate.y = y;
		candidates.add(candidate);
	}

	private void intersectStraightStraight(CrossingSegment first, CrossingSegment second, List<Candidate> candidates)
	{
		PlanPoint firstStart = first.start;
		PlanPoint firstEnd = first.end;
		PlanPoint secondStart = second.start;
		PlanPoint secondEnd = second.end;
		double firstX = firstEnd.x - firstStart.x;
		double firstY = firstEnd.y - firstStart.y;
		double secondX = secondEnd.x - secondStart.x;
		double secondY = secondEnd.y - secondStart.y;
		double denominator = cross(firstX, firstY, secondX, secondY);
		if (Math.abs(denominator) <= CROSSING_PLAN_EPSILON)
			return;
		double startX = secondStart.x - firstStart.x;
		double startY = secondStart.y - firstStart.y;
		double firstParameter = cross(startX, startY, secondX, secondY) / denominator;
		double secondParameter = cross(startX, startY, firstX, firstY) / denominator;
		if (firstParameter < -CROSSING_PLAN_EPSILON || firstParameter > 1.0 + CROSSING_PLAN_EPSILON
			|| secondParameter < -CROSSING_PLAN_EPSILON || secondParameter > 1.0 + CROSSING_PLAN_EPSILON)
			return;
		firstParameter = clamp(firstParameter, 0.0, 1.0);
		secondParameter = clamp(secondParameter, 0.0, 1.0);
		appendCandidate(candidates, first.index, second.index, firstParameter * first.segment.length,
			secondParameter * second.segment.length, firstStart.x + firstParameter * firstX, firstStart.y + firstParameter * firstY);
	}

	private void intersectStraightCurve(CrossingSegment straight, CrossingSegment curve, boolean straightIsFirst, List<Candidate> candidates)
	{
		PlanPoint lineStart = straight.start;
		PlanPoint lineEnd = straight.end;
		double lineX = lineEnd.x - lineStart.x;
		double lineY = lineEnd.y - lineStart.y;
		double offsetX = lineStart.x - curve.segment.centerX;
		double offsetY = lineStart.y - curve.segment.centerY;
		double a = lineX * lineX + lineY * lineY;
		if (a <= CROSSING_PLAN_EPSILON)
			return;
		double b = 2.0 * (offsetX * lineX + offsetY * lineY);
		// the radius squared in floats, as trackgen's
		double c = offsetX * offsetX + offsetY * offsetY - curve.segment.radius * curve.segment.radius;
		double discriminant = b * b - 4.0 * a * c;
		if (discriminant < -CROSSING_PLAN_EPSILON)
			return;
		double[] lineParameter;
		if (discriminant <= CROSSING_PLAN_EPSILON)
		{
			lineParameter = new double[] { -b / (2.0 * a) };
		}
		else
		{
			discriminant = Math.sqrt(discriminant);
			lineParameter = new double[] { (-b - discriminant) / (2.0 * a), (-b + discriminant) / (2.0 * a) };
		}
		for (double parameter : lineParameter)
		{
			if (parameter < -CROSSING_PLAN_EPSILON || parameter > 1.0 + CROSSING_PLAN_EPSILON)
				continue;
			parameter = clamp(parameter, 0.0, 1.0);
			double x = lineStart.x + parameter * lineX;
			double y = lineStart.y + parameter * lineY;
			double curveToStart = getCurveToStart(curve.segment, x, y);
			if (Double.isNaN(curveToStart))
				continue;
			if (straightIsFirst)
				appendCandidate(candidates, straight.index, curve.index, parameter * straight.segment.length, curveToStart, x, y);
			else
				appendCandidate(candidates, curve.index, straight.index, curveToStart, parameter * straight.segment.length, x, y);
		}
	}

	private static void intersectCurveCurve(CrossingSegment first, CrossingSegment second, List<Candidate> candidates)
	{
		// in floats, as trackgen's
		double deltaX = second.segment.centerX - first.segment.centerX;
		double deltaY = second.segment.centerY - first.segment.centerY;
		double firstRadius = first.segment.radius;
		double secondRadius = second.segment.radius;
		double centerDistanceSquared = deltaX * deltaX + deltaY * deltaY;
		if (centerDistanceSquared <= CROSSING_PLAN_EPSILON)
			return;
		double centerDistance = Math.sqrt(centerDistanceSquared);
		if (centerDistance > firstRadius + secondRadius + CROSSING_PLAN_EPSILON
			|| centerDistance < Math.abs(firstRadius - secondRadius) - CROSSING_PLAN_EPSILON)
			return;
		double along = (firstRadius * firstRadius - secondRadius * secondRadius + centerDistanceSquared) / (2.0 * centerDistance);
		double height = firstRadius * firstRadius - along * along;
		if (height < -CROSSING_PLAN_EPSILON)
			return;
		height = height <= 0.0 ? 0.0 : Math.sqrt(height);
		double middleX = first.segment.centerX + along * deltaX / centerDistance;
		double middleY = first.segment.centerY + along * deltaY / centerDistance;
		double normalX = -deltaY / centerDistance;
		double normalY = deltaX / centerDistance;
		int pointCount = height <= CROSSING_PLAN_EPSILON ? 1 : 2;
		for (int pointIndex = 0; pointIndex < pointCount; pointIndex++)
		{
			double direction = pointIndex == 0 ? 1.0 : -1.0;
			double x = middleX + direction * height * normalX;
			double y = middleY + direction * height * normalY;
			double firstToStart = getCurveToStart(first.segment, x, y);
			double secondToStart = getCurveToStart(second.segment, x, y);
			if (!Double.isNaN(firstToStart) && !Double.isNaN(secondToStart))
				appendCandidate(candidates, first.index, second.index, firstToStart, secondToStart, x, y);
		}
	}

	private static boolean segmentIndicesAreAdjacent(int first, int second, int segmentCount)
	{
		int difference = Math.abs(first - second);
		return difference <= 1 || difference == segmentCount - 1;
	}

	private static boolean candidateBranchesMatch(Candidate first, Candidate second, int segmentCount)
	{
		boolean direct = segmentIndicesAreAdjacent(first.firstIndex, second.firstIndex, segmentCount)
			&& segmentIndicesAreAdjacent(first.secondIndex, second.secondIndex, segmentCount);
		boolean reversed = segmentIndicesAreAdjacent(first.firstIndex, second.secondIndex, segmentCount)
			&& segmentIndicesAreAdjacent(first.secondIndex, second.firstIndex, segmentCount);
		return direct || reversed;
	}

	private static int compareCandidates(Candidate first, Candidate second)
	{
		if (first.firstIndex != second.firstIndex)
			return Integer.compare(first.firstIndex, second.firstIndex);
		if (first.secondIndex != second.secondIndex)
			return Integer.compare(first.secondIndex, second.secondIndex);
		if (first.firstToStart != second.firstToStart)
			return first.firstToStart < second.firstToStart ? -1 : 1;
		return first.secondToStart < second.secondToStart ? -1 : first.secondToStart > second.secondToStart ? 1 : 0;
	}

	private void findCrossingCandidates(List<CrossingSegment> segments, List<Candidate> candidates)
	{
		List<Candidate> rawCandidates = new ArrayList<Candidate>();
		// a box around each centerline: a straight's ends, or a curve's arc
		final double margin = 1e-3;
		for (CrossingSegment segment : segments)
		{
			Seg seg = segment.segment;
			if (seg.type == GameTrack.TR_STR)
			{
				segment.start = getTrackCenterPoint(seg, 0.0, null);
				segment.end = getTrackCenterPoint(seg, seg.length, null);
				segment.minX = Math.min(segment.start.x, segment.end.x) - margin;
				segment.maxX = Math.max(segment.start.x, segment.end.x) + margin;
				segment.minY = Math.min(segment.start.y, segment.end.y) - margin;
				segment.maxY = Math.max(segment.start.y, segment.end.y) + margin;
			}
			else
			{
				// points along the arc, and the most it bulges out between them
				final int n = 8;
				double r = Math.abs(seg.radius);
				double bulge = r * (1 - Math.cos(Math.min(Math.PI, Math.abs(seg.arc) / (2 * n)))) + margin;
				segment.minX = segment.minY = Double.MAX_VALUE;
				segment.maxX = segment.maxY = -Double.MAX_VALUE;
				for (int k = 0; k <= n; k++)
				{
					double a = seg.type == GameTrack.TR_LFT ? seg.angleZS + seg.arc * k / n : seg.angleZS - seg.arc * k / n;
					double x = seg.type == GameTrack.TR_LFT ? seg.centerX + r * Math.sin(a) : seg.centerX - r * Math.sin(a);
					double y = seg.type == GameTrack.TR_LFT ? seg.centerY - r * Math.cos(a) : seg.centerY + r * Math.cos(a);
					segment.minX = Math.min(segment.minX, x - bulge);
					segment.maxX = Math.max(segment.maxX, x + bulge);
					segment.minY = Math.min(segment.minY, y - bulge);
					segment.maxY = Math.max(segment.maxY, y + bulge);
				}
				// a whole circle or more
				if (Math.abs(seg.arc) >= 2 * Math.PI)
				{
					segment.minX = seg.centerX - r - margin;
					segment.maxX = seg.centerX + r + margin;
					segment.minY = seg.centerY - r - margin;
					segment.maxY = seg.centerY + r + margin;
				}
			}
		}
		// each pair whose boxes overlap, found by sweeping along x; the lower index first,
		// as trackgen's pairs are (the candidates are sorted afterward)
		List<CrossingSegment> byX = new ArrayList<CrossingSegment>(segments);
		Collections.sort(byX, (a, b) -> Double.compare(a.minX, b.minX));
		for (int i = 0; i < byX.size(); i++)
		{
			CrossingSegment a = byX.get(i);
			for (int j = i + 1; j < byX.size() && byX.get(j).minX <= a.maxX; j++)
			{
				CrossingSegment b = byX.get(j);
				if (a.maxY < b.minY || b.maxY < a.minY)
					continue;
				CrossingSegment first = a.index < b.index ? a : b;
				CrossingSegment second = a.index < b.index ? b : a;
				if (segmentIndicesAreAdjacent(first.index, second.index, segments.size()))
					continue;
				if (first.segment.type == GameTrack.TR_STR && second.segment.type == GameTrack.TR_STR)
					intersectStraightStraight(first, second, rawCandidates);
				else if (first.segment.type == GameTrack.TR_STR)
					intersectStraightCurve(first, second, true, rawCandidates);
				else if (second.segment.type == GameTrack.TR_STR)
					intersectStraightCurve(second, first, false, rawCandidates);
				else
					intersectCurveCurve(first, second, rawCandidates);
			}
		}
		Collections.sort(rawCandidates, TrackCrossing::compareCandidates);
		for (Candidate raw : rawCandidates)
		{
			boolean duplicate = false;
			for (Candidate kept : candidates)
			{
				if (distanceSquared(raw.x, raw.y, kept.x, kept.y) <= CROSSING_POINT_EPSILON * CROSSING_POINT_EPSILON
					&& candidateBranchesMatch(raw, kept, segments.size()))
				{
					duplicate = true;
					break;
				}
			}
			if (!duplicate)
				candidates.add(raw);
		}
	}

	private static double wrapTrackDistance(double distance, double trackLength)
	{
		double wrapped = fmod(distance, trackLength);
		if (wrapped < 0.0)
			wrapped += trackLength;
		if (wrapped >= trackLength)
			wrapped = 0.0;
		return wrapped;
	}

	/** @return the segment this far along the track, with toStart on it in [1]; null when none */
	private static Object[] locateTrackDistance(List<CrossingSegment> segments, double trackLength, double distance)
	{
		double wrapped = wrapTrackDistance(distance, trackLength);
		for (int index = 0; index < segments.size(); index++)
		{
			double start = segments.get(index).startDistance;
			double end = start + segments.get(index).segment.length;
			if (wrapped >= start - CROSSING_PLAN_EPSILON && (wrapped < end - CROSSING_PLAN_EPSILON || index == segments.size() - 1))
			{
				Seg segment = segments.get(index).segment;
				double physicalOffset = clamp(wrapped - start, 0.0, segment.length);
				return new Object[] { segment, segmentToStart(segment, physicalOffset) };
			}
		}
		return null;
	}

	private static float getRenderedToStartStep(Seg segment, float trackStep)
	{
		if (segment.type == GameTrack.TR_STR)
			return trackStep;
		float radius = segment.type == GameTrack.TR_LFT ? segment.radiusr : segment.radiusl;
		if (radius <= CROSSING_PLAN_EPSILON || segment.radius <= CROSSING_PLAN_EPSILON)
			return trackStep;
		return trackStep / radius;
	}

	private static void appendSampleDistance(List<SampleDistance> distances, double distance, boolean renderedVertex,
		Seg segment, double toStart, double rangeStart, double rangeEnd)
	{
		if (distance >= rangeStart - CROSSING_PLAN_EPSILON && distance <= rangeEnd + CROSSING_PLAN_EPSILON)
		{
			SampleDistance sample = new SampleDistance();
			sample.distance = clamp(distance, rangeStart, rangeEnd);
			sample.renderedVertex = renderedVertex;
			sample.segment = segment;
			sample.toStart = toStart;
			distances.add(sample);
		}
	}

	private void collectSampleDistances(List<CrossingSegment> segments, double trackLength, double rangeStart, double rangeEnd,
		List<SampleDistance> distances)
	{
		int firstLap = (int) Math.floor(rangeStart / trackLength) - 1;
		int lastLap = (int) Math.ceil(rangeEnd / trackLength) + 1;
		appendSampleDistance(distances, rangeStart, false, null, 0.0, rangeStart, rangeEnd);
		appendSampleDistance(distances, rangeEnd, false, null, 0.0, rangeStart, rangeEnd);
		for (int lap = firstLap; lap <= lastLap; lap++)
		{
			for (CrossingSegment crossingSegment : segments)
			{
				Seg segment = crossingSegment.segment;
				double segmentStart = crossingSegment.startDistance + lap * trackLength;
				double segmentEnd = segmentStart + segment.length;
				if (segmentEnd < rangeStart || segmentStart > rangeEnd)
					continue;
				appendSampleDistance(distances, segmentStart, true, segment, 0.0, rangeStart, rangeEnd);
				appendSampleDistance(distances, segmentEnd, true, segment, segmentEndToStart(segment), rangeStart, rangeEnd);
				float sampleStep = getRenderedToStartStep(segment, (float) trackStep);
				if (!finite(sampleStep) || sampleStep <= CROSSING_PLAN_EPSILON)
					continue;
				// a float, as trackgen's
				for (float sampleOffset = sampleStep; sampleOffset < segmentEndToStart(segment) - CROSSING_PLAN_EPSILON; sampleOffset += sampleStep)
				{
					double sampleDistance = segmentStart + segmentPhysicalOffset(segment, sampleOffset);
					appendSampleDistance(distances, sampleDistance, true, segment, sampleOffset, rangeStart, rangeEnd);
				}
			}
		}
		// a stable sort
		Collections.sort(distances, (first, second) -> first.distance < second.distance ? -1 : first.distance > second.distance ? 1 : 0);
		for (int index = 1; index < distances.size();)
		{
			SampleDistance previous = distances.get(index - 1);
			SampleDistance current = distances.get(index);
			if (Math.abs(previous.distance - current.distance) <= CROSSING_PLAN_EPSILON)
			{
				if (previous.renderedVertex && current.renderedVertex && previous.segment != current.segment)
				{
					index++;
					continue;
				}
				boolean previousAtSegmentEnd = previous.renderedVertex && previous.segment != null
					&& Math.abs(previous.toStart - segmentEndToStart(previous.segment)) <= CROSSING_PLAN_EPSILON;
				boolean currentAtSegmentEnd = current.renderedVertex && current.segment != null
					&& Math.abs(current.toStart - segmentEndToStart(current.segment)) <= CROSSING_PLAN_EPSILON;
				if (current.renderedVertex && (!previous.renderedVertex || currentAtSegmentEnd && !previousAtSegmentEnd))
					previous.set(current);
				else
					previous.renderedVertex = previous.renderedVertex || current.renderedVertex;
				distances.remove(index);
			}
			else
			{
				index++;
			}
		}
	}

	private static Seg getOutermostSegment(Seg mainSegment, int side)
	{
		Seg segment = mainSegment;
		while (segment.side(side) != null)
			segment = segment.side(side);
		return segment;
	}

	private BoundaryPoint sampleSegmentBoundaryHeight(Seg segment, double toStart, double toRight)
	{
		BoundaryPoint point = new BoundaryPoint();
		PlanPoint xy = localToGlobal(segment, toStart, toRight);
		point.x = xy.x;
		point.y = xy.y;
		point.z = heightL(segment, toStart, toRight);
		point.distance = 0.0;
		point.minimumSurfaceZ = point.z;
		point.maximumSurfaceZ = point.z;
		point.barrierTopZ = point.z;
		point.renderedVertex = false;
		return point;
	}

	/** includeSegmentSurfaceHeights: the lowest and highest edge of the segment, into minMax */
	private void includeSegmentSurfaceHeights(Seg segment, double toStart, double[] minMax)
	{
		BoundaryPoint right = sampleSegmentBoundaryHeight(segment, toStart, 0.0);
		BoundaryPoint left = sampleSegmentBoundaryHeight(segment, toStart, GameTrack.width(segment, (float) toStart));
		minMax[0] = Math.min(minMax[0], Math.min(right.z, left.z));
		minMax[1] = Math.max(minMax[1], Math.max(right.z, left.z));
	}

	private BoundaryPoint sampleOuterBoundary(Seg mainSegment, double toStart, int side, boolean renderedVertex)
	{
		BoundaryPoint point = new BoundaryPoint();
		GameTrack.Barrier barrier = mainSegment.barrier[side];
		Seg outerSegment = getOutermostSegment(mainSegment, side);
		double wallWidth = 0.0;
		double segmentEnd = segmentEndToStart(mainSegment);
		boolean atStart = Math.abs(toStart) <= CROSSING_PLAN_EPSILON;
		boolean atEnd = Math.abs(toStart - segmentEnd) <= CROSSING_PLAN_EPSILON;
		double edgeToRight = side == TR_SIDE_RGT ? 0.0 : GameTrack.width(outerSegment, (float) toStart);
		if (renderedVertex && (atStart || atEnd))
		{
			int vertexIndex = side == TR_SIDE_RGT ? (atStart ? GameTrack.TR_SR : GameTrack.TR_ER) : (atStart ? GameTrack.TR_SL : GameTrack.TR_EL);
			point.x = outerSegment.vertex[vertexIndex][0];
			point.y = outerSegment.vertex[vertexIndex][1];
			point.z = outerSegment.vertex[vertexIndex][2];
		}
		else
		{
			PlanPoint xy = localToGlobal(outerSegment, toStart, edgeToRight);
			point.x = xy.x;
			point.y = xy.y;
			point.z = heightL(outerSegment, toStart, edgeToRight);
		}
		double[] minMax = { DBL_MAX, -DBL_MAX };
		includeSegmentSurfaceHeights(mainSegment, toStart, minMax);
		for (int surfaceSide = 0; surfaceSide < 2; surfaceSide++)
		{
			for (Seg surfaceSegment = mainSegment.side(surfaceSide); surfaceSegment != null; surfaceSegment = surfaceSegment.side(surfaceSide))
				includeSegmentSurfaceHeights(surfaceSegment, toStart, minMax);
		}
		point.minimumSurfaceZ = minMax[0];
		point.maximumSurfaceZ = minMax[1];
		point.barrierTopZ = point.maximumSurfaceZ;
		if (barrier != null)
		{
			if (barrier.style == GameTrack.TR_WALL && finite(barrier.width) && barrier.width > 0.0)
				wallWidth = barrier.width;
			if (finite(barrier.height) && barrier.height > 0.0)
				point.barrierTopZ = Math.max(point.barrierTopZ, point.z + barrier.height);
		}
		if (wallWidth > 0.0)
		{
			PlanPoint xy = localToGlobal(outerSegment, toStart, side == TR_SIDE_RGT ? edgeToRight - wallWidth : edgeToRight + wallWidth);
			point.x = xy.x;
			point.y = xy.y;
		}
		if (renderedVertex)
		{
			point.x = (float) point.x;
			point.y = (float) point.y;
			point.z = (float) point.z;
		}
		point.distance = 0.0;
		point.renderedVertex = renderedVertex;
		return point;
	}

	private boolean sampleBoundaryLine(List<CrossingSegment> segments, double trackLength, double centerDistance,
		double halfExtent, int side, BoundaryLine line)
	{
		List<SampleDistance> distances = new ArrayList<SampleDistance>();
		double rangeStart = centerDistance - halfExtent;
		double rangeEnd = centerDistance + halfExtent;
		collectSampleDistances(segments, trackLength, rangeStart, rangeEnd, distances);
		for (SampleDistance sample : distances)
		{
			Seg segment;
			double toStart;
			if (sample.renderedVertex && sample.segment != null)
			{
				segment = sample.segment;
				toStart = sample.toStart;
			}
			else
			{
				Object[] located = locateTrackDistance(segments, trackLength, sample.distance);
				if (located == null)
					return false;
				segment = (Seg) located[0];
				toStart = (Double) located[1];
			}
			BoundaryPoint point = sampleOuterBoundary(segment, toStart, side, sample.renderedVertex);
			point.distance = sample.distance;
			if (!line.points.isEmpty())
			{
				BoundaryPoint previous = line.back();
				if (Math.abs(previous.distance - point.distance) <= CROSSING_PLAN_EPSILON
					&& distanceSquared(previous.x, previous.y, point.x, point.y) <= CROSSING_RENDERED_ENDPOINT_EPSILON * CROSSING_RENDERED_ENDPOINT_EPSILON)
				{
					previous.minimumSurfaceZ = Math.min(previous.minimumSurfaceZ, point.minimumSurfaceZ);
					previous.maximumSurfaceZ = Math.max(previous.maximumSurfaceZ, point.maximumSurfaceZ);
					previous.barrierTopZ = Math.max(previous.barrierTopZ, point.barrierTopZ);
					previous.renderedVertex = previous.renderedVertex || point.renderedVertex;
					continue;
				}
			}
			line.points.add(point);
		}
		return line.points.size() >= 2;
	}

	private static PlanPoint getOffsetNormal(BoundaryPoint first, BoundaryPoint second, int side)
	{
		double deltaX = second.x - first.x;
		double deltaY = second.y - first.y;
		double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		if (length <= CROSSING_PLAN_EPSILON)
			return new PlanPoint(0.0, 0.0);
		if (side == TR_SIDE_RGT)
			return new PlanPoint(deltaY / length, -deltaX / length);
		return new PlanPoint(-deltaY / length, deltaX / length);
	}

	private static PlanPoint getBoundaryNormal(BoundaryLine line, double distance, int side)
	{
		List<BoundaryPoint> points = line.points;
		if (points.size() < 2)
			return new PlanPoint(0.0, 0.0);
		for (int index = 0; index + 1 < points.size(); index++)
		{
			if (distance <= points.get(index + 1).distance + CROSSING_PLAN_EPSILON)
				return getOffsetNormal(points.get(index), points.get(index + 1), side);
		}
		return getOffsetNormal(points.get(points.size() - 2), line.back(), side);
	}

	private static BoundaryLine offsetBoundaryLine(BoundaryLine line, int side, double gap)
	{
		BoundaryLine offset = new BoundaryLine();
		for (BoundaryPoint point : line.points)
			offset.points.add(point.copy());
		if (gap <= CROSSING_PLAN_EPSILON || line.points.size() < 2)
			return offset;
		List<BoundaryPoint> points = line.points;
		for (int index = 0; index < points.size(); index++)
		{
			PlanPoint firstNormal, secondNormal;
			if (index == 0)
			{
				firstNormal = getOffsetNormal(points.get(0), points.get(1), side);
				secondNormal = firstNormal;
			}
			else if (index == points.size() - 1)
			{
				firstNormal = getOffsetNormal(points.get(index - 1), points.get(index), side);
				secondNormal = firstNormal;
			}
			else
			{
				firstNormal = getOffsetNormal(points.get(index - 1), points.get(index), side);
				secondNormal = getOffsetNormal(points.get(index), points.get(index + 1), side);
			}
			PlanPoint miter = new PlanPoint(firstNormal.x + secondNormal.x, firstNormal.y + secondNormal.y);
			double miterLength = Math.sqrt(miter.x * miter.x + miter.y * miter.y);
			if (miterLength <= CROSSING_PLAN_EPSILON)
			{
				miter = secondNormal;
			}
			else
			{
				miter.x /= miterLength;
				miter.y /= miterLength;
			}
			double denominator = miter.x * secondNormal.x + miter.y * secondNormal.y;
			if (denominator <= CROSSING_PLAN_EPSILON)
			{
				miter = secondNormal;
				denominator = 1.0;
			}
			offset.points.get(index).x += miter.x * gap / denominator;
			offset.points.get(index).y += miter.y * gap / denominator;
		}
		return offset;
	}

	/** @return a copy of the point on the line this far along it */
	private static BoundaryPoint interpolateBoundaryPoint(BoundaryLine line, double distance)
	{
		List<BoundaryPoint> points = line.points;
		if (distance <= line.front().distance)
			return line.front().copy();
		if (distance >= line.back().distance)
			return line.back().copy();
		for (int index = 0; index + 1 < points.size(); index++)
		{
			BoundaryPoint first = points.get(index);
			BoundaryPoint second = points.get(index + 1);
			if (distance <= second.distance + CROSSING_PLAN_EPSILON)
			{
				if (Math.abs(distance - first.distance) <= CROSSING_PLAN_EPSILON)
					return first.copy();
				if (Math.abs(distance - second.distance) <= CROSSING_PLAN_EPSILON)
					return second.copy();
				double length = second.distance - first.distance;
				double parameter = length <= CROSSING_PLAN_EPSILON ? 0.0 : (distance - first.distance) / length;
				BoundaryPoint result = new BoundaryPoint();
				result.distance = distance;
				result.x = first.x + parameter * (second.x - first.x);
				result.y = first.y + parameter * (second.y - first.y);
				result.z = first.z + parameter * (second.z - first.z);
				result.minimumSurfaceZ = first.minimumSurfaceZ + parameter * (second.minimumSurfaceZ - first.minimumSurfaceZ);
				result.maximumSurfaceZ = first.maximumSurfaceZ + parameter * (second.maximumSurfaceZ - first.maximumSurfaceZ);
				result.barrierTopZ = first.barrierTopZ + parameter * (second.barrierTopZ - first.barrierTopZ);
				result.renderedVertex = false;
				return result;
			}
		}
		return line.back().copy();
	}

	/** @return copies of the line's points between two distances: its ends there and its rendered points between */
	private static List<BoundaryPoint> sliceBoundaryLine(BoundaryLine line, double startDistance, double endDistance)
	{
		List<BoundaryPoint> result = new ArrayList<BoundaryPoint>();
		boolean reverse = startDistance > endDistance;
		double minimum = reverse ? endDistance : startDistance;
		double maximum = reverse ? startDistance : endDistance;
		result.add(interpolateBoundaryPoint(line, minimum));
		for (BoundaryPoint point : line.points)
		{
			if (point.renderedVertex && point.distance > minimum + CROSSING_POINT_EPSILON && point.distance < maximum - CROSSING_POINT_EPSILON)
				result.add(point.copy());
		}
		result.add(interpolateBoundaryPoint(line, maximum));
		if (reverse)
			Collections.reverse(result);
		return result;
	}

	/** @return a copy of the rendered point at the end, or the nearest one beyond it away from interiorDistance; null when none */
	private static BoundaryPoint findRenderedExtension(BoundaryLine line, double endpointDistance, double interiorDistance)
	{
		boolean searchBefore = interiorDistance > endpointDistance;
		for (BoundaryPoint point : line.points)
		{
			if (point.renderedVertex && Math.abs(point.distance - endpointDistance) <= CROSSING_POINT_EPSILON)
				return point.copy();
		}
		BoundaryPoint extension = null;
		for (BoundaryPoint point : line.points)
		{
			if (!point.renderedVertex)
				continue;
			if (searchBefore)
			{
				if (point.distance >= endpointDistance - CROSSING_PLAN_EPSILON)
					continue;
				if (extension == null || point.distance > extension.distance)
					extension = point;
			}
			else
			{
				if (point.distance <= endpointDistance + CROSSING_PLAN_EPSILON)
					continue;
				if (extension == null || point.distance < extension.distance)
					extension = point;
			}
		}
		return extension == null ? null : extension.copy();
	}

	private static double outerPortalDistance(double socketDistance, double boundaryDistance, double interiorDistance)
	{
		return interiorDistance > socketDistance ? Math.min(socketDistance, boundaryDistance) : Math.max(socketDistance, boundaryDistance);
	}

	private static LineIntersection findBoundaryIntersection(BoundaryLine first, BoundaryLine second, double crossingX, double crossingY)
	{
		LineIntersection result = new LineIntersection();
		for (int firstIndex = 0; firstIndex + 1 < first.points.size(); firstIndex++)
		{
			BoundaryPoint firstStart = first.points.get(firstIndex);
			BoundaryPoint firstEnd = first.points.get(firstIndex + 1);
			double firstX = firstEnd.x - firstStart.x;
			double firstY = firstEnd.y - firstStart.y;
			for (int secondIndex = 0; secondIndex + 1 < second.points.size(); secondIndex++)
			{
				BoundaryPoint secondStart = second.points.get(secondIndex);
				BoundaryPoint secondEnd = second.points.get(secondIndex + 1);
				double secondX = secondEnd.x - secondStart.x;
				double secondY = secondEnd.y - secondStart.y;
				double denominator = cross(firstX, firstY, secondX, secondY);
				if (Math.abs(denominator) <= CROSSING_PLAN_EPSILON)
					continue;
				double startX = secondStart.x - firstStart.x;
				double startY = secondStart.y - firstStart.y;
				double firstParameter = cross(startX, startY, secondX, secondY) / denominator;
				double secondParameter = cross(startX, startY, firstX, firstY) / denominator;
				if (firstParameter < -CROSSING_PLAN_EPSILON || firstParameter > 1.0 + CROSSING_PLAN_EPSILON
					|| secondParameter < -CROSSING_PLAN_EPSILON || secondParameter > 1.0 + CROSSING_PLAN_EPSILON)
					continue;
				firstParameter = clamp(firstParameter, 0.0, 1.0);
				secondParameter = clamp(secondParameter, 0.0, 1.0);
				double x = firstStart.x + firstParameter * firstX;
				double y = firstStart.y + firstParameter * firstY;
				double d2 = distanceSquared(x, y, crossingX, crossingY);
				if (d2 < result.distanceSquared)
				{
					result.found = true;
					result.firstDistance = firstStart.distance + firstParameter * (firstEnd.distance - firstStart.distance);
					result.secondDistance = secondStart.distance + secondParameter * (secondEnd.distance - secondStart.distance);
					result.x = x;
					result.y = y;
					result.distanceSquared = d2;
				}
			}
		}
		return result;
	}

	private static void appendPolygonSlice(List<BoundaryPoint> polygon, List<BoundaryPoint> slice)
	{
		for (BoundaryPoint point : slice)
		{
			if (!polygon.isEmpty())
			{
				BoundaryPoint back = polygon.get(polygon.size() - 1);
				if (distanceSquared(back.x, back.y, point.x, point.y) <= CROSSING_PLAN_EPSILON * CROSSING_PLAN_EPSILON)
					continue;
			}
			polygon.add(point);
		}
	}

	private static void simplifyPolygon(List<BoundaryPoint> polygon)
	{
		boolean changed = true;
		if (polygon.size() > 1)
		{
			BoundaryPoint front = polygon.get(0), back = polygon.get(polygon.size() - 1);
			if (distanceSquared(front.x, front.y, back.x, back.y) <= CROSSING_PLAN_EPSILON * CROSSING_PLAN_EPSILON)
				polygon.remove(polygon.size() - 1);
		}
		while (changed && polygon.size() > 3)
		{
			changed = false;
			for (int index = 0; index < polygon.size(); index++)
			{
				BoundaryPoint previous = polygon.get((index + polygon.size() - 1) % polygon.size());
				BoundaryPoint current = polygon.get(index);
				BoundaryPoint next = polygon.get((index + 1) % polygon.size());
				double firstX = current.x - previous.x;
				double firstY = current.y - previous.y;
				double secondX = next.x - current.x;
				double secondY = next.y - current.y;
				if (distanceSquared(previous.x, previous.y, current.x, current.y) <= CROSSING_PLAN_EPSILON * CROSSING_PLAN_EPSILON
					|| !current.renderedVertex && Math.abs(cross(firstX, firstY, secondX, secondY)) <= CROSSING_PLAN_EPSILON
						* (Math.sqrt(firstX * firstX + firstY * firstY) + Math.sqrt(secondX * secondX + secondY * secondY)))
				{
					polygon.remove(index);
					changed = true;
					break;
				}
			}
		}
	}

	private static double polygonArea(List<BoundaryPoint> polygon)
	{
		double area = 0.0;
		for (int index = 0; index < polygon.size(); index++)
		{
			BoundaryPoint first = polygon.get(index);
			BoundaryPoint second = polygon.get((index + 1) % polygon.size());
			area += first.x * second.y - second.x * first.y;
		}
		return area / 2.0;
	}

	private static boolean pointInsideTriangle(BoundaryPoint point, BoundaryPoint first, BoundaryPoint second, BoundaryPoint third, double orientation)
	{
		double firstCross = cross(second.x - first.x, second.y - first.y, point.x - first.x, point.y - first.y) * orientation;
		double secondCross = cross(third.x - second.x, third.y - second.y, point.x - second.x, point.y - second.y) * orientation;
		double thirdCross = cross(first.x - third.x, first.y - third.y, point.x - third.x, point.y - third.y) * orientation;
		return firstCross > CROSSING_PLAN_EPSILON && secondCross > CROSSING_PLAN_EPSILON && thirdCross > CROSSING_PLAN_EPSILON;
	}

	/** ear clipping */
	private static boolean triangulatePolygon(List<BoundaryPoint> polygon, List<int[]> triangles)
	{
		double area = polygonArea(polygon);
		double orientation = area >= 0.0 ? 1.0 : -1.0;
		int guard = 0;
		if (polygon.size() < 3 || Math.abs(area) <= CROSSING_PLAN_EPSILON)
			return false;
		List<Integer> indices = new ArrayList<Integer>();
		for (int index = 0; index < polygon.size(); index++)
			indices.add(index);
		while (indices.size() > 3 && guard < polygon.size() * polygon.size())
		{
			boolean earFound = false;
			for (int index = 0; index < indices.size(); index++)
			{
				int previousIndex = indices.get((index + indices.size() - 1) % indices.size());
				int currentIndex = indices.get(index);
				int nextIndex = indices.get((index + 1) % indices.size());
				BoundaryPoint previous = polygon.get(previousIndex);
				BoundaryPoint current = polygon.get(currentIndex);
				BoundaryPoint next = polygon.get(nextIndex);
				double corner = cross(current.x - previous.x, current.y - previous.y, next.x - current.x, next.y - current.y) * orientation;
				if (corner <= CROSSING_PLAN_EPSILON)
					continue;
				boolean containsPoint = false;
				for (int vertexIndex : indices)
				{
					if (vertexIndex == previousIndex || vertexIndex == currentIndex || vertexIndex == nextIndex)
						continue;
					if (pointInsideTriangle(polygon.get(vertexIndex), previous, current, next, orientation))
					{
						containsPoint = true;
						break;
					}
				}
				if (containsPoint)
					continue;
				triangles.add(new int[] { previousIndex, currentIndex, nextIndex });
				indices.remove(index);
				earFound = true;
				break;
			}
			if (!earFound)
				return false;
			guard++;
		}
		if (indices.size() == 3)
			triangles.add(new int[] { indices.get(0), indices.get(1), indices.get(2) });
		return !triangles.isEmpty();
	}

	/** resolveSurfaceTexture: the texture of the surface named after a material, and its size in size[0] */
	private String resolveSurfaceTexture(String material, double[] size)
	{
		String defaultTexture = "concrete".equals(material) ? "tr-concrete_nmm.rgb" : "tr-asphalt.rgb";
		if (size != null)
			size[0] = CROSSING_TEXTURE_SIZE;
		Surface surface = RoadTexture.findSurface(editorFrame, material);
		if (size != null && surface != null)
		{
			size[0] = Double.isNaN(surface.getTextureSize()) ? CROSSING_TEXTURE_SIZE : (float) surface.getTextureSize();
			if (!finite(size[0]) || size[0] <= 0.0)
				size[0] = CROSSING_TEXTURE_SIZE;
		}
		return surface == null || surface.getTextureName() == null ? defaultTexture : surface.getTextureName();
	}

	/** getBoundaryMaterial: the outermost side's surface, else the main segment's */
	private static String getBoundaryMaterial(Seg mainSegment, int side)
	{
		Seg outer = getOutermostSegment(mainSegment, side);
		if (outer.surface != null)
			return outer.surface;
		if (mainSegment.surface != null)
			return mainSegment.surface;
		return "asphalt";
	}

	/** resolveCrossingTexture: the surface the Terrain Generation names for a part, else the material's */
	private CrossingTexture resolveCrossingTexture(int surfaceIndex, String fallbackSurface)
	{
		CrossingTexture texture = new CrossingTexture();
		double[] size = new double[1];
		texture.name = resolveSurfaceTexture(fallbackSurface, size);
		texture.sizeU = size[0];
		texture.sizeV = texture.sizeU;
		String surfaceName = editorFrame.getTrackData().getTerrainGeneration().getCrossingBridgeSurface(surfaceIndex);
		if (surfaceName == null || surfaceName.isEmpty())
			surfaceName = TerrainGeneration.DEFAULT_CROSSING_BRIDGE_SURFACES[surfaceIndex];
		Surface surface = RoadTexture.findSurface(editorFrame, surfaceName);
		String name = surface == null ? null : surface.getTextureName();
		if (name == null || name.isEmpty())
			return texture;
		texture.name = name;
		double sizeU = Double.isNaN(surface.getTextureSize()) ? texture.sizeU : (float) surface.getTextureSize();
		if (finite(sizeU) && sizeU > 0.0)
			texture.sizeU = sizeU;
		double sizeV = Double.isNaN(surface.getTextureSizeV()) ? texture.sizeU : (float) surface.getTextureSizeV();
		texture.sizeV = finite(sizeV) && sizeV > 0.0 ? sizeV : texture.sizeU;
		return texture;
	}

	private static final int	UNDERSIDE	= 0;
	private static final int	SIDE		= 1;
	private static final int	SOCKET		= 2;
	private static final int	FLOOR		= 3;

	/** boundaryTextureDistance: the horizontal length along the line to where (x, y) is closest */
	private static double boundaryTextureDistance(BoundaryLine line, double x, double y)
	{
		double along = 0.0;
		double closest = DBL_MAX;
		double result = 0.0;
		for (int index = 0; index + 1 < line.points.size(); index++)
		{
			BoundaryPoint first = line.points.get(index);
			BoundaryPoint second = line.points.get(index + 1);
			double dx = second.x - first.x;
			double dy = second.y - first.y;
			double squaredLength = dx * dx + dy * dy;
			double length = Math.sqrt(squaredLength);
			if (length > CROSSING_PLAN_EPSILON)
			{
				double parameter = clamp(((x - first.x) * dx + (y - first.y) * dy) / squaredLength, 0.0, 1.0);
				double distance = distanceSquared(x, y, first.x + parameter * dx, first.y + parameter * dy);
				if (distance < closest)
				{
					closest = distance;
					result = along + parameter * length;
				}
			}
			along += length;
		}
		return result;
	}

	private static double boundaryTextureOrigin(BoundaryLine line, double distance)
	{
		BoundaryPoint point = interpolateBoundaryPoint(line, distance);
		return boundaryTextureDistance(line, point.x, point.y);
	}

	/** fitVerticalTexture: scaled up to fit the parts' height, keeping its aspect; their top in top[0] */
	private static CrossingTexture fitVerticalTexture(CrossingTexture texture, CrossingObject[] parts, double[] top)
	{
		double minimum = DBL_MAX;
		double maximum = -DBL_MAX;
		for (CrossingObject part : parts)
		{
			for (double[] vertex : part.vertices)
			{
				minimum = Math.min(minimum, vertex[2]);
				maximum = Math.max(maximum, vertex[2]);
			}
		}
		top[0] = maximum;
		double scale = Math.max(1.0, (maximum - minimum) / texture.sizeV);
		CrossingTexture fitted = new CrossingTexture();
		fitted.name = texture.name;
		fitted.sizeU = texture.sizeU * scale;
		fitted.sizeV = texture.sizeV * scale;
		return fitted;
	}

	private static void mapVerticalTexture(CrossingObject object, BoundaryLine line, double origin, CrossingTexture texture, double top)
	{
		object.texture = texture.name;
		for (double[] point : object.vertices)
		{
			point[3] = (boundaryTextureDistance(line, point[0], point[1]) - origin) / texture.sizeU;
			point[4] = 1.0 + (point[2] - top) / texture.sizeV;
		}
	}

	private static void mapFillerTexture(CrossingObject object, BoundaryPoint corner, BoundaryLine socket, double origin,
		PlanPoint lowerTangent, CrossingTexture texture, double top)
	{
		object.texture = texture.name;
		PlanPoint horizontal = new PlanPoint(lowerTangent.x, lowerTangent.y);
		double largest = 0.0;
		for (int[] triangle : object.triangles)
		{
			double[] a = object.vertices.get(triangle[0]);
			double[] b = object.vertices.get(triangle[1]);
			double[] c = object.vertices.get(triangle[2]);
			double nx = (b[1] - a[1]) * (c[2] - a[2]) - (b[2] - a[2]) * (c[1] - a[1]);
			double ny = (b[2] - a[2]) * (c[0] - a[0]) - (b[0] - a[0]) * (c[2] - a[2]);
			double length = Math.sqrt(nx * nx + ny * ny);
			if (length > largest)
			{
				largest = length;
				horizontal.x = ny / length;
				horizontal.y = -nx / length;
			}
		}
		if (horizontal.x * lowerTangent.x + horizontal.y * lowerTangent.y < 0.0)
		{
			horizontal.x = -horizontal.x;
			horizontal.y = -horizontal.y;
		}
		double anchor = boundaryTextureDistance(socket, corner.x, corner.y) - origin;
		for (double[] point : object.vertices)
		{
			point[3] = (anchor + (point[0] - corner.x) * horizontal.x + (point[1] - corner.y) * horizontal.y) / texture.sizeU;
			point[4] = 1.0 + (point[2] - top) / texture.sizeV;
		}
	}

	/** mapCrossingTextures: the textures and their coordinates, once all the parts are built */
	private void mapCrossingTextures(Seg lowerSegment, PlanPoint center, PlanPoint upperTangent, PlanPoint lowerTangent,
		BoundaryLine[] upperLines, BoundaryLine[] socketLines, double upperDistance, double lowerDistance,
		BoundaryPoint[][] corners, Geometry geometry)
	{
		CrossingTexture underside = resolveCrossingTexture(UNDERSIDE, "concrete");
		PlanPoint across = new PlanPoint(-upperTangent.y, upperTangent.x);
		double minimumU = DBL_MAX;
		double maximumU = -DBL_MAX;
		double minimumV = DBL_MAX;
		for (double[] point : geometry.underside.vertices)
		{
			double x = point[0] - center.x;
			double y = point[1] - center.y;
			double u = x * across.x + y * across.y;
			minimumU = Math.min(minimumU, u);
			maximumU = Math.max(maximumU, u);
			minimumV = Math.min(minimumV, x * upperTangent.x + y * upperTangent.y);
		}
		double scale = Math.max(1.0, (maximumU - minimumU) / underside.sizeU);
		geometry.underside.texture = underside.name;
		for (double[] point : geometry.underside.vertices)
		{
			double x = point[0] - center.x;
			double y = point[1] - center.y;
			point[3] = (x * across.x + y * across.y - minimumU) / (underside.sizeU * scale);
			point[4] = (x * upperTangent.x + y * upperTangent.y - minimumV) / (underside.sizeV * scale);
		}

		CrossingObject[] fascia = { geometry.bridgeRight, geometry.bridgeLeft };
		CrossingObject[] sockets = { geometry.socketRight, geometry.socketLeft };
		CrossingObject[] socketParts = { sockets[0], sockets[1], geometry.filler[0][0], geometry.filler[0][1],
			geometry.filler[1][0], geometry.filler[1][1] };
		CrossingObject[] floors = { geometry.floorRight, geometry.floorLeft };
		double[] sideTop = new double[1], socketTop = new double[1];
		CrossingTexture sideTexture = fitVerticalTexture(resolveCrossingTexture(SIDE, "concrete"), fascia, sideTop);
		CrossingTexture socketTexture = fitVerticalTexture(resolveCrossingTexture(SOCKET, "concrete"), socketParts, socketTop);
		for (int side = 0; side < 2; side++)
		{
			mapVerticalTexture(fascia[side], upperLines[side], boundaryTextureOrigin(upperLines[side], upperDistance), sideTexture, sideTop[0]);
			double socketOrigin = boundaryTextureOrigin(socketLines[side], lowerDistance);
			mapVerticalTexture(sockets[side], socketLines[side], socketOrigin, socketTexture, socketTop[0]);
			for (int upperSide = 0; upperSide < 2; upperSide++)
				mapFillerTexture(geometry.filler[upperSide][side], corners[upperSide][side], socketLines[side], socketOrigin, lowerTangent, socketTexture, socketTop[0]);
			CrossingTexture floorTexture = resolveCrossingTexture(FLOOR, getBoundaryMaterial(lowerSegment, side));
			floors[side].texture = floorTexture.name;
			for (double[] point : floors[side].vertices)
			{
				double x = point[0] - center.x;
				double y = point[1] - center.y;
				point[3] = (x * lowerTangent.x + y * lowerTangent.y) / floorTexture.sizeU;
				point[4] = (-x * lowerTangent.y + y * lowerTangent.x) / floorTexture.sizeV;
			}
		}
	}

	private static void appendObjectPoint(CrossingObject object, double x, double y, double z, double u, double v)
	{
		object.vertices.add(new double[] { x, y, z, u, v });
	}

	private static void appendStripTriangles(CrossingObject object, int stationCount)
	{
		for (int station = 0; station + 1 < stationCount; station++)
		{
			object.triangles.add(new int[] { 2 * station, 2 * station + 1, 2 * station + 2 });
			object.triangles.add(new int[] { 2 * station + 1, 2 * station + 3, 2 * station + 2 });
		}
	}

	private static int appendObjectTriangle(CrossingObject object, int first, int second, int third)
	{
		object.triangles.add(new int[] { first, second, third });
		return object.triangles.size() - 1;
	}

	private static int findPolygonVertex(List<BoundaryPoint> polygon, BoundaryPoint point)
	{
		for (int index = 0; index < polygon.size(); index++)
		{
			if (distanceSquared(polygon.get(index).x, polygon.get(index).y, point.x, point.y) <= CROSSING_POINT_EPSILON * CROSSING_POINT_EPSILON)
				return index;
		}
		return -1;
	}

	private static boolean collectPolygonIndices(List<BoundaryPoint> polygon, List<BoundaryPoint> line, List<Integer> indices)
	{
		for (BoundaryPoint point : line)
		{
			int vertexIndex = findPolygonVertex(polygon, point);
			if (vertexIndex < 0)
				return false;
			if (indices.isEmpty() || indices.get(indices.size() - 1) != vertexIndex)
				indices.add(vertexIndex);
		}
		return !indices.isEmpty();
	}

	/** appendBridgeStripTriangles: the two long edges zipped together by distance */
	private static boolean appendBridgeStripTriangles(List<BoundaryPoint> polygon, List<BoundaryPoint> right, List<BoundaryPoint> left, CrossingObject object)
	{
		List<Integer> rightIndices = new ArrayList<Integer>();
		List<Integer> leftIndices = new ArrayList<Integer>();
		if (!collectPolygonIndices(polygon, right, rightIndices) || !collectPolygonIndices(polygon, left, leftIndices)
			|| rightIndices.size() != right.size() || leftIndices.size() != left.size() || rightIndices.isEmpty() || leftIndices.isEmpty())
			return false;
		int rightIndex = 0, leftIndex = 0;
		if (rightIndices.size() == 1)
		{
			for (leftIndex = 0; leftIndex + 1 < leftIndices.size(); leftIndex++)
				appendObjectTriangle(object, rightIndices.get(0), leftIndices.get(leftIndex), leftIndices.get(leftIndex + 1));
			return true;
		}
		if (leftIndices.size() == 1)
		{
			for (rightIndex = 0; rightIndex + 1 < rightIndices.size(); rightIndex++)
				appendObjectTriangle(object, rightIndices.get(rightIndex), leftIndices.get(0), rightIndices.get(rightIndex + 1));
			return true;
		}
		double rightSpan = right.get(right.size() - 1).distance - right.get(0).distance;
		double leftSpan = left.get(left.size() - 1).distance - left.get(0).distance;
		if (Math.abs(rightSpan) <= CROSSING_PLAN_EPSILON || rightSpan * leftSpan <= 0.0)
			return false;
		double direction = rightSpan > 0.0 ? 1.0 : -1.0;
		while (rightIndex + 1 < rightIndices.size() || leftIndex + 1 < leftIndices.size())
		{
			if (rightIndex + 1 >= rightIndices.size())
			{
				appendObjectTriangle(object, rightIndices.get(rightIndex), leftIndices.get(leftIndex), leftIndices.get(leftIndex + 1));
				leftIndex++;
			}
			else if (leftIndex + 1 >= leftIndices.size())
			{
				appendObjectTriangle(object, rightIndices.get(rightIndex), leftIndices.get(leftIndex), rightIndices.get(rightIndex + 1));
				rightIndex++;
			}
			else
			{
				double rightDistance = direction * right.get(rightIndex + 1).distance;
				double leftDistance = direction * left.get(leftIndex + 1).distance;
				if (Math.abs(rightDistance - leftDistance) <= CROSSING_POINT_EPSILON)
				{
					appendObjectTriangle(object, rightIndices.get(rightIndex), leftIndices.get(leftIndex), rightIndices.get(rightIndex + 1));
					appendObjectTriangle(object, leftIndices.get(leftIndex), leftIndices.get(leftIndex + 1), rightIndices.get(rightIndex + 1));
					rightIndex++;
					leftIndex++;
				}
				else if (rightDistance < leftDistance)
				{
					appendObjectTriangle(object, rightIndices.get(rightIndex), leftIndices.get(leftIndex), rightIndices.get(rightIndex + 1));
					rightIndex++;
				}
				else
				{
					appendObjectTriangle(object, rightIndices.get(rightIndex), leftIndices.get(leftIndex), leftIndices.get(leftIndex + 1));
					leftIndex++;
				}
			}
		}
		return true;
	}

	/** orientBridgeCap: the cap from the right edge's end to the left's, or null when it doesn't join them */
	private static List<BoundaryPoint> orientBridgeCap(List<BoundaryPoint> cap, BoundaryPoint right, BoundaryPoint left)
	{
		if (cap.size() < 2)
			return null;
		List<BoundaryPoint> row = new ArrayList<BoundaryPoint>(cap);
		BoundaryPoint front = row.get(0), back = row.get(row.size() - 1);
		double e2 = CROSSING_POINT_EPSILON * CROSSING_POINT_EPSILON;
		if (distanceSquared(front.x, front.y, right.x, right.y) <= e2 && distanceSquared(back.x, back.y, left.x, left.y) <= e2)
			return row;
		if (distanceSquared(front.x, front.y, left.x, left.y) <= e2 && distanceSquared(back.x, back.y, right.x, right.y) <= e2)
		{
			Collections.reverse(row);
			return row;
		}
		return null;
	}

	private static double[] collectRowProgress(List<BoundaryPoint> row)
	{
		if (row.size() < 2)
			return null;
		double[] progress = new double[row.size()];
		double total = 0.0;
		for (int index = 1; index < row.size(); index++)
		{
			total += Math.sqrt(distanceSquared(row.get(index - 1).x, row.get(index - 1).y, row.get(index).x, row.get(index).y));
			progress[index] = total;
		}
		if (total <= CROSSING_PLAN_EPSILON)
			return null;
		for (int index = 1; index < progress.length; index++)
			progress[index] /= total;
		return progress;
	}

	/** appendBridgeRowTriangles: a cap folded into the row of points next to it */
	private static boolean appendBridgeRowTriangles(List<BoundaryPoint> polygon, List<BoundaryPoint> firstRow, List<BoundaryPoint> secondRow, CrossingObject object)
	{
		List<Integer> firstIndices = new ArrayList<Integer>();
		List<Integer> secondIndices = new ArrayList<Integer>();
		if (!collectPolygonIndices(polygon, firstRow, firstIndices) || !collectPolygonIndices(polygon, secondRow, secondIndices)
			|| firstIndices.size() != firstRow.size() || secondIndices.size() != secondRow.size())
			return false;
		double[] firstProgress = collectRowProgress(firstRow);
		double[] secondProgress = firstProgress == null ? null : collectRowProgress(secondRow);
		if (firstProgress == null || secondProgress == null)
			return false;
		int firstIndex = 0, secondIndex = 0;
		while (firstIndex + 1 < firstIndices.size() || secondIndex + 1 < secondIndices.size())
		{
			if (firstIndex + 1 >= firstIndices.size())
			{
				appendObjectTriangle(object, firstIndices.get(firstIndex), secondIndices.get(secondIndex), secondIndices.get(secondIndex + 1));
				secondIndex++;
			}
			else if (secondIndex + 1 >= secondIndices.size())
			{
				appendObjectTriangle(object, firstIndices.get(firstIndex), secondIndices.get(secondIndex), firstIndices.get(firstIndex + 1));
				firstIndex++;
			}
			else if (Math.abs(firstProgress[firstIndex + 1] - secondProgress[secondIndex + 1]) <= CROSSING_POINT_EPSILON)
			{
				appendObjectTriangle(object, firstIndices.get(firstIndex), secondIndices.get(secondIndex), firstIndices.get(firstIndex + 1));
				appendObjectTriangle(object, firstIndices.get(firstIndex + 1), secondIndices.get(secondIndex), secondIndices.get(secondIndex + 1));
				firstIndex++;
				secondIndex++;
			}
			else if (firstProgress[firstIndex + 1] < secondProgress[secondIndex + 1])
			{
				appendObjectTriangle(object, firstIndices.get(firstIndex), secondIndices.get(secondIndex), firstIndices.get(firstIndex + 1));
				firstIndex++;
			}
			else
			{
				appendObjectTriangle(object, firstIndices.get(firstIndex), secondIndices.get(secondIndex), secondIndices.get(secondIndex + 1));
				secondIndex++;
			}
		}
		return true;
	}

	private static boolean appendBridgeCapTriangles(List<BoundaryPoint> polygon, List<BoundaryPoint> cap, CrossingObject object)
	{
		List<Integer> capIndices = new ArrayList<Integer>();
		if (!collectPolygonIndices(polygon, cap, capIndices))
			return false;
		if (capIndices.size() < 3)
			return true;
		List<BoundaryPoint> capPolygon = new ArrayList<BoundaryPoint>();
		for (int index : capIndices)
			capPolygon.add(polygon.get(index));
		if (Math.abs(polygonArea(capPolygon)) <= CROSSING_PLAN_EPSILON)
			return true;
		List<int[]> capTriangles = new ArrayList<int[]>();
		if (!triangulatePolygon(capPolygon, capTriangles))
			return false;
		for (int[] triangle : capTriangles)
			appendObjectTriangle(object, capIndices.get(triangle[0]), capIndices.get(triangle[1]), capIndices.get(triangle[2]));
		return true;
	}

	/** triangulateBridgeUnderside: the caps as end cells, the rest a strip */
	private static boolean triangulateBridgeUnderside(List<BoundaryPoint> polygon, List<BoundaryPoint> right, List<BoundaryPoint> left,
		List<BoundaryPoint> firstCap, List<BoundaryPoint> secondCap, CrossingObject object)
	{
		int firstTriangle = object.triangles.size();
		if (right.size() < 3 || left.size() < 3)
		{
			return appendBridgeStripTriangles(polygon, right, left, object) && appendBridgeCapTriangles(polygon, firstCap, object)
				&& appendBridgeCapTriangles(polygon, secondCap, object) && object.triangles.size() > firstTriangle;
		}
		List<BoundaryPoint> firstRow = orientBridgeCap(firstCap, right.get(0), left.get(0));
		List<BoundaryPoint> secondRow = orientBridgeCap(secondCap, right.get(right.size() - 1), left.get(left.size() - 1));
		if (firstRow == null || secondRow == null)
			return false;
		List<BoundaryPoint> firstInteriorRow = new ArrayList<BoundaryPoint>();
		firstInteriorRow.add(right.get(1));
		firstInteriorRow.add(left.get(1));
		List<BoundaryPoint> secondInteriorRow = new ArrayList<BoundaryPoint>();
		secondInteriorRow.add(right.get(right.size() - 2));
		secondInteriorRow.add(left.get(left.size() - 2));
		List<BoundaryPoint> middleRight = new ArrayList<BoundaryPoint>(right.subList(1, right.size() - 1));
		List<BoundaryPoint> middleLeft = new ArrayList<BoundaryPoint>(left.subList(1, left.size() - 1));
		return appendBridgeRowTriangles(polygon, firstRow, firstInteriorRow, object)
			&& appendBridgeStripTriangles(polygon, middleRight, middleLeft, object)
			&& appendBridgeRowTriangles(polygon, secondInteriorRow, secondRow, object) && object.triangles.size() > firstTriangle;
	}

	/** orientObjectTriangle: turned over unless its normal points toward the target direction */
	private static void orientObjectTriangle(CrossingObject object, int triangleIndex, double targetX, double targetY, double targetZ)
	{
		int[] triangle = object.triangles.get(triangleIndex);
		double[] first = object.vertices.get(triangle[0]);
		double[] second = object.vertices.get(triangle[1]);
		double[] third = object.vertices.get(triangle[2]);
		double firstX = second[0] - first[0];
		double firstY = second[1] - first[1];
		double firstZ = second[2] - first[2];
		double secondX = third[0] - first[0];
		double secondY = third[1] - first[1];
		double secondZ = third[2] - first[2];
		double normalX = firstY * secondZ - firstZ * secondY;
		double normalY = firstZ * secondX - firstX * secondZ;
		double normalZ = firstX * secondY - firstY * secondX;
		if (normalX * targetX + normalY * targetY + normalZ * targetZ < 0.0)
		{
			int swap = triangle[1];
			triangle[1] = triangle[2];
			triangle[2] = swap;
		}
	}

	private static void orientObjectVertically(CrossingObject object, double direction)
	{
		for (int triangleIndex = 0; triangleIndex < object.triangles.size(); triangleIndex++)
			orientObjectTriangle(object, triangleIndex, 0.0, 0.0, direction);
	}

	private static void removeCollapsedPlanTriangles(CrossingObject object)
	{
		object.triangles.removeIf(triangle -> {
			double[] first = object.vertices.get(triangle[0]);
			double[] second = object.vertices.get(triangle[1]);
			double[] third = object.vertices.get(triangle[2]);
			return Math.abs(cross(second[0] - first[0], second[1] - first[1], third[0] - first[0], third[1] - first[1])) <= CROSSING_PLAN_EPSILON;
		});
	}

	/** buildSocketWall: the wall beside the lower part, from its ground up to the underside, facing the lower part */
	private static void buildSocketWall(BoundaryLine boundary, BoundaryLine socket, double firstDistance, double secondDistance,
		int side, double undersideZ, double textureSize, CrossingObject object)
	{
		List<BoundaryPoint> boundaryPoints = sliceBoundaryLine(boundary, firstDistance, secondDistance);
		List<BoundaryPoint> socketPoints = sliceBoundaryLine(socket, firstDistance, secondDistance);
		double along = 0.0;
		int firstTriangle = object.triangles.size();
		if (boundaryPoints.size() != socketPoints.size())
			return;
		for (int index = 0; index < socketPoints.size(); index++)
		{
			BoundaryPoint point = socketPoints.get(index);
			if (index > 0)
			{
				double[] previous = object.vertices.get(object.vertices.size() - 2);
				along += Math.sqrt(distanceSquared(previous[0], previous[1], point.x, point.y));
			}
			appendObjectPoint(object, point.x, point.y, point.z, along / textureSize, 0.0);
			appendObjectPoint(object, point.x, point.y, undersideZ, along / textureSize, (undersideZ - point.z) / textureSize);
		}
		appendStripTriangles(object, socketPoints.size());
		for (int index = 0; index + 1 < socketPoints.size(); index++)
		{
			BoundaryPoint firstBoundary = boundaryPoints.get(index);
			BoundaryPoint secondBoundary = boundaryPoints.get(index + 1);
			BoundaryPoint firstSocket = socketPoints.get(index);
			BoundaryPoint secondSocket = socketPoints.get(index + 1);
			double targetX = firstBoundary.x + secondBoundary.x - firstSocket.x - secondSocket.x;
			double targetY = firstBoundary.y + secondBoundary.y - firstSocket.y - secondSocket.y;
			if (targetX * targetX + targetY * targetY <= CROSSING_PLAN_EPSILON * CROSSING_PLAN_EPSILON)
			{
				PlanPoint normal = getBoundaryNormal(boundary, (firstBoundary.distance + secondBoundary.distance) / 2.0, side);
				targetX = -normal.x;
				targetY = -normal.y;
			}
			orientObjectTriangle(object, firstTriangle + 2 * index, targetX, targetY, 0.0);
			orientObjectTriangle(object, firstTriangle + 2 * index + 1, targetX, targetY, 0.0);
		}
	}

	/** buildFloorStrip: the ground between the lower part's edge and the wall, facing up */
	private static void buildFloorStrip(BoundaryLine boundary, BoundaryLine socket, double firstDistance, double secondDistance,
		BoundaryPoint firstExtension, BoundaryPoint secondExtension, double textureSize, CrossingObject object)
	{
		List<BoundaryPoint> boundaryPoints = sliceBoundaryLine(boundary, firstDistance, secondDistance);
		List<BoundaryPoint> socketPoints = sliceBoundaryLine(socket, firstDistance, secondDistance);
		if (boundaryPoints.size() != socketPoints.size())
			return;
		boolean hasGap = false;
		for (int index = 0; index < boundaryPoints.size(); index++)
		{
			if (distanceSquared(boundaryPoints.get(index).x, boundaryPoints.get(index).y, socketPoints.get(index).x, socketPoints.get(index).y)
				> CROSSING_PLAN_EPSILON * CROSSING_PLAN_EPSILON)
			{
				hasGap = true;
				break;
			}
		}
		if (!hasGap)
			return;
		double along = 0.0;
		BoundaryPoint previousBoundary = null;
		for (int index = 0; index < boundaryPoints.size(); index++)
		{
			BoundaryPoint boundaryPoint;
			BoundaryPoint socketPoint = socketPoints.get(index);
			if (index == 0)
				boundaryPoint = firstExtension;
			else if (index + 1 == boundaryPoints.size())
				boundaryPoint = secondExtension;
			else
				boundaryPoint = boundaryPoints.get(index);
			if (index > 0)
				along += Math.sqrt(distanceSquared(previousBoundary.x, previousBoundary.y, boundaryPoint.x, boundaryPoint.y));
			double width = Math.sqrt(distanceSquared(boundaryPoint.x, boundaryPoint.y, socketPoint.x, socketPoint.y));
			appendObjectPoint(object, boundaryPoint.x, boundaryPoint.y, boundaryPoint.z, along / textureSize, 0.0);
			appendObjectPoint(object, socketPoint.x, socketPoint.y, socketPoint.z, along / textureSize, width / textureSize);
			previousBoundary = boundaryPoint;
		}
		appendStripTriangles(object, boundaryPoints.size());
		orientObjectVertically(object, 1.0);
	}

	private static int findObjectVertex(CrossingObject object, BoundaryPoint point, double z)
	{
		for (int index = 0; index < object.vertices.size(); index++)
		{
			double[] vertex = object.vertices.get(index);
			if (distanceSquared(vertex[0], vertex[1], point.x, point.y) <= CROSSING_PLAN_EPSILON * CROSSING_PLAN_EPSILON
				&& Math.abs(vertex[2] - z) <= CROSSING_HEIGHT_EPSILON)
				return index;
		}
		return -1;
	}

	/** appendObjectFace: one triangle copied into another object, reusing its identical points */
	private static void appendObjectFace(CrossingObject source, int triangleIndex, CrossingObject object)
	{
		int[] vertices = new int[3];
		for (int reference = 0; reference < 3; reference++)
		{
			double[] point = source.vertices.get(source.triangles.get(triangleIndex)[reference]);
			int index;
			for (index = 0; index < object.vertices.size(); index++)
			{
				double[] existing = object.vertices.get(index);
				if (existing[0] == point[0] && existing[1] == point[1] && existing[2] == point[2] && existing[3] == point[3] && existing[4] == point[4])
					break;
			}
			vertices[reference] = index;
			if (index == object.vertices.size())
				object.vertices.add(point.clone());
		}
		appendObjectTriangle(object, vertices[0], vertices[1], vertices[2]);
	}

	/** appendSocketPortal: a portal closed, the part in the bridge side's plane to it, the rest to the return */
	private static void appendSocketPortal(BoundaryPoint bridgeTop, BoundaryPoint socketCorner, BoundaryPoint floorExtension,
		PlanPoint fasciaNormal, double undersideZ, double textureSize, CrossingObject socket, CrossingObject fascia, CrossingObject filler)
	{
		CrossingObject portal = new CrossingObject();
		int firstVertex = 0;
		int baseVertex = findObjectVertex(socket, socketCorner, socketCorner.z);
		int topVertex = findObjectVertex(socket, socketCorner, undersideZ);
		boolean reverse = false;
		// continue the wall's winding across its end
		for (int[] triangle : socket.triangles)
		{
			for (int edge = 0; edge < 3; edge++)
			{
				if (triangle[edge] == topVertex && triangle[(edge + 1) % 3] == baseVertex)
					reverse = true;
			}
		}
		double upperU = Math.sqrt(distanceSquared(bridgeTop.x, bridgeTop.y, socketCorner.x, socketCorner.y)) / textureSize;
		double floorU = -Math.sqrt(distanceSquared(floorExtension.x, floorExtension.y, socketCorner.x, socketCorner.y)) / textureSize;
		appendObjectPoint(portal, bridgeTop.x, bridgeTop.y, bridgeTop.z, upperU, (bridgeTop.z - socketCorner.z) / textureSize);
		appendObjectPoint(portal, socketCorner.x, socketCorner.y, undersideZ, 0.0, (undersideZ - socketCorner.z) / textureSize);
		appendObjectPoint(portal, socketCorner.x, socketCorner.y, socketCorner.z, 0.0, 0.0);
		appendObjectPoint(portal, floorExtension.x, floorExtension.y, floorExtension.z, floorU, (floorExtension.z - socketCorner.z) / textureSize);
		// with the upper end right above the socket corner, the other diagonal keeps the underside point
		int[][] regular = { { 0, 1, 2 }, { 0, 2, 3 } };
		int[][] aligned = { { 0, 1, 3 }, { 1, 2, 3 } };
		boolean alignedEndpoint = upperU * textureSize <= CROSSING_POINT_EPSILON;
		int[][] triangles = alignedEndpoint ? aligned : regular;
		boolean coplanarReturn = Math.abs((floorExtension.x - socketCorner.x) * fasciaNormal.x
			+ (floorExtension.y - socketCorner.y) * fasciaNormal.y) <= CROSSING_POINT_EPSILON;
		for (int index = 0; index < 2; index++)
		{
			int first = firstVertex + triangles[index][0];
			int second = firstVertex + triangles[index][1];
			int third = firstVertex + triangles[index][2];
			double[] a = portal.vertices.get(first);
			double[] b = portal.vertices.get(second);
			double[] c = portal.vertices.get(third);
			double nx = (b[1] - a[1]) * (c[2] - a[2]) - (b[2] - a[2]) * (c[1] - a[1]);
			double ny = (b[2] - a[2]) * (c[0] - a[0]) - (b[0] - a[0]) * (c[2] - a[2]);
			double nz = cross(b[0] - a[0], b[1] - a[1], c[0] - a[0], c[1] - a[1]);
			if (nx * nx + ny * ny + nz * nz <= CROSSING_PLAN_EPSILON * CROSSING_PLAN_EPSILON)
				continue;
			appendObjectTriangle(portal, first, reverse ? third : second, reverse ? second : third);
			appendObjectFace(portal, portal.triangles.size() - 1, coplanarReturn || !alignedEndpoint && index == 0 ? fascia : filler);
		}
	}

	private static void appendTerrainPortalMask(BoundaryPoint bridgeTop, BoundaryPoint socketCorner, BoundaryPoint floorExtension, CrossingObject object)
	{
		int firstVertex = object.vertices.size();
		appendObjectPoint(object, bridgeTop.x, bridgeTop.y, bridgeTop.z, 0.0, 0.0);
		appendObjectPoint(object, socketCorner.x, socketCorner.y, socketCorner.z, 0.0, 0.0);
		appendObjectPoint(object, floorExtension.x, floorExtension.y, floorExtension.z, 0.0, 0.0);
		appendObjectTriangle(object, firstVertex, firstVertex + 1, firstVertex + 2);
	}

	/** buildBridgeSide: a side of the bridge, from the underside up to the upper part's edge, facing out */
	private static boolean buildBridgeSide(BoundaryLine boundary, List<BoundaryPoint> slice, BoundaryPoint firstExtension,
		BoundaryPoint secondExtension, int side, double undersideZ, double textureSize, CrossingObject underside, CrossingObject object)
	{
		List<Integer> bottomIndices = new ArrayList<Integer>();
		List<Integer> topIndices = new ArrayList<Integer>();
		int firstTriangle = object.triangles.size();
		if (slice.size() < 2 || firstExtension.z <= undersideZ + CROSSING_HEIGHT_EPSILON || secondExtension.z <= undersideZ + CROSSING_HEIGHT_EPSILON)
			return false;
		List<BoundaryPoint> topPoints = new ArrayList<BoundaryPoint>(slice);
		topPoints.set(0, firstExtension);
		topPoints.set(topPoints.size() - 1, secondExtension);
		for (int index = 0; index < slice.size(); index++)
		{
			int bottomIndex = findObjectVertex(underside, slice.get(index), undersideZ);
			if (bottomIndex < 0 || topPoints.get(index).z <= undersideZ + CROSSING_HEIGHT_EPSILON)
				return false;
			double[] bottom = underside.vertices.get(bottomIndex);
			bottomIndices.add(object.vertices.size());
			object.vertices.add(bottom.clone());
			topIndices.add(object.vertices.size());
			BoundaryPoint top = topPoints.get(index);
			appendObjectPoint(object, top.x, top.y, top.z, bottom[3], bottom[4] + (top.z - undersideZ) / textureSize);
		}
		for (int index = 0; index + 1 < slice.size(); index++)
		{
			appendObjectTriangle(object, topIndices.get(index), bottomIndices.get(index), topIndices.get(index + 1));
			appendObjectTriangle(object, bottomIndices.get(index), bottomIndices.get(index + 1), topIndices.get(index + 1));
		}
		for (int index = firstTriangle; index < object.triangles.size(); index++)
		{
			int segmentIndex = (index - firstTriangle) / 2;
			double distance = (topPoints.get(segmentIndex).distance + topPoints.get(segmentIndex + 1).distance) / 2.0;
			PlanPoint normal = getBoundaryNormal(boundary, distance, side);
			orientObjectTriangle(object, index, normal.x, normal.y, 0.0);
		}
		return true;
	}

	private static double pointToSegmentDistance(double pointX, double pointY, BoundaryPoint first, BoundaryPoint second)
	{
		double segmentX = second.x - first.x;
		double segmentY = second.y - first.y;
		double lengthSquared = segmentX * segmentX + segmentY * segmentY;
		double parameter = 0.0;
		if (lengthSquared > CROSSING_PLAN_EPSILON)
			parameter = clamp(((pointX - first.x) * segmentX + (pointY - first.y) * segmentY) / lengthSquared, 0.0, 1.0);
		return Math.sqrt(distanceSquared(pointX, pointY, first.x + parameter * segmentX, first.y + parameter * segmentY));
	}

	private static double minimumLineDistance(List<BoundaryPoint> first, List<BoundaryPoint> second)
	{
		double minimum = DBL_MAX;
		for (BoundaryPoint point : first)
			for (int segmentIndex = 0; segmentIndex + 1 < second.size(); segmentIndex++)
				minimum = Math.min(minimum, pointToSegmentDistance(point.x, point.y, second.get(segmentIndex), second.get(segmentIndex + 1)));
		for (BoundaryPoint point : second)
			for (int segmentIndex = 0; segmentIndex + 1 < first.size(); segmentIndex++)
				minimum = Math.min(minimum, pointToSegmentDistance(point.x, point.y, first.get(segmentIndex), first.get(segmentIndex + 1)));
		return minimum == DBL_MAX ? 0.0 : minimum;
	}

	private static double minimumZ(List<BoundaryPoint> points)
	{
		double minimum = DBL_MAX;
		for (BoundaryPoint point : points)
			minimum = Math.min(minimum, point.minimumSurfaceZ);
		return minimum;
	}

	private static double maximumBarrierTopZ(List<BoundaryPoint> points)
	{
		double maximum = -DBL_MAX;
		for (BoundaryPoint point : points)
			maximum = Math.max(maximum, point.barrierTopZ);
		return maximum;
	}

	/** buildCrossingGeometry: one crossing, with the sampling widened up to twice when the edges don't meet */
	private boolean buildCrossingGeometry(List<CrossingSegment> segments, double trackLength, Candidate candidate,
		boolean firstIsUpper, int crossingIndex, Geometry geometry)
	{
		CrossingSegment upperSegment = segments.get(firstIsUpper ? candidate.firstIndex : candidate.secondIndex);
		CrossingSegment lowerSegment = segments.get(firstIsUpper ? candidate.secondIndex : candidate.firstIndex);
		double upperToStart = firstIsUpper ? candidate.firstToStart : candidate.secondToStart;
		double lowerToStart = firstIsUpper ? candidate.secondToStart : candidate.firstToStart;
		double upperCenterDistance = upperSegment.startDistance + segmentPhysicalOffset(upperSegment.segment, upperToStart);
		double lowerCenterDistance = lowerSegment.startDistance + segmentPhysicalOffset(lowerSegment.segment, lowerToStart);
		PlanPoint upperTangent = getTrackTangent(upperSegment.segment, upperToStart);
		PlanPoint lowerTangent = getTrackTangent(lowerSegment.segment, lowerToStart);
		BoundaryPoint upperRightCenter = sampleOuterBoundary(upperSegment.segment, upperToStart, TR_SIDE_RGT, false);
		BoundaryPoint upperLeftCenter = sampleOuterBoundary(upperSegment.segment, upperToStart, TR_SIDE_LFT, false);
		BoundaryPoint lowerRightCenter = sampleOuterBoundary(lowerSegment.segment, lowerToStart, TR_SIDE_RGT, false);
		BoundaryPoint lowerLeftCenter = sampleOuterBoundary(lowerSegment.segment, lowerToStart, TR_SIDE_LFT, false);
		double sine = Math.abs(cross(upperTangent.x, upperTangent.y, lowerTangent.x, lowerTangent.y));
		PlanPoint upperCenter = getTrackCenterPoint(upperSegment.segment, upperToStart, null);
		PlanPoint lowerCenter = getTrackCenterPoint(lowerSegment.segment, lowerToStart, null);
		if (sine <= CROSSING_PLAN_EPSILON)
			return false;
		double upperWidth = Math.max(Math.sqrt(distanceSquared(upperCenter.x, upperCenter.y, upperRightCenter.x, upperRightCenter.y)),
			Math.sqrt(distanceSquared(upperCenter.x, upperCenter.y, upperLeftCenter.x, upperLeftCenter.y)));
		double lowerWidth = Math.max(Math.sqrt(distanceSquared(lowerCenter.x, lowerCenter.y, lowerRightCenter.x, lowerRightCenter.y)),
			Math.sqrt(distanceSquared(lowerCenter.x, lowerCenter.y, lowerLeftCenter.x, lowerLeftCenter.y))) + socketGap;
		double halfExtent = 1.5 * (upperWidth + lowerWidth) / sine + 5.0;
		double branchDistance = Math.abs(upperCenterDistance - lowerCenterDistance);
		branchDistance = Math.min(branchDistance, trackLength - branchDistance);
		double maximumExtent = Math.min(trackLength * 0.2, branchDistance * 0.45);
		if (maximumExtent <= 0.0)
			return false;
		halfExtent = Math.min(halfExtent, maximumExtent);

		for (int attempt = 0; attempt < 3; attempt++)
		{
			BoundaryLine upperRight = new BoundaryLine();
			BoundaryLine upperLeft = new BoundaryLine();
			BoundaryLine lowerRight = new BoundaryLine();
			BoundaryLine lowerLeft = new BoundaryLine();
			if (!sampleBoundaryLine(segments, trackLength, upperCenterDistance, halfExtent, TR_SIDE_RGT, upperRight)
				|| !sampleBoundaryLine(segments, trackLength, upperCenterDistance, halfExtent, TR_SIDE_LFT, upperLeft)
				|| !sampleBoundaryLine(segments, trackLength, lowerCenterDistance, halfExtent, TR_SIDE_RGT, lowerRight)
				|| !sampleBoundaryLine(segments, trackLength, lowerCenterDistance, halfExtent, TR_SIDE_LFT, lowerLeft))
				return false;
			BoundaryLine socketRight = offsetBoundaryLine(lowerRight, TR_SIDE_RGT, socketGap);
			BoundaryLine socketLeft = offsetBoundaryLine(lowerLeft, TR_SIDE_LFT, socketGap);
			LineIntersection rightRight = findBoundaryIntersection(upperRight, socketRight, candidate.x, candidate.y);
			LineIntersection rightLeft = findBoundaryIntersection(upperRight, socketLeft, candidate.x, candidate.y);
			LineIntersection leftRight = findBoundaryIntersection(upperLeft, socketRight, candidate.x, candidate.y);
			LineIntersection leftLeft = findBoundaryIntersection(upperLeft, socketLeft, candidate.x, candidate.y);
			LineIntersection rightRightBoundary = findBoundaryIntersection(upperRight, lowerRight, candidate.x, candidate.y);
			LineIntersection rightLeftBoundary = findBoundaryIntersection(upperRight, lowerLeft, candidate.x, candidate.y);
			LineIntersection leftRightBoundary = findBoundaryIntersection(upperLeft, lowerRight, candidate.x, candidate.y);
			LineIntersection leftLeftBoundary = findBoundaryIntersection(upperLeft, lowerLeft, candidate.x, candidate.y);
			if (!rightRight.found || !rightLeft.found || !leftRight.found || !leftLeft.found || !rightRightBoundary.found
				|| !rightLeftBoundary.found || !leftRightBoundary.found || !leftLeftBoundary.found)
			{
				if (halfExtent >= maximumExtent - CROSSING_PLAN_EPSILON)
					return false;
				halfExtent = Math.min(halfExtent * 2.0, maximumExtent);
				continue;
			}
			BoundaryPoint upperRightFirstExtension = findRenderedExtension(upperRight, rightRight.firstDistance, rightLeft.firstDistance);
			BoundaryPoint upperRightSecondExtension = upperRightFirstExtension == null ? null
				: findRenderedExtension(upperRight, rightLeft.firstDistance, rightRight.firstDistance);
			BoundaryPoint upperLeftFirstExtension = upperRightSecondExtension == null ? null
				: findRenderedExtension(upperLeft, leftRight.firstDistance, leftLeft.firstDistance);
			BoundaryPoint upperLeftSecondExtension = upperLeftFirstExtension == null ? null
				: findRenderedExtension(upperLeft, leftLeft.firstDistance, leftRight.firstDistance);
			BoundaryPoint lowerRightFirstExtension = upperLeftSecondExtension == null ? null
				: findRenderedExtension(lowerRight, outerPortalDistance(rightRight.secondDistance, rightRightBoundary.secondDistance,
					leftRight.secondDistance), leftRight.secondDistance);
			BoundaryPoint lowerRightSecondExtension = lowerRightFirstExtension == null ? null
				: findRenderedExtension(lowerRight, outerPortalDistance(leftRight.secondDistance, leftRightBoundary.secondDistance,
					rightRight.secondDistance), rightRight.secondDistance);
			BoundaryPoint lowerLeftFirstExtension = lowerRightSecondExtension == null ? null
				: findRenderedExtension(lowerLeft, outerPortalDistance(rightLeft.secondDistance, rightLeftBoundary.secondDistance,
					leftLeft.secondDistance), leftLeft.secondDistance);
			BoundaryPoint lowerLeftSecondExtension = lowerLeftFirstExtension == null ? null
				: findRenderedExtension(lowerLeft, outerPortalDistance(leftLeft.secondDistance, leftLeftBoundary.secondDistance,
					rightLeft.secondDistance), rightLeft.secondDistance);
			if (lowerLeftSecondExtension == null)
			{
				if (halfExtent >= maximumExtent - CROSSING_PLAN_EPSILON)
					return false;
				halfExtent = Math.min(halfExtent * 2.0, maximumExtent);
				continue;
			}

			List<BoundaryPoint> upperRightSlice = sliceBoundaryLine(upperRight, rightRight.firstDistance, rightLeft.firstDistance);
			List<BoundaryPoint> upperLeftSlice = sliceBoundaryLine(upperLeft, leftRight.firstDistance, leftLeft.firstDistance);
			List<BoundaryPoint> lowerRightSlice = sliceBoundaryLine(lowerRight, rightRight.secondDistance, leftRight.secondDistance);
			List<BoundaryPoint> lowerLeftSlice = sliceBoundaryLine(lowerLeft, rightLeft.secondDistance, leftLeft.secondDistance);
			geometry.minimumUpperZ = Math.min(minimumZ(upperRightSlice), minimumZ(upperLeftSlice));
			double undersideZ = geometry.minimumUpperZ - bridgeThickness;
			double lowerMaximumZ = Math.max(maximumBarrierTopZ(lowerRightSlice), maximumBarrierTopZ(lowerLeftSlice));
			if (undersideZ <= lowerMaximumZ + CROSSING_HEIGHT_EPSILON)
				return false;

			List<BoundaryPoint> bridgeLeftCap = sliceBoundaryLine(socketLeft, rightLeft.secondDistance, leftLeft.secondDistance);
			List<BoundaryPoint> bridgeRightCap = sliceBoundaryLine(socketRight, leftRight.secondDistance, rightRight.secondDistance);
			List<BoundaryPoint> bridgePolygon = new ArrayList<BoundaryPoint>();
			appendPolygonSlice(bridgePolygon, upperRightSlice);
			appendPolygonSlice(bridgePolygon, bridgeLeftCap);
			appendPolygonSlice(bridgePolygon, sliceBoundaryLine(upperLeft, leftLeft.firstDistance, leftRight.firstDistance));
			appendPolygonSlice(bridgePolygon, bridgeRightCap);
			simplifyPolygon(bridgePolygon);
			if (bridgePolygon.size() < 3)
				return false;

			double[] size = new double[1];
			geometry.underside.texture = resolveSurfaceTexture("concrete", size);
			double concreteTextureSize = size[0];
			for (BoundaryPoint point : bridgePolygon)
				appendObjectPoint(geometry.underside, point.x, point.y, undersideZ, point.x / concreteTextureSize, point.y / concreteTextureSize);
			if (!triangulateBridgeUnderside(bridgePolygon, upperRightSlice, upperLeftSlice, bridgeRightCap, bridgeLeftCap, geometry.underside))
			{
				geometry.underside.vertices.clear();
				geometry.underside.triangles.clear();
				return false;
			}
			removeCollapsedPlanTriangles(geometry.underside);
			if (geometry.underside.triangles.isEmpty())
			{
				geometry.underside.vertices.clear();
				return false;
			}
			orientObjectVertically(geometry.underside, -1.0);
			geometry.bridgeRight.texture = geometry.underside.texture;
			geometry.bridgeLeft.texture = geometry.underside.texture;
			if (!buildBridgeSide(upperRight, upperRightSlice, upperRightFirstExtension, upperRightSecondExtension, TR_SIDE_RGT, undersideZ,
				concreteTextureSize, geometry.underside, geometry.bridgeRight)
				|| !buildBridgeSide(upperLeft, upperLeftSlice, upperLeftFirstExtension, upperLeftSecondExtension, TR_SIDE_LFT, undersideZ,
					concreteTextureSize, geometry.underside, geometry.bridgeLeft))
				return false;

			geometry.socketRight.texture = geometry.underside.texture;
			geometry.socketLeft.texture = geometry.underside.texture;
			geometry.floorRight.texture = resolveSurfaceTexture(getBoundaryMaterial(lowerSegment.segment, TR_SIDE_RGT), size);
			double rightFloorTextureSize = size[0];
			geometry.floorLeft.texture = resolveSurfaceTexture(getBoundaryMaterial(lowerSegment.segment, TR_SIDE_LFT), size);
			double leftFloorTextureSize = size[0];
			buildSocketWall(lowerRight, socketRight, rightRight.secondDistance, leftRight.secondDistance, TR_SIDE_RGT, undersideZ,
				concreteTextureSize, geometry.socketRight);
			buildSocketWall(lowerLeft, socketLeft, rightLeft.secondDistance, leftLeft.secondDistance, TR_SIDE_LFT, undersideZ,
				concreteTextureSize, geometry.socketLeft);
			buildFloorStrip(lowerRight, socketRight, rightRight.secondDistance, leftRight.secondDistance, lowerRightFirstExtension,
				lowerRightSecondExtension, rightFloorTextureSize, geometry.floorRight);
			buildFloorStrip(lowerLeft, socketLeft, rightLeft.secondDistance, leftLeft.secondDistance, lowerLeftFirstExtension,
				lowerLeftSecondExtension, leftFloorTextureSize, geometry.floorLeft);
			appendSocketPortal(upperRightFirstExtension, interpolateBoundaryPoint(socketRight, rightRight.secondDistance),
				lowerRightFirstExtension, getBoundaryNormal(upperRight, rightRight.firstDistance, TR_SIDE_RGT), undersideZ,
				concreteTextureSize, geometry.socketRight, geometry.bridgeRight, geometry.filler[TR_SIDE_RGT][TR_SIDE_RGT]);
			appendSocketPortal(upperLeftFirstExtension, interpolateBoundaryPoint(socketRight, leftRight.secondDistance),
				lowerRightSecondExtension, getBoundaryNormal(upperLeft, leftRight.firstDistance, TR_SIDE_LFT), undersideZ,
				concreteTextureSize, geometry.socketRight, geometry.bridgeLeft, geometry.filler[TR_SIDE_LFT][TR_SIDE_RGT]);
			appendSocketPortal(upperRightSecondExtension, interpolateBoundaryPoint(socketLeft, rightLeft.secondDistance),
				lowerLeftFirstExtension, getBoundaryNormal(upperRight, rightLeft.firstDistance, TR_SIDE_RGT), undersideZ,
				concreteTextureSize, geometry.socketLeft, geometry.bridgeRight, geometry.filler[TR_SIDE_RGT][TR_SIDE_LFT]);
			appendSocketPortal(upperLeftSecondExtension, interpolateBoundaryPoint(socketLeft, leftLeft.secondDistance),
				lowerLeftSecondExtension, getBoundaryNormal(upperLeft, leftLeft.firstDistance, TR_SIDE_LFT), undersideZ,
				concreteTextureSize, geometry.socketLeft, geometry.bridgeLeft, geometry.filler[TR_SIDE_LFT][TR_SIDE_LFT]);
			if (!geometry.floorRight.triangles.isEmpty())
			{
				appendTerrainPortalMask(upperRightFirstExtension, interpolateBoundaryPoint(socketRight, rightRight.secondDistance),
					lowerRightFirstExtension, geometry.terrainMask);
				appendTerrainPortalMask(upperLeftFirstExtension, interpolateBoundaryPoint(socketRight, leftRight.secondDistance),
					lowerRightSecondExtension, geometry.terrainMask);
			}
			if (!geometry.floorLeft.triangles.isEmpty())
			{
				appendTerrainPortalMask(upperRightSecondExtension, interpolateBoundaryPoint(socketLeft, rightLeft.secondDistance),
					lowerLeftFirstExtension, geometry.terrainMask);
				appendTerrainPortalMask(upperLeftSecondExtension, interpolateBoundaryPoint(socketLeft, leftLeft.secondDistance),
					lowerLeftSecondExtension, geometry.terrainMask);
			}

			geometry.upperSegmentId = upperSegment.segment.id;
			geometry.lowerSegmentId = lowerSegment.segment.id;
			geometry.crossingX = candidate.x;
			geometry.crossingY = candidate.y;
			geometry.undersideZ = undersideZ;
			geometry.minimumBridgeThickness = geometry.minimumUpperZ - undersideZ;
			geometry.measuredSocketGap[TR_SIDE_RGT] = socketGap <= CROSSING_PLAN_EPSILON ? 0.0
				: minimumLineDistance(sliceBoundaryLine(socketRight, rightRight.secondDistance, leftRight.secondDistance), lowerRight.points);
			geometry.measuredSocketGap[TR_SIDE_LFT] = socketGap <= CROSSING_PLAN_EPSILON ? 0.0
				: minimumLineDistance(sliceBoundaryLine(socketLeft, rightLeft.secondDistance, leftLeft.secondDistance), lowerLeft.points);
			geometry.underside.name = "TRKBRIDGE_UNDERSIDE" + crossingIndex;
			geometry.bridgeRight.name = "TRKBRIDGE_R" + crossingIndex;
			geometry.bridgeLeft.name = "TRKBRIDGE_L" + crossingIndex;
			geometry.socketRight.name = "TRKSOCKET_WALL_R" + crossingIndex;
			geometry.socketLeft.name = "TRKSOCKET_WALL_L" + crossingIndex;
			for (int upperSide = 0; upperSide < 2; upperSide++)
			{
				for (int lowerSide = 0; lowerSide < 2; lowerSide++)
				{
					geometry.filler[upperSide][lowerSide].name = "TRKFILLER_" + (upperSide == TR_SIDE_RGT ? 'R' : 'L')
						+ (lowerSide == TR_SIDE_RGT ? 'R' : 'L') + crossingIndex;
					geometry.filler[upperSide][lowerSide].texture = geometry.underside.texture;
				}
			}
			geometry.floorRight.name = "TRKFLOOR_R" + crossingIndex;
			geometry.floorLeft.name = "TRKFLOOR_L" + crossingIndex;
			BoundaryLine[] upperLines = { upperRight, upperLeft };
			BoundaryLine[] socketLines = { socketRight, socketLeft };
			BoundaryPoint[][] corners = {
				{ interpolateBoundaryPoint(socketRight, rightRight.secondDistance), interpolateBoundaryPoint(socketLeft, rightLeft.secondDistance) },
				{ interpolateBoundaryPoint(socketRight, leftRight.secondDistance), interpolateBoundaryPoint(socketLeft, leftLeft.secondDistance) } };
			mapCrossingTextures(lowerSegment.segment, upperCenter, upperTangent, lowerTangent, upperLines, socketLines,
				upperCenterDistance, lowerCenterDistance, corners, geometry);
			return true;
		}
		return false;
	}
}
