/*
 *   UndoSegmentChange.java
 *   Created on 29 ??? 2005
 *
 *    The UndoSegmentChange.java is part of TrackEditor-0.6.0.
 *
 *    TrackEditor-0.6.0 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.6.0 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.6.0; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package utils.undo;

import gui.EditorFrame;
import utils.SegmentVector;
import utils.circuit.Segment;

/**
 * @author Charalampos Alexopoulos
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UndoSegmentChange implements UndoInterface
{
	private SegmentVector	data;
	private utils.TrackData	trackData;
	private Segment 		original;
	private Segment 		clone;
	private int 			pos;
	private long			uid;
	// the renames in the segment dialog, in order; their references are the
	// only ones undo and redo change
	private java.util.List<Rename>	renames	= new java.util.ArrayList<Rename>();

	/** One rename: the references to the segment when it started, and its names. */
	public static class Rename
	{
		final utils.TrackData.SegmentReferences	references;
		final String							from;
		String									to;

		Rename(utils.TrackData.SegmentReferences references, String from)
		{
			this.references = references;
			this.from = from;
			this.to = from;
		}

		/** Renames the segment's references to the name. */
		public void to(String name)
		{
			to = name;
			references.rename(name);
		}
	}

	/**
	 * 
	 */
	public UndoSegmentChange(EditorFrame editorFrame, Segment segment)
	{
		trackData = editorFrame.getTrackData();
		data = editorFrame.getTrackData().getSegments();
		clone = (Segment) segment.clone();
		original = segment;
		pos = data.indexOf(original);
		uid = segment.getUid();

		if (!data.validateLinks())
		{
			System.out.println("UndoSegmentChange");
			data.dumpLinks();
		}
	}

	/* (non-Javadoc)
	 * @see utils.undo.UndoInterface#undo()
	 */
	public void undo()
	{
		pos = position();
		original = (Segment) data.get(pos).clone();
		// a rename in the dialog also renamed the pit and camera references
		if (renames.isEmpty())
			trackData.renameSegmentReferences(original.getName(), clone.getName());
		for (int i = renames.size() - 1; i >= 0; i--)
			renames.get(i).references.rename(renames.get(i).from);
		data.set(pos,clone);

		if (!data.validateLinks())
		{
			System.out.println("UndoSegmentChange.undo");
			data.dumpLinks();
		}
	}

	/* (non-Javadoc)
	 * @see utils.undo.UndoInterface#redo()
	 */
	public void redo()
	{
		pos = position();
		if (renames.isEmpty())
			trackData.renameSegmentReferences(data.get(pos).getName(), original.getName());
		for (Rename rename : renames)
			rename.references.rename(rename.to);
		data.set(pos,original);

		if (!data.validateLinks())
		{
			System.out.println("UndoSegmentChange.redo");
			data.dumpLinks();
		}
	}

	/**
	 * Starts a rename of the segment, from its name now: the pit and camera
	 * references to that name are the ones that follow it.
	 */
	public Rename startRename(String from)
	{
		Rename rename = new Rename(trackData.findSegmentReferences(from), from);
		renames.add(rename);
		return rename;
	}

	/**
	 * @return a rename that isn't part of an undo step
	 */
	public static Rename rename(utils.TrackData trackData, String from)
	{
		return new Rename(trackData.findSegmentReferences(from), from);
	}

	/**
	 * @return where the segment is now: segments inserted or removed before it
	 *         since (a split, pit generation) move it, so it's found by what it
	 *         is, and only by where it was when it isn't found
	 */
	private int position()
	{
		for (int i = 0; i < data.size(); i++)
		{
			if (data.get(i).getUid() == uid)
				return i;
		}
		return pos;
	}
}
