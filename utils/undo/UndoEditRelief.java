package utils.undo;

import java.util.Vector;

import utils.circuit.ObjShapeRelief;

public class UndoEditRelief implements UndoInterface
{
	private Vector<ObjShapeRelief>	reliefs;
	private ObjShapeRelief 			original;
	private ObjShapeRelief 			clone;
	private int 					index;

	public UndoEditRelief(Vector<ObjShapeRelief> reliefs, ObjShapeRelief relief)
	{
		this.reliefs = reliefs;
		index = reliefs.indexOf(relief);
		clone = (ObjShapeRelief) relief.clone();
		original = relief;
	}

	public void undo()
	{
		original = (ObjShapeRelief) reliefs.get(index).clone();
		reliefs.get(index).set(clone);
	}

	public void redo()
	{
		reliefs.get(index).set(original);
	}
}
