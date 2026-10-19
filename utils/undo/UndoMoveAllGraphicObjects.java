package utils.undo;

import java.util.Vector;

import utils.circuit.GraphicObject;

public class UndoMoveAllGraphicObjects implements UndoInterface
{
	private Vector<GraphicObject>	graphicObjects;
	private Vector<MovedObject>		undo;
	private Vector<MovedObject>		redo;

	public UndoMoveAllGraphicObjects(Vector<GraphicObject> graphicObjects, Vector<MovedObject>	movedObjects)
	{
		this.graphicObjects = graphicObjects;
		this.undo = movedObjects;
	}

	public void undo()
	{
		for (int i = 0; i < undo.size(); i++)
		{
			undo.get(i).oldObject.objectMap.addObjectAt(undo.get(i).oldObject.object, undo.get(i).oldObject.objectIndex);
			graphicObjects.remove(undo.get(i).newObject);
		}
		redo = undo;
	}

	public void redo()
	{
		for (int i = 0; i < undo.size(); i++)
		{
			redo.get(i).oldObject.objectMap.removeObject(redo.get(i).oldObject.object);
			graphicObjects.add(redo.get(i).newObject);
		}
		undo = redo;
	}
}
