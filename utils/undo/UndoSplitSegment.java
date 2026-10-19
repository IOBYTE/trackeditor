package utils.undo;

import gui.EditorFrame;
import utils.Editor;
import utils.SegmentVector;
import utils.circuit.Segment;

public class UndoSplitSegment implements UndoInterface
{
	private EditorFrame	editorFrame		= null;

	private Segment		original;	// cloned
	private Segment		changed;
	private int			changedPos;

	private Segment		added;		// undo
	private Segment		redo;
	private int			addedPos;

	/**
	 * 
	 */
	public UndoSplitSegment(EditorFrame editorFrame, Segment changed, Segment original, Segment added)
	{
		SegmentVector data = editorFrame.getTrackData().getSegments();

		this.editorFrame = editorFrame;
		this.original = original;
		this.changed = changed;
		this.added = added;
		this.changedPos = data.indexOf(changed);
		this.addedPos = data.indexOf(added);

		if (!data.validateLinks())
		{
			System.out.println("UndoSplitSegment");
			data.dumpLinks();
		}
	}

	/* (non-Javadoc)
	 * @see undo.UndoInterface#undo()
	 */
	public void undo()
	{
		SegmentVector data = editorFrame.getTrackData().getSegments();

		if (added.getType() == "str")
		{
			int count = Editor.getProperties().getStraightNameCount() - 1;
			Editor.getProperties().setStraightNameCount(count);
		}
		else
		{
			int count = Editor.getProperties().getCurveNameCount() - 1;
			Editor.getProperties().setCurveNameCount(count);			
		}
		data.remove(addedPos);
		redo = added;
		added = null;

		data.set(changedPos, original);

		if (!data.validateLinks())
		{
			System.out.println("UndoSplitSegment.undo");
			data.dumpLinks();
		}
	}

	/* (non-Javadoc)
	 * @see undo.UndoInterface#redo()
	 */
	public void redo()
	{
		SegmentVector data = editorFrame.getTrackData().getSegments();

		data.set(changedPos, changed);

		if (redo.getType() == "str")
		{
			int count = Editor.getProperties().getStraightNameCount() + 1;
			Editor.getProperties().setStraightNameCount(count);
		}
		else
		{
			int count = Editor.getProperties().getCurveNameCount() + 1;
			Editor.getProperties().setCurveNameCount(count);			
		}		
		data.insertElementAt(redo, addedPos);
		added = redo;
		redo = null;

		if (!data.validateLinks())
		{
			System.out.println("UndoSplitSegment.redo");
			data.dumpLinks();
		}
	}
}
