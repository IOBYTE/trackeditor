/*
 *   UndoAddSegment.java
 *   Created on 29 ??? 2005
 *
 *    The UndoAddSegment.java is part of TrackEditor-0.6.0.
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
public class UndoAddSegment implements UndoInterface
{
	private SegmentVector	data;
	private Segment 		undo;
	private Segment 		redo;
	private int 			pos;

	/**
	 *
	 */
	public UndoAddSegment(EditorFrame editorFrame, Segment segment)
	{
		data = editorFrame.getTrackData().getSegments();
		undo = segment;
		redo = null;

		if (!data.validateLinks())
		{
			System.out.println("UndoAddSegment");
			data.dumpLinks();
		}
	}

	/* (non-Javadoc)
	 * @see undo.UndoInterface#undo()
	 */
	public void undo()
	{
		pos = data.indexOf(undo);
		if ("str".equals(undo.getType()))
		{
			int count = Properties.getInstance().getStraightNameCount() - 1;
			Properties.getInstance().setStraightNameCount(count);
		}
		else
		{
			int count = Properties.getInstance().getCurveNameCount() - 1;
			Properties.getInstance().setCurveNameCount(count);
		}
		data.remove(undo);
		redo = undo;
		undo = null;

		if (!data.validateLinks())
		{
			System.out.println("UndoAddSegment");
			data.dumpLinks();
		}
	}

	/* (non-Javadoc)
	 * @see undo.UndoInterface#redo()
	 */
	public void redo()
	{
		if ("str".equals(redo.getType()))
		{
			int count = Properties.getInstance().getStraightNameCount() + 1;
			Properties.getInstance().setStraightNameCount(count);
		}
		else
		{
			int count = Properties.getInstance().getCurveNameCount() + 1;
			Properties.getInstance().setCurveNameCount(count);
		}
		data.insertElementAt(redo, pos);
		undo = redo;
		redo = null;

		if (!data.validateLinks())
		{
			System.out.println("UndoAddSegment");
			data.dumpLinks();
		}
	}
}
