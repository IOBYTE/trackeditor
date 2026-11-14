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
import java.awt.geom.Path2D;
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

	private TrackMapImage()
	{
	}

	/**
	 * @param map true for TORCS's map, false for Speed Dreams' and CSR's outline
	 * @return the picture, or null when the track has no shape to draw (a
	 *         segment the game can't build leaves points that aren't numbers)
	 */
	public static BufferedImage render(GameTrack track, boolean map)
	{
		List<double[]> points = centerline(track);
		if (points.size() < 2)
			return null;
		for (double[] p : points)
		{
			if (!Double.isFinite(p[0]) || !Double.isFinite(p[1]))
				return null;
		}

		int size = map ? MAP_SIZE : OUTLINE_SIZE;
		double line = map ? 22 : 8;
		double margin = map ? 8 : 12;

		// where the track starts, and the way it goes
		GameTrack.Seg first = track.segs.get(0);
		double[] start = points.get(0);
		double dx = Math.cos(first.angleZS), dy = Math.sin(first.angleZS);

		// the shape fitted to the picture: its scale is worked out from the
		// centerline, then the room the line and the start marker need around it
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
		for (double[] p : points)
		{
			minX = Math.min(minX, p[0]);
			maxX = Math.max(maxX, p[0]);
			minY = Math.min(minY, p[1]);
			maxY = Math.max(maxY, p[1]);
		}
		double extent = Math.max(Math.max(maxX - minX, maxY - minY), 1e-6);
		// the marker reaches this far from the start, in pixels
		double marker = map ? line * 3.2 : line * 1.6;
		double room = size - 2 * margin - line - 2 * marker;
		if (room <= 0)
			return null;
		double scale = room / extent;

		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		try
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			// centered; the picture's y goes down, the track's up
			double cx = (minX + maxX) / 2, cy = (minY + maxY) / 2;
			double ox = size / 2.0 - cx * scale, oy = size / 2.0 + cy * scale;

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

			// the start, and the way along and to the left of the track, in the picture
			double sx = ox + start[0] * scale, sy = oy - start[1] * scale;
			double ax = dx, ay = -dy, lx = -dy, ly = -dx;
			if (map)
				drawFlag(g, sx, sy, ax, ay, lx, ly, line);
			else
				drawStartMark(g, sx, sy, lx, ly, line);
		}
		finally
		{
			g.dispose();
		}
		return image;
	}

	/** A flag on a pole at the start line, on the left of the track, pointing the way the cars go */
	private static void drawFlag(Graphics2D g, double sx, double sy, double ax, double ay, double lx, double ly, double line)
	{
		double half = line * 0.22, pole = line * 3.0, flag = line * 1.5, flagHeight = line * 0.9;
		double tx = sx + lx * pole, ty = sy + ly * pole;

		Path2D.Double flagShape = new Path2D.Double();
		flagShape.moveTo(tx - ax * half, ty - ay * half);
		flagShape.lineTo(tx + ax * flag - lx * flagHeight / 2, ty + ay * flag - ly * flagHeight / 2);
		flagShape.lineTo(tx - ax * half - lx * flagHeight, ty - ay * half - ly * flagHeight);
		flagShape.closePath();

		Path2D.Double poleShape = new Path2D.Double();
		poleShape.moveTo(sx - ax * half - lx * line * 0.5, sy - ay * half - ly * line * 0.5);
		poleShape.lineTo(sx + ax * half - lx * line * 0.5, sy + ay * half - ly * line * 0.5);
		poleShape.lineTo(tx + ax * half, ty + ay * half);
		poleShape.lineTo(tx - ax * half, ty - ay * half);
		poleShape.closePath();

		g.setStroke(new BasicStroke((float) Math.max(2, line / 8), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for (Path2D.Double part : new Path2D.Double[] { poleShape, flagShape })
		{
			g.setColor(LINE);
			g.fill(part);
			g.setColor(EDGE);
			g.draw(part);
		}
	}

	/** A red mark across the track at the start line */
	private static void drawStartMark(Graphics2D g, double sx, double sy, double lx, double ly, double line)
	{
		double reach = line * 1.4;
		g.setColor(START);
		g.setStroke(new BasicStroke((float) Math.max(2, line * 0.6), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		g.draw(new java.awt.geom.Line2D.Double(sx - lx * reach, sy - ly * reach, sx + lx * reach, sy + ly * reach));
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
