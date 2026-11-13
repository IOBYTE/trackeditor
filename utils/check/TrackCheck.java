/*
 *   TrackCheck.java
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
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.GameType;
import utils.Properties;

/**
 * Checks tracks without the editor, as its Check does, for a command line or a CI
 * job: java -jar trackeditor.jar check --help, or java -cp trackeditor.jar
 * utils.check.TrackCheck --help
 *
 * The game and its folders come from the options, or else from the editor's
 * preferences (not with --no-prefs). Tracks are files, patterns the checker
 * expands itself (only <name>/<name>.xml), and @lists of them.
 *
 * Exit codes: 0 no messages (or only ones in the baseline), 1 new messages, 2 the
 * check couldn't run (a bad option, no tracks found, a track that can't be read,
 * trackgen failing).
 */
public final class TrackCheck
{
	/** Exit code: no messages, or only ones in the baseline */
	public static final int		OK				= 0;
	/** Exit code: new messages */
	public static final int		MESSAGES		= 1;
	/** Exit code: the check couldn't run */
	public static final int		FAILED			= 2;

	// how it's run when run as this class (the editor's jar runs it as "check", see TrackEditor)
	private static final String	COMMAND			= "java -cp trackeditor.jar utils.check.TrackCheck";
	private static final String	BASELINE_HEADER	= "# Track editor check baseline: one message per line, the track file, a tab, then the message";

	// the editor's preferences (gui.EditorFrame's package), and the keys it uses
	private static final String	PREFERENCES_NODE		= "/gui";
	private static final String	GAME_TYPE				= "GameType";
	private static final String	CARS_SPORTS_RACING		= "CarsSportsRacing";
	private static final String	CHECK_DEFAULT_OBJECTS	= "CheckDefaultObjects";
	private static final String[]	DIRECTORY_KEYS		= { "DataDirectory", "BinDirectory", "LibDirectory", "LocalDirectory" };

	// a message about a line of the file, as Check says it, and as reading says it
	private static final Pattern	LINE		= Pattern.compile("^Line (\\d+): ");
	private static final Pattern	READ_LINE	= Pattern.compile("^line (\\d+): ");

	private static final boolean	WINDOWS		= File.separatorChar == '\\';

	// Java's preferences log "Created user preferences directory" on a computer
	// without any, like a CI one: kept so its level stays set
	private static final java.util.logging.Logger	PREFERENCES_LOG	= java.util.logging.Logger.getLogger("java.util.prefs");

	private enum Format
	{
		TEXT, GITHUB
	}

	/** The kinds of messages Check hides at first */
	private enum Kind
	{
		SAVING, PNG, OBJECTS
	}

	/** How checking a track went */
	private enum Result
	{
		CHECKED, NOT_READ, NOT_CHECKED, CLOSURE_NOT_CHECKED
	}

	/** A message to report, about a track file, or about all of them when the file is "" */
	private static final class Report
	{
		final String	file;
		final int		line;
		final String	title;
		final String	text;
		final boolean	error;

		Report(String file, int line, String title, String text, boolean error)
		{
			this.file = file;
			this.line = line;
			this.title = title;
			this.text = text;
			this.error = error;
		}

		/** @return what a baseline matches: the file and the message, without its line */
		String key()
		{
			return file + "\t" + (title == null ? "" : title + ": ") + text;
		}
	}

	/** A track file or pattern from the command line or a list, as given and where it is */
	private static final class Input
	{
		// as given, for messages
		final String	text;
		// the path to use: relative to the list's folder for a list's entries
		final String	path;
		// after --: only a file, not a pattern or a list
		final boolean	literal;

		Input(String text, String path, boolean literal)
		{
			this.text = text;
			this.path = path;
			this.literal = literal;
		}
	}

	/** A track to check: its file, and its name in the messages */
	private static final class Track
	{
		final File		file;
		final String	name;

		Track(File file, String name)
		{
			this.file = file;
			this.name = name;
		}
	}

	// the options as given (null when not)
	private String					gameOption		= null;
	private String					dataOption		= null;
	private String					localOption		= null;
	private String					binOption		= null;
	private boolean					noPrefs			= false;
	private boolean					checkDefaultObjectsOption	= false;
	private boolean					trackgen		= false;
	// how long trackgen may take for a track before it's stopped, 0 for as long as it takes
	private long					trackgenTimeout	= 300;
	private final Set<Kind>			show			= EnumSet.noneOf(Kind.class);
	private Format					format			= Format.TEXT;
	private String					baseline		= null;
	private String					writeBaseline	= null;
	private boolean					verbose			= false;
	private final List<Input>		inputs			= new ArrayList<Input>();

	// the settings used, and where each came from (for --verbose)
	private GameType				game			= null;
	private String					dataDirectory	= null;
	private String					localDirectory	= null;
	private String					binDirectory	= null;
	private boolean					checkDefaultObjects	= false;
	private final List<String>		settings		= new ArrayList<String>();
	// the editor's preferences and game, null when not used
	private Preferences				preferences		= null;
	private GameType				editorGame		= null;

	private final List<Track>		tracks			= new ArrayList<Track>();
	private int						skipped			= 0;

	private PrintStream				out;
	private final PrintStream		err;
	// how it was run, for --help
	private final String			command;

	private TrackCheck(String command, PrintStream out, PrintStream err)
	{
		this.command = command;
		this.out = out;
		this.err = err;
	}

	public static void main(String[] args)
	{
		// no window, ever: also on a computer without a display
		System.setProperty("java.awt.headless", "true");
		System.exit(run(args, System.out, System.err));
	}

	/**
	 * Checks the tracks the arguments give.
	 *
	 * @param out where the messages go
	 * @param err where problems running the check go, and --verbose's settings
	 * @return the exit code: OK, MESSAGES or FAILED
	 */
	public static int run(String[] args, PrintStream out, PrintStream err)
	{
		return run(COMMAND, args, out, err);
	}

	/**
	 * Checks the tracks the arguments give.
	 *
	 * @param command how it was run, as --help shows it, like java -jar trackeditor.jar check
	 * @param out where the messages go
	 * @param err where problems running the check go, and --verbose's settings
	 * @return the exit code: OK, MESSAGES or FAILED
	 */
	public static int run(String command, String[] args, PrintStream out, PrintStream err)
	{
		TrackCheck check = new TrackCheck(command, out, err);
		try
		{
			try
			{
				Integer done = check.parse(args);
				if (done != null)
					return done;
				check.settings();
				check.findTracks();
				check.gameHint();
			}
			catch (UsageException e)
			{
				err.println(e.getMessage());
				err.println("Try " + check.command + " --help for the options.");
				return FAILED;
			}
			catch (java.nio.file.InvalidPathException e)
			{
				// a list's entry or a pattern that can't be a path here
				err.println("Not a path here: " + e.getMessage());
				return FAILED;
			}
			catch (IllegalArgumentException e)
			{
				// an option's value that can't be used, with why: not a bug
				if (e.getMessage() == null || e.getClass() != IllegalArgumentException.class)
					throw e;
				err.println(e.getMessage());
				return FAILED;
			}
			return check.check();
		}
		catch (RuntimeException | Error e)
		{
			// a bug: say so, rather than end with Java's exit status 1, which means messages
			err.println("The check failed: " + bug(e));
			e.printStackTrace(err);
			return FAILED;
		}
	}

	/** @return what went wrong, and where in the checker, to report as a bug */
	private static String bug(Throwable e)
	{
		StackTraceElement[] trace = e.getStackTrace();
		return e + (trace.length == 0 ? "" : " at " + trace[0]) + " (a bug in the checker: please report it)";
	}

	/** A mistake in the options, which --help explains */
	private static final class UsageException extends IllegalArgumentException
	{
		private static final long	serialVersionUID	= 1L;

		UsageException(String message)
		{
			super(message);
		}
	}

	/**
	 * Reads the options and the tracks given.
	 *
	 * @return the exit code when there is nothing to check (help, version), else null
	 * @throws UsageException for a bad option, with why
	 */
	private Integer parse(String[] args)
	{
		boolean options = true;
		for (int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			if (!options || !arg.startsWith("-") || arg.equals("-"))
			{
				inputs.add(new Input(arg, arg, !options));
				continue;
			}
			String name = arg;
			String value = null;
			int equals = arg.indexOf('=');
			if (arg.startsWith("--") && equals > 0)
			{
				name = arg.substring(0, equals);
				value = arg.substring(equals + 1);
			}
			switch (name)
			{
			case "--":
				options = false;
				break;
			case "-h":
			case "--help":
				out.print(help(command));
				return OK;
			case "--version":
				out.println("Track editor " + Properties.getInstance().version);
				return OK;
			case "--game":
				gameOption = value != null ? value : next(args, ++i, name);
				break;
			case "--data":
				dataOption = value != null ? value : next(args, ++i, name);
				break;
			case "--local":
				localOption = value != null ? value : next(args, ++i, name);
				break;
			case "--bin":
				binOption = value != null ? value : next(args, ++i, name);
				break;
			case "--no-prefs":
				noValue(name, value);
				noPrefs = true;
				break;
			case "--trackgen":
				noValue(name, value);
				trackgen = true;
				break;
			case "--trackgen-timeout":
				String seconds = value != null ? value : next(args, ++i, name);
				try
				{
					trackgenTimeout = Long.parseLong(seconds.trim());
				}
				catch (NumberFormatException e)
				{
					trackgenTimeout = -1;
				}
				if (trackgenTimeout < 0)
					throw new UsageException(name + " " + seconds + " isn't a number of seconds: give 0 or more (0: no limit)");
				break;
			case "--check-default-objects":
				noValue(name, value);
				checkDefaultObjectsOption = true;
				break;
			case "--show":
				for (String kind : (value != null ? value : next(args, ++i, name)).split(","))
					show(kind.trim());
				break;
			case "--format":
				String f = value != null ? value : next(args, ++i, name);
				if (f.equalsIgnoreCase("text"))
					format = Format.TEXT;
				else if (f.equalsIgnoreCase("github"))
					format = Format.GITHUB;
				else
					throw new UsageException("Unknown format " + f + ": it's text or github");
				break;
			case "--baseline":
				baseline = value != null ? value : next(args, ++i, name);
				break;
			case "--write-baseline":
				writeBaseline = value != null ? value : next(args, ++i, name);
				break;
			case "-v":
			case "--verbose":
				noValue(name, value);
				verbose = true;
				break;
			default:
				throw new UsageException("Unknown option " + arg);
			}
		}
		if (baseline != null && writeBaseline != null)
			throw new UsageException("Give --baseline or --write-baseline, not both");
		if (inputs.isEmpty())
			throw new UsageException("Give the track files to check");
		return null;
	}

	private static String next(String[] args, int i, String option)
	{
		if (i >= args.length || args[i].isEmpty())
			throw new UsageException(option + " needs a value");
		return args[i];
	}

	private static void noValue(String option, String value)
	{
		if (value != null)
			throw new UsageException(option + " doesn't take a value");
	}

	private void show(String kind)
	{
		switch (kind.toLowerCase())
		{
		case "saving":
			show.add(Kind.SAVING);
			break;
		case "png":
			show.add(Kind.PNG);
			break;
		case "objects":
			show.add(Kind.OBJECTS);
			break;
		case "all":
			show.addAll(EnumSet.allOf(Kind.class));
			break;
		default:
			throw new UsageException("Unknown kind of message " + kind + ": it's saving, png, objects or all");
		}
	}

	/** @return the game, by its short name or its name in the editor's preferences */
	private static GameType game(String name)
	{
		for (GameType game : GameType.values())
		{
			if (name.equalsIgnoreCase(game.getShortName()) || name.equalsIgnoreCase(game.name()))
				return game;
		}
		if (name.equalsIgnoreCase("sd"))
			return GameType.SPEED_DREAMS;
		throw new UsageException("Unknown game " + name + ": it's torcs, sd or csr");
	}

	/**
	 * Works out the game and its folders: the options, or else the editor's
	 * preferences (not with --no-prefs), or else the defaults.
	 *
	 * @throws IllegalArgumentException when they can't be used, with why
	 */
	private void settings()
	{
		preferences = noPrefs ? null : editorPreferences();
		// the editor's game: its folders can have the old keys, from before each game had its own
		editorGame = preferences == null ? null : preferencesGame(preferences);

		if (gameOption != null)
		{
			game = game(gameOption);
			settings.add("Game: " + game.getName() + " (--game)");
		}
		else if (editorGame != null)
		{
			game = editorGame;
			settings.add("Game: " + game.getName() + " (the editor's preferences)");
		}
		else
		{
			throw new UsageException("Give the game the tracks are for: --game torcs, sd or csr"
				+ (noPrefs ? "" : " (the editor's preferences don't have one)"));
		}

		dataDirectory = folder("--data", dataOption, preferences, editorGame, 0, "Data folder");
		binDirectory = folder("--bin", binOption, preferences, editorGame, 1, "Bin folder");
		// only Speed Dreams has local tracks
		if (game.hasLocalTracks())
		{
			String local = folder("--local", localOption, preferences, editorGame, 3, "Local folder");
			localDirectory = TrackFolders.localDirectory(game, local);
			if (local == null)
				settings.add("Local folder: " + (localDirectory == null ? "none" : localDirectory + " (the standard one)"));
		}
		else if (localOption != null)
		{
			err.println("--local isn't used: " + game.getName() + " has no local folder, only Speed Dreams has");
		}

		if (checkDefaultObjectsOption)
		{
			checkDefaultObjects = true;
			settings.add("Check the default objects' models: yes (--check-default-objects)");
		}
		else if (preferences != null && preferences.get(CHECK_DEFAULT_OBJECTS, null) != null)
		{
			checkDefaultObjects = preferences.getBoolean(CHECK_DEFAULT_OBJECTS, false);
			settings.add("Check the default objects' models: " + (checkDefaultObjects ? "yes" : "no") + " (the editor's preferences)");
		}
		else
		{
			settings.add("Check the default objects' models: no");
		}

		if (trackgen)
		{
			settings.add("Trackgen: " + Trackgen.path(game, binDirectory, dataDirectory) + ", "
				+ (trackgenTimeout == 0 ? "with no time limit" : "stopped after " + trackgenTimeout + (trackgenTimeout == 1 ? " second" : " seconds"))
				+ " a track");
		}
		if (noPrefs)
			settings.add("The editor's preferences aren't used (--no-prefs)");
		StringBuilder shown = new StringBuilder("Messages Check hides at first that are shown: ");
		shown.append(show.isEmpty() ? "none" : show.toString().toLowerCase().replaceAll("[\\[\\]]", ""));
		settings.add(shown.toString());

		// a folder that isn't the game's data folder makes most messages wrong: say so once
		if (dataDirectory != null && !new File(dataDirectory, "tracks").isDirectory())
		{
			File inner = new File(dataDirectory, "data");
			throw new IllegalArgumentException("The data folder " + dataDirectory + " has no tracks folder"
				+ (hasTrackFolders(new File(inner, "tracks")) ? ": is the data folder " + inner.getPath() + "?" : "")
				+ (dataOption == null ? " (it's the editor's, from Preferences)" : ""));
		}
	}

	/**
	 * @return whether the folder has tracks: <category>/<name> folders (the shared
	 *         data/tracks folder only has surfaces.xml and objects.xml)
	 */
	private static boolean hasTrackFolders(File tracks)
	{
		File[] categories = tracks.listFiles(File::isDirectory);
		if (categories == null)
			return false;
		for (File category : categories)
		{
			File[] names = category.listFiles(File::isDirectory);
			if (names != null && names.length > 0)
				return true;
		}
		return false;
	}

	/**
	 * @return the editor's preferences, or null when there are none (as on a CI
	 *         computer): looked up without making them
	 */
	private static Preferences editorPreferences()
	{
		PREFERENCES_LOG.setLevel(java.util.logging.Level.WARNING);
		try
		{
			Preferences root = Preferences.userRoot();
			return root.nodeExists(PREFERENCES_NODE) ? root.node(PREFERENCES_NODE) : null;
		}
		catch (BackingStoreException | SecurityException | IllegalStateException e)
		{
			return null;
		}
	}

	/** @return the editor's game, or the old CSR preference's, or null */
	private static GameType preferencesGame(Preferences preferences)
	{
		String name = preferences.get(GAME_TYPE, null);
		if (name != null)
		{
			try
			{
				return GameType.valueOf(name);
			}
			catch (IllegalArgumentException e)
			{
				// from a newer editor: use the old preference
			}
		}
		String csr = preferences.get(CARS_SPORTS_RACING, null);
		if (csr == null)
			return null;
		return Boolean.parseBoolean(csr) ? GameType.CARS_SPORTS_RACING : GameType.SPEED_DREAMS;
	}

	/**
	 * @param kind which of the editor's directories (EditorFrame.DATA_DIRECTORY and so on)
	 * @return the folder given, as an absolute path, or else the editor's for the
	 *         game, or null
	 * @throws IllegalArgumentException for a folder given that isn't a folder
	 */
	private String folder(String option, String given, Preferences preferences, GameType editorGame, int kind, String what)
	{
		if (given != null)
		{
			File folder = new File(given);
			if (!folder.isDirectory())
			{
				throw new IllegalArgumentException(option + " " + given + " isn't a folder" + (folder.isAbsolute() ? ""
					: " (it was given as " + given + ", which is " + folder.getAbsolutePath() + "): in bash, quote paths "
					+ "with backslashes, or use forward slashes"));
			}
			String path = folder.getAbsoluteFile().toPath().normalize().toString();
			settings.add(what + ": " + path + " (" + option + ")");
			return path;
		}
		if (preferences == null)
			return null;

		String value = preferenceFolder(preferences, game, editorGame, kind);
		if (value == null)
			return null;
		if (!new File(value).isDirectory())
		{
			err.println("The editor's " + what.toLowerCase() + " for " + game.getShortName() + ", " + value
				+ ", isn't a folder: it isn't used (give " + option + ")");
			return null;
		}
		settings.add(what + ": " + value + " (the editor's preferences)");
		return value;
	}

	/**
	 * @param kind which of the editor's directories (EditorFrame.DATA_DIRECTORY and so on)
	 * @return the editor's folder of that kind for the game, as set, or null
	 */
	private static String preferenceFolder(Preferences preferences, GameType game, GameType editorGame, int kind)
	{
		String value = preferences.get(game.name() + "." + DIRECTORY_KEYS[kind], null);
		if (value == null && game == editorGame)
		{
			// like the editor: the old shared keys, when the game has none of its own
			boolean any = false;
			for (String key : DIRECTORY_KEYS)
				any |= preferences.get(game.name() + "." + key, null) != null;
			if (!any)
				value = preferences.get(DIRECTORY_KEYS[kind], null);
		}
		return value == null || value.isEmpty() ? null : value;
	}

	/**
	 * Says once when tracks aren't in the game's tracks folders but are in another
	 * game's, by the editor's preferences: the editor was likely last used for that
	 * game, or --game is wrong, and most messages would be.
	 */
	private void gameHint()
	{
		if (preferences == null || tracks.isEmpty())
			return;
		List<File> own = TrackFolders.tracksDirectories(localDirectory, dataDirectory);
		for (GameType other : GameType.values())
		{
			if (other == game)
				continue;
			String data = preferenceFolder(preferences, other, editorGame, 0);
			String local = other.hasLocalTracks() ? TrackFolders.localDirectory(other, preferenceFolder(preferences, other, editorGame, 3)) : null;
			List<File> theirs = TrackFolders.tracksDirectories(local, data);
			int count = 0;
			File where = null;
			for (Track track : tracks)
			{
				if (folderOf(track.file, own) != null)
					continue;
				File folder = folderOf(track.file, theirs);
				if (folder != null)
				{
					count++;
					where = folder;
				}
			}
			if (count > 0)
			{
				err.println((count == tracks.size() ? (count == 1 ? "The track is" : "All " + count + " tracks are")
					: count + " of the " + tracks.size() + (count == 1 ? " tracks is" : " tracks are")) + " in " + other.getName() + "'s tracks folder " + where.getPath()
					+ ", not " + game.getName() + "'s: did you mean --game " + option(other) + "?");
			}
		}
	}

	/** @return the folder of those the file is in, or null */
	private static File folderOf(File file, List<File> folders)
	{
		String path = canonical(file);
		for (File folder : folders)
		{
			String inside = canonical(folder) + File.separator;
			if (WINDOWS ? path.toLowerCase().startsWith(inside.toLowerCase()) : path.startsWith(inside))
				return folder;
		}
		return null;
	}

	private static String canonical(File file)
	{
		try
		{
			return file.getCanonicalPath();
		}
		catch (IOException | SecurityException e)
		{
			return file.getAbsolutePath();
		}
	}

	/** @return the game as --game takes it */
	private static String option(GameType game)
	{
		return game == GameType.SPEED_DREAMS ? "sd" : game.getShortName().toLowerCase();
	}

	/**
	 * Finds the tracks: the files given, the patterns' tracks, and the lists'.
	 *
	 * @throws IllegalArgumentException for a list that can't be read or a pattern
	 *         that finds no tracks, with why
	 */
	private void findTracks()
	{
		Set<String> seen = new HashSet<String>();
		for (Input input : inputs)
		{
			if (!input.literal && input.text.startsWith("@") && input.text.length() > 1)
			{
				for (Input entry : readList(input.text.substring(1)))
					add(entry, seen);
			}
			else
			{
				add(input, seen);
			}
		}
	}

	/** @return a list's entries: one track file or pattern a line, not blank lines or # comments */
	private List<Input> readList(String list)
	{
		File file = new File(list);
		List<String> lines;
		try
		{
			lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException(file.isFile() ? "Couldn't read the list " + list + ": " + e.getLocalizedMessage()
				: "There is no list file " + list);
		}
		String folder = file.getParent();
		List<Input> entries = new ArrayList<Input>();
		for (String line : lines)
		{
			String entry = line.replace("\uFEFF", "").trim();
			if (entry.isEmpty() || entry.startsWith("#"))
				continue;
			if (entry.startsWith("@"))
				throw new IllegalArgumentException("The list " + list + " has " + entry + ": a list can't include another list");
			// relative to the list's folder, so a list works from anywhere
			String path = folder == null || new File(entry).isAbsolute() || entry.startsWith("/") || entry.startsWith("\\") ? entry
				: folder + "/" + entry;
			entries.add(new Input(entry, path, false));
		}
		if (verbose)
			settings.add("List " + list + ": " + entries.size() + (entries.size() == 1 ? " entry" : " entries"));
		return entries;
	}

	/** Adds the track file, or the pattern's tracks, once each */
	private void add(Input input, Set<String> seen)
	{
		String path = tidy(input.path);
		// a file named with [ (or, outside Windows, * or ?) that is there: the file, not a pattern
		if (input.literal || !isPattern(path) || new File(path).isFile())
		{
			add(new Track(new File(path), path), seen);
			return;
		}
		List<Track> found = new ArrayList<Track>();
		List<String> notTracks = new ArrayList<String>();
		expand(path, found, notTracks);
		if (found.isEmpty())
		{
			throw new IllegalArgumentException("No tracks match " + input.text + (notTracks.isEmpty() ? ""
				: ": it matches " + notTracks.size() + (notTracks.size() == 1 ? " file that isn't" : " files that aren't")
				+ " <name>/<name>.xml, like " + notTracks.get(0))
				+ (path.indexOf('[') >= 0 ? " ([abc] matches one of those letters: for a name with [ in it, put -- before the file)" : ""));
		}
		skipped += notTracks.size();
		int before = tracks.size();
		for (Track track : found)
			add(track, seen);
		if (verbose)
		{
			settings.add("Pattern " + input.text + ": " + (tracks.size() - before) + (tracks.size() - before == 1 ? " track" : " tracks")
				+ (notTracks.isEmpty() ? "" : ", skipped " + notTracks.size() + (notTracks.size() == 1 ? " that isn't" : " that aren't")
					+ " <name>/<name>.xml: " + String.join(", ", notTracks)));
		}
	}

	private void add(Track track, Set<String> seen)
	{
		String key;
		try
		{
			key = track.file.getCanonicalPath();
		}
		catch (IOException | SecurityException e)
		{
			key = track.file.getAbsolutePath();
		}
		if (WINDOWS)
			key = key.toLowerCase();
		if (seen.add(key))
			tracks.add(track);
	}

	/**
	 * @return the path with / and without . and folder/.. (a list's ../tracks), as
	 *         messages and GitHub's annotations name the file; worked out on the text,
	 *         as a pattern can't be a Path on Windows
	 */
	static String tidy(String path)
	{
		// a network path, \\\\server\\share or //server/share: it keeps its two slashes, and
		// server and share as its root
		boolean network = path.length() > 2 && (path.startsWith("\\\\") || path.startsWith("//"))
			&& path.charAt(2) != '/' && path.charAt(2) != '\\';
		String[] parts = (network ? path.substring(2) : path).split("[/\\\\]", -1);
		// the parts that are the root, which .. doesn't go above
		int rootParts = network ? Math.min(2, parts.length) : 0;
		List<String> kept = new ArrayList<String>();
		for (int i = 0; i < parts.length; i++)
		{
			String part = parts[i];
			if (i < rootParts)
			{
				kept.add(part);
				continue;
			}
			// the root ("" before the first /) and a Windows drive stay
			boolean root = !network && i == 0 && (part.isEmpty() || part.endsWith(":"));
			if (!root && (part.equals(".") || (part.isEmpty() && i < parts.length - 1)))
				continue;
			if (!root && part.equals("..") && kept.size() > rootParts && !kept.get(kept.size() - 1).equals("..")
				&& !(!network && kept.size() == 1 && (kept.get(0).isEmpty() || kept.get(0).endsWith(":"))))
			{
				kept.remove(kept.size() - 1);
				continue;
			}
			kept.add(part);
		}
		if (network)
			return "//" + String.join("/", kept);
		if (kept.isEmpty())
			return ".";
		if (kept.size() == 1 && kept.get(0).isEmpty())
			return "/";
		return String.join("/", kept);
	}

	/** @return whether the path has a wildcard: *, ? or [ */
	private static boolean isPattern(String path)
	{
		return path.indexOf('*') >= 0 || path.indexOf('?') >= 0 || path.indexOf('[') >= 0;
	}

	/**
	 * Finds the files a pattern matches: * and ? in a name, [abc] and [a-z] for a
	 * letter, ** for any number of folders.
	 *
	 * @param found gets the tracks: files named like their folder, <name>/<name>.xml
	 * @param notTracks gets the other files' names
	 */
	private static void expand(String pattern, List<Track> found, List<String> notTracks)
	{
		String[] parts = pattern.split("[/\\\\]", -1);
		// the folder before the first wildcard, as given
		int first = 0;
		while (first < parts.length - 1 && !isPattern(parts[first]))
			first++;
		StringBuilder baseText = new StringBuilder();
		for (int i = 0; i < first; i++)
			baseText.append(i == 0 ? "" : "/").append(parts[i]);
		String base = first == 0 ? "" : baseText.length() == 0 ? "/" : baseText.toString();
		// Windows: "C:" alone is the drive's current folder, "C:/" its root
		if (base.length() == 2 && base.charAt(1) == ':')
			base += "/";

		StringBuilder regex = new StringBuilder();
		boolean anyDepth = false;
		for (int i = first; i < parts.length; i++)
		{
			if (parts[i].equals("**"))
			{
				regex.append("(?:[^/]+/)*");
				anyDepth = true;
				continue;
			}
			regex.append(globToRegex(parts[i]));
			if (i < parts.length - 1)
				regex.append('/');
		}
		final Pattern matcher = Pattern.compile(regex.toString(), WINDOWS ? Pattern.CASE_INSENSITIVE : 0);
		final Path start = new File(base.isEmpty() ? "." : base).toPath();
		final String prefix = base.isEmpty() ? "" : base.endsWith("/") ? base : base + "/";
		final List<Path> files = new ArrayList<Path>();
		if (!Files.isDirectory(start))
			return;
		try
		{
			// following links to track folders: a loop is a failed visit, skipped
			Files.walkFileTree(start, EnumSet.of(FileVisitOption.FOLLOW_LINKS), anyDepth ? Integer.MAX_VALUE : parts.length - first,
				new SimpleFileVisitor<Path>()
				{
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
					{
						if (attributes.isRegularFile() && matcher.matcher(relative(start, file)).matches())
							files.add(file);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFileFailed(Path file, IOException e)
					{
						// a folder that can't be read, or a link back to a folder above
						return FileVisitResult.CONTINUE;
					}
				});
		}
		catch (IOException e)
		{
			return;
		}
		List<String> names = new ArrayList<String>();
		Map<String, Path> byName = new HashMap<String, Path>();
		for (Path file : files)
		{
			String name = prefix + relative(start, file);
			names.add(name);
			byName.put(name, file);
		}
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		for (String name : names)
		{
			File file = byName.get(name).toFile();
			String folder = file.getParentFile() == null ? null : file.getAbsoluteFile().getParentFile().getName();
			String expected = folder + ".xml";
			if (WINDOWS ? file.getName().equalsIgnoreCase(expected) : file.getName().equals(expected))
				found.add(new Track(file, name));
			else
				notTracks.add(name);
		}
	}

	/** @return the file's path from the folder, with / */
	private static String relative(Path folder, Path file)
	{
		return folder.relativize(file).toString().replace('\\', '/');
	}

	/** @return a regular expression for a pattern's name: * and ? in it, [abc] and [!abc] */
	private static String globToRegex(String glob)
	{
		StringBuilder regex = new StringBuilder();
		for (int i = 0; i < glob.length(); i++)
		{
			char c = glob.charAt(i);
			if (c == '*')
				regex.append("[^/]*");
			else if (c == '?')
				regex.append("[^/]");
			else if (c == '[' && glob.indexOf(']', i + 1) > i + 1)
			{
				int end = glob.indexOf(']', i + 1);
				String set = glob.substring(i + 1, end);
				if (set.startsWith("!"))
					set = "^" + set.substring(1);
				regex.append('[').append(set.replace("\\", "\\\\").replace("[", "\\[")).append(']');
				i = end;
			}
			else
				regex.append(Pattern.quote(String.valueOf(c)));
		}
		return regex.toString();
	}

	/** Checks the tracks, reports the messages and says how it went */
	private int check()
	{
		// GitHub reads the annotations as UTF-8, whatever the computer's character set
		if (format == Format.GITHUB)
		{
			try
			{
				out = new PrintStream(out, true, "UTF-8");
			}
			catch (java.io.UnsupportedEncodingException e)
			{
				// every Java has UTF-8
			}
		}
		if (verbose)
		{
			for (String setting : settings)
				err.println(setting);
		}

		List<Report> reports = new ArrayList<Report>();
		if (tracks.isEmpty())
		{
			// only empty lists: nothing changed, say, so nothing to check
			out.println("No tracks to check");
			return writeBaseline != null ? writeBaseline(reports, false) : OK;
		}

		// the messages already known
		Map<String, Integer> known = new HashMap<String, Integer>();
		if (baseline != null)
		{
			if (!new File(baseline).isFile())
			{
				err.println("There is no baseline file " + baseline);
				return FAILED;
			}
			try
			{
				for (String line : Files.readAllLines(new File(baseline).toPath(), StandardCharsets.UTF_8))
				{
					if (!line.isEmpty() && !line.startsWith("#"))
						known.merge(line, 1, Integer::sum);
				}
			}
			catch (IOException e)
			{
				err.println("Couldn't read the baseline " + baseline + ": " + e.getLocalizedMessage());
				return FAILED;
			}
		}

		// the game's default surfaces and objects, read once: what's wrong with them is about every track
		LoadedTrack shared = new LoadedTrack(game, dataDirectory, localDirectory);
		shared.setCheckDefaultObjects(checkDefaultObjects);
		TrackLoader.readDefaults(shared, (title, message, error) -> reports.add(new Report("", 0, title, oneLine(message), error)));
		int fresh = 0;
		for (Report report : reports)
		{
			if (!known(known, report))
			{
				fresh++;
				print(report);
			}
		}
		int general = reports.size();

		// tracks that couldn't be read or checked
		int notRead = 0;
		int closureNotChecked = 0;
		for (Track track : tracks)
		{
			Result result;
			try
			{
				result = check(track, shared, reports);
			}
			catch (RuntimeException | StackOverflowError e)
			{
				// a bug in the checker with this track: say so and check the others
				error(track.name, "Couldn't check the track: " + bug(e));
				result = Result.NOT_CHECKED;
			}
			if (result == Result.NOT_READ || result == Result.NOT_CHECKED)
				notRead++;
			else if (result == Result.CLOSURE_NOT_CHECKED)
				closureNotChecked++;
		}
		boolean failed = notRead > 0 || closureNotChecked > 0;

		if (writeBaseline != null)
			return writeBaseline(reports, failed);

		for (int i = general; i < reports.size(); i++)
		{
			Report report = reports.get(i);
			if (!known(known, report))
			{
				fresh++;
				print(report);
			}
		}

		StringBuilder summary = new StringBuilder();
		int checked = tracks.size() - notRead;
		summary.append(checked).append(checked == 1 ? " track" : " tracks").append(" checked");
		if (notRead > 0)
			summary.append(", ").append(notRead).append(notRead == 1 ? " track" : " tracks").append(" couldn't be read or checked");
		summary.append(": ");
		summary.append(fresh == 0 ? "no" : String.valueOf(fresh)).append(baseline != null ? " new" : "")
			.append(fresh == 1 ? " message" : " messages");
		if (baseline != null)
			summary.append(", ").append(reports.size() - fresh).append(" in the baseline");
		if (!trackgen)
			summary.append(" (closure not checked: add --trackgen)");
		else if (closureNotChecked > 0)
			summary.append(" (closure not checked for ").append(closureNotChecked)
				.append(closureNotChecked == 1 ? " track: trackgen couldn't run)" : " tracks: trackgen couldn't run)");
		if (skipped > 0)
			summary.append("; skipped ").append(skipped).append(skipped == 1 ? " file that isn't" : " files that aren't")
				.append(" <name>/<name>.xml").append(verbose ? "" : " (--verbose lists them)");
		out.println(summary);

		if (failed)
			return FAILED;
		return fresh > 0 ? MESSAGES : OK;
	}

	/** @return whether the report is in the baseline: each line of it matches one report */
	private static boolean known(Map<String, Integer> known, Report report)
	{
		Integer count = known.get(report.key());
		if (count == null || count == 0)
			return false;
		known.put(report.key(), count - 1);
		return true;
	}

	/** Writes the messages as the baseline, unless a track couldn't be checked */
	private int writeBaseline(List<Report> reports, boolean failed)
	{
		if (failed)
		{
			err.println("The baseline " + writeBaseline + " wasn't written: not every track could be checked");
			return FAILED;
		}
		List<String> lines = new ArrayList<String>();
		for (Report report : reports)
			lines.add(report.key());
		Collections.sort(lines);
		lines.add(0, BASELINE_HEADER);
		try
		{
			Files.write(new File(writeBaseline).toPath(), lines, StandardCharsets.UTF_8);
		}
		catch (IOException e)
		{
			err.println("Couldn't write the baseline " + writeBaseline + ": " + e.getLocalizedMessage());
			return FAILED;
		}
		out.println("Wrote " + reports.size() + (reports.size() == 1 ? " message" : " messages") + " for "
			+ tracks.size() + (tracks.size() == 1 ? " track" : " tracks") + " to " + writeBaseline);
		return OK;
	}

	/**
	 * Reads and checks a track, adding its messages to the reports.
	 *
	 * @param shared the game, its folders and its default surfaces and objects
	 * @return whether the track was read and checked, and with --trackgen its closure
	 */
	private Result check(Track track, LoadedTrack shared, List<Report> reports)
	{
		final String name = track.name;
		if (!track.file.isFile())
		{
			error(name, "There is no track file " + track.file.getPath());
			return Result.NOT_READ;
		}

		final List<Report> notices = new ArrayList<Report>();
		ReadNotices readNotices = (title, message, error) -> notices.add(new Report(name, 0, title, oneLine(message), error));
		LoadedTrack loaded;
		try
		{
			loaded = TrackLoader.load(track.file, shared, readNotices);
		}
		catch (Exception e)
		{
			String reason = e.getLocalizedMessage();
			error(name, "Couldn't read the track: " + (reason == null || reason.isEmpty() || e instanceof RuntimeException ? e.toString() : reason));
			return Result.NOT_READ;
		}
		reports.addAll(notices);

		Result result = Result.CHECKED;
		if (trackgen)
		{
			try
			{
				TrackgenOutput output = Trackgen.calculate(game, binDirectory, dataDirectory, loaded.getLocalDirectory(),
					Properties.getInstance().getPath(), loaded.getTrackData(), trackgenTimeout);
				Closure.setLast(output.closure(Closure.key(loaded)));
			}
			catch (IOException e)
			{
				String reason = oneLine(e.getLocalizedMessage());
				// trackgen not found: the editor has its folder in Preferences, the checker needs --bin
				if (binDirectory == null && reason.startsWith("Couldn't run "))
					reason += " (give --bin, the folder " + game.getTrackgen() + " is in)";
				error(name, "Closure not checked: " + reason);
				result = Result.CLOSURE_NOT_CHECKED;
			}
			catch (RuntimeException e)
			{
				// a bug running trackgen: the track's other checks still run
				error(name, "Closure not checked: " + bug(e));
				result = Result.CLOSURE_NOT_CHECKED;
			}
		}

		// what the editor says in a message box when it opens the track: values the game
		// reads differently than they are written, keys it reads once. Check has them
		// with what saving changes, so they're reported here unless those are shown.
		if (!show.contains(Kind.SAVING))
		{
			for (String warning : loaded.getNumberWarnings())
			{
				int line = 0;
				String text = oneLine(warning);
				Matcher matcher = READ_LINE.matcher(text);
				if (matcher.find())
				{
					line = Integer.parseInt(matcher.group(1));
					text = text.substring(matcher.end());
				}
				reports.add(new Report(name, line, null, text, false));
			}
		}

		for (CheckMessage message : new TrackChecker(loaded).check())
		{
			// the messages Check hides at first, unless --show, and Check's own lines
			if ((message.isObjectFile() && !show.contains(Kind.OBJECTS)) || (message.isPng() && !show.contains(Kind.PNG))
				|| (message.isSaving() && !show.contains(Kind.SAVING)))
				continue;
			Subject subject = message.getSubject();
			if (subject != null && subject.getKind() == Subject.Kind.CALCULATE_CLOSURE)
				continue;
			String text = message.getText();
			if (text.isEmpty() || (subject == null && text.equals("Checking complete!")))
				continue;
			int line = 0;
			Matcher matcher = LINE.matcher(text);
			if (matcher.find())
			{
				line = Integer.parseInt(matcher.group(1));
				text = text.substring(matcher.end());
			}
			reports.add(new Report(name, line, null, text, false));
		}
		return result;
	}

	/** Reports a problem that stopped a track being checked, right away */
	private void error(String file, String text)
	{
		print(new Report(file, 0, null, text, true));
	}

	private void print(Report report)
	{
		if (format == Format.GITHUB)
		{
			StringBuilder line = new StringBuilder(report.error ? "::error" : "::warning");
			String separator = " ";
			if (!report.file.isEmpty())
			{
				line.append(" file=").append(property(report.file));
				separator = ",";
				if (report.line > 0)
					line.append(",line=").append(report.line);
			}
			line.append(separator).append("title=").append(property(report.title != null ? report.title : "Track check"));
			line.append("::").append(data(report.text));
			out.println(line);
			return;
		}
		StringBuilder line = new StringBuilder(report.file);
		if (report.line > 0)
			line.append(':').append(report.line);
		if (!report.file.isEmpty())
			line.append(": ");
		if (report.error)
			line.append("error: ");
		if (report.title != null)
			line.append(report.title).append(": ");
		line.append(report.text);
		out.println(line);
	}

	/** @return the text on one line: a message box's lines, joined */
	private static String oneLine(String text)
	{
		if (text == null)
			return "";
		return text.trim().replaceAll("\\s*\\n\\s*", " ");
	}

	/** @return the text as a GitHub workflow command's message */
	private static String data(String text)
	{
		return text.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A");
	}

	/** @return the text as a GitHub workflow command's property */
	private static String property(String text)
	{
		return data(text).replace(":", "%3A").replace(",", "%2C");
	}

	private static String help(String command)
	{
		String n = System.lineSeparator();
		return "Usage: " + command + " [options] track.xml|\"pattern\"|@list.txt ..." + n
			+ n
			+ "Checks tracks the way the editor's Check does, without opening the editor." + n
			+ n
			+ "Tracks:" + n
			+ "  track.xml            a track file (used even if it isn't <name>/<name>.xml)" + n
			+ "  \"tracks/*/*/*.xml\"   a pattern, expanded by the checker (quote it): * and ? in a" + n
			+ "                       name, [abc], and ** for any number of folders. Only" + n
			+ "                       <name>/<name>.xml files are used, so .prj.xml and other" + n
			+ "                       files are skipped" + n
			+ "  @list.txt            a file of track files or patterns, one a line; # starts a" + n
			+ "                       comment, and paths are relative to the list's folder. An" + n
			+ "                       empty list is nothing to check, not an error" + n
			+ "  --                   what follows are track files, even if they start with -" + n
			+ n
			+ "Game (default: the editor's preferences):" + n
			+ "  --game GAME          torcs, sd or csr" + n
			+ "  --data DIR           the game's data folder, which has its tracks folder" + n
			+ "  --local DIR          Speed Dreams' local folder (default: also its standard one)" + n
			+ "  --bin DIR            where trackgen is (default: also the PATH, or TORCS' data folder)" + n
			+ "  --no-prefs           don't use the editor's preferences: the result depends only on" + n
			+ "                       the command line (use this in CI)" + n
			+ n
			+ "Checks:" + n
			+ "  --trackgen           run trackgen to check the track closes, as Calc does" + n
			+ "                       (without it, closure isn't checked)" + n
			+ "  --trackgen-timeout SECONDS" + n
			+ "                       stop trackgen when it takes longer for a track (default: 300;" + n
			+ "                       0: no limit). The track's closure isn't checked then" + n
			+ "  --show KINDS         also report messages Check hides at first: saving (what saving" + n
			+ "                       changes), png (converting textures), objects (the objects'" + n
			+ "                       models), or all; comma separated. Without saving, the" + n
			+ "                       warnings the editor shows when it opens a track are reported" + n
			+ "                       instead: values the game reads differently than they're" + n
			+ "                       written, and keys in their section more than once" + n
			+ "  --check-default-objects" + n
			+ "                       check the default objects' models too (default: the editor's" + n
			+ "                       preference)" + n
			+ n
			+ "Output:" + n
			+ "  --format FORMAT      text (default), or github: annotations for GitHub Actions," + n
			+ "                       in UTF-8" + n
			+ "  --baseline FILE      only report messages that aren't in FILE" + n
			+ "  --write-baseline FILE" + n
			+ "                       write the messages to FILE as the baseline, and exit 0" + n
			+ "  -v, --verbose        show the settings used, where each came from, and the files" + n
			+ "                       a pattern skipped" + n
			+ "  -h, --help           show this help" + n
			+ "  --version            show the editor's version" + n
			+ n
			+ "Exit codes:" + n
			+ "  0  no messages, or only ones in the baseline" + n
			+ "  1  new messages" + n
			+ "  2  the check couldn't run: a bad option, no tracks found, a track that can't be" + n
			+ "     read, or trackgen failing with --trackgen" + n;
	}
}
