/*
 *   Closure.java
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
package utils.check;

import utils.Properties;
import utils.circuit.Curve;
import utils.circuit.Segment;

/**
 * Trackgen's deltas for a track, as it was when they were calculated, and how
 * close they have to be for the track to close (see DeltaPanel and the checks).
 */
public final class Closure
{
	// how close trackgen's deltas have to be for the track to close: the TORCS
	// tracktools' targets (Delta Y's is just over the rounding of trackgen's floats)
	public static final double	TARGET_DX		= 0.0005;
	public static final double	TARGET_DY		= 0.001;
	public static final double	TARGET_DZ		= 0.00005;
	public static final double	TARGET_ANGLE	= 0.0001;	// degrees

	// How far apart trackgen's x and y can come out for a meter of the track's size:
	// its floats hold the heading in steps of about 0.0000005 radians, so a step's
	// difference moves the end that far for each meter it goes on, and the points
	// round too. Closing a track closer than that is down to luck.
	private static final double	ROUNDING_PER_METER	= 0.000001;

	public final String		key;
	public final double		dX;
	public final double		dY;
	public final double		dZ;
	public final double		angle;
	// the track's largest coordinate
	public final double		size;
	// trackgen's lines, as shown
	public final String[]	lines;

	public Closure(String key, double dX, double dY, double dZ, double angle, double size, String[] lines)
	{
		this.size = size;
		this.lines = lines;
		this.key = key;
		this.dX = dX;
		this.dY = dY;
		this.dZ = dZ;
		this.angle = angle;
	}

	// the deltas of the last trackgen run
	private static volatile Closure	last	= null;

	/** Keeps the deltas of a trackgen run (see last) */
	public static void setLast(Closure closure)
	{
		last = closure;
	}

	/**
	 * @return trackgen's deltas from the last Calc, Auto or Length, or null when the
	 *         track has changed since (or is another track), so they may be out
	 */
	public static Closure last(TrackContext trackContext)
	{
		Closure closure = last;
		if (closure == null || trackContext.getTrackData() == null || !closure.key.equals(key(trackContext)))
			return null;
		return closure;
	}

	/**
	 * @return what the deltas depend on: the track's file, the main track's width (the
	 *         deltas are between its left edges) and the segments' shapes and heights
	 */
	public static String key(TrackContext trackContext)
	{
		StringBuilder key = new StringBuilder();
		key.append(Properties.getInstance().getPath()).append('|').append(trackContext.getGameType())
			.append('|').append(trackContext.getTrackData().getMainTrack().getValidWidth());
		for (Segment segment : trackContext.getTrackData().getSegments())
		{
			key.append('|').append(segment.getName()).append(',').append(segment.getType());
			if (segment instanceof Curve)
			{
				Curve curve = (Curve) segment;
				key.append(',').append(curve.getArcDeg()).append(',').append(curve.getRadiusStart())
					.append(',').append(curve.getRadiusEnd());
			}
			else
			{
				key.append(',').append(segment.getLength());
			}
			double[] values = { segment.getProfilStepsLength(), segment.getHeightStart(), segment.getHeightStartLeft(),
				segment.getHeightStartRight(), segment.getHeightEnd(), segment.getHeightEndLeft(), segment.getHeightEndRight(),
				segment.getGrade(), segment.getBankingStart(), segment.getBankingEnd() };
			for (double value : values)
				key.append(',').append(value);
		}
		return key.toString();
	}

	/**
	 * @return how far Delta X or Delta Y can be from 0 and be within trackgen's
	 *         rounding on a track of a size (its largest coordinate), or the target
	 *         when that's more
	 */
	public static double allowed(double target, double size)
	{
		return Double.isNaN(size) ? target : Math.max(target, size * ROUNDING_PER_METER);
	}

	/** @return the number without an exponent or trailing zeros */
	public static String plain(double value)
	{
		return java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
	}
}
