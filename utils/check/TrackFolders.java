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
	 * @param trackFolder the track's folder
	 * @return the data folder of a track in a data folder's tracks folder,
	 *         &lt;data&gt;/tracks/&lt;category&gt;/&lt;name&gt;: the folder three up
	 *         from the track's, when it has data/tracks/surfaces.xml. That's where
	 *         the game finds the default surfaces and objects the track includes
	 *         (../../../data/tracks/surfaces.xml), so it needs no data folder set.
	 *         Null for a track somewhere else.
	 */
	public static String dataDirectoryOf(File trackFolder)
	{
		if (trackFolder == null)
			return null;
		// without . and .. in it, which would be counted as folders
		File category = trackFolder.getAbsoluteFile().toPath().normalize().toFile().getParentFile();
		File tracks = category == null ? null : category.getParentFile();
		File data = tracks == null ? null : tracks.getParentFile();
		if (!isDataFolder(data))
			return null;
		return data.toPath().normalize().toString();
	}

	/**
	 * @return whether the folder is a game's data folder: it has
	 *         data/tracks/surfaces.xml, the default surfaces every track includes
	 */
	public static boolean isDataFolder(File folder)
	{
		return folder != null && new File(folder, "data" + sep + "tracks" + sep + "surfaces.xml").isFile();
	}

	/** What's wrong with a folder given as a game's data folder, and the folder meant when that can be told */
	public static final class DataFolderProblem
	{
		/** what's wrong, as a sentence */
		public final String	text;
		/** the data folder that was likely meant, or null */
		public final String	suggestion;

		DataFolderProblem(String text, File suggestion)
		{
			this.text = text;
			this.suggestion = suggestion == null ? null : suggestion.getPath();
		}
	}

	/**
	 * Tells a data folder that is one by its layout: &lt;data&gt;/data/tracks/surfaces.xml,
	 * and &lt;data&gt;/tracks with the tracks. The usual mistakes are a folder too high
	 * (the one with the data folder in it), too low (the data folder's own data
	 * folder, or its tracks folder) or a track's folder.
	 *
	 * @return what's wrong with the folder as a data folder, or null when it is one
	 */
	public static DataFolderProblem dataFolderProblem(File folder)
	{
		folder = folder.getAbsoluteFile().toPath().normalize().toFile();
		if (!folder.exists())
			return new DataFolderProblem(folder + " doesn't exist.", null);
		if (!folder.isDirectory())
			return new DataFolderProblem(folder + " isn't a folder.", null);
		String objects = "data" + sep + "tracks" + sep + "objects.xml";
		if (isDataFolder(folder))
		{
			// the games ship both, and a track that includes a missing one can't be read
			if (!new File(folder, objects).isFile())
			{
				return new DataFolderProblem(folder + " has no " + objects + ", the default objects that tracks include. "
					+ "The game needs it too, so restore it from the game's files.", null);
			}
			return null;
		}
		File inner = new File(folder, "data");
		if (isDataFolder(inner))
			return new DataFolderProblem(folder + " isn't the data folder, but " + inner + " in it is.", inner);
		// its data or tracks folder, or a category's or track's folder in it
		File up = folder.getParentFile();
		for (int level = 0; level < 4 && up != null; level++, up = up.getParentFile())
		{
			if (isDataFolder(up))
				return new DataFolderProblem(folder + " is inside the data folder " + up + ".", up);
		}
		String surfaces = "data" + sep + "tracks" + sep + "surfaces.xml";
		if (new File(folder, "tracks").isDirectory() && new File(folder, "data" + sep + "tracks").isDirectory())
		{
			return new DataFolderProblem(folder + " has no " + surfaces + ", the default surfaces every track includes. "
				+ "The game needs it too, so restore it from the game's files.", null);
		}
		return new DataFolderProblem(folder + " doesn't look like a data folder because it has no " + surfaces + ".", null);
	}

	/**
	 * @return the default files the track includes that the game won't find,
	 *         "surfaces.xml" and "objects.xml", none when there are none. A track
	 *         includes them as ../../../data/tracks/surfaces.xml and objects.xml,
	 *         which the game reads from the track's folder, or for a Speed Dreams
	 *         track in its local tracks folder from the data folder. Without them
	 *         it can't read the track at all (params.cpp, xmlExternalEntityRefHandler
	 *         in each game).
	 * @param trackPath the track's folder
	 */
	/** @return whether the track's folder is in the local directory's tracks folder (Speed Dreams') */
	public static boolean isLocalTrack(String localDirectory, String trackPath)
	{
		if (localDirectory == null || trackPath == null)
			return false;
		File category = new File(trackPath).getAbsoluteFile().toPath().normalize().toFile().getParentFile();
		File tracks = category == null ? null : category.getParentFile();
		return tracks != null && sameFile(tracks, new File(localDirectory, "tracks"));
	}

	public static List<String> missingSharedFiles(String localDirectory, String dataDirectory, String trackPath, TrackData trackData)
	{
		List<String> missing = new ArrayList<String>();
		boolean surfaces = trackData.getHasSurfacesReference();
		boolean objects = trackData.getHasObjectsReference();
		if ((!surfaces && !objects) || trackPath == null)
			return missing;
		File folder = new File(trackPath).getAbsoluteFile().toPath().normalize().toFile();
		File shared = new File(folder, ".." + sep + ".." + sep + ".." + sep + "data" + sep + "tracks").toPath().normalize().toFile();
		if (isLocalTrack(localDirectory, trackPath))
		{
			if (dataDirectory == null || dataDirectory.isEmpty())
				return missing;
			shared = new File(dataDirectory, "data" + sep + "tracks");
		}
		if (surfaces && !new File(shared, "surfaces.xml").isFile())
			missing.add("surfaces.xml");
		if (objects && !new File(shared, "objects.xml").isFile())
			missing.add("objects.xml");
		return missing;
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
