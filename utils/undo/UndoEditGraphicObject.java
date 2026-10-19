package utils.undo;

import java.util.Vector;

import utils.circuit.GraphicObject;

public class UndoEditGraphicObject implements UndoInterface
{
	private Vector<GraphicObject>	graphicObjects;
	private GraphicObject 			original;
	private GraphicObject 			clone;
	private int 					index;

	public UndoEditGraphicObject(Vector<GraphicObject> graphicObjects, GraphicObject object)
	{
		this.graphicObjects = graphicObjects;
		index = graphicObjects.indexOf(object);
		clone = (GraphicObject) object.clone();
		original = object;
	}

	public void undo()
	{
		original = (GraphicObject) graphicObjects.get(index).clone();
		graphicObjects.setElementAt(clone, index);
	}

	public void redo()
	{
		graphicObjects.setElementAt(original, index);
	}
}
