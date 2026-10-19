package utils.undo;

import utils.circuit.GraphicObject;

public class MovedObject
{
	public GraphicObject	newObject;
	public ObjectMapObject	oldObject;

	public MovedObject(GraphicObject newObject, ObjectMapObject oldObject)
	{
		this.newObject = newObject;
		this.oldObject = oldObject;
	}
}
