/*
 *   GameTrack.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package gui.view3d;

import java.util.ArrayList;
import java.util.List;

import gui.EditorFrame;
import plugin.torcs.GfParmNumbers;
import utils.circuit.Camera;
import utils.circuit.Curve;
import utils.circuit.GameSides;
import utils.circuit.MainTrack;
import utils.circuit.Pits;
import utils.circuit.Point3D;
import utils.circuit.RoadTexture;
import utils.circuit.Segment;
import utils.circuit.Surface;

/**
 * The track as the game's track loader builds it (trackv1 track4.cpp), for
 * trackgen's scene generation: one main segment for each step of each editor
 * segment, with its borders, sides and barriers. The corners come from the
 * editor's segment points; the heights of the borders and sides, and the
 * values between the corners, are worked out as the game does (AddSides, and
 * RtTrackLocal2Global and RtTrackHeightL in robottools).
 */
public class GameTrack
{
	// segment types and kinds, as in the game's track.h
	public static final int	TR_RGT			= 1;
	public static final int	TR_LFT			= 2;
	public static final int	TR_STR			= 3;

	public static final int	TR_MAIN			= 1;
	public static final int	TR_LSIDE		= 2;
	public static final int	TR_RSIDE		= 3;
	public static final int	TR_LBORDER		= 4;
	public static final int	TR_RBORDER		= 5;

	public static final int	TR_PLAN			= 0;
	public static final int	TR_CURB			= 1;
	public static final int	TR_WALL			= 2;
	public static final int	TR_FENCE		= 3;
	public static final int	TR_PITBUILDING	= 4;
	public static final int	TR_NO_BARRIER	= 5;
	public static final int	TR_FENCE1		= 6;
	public static final int	TR_FENCE2		= 7;

	public static final int	TR_SL			= 0;
	public static final int	TR_SR			= 1;
	public static final int	TR_EL			= 2;
	public static final int	TR_ER			= 3;

	public static final int	TR_SIDE_RGT		= 0;
	public static final int	TR_SIDE_LFT		= 1;

	public static final int	TR_PIT			= 0x10;
	public static final int	TR_PITBUILD		= 0x200;

	// the editor's segment points of each step (see Segment)
	private static final int	TRACK_START_LEFT			= 0;
	private static final int	TRACK_END_LEFT				= 1;
	private static final int	TRACK_END_RIGHT				= 2;
	private static final int	TRACK_START_RIGHT			= 3;
	private static final int	LEFT_BORDER_START_LEFT		= 4;
	private static final int	LEFT_BORDER_END_LEFT		= 5;
	private static final int	LEFT_SIDE_START_LEFT		= 8;
	private static final int	LEFT_SIDE_END_LEFT			= 9;
	private static final int	RIGHT_BORDER_START_RIGHT	= 16;
	private static final int	RIGHT_BORDER_END_RIGHT		= 17;
	private static final int	RIGHT_SIDE_START_RIGHT		= 20;
	private static final int	RIGHT_SIDE_END_RIGHT		= 21;

	/** A barrier, as tTrackBarrier. */
	public static final class Barrier
	{
		public int		style;
		public float	width;
		public float	height;
		public String	surface;
	}

	/** A main, border or side segment, as tTrackSeg. */
	public static final class Seg
	{
		public String		name;
		public int			id;
		public int			type;
		public int			type2;
		public int			style;
		public float		length;
		public float		lgfromstart;
		public float		width, startWidth, endWidth;
		public float		height;
		public float		radius, radiusr, radiusl, arc;
		public float		centerX, centerY;
		public float		angleZS, angleZE, angleXS, angleXE;
		public float		Kzl, Kzw, Kyl;
		/** corners: TR_SL, TR_SR, TR_EL, TR_ER; x, y, z */
		public final float[][]	vertex	= new float[4][3];
		public String		surface;
		public float		kRoughness, kRoughWaveLen;
		public int			raceInfo;
		/** the next segment out to the right and left; CSR also links borders back to the main segment */
		public Seg			rside, lside;
		public Seg			prev, next;
		public final Barrier[]	barrier	= new Barrier[2];

		public Seg side(int side)
		{
			return side == TR_SIDE_RGT ? rside : lside;
		}
	}

	public final EditorFrame.GameType	game;
	public final List<Seg>				segs	= new ArrayList<Seg>();
	public final float					width;
	public final float					trackStep;
	private final EditorFrame			editorFrame;

	private GameTrack(EditorFrame editorFrame)
	{
		this.editorFrame = editorFrame;
		game = editorFrame.getGameType();
		MainTrack mainTrack = editorFrame.getTrackData().getMainTrack();
		width = (float) mainTrack.getValidWidth();
		double step = editorFrame.getTrackData().getTerrainGeneration().getTrackStep();
		trackStep = (float) (Double.isNaN(step) || step <= 0 ? (game == EditorFrame.GameType.TORCS ? 5.0 : 10.0) : step);
	}

	/** @return the open track as the game builds it, or null when there is none */
	public static GameTrack build(EditorFrame editorFrame)
	{
		if (editorFrame.getTrackData() == null || editorFrame.getTrackData().getSegments() == null)
			return null;
		GameTrack track = new GameTrack(editorFrame);
		track.buildSegments();
		if (track.segs.isEmpty())
			return null;
		track.markPits();
		return track;
	}

	private void buildSegments()
	{
		MainTrack mainTrack = editorFrame.getTrackData().getMainTrack();
		// a side follows the track's banking unless its banking type is "level" (the default)
		int[] bankType = { bankType(mainTrack.getRight().getSideBankingType()), bankType(mainTrack.getLeft().getSideBankingType()) };
		float fromStart = 0;

		for (Segment segment : editorFrame.getTrackData().getSegments())
		{
			Point3D[] points = segment.getPoints();
			int steps = segment.nbSteps;
			if (points == null || steps <= 0)
				continue;
			int stride = points.length / steps;
			int type = segment instanceof Curve ? ("lft".equals(segment.getType()) ? TR_LFT : TR_RGT) : TR_STR;
			GameSides.State[] sides = { GameSides.of(segment, true, editorFrame), GameSides.of(segment, false, editorFrame) };
			String surfaceName = segment.getValidSurface(editorFrame);
			float stepLength = (float) Math.abs(segment.stepLength);

			for (int step = 0; step < steps; step++)
			{
				int o = step * stride;
				Seg m = new Seg();
				m.name = segment.getName();
				m.id = segs.size();
				m.type = type;
				m.type2 = TR_MAIN;
				m.style = TR_PLAN;
				set(m.vertex[TR_SL], points[o + TRACK_START_LEFT]);
				set(m.vertex[TR_SR], points[o + TRACK_START_RIGHT]);
				set(m.vertex[TR_EL], points[o + TRACK_END_LEFT]);
				set(m.vertex[TR_ER], points[o + TRACK_END_RIGHT]);
				m.length = stepLength;
				m.lgfromstart = fromStart;
				fromStart += stepLength;
				m.width = m.startWidth = m.endWidth = width;
				setSurface(m, surfaceName);

				// the heading: the start edge turned right
				float zs = (float) (Math.atan2(m.vertex[TR_SL][1] - m.vertex[TR_SR][1], m.vertex[TR_SL][0] - m.vertex[TR_SR][0]) - Math.PI / 2);
				m.angleZS = zs;
				if (type == TR_STR)
				{
					m.angleZE = zs;
				}
				else
				{
					// the step's radius and turn, as the game and Curve.calcShape work them out
					Curve curve = (Curve) segment;
					double rs = curve.getRadiusStart();
					double re = curve.hasRadiusEnd() ? curve.getRadiusEnd() : rs;
					double dradius = re == rs ? 0 : steps != 1 ? (re - rs) / (steps - 1) : re - rs;
					double radius = rs + step * dradius;
					double arc = stepLength / radius;
					m.arc = (float) arc;
					m.angleZE = (float) (type == TR_LFT ? zs + arc : zs - arc);
					m.radius = (float) radius;
					float w2 = width / 2;
					m.radiusr = type == TR_LFT ? m.radius + w2 : m.radius - w2;
					m.radiusl = type == TR_LFT ? m.radius - w2 : m.radius + w2;
					// the right corner is radiusr from the center
					double sx = Math.sin(zs), cy = Math.cos(zs);
					if (type == TR_LFT)
					{
						m.centerX = (float) (m.vertex[TR_SR][0] - m.radiusr * sx);
						m.centerY = (float) (m.vertex[TR_SR][1] + m.radiusr * cy);
					}
					else
					{
						m.centerX = (float) (m.vertex[TR_SR][0] + m.radiusr * sx);
						m.centerY = (float) (m.vertex[TR_SR][1] - m.radiusr * cy);
					}
				}
				m.angleXS = (float) Math.atan2(m.vertex[TR_SL][2] - m.vertex[TR_SR][2], width);
				m.angleXE = (float) Math.atan2(m.vertex[TR_EL][2] - m.vertex[TR_ER][2], width);
				slopes(m);

				for (int side = 0; side < 2; side++)
					addSides(m, side, sides[side], bankType[side], points, o, step, steps);

				if (!segs.isEmpty())
				{
					Seg last = segs.get(segs.size() - 1);
					last.next = m;
					m.prev = last;
				}
				segs.add(m);
			}
		}
		if (!segs.isEmpty())
		{
			Seg first = segs.get(0), last = segs.get(segs.size() - 1);
			last.next = first;
			first.prev = last;
		}
	}

	/** Adds a main segment's border, side and barrier on one side, as AddSides does. */
	private void addSides(Seg m, int side, GameSides.State state, int type, Point3D[] points, int o, int step, int steps)
	{
		boolean right = side == TR_SIDE_RGT;
		Seg inner = m;

		float bw = (float) state.borderWidth;
		if (bw != 0)
		{
			Seg b = element(m, inner, right ? TR_RBORDER : TR_LBORDER, type, bw, bw,
				points[o + (right ? RIGHT_BORDER_START_RIGHT : LEFT_BORDER_START_LEFT)],
				points[o + (right ? RIGHT_BORDER_END_RIGHT : LEFT_BORDER_END_LEFT)]);
			b.height = (float) state.borderHeight;
			b.style = "plan".equals(state.borderStyle) ? TR_PLAN : "curb".equals(state.borderStyle) ? TR_CURB : TR_WALL;
			if (b.style == TR_PLAN)
				b.height = 0;
			setSurface(b, state.borderSurface);
			// the CSR loader links a border back to its main segment
			if (game == EditorFrame.GameType.CARS_SPORTS_RACING)
			{
				if (right)
					b.lside = m;
				else
					b.rside = m;
			}
			inner = b;
		}

		float sw0 = (float) state.sideStartWidth, ew0 = (float) state.sideEndWidth;
		float kew = (ew0 - sw0) / (float) steps;
		float sw = sw0 + (float) step * kew;
		float ew = sw0 + (float) (step + 1) * kew;
		if (sw != 0 || ew != 0)
		{
			Seg s = element(m, inner, right ? TR_RSIDE : TR_LSIDE, type, sw, ew,
				points[o + (right ? RIGHT_SIDE_START_RIGHT : LEFT_SIDE_START_LEFT)],
				points[o + (right ? RIGHT_SIDE_END_RIGHT : LEFT_SIDE_END_LEFT)]);
			s.style = TR_PLAN;
			setSurface(s, state.sideSurface);
		}

		Barrier barrier = new Barrier();
		barrier.style = barrierStyle(state.barrierStyle);
		barrier.width = (float) state.barrierWidth;
		barrier.height = (float) state.barrierHeight;
		barrier.surface = state.barrierSurface;
		m.barrier[side] = barrier;
	}

	/** @return a border or side outside inner: its outer corners from the editor's points, its heights as AddSides makes them */
	private Seg element(Seg m, Seg inner, int type2, int bankType, float startWidth, float endWidth, Point3D outerStart, Point3D outerEnd)
	{
		boolean right = type2 == TR_RBORDER || type2 == TR_RSIDE;
		Seg e = new Seg();
		e.name = m.name;
		e.id = m.id;
		e.type = m.type;
		e.type2 = type2;
		e.startWidth = startWidth;
		e.endWidth = endWidth;
		e.width = Math.min(startWidth, endWidth);
		e.angleXS = inner.angleXS * (float) bankType;
		e.angleXE = inner.angleXE * (float) bankType;
		e.angleZS = m.angleZS;
		e.angleZE = m.angleZE;
		e.arc = m.arc;
		e.centerX = m.centerX;
		e.centerY = m.centerY;
		float tanS = (float) Math.tan(inner.angleXS), tanE = (float) Math.tan(inner.angleXE);
		if (right)
		{
			copy(e.vertex[TR_SL], inner.vertex[TR_SR]);
			copy(e.vertex[TR_EL], inner.vertex[TR_ER]);
			setXY(e.vertex[TR_SR], outerStart);
			setXY(e.vertex[TR_ER], outerEnd);
			e.vertex[TR_SR][2] = e.vertex[TR_SL][2] - (float) bankType * startWidth * tanS;
			e.vertex[TR_ER][2] = e.vertex[TR_EL][2] - (float) bankType * endWidth * tanE;
			if (m.type != TR_STR)
			{
				e.radiusl = inner.radiusr;
				e.radiusr = m.type == TR_LFT ? e.radiusl + startWidth : e.radiusl - startWidth;
			}
			inner.rside = e;
		}
		else
		{
			copy(e.vertex[TR_SR], inner.vertex[TR_SL]);
			copy(e.vertex[TR_ER], inner.vertex[TR_EL]);
			setXY(e.vertex[TR_SL], outerStart);
			setXY(e.vertex[TR_EL], outerEnd);
			e.vertex[TR_SL][2] = e.vertex[TR_SR][2] + (float) bankType * startWidth * tanS;
			e.vertex[TR_EL][2] = e.vertex[TR_ER][2] + (float) bankType * endWidth * tanE;
			if (m.type != TR_STR)
			{
				e.radiusr = inner.radiusl;
				e.radiusl = m.type == TR_LFT ? e.radiusr - startWidth : e.radiusr + startWidth;
			}
			inner.lside = e;
		}
		if (m.type == TR_STR)
		{
			e.length = m.length;
		}
		else
		{
			e.radius = (e.radiusl + e.radiusr) / 2;
			e.length = e.radius * e.arc;
		}
		slopes(e);
		e.Kyl = (endWidth - startWidth) / (m.type == TR_STR ? e.length : e.arc);
		return e;
	}

	/** Sets the slopes RtTrackHeightL uses: along the right edge and of the banking. */
	private static void slopes(Seg s)
	{
		float along = s.type == TR_STR ? s.length : s.arc;
		if (along == 0)
			return;
		s.Kzl = (s.vertex[TR_ER][2] - s.vertex[TR_SR][2]) / along;
		s.Kzw = (s.angleXE - s.angleXS) / along;
	}

	private void setSurface(Seg s, String name)
	{
		s.surface = name;
		Surface surface = name == null ? null : RoadTexture.findSurface(editorFrame, name);
		double roughness = surface == null || Double.isNaN(surface.getRoughness()) ? 0 : surface.getRoughness();
		double wavelength = surface == null || Double.isNaN(surface.getRoughnessWavelength()) ? 1 : surface.getRoughnessWavelength();
		s.kRoughness = (float) (roughness / 2);
		s.kRoughWaveLen = (float) (2 * Math.PI / wavelength);
	}

	private int barrierStyle(String style)
	{
		if ("fence".equals(style))
			return TR_FENCE;
		if ("fence1".equals(style))
			return TR_FENCE1;
		if ("fence2".equals(style))
			return TR_FENCE2;
		if ("no barrier".equals(style))
			return TR_NO_BARRIER;
		return TR_WALL;
	}

	private static int bankType(String type)
	{
		return type == null || type.equals("level") ? 0 : 1;
	}

	/** Marks the barriers at the pit buildings, and the pit lane sides, as the game's InitPits does. */
	private void markPits()
	{
		Pits pits = editorFrame.getTrackData().getMainTrack().getPits();
		if (pits == null)
			return;
		boolean right = !"left".equals(pits.getSide());
		int side = right ? TR_SIDE_RGT : TR_SIDE_LFT;
		Seg entry = first(pits.getEntry()), exit = last(pits.getExit());
		Seg start = first(pits.getStart()), end = last(pits.getEnd());

		if (game == EditorFrame.GameType.TORCS)
		{
			if (entry == null || exit == null || start == null || end == null)
				return;
			// the pit lane: its side has TR_PIT, and its barrier is the pit buildings
			for (Seg m = start; ; m = m.next)
			{
				m.barrier[side] = copyWithStyle(m.barrier[side], TR_PITBUILDING);
				Seg s = m.side(side);
				if (s != null)
				{
					s.raceInfo |= TR_PIT;
					if (s.side(side) != null)
						s.side(side).raceInfo |= TR_PIT;
				}
				if (m == end)
					break;
			}
			return;
		}

		// Speed Dreams and CSR: from the pit buildings start up to (not including) their end
		Seg buildingsStart = null, buildingsEnd = null;
		if (start != null)
		{
			buildingsStart = pits.getStartBuildings() == null ? start : first(pits.getStartBuildings());
			if (buildingsStart == null)
				buildingsStart = start;
		}
		if (end != null)
		{
			buildingsEnd = pits.getStopBuildings() == null ? end : last(pits.getStopBuildings());
			if (buildingsEnd == null)
				buildingsEnd = end;
		}
		if (buildingsStart == null || buildingsEnd == null)
			return;
		for (Seg m = buildingsStart; m != buildingsEnd; m = m.next)
		{
			m.barrier[side] = copyWithStyle(m.barrier[side], TR_PITBUILDING);
			Seg s = m.side(side);
			if (s != null && s.side(side) != null)
				s.side(side).raceInfo |= TR_PITBUILD;
			if (m.next == buildingsStart)
				break;
		}
	}

	private static Barrier copyWithStyle(Barrier barrier, int style)
	{
		Barrier b = new Barrier();
		b.style = style;
		b.width = barrier.width;
		b.height = barrier.height;
		b.surface = barrier.surface;
		return b;
	}

	/** @return the first step of the last run of segments with this name, as the game's search finds it */
	private Seg first(String name)
	{
		if (name == null)
			return null;
		Seg s = segs.get(segs.size() - 1);
		boolean found = false;
		for (int i = 0; i <= segs.size(); i++)
		{
			if (name.equals(s.name))
				found = true;
			else if (found)
				return s.next;
			s = s.prev;
		}
		return found ? s.next : null;
	}

	/** @return the last step of the last segment with this name */
	private Seg last(String name)
	{
		if (name == null)
			return null;
		Seg s = segs.get(segs.size() - 1);
		for (int i = 0; i <= segs.size(); i++)
		{
			if (name.equals(s.name))
				return s;
			s = s.prev;
		}
		return null;
	}

	// RtTrackGetWidth
	public static float width(Seg seg, float toStart)
	{
		return Math.abs(seg.startWidth + toStart * seg.Kyl);
	}

	/** RtTrackLocal2Global with TR_TORIGHT: toStart is a length on a straight and an angle in a curve. */
	public static float[] local2Global(Seg seg, float toStart, float toRight)
	{
		boolean leftKind = seg.type2 == TR_MAIN || seg.type2 == TR_LSIDE || seg.type2 == TR_LBORDER;
		boolean rightKind = seg.type2 == TR_RSIDE || seg.type2 == TR_RBORDER;
		float x, y;
		switch (seg.type)
		{
		case TR_STR:
		{
			float tr = leftKind ? toRight : rightKind ? toRight - seg.Kyl * toStart : 0;
			float cos = (float) Math.cos(seg.angleZS), sin = (float) Math.sin(seg.angleZS);
			x = seg.vertex[TR_SR][0] + toStart * cos - tr * sin;
			y = seg.vertex[TR_SR][1] + toStart * sin + tr * cos;
			break;
		}
		case TR_LFT:
		{
			float a = seg.angleZS + toStart;
			float r = leftKind ? seg.radiusr - toRight : rightKind ? seg.radiusl + seg.startWidth + seg.Kyl * toStart - toRight : 0;
			x = seg.centerX + r * (float) Math.sin(a);
			y = seg.centerY - r * (float) Math.cos(a);
			break;
		}
		default:
		{
			float a = seg.angleZS - toStart;
			float r = leftKind ? seg.radiusr + toRight : rightKind ? seg.radiusl - seg.startWidth - seg.Kyl * toStart + toRight : 0;
			x = seg.centerX - r * (float) Math.sin(a);
			y = seg.centerY + r * (float) Math.cos(a);
			break;
		}
		}
		return new float[] { x, y };
	}

	/** RtTrackHeightL: the height of the road or side, with the surface's roughness and a curb's height. */
	public float heightL(Seg seg, float toStart, float toRight)
	{
		float tr = toRight;
		if (tr < 0 && seg.rside != null)
		{
			seg = seg.rside;
			tr += seg.width;
			if (tr < 0 && seg.rside != null)
			{
				seg = seg.rside;
				tr += width(seg, toStart);
			}
		}
		else if (tr > seg.width && seg.lside != null)
		{
			tr -= seg.width;
			seg = seg.lside;
			if (tr > seg.width && seg.lside != null)
			{
				tr -= width(seg, toStart);
				seg = seg.lside;
			}
		}
		float lg = seg.type == TR_STR ? toStart : toStart * seg.radius;
		float base = seg.vertex[TR_SR][2] + toStart * seg.Kzl;
		float angle = seg.angleXS + toStart * seg.Kzw;
		if (game == EditorFrame.GameType.TORCS)
		{
			float height = base + tr * (float) Math.tan(angle);
			if (seg.style == TR_CURB)
			{
				float alpha = seg.type2 == TR_RBORDER ? seg.width - tr : tr;
				float roughness = seg.kRoughness * (float) Math.sin(seg.kRoughWaveLen * lg);
				return height + alpha * (seg.height + roughness) / seg.width;
			}
			return height + seg.kRoughness * (float) Math.sin(seg.kRoughWaveLen * tr) * (float) Math.sin(seg.kRoughWaveLen * lg);
		}
		if (seg.style == TR_CURB)
		{
			float slope = (float) Math.atan2(seg.height, seg.width);
			if (seg.type2 == TR_RBORDER)
			{
				float alpha = seg.width - tr;
				float noise = seg.kRoughness * (float) Math.sin(seg.kRoughWaveLen * lg) * alpha / seg.width;
				return base + tr * (float) Math.tan(angle) + alpha * slope + noise;
			}
			return base + tr * ((float) Math.tan(angle) + slope) + seg.kRoughness * (float) Math.sin(seg.kRoughWaveLen * lg) * tr / seg.width;
		}
		return base + tr * (float) Math.tan(angle) + seg.kRoughness * (float) Math.sin(seg.kRoughWaveLen * tr) * (float) Math.sin(seg.kRoughWaveLen * lg);
	}

	private static void set(float[] v, Point3D p)
	{
		v[0] = (float) p.x;
		v[1] = (float) p.y;
		v[2] = (float) p.z;
	}

	private static void setXY(float[] v, Point3D p)
	{
		v[0] = (float) p.x;
		v[1] = (float) p.y;
	}

	private static void copy(float[] to, float[] from)
	{
		to[0] = from[0];
		to[1] = from[1];
		to[2] = from[2];
	}

	/**
	 * A track camera: where the game puts it, how high it is above the ground there,
	 * and the middle of the road it films, all along it and halfway.
	 */
	public static final class CameraView
	{
		public final String		name;
		public final float[]	eye;
		public final float		height;
		public final float[][]	filmed;
		public final float[]	target;

		CameraView(String name, float[] eye, float height, float[][] filmed, float[] target)
		{
			this.name = name;
			this.eye = eye;
			this.height = height;
			this.filmed = filmed;
			this.target = target;
		}
	}

	/**
	 * The track's cameras where the game's track loader puts them (ReadTrack4): "to
	 * start" along and "to right" across the first part of their segment ("to start"
	 * is an angle in a curve), "height" above the ground there. Each looks at the
	 * middle of the road it films, from its "fov start" segment up to its "fov end"
	 * one. A camera whose segment isn't in the track is left out.
	 */
	public List<CameraView> cameras(List<Camera> cameras)
	{
		List<CameraView> views = new ArrayList<CameraView>();
		if (cameras == null)
			return views;
		for (Camera camera : cameras)
		{
			Seg seg = firstStep(camera.getSegment());
			if (seg == null)
				continue;
			// GfParmGetCurNum: in SI units, 0 when missing
			float toStart = (float) orZero(camera.getToStartUnits() == null ? camera.getToStart()
				: GfParmNumbers.toSI(camera.getToStartUnits(), camera.getToStart(), game, null));
			float toRight = (float) orZero(camera.getToRight());
			float[] xy = local2Global(seg, toStart, toRight);
			float height = (float) orZero(camera.getHeight());
			float[] eye = { xy[0], xy[1], heightL(seg, toStart, toRight) + height };

			// the segments it films, as the loader gives them the camera
			Seg start = firstStep(camera.getFovStart()), end = firstStep(camera.getFovEnd());
			if (start == null)
				start = seg;
			List<Seg> filmed = new ArrayList<Seg>();
			float length = 0;
			Seg s = start;
			do
			{
				filmed.add(s);
				length += s.length;
				s = s.next;
			}
			while (s != end && s != start);
			// the middle of the road along it, and halfway
			int n = (int) Math.min(400, Math.max(2, Math.ceil(length / 2)));
			float[][] points = new float[n + 1][];
			for (int i = 0; i <= n; i++)
				points[i] = middle(filmed, length * i / n);
			views.add(new CameraView(camera.getName(), eye, height, points, middle(filmed, length / 2)));
		}
		return views;
	}

	/** @return the point in the middle of the road this far along the segments */
	private float[] middle(List<Seg> segments, float distance)
	{
		Seg s = segments.get(segments.size() - 1);
		for (Seg f : segments)
		{
			if (distance <= f.length)
			{
				s = f;
				break;
			}
			distance -= f.length;
		}
		distance = Math.max(0, Math.min(distance, s.length));
		float along = s.type == TR_STR ? distance : distance / s.radius;
		float across = s.width / 2;
		float[] xy = local2Global(s, along, across);
		return new float[] { xy[0], xy[1], heightL(s, along, across) };
	}

	/** @return the first step of the named segment: the one its id names */
	private Seg firstStep(String name)
	{
		if (name != null)
			for (Seg seg : segs)
				if (name.equals(seg.name))
					return seg;
		return null;
	}

	private static double orZero(double value)
	{
		return Double.isNaN(value) ? 0 : value;
	}
}
