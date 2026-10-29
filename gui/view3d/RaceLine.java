/*
 *   RaceLine.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package gui.view3d;

import gui.view3d.GameTrack.Seg;

/**
 * The racing line trackgen works out for the raceline texture layer
 * (tools/trackgen raceline.cpp, the same in all three games): the track is cut
 * into 2 m pieces, each with a lane from 0 (the left edge) to 1 (the right),
 * which are smoothed to the widest curves that keep the given distances from
 * the edges. {@link #textureOffset} is what moves the raceline texture across
 * the road.
 */
public class RaceLine
{
	private static final double	SEG_LENGTH	= 2.0;
	private static final double	SECURITY_R	= 100.0;

	private final double		sideDistExt;
	private final double		sideDistInt;
	private double				trackWidth;
	private int					nSegments;
	// for each piece: the left and right edges, the racing line and its lane
	private double[]			lx, ly, rx, ry, tx, ty, lane;

	/** Works out the racing line, as generateRaceLine does. */
	public RaceLine(GameTrack track, double sideDistExt, double sideDistInt)
	{
		this.sideDistExt = sideDistExt;
		this.sideDistInt = sideDistInt;
		splitTrack(track);
		if (nSegments < 4)
			return;
		for (int step = 128; (step /= 2) > 0;)
		{
			for (int i = 100 * (int) Math.sqrt((double) step); --i >= 0;)
				smooth(step);
			interpolate(step);
		}
	}

	/** getTexureOffset: the racing line's lane, from the middle, this far from the start. */
	public double textureOffset(double length)
	{
		if (nSegments < 4)
			return 0.0;
		double seg = length / SEG_LENGTH;
		double rem = seg - Math.floor(seg);
		int length1 = ((int) Math.floor(seg)) % nSegments;
		if (length1 < 0)
			length1 += nSegments;
		int length2 = (length1 + 1) % nSegments;
		return (lane[length1] * (1.0 - rem) + lane[length2] * rem) - 0.5;
	}

	private void splitTrack(GameTrack track)
	{
		double length = 0;
		for (Seg seg : track.segs)
			length += seg.length;
		Seg first = track.segs.get(0);
		trackWidth = first.width;
		nSegments = (int) Math.floor(length / SEG_LENGTH);
		if (nSegments < 4)
			return;
		lx = new double[nSegments];
		ly = new double[nSegments];
		rx = new double[nSegments];
		ry = new double[nSegments];
		tx = new double[nSegments];
		ty = new double[nSegments];
		lane = new double[nSegments];

		double lastSegLen = 0.0;
		double curSegLen = 0.0;
		int id = 0;
		Seg seg = first;
		do
		{
			float[] sl = seg.vertex[GameTrack.TR_SL], sr = seg.vertex[GameTrack.TR_SR];
			if (seg.type == GameTrack.TR_STR)
			{
				double scale = seg.length;
				float[] el = seg.vertex[GameTrack.TR_EL], er = seg.vertex[GameTrack.TR_ER];
				double dxl = (el[0] - sl[0]) / scale;
				double dyl = (el[1] - sl[1]) / scale;
				double dxr = (er[0] - sr[0]) / scale;
				double dyr = (er[1] - sr[1]) / scale;
				while (curSegLen < seg.length && id < nSegments)
				{
					lx[id] = sl[0] + dxl * curSegLen;
					ly[id] = sl[1] + dyl * curSegLen;
					rx[id] = sr[0] + dxr * curSegLen;
					ry[id] = sr[1] + dyr * curSegLen;
					middle(id++);
					lastSegLen = curSegLen;
					curSegLen += SEG_LENGTH;
				}
			}
			else
			{
				double dphi = 1.0 / seg.radius;
				dphi = seg.type == GameTrack.TR_LFT ? dphi : -dphi;
				while (curSegLen < seg.length && id < nSegments)
				{
					double phi = curSegLen * dphi, cos = Math.cos(phi), sin = Math.sin(phi);
					double dx = sl[0] - seg.centerX, dy = sl[1] - seg.centerY;
					lx[id] = seg.centerX + dx * cos - dy * sin;
					ly[id] = seg.centerY + dx * sin + dy * cos;
					dx = sr[0] - seg.centerX;
					dy = sr[1] - seg.centerY;
					rx[id] = seg.centerX + dx * cos - dy * sin;
					ry[id] = seg.centerY + dx * sin + dy * cos;
					middle(id++);
					lastSegLen = curSegLen;
					curSegLen += SEG_LENGTH;
				}
			}
			curSegLen = SEG_LENGTH - (seg.length - lastSegLen);
			lastSegLen = curSegLen;
			while (curSegLen > SEG_LENGTH)
				curSegLen -= SEG_LENGTH;
			seg = seg.next;
		}
		while (seg != first);
	}

	private void middle(int i)
	{
		tx[i] = (lx[i] + rx[i]) / 2.0;
		ty[i] = (ly[i] + ry[i]) / 2.0;
		lane[i] = 0.5;
	}

	private double rInverse(int prev, double px, double py, int next)
	{
		double x1 = tx[next] - px;
		double y1 = ty[next] - py;
		double x2 = tx[prev] - px;
		double y2 = ty[prev] - py;
		double x3 = tx[next] - tx[prev];
		double y3 = ty[next] - ty[prev];
		double det = x1 * y2 - x2 * y1;
		double n1 = x1 * x1 + y1 * y1;
		double n2 = x2 * x2 + y2 * y2;
		double n3 = x3 * x3 + y3 * y3;
		double nnn = Math.sqrt(n1 * n2 * n3);
		return 2 * det / nnn;
	}

	private void updateTxTy(int i)
	{
		tx[i] = lane[i] * rx[i] + (1.0 - lane[i]) * lx[i];
		ty[i] = lane[i] * ry[i] + (1.0 - lane[i]) * ly[i];
	}

	private void adjustRadius(int prev, int i, int next, double targetRInverse, double security)
	{
		double oldLane = lane[i];
		// start by aligning points for a reasonable initial lane
		lane[i] = (-(ty[next] - ty[prev]) * (lx[i] - tx[prev]) + (tx[next] - tx[prev]) * (ly[i] - ty[prev])) /
			((ty[next] - ty[prev]) * (rx[i] - lx[i]) - (tx[next] - tx[prev]) * (ry[i] - ly[i]));
		if (lane[i] < -0.2)
			lane[i] = -0.2;
		else if (lane[i] > 1.2)
			lane[i] = 1.2;
		updateTxTy(i);

		// Newton-like resolution method
		double dLane = 0.0001;
		double dx = dLane * (rx[i] - lx[i]), dy = dLane * (ry[i] - ly[i]);
		double dRInverse = rInverse(prev, tx[i] + dx, ty[i] + dy, next);
		if (dRInverse > 0.000000001)
		{
			lane[i] += (dLane / dRInverse) * targetRInverse;
			double extLane = Math.min(0.5, (sideDistExt + security) / trackWidth);
			double intLane = Math.min(0.5, (sideDistInt + security) / trackWidth);
			if (targetRInverse >= 0.0)
			{
				if (lane[i] < intLane)
					lane[i] = intLane;
				if (1 - lane[i] < extLane)
				{
					if (1 - oldLane < extLane)
						lane[i] = Math.min(oldLane, lane[i]);
					else
						lane[i] = 1 - extLane;
				}
			}
			else
			{
				if (lane[i] < extLane)
				{
					if (oldLane < extLane)
						lane[i] = Math.max(oldLane, lane[i]);
					else
						lane[i] = extLane;
				}
				if (1 - lane[i] < intLane)
					lane[i] = 1 - intLane;
			}
		}
		updateTxTy(i);
	}

	private void smooth(int step)
	{
		// trackgen reads outside the pieces on a track this short
		if (nSegments < 2 * step)
			return;
		int prev = ((nSegments - step) / step) * step;
		int prevprev = prev - step;
		int next = step;
		int nextnext = next + step;
		for (int i = 0; i <= nSegments - step; i += step)
		{
			double ri0 = rInverse(prevprev, tx[prev], ty[prev], i);
			double ri1 = rInverse(i, tx[next], ty[next], nextnext);
			double lPrev = Math.hypot(tx[i] - tx[prev], ty[i] - ty[prev]);
			double lNext = Math.hypot(tx[i] - tx[next], ty[i] - ty[next]);
			double targetRInverse = (lNext * ri0 + lPrev * ri1) / (lNext + lPrev);
			double security = lPrev * lNext / (8 * SECURITY_R);
			adjustRadius(prev, i, next, targetRInverse, security);
			prevprev = prev;
			prev = i;
			next = nextnext;
			nextnext = next + step;
			if (nextnext > nSegments - step)
				nextnext = 0;
		}
	}

	private void stepInterpolate(int iMin, int iMax, int step)
	{
		int next = (iMax + step) % nSegments;
		if (next > nSegments - step)
			next = 0;
		int prev = (((nSegments + iMin - step) % nSegments) / step) * step;
		if (prev > nSegments - step)
			prev -= step;
		double ir0 = rInverse(prev, tx[iMin], ty[iMin], iMax % nSegments);
		double ir1 = rInverse(iMin, tx[iMax % nSegments], ty[iMax % nSegments], next);
		for (int k = iMax; --k > iMin;)
		{
			double x = (double) (k - iMin) / (double) (iMax - iMin);
			double targetRInverse = x * ir1 + (1 - x) * ir0;
			adjustRadius(iMin, k, iMax % nSegments, targetRInverse, 0.0);
		}
	}

	private void interpolate(int step)
	{
		if (step > 1 && nSegments >= 2 * step)
		{
			int i;
			for (i = step; i <= nSegments - step; i += step)
				stepInterpolate(i - step, i, step);
			stepInterpolate(i - step, nSegments, step);
		}
	}
}
