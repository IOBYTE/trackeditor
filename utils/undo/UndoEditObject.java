package utils.undo;

import utils.circuit.ObjShapeObject;
import utils.circuit.ObjectMap;

/**
 * Undoes an edit of an object map object by copying the old values back into
 * the object itself, as UndoEditGraphicObject does: an index would point at a
 * different object once objects before it are deleted.
 */
public class UndoEditObject implements UndoInterface
{
	private ObjectMap		objectMap;
	private ObjShapeObject	object;
	private ObjShapeObject	before;	// the values before the edit
	private ObjShapeObject	after;	// the values when it was undone

	public UndoEditObject(ObjectMap objectMap, ObjShapeObject object)
	{
		this.objectMap = objectMap;
		this.object = object;
		before = (ObjShapeObject) object.clone();
	}

	public void undo()
	{
		after = (ObjShapeObject) object.clone();
		object.set(before);
		// the object map image has to be written again
		objectMap.setChanged(true);
	}

	public void redo()
	{
		object.set(after);
		objectMap.setChanged(true);
	}
}
