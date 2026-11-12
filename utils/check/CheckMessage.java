/*
 *   CheckMessage.java
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

import java.util.regex.Pattern;

/**
 * A message of the track checks: its text, what it is about, and the kinds of
 * message the Check dialog can hide.
 */
public final class CheckMessage
{
	/**
	 * What the messages say about something in the file that saving removes: "saving
	 * removes it", or "saving for Speed Dreams removes it" (a game's name can have
	 * spaces)
	 */
	private static final Pattern	SAVING_MESSAGE		= Pattern.compile("\\bsaving (for .+? )?removes (it|this one)\\b");

	/** What the messages say about a texture that isn't found, the data folder set or not */
	private static final Pattern	TEXTURE_NOT_FOUND	= Pattern.compile(" texture .+ (not found$|isn't in the track's folder)");

	/** How the messages suggesting converting a texture to png end */
	public static final String		PNG_MESSAGE			= " should be converted to png format";

	private final String			text;
	private final Subject			subject;
	// about an object's model: its AC3D file or textures
	private final boolean			objectFile;
	// only suggests converting an .rgb texture to png
	private final boolean			png;
	// says the file has something that saving removes
	private final boolean			saving;
	// says a texture isn't found
	private final boolean			texture;

	/**
	 * @param subject what it is about, or null
	 * @param objectFile whether it's about an object's model: its AC3D file or textures
	 */
	public CheckMessage(String text, Subject subject, boolean objectFile)
	{
		this.text = text;
		this.subject = subject;
		this.objectFile = objectFile;
		this.png = text.endsWith(PNG_MESSAGE);
		this.saving = SAVING_MESSAGE.matcher(text).find();
		this.texture = TEXTURE_NOT_FOUND.matcher(text).find();
	}

	public String getText()
	{
		return text;
	}

	/** @return what it is about, or null for nothing that can be shown */
	public Subject getSubject()
	{
		return subject;
	}

	/** @return whether it's about an object's model: its AC3D file or textures */
	public boolean isObjectFile()
	{
		return objectFile;
	}

	/** @return whether it only suggests converting an .rgb texture to png */
	public boolean isPng()
	{
		return png;
	}

	/** @return whether it says the file has something that saving removes */
	public boolean isSaving()
	{
		return saving;
	}

	/** @return whether it says a texture isn't found */
	public boolean isTexture()
	{
		return texture;
	}

	@Override
	public String toString()
	{
		return text;
	}
}
