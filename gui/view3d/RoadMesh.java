/*
 *   RoadMesh.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package gui.view3d;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gui.EditorFrame;
import utils.circuit.RoadTexture;

/**
 * What the 3D view draws: the track's road, borders, sides and barriers as
 * the game's trackgen makes them (see {@link TrackScene}), with the textures
 * they use and the space they take. It is made from the editor's segment
 * points, and copied, so the view can draw while the track is being edited.
 */
public class RoadMesh
{
	/** the strips; null when no track is open */
	public final TrackScene	scene;
	public final int		segmentCount;
	public final int		triangleCount;
	/** the textures the strips and their raceline layer use, and their files (null when one can't be found) */
	public final String[]	textureNames;
	public final File[]		textureFiles;
	public final double		minX, maxX, minY, maxY, minZ, maxZ;
	/** the track's cameras */
	public final List<GameTrack.CameraView>	cameras;

	private RoadMesh(TrackScene scene, int segmentCount, EditorFrame editorFrame)
	{
		this.scene = scene;
		this.segmentCount = segmentCount;
		cameras = scene == null ? new ArrayList<GameTrack.CameraView>() : scene.track.cameras(editorFrame.getTrackData().getCameras());
		List<String> names = new ArrayList<String>();
		List<File> files = new ArrayList<File>();
		double x0 = Double.MAX_VALUE, x1 = -Double.MAX_VALUE, y0 = Double.MAX_VALUE, y1 = -Double.MAX_VALUE;
		double z0 = Double.MAX_VALUE, z1 = -Double.MAX_VALUE;
		int triangles = 0;
		if (scene != null)
		{
			for (TrackScene.Strip strip : scene.strips)
			{
				triangles += strip.triangles().length / 3;
				for (String texture : new String[] { strip.texture, strip.racelineTexture })
				{
					if (texture != null && !names.contains(texture))
					{
						names.add(texture);
						files.add(RoadTexture.findTextureFile(editorFrame, texture));
					}
				}
				for (int i = 0; i < strip.size(); i++)
				{
					x0 = Math.min(x0, strip.x(i));
					x1 = Math.max(x1, strip.x(i));
					y0 = Math.min(y0, strip.y(i));
					y1 = Math.max(y1, strip.y(i));
					z0 = Math.min(z0, strip.z(i));
					z1 = Math.max(z1, strip.z(i));
				}
			}
		}
		if (triangles == 0)
		{
			x0 = x1 = y0 = y1 = z0 = z1 = 0;
		}
		triangleCount = triangles;
		textureNames = names.toArray(new String[0]);
		textureFiles = files.toArray(new File[0]);
		minX = x0;
		maxX = x1;
		minY = y0;
		maxY = y1;
		minZ = z0;
		maxZ = z1;
	}

	/**
	 * @param raceline with the road's raceline layer
	 * @return what the open track looks like, empty when there is none
	 */
	public static RoadMesh build(EditorFrame editorFrame, boolean raceline)
	{
		TrackScene scene = TrackScene.build(editorFrame, raceline);
		int segments = editorFrame.getTrackData() == null || editorFrame.getTrackData().getSegments() == null ? 0 : editorFrame.getTrackData().getSegments().size();
		return new RoadMesh(scene, segments, editorFrame);
	}

	public boolean isEmpty()
	{
		return triangleCount == 0;
	}

	/** @return half the diagonal of the track seen from above */
	public double getRadius()
	{
		return Math.max(1, Math.hypot(maxX - minX, maxY - minY) / 2);
	}
}
