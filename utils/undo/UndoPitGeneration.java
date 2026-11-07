package utils.undo;

import java.util.Vector;

import gui.EditorFrame;
import utils.Properties;
import utils.SegmentVector;
import utils.circuit.Camera;
import utils.circuit.Pits;
import utils.circuit.Segment;

/**
 * Undo step for pit generation. Captures the entire segment list and pit
 * configuration before and after generation, since the operation can split
 * segments (changing the count) and modify properties on an unbounded
 * number of segments, and the cameras, which follow the segments it renames.
 * The segments themselves are kept too, so undo and redo put back the same
 * segment objects with their values: the other undo steps and the segment
 * dialog refer to them.
 */
public class UndoPitGeneration implements UndoInterface
{
	private final EditorFrame editorFrame;

	// state before generation: the segments and their values
	private final Vector<Segment> instancesBefore;
	private final Vector<Segment> segmentsBefore;
	private final Pits pitsBefore;
	private final Vector<Camera> camerasBefore;
	private final int straightCountBefore;
	private final int curveCountBefore;

	// state after generation (captured by captureAfter)
	private Vector<Segment> instancesAfter;
	private Vector<Segment> segmentsAfter;
	private Pits pitsAfter;
	private Vector<Camera> camerasAfter;
	private int straightCountAfter;
	private int curveCountAfter;

	/**
	 * Call before generation to snapshot the current state.
	 */
	public UndoPitGeneration(EditorFrame editorFrame)
	{
		this.editorFrame = editorFrame;
		this.instancesBefore = new Vector<Segment>(editorFrame.getTrackData().getSegments());
		this.segmentsBefore = cloneSegments(editorFrame.getTrackData().getSegments());
		this.pitsBefore = clonePits(editorFrame.getPits());
		this.camerasBefore = cloneCameras(editorFrame.getTrackData().getCameras());
		this.straightCountBefore = Properties.getInstance().getStraightNameCount();
		this.curveCountBefore = Properties.getInstance().getCurveNameCount();
	}

	/**
	 * Call after generation to snapshot the new state.
	 */
	public void captureAfter()
	{
		this.instancesAfter = new Vector<Segment>(editorFrame.getTrackData().getSegments());
		this.segmentsAfter = cloneSegments(editorFrame.getTrackData().getSegments());
		this.pitsAfter = clonePits(editorFrame.getPits());
		this.camerasAfter = cloneCameras(editorFrame.getTrackData().getCameras());
		this.straightCountAfter = Properties.getInstance().getStraightNameCount();
		this.curveCountAfter = Properties.getInstance().getCurveNameCount();
	}

	public void undo()
	{
		restore(instancesBefore, segmentsBefore, pitsBefore, camerasBefore, straightCountBefore, curveCountBefore);
	}

	public void redo()
	{
		restore(instancesAfter, segmentsAfter, pitsAfter, camerasAfter, straightCountAfter, curveCountAfter);
	}

	private void restore(Vector<Segment> instances, Vector<Segment> segments, Pits pits, Vector<Camera> cameras, int straightCount, int curveCount)
	{
		editorFrame.getTrackData().getSegments().replaceAll(restoreSegments(instances, segments));
		editorFrame.getTrackData().getMainTrack().setPits(clonePits(pits));
		Vector<Camera> list = editorFrame.getTrackData().getCameras();
		list.clear();
		list.addAll(cloneCameras(cameras));
		Properties.getInstance().setStraightNameCount(straightCount);
		Properties.getInstance().setCurveNameCount(curveCount);
		editorFrame.getCircuitView().redrawCircuit();
		editorFrame.getCircuitView().invalidate();
		editorFrame.getCircuitView().repaint();
	}

	/**
	 * @return the segments with their values put back, so a segment is still the same
	 *         object (and a later undo step or the segment dialog still finds it)
	 */
	public static Vector<Segment> restoreSegments(Vector<Segment> instances, Vector<Segment> values)
	{
		Vector<Segment> segments = new Vector<Segment>(instances.size());
		for (int i = 0; i < instances.size(); i++)
		{
			Segment segment = instances.get(i);
			segment.set(values.get(i));
			segments.add(segment);
		}
		return segments;
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

	private static Vector<Camera> cloneCameras(Vector<Camera> cameras)
	{
		Vector<Camera> clones = new Vector<Camera>(cameras.size());
		for (Camera camera : cameras)
		{
			clones.add(UndoCameras.copy(camera));
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
