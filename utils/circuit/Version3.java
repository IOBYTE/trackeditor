package utils.circuit;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import gui.EditorFrame;
import utils.TrackData;

/**
 * What a version 3 track means to the games' version 3 loaders (track3.cpp) that
 * the version 4 loaders read differently, so a version 3 track can become a
 * version 4 track without changing.
 */
public final class Version3
{
	// the surface the version 3 loaders give a barrier without one
	private static final String	DEFAULT_BARRIER_SURFACE	= "barrier";

	private Version3()
	{
	}

	/**
	 * Changes a version 3 track to version 4, writing out everything the version 4
	 * loaders would read differently:
	 * <ul>
	 * <li>the barrier comes only from the Main Track, 1 m high by default (0.6 m in v4):
	 *     segment barriers are removed.</li>
	 * <li>the banking type and the DoV factor of a segment aren't read: they are removed.</li>
	 * <li>in TORCS, a segment without a border height has a height of 0 (v4 inherits it).</li>
	 * <li>pits are built only for "track side" pits, and max pits isn't read. In TORCS
	 *     version 4 the pits are built whenever their segments are named, so the names
	 *     are removed from a track without pits.</li>
	 * <li>a surface without a rebound has a rebound of 1 (0.5 in v4): the track's own
	 *     surfaces get 1, and a Main Track barrier surface from the game's surfaces.xml
	 *     is copied into the track under a new name with a rebound of 1 (the game can't
	 *     read a track that defines a surface surfaces.xml has too). The road surfaces
	 *     from surfaces.xml can't be kept: see {@link #getReboundWarnings}.</li>
	 * </ul>
	 */
	public static void convertToVersion4(EditorFrame editorFrame)
	{
		TrackData trackData = editorFrame.getTrackData();
		if (trackData.getHeader().getVersion() != 3)
			return;

		boolean torcs = editorFrame.getGameType() == EditorFrame.GameType.TORCS;
		MainTrack mainTrack = trackData.getMainTrack();

		for (SegmentSide main : new SegmentSide[] {mainTrack.getLeft(), mainTrack.getRight()})
		{
			if (Double.isNaN(main.getBarrierHeight()))
				main.setBarrierHeight(1.0);
		}

		if (trackData.getSegments() != null)
		{
			double[] borderHeight = {height(mainTrack.getLeft()), height(mainTrack.getRight())};

			for (Segment segment : trackData.getSegments())
			{
				segment.setDovFactor(Double.NaN);

				SegmentSide[] sides = {segment.getLeft(), segment.getRight()};
				for (int i = 0; i < sides.length; i++)
				{
					SegmentSide side = sides[i];
					side.setSideBankingType(null);
					side.setBarrierStyle(null);
					side.setBarrierWidth(Double.NaN);
					side.setBarrierHeight(Double.NaN);
					side.setBarrierSurface(null);

					if (!Double.isNaN(side.getBorderHeight()))
					{
						borderHeight[i] = side.getBorderHeight();
					}
					else if (torcs && borderHeight[i] != 0.0)
					{
						side.setBorderHeight(0.0);
						borderHeight[i] = 0.0;
					}
				}
			}
		}

		Pits pits = editorFrame.getPits();
		if (pits.getStyle() != 1)
		{
			pits.setStyle(0);
			if (torcs)
			{
				pits.setEntry(null);
				pits.setExit(null);
				pits.setStart(null);
				pits.setEnd(null);
			}
		}
		pits.setMaxPits(Integer.MAX_VALUE);

		Vector<Surface> surfaces = trackData.getSurfaces();
		if (surfaces != null)
		{
			for (Surface surface : surfaces)
			{
				if (Double.isNaN(surface.getRebound()))
					surface.setRebound(1.0);
			}
		}

		keepBarrierRebound(editorFrame);

		trackData.getHeader().setVersion(4);
		editorFrame.documentIsModified = true;
	}

	/**
	 * Gives each Main Track barrier surface the version 3 rebound of 1 when it comes
	 * from surfaces.xml without one, by copying it into the track under a new name.
	 * A barrier surface defined nowhere is defined in the track with a rebound of 1.
	 */
	private static void keepBarrierRebound(EditorFrame editorFrame)
	{
		TrackData trackData = editorFrame.getTrackData();
		if (trackData.getHasSurfacesReference() && editorFrame.getDefaultSurfaces().isEmpty())
			return;	// surfaces.xml couldn't be read: getReboundWarnings says so

		MainTrack mainTrack = trackData.getMainTrack();
		List<String> from = new ArrayList<String>();
		List<String> to = new ArrayList<String>();

		for (SegmentSide main : new SegmentSide[] {mainTrack.getLeft(), mainTrack.getRight()})
		{
			String name = barrierSurface(main);
			int done = from.indexOf(name);
			if (done >= 0)
			{
				if (!to.get(done).equals(name))
					main.setBarrierSurface(to.get(done));
				continue;
			}

			String newName = name;
			if (find(trackData.getSurfaces(), name) == null)
			{
				Surface shared = sharedSurface(editorFrame, name);
				if (shared == null)
				{
					// unknown to the game too: it has the defaults, rebound 1 in v3
					Surface surface = new Surface();
					surface.setName(name);
					surface.setRebound(1.0);
					trackData.getSurfaces().add(surface);
				}
				else if (Double.isNaN(shared.getRebound()))
				{
					Surface surface = shared.copy();
					newName = uniqueName(editorFrame, name + "-v3");
					surface.setName(newName);
					surface.setRebound(1.0);
					surface.setComment("copy of " + name + " with the rebound of a version 3 track");
					trackData.getSurfaces().add(surface);
					main.setBarrierSurface(newName);
				}
			}
			from.add(name);
			to.add(newName);
		}
	}

	/**
	 * @return what converting the track to version 4 can't keep, for the user: the road,
	 *         side and border surfaces from surfaces.xml without a rebound go from 1 to
	 *         0.5 (cars bottoming out on them), and a barrier surface can't be checked
	 *         when surfaces.xml couldn't be read. Empty when nothing changes.
	 */
	public static List<String> getReboundWarnings(EditorFrame editorFrame)
	{
		List<String> warnings = new ArrayList<String>();
		TrackData trackData = editorFrame.getTrackData();
		if (trackData.getHeader().getVersion() != 3)
			return warnings;

		boolean sharedUnknown = trackData.getHasSurfacesReference() && editorFrame.getDefaultSurfaces().isEmpty();
		if (sharedUnknown)
		{
			warnings.add("The game's surfaces.xml couldn't be read (is the data directory set?), so surfaces from it " +
				"that have no rebound, including the barrier surface, go from a rebound of 1.0 to 0.5.");
			return warnings;
		}

		Set<String> road = new LinkedHashSet<String>();
		if (trackData.getSegments() != null)
		{
			for (Segment segment : trackData.getSegments())
			{
				road.add(segment.getValidSurface(editorFrame));
				for (boolean right : new boolean[] {false, true})
				{
					GameSides.State state = GameSides.of(segment, right, editorFrame);
					if (state.sideStartWidth > 0 || state.sideEndWidth > 0)
						road.add(state.sideSurface);
					if (state.borderWidth > 0)
						road.add(state.borderSurface);
				}
			}
		}

		List<String> losing = new ArrayList<String>();
		for (String name : road)
		{
			if (name == null || find(trackData.getSurfaces(), name) != null)
				continue;	// the track's own surfaces get a rebound of 1
			Surface shared = sharedSurface(editorFrame, name);
			if (shared == null || Double.isNaN(shared.getRebound()))
				losing.add(name);
		}
		if (!losing.isEmpty())
		{
			warnings.add("These surfaces have no rebound, so it goes from 1.0 to 0.5 (it matters when a car bottoms out " +
				"on them): " + String.join(", ", losing) + ".");
		}
		return warnings;
	}

	private static String barrierSurface(SegmentSide main)
	{
		String name = main.getBarrierSurface();
		return name == null || name.isEmpty() ? DEFAULT_BARRIER_SURFACE : name;
	}

	/** @return the surfaces.xml surface the track reads, or null when it has none of that name */
	private static Surface sharedSurface(EditorFrame editorFrame, String name)
	{
		if (!editorFrame.getTrackData().getHasSurfacesReference())
			return null;
		return find(editorFrame.getDefaultSurfaces(), name);
	}

	private static Surface find(Vector<Surface> surfaces, String name)
	{
		if (surfaces == null)
			return null;
		for (Surface surface : surfaces)
		{
			if (name.equals(surface.getName()))
				return surface;
		}
		return null;
	}

	private static String uniqueName(EditorFrame editorFrame, String base)
	{
		String name = base;
		for (int i = 2; find(editorFrame.getTrackData().getSurfaces(), name) != null || find(editorFrame.getDefaultSurfaces(), name) != null; i++)
			name = base + "-" + i;
		return name;
	}

	private static double height(SegmentSide main)
	{
		return Double.isNaN(main.getBorderHeight()) ? 0.0 : main.getBorderHeight();
	}
}
