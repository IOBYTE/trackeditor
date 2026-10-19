package utils.undo;

import utils.circuit.GraphicObject;

public class DeletedObject
{
	public GraphicObject	object;
	public int				objectIndex;
	
	public DeletedObject(GraphicObject object, int objectIndex)
	{
		this.object = object;
		this.objectIndex = objectIndex;
	}
}
