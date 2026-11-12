/*
 *   GameType.java
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

/** The game the tracks are edited for */
public enum GameType
{
	SPEED_DREAMS("Speed Dreams", "Speed Dreams", "sd2-trackgen", "sd2-accc"),
	CARS_SPORTS_RACING("Cars Sports Racing", "CSR", "csr-trackgen", "csr-accc"),
	TORCS("TORCS", "TORCS", "trackgen", "accc");

	private final String	name;
	private final String	shortName;
	private final String	trackgen;
	private final String	accc;

	GameType(String name, String shortName, String trackgen, String accc)
	{
		this.name = name;
		this.shortName = shortName;
		this.trackgen = trackgen;
		this.accc = accc;
	}

	/** @return the game's accc program, which makes a .acc track from trackgen's .ac files */
	public String getAccc()
	{
		return accc;
	}

	/** @return the game's name, like "Cars Sports Racing" */
	public String getName()
	{
		return name;
	}

	/** @return a short name for narrow labels, like "CSR" */
	public String getShortName()
	{
		return shortName;
	}

	/** @return the game's trackgen program */
	public String getTrackgen()
	{
		return trackgen;
	}

	/**
	 * @return whether the game's trackgen can make acc files itself (only Speed
	 * Dreams': the others make them with accc from trackgen's .ac files)
	 */
	public boolean canExportAcc()
	{
		return this == SPEED_DREAMS;
	}

	/**
	 * @return why the game can't load a texture with this name's format, or
	 *         null. TORCS loads .png and .rgb; Speed Dreams and CSR load .png,
	 *         .jpg and the SGI formats (.rgb, .rgba, .int, .inta, .bw)
	 *         everywhere, and other formats (like .jpeg, .bmp or .dds) only in
	 *         their OpenSceneGraph graphics, not the older ones or trackgen.
	 */
	public String textureFormatProblem(String texture)
	{
		if (texture == null)
			return null;
		String name = texture.trim().replace('\\', '/');
		name = name.substring(name.lastIndexOf('/') + 1);
		int dot = name.lastIndexOf('.');
		if (dot < 0 || dot == name.length() - 1)
			return null;
		String extension = name.substring(dot + 1).toLowerCase(java.util.Locale.ROOT);
		if (this == TORCS)
		{
			if (extension.equals("png") || extension.equals("rgb"))
				return null;
			return "TORCS only loads .png and .rgb textures";
		}
		if (java.util.Arrays.asList("png", "jpg", "rgb", "rgba", "int", "inta", "bw").contains(extension))
			return null;
		String osg = this == SPEED_DREAMS ? "osggraph" : "opengl3";
		String old = this == SPEED_DREAMS ? "ssggraph" : "opengl1";
		return "only " + shortName + (shortName.endsWith("s") ? "' " : "'s ") + osg + " graphics load ." + extension + " textures, not " + old
			+ " or trackgen: use .png, .jpg or .rgb";
	}

	/**
	 * @return whether the game also lists the tracks in the user's own folder (only
	 *         Speed Dreams: its local directory's tracks folder, as well as its data
	 *         directory's)
	 */
	public boolean hasLocalTracks()
	{
		return this == SPEED_DREAMS;
	}

	/**
	 * @return the folder the game keeps the user's settings and tracks in when it isn't
	 *         given --localdir: Speed Dreams' <Documents>\speed-dreams-2.settings on
	 *         Windows, ~/.speed-dreams-2 elsewhere; null for the other games
	 */
	public java.io.File defaultLocalDirectory()
	{
		if (!hasLocalTracks())
			return null;
		String home = System.getProperty("user.home");
		if (System.getProperty("os.name", "").startsWith("Windows"))
			return new java.io.File(new java.io.File(home, "Documents"), "speed-dreams-2.settings");
		return new java.io.File(home, ".speed-dreams-2");
	}

	/** @return whether trackgen can leave out the start bridge, -nb (not TORCS') */
	public boolean hasNoBridgeTrackgen()
	{
		return this != TORCS;
	}

	/** @return whether trackgen has -q, quiet mode (only TORCS') */
	public boolean hasQuietTrackgen()
	{
		return this == TORCS;
	}

	/** @return whether the game has the fence1 and fence2 barrier styles (only Speed Dreams) */
	public boolean hasFence1Fence2()
	{
		return this == SPEED_DREAMS;
	}

	/** @return whether the game has the "no barrier" barrier style (only Speed Dreams) */
	public boolean hasNoBarrier()
	{
		return this == SPEED_DREAMS;
	}

	/** @return whether segments have a "penalty" (only CSR) */
	public boolean hasSegmentPenalty()
	{
		return this == CARS_SPORTS_RACING;
	}

	/** @return whether the header has a "short name" (only CSR) */
	public boolean hasShortName()
	{
		return this == CARS_SPORTS_RACING;
	}

	/** @return whether the header has a "car category" (only CSR) */
	public boolean hasCarCategory()
	{
		return this == CARS_SPORTS_RACING;
	}

	/** @return whether the header has "disabled penalties" (only Speed Dreams) */
	public boolean hasDisabledPenalties()
	{
		return this == SPEED_DREAMS;
	}

	/** @return whether Local Info has a "best lap" (only CSR) */
	public boolean hasBestLap()
	{
		return this == CARS_SPORTS_RACING;
	}

	/** @return whether the graphic section has the per-type 3d object files (only CSR) */
	public boolean hasTypeObjectFiles()
	{
		return this == CARS_SPORTS_RACING;
	}

	/** @return whether trackgen reads an object's "use material" (only Speed Dreams') */
	public boolean hasUseMaterial()
	{
		return this == SPEED_DREAMS;
	}

	/** @return whether trackgen reads the terrain's "tiled file" (only Speed Dreams') */
	public boolean hasTiledFile()
	{
		return this == SPEED_DREAMS;
	}

	/** @return whether trackgen places individual graphic objects (only Speed Dreams'): the others only have object maps */
	public boolean hasGraphicObjects()
	{
		return this == SPEED_DREAMS;
	}

	/**
	 * @return whether a segment side's "width" sets both its widths (not TORCS: there it
	 * is the end width, and the start width is "start width" or the previous end width)
	 */
	public boolean widthSetsBothSideWidths()
	{
		return this != TORCS;
	}

	/**
	 * @return whether the game has what Speed Dreams added to the track file: Local
	 * Info, Sectors, Track Lights, the header's subcategory, email and copyright and a
	 * segment's DoV factor (not TORCS: it reads none of them)
	 */
	public boolean hasSpeedDreamsExtensions()
	{
		return this != TORCS;
	}

	/**
	 * @return whether trackgen reads the terrain's "use object materials" and an
	 * object's border distance and scale settings (not TORCS')
	 */
	public boolean hasSpeedDreamsTrackgenKeys()
	{
		return this != TORCS;
	}

	/**
	 * @return whether trackgen reads the terrain transition, the crossing bridge
	 * settings and a surface's "texture size v" (only TORCS')
	 */
	public boolean hasTorcsTrackgenKeys()
	{
		return this == TORCS;
	}

	/**
	 * @return whether a segment barrier style other than fence is a wall (TORCS: there
	 * are only fences and walls); the others ignore a style they don't know
	 */
	public boolean otherBarrierStylesAreWalls()
	{
		return this == TORCS;
	}

	/**
	 * @return whether a plan border sets the border height to 0 (not TORCS: it doesn't
	 * use the height for a plan border, but keeps it for the borders after it)
	 */
	public boolean planBorderResetsHeight()
	{
		return this != TORCS;
	}

	/**
	 * @return whether the pits have a style, buildings, max pits and an indicator (not
	 * TORCS: it only has track side pits)
	 */
	public boolean hasPitOptions()
	{
		return this != TORCS;
	}

	/** @return the track categories: the directories the game's tracks are in */
	public String[] getTrackCategories()
	{
		switch (this)
		{
			case CARS_SPORTS_RACING:
				return new String[] {"circuit", "development", "dirt", "grandprix", "road", "speedway"};
			case TORCS:
				return new String[] {"dirt", "experimental", "oval", "road", "test"};
			default:
				return new String[] {"circuit", "development", "dirt", "gprix", "karting", "road", "speedway"};
		}
	}

	public String toString()
	{
		return name;
	}
}
