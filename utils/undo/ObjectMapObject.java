package utils.undo;

import utils.circuit.ObjShapeObject;
import utils.circuit.ObjectMap;

public class ObjectMapObject
{
	public ObjectMap 		objectMap;
	public ObjShapeObject 	object;
	public int				objectIndex;
	
	public ObjectMapObject(ObjectMap objectMap, ObjShapeObject object)
	{
		this.objectMap = objectMap;
		this.object = object;
		this.objectIndex = objectMap.getObjectIndex(object);
	}
}
