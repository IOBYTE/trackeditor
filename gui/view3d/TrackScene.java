/*
 *   TrackScene.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package gui.view3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gui.EditorFrame;
import utils.circuit.MainTrack;
import utils.circuit.RoadTexture;
import utils.circuit.Surface;

/**
 * The track's road, borders, sides and barriers as the game's trackgen makes
 * them: textured triangle strips, each an object of the exported track, with
 * trackgen's names, texture coordinates and one or two sided surfaces.
 * {@link SceneCsrSd} follows Speed Dreams' and CSR's trackgen (track.cpp),
 * {@link SceneTorcs} TORCS' (trackscene*.cpp).
 */
public abstract class TrackScene
{
	/** One of trackgen's objects: a triangle strip. */
	public static final class Strip
	{
		public String		name;
		public String		texture;
		/** the surface whose texture it has */
		public String		surface;
		/** the raceline layer's texture, when the road's surface has one */
		public String		racelineTexture;
		public boolean		doubleSided;
		/**
		 * one of two strips in the same place facing opposite ways, a fence's two
		 * faces: only the one facing the viewer is drawn, as in the game
		 */
		public boolean		backToBack;
		/** what the strip is part of: the road, a border, a side or a barrier */
		public Part			part;
		/** x, y, z for each point */
		private float[]		points	= new float[48];
		/** u, v for each point */
		private float[]		uvs		= new float[32];
		/** u, v of the raceline layer for each point */
		private float[]		racelineUvs	= new float[32];
		private int			count;

		public int size()
		{
			return count;
		}

		public float x(int i)
		{
			return points[i * 3];
		}

		public float y(int i)
		{
			return points[i * 3 + 1];
		}

		public float z(int i)
		{
			return points[i * 3 + 2];
		}

		public float u(int i)
		{
			return uvs[i * 2];
		}

		public float v(int i)
		{
			return uvs[i * 2 + 1];
		}

		public float racelineU(int i)
		{
			return racelineUvs[i * 2];
		}

		public float racelineV(int i)
		{
			return racelineUvs[i * 2 + 1];
		}

		void setRaceline(int i, float u, float v)
		{
			racelineUvs[i * 2] = u;
			racelineUvs[i * 2 + 1] = v;
		}

		void add(float u, float v, float x, float y, float z)
		{
			if (count * 3 + 3 > points.length)
			{
				points = java.util.Arrays.copyOf(points, points.length * 2);
				uvs = java.util.Arrays.copyOf(uvs, uvs.length * 2);
				racelineUvs = java.util.Arrays.copyOf(racelineUvs, racelineUvs.length * 2);
			}
			points[count * 3] = x;
			points[count * 3 + 1] = y;
			points[count * 3 + 2] = z;
			uvs[count * 2] = u;
			uvs[count * 2 + 1] = v;
			count++;
		}

		/** @return the points of each triangle, as trackgen turns the strip into triangles */
		public int[] triangles()
		{
			if (count < 3)
				return new int[0];
			int[] t = new int[(count - 2) * 3];
			t[0] = 0;
			t[1] = 1;
			t[2] = 2;
			int n = 3;
			for (int i = 2; i < count - 1; i++)
			{
				if (i % 2 == 0)
				{
					t[n++] = i;
					t[n++] = i - 1;
					t[n++] = i + 1;
				}
				else
				{
					t[n++] = i - 1;
					t[n++] = i;
					t[n++] = i + 1;
				}
			}
			return t;
		}
	}

	public enum Part { ROAD, BORDER, SIDE, BARRIER };

	/** the strips in the order trackgen makes them; empty ones are left out */
	public final List<Strip>	strips	= new ArrayList<Strip>();

	protected final GameTrack	track;
	protected final EditorFrame	editorFrame;

	// trackgen's state
	protected Strip				current;
	protected Part				part;
	protected String			curTexture;
	protected String			curSurface;
	protected String			curRaceline;
	// the raceline texture of each texture, for the games that tell textures apart by their name only
	private final Map<String, String>	racelineOfTexture	= new HashMap<String, String>();
	/** whether the road has the raceline layer a raceline export gives it */
	protected boolean			racelineLayer;
	protected String			curTextureKey;
	protected int				curTexId, prevTexId;
	protected boolean			curDoubleSided, prevDoubleSided;
	// making a fence's two faces
	protected boolean			curBackToBack;
	protected boolean			startNeeded;
	protected int				curTexType;
	protected boolean			curTexLink;
	protected float				curTexOffset;
	protected float				curTexSize;
	private final Map<String, Integer>	textureIds	= new HashMap<String, Integer>();

	protected TrackScene(GameTrack track, EditorFrame editorFrame)
	{
		this.track = track;
		this.editorFrame = editorFrame;
	}

	/**
	 * @param raceline with the raceline layer a raceline export adds to the road
	 * @return the open track's scene as its game's trackgen makes it, or null when there's no track
	 */
	public static TrackScene build(EditorFrame editorFrame, boolean raceline)
	{
		GameTrack track = GameTrack.build(editorFrame);
		if (track == null)
			return null;
		TrackScene scene = track.game == EditorFrame.GameType.TORCS ? new SceneTorcs(track, editorFrame) : new SceneCsrSd(track, editorFrame);
		scene.racelineLayer = raceline;
		scene.generate();
		// trackgen skips objects that aren't triangles
		scene.strips.removeIf(s -> s.size() < 3);
		return scene;
	}

	protected abstract void generate();

	protected static final float	LG_STEP_MAX	= 50.0f;

	/**
	 * The main track, as all three trackgens make it, with its raceline layer: trackgen -r
	 * makes the road again with each surface's raceline texture, its u in road widths and its
	 * v moved across by the racing line, and accc adds it to the road as a second texture.
	 */
	protected void mainTrack()
	{
		part = Part.ROAD;
		prevTexId = 0;
		startNeeded = true;
		float running = 0;
		float width = track.width;
		float texMaxT = 0;
		RaceLine raceLine = racelineLayer ? raceLine() : null;
		MainTrack main = editorFrame.getTrackData().getMainTrack();
		double scale = Double.isNaN(main.getRacelineWidthscale()) ? 1.0 : main.getRacelineWidthscale();
		float rlOffset = (float) ((1.0 - 1.0 / scale) / 2.0);
		for (int i = 0; i < track.segs.size(); i++)
		{
			GameTrack.Seg seg = track.segs.get(i);
			checkDispList(seg.surface, "tkMn", i, seg.lgfromstart);
			float curTexSeg = (curTexLink ? seg.lgfromstart : 0) + curTexOffset;
			float texLen = curTexSeg / curTexSize;
			// the raceline layer's texture is a road width long
			float rlTexLen = curTexSeg / seg.width;
			if (startNeeded || running > LG_STEP_MAX)
			{
				newDispList(false, "tkMn", i);
				running = 0;
				texMaxT = texMaxT(width);
				roadPoint(texLen, texMaxT, seg.vertex[GameTrack.TR_SL], raceLine, rlTexLen, seg.lgfromstart, true, scale, rlOffset);
				roadPoint(texLen, 0, seg.vertex[GameTrack.TR_SR], raceLine, rlTexLen, seg.lgfromstart, false, scale, rlOffset);
			}
			float texStep = texStep(seg, seg);
			float rlTexStep = texStep * curTexSize / seg.width;
			for (float ts : steps(seg, seg))
			{
				texLen += texStep;
				rlTexLen += rlTexStep;
				float lg = seg.lgfromstart + (seg.type == GameTrack.TR_STR ? ts : ts * seg.radius);
				roadPoint(texLen, texMaxT, point(seg, ts, width), raceLine, rlTexLen, lg, true, scale, rlOffset);
				roadPoint(texLen, 0, point(seg, ts, 0), raceLine, rlTexLen, lg, false, scale, rlOffset);
			}
			texLen = (curTexSeg + seg.length) / curTexSize;
			rlTexLen = (curTexSeg + seg.length) / seg.width;
			roadPoint(texLen, texMaxT, seg.vertex[GameTrack.TR_EL], raceLine, rlTexLen, seg.lgfromstart + seg.length, true, scale, rlOffset);
			roadPoint(texLen, 0, seg.vertex[GameTrack.TR_ER], raceLine, rlTexLen, seg.lgfromstart + seg.length, false, scale, rlOffset);
			startNeeded = false;
			running += seg.length;
		}
	}

	private void roadPoint(float u, float v, float[] p, RaceLine raceLine, float rlU, float lg, boolean left, double scale, float rlOffset)
	{
		int before = current.size();
		setPoint(u, v, p, 0);
		if (raceLine != null && current.size() > before)
		{
			float rlto = (float) (raceLine.textureOffset(lg) / scale);
			current.setRaceline(before, rlU, left ? 1.0f - rlOffset + rlto : rlOffset + rlto);
		}
	}

	/** @return whether a surface of the open track's road names a raceline texture */
	public static boolean namesRaceline(EditorFrame editorFrame)
	{
		if (editorFrame.getTrackData() == null || editorFrame.getTrackData().getSegments() == null)
			return false;
		for (utils.circuit.Segment segment : editorFrame.getTrackData().getSegments())
		{
			Surface surface = RoadTexture.findSurface(editorFrame, segment.getValidSurface(editorFrame));
			if (surface != null && surface.getRacelineName() != null && !surface.getRacelineName().isEmpty())
				return true;
		}
		return false;
	}

	// the last racing line: it only changes with the road's shape, and takes a while on a long track
	private static RaceLine		lastRaceLine;
	private static long			lastRaceLineKey;

	private RaceLine raceLine()
	{
		MainTrack main = editorFrame.getTrackData().getMainTrack();
		double ext = Double.isNaN(main.getRacelineExt()) ? 2.0 : main.getRacelineExt();
		double in = Double.isNaN(main.getRacelineInt()) ? 2.0 : main.getRacelineInt();
		long key = Double.doubleToLongBits(ext) * 31 + Double.doubleToLongBits(in);
		for (GameTrack.Seg seg : track.segs)
		{
			key = key * 31 + seg.type;
			key = key * 31 + Float.floatToIntBits(seg.length);
			key = key * 31 + Float.floatToIntBits(seg.radius);
			for (float[] v : seg.vertex)
				key = (key * 31 + Float.floatToIntBits(v[0])) * 31 + Float.floatToIntBits(v[1]);
		}
		synchronized (TrackScene.class)
		{
			if (lastRaceLine == null || lastRaceLineKey != key)
			{
				lastRaceLine = new RaceLine(track, ext, in);
				lastRaceLineKey = key;
			}
			return lastRaceLine;
		}
	}

	protected boolean torcs()
	{
		return track.game == EditorFrame.GameType.TORCS;
	}

	/** NEWDISPLIST: a new strip, or a new texture for an empty one; texchange asks for a new start. */
	protected void newDispList(boolean texchange, String name, int id)
	{
		startNeeded = texchange;
		if (current != null && current.size() == 0)
		{
			// trackgen keeps the empty object, with its name
			current.texture = curTexture;
			current.surface = curSurface;
			current.racelineTexture = racelineLayer && part == Part.ROAD ? curRaceline : null;
			current.doubleSided = curDoubleSided;
			current.part = part;
			return;
		}
		current = new Strip();
		current.name = name + id;
		current.texture = curTexture;
		current.surface = curSurface;
		current.racelineTexture = racelineLayer && part == Part.ROAD ? curRaceline : null;
		current.doubleSided = curDoubleSided;
		current.part = part;
		strips.add(current);
	}

	/** SETPOINT: a point of the current strip; nothing when the texture has no name. */
	protected void setPoint(float u, float v, float x, float y, float z)
	{
		if (curTexture == null || curTexture.isEmpty())
			return;
		if (curBackToBack)
			current.backToBack = true;
		current.add(u, v, x, y, z);
	}

	protected void setPoint(float u, float v, float[] vertex, float zAdd)
	{
		setPoint(u, v, vertex[0], vertex[1], vertex[2] + zAdd);
	}

	/** CHECKDISPLIST: the surface's texture and mapping, with a new strip when the texture changes. */
	protected void checkDispList(String surfaceName, String name, int id, float off)
	{
		Surface surface = surfaceName == null ? null : RoadTexture.findSurface(editorFrame, surfaceName);
		String texture = text(surface == null ? null : surface.getTextureName(), torcs() ? "tr-asphalt.rgb" : "tr-asphalt.png");
		String bump = text(surface == null ? null : surface.getBumpName(), "");
		String raceline = text(surface == null ? null : surface.getRacelineName(), torcs() ? "" : "raceline.png");
		// TORCS tells textures apart by all three names, the others by the texture's
		String key = torcs() ? texture + "\n" + bump + "\n" + raceline : texture;
		Integer known = textureIds.get(key);
		if (known == null)
		{
			known = textureIds.size() + 1;
			textureIds.put(key, known);
		}
		curTexId = known;
		curTexture = texture;
		curSurface = surfaceName;
		// the raceline layer's texture: the surface's "raceline name", which Speed Dreams and CSR make
		// raceline.png when it has none and TORCS leaves empty (no layer). Speed Dreams and CSR tell
		// textures apart by their name only, so a texture keeps the raceline of the first surface it
		// is seen with; TORCS tells them apart by all three names (above)
		String layer = surface == null || surface.getRacelineName() == null ? (torcs() ? "" : "raceline.png") : surface.getRacelineName();
		if (!torcs())
		{
			if (!racelineOfTexture.containsKey(texture))
				racelineOfTexture.put(texture, layer);
			layer = racelineOfTexture.get(texture);
		}
		curRaceline = layer.isEmpty() ? null : layer;
		boolean surfaceChange = !torcs() && curDoubleSided != prevDoubleSided;
		if (curTexId != prevTexId || surfaceChange || startNeeded)
		{
			String type = surface == null ? null : surface.getTextureType();
			curTexType = type == null || type.equals("continuous") ? 1 : 0;
			String link = surface == null ? null : surface.getTextureLinkWithPrevious();
			curTexLink = link == null || link.equals("yes");
			curTexOffset = surface != null && "yes".equals(surface.getTextureStartOnBoundary()) ? -off : 0;
			curTexSize = surface == null || Double.isNaN(surface.getTextureSize()) ? 20 : (float) surface.getTextureSize();
			prevTexId = curTexId;
			prevDoubleSided = curDoubleSided;
			newDispList(true, name, id);
		}
	}

	protected float texMaxT(float width)
	{
		return curTexType == 1 ? width / curTexSize : 1.0f + (float) Math.floor(width / curTexSize);
	}

	/** The positions along a segment where trackgen adds points between its ends: a length on a straight, an angle in a curve. */
	protected float[] steps(GameTrack.Seg seg, GameTrack.Seg mseg)
	{
		float lmax = track.trackStep;
		List<Float> list = new ArrayList<Float>();
		switch (seg.type)
		{
		case GameTrack.TR_STR:
			for (float ts = lmax; ts < seg.length; ts += lmax)
				list.add(ts);
			break;
		case GameTrack.TR_LFT:
		{
			float step = lmax / mseg.radiusr;
			float anz = seg.angleZS + step;
			for (float ts = step; anz < seg.angleZE; ts += step, anz += step)
				list.add(ts);
			break;
		}
		default:
		{
			float step = lmax / mseg.radiusl;
			float anz = seg.angleZS - step;
			for (float ts = step; anz > seg.angleZE; ts += step, anz -= step)
				list.add(ts);
			break;
		}
		}
		float[] steps = new float[list.size()];
		for (int i = 0; i < steps.length; i++)
			steps[i] = list.get(i);
		return steps;
	}

	/** @return the u step between trackgen's points along a segment */
	protected float texStep(GameTrack.Seg seg, GameTrack.Seg mseg)
	{
		if (seg.type == GameTrack.TR_STR)
			return track.trackStep / curTexSize;
		float step = track.trackStep / (seg.type == GameTrack.TR_LFT ? mseg.radiusr : mseg.radiusl);
		return step * mseg.radius / curTexSize;
	}

	/** @return toStart at the end of a segment */
	protected static float endToStart(GameTrack.Seg seg)
	{
		return seg.type == GameTrack.TR_STR ? seg.length : seg.arc;
	}

	protected float[] point(GameTrack.Seg seg, float toStart, float toRight)
	{
		float[] xy = GameTrack.local2Global(seg, toStart, toRight);
		return new float[] { xy[0], xy[1], track.heightL(seg, toStart, toRight) };
	}

	private static String text(String value, String otherwise)
	{
		return value == null ? otherwise : value;
	}
}
