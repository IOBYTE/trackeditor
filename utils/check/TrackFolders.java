/*
 *   TrackFolders.java
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import utils.GameType;
import utils.TrackData;

/**
 * Where a game lists tracks from and finds a track's own files: the data
 * directory's tracks folder, and for Speed Dreams its local one too.
 */
public final class TrackFolders
{
	private static final String	sep	= System.getProperty("file.separator");

	private TrackFolders()
	{
	}

	/**
	 * @param setting the local directory set for the game, or null
	 * @return the game's local directory, where it keeps the user's settings and
	 *         tracks: the one set, or the game's standard one when that exists; null
	 *         for a game without one (see GameType.hasLocalTracks)
	 */
	public static String localDirectory(GameType game, String setting)
	{
		if (!game.hasLocalTracks())
			return null;
		if (setting != null && !setting.isEmpty())
			return setting;
		File standard = game.defaultLocalDirectory();
		return standard != null && standard.isDirectory() ? standard.getPath() : null;
	}

	/**
	 * @return the folders a game lists tracks from, as
	 *         <folder>/<category>/<name>/<name>.xml: the local directory's tracks
	 *         first, then the data directory's. Empty when neither is set.
	 */
	public static List<File> tracksDirectories(String localDirectory, String dataDirectory)
	{
		List<File> folders = new ArrayList<File>();
		if (localDirectory != null)
			folders.add(new File(localDirectory, "tracks"));
		if (dataDirectory != null && !dataDirectory.isEmpty())
			folders.add(new File(dataDirectory, "tracks"));
		return folders;
	}

	/**
	 * @param tracks the folders the game lists tracks from (see tracksDirectories)
	 * @param path the track's folder
	 * @return why the game won't list the track where it is, or null: games list
	 *         <tracks folder>/<category>/<name>, taking the category from the
	 *         folder. Null when there are no tracks folders or no track.
	 */
	public static String placementProblem(GameType game, List<File> tracks, String path, TrackData trackData)
	{
		if (tracks.isEmpty() || trackData == null)
			return null;
		File folder = new File(path);
		File categoryFolder = folder.getAbsoluteFile().getParentFile();
		File tracksFolder = categoryFolder == null ? null : categoryFolder.getParentFile();
		boolean listed = false;
		for (File candidate : tracks)
			listed |= tracksFolder != null && sameFile(tracksFolder, candidate);
		if (!listed)
		{
			StringBuilder where = new StringBuilder();
			for (File candidate : tracks)
				where.append(where.length() == 0 ? "" : " or ").append(candidate.getPath());
			return game.getName() + " won't list the track: it reads tracks from " + where + ", as <category>" + sep + "<name>,"
				+ " and this track is in " + folder.getPath();
		}
		String category = trackData.getHeader().getCategory();
		if (category != null && !category.isEmpty() && !category.equals(categoryFolder.getName()))
			return "Category " + category + " isn't the folder the track is in, " + categoryFolder.getName() + ": " + game.getName()
				+ " lists the track under " + categoryFolder.getName();
		return null;
	}

	/**
	 * @param path the track's folder
	 * @return the installed track of the same category and name, when the track is in
	 *         the local tracks folder and there is one, or null. Speed Dreams'
	 *         graphics look for a track's textures and 3D model in its local folder,
	 *         then in the installed track's.
	 */
	public static String installedTrackFolder(String localDirectory, String dataDirectory, String path)
	{
		if (localDirectory == null || dataDirectory == null || dataDirectory.isEmpty() || path == null || path.isEmpty())
			return null;
		File folder = new File(path).getAbsoluteFile();
		File category = folder.getParentFile();
		if (category != null && category.getParentFile() != null && sameFile(category.getParentFile(), new File(localDirectory, "tracks")))
		{
			File installed = new File(new File(new File(dataDirectory, "tracks"), category.getName()), folder.getName());
			if (installed.isDirectory() && !sameFile(installed, folder))
				return installed.getPath();
		}
		return null;
	}

	/**
	 * @param path the track's folder
	 * @param installed the installed track's folder, or null (see installedTrackFolder)
	 * @return the folders the game finds the track's own textures and 3D model in, in
	 *         the order it looks: the track's folder, then the installed track's, then
	 *         the other folders given that aren't null
	 */
	public static String[] trackFolders(String path, String installed, String... more)
	{
		List<String> folders = new ArrayList<String>();
		folders.add(path);
		if (installed != null)
			folders.add(installed);
		for (String folder : more)
		{
			if (folder != null)
				folders.add(folder);
		}
		return folders.toArray(new String[folders.size()]);
	}

	/** @return whether both are the same file or folder, false when either doesn't exist */
	public static boolean sameFile(File a, File b)
	{
		try
		{
			return Files.isSameFile(a.toPath(), b.toPath());
		}
		catch (IOException | SecurityException e)
		{
			// not the same: one of them doesn't exist
			return false;
		}
	}
}
