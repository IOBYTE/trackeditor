package utils.undo;

import utils.circuit.ObjShapeObject;
import utils.circuit.ObjectMap;

public class UndoEditObject  implements UndoInterface
{
	private ObjectMap		objectMap;
	private ObjShapeObject 	original;
	private ObjShapeObject 	clone;
	private int 			index;

	public UndoEditObject(ObjectMap objectMap, ObjShapeObject object)
	{
		this.objectMap = objectMap;
		index = objectMap.getObjectIndex(object);
		clone = (ObjShapeObject) object.clone();
		original = object;
	}

	public void undo()
	{
		original = (ObjShapeObject) objectMap.getObjectAt(index).clone();
		objectMap.getObjectAt(index).set(clone);
	}

	public void redo()
	{
		objectMap.getObjectAt(index).set(original);
	}
}
