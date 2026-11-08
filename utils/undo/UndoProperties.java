package utils.undo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gui.EditorFrame;
import utils.Properties;
import utils.TrackData;
import utils.circuit.ObjShapeObject;
import utils.circuit.ObjShapeRelief;
import utils.circuit.ObjectMap;
import utils.circuit.Reliefs;

/**
 * Undoes the changes Ok in the Properties dialog made, as one step: everything
 * the track data holds except its segments (pit generation, the only tab that
 * changes segments, adds its own steps when Apply is pressed), and the
 * background image.
 *
 * Make it before the tabs apply their changes and call captureAfter() once
 * they have.
 */
public class UndoProperties implements UndoInterface
{
	private final EditorFrame	editorFrame;
	private final TrackData		trackData;
	private final Snapshot		before;
	private final Image			imageBefore;
	private Snapshot			after;
	private Image				imageAfter;
	// what the object map images and the relief file hold, before
	private final Map<Object, List<Object>>	filesBefore;
	// the object maps and reliefs whose files Ok changed: only those are written again
	private final Set<Object>	changedFiles	= Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());

	public UndoProperties(EditorFrame editorFrame)
	{
		this.editorFrame = editorFrame;
		this.trackData = editorFrame.getTrackData();
		before = new Snapshot(trackData);
		imageBefore = new Image();
		filesBefore = files();
	}

	/** Call after the change. */
	public void captureAfter()
	{
		after = new Snapshot(trackData);
		imageAfter = new Image();

		Map<Object, List<Object>> filesAfter = files();
		changedFiles.clear();
		for (Map.Entry<Object, List<Object>> entry : filesBefore.entrySet())
		{
			if (!entry.getValue().equals(filesAfter.get(entry.getKey())))
				changedFiles.add(entry.getKey());
		}
		for (Object file : filesAfter.keySet())
		{
			if (!filesBefore.containsKey(file))
				changedFiles.add(file);
		}
	}

	/**
	 * @return for each object map and the reliefs, what its file holds: the
	 *         objects' colors and pixels, the reliefs' lines
	 */
	private Map<Object, List<Object>> files()
	{
		Map<Object, List<Object>> files = new IdentityHashMap<Object, List<Object>>();

		for (ObjectMap objectMap : trackData.getObjectMaps())
		{
			List<Object> content = new ArrayList<Object>();
			content.add(objectMap.getObjectMap());
			content.add(objectMap.getImageWidth());
			content.add(objectMap.getImageHeight());
			for (ObjShapeObject object : objectMap.getObjects())
			{
				content.add(object.getRGB());
				content.add(object.getImageX());
				content.add(object.getImageY());
			}
			files.put(objectMap, content);
		}

		Reliefs reliefs = trackData.getReliefs();
		if (reliefs != null)
		{
			List<Object> content = new ArrayList<Object>();
			content.add(reliefs.getFileName());
			for (ObjShapeRelief relief : reliefs.getReliefs())
			{
				content.add(relief.getReliefName());
				content.add(relief.getReliefType());
				content.add(relief.getLineType());
				for (double[] vertex : relief.getVertices())
					content.add(java.util.Arrays.toString(vertex));
			}
			files.put(reliefs, content);
		}
		return files;
	}

	/** @return whether anything changed */
	public boolean changed()
	{
		return after == null || !before.sameAs(after) || !imageBefore.equals(imageAfter);
	}

	public void undo()
	{
		restore(before, imageBefore);
	}

	public void redo()
	{
		if (after != null)
			restore(after, imageAfter);
	}

	private void restore(Snapshot snapshot, Image image)
	{
		// whether each file still has to be written: a save since can have written it
		Map<Object, Boolean> unsaved = new IdentityHashMap<Object, Boolean>();
		for (ObjectMap objectMap : trackData.getObjectMaps())
			unsaved.put(objectMap, objectMap.getChanged());
		if (trackData.getReliefs() != null)
			unsaved.put(trackData.getReliefs(), trackData.getReliefs().getChanged());

		snapshot.restore();

		// the files Ok changed have to be written again; the others are as they were
		// (one Ok deleted, and this brings back, keeps the state the snapshot has)
		for (ObjectMap objectMap : trackData.getObjectMaps())
		{
			if (changedFiles.contains(objectMap))
				objectMap.setChanged(true);
			else if (unsaved.containsKey(objectMap))
				objectMap.setChanged(unsaved.get(objectMap));
		}
		Reliefs reliefs = trackData.getReliefs();
		if (reliefs != null)
		{
			if (changedFiles.contains(reliefs))
				reliefs.setChanged(true);
			else if (unsaved.containsKey(reliefs))
				reliefs.setChanged(unsaved.get(reliefs));
		}

		boolean imageChanged = !image.equals(new Image());
		image.restore();
		if (imageChanged && editorFrame.getCircuitView() != null)
			editorFrame.getCircuitView().setBackgroundImage(Properties.getInstance().getImage());
		Properties.getInstance().valueChanged();
	}

	/** The background image, which the Image tab sets */
	private static class Image
	{
		final String	file;
		final double	scale;
		final double	offsetX;
		final double	offsetY;

		Image()
		{
			Properties properties = Properties.getInstance();
			file = properties.getImage();
			scale = properties.getImageScale();
			offsetX = properties.getImgOffset().getX();
			offsetY = properties.getImgOffset().getY();
		}

		void restore()
		{
			Properties properties = Properties.getInstance();
			properties.setImage(file);
			properties.setImageScale(scale);
			properties.getImgOffset().setLocation(offsetX, offsetY);
		}

		@Override
		public boolean equals(Object other)
		{
			if (!(other instanceof Image))
				return false;
			Image image = (Image) other;
			return java.util.Objects.equals(file, image.file) && Double.compare(scale, image.scale) == 0
				&& Double.compare(offsetX, image.offsetX) == 0 && Double.compare(offsetY, image.offsetY) == 0;
		}

		@Override
		public int hashCode()
		{
			return java.util.Objects.hash(file, scale, offsetX, offsetY);
		}
	}
}
