package utils.undo;

import java.util.Vector;

import utils.circuit.GraphicObject;

public class UndoAddGraphicObject implements UndoInterface
{
	private Vector<GraphicObject>	graphicObjects;
	private GraphicObject			undo;
	private GraphicObject			redo;
	private int						index;

	public UndoAddGraphicObject(Vector<GraphicObject> graphicObjects, GraphicObject object)
	{
		this.graphicObjects = graphicObjects;
		undo = object;
		redo = null;
	}

	public void undo()
	{
		index = graphicObjects.indexOf(undo);
		graphicObjects.remove(undo);
		redo = undo;
		undo = null;
	}

	public void redo()
	{
		graphicObjects.insertElementAt(redo, index);
		undo = redo;
		redo = null;
	}
}
