package utils.undo;

import java.util.Vector;

public class UndoDeleteAllObjects implements UndoInterface
{
	private Vector<ObjectMapObject> undo;
	private Vector<ObjectMapObject> redo;
	
	public UndoDeleteAllObjects(Vector<ObjectMapObject> deletedObjects)
	{
		this.undo = deletedObjects;
	}

	public void undo()
	{
		for (int i = undo.size() - 1; i >= 0 ; i--)
		{
			ObjectMapObject objectMapObject = undo.get(i);
			
			objectMapObject.objectMap.addObjectAt(objectMapObject.object, objectMapObject.objectIndex);
		}
		redo = undo;
		undo = null;
	}

	public void redo()
	{
		for (int i = 0; i < redo.size(); i++)
		{			
			ObjectMapObject objectMapObject = redo.get(i);
			
			objectMapObject.objectMap.removeObject(objectMapObject.object);
		}
		undo = redo;
		redo = null;
	}
}
