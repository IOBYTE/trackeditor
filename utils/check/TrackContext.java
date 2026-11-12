/*
 *   TrackContext.java
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

import java.util.Vector;

import utils.GameType;
import utils.TrackData;
import utils.circuit.GraphicObject;
import utils.circuit.ObjectMap;
import utils.circuit.Pits;
import utils.circuit.Reliefs;
import utils.circuit.Segment;
import utils.circuit.Surface;
import utils.circuit.TerrainGeneration;
import utils.circuit.TrackObject;

/**
 * What the track model, the track writer and the checks need from the editor: the
 * track and the settings of the game it's edited for. The editor's window is one;
 * a command line checker can be another, without a window.
 */
public interface TrackContext
{
	/** @return the track */
	TrackData getTrackData();

	/** @return the game the track is edited for */
	GameType getGameType();

	/** @return the game's data directory, or null when not set */
	String getDataDirectory();

	/** @return the game's default surfaces, from its data/tracks/surfaces.xml */
	Vector<Surface> getDefaultSurfaces();

	/** @return the game's default objects, from its data/tracks/objects.xml */
	Vector<TrackObject> getDefaultObjects();

	/** @return whether objects of the game's default objects are checked too */
	boolean getCheckDefaultObjects();

	/**
	 * @return the folders the game finds the track's own textures and 3D model in, in
	 *         the order it looks, then the other folders given that aren't null
	 */
	String[] getTrackFolders(String... more);

	/** @return the track's pits */
	default Pits getPits()
	{
		return getTrackData().getMainTrack().getPits();
	}

	/** @return the track's reliefs */
	default Reliefs getReliefs()
	{
		return getTrackData().getReliefs();
	}

	/** @return the track's terrain generation */
	default TerrainGeneration getTerrainGeneration()
	{
		return getTrackData().getTerrainGeneration();
	}

	/** @return the track's object maps */
	default Vector<ObjectMap> getObjectMaps()
	{
		return getTrackData().getObjectMaps();
	}

	/** @return the track's graphic objects */
	default Vector<GraphicObject> getGraphicObjects()
	{
		return getTrackData().getGraphicObjects();
	}

	/** @return the track's segment of that name, or null */
	default Segment getSegment(String name)
	{
		return getTrackData().getSegments().getSegment(name);
	}

	/** @return whether the track is edited for Cars Sports Racing */
	default boolean getCarsSportsRacing()
	{
		return getGameType() == GameType.CARS_SPORTS_RACING;
	}
}
