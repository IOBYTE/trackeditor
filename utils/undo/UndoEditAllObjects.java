package utils.undo;

import java.util.Vector;
import utils.circuit.ObjShapeObject;

public class UndoEditAllObjects implements UndoInterface
{
	private Vector<ObjectMapObject> original;
	private Vector<ObjectMapObject> clone;

	public UndoEditAllObjects(Vector<ObjectMapObject> editedObjects)
	{
		original = editedObjects;
		clone = new Vector<ObjectMapObject>();
		for (int i = 0; i < original.size(); i++)
		{
			clone.add(new ObjectMapObject(original.get(i).objectMap, (ObjShapeObject) original.get(i).object.clone()));
		}
	}

	public void undo()
	{
		for (int i = 0; i < original.size(); i++)
		{
			int index = original.get(i).objectMap.getObjectIndex(original.get(i).object);
			clone.get(i).objectMap.setObjectAt(index, clone.get(i).object);
		}
	}

	public void redo()
	{
		for (int i = 0; i < clone.size(); i++)
		{			
			ObjectMapObject objectMapObject = clone.get(i);

			int index = objectMapObject.objectMap.getObjectIndex(objectMapObject.object);
			objectMapObject.objectMap.setObjectAt(index, original.get(i).object);
		}
	}
}
