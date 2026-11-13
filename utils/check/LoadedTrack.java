/*
 *   LoadedTrack.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import utils.GameType;
import utils.Properties;
import utils.TrackData;
import utils.circuit.Surface;
import utils.circuit.TrackObject;

/**
 * A track read without the editor (see TrackLoader.load), with what checking it
 * needs: the game, its directories and its default surfaces and objects. The
 * track's folder is the path in Properties, as in the editor.
 */
public class LoadedTrack implements TrackContext
{
	private final GameType				game;
	private final String				dataDirectory;
	private final String				localDirectory;
	private final Vector<Surface>		defaultSurfaces		= new Vector<Surface>();
	private final Vector<TrackObject>	defaultObjects		= new Vector<TrackObject>();
	private boolean						checkDefaultObjects	= false;
	private TrackData					trackData			= null;
	private List<String>				numberWarnings		= new ArrayList<String>();

	/**
	 * @param dataDirectory the game's data directory, or null
	 * @param localDirectory the game's local directory, or null (see TrackFolders.localDirectory)
	 */
	public LoadedTrack(GameType game, String dataDirectory, String localDirectory)
	{
		this.game = game;
		this.dataDirectory = dataDirectory;
		this.localDirectory = localDirectory;
	}

	public TrackData getTrackData()
	{
		return trackData;
	}

	void setTrackData(TrackData trackData)
	{
		this.trackData = trackData;
	}

	public GameType getGameType()
	{
		return game;
	}

	public String getDataDirectory()
	{
		return dataDirectory;
	}

	/** @return the game's local directory, or null */
	public String getLocalDirectory()
	{
		return localDirectory;
	}

	public Vector<Surface> getDefaultSurfaces()
	{
		return defaultSurfaces;
	}

	public Vector<TrackObject> getDefaultObjects()
	{
		return defaultObjects;
	}

	public boolean getCheckDefaultObjects()
	{
		return checkDefaultObjects;
	}

	/** Sets whether the default objects' models are checked too, as the editor's preference does */
	public void setCheckDefaultObjects(boolean checkDefaultObjects)
	{
		this.checkDefaultObjects = checkDefaultObjects;
	}

	public String[] getTrackFolders(String... more)
	{
		String path = Properties.getInstance().getPath();
		return TrackFolders.trackFolders(path, TrackFolders.installedTrackFolder(localDirectory, dataDirectory, path), more);
	}

	public String getTrackPlacementProblem()
	{
		return TrackFolders.placementProblem(game, TrackFolders.tracksDirectories(localDirectory, dataDirectory),
			Properties.getInstance().getPath(), trackData);
	}

	/** @return the values the game reads differently than they are written, found reading the track */
	public List<String> getNumberWarnings()
	{
		return numberWarnings;
	}

	void setNumberWarnings(List<String> numberWarnings)
	{
		this.numberWarnings = numberWarnings;
	}
}
