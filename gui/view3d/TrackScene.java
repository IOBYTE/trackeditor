/*
 *   TrackScene.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package gui.view3d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import gui.EditorFrame;
import utils.GameType;
import utils.Properties;
import utils.ac3d.Ac3d;
import utils.ac3d.Ac3dMaterial;
import utils.ac3d.Ac3dObject;
import utils.ac3d.Ac3dSurface;
import utils.circuit.MainTrack;
import utils.circuit.RoadTexture;
import utils.circuit.Surface;
import utils.circuit.TrackLight;
import utils.circuit.TurnMarks;

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
		/** the texture doesn't repeat: its edges stretch past it, as on the game's pit doors */
		public boolean		clamp;
		/** the material's color of an object without a texture, or null */
		public float[]		color;
		/** what the strip is part of: the road, a border, a side or a barrier */
		public Part			part;
		/** a start light's texture when it is off, its role and its index */
		public String		textureOff;
		public String		lightRole;
		public int			lightIndex;
		/** x, y, z for each point */
		private float[]		points	= new float[48];
		/** u, v for each point */
		private float[]		uvs		= new float[32];
		/** u, v of the raceline layer for each point */
		private float[]		racelineUvs	= new float[32];
		private int			count;
		/** the points of each triangle, for an object made of triangles rather than a strip */
		private int[]		triangleList;

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
			if (triangleList != null)
				return triangleList;
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

	public enum Part { ROAD, BORDER, SIDE, BARRIER, TURN_MARK, START_BRIDGE, PIT_BUILDING, PIT_DOOR, CROSSING, TRACK_LIGHT };

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
	// a texture that doesn't repeat
	protected boolean			curClamp;
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
		TrackScene scene = track.game == GameType.TORCS ? new SceneTorcs(track, editorFrame) : new SceneCsrSd(track, editorFrame);
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
		return track.game == GameType.TORCS;
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
			current.clamp = curClamp;
			current.part = part;
			return;
		}
		current = new Strip();
		current.name = name + id;
		current.texture = curTexture;
		current.surface = curSurface;
		current.racelineTexture = racelineLayer && part == Part.ROAD ? curRaceline : null;
		current.doubleSided = curDoubleSided;
		current.clamp = curClamp;
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
		curTexId = textureId(key);
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

	/** SETTEXTURE's texture id: one for each texture the game tells apart */
	private int textureId(String key)
	{
		Integer known = textureIds.get(key);
		if (known == null)
		{
			known = textureIds.size() + 1;
			textureIds.put(key, known);
		}
		return known;
	}

	/** CHECKDISPLIST2: a texture named without its extension, which is .rgb in TORCS and .png in the others. */
	protected void checkDispList2(String texture, String name, int id)
	{
		checkDispList3(texture + (torcs() ? ".rgb" : ".png"), name, id);
	}

	/**
	 * CHECKDISPLIST3: a texture file without a bump map or raceline, with a new strip
	 * when the texture changes, and in TORCS also when a start is needed.
	 */
	protected void checkDispList3(String texture, String name, int id)
	{
		curTexId = textureId(torcs() ? texture + "\n\n" : texture);
		curTexture = texture;
		curSurface = null;
		curRaceline = null;
		if (curTexId != prevTexId || torcs() && startNeeded)
		{
			prevTexId = curTexId;
			newDispList(true, name, id);
		}
	}

	/**
	 * The bridges TORCS's trackgen builds where the track crosses itself, after the
	 * rest of the track: objects made of triangles.
	 */
	protected void crossings()
	{
		TrackCrossing crossing = new TrackCrossing(track, editorFrame);
		for (TrackCrossing.Geometry geometry : crossing.crossings)
		{
			for (TrackCrossing.CrossingObject object : geometry.objects())
			{
				if (object.triangles.isEmpty())
					continue;
				Strip strip = new Strip();
				strip.name = object.name;
				strip.texture = object.texture;
				strip.part = Part.CROSSING;
				for (double[] v : object.vertices)
					strip.add((float) v[3], (float) v[4], (float) v[0], (float) v[1], (float) v[2]);
				int[] triangles = new int[object.triangles.size() * 3];
				for (int i = 0; i < object.triangles.size(); i++)
					System.arraycopy(object.triangles.get(i), 0, triangles, i * 3, 3);
				strip.triangleList = triangles;
				strips.add(strip);
			}
		}
	}

	/** What trackgen adds after the barriers: the turn marks, the start bridge and the pit buildings. */
	protected void furniture()
	{
		curDoubleSided = false;
		// a sign's front and back are in the same place facing opposite ways, as a fence's faces
		curBackToBack = true;
		turnMarks();
		curBackToBack = false;
		// as trackgen -nb, when the project leaves it out (Properties > Terrain)
		if (Properties.getInstance().isStartBridge() || !editorFrame.getGameType().hasNoBridgeTrackgen())
			startBridge();
		pitBuildings();
		pitDoors();
		trackLights();
	}

	/**
	 * The start lights: not trackgen's, the game adds them when a race starts.
	 * Each is a two-sided rectangle standing between its two corners, at the
	 * game's coordinates. They show lit, and go on and off as in a race when
	 * driving from the grid. Only Speed Dreams and CSR have
	 * them, and only the roles they know are drawn.
	 */
	private void trackLights()
	{
		if (!editorFrame.getGameType().hasSpeedDreamsExtensions())
			return;
		String data = editorFrame.getDataDirectory();
		// the corners are in the game's coordinates, moved to start at 0
		float[] origin = null;
		int k = 0;
		for (TrackLight light : editorFrame.getTrackData().getTrackLights())
		{
			String role = light.getRole();
			if (role == null || !(role.equals("st_red") || role.equals("st_green") || role.equals("st_green_st") || role.equals("st_yellow")))
				continue;
			if (origin == null)
				origin = track.gameOrigin();
			float x1 = origin[0] + (float) orDefault(light.getTopLeftX(), 0), y1 = origin[1] + (float) orDefault(light.getTopLeftY(), 0), z1 = origin[2] + (float) orDefault(light.getTopLeftZ(), 0);
			float x2 = origin[0] + (float) orDefault(light.getBottomRightX(), 0), y2 = origin[1] + (float) orDefault(light.getBottomRightY(), 0), z2 = origin[2] + (float) orDefault(light.getBottomRightZ(), 0);
			Strip strip = new Strip();
			strip.name = "light " + (light.getName() != null ? light.getName() : String.valueOf(k + 1));
			k++;
			strip.part = Part.TRACK_LIGHT;
			strip.doubleSided = true;
			strip.clamp = true;
			strip.color = new float[] { (float) orDefault(light.getRed(), 1), (float) orDefault(light.getGreen(), 1), (float) orDefault(light.getBlue(), 1) };
			// the game looks for the texture from its data folder
			strip.texture = lightTexture(light.getTextureOn(), data, editorFrame.getTrackFolders());
			strip.textureOff = lightTexture(light.getTextureOff(), data, editorFrame.getTrackFolders());
			strip.lightRole = role;
			strip.lightIndex = light.getIndex() == Integer.MAX_VALUE ? 0 : light.getIndex();
			// as the game makes it: down the top left edge, then the bottom right one
			strip.add(0, 0, x1, y1, z1);
			strip.add(0, 1, x1, y1, z2);
			strip.add(1, 0, x2, y2, z1);
			strip.add(1, 1, x2, y2, z2);
			strips.add(strip);
		}
	}

	/**
	 * The turn marks: a sign for each of a segment's marks, that many meters before
	 * it, outside the turn, with its back.
	 */
	private void turnMarks()
	{
		part = Part.TURN_MARK;
		TurnMarks marks = editorFrame.getTrackData().getGraphic().getTurnMarks();
		float tmWidth = (float) orDefault(marks.getWidth(), TurnMarks.DEFAULT_WIDTH);
		float tmHeight = (float) orDefault(marks.getHeight(), TurnMarks.DEFAULT_HEIGHT);
		float tmVSpace = (float) orDefault(marks.getVerticalSpace(), TurnMarks.DEFAULT_VERTICAL_SPACE);
		float tmHSpace = (float) orDefault(marks.getHorizontalSpace(), TurnMarks.DEFAULT_HORIZONTAL_SPACE);
		float trackLength = track.length();
		for (GameTrack.Seg seg : track.segs)
		{
			if (seg.marks == null)
				continue;
			for (int mark : seg.marks)
			{
				// the segment that far before it
				float lgfs = seg.lgfromstart - mark;
				if (lgfs < 0)
					lgfs += trackLength;
				GameTrack.Seg mseg = track.segs.get(0);
				for (GameTrack.Seg s : track.segs)
				{
					if (lgfs >= s.lgfromstart && lgfs < s.lgfromstart + s.length)
					{
						mseg = s;
						break;
					}
				}
				String texture;
				float toRight;
				if (seg.type == GameTrack.TR_RGT)
				{
					texture = "turn" + mark + "R";
					toRight = track.width + tmHSpace + tmWidth;
				}
				else
				{
					texture = "turn" + mark + "L";
					toRight = -tmHSpace;
				}
				float toStart = lgfs - mseg.lgfromstart;
				if (mseg.type != GameTrack.TR_STR)
					toStart = toStart / mseg.radius;
				float[] xy = GameTrack.local2Global(mseg, toStart, toRight);
				float x = xy[0], y = xy[1];
				float z = tmVSpace + track.heightL(mseg, toStart, toRight);
				float[] n;
				if (seg.type == GameTrack.TR_LFT)
				{
					n = GameTrack.sideNormal(mseg, x, y, GameTrack.TR_RGT);
					n[0] = -n[0];
					n[1] = -n[1];
				}
				else
				{
					n = GameTrack.sideNormal(mseg, x, y, GameTrack.TR_LFT);
				}
				float x2 = x + tmWidth * n[0], y2 = y + tmWidth * n[1];
				checkDispList2(texture, "TuMk", mseg.id);
				setPoint(0.0f, 0.0f, x, y, z);
				setPoint(1.0f, 0.0f, x2, y2, z);
				setPoint(0.0f, 1.0f, x, y, z + tmHeight);
				setPoint(1.0f, 1.0f, x2, y2, z + tmHeight);
				checkDispList2("back-sign", "TuMk", mseg.id);
				setPoint(0.0f, 0.0f, x2, y2, z);
				setPoint(1.0f, 0.0f, x, y, z);
				setPoint(0.0f, 1.0f, x2, y2, z + tmHeight);
				setPoint(1.0f, 1.0f, x, y, z + tmHeight);
			}
		}
	}

	/** A start light's texture, where the game looks for it: from its data folder */
	private static String lightTexture(String texture, String data, String[] trackFolders)
	{
		String[] folders = new String[trackFolders.length + 1];
		folders[0] = data;
		System.arraycopy(trackFolders, 0, folders, 1, trackFolders.length);
		return texture == null || texture.isEmpty() ? "" : foundTexture("track light/", texture, folders);
	}

	private static double orDefault(double value, double otherwise)
	{
		return Double.isNaN(value) ? otherwise : value;
	}

	private static final float	BR_HEIGHT_1	= 8.0f;
	private static final float	BR_HEIGHT_2	= 6.0f;
	private static final float	BR_WIDTH_0	= 2.0f;
	private static final float	BR_WIDTH_1	= 2.0f;

	/**
	 * The start bridge: a pylon outside each edge of the first segment and the beam
	 * across. Speed Dreams makes it of more strips than CSR and TORCS.
	 */
	private void startBridge()
	{
		part = Part.START_BRIDGE;
		boolean sd = track.game == GameType.SPEED_DREAMS;
		checkDispList2("pylon1", "S0Bg", 0);
		GameTrack.Seg mseg = track.segs.get(0);
		GameTrack.Seg seg = mseg.rside == null ? mseg : mseg.rside.rside != null ? mseg.rside.rside : mseg.rside;
		float x = seg.vertex[GameTrack.TR_SR][0];
		float y = (float) (seg.vertex[GameTrack.TR_SR][1] - 0.1);
		float z = seg.vertex[GameTrack.TR_SR][2];

		// the right pylon
		setPoint(0, 0, x, y, z);
		setPoint(0, 1, x, y, z + BR_HEIGHT_2);
		x += BR_WIDTH_0;
		setPoint(1, 0, x, y, z);
		setPoint(1, 1, x, y, z + BR_HEIGHT_2);
		y -= BR_WIDTH_1;
		setPoint(2, 0, x, y, z);
		setPoint(2, 1, x, y, z + BR_HEIGHT_2);
		x -= BR_WIDTH_0;
		setPoint(3, 0, x, y, z);
		setPoint(3, 1, x, y, z + BR_HEIGHT_2);
		y += BR_WIDTH_1;
		setPoint(4, 0, x, y, z);
		setPoint(4, 1, x, y, z + BR_HEIGHT_2);

		// the left pylon
		newDispList(false, "S1Bg", 0);
		seg = mseg.lside == null ? mseg : mseg.lside.lside != null ? mseg.lside.lside : mseg.lside;
		float x2 = seg.vertex[GameTrack.TR_SL][0];
		float y2 = (float) (seg.vertex[GameTrack.TR_SL][1] + 0.1);
		float z2 = seg.vertex[GameTrack.TR_SL][2];
		setPoint(0, 1, x2, y2, z + BR_HEIGHT_2);
		setPoint(0, 0, x2, y2, z2);
		x2 += BR_WIDTH_0;
		setPoint(1, 1, x2, y2, z + BR_HEIGHT_2);
		setPoint(1, 0, x2, y2, z2);
		y2 += BR_WIDTH_1;
		setPoint(2, 1, x2, y2, z + BR_HEIGHT_2);
		setPoint(2, 0, x2, y2, z2);
		x2 -= BR_WIDTH_0;
		setPoint(3, 1, x2, y2, z + BR_HEIGHT_2);
		setPoint(3, 0, x2, y2, z2);
		y2 -= BR_WIDTH_1;
		setPoint(4, 1, x2, y2, z + BR_HEIGHT_2);
		setPoint(4, 0, x2, y2, z2);

		// the right pylon's top
		checkDispList2("pylon2", "S2Bg", 0);
		setPoint(0, 1, x, y, z + BR_HEIGHT_1);
		setPoint(0, 0, x, y, z + BR_HEIGHT_2);
		y -= BR_WIDTH_1;
		setPoint(1, 1, x, y, z + BR_HEIGHT_1);
		setPoint(1, 0, x, y, z + BR_HEIGHT_2);
		x += BR_WIDTH_0;
		setPoint(2, 1, x, y, z + BR_HEIGHT_1);
		setPoint(2, 0, x, y, z + BR_HEIGHT_2);
		y += BR_WIDTH_1;
		setPoint(3, 1, x, y, z + BR_HEIGHT_1);
		setPoint(3, 0, x, y, z + BR_HEIGHT_2);
		x -= BR_WIDTH_0;
		if (sd)
			newDispList(false, "S3Bg", 0);
		setPoint(3, 1, x + BR_WIDTH_0, y, z + BR_HEIGHT_1);
		setPoint(3, 0, x, y, z + BR_HEIGHT_1);
		y -= BR_WIDTH_1;
		setPoint(4, 1, x + BR_WIDTH_0, y, z + BR_HEIGHT_1);
		setPoint(4, 0, x, y, z + BR_HEIGHT_1);
		// back to the start
		y += BR_WIDTH_1;

		// the left pylon's top
		newDispList(false, sd ? "S4Bg" : "S3Bg", 0);
		y2 += BR_WIDTH_1;
		setPoint(0, 1, x2 + BR_WIDTH_0, y2, z + BR_HEIGHT_1);
		setPoint(0, 0, x2, y2, z + BR_HEIGHT_1);
		y2 -= BR_WIDTH_1;
		setPoint(1, 1, x2 + BR_WIDTH_0, y2, z + BR_HEIGHT_1);
		setPoint(1, 0, x2, y2, z + BR_HEIGHT_1);
		x2 += BR_WIDTH_0;
		if (sd)
			newDispList(false, "S5Bg", 0);
		setPoint(1, 1, x2, y2, z + BR_HEIGHT_1);
		setPoint(1, 0, x2, y2, z + BR_HEIGHT_2);
		y2 += BR_WIDTH_1;
		setPoint(2, 1, x2, y2, z + BR_HEIGHT_1);
		setPoint(2, 0, x2, y2, z + BR_HEIGHT_2);
		x2 -= BR_WIDTH_0;
		setPoint(3, 1, x2, y2, z + BR_HEIGHT_1);
		setPoint(3, 0, x2, y2, z + BR_HEIGHT_2);
		y2 -= BR_WIDTH_1;
		setPoint(4, 1, x2, y2, z + BR_HEIGHT_1);
		setPoint(4, 0, x2, y2, z + BR_HEIGHT_2);

		// the beam across
		checkDispList2("pylon3", sd ? "S6Bg" : "S4Bg", 2);
		setPoint(0, 0, x2, y2, z + BR_HEIGHT_2);
		setPoint(1, 0, x, y, z + BR_HEIGHT_2);
		setPoint(0, 0.25f, x2, y2, z + BR_HEIGHT_1);
		setPoint(1, 0.25f, x, y, z + BR_HEIGHT_1);
		x += BR_WIDTH_0;
		x2 += BR_WIDTH_0;
		setPoint(0, 0.5f, x2, y2, z + BR_HEIGHT_1);
		setPoint(1, 0.5f, x, y, z + BR_HEIGHT_1);
		setPoint(0, 0.75f, x2, y2, z + BR_HEIGHT_2);
		setPoint(1, 0.75f, x, y, z + BR_HEIGHT_2);
		x -= BR_WIDTH_0;
		x2 -= BR_WIDTH_0;
		setPoint(0, 1, x2, y2, z + BR_HEIGHT_2);
		setPoint(1, 1, x, y, z + BR_HEIGHT_2);
	}

	private static final float	PIT_HEIGHT	= 5.0f;
	private static final float	PIT_DEEP	= 10.0f;
	private static final float	PIT_TOP		= 0.2f;

	/**
	 * The pit buildings: a cap at each end and, for each pit, its front, roof and
	 * back. Speed Dreams and CSR make them two-sided, in one strip; TORCS makes a
	 * strip for each pit, wound so the faces point out.
	 */
	private void pitBuildings()
	{
		GameTrack.PitLayout pits = track.pitBuildings();
		if (pits == null)
			return;
		part = Part.PIT_BUILDING;
		boolean torcs = torcs();
		String ext = torcs ? ".rgb" : ".png";
		boolean right = pits.side == GameTrack.TR_RGT;
		curDoubleSided = !torcs;
		int uid = 1;

		GameTrack.LocalPos pit = pits.pits.get(0);
		startNeeded = true;
		checkDispList3("concrete2" + ext, "P" + uid++ + "ts", pit.seg.id);
		float[] xy = GameTrack.local2Global(pit.seg, pit.toStart, pit.toRight);
		float[] n = GameTrack.sideNormal(pit.seg, xy[0], xy[1], pits.side);
		float z2 = track.heightG(pit.seg, xy[0], xy[1]);
		// Speed Dreams and CSR wind the caps the same way at both ends, TORCS by the side
		if (torcs && right)
			pitCapCW(xy[0], xy[1], z2, n);
		else
			pitCapCCW(xy[0], xy[1], z2, n);

		// each pit's front, roof and back
		int[] order = torcs && right ? new int[] { 1, 0, 3, 2, 5, 4, 7, 6, 9, 8 } : new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		for (GameTrack.LocalPos p : pits.pits)
		{
			pit = p;
			startNeeded = true;
			checkDispList3("concrete" + ext, "P" + uid++ + "ts", pit.seg.id);
			xy = GameTrack.local2Global(pit.seg, pit.toStart, pit.toRight);
			float x2 = xy[0], y2 = xy[1];
			n = GameTrack.sideNormal(pit.seg, x2, y2, pits.side);
			z2 = track.heightG(pit.seg, x2, y2);
			float x3, y3;
			if (right)
			{
				x3 = x2 + pits.len * n[1];
				y3 = y2 - pits.len * n[0];
			}
			else
			{
				x3 = x2 - pits.len * n[1];
				y3 = y2 + pits.len * n[0];
			}
			float z3 = track.heightG(pit.seg, x3, y3);
			float tx = PIT_TOP * n[0], ty = PIT_TOP * n[1];
			float dx = -PIT_DEEP * n[0], dy = -PIT_DEEP * n[1];
			float[][] points = {
				{ pits.len, 0.0f, x2, y2, z2 + PIT_HEIGHT - PIT_TOP },
				{ 0.0f, 0.0f, x3, y3, z3 + PIT_HEIGHT - PIT_TOP },
				{ pits.len, PIT_TOP, x2 + tx, y2 + ty, z2 + PIT_HEIGHT - PIT_TOP },
				{ 0.0f, PIT_TOP, x3 + tx, y3 + ty, z3 + PIT_HEIGHT - PIT_TOP },
				{ pits.len, 2.0f * PIT_TOP, x2 + tx, y2 + ty, z2 + PIT_HEIGHT },
				{ 0.0f, 2.0f * PIT_TOP, x3 + tx, y3 + ty, z3 + PIT_HEIGHT },
				{ pits.len, 2.0f * PIT_TOP + PIT_DEEP, x2 + dx, y2 + dy, z2 + PIT_HEIGHT },
				{ 0.0f, 2.0f * PIT_TOP + PIT_DEEP, x3 + dx, y3 + dy, z3 + PIT_HEIGHT },
				{ pits.len, 2.0f * PIT_TOP + PIT_DEEP + PIT_HEIGHT, x2 + dx, y2 + dy, z2 },
				{ 0.0f, 2.0f * PIT_TOP + PIT_DEEP + PIT_HEIGHT, x3 + dx, y3 + dy, z3 } };
			for (int i : order)
				setPoint(points[i][0], points[i][1], points[i][2], points[i][3], points[i][4]);
		}

		// the cap after the last pit
		startNeeded = true;
		checkDispList3("concrete2" + ext, "P" + uid++ + "ts", pit.seg.id);
		xy = GameTrack.local2Global(pit.seg, pit.toStart, pit.toRight);
		n = GameTrack.sideNormal(pit.seg, xy[0], xy[1], pits.side);
		float x = right ? xy[0] + pits.len * n[1] : xy[0] - pits.len * n[1];
		float y = right ? xy[1] - pits.len * n[0] : xy[1] + pits.len * n[0];
		z2 = track.heightG(pit.seg, x, y);
		if (torcs && right)
			pitCapCCW(x, y, z2, n);
		else
			pitCapCW(x, y, z2, n);
	}

	/**
	 * The pit doors: not trackgen's, the game adds them when a race starts, each
	 * pit's team logo on a 4.8 m high, two-sided rectangle across the front of its
	 * building. Here each door shows its pit's number instead, counting from the
	 * pit start.
	 */
	private void pitDoors()
	{
		GameTrack.PitLayout pits = track.pitPlaces();
		if (pits == null)
			return;
		if (pits.noBuilding)
		{
			lowPitWall(pits);
			return;
		}
		part = Part.PIT_DOOR;
		boolean right = pits.side == GameTrack.TR_RGT;
		double validWidth = editorFrame.getTrackData().getMainTrack().getPits().getValidWidth();
		float width = (float) validWidth;
		float half = pits.len / 2;
		// the number fills the door, read from the track
		float u0 = 0, u1 = 1, v0 = 0, v1 = 1;
		curDoubleSided = true;
		curClamp = true;
		int uid = 1;
		for (GameTrack.LocalPos p : pits.pits)
		{
			// the game's pit position: the middle of the pit, half its width in from
			// the building; "to start" a length even in a curve, as for the buildings
			float toRight = right ? p.toRight + width / 2 : p.toRight - width / 2;
			float[] xy = GameTrack.local2Global(p.seg, p.toStart + half, toRight);
			float[] n = GameTrack.sideNormal(p.seg, xy[0], xy[1], pits.side);
			startNeeded = true;
			String texture = pitNumberTexture(uid, 0);
			String name = "PD" + uid++;
			checkDispList3(texture, name, p.seg.id);
			// each door is its own object in the game, also when the texture doesn't change
			if (current.size() > 0)
				newDispList(true, name, p.seg.id);
			float x2 = xy[0] - width / 2 * n[0] + half * n[1];
			float y2 = xy[1] - width / 2 * n[1] - half * n[0];
			float z2 = track.heightG(p.seg, x2, y2);
			setPoint(u0, v0, x2, y2, z2);
			setPoint(u0, v1, x2, y2, z2 + 4.8f);
			x2 = xy[0] - width / 2 * n[0] - half * n[1];
			y2 = xy[1] - width / 2 * n[1] + half * n[0];
			z2 = track.heightG(p.seg, x2, y2);
			setPoint(u1, v0, x2, y2, z2);
			setPoint(u1, v1, x2, y2, z2 + 4.8f);
		}
		curClamp = false;
	}

	// the textures made here, not found in the track or game data: by name
	private static final Map<String, File>	generatedTextures	= new HashMap<String, File>();

	/** @return the file of a texture made here, or null when it isn't one */
	public static File generatedTexture(String name)
	{
		synchronized (generatedTextures)
		{
			return generatedTextures.get(name);
		}
	}

	/**
	 * Speed Dreams' low wall for "no building" pits: for each pit, 0.9 m high, a
	 * piece of wall, the team's logo and more wall. Here the logo is the pit's
	 * number. The wall is where the game puts it: it moves only the x of each
	 * pit's middle to the start of the wall, so it is a little off from the pits
	 * on tracks that don't run along y. With a pit indicator, each pit has one at
	 * the end of its logo, as a pit with a car has in the game.
	 */
	private void lowPitWall(GameTrack.PitLayout pits)
	{
		part = Part.PIT_DOOR;
		boolean right = pits.side == GameTrack.TR_RGT;
		float width = (float) editorFrame.getTrackData().getMainTrack().getPits().getValidWidth();
		float half = pits.len / 2, quarter = pits.len / 4;
		curDoubleSided = true;
		curClamp = true;
		List<IndicatorPart> indicator = pitIndicator();
		// the game only looks for the wall's texture in data/textures
		String data = editorFrame.getDataDirectory();
		String wall = foundTexture("pit wall/", "tr-wall-gr.png", data == null ? null : data + File.separator + "data" + File.separator + "textures");
		int uid = 1;
		for (GameTrack.LocalPos p : pits.pits)
		{
			float toRight = right ? p.toRight + width / 2 : p.toRight - width / 2;
			float[] xy = GameTrack.local2Global(p.seg, p.toStart + half, toRight);
			float[] n = GameTrack.sideNormal(p.seg, xy[0], xy[1], pits.side);
			// as the game has it: only x is moved
			float x0 = xy[0] - width / 2 * n[0] - quarter * n[1], y0 = xy[1];
			float[] along = { half, quarter, -quarter, -half };
			float[][] q = new float[4][];
			for (int k = 0; k < 4; k++)
			{
				float x = x0 - width / 2 * n[0] + along[k] * n[1];
				float y = y0 - width / 2 * n[1] - along[k] * n[0];
				q[k] = new float[] { x, y, track.heightG(p.seg, x, y) };
			}
			int id = p.seg.id;
			lowWallPart(wall, "PW" + uid + "a", id, q[0], q[1], 0.25f);
			lowWallPart(pitNumberTexture(uid, 1), "PW" + uid + "b", id, q[1], q[2], 1);
			lowWallPart(wall, "PW" + uid + "c", id, q[2], q[3], 0.25f);
			if (indicator != null)
				addPitIndicator(indicator, q[2], uid);
			uid++;
		}
		curClamp = false;
	}

	/** A part of the pit indicator model: an object's polygons with one texture, sidedness and color */
	private static final class IndicatorPart
	{
		String			texture;
		boolean			logo, doubleSided;
		float[]			color;
		final List<float[]>	points		= new ArrayList<float[]>();
		final List<Integer>	triangles	= new ArrayList<Integer>();
	}

	/**
	 * The pit indicator the track's "pit indicator" asks for, as the game reads
	 * it: pit_indicator.ac for 1, normal_pit_indicator.ac above 1, from the
	 * track's folder or data/objects, in the game's axes.
	 *
	 * @return its parts, or null when there is none or it can't be read
	 */
	private List<IndicatorPart> pitIndicator()
	{
		int value = editorFrame.getTrackData().getMainTrack().getPits().getIndicator();
		if (value == Integer.MAX_VALUE || value < 1)
			return null;
		String name = value == 1 ? "pit_indicator.ac" : "normal_pit_indicator.ac";
		String data = editorFrame.getDataDirectory();
		File file = findFile(name, Properties.getInstance().getPath(), data == null ? null : data + File.separator + "data" + File.separator + "objects");
		if (file == null)
			return null;
		Ac3d ac3d = new Ac3d();
		try
		{
			ac3d.read(file);
		}
		catch (Exception e)
		{
			return null;
		}
		List<IndicatorPart> parts = new ArrayList<IndicatorPart>();
		double[][] identity = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 }, { 0, 0, 0 } };
		indicatorParts(ac3d.getRoot(), identity, ac3d.getMaterials(), parts);
		return parts;
	}

	/** @return the first of the folders that has the file, or null */
	private static File findFile(String name, String... folders)
	{
		for (String folder : folders)
		{
			if (folder == null || folder.isEmpty())
				continue;
			File file = new File(folder, name);
			if (file.isFile())
				return file;
		}
		return null;
	}

	/** An object's polygons and its kids', placed by their loc and rot as the game's loader places them */
	private void indicatorParts(Ac3dObject object, double[][] parent, java.util.Vector<Ac3dMaterial> materials, List<IndicatorPart> parts)
	{
		if (object == null)
			return;
		// the object's rot and loc, then its parent's: a point is a row times the matrix
		double[][] local = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 }, { 0, 0, 0 } };
		double[] rot = object.getRot();
		if (rot != null && rot.length == 9)
			for (int r = 0; r < 3; r++)
				for (int c = 0; c < 3; c++)
					local[r][c] = rot[r * 3 + c];
		double[] loc = object.getLoc();
		if (loc != null && loc.length == 3)
			local[3] = loc.clone();
		double[][] matrix = new double[4][3];
		for (int r = 0; r < 4; r++)
			for (int c = 0; c < 3; c++)
			{
				double v = r == 3 ? parent[3][c] : 0;
				for (int k = 0; k < 3; k++)
					v += local[r][k] * parent[k][c];
				matrix[r][c] = v;
			}

		String texture = object.getTexture();
		Map<String, IndicatorPart> byKind = new HashMap<String, IndicatorPart>();
		java.util.Vector<double[]> vertices = object.getVertices();
		for (Ac3dSurface surface : object.getSurfaces())
		{
			if (!surface.isPolygon() || surface.getRefs().size() < 3)
				continue;
			boolean doubleSided = surface.isDoubleSided();
			int mat = surface.getMat();
			String key = doubleSided + " " + mat;
			IndicatorPart part = byKind.get(key);
			if (part == null)
			{
				part = new IndicatorPart();
				part.texture = texture;
				part.logo = texture != null && texture.toLowerCase().startsWith("logo");
				part.doubleSided = doubleSided;
				if ((texture == null || texture.isEmpty()) && mat >= 0 && mat < materials.size())
				{
					double[] rgb = materials.get(mat).getRgb();
					if (rgb != null)
						part.color = new float[] { (float) rgb[0], (float) rgb[1], (float) rgb[2] };
				}
				byKind.put(key, part);
				parts.add(part);
			}
			int first = part.points.size();
			for (Ac3dSurface.Ref ref : surface.getRefs())
			{
				double[] v = vertices.get(ref.index);
				double[] w = new double[3];
				for (int c = 0; c < 3; c++)
					w[c] = v[0] * matrix[0][c] + v[1] * matrix[1][c] + v[2] * matrix[2][c] + matrix[3][c];
				// AC3D's y is up: the game's x, y, z are its x, -z, y
				part.points.add(new float[] { (float) w[0], (float) -w[2], (float) w[1], (float) ref.coord[0], (float) ref.coord[1] });
			}
			// a fan, as the game's loader makes polygons into triangles
			for (int k = 1; k + 1 < surface.getRefs().size(); k++)
			{
				part.triangles.add(first);
				part.triangles.add(first + k);
				part.triangles.add(first + k + 1);
			}
		}
		for (Ac3dObject kid : object.getKids())
			indicatorParts(kid, matrix, materials, parts);
	}

	/**
	 * A pit's indicator at a point, not turned, as the game puts it; its logo
	 * is the pit's number.
	 */
	private void addPitIndicator(List<IndicatorPart> parts, float[] at, int number)
	{
		String data = editorFrame.getDataDirectory();
		String dataPath = data == null ? null : data + File.separator + "data" + File.separator;
		int k = 0;
		for (IndicatorPart part : parts)
		{
			if (part.triangles.isEmpty())
				continue;
			Strip strip = new Strip();
			strip.name = "PI" + number + "-" + k++;
			strip.part = Part.PIT_DOOR;
			strip.doubleSided = part.doubleSided;
			strip.color = part.color;
			// the logo's band (v 0 to 0.33) gets the pit's number
			float vScale = 1;
			if (part.logo)
			{
				strip.texture = pitNumberTexture(number, 2);
				strip.clamp = true;
				vScale = 1 / 0.33f;
			}
			else if (part.texture != null && !part.texture.isEmpty())
				strip.texture = indicatorTexture(part.texture, dataPath, editorFrame);
			else
				strip.texture = "";
			for (float[] p : part.points)
				strip.add(p[3], p[4] * vScale, at[0] + p[0], at[1] + p[1], at[2] + p[2]);
			int[] triangles = new int[part.triangles.size()];
			for (int i = 0; i < triangles.length; i++)
				triangles[i] = part.triangles.get(i);
			strip.triangleList = triangles;
			strips.add(strip);
		}
	}

	/**
	 * A texture of the pit indicator, where the game looks for it: the track's
	 * folder, data/objects, data/textures, data/img.
	 *
	 * @return its texture name
	 */
	private static String indicatorTexture(String texture, String dataPath, EditorFrame editorFrame)
	{
		return foundTexture("pit indicator/", texture, editorFrame.getTrackFolders(
			dataPath == null ? null : dataPath + "objects", dataPath == null ? null : dataPath + "textures",
			dataPath == null ? null : dataPath + "img"));
	}

	/**
	 * A texture found in the folders the game looks in, not where the track's
	 * textures are found. Its name has a prefix, so the track's own textures with
	 * the same name aren't affected.
	 *
	 * @return its texture name
	 */
	private static String foundTexture(String prefix, String texture, String... folders)
	{
		String name = prefix + texture;
		File file = findFile(texture, folders);
		synchronized (generatedTextures)
		{
			if (file != null)
				generatedTextures.put(name, file);
			else
				generatedTextures.remove(name);
		}
		return name;
	}

	/** @return a texture's name as the game knows it, without the prefix of one found here */
	public static String textureDisplayName(String name)
	{
		if (name != null && (name.startsWith("pit wall/") || name.startsWith("pit indicator/") || name.startsWith("track light/")))
			return name.substring(name.indexOf('/') + 1);
		return name;
	}

	/** A piece of the low pit wall from a to b, its texture up to vTop */
	private void lowWallPart(String texture, String name, int id, float[] a, float[] b, float vTop)
	{
		startNeeded = true;
		checkDispList3(texture, name, id);
		// each piece is its own object in the game
		if (current.size() > 0)
			newDispList(true, name, id);
		setPoint(0, 0, a[0], a[1], a[2]);
		setPoint(0, vTop, a[0], a[1], a[2] + 0.9f);
		setPoint(1, 0, b[0], b[1], b[2]);
		setPoint(1, vTop, b[0], b[1], b[2] + 0.9f);
	}

	/**
	 * A pit's number, for its door (kind 0), its part of the low pit wall (1) or
	 * its pit indicator's sign (2), made once for each number and kept in the
	 * temp folder until the editor exits.
	 *
	 * @return its texture name
	 */
	private static String pitNumberTexture(int number, int kind)
	{
		boolean door = kind == 0;
		String name = (door ? "pit door " : kind == 1 ? "pit wall " : "pit sign ") + number + ".png";
		synchronized (generatedTextures)
		{
			File file = generatedTextures.get(name);
			if (file != null && file.isFile())
				return name;
			try
			{
				// about a door's shape, 15 m by 4.8 m, the wall's logo, 7.5 m by 0.9 m,
				// or the indicator's sign, 0.8 m by 0.26 m
				int w = door ? 320 : kind == 1 ? 400 : 240, h = door ? 104 : kind == 1 ? 48 : 80;
				BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				if (door)
				{
					// a light roller door: its slats, and a frame
					g.setColor(new Color(214, 216, 218));
					g.fillRect(0, 0, w, h);
					g.setColor(new Color(188, 191, 194));
					for (int y = 8; y < h; y += 8)
						g.drawLine(0, y, w, y);
					g.setColor(new Color(70, 74, 80));
					g.setStroke(new BasicStroke(6));
					g.drawRect(3, 3, w - 6, h - 6);
				}
				else
				{
					// a white wall
					g.setColor(new Color(238, 238, 234));
					g.fillRect(0, 0, w, h);
				}
				// the number
				String text = Integer.toString(number);
				g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, door ? 72 : kind == 1 ? 38 : 60));
				FontMetrics metrics = g.getFontMetrics();
				int x = (w - metrics.stringWidth(text)) / 2;
				int y = (h - metrics.getHeight()) / 2 + metrics.getAscent();
				g.setColor(new Color(30, 60, 140));
				g.drawString(text, x, y);
				g.dispose();
				file = File.createTempFile("trackeditor-pit-door-", ".png");
				file.deleteOnExit();
				ImageIO.write(image, "png", file);
				generatedTextures.put(name, file);
			}
			catch (Exception e)
			{
				// the door shows a plain color
			}
		}
		return name;
	}

	/** pitCapCCW: a pit building's end, counterclockwise */
	private void pitCapCCW(float x, float y, float z, float[] n)
	{
		setPoint(1.0f - PIT_TOP, PIT_HEIGHT - PIT_TOP, x + PIT_TOP * n[0], y + PIT_TOP * n[1], z + PIT_HEIGHT - PIT_TOP);
		setPoint(1.0f - PIT_TOP, PIT_HEIGHT, x + PIT_TOP * n[0], y + PIT_TOP * n[1], z + PIT_HEIGHT);
		setPoint(1.0f, PIT_HEIGHT - PIT_TOP, x, y, z + PIT_HEIGHT - PIT_TOP);
		setPoint(1.0f + PIT_DEEP, PIT_HEIGHT, x - PIT_DEEP * n[0], y - PIT_DEEP * n[1], z + PIT_HEIGHT);
		setPoint(1.0f, 0, x, y, z);
		setPoint(1.0f + PIT_DEEP, 0, x - PIT_DEEP * n[0], y - PIT_DEEP * n[1], z);
	}

	/** pitCapCW: a pit building's end, clockwise */
	private void pitCapCW(float x, float y, float z, float[] n)
	{
		setPoint(1.0f - PIT_TOP, PIT_HEIGHT, x + PIT_TOP * n[0], y + PIT_TOP * n[1], z + PIT_HEIGHT);
		setPoint(1.0f - PIT_TOP, PIT_HEIGHT - PIT_TOP, x + PIT_TOP * n[0], y + PIT_TOP * n[1], z + PIT_HEIGHT - PIT_TOP);
		setPoint(1.0f + PIT_DEEP, PIT_HEIGHT, x - PIT_DEEP * n[0], y - PIT_DEEP * n[1], z + PIT_HEIGHT);
		setPoint(1.0f, PIT_HEIGHT - PIT_TOP, x, y, z + PIT_HEIGHT - PIT_TOP);
		setPoint(1.0f + PIT_DEEP, 0, x - PIT_DEEP * n[0], y - PIT_DEEP * n[1], z);
		setPoint(1.0f, 0, x, y, z);
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
		// a step's points come every lmax along its outside edge: an outside
		// radius of 0 or less, or more than MAX_STEP_POINTS points, means a broken
		// length or turn, and adding them would never end
		float outside = seg.type == GameTrack.TR_LFT ? mseg.radiusr : mseg.radiusl;
		float along = seg.type == GameTrack.TR_STR ? seg.length : Math.abs(seg.angleZE - seg.angleZS) * outside;
		if (!(lmax > 0) || (seg.type != GameTrack.TR_STR && !(outside > 0)) || !(along / lmax <= MAX_STEP_POINTS))
			return new float[0];
		// nor are a step's points worked out once the scene's would be more than
		// MAX_SCENE_POINTS (see below): otherwise each of a broken track's steps
		// would still add up to MAX_STEP_POINTS points only to drop them
		if (stepPoints + along / lmax > MAX_SCENE_POINTS)
			return new float[0];
		// a step too small to move the float it is added to (the angle of a step
		// on a huge radius) would never reach the end either
		if (seg.type == GameTrack.TR_STR)
		{
			if (seg.length + lmax == seg.length)
				return new float[0];
		}
		else
		{
			float step = lmax / outside;
			if (!(step > 0) || seg.angleZS + step == seg.angleZS || seg.angleZS - step == seg.angleZS
				|| seg.angleZE + step == seg.angleZE || seg.angleZE - step == seg.angleZE)
				return new float[0];
		}
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
		// and a scene with more points than MAX_SCENE_POINTS is of a broken track
		// (a huge arc or radius in its file): the rest of it gets no points between
		// its steps' ends, or building it would use all the memory
		if (stepPoints + (long) list.size() > MAX_SCENE_POINTS)
			return new float[0];
		stepPoints += list.size();
		float[] steps = new float[list.size()];
		for (int i = 0; i < steps.length; i++)
			steps[i] = list.get(i);
		return steps;
	}

	/** the points steps() has added to this scene, and the most it adds (see there) */
	private long				stepPoints		= 0;
	private static final int	MAX_SCENE_POINTS	= 2000000;

	/** the most points steps() adds in one step (see there) */
	private static final int	MAX_STEP_POINTS	= 100000;

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
