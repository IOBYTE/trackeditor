package utils.undo;

import java.util.Vector;

import utils.circuit.GraphicObject;
import utils.circuit.ObjShapeObject;
import utils.circuit.ObjectMap;

public class UndoMoveGraphicObject implements UndoInterface
{
	private Vector<GraphicObject> 	graphicObjects;
	private MovedObject				undo;
	private MovedObject				redo;
	private int						index;

	public UndoMoveGraphicObject(Vector<GraphicObject> graphicObjects, GraphicObject graphicObject, ObjectMap objectMap, ObjShapeObject object)
	{
		this.graphicObjects = graphicObjects;
		undo = new MovedObject(graphicObject, new ObjectMapObject(objectMap, object));
		redo = null;
		index = objectMap.getObjectIndex(object);
	}

	public void undo()
	{
		undo.oldObject.objectMap.addObjectAt(undo.oldObject.object, index);
		graphicObjects.remove(undo.newObject);
		redo = undo;
		undo = null;
	}

	public void redo()
	{
		index = redo.oldObject.objectMap.getObjectIndex(redo.oldObject.object);
		redo.oldObject.objectMap.removeObject(redo.oldObject.object);
		graphicObjects.add(redo.newObject);
		undo = redo;
		redo = null;
	}
}
