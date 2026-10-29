/*
 *   SplashWindow.java
 *   Created on Dec 4, 2004
 *
 *    The SplashWindow.java is part of TrackEditor-0.3.0.
 *
 *    TrackEditor-0.3.0 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.3.0 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.3.0; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package gui.splash;

import java.awt.BorderLayout;
import java.awt.Cursor;

import javax.swing.JWindow;

/**
 * @author babis
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SplashScreen extends JWindow
{
	private static SplashScreen	instance;

	private SplashPanel			panel	= null;
	private static boolean		doSplash;

	public static synchronized SplashScreen getInstance()
	{
		if (!doSplash)
			return null;
		if (instance == null)
		{
			instance = new SplashScreen("Loading TrackEditor...", "Splash.png");
		}
		return instance;
	}

	private SplashScreen(String title, String iconName)
	{
		super();

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		getContentPane().setLayout(new BorderLayout(0, 0));

		panel = new SplashPanel(iconName);
		panel.setStatus(title);

		getContentPane().add(panel);
		pack();
		// stay above the editor window when it opens, instead of going behind it and
		// coming back (a blink)
		setAlwaysOnTop(true);
		// center the whole window, once it has its size
		setLocationRelativeTo(null);
	}

	/**
	 * The editor is built on the event dispatch thread, which can't paint while
	 * it is busy building, so paint the splash now.
	 */
	private void paintNow()
	{
		if (isShowing())
			getRootPane().paintImmediately(0, 0, getRootPane().getWidth(), getRootPane().getHeight());
	}

	////////////////////////////////////////////////////////////////

	public void setStatus(String s)
	{
		panel.setStatus(s);
		paintNow();
	}

	/**
	 * @param i
	 */
	public void incProgress(int i)
	{
		panel.incProgress(i);
		paintNow();
	}

	public void setVisible(boolean b)
	{
		super.setVisible(b);
		if (b)
			paintNow();
	}

	/** A closed splash can't be shown again, so the next one is made new */
	public void dispose()
	{
		super.dispose();
		synchronized (SplashScreen.class)
		{
			if (instance == this)
				instance = null;
		}
	}

	public static void setDoSplash(boolean dosplash)
	{
		doSplash = dosplash;
	}

	public static boolean getDoSplash()
	{
		return doSplash;
	}

}