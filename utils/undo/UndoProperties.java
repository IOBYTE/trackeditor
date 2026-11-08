package utils.undo;

import gui.EditorFrame;
import utils.Properties;
import utils.TrackData;
import utils.circuit.ObjectMap;

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

	public UndoProperties(EditorFrame editorFrame)
	{
		this.editorFrame = editorFrame;
		this.trackData = editorFrame.getTrackData();
		before = new Snapshot(trackData);
		imageBefore = new Image();
	}

	/** Call after the change. */
	public void captureAfter()
	{
		after = new Snapshot(trackData);
		imageAfter = new Image();
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
		snapshot.restore();

		// the files they write have to be written again
		for (ObjectMap objectMap : trackData.getObjectMaps())
			objectMap.setChanged(true);
		trackData.getReliefs().setChanged(true);

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
