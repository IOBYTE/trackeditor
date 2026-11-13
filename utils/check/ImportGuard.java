/*
 *   ImportGuard.java
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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Keeps the track checker free of the editor's window code: it follows every
 * class TrackCheck can reach in the compiled classes (imports, fully qualified
 * names, and what those classes use in turn), and fails when any uses Swing,
 * AWT's windows, JOGL, the look and feel, the gui packages or TrackEditor. The
 * build runs it after making the jar; it can also be run from a class folder:
 * java -cp trackeditor.jar utils.check.ImportGuard
 *
 * Exit codes: 0 none, 1 some (each listed with how TrackCheck reaches it), 2 it
 * couldn't run.
 */
public final class ImportGuard
{
	// where the checker starts: everything it can reach must work without a window
	private static final String[]	ROOTS		= { "utils/check/TrackCheck" };

	// what needs a window or is the editor's: by the start of the class's name
	private static final String[]	FORBIDDEN	= { "javax/swing/", "java/awt/", "sun/awt/", "com/jogamp/", "javax/media/opengl/",
		"com/jgoodies/", "gui/", "utils/TrackEditor" };

	// AWT's classes that don't need a window: geometry, images, colors, drawing (the
	// track's shapes draw themselves, also into images), and plain events
	private static final String[]	ALLOWED		= { "java/awt/geom/", "java/awt/image/", "java/awt/Point", "java/awt/Rectangle",
		"java/awt/Dimension", "java/awt/Color", "java/awt/Shape", "java/awt/Polygon", "java/awt/Graphics", "java/awt/Graphics2D",
		"java/awt/BasicStroke", "java/awt/Stroke", "java/awt/RenderingHints", "java/awt/event/ActionListener",
		"java/awt/event/ActionEvent" };

	// a class's name in a descriptor or signature: Lname; or Lname<
	private static final Pattern	DESCRIPTOR	= Pattern.compile("L([\\w/$]+)[;<]");

	private ImportGuard()
	{
	}

	public static void main(String[] args)
	{
		try
		{
			List<String> problems = check();
			for (String problem : problems)
				System.out.println(problem);
			if (problems.isEmpty())
			{
				System.out.println("The track checker uses none of the editor's window code");
				System.exit(0);
			}
			System.out.println(problems.size() + (problems.size() == 1 ? " use" : " uses")
				+ " of the editor's window code by the track checker: it would need a display, or the editor's classes");
			System.exit(1);
		}
		catch (IOException e)
		{
			System.err.println("Couldn't check the track checker's classes: " + e.getLocalizedMessage());
			System.exit(2);
		}
	}

	/**
	 * @return what the classes the roots reach use that they mustn't, each with how
	 *         the first root reaches it
	 */
	static List<String> check() throws IOException
	{
		// the editor's classes are where this one is: the jar or class folder
		String home = location(ImportGuard.class.getName().replace('.', '/'));
		if (home == null)
			throw new IOException("can't find ImportGuard's own class file");

		Map<String, String> reachedFrom = new HashMap<String, String>();
		ArrayDeque<String> queue = new ArrayDeque<String>();
		for (String root : ROOTS)
		{
			if (!home.equals(location(root)))
				throw new IOException("can't find " + root + " next to ImportGuard");
			reachedFrom.put(root, null);
			queue.add(root);
		}

		Set<String> problems = new TreeSet<String>();
		while (!queue.isEmpty())
		{
			String name = queue.poll();
			for (String used : uses(name))
			{
				if (used.equals(name))
					continue;
				if (forbidden(used))
				{
					problems.add(name.replace('/', '.') + " uses " + used.replace('/', '.') + "  (" + path(name, reachedFrom) + ")");
					continue;
				}
				// follow only the editor's own classes: Java's and the libraries' aren't checked
				if (!reachedFrom.containsKey(used) && home.equals(location(used)))
				{
					reachedFrom.put(used, name);
					queue.add(used);
				}
			}
		}
		return new ArrayList<String>(problems);
	}

	private static boolean forbidden(String name)
	{
		for (String allowed : ALLOWED)
		{
			if (name.equals(allowed) || (allowed.endsWith("/") && name.startsWith(allowed)))
				return false;
		}
		for (String forbidden : FORBIDDEN)
		{
			if (name.startsWith(forbidden))
				return true;
		}
		return false;
	}

	/** @return how the roots reach the class, like TrackCheck > TrackChecker > GameTrack */
	private static String path(String name, Map<String, String> reachedFrom)
	{
		List<String> path = new ArrayList<String>();
		for (String at = name; at != null; at = reachedFrom.get(at))
			path.add(at.substring(at.lastIndexOf('/') + 1));
		Collections.reverse(path);
		return String.join(" > ", path);
	}

	/** @return the jar or folder the class file is in, or null when there is none */
	private static String location(String name)
	{
		URL url = ImportGuard.class.getClassLoader().getResource(name + ".class");
		if (url == null)
			return null;
		String text = url.toString();
		// jar:file:/x/trackeditor.jar!/utils/X.class, or file:/x/classes/utils/X.class
		int bang = text.indexOf("!/");
		if (bang >= 0)
			return text.substring(0, bang);
		return text.endsWith(name + ".class") ? text.substring(0, text.length() - name.length() - ".class".length()) : text;
	}

	/**
	 * @return the classes a class file names: its class constants, and the classes in
	 *         its fields', methods' and generics' descriptors and signatures
	 */
	private static Set<String> uses(String name) throws IOException
	{
		Set<String> used = new LinkedHashSet<String>();
		InputStream stream = ImportGuard.class.getClassLoader().getResourceAsStream(name + ".class");
		if (stream == null)
			return used;
		try (DataInputStream in = new DataInputStream(stream))
		{
			if (in.readInt() != 0xCAFEBABE)
				throw new IOException(name + ".class isn't a class file");
			in.readUnsignedShort();
			in.readUnsignedShort();
			int count = in.readUnsignedShort();
			String[] utf8 = new String[count];
			List<Integer> classes = new ArrayList<Integer>();
			for (int i = 1; i < count; i++)
			{
				int tag = in.readUnsignedByte();
				switch (tag)
				{
				case 1:		// UTF-8
					utf8[i] = in.readUTF();
					break;
				case 7:		// class
					classes.add(in.readUnsignedShort());
					break;
				case 8:		// string
				case 16:	// method type
				case 19:	// module
				case 20:	// package
					in.readUnsignedShort();
					break;
				case 3:		// integer
				case 4:		// float
				case 9:		// field
				case 10:	// method
				case 11:	// interface method
				case 12:	// name and type
				case 17:	// dynamic
				case 18:	// invoke dynamic
					in.readInt();
					break;
				case 5:		// long
				case 6:		// double
					in.readLong();
					i++;
					break;
				case 15:	// method handle
					in.readUnsignedByte();
					in.readUnsignedShort();
					break;
				default:
					throw new IOException(name + ".class has an unknown constant " + tag);
				}
			}
			for (int index : classes)
			{
				String className = utf8[index];
				// an array's class: [Ljava/lang/String;
				Matcher array = DESCRIPTOR.matcher(className);
				if (className.startsWith("["))
				{
					while (array.find())
						used.add(array.group(1));
				}
				else
				{
					used.add(className);
				}
			}
			// descriptors and signatures: (Ljavax/swing/JPanel;)V, Ljava/util/List<Lgui/X;>;
			for (String text : utf8)
			{
				if (text == null || text.indexOf('L') < 0)
					continue;
				Matcher matcher = DESCRIPTOR.matcher(text);
				while (matcher.find())
				{
					String found = matcher.group(1);
					if (found.indexOf('/') > 0)
						used.add(found);
				}
			}
		}
		return used;
	}
}
