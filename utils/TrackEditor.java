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
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
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

		return Editor.getProperties().title;
	}

	public static void main(String[] args)
	{
		executableName = new File(TrackEditor.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
		// ignore the extension (csr-trackeditor.jar -> csr-trackeditor)
		int dot = executableName.lastIndexOf('.');
		if (dot > 0)
			executableName = executableName.substring(0, dot);

		Toolkit.getDefaultToolkit().getSystemEventQueue().push(new TCPopupEventQueue());
		
		PlasticLookAndFeel.setCurrentTheme(new DesertBlue());
		try
		{
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		} catch (Exception e)
		{
		}
		System.out.println("Java version    : "+System.getProperty("java.version"));
		System.out.println("OS              : "+System.getProperty("os.name"));
		System.out.println("OS architecture : "+System.getProperty("os.arch"));
		System.out.println("OS version      : "+System.getProperty("os.version"));
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