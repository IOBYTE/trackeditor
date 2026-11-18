/*
 *   TrackChecker.java
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

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.imageio.ImageIO;

import utils.GameType;
import utils.Properties;
import utils.SegmentVector;
import utils.TrackData;
import utils.ac3d.Ac3d;
import utils.ac3d.Ac3dException;
import utils.ac3d.Ac3dMaterial;
import utils.ac3d.Ac3dObject;
import utils.ac3d.Ac3dSurface;
import utils.circuit.Curve;
import utils.circuit.EnvironmentMapping;
import utils.circuit.GameSides;
import utils.circuit.Graphic;
import utils.circuit.GraphicObject;
import utils.circuit.ObjShapeObject;
import utils.circuit.ObjShapeTerrain;
import utils.circuit.ObjectMap;
import utils.circuit.Pits;
import utils.circuit.Segment;
import utils.circuit.SegmentSide;
import utils.circuit.Surface;
import utils.circuit.TerrainGeneration;
import utils.circuit.TrackLight;
import utils.circuit.TrackObject;

/**
 * Checks a track for what the game won't read as meant, and says what each
 * message is about. It needs no window: Check shows its messages, and the
 * messages say what to show when double-clicked (see Subject).
 */
public class TrackChecker
{
	private final String 		sep 			= System.getProperty("file.separator");
	private final java.util.List<CheckMessage>	messages	= new java.util.ArrayList<CheckMessage>();
	private final Report		report			= new Report();
	private final TrackContext	trackContext;
	private Vector<Surface>		defaultSurfaces	= null;
	private Vector<TrackObject>	defaultObjects	= null;
	private Boolean				doubleSided		= false;
	private Boolean				flatShaded		= false;
	private Set<String> 		textures 		= new HashSet<String>();
	private String				dataDirectory	= null;
	// the segments whose own message says the road or its sides fold over or pass
	// through themselves: an overlap of one, or of the two next to it, is the same
	// problem (see checkCrossings)
	private final Set<String>	foldedSegments	= new HashSet<String>();
	// the segments whose heights Check reports as not agreeing: a steep grade or
	// a crest at one, or next to one, is the same problem (see checkProfile)
	private final Set<String>	heightProblems	= new HashSet<String>();
	private TrackData			trackData		= null;
	private Map<String,Boolean>	imageHasAlpha	= new HashMap<String, Boolean>();

	public TrackChecker(TrackContext trackContext)
	{
		this.trackContext = trackContext;
	}

	/**
	 * Checks the track as it is now.
	 *
	 * @return the messages, in order, ending with "Checking complete!"; none
	 *         when there is no track
	 */
	public java.util.List<CheckMessage> check()
	{
		messages.clear();
		report.reset();
		trackData = trackContext.getTrackData();
		if (trackData == null)
			return new java.util.ArrayList<CheckMessage>();
		dataDirectory = trackContext.getDataDirectory();
		defaultSurfaces = trackData.getHasSurfacesReference() ? trackContext.getDefaultSurfaces() : new Vector<Surface>();
		defaultObjects = trackData.getHasObjectsReference() ? trackContext.getDefaultObjects() : new Vector<TrackObject>();

		// what the messages of each check are about, unless they say
		check(null, this::checkSharedFiles);
		check(null, this::checkSectionNames);
		check(null, () ->
		{
			int before = messages.size();
			checkTrackHeight();
			// the segments whose heights it reports, for checkProfile
			for (int i = before; i < messages.size(); i++)
			{
				Segment segment = messages.get(i).getSubject() == null ? null : messages.get(i).getSubject().getSegment();
				if (segment != null)
					heightProblems.add(segment.getName());
			}
		});
		check(null, this::checkSurfaces);
		check(properties("Objects"), this::checkObjects);
		check(properties("Terrain"), this::checkTerrainGeneration);
		check(properties("Env Map"), this::checkEnvironmentMapping);
		if (trackContext.getGameType().hasSpeedDreamsExtensions())
			check(properties("Lights"), this::checkTrackLights);
		check(properties("Graphic"), this::checkGraphic);
		check(properties("Graphic 3D"), this::checkGraphic3D);
		check(null, this::checkMenuImages);
		check(properties("Pit"), this::checkPits);
		check(properties("Cameras"), this::checkCameras);
		check(null, this::checkTrack);
		check(null, this::checkVariableBorders);
		check(null, this::checkTaperedCurbs);
		check(null, this::checkCrossings);
		check(null, this::checkProfile);
		check(properties("General"), this::checkHeader);
		check(properties("Georeference"), this::checkGeoreference);
		// lines of the file: what saving drops isn't in the editor to show
		check(null, this::checkUnusedKeys);
		// last: the values no other check has reported
		check(null, this::checkNumbers);

		report.append("Checking complete!\n");
		return new java.util.ArrayList<CheckMessage>(messages);
	}

	/**
	 * @return the file as the games find one: the name, with any folder in it,
	 *         in the first of the folders that has it, or null. A full path isn't
	 *         found: the games put it after their folders too (see isFullPath).
	 */
	public static File findFile(String name, String... folders)
	{
		if (isFullPath(name))
			return null;
		for (String folder : folders)
		{
			if (folder == null)
				continue;
			File file = new File(folder, name);
			if (file.isFile())
				return file;
		}
		return null;
	}

	/**
	 * The radius above which TORCS's track tools (torcs-audit-track-radii) flag a
	 * curve for review: its shape depends on the track loader's floating-point
	 * rounding. It isn't wrong, so the message says what to look at.
	 */
	private static final double	LARGE_RADIUS			= 4000;
	private static final String	LARGE_RADIUS_MESSAGE	= " m is more than " + (int) LARGE_RADIUS + " m: the shape of a curve this large "
		+ "depends on the track loader's floating-point rounding, so check it closes as expected (a straight may suit better)\n";

	/** How the messages suggesting converting a texture to png end */
	private static final String	PNG_MESSAGE	= CheckMessage.PNG_MESSAGE;

	/** For a message about nothing the editor can show */
	private static final Subject	NONE	= Subject.NONE;

	/**
	 * Collects the messages: text up to a new line is one message, about the
	 * target given with it or else the one set with about().
	 */
	private class Report
	{
		private final StringBuilder	line	= new StringBuilder();
		private Subject				lineTarget;
		private boolean				lineStarted;
		private Subject				context;
		// whether the messages are about an object's model: its AC3D file or textures
		boolean						objectFile;
		private boolean				lineObjectFile;

		/**
		 * The target of the messages that don't give one, until it is set again.
		 *
		 * @return the one it replaces
		 */
		Subject about(Subject target)
		{
			Subject previous = context;
			context = target;
			return previous;
		}

		void append(String text)
		{
			append(text, null);
		}

		void append(String text, Subject target)
		{
			for (int i = 0; i < text.length(); i++)
			{
				if (!lineStarted)
				{
					lineTarget = target != null ? target : context;
					lineObjectFile = objectFile;
					lineStarted = true;
				}
				char c = text.charAt(i);
				if (c != '\n')
				{
					line.append(c);
					continue;
				}
				messages.add(new CheckMessage(line.toString(), lineTarget == NONE ? null : lineTarget, lineObjectFile));
				line.setLength(0);
				lineStarted = false;
			}
		}

		void reset()
		{
			line.setLength(0);
			lineStarted = false;
			context = null;
			objectFile = false;
		}
	}

	/** Runs a check whose messages are about the target unless they say otherwise */
	private void check(Subject about, Runnable check)
	{
		report.about(about);
		try
		{
			check.run();
		}
		finally
		{
			report.about(null);
		}
	}

	/** @return the segment, to select in the track view and open in the segment dialog */
	private Subject segment(final Segment segment)
	{
		return segment(segment, null);
	}

	/**
	 * @return the segment, to select in the track view and open in the segment dialog
	 *         on the tab: Left or Right for a side, null for as it is (see show)
	 */
	private Subject segment(final Segment segment, final String tab)
	{
		return Subject.segment(segment, tab);
	}

	/** @return a target for the segment with the name, null if there is none */
	private Subject segment(String name)
	{
		return name == null ? null : segment(trackData.getSegments().getSegmentFromName(name.trim()));
	}

	/**
	 * @return a tab of the Properties dialog, and in it the tab with the next title
	 *         and so on (see PropertiesDialog.showTabs)
	 */
	private Subject properties(final String... tabs)
	{
		return Subject.properties(tabs);
	}

	private void checkTrack()
	{
		SegmentVector segments = trackData.getSegments();

		if (segments.isEmpty())
		{
			report.append("The track has no segments\n");
		}

		checkTrackCloses(segments);

		for (Segment segment : segments)
		{
			report.about(segment(segment));
			checkSegmentSize(segment);
			checkSegmentLimits(segment);
		}
		double trackSteps = Segment.getTrackSteps(trackContext);
		if (trackSteps > Segment.MAX_TRACK_STEPS)
			report.append(String.format(java.util.Locale.US, "The track needs %,.0f steps, more than the editor's limit of %,d. "
				+ "The editor draws each segment with fewer steps, and the game may be slow to load it.\n", trackSteps, Segment.MAX_TRACK_STEPS), NONE);
		report.about(properties("Track"));
		double mainWidth = trackData.getMainTrack().getWidth();
		if (!Double.isNaN(mainWidth) && mainWidth <= 0)
			report.append("Main Track: width is " + format(mainWidth) + " m. It must be greater than 0.\n");
		double mainStepsLength = trackData.getMainTrack().getProfilStepsLength();
		if (mainStepsLength > 0 && mainStepsLength < Segment.MIN_PROFIL_STEPS_LENGTH)
			report.append("Main Track: profil steps length " + format(mainStepsLength) + " m is shorter than the editor allows ("
				+ format(Segment.MIN_PROFIL_STEPS_LENGTH) + " m), so segments that use it need a very large number of steps\n");
		else if (mainStepsLength < 0)
			report.append("Main Track: profil steps length " + format(mainStepsLength) + " m is negative. It must be 0 (none) or at least "
				+ format(Segment.MIN_PROFIL_STEPS_LENGTH) + " m. Segments that use it are built in one step.\n");
		report.about(null);

		// a side reaching past a curve's center: GameSides.problems, in checkSide

		report.about(properties("Track", "Left"));
		checkMainTrackSide(trackData.getMainTrack().getLeft(), "Left");
		report.about(properties("Track", "Right"));
		checkMainTrackSide(trackData.getMainTrack().getRight(), "Right");
		report.about(null);

		// the editor shows what the game uses (see GameSides); these point out values in the
		// file that the game ignores or that it takes from an earlier segment
		for (int i = 0; i < segments.size(); i++)
		{
			Segment segment = segments.get(i);
			Segment previousSegment = i > 0 ? segments.get(i - 1) : null;

			report.about(segment(segment, "Left"));
			checkSide(segment, previousSegment, segment.getLeft(), "Left", false);
			report.about(segment(segment, "Right"));
			checkSide(segment, previousSegment, segment.getRight(), "Right", true);
		}
		report.about(null);
	}

	/**
	 * Values the segment dialog doesn't accept (see the limits in Segment): a
	 * track read from a file can have them, and the editor builds such a segment
	 * in at most Segment.MAX_STEPS steps.
	 */
	private void checkSegmentLimits(Segment segment)
	{
		String name = "Segment " + segment.getName() + ": ";
		double length;
		if (segment.getType().equals("str"))
		{
			length = segment.getLength();
			if (length > Segment.MAX_LENGTH)
				report.append(name + "length " + format(length) + " m is longer than the editor allows (" + format(Segment.MAX_LENGTH) + " m)\n");
		}
		else
		{
			Curve curve = (Curve) segment;
			if (curve.getRadiusStart() > Segment.MAX_RADIUS)
				report.append(name + "radius " + format(curve.getRadiusStart()) + " m is larger than the editor allows (" + format(Segment.MAX_RADIUS) + " m)\n");
			else if (curve.getRadiusStart() > LARGE_RADIUS)
				report.append(name + "radius " + format(curve.getRadiusStart()) + LARGE_RADIUS_MESSAGE);
			if (curve.getValidRadiusEnd() > Segment.MAX_RADIUS && curve.getValidRadiusEnd() != curve.getRadiusStart())
				report.append(name + "end radius " + format(curve.getValidRadiusEnd()) + " m is larger than the editor allows (" + format(Segment.MAX_RADIUS) + " m)\n");
			else if (curve.getValidRadiusEnd() > LARGE_RADIUS && curve.getValidRadiusEnd() != curve.getRadiusStart())
				report.append(name + "end radius " + format(curve.getValidRadiusEnd()) + LARGE_RADIUS_MESSAGE);
			if (curve.getArcDeg() > Segment.MAX_ARC)
				report.append(name + "arc " + format(curve.getArcDeg()) + " deg is more than the editor allows (" + format(Segment.MAX_ARC) + " deg, five turns)\n");
			length = curve.getArcRad() * (curve.getRadiusStart() + curve.getValidRadiusEnd()) / 2;
		}
		if (segment.hasProfilSteps() && segment.getProfilSteps() > Segment.MAX_PROFIL_STEPS)
			report.append(name + "steps " + segment.getProfilSteps() + " is more than the editor allows (" + Segment.MAX_PROFIL_STEPS + ")\n");
		if (segment.hasProfilStepsLength() && segment.getProfilStepsLength() > 0 && segment.getProfilStepsLength() < Segment.MIN_PROFIL_STEPS_LENGTH)
			report.append(name + "steps length " + format(segment.getProfilStepsLength()) + " m is shorter than the editor allows ("
				+ format(Segment.MIN_PROFIL_STEPS_LENGTH) + " m)\n");
		else if (segment.hasProfilStepsLength() && segment.getProfilStepsLength() < 0)
			report.append(name + "steps length " + format(segment.getProfilStepsLength()) + " m is negative. It must be 0 (none) or at least "
				+ format(Segment.MIN_PROFIL_STEPS_LENGTH) + " m. The segment is built in one step.\n");
		double steps = segment.getGameSteps(trackContext, length);
		if (steps > Segment.MAX_STEPS)
			report.append(name + String.format(java.util.Locale.US, "the game builds it in %,.0f steps, more than the editor's limit of %,d. "
				+ "The editor draws it in %,d, and the game may be slow to load it.\n", steps, Segment.MAX_STEPS, Segment.MAX_STEPS));
	}

	/**
	 * A straight needs a length, and a curve a radius and an arc, greater than 0.
	 * The games read a missing one as 0, and build each segment in steps of the
	 * profile step length: with 0 a straight or an arc has no length (a curve with
	 * no radius turns on the spot), and with less than 0 the steps go backwards, or
	 * there are none and the segment isn't built.
	 */
	private void checkSegmentSize(Segment segment)
	{
		String name = "Segment " + segment.getName() + ": ";
		if (segment.getType().equals("str"))
		{
			double length = segment.getLength();
			if (Double.isNaN(length))
				report.append(name + "length is missing. The game reads it as 0, so the straight has no length.\n");
			else if (length == 0)
				report.append(name + "length 0: the straight has no length\n");
			else if (length < 0)
				report.append(name + "length " + format(length) + " m: a straight needs a length greater than 0. "
					+ "The game builds this one backward or not at all.\n");
			return;
		}

		Curve curve = (Curve) segment;
		double radius = curve.getRadiusStart();
		if (Double.isNaN(radius))
			report.append(name + "radius is missing. The game reads it as 0, so the curve turns in place.\n");
		else if (radius == 0)
			report.append(name + "radius is 0, so the curve turns in place\n");
		else if (radius < 0)
			report.append(name + "radius " + format(radius) + " m: a curve needs a radius greater than 0\n");

		// an end radius that isn't set is the start radius
		double end = curve.getValidRadiusEnd();
		if (!Double.isNaN(end) && end <= 0 && end != radius)
			report.append(name + "end radius " + format(end) + " m: a curve needs a radius greater than 0\n");

		// less than half the width: the road's inside edge crosses the curve's centre
		// (the segment dialog doesn't allow it)
		double half = trackData.getMainTrack().getValidWidth() / 2;
		if (radius > 0 && radius < half)
		{
			report.append(name + "radius " + format(radius) + " m is less than half the track's width (" + format(half)
				+ " m): the inside edge of the road crosses the curve's center\n");
			foldedSegments.add(curve.getName());
		}
		if (end > 0 && end < half && end != radius)
		{
			report.append(name + "end radius " + format(end) + " m is less than half the track's width (" + format(half)
				+ " m): the inside edge of the road crosses the curve's center\n");
			foldedSegments.add(curve.getName());
		}

		double arc = curve.getArcDeg();
		if (Double.isNaN(arc))
			report.append(name + "arc is missing. The game reads it as 0, so the curve has no length and doesn't turn.\n");
		else if (arc == 0)
			report.append(name + "arc 0: the curve has no length and doesn't turn\n");
		else if (arc < 0)
			report.append(name + "arc " + format(arc) + " deg: a curve needs an arc greater than 0. "
				+ "The game builds this one backward or not at all.\n");
		else if (arc > 360)
			checkFullTurns(curve, name);
	}

	/**
	 * the least two parts of the road that overlap in plan have to be apart in
	 * height, like a curve of more than a full turn each turn (m): a car's height
	 * with room to spare, less than a parking garage's floors
	 */
	private static final double	MIN_CLEARANCE	= 2.5;

	/**
	 * More than a full turn is fine when the curve climbs or drops enough each turn
	 * (a parking garage ramp winds over itself several times), or when its radius
	 * changes by more than the road's whole width each turn (a spiral that doesn't
	 * reach itself). Otherwise it passes through itself, almost always from a
	 * mistyped arc.
	 */
	private void checkFullTurns(Curve curve, String name)
	{
		double turns = curve.getArcDeg() / 360;

		// the road, sides, borders and barriers from one edge to the other
		double across = trackData.getMainTrack().getValidWidth();
		for (boolean right : new boolean[] { false, true })
		{
			GameSides.State side = GameSides.of(curve, right, trackContext);
			across += Math.max(side.sideStartWidth, side.sideEndWidth) + side.borderWidth + side.barrierWidth;
		}
		if (Math.abs(curve.getValidRadiusEnd() - curve.getRadiusStart()) / turns >= across)
			return;

		double rise = (curve.getCalculatedHeightEndLeft() + curve.getCalculatedHeightEndRight()
			- curve.getCalculatedHeightStartLeft() - curve.getCalculatedHeightStartRight()) / 2;
		double perTurn = Math.abs(rise) / turns;
		if (!(perTurn >= MIN_CLEARANCE))
		{
			report.append(name + "arc is " + format(curve.getArcDeg()) + " deg, more than a full turn (360 deg), and the curve's height changes only "
				+ (Double.isNaN(perTurn) ? "0" : format(perTurn)) + " m per turn (less than " + format(MIN_CLEARANCE)
				+ " m), so the road passes through itself\n");
			foldedSegments.add(curve.getName());
		}
	}

	/**
	 * Where the track passes over or through itself, in different segments: two
	 * parts that overlap in plan, from one side's barrier to the other's, with
	 * their roads less than MIN_CLEARANCE apart in height (a figure eight without
	 * a climb, or a track that runs back over itself), as the game builds them
	 * (see TrackCrossing.findOverlaps). A curve of more than a full turn over
	 * itself is checkFullTurns'. TORCS's trackgen builds a bridge where the
	 * centerlines cross at different heights; where it can't, the upper road has
	 * nothing under it.
	 */
	private void checkCrossings()
	{
		utils.circuit.GameTrack track;
		java.util.List<utils.circuit.TrackCrossing.Overlap> overlaps;
		try
		{
			track = utils.circuit.GameTrack.build(trackContext);
			overlaps = utils.circuit.TrackCrossing.findOverlaps(track, MIN_CLEARANCE);
		}
		catch (RuntimeException e)
		{
			// a track the game can't build either: the segments' own messages say why
			return;
		}
		if (track == null)
			return;
		// the segments next to each folded one: the two either side of a curve that
		// folds over overlap through it
		java.util.Set<String> besideFolded = new java.util.HashSet<String>();
		SegmentVector segments = trackData.getSegments();
		for (int i = 0; i < segments.size(); i++)
		{
			if (!foldedSegments.contains(segments.get(i).getName()))
				continue;
			besideFolded.add(pair(segments.get((i + segments.size() - 1) % segments.size()).getName(),
				segments.get((i + 1) % segments.size()).getName()));
		}
		java.util.Set<String> reported = new java.util.HashSet<String>();
		for (utils.circuit.TrackCrossing.Overlap overlap : overlaps)
		{
			String first = overlap.firstSegment, second = overlap.secondSegment;
			reported.add(pair(first, second));
			// already reported as folding over or passing through itself
			if (foldedSegments.contains(first) || foldedSegments.contains(second) || besideFolded.contains(pair(first, second)))
				continue;
			report.append("Segments " + first + " and " + second + " overlap around (" + format(overlap.x) + ", " + format(overlap.y)
				+ ") with their roads only " + format(overlap.heightDifference) + " m apart in height (less than " + format(MIN_CLEARANCE)
				+ " m), so the road passes through itself\n", segment(second));
		}
		if (trackContext.getGameType() != GameType.TORCS)
			return;
		utils.circuit.TrackCrossing crossing;
		try
		{
			crossing = new utils.circuit.TrackCrossing(track, trackContext);
		}
		catch (RuntimeException e)
		{
			return;
		}
		for (utils.circuit.TrackCrossing.Geometry geometry : crossing.unbuilt)
		{
			String upper = segmentName(track, geometry.upperSegmentId), lower = segmentName(track, geometry.lowerSegmentId);
			// one already reported: too close in height for any bridge
			if (!reported.add(pair(upper, lower)))
				continue;
			String where = "TORCS's trackgen builds no bridge where segment " + upper + " crosses over segment " + lower + " around ("
				+ format(geometry.crossingX) + ", " + format(geometry.crossingY) + "), so the upper road has nothing under it";
			if (geometry.tooLow)
				report.append(where + ". The upper road is only " + format(geometry.minimumUpperZ - geometry.lowerBarrierTopZ)
					+ " m above the lower one's barriers, and the bridge is " + format(crossing.thickness)
					+ " m thick (Terrain Generation): raise the upper road or make the bridge thinner.\n", segment(upper));
			else
				report.append(where + ": the roads cross too close to parallel, or too near another part of the track, for trackgen to fit one\n", segment(upper));
		}
	}

	// what real tracks stay within (measured on about 220 of the games' tracks:
	// the steepest road climbs 24%, the steepest dirt track 29%, the most banked
	// 30 degrees, against 37 on the most banked real ones; no road, circuit, oval
	// or speedway has a crest or dip tighter than 67 m, and none changes its banking
	// by more than 10 degrees in 10 m)
	private static final double	MAX_GRADE			= 0.30;
	private static final double	MAX_DIRT_GRADE		= 0.40;
	private static final double	MAX_BANKING_DEG		= 37;
	private static final double	MIN_VERTICAL_RADIUS	= 50;
	// degrees per meter: 15 degrees in 10 m
	private static final double	MAX_BANKING_CHANGE	= 1.5;
	// the length the grade is measured over: about a car's
	private static final double	PROFILE_STEP		= 5;
	private static final double	GRAVITY				= 9.81;

	/**
	 * Whether the track can be driven, not only whether its heights agree: a
	 * grade or banking steeper than real tracks have, banking that changes faster
	 * than on real tracks (it rolls cars over sharply), and on tracks other than
	 * dirt tracks (which jump on purpose) a crest that throws cars into the air
	 * or a dip that bottoms them out, as tight as real tracks never are. The
	 * road's middle and edges are sampled every meter as the game builds them
	 * (see GameTrack.heightL); each segment gets one message of each kind, at its
	 * worst. Not a segment whose heights don't agree, or next to one: those
	 * messages come first.
	 */
	private void checkProfile()
	{
		utils.circuit.GameTrack track;
		try
		{
			track = utils.circuit.GameTrack.build(trackContext);
		}
		catch (RuntimeException e)
		{
			return;
		}
		if (track == null)
			return;
		boolean dirt = "dirt".equals(trackData.getHeader().getCategory());
		double maxGrade = dirt ? MAX_DIRT_GRADE : MAX_GRADE;

		// along the lap: where, the road's height in its middle, and its segment
		java.util.List<Double> distances = new java.util.ArrayList<Double>(), heights = new java.util.ArrayList<Double>();
		// the banking, in degrees: positive when the left edge is higher
		java.util.List<Double> banks = new java.util.ArrayList<Double>();
		java.util.List<String> names = new java.util.ArrayList<String>();
		java.util.Map<String, Double> banking = new java.util.LinkedHashMap<String, Double>();
		double lap = 0;
		for (utils.circuit.GameTrack.Seg seg : track.segs)
		{
			if (!(seg.length > 0) || Double.isInfinite(seg.length))
				continue;
			int steps = (int) Math.ceil(seg.length);
			for (int i = 0; i < steps; i++)
			{
				double along = seg.length * i / steps;
				float toStart = (float) (seg.type == utils.circuit.GameTrack.TR_STR ? along : along / seg.radius);
				float width = utils.circuit.GameTrack.width(seg, toStart);
				double right = track.heightL(seg, toStart, 0), left = track.heightL(seg, toStart, width);
				double middle = track.heightL(seg, toStart, width / 2);
				if (!Double.isFinite(right) || !Double.isFinite(left) || !Double.isFinite(middle) || !(width > 0))
					continue;
				double bank = Math.toDegrees(Math.atan2(Math.abs(left - right), width));
				banking.merge(seg.name, bank, Math::max);
				distances.add(lap + along);
				heights.add(middle);
				banks.add(Math.toDegrees(Math.atan2(left - right, width)));
				names.add(seg.name);
			}
			lap += seg.length;
		}
		int count = distances.size();
		int window = (int) PROFILE_STEP;
		if (count < 2 * window + 1)
			return;

		java.util.Map<String, Double> grades = new java.util.LinkedHashMap<String, Double>();
		java.util.Map<String, Double> crests = new java.util.LinkedHashMap<String, Double>();
		java.util.Map<String, Double> dips = new java.util.LinkedHashMap<String, Double>();
		java.util.Map<String, Double> bankingChanges = new java.util.LinkedHashMap<String, Double>();
		// a crest or dip tighter than MIN_VERTICAL_RADIUS, while the road goes over or
		// through it: it goes to the segment where it is tightest, once
		int bend = 0;
		double tightest = Double.NaN;
		String tightestAt = null;
		for (int i = 0; i <= count; i++)
		{
			if (i == count)
			{
				endBend(bend, tightest, tightestAt, crests, dips);
				break;
			}
			// the lap goes on past the start line
			int before = (i - window + count) % count, after = (i + window) % count;
			double back = distance(distances, before, i, lap), ahead = distance(distances, i, after, lap);
			double gradeBefore = (heights.get(i) - heights.get(before)) / back;
			double gradeAfter = (heights.get(after) - heights.get(i)) / ahead;
			grades.merge(names.get(i), Math.abs(gradeAfter), Math::max);
			bankingChanges.merge(names.get(i), Math.abs(banks.get(after) - banks.get(before)) / (back + ahead), Math::max);
			double change = gradeAfter - gradeBefore;
			// the radius of the curve the road goes over (a crest) or through (a dip)
			double radius = Math.abs(change) < 1e-9 ? Double.POSITIVE_INFINITY : (back + ahead) / 2 / Math.abs(change);
			int kind = dirt || !(radius < MIN_VERTICAL_RADIUS) ? 0 : change < 0 ? -1 : 1;
			if (kind != bend)
			{
				endBend(bend, tightest, tightestAt, crests, dips);
				bend = kind;
				tightest = Double.NaN;
			}
			if (kind != 0 && !(radius >= tightest))
			{
				tightest = radius;
				tightestAt = names.get(i);
			}
		}

		SegmentVector segments = trackData.getSegments();
		for (int i = 0; i < segments.size(); i++)
		{
			Segment segment = segments.get(i);
			String name = segment.getName();
			// one whose heights don't agree with its own or its neighbors' has a step there
			if (heightProblems.contains(name) || heightProblems.contains(segments.get((i + 1) % segments.size()).getName())
				|| heightProblems.contains(segments.get((i + segments.size() - 1) % segments.size()).getName()))
				continue;
			Subject where = segment(segment);
			Double grade = grades.get(name);
			if (grade != null && grade > maxGrade)
				report.append("Segment " + name + ": the road is as steep as " + Math.round(grade * 100) + "% (more than "
					+ Math.round(maxGrade * 100) + "%" + (dirt ? " on a dirt track" : "") + "), steeper than real tracks get\n", where);
			Double bank = banking.get(name);
			if (bank != null && bank > MAX_BANKING_DEG)
				report.append("Segment " + name + ": the road is banked as much as " + Math.round(bank) + " deg (more than "
					+ Math.round(MAX_BANKING_DEG) + " deg), more than real tracks are\n", where);
			Double bankingChange = bankingChanges.get(name);
			if (bankingChange != null && bankingChange > MAX_BANKING_CHANGE)
				report.append("Segment " + name + ": the banking changes by as much as " + Math.round(bankingChange * 10) + " deg in 10 m (more than "
					+ Math.round(MAX_BANKING_CHANGE * 10) + " deg), so it rolls cars over more sharply than real tracks do\n", where);
			Double crest = crests.get(name);
			if (crest != null && crest < MIN_VERTICAL_RADIUS)
				report.append("Segment " + name + ": a crest with a radius of only " + Math.round(crest) + " m (less than "
					+ Math.round(MIN_VERTICAL_RADIUS) + " m) throws cars into the air above " + kmh(crest) + " km/h\n", where);
			Double dip = dips.get(name);
			if (dip != null && dip < MIN_VERTICAL_RADIUS)
				report.append("Segment " + name + ": a dip with a radius of only " + Math.round(dip) + " m (less than "
					+ Math.round(MIN_VERTICAL_RADIUS) + " m) doubles the load on cars' suspension above " + kmh(dip)
					+ " km/h, so they bottom out\n", where);
		}
	}

	/** Keeps a crest (-1) or dip (1) that has ended at the segment where it is tightest, the tightest of the segment's */
	private static void endBend(int kind, double radius, String at, java.util.Map<String, Double> crests, java.util.Map<String, Double> dips)
	{
		if (kind != 0 && at != null)
			(kind < 0 ? crests : dips).merge(at, radius, Math::min);
	}

	/** @return how far it is along the lap from one sample to another, ahead of it */
	private static double distance(java.util.List<Double> distances, int from, int to, double lap)
	{
		double d = distances.get(to) - distances.get(from);
		return d > 0 ? d : d + lap;
	}

	/** @return the speed at which a crest or dip of the radius pulls a car with the force of gravity (km/h) */
	private static long kmh(double radius)
	{
		return Math.round(Math.sqrt(GRAVITY * radius) * 3.6);
	}

	/** @return the name of the game's segment with the id */
	private static String segmentName(utils.circuit.GameTrack track, int id)
	{
		for (utils.circuit.GameTrack.Seg segment : track.segs)
		{
			if (segment.id == id)
				return segment.name;
		}
		return String.valueOf(id);
	}

	/** @return the two segments' names, the same either way round */
	private static String pair(String first, String second)
	{
		return first.compareTo(second) <= 0 ? first + "\n" + second : second + "\n" + first;
	}

	/**
	 * The games build each segment on from the end of the one before and don't
	 * join the last one to the first, so a track whose end isn't where it starts,
	 * in the same direction, overlaps itself or leaves a gap at the start line.
	 * A gap or turn under 1 cm or 0.01 degrees is reported when trackgen's deltas,
	 * from the Delta's dialog when the track hasn't changed since, are over the closure
	 * targets that its Auto closes a track to, or on a big track trackgen's rounding:
	 * the editor's shape can be a few millimeters out from trackgen's, which adds up
	 * the segments in floats.
	 * Without trackgen's deltas it says so, so they can be calculated.
	 */
	private void checkTrackCloses(SegmentVector segments)
	{
		if (segments == null || segments.size() < 2)
			return;
		Segment first = segments.get(0);
		Segment last = segments.get(segments.size() - 1);
		if (last.endTrackCenter == null || first.startTrackCenter == null)
			return;
		double dx = last.endTrackCenter.x - first.startTrackCenter.x;
		double dy = last.endTrackCenter.y - first.startTrackCenter.y;
		double gap = Math.hypot(dx, dy);
		double turn = Math.toDegrees(Math.IEEEremainder(last.endTrackAlpha - first.startTrackAlpha, 2 * Math.PI));
		// a segment with no shape, like a curve with radius 0, leaves no end to measure from
		if (!Double.isFinite(gap) || !Double.isFinite(turn))
		{
			report.append("Can't tell whether the track closes: where segment " + last.getName()
				+ " ends can't be calculated. Fix the segments reported first.\n", segment(last));
			return;
		}
		if (gap < 0.01 && Math.abs(turn) < 0.01)
		{
			Closure closure = Closure.last(trackContext);
			if (closure == null)
			{
				report.append("Closure hasn't been checked against trackgen's targets. Double-click to run Calc in the Delta dialog.\n",
					Subject.calculateClosure());
				return;
			}
			java.util.List<String> off = new java.util.ArrayList<String>();
			// within trackgen's rounding on a big track is as close as it gets
			if (Math.abs(closure.dX) > Closure.allowed(Closure.TARGET_DX, closure.size))
				off.add("Delta X " + Closure.plain(closure.dX) + " (target \u00b1" + Closure.plain(Closure.TARGET_DX) + " m)");
			if (Math.abs(closure.dY) > Closure.allowed(Closure.TARGET_DY, closure.size))
				off.add("Delta Y " + Closure.plain(closure.dY) + " (target \u00b1" + Closure.plain(Closure.TARGET_DY) + " m)");
			if (Math.abs(closure.dZ) > Closure.TARGET_DZ)
				off.add("Delta Z " + Closure.plain(closure.dZ) + " (target \u00b1" + Closure.plain(Closure.TARGET_DZ) + " m)");
			if (Math.abs(closure.angle) > Closure.TARGET_ANGLE)
				off.add("Delta Ang " + Closure.plain(closure.angle) + " (target \u00b1" + Closure.plain(Closure.TARGET_ANGLE) + " degrees)");
			if (off.isEmpty())
				return;
			report.append("The track doesn't close exactly (trackgen's " + String.join(", ", off)
				+ "). Auto in the Delta dialog will close it.\n", Subject.lastClosure());
			return;
		}

		// how far the end is ahead of the start, along the start's direction, and to its left
		double along = dx * Math.cos(first.startTrackAlpha) + dy * Math.sin(first.startTrackAlpha);
		double across = -dx * Math.sin(first.startTrackAlpha) + dy * Math.cos(first.startTrackAlpha);
		String message = "Track doesn't close: segment " + last.getName() + " ends " + String.format("%.2f", gap)
			+ " m from the start of segment " + first.getName();
		if (gap >= 0.01)
		{
			String ahead = Math.abs(along) >= 0.005 ? String.format("%.2f m %s", Math.abs(along), along >= 0 ? "ahead" : "behind") : null;
			String side = Math.abs(across) >= 0.005 ? String.format("%.2f m to the %s", Math.abs(across), across >= 0 ? "left" : "right") : null;
			message += " (" + (ahead == null ? side : side == null ? ahead : ahead + ", " + side) + ")";
		}
		if (Math.abs(turn) >= 0.01)
			message += String.format(", heading %.2f degrees to the %s of it", Math.abs(turn), turn >= 0 ? "left" : "right");
		report.append(message + "\n", segment(last));
	}

	/**
	 * The Main Track's border and barrier, which the segments start from. Every game and
	 * track version makes a border style other than plan and curb a wall, and a barrier
	 * style other than fence a wall (Speed Dreams' version 4 and 5 loaders also have
	 * fence1 and fence2), "no barrier" too: only a segment can have no barrier.
	 */
	private void checkMainTrackSide(SegmentSide main, String name)
	{
		String prefix = "Main Track " + name;
		boolean version3 = trackData.getHeader().getVersion() == 3;

		// border (plan when it isn't set)
		String borderStyle = main.getBorderStyle();
		if (hasText(borderStyle) && !borderStyle.equals("plan") && !borderStyle.equals("curb") && !borderStyle.equals("wall"))
		{
			report.append(prefix + " border style \"" + borderStyle + "\" isn't valid, so the game treats it as a wall\n");
		}
		if (trackContext.getGameType().planBorderResetsHeight() && (!hasText(borderStyle) || borderStyle.equals("plan")) &&
			!Double.isNaN(main.getBorderHeight()) && main.getBorderHeight() != 0)
		{
			report.append(prefix + " border style is plan, but a height of " + main.getBorderHeight() + " is set. The game ignores it and uses 0. Set the border style to curb or wall, or remove the height.\n");
		}

		// barrier (fence when it isn't set)
		String barrierStyle = main.getBarrierStyle();
		boolean fence1Fence2 = !version3 && trackContext.getGameType().hasFence1Fence2();
		boolean fence = !hasText(barrierStyle) || barrierStyle.equals("fence") ||
			(fence1Fence2 && (barrierStyle.equals("fence1") || barrierStyle.equals("fence2")));
		if (!fence && !barrierStyle.equals("wall"))
		{
			report.append(prefix + " barrier style \"" + barrierStyle + "\" isn't " + (fence1Fence2 ? "fence, fence1, fence2" : "fence") +
				" or wall" + (version3 ? " in a version 3 track" : "") + ", so the game treats it as a wall" +
				(barrierStyle.equals("no barrier") ? " (only a segment can have no barrier)" : "") + "\n");
		}
		if (fence && !Double.isNaN(main.getBarrierWidth()) && main.getBarrierWidth() != 0)
		{
			report.append(prefix + " barrier style is " + (hasText(barrierStyle) ? barrierStyle : "fence") + ", but a width of " + main.getBarrierWidth() + " is set. The game ignores it and uses 0. Set the barrier style to wall, or remove the width.\n");
		}
	}

	/** The side's values the game ignores or reads differently (GameSides.problems: the segment dialog shows them too) */
	private void checkSide(Segment segment, Segment previousSegment, SegmentSide side, String name, boolean right)
	{
		String prefix = "Segment " + segment.getName() + " " + name;
		for (GameSides.Problem problem : GameSides.problems(segment, previousSegment, right, trackContext))
		{
			report.append(prefix + " " + sentences(problem.text) + "\n");
			if (problem.folds)
				foldedSegments.add(segment.getName());
		}
	}

	/**
	 * Every segment, surface, object, camera, track light, sector, environment map,
	 * object map and graphic object is a section named after it. The games' parameter
	 * parser stops at a second section with the same name, so the whole track file
	 * fails to load, and it reads a / in a name as a section inside another one, so
	 * that item is lost. The editor's panels allow neither, but a track read from a
	 * file can have them, and saving writes them out again. Checked on the document
	 * saving makes, so only what is saved for this game counts.
	 */
	private void checkSectionNames()
	{
		org.jdom2.Document document;
		try
		{
			document = new plugin.torcs.XmlWriter(trackContext).getDocument("");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		Map<String, Integer> counts = new java.util.LinkedHashMap<String, Integer>();
		java.util.List<String[]> slashes = new java.util.ArrayList<String[]>();
		countSections(document.getRootElement(), "", counts, slashes);

		// a section inside a duplicated one is reported with it
		Set<String> duplicated = new HashSet<String>();
		for (Map.Entry<String, Integer> entry : counts.entrySet())
		{
			String path = entry.getKey();
			if (entry.getValue() < 2 || insideAny(path, duplicated))
				continue;
			duplicated.add(path);
			int slash = path.lastIndexOf('/');
			String parent = slash < 0 ? "" : path.substring(0, slash);
			String name = path.substring(slash + 1);
			// a name with a / is in one of the lists all the same: "Cameras/a/b" is camera a/b
			for (String list : LISTS)
			{
				if (parent.startsWith(list + "/"))
				{
					name = path.substring(list.length() + 1);
					parent = list;
					break;
				}
			}
			String[] kind = sectionKind(parent);
			report.append(kind[0] + " name \"" + name + "\" is used " + entry.getValue()
				+ " times. The game can't load a track file that has two sections with the same name (" + path
				+ "). Rename all but one.\n", sectionTarget(parent, name));
		}
		for (String[] slash : slashes)
		{
			String[] kind = sectionKind(slash[0]);
			report.append(kind[0] + " name \"" + slash[1] + "\" contains a slash (/). The game reads it as a section nested inside "
				+ "another, so the " + kind[1] + " is lost.\n", sectionTarget(slash[0], slash[1]));
		}
	}

	/**
	 * CSR's version 4 loader reads a segment's border "start width" and "end width"
	 * (when it has no "width"), and "start height" and "end height" (when it has no
	 * "height"). The editor uses them only with its experimental preference on;
	 * without it, it draws and checks the track with one width and one height.
	 */
	private void checkVariableBorders()
	{
		if (!trackContext.getCarsSportsRacing() || trackData.getHeader().getVersion() != 4 || trackContext.getVariableBorderWidth())
			return;
		Segment first = null;
		int count = 0;
		for (int i = 0; i < trackData.getSegments().size(); i++)
		{
			Segment segment = trackData.getSegments().get(i);
			for (SegmentSide side : new SegmentSide[] { segment.getLeft(), segment.getRight() })
			{
				if ((Double.isNaN(side.getBorderWidth()) && (!Double.isNaN(side.getBorderStartWidth()) || !Double.isNaN(side.getBorderEndWidth())))
					|| (Double.isNaN(side.getBorderHeight()) && (!Double.isNaN(side.getBorderStartHeight()) || !Double.isNaN(side.getBorderEndHeight()))))
				{
					if (first == null)
						first = segment;
					count++;
					break;
				}
			}
		}
		if (first == null)
			return;
		report.append((count == 1 ? "Segment " + first.getName() + "'s border has" : count + " segments' borders have, from " + first.getName() + ",")
			+ " a start or end width or height, which CSR uses, but the editor draws and checks the track with one width and one height for each border. "
			+ "Turn on \"CSR: variable-width borders (experimental)\" in Preferences to edit and see them.\n", segment(first));
	}

	/**
	 * CSR: a tapered curb keeps the slope of its wide end, and the curb after it
	 * should have the same slope (GameSides.curbSlopeMismatch: the segment dialog
	 * shows it too).
	 */
	private void checkTaperedCurbs()
	{
		SegmentVector segments = trackData.getSegments();
		int count = segments.size();
		for (int i = 0; i < count; i++)
		{
			Segment segment = segments.get(i);
			// the track is a loop: the last segment is followed by the first
			Segment next = segments.get((i + 1) % count);
			for (int j = 0; j < 2; j++)
			{
				boolean right = j == 1;
				String name = right ? "Right" : "Left";
				String text = GameSides.curbSlopeMismatch(segment, next, right, trackContext);
				if (text != null)
					report.append("Segment " + segment.getName() + " " + name + " " + sentences(text) + "\n", segment(segment, name));
			}
		}
	}

	/**
	 * The values too large for the game, as saving would write them: it keeps numbers
	 * as floats, so one beyond about 3.4e38 (Float.MAX_VALUE) is infinite to it, like
	 * 1e999 is to both. A value that another check has reported already, for the same
	 * item, isn't reported again.
	 */
	private void checkNumbers()
	{
		org.jdom2.Document document;
		try
		{
			document = new plugin.torcs.XmlWriter(trackContext).getDocument("");
		}
		catch (Exception e)
		{
			return;
		}
		java.util.List<String[]> found = new java.util.ArrayList<String[]>();
		findHugeNumbers(document.getRootElement(), "", found);

		int reported = messages.size();
		for (String[] value : found)
		{
			String	section	= value[0];
			String	key		= value[1];
			double	number	= Double.parseDouble(value[2]);
			Subject	target	= keyTarget(section + "/" + key, null);
			String	shown	= CheckMessage.readableNumbers(number + (Double.isInfinite(number) ? " m" : ""));

			boolean known = false;
			for (int i = 0; i < reported && !known; i++)
			{
				CheckMessage message = messages.get(i);
				known = sameItem(message.getSubject(), target) && message.getText().contains(shown);
			}
			if (known)
				continue;

			report.append(placeOf(section) + ": " + key + " is " + shown
				+ (Double.isInfinite(number) ? ", too large to be a number, so the game can't use it"
					: ", more than the largest number the game keeps (about 3.4e38), so it reads it as infinite")
				+ ". Set a real value.\n", target == null ? NONE : target);
		}
	}

	/** @return whether both are about one item: a segment on any of its dialog's tabs */
	private static boolean sameItem(Subject one, Subject other)
	{
		if (one != null && other != null && one.getKind() == Subject.Kind.SEGMENT && other.getKind() == Subject.Kind.SEGMENT)
			return one.getSegment() == other.getSegment();
		return java.util.Objects.equals(one, other);
	}

	/** Adds the attnums in element, with their section's path, whose values the game can't keep */
	private static void findHugeNumbers(org.jdom2.Element element, String path, java.util.List<String[]> found)
	{
		for (org.jdom2.Element child : element.getChildren())
		{
			String name = child.getAttributeValue("name", "");
			if (child.getName().equals("section"))
			{
				findHugeNumbers(child, path.isEmpty() ? name : path + "/" + name, found);
			}
			else if (child.getName().equals("attnum"))
			{
				double value = plugin.torcs.GfParmNumbers.parse(child.getAttributeValue("val"), new boolean[1]);
				if (Double.isInfinite(value) || Math.abs(value) > Float.MAX_VALUE)
					found.add(new String[] { path, name, Double.toString(value) });
			}
		}
	}

	/** @return how the messages name a section: "Segment front Left Side", "Surface road", "Main Track Pits" */
	private static String placeOf(String section)
	{
		for (String list : LISTS)
		{
			if (!section.startsWith(list + "/"))
				continue;
			String rest = section.substring(list.length() + 1);
			return sectionKind(list)[0] + " " + rest.replace("/", " ");
		}
		return section.replace("/", " ");
	}

	/** Counts the full names of the sections in element, and notes the names with a / */
	private static void countSections(org.jdom2.Element element, String path, Map<String, Integer> counts,
		java.util.List<String[]> slashes)
	{
		for (org.jdom2.Element child : element.getChildren("section"))
		{
			String name = child.getAttributeValue("name", "");
			String childPath = path.isEmpty() ? name : path + "/" + name;
			if (name.contains("/"))
				slashes.add(new String[] { path, name });
			counts.merge(childPath, 1, Integer::sum);
			countSections(child, childPath, counts, slashes);
		}
	}

	/** @return whether path is inside one of the sections */
	private static boolean insideAny(String path, Set<String> sections)
	{
		for (String section : sections)
		{
			if (path.startsWith(section + "/"))
				return true;
		}
		return false;
	}

	// the sections that are lists of the editor's named items (sectionTarget, sectionKind)
	private static final String[] LISTS = { "Main Track/Track Segments", "Main Track/segments", "Surfaces/List", "Surfaces", "Objects",
		"Cameras/list", "Cameras", "Track Lights", "Sectors", "Graphic/Environment Mapping", "Graphic/Terrain Generation/Object Maps",
		"Graphic/Terrain Generation/Objects" };

	/**
	 * @return where a section in the parent section is edited: the first segment with
	 *         the name, or the Properties tab of the item; null when it isn't one of
	 *         the editor's lists
	 */
	private Subject sectionTarget(String parent, String name)
	{
		switch (parent)
		{
		case "Main Track/Track Segments":
		case "Main Track/segments":
			return segment(name);
		case "Surfaces":
		case "Surfaces/List":
			return properties("Surfaces", name);
		case "Objects":
			return properties("Objects", name);
		case "Cameras":
		case "Cameras/list":
			return properties("Cameras", name);
		case "Track Lights":
			return properties("Lights", name);
		case "Sectors":
			return properties("Sectors", name);
		case "Graphic/Environment Mapping":
			return properties("Env Map", name);
		case "Graphic/Terrain Generation/Object Maps":
			return properties("Terrain", "Object Maps", name);
		case "Graphic/Terrain Generation/Objects":
			return properties("Terrain", "Objects");
		default:
			return null;
		}
	}

	/** @return what the sections in the parent section are: capitalized, and not */
	private static String[] sectionKind(String parent)
	{
		switch (parent)
		{
		case "Main Track/Track Segments":
		case "Main Track/segments":
			return new String[] { "Segment", "segment" };
		case "Surfaces":
		case "Surfaces/List":
			return new String[] { "Surface", "surface" };
		case "Objects":
			return new String[] { "Object", "object" };
		case "Cameras":
		case "Cameras/list":
			return new String[] { "Camera", "camera" };
		case "Track Lights":
			return new String[] { "Track light", "track light" };
		case "Sectors":
			return new String[] { "Sector", "sector" };
		case "Graphic/Environment Mapping":
			return new String[] { "Environment map", "environment map" };
		case "Graphic/Terrain Generation/Object Maps":
			return new String[] { "Object map", "object map" };
		case "Graphic/Terrain Generation/Objects":
			return new String[] { "Graphic object", "graphic object" };
		default:
			return new String[] { parent.isEmpty() ? "Section" : "Section in " + parent, "section" };
		}
	}

	/**
	 * What's in the track file that saving won't keep: keys and sections the editor
	 * doesn't read or skips, and keys the game in use doesn't; and the values saving
	 * changes (found when the track was read).
	 */
	private void checkUnusedKeys()
	{
		for (String message : trackData.getUnusedKeys())
			report.append(message + "\n", keyTarget(trackData.getUnusedKeyPath(message), trackData.getUnusedKeyItem(message)));
	}

	// how the paths of the keys outside the editor's lists start, and the Properties
	// tabs with them, the deepest first
	private static final String[][] SECTION_TABS = { { "Main Track/Pits/", "Pit" }, { "Main Track/Left ", "Track", "Left" },
		{ "Main Track/Right ", "Track", "Right" }, { "Main Track/", "Track" }, { "Graphic/Terrain Generation/", "Terrain" },
		{ "Graphic/Turn Marks/", "Turn Marks" }, { "Graphic/3d ", "Graphic 3D" }, { "Graphic/", "Graphic" }, { "Header/", "General" }, { "Local Info/", "Local Info" },
		{ "Starting Grid/", "Grid" }, { "Georeference/", "Georeference" } };

	/**
	 * @return where a key in the file is edited: a key in a segment the segment, on the
	 *         dialog's tab with it; a key in an item of the editor's lists (a light,
	 *         camera, surface...) the item's Properties tab; else the Properties tab
	 *         with the section; null when it's none of those
	 * @param item the segment or item the key was in when the track was read (see
	 *        TrackData.getUnusedKeyItem), found by the name it has now; null to find
	 *        it by the name in the path
	 */
	private Subject keyTarget(String path, Object item)
	{
		if (path == null)
			return null;
		String[] place = TrackData.segmentPlace(path);
		if (place != null)
		{
			if (!(item instanceof Segment))
				return segment(trackData.getSegments().getSegmentFromName(place[0]), place[1]);
			// not one deleted since
			return trackData.getSegments().contains(item) ? segment((Segment) item, place[1]) : null;
		}
		for (String list : LISTS)
		{
			if (!path.startsWith(list + "/"))
				continue;
			String rest = path.substring(list.length() + 1);
			int slash = rest.indexOf('/');
			// a key of the list itself isn't in an item
			if (slash <= 0)
				return null;
			if (item == null)
				return sectionTarget(list, rest.substring(0, slash));
			// renamed: by its name now; deleted: nowhere
			for (Object listItem : TrackLoader.listItems(trackData, list))
			{
				if (listItem == item)
					return sectionTarget(list, TrackLoader.itemName(item));
			}
			return null;
		}
		for (String[] section : SECTION_TABS)
		{
			if (path.startsWith(section[0]))
				return properties(java.util.Arrays.copyOfRange(section, 1, section.length));
		}
		return null;
	}

	/**
	 * The Georeference the track's file has (only TORCS reads one): the
	 * Properties dialog checks the values typed there, but not those read.
	 */
	private void checkGeoreference()
	{
		if (trackContext.getGameType() != GameType.TORCS)
			return;
		String problem = trackData.getGeoreference().problem();
		if (problem != null)
			report.append("Georeference: " + problem + ": TORCS ignores the latitude and longitude\n");
	}

	public void checkHeader()
	{
		// where the track is: the games list <tracks folder>/<category>/<name>
		String placement = trackContext.getTrackPlacementProblem();
		if (placement != null)
			report.append(placement + "\n");

		// TORCS reads no subcategory
		if (trackContext.getGameType().hasSpeedDreamsExtensions() && trackData.getHeader().getSubcategory() != null)
		{
			if (!"speedway".equals(trackData.getHeader().getCategory()))
			{
				report.append("Subcategory " + trackData.getHeader().getSubcategory() + " should only be used with the speedway category\n");
			}
		}
	}

	private void checkCameras()
	{
		Vector<utils.circuit.Camera> cameras = trackData.getCameras();
		for (utils.circuit.Camera camera : cameras)
			for (String problem : CameraProblems.segmentProblems(trackData.getSegments(), camera.getName(),
				camera.getSegment(), camera.getFovStart(), camera.getFovEnd()))
				report.append(sentences(problem) + "\n", properties("Cameras", camera.getName()));

		SegmentVector segments = trackData.getSegments();
		int count = segments.size();
		if (count == 0)
			return;
		Subject cameraTab = report.about(null);
		for (utils.circuit.Camera camera : cameras)
		{
			report.about(properties("Cameras", camera.getName()));
			checkCameraPlace(camera, segments);
			double height = camera.getHeight();
			if (Double.isNaN(height) || height == 0)
				report.append("Camera " + camera.getName() + ": height is missing, so the game places it on the ground\n");
			else if (height < 0)
				report.append("Camera " + camera.getName() + ": a height of " + format(height) + " m places it below the ground\n");
		}
		report.about(cameraTab);

		// which camera the game uses on each segment: the last one whose stretch has it,
		// a name no segment has being the first segment (a missing one stops the game)
		String[] owners = new String[count];
		for (utils.circuit.Camera camera : cameras)
		{
			if (!hasText(camera.getSegment()) || !hasText(camera.getFovStart()) || !hasText(camera.getFovEnd()))
				continue;
			int start = Math.max(0, segments.indexOf(segments.getSegmentFromName(camera.getFovStart().trim())));
			int end = Math.max(0, segments.indexOf(segments.getSegmentFromName(camera.getFovEnd().trim())));
			int i = start;
			do
			{
				owners[i] = camera.getName();
				i = (i + 1) % count;
			}
			while (i != end);
		}
		Set<String> filming = new HashSet<String>(java.util.Arrays.asList(owners));
		for (utils.circuit.Camera camera : cameras)
			if (hasText(camera.getSegment()) && hasText(camera.getFovStart()) && hasText(camera.getFovEnd())
				&& !filming.contains(camera.getName()))
				report.append("Camera " + camera.getName() + " is never used, because cameras later in the list cover its entire range ("
					+ camera.getFovStart() + " to " + camera.getFovEnd() + ")\n", properties("Cameras", camera.getName()));

		// the stretches no camera films, from the first segment of each
		if (cameras.isEmpty())
		{
			report.append("No cameras. The game's TV views will show the cars from 120 m above the track.\n");
			return;
		}
		if (!filming.contains(null))
			return;
		if (filming.size() == 1)
		{
			report.append("No camera covers any segment. The game's TV views will show the cars from 120 m above the track.\n");
			return;
		}
		for (int i = 0; i < count; i++)
		{
			if (owners[i] != null || owners[(i - 1 + count) % count] == null)
				continue;
			int last = i;
			while (owners[(last + 1) % count] == null)
				last = (last + 1) % count;
			String from = segments.get(i).getName(), to = segments.get(last).getName();
			report.append("No camera covers " + (i == last ? "segment " + from : "segments " + from + " to " + to)
				+ ". The game's TV views will show the cars from 120 m above the track there.\n", segment(segments.get(i)));
		}
	}

	/**
	 * Checks where To Start puts a camera, as the game reads it: the camera's
	 * tab shows the same (CameraProperties.toStartProblem).
	 */
	private void checkCameraPlace(utils.circuit.Camera camera, SegmentVector segments)
	{
		Segment segment = hasText(camera.getSegment()) ? segments.getSegmentFromName(camera.getSegment().trim()) : null;
		double value = camera.getToStart();
		if (segment == null || Double.isNaN(value))
			return;
		boolean curve = !segment.getType().equals("str");
		// a camera made here is saved in degrees on a curve and without a unit on a straight
		String units = camera.isToStartUnitsKnown() ? camera.getToStartUnits() : curve ? "deg" : null;
		String problem = CameraProblems.toStartProblem(segment, value, units, trackContext.getGameType());
		if (problem != null)
			report.append("Camera " + camera.getName() + ": " + sentences(problem) + "\n");
	}

	/**
	 * A material name with a space, in an AC3D file: TORCS's loader reads the
	 * name with sscanf("%s rgb ..."), which stops at the space, so the material
	 * isn't set up but still counts, and a surface using it reads a material
	 * that was never made (grloadac.cpp, do_material and do_mat). CSR's and
	 * Speed Dreams' loaders and accc read the name up to its closing quote.
	 */
	private static final String	SPACE_PROBLEM	= "with a space in its name. TORCS can't read the name, and it may crash when it loads the track.";

	/**
	 * A default file the track includes (&amp;default-surfaces; or
	 * &amp;default-objects;) that isn't where the game reads it: the game, and
	 * trackgen, can't read the track at all (see TrackFolders.missingSharedFiles)
	 */
	private void checkSharedFiles()
	{
		String trackPath = Properties.getInstance().getPath();
		java.util.List<String> missing = TrackFolders.missingSharedFiles(trackContext.getLocalDirectory(), dataDirectory, trackPath, trackData);
		if (missing.isEmpty())
			return;
		String where = TrackFolders.isLocalTrack(trackContext.getLocalDirectory(), trackPath) ? "the data folder's data" + sep + "tracks"
			: "the data" + sep + "tracks folder three folders up from the track";
		report.append("The track includes the game's default " + (missing.size() == 2 ? "surfaces and objects" : missing.get(0).replace(".xml", ""))
			+ ", but there's no " + String.join(" or ", missing) + " in " + where
			+ ", so the game can't read the track. Restore " + (missing.size() == 2 ? "them" : "it") + " from the game's files.\n");
	}

	/** @return a message of more than one sentence with the last one's period, any other as it is */
	private static String sentences(String text)
	{
		return text.contains(". ") ? text + "." : text;
	}

	/** @return a number with at most 2 decimals, and none when it is whole */
	private static String format(double value)
	{
		String text = String.format(java.util.Locale.US, "%.2f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
		return "-0".equals(text) ? "0" : text;
	}

	private void checkPits()
	{
		Pits pits = trackData.getMainTrack().getPits();

		if (pits == null)
			return;

		boolean noSegments = !hasText(pits.getEntry()) && !hasText(pits.getStart()) && !hasText(pits.getEnd())
				&& !hasText(pits.getExit());

		// version 3 tracks only have pits with the pit type "track side"
		if (!noSegments && trackData.getHeader().getVersion() == 3 && pits.getStyle() != 1)
		{
			report.append("Pit segments are set, but a version 3 track only has pits when the pit type is \"track side\", so the game ignores them\n");
			return;
		}

		if (!noSegments)
		{
			SegmentVector segments = trackData.getSegments();

			if (!hasText(pits.getEntry()))
				report.append("Pit entry is missing. Without it, the game has no pits.\n");
			else if (segments.getSegmentFromName(pits.getEntry()) == null)
				report.append("Invalid pit entry: " + pits.getEntry() + " isn't a segment, so the game has no pits\n");

			if (!hasText(pits.getStart()))
				report.append("Pit start is missing. Without it, the game has no pits.\n");
			else if (segments.getSegmentFromName(pits.getStart()) == null)
				report.append("Invalid pit start: " + pits.getStart() + " isn't a segment, so the game has no pits\n");

			if (!hasText(pits.getEnd()))
				report.append("Pit end is missing. Without it, the game has no pits.\n");
			else if (segments.getSegmentFromName(pits.getEnd()) == null)
				report.append("Invalid pit end: " + pits.getEnd() + " isn't a segment, so the game has no pits\n");

			if (!hasText(pits.getExit()))
				report.append("Pit exit is missing. Without it, the game has no pits.\n");
			else if (segments.getSegmentFromName(pits.getExit()) == null)
				report.append("Invalid pit exit: " + pits.getExit() + " isn't a segment, so the game has no pits\n");

			String order = Pits.orderProblem(segments, pits.getEntry(), pits.getStart(), pits.getStartBuildings(),
				pits.getStopBuildings(), pits.getEnd(), pits.getExit(), trackContext.getGameType().hasPitOptions());
			if (order != null)
				report.append(order + "\n");

			// Speed Dreams and CSR: a buildings segment that isn't found is the pit start or end
			if (trackContext.getGameType().hasPitOptions())
			{
				if (hasText(pits.getStartBuildings()) && segments.getSegmentFromName(pits.getStartBuildings()) == null)
					report.append("Invalid pit start buildings: " + pits.getStartBuildings() + " isn't a segment, so the game uses the pit start\n");
				if (hasText(pits.getStopBuildings()) && segments.getSegmentFromName(pits.getStopBuildings()) == null)
					report.append("Invalid pit stop buildings: " + pits.getStopBuildings() + " isn't a segment, so the game uses the pit end\n");
			}

			// the games read any side but "right" as left
			if (!hasText(pits.getSide()))
				report.append("Pit side is missing. The game uses the right side.\n");
			else if (!(pits.getSide().equals("left") || pits.getSide().equals("right")))
				report.append("Pit side \"" + pits.getSide() + "\" isn't valid (the game uses the left side)\n");
		}

		// the games only show a pit indicator with the pit type "no building"
		int indicator = pits.getIndicator();
		if (trackContext.getGameType().hasPitOptions() && trackData.getHeader().getVersion() != 3
			&& indicator != Integer.MAX_VALUE)
		{
			int style = pits.getStyle() == Integer.MAX_VALUE ? 1 : pits.getStyle();
			if (indicator < 1)
				report.append("Pit indicator " + indicator + " isn't valid. The game shows an indicator only for 1 (pit_indicator.ac) or higher (normal_pit_indicator.ac).\n");
			else if (style != 3)
				report.append("Pit indicator is set, but the game only shows it when the pit type is \"no building\"\n");
		}

		if (!noSegments)
		{
			checkPitCount();
			checkPitsOnCurves();
			checkPitsParallel();
			checkNoBuildingPitLane(pits);
			checkPitsEnd();
			checkPitsAtFinishLine();
		}
	}

	/**
	 * Trackgen starts the side textures anew at the finish line, so when the pits
	 * go across it the pit boxes' markings after the line only line up with the
	 * pits when the line is a whole number of pits from where they start.
	 */
	private void checkPitsAtFinishLine()
	{
		String problem;
		try
		{
			utils.circuit.GameTrack track = utils.circuit.GameTrack.build(trackContext);
			utils.circuit.GameTrack.PitLayout layout = track == null ? null : track.pitPlaces();
			problem = layout == null ? null : layout.lineProblem();
		}
		catch (RuntimeException e)
		{
			return;
		}
		if (problem != null)
			report.append(problem + ": Fit Segments in the Pit properties moves the start of the pits (Apply and Ok also offer this)\n");
	}

	/**
	 * More pits than the editor shows (GameTrack.MAX_PITS): the pit length is far
	 * too short for the pit lane, or the pit lane far too long.
	 */
	private void checkPitCount()
	{
		utils.circuit.GameTrack.PitLayout layout;
		try
		{
			utils.circuit.GameTrack track = utils.circuit.GameTrack.build(trackContext);
			layout = track == null ? null : track.pitPlaces();
		}
		catch (RuntimeException e)
		{
			return;
		}
		if (layout == null || layout.fits <= utils.circuit.GameTrack.MAX_PITS)
			return;
		report.append(String.format(java.util.Locale.US, "The game places %,d pits, each %s m long. That's more than the editor shows (%,d). "
			+ "The pit length is too short for the pit lane, or the pit lane is too long.\n",
			layout.fits, Float.toString(layout.len), utils.circuit.GameTrack.MAX_PITS));
	}

	/** The last pit should end at the end of the stop buildings segment (the pit end in TORCS). */
	private void checkPitsEnd()
	{
		String problem;
		try
		{
			utils.circuit.GameTrack track = utils.circuit.GameTrack.build(trackContext);
			utils.circuit.GameTrack.PitLayout layout = track == null ? null : track.pitPlaces();
			problem = layout == null ? null : layout.endProblem();
		}
		catch (RuntimeException e)
		{
			return;
		}
		if (problem != null)
			report.append(problem + ": Fit Segments in the Pit properties fits the segments to the pits (Apply and Ok also offer this)\n");
	}

	/**
	 * The games measure a pit's place along its segment in meters, but read it as
	 * an angle in a curve, so what they draw for a pit in a curve (its building,
	 * door, low wall or indicator) is in the wrong place. Pit stops still work.
	 */
	private void checkPitsOnCurves()
	{
		utils.circuit.GameTrack.PitLayout layout;
		try
		{
			utils.circuit.GameTrack track = utils.circuit.GameTrack.build(trackContext);
			layout = track == null ? null : track.pitPlaces();
		}
		catch (RuntimeException e)
		{
			return;
		}
		if (layout == null)
			return;
		// the pits in each curve, in order
		Map<String, java.util.List<Integer>> curves = new java.util.LinkedHashMap<String, java.util.List<Integer>>();
		for (int i = 0; i < layout.pits.size(); i++)
		{
			utils.circuit.GameTrack.Seg seg = layout.pits.get(i).seg;
			if (seg.type != utils.circuit.GameTrack.TR_STR)
				curves.computeIfAbsent(seg.name, k -> new java.util.ArrayList<Integer>()).add(i + 1);
		}
		if (curves.isEmpty())
			return;
		StringBuilder list = new StringBuilder();
		for (Map.Entry<String, java.util.List<Integer>> curve : curves.entrySet())
		{
			if (list.length() > 0)
				list.append("; ");
			java.util.List<Integer> numbers = curve.getValue();
			int first = numbers.get(0), last = numbers.get(numbers.size() - 1);
			list.append(curve.getKey()).append(first == last ? ": pit " + first : ": pits " + first + "-" + last);
		}
		report.append("Pits on a curve (" + list + "): the game reads a pit's position along a curve as an angle, so it draws "
			+ "the buildings, doors, low walls, or indicators in the wrong place (pit stops still work)\n", segment(curves.keySet().iterator().next()));
	}

	/** how far the pit side's width may differ from the first pit's along the pits, in meters */
	private static final double	PIT_PARALLEL_TOLERANCE	= 0.01;

	/**
	 * The games assume the pits are parallel to the track: each pit is placed at
	 * the outer edge of the pit side (its border and side) where the pit starts,
	 * and laid along the track from there. So from the start of the first pit to
	 * the end of the last, the pit side should be the same width throughout: one
	 * that tapers, or changes width from one segment to the next, leaves pits off
	 * the pit lane's edge.
	 */
	private void checkPitsParallel()
	{
		utils.circuit.GameTrack.PitLayout layout;
		try
		{
			utils.circuit.GameTrack track = utils.circuit.GameTrack.build(trackContext);
			layout = track == null ? null : track.pitPlaces();
		}
		catch (RuntimeException e)
		{
			return;
		}
		if (layout == null || layout.pits.isEmpty())
			return;

		// the pit side's width at the start of the first pit
		utils.circuit.GameTrack.LocalPos firstPit = layout.pits.get(0);
		double reference = pitSideWidth(firstPit.seg, layout.side, firstPit.toStart);
		if (Double.isNaN(reference))
			return;

		// each pit's narrowest and widest pit side: the widths change linearly
		// along a step, so its ends and the steps' ends in between are enough
		double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
		Map<String, java.util.List<Integer>> segmentsWithPits = new java.util.LinkedHashMap<String, java.util.List<Integer>>();
		for (int i = 0; i < layout.pits.size(); i++)
		{
			utils.circuit.GameTrack.LocalPos pit = layout.pits.get(i);
			utils.circuit.GameTrack.Seg seg = pit.seg;
			double toStart = pit.toStart;
			double left = layout.len;
			double low = Double.POSITIVE_INFINITY, high = Double.NEGATIVE_INFINITY;
			boolean offEdge = false;
			for (int guard = 0; guard < 100000; guard++)
			{
				double end = Math.min(seg.length, toStart + left);
				for (double at : new double[] { toStart, end })
				{
					double width = pitSideWidth(seg, layout.side, at);
					if (Double.isNaN(width))
						continue;
					low = Math.min(low, width);
					high = Math.max(high, width);
				}
				left -= end - toStart;
				if (left <= 0.001)
					break;
				seg = seg.next;
				toStart = 0;
			}
			if (low > high)
				continue;
			if (Math.abs(low - reference) > PIT_PARALLEL_TOLERANCE || Math.abs(high - reference) > PIT_PARALLEL_TOLERANCE)
				offEdge = true;
			if (!offEdge)
				continue;
			min = Math.min(min, low);
			max = Math.max(max, high);
			segmentsWithPits.computeIfAbsent(pit.seg.name, k -> new java.util.ArrayList<Integer>()).add(i + 1);
		}
		if (segmentsWithPits.isEmpty())
			return;

		StringBuilder list = new StringBuilder();
		for (Map.Entry<String, java.util.List<Integer>> segment : segmentsWithPits.entrySet())
		{
			if (list.length() > 0)
				list.append("; ");
			java.util.List<Integer> numbers = segment.getValue();
			int first = numbers.get(0), last = numbers.get(numbers.size() - 1);
			list.append(segment.getKey()).append(first == last ? ": pit " + first : ": pits " + first + "-" + last);
		}
		String range = Math.abs(max - min) <= PIT_PARALLEL_TOLERANCE ? format(min) : format(min) + "-" + format(max);
		report.append("Pits not parallel to the track (" + list + "): the pit side (border and side) is " + format(reference)
			+ " m wide at the first pit but " + range + " m at these pits. The games assume the pits are parallel to the track, "
			+ "so these pits don't line up with the pit lane's edge.\n",
			segment(trackData.getSegments().getSegmentFromName(segmentsWithPits.keySet().iterator().next()),
				layout.side == utils.circuit.GameTrack.TR_RGT ? "Right" : "Left"));
	}

	/**
	 * @return how far the outer edge of the pit side (its border and side) is from
	 *         the edge of the road, toStart meters into the main segment (a length
	 *         also in a curve); NaN when the segment has no border or side there
	 */
	private static double pitSideWidth(utils.circuit.GameTrack.Seg main, int side, double toStart)
	{
		boolean right = side == utils.circuit.GameTrack.TR_RGT;
		double along = main.length > 0 ? Math.max(0, Math.min(1, toStart / main.length)) : 0;
		double width = 0;
		boolean any = false;
		utils.circuit.GameTrack.Seg e = right ? main.rside : main.lside;
		for (int guard = 0; e != null && guard < 2; guard++)
		{
			boolean ours = right ? e.type2 == utils.circuit.GameTrack.TR_RBORDER || e.type2 == utils.circuit.GameTrack.TR_RSIDE
				: e.type2 == utils.circuit.GameTrack.TR_LBORDER || e.type2 == utils.circuit.GameTrack.TR_LSIDE;
			if (!ours)
				break;
			width += e.startWidth + (e.endWidth - e.startWidth) * along;
			any = true;
			e = right ? e.rside : e.lside;
		}
		return any ? width : Double.NaN;
	}

	/**
	 * Speed Dreams and CSR don't count driving inside a curve's edge as cutting
	 * in the pit lane, from the pit entry to the pit exit, but only with track
	 * side pits: with "no building", a pit lane on the inside of a curve gets
	 * cutting penalties (from the Amateur skill level).
	 */
	private void checkNoBuildingPitLane(Pits pits)
	{
		if (!trackContext.getGameType().hasPitOptions() || trackData.getHeader().getVersion() == 3 || pits.getStyle() != 3)
			return;
		SegmentVector segments = trackData.getSegments();
		Segment entry = segments.getSegmentFromName(pits.getEntry());
		Segment exit = segments.getSegmentFromName(pits.getExit());
		if (entry == null || exit == null)
			return;
		// the side the pits are on: any side but "right" is left
		String inside = pits.getSide() == null || "right".equals(pits.getSide()) ? "rgt" : "lft";
		int from = segments.indexOf(entry), to = segments.indexOf(exit);
		StringBuilder list = new StringBuilder();
		Segment first = null;
		for (int i = from; ; i = (i + 1) % segments.size())
		{
			Segment segment = segments.get(i);
			if (inside.equals(segment.getType()))
			{
				if (first == null)
					first = segment;
				if (list.length() > 0)
					list.append(", ");
				list.append(segment.getName());
			}
			if (i == to)
				break;
		}
		if (list.length() > 0)
			report.append("Pit lane on the inside of curves (" + list + "): with the pit type \"no building\" the game doesn't "
				+ "exempt the pit lane from the cutting rule, so cars using the pits there can be penalized for cutting\n", segment(first));
	}

	private void checkTrackHeight()
	{
		SegmentVector segments = trackData.getSegments();

		double width = trackData.getMainTrack().getValidWidth();

		double firstHeightStart = Double.NaN;
		double previousHeightEnd = Double.NaN;

		double firstHeightStartLeft = Double.NaN;
		double firstHeightStartRight = Double.NaN;
		double previousHeightEndLeft = Double.NaN;
		double previousHeightEndRight = Double.NaN;

		for (int i = 0; i < segments.size(); i++)
		{
			Segment segment = segments.get(i);
			report.about(segment(segment));

			String	segmentInfo = "Segment " + segment.getName() + " : ";

			double heightStart = segment.getHeightStart();
			boolean hasHeightStart = !Double.isNaN(heightStart);

			double heightStartLeft = segment.getHeightStartLeft();
			boolean hasHeightStartLeft = !Double.isNaN(heightStartLeft);

			double heightStartRight = segment.getHeightStartRight();
			boolean hasHeightStartRight = !Double.isNaN(heightStartRight);

			boolean hasHeightStartLeftRight = hasHeightStartLeft && hasHeightStartRight;
			boolean hasBankingStartFromHeights = hasHeightStartLeftRight && heightStartLeft != heightStartRight;

			double bankingStart = segment.getBankingStart();
			boolean hasBankingStart = !Double.isNaN(bankingStart);

			if (hasHeightStart && (hasHeightStartLeft || hasHeightStartRight))
			{
				report.append(segmentInfo + "sets Height Start together with Height Start Left or Right. Use one or the other.\n");
			}

			// Track should start at an elevation of 0.0.
			if (i == 0)
			{
				firstHeightStart = heightStart;
				firstHeightStartLeft = heightStartLeft;
				firstHeightStartRight = heightStartRight;

				if (hasHeightStartLeft && hasHeightStartRight)
				{
					double centerHeight = (heightStartLeft + heightStartRight) / 2.0;

					if (centerHeight != 0.0)
					{
						// disabled because many tracks don't start at 0 because of banking
						// or because they use the actual elevation
						//report.append(segmentInfo + "Track height at start should be 0. Actual height: " + centerHeight +"\n");
					}
				}
			}

			if (hasBankingStart && hasBankingStartFromHeights)
			{
				report.append(segmentInfo + "sets a banking start angle, and its left and right start heights also define a banking\n");

				double bankingFromHeights = Math.toDegrees(Math.atan2(heightStartLeft - heightStartRight, width));

				if (bankingStart != bankingFromHeights)
				{
					report.append(segmentInfo + "Banking start (" + bankingStart + ") doesn't match the banking from the heights (" + bankingFromHeights + ")\n");
				}
			}

			double heightEnd = segment.getHeightEnd();
			boolean hasHeightEnd = !Double.isNaN(heightEnd);

			double heightEndLeft = segment.getHeightEndLeft();
			boolean hasHeightEndLeft = !Double.isNaN(heightEndLeft);

			double heightEndRight = segment.getHeightEndRight();
			boolean hasHeightEndRight = !Double.isNaN(heightEndRight);

			boolean hasHeightEndLeftRight = hasHeightEndLeft && hasHeightEndRight;
			boolean hasBankingEndFromHeights = hasHeightEndLeftRight && heightEndLeft != heightEndRight;

			double bankingEnd = segment.getBankingEnd();
			boolean hasBankingEnd = !Double.isNaN(bankingEnd);

			if (hasHeightEnd && (hasHeightEndLeft || hasHeightEndRight))
			{
				report.append(segmentInfo + "sets Height End together with Height End Left or Right. Use one or the other.\n");
			}

			if (hasBankingEnd && hasBankingEndFromHeights)
			{
				report.append(segmentInfo + "sets a banking end angle, and its left and right end heights also define a banking\n");

				double bankingFromHeights = Math.toDegrees(Math.atan2(heightEndLeft - heightEndRight, width));

				if (bankingEnd != bankingFromHeights)
				{
					report.append(segmentInfo + "Banking end (" + bankingEnd + ") doesn't match the banking from the heights (" + bankingFromHeights + ")\n");
				}
			}

			double grade = segment.getGrade();
			boolean hasGrade = !Double.isNaN(grade);

			if (hasGrade && hasHeightEndLeftRight)
			{
				if (hasBankingEndFromHeights)
				{
					report.append(segmentInfo + "sets a grade, and its left and right end heights also define the end height and banking\n");
				}
				else
				{
					report.append(segmentInfo + "sets both a grade and left and right end heights\n");
				}
			}

			Segment previous = segment.previousShape;
			Segment next = segment.nextShape;

			if (previous != null && hasHeightStartLeftRight)
			{
				if (!Double.isNaN(previousHeightEndLeft) && previousHeightEndLeft != heightStartLeft)
				{
					report.append(segmentInfo + "Previous segment's left end height (" + previousHeightEndLeft + ") doesn't match this segment's left start height (" + heightStartLeft + ")\n");
				}

				if (!Double.isNaN(previousHeightEnd) && previousHeightEnd != heightStartLeft)
				{
					report.append(segmentInfo + "Previous segment's end height (" + previousHeightEnd + ") doesn't match this segment's left start height (" + heightStartLeft + ")\n");
				}

				if (!Double.isNaN(previousHeightEndRight) && previousHeightEndRight != heightStartRight)
				{
					report.append(segmentInfo + "Previous segment's right end height (" + previousHeightEndRight + ") doesn't match this segment's right start height (" + heightStartRight + ")\n");
				}

				if (!Double.isNaN(previousHeightEnd) && previousHeightEnd != heightStartRight)
				{
					report.append(segmentInfo + "Previous segment's end height (" + previousHeightEnd + ") doesn't match this segment's right start height (" + heightStartRight + ")\n");
				}
			}
			else if (previous != null && hasHeightStart)
			{
				if (!Double.isNaN(previousHeightEndLeft) && previousHeightEndLeft != heightStart)
				{
					report.append(segmentInfo + "Previous segment's left end height (" + previousHeightEndLeft + ") doesn't match this segment's start height (" + heightStart + ")\n");
				}

				if (!Double.isNaN(previousHeightEndRight) && previousHeightEndRight != heightStart)
				{
					report.append(segmentInfo + "Previous segment's right end height (" + previousHeightEndRight + ") doesn't match this segment's start height (" + heightStart + ")\n");
				}

				if (!Double.isNaN(previousHeightEnd) && previousHeightEnd != heightStart)
				{
					report.append(segmentInfo + "Previous segment's end height (" + previousHeightEnd + ") doesn't match this segment's start height (" + heightStart + ")\n");
				}
			}
			else if (next == null && hasHeightEndLeftRight)
			{
				if (!Double.isNaN(firstHeightStartLeft) && firstHeightStartLeft != heightEndLeft)
				{
					report.append(segmentInfo + "Left end height (" + heightEndLeft + ") doesn't match the first segment's left start height (" + firstHeightStartLeft + ")\n");
				}

				if (!Double.isNaN(firstHeightStart) && firstHeightStart != heightEndLeft)
				{
					report.append(segmentInfo + "Left end height (" + heightEndLeft + ") doesn't match the first segment's start height (" + firstHeightStart + ")\n");
				}

				if (!Double.isNaN(firstHeightStartRight) && firstHeightStartRight != heightEndRight)
				{
					report.append(segmentInfo + "Right end height (" + heightEndRight + ") doesn't match the first segment's right start height (" + firstHeightStartRight + ")\n");
				}

				if (!Double.isNaN(firstHeightStart) && firstHeightStart != heightEndRight)
				{
					report.append(segmentInfo + "Right end height (" + heightEndRight + ") doesn't match the first segment's start height (" + firstHeightStart + ")\n");
				}
			}
			else if (next == null && hasHeightEnd)
			{
				if (!Double.isNaN(firstHeightStartLeft) && firstHeightStartLeft != heightEnd)
				{
					report.append(segmentInfo + "End height (" + heightEnd + ") doesn't match the first segment's left start height (" + firstHeightStartLeft + ")\n");
				}

				if (!Double.isNaN(firstHeightStartRight) && firstHeightStartRight != heightEnd)
				{
					report.append(segmentInfo + "End height (" + heightEnd + ") doesn't match the first segment's right start height (" + firstHeightStartRight + ")\n");
				}

				if (!Double.isNaN(firstHeightStart) && firstHeightStart != heightEnd)
				{
					report.append(segmentInfo + "End height (" + heightEnd + ") doesn't match the first segment's start height (" + firstHeightStart + ")\n");
				}
			}

			previousHeightEnd = heightEnd;
			previousHeightEndLeft = heightEndLeft;
			previousHeightEndRight = heightEndRight;
		}
		checkTrackEndHeight(segments);
	}

	/**
	 * The end of the last segment meets the start of the first. When the file
	 * gives both heights, checkTrackHeight compares them; otherwise the missing
	 * one is worked out as the game does: from the last grade, or kept from the
	 * segment before, and the track starts at 0.
	 */
	private void checkTrackEndHeight(SegmentVector segments)
	{
		if (segments.isEmpty())
			return;
		Segment first = segments.get(0);
		Segment last = segments.get(segments.size() - 1);
		boolean lastGiven = last.hasHeightEnd() || (last.hasHeightEndLeft() && last.hasHeightEndRight());
		boolean firstGiven = first.hasHeightStart() || first.hasHeightStartLeft() || first.hasHeightStartRight();
		if (lastGiven && firstGiven)
			return;

		trackData.calculateSegmentValues();
		double endLeft = last.getCalculatedHeightEndLeft();
		double endRight = last.getCalculatedHeightEndRight();
		double startLeft = first.getCalculatedHeightStartLeft();
		double startRight = first.getCalculatedHeightStartRight();
		// a millimetre: the heights worked out add up lengths and grades
		double tolerance = 0.001;
		if (Double.isNaN(endLeft) || Double.isNaN(endRight) || Double.isNaN(startLeft) || Double.isNaN(startRight)
			|| (Math.abs(endLeft - startLeft) <= tolerance && Math.abs(endRight - startRight) <= tolerance))
			return;

		report.append("Segment " + last.getName() + " : end height " + heights(endLeft, endRight)
			+ (lastGiven ? "" : " (calculated, because none is set)") + " doesn't match the start of the first segment "
			+ first.getName() + " " + heights(startLeft, startRight)
			+ (firstGiven ? "" : " (no start height is set, so it starts at 0)") + "\n", segment(last));
	}

	/** @return one height when both sides have it, else left and right */
	private static String heights(double left, double right)
	{
		if (Math.abs(left - right) <= 0.001)
			return format((left + right) / 2);
		return "left " + format(left) + " right " + format(right);
	}

	private void checkGraphic()
	{
		String image = trackData.getGraphic().getBackgroundImage();
		if (hasText(image))
			checkTrackTexture("Graphic Background Image ", image);
	}

	/**
	 * The pictures the game's track menu shows, from the track's folder, named
	 * after the folder. TORCS shows the map &lt;name&gt;.png (a PNG), and nothing
	 * without it. Speed Dreams and CSR show outline.png, and a preview,
	 * &lt;name&gt;.jpg or else &lt;name&gt;.png; without them they show their own
	 * placeholders. None is needed to race on the track.
	 */
	private void checkMenuImages()
	{
		String path = Properties.getInstance().getPath();
		if (path == null || path.isEmpty() || !new File(path).isDirectory())
			return;
		File folder = new File(path);
		String name = folder.getAbsoluteFile().getName();
		// the game's name as a possessive: Speed Dreams', TORCS's
		String gameName = trackContext.getGameType().getName();
		String game = gameName + (gameName.endsWith("s") ? "'" : "'s");
		if (trackContext.getGameType() == GameType.TORCS)
		{
			checkMenuImage(new File(folder, name + ".png"), "Menu map", game + " track menu shows no map of the track");
			return;
		}
		checkMenuImage(new File(folder, "outline.png"), "Menu outline", game + " track menu shows a placeholder instead of the track's outline");
		File jpg = new File(folder, name + ".jpg");
		File png = new File(folder, name + ".png");
		if (jpg.exists())
			checkMenuImage(jpg, "Menu preview", game + " track menu shows a placeholder picture");
		else if (png.exists())
			checkMenuImage(png, "Menu preview", game + " track menu shows a placeholder picture");
		else
			report.append("Menu preview " + jpg.getName() + " or " + png.getName() + " isn't in the track's folder, so " + game
				+ " track menu shows a placeholder picture\n");
	}

	/** Reports a picture of the track menu that isn't in the track's folder or can't be read */
	private void checkMenuImage(File file, String what, String without)
	{
		if (!file.isFile())
		{
			report.append(what + " " + file.getName() + " isn't in the track's folder, so " + without + "\n");
			return;
		}
		BufferedImage image = null;
		try
		{
			image = ImageIO.read(file);
		}
		catch (Exception e)
		{
		}
		if (image == null)
		{
			String type = file.getName().endsWith(".jpg") ? "jpg" : "png";
			report.append(what + " " + file.getName() + " can't be read: it isn't a valid " + type + " image, so " + without + "\n");
		}
	}

	/** The AC3D files the game shows the track with: the track's own, and CSR's for each kind of object */
	private void checkGraphic3D()
	{
		Graphic graphic = trackData.getGraphic();
		checkGraphic3DFile("Description", graphic.getDescription());
		if (trackContext.getGameType().hasTypeObjectFiles())
		{
			checkGraphic3DFile("Terrain", graphic.getTerrainObjects());
			checkGraphic3DFile("Road", graphic.getRoadObjects());
			checkGraphic3DFile("Road2", graphic.getRoad2Objects());
			checkGraphic3DFile("Trees", graphic.getTreesObjects());
			checkGraphic3DFile("Building", graphic.getBuildingObjects());
			checkGraphic3DFile("House", graphic.getHouseObjects());
			checkGraphic3DFile("Cars", graphic.getCarsObjects());
			checkGraphic3DFile("Truck", graphic.getTruckObjects());
			checkGraphic3DFile("Tribunes", graphic.getTribunesObjects());
			checkGraphic3DFile("Pub", graphic.getPubObjects());
			checkGraphic3DFile("Decors", graphic.getDecorsObjects());
			checkGraphic3DFile("Water", graphic.getWaterObjects());
			checkGraphic3DFile("Grass", graphic.getGrassObjects());
			checkGraphic3DFile("Sand", graphic.getSandObjects());
			checkGraphic3DFile("Rock", graphic.getRockObjects());
			checkGraphic3DFile("Curb", graphic.getCurbObjects());
			checkGraphic3DFile("Fence", graphic.getFenceObjects());
			checkGraphic3DFile("Barrier", graphic.getBarrierObjects());
			checkGraphic3DFile("Wall", graphic.getWallObjects());
			checkGraphic3DFile("Environment", graphic.getEnvironmentObjects());
		}
	}

	private void checkGraphic3DFile(String what, String file)
	{
		if (hasText(file) && !reportFullPath("Graphic 3D " + what + " file", file, "the track's folder", false, trackContext.getTrackFolders())
			&& findFile(file, trackContext.getTrackFolders()) == null)
			report.append("Graphic 3D " + what + " file " + file + " not found\n");
	}

	private void checkTrackLights()
	{
		Vector<TrackLight> lightData = trackData.getTrackLights();

		for (int i = 0; i < lightData.size(); i++)
		{
			TrackLight light = lightData.get(i);
			report.about(properties("Lights", light.getName()));

			checkLightTexture("Track Lights " + light.getName() + " Texture On", light.getTextureOn());
			checkLightTexture("Track Lights " + light.getName() + " Texture Off", light.getTextureOff());
		}
	}

	/**
	 * A texture of the track's own, like an environment map or the background
	 * image, is usually in the track's folder: one that isn't is reported even
	 * when the game's data folder isn't set, so data/textures can't be searched.
	 */
	private void checkTrackTexture(String description, String texture)
	{
		checkTexture(description, texture, true);
		if (hasText(texture) && !isFullPath(texture) && (dataDirectory == null || dataDirectory.isEmpty())
			&& findFile(texture, Properties.getInstance().getPath()) == null)
		{
			report.append(description + " texture " + texture
				+ " isn't in the track's folder (set the game's data folder in Preferences to look in data/textures too)\n");
		}
	}

	/**
	 * @return whether .rgb textures should be converted to png: only for Speed
	 *         Dreams and CSR, TORCS tracks use .rgb
	 */
	private boolean wantsPng()
	{
		return trackContext.getGameType() != GameType.TORCS;
	}

	/**
	 * A light needs no texture: it has no image then. The game's tracks name
	 * light textures from the game's data folder, like
	 * data/textures/start-light-red.png, so a path isn't a problem here.
	 */
	private void checkLightTexture(String description, String texture)
	{
		if (!hasText(texture))
			return;

		if (isFullPath(texture))
		{
			// the game loads it from where it is: on this computer only
			String use = nameIn(texture, dataDirectory);
			report.append(description + " texture " + texture + " is a full path, so the track only works on this computer. "
				+ "The game loads light textures from the data folder. " + (use != null ? "Use " + use + " instead."
					: "Put the file in its data" + sep + "textures folder and use data/textures/" + new File(texture).getName() + ".") + "\n");
		}
		// the game loads it from its data folder; without it, it can't be found
		else if (hasText(dataDirectory) && TrackFolders.findLightTexture(trackContext, texture) == null)
		{
			report.append(description + " texture " + texture + " not found\n");
		}

		if (texture.endsWith(".rgb") && wantsPng())
		{
			report.append(description + " texture " + texture + PNG_MESSAGE + "\n");
		}

		String format = trackContext.getGameType().textureFormatProblem(texture);
		if (format != null)
		{
			report.append(description + " texture " + texture + ": " + format + "\n");
		}
	}
	private void checkEnvironmentMapping()
	{
		Vector<EnvironmentMapping>	envMaps = trackData.getGraphic().getEnvironmentMapping();

		for (int i = 0; i < envMaps.size(); i++)
		{
			String texture = envMaps.get(i).getEnvMapImage();
			report.about(properties("Env Map", envMaps.get(i).getName()));
			checkTrackTexture("Environment Map " + envMaps.get(i).getName(), texture);
		}
	}

	/**
	 * Trackgen compares the first 8 characters, so interior2 is interior.
	 *
	 * @param data an AC3D object's data
	 * @return whether trackgen uses the object as an interior or exterior relief
	 */
	private static boolean isReliefData(String data)
	{
		return data.startsWith("interior") || data.startsWith("exterior");
	}

	/**
	 * Counts the lines trackgen uses as reliefs: lines in an object with
	 * interior or exterior data, or anywhere under one.
	 *
	 * @param object the object to count from
	 * @param inRelief whether a parent has interior or exterior data
	 * @param torcs whether any data counts (TORCS treats it as exterior)
	 * @return the number of objects with relief lines
	 */
	private static int countReliefLines(Ac3dObject object, boolean inRelief, boolean torcs)
	{
		String data = object.getData();

		if (data != null && (torcs || isReliefData(data)))
		{
			inRelief = true;
		}

		int count = 0;

		if (inRelief)
		{
			for (Ac3dSurface surface : object.getSurfaces())
			{
				if (surface.isOpenLine() || surface.isClosedLine())
				{
					count++;
					break;
				}
			}
		}

		for (Ac3dObject kid : object.getKids())
		{
			count += countReliefLines(kid, inRelief, torcs);
		}

		return count;
	}

	private void checkTerrainGeneration()
	{
		// TORCS trackgen stops with an error for these
		if (trackContext.getGameType().hasTorcsTrackgenKeys())
		{
			double gap = trackData.getGraphic().getTerrainGeneration().getCrossingBridgeSocketGap();
			double thickness = trackData.getGraphic().getTerrainGeneration().getCrossingBridgeThickness();
			if (!Double.isNaN(gap) && (Double.isInfinite(gap) || gap < 0))
				report.append("Terrain Generation crossing bridge socket gap " + gap + " can't be negative. Trackgen will stop.\n", properties("Terrain", "Bridges"));
			if (!Double.isNaN(thickness) && (Double.isInfinite(thickness) || thickness <= 0))
				report.append("Terrain Generation crossing bridge thickness " + thickness + " must be greater than 0. Trackgen will stop.\n", properties("Terrain", "Bridges"));
		}

		String reliefFile = trackData.getGraphic().getTerrainGeneration().getReliefFile();

		if (reliefFile != null && !reliefFile.isEmpty())
		{
			// like trackgen, in the track's folder only
			File file;
			try
			{
				file = trackFolderFile(reliefFile);
			}
			catch (java.nio.file.InvalidPathException e)
			{
				file = null;
			}

			if (file == null || !file.exists())
			{
				report.append("Terrain Generation relief file " + reliefFile + " not found" + notFoundReason(reliefFile, false) + "\n");
			}
			else
			{
				Ac3d ac3dFile = new Ac3d();

				try
				{
					ac3dFile.read(file);

					for (Ac3dMaterial material : ac3dFile.getMaterials())
					{
						String name = material.getName();

						// only TORCS's AC3D loader can't read a quoted name with a space (see SPACE_PROBLEM)
						if (name != null && name.contains(" ") && trackContext.getGameType() == GameType.TORCS)
						{
							report.append("Terrain Generation relief file " + reliefFile + " has a material, \"" + name + "\", " + SPACE_PROBLEM + "\n");
						}
					}

					Ac3dObject root = ac3dFile.getRoot();
					boolean torcs = trackContext.getGameType() == GameType.TORCS;

					if (root == null || countReliefLines(root, false, torcs) == 0)
					{
						report.append("Terrain Generation relief file " + reliefFile + " : no interior or exterior lines\n");
					}

					if (root != null && "world".equals(root.getType()))
					{
						for (int i = 0; i < root.getKids().size(); i++)
						{
							Ac3dObject object = root.getKids().get(i);

							if ("poly".equals(object.getType()))
							{
								String data = object.getData();

								if (data == null)
								{
									report.append("Terrain Generation relief file " + reliefFile + " line " + object.getLinenum() + " : missing interior or exterior data\n");
								}
								else if (!isReliefData(data))
								{
									// TORCS trackgen puts everything that isn't interior in the exterior list
									String result = torcs ? "treated as exterior by" : "ignored by";

									report.append("Terrain Generation relief file " + reliefFile + " line " + object.getLinenum() + " : expected interior or exterior data but found " + data + ", " + result + " " + trackContext.getGameType().getShortName() + " trackgen\n");
								}

								// sd2-trackgen expects borders and holes to be closed line and generates bad terrain when using line and duplicate vertices
								for (int j = 0; j < object.getSurfaces().size(); j++)
								{
									Ac3dSurface	surface = object.getSurfaces().get(j);

									if (surface.isOpenLine())
									{
										double[] firstVertex = object.getVertices().get(0);
										double[] lastVertex = object.getVertices().get(object.getVertices().size() - 1);

										if (firstVertex[0] == lastVertex[0] && firstVertex[1] == lastVertex[1] && firstVertex[2] == lastVertex[2])
										{
											report.append("Terrain Generation relief file " + reliefFile + " line " + object.getLinenum() + " : a line whose first and last vertices are the same\n");
										}
									}
								}
							}
						}
					}
				}
				catch (Ac3dException e)
				{
					report.append("Terrain Generation relief file " + reliefFile + " line " + e.getLineNumber() + " : " + e.getLocalizedMessage() + "\n");
				}
				catch (Exception e)
				{
					report.append("Terrain Generation relief file " + utils.circuit.Reliefs.problem(reliefFile, e) + "\n");
				}
			}
		}

		String elevationFile = trackData.getGraphic().getTerrainGeneration().getElevationMap();

		if (elevationFile != null && !elevationFile.isEmpty())
		{
			if (findFile(elevationFile, Properties.getInstance().getPath()) == null)
			{
				report.append("Terrain Generation elevation file " + elevationFile + " not found" + notFoundReason(elevationFile, false) + "\n");
			}
		}

		// only Speed Dreams trackgen reads the tiled file
		String tiledFile = trackData.getGraphic().getTerrainGeneration().getTiledFile();

		if (trackContext.getGameType().hasTiledFile() && tiledFile != null && !tiledFile.isEmpty())
		{
			if (findFile(tiledFile, Properties.getInstance().getPath()) == null)
			{
				report.append("Terrain Generation tiled file " + tiledFile + " not found" + notFoundReason(tiledFile, false) + "\n");
			}
		}

		// get colors from image files
		Vector<ObjectMap>	objectMaps = trackData.getObjectMaps();
		Set<Integer>		colors = new HashSet<Integer>();

		for (int i = 0; i < objectMaps.size(); i++)
		{
			ObjectMap objectMap = objectMaps.get(i);
			report.about(properties("Terrain", "Object Maps", objectMap.getName()));
			String objectMapName = objectMap.getName();
			String objectMapFile = objectMap.getObjectMap();

			if (!hasText(objectMapName))
			{
				report.append("Terrain Generation object map " + (i + 1) + " is missing a name\n");
				objectMapName = (i + 1) + "";
			}

			if (!hasText(objectMapFile))
			{
				report.append("Terrain Generation object map " + objectMapName + " is missing a file name\n");
			}
			else
			{
				File file;
				try
				{
					// like trackgen, in the track's folder, with any folder in the name from there
					Paths.get(objectMapFile);
					file = trackFolderFile(objectMapFile);
				}
				catch (java.nio.file.InvalidPathException e)
				{
					// a name with characters the file system doesn't allow
					report.append("Terrain Generation object map " + objectMapName + " file " + objectMapFile + " isn't a valid file name\n");
					continue;
				}

				if (file == null || !file.exists())
				{
					report.append("Terrain Generation object map file " + objectMapFile + " not found" + notFoundReason(objectMapFile, false) + "\n");
				}
				else
				{
					colors.addAll(objectMap.getColors());

					// trackgen spreads an image of any size over the terrain, but only reads PNG
					if (!isPng(file))
					{
						report.append("Terrain Generation object map " + objectMapName + " file " + objectMapFile
							+ " isn't a PNG image: trackgen only reads PNG object maps, so it places none of its objects\n");
					}
					else if (objectMap.getImageWidth() <= 0 || objectMap.getImageHeight() <= 0)
					{
						report.append("Terrain Generation object map " + objectMapName + " file " + objectMapFile
							+ " couldn't be read when the track was opened, so the map has no objects and saving won't overwrite the file\n");
					}
				}
			}
		}

		Set<Integer> definedColors = new HashSet<Integer>();
		// the object each color places: the track's own, else the default one
		Map<Integer, String> colorNames = new HashMap<Integer, String>();

		report.about(properties("Terrain"));

		// get defined colors from track objects
		for (int i = 0; i < trackData.getObjects().size(); i++)
		{
			int color = trackData.getObjects().get(i).getColor();

			if (color == Integer.MAX_VALUE)
			{
				report.append("Track object " + (i + 1) + " is missing a color\n", properties("Objects", trackData.getObjects().get(i).getName()));
			}
			else
			{
				definedColors.add(color);
				colorNames.putIfAbsent(color, trackData.getObjects().get(i).getName());
			}
		}

		// get defined colors from default objects
		for (int i = 0; i < defaultObjects.size(); i++)
		{
			int color = defaultObjects.get(i).getColor();

			if (color == Integer.MAX_VALUE)
			{
				report.append("Default object " + (i + 1) + " is missing a color\n", NONE);
			}
			else
			{
				definedColors.add(color);
				colorNames.putIfAbsent(color, defaultObjects.get(i).getName());
			}
		}

		// check for undefined image colors
		Iterator<Integer> colorsIterator = colors.iterator();

		while (colorsIterator.hasNext())
		{
			int color = colorsIterator.next();

			if (!definedColors.contains(color))
			{
				report.append("No object is defined for color " + String.format("0x%06X", color) + "\n", properties("Objects"));
			}
		}

		// check for big pixels: only for the objects' colors, as trackgen places
		// nothing for the others (a map's background or fill, said once above)
		colorsIterator = colors.iterator();

		while (colorsIterator.hasNext())
		{
			int color = colorsIterator.next();
			if (!definedColors.contains(color))
				continue;
			Set<Point> pixels = new HashSet<Point>();

			for (int i = 0; i < objectMaps.size(); i++)
			{
				Vector<ObjShapeObject>	objects = objectMaps.get(i).getObjects();

				for (int j = 0; j < objects.size(); j++)
				{
					ObjShapeObject object = objects.get(j);

					if (object.getRGB() == color)
					{
						Point pixel = new Point(object.getImageX(), object.getImageY());

						if (pixels.contains(pixel))
						{
							report.append("Duplicate objects for color " + String.format("0x%06X", color) +
									" at " + object.getImageX() + ", " + object.getImageY() + "\n", properties("Terrain", "Object Maps"));
						}
						else
						{
							pixels.add(pixel);
						}
					}
				}
			}

			// one message a color: a blob of pixels is many objects in a clump, however big
			int adjacent = 0;
			Point first = null;
			Iterator<Point> pixelsIterator = pixels.iterator();

			while (pixelsIterator.hasNext())
			{
				Point pixel = pixelsIterator.next();

				if (pixels.contains(new Point(pixel.x + 1, pixel.y)) || pixels.contains(new Point(pixel.x, pixel.y + 1)))
				{
					adjacent++;
					// the top left one, so the message is the same each time
					if (first == null || pixel.y < first.y || (pixel.y == first.y && pixel.x < first.x))
						first = pixel;
				}
			}
			if (adjacent > 0)
			{
				String name = colorNames.get(color);
				report.append("Adjacent pixels for object color " + String.format("0x%06X", color) + (name == null ? "" : " (" + name + ")")
					+ ": " + adjacent + (adjacent == 1 ? " pixel touches" : " pixels touch") + " another pixel of the same color (the first at "
					+ first.x + ", " + first.y + "). Trackgen places an object on every pixel, so these form a cluster of objects.\n",
					properties("Terrain", "Object Maps"));
			}
		}

		//check for objects outside terrain boundary
		SegmentVector track = trackContext.getTrackData().getSegments();
		int size = track.size();
		Rectangle2D.Double boundingRectangle = null;

		for(int i = 0; i < size; i++)
		{
			Rectangle2D.Double r = track.get(i).getBounds();

			if (boundingRectangle == null)
				boundingRectangle = new Rectangle2D.Double(r.x, r.y, r.width, r.height);	// a copy, add() changes it
			else
				boundingRectangle.add(r);
		}
		// without segments there is no terrain to place anything on (checkTrack says so)
		if (boundingRectangle == null)
			return;
		double border = trackContext.getTrackData().getGraphic().getTerrainGeneration().getBorderMargin();
		if (Double.isNaN(border))
		{
			// trackgen uses this when the track doesn't set one
			border = TerrainGeneration.DEFAULT_BORDER_MARGIN;
		}
		Rectangle2D.Double terrainRectangle = new Rectangle2D.Double(boundingRectangle.getMinX() - border,
																	 boundingRectangle.getMinY() - border,
																	 boundingRectangle.getWidth() + (border * 2),
																	 boundingRectangle.getHeight() + (border * 2));

		// where trackgen makes terrain: the border or relief border, less the holes
		ObjShapeTerrain terrainShape = new ObjShapeTerrain();
		terrainShape.calcShape(trackContext, boundingRectangle);

		for (int i = 0; i < objectMaps.size(); i++)
		{
			ObjectMap objectMap = objectMaps.get(i);
			Vector<ObjShapeObject> objects = objectMap.getObjects();
			double widthScale = terrainRectangle.getWidth() / objectMap.getImageWidth();
			double heightScale = terrainRectangle.getHeight() / objectMap.getImageHeight();

			for (int j = 0; j < objects.size(); j++)
			{
				ObjShapeObject object = objects.get(j);
				Point2D.Double location = object.getTrackLocation();

				if (location == null)
				{
					double worldX = terrainRectangle.getMinX() + (object.getImageX() * widthScale);
					double worldY = terrainRectangle.getMinY() + ((objectMap.getImageHeight() - object.getImageY()) * heightScale);

					location = new Point2D.Double(worldX, worldY);
				}

				if (!terrainRectangle.contains(location))
				{
					report.append("Object map " + objectMaps.get(i).getName() + " object " + object.getName() + " is outside the terrain boundary\n", properties("Terrain", "Object Maps", objectMaps.get(i).getName()));
				}
				else
				{
					String place = placeText(terrainShape, location.x, location.y);
					if (place != null)
						report.append("Object map " + objectMaps.get(i).getName() + " object " + object.getName() + " " + place + "\n", properties("Terrain", "Object Maps", objectMaps.get(i).getName()));
				}
			}
		}

		//check for graphic objects outside the terrain (beyond the border or relief border)
		//or inside a hole: trackgen finds no height there (no terrain or track), so it leaves them out
		Vector<GraphicObject> graphicObjects = trackData.getGraphic().getTerrainGeneration().getGraphicObjects();
		if (trackContext.getGameType().hasGraphicObjects())
		{
			for (GraphicObject graphicObject : graphicObjects)
			{
				String place = placeText(terrainShape, graphicObject.getX(), graphicObject.getY());
				if (place != null)
					report.append("Graphic Object " + graphicObject.getName() + " " + place + "\n", properties("Terrain", "Objects"));
			}
		}

		// (graphic objects with the same name: see checkSectionNames)
	}

	/** @return whether the file starts like a PNG image */
	private static boolean isPng(File file)
	{
		byte[] signature = { (byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n' };
		byte[] start = new byte[signature.length];
		try (java.io.FileInputStream in = new java.io.FileInputStream(file))
		{
			return in.read(start) == start.length && java.util.Arrays.equals(start, signature);
		}
		catch (java.io.IOException e)
		{
			return false;
		}
	}

	/**
	 * Finds a model as the games' trackgen does: the name, with any folder in it,
	 * in the track's folder, then in the data folder's data/objects. A full path
	 * isn't found: they put it after those folders too (see isFullPath).
	 *
	 * @return the file, or null when the game doesn't find it
	 */
	private File findObjectFile(String object)
	{
		if (isFullPath(object))
			return null;

		File file = new File(Properties.getInstance().getPath(), object);
		if (file.isFile())
			return file;

		if (dataDirectory != null)
		{
			file = new File(dataDirectory + sep + "data" + sep + "objects", object);
			if (file.isFile())
				return file;
		}
		return null;
	}

	/** @return whether the name is a full path: C:\..., \\server\... or /... */
	public static boolean isFullPath(String name)
	{
		return name.startsWith("/") || name.startsWith("\\") || (name.length() > 1 && name.charAt(1) == ':')
			|| new File(name).isAbsolute();
	}

	/**
	 * @param model true for an object's model, which the game also looks for in
	 *        the data folder's data/objects; false for a relief file or object map
	 * @return why a file isn't found, after "... not found": a full path that is
	 *         there isn't where the game looks; TORCS goes on without a model
	 */
	private String notFoundReason(String name, boolean model)
	{
		String where = model ? "the track's folder and the data folder's data" + sep + "objects" : "the track's folder";
		if (isFullPath(name) && new File(name).isFile())
			return ". The game looks for it in " + where + ", so it doesn't find a file given by its full path.";
		if (model && trackContext.getGameType() == GameType.TORCS)
			return " in " + where + ". TORCS's trackgen doesn't skip it but tries to load it anyway, which can stop trackgen.";
		return "";
	}

	/**
	 * @return the relief file or object map name names as trackgen finds it: in
	 *         the track's folder, with any folder in the name from there; null
	 *         for a full path, which it puts after the track's folder too
	 */
	private File trackFolderFile(String name)
	{
		return isFullPath(name) ? null : new File(Properties.getInstance().getPath(), name);
	}

	/**
	 * Reports a file the track names by its full path. Another computer doesn't
	 * have it there, and the games look for a track's files in their folders,
	 * so the track only works on this computer, if it works at all.
	 *
	 * @param description what names it, as "Surface asphalt texture"
	 * @param where where the game looks for it, for the message
	 * @param nameOnly true when the game uses only the file's name, so a file in
	 *        a folder below those isn't found either
	 * @param folders where the game looks for it, for the name to use when the
	 *        file is in one of them
	 * @return whether the name is a full path
	 */
	private boolean reportFullPath(String description, String name, String where, boolean nameOnly, String... folders)
	{
		if (!isFullPath(name))
			return false;
		String use = nameIn(name, folders);
		if (nameOnly && use != null && use.contains("/"))
			use = null;
		report.append(description + " " + name + " is a full path, so the track only works on this computer, if it works at all. "
			+ "The game looks for it in " + where + ". " + (use != null ? "Use " + use + " instead." : "Put the file there and use its name.") + "\n");
		return true;
	}

	/**
	 * @return the file's name from the first of the folders it is in, with /, or
	 *         null when it is in none of them
	 */
	private static String nameIn(String fullPath, String... folders)
	{
		try
		{
			java.nio.file.Path path = Paths.get(fullPath).toAbsolutePath().normalize();
			for (String folder : folders)
			{
				if (folder == null || folder.isEmpty())
					continue;
				java.nio.file.Path base = Paths.get(folder).toAbsolutePath().normalize();
				if (path.startsWith(base) && !path.equals(base))
					return base.relativize(path).toString().replace('\\', '/');
			}
		}
		catch (java.nio.file.InvalidPathException e)
		{
			// not a path here: no name to give
		}
		return null;
	}

	/** @return where the game looks for textures (and the background image), with the data folder's folders when it is set */
	private String[] textureFolders()
	{
		return TrackFolders.textureFolders(trackContext);
	}

	/** @return the folders textureFolders gives, for a message */
	private String textureWhere(boolean image)
	{
		String data = "data" + sep + "textures, data" + sep + "img";
		if (image)
			data = "data" + sep + "img, data" + sep + "textures";
		return "the track's folder, " + data + " and the data folder" + (image ? ", using only its file name" : "");
	}

	/**
	 * @return the object file as messages name it: from the track's folder
	 *         ("pits.ac", "objects\tree.ac"), from the data folder's data/objects
	 *         ("tree3.ac in the data folder's data\objects"), else its path
	 */
	private String objectFileName(File file)
	{
		java.nio.file.Path path = file.toPath().toAbsolutePath().normalize();
		java.nio.file.Path track = Paths.get(Properties.getInstance().getPath()).toAbsolutePath().normalize();
		if (path.startsWith(track))
			return track.relativize(path).toString();
		if (dataDirectory != null)
		{
			java.nio.file.Path objects = Paths.get(dataDirectory, "data", "objects").toAbsolutePath().normalize();
			if (path.startsWith(objects))
				return objects.relativize(path) + " in the data folder's data" + sep + "objects";
		}
		return file.getPath();
	}
	private Boolean hasText(String text)
	{
		return !(text == null || text.trim().isEmpty());
	}
	/**
	 * @return null when trackgen finds a height there for an object: on the terrain, or
	 *         on the track (trackgen uses the track's height when there's no terrain);
	 *         otherwise where it is
	 */
	private String placeText(ObjShapeTerrain terrainShape, double x, double y)
	{
		ObjShapeTerrain.Place place = terrainShape.place(x, y);
		if (place == ObjShapeTerrain.Place.ON)
			return null;
		Point2D.Double point = new Point2D.Double(x, y);
		for (Segment segment : trackContext.getTrackData().getSegments())
		{
			if (segment.contains(point))
				return null;
		}
		return (place == ObjShapeTerrain.Place.IN_HOLE ? "inside a terrain hole" : "outside terrain boundary") +
			": trackgen leaves it out";
	}

	/** Checks an object's model: its AC3D file and textures (hidden unless asked for) */
	private void checkTrackObject(TrackObject trackObject, String type)
	{
		// a model that isn't there isn't a problem in the model: always shown
		if (!checkModelFound(trackObject, type))
			return;
		report.objectFile = true;
		try
		{
			checkTrackObjectModel(trackObject, type);
		}
		finally
		{
			report.objectFile = false;
		}
	}

	/**
	 * Reports an object without a model, or whose model isn't found.
	 * @return whether its model is found
	 */
	private boolean checkModelFound(TrackObject trackObject, String type)
	{
		String object = trackObject.getObject();
		if (!hasText(object))
		{
			report.append(type + " object " + trackObject.getName() + " has no model\n");
			return false;
		}
		if (findObjectFile(object) == null)
		{
			report.append(type + " object " + trackObject.getName() + " model " + object + " not found" + notFoundReason(object, true) + "\n");
			return false;
		}
		return true;
	}

	private void checkTrackObjectModel(TrackObject trackObject, String type)
	{
		doubleSided = false;
		flatShaded = false;
		textures.clear();

		String	object = trackObject.getObject();
		File	file = findObjectFile(object);

		try (BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			String line;

			while ((line = br.readLine()) != null)
			{
				if (line.startsWith("texture"))
				{
					String texture = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					checkTexture(type + " object " + trackObject.getName() + " model " + object, texture);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		Ac3d ac3dFile = new Ac3d();

		try
		{
			ac3dFile.read(file);

			for (Ac3dMaterial material : ac3dFile.getMaterials())
			{
				String name = material.getName();

				// only TORCS's AC3D loader can't read a quoted name with a space (see SPACE_PROBLEM)
				if (name != null && name.contains(" ") && trackContext.getGameType() == GameType.TORCS)
				{
					report.append("Object file " + objectFileName(file) + " has a material, \"" + name + "\", " + SPACE_PROBLEM + "\n");
				}
			}

			Ac3dObject root = ac3dFile.getRoot();

			if (root != null && "world".equals(root.getType()))
			{
				for (int i = 0; i < root.getKids().size(); i++)
				{
					checkKid(file, root.getKids().get(i));
				}
			}

			if (doubleSided)
			{
				report.append("Object file " + objectFileName(file) + " has a double-sided surface\n");
			}

			if (flatShaded)
			{
				report.append("Object file " + objectFileName(file) + " has a flat-shaded surface\n");
			}

			if (textures.size() > 1)
			{
				int hasAlpha = 0;
				boolean unreadable = false;
				for (String filename : textures)
				{
					File textureFile = findTextureFile(filename);
					if (filename.endsWith(".rgba"))
					{
						hasAlpha++;
					}
					else if (textureFile != null && filename.endsWith(".png"))
					{
						Boolean alpha = pngHasAlpha(textureFile, file);

						if (alpha == null)
						{
							unreadable = true;
						}
						else if (alpha)
						{
							hasAlpha++;
						}
					}
				}

				// check for more than one of either alpha or non-alpha textures
				// (not known with a texture that can't be read)
				if (unreadable)
				{
				}
				else if (hasAlpha == 0 || hasAlpha == textures.size() || (textures.size() - hasAlpha > 1) || hasAlpha > 1)
				{
					report.append("Object file " + objectFileName(file) + " : has multiple textures\n");
				}
			}
		}
		catch (Exception e)
		{
			report.append("Object file " + utils.FileProblem.describe(objectFileName(file), e, file.toPath()) + "\n");
		}
	}

	public void checkKid(File file, Ac3dObject object)
	{
		if ("poly".equals(object.getType()))
		{
			if (object.getTexture() == null || object.getTexture().isEmpty())
			{
				report.append("Object file " + objectFileName(file) + " line " + object.getLinenum() + " : an object has no texture\n");
			}

			if (object.getSurfaces().isEmpty())
			{
				report.append("Object file " + objectFileName(file) + " line " + object.getLinenum() + " : an object has no surfaces\n");
				return;
			}

			Set<Integer> types = new HashSet<Integer>();
			Set<Integer> mats = new HashSet<Integer>();
			String objectTexture = object.getTexture();

			if (objectTexture != null)
			{
				textures.add(objectTexture);
			}

			for (int i = 0; i < object.getSurfaces().size(); i++)
			{
				Ac3dSurface	surface = object.getSurfaces().get(i);

				if (surface.getRefs().size() != 3)
				{
					report.append("Object file " + objectFileName(file) + " line " + surface.getLinenum() + " : surface with " +  surface.getRefs().size() + " vertices\n");
				}

				types.add(surface.getSurf());
				mats.add(surface.getMat());

				// a double sided surface is only needed where the texture is see-through:
				// an opaque one (.rgb, or a .png without alpha) is reported
				if (surface.isDoubleSided() && objectTexture != null)
				{
					File textureFile = findTextureFile(objectTexture);
					if (objectTexture.endsWith(".rgba"))
					{
					}
					else if (objectTexture.endsWith(".rgb"))
					{
						doubleSided = true;
					}
					else if (textureFile != null && objectTexture.endsWith(".png"))
					{
						// not known with a texture that can't be read
						if (Boolean.FALSE.equals(pngHasAlpha(textureFile, file)))
						{
							doubleSided = true;
						}
					}
				}

				if (surface.isFlatShaded())
				{
					flatShaded = true;
				}
			}

			if (types.size() > 1)
			{
				report.append("Object file " + objectFileName(file) + " line " + object.getLinenum() + " : object with " + types.size() + " surface types\n");
			}

			if (mats.size() > 1)
			{
				report.append("Object file " + objectFileName(file) + " line " + object.getLinenum() + " : object with " + mats.size() + " materials\n");
			}
		}
		else
		{
			for (int i = 0; i < object.getKids().size(); i++)
			{
				checkKid(file, object.getKids().get(i));
			}
		}
	}

	private void checkTexture(String description, String texture)
	{
		checkTexture(description, texture, false);
	}

	/**
	 * @param image true for a texture the game uses by its name only, like the
	 *        background image or an environment map
	 */
	private void checkTexture(String description, String texture, boolean image)
	{
		Vector<String> messages = new Vector<String>();
		if (!hasText(texture))
		{
			messages.add(" missing texture\n");
		}
		else
		{
			// a full path is reported once, as that, not as not found or a path
			boolean fullPath = reportFullPath(description.trim() + " texture", texture, textureWhere(image), image, textureFolders());
			File textureFile = fullPath ? null : findTextureFile(texture);
			// without the game's data folder, one that isn't the track's can't be found
			if (textureFile == null && !fullPath && hasText(dataDirectory))
			{
				messages.add(" texture " + texture + " not found\n");
			}

			if (texture.endsWith(".rgb") && wantsPng())
			{
				messages.add(" texture " + texture + PNG_MESSAGE + "\n");
			}

			// a format the game can't load
			String format = trackContext.getGameType().textureFormatProblem(texture);
			if (format != null)
			{
				messages.add(" texture " + texture + ": " + format + "\n");
			}

			if (!fullPath && (texture.contains("/") || texture.contains("\\")))
			{
				messages.add(" texture " + texture + " should remove path\n");
			}
		}
		for (int i = 0; i < messages.size(); i++)
		{
			report.append(description + messages.get(i));
		}
	}
	private void checkObjects()
	{
		// check for duplicate object names
		for (int i = 0; i < trackData.getObjects().size(); i++)
		{
			String name = trackData.getObjects().get(i).getName();

			if (name == null || name.isEmpty())
			{
				report.append("Track object " + (i + 1) + " is missing a name\n", properties("Objects"));
			}
			else
			{
				// (two track objects with one name: see checkSectionNames)
				// the default objects are in the same section when the track includes them
				for (int j = 0; trackData.getHasObjectsReference() && j < defaultObjects.size(); j++)
				{
					if (name.equals(defaultObjects.get(j).getName()))
					{
						report.append("Track object " + (i + 1) + " " + name + " has the same name as default object " + (j + 1)
							+ ": the track includes the default objects, and the game can't load a track file with two sections of the same name\n", properties("Objects", name));
					}
				}
			}
		}

		// check for duplicate colors
		for (int i = 0; i < trackData.getObjects().size(); i++)
		{
			String name = trackData.getObjects().get(i).getName();
			int color = trackData.getObjects().get(i).getColor();

			if (color == Integer.MAX_VALUE)
			{
				report.append("Track object " + (i + 1) + " is missing a color\n", properties("Objects", trackData.getObjects().get(i).getName()));
			}
			else
			{
				for (int j = i + 1; j < trackData.getObjects().size(); j++)
				{
					if (color == trackData.getObjects().get(j).getColor())
					{
						report.append("Track object " + (i + 1) + " " + name + " has the same color as track object " + (j + 1) + " " + trackData.getObjects().get(j).getName() + "\n", properties("Objects", name));
					}
				}

				for (int j = 0; j < defaultObjects.size(); j++)
				{
					if (color == defaultObjects.get(j).getColor())
					{
						report.append("Track object " + (i + 1) + " " + name + " has the same color as default object " + (j + 1) + " " + defaultObjects.get(j).getName() + "\n", properties("Objects", name));
					}
				}
			}
		}

		// the models of the objects the track places: trackgen only loads those
		Set<Integer> used = usedObjectColors();
		for (int i = 0; i < trackData.getObjects().size(); i++)
		{
			TrackObject trackObject = trackData.getObjects().get(i);
			report.about(properties("Objects", trackObject.getName()));
			if (isUsed(trackObject, used))
			{
				checkTrackObject(trackObject, "Track");
			}
			// one the track doesn't place, as when its object map is missing,
			// still needs its model when it is placed
			else
			{
				checkModelFound(trackObject, "Track");
			}
		}

		if (trackContext.getCheckDefaultObjects())
		{
			for (int i = 0; i < defaultObjects.size(); i++)
			{
				if (isUsed(defaultObjects.get(i), used))
				{
					// a default object isn't edited in the track
					report.about(NONE);
					checkTrackObject(defaultObjects.get(i), "Default");
				}
			}
		}
	}

	/** @return the colors of the objects the track places: in its object maps, and the ones placed in the editor */
	private Set<Integer> usedObjectColors()
	{
		Set<Integer> used = new HashSet<Integer>();
		for (ObjectMap objectMap : trackData.getObjectMaps())
		{
			for (int color : objectMap.getColors())
				used.add(color & 0xFFFFFF);
		}
		for (utils.circuit.GraphicObject object : trackData.getGraphicObjects())
			used.add(object.getColor() & 0xFFFFFF);
		return used;
	}

	private static boolean isUsed(TrackObject object, Set<Integer> used)
	{
		int color = object.getColor();
		return color != Integer.MAX_VALUE && used.contains(color & 0xFFFFFF);
	}
	/**
	 * Reads a png texture once, and reports one that can't be read (ImageIO gives
	 * no image for a file that isn't one it knows).
	 *
	 * @param objectFile the object's file that uses it, for the message
	 * @return whether it has an alpha channel, or null when it can't be read
	 */
	private Boolean pngHasAlpha(File textureFile, File objectFile)
	{
		String path = textureFile.getAbsolutePath();
		if (!imageHasAlpha.containsKey(path))
		{
			Boolean alpha = null;
			try
			{
				BufferedImage image = ImageIO.read(textureFile);
				if (image != null)
					alpha = image.getColorModel().hasAlpha();
			}
			catch (Exception e)
			{
			}
			if (alpha == null)
				report.append("Object file " + objectFileName(objectFile) + " : texture " + textureFile.getName()
					+ " can't be read: it isn't a valid png image\n");
			imageHasAlpha.put(path, alpha);
		}
		return imageHasAlpha.get(path);
	}

	/** @return the texture where the game finds it (see TrackFolders.textureFolders), or null */
	private File findTextureFile(String texture)
	{
		return TrackFolders.findTexture(trackContext, texture);
	}
	/**
	 * @param where what uses the surface: a missing surface, or a default surface's
	 *        textures, are about it; a track surface's textures are about the surface
	 */
	private void checkSurface(String surface, String description, Subject where)
	{
		if (surface == null || surface.isEmpty())
			return;

		String message = description + " surface " + surface;
		Subject outer = report.about(where);
		try
		{
			checkSurface(surface, message);
		}
		finally
		{
			report.about(outer);
		}
	}

	/** @return whether the surface was found */
	private boolean checkSurface(String surface, String message)
	{
		for (int i = 0; i < trackData.getSurfaces().size(); i++)
		{
			if (trackData.getSurfaces().get(i).getName().equals(surface))
			{
				Subject outer = report.about(properties("Surfaces", surface));
				String texture = trackData.getSurfaces().get(i).getTextureName();
				checkTexture(message, texture);

				texture = trackData.getSurfaces().get(i).getBumpName();
				if (hasText(texture))
					checkTexture(message + " Bump ", texture);

				texture = trackData.getSurfaces().get(i).getRacelineName();
				if (hasText(texture))
					checkTexture(message + " Raceline ", texture);

				report.about(outer);
				return true;
			}
		}
		for (int i = 0; i < defaultSurfaces.size(); i++)
		{
			if (defaultSurfaces.get(i).getName().equals(surface))
			{
				String texture = defaultSurfaces.get(i).getTextureName();
				checkTexture(message, texture);

				texture = defaultSurfaces.get(i).getBumpName();
				if (hasText(texture))
					checkTexture(message + " Bump ", texture);

				texture = defaultSurfaces.get(i).getRacelineName();
				if (hasText(texture))
					checkTexture(message + " Raceline ", texture);

				return true;
			}
		}

		report.append(message + " not found\n");
		return false;
	}
	private void checkSurfaces()
	{
		// check for duplicate surface names
		for (int i = 0; i < trackData.getSurfaces().size(); i++)
		{
			String name = trackData.getSurfaces().get(i).getName();

			if (!hasText(name))
			{
				report.append("Track surface " + (i + 1) + " is missing a name\n", properties("Surfaces"));
			}
			else
			{
				// (two track surfaces with one name: see checkSectionNames)
				// the default surfaces are in the same section when the track includes them
				for (int j = 0; trackData.getHasSurfacesReference() && j < defaultSurfaces.size(); j++)
				{
					if (name.equals(defaultSurfaces.get(j).getName()))
					{
						report.append("Track surface " + (i + 1) + " " + name + " has the same name as default surface " + (j + 1)
							+ ": the track includes the default surfaces, and the game can't load a track file with two sections of the same name\n", properties("Surfaces", name));
					}
				}
			}

			// TORCS: the texture's height on crossing bridges; an invalid one is the texture size
			double sizeV = trackData.getSurfaces().get(i).getTextureSizeV();
			if (trackContext.getGameType().hasTorcsTrackgenKeys() && !Double.isNaN(sizeV) && !(sizeV > 0))
			{
				String what = hasText(name) ? " " + name : "";
				report.append("Track surface " + (i + 1) + what + ": Texture Size V (" + format(sizeV)
					+ " m) must be greater than 0. Crossing bridges will use the texture size instead.\n",
					hasText(name) ? properties("Surfaces", name) : properties("Surfaces"));
			}
		}

		checkSurface(trackData.getMainTrack().getSurface(), "Main Track", properties("Track"));
		checkSurface(trackData.getMainTrack().getLeft().getBorderSurface(), "Main Track Left Border", properties("Track", "Left"));
		checkSurface(trackData.getMainTrack().getLeft().getSideSurface(), "Main Track Left Side", properties("Track", "Left"));
		checkSurface(trackData.getMainTrack().getLeft().getBarrierSurface(), "Main Track Left Barrier", properties("Track", "Left"));
		checkSurface(trackData.getMainTrack().getRight().getBorderSurface(), "Main Track Right Border", properties("Track", "Right"));
		checkSurface(trackData.getMainTrack().getRight().getSideSurface(), "Main Track Right Side", properties("Track", "Right"));
		checkSurface(trackData.getMainTrack().getRight().getBarrierSurface(), "Main Track Right Barrier", properties("Track", "Right"));
		for (int i = 0; i < trackData.getSegments().size(); i++)
		{
			Segment segment = trackData.getSegments().get(i);

			checkSurface(segment.getSurface(), "Segment " + segment.getName() + " Track", segment(segment));
			checkSurface(segment.getLeft().getBorderSurface(), "Segment " + segment.getName() + " Left Border", segment(segment, "Left"));
			checkSurface(segment.getLeft().getSideSurface(), "Segment " + segment.getName() + " Left Side", segment(segment, "Left"));
			checkSurface(segment.getLeft().getBarrierSurface(), "Segment " + segment.getName() + " Left Barrier", segment(segment, "Left"));
			checkSurface(segment.getRight().getBorderSurface(), "Segment " + segment.getName() + " Right Border", segment(segment, "Right"));
			checkSurface(segment.getRight().getSideSurface(), "Segment " + segment.getName() + " Right Side", segment(segment, "Right"));
			checkSurface(segment.getRight().getBarrierSurface(), "Segment " + segment.getName() + " Right Barrier", segment(segment, "Right"));
		}
		checkSurface(trackData.getGraphic().getTerrainGeneration().getSurface(), "Terrain", properties("Terrain"));
		// the surfaces of the bridges where the track crosses itself: unset is the default
		if (trackContext.getGameType().hasTorcsTrackgenKeys())
		{
			for (int i = 0; i < TerrainGeneration.CROSSING_BRIDGE_SURFACE_KEYS.length; i++)
				checkSurface(trackData.getGraphic().getTerrainGeneration().getCrossingBridgeSurface(i),
					"Terrain Bridges " + TerrainGeneration.CROSSING_BRIDGE_SURFACE_NAMES[i], properties("Terrain", "Bridges"));
		}
		checkGrip();
	}

	// what every game's track loader uses for a value that a surface doesn't have
	private static final double	DEFAULT_FRICTION			= 0.8;
	private static final double	DEFAULT_ROLLING_RESISTANCE	= 0.001;
	// less than any surface that the games' default surfaces have for driving on: sand
	// and gravel are 0.6, and only barriers and walls are less. Ice is about 0.1.
	private static final double	LOW_FRICTION				= 0.3;
	private static final double	LEAST_DEFAULT_FRICTION		= 0.6;
	// the most that the games' default surfaces have: gravel
	private static final double	MOST_ROLLING_RESISTANCE		= 0.15;
	private static final double	ABSURD_ROLLING_RESISTANCE	= 0.5;

	/**
	 * Checks the grip of the surfaces that cars drive on: the track, and the borders
	 * and sides that the game gives a width (GameSides). A barrier, or a border that
	 * is a wall, has no grip on purpose, so those aren't checked.
	 */
	private void checkGrip()
	{
		Map<String, List<String>>	uses		= new LinkedHashMap<String, List<String>>();
		Map<String, Subject>		firstUse	= new HashMap<String, Subject>();

		addUse(uses, firstUse, trackData.getMainTrack().getSurface(), "Main Track", properties("Track"));
		for (int i = 0; i < trackData.getSegments().size(); i++)
		{
			Segment	segment	= trackData.getSegments().get(i);
			String	name	= "Segment " + segment.getName();

			addUse(uses, firstUse, segment.getSurface(), name + " Track", segment(segment));
			for (int j = 0; j < 2; j++)
			{
				boolean			right	= j == 1;
				String			side	= right ? "Right" : "Left";
				GameSides.State	state	= GameSides.of(segment, right, trackContext);

				if (state.borderWidth > 0 && !"wall".equals(state.borderStyle))
					addUse(uses, firstUse, state.borderSurface, name + " " + side + " Border", segment(segment, side));
				if (state.sideStartWidth > 0 || state.sideEndWidth > 0)
					addUse(uses, firstUse, state.sideSurface, name + " " + side + " Side", segment(segment, side));
			}
		}

		for (Map.Entry<String, List<String>> use : uses.entrySet())
		{
			String	name	= use.getKey();
			Surface	surface	= findSurface(trackData.getSurfaces(), name);
			Subject	target	= properties("Surfaces", name);
			boolean	isDefault	= surface == null;

			if (isDefault)
			{
				// a default surface can't be changed here, so the message is about what uses it
				surface = findSurface(defaultSurfaces, name);
				target = firstUse.get(name);
			}
			// (a surface that isn't found is reported by checkSurface)
			if (surface == null)
				continue;

			List<String>	where	= use.getValue();
			String			usedBy	= " It is used by " + where.get(0)
				+ (where.size() == 1 ? "" : " and " + (where.size() - 1) + (where.size() == 2 ? " other part" : " other parts") + " of the track")
				+ "."
				// a default surface is fixed by choosing another one where it is used
				+ (isDefault ? " Choose another surface there, rather than changing the game's default surfaces.\n" : "\n");
			String			what	= (isDefault ? "Default surface " : "Surface ") + name;

			double	friction			= surface.getFriction();
			double	rollingResistance	= surface.getRollingResistance();

			if (Double.isNaN(friction) && Double.isNaN(rollingResistance))
				report.append(what + " has no friction and no rolling resistance, so the game uses " + format(DEFAULT_FRICTION)
					+ " and " + formatSmall(DEFAULT_ROLLING_RESISTANCE) + ", whatever the surface is." + usedBy, target);
			else if (Double.isNaN(friction))
				report.append(what + " has no friction, so the game uses " + format(DEFAULT_FRICTION) + ", whatever the surface is." + usedBy, target);
			else if (Double.isNaN(rollingResistance))
				report.append(what + " has no rolling resistance, so the game uses " + formatSmall(DEFAULT_ROLLING_RESISTANCE)
					+ ", whatever the surface is." + usedBy, target);

			if (!Double.isNaN(friction) && !(friction > 0 && !Double.isInfinite(friction)))
				report.append(what + " has no grip: its friction is " + formatSmall(friction)
					+ ", so cars on it can't steer, brake or accelerate." + (isDefault ? "" : " Friction must be greater than 0.") + usedBy, target);
			else if (friction < LOW_FRICTION)
				report.append(what + ": friction " + formatSmall(friction) + " is less than " + format(LOW_FRICTION)
					+ ", so cars on it slide as if on ice (about 0.1). The least that the default surfaces cars drive on have is "
					+ format(LEAST_DEFAULT_FRICTION) + " (sand and gravel)." + usedBy, target);

			if (rollingResistance < 0)
				report.append(what + ": rolling resistance " + formatSmall(rollingResistance)
					+ " is less than 0, so it would push cars forward." + usedBy, target);
			else if (rollingResistance > ABSURD_ROLLING_RESISTANCE)
				report.append(what + ": rolling resistance " + formatSmall(rollingResistance) + " would hold back a car rolling on it by more than "
					+ "half its weight. The most that the default surfaces have is " + format(MOST_ROLLING_RESISTANCE) + " (gravel)." + usedBy, target);

			double	roughness	= surface.getRoughness();
			double	wavelength	= surface.getRoughnessWavelength();

			if (roughness < 0 || Double.isInfinite(roughness))
				report.append(what + ": roughness " + formatSmall(roughness) + " m must be 0 or more." + usedBy, target);
			if (!Double.isNaN(wavelength) && !(wavelength > 0 && !Double.isInfinite(wavelength)))
				report.append(what + ": roughness wavelength " + formatSmall(wavelength)
					+ " m must be greater than 0, because the game divides by it." + usedBy, target);
		}
	}

	private static void addUse(Map<String, List<String>> uses, Map<String, Subject> firstUse, String surface, String description, Subject where)
	{
		if (surface == null || surface.isEmpty())
			return;

		List<String> list = uses.get(surface);
		if (list == null)
		{
			list = new ArrayList<String>();
			uses.put(surface, list);
			firstUse.put(surface, where);
		}
		list.add(description);
	}

	private static Surface findSurface(List<Surface> surfaces, String name)
	{
		for (int i = 0; i < surfaces.size(); i++)
		{
			if (name.equals(surfaces.get(i).getName()))
				return surfaces.get(i);
		}
		return null;
	}

	/** @return a number with at most 3 significant digits, as 0.001 or 1.5 */
	private static String formatSmall(double value)
	{
		if (Double.isInfinite(value))
			return value > 0 ? "too big to be a number" : "too small to be a number";
		if (Double.isNaN(value))
			return "not a number";
		return new java.math.BigDecimal(value).round(new java.math.MathContext(3)).stripTrailingZeros().toPlainString();
	}
}
