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
 * <li>A side's start width that isn't set is the previous end width, and an end width
 *     that isn't set is the start width. The Main Track only has "width". (A side "width"
 *     sets both widths; in TORCS it is the end width: XmlReader reads it as that.)</li>
 * <li>A "plan" border has height 0, and that 0 carries on to later segments (not in
 *     TORCS: it keeps the height, which a later border without one uses).</li>
 * <li>A border style other than "plan" or "curb" is a wall.</li>
 * <li>A fence has width 0 and "no barrier" has width and height 0; both carry on.
 *     A barrier style the game doesn't know is ignored (in TORCS, any style other
 *     than a fence is a wall). On the Main Track, any barrier style other than a
 *     fence is a wall.</li>
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
		EditorFrame.GameType game = editorFrame.getGameType();
		boolean version3 = editorFrame.getTrackData().getHeader().getVersion() == 3;
		MainTrack mainTrack = editorFrame.getTrackData().getMainTrack();
		State state = start(right ? mainTrack.getRight() : mainTrack.getLeft(), game, version3);

		// the segments from the first one to this one
		ArrayList<Segment> chain = new ArrayList<Segment>();
		int limit = editorFrame.getTrackData().getSegments() == null ? 0 : editorFrame.getTrackData().getSegments().size();
		for (Segment s = segment; s != null && chain.size() <= limit; s = s.previousShape)
			chain.add(s);

		for (int i = chain.size() - 1; i >= 0; i--)
		{
			Segment s = chain.get(i);
			apply(state, right ? s.getRight() : s.getLeft(), game, version3);
		}

		return state;
	}

	private static State start(SegmentSide main, EditorFrame.GameType game, boolean version3)
	{
		State s = new State();

		// the Main Track has only "width": the editor keeps it as equal start and end widths
		s.sideEndWidth = number(main.getMainTrackSideWidth(), 0.0);
		s.sideStartWidth = s.sideEndWidth;
		s.sideSurface = text(main.getSideSurface(), "grass");

		s.borderWidth = number(main.getBorderWidth(), 0.0);
		s.borderHeight = number(main.getBorderHeight(), 0.0);
		s.borderSurface = text(main.getBorderSurface(), "grass");
		setBorderStyle(s, text(main.getBorderStyle(), "plan"), game);

		s.barrierSurface = text(main.getBarrierSurface(), "barrier");
		// the version 3 loaders make it 1 m high
		s.barrierHeight = number(main.getBarrierHeight(), version3 ? 1.0 : 0.6);
		String style = text(main.getBarrierStyle(), "fence");
		// the version 3 loaders only know fence and wall
		if (version3 ? style.equals("fence") : isFence(style, game))
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

	private static void apply(State s, SegmentSide side, EditorFrame.GameType game, boolean version3)
	{
		// side
		double start = side.getSideStartWidth();
		double end = side.getSideEndWidth();
		s.sideStartWidth = number(start, s.sideEndWidth);
		s.sideEndWidth = number(end, s.sideStartWidth);
		s.sideSurface = text(side.getSideSurface(), s.sideSurface);

		// border
		s.borderWidth = number(side.getBorderWidth(), s.borderWidth);
		// in TORCS version 3, a segment without a border height has a height of 0
		s.borderHeight = number(side.getBorderHeight(), version3 && game == EditorFrame.GameType.TORCS ? 0.0 : s.borderHeight);
		s.borderSurface = text(side.getBorderSurface(), s.borderSurface);
		setBorderStyle(s, text(side.getBorderStyle(), s.borderStyle), game);

		// the version 3 loaders read the barrier only from the Main Track
		if (version3)
			return;

		// barrier
		s.barrierSurface = text(side.getBarrierSurface(), s.barrierSurface);
		s.barrierHeight = number(side.getBarrierHeight(), s.barrierHeight);
		String style = text(side.getBarrierStyle(), s.barrierStyle);
		if (isFence(style, game))
		{
			s.barrierStyle = style;
			s.barrierWidth = 0.0;
		}
		else if (style.equals("wall") || game.otherBarrierStylesAreWalls())
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

	private static void setBorderStyle(State s, String style, EditorFrame.GameType game)
	{
		if (style.equals("plan"))
		{
			s.borderStyle = "plan";
			if (game.planBorderResetsHeight())
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

	private static boolean isFence(String style, EditorFrame.GameType game)
	{
		return style.equals("fence") || (game.hasFence1Fence2() && (style.equals("fence1") || style.equals("fence2")));
	}

	private static double number(double value, double inherited)
	{
		return Double.isNaN(value) ? inherited : value;
	}

	private static String text(String value, String inherited)
	{
		return value == null || value.isEmpty() ? inherited : value;
	}

	/** What the game makes differently of a side's own value: the field is the value's name */
	public static final class Problem
	{
		/** border style, border height, barrier style, barrier width or barrier height */
		public final String	field;
		/** what the game does, starting with the field's name */
		public final String	text;

		Problem(String field, String text)
		{
			this.field = field;
			this.text = text;
		}
	}

	/**
	 * @param previousSegment the segment before, null for the first one
	 * @return the values the side has that the game ignores or reads differently,
	 *         as Check and the segment dialog show them
	 */
	public static java.util.List<Problem> problems(Segment segment, Segment previousSegment, boolean right, EditorFrame editorFrame)
	{
		java.util.List<Problem> problems = new ArrayList<Problem>();
		SegmentSide side = right ? segment.getRight() : segment.getLeft();
		State state = of(segment, right, editorFrame);
		EditorFrame.GameType game = editorFrame.getGameType();

		// border
		String borderStyle = side.getBorderStyle();
		if (borderStyle != null && !borderStyle.isEmpty() &&
			!borderStyle.equals("plan") && !borderStyle.equals("curb") && !borderStyle.equals("wall"))
		{
			problems.add(new Problem("border style", "border style \"" + borderStyle + "\" isn't a border style, so the game makes it a wall"));
		}
		// (TORCS keeps a plan border's height for the borders after it, so it isn't ignored there)
		if (game.planBorderResetsHeight() &&
			state.borderStyle.equals("plan") && !Double.isNaN(side.getBorderHeight()) && side.getBorderHeight() != 0)
		{
			problems.add(new Problem("border height", "border style is plan with " + side.getBorderHeight() + " height, which the game ignores (it uses 0): set border style to curb or wall or don't set a height"));
		}

		// barrier
		String barrierStyle = side.getBarrierStyle();
		boolean fence = state.barrierStyle.startsWith("fence");
		boolean walls = game.otherBarrierStylesAreWalls();
		if (barrierStyle != null && !barrierStyle.isEmpty() && !barrierStyle.equals("wall") &&
			!(barrierStyle.equals("no barrier") && !walls) &&
			!barrierStyle.equals("fence") && !(game.hasFence1Fence2() && (barrierStyle.equals("fence1") || barrierStyle.equals("fence2"))))
		{
			if (walls)
				problems.add(new Problem("barrier style", "barrier style \"" + barrierStyle + "\" isn't fence or wall, so the game makes it a wall"));
			else
				problems.add(new Problem("barrier style", "barrier style \"" + barrierStyle + "\" isn't one the game knows, so it keeps the " + state.barrierStyle + " from before"));
		}
		if ((fence || state.barrierStyle.equals("no barrier")) && !Double.isNaN(side.getBarrierWidth()) && side.getBarrierWidth() != 0)
		{
			problems.add(new Problem("barrier width", "barrier style is " + state.barrierStyle + " with " + side.getBarrierWidth() + " width, which the game ignores (it uses 0): set barrier style to wall or don't set a width"));
		}
		if (state.barrierStyle.equals("no barrier") && !Double.isNaN(side.getBarrierHeight()) && side.getBarrierHeight() != 0)
		{
			problems.add(new Problem("barrier height", "barrier style is no barrier with " + side.getBarrierHeight() + " height, which the game ignores (it uses 0)"));
		}

		if (previousSegment == null)
			return problems;

		// a new style takes its size from the previous segment unless it is set
		State previous = of(previousSegment, right, editorFrame);
		if (!state.borderStyle.equals(previous.borderStyle) && !state.borderStyle.equals("plan") && Double.isNaN(side.getBorderHeight()))
		{
			problems.add(new Problem("border height", "border style changed from " + previous.borderStyle + " to " + state.borderStyle + " but new height not set (the game uses " + state.borderHeight + ")"));
		}
		if (!state.barrierStyle.equals(previous.barrierStyle) && state.barrierStyle.equals("wall") && Double.isNaN(side.getBarrierWidth()))
		{
			problems.add(new Problem("barrier width", "barrier style changed from " + previous.barrierStyle + " to wall but new width not set (the game uses " + state.barrierWidth + ")"));
		}
		// CSR's track loader takes a missing barrier style from a list that has no
		// "no barrier", so after one it reads past the end of the list (once per run
		// of segments: reported where the run starts)
		SegmentSide previousSide = right ? previousSegment.getRight() : previousSegment.getLeft();
		if (editorFrame.getCarsSportsRacing() && "no barrier".equals(previousSide.getBarrierStyle()) &&
			(barrierStyle == null || barrierStyle.isEmpty()))
		{
			problems.add(new Problem("barrier style", "follows a no barrier side without setting its own barrier style: CSR then uses an undefined style for it and the segments after it until one sets a barrier style, and can crash"));
		}
		return problems;
	}
}
