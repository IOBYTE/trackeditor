package utils.undo;

import java.util.Vector;

import utils.circuit.GraphicObject;

/**
 * Undoes an edit of a graphic object by copying the old values back into the
 * object itself, so other undo steps that refer to it (delete, move) still find it.
 */
public class UndoEditGraphicObject implements UndoInterface
{
	private GraphicObject	object;
	private GraphicObject	before;	// the values before the edit
	private GraphicObject	after;	// the values when it was undone

	public UndoEditGraphicObject(Vector<GraphicObject> graphicObjects, GraphicObject object)
	{
		this.object = object;
		before = (GraphicObject) object.clone();
	}

	public void undo()
	{
		after = (GraphicObject) object.clone();
		object.set(before);
	}

	public void redo()
	{
		object.set(after);
	}
}
