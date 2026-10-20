/*
 *   AtomicFile.java
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Replaces a file only once its new contents are completely written, so a
 * failure part way through can't leave a truncated file behind.
 */
public final class AtomicFile
{
	public interface Writer
	{
		/**
		 * Writes the complete new contents to the given (temporary) file.
		 */
		void write(File file) throws IOException;
	}

	private AtomicFile()
	{
	}

	/**
	 * Writes <file>.tmp next to the target with the writer, then moves it over the target.
	 * The temporary file is removed if anything fails.
	 */
	public static void write(Path target, Writer writer) throws IOException
	{
		target = target.toAbsolutePath();
		Path temp = target.resolveSibling(target.getFileName() + ".tmp");

		try
		{
			writer.write(temp.toFile());

			try
			{
				Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			}
			catch (IOException e)
			{
				// not every file system can replace atomically
				Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
			}
		}
		finally
		{
			Files.deleteIfExists(temp);
		}
	}
}
