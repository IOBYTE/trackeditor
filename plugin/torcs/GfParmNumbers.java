/*
 *   GfParmNumbers.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package plugin.torcs;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gui.EditorFrame;

/**
 * Reads numbers and units the way the game's parameter library does
 * (libs/tgf/params.cpp: getValNumFromStr, GfParmUnit2SI and GfParmSI2Unit),
 * so the editor sees the same values as the game.
 */
public final class GfParmNumbers
{
	// what sscanf("%g") accepts at the start of a string
	private static final Pattern	NUMBER	= Pattern.compile(
		"[+-]?(?:(?:\\d+\\.?\\d*|\\.\\d+)(?:[eE][+-]?\\d+)?|(?i:inf(?:inity)?|nan))");
	private static final Pattern	HEX		= Pattern.compile("0[xX][0-9a-fA-F]+");

	private static final double		ZEROC	= 273.15;
	private static final double		G		= 9.80665;	// TORCS's lbf

	private GfParmNumbers()
	{
	}

	/**
	 * Parses an attnum value like the game: an empty value is 0, "0x..." is hexadecimal,
	 * anything else is read as far as it looks like a number ("-4-0" is -4, "*2.5" is 0).
	 *
	 * @param clean set to whether the whole text was a valid number
	 */
	public static double parse(String text, boolean[] clean)
	{
		clean[0] = false;

		if (text == null || text.isEmpty())
			return 0;

		if (text.startsWith("0x") || text.startsWith("0X"))
		{
			Matcher m = HEX.matcher(text);
			if (m.lookingAt())
			{
				clean[0] = text.substring(m.end()).trim().isEmpty();
				try
				{
					return Long.parseLong(m.group().substring(2), 16);
				}
				catch (NumberFormatException e)
				{
					return Long.MAX_VALUE;	// strtol saturates
				}
			}
			return 0;
		}

		// sscanf skips leading white space
		int start = 0;
		while (start < text.length() && Character.isWhitespace(text.charAt(start)))
			start++;

		Matcher m = NUMBER.matcher(text);
		m.region(start, text.length());
		if (!m.lookingAt())
			return 0;

		clean[0] = text.substring(m.end()).trim().isEmpty();

		String number = m.group();
		String lower = number.toLowerCase();
		if (lower.contains("inf"))
			return lower.startsWith("-") ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
		if (lower.contains("nan"))
			return Double.NaN;

		return Double.parseDouble(number);
	}

	/**
	 * Converts a value in the given unit to SI, like GfParmUnit2SI.
	 * Units the game doesn't know are taken as SI; they are added to unknown.
	 */
	public static double toSI(String unit, double value, EditorFrame.GameType game, List<String> unknown)
	{
		return convert(unit, value, false, game, unknown);
	}

	/**
	 * Converts an SI value to the given unit, like GfParmSI2Unit.
	 */
	public static double fromSI(String unit, double value, EditorFrame.GameType game, List<String> unknown)
	{
		return convert(unit, value, true, game, unknown);
	}

	/**
	 * Converts a value from one unit to another through SI, as the game does when a
	 * file uses a different unit than the one the value is read in.
	 * A missing unit means SI, as it does for the game.
	 */
	public static double convert(double value, String fromUnit, String toUnit, EditorFrame.GameType game, List<String> unknown)
	{
		String from = fromUnit == null ? "" : fromUnit;
		String to = toUnit == null ? "" : toUnit;

		if (from.equals(to))
			return value;

		return fromSI(to, toSI(from, value, game, unknown), game, unknown);
	}

	// GfParmUnit2SI / GfParmSI2Unit: units are separated by '.', a '/' inverts the
	// ones after it, and a '2' squares the one before it
	private static double convert(String unit, double value, boolean toUnit, EditorFrame.GameType game, List<String> unknown)
	{
		if (unit == null || unit.isEmpty())
			return value;

		double[] dest = { value };
		boolean invert = toUnit;
		StringBuilder buf = new StringBuilder();

		for (int i = 0; i < unit.length(); i++)
		{
			char c = unit.charAt(i);
			switch (c)
			{
				case '.':
					evalUnit(buf.toString(), dest, invert, game, unknown);
					buf.setLength(0);
					break;
				case '/':
					evalUnit(buf.toString(), dest, invert, game, unknown);
					buf.setLength(0);
					invert = !toUnit;
					break;
				case '2':
					evalUnit(buf.toString(), dest, invert, game, unknown);
					evalUnit(buf.toString(), dest, invert, game, unknown);
					buf.setLength(0);
					break;
				default:
					buf.append(c);
					break;
			}
		}

		evalUnit(buf.toString(), dest, invert, game, unknown);
		return dest[0];
	}

	private static void evalUnit(String unit, double[] dest, boolean invert, EditorFrame.GameType game, List<String> unknown)
	{
		if (game == EditorFrame.GameType.TORCS)
		{
			evalTorcsUnit(unit, dest, invert, unknown);
			return;
		}

		double coeff;

		switch (unit)
		{
			// SI, and units the game treats as SI
			case "m": case "N": case "kg": case "s": case "rad": case "Pa": case "K":
			case "l": case "litre":
				return;

			case "deg":					coeff = Math.PI / 180.0; break;
			case "lbs": case "lb":		coeff = 0.45359237; break;
			case "%": case "percent":	coeff = 0.01; break;
			case "mm":					coeff = 0.001; break;
			case "rpm": case "RPM":		coeff = 0.104719755; break;
			case "kPa":					coeff = 1000.0; break;
			case "feet": case "ft":		coeff = 0.304801; break;	// the game's value, not 0.3048
			case "km":					coeff = 1000.0; break;
			case "cm":					coeff = 0.01; break;
			case "in": case "inch": case "inches":	coeff = 0.0254; break;
			case "psi": case "PSI":		coeff = 6894.76; break;
			case "mph": case "MPH":		coeff = 0.44704; break;
			case "kph": case "KPH":		coeff = 0.277778; break;
			case "MPa":					coeff = 1000000.0; break;
			case "h": case "hour": case "hours":	coeff = 3600.0; break;
			case "day": case "days":	coeff = 24 * 3600.0; break;
			case "slug": case "slugs":	coeff = 14.59484546; break;
			case "kN":					coeff = 1000.0; break;
			case "cc":					coeff = 0.001; break;
			case "C":
				dest[0] += invert ? -ZEROC : ZEROC;
				return;

			case "ci":
				// Speed Dreams only
				if (game == EditorFrame.GameType.SPEED_DREAMS)
				{
					coeff = 0.0163871;
					break;
				}
				// fall through: unknown to CSR

			default:
				// the game uses unknown units as they are
				if (unknown != null && !unit.isEmpty() && !unknown.contains(unit))
					unknown.add(unit);
				return;
		}

		if (invert)
			dest[0] /= coeff;
		else
			dest[0] *= coeff;
	}

	// TORCS's evalUnit: fewer units than the others, and lbf
	private static void evalTorcsUnit(String unit, double[] dest, boolean invert, List<String> unknown)
	{
		double coeff;

		switch (unit)
		{
			case "m": case "kg": case "s": case "rad": case "Pa":
				return;

			case "feet": case "ft":		coeff = 0.304801; break;	// the game's value, not 0.3048
			case "deg":					coeff = Math.PI / 180.0; break;
			case "h": case "hour": case "hours":	coeff = 3600.0; break;
			case "day": case "days":	coeff = 24 * 3600.0; break;
			case "km":					coeff = 1000.0; break;
			case "mm":					coeff = 0.001; break;
			case "cm":					coeff = 0.01; break;
			case "in": case "inch": case "inches":	coeff = 0.0254; break;
			case "lbs": case "lb":		coeff = 0.45359237; break;
			case "lbf":					coeff = 0.45359237 * G; break;
			case "slug": case "slugs":	coeff = 14.59484546; break;
			case "kPa":					coeff = 1000.0; break;
			case "MPa":					coeff = 1000000.0; break;
			case "PSI": case "psi":		coeff = 6894.76; break;
			case "rpm": case "RPM":		coeff = 0.104719755; break;
			case "percent": case "%":	coeff = 0.01; break;
			case "mph": case "MPH":		coeff = 0.44704; break;

			default:
				// the game uses unknown units as they are
				if (unknown != null && !unit.isEmpty() && !unknown.contains(unit))
					unknown.add(unit);
				return;
		}

		if (invert)
			dest[0] /= coeff;
		else
			dest[0] *= coeff;
	}
}
