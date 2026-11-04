/*
 *   TrackEditor.java
 *   Created on Aug 26, 2004
 *
 *    The TrackEditor.java is part of TrackEditor.
 *
 *    TrackEditor is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package utils;

import java.awt.Toolkit;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertBlue;

import gui.EditorFrame;
import miscel.TCPopupEventQueue;


/**
 * @author babis
 *
 */
public class TrackEditor
{
	private static EditorFrame gui;

	public static String executableName = null;

	/**
	 * @return the editor's name: csr-trackeditor when run as that, otherwise
	 * sd2-trackeditor (also when run some other way, like from Eclipse)
	 */
	public static String getAppName()
	{
		if ("csr-trackeditor".equals(executableName))
			return "csr-trackeditor";

		return Properties.getInstance().title;
	}

	/**
	 * Relaunches the JVM with a UTF-8 locale if file names are in a non-UTF-8
	 * encoding, which makes Swing's JFileChooser crash on non-ASCII file names
	 * (e.g. a home folder named "Música" on Linux with the C or POSIX locale).
	 * Only Linux and other Unix systems need this: Windows file names don't
	 * go through that encoding, and macOS always uses UTF-8.
	 *
	 * @param jarPath the jar file the editor was run from
	 */
	private static void relaunchForUtf8(String jarPath, String[] args)
	{
		if (utf8FileNames())
			return;

		// already relaunched and it didn't help: don't loop
		if ("true".equals(System.getProperty("trackeditor.utf8.relaunched")))
			return;

		try
		{
			String java = System.getProperty("java.home") + File.separator
				+ "bin" + File.separator + "java";

			List<String> cmd = new ArrayList<String>();
			cmd.add(java);
			// keep the options this JVM was started with (memory, -D settings...)
			cmd.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
			cmd.add("-Dfile.encoding=UTF-8");
			cmd.add("-Dsun.jnu.encoding=UTF-8");
			cmd.add("-Dtrackeditor.utf8.relaunched=true");
			cmd.add("-jar");
			cmd.add(jarPath);
			cmd.addAll(Arrays.asList(args));

			ProcessBuilder builder = new ProcessBuilder(cmd).inheritIO();
			// the file name encoding comes from the locale; the -D settings
			// above don't change it on newer Java versions
			builder.environment().put("LC_ALL", "C.UTF-8");
			Process p = builder.start();
			System.exit(p.waitFor());
		}
		catch (Exception e)
		{
			// can't relaunch (e.g. no jar, or security manager): continue
			// with the current encoding and hope for the best
			System.err.println("Could not relaunch for UTF-8: " + e);
		}
	}

	/**
	 * @return the jar (or class folder) the editor runs from. The jar as it was
	 *         given to java when it can be found that way: it works even when the
	 *         file name encoding can't hold the folders' names. Otherwise the code
	 *         source's location, decoded (a space in it is %20).
	 */
	private static File codeSource()
	{
		String classPath = System.getProperty("java.class.path", "");
		File given = new File(classPath);
		if (!classPath.isEmpty() && !classPath.contains(File.pathSeparator) && given.isFile())
			return given;
		java.net.URL location = TrackEditor.class.getProtectionDomain().getCodeSource().getLocation();
		try
		{
			return new File(location.toURI());
		}
		catch (Exception e)
		{
			try
			{
				return new File(java.net.URLDecoder.decode(location.getPath(), "UTF-8"));
			}
			catch (Exception e2)
			{
				return new File(location.getPath());
			}
		}
	}

	/** @return whether file names are in UTF-8, or don't need to be (Windows, macOS) */
	private static boolean utf8FileNames()
	{
		String os = System.getProperty("os.name", "").toLowerCase();
		if (os.startsWith("windows") || os.startsWith("mac"))
			return true;
		String encoding = System.getProperty("sun.jnu.encoding", "");
		return encoding.equalsIgnoreCase("UTF-8") || encoding.equalsIgnoreCase("UTF8");
	}

	public static void main(String[] args)
	{
		File jarFile = codeSource();
		executableName = jarFile.getName();
		// ignore the extension (csr-trackeditor.jar -> csr-trackeditor)
		int dot = executableName.lastIndexOf('.');
		if (dot > 0)
			executableName = executableName.substring(0, dot);

		// only relaunch when running from a jar (not from a class directory)
		if (jarFile.isFile())
			relaunchForUtf8(jarFile.getPath(), args);

		Toolkit.getDefaultToolkit().getSystemEventQueue().push(new TCPopupEventQueue());
		
		PlasticLookAndFeel.setCurrentTheme(new DesertBlue());
		try
		{
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		} catch (Exception e)
		{
		}
		System.out.println("Track editor    : "+Properties.getInstance().version);
		System.out.println("Java version    : "+System.getProperty("java.version"));
		System.out.println("OS              : "+System.getProperty("os.name"));
		System.out.println("OS architecture : "+System.getProperty("os.arch"));
		System.out.println("OS version      : "+System.getProperty("os.version"));
		// relaunching didn't help (no C.UTF-8 locale) or wasn't possible
		if (!utf8FileNames())
			System.err.println("File names are in " + System.getProperty("sun.jnu.encoding") + ", not UTF-8, so files and folders"
				+ " with accented letters in their names can't be opened. Start the editor with a UTF-8 locale,"
				+ " such as: LC_ALL=C.UTF-8 java -jar trackeditor.jar");
		// Swing windows must be made and used on the event dispatch thread
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				gui = new EditorFrame();
			}
		});
	}
}