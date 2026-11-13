/*
 *   AppName.java
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
package utils;

/**
 * The editor's name, as its jar is named, for its title and the files it
 * writes. Without Swing, so the track checker uses it too (see TrackEditor).
 */
public final class AppName
{
	/**
	 * The jar's name without .jar, like csr-trackeditor: set when the editor or the
	 * checker starts from its jar, null when run another way (like from Eclipse)
	 */
	public static String executableName = null;

	private AppName()
	{
	}

	/**
	 * @return the editor's name: csr-trackeditor when run as that, otherwise
	 * sd2-trackeditor (also when run some other way, like from Eclipse)
	 */
	public static String get()
	{
		if ("csr-trackeditor".equals(executableName))
			return "csr-trackeditor";

		return Properties.getInstance().title;
	}
}
