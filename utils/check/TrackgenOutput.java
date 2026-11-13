/*
 *   TrackgenOutput.java
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

import java.util.ArrayDeque;

/**
 * The track data trackgen prints (-z): the lines the Delta dialog shows, and the
 * track's length, size and deltas read from them.
 */
public final class TrackgenOutput
{
	/** The lines of track data, in the order the Delta dialog shows them */
	public enum Field
	{
		NAME, AUTHOR, FILE_NAME, LENGTH, WIDTH, X_SIZE, Y_SIZE, DELTA_X, DELTA_Y, DELTA_Z, DELTA_ANGLE
	}

	// the last few lines that weren't track data, to show if trackgen fails: a line
	// repeated (but for its time) counts once
	private static final int		MAX_OTHER_LINES	= 15;
	// the time the game's log starts its lines with, like 00:00:00.170
	private static final java.util.regex.Pattern	TIME	= java.util.regex.Pattern.compile("^\\d+:\\d\\d:\\d\\d\\.\\d+\\s+");

	private double					length			= Double.NaN;
	// the track's largest coordinate
	private double					size			= Double.NaN;
	private double					dX				= Double.NaN;
	private double					dY				= Double.NaN;
	private double					dZ				= Double.NaN;
	// degrees
	private double					angle			= Double.NaN;
	private final String[]			lines			= new String[Field.values().length];
	private final ArrayDeque<String>	otherLines	= new ArrayDeque<String>();
	// how many more times each of those lines came straight after it
	private final ArrayDeque<int[]>		repeats		= new ArrayDeque<int[]>();

	/**
	 * Reads a line of trackgen's output.
	 *
	 * @return the track data it gives, or null for another line
	 */
	public Field read(String line)
	{
		Field field = null;
		int equals = line.indexOf("=");
		String key = equals == -1 ? "" : line.substring(0, equals);

		try
		{
			if (key.equals("name      ")) {
				field = Field.NAME;
			} else if (key.equals("authors   ") || key.equals("author    ")) {
				field = Field.AUTHOR;
			} else if (key.equals("filename  ")) {
				field = Field.FILE_NAME;
			} else if (key.equals("length    ")) {
				length = Double.parseDouble(line.substring(equals + 1).trim());
				field = Field.LENGTH;
			} else if (key.equals("width     ")) {
				field = Field.WIDTH;
			} else if (key.equals("XSize     ")) {
				field = Field.X_SIZE;
				size = Double.isNaN(size) ? Math.abs(Double.parseDouble(line.substring(equals + 1).trim()))
					: Math.max(size, Math.abs(Double.parseDouble(line.substring(equals + 1).trim())));
			} else if (key.equals("YSize     ")) {
				field = Field.Y_SIZE;
				size = Double.isNaN(size) ? Math.abs(Double.parseDouble(line.substring(equals + 1).trim()))
					: Math.max(size, Math.abs(Double.parseDouble(line.substring(equals + 1).trim())));
			} else if (key.equals("Delta X   ")) {
				dX = Double.parseDouble(line.substring(equals + 1).trim());
				field = Field.DELTA_X;
			} else if (key.equals("Delta Y   ")) {
				dY = Double.parseDouble(line.substring(equals + 1).trim());
				field = Field.DELTA_Y;
			} else if (key.equals("Delta Z   ")) {
				dZ = Double.parseDouble(line.substring(equals + 1).trim());
				field = Field.DELTA_Z;
			} else if (key.equals("Delta Ang ")) {
				angle = Double.parseDouble(line.substring(line.indexOf("(") + 1, line.indexOf(")")).trim());
				field = Field.DELTA_ANGLE;
			}
		}
		catch (NumberFormatException | IndexOutOfBoundsException e)
		{
			field = null;
		}

		if (field != null)
		{
			lines[field.ordinal()] = line;
		}
		else if (!key.isEmpty() || !line.trim().isEmpty())
		{
			if (!otherLines.isEmpty() && untimed(otherLines.peekLast()).equals(untimed(line)))
			{
				repeats.peekLast()[0]++;
			}
			else
			{
				if (otherLines.size() == MAX_OTHER_LINES)
				{
					otherLines.removeFirst();
					repeats.removeFirst();
				}
				otherLines.addLast(line);
				repeats.addLast(new int[1]);
			}
		}
		return field;
	}

	/** @return the delta's target (see Closure), NaN when the field isn't a delta */
	public static double target(Field field)
	{
		switch (field)
		{
		case DELTA_X:
			return Closure.TARGET_DX;
		case DELTA_Y:
			return Closure.TARGET_DY;
		case DELTA_Z:
			return Closure.TARGET_DZ;
		case DELTA_ANGLE:
			return Closure.TARGET_ANGLE;
		default:
			return Double.NaN;
		}
	}

	/** @return the delta read, NaN when the field isn't a delta or wasn't read */
	public double value(Field field)
	{
		switch (field)
		{
		case DELTA_X:
			return dX;
		case DELTA_Y:
			return dY;
		case DELTA_Z:
			return dZ;
		case DELTA_ANGLE:
			return angle;
		default:
			return Double.NaN;
		}
	}

	/** @return whether trackgen reported the track's length and deltas */
	public boolean complete()
	{
		return !(Double.isNaN(length) || Double.isNaN(dX) || Double.isNaN(dY) || Double.isNaN(angle));
	}

	/**
	 * @return the last lines that weren't track data, after two new lines, or "": a
	 *         line repeated straight after itself (but for its time) once, with how
	 *         many more times it came
	 */
	public String otherOutput()
	{
		if (otherLines.isEmpty())
			return "";

		StringBuilder text = new StringBuilder("\n\n");
		java.util.Iterator<int[]> counts = repeats.iterator();
		for (String line : otherLines)
		{
			int more = counts.next()[0];
			text.append(line).append("\n");
			if (more > 0)
				text.append("    (").append(more).append(more == 1 ? " more time)" : " more times)").append("\n");
		}
		return text.toString();
	}

	/** @return the line without the time the game's log starts it with */
	private static String untimed(String line)
	{
		return TIME.matcher(line).replaceFirst("");
	}

	/** @return the deltas, for the track as it was when trackgen read it (see Closure.key) */
	public Closure closure(String key)
	{
		return new Closure(key, dX, dY, dZ, angle, size, lines.clone());
	}

	public double getLength()
	{
		return length;
	}

	public double getSize()
	{
		return size;
	}

	public double getDX()
	{
		return dX;
	}

	public double getDY()
	{
		return dY;
	}

	public double getDZ()
	{
		return dZ;
	}

	public double getAngle()
	{
		return angle;
	}
}
