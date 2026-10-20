/*
 *   GameSides.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package utils.circuit;

import java.util.ArrayList;

import gui.EditorFrame;

/**
 * The side, border and barrier values the game uses for a segment.
 *
 * Follows the track loader (trackv1 track4.cpp, InitSides and AddSides), which
 * works through the segments in order:
 * <ul>
 * <li>The Main Track gives the start values. A value it doesn't set is the game's
 *     default: no side or border (width 0, "grass", "plan"), and a "fence" barrier
 *     0.6 m high on "barrier".</li>
 * <li>A segment value that isn't set is the previous segment's.</li>
 * <li>A side "width" sets both widths; otherwise the start width is the previous
 *     end width and the end width is the start width. The Main Track only has "width".</li>
 * <li>A "plan" border has height 0, and that 0 carries on to later segments.</li>
 * <li>A border style other than "plan" or "curb" is a wall.</li>
 * <li>A fence has width 0 and "no barrier" has width and height 0; both carry on.
 *     A barrier style the game doesn't know is ignored. On the Main Track, any
 *     barrier style other than a fence is a wall.</li>
 * </ul>
 */
public final class GameSides
{
	public static final class State
	{
		public double	sideStartWidth;
		public double	sideEndWidth;
		public String	sideSurface;

		public double	borderWidth;
		public double	borderHeight;
		public String	borderSurface;
		public String	borderStyle;

		public double	barrierWidth;
		public double	barrierHeight;
		public String	barrierSurface;
		public String	barrierStyle;
	}

	private GameSides()
	{
	}

	/**
	 * @return the values the game uses for this segment's right or left side
	 */
	public static State of(Segment segment, boolean right, EditorFrame editorFrame)
	{
		boolean speedDreams = !editorFrame.getCarsSportsRacing();
		MainTrack mainTrack = editorFrame.getTrackData().getMainTrack();
		State state = start(right ? mainTrack.getRight() : mainTrack.getLeft(), speedDreams);

		// the segments from the first one to this one
		ArrayList<Segment> chain = new ArrayList<Segment>();
		int limit = editorFrame.getTrackData().getSegments() == null ? 0 : editorFrame.getTrackData().getSegments().size();
		for (Segment s = segment; s != null && chain.size() <= limit; s = s.previousShape)
			chain.add(s);

		for (int i = chain.size() - 1; i >= 0; i--)
		{
			Segment s = chain.get(i);
			apply(state, right ? s.getRight() : s.getLeft(), speedDreams);
		}

		return state;
	}

	private static State start(SegmentSide main, boolean speedDreams)
	{
		State s = new State();

		// only "width" counts on the Main Track (the editor keeps it as equal start and end widths)
		double start = main.getSideStartWidth();
		double end = main.getSideEndWidth();
		s.sideEndWidth = (!Double.isNaN(start) && start == end) ? start : 0.0;
		s.sideStartWidth = s.sideEndWidth;
		s.sideSurface = text(main.getSideSurface(), "grass");

		s.borderWidth = number(main.getBorderWidth(), 0.0);
		s.borderHeight = number(main.getBorderHeight(), 0.0);
		s.borderSurface = text(main.getBorderSurface(), "grass");
		setBorderStyle(s, text(main.getBorderStyle(), "plan"));

		s.barrierSurface = text(main.getBarrierSurface(), "barrier");
		s.barrierHeight = number(main.getBarrierHeight(), 0.6);
		String style = text(main.getBarrierStyle(), "fence");
		if (isFence(style, speedDreams))
		{
			s.barrierStyle = style;
			s.barrierWidth = 0.0;
		}
		else
		{
			s.barrierStyle = "wall";
			s.barrierWidth = number(main.getBarrierWidth(), 0.5);
		}

		return s;
	}

	private static void apply(State s, SegmentSide side, boolean speedDreams)
	{
		// side
		double start = side.getSideStartWidth();
		double end = side.getSideEndWidth();
		s.sideStartWidth = number(start, s.sideEndWidth);
		s.sideEndWidth = number(end, s.sideStartWidth);
		s.sideSurface = text(side.getSideSurface(), s.sideSurface);

		// border
		s.borderWidth = number(side.getBorderWidth(), s.borderWidth);
		s.borderHeight = number(side.getBorderHeight(), s.borderHeight);
		s.borderSurface = text(side.getBorderSurface(), s.borderSurface);
		setBorderStyle(s, text(side.getBorderStyle(), s.borderStyle));

		// barrier
		s.barrierSurface = text(side.getBarrierSurface(), s.barrierSurface);
		s.barrierHeight = number(side.getBarrierHeight(), s.barrierHeight);
		String style = text(side.getBarrierStyle(), s.barrierStyle);
		if (isFence(style, speedDreams))
		{
			s.barrierStyle = style;
			s.barrierWidth = 0.0;
		}
		else if (style.equals("wall"))
		{
			s.barrierStyle = "wall";
			s.barrierWidth = number(side.getBarrierWidth(), s.barrierWidth);
		}
		else if (style.equals("no barrier"))
		{
			s.barrierStyle = "no barrier";
			s.barrierWidth = 0.0;
			s.barrierHeight = 0.0;
		}
		// any other style is ignored: everything stays as it was
	}

	private static void setBorderStyle(State s, String style)
	{
		if (style.equals("plan"))
		{
			s.borderStyle = "plan";
			s.borderHeight = 0.0;
		}
		else if (style.equals("curb"))
		{
			s.borderStyle = "curb";
		}
		else
		{
			s.borderStyle = "wall";
		}
	}

	private static boolean isFence(String style, boolean speedDreams)
	{
		return style.equals("fence") || (speedDreams && (style.equals("fence1") || style.equals("fence2")));
	}

	private static double number(double value, double inherited)
	{
		return Double.isNaN(value) ? inherited : value;
	}

	private static String text(String value, String inherited)
	{
		return value == null || value.isEmpty() ? inherited : value;
	}
}
