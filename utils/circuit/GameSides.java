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

import utils.GameType;
import utils.check.TrackContext;

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
 * <li>CSR's version 4 loader (experimental in the editor, see
 *     TrackContext.getVariableBorderWidth): a segment's border "width" sets both of its
 *     widths; without one, a "start width" that isn't set is the previous end width,
 *     and an "end width" that isn't set is the start width. The Main Track only has
 *     "width". Heights the same way, except that an end with no width and no height
 *     set is 0 high, and a curb whose segment sets "height" and whose widths differ
 *     keeps the slope of its wide end.</li>
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
		// the border's widths at the segment's start and end: both borderWidth unless
		// borders can vary (TrackContext.getVariableBorderWidth)
		public double	borderStartWidth;
		public double	borderEndWidth;
		public double	borderHeight;
		// the border's heights at the segment's start and end: both borderHeight unless
		// borders can vary (TrackContext.getVariableBorderWidth)
		public double	borderStartHeight;
		public double	borderEndHeight;
		/** a curb whose segment sets "height" and whose widths differ: its heights are worked out from its widths */
		public boolean	borderHeightsFromWidths;
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
	public static State of(Segment segment, boolean right, TrackContext trackContext)
	{
		GameType game = trackContext.getGameType();
		boolean version3 = trackContext.getTrackData().getHeader().getVersion() == 3;
		boolean variable = trackContext.getVariableBorderWidth();
		MainTrack mainTrack = trackContext.getTrackData().getMainTrack();
		State state = start(right ? mainTrack.getRight() : mainTrack.getLeft(), game, version3);

		// the segments from the first one to this one
		ArrayList<Segment> chain = new ArrayList<Segment>();
		int limit = trackContext.getTrackData().getSegments() == null ? 0 : trackContext.getTrackData().getSegments().size();
		for (Segment s = segment; s != null && chain.size() <= limit; s = s.previousShape)
			chain.add(s);

		for (int i = chain.size() - 1; i >= 0; i--)
		{
			Segment s = chain.get(i);
			apply(state, right ? s.getRight() : s.getLeft(), game, version3, variable);
		}

		return state;
	}

	private static State start(SegmentSide main, GameType game, boolean version3)
	{
		State s = new State();

		// the Main Track has only "width": the editor keeps it as equal start and end widths
		s.sideEndWidth = number(main.getMainTrackSideWidth(), 0.0);
		s.sideStartWidth = s.sideEndWidth;
		s.sideSurface = text(main.getSideSurface(), "grass");

		s.borderWidth = number(main.getBorderWidth(), 0.0);
		s.borderStartWidth = s.borderWidth;
		s.borderEndWidth = s.borderWidth;
		s.borderHeight = number(main.getBorderHeight(), 0.0);
		s.borderStartHeight = s.borderHeight;
		s.borderEndHeight = s.borderHeight;
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

	private static void apply(State s, SegmentSide side, GameType game, boolean version3, boolean variable)
	{
		// side
		double start = side.getSideStartWidth();
		double end = side.getSideEndWidth();
		s.sideStartWidth = number(start, s.sideEndWidth);
		s.sideEndWidth = number(end, s.sideStartWidth);
		s.sideSurface = text(side.getSideSurface(), s.sideSurface);

		// border
		if (variable && Double.isNaN(side.getBorderWidth()))
		{
			s.borderStartWidth = number(side.getBorderStartWidth(), s.borderEndWidth);
			s.borderEndWidth = number(side.getBorderEndWidth(), s.borderStartWidth);
			s.borderWidth = s.borderStartWidth;
		}
		else
		{
			s.borderWidth = number(side.getBorderWidth(), s.borderWidth);
			s.borderStartWidth = s.borderWidth;
			s.borderEndWidth = s.borderWidth;
		}
		// heights the same way as widths: a missing start height is the previous end
		// height, and a missing end height is the start height; but an end that has
		// no width and no height set is 0 high (the end height then isn't that 0, it
		// is the start height the segment would otherwise have)
		if (variable && Double.isNaN(side.getBorderHeight()))
		{
			double inherited = number(side.getBorderStartHeight(), s.borderEndHeight);
			s.borderStartHeight = Double.isNaN(side.getBorderStartHeight()) && s.borderStartWidth == 0 ? 0.0 : inherited;
			s.borderEndHeight = !Double.isNaN(side.getBorderEndHeight()) ? side.getBorderEndHeight()
				: s.borderEndWidth == 0 ? 0.0 : inherited;
			s.borderHeight = s.borderStartHeight;
		}
		else
		{
			// in TORCS version 3, a segment without a border height has a height of 0
			s.borderHeight = number(side.getBorderHeight(), version3 && game == GameType.TORCS ? 0.0 : s.borderHeight);
			s.borderStartHeight = s.borderHeight;
			s.borderEndHeight = s.borderHeight;
		}
		s.borderSurface = text(side.getBorderSurface(), s.borderSurface);
		setBorderStyle(s, text(side.getBorderStyle(), s.borderStyle), game);
		// CSR: a curb whose segment sets "height" and whose widths differ keeps the
		// slope of its wide end, so its height goes with its width: from that height
		// at the wide end down in proportion. These are its heights as it is drawn
		// and saved, and the next segment's border starts as high as this one ends.
		// Start and end heights, and a height carried on from the previous segment,
		// are used as they are: equal ones give a curb of one height whatever its
		// width.
		s.borderHeightsFromWidths = false;
		if (variable && !Double.isNaN(side.getBorderHeight()) && "curb".equals(s.borderStyle)
			&& Math.abs(s.borderStartWidth - s.borderEndWidth) >= TAPER_TOLERANCE)
		{
			double wide = Math.max(s.borderStartWidth, s.borderEndWidth);
			double height = s.borderStartHeight;
			s.borderStartHeight = height * s.borderStartWidth / wide;
			s.borderEndHeight = height * s.borderEndWidth / wide;
			s.borderHeightsFromWidths = true;
		}

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

	private static void setBorderStyle(State s, String style, GameType game)
	{
		if (style.equals("plan"))
		{
			s.borderStyle = "plan";
			if (game.planBorderResetsHeight())
			{
				s.borderHeight = 0.0;
				s.borderStartHeight = 0.0;
				s.borderEndHeight = 0.0;
			}
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

	private static boolean isFence(String style, GameType game)
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
		/** side start width, side end width, border style, border height, barrier style, barrier width or barrier height */
		public final String	field;
		/** what the game does, starting with the field's name */
		public final String	text;
		/** whether the game folds the side over, past the curve's center */
		public final boolean	folds;

		Problem(String field, String text)
		{
			this(field, text, false);
		}

		Problem(String field, String text, boolean folds)
		{
			this.field = field;
			this.text = text;
			this.folds = folds;
		}
	}

	/**
	 * @param previousSegment the segment before, null for the first one
	 * @return the values the side has that the game ignores or reads differently,
	 *         as Check and the segment dialog show them
	 */
	/**
	 * On the inside of a curve, the side, its border and a wall barrier can reach
	 * past the curve's center: the game then builds them with a negative radius,
	 * so they cross over the center and fold over, the barrier inside out.
	 */
	private static void insideCurve(java.util.List<Problem> problems, Segment segment, boolean right, State state, TrackContext trackContext)
	{
		if (!(segment instanceof Curve) || right != segment.getType().equals("rgt"))
			return;
		Curve curve = (Curve) segment;
		double halfWidth = trackContext.getTrackData().getMainTrack().getValidWidth() / 2;
		// a border's width can change along the segment (CSR, experimental)
		double[] borders = { state.borderStartWidth, state.borderEndWidth };
		// the games whose segments can have no barrier (version 3 tracks take the barrier from the Main Track)
		boolean noBarrier = !trackContext.getGameType().otherBarrierStylesAreWalls()
			&& trackContext.getTrackData().getHeader().getVersion() != 3;
		String[] ends = { "start", "end" };
		double[] radii = { curve.getRadiusStart(), curve.getValidRadiusEnd() };
		double[] sides = { state.sideStartWidth, state.sideEndWidth };
		for (int i = 0; i < 2; i++)
		{
			// the same radius at both ends is reported once, at the end that is wider
			if (i == 0 && radii[0] == radii[1] && sides[1] + borders[1] >= sides[0] + borders[0])
				continue;
			if (i == 1 && radii[0] == radii[1] && sides[0] + borders[0] > sides[1] + borders[1])
				continue;
			double toBorder = halfWidth + borders[i];
			double side = toBorder + sides[i];
			double reach = side + state.barrierWidth;
			if (!(reach > radii[i]))
				continue;
			boolean sideReaches = side > radii[i];
			String at = radii[0] == radii[1] && borders[0] == borders[1] ? "" : " at its " + ends[i];
			String radius = radii[0] == radii[1] ? "radius" : ends[i] + " radius";
			String widthName = "the side's " + (sides[0] == sides[1] ? "" : ends[i] + " ") + "width";
			String fix;
			if (noBarrier && !state.barrierStyle.equals("no barrier"))
			{
				// a side that ends at the center leaves its barrier no length: the style for that is no barrier
				fix = sideReaches
					? narrower(widthName, sides[i], borders[i], radii[i] - toBorder) + " and its barrier style to no barrier (a barrier at the center has no length), "
						+ larger(radius, reach)
					: "set its barrier style to no barrier, or " + narrower(widthName, sides[i], borders[i], radii[i] - toBorder - state.barrierWidth)
						+ ", " + larger(radius, reach);
			}
			else
			{
				fix = narrower(widthName, sides[i], borders[i], radii[i] - toBorder - state.barrierWidth) + ", " + larger(radius, reach);
			}
			String text = String.format(java.util.Locale.US, "%s %.2f m in from the middle of the track%s, past the curve's center "
				+ "(%s %.2f m), so the game folds the side and its barrier over: %s",
				!sideReaches ? "wall barrier reaches" : state.barrierWidth > 0 ? "side and its wall barrier reach" : "side reaches",
				reach, at, radius, radii[i], fix);
			// the same width at both ends shows on the one the segment sets
			SegmentSide own = right ? segment.getRight() : segment.getLeft();
			String field = sides[0] != sides[1] ? ends[i] : !Double.isNaN(own.getSideStartWidth()) || Double.isNaN(own.getSideEndWidth()) ? "start" : "end";
			problems.add(new Problem(sideReaches ? "side " + field + " width" : "barrier width", text, true));
		}
	}

	/**
	 * @return how to make the side room wide at most, or its border narrower when
	 *         the side alone can't be (room is less than 0)
	 */
	private static String narrower(String widthName, double sideWidth, double borderWidth, double room)
	{
		if (room >= 0)
			return String.format(java.util.Locale.US, "set %s to %.2f m or less (it is %.2f m)", widthName, room, sideWidth);
		double border = borderWidth + room;
		if (border >= 0)
			return String.format(java.util.Locale.US, "set %s to 0 and its border's width to %.2f m or less (it is %.2f m)", widthName, border, borderWidth);
		// the track itself reaches past the center
		return "make the track narrower";
	}

	/** @return the radius that leaves room for what reaches that far in */
	private static String larger(String radius, double reach)
	{
		return String.format(java.util.Locale.US, "or set the %s to %.2f m or more", radius, reach);
	}

	public static java.util.List<Problem> problems(Segment segment, Segment previousSegment, boolean right, TrackContext trackContext)
	{
		java.util.List<Problem> problems = new ArrayList<Problem>();
		SegmentSide side = right ? segment.getRight() : segment.getLeft();
		State state = of(segment, right, trackContext);
		GameType game = trackContext.getGameType();

		// border
		String borderStyle = side.getBorderStyle();
		if (borderStyle != null && !borderStyle.isEmpty() &&
			!borderStyle.equals("plan") && !borderStyle.equals("curb") && !borderStyle.equals("wall"))
		{
			problems.add(new Problem("border style", "border style \"" + borderStyle + "\" isn't valid, so the game treats it as a wall"));
		}
		// (TORCS keeps a plan border's height for the borders after it, so it isn't ignored there)
		if (game.planBorderResetsHeight() && state.borderStyle.equals("plan"))
		{
			// (CSR: a start or end height too)
			String[] names = { "height", "start height", "end height" };
			double[] heights = { side.getBorderHeight(), side.getBorderStartHeight(), side.getBorderEndHeight() };
			for (int i = 0; i < names.length; i++)
			{
				if (!Double.isNaN(heights[i]) && heights[i] != 0)
				{
					problems.add(new Problem("border height", "border style is plan, but a " + names[i] + " of " + heights[i] + " is set. The game ignores it and uses 0. Set the border style to curb or wall, or remove the " + names[i]));
					break;
				}
			}
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
				problems.add(new Problem("barrier style", "barrier style \"" + barrierStyle + "\" isn't fence or wall, so the game treats it as a wall"));
			else
				problems.add(new Problem("barrier style", "barrier style \"" + barrierStyle + "\" isn't recognized by the game, so it keeps the previous style (" + state.barrierStyle + ")"));
		}
		if ((fence || state.barrierStyle.equals("no barrier")) && !Double.isNaN(side.getBarrierWidth()) && side.getBarrierWidth() != 0)
		{
			problems.add(new Problem("barrier width", "barrier style is " + state.barrierStyle + ", but a width of " + side.getBarrierWidth() + " is set. The game ignores it and uses 0. Set the barrier style to wall, or remove the width"));
		}
		if (state.barrierStyle.equals("no barrier") && !Double.isNaN(side.getBarrierHeight()) && side.getBarrierHeight() != 0)
		{
			problems.add(new Problem("barrier height", "barrier style is no barrier, but a height of " + side.getBarrierHeight() + " is set. The game ignores it and uses 0"));
		}

		insideCurve(problems, segment, right, state, trackContext);

		if (previousSegment == null)
			return problems;

		// a new style takes its size from the previous segment unless it is set
		State previous = of(previousSegment, right, trackContext);
		if (!state.borderStyle.equals(previous.borderStyle) && !state.borderStyle.equals("plan") && Double.isNaN(side.getBorderHeight())
			&& Double.isNaN(side.getBorderStartHeight()) && Double.isNaN(side.getBorderEndHeight()))
		{
			// (CSR: the heights it takes can differ at its start and end)
			String uses = Math.abs(state.borderStartHeight - state.borderEndHeight) < TAPER_TOLERANCE ? String.valueOf(state.borderStartHeight)
				: "a start height of " + number(state.borderStartHeight) + " and an end height of " + number(state.borderEndHeight);
			problems.add(new Problem("border height", "border style changed from " + previous.borderStyle + " to " + state.borderStyle + ", but no new height is set (the game uses " + uses + ")"));
		}
		if (!state.barrierStyle.equals(previous.barrierStyle) && state.barrierStyle.equals("wall") && Double.isNaN(side.getBarrierWidth()))
		{
			problems.add(new Problem("barrier width", "barrier style changed from " + previous.barrierStyle + " to wall, but no new width is set (the game uses " + state.barrierWidth + ")"));
		}
		// CSR's track loader takes a missing barrier style from a list that has no
		// "no barrier", so after one it reads past the end of the list (once per run
		// of segments: reported where the run starts)
		SegmentSide previousSide = right ? previousSegment.getRight() : previousSegment.getLeft();
		if (trackContext.getCarsSportsRacing() && "no barrier".equals(previousSide.getBarrierStyle()) &&
			(barrierStyle == null || barrierStyle.isEmpty()))
		{
			problems.add(new Problem("barrier style", "comes after a side with no barrier but doesn't set its own barrier style. CSR then uses an undefined style for it and the following segments until one sets a style, and it can crash"));
		}
		return problems;
	}

	/** the tolerance for a curb's widths and heights, in meters, and for two curbs' slopes */
	private static final double	TAPER_TOLERANCE	= 0.001;

	/**
	 * CSR: where a segment's border meets the next segment's and both have the same
	 * surface, it is one border, and a width or a height that changes there is a step
	 * in it. The widths and heights are the ones the game uses (see apply): a height
	 * matters only where both have a width.
	 *
	 * @return what is wrong, starting with "border ends", when the border's width or
	 *         height steps where it meets the next segment's; null otherwise
	 */
	public static String borderStep(Segment segment, Segment next, boolean right, TrackContext trackContext)
	{
		if (segment == null || next == null || segment == next || !trackContext.getVariableBorderWidth())
			return null;
		State border = of(segment, right, trackContext);
		State after = of(next, right, trackContext);
		if (border.borderSurface == null || !border.borderSurface.equals(after.borderSurface))
			return null;
		double width = border.borderEndWidth, nextWidth = after.borderStartWidth;
		// no border on either side of the joint
		if (width < TAPER_TOLERANCE && nextWidth < TAPER_TOLERANCE)
			return null;
		double height = border.borderEndHeight, nextHeight = after.borderStartHeight;
		boolean widthSteps = Math.abs(width - nextWidth) >= TAPER_TOLERANCE;
		boolean heightSteps = width >= TAPER_TOLERANCE && nextWidth >= TAPER_TOLERANCE && Math.abs(height - nextHeight) >= TAPER_TOLERANCE;
		if (!widthSteps && !heightSteps)
			return null;

		String side = right ? "right" : "left";
		String what = widthSteps && heightSteps ? "its width and height step" : widthSteps ? "its width steps" : "its height steps";
		String fixNext = next.getName() + "'s " + values("start", widthSteps, width, heightSteps, height);
		String fixThis = segment.getName() + "'s " + values("end", widthSteps, nextWidth, heightSteps, nextHeight);
		return "border ends " + number(width) + " m wide and " + number(height) + " m high, but the " + side
			+ " border of the next segment, " + next.getName() + ", which has the same surface (" + border.borderSurface + "), starts "
			+ number(nextWidth) + " m wide and " + number(nextHeight) + " m high, so " + what + " there. Set " + fixNext + ", or " + fixThis;
	}

	/** @return "start width to 1 m and start height to 0.05 m", for the values that step */
	private static String values(String end, boolean width, double widthValue, boolean height, double heightValue)
	{
		String text = width ? end + " width to " + number(widthValue) + " m" : "";
		if (height)
			text += (width ? " and " : "") + end + " height to " + number(heightValue) + " m";
		return text;
	}

	/**
	 * CSR: where one curb meets the next, their tops should have the same slope
	 * (height / width), or they don't meet. A curb's height at an end is its start or
	 * end height when they differ; one whose segment sets "height" and whose start
	 * and end widths differ keeps the slope of its wide end, so its height there
	 * tapers with its width; otherwise it is its height. Checked only where either curb's widths or
	 * heights change along it: two curbs of one width and one height each can be
	 * different heights on purpose.
	 *
	 * @return what is wrong, starting with "curb ends", when the segment's curb and
	 *         the next segment's meet at different slopes; null otherwise
	 */
	public static String curbSlopeMismatch(Segment segment, Segment next, boolean right, TrackContext trackContext)
	{
		if (segment == null || next == null || !trackContext.getVariableBorderWidth())
			return null;
		// borders of one surface that step where they meet: borderStep says so (and
		// one that doesn't step meets at the same slope)
		if (borderStep(segment, next, right, trackContext) != null)
			return null;
		State curb = of(segment, right, trackContext);
		State after = of(next, right, trackContext);
		if (!isCurb(curb) || !isCurb(after) || (!varies(curb) && !varies(after)))
			return null;
		// a curb that narrows to nothing, or starts from nothing, has no top to meet
		double width = curb.borderEndWidth, nextWidth = after.borderStartWidth;
		if (width < TAPER_TOLERANCE || nextWidth < TAPER_TOLERANCE)
			return null;
		double height = curbHeight(curb, true), nextHeight = curbHeight(after, false);
		double slope = height / width, nextSlope = nextHeight / nextWidth;
		if (Math.abs(slope - nextSlope) <= TAPER_TOLERANCE)
			return null;
		String side = right ? "right" : "left";
		boolean wideEnd = curb.borderHeightsFromWidths;
		// the fixes: the next curb starting at this one's slope, or this one ending at
		// the next one's, each keeping its other end as high as it is now
		String fixNext = fix(next.getName(), after, slope * nextWidth, curbHeight(after, true), true);
		String fixThis = fix(segment.getName(), curb, curbHeight(curb, false), nextSlope * width, false);
		String fixes = fixNext != null && fixThis != null ? "Set " + fixNext + ", or " + fixThis
			: fixNext != null || fixThis != null ? "Set " + (fixNext != null ? fixNext : fixThis)
			: "Change their widths or heights so they meet";
		return "curb ends " + number(width) + " m wide and " + number(height) + " m high, a slope of " + number(slope * 100) + "%"
			+ (wideEnd ? " (a curb whose width changes and whose height is set with \"height\" keeps the slope of its wide end)" : "")
			+ ", but the " + side + " curb of the next segment, " + next.getName() + ", starts " + number(nextWidth) + " m wide and "
			+ number(nextHeight) + " m high, " + number(nextSlope * 100) + "%, so their tops don't meet. " + fixes;
	}

	/**
	 * @return how to give a curb these heights at its start and end: the one that
	 *         changes when its heights already differ, both otherwise (one alone
	 *         would leave the other inherited); the same at both ends is its height,
	 *         or its start and end height when its widths differ, as "height" would
	 *         make it taper with its width
	 */
	private static String fix(String name, State curb, double start, double end, boolean startChanges)
	{
		if (Math.abs(start - end) < TAPER_TOLERANCE / 2)
			return Math.abs(curb.borderStartWidth - curb.borderEndWidth) >= TAPER_TOLERANCE
				? name + "'s start height and end height to " + number(start) + " m"
				: name + "'s height to " + number(start) + " m";
		if (Math.abs(curb.borderStartHeight - curb.borderEndHeight) >= TAPER_TOLERANCE && !curb.borderHeightsFromWidths)
			return name + "'s " + (startChanges ? "start height to " + number(start) : "end height to " + number(end)) + " m";
		return name + "'s start height to " + number(start) + " m and end height to " + number(end) + " m";
	}

	private static boolean isCurb(State state)
	{
		return "curb".equals(state.borderStyle) && (state.borderStartHeight > 0 || state.borderEndHeight > 0)
			&& (state.borderStartWidth > 0 || state.borderEndWidth > 0);
	}

	/** @return whether a border's width or height changes along it */
	private static boolean varies(State border)
	{
		return Math.abs(border.borderStartWidth - border.borderEndWidth) >= TAPER_TOLERANCE
			|| Math.abs(border.borderStartHeight - border.borderEndHeight) >= TAPER_TOLERANCE;
	}

	/**
	 * @return how high a curb is at its end or start (borderHeights)
	 */
	public static double curbHeight(State curb, boolean end)
	{
		return borderHeights(curb)[end ? 1 : 0];
	}

	/**
	 * @return how high a border is at its start and its end, as it is drawn and
	 *         saved (a curb whose segment sets "height" and whose widths differ: worked out from its
	 *         widths, see apply)
	 */
	public static double[] borderHeights(State border)
	{
		return new double[] { border.borderStartHeight, border.borderEndHeight };
	}

	/** @return a number to at most 3 significant digits */
	private static String number(double value)
	{
		return new java.math.BigDecimal(value).round(new java.math.MathContext(3)).stripTrailingZeros().toPlainString();
	}
}
