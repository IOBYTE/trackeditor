/*
 *   SceneCsrSd.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package gui.view3d;

import static gui.view3d.GameTrack.*;

import gui.EditorFrame;
import gui.view3d.GameTrack.Barrier;
import gui.view3d.GameTrack.Seg;

/**
 * The scene as Speed Dreams' and CSR's trackgen make it (tools/trackgen
 * track.cpp, InitScene): the main track, the right border and side, the left
 * border and side, then the right and left barriers. The two differ in a few
 * places, all kept: Speed Dreams turns the texture of right border walls and
 * of right barriers around, has the fence1 and fence2 barriers, and starts a
 * new barrier strip only where the barriers don't meet; CSR starts a left
 * barrier strip on every segment and caps nearly every left wall.
 */
public class SceneCsrSd extends TrackScene
{
	private final boolean		sd;
	// where the barrier's distance was last measured from; trackgen keeps it from one barrier to the next
	private float				xprev, yprev;

	SceneCsrSd(GameTrack track, EditorFrame editorFrame)
	{
		super(track, editorFrame);
		sd = track.game == EditorFrame.GameType.SPEED_DREAMS;
	}

	@Override
	protected void generate()
	{
		mainTrack();
		border(TR_SIDE_RGT);
		side(TR_SIDE_RGT);
		border(TR_SIDE_LFT);
		side(TR_SIDE_LFT);
		rightBarrier();
		leftBarrier();
	}

	/** A border: plan in one strip, curbs in two (top, then the face), walls in three. */
	private void border(int side)
	{
		part = Part.BORDER;
		boolean right = side == TR_SIDE_RGT;
		int borderType = right ? TR_RBORDER : TR_LBORDER;
		Seg lastSeg = null;
		for (int j = 0; j < 3; j++)
		{
			prevTexId = 0;
			startNeeded = true;
			float running = 0;
			String sname = "t" + j + (right ? "RB" : "LB");
			for (int i = 0; i < track.segs.size(); i++)
			{
				Seg mseg = track.segs.get(i);
				Seg seg = mseg.side(side);
				if (seg == null || seg.type2 != borderType)
				{
					startNeeded = true;
					lastSeg = null;
					continue;
				}
				Seg prev = mseg.prev.side(side);
				if ((lastSeg != null && lastSeg.style != seg.style) || prev == null || prev.width != seg.width || prev.height != seg.height)
					startNeeded = true;
				checkDispList(seg.surface, sname, i, mseg.lgfromstart);
				float curTexSeg = (curTexLink ? mseg.lgfromstart : 0) + curTexOffset;
				float texLen = curTexSeg / curTexSize;
				if (startNeeded || running > LG_STEP_MAX)
				{
					newDispList(false, sname, i);
					running = 0;
					float texMaxT = texMaxT(GameTrack.width(seg, 0));
					boolean startsCurb = prev == null || prev.type2 != borderType || prev.style != TR_CURB ||
						prev.width != seg.width || prev.height != seg.height;
					boolean startsWall = prev == null || prev.type2 != borderType || prev.style != TR_WALL ||
						prev.height < seg.height || prev.width < seg.width;
					float cap = seg.width / curTexSize;
					if (right)
					{
						if (seg.style == TR_CURB && j == 0 && startsCurb)
						{
							setPoint(texLen, 0, seg.vertex[TR_SL], -0.1f);
							setPoint(texLen, texMaxT, seg.vertex[TR_SR], 0);
						}
						if (seg.style == TR_WALL && j == 2 && startsWall)
						{
							float u = sd ? -texLen - cap : texLen - cap;
							setPoint(u, 0.66f, seg.vertex[TR_SL], seg.height);
							setPoint(u, 1.0f, seg.vertex[TR_SL], 0);
						}
					}
					else
					{
						if (seg.style == TR_CURB && j == 1 && startsCurb)
						{
							setPoint(texLen, texMaxT, seg.vertex[TR_SL], 0);
							setPoint(texLen, 0, seg.vertex[TR_SR], -0.1f);
						}
						if (seg.style == TR_WALL && j == 0 && startsWall)
						{
							setPoint(texLen - cap, 1.0f, seg.vertex[TR_SR], 0);
							setPoint(texLen - cap, 0.66f, seg.vertex[TR_SR], seg.height);
						}
					}
					borderSection(seg, right, j, texLen, texMaxT, seg.vertex[TR_SL], seg.vertex[TR_SR]);
				}
				texLen += texStep(seg, mseg);
				float texStep = texStep(seg, mseg);
				for (float ts : steps(seg, mseg))
				{
					float w = GameTrack.width(seg, ts);
					borderSection(seg, right, j, texLen, texMaxT(w), point(seg, ts, w), point(seg, ts, 0));
					texLen += texStep;
				}
				texLen = (curTexSeg + mseg.length) / curTexSize;
				float texMaxT = texMaxT(GameTrack.width(seg, endToStart(seg)));
				borderSection(seg, right, j, texLen, texMaxT, seg.vertex[TR_EL], seg.vertex[TR_ER]);
				Seg next = mseg.next.side(side);
				boolean endsCurb = next != null && (next.type2 != borderType || next.style != TR_CURB ||
					next.width != seg.width || next.height != seg.height);
				boolean endsWall = next != null && (next.type2 != borderType || next.style != TR_WALL ||
					next.height < seg.height || next.width < seg.width);
				float cap = seg.width / curTexSize;
				if (right)
				{
					if (seg.style == TR_CURB && j == 0 && endsCurb)
					{
						setPoint(texLen, 0, seg.vertex[TR_EL], -0.1f);
						setPoint(texLen, texMaxT, seg.vertex[TR_ER], 0);
					}
					if (seg.style == TR_WALL && j == 2 && endsWall)
					{
						float u = sd ? -texLen + cap : texLen + cap;
						setPoint(u, 0.66f, seg.vertex[TR_EL], seg.height);
						setPoint(u, 1.0f, seg.vertex[TR_EL], 0);
					}
				}
				else
				{
					if (seg.style == TR_CURB && j == 1 && endsCurb)
					{
						setPoint(texLen, texMaxT, seg.vertex[TR_EL], 0);
						setPoint(texLen, 0, seg.vertex[TR_ER], -0.1f);
					}
					if (seg.style == TR_WALL && j == 0 && endsWall)
					{
						setPoint(texLen + cap, 1.0f, seg.vertex[TR_ER], 0);
						setPoint(texLen + cap, 0.66f, seg.vertex[TR_ER], seg.height);
					}
				}
				startNeeded = false;
				running += seg.length;
				lastSeg = seg;
			}
		}
	}

	/**
	 * One cross-section of a border strip, from its left point l (toRight is the
	 * width) to its right point r (toRight 0); the heights are each point's own.
	 */
	private void borderSection(Seg seg, boolean right, int j, float texLen, float texMaxT, float[] l, float[] r)
	{
		float h = seg.height;
		switch (seg.style)
		{
		case TR_PLAN:
			if (j == 0)
			{
				setPoint(texLen, right ? 0 : texMaxT, l, 0);
				setPoint(texLen, right ? texMaxT : 0, r, 0);
			}
			break;
		case TR_CURB:
			if (right)
			{
				if (j == 0)
				{
					setPoint(texLen, 0, l, 0);
					setPoint(texLen, texMaxT, r, h);
				}
				else if (j == 1)
				{
					setPoint(texLen, texMaxT, r, h);
					setPoint(texLen, texMaxT, r, 0);
				}
			}
			else
			{
				if (j == 0)
				{
					setPoint(texLen, texMaxT, l, 0);
					setPoint(texLen, texMaxT, l, h);
				}
				else if (j == 1)
				{
					setPoint(texLen, texMaxT, l, h);
					setPoint(texLen, 0, r, 0);
				}
			}
			break;
		case TR_WALL:
			if (right)
			{
				float u = sd ? -texLen : texLen;
				if (j == 0)
				{
					setPoint(u, 0, l, 0);
					setPoint(u, 0.33f, l, h);
				}
				else if (j == 1)
				{
					setPoint(u, 0.33f, l, h);
					setPoint(u, 0.66f, r, h);
				}
				else
				{
					setPoint(u, 0.66f, r, h);
					setPoint(u, 1.0f, r, 0);
				}
			}
			else
			{
				if (j == 0)
				{
					setPoint(texLen, 1.0f, l, 0);
					setPoint(texLen, 0.66f, l, h);
				}
				else if (j == 1)
				{
					setPoint(texLen, 0.66f, l, h);
					setPoint(texLen, 0.33f, r, h);
				}
				else
				{
					setPoint(texLen, 0.33f, r, h);
					setPoint(texLen, 0.0f, r, 0);
				}
			}
			break;
		}
	}

	/** A side: outside the border, or the road when there's no border. */
	private void side(int side)
	{
		part = Part.SIDE;
		boolean right = side == TR_SIDE_RGT;
		int sideType = right ? TR_RSIDE : TR_LSIDE;
		String name = right ? "tkRS" : "tkLS";
		prevTexId = 0;
		startNeeded = true;
		float running = 0;
		boolean hasBorder = false;
		Seg lastSeg = null;
		for (int i = 0; i < track.segs.size(); i++)
		{
			Seg mseg = track.segs.get(i);
			Seg first = mseg.side(side);
			if (first == null || (first.type2 != sideType && first.side(side) == null))
			{
				startNeeded = true;
				continue;
			}
			Seg seg = first;
			if (seg.side(side) != null)
			{
				seg = seg.side(side);
				if (!hasBorder)
				{
					startNeeded = true;
					hasBorder = true;
				}
			}
			else if (hasBorder)
			{
				startNeeded = true;
				hasBorder = false;
			}
			if (!startNeeded && lastSeg != null)
			{
				if (distance(lastSeg.vertex[TR_EL], seg.vertex[TR_SL]) > 0.01f || distance(lastSeg.vertex[TR_ER], seg.vertex[TR_SR]) > 0.01f)
					startNeeded = true;
			}
			checkDispList(seg.surface, name, i, mseg.lgfromstart);
			float curTexSeg = (curTexLink ? mseg.lgfromstart : 0) + curTexOffset;
			float texLen = curTexSeg / curTexSize;
			if (startNeeded || running > LG_STEP_MAX)
			{
				newDispList(false, name, i);
				running = 0;
				float texMaxT = texMaxT(GameTrack.width(seg, 0));
				setPoint(texLen, right ? 0 : texMaxT, seg.vertex[TR_SL], 0);
				setPoint(texLen, right ? texMaxT : 0, seg.vertex[TR_SR], 0);
			}
			float texStep = texStep(seg, mseg);
			texLen += texStep;
			for (float ts : steps(seg, mseg))
			{
				float w = GameTrack.width(seg, ts);
				float texMaxT = texMaxT(w);
				setPoint(texLen, right ? 0 : texMaxT, point(seg, ts, w), 0);
				setPoint(texLen, right ? texMaxT : 0, point(seg, ts, 0), 0);
				texLen += texStep;
			}
			texLen = (curTexSeg + mseg.length) / curTexSize;
			float texMaxT = texMaxT(GameTrack.width(seg, endToStart(seg)));
			setPoint(texLen, right ? 0 : texMaxT, seg.vertex[TR_EL], 0);
			setPoint(texLen, right ? texMaxT : 0, seg.vertex[TR_ER], 0);
			startNeeded = false;
			running += seg.length;
			lastSeg = seg;
		}
	}

	private static float distance(float[] a, float[] b)
	{
		float dx = a[0] - b[0], dy = a[1] - b[1], dz = a[2] - b[2];
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	private static boolean differ(Barrier a, Barrier b)
	{
		return a.style != b.style || a.width != b.width || a.height != b.height;
	}

	private static boolean apart(float[] a, float[] b)
	{
		return Math.abs(a[0] - b[0]) > 0.001f || Math.abs(a[1] - b[1]) > 0.001f;
	}

	private static Seg outermost(Seg seg, int side)
	{
		while (seg.side(side) != null)
			seg = seg.side(side);
		return seg;
	}

	/** The right barrier: along the outer edge of the right side; its u is the distance along that edge. */
	private void rightBarrier()
	{
		part = Part.BARRIER;
		Barrier lastBarrier = track.segs.get(0).prev.barrier[TR_SIDE_RGT];
		curDoubleSided = false;
		for (int j = 0; j < 3; j++)
		{
			prevTexId = 0;
			float texLen = 0;
			startNeeded = true;
			float running = 0;
			String sname = "B" + j + "Rt";
			for (int i = 0; i < track.segs.size(); i++)
			{
				Seg mseg = track.segs.get(i);
				Seg seg = outermost(mseg, TR_SIDE_RGT);
				Barrier bar = mseg.barrier[TR_SIDE_RGT];
				if ((seg.raceInfo & TR_PITBUILD) != 0 || bar.style == TR_NO_BARRIER)
				{
					startNeeded = true;
					running = 0;
					lastBarrier = bar;
					xprev = 0;
					yprev = 0;
					continue;
				}
				boolean startPolygonNeeded = false;
				if (lastBarrier != null)
				{
					if (differ(lastBarrier, bar))
					{
						startNeeded = true;
					}
					else if (sd)
					{
						Seg prevSide = outermost(mseg.prev, TR_SIDE_RGT);
						startPolygonNeeded = apart(seg.vertex[TR_SR], prevSide.vertex[TR_ER]);
						if (startPolygonNeeded)
							startNeeded = true;
					}
					else if (Math.abs(xprev - seg.vertex[TR_SR][0]) > 0.001f || Math.abs(yprev - seg.vertex[TR_SR][1]) > 0.001f)
					{
						startNeeded = true;
						startPolygonNeeded = true;
					}
				}
				curDoubleSided = bar.style == TR_FENCE;
				curBackToBack = bar.style == TR_FENCE1 || bar.style == TR_FENCE2;
				checkDispList(bar.surface, sname, i, 0);
				if (!curTexLink)
					texLen = 0;
				if (bar.style == TR_FENCE && j != 0)
				{
					xprev = seg.vertex[TR_SR][0];
					yprev = seg.vertex[TR_SR][1];
					lastBarrier = bar;
					continue;
				}
				float h = bar.height, w = bar.width;
				if (startNeeded || running > LG_STEP_MAX)
				{
					newDispList(false, sname, i);
					if (curTexType == 0)
						texLen = 0;
					running = 0;
					xprev = seg.vertex[TR_SR][0];
					yprev = seg.vertex[TR_SR][1];
					float[] edge = seg.vertex[TR_SR];
					float[] out = withZ(GameTrack.local2Global(seg, 0, -w), edge[2]);
					Barrier prevBar = mseg.prev.barrier[TR_SIDE_RGT];
					boolean cap = prevBar.style != TR_WALL || prevBar.height != h || prevBar.width != w || startPolygonNeeded;
					rightBarrierSection(bar, j, texLen, edge, out, cap ? -1 : 0);
				}
				float texStep;
				for (float ts : steps(seg, mseg))
				{
					float[] xy = GameTrack.local2Global(seg, ts, 0);
					float curHeight = track.heightL(seg, ts, 0);
					texStep = (float) Math.sqrt((xy[1] - yprev) * (xy[1] - yprev) + (xy[0] - xprev) * (xy[0] - xprev)) / curTexSize;
					texLen += texStep;
					xprev = xy[0];
					yprev = xy[1];
					float[] edge = { xy[0], xy[1], curHeight };
					float[] out = withZ(GameTrack.local2Global(seg, ts, -w), curHeight);
					rightBarrierSection(bar, j, texLen, edge, out, 0);
				}
				float[] er = seg.vertex[TR_ER];
				texStep = (float) Math.sqrt((er[1] - yprev) * (er[1] - yprev) + (er[0] - xprev) * (er[0] - xprev)) / curTexSize;
				texLen += texStep;
				xprev = er[0];
				yprev = er[1];
				float[] out = withZ(GameTrack.local2Global(seg, endToStart(seg), -w), er[2]);
				Seg nextSide = outermost(mseg.next, TR_SIDE_RGT);
				boolean endPolygonNeeded = Math.abs(xprev - nextSide.vertex[TR_SR][0]) > 0.001f || Math.abs(yprev - nextSide.vertex[TR_SR][1]) > 0.001f;
				Barrier nextBar = mseg.next.barrier[TR_SIDE_RGT];
				boolean cap = nextBar.style != TR_WALL || nextBar.height != h || nextBar.width != w || endPolygonNeeded;
				rightBarrierSection(bar, j, texLen, er, out, cap ? 1 : 0);
				startNeeded = false;
				running += seg.length;
				lastBarrier = bar;
			}
		}
	}

	/**
	 * A cross-section of the right barrier at the side's edge e, with the wall's
	 * outer edge o; cap -1 or 1 adds a wall's start or end cap.
	 */
	private void rightBarrierSection(Barrier bar, int j, float texLen, float[] e, float[] o, int cap)
	{
		float h = bar.height;
		float t = sd ? -texLen : texLen;
		switch (bar.style)
		{
		case TR_FENCE:
			if (j == 0)
			{
				setPoint(t, 0, e, 0);
				setPoint(t, 1, e, h);
			}
			break;
		case TR_FENCE1:
			if (j == 0)
			{
				setPoint(-texLen, 0, e, 0);
				setPoint(-texLen, 1, e, h);
			}
			else if (j == 2)
			{
				setPoint(texLen, 1, e, h);
				setPoint(texLen, 0, e, 0);
			}
			break;
		case TR_FENCE2:
			if (j == 0)
			{
				setPoint(-texLen, 0, e, 0);
				setPoint(-texLen, 0.5f, e, h);
			}
			else if (j == 2)
			{
				setPoint(-texLen, 0.5f, e, h);
				setPoint(-texLen, 1, e, 0);
			}
			break;
		case TR_WALL:
			if (j == 0)
			{
				setPoint(t, 0, e, 0);
				setPoint(t, 0.33f, e, h);
			}
			else if (j == 1)
			{
				setPoint(t, 0.33f, e, h);
				setPoint(t, 0.66f, o, h);
			}
			else
			{
				float u = t - bar.width / curTexSize;
				if (cap < 0)
				{
					setPoint(u, 0.66f, e, h);
					setPoint(u, 1.0f, e, 0);
				}
				setPoint(t, 0.66f, o, h);
				setPoint(t, 1.0f, o, 0);
				if (cap > 0)
				{
					u = t + bar.width / curTexSize;
					setPoint(u, 0.66f, e, h);
					setPoint(u, 1.0f, e, 0);
				}
			}
			break;
		}
	}

	/** The left barrier: along the outer edge of the left side. */
	private void leftBarrier()
	{
		part = Part.BARRIER;
		Barrier lastBarrier = track.segs.get(0).prev.barrier[TR_SIDE_LFT];
		curDoubleSided = false;
		for (int j = 0; j < 3; j++)
		{
			prevTexId = 0;
			float texLen = 0;
			startNeeded = true;
			float running = 0;
			String sname = "B" + j + "Lt";
			for (int i = 0; i < track.segs.size(); i++)
			{
				Seg mseg = track.segs.get(i);
				Seg seg = outermost(mseg, TR_SIDE_LFT);
				Barrier bar = mseg.barrier[TR_SIDE_LFT];
				if ((seg.raceInfo & TR_PITBUILD) != 0 || bar.style == TR_NO_BARRIER)
				{
					running = 0;
					startNeeded = true;
					xprev = 0;
					yprev = 0;
					continue;
				}
				boolean startPolygonNeeded = false;
				if (lastBarrier != null)
				{
					if (differ(lastBarrier, bar))
					{
						startNeeded = true;
					}
					else if (sd)
					{
						Seg prevSide = outermost(mseg.prev, TR_SIDE_LFT);
						startPolygonNeeded = apart(seg.vertex[TR_SL], prevSide.vertex[TR_EL]);
						if (startPolygonNeeded)
							startNeeded = true;
					}
					else
					{
						// CSR walks from the next segment, out to the right (it ends at the right side)
						Seg prevSide = mseg.next;
						while (prevSide != null && prevSide.lside != null)
							prevSide = prevSide.rside;
						startPolygonNeeded = prevSide == null || apart(seg.vertex[TR_SL], prevSide.vertex[TR_EL]);
						startNeeded = true;
					}
				}
				curDoubleSided = bar.style == TR_FENCE;
				curBackToBack = bar.style == TR_FENCE1 || bar.style == TR_FENCE2;
				checkDispList(bar.surface, sname, i, 0);
				if (!curTexLink)
					texLen = 0;
				if (bar.style == TR_FENCE && j != 0)
				{
					xprev = seg.vertex[TR_SR][0];
					yprev = seg.vertex[TR_SR][1];
					continue;
				}
				float h = bar.height, w = bar.width;
				if (startNeeded || running > LG_STEP_MAX)
				{
					newDispList(false, sname, i);
					if (curTexType == 0)
						texLen = 0;
					running = 0;
					// trackgen starts the distance at the side's right corner
					xprev = seg.vertex[TR_SR][0];
					yprev = seg.vertex[TR_SR][1];
					float[] edge = seg.vertex[TR_SL];
					float[] out = withZ(GameTrack.local2Global(seg, 0, w + GameTrack.width(seg, 0)), edge[2]);
					Barrier prevBar = mseg.prev.barrier[TR_SIDE_LFT];
					boolean cap = prevBar.style != TR_WALL || prevBar.height != h || prevBar.width != w || startPolygonNeeded;
					leftBarrierSection(bar, j, texLen, edge, out, cap ? -1 : 0);
				}
				float texStep;
				for (float ts : steps(seg, mseg))
				{
					float tr = GameTrack.width(seg, ts);
					float[] xy = GameTrack.local2Global(seg, ts, tr);
					float curHeight = track.heightL(seg, ts, tr);
					texStep = (float) Math.sqrt((xy[1] - yprev) * (xy[1] - yprev) + (xy[0] - xprev) * (xy[0] - xprev)) / curTexSize;
					texLen += texStep;
					xprev = xy[0];
					yprev = xy[1];
					float[] edge = { xy[0], xy[1], curHeight };
					float[] out = withZ(GameTrack.local2Global(seg, ts, w + tr), curHeight);
					leftBarrierSection(bar, j, texLen, edge, out, 0);
				}
				// and measures the last piece to the right corner too
				float[] er = seg.vertex[TR_ER];
				texStep = (float) Math.sqrt((er[1] - yprev) * (er[1] - yprev) + (er[0] - xprev) * (er[0] - xprev)) / curTexSize;
				texLen += texStep;
				xprev = er[0];
				yprev = er[1];
				float[] el = seg.vertex[TR_EL];
				float endTs = endToStart(seg);
				float[] out = withZ(GameTrack.local2Global(seg, endTs, w + GameTrack.width(seg, endTs)), el[2]);
				Seg nextSide = outermost(mseg.next, TR_SIDE_LFT);
				boolean endPolygonNeeded = apart(el, nextSide.vertex[TR_SL]);
				Barrier nextBar = mseg.next.barrier[TR_SIDE_LFT];
				boolean cap = nextBar.style != TR_WALL || nextBar.height != h || nextBar.width != w || endPolygonNeeded;
				leftBarrierSection(bar, j, texLen, el, out, cap ? 1 : 0);
				startNeeded = false;
				running += seg.length;
				lastBarrier = bar;
			}
		}
	}

	/** A cross-section of the left barrier at the side's edge e, with the wall's outer edge o. */
	private void leftBarrierSection(Barrier bar, int j, float texLen, float[] e, float[] o, int cap)
	{
		float h = bar.height;
		switch (bar.style)
		{
		case TR_FENCE:
			if (j == 0)
			{
				setPoint(texLen, 1, e, h);
				setPoint(texLen, 0, e, 0);
			}
			break;
		case TR_FENCE1:
			if (j == 0)
			{
				setPoint(texLen, 1, e, h);
				setPoint(texLen, 0, e, 0);
			}
			else if (j == 2)
			{
				setPoint(-texLen, 0, e, 0);
				setPoint(-texLen, 1, e, h);
			}
			break;
		case TR_FENCE2:
			if (j == 0)
			{
				setPoint(texLen, 0.5f, e, h);
				setPoint(texLen, 0, e, 0);
			}
			else if (j == 2)
			{
				setPoint(texLen, 1, e, 0);
				setPoint(texLen, 0.5f, e, h);
			}
			break;
		case TR_WALL:
			if (j == 0)
			{
				if (cap < 0)
				{
					float u = texLen - bar.width / curTexSize;
					setPoint(u, 1.0f, e, 0);
					setPoint(u, 0.66f, e, h);
				}
				setPoint(texLen, 1.0f, o, 0);
				setPoint(texLen, 0.66f, o, h);
				if (cap > 0)
				{
					float u = texLen + bar.width / curTexSize;
					setPoint(u, 1.0f, e, 0);
					setPoint(u, 0.66f, e, h);
				}
			}
			else if (j == 1)
			{
				setPoint(texLen, 0.66f, o, h);
				setPoint(texLen, 0.33f, e, h);
			}
			else
			{
				setPoint(texLen, 0.33f, e, h);
				setPoint(texLen, 0, e, 0);
			}
			break;
		}
	}

	private static float[] withZ(float[] xy, float z)
	{
		return new float[] { xy[0], xy[1], z };
	}
}
