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
		objectMap.addObjectAt(undo, index);
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
