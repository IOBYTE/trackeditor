/*
 *   SceneTorcs.java
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
 * The scene as TORCS' trackgen makes it (tools/trackgen trackscene*.cpp): the
 * main track, the right border and side, the left border and side, then the
 * right and left barriers. Right-hand textures run the other way (u is 1 less
 * the distance); fences are two one-sided strips; the pit lane has no barrier.
 */
public class SceneTorcs extends TrackScene
{
	SceneTorcs(GameTrack track, EditorFrame editorFrame)
	{
		super(track, editorFrame);
	}

	@Override
	protected void generate()
	{
		mainTrack();
		borders(TR_SIDE_RGT);
		side(TR_SIDE_RGT);
		borders(TR_SIDE_LFT);
		side(TR_SIDE_LFT);
		barriers(TR_SIDE_RGT);
		barriers(TR_SIDE_LFT);
	}

	private static float texS(int side, float texLen)
	{
		return side == TR_SIDE_RGT ? 1.0f - texLen : texLen;
	}

	/** emitWallCrossSection: layer 0 is the face at outer, 1 the top, 2 the face at inner. */
	private void wallSection(int side, int layer, float texLen, float[] outer, float[] inner, float height)
	{
		float[] rightT = { 0.0f, 0.33f, 0.66f, 1.0f };
		float[] leftT = { 1.0f, 0.66f, 0.33f, 0.0f };
		boolean right = side == TR_SIDE_RGT;
		float s = texS(side, texLen);
		float first = right ? rightT[layer] : leftT[layer];
		float second = right ? rightT[layer + 1] : leftT[layer + 1];
		if (layer == 0)
		{
			setPoint(s, first, outer, 0);
			setPoint(s, second, outer, height);
		}
		else if (layer == 1)
		{
			setPoint(s, first, outer, height);
			setPoint(s, second, inner, height);
		}
		else
		{
			setPoint(s, first, inner, height);
			setPoint(s, second, inner, 0);
		}
	}

	/** emitWallCap */
	private void wallCap(int side, float s, float[] vertex, float height)
	{
		boolean right = side == TR_SIDE_RGT;
		setPoint(s, right ? 0.66f : 1.0f, vertex, right ? height : 0);
		setPoint(s, right ? 1.0f : 0.66f, vertex, right ? 0 : height);
	}

	/** emitBorderCrossSection: outer is TR_SL / TR_EL (toRight the width), inner TR_SR / TR_ER. */
	private void borderSection(Seg seg, int side, int layer, float texLen, float texMaxT, float[] outer, float[] inner)
	{
		boolean right = side == TR_SIDE_RGT;
		float s = texS(side, texLen);
		float height = seg.height;
		switch (seg.style)
		{
		case TR_PLAN:
			if (layer == 0)
			{
				setPoint(s, right ? 0 : texMaxT, outer, 0);
				setPoint(s, right ? texMaxT : 0, inner, 0);
			}
			break;
		case TR_CURB:
		{
			int slopeLayer = right ? 0 : 1;
			int verticalLayer = 1 - slopeLayer;
			if (layer == slopeLayer)
			{
				setPoint(s, right ? 0 : texMaxT, outer, right ? 0 : height);
				setPoint(s, right ? texMaxT : 0, inner, right ? height : 0);
			}
			else if (layer == verticalLayer && height > 0.0f)
			{
				float[] at = right ? inner : outer;
				setPoint(s, texMaxT, at, right ? height : 0);
				setPoint(s, texMaxT, at, right ? 0 : height);
			}
			break;
		}
		case TR_WALL:
			wallSection(side, layer, texLen, outer, inner, height);
			break;
		}
	}

	private void borders(int side)
	{
		part = Part.BORDER;
		boolean right = side == TR_SIDE_RGT;
		int borderType = right ? TR_RBORDER : TR_LBORDER;
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
					continue;
				}
				checkDispList(seg.surface, sname, i, mseg.lgfromstart);
				float curTexSeg = (curTexLink ? mseg.lgfromstart : 0) + curTexOffset;
				float texLen = curTexSeg / curTexSize;
				int slopeLayer = right ? 0 : 1;
				int innerWallLayer = right ? 2 : 0;
				if (startNeeded || running > LG_STEP_MAX)
				{
					newDispList(false, sname, i);
					running = 0;
					float texMaxT = texMaxT(GameTrack.width(seg, 0));
					Seg prev = mseg.prev.side(side);
					boolean startsBorder = prev == null || prev.type2 != borderType || prev.style != seg.style;
					float s = texS(side, texLen);
					// the start caps come first, to keep the strip's winding
					if (startsBorder && seg.height > 0.0f && seg.style == TR_CURB && j == slopeLayer)
					{
						setPoint(s, right ? 0 : texMaxT, seg.vertex[TR_SL], right ? -0.1f : 0);
						setPoint(s, right ? texMaxT : 0, seg.vertex[TR_SR], right ? 0 : -0.1f);
					}
					if (startsBorder && seg.style == TR_WALL && j == innerWallLayer)
						wallCap(side, texS(side, texLen - seg.width / curTexSize), seg.vertex[right ? TR_SL : TR_SR], seg.height);
					borderSection(seg, side, j, texLen, texMaxT, seg.vertex[TR_SL], seg.vertex[TR_SR]);
				}
				float texStep = texStep(seg, mseg);
				texLen += texStep;
				for (float ts : steps(seg, mseg))
				{
					float width = GameTrack.width(seg, ts);
					borderSection(seg, side, j, texLen, texMaxT(width), point(seg, ts, width), point(seg, ts, 0));
					texLen += texStep;
				}
				texLen = (curTexSeg + mseg.length) / curTexSize;
				float texMaxT = texMaxT(GameTrack.width(seg, endToStart(seg)));
				borderSection(seg, side, j, texLen, texMaxT, seg.vertex[TR_EL], seg.vertex[TR_ER]);
				Seg next = mseg.next.side(side);
				// a missing next border has no end cap
				boolean endsBorder = next != null && (next.type2 != borderType || next.style != seg.style);
				float s = texS(side, texLen);
				if (endsBorder && seg.height > 0.0f && seg.style == TR_CURB && j == slopeLayer)
				{
					setPoint(s, right ? 0 : texMaxT, seg.vertex[TR_EL], right ? -0.1f : 0);
					setPoint(s, right ? texMaxT : 0, seg.vertex[TR_ER], right ? 0 : -0.1f);
				}
				if (endsBorder && seg.style == TR_WALL && j == innerWallLayer)
					wallCap(side, texS(side, texLen + seg.width / curTexSize), seg.vertex[right ? TR_EL : TR_ER], seg.height);
				startNeeded = false;
				running += seg.length;
			}
		}
	}

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
				Seg prevBorder = mseg.prev.side(side);
				if (!hasBorder)
				{
					startNeeded = true;
					hasBorder = true;
				}
				else if (prevBorder == null || first.width != prevBorder.width)
				{
					// a new border width needs a new start too
					startNeeded = true;
				}
			}
			else if (hasBorder)
			{
				startNeeded = true;
				hasBorder = false;
			}
			checkDispList(seg.surface, name, i, mseg.lgfromstart);
			float curTexSeg = (curTexLink ? mseg.lgfromstart : 0) + curTexOffset;
			float texLen = curTexSeg / curTexSize;
			if (startNeeded || running > LG_STEP_MAX)
			{
				newDispList(false, name, i);
				running = 0;
				float texMaxT = texMaxT(GameTrack.width(seg, 0));
				sideSection(side, texLen, texMaxT, seg.vertex[TR_SL], seg.vertex[TR_SR]);
			}
			float texStep = texStep(seg, mseg);
			texLen += texStep;
			for (float ts : steps(seg, mseg))
			{
				float width = GameTrack.width(seg, ts);
				sideSection(side, texLen, texMaxT(width), point(seg, ts, width), point(seg, ts, 0));
				texLen += texStep;
			}
			texLen = (curTexSeg + mseg.length) / curTexSize;
			float texMaxT = texMaxT(GameTrack.width(seg, endToStart(seg)));
			sideSection(side, texLen, texMaxT, seg.vertex[TR_EL], seg.vertex[TR_ER]);
			startNeeded = false;
			running += seg.length;
		}
	}

	private void sideSection(int side, float texLen, float texMaxT, float[] outer, float[] inner)
	{
		boolean right = side == TR_SIDE_RGT;
		float s = texS(side, texLen);
		setPoint(s, right ? 0 : texMaxT, outer, 0);
		setPoint(s, right ? texMaxT : 0, inner, 0);
	}

	/** emitBarrierCrossSection: at the side's edge, with a wall's other edge at offset. */
	private void barrierSection(Barrier barrier, int side, int layer, float texLen, float[] edge, float[] offset)
	{
		boolean right = side == TR_SIDE_RGT;
		if (barrier.style == TR_FENCE)
		{
			if (layer < 2)
			{
				float s = texS(side, texLen);
				boolean firstIsTop = right ? layer == 1 : layer == 0;
				setPoint(s, firstIsTop ? 1.0f : 0.0f, edge, firstIsTop ? barrier.height : 0);
				setPoint(s, firstIsTop ? 0.0f : 1.0f, edge, firstIsTop ? 0 : barrier.height);
			}
			return;
		}
		float[] outer = right ? edge : offset;
		float[] inner = right ? offset : edge;
		wallSection(side, layer, texLen, outer, inner, barrier.height);
	}

	private void barriers(int side)
	{
		part = Part.BARRIER;
		boolean right = side == TR_SIDE_RGT;
		for (int j = 0; j < 3; j++)
		{
			prevTexId = 0;
			startNeeded = true;
			float running = 0;
			String sname = "B" + j + (right ? "Rt" : "Lt");
			for (int i = 0; i < track.segs.size(); i++)
			{
				Seg mseg = track.segs.get(i);
				Seg first = mseg.side(side);
				if (first != null && (first.raceInfo & TR_PIT) != 0)
				{
					running = 0;
					newDispList(true, sname, i);
					continue;
				}
				Barrier barrier = mseg.barrier[side];
				checkDispList(barrier.surface, sname, i, mseg.lgfromstart);
				curBackToBack = barrier.style == TR_FENCE;
				float curTexSeg = (curTexLink ? mseg.lgfromstart : 0) + curTexOffset;
				float texLen = curTexSeg / curTexSize;
				Seg seg = first == null ? mseg : first.side(side) != null ? first.side(side) : first;
				float w = barrier.width, h = barrier.height;
				int capLayer = right ? 2 : 0;
				if (startNeeded || running > LG_STEP_MAX)
				{
					newDispList(false, sname, i);
					running = 0;
					if (curTexType == 0)
						texLen = 0;
					float[] edge = seg.vertex[right ? TR_SR : TR_SL];
					float[] offset = withZ(GameTrack.local2Global(seg, 0, right ? -w : GameTrack.width(seg, 0) + w), edge[2]);
					Barrier prev = mseg.prev.barrier[side];
					boolean startsWall = prev.style != TR_WALL || prev.height != h;
					if (barrier.style == TR_WALL && startsWall && j == capLayer)
						wallCap(side, texS(side, texLen - w / curTexSize), edge, h);
					barrierSection(barrier, side, j, texLen, edge, offset);
				}
				float texStep = texStep(seg, mseg);
				texLen += texStep;
				for (float ts : steps(seg, mseg))
				{
					float toRight = right ? 0 : GameTrack.width(seg, ts);
					float[] edge = point(seg, ts, toRight);
					float[] offset = withZ(GameTrack.local2Global(seg, ts, toRight + (right ? -w : w)), edge[2]);
					barrierSection(barrier, side, j, texLen, edge, offset);
					texLen += texStep;
				}
				texLen = (curTexSeg + mseg.length) / curTexSize;
				float endTs = endToStart(seg);
				float[] edge = seg.vertex[right ? TR_ER : TR_EL];
				float[] offset = withZ(GameTrack.local2Global(seg, endTs, right ? -w : GameTrack.width(seg, endTs) + w), edge[2]);
				barrierSection(barrier, side, j, texLen, edge, offset);
				Barrier next = mseg.next.barrier[side];
				boolean endsWall = next.style != TR_WALL || next.height != h;
				if (barrier.style == TR_WALL && endsWall && j == capLayer)
					wallCap(side, texS(side, texLen + w / curTexSize), edge, h);
				startNeeded = false;
				running += seg.length;
			}
		}
	}

	private static float[] withZ(float[] xy, float z)
	{
		return new float[] { xy[0], xy[1], z };
	}
}
