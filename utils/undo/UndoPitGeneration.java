package utils.undo;

import java.util.Vector;

import gui.EditorFrame;
import utils.Properties;
import utils.SegmentVector;
import utils.circuit.Pits;
import utils.circuit.Segment;

/**
 * Undo step for pit generation. Captures the entire segment list and pit
 * configuration before and after generation, since the operation can split
 * segments (changing the count) and modify properties on an unbounded
 * number of segments.
 */
public class UndoPitGeneration implements UndoInterface
{
	private final EditorFrame editorFrame;

	// state before generation
	private final Vector<Segment> segmentsBefore;
	private final Pits pitsBefore;
	private final int straightCountBefore;
	private final int curveCountBefore;

	// state after generation (captured by captureAfter)
	private Vector<Segment> segmentsAfter;
	private Pits pitsAfter;
	private int straightCountAfter;
	private int curveCountAfter;

	/**
	 * Call before generation to snapshot the current state.
	 */
	public UndoPitGeneration(EditorFrame editorFrame)
	{
		this.editorFrame = editorFrame;
		this.segmentsBefore = cloneSegments(editorFrame.getTrackData().getSegments());
		this.pitsBefore = clonePits(editorFrame.getPits());
		this.straightCountBefore = Properties.getInstance().getStraightNameCount();
		this.curveCountBefore = Properties.getInstance().getCurveNameCount();
	}

	/**
	 * Call after generation to snapshot the new state.
	 */
	public void captureAfter()
	{
		this.segmentsAfter = cloneSegments(editorFrame.getTrackData().getSegments());
		this.pitsAfter = clonePits(editorFrame.getPits());
		this.straightCountAfter = Properties.getInstance().getStraightNameCount();
		this.curveCountAfter = Properties.getInstance().getCurveNameCount();
	}

	public void undo()
	{
		restore(segmentsBefore, pitsBefore, straightCountBefore, curveCountBefore);
	}

	public void redo()
	{
		restore(segmentsAfter, pitsAfter, straightCountAfter, curveCountAfter);
	}

	private void restore(Vector<Segment> segments, Pits pits, int straightCount, int curveCount)
	{
		editorFrame.getTrackData().getSegments().replaceAll(cloneSegments(segments));
		editorFrame.getTrackData().getMainTrack().setPits(clonePits(pits));
		Properties.getInstance().setStraightNameCount(straightCount);
		Properties.getInstance().setCurveNameCount(curveCount);
		editorFrame.getCircuitView().redrawCircuit();
		editorFrame.getCircuitView().invalidate();
		editorFrame.getCircuitView().repaint();
	}

	private static Vector<Segment> cloneSegments(SegmentVector data)
	{
		Vector<Segment> clones = new Vector<Segment>(data.size());
		for (int i = 0; i < data.size(); i++)
		{
			clones.add((Segment) data.get(i).clone());
		}
		return clones;
	}

	private static Vector<Segment> cloneSegments(Vector<Segment> data)
	{
		Vector<Segment> clones = new Vector<Segment>(data.size());
		for (int i = 0; i < data.size(); i++)
		{
			clones.add((Segment) data.get(i).clone());
		}
		return clones;
	}

	private static Pits clonePits(Pits original)
	{
		Pits copy = new Pits();
		copy.setSide(original.getSide());
		copy.setEntry(original.getEntry());
		copy.setStart(original.getStart());
		copy.setStartBuildings(original.getStartBuildings());
		copy.setStopBuildings(original.getStopBuildings());
		copy.setMaxPits(original.getMaxPits());
		copy.setEnd(original.getEnd());
		copy.setExit(original.getExit());
		copy.setWidth(original.getWidth());
		copy.setLength(original.getLength());
		copy.setStyle(original.getStyle());
		copy.setIndicator(original.getIndicator());
		copy.setSpeedLimitValue(original.getSpeedLimitValue());
		copy.setSpeedLimitUnits(original.getSpeedLimitUnits());
		copy.copyGeneratorFrom(original);
		return copy;
	}
}
