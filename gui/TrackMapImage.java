/*
 *   TrackMapImage.java
 *
 *    This file is part of the track editor.
 *
 *    The track editor is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    The track editor is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 */
package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import utils.circuit.GameTrack;

/**
 * The picture of the track the game's track menu shows: the main track's
 * centerline as the game builds it, drawn in white on a transparent background
 * and fitted to a square. TORCS's map (&lt;name&gt;.png) is 1024 pixels with a
 * flag at the start line pointing the way the cars go, like the maps TORCS
 * comes with. Speed Dreams' and CSR's outline (outline.png) is 512 pixels with
 * a red mark across the start line.
 */
public final class TrackMapImage
{
	/** the size of TORCS's maps */
	public static final int		MAP_SIZE		= 1024;
	/** the size of Speed Dreams' and CSR's outlines */
	public static final int		OUTLINE_SIZE	= 512;

	private static final Color	LINE			= Color.WHITE;
	private static final Color	EDGE			= new Color(40, 40, 40);
	private static final Color	START			= new Color(220, 30, 30);
	// how far apart the centerline's points are, in meters
	private static final double	STEP			= 1;
	// room left for antialiasing beyond the margin, in pixels
	private static final double	SMOOTHING		= 3;

	/** Why the track can't be drawn, for the user */
	public static final class NotDrawableException extends Exception
	{
		private static final long	serialVersionUID	= 1L;

		NotDrawableException(String message)
		{
			super(message);
		}
	}

	/** The parts of the start marker, around the start of the track in the picture */
	private static final class Marker
	{
		// filled white with a dark edge (the flag) or filled in red (the start mark)
		final List<Shape>	parts	= new ArrayList<Shape>();
		final Color			fill;
		final BasicStroke	edge;

		Marker(Color fill, BasicStroke edge)
		{
			this.fill = fill;
			this.edge = edge;
		}

		/** @return what it covers, its edge included */
		Rectangle2D bounds()
		{
			Rectangle2D bounds = null;
			for (Shape part : parts)
			{
				Rectangle2D b = (edge == null ? part : edge.createStrokedShape(part)).getBounds2D();
				if (edge != null)
					b = b.createUnion(part.getBounds2D());
				bounds = bounds == null ? b : bounds.createUnion(b);
			}
			return bounds;
		}
	}

	private TrackMapImage()
	{
	}

	/**
	 * @param map true for TORCS's map, false for Speed Dreams' and CSR's outline
	 * @return the picture
	 * @throws NotDrawableException when the track has no shape to draw, or
	 *         doesn't close, so the picture would show a piece of track that
	 *         isn't there
	 */
	public static BufferedImage render(GameTrack track, boolean map) throws NotDrawableException
	{
		List<double[]> points = centerline(track);
		if (points.size() < 2)
			throw new NotDrawableException("The track has no segments to draw.");
		for (double[] p : points)
		{
			if (!Double.isFinite(p[0]) || !Double.isFinite(p[1]))
				throw new NotDrawableException("The track's shape can't be worked out: Check shows what's wrong with its segments.");
		}

		int size = map ? MAP_SIZE : OUTLINE_SIZE;
		double line = map ? 22 : 8;
		// as TORCS's maps: max(2, size / 128), plus room for antialiasing
		double margin = Math.max(2, size / 128.0) + SMOOTHING;

		// where the track starts, and the way it goes, in the picture (its y goes
		// down, the track's up)
		GameTrack.Seg first = track.segs.get(0);
		double[] start = points.get(0);
		double ax = Math.cos(first.angleZS), ay = -Math.sin(first.angleZS);
		double lx = ay, ly = -ax;
		Marker marker = map ? flag(ax, ay, lx, ly, line) : startMark(lx, ly, line);
		Rectangle2D markerBounds = marker.bounds();

		// the largest scale at which the line and the marker fit: the space they
		// take grows with the scale (it's convex in it), so the scales that fit
		// are from 0 up to the one looked for
		double room = size - 2 * margin;
		double hi = 1;
		while (span(points, line, start, markerBounds, hi) < room && hi < 1e9)
			hi *= 2;
		double lo = 0;
		if (span(points, line, start, markerBounds, lo) >= room)
			throw new NotDrawableException("The picture is too small for the track.");
		for (int i = 0; i < 60; i++)
		{
			double mid = (lo + hi) / 2;
			if (span(points, line, start, markerBounds, mid) <= room)
				lo = mid;
			else
				hi = mid;
		}
		double scale = lo;

		// a gap the picture would show: the end of the last segment isn't at the start
		GameTrack.Seg last = track.segs.get(track.segs.size() - 1);
		float lastAlong = last.type == GameTrack.TR_STR ? last.length : last.arc;
		float[] end = GameTrack.local2Global(last, lastAlong, GameTrack.width(last, lastAlong) / 2);
		double gap = Math.hypot(end[0] - start[0], end[1] - start[1]);
		if (!(gap * scale <= line / 2))
		{
			throw new NotDrawableException(String.format(java.util.Locale.US,
				"The track doesn't close: segment %s ends %.2f m from the start of segment %s, so the picture would show "
					+ "a piece of track that isn't there. Close the track first: Check shows the gap.",
				last.name, gap, first.name));
		}

		// the drawing centered
		double[] b = bounds(points, line, start, markerBounds, scale);
		double ox = size / 2.0 - (b[0] + b[2]) / 2, oy = size / 2.0 - (b[1] + b[3]) / 2;

		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		try
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			Path2D.Double path = new Path2D.Double();
			for (int i = 0; i < points.size(); i++)
			{
				double x = ox + points.get(i)[0] * scale, y = oy - points.get(i)[1] * scale;
				if (i == 0)
					path.moveTo(x, y);
				else
					path.lineTo(x, y);
			}
			path.closePath();
			g.setColor(LINE);
			g.setStroke(new BasicStroke((float) line, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.draw(path);

			AffineTransform at = AffineTransform.getTranslateInstance(ox + start[0] * scale, oy - start[1] * scale);
			for (Shape part : marker.parts)
			{
				Shape placed = at.createTransformedShape(part);
				g.setColor(marker.fill);
				g.fill(placed);
				if (marker.edge != null)
				{
					g.setColor(EDGE);
					g.setStroke(marker.edge);
					g.draw(placed);
				}
			}
		}
		finally
		{
			g.dispose();
		}
		return image;
	}

	/** @return the larger of the drawing's width and height, in pixels, at the scale */
	private static double span(List<double[]> points, double line, double[] start, Rectangle2D marker, double scale)
	{
		double[] b = bounds(points, line, start, marker, scale);
		return Math.max(b[2] - b[0], b[3] - b[1]);
	}

	/**
	 * @return what the line and the marker cover at the scale, before they are
	 *         placed in the picture: minimum x, minimum y, maximum x, maximum y
	 */
	private static double[] bounds(List<double[]> points, double line, double[] start, Rectangle2D marker, double scale)
	{
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
		for (double[] p : points)
		{
			double x = p[0] * scale, y = -p[1] * scale;
			minX = Math.min(minX, x);
			maxX = Math.max(maxX, x);
			minY = Math.min(minY, y);
			maxY = Math.max(maxY, y);
		}
		minX -= line / 2;
		minY -= line / 2;
		maxX += line / 2;
		maxY += line / 2;
		double sx = start[0] * scale, sy = -start[1] * scale;
		minX = Math.min(minX, sx + marker.getMinX());
		minY = Math.min(minY, sy + marker.getMinY());
		maxX = Math.max(maxX, sx + marker.getMaxX());
		maxY = Math.max(maxY, sy + marker.getMaxY());
		return new double[] { minX, minY, maxX, maxY };
	}

	/**
	 * A flag on a pole at the start line, on the left of the track, pointing the
	 * way the cars go, around the start
	 *
	 * @param ax, ay the way along the track
	 * @param lx, ly the way to its left
	 */
	private static Marker flag(double ax, double ay, double lx, double ly, double line)
	{
		double half = line * 0.22, pole = line * 3.0, flag = line * 1.5, flagHeight = line * 0.9;
		double tx = lx * pole, ty = ly * pole;

		Path2D.Double poleShape = new Path2D.Double();
		poleShape.moveTo(-ax * half - lx * line * 0.5, -ay * half - ly * line * 0.5);
		poleShape.lineTo(ax * half - lx * line * 0.5, ay * half - ly * line * 0.5);
		poleShape.lineTo(tx + ax * half, ty + ay * half);
		poleShape.lineTo(tx - ax * half, ty - ay * half);
		poleShape.closePath();

		Path2D.Double flagShape = new Path2D.Double();
		flagShape.moveTo(tx - ax * half, ty - ay * half);
		flagShape.lineTo(tx + ax * flag - lx * flagHeight / 2, ty + ay * flag - ly * flagHeight / 2);
		flagShape.lineTo(tx - ax * half - lx * flagHeight, ty - ay * half - ly * flagHeight);
		flagShape.closePath();

		Marker marker = new Marker(LINE, new BasicStroke((float) Math.max(2, line / 8), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		marker.parts.add(poleShape);
		marker.parts.add(flagShape);
		return marker;
	}

	/** A red mark across the track at the start line, around the start */
	private static Marker startMark(double lx, double ly, double line)
	{
		double reach = line * 1.4;
		BasicStroke stroke = new BasicStroke((float) Math.max(2, line * 0.6), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		Marker marker = new Marker(START, null);
		marker.parts.add(stroke.createStrokedShape(new Line2D.Double(-lx * reach, -ly * reach, lx * reach, ly * reach)));
		return marker;
	}

	/**
	 * @return the main track's centerline as the game builds it, from the start
	 *         of the first segment, a point about every meter
	 */
	static List<double[]> centerline(GameTrack track)
	{
		List<double[]> points = new ArrayList<double[]>();
		for (GameTrack.Seg seg : track.segs)
		{
			int n = Math.max(1, (int) Math.ceil(seg.length / STEP));
			if (n > 100000)
				n = 100000;
			// on a straight how far along it, in a curve how far around it
			double along = seg.type == GameTrack.TR_STR ? seg.length : seg.arc;
			for (int i = 0; i < n; i++)
			{
				float toStart = (float) (along * i / n);
				float[] p = GameTrack.local2Global(seg, toStart, GameTrack.width(seg, toStart) / 2);
				points.add(new double[] { p[0], p[1] });
			}
		}
		return points;
	}
}
