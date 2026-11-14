/*
 *   FileProblem.java
 *
 *    This file is part of the CSR track editor.
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Says why a file couldn't be read or written, in the editor's words and
 * without the file's path: the message it goes in names the file the way the
 * track does ("relief.ac : the file doesn't exist"), where Java's own text
 * repeats the whole path ("relief.ac : C:\...\relief.ac (The system cannot
 * find the file specified)").
 */
public final class FileProblem
{
	private FileProblem()
	{
	}

	/**
	 * @param e what went wrong
	 * @param file the file, null if it isn't known
	 * @param writing true if the file was being written
	 * @return why, starting in lower case
	 */
	public static String reason(Throwable e, Path file, boolean writing)
	{
		if (e instanceof InvalidPathException)
			return "it isn't a valid file name";

		boolean fileProblem = e instanceof FileNotFoundException || e instanceof FileSystemException;

		// the file itself says best what's wrong
		if (fileProblem && file != null)
		{
			File f = file.toFile();
			if (f.isDirectory())
				return "it's a folder, not a file";
			if (!writing && !f.exists())
				return "the file doesn't exist";
			Path parent = file.toAbsolutePath().getParent();
			if (writing && parent != null && !parent.toFile().isDirectory())
				return "its folder doesn't exist";
		}

		String cant = writing ? "it can't be written: " : "it can't be read: ";
		if (e instanceof AccessDeniedException)
			return cant + "access is denied";
		if (e instanceof NoSuchFileException)
			return writing ? "its folder doesn't exist" : "the file doesn't exist";
		if (e instanceof FileSystemException)
		{
			String why = ((FileSystemException) e).getReason();
			if (why != null && !why.isEmpty())
				return cant + startLower(why);
		}

		String message = e.getLocalizedMessage();
		if (e instanceof FileNotFoundException && message != null)
		{
			// "<path> (<why>)": only the why
			int open = message.lastIndexOf(" (");
			if (open >= 0 && message.endsWith(")"))
				return cant + startLower(message.substring(open + 2, message.length() - 1));
		}
		return message == null || message.isEmpty() ? e.getClass().getSimpleName() : message;
	}

	// a fault in a file, as Ac3d.read reports it: "line <n> <what>"
	private static final java.util.regex.Pattern	FAULT_LINE	= java.util.regex.Pattern.compile("line (\\d+) (.*)", java.util.regex.Pattern.DOTALL);

	/**
	 * @param name the file as the message names it
	 * @param file the file, null if it isn't known
	 * @return why the file couldn't be read, for a message: its name, the line
	 *         for a fault in it ("relief.ac line 12 : ..."), and why without the
	 *         file's path (see reason)
	 */
	public static String describe(String name, Exception e, Path file)
	{
		if (e instanceof utils.ac3d.Ac3dException)
		{
			int line = ((utils.ac3d.Ac3dException) e).getLineNumber();
			return name + (line > 0 ? " line " + line : "") + " : " + e.getLocalizedMessage();
		}
		String why = reason(e, file, false);
		java.util.regex.Matcher fault = FAULT_LINE.matcher(why);
		if (fault.matches())
			return name + " line " + fault.group(1) + " : " + fault.group(2);
		return name + " : " + why;
	}

	/**
	 * @return text with its first letter in lower case, unless it starts a
	 *         word in capitals ("JPEG")
	 */
	private static String startLower(String text)
	{
		if (text.length() > 1 && Character.isUpperCase(text.charAt(0)) && Character.isUpperCase(text.charAt(1)))
			return text;
		return text.isEmpty() ? text : Character.toLowerCase(text.charAt(0)) + text.substring(1);
	}
}
