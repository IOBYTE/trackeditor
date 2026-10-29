/*
 *   RoadTexture.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package utils.circuit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gui.EditorFrame;
import utils.Editor;
import utils.SegmentVector;
import utils.TrackData;

/**
 * The main track road's texture and texture coordinates, worked out as trackgen
 * does it (the main track in tools/trackgen/track.cpp), so the 3D view and Dump
 * Ac3d look like the game.
 * <p>
 * u runs along the road and v across it, in texture sizes: v is 0 on the right
 * edge and the main track width over the size on the left (a whole number plus
 * one for a discrete texture). u is the distance from the start of the track, or
 * from the segment's start when the texture isn't linked with the previous one,
 * less the distance at which the texture began when it starts on the boundary.
 * Like trackgen, a surface's mapping is only read when the texture changes, and
 * a missing surface or value takes trackgen's default (TORCS' default texture
 * is an .rgb).
 */
public class RoadTexture
{
	public static final String	DEFAULT_TEXTURE	= "tr-asphalt.png";
	public static final String	DEFAULT_TORCS_TEXTURE	= "tr-asphalt.rgb";
	public static final double	DEFAULT_SIZE	= 20;

	/** the segment's texture */
	public final String			texture;
	/** u at the segment's start and end: steps in between are evenly spaced, as in the game */
	public final double			uStart, uEnd;
	/** v on the left edge */
	public final double			vLeft;

	private RoadTexture(String texture, double uStart, double uEnd, double vLeft)
	{
		this.texture = texture;
		this.uStart = uStart;
		this.uEnd = uEnd;
		this.vLeft = vLeft;
	}

	/** @return u at a step boundary, from 0 at the segment's start to steps at its end */
	public double u(int step, int steps)
	{
		return steps <= 0 ? uStart : uStart + (uEnd - uStart) * step / steps;
	}

	/** @return the texture mapping of each segment of the track, in order */
	public static List<RoadTexture> of(EditorFrame editorFrame)
	{
		List<RoadTexture> mappings = new ArrayList<RoadTexture>();
		TrackData trackData = editorFrame.getTrackData();
		if (trackData == null || trackData.getSegments() == null)
			return mappings;

		SegmentVector segments = trackData.getSegments();
		double width = trackData.getMainTrack().getValidWidth();
		String defaultTexture = editorFrame.getGameType() == EditorFrame.GameType.TORCS ? DEFAULT_TORCS_TEXTURE : DEFAULT_TEXTURE;
		double fromStart = 0;
		String texture = null;
		boolean continuous = true, link = true;
		double size = DEFAULT_SIZE, offset = 0;

		for (Segment segment : segments)
		{
			Surface surface = findSurface(editorFrame, segment.getValidSurface(editorFrame));
			String name = surface == null || surface.getTextureName() == null ? defaultTexture : surface.getTextureName();
			if (!name.equals(texture))
			{
				texture = name;
				// trackgen compares the text: anything else is the other choice
				continuous = surface == null || surface.getTextureType() == null || surface.getTextureType().equals("continuous");
				link = surface == null || surface.getTextureLinkWithPrevious() == null || surface.getTextureLinkWithPrevious().equals("yes");
				offset = surface != null && "yes".equals(surface.getTextureStartOnBoundary()) ? -fromStart : 0;
				size = surface == null || Double.isNaN(surface.getTextureSize()) ? DEFAULT_SIZE : surface.getTextureSize();
				if (size <= 0)
					size = DEFAULT_SIZE;
			}
			double length = gameLength(segment);
			double start = (link ? fromStart : 0) + offset;
			double vLeft = continuous ? width / size : 1 + Math.floor(width / size);
			mappings.add(new RoadTexture(texture, start / size, (start + length) / size, vLeft));
			fromStart += length;
		}
		return mappings;
	}

	/** @return the segment's length along the middle of the road, as the game's track loader calculates it */
	public static double gameLength(Segment segment)
	{
		// the game's steps, worked out when the segment was drawn: for a curve whose radius
		// changes, the step length is corrected so the steps turn through the whole arc
		if (segment.nbSteps > 0 && !Double.isNaN(segment.stepLength))
			return Math.abs(segment.stepLength * segment.nbSteps);
		double length;
		if (segment instanceof Curve)
		{
			Curve curve = (Curve) segment;
			double end = curve.hasRadiusEnd() ? curve.getRadiusEnd() : curve.getRadiusStart();
			length = curve.getArcRad() * (curve.getRadiusStart() + end) / 2;
		}
		else
			length = segment.getLength();
		return Double.isNaN(length) ? 0 : Math.abs(length);
	}

	/** @return the track's surface with this name, or else the default one, or null */
	public static Surface findSurface(EditorFrame editorFrame, String name)
	{
		for (Surface surface : editorFrame.getTrackData().getSurfaces())
		{
			if (name.equals(surface.getName()))
				return surface;
		}
		for (Surface surface : editorFrame.getDefaultSurfaces())
		{
			if (name.equals(surface.getName()))
				return surface;
		}
		return null;
	}

	/** @return the texture in the track's folder or the game's textures folder, or null */
	public static File findTextureFile(EditorFrame editorFrame, String texture)
	{
		File file = new File(Editor.getProperties().getPath(), texture);
		if (file.isFile())
			return file;
		String data = editorFrame.getDataDirectory();
		if (data != null && !data.isEmpty())
		{
			file = new File(data + File.separator + "data" + File.separator + "textures", texture);
			if (file.isFile())
				return file;
		}
		return null;
	}
}
