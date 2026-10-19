package utils.undo;

import utils.circuit.ObjShapeObject;
import utils.circuit.ObjectMap;

public class UndoAddObject implements UndoInterface
{
	private ObjectMap		objectMap;
	private ObjShapeObject	undo;
	private ObjShapeObject	redo;
	private int				index;

	public UndoAddObject(ObjectMap objectMap, ObjShapeObject object)
	{
		this.objectMap = objectMap;
		undo = object;
		redo = null;
	}
	
	public void undo()
	{
		index = objectMap.getObjectIndex(undo);
		objectMap.removeObject(undo);
		redo = undo;
		undo = null;
	}
	
	public void redo()
	{
		objectMap.addObjectAt(redo, index);
		undo = redo;
		redo = null;
	}
}
