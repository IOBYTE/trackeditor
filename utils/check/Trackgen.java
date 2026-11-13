/*
 *   Trackgen.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import utils.GameType;
import utils.TrackData;

/**
 * How the editor runs a game's trackgen on a track: which trackgen, where, with
 * which arguments, and why it can't. The Delta dialog uses it to calculate the
 * deltas, and so does checking without the editor (see run).
 */
public final class Trackgen
{
	private static final String	sep	= System.getProperty("file.separator");

	private Trackgen()
	{
	}

	/**
	 * @return the game's trackgen, in the bin directory when one is set. TORCS is
	 * installed with trackgen in its data directory, so it is used from there too.
	 */
	public static String path(GameType game, String binDirectory, String dataDirectory)
	{
		String trackgen = game.getTrackgen();
		if (binDirectory != null && !binDirectory.isEmpty())
			return binDirectory + sep + trackgen;
		if (game == GameType.TORCS && dataDirectory != null && !dataDirectory.isEmpty() &&
			(new File(dataDirectory, trackgen).isFile() || new File(dataDirectory, trackgen + ".exe").isFile()))
			return dataDirectory + sep + trackgen;
		return trackgen;
	}

	/**
	 * @param trackPath the track's folder
	 * @return the track's name, its folder's: also of a path that ends with a
	 *         separator, or is written with the other kind of slash
	 */
	static String trackName(String trackPath)
	{
		return new File(trackPath).getName();
	}

	/**
	 * @param trackPath the track's folder
	 * @return trackgen's arguments for where the track is: CSR's and Speed Dreams'
	 *         trackgen read the track from, and write their files into, the game's
	 *         installed data unless given the track's folder (-i and -o), so they're given
	 *         the folder the track is in. They find the relief, object maps and objects
	 *         next to the track file. TORCS trackgen has -i and -o too, but still looks
	 *         for those under its tracks/<category>/<name>, so it isn't given them.
	 */
	public static List<String> folderArgs(GameType game, String trackPath)
	{
		List<String> args = new ArrayList<String>();
		if (game != GameType.TORCS)
		{
			// with forward slashes and as the file system spells it: Speed Dreams reads
			// the default surfaces and objects of a track in its local directory from its
			// data directory only when the track's path starts with the local directory
			// as it spells it, like C:/Users/Bob/Documents/speed-dreams-2.settings/
			String path = trackPath;
			try
			{
				path = new File(path).getCanonicalPath();
			}
			catch (IOException | SecurityException e)
			{
				path = new File(path).getAbsolutePath();
			}
			path = path.replace(File.separatorChar, '/');
			args.add("-i");
			args.add(path);
			args.add("-o");
			args.add(path);
		}
		return args;
	}

	/**
	 * @return the directory trackgen runs in: TORCS trackgen reads the track (and its
	 * other files) from tracks/<category>/<name> and data/ in the directory it runs
	 * in, its data directory; null for the others (they find their data themselves)
	 */
	public static File directory(GameType game, String dataDirectory)
	{
		if (game != GameType.TORCS || dataDirectory == null || dataDirectory.isEmpty())
			return null;
		return new File(dataDirectory);
	}

	/**
	 * @param trackPath the track's folder
	 * @return why trackgen can't read the track, or null: TORCS trackgen only reads
	 * tracks/<category>/<name>/<name>.xml in its data directory, and CSR's and Speed
	 * Dreams' only find the default surfaces and objects from their tracks folders
	 * (see sharedFilesProblem)
	 */
	public static String problem(GameType game, String dataDirectory, String localDirectory, String trackPath, TrackData trackData)
	{
		if (game != GameType.TORCS)
			return trackData == null ? null : sharedFilesProblem(game, dataDirectory, localDirectory, trackPath, trackData);

		if (dataDirectory == null || dataDirectory.isEmpty())
			return "Set the TORCS data directory (the folder TORCS is installed in) in Preferences:\n" +
				"TORCS trackgen runs there and reads the track from its tracks folder.";

		String name = trackName(trackPath);
		File expected = new File(dataDirectory + sep + "tracks" + sep + trackData.getHeader().getCategory() + sep + name);
		if (TrackFolders.sameFile(new File(trackPath), expected))
			return null;
		return "TORCS trackgen only reads the track from its data directory, at\n" + expected +
			"\n\nbut this track is in\n" + trackPath +
			"\n\nSave the track there (its folder is the track's category, \"" + trackData.getHeader().getCategory() + "\"),\n" +
			"or set the TORCS data directory in Preferences.";
	}

	/**
	 * @return why CSR's or Speed Dreams' trackgen can't read the shared surfaces or
	 *         objects the track includes, or null. They're included as
	 *         ../../../data/tracks/surfaces.xml and objects.xml, from the track's
	 *         folder: the data directory's when the track is in its tracks folder.
	 *         Speed Dreams reads them from its data directory for a track in its local
	 *         tracks folder too.
	 */
	private static String sharedFilesProblem(GameType game, String dataDirectory, String localDirectory, String trackPath, TrackData trackData)
	{
		boolean surfaces = trackData.getHasSurfacesReference();
		boolean objects = trackData.getHasObjectsReference();
		if (!surfaces && !objects)
			return null;
		File folder = new File(trackPath).getAbsoluteFile();
		File shared = new File(folder, ".." + sep + ".." + sep + ".." + sep + "data" + sep + "tracks").toPath().normalize().toFile();
		if ((!surfaces || new File(shared, "surfaces.xml").isFile()) && (!objects || new File(shared, "objects.xml").isFile()))
			return null;
		File categoryFolder = folder.getParentFile();
		File tracksFolder = categoryFolder == null ? null : categoryFolder.getParentFile();
		if (localDirectory != null && tracksFolder != null && TrackFolders.sameFile(tracksFolder, new File(localDirectory, "tracks")))
			return null;
		List<File> tracks = TrackFolders.tracksDirectories(localDirectory, dataDirectory);
		StringBuilder where = new StringBuilder();
		for (File candidate : tracks)
			where.append("\n").append(candidate.getPath()).append(sep).append(trackData.getHeader().getCategory()).append(sep)
				.append(folder.getName());
		return game.getTrackgen() + " can't read the default " + (surfaces && objects ? "surfaces and objects" : surfaces ? "surfaces" : "objects")
			+ " this track includes: it looks for them\nthree folders up from the track, at\n" + shared.getPath() + "\n\n"
			+ (tracks.isEmpty() ? "Set the " + game.getName() + " data directory in Preferences, and save the track in its tracks folder."
				: "Save the track in a folder " + game.getName() + " reads tracks from:" + where);
	}

	/**
	 * @return that trackgen failed, with its exit status, and how it crashed when the
	 *         status says (Windows' exception codes, a signal on Linux and macOS)
	 */
	public static String failed(String trackgen, int exitValue)
	{
		// stopped with Ctrl+C or Ctrl+Break
		if (exitValue == 0xC000013A || exitValue == 0x40010004)
			return trackgen + " was stopped (exit status " + exitValue + String.format(", 0x%08X", exitValue) + ")";
		String crash = crash(exitValue);
		if (crash == null)
			return trackgen + " failed (exit status " + exitValue + ")";
		return trackgen + " crashed: " + crash + " (exit status " + exitValue
			+ (exitValue < 0 ? String.format(", 0x%08X", exitValue) : "") + ")";
	}

	/** @return how a program crashed, by its exit status, or null when it didn't say */
	private static String crash(int exitValue)
	{
		switch (exitValue)
		{
		// Windows' exception codes
		case 0xC0000005:
			return "access violation (it read or wrote memory it shouldn't)";
		case 0xC00000FD:
			return "stack overflow";
		case 0xC0000094:
			return "integer division by zero";
		case 0xC000008E:
			return "floating point division by zero";
		case 0xC000001D:
			return "illegal instruction";
		case 0xC0000374:
			return "heap corruption";
		case 0xC0000409:
			return "stack buffer overrun or fast fail";
		case 0xC0000135:
			return "a DLL it needs wasn't found";
		case 0xC0000139:
			return "a function it needs wasn't found in a DLL";
		case 0xC000007B:
			return "a DLL it needs is for another processor (32/64 bits)";
		// Linux and macOS: 128 and the signal
		case 128 + 6:
			return "aborted (SIGABRT)";
		case 128 + 8:
			return "floating point exception (SIGFPE)";
		case 128 + 11:
			return "segmentation fault (SIGSEGV)";
		case 128 + 4:
			return "illegal instruction (SIGILL)";
		case 128 + 7:
			return "bus error (SIGBUS)";
		default:
			return null;
		}
	}

	/**
	 * @param trackgen the trackgen to run (see path)
	 * @param trackPath the track's folder
	 * @return the command calculating the track's data and deltas (-z: no files written)
	 */
	public static List<String> deltasCommand(String trackgen, GameType game, String category, String trackPath)
	{
		String name = trackName(trackPath);
		List<String> command = new ArrayList<String>(Arrays.asList(trackgen, "-z", "-c", category, "-n", name));
		command.addAll(folderArgs(game, trackPath));
		return command;
	}

	/**
	 * Runs trackgen to calculate a track's data and deltas, as Calc in the Delta
	 * dialog does, without a window, and with no time limit (see the other).
	 */
	public static TrackgenOutput calculate(GameType game, String binDirectory, String dataDirectory, String localDirectory,
		String trackPath, TrackData trackData) throws IOException
	{
		return calculate(game, binDirectory, dataDirectory, localDirectory, trackPath, trackData, 0);
	}

	/**
	 * Runs trackgen to calculate a track's data and deltas, as Calc in the Delta
	 * dialog does, without a window. The track file is read as it is on disk.
	 *
	 * @param binDirectory where trackgen is, or null (see path)
	 * @param localDirectory the game's local directory, or null
	 * @param trackPath the track's folder
	 * @param timeoutSeconds how long trackgen may take before it's stopped, 0 for as
	 *        long as it takes
	 * @return trackgen's output
	 * @throws IOException with why trackgen couldn't run, took too long or didn't
	 *         report the deltas
	 */
	public static TrackgenOutput calculate(GameType game, String binDirectory, String dataDirectory, String localDirectory,
		String trackPath, TrackData trackData, final long timeoutSeconds) throws IOException
	{
		String problem = problem(game, dataDirectory, localDirectory, trackPath, trackData);
		if (problem != null)
			throw new IOException(problem);
		String trackgen = path(game, binDirectory, dataDirectory);
		ProcessBuilder builder = new ProcessBuilder(deltasCommand(trackgen, game, trackData.getHeader().getCategory(), trackPath));
		builder.redirectErrorStream(true);
		// TORCS trackgen reads the track from the directory it runs in
		builder.directory(directory(game, dataDirectory));
		Process process;
		try
		{
			process = builder.start();
		}
		catch (IOException e)
		{
			throw new IOException("Couldn't run " + trackgen + ": " + e.getLocalizedMessage(), e);
		}
		process.getOutputStream().close();

		// trackgen's output is read on a thread of its own, so a time limit holds even when
		// the reading wouldn't end (a process trackgen started keeping its output open)
		final TrackgenOutput output = new TrackgenOutput();
		final InputStream stream = process.getInputStream();
		final IOException[] readError = new IOException[1];
		Thread reader = new Thread(new Runnable()
		{
			public void run()
			{
				try (BufferedReader in = new BufferedReader(new InputStreamReader(stream)))
				{
					String line;
					while ((line = in.readLine()) != null)
					{
						synchronized (output)
						{
							output.read(line);
						}
					}
				}
				catch (IOException e)
				{
					readError[0] = e;
				}
			}
		}, "trackgen output");
		reader.setDaemon(true);
		reader.start();

		int exitValue;
		try
		{
			if (timeoutSeconds > 0)
			{
				long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(timeoutSeconds);
				boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
				if (finished)
				{
					// and its output read, within the same time
					reader.join(Math.max(1, TimeUnit.NANOSECONDS.toMillis(deadline - System.nanoTime())));
					finished = !reader.isAlive();
				}
				if (!finished)
				{
					process.destroyForcibly();
					// what it printed until then, if the reading ends now
					reader.join(2000);
					synchronized (output)
					{
						throw new IOException(trackgen + " took more than " + timeoutSeconds
							+ (timeoutSeconds == 1 ? " second" : " seconds") + " and was stopped" + output.otherOutput());
					}
				}
			}
			else
			{
				process.waitFor();
				reader.join();
			}
			exitValue = process.exitValue();
		}
		catch (InterruptedException e)
		{
			process.destroy();
			Thread.currentThread().interrupt();
			throw new IOException(trackgen + " was interrupted", e);
		}
		if (readError[0] != null)
			throw readError[0];
		if (exitValue != 0)
			throw new IOException(failed(trackgen, exitValue) + output.otherOutput());
		if (!output.complete())
			throw new IOException(trackgen + " didn't report the track length and deltas" + output.otherOutput());
		return output;
	}
}
