/*
 *   UndoDeleteSegment.java
 *   Created on 29 ??? 2005
 *
 *    The UndoDeleteSegment.java is part of TrackEditor-0.6.0.
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
import utils.Properties;
import utils.SegmentVector;
import utils.circuit.Segment;

/**
 * @author Charalampos Alexopoulos
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UndoDeleteSegment implements UndoInterface
{
	private SegmentVector	data;
	private Segment 		undo;
	private Segment 		redo;
	private int 			pos;
	// the segment after, which the delete changed so it keeps its look (see Segment.keep)
	private Segment			next;
	private Segment			nextBefore;
	private Segment			nextAfter;

	/**
	 *
	 */
	/**
	 * @param next the segment after, which the delete changes so it keeps its look,
	 *        or null; call nextChanged once it has
	 */
	public UndoDeleteSegment(EditorFrame editorFrame, Segment segment, Segment next)
	{
		this.next = next;
		if (next != null)
			nextBefore = (Segment) next.clone();
		data = editorFrame.getTrackData().getSegments();
		undo = segment;
		redo = null;
		pos = data.indexOf(undo);

		if (!data.validateLinks())
		{
			System.out.println("UndoDeleteSegment");
			data.dumpLinks();
		}
	}

	/**
	 * Records the segment after as the delete left it, for redo.
	 */
	public void nextChanged()
	{
		if (next != null)
			nextAfter = (Segment) next.clone();
	}

	/* (non-Javadoc)
	 * @see utils.undo.UndoInterface#undo()
	 */
	public void undo()
	{
		if ("str".equals(undo.getType()))
		{
			int count = Properties.getInstance().getStraightNameCount() + 1;
			Properties.getInstance().setStraightNameCount(count);
		}
		else
		{
			int count = Properties.getInstance().getCurveNameCount() + 1;
			Properties.getInstance().setCurveNameCount(count);
		}
		data.insertElementAt(undo,pos);
		if (next != null)
			next.set(nextBefore);
		redo = undo;
		undo = null;

		if (!data.validateLinks())
		{
			System.out.println("UndoDeleteSegment");
			data.dumpLinks();
		}
	}

	/* (non-Javadoc)
	 * @see utils.undo.UndoInterface#redo()
	 */
	public void redo()
	{
		pos = data.indexOf(redo);
		if ("str".equals(redo.getType()))
		{
			int count = Properties.getInstance().getStraightNameCount() - 1;
			Properties.getInstance().setStraightNameCount(count);
		}
		else
		{
			int count = Properties.getInstance().getCurveNameCount() - 1;
			Properties.getInstance().setCurveNameCount(count);
		}
		data.remove(redo);
		if (next != null && nextAfter != null)
			next.set(nextAfter);
		undo = redo;
		redo = null;

		if (!data.validateLinks())
		{
			System.out.println("UndoDeleteSegment");
			data.dumpLinks();
		}
	}
}
