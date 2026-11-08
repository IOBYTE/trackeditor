package utils.undo;

import utils.circuit.ObjShapeObject;
import utils.circuit.ObjectMap;

public class UndoDeleteObject implements UndoInterface
{
	private ObjectMap		objectMap;
	private ObjShapeObject	undo;
	private ObjShapeObject	redo;
	private int				index;

	public UndoDeleteObject(ObjectMap objectMap, ObjShapeObject object)
	{
		this.objectMap = objectMap;
		undo = object;
		redo = null;
		index = objectMap.getObjectIndex(undo);
	}

	public void undo()
	{
		// objects deleted since (in the Properties dialog) can leave the list shorter
		objectMap.addObjectAt(undo, Math.min(index, objectMap.getObjects().size()));
		redo = undo;
		undo = null;
	}

	public void redo()
	{
		index = objectMap.getObjectIndex(redo);
		objectMap.removeObject(redo);
		undo = redo;
		redo = null;
	}
}
