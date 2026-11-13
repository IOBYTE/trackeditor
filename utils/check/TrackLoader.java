/*
 *   TrackLoader.java
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.jdom2.JDOMException;

import plugin.torcs.XmlReader;
import plugin.torcs.XmlWriter;
import utils.GameType;
import utils.Properties;
import utils.TrackData;
import utils.circuit.ObjShapeObject;
import utils.circuit.ObjectMap;
import utils.circuit.Segment;
import utils.circuit.Surface;
import utils.circuit.TrackObject;

/**
 * Reads a track file into a track, as the editor opens it, without a window:
 * the file, then what saving won't keep from it and the values it changes,
 * which Check lists. The editor asks what to do in between (a version 5 track
 * for TORCS); load reads a track for checking without the editor.
 */
public class TrackLoader
{
	private static final String	sep	= System.getProperty("file.separator");

	private final TrackContext	trackContext;
	private final ReadNotices	notices;
	private XmlReader			xmlReader	= null;

	/**
	 * @param trackContext the track to read into: its track data, new for the file,
	 *        and the game and default objects it is read with
	 * @param notices where the problems the user has to know at once go
	 */
	public TrackLoader(TrackContext trackContext, ReadNotices notices)
	{
		this.trackContext = trackContext;
		this.notices = notices;
	}

	/**
	 * Reads the file into the track data, naming the object maps' objects.
	 *
	 * @return the values the game reads differently than they are written, to tell
	 *         the user about
	 */
	public List<String> read(File file) throws JDOMException, IOException
	{
		xmlReader = new XmlReader(trackContext, notices);
		xmlReader.readXml(file.getAbsolutePath());

		Vector<ObjectMap> objectMaps = trackContext.getTrackData().getObjectMaps();
		for (int i = 0; i < objectMaps.size(); i++)
		{
			Vector<ObjShapeObject> objects = objectMaps.get(i).getObjects();

			for (int j = 0; j < objects.size(); j++)
			{
				objects.get(j).setName(objectColorName(trackContext, objects.get(j).getRGB()));
			}
		}
		return xmlReader.getWarnings();
	}

	/**
	 * Keeps what saving won't keep from the file just read, and the values it
	 * changes, in the track data for Check (see TrackData.getUnusedKeys), as saving
	 * the track would write it now.
	 */
	public void findSavingChanges()
	{
		// what saving won't keep from the file, and the values it changes (shown
		// when the track was read too), for the Check dialog, in file order
		// with where each message is from, so the Check dialog can show it
		java.util.Map<String, String> paths = new java.util.HashMap<String, String>();
		List<String> unusedKeys = findUnusedKeys(paths);
		for (String warning : xmlReader.getValueWarnings())
		{
			String message = warning.startsWith("line ") ? "Line " + warning.substring("line ".length()) : warning;
			unusedKeys.add(message);
			if (xmlReader.getWarningPath(warning) != null)
				paths.put(message, xmlReader.getWarningPath(warning));
			addFileText(warning);
		}
		Collections.sort(unusedKeys, Comparator.comparingInt(TrackLoader::messageLine));
		trackContext.getTrackData().setUnusedKeys(unusedKeys);
		trackContext.getTrackData().setUnusedKeyPaths(paths);
	}

	/**
	 * Reads a track for checking, as the editor opens it without its project: the
	 * game's default surfaces and objects, then the track. A version 5 track isn't
	 * converted for TORCS, as the editor offers to; a notice says so.
	 *
	 * @param game the game the track is read for
	 * @param dataDirectory the game's data directory, or null
	 * @param localDirectory Speed Dreams' local directory, or null for its standard
	 *        one (see TrackFolders.localDirectory)
	 * @param notices where the problems the user has to know at once go
	 * @return the track, ready to check (see TrackChecker)
	 */
	public static LoadedTrack load(File file, GameType game, String dataDirectory, String localDirectory, ReadNotices notices)
		throws JDOMException, IOException
	{
		LoadedTrack settings = new LoadedTrack(game, dataDirectory, TrackFolders.localDirectory(game, localDirectory));
		readDefaults(settings, notices);
		return load(file, settings, notices);
	}

	/**
	 * Reads the game's default surfaces and objects into the track's, to read them
	 * once for many tracks (see load).
	 */
	public static void readDefaults(LoadedTrack track, ReadNotices notices)
	{
		readDefaultSurfaces(track, track.getDataDirectory(), track.getDefaultSurfaces(), notices);
		readDefaultObjects(track, track.getDataDirectory(), track.getDefaultObjects(), notices);
	}

	/**
	 * Reads a track for checking, as the other load does, with the game, directories,
	 * default surfaces and objects and settings of another track, read once for
	 * many tracks (see readDefaults).
	 *
	 * @param settings the game, directories, default surfaces and objects and
	 *        settings: it isn't changed
	 */
	public static LoadedTrack load(File file, LoadedTrack settings, ReadNotices notices) throws JDOMException, IOException
	{
		GameType game = settings.getGameType();
		// no project: like importing a track file, the settings start new
		Properties properties = new Properties();
		properties.setPath(file.getAbsoluteFile().getParent());
		Properties.setInstance(properties);

		LoadedTrack track = new LoadedTrack(game, settings.getDataDirectory(), settings.getLocalDirectory());
		track.getDefaultSurfaces().addAll(settings.getDefaultSurfaces());
		track.getDefaultObjects().addAll(settings.getDefaultObjects());
		track.setCheckDefaultObjects(settings.getCheckDefaultObjects());

		track.setTrackData(new TrackData());
		TrackLoader loader = new TrackLoader(track, notices);
		track.setNumberWarnings(loader.read(file));
		if (game == GameType.TORCS && track.getTrackData().getHeader().getVersion() == 5)
		{
			notices.notice("Track Version", file.getName() + " is a version 5 track. TORCS can't load version 5 tracks: "
				+ "the editor offers to convert it to version 4.", false);
		}
		loader.findSavingChanges();
		return track;
	}

	/**
	 * Reads the game's default surfaces (data/tracks/surfaces.xml in its data
	 * directory) into the vector, when the data directory is set.
	 */
	public static void readDefaultSurfaces(TrackContext trackContext, String dataDirectory, Vector<Surface> surfaces, ReadNotices notices)
	{
		if (dataDirectory != null && !dataDirectory.isEmpty())
		{
			String defaultSurfaceFile = dataDirectory + sep + "data" + sep + "tracks" + sep + "surfaces.xml";
			try
			{
				XmlReader xmlreader = new XmlReader(trackContext, notices);
				xmlreader.readDefaultSurfaces(defaultSurfaceFile, surfaces);
			}
			catch (Exception e)
			{
				notices.notice("Opening Default Surfaces", e.getLocalizedMessage(), true);
			}
		}
	}

	/**
	 * Reads the game's default objects (data/tracks/objects.xml in its data
	 * directory) into the vector, when the data directory is set.
	 */
	public static void readDefaultObjects(TrackContext trackContext, String dataDirectory, Vector<TrackObject> objects, ReadNotices notices)
	{
		if (dataDirectory != null && !dataDirectory.isEmpty())
		{
			String defaultObjectFile = dataDirectory + sep + "data" + sep + "tracks" + sep + "objects.xml";
			try
			{
				XmlReader xmlreader = new XmlReader(trackContext, notices);
				xmlreader.readDefaultObjects(defaultObjectFile, objects);
			}
			catch (Exception e)
			{
				notices.notice("Opening Default Objects", e.getLocalizedMessage(), true);
			}
		}
	}

	/**
	 * @return the name of the track's object, or else the default object, an object
	 *         map's color stands for, or null
	 */
	public static String objectColorName(TrackContext trackContext, int rgb)
	{
		Vector<TrackObject>	trackObjects = trackContext.getTrackData().getTrackObjects();
		Vector<TrackObject>	defaultObjects = trackContext.getDefaultObjects();

		for (int i = 0; i < trackObjects.size(); i++)
		{
			if (trackObjects.get(i).getColor() == rgb)
			{
				return trackObjects.get(i).getName();
			}
		}

		for (int i = 0; i < defaultObjects.size(); i++)
		{
			if (defaultObjects.get(i).getColor() == rgb)
			{
				return defaultObjects.get(i).getName();
			}
		}

		return null;
	}

	/**
	 * @return the keys and sections in the file just read that saving won't keep, as
	 *         messages: the ones the editor doesn't read (or skips, like the game), and
	 *         the ones it reads that it doesn't write for this game (the game doesn't
	 *         use them)
	 * @param paths gets the path in the file of what each message is about
	 */
	private List<String> findUnusedKeys(java.util.Map<String, String> paths)
	{
		List<String> messages = new ArrayList<String>();
		for (XmlReader.FileKey key : xmlReader.getUnreadKeys())
		{
			String what = key.section ? "section " + key.path + " (" + key.keys + (key.keys == 1 ? " key" : " keys") + ")" : key.path;
			String message;
			if ("attstr".equals(key.wrongType))
				message = lineText(key) + what + " is text (attstr) but is read as a number (attnum), so the game doesn't use it and saving removes it";
			else if ("attnum".equals(key.wrongType))
				message = lineText(key) + what + " is a number (attnum) but is read as text (attstr), so the game doesn't use it and saving removes it";
			else if (key.reason != null)
				message = lineText(key) + what + ": " + key.reason;
			else
				message = lineText(key) + what + ": the editor doesn't read it, so saving removes it";
			messages.add(message);
			paths.put(message, key.path);
		}
		try
		{
			java.util.Set<String> written = XmlReader.getKeyPaths(new XmlWriter(trackContext).getDocument(""));
			for (XmlReader.FileKey key : xmlReader.getReadKeys())
			{
				if (!written.contains(key.path) && !writtenAsOther(key.path, written))
				{
					String message = lineText(key) + key.path + ": " + trackContext.getGameType().getName() + " doesn't use it, so saving for " +
						trackContext.getGameType().getShortName() + " removes it";
					messages.add(message);
					paths.put(message, key.path);
				}
			}
		}
		catch (Exception e)
		{
			// only this part of the check is lost
			e.printStackTrace();
		}
		return messages;
	}

	/**
	 * @return whether the writer keeps the key under another name in its section: a
	 *         side's equal start and end widths become "width" (and a TORCS end width
	 *         "width" can become "end width"), and the pits' "type" of some TORCS tracks
	 *         becomes "pit style", and a curve's end radius equal to its start radius
	 *         is left out
	 */
	private static boolean writtenAsOther(String path, java.util.Set<String> written)
	{
		int slash = path.lastIndexOf('/');
		if (slash < 0)
			return false;
		String section = path.substring(0, slash + 1);
		String name = path.substring(slash + 1);
		String[] others;
		switch (name)
		{
		case "start width":
		case "end width":
			others = new String[] {"width"};
			break;
		case "width":
			others = new String[] {"start width", "end width"};
			break;
		case "type":
			others = section.endsWith("Pits/") ? new String[] {"pit style"} : new String[0];
			break;
		case "end radius":
			// left out when it's the start radius, which the game uses then
			others = new String[] {"radius"};
			break;
		default:
			others = new String[0];
		}
		for (String other : others)
		{
			if (written.contains(section + other))
				return true;
		}
		return false;
	}

	/**
	 * Keeps a value the file has as text the game reads as another number, for the
	 * segment dialog or the Properties dialog to show (see TrackData.FileText).
	 */
	private void addFileText(String warning)
	{
		TrackData trackData = trackContext.getTrackData();
		String text = xmlReader.getWarningText(warning);
		String path = xmlReader.getWarningPath(warning);
		String[] place = TrackData.segmentPlace(path);
		if (text == null)
			return;
		String message = warning.replaceFirst("^line \\d+: ", "");
		if (place == null)
		{
			// outside the segments, with the number the game reads (a field showing
			// another number, in another unit, doesn't show it): in a light by the
			// light (the first with its name, as the game finds it), else by its path
			if (path == null)
				return;
			TrackData.FileText fileText = new TrackData.FileText(text, plugin.torcs.GfParmNumbers.parse(text, new boolean[1]), message);
			String[] parts = path.split("/", 3);
			if (parts.length == 3 && parts[0].equals("Track Lights"))
			{
				for (utils.circuit.TrackLight light : trackData.getTrackLights())
				{
					if (parts[1].equals(light.getName()))
					{
						trackData.addFileText(light, parts[2], fileText);
						return;
					}
				}
			}
			trackData.addFileText(null, path, fileText);
			return;
		}
		if (place.length < 3)
			return;
		Segment segment = trackData.getSegments().getSegmentFromName(place[0]);
		if (segment == null)
			return;
		for (int i = 2; i < place.length; i++)
		{
			String field = place[i];
			int slash = field.indexOf('/');
			Object owner = slash < 0 ? segment : field.startsWith("Left/") ? segment.getLeft() : segment.getRight();
			try
			{
				Object value = owner.getClass().getMethod("get" + field.substring(slash + 1)).invoke(owner);
				trackData.addFileText(segment, field, new TrackData.FileText(text, ((Number) value).doubleValue(), message));
			}
			catch (Exception e)
			{
				// a field the segment doesn't have (an arc on a straight)
			}
		}
	}

	private static String lineText(XmlReader.FileKey key)
	{
		return key.line > 0 ? "Line " + key.line + ": " : "";
	}

	/** @return the line a message starting with lineText is about, after all lines if none */
	private static int messageLine(String message)
	{
		java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("^Line (\\d+): ").matcher(message);
		return matcher.find() ? Integer.parseInt(matcher.group(1)) : Integer.MAX_VALUE;
	}
}
