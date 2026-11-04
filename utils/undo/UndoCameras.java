/*
 *   UndoCameras.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package utils.undo;

import java.util.Vector;

import utils.TrackData;
import utils.circuit.Camera;

/**
 * Undo step for changes to the track's cameras: keeps all of them before the
 * change and after it, so adding, deleting and editing undo the same way.
 */
public class UndoCameras implements UndoInterface
{
	private final TrackData		trackData;
	private final Vector<Camera>	before;
	private Vector<Camera>			after;

	/** Call before the change. */
	public UndoCameras(TrackData trackData)
	{
		this.trackData = trackData;
		this.before = copy(trackData.getCameras());
	}

	/** Call after the change. */
	public void captureAfter()
	{
		after = copy(trackData.getCameras());
	}

	/** @return whether the cameras changed */
	public boolean changed()
	{
		if (after == null || after.size() != before.size())
			return true;
		for (int i = 0; i < before.size(); i++)
			if (!same(before.get(i), after.get(i)))
				return true;
		return false;
	}

	public void undo()
	{
		restore(before);
	}

	public void redo()
	{
		if (after != null)
			restore(after);
	}

	private void restore(Vector<Camera> cameras)
	{
		Vector<Camera> list = trackData.getCameras();
		list.clear();
		list.addAll(copy(cameras));
	}

	private static Vector<Camera> copy(Vector<Camera> cameras)
	{
		Vector<Camera> copies = new Vector<Camera>(cameras.size());
		for (Camera camera : cameras)
			copies.add(copy(camera));
		return copies;
	}

	public static Camera copy(Camera camera)
	{
		Camera copy = new Camera();
		copy.setName(camera.getName());
		copy.setComment(camera.getComment());
		copy.setSegment(camera.getSegment());
		copy.setToRight(camera.getToRight());
		copy.setToStart(camera.getToStart());
		if (camera.isToStartUnitsKnown())
			copy.setToStartUnits(camera.getToStartUnits());
		copy.setHeight(camera.getHeight());
		copy.setFovStart(camera.getFovStart());
		copy.setFovEnd(camera.getFovEnd());
		return copy;
	}

	private static boolean same(Camera a, Camera b)
	{
		return java.util.Objects.equals(a.getName(), b.getName()) && java.util.Objects.equals(a.getComment(), b.getComment())
			&& java.util.Objects.equals(a.getSegment(), b.getSegment()) && same(a.getToRight(), b.getToRight())
			&& same(a.getToStart(), b.getToStart()) && a.isToStartUnitsKnown() == b.isToStartUnitsKnown()
			&& java.util.Objects.equals(a.getToStartUnits(), b.getToStartUnits()) && same(a.getHeight(), b.getHeight())
			&& java.util.Objects.equals(a.getFovStart(), b.getFovStart()) && java.util.Objects.equals(a.getFovEnd(), b.getFovEnd());
	}

	private static boolean same(double a, double b)
	{
		return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
	}
}
