package utils.undo;

import java.util.Vector;

import utils.circuit.GraphicObject;

public class UndoDeleteGraphicObject implements UndoInterface
{
	private Vector<GraphicObject>	graphicObjects;
	private GraphicObject			undo;
	private GraphicObject			redo;
	private int						index;

	public UndoDeleteGraphicObject(Vector<GraphicObject> graphicObjects, GraphicObject object)
	{
		this.graphicObjects = graphicObjects;
		undo = object;
		redo = null;
		index = graphicObjects.indexOf(undo);
	}

	public void undo()
	{
		// objects deleted since (in the Properties dialog) can leave the list shorter
		graphicObjects.add(Math.min(index, graphicObjects.size()), undo);
		redo = undo;
		undo = null;
	}

	public void redo()
	{
		index = graphicObjects.indexOf(redo);
		graphicObjects.remove(redo);
		undo = redo;
		redo = null;
	}
}
