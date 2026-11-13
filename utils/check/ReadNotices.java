/*
 *   ReadNotices.java
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

/**
 * Where reading a track reports what the user has to know at once: a file it
 * couldn't read, a section the track doesn't have. The editor shows each in a
 * message box; reading without a window can print or collect them.
 */
public interface ReadNotices
{
	/**
	 * @param title what was being read, as a message box's title
	 * @param error whether it is an error rather than a warning
	 */
	void notice(String title, String message, boolean error);
}
