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
	/** where the track's cameras are, for their markers */
	public final List<CameraMarker>			cameraMarkers;

	/** A track camera's marker: where it stands, which way it looks, its color and its name. */
	public static final class CameraMarker
	{
		public final String		name;
		/** x, y, the ground's height and the camera's height above it */
		public final float		x, y, ground, height;
		/** toward where its stretch starts, flat */
		public final float		aimX, aimY;
		public final float[]	color;
		/** its name, white on its color; null when it couldn't be made */
		public final File		nameImage;

		CameraMarker(String name, float x, float y, float ground, float height, float aimX, float aimY, float[] color, File nameImage)
		{
			this.name = name;
			this.x = x;
			this.y = y;
			this.ground = ground;
			this.height = height;
			this.aimX = aimX;
			this.aimY = aimY;
			this.color = color;
			this.nameImage = nameImage;
		}
	}

	// the cameras' colors, as the 2D view gives them: in the order they film round the lap
	private static final int[]	CAMERA_COLORS	= { 0x378ADD, 0x1D9E75, 0xD85A30, 0x7F77DD, 0xBA7517, 0xD4537E, 0x639922,
		0x1F6F8B, 0x2AA8BE, 0xC83C3C };
	// each name's image, made once
	private static final java.util.Map<String, File>	nameImages	= new java.util.HashMap<String, File>();

	private RoadMesh(TrackScene scene, int segmentCount, EditorFrame editorFrame)
	{
		this.scene = scene;
		this.segmentCount = segmentCount;
		cameras = scene == null ? new ArrayList<GameTrack.CameraView>() : scene.track.cameras(editorFrame.getTrackData().getCameras());
		cameraMarkers = scene == null ? new ArrayList<CameraMarker>() : cameraMarkers(scene.track, editorFrame.getTrackData().getCameras());
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
				for (String texture : new String[] { strip.texture, strip.racelineTexture, strip.textureOff })
				{
					if (texture != null && !texture.isEmpty() && !names.contains(texture))
					{
						names.add(texture);
						File generated = TrackScene.generatedTexture(texture);
						// one found here that isn't there isn't looked for with the track's textures
						boolean found = !TrackScene.textureDisplayName(texture).equals(texture);
						files.add(generated != null ? generated : found ? null : RoadTexture.findTextureFile(editorFrame, texture));
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

	/** @return the markers of the cameras the game can place: their segment is one */
	private static List<CameraMarker> cameraMarkers(GameTrack track, List<utils.circuit.Camera> cameras)
	{
		utils.circuit.Camera[] owners = track.cameraOwners(cameras);
		java.util.Map<utils.circuit.Camera, Integer> colors = new java.util.HashMap<utils.circuit.Camera, Integer>();
		for (utils.circuit.Camera owner : owners)
			if (owner != null && !colors.containsKey(owner))
				colors.put(owner, CAMERA_COLORS[colors.size() % CAMERA_COLORS.length]);
		for (utils.circuit.Camera camera : cameras)
			if (!colors.containsKey(camera))
				colors.put(camera, CAMERA_COLORS[colors.size() % CAMERA_COLORS.length]);
		List<CameraMarker> markers = new ArrayList<CameraMarker>();
		for (utils.circuit.Camera camera : cameras)
		{
			if (track.firstStep(camera.getSegment()) == null)
				continue;
			float[] eye = track.cameraEye(camera);
			// toward the middle of the road where its stretch starts
			float aimX = 1, aimY = 0;
			GameTrack.Seg start = track.firstStep(camera.getFovStart());
			if (start != null)
			{
				float[] at = GameTrack.local2Global(start, 0, start.width / 2);
				float dx = at[0] - eye[0], dy = at[1] - eye[1], d = (float) Math.hypot(dx, dy);
				if (d > 0.01f)
				{
					aimX = dx / d;
					aimY = dy / d;
				}
			}
			int rgb = colors.get(camera);
			float[] color = { ((rgb >> 16) & 255) / 255f, ((rgb >> 8) & 255) / 255f, (rgb & 255) / 255f };
			String name = camera.getName() == null ? "" : camera.getName();
			markers.add(new CameraMarker(name, eye[0], eye[1], eye[2], eye[3], aimX, aimY, color, nameImage(name, rgb)));
		}
		return markers;
	}

	/** @return an image of a camera's name, white on its color */
	private static File nameImage(String name, int rgb)
	{
		String key = name + "|" + rgb;
		synchronized (nameImages)
		{
			File file = nameImages.get(key);
			if (file != null && file.isFile())
				return file;
			try
			{
				int w = 40 * Math.max(4, name.length()), h = 96;
				java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_RGB);
				java.awt.Graphics2D g = image.createGraphics();
				g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.setColor(new java.awt.Color(rgb));
				g.fillRect(0, 0, w, h);
				g.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 64));
				java.awt.FontMetrics metrics = g.getFontMetrics();
				g.setColor(java.awt.Color.white);
				g.drawString(name, (w - metrics.stringWidth(name)) / 2, (h - metrics.getHeight()) / 2 + metrics.getAscent());
				g.dispose();
				file = File.createTempFile("trackeditor-camera-", ".png");
				file.deleteOnExit();
				javax.imageio.ImageIO.write(image, "png", file);
				nameImages.put(key, file);
				return file;
			}
			catch (Exception e)
			{
				return null;
			}
		}
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
