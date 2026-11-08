package utils.undo;

import java.util.Vector;

import utils.circuit.ObjShapeRelief;

/**
 * Undoes an edit of a relief by copying the old values back into the relief
 * itself, as UndoEditGraphicObject does: an index would point at a different
 * relief once a relief before it is deleted, or past the end of the list.
 */
public class UndoEditRelief implements UndoInterface
{
	private ObjShapeRelief	relief;
	private ObjShapeRelief	before;	// the values before the edit
	private ObjShapeRelief	after;	// the values when it was undone

	/**
	 * @param reliefs the track's reliefs; not needed, the relief is changed in place
	 * @param relief the relief about to be edited
	 */
	public UndoEditRelief(Vector<ObjShapeRelief> reliefs, ObjShapeRelief relief)
	{
		this.relief = relief;
		before = (ObjShapeRelief) relief.clone();
	}

	public void undo()
	{
		after = (ObjShapeRelief) relief.clone();
		relief.set(before);
	}

	public void redo()
	{
		relief.set(after);
	}
}
