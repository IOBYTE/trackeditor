package utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Reads SGI RGB image files (.rgb, .rgba, .sgi, .bw) into a BufferedImage.
 * Supports RLE compressed and uncompressed images with 1 byte per channel.
 */
public class SgiReader
{
	private static final int MAGIC = 474;
	private static final int HEADER_SIZE = 512;
	private static final int STORAGE_RLE = 1;

	/**
	 * @return true if the file has an SGI image extension
	 */
	public static boolean isSgiFile(String name)
	{
		if (name == null)
			return false;
		String lower = name.toLowerCase();
		return lower.endsWith(".rgb") || lower.endsWith(".rgba")
			|| lower.endsWith(".sgi") || lower.endsWith(".bw");
	}

	/**
	 * Reads an SGI RGB file into a BufferedImage.
	 *
	 * @param file the SGI image file
	 * @return the image
	 * @throws IOException if the file can't be read or isn't a valid SGI image
	 */
	public static BufferedImage read(File file) throws IOException
	{
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		try
		{
			// header
			int magic = raf.readShort() & 0xFFFF;
			if (magic != MAGIC)
				throw new IOException("Not an SGI image: bad magic number");

			int storage = raf.readByte() & 0xFF;
			int bpc = raf.readByte() & 0xFF;
			if (bpc != 1)
				throw new IOException("Only 1 byte per channel SGI images are supported");

			raf.readShort(); // dimension (unused)
			int width = raf.readShort() & 0xFFFF;
			int height = raf.readShort() & 0xFFFF;
			int channels = raf.readShort() & 0xFFFF;

			if (width <= 0 || height <= 0 || channels <= 0 || channels > 4)
				throw new IOException("Invalid SGI image dimensions: " + width + "x" + height + "x" + channels);

			boolean hasAlpha = channels >= 4;
			int imageType = hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
			BufferedImage image = new BufferedImage(width, height, imageType);

			byte[][] channelData = new byte[channels][width * height];

			if (storage == STORAGE_RLE)
				readRLE(raf, channelData, width, height, channels);
			else
				readUncompressed(raf, channelData, width, height, channels);

			// SGI images are stored bottom-to-top
			for (int y = 0; y < height; y++)
			{
				int srcRow = y * width;
				int dstY = height - 1 - y;
				for (int x = 0; x < width; x++)
				{
					int r = channelData[0][srcRow + x] & 0xFF;
					int g = channels > 1 ? channelData[1][srcRow + x] & 0xFF : r;
					int b = channels > 2 ? channelData[2][srcRow + x] & 0xFF : r;
					int a = hasAlpha ? channelData[3][srcRow + x] & 0xFF : 0xFF;
					image.setRGB(x, dstY, (a << 24) | (r << 16) | (g << 8) | b);
				}
			}

			return image;
		}
		finally
		{
			raf.close();
		}
	}

	private static void readUncompressed(RandomAccessFile raf, byte[][] channelData,
		int width, int height, int channels) throws IOException
	{
		raf.seek(HEADER_SIZE);
		for (int c = 0; c < channels; c++)
		{
			raf.readFully(channelData[c]);
		}
	}

	private static void readRLE(RandomAccessFile raf, byte[][] channelData,
		int width, int height, int channels) throws IOException
	{
		int tableEntries = height * channels;

		// read offset and length tables
		raf.seek(HEADER_SIZE);
		int[] offsets = new int[tableEntries];
		int[] lengths = new int[tableEntries];
		for (int i = 0; i < tableEntries; i++)
			offsets[i] = raf.readInt();
		for (int i = 0; i < tableEntries; i++)
			lengths[i] = raf.readInt();

		// decode each scanline
		byte[] buf = new byte[width * 2 + 2];
		for (int c = 0; c < channels; c++)
		{
			for (int y = 0; y < height; y++)
			{
				int tableIndex = y + c * height;
				raf.seek(offsets[tableIndex]);
				int len = lengths[tableIndex];
				if (len > buf.length)
					buf = new byte[len];
				raf.readFully(buf, 0, len);

				int dstOffset = y * width;
				int dst = 0;
				int src = 0;
				while (src < len && dst < width)
				{
					int pixel = buf[src++] & 0xFF;
					int count = pixel & 0x7F;
					if (count == 0)
						break;
					if ((pixel & 0x80) != 0)
					{
						// copy count literal bytes
						for (int i = 0; i < count && dst < width; i++)
							channelData[c][dstOffset + dst++] = buf[src++];
					}
					else
					{
						// repeat next byte count times
						byte value = buf[src++];
						for (int i = 0; i < count && dst < width; i++)
							channelData[c][dstOffset + dst++] = value;
					}
				}
			}
		}
	}
}
