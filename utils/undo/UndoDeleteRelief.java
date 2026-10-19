package utils.undo;

import java.util.Vector;

import utils.circuit.ObjShapeRelief;

public class UndoDeleteRelief implements UndoInterface
{
	private Vector<ObjShapeRelief>	reliefs;
	private ObjShapeRelief			undo;
	private ObjShapeRelief			redo;
	private int						index;

	public UndoDeleteRelief(Vector<ObjShapeRelief> reliefs, ObjShapeRelief relief)
	{
		this.reliefs = reliefs;
		index = reliefs.indexOf(relief);
		undo = relief;
		redo = null;
	}

	public void undo()
	{
		reliefs.insertElementAt(undo, index);
		redo = undo;
		undo = null;
	}

	public void redo()
	{
		index = reliefs.indexOf(redo);
		reliefs.remove(redo);
		undo = redo;
		redo = null;
	}
}

