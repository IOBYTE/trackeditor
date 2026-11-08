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

	/**
	 * @param objectIndex where the object is in the map's list, when the caller
	 *        knows it: finding it means searching the list
	 */
	public ObjectMapObject(ObjectMap objectMap, ObjShapeObject object, int objectIndex)
	{
		this.objectMap = objectMap;
		this.object = object;
		this.objectIndex = objectIndex;
	}
}
