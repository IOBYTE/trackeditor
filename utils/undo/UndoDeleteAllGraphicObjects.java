package utils.undo;

import java.util.Vector;

import utils.circuit.GraphicObject;

public class UndoDeleteAllGraphicObjects implements UndoInterface
{
	Vector<GraphicObject>	graphicObjects;
	Vector<DeletedObject>	undo;
	Vector<DeletedObject>	redo;
	
	public UndoDeleteAllGraphicObjects(Vector<GraphicObject> graphicObjects, Vector<DeletedObject> deletedObjects)
	{
		this.graphicObjects = graphicObjects;
		this.undo = deletedObjects;
	}

	public void undo()
	{
		for (int i = undo.size() - 1; i >= 0 ; i--)
		{
			graphicObjects.add(undo.get(i).objectIndex, undo.get(i).object);
		}
		redo = undo;
		undo = null;
	}
	
	public void redo()
	{
		for (int i = 0; i < redo.size(); i++)
		{			
			graphicObjects.remove(redo.get(i).object);
		}
		undo = redo;
		redo = null;		
	}
}
