/*
 *   CameraProblems.java
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

import utils.GameType;
import utils.SegmentVector;
import utils.circuit.Curve;
import utils.circuit.Segment;

/** What's wrong with a camera, as Check and the Cameras tab say it */
public final class CameraProblems
{
	private CameraProblems()
	{
	}

	/**
	 * Says where To Start puts a camera when it isn't likely where it was meant,
	 * as the game reads it: in the unit the file gives (deg becomes rad, ft
	 * becomes m), then as a length along a straight or as an angle in radians
	 * round a curve. Past the end of a straight is still on its line, and often
	 * meant; past the end of a curve goes on round its circle, away from the
	 * road. Check and the camera's tab both say it.
	 *
	 * @param units the unit To Start is saved in, null or empty for none
	 * @return the problem, starting "to start", or null
	 */
	public static String toStartProblem(Segment segment, double value, String units, GameType game)
	{
		if (segment == null || Double.isNaN(value))
			return null;
		boolean curve = !segment.getType().equals("str");
		boolean angle = "deg".equals(units) || "rad".equals(units);
		boolean length = units != null && !units.isEmpty() && !angle;
		double read = units == null || units.isEmpty() ? value
			: plugin.torcs.GfParmNumbers.toSI(units, value, game, null);
		String given = format(value) + (length || angle ? " " + units : "");
		if (curve && length)
		{
			return "to start " + given + " is a length on curve " + segment.getName()
				+ ": the game reads it as " + format(read) + " radians (" + format(Math.toDegrees(read))
				+ " deg) round the curve, so the camera is far from where it should be";
		}
		if (!curve && angle && value != 0)
		{
			return "to start " + given + " is an angle on straight " + segment.getName()
				+ ": the game reads it as " + format(read) + " m along it";
		}
		if (!curve)
			return null;
		double arc = ((Curve) segment).getArcRad();
		if (Double.isNaN(arc))
			return null;
		double past = read > arc ? read - arc : read < 0 ? -read : 0;
		if (past <= Math.toRadians(1))
			return null;
		boolean bare = units == null || units.isEmpty();
		String text = "to start " + given + (bare ? " (radians, " + format(Math.toDegrees(read)) + " deg)" : "");
		if (Math.abs(read) >= 2 * Math.PI)
		{
			// round and round the same circle
			double turned = Math.toDegrees(read) % 360;
			if (turned < 0)
				turned += 360;
			text += " is more than a full turn round curve " + segment.getName() + " (" + format(Math.toDegrees(arc))
				+ " deg): the game puts the camera " + format(turned) + " deg round its circle from its start";
		}
		else
			text += " is " + (read < 0 ? "before the start" : "past the end") + " of curve " + segment.getName() + " ("
				+ format(Math.toDegrees(arc)) + " deg): the game puts the camera " + format(Math.toDegrees(past)) + " deg "
				+ (read < 0 ? "before it" : "past it") + ", on the same circle";
		// more than a turn without a unit: most likely meant as meters along the curve
		double radius = ((Curve) segment).getRadiusStart();
		if (bare && Math.abs(read) > 2 * Math.PI && !Double.isNaN(radius) && radius > 0)
			text += "; if " + format(value) + " was meant as meters along it, that is " + format(Math.toDegrees(value / radius)) + " deg";
		return text;
	}

	/** @return a number with at most 2 decimals, and none when it is whole */
	private static String format(double value)
	{
		String text = String.format(java.util.Locale.US, "%.2f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
		return "-0".equals(text) ? "0" : text;
	}

	/**
	 * @return what's wrong with the segments a camera refers to: the game finds
	 *         them by name, stops loading the track when one isn't set, and uses
	 *         the first segment for a name no segment has
	 */
	public static java.util.List<String> segmentProblems(SegmentVector segments, String camera, String segment,
		String fovStart, String fovEnd)
	{
		java.util.List<String> problems = new java.util.ArrayList<String>();
		String[] names = { segment, fovStart, fovEnd };
		String[] labels = { "segment", "fov start", "fov end" };
		String[] results = { "so the game puts the camera on the first segment",
			"so the game uses the camera from the first segment", "so the game uses the camera up to the first segment" };
		for (int i = 0; i < names.length; i++)
		{
			String name = names[i] == null ? "" : names[i].trim();
			if (name.isEmpty())
				problems.add("Camera " + camera + ": missing " + labels[i] + ": the game won't load the track without it");
			else if (segments.getSegmentFromName(name) == null)
				problems.add("Camera " + camera + ": " + labels[i] + " \"" + name + "\" isn't a segment, " + results[i]);
		}
		return problems;
	}
}
