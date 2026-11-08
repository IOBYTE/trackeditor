package utils.undo;

import utils.circuit.ObjShapeRelief;
import utils.circuit.Reliefs;

/**
 * Undoes deleting a relief. Deleting the last one also drops the relief file
 * (Reliefs.dropFileIfEmpty()), so undoing it puts the file name back.
 */
public class UndoDeleteRelief implements UndoInterface
{
	private Reliefs			reliefs;
	private ObjShapeRelief	undo;
	private ObjShapeRelief	redo;
	private int				index;
	private String			fileName;	// the relief file before the delete

	/**
	 * Call before the relief is deleted.
	 */
	public UndoDeleteRelief(Reliefs reliefs, ObjShapeRelief relief)
	{
		this.reliefs = reliefs;
		index = reliefs.getReliefs().indexOf(relief);
		undo = relief;
		redo = null;
		fileName = reliefs.getFileName();
	}

	public void undo()
	{
		// reliefs deleted since (in the Properties dialog) can leave the list shorter
		reliefs.getReliefs().insertElementAt(undo, Math.min(index, reliefs.getReliefs().size()));
		String now = reliefs.getFileName();
		if ((now == null || now.isEmpty()) && fileName != null && !fileName.isEmpty())
			reliefs.setNewFileName(fileName);
		redo = undo;
		undo = null;
	}

	public void redo()
	{
		index = reliefs.getReliefs().indexOf(redo);
		reliefs.getReliefs().remove(redo);
		reliefs.dropFileIfEmpty();
		undo = redo;
		redo = null;
	}
}
