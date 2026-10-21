package utils.undo;

import java.util.Vector;
import utils.circuit.ObjShapeObject;

/**
 * Undoes an edit of several object map objects by copying the old values back
 * into the objects themselves, so other undo steps that refer to them (add,
 * delete, move) still find them.
 */
public class UndoEditAllObjects implements UndoInterface
{
	private Vector<ObjectMapObject>	objects;
	private Vector<ObjShapeObject>	before;	// the values before the edit
	private Vector<ObjShapeObject>	after;	// the values when it was undone

	public UndoEditAllObjects(Vector<ObjectMapObject> editedObjects)
	{
		objects = editedObjects;
		before = copies();
	}

	private Vector<ObjShapeObject> copies()
	{
		Vector<ObjShapeObject> copies = new Vector<ObjShapeObject>();
		for (int i = 0; i < objects.size(); i++)
		{
			copies.add((ObjShapeObject) objects.get(i).object.clone());
		}
		return copies;
	}

	private void restore(Vector<ObjShapeObject> values)
	{
		for (int i = 0; i < objects.size(); i++)
		{
			ObjectMapObject objectMapObject = objects.get(i);
			objectMapObject.object.set(values.get(i));
			objectMapObject.objectMap.addColor(objectMapObject.object.getRGB());
			// the object map image has to be written again
			objectMapObject.objectMap.setChanged(true);
		}
	}

	public void undo()
	{
		after = copies();
		restore(before);
	}

	public void redo()
	{
		restore(after);
	}
}
