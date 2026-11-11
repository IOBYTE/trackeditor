/*
 *   TrackData.java
 *   Created on Aug 26, 2004
 *
 *    The TrackData.java is part of TrackEditor-0.6.2.
 *
 *    TrackEditor-0.6.2 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.6.2 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.6.2TrackEditor; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package utils;

import java.util.Vector;

import utils.circuit.Camera;
import utils.circuit.Curve;
import utils.circuit.Georeference;
import utils.circuit.Graphic;
import utils.circuit.GraphicObject;
import utils.circuit.Header;
import utils.circuit.LocalInfo;
import utils.circuit.MainTrack;
import utils.circuit.ObjectMap;
import utils.circuit.Reliefs;
import utils.circuit.Sector;
import utils.circuit.Segment;
import utils.circuit.StartingGrid;
import utils.circuit.Surface;
import utils.circuit.TerrainGeneration;
import utils.circuit.TrackLight;
import utils.circuit.TrackObject;


/**
 * @author Charalampos Alexopoulos
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class TrackData
{
	private Header					header					= new Header();
	private java.util.List<String>	unusedKeys				= new java.util.ArrayList<String>();
    private LocalInfo				localInfo				= new LocalInfo();
    private StartingGrid			startingGrid			= new StartingGrid();
    private Graphic					graphic					= new Graphic();
    private Vector<Surface> 		surfaces				= new Vector<Surface>();
    private Vector<Camera> 			cameras					= new Vector<Camera>();
    private Vector<TrackObject> 	trackObjects			= new Vector<TrackObject>();
    private Vector<TrackLight> 		trackLights				= new Vector<TrackLight>();
    private MainTrack				mainTrack				= new MainTrack();
    private SegmentVector			segments				= null;
    private Vector<Sector>			sectors					= new Vector<Sector>();
	// TORCS only
	private Georeference			georeference			= new Georeference();

    private boolean					hasSurfacesEntity		= false;
    private boolean					hasSurfacesReference	= false;
    private boolean					hasObjectsEntity		= false;
    private boolean					hasObjectsReference		= false;

	/**
	 * @return the keys and sections in the file the track was read from that saving
	 *         won't keep, as messages: ones the editor doesn't read, and ones it reads
	 *         that the game doesn't use
	 */
	public java.util.List<String> getUnusedKeys()
	{
		return unusedKeys;
	}

	public void setUnusedKeys(java.util.List<String> unusedKeys)
	{
		this.unusedKeys = unusedKeys;
	}

	// where each of the unused keys' messages is from: the key's path in the file
	private java.util.Map<String, String>	unusedKeyPaths	= new java.util.HashMap<String, String>();

	/** @return the path in the file of what the message from getUnusedKeys is about, null if none */
	public String getUnusedKeyPath(String message)
	{
		return unusedKeyPaths.get(message);
	}

	public void setUnusedKeyPaths(java.util.Map<String, String> unusedKeyPaths)
	{
		this.unusedKeyPaths = unusedKeyPaths;
	}

	/**
	 * A value the file has as text the game reads as another number ("*2.5" read
	 * as 0): the segment dialog shows the text in red while the segment still has
	 * the value read, until the track is saved, which writes the number.
	 */
	public static class FileText
	{
		public final String	text;
		public final double	value;
		public final String	message;

		public FileText(String text, double value, String message)
		{
			this.text = text;
			this.value = value;
			this.message = message;
		}
	}

	// by segment, and in it by the segment dialog's field: its side ("Left/" or
	// "Right/", none for the segment's own) and the method it sets
	private final java.util.Map<Segment, java.util.Map<String, FileText>>	fileTexts	=
		new java.util.IdentityHashMap<Segment, java.util.Map<String, FileText>>();

	/** @return the segment's values the file has as such text, by field (see fileTexts) */
	public java.util.Map<String, FileText> getFileTexts(Segment segment)
	{
		java.util.Map<String, FileText> texts = fileTexts.get(segment);
		return texts != null ? texts : java.util.Collections.<String, FileText> emptyMap();
	}

	public void addFileText(Segment segment, String field, FileText text)
	{
		fileTexts.computeIfAbsent(segment, s -> new java.util.HashMap<String, FileText>()).put(field, text);
	}

	/** Forgets the file's text: once the track is saved the file has the numbers */
	public void clearFileTexts()
	{
		fileTexts.clear();
	}

	// the segment dialog's fields set by a segment's keys, by the key's name
	private static final java.util.Map<String, String>	SEGMENT_FIELDS	= new java.util.HashMap<String, String>();
	static
	{
		String[][] fields = { { "lg", "Length" }, { "arc", "ArcDeg" }, { "radius", "RadiusStart" }, { "end radius", "RadiusEnd" },
			{ "z start", "HeightStart" }, { "z start left", "HeightStartLeft" }, { "z start right", "HeightStartRight" },
			{ "z end", "HeightEnd" }, { "z end left", "HeightEndLeft" }, { "z end right", "HeightEndRight" },
			{ "grade", "Grade" }, { "banking start", "BankingStart" }, { "banking end", "BankingEnd" },
			{ "profil steps", "ProfilSteps" }, { "profil steps length", "ProfilStepsLength" },
			{ "profil start tangent", "ProfilStartTangent" }, { "profil start tangent left", "ProfilStartTangentLeft" },
			{ "profil start tangent right", "ProfilStartTangentRight" }, { "profil end tangent", "ProfilEndTangent" },
			{ "profil end tangent left", "ProfilEndTangentLeft" }, { "profil end tangent right", "ProfilEndTangentRight" } };
		for (String[] field : fields)
			SEGMENT_FIELDS.put(field[0], field[1]);
	}

	/**
	 * Where a key in the file is in the segment dialog.
	 *
	 * @param path the key's path in the file, as XmlReader.getKeyPaths has it
	 * @return null when it isn't in a segment; else the segment's name, the
	 *         dialog's tab (Left, Center or Right), and the fields it sets (see
	 *         fileTexts; none when it sets no number field)
	 */
	public static String[] segmentPlace(String path)
	{
		if (path == null)
			return null;
		String[] parts = path.split("/", -1);
		if (parts.length < 3 || !"Main Track".equals(parts[0]) || !("Track Segments".equals(parts[1]) || "segments".equals(parts[1])))
			return null;
		String segment = parts[2];
		if (parts.length == 3)
			return new String[] { segment, "Center" };
		String side = null, part = null, key;
		if (parts.length == 4)
		{
			// version 3 has the sides' keys in the segment: lside start width, rborder height
			java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("^([lr])(side|border|barrier) (.+)$").matcher(parts[3]);
			if (matcher.matches())
			{
				side = matcher.group(1).equals("l") ? "Left" : "Right";
				part = matcher.group(2);
				key = matcher.group(3);
			}
			else
			{
				String field = SEGMENT_FIELDS.get(parts[3]);
				return field == null ? new String[] { segment, "Center" } : new String[] { segment, "Center", field };
			}
		}
		else
		{
			// version 4 has them in sections: Left Side, Right Border, Left Barrier
			String[] words = parts[3].split(" ");
			if (words.length != 2 || !(words[0].equals("Left") || words[0].equals("Right")))
				return new String[] { segment, "Center" };
			side = words[0];
			part = words[1].toLowerCase();
			key = parts[4];
		}
		String[] fields;
		if (part.equals("side"))
			fields = key.equals("start width") ? new String[] { "SideStartWidth" } : key.equals("end width") ? new String[] { "SideEndWidth" }
				: key.equals("width") ? new String[] { "SideStartWidth", "SideEndWidth" } : new String[0];
		else if (part.equals("border") || part.equals("barrier"))
		{
			String name = part.equals("border") ? "Border" : "Barrier";
			fields = key.equals("width") ? new String[] { name + "Width" } : key.equals("height") ? new String[] { name + "Height" } : new String[0];
		}
		else
			fields = new String[0];
		String[] place = new String[2 + fields.length];
		place[0] = segment;
		place[1] = side;
		for (int i = 0; i < fields.length; i++)
			place[2 + i] = side + "/" + fields[i];
		return place;
	}

	/**
	 * @return Returns the header.
	 */
	public Header getHeader()
	{
		return header;
	}
	/**
	 * @param header
	 *            The header to set.
	 */
	public void setHeader(Header header)
	{
		this.header = header;
	}

    /**
     * @return Returns the localInfo.
     */
    public LocalInfo getLocalInfo()
    {
        return localInfo;
    }
    /**
     * @param localInfo The localInfo to set.
     */
    public void setLocalInfo(LocalInfo localInfo)
    {
        this.localInfo = localInfo;
    }

	/**
	 * @return the TORCS georeference
	 */
	public Georeference getGeoreference()
	{
		return georeference;
	}

	public void setGeoreference(Georeference georeference)
	{
		this.georeference = georeference;
	}

    /**
     * @return Returns the startingGrid.
     */
    public StartingGrid getStartingGrid()
    {
        return startingGrid;
    }
    /**
     * @param startingGrid The startingGrid to set.
     */
    public void setStartingGrid(StartingGrid startingGrid)
    {
        this.startingGrid = startingGrid;
    }

    /**
     * @return Returns the surfaces.
     */
    public Vector<Surface> getSurfaces()
    {
        return surfaces;
    }
    /**
     * @param trackData The surfaces to set.
     */
    public void setSurfaces(Vector<Surface> data)
    {
        surfaces = data;
    }

    /**
     * @return Returns the cameras.
     */
    public Vector<Camera> getCameras()
    {
        return cameras;
    }
    /**
     * @param cameras The cameras to set.
     */
    public void setCameras(Vector<Camera> data)
    {
        cameras = data;
    }

    /**
     * @return Returns the trackObjects.
     */
    public Vector<TrackObject> getObjects()
    {
        return trackObjects;
    }
    /**
     * @param objects The trackObjects to set.
     */
    public void setObjects(Vector<TrackObject> data)
    {
        trackObjects = data;
    }

    /**
     * @return Returns the trackLights.
     */
    public Vector<TrackLight> getTrackLights()
    {
        return trackLights;
    }
    /**
     * @param trackLights The trackLights to set.
     */
    public void setTrackLights(Vector<TrackLight> data)
    {
        trackLights = data;
    }

    /**
     * @return Returns the graphic.
     */
    public Graphic getGraphic()
    {
		return graphic;
	}
    /**
     * @param graphic The graphics to set.
     */
	public void setGraphic(Graphic data)
	{
		graphic = data;
	}

    /**
     * @return Returns the mainTrack.
     */
    public MainTrack getMainTrack()
    {
		return mainTrack;
	}

	public boolean getHasSurfacesEntity()
	{
		return hasSurfacesEntity;
	}

	public void setHasSurfacesEntity(boolean hasSurfacesEntity)
	{
		this.hasSurfacesEntity = hasSurfacesEntity;
	}

	public boolean getHasSurfacesReference()
	{
		return hasSurfacesReference;
	}

	public void setHasSurfacesReference(boolean hasSurfacesReference)
	{
		this.hasSurfacesReference = hasSurfacesReference;
	}

	public boolean getHasObjectsEntity()
	{
		return hasObjectsEntity;
	}

	public void setHasObjectsEntity(boolean hasObjectsEntity)
	{
		this.hasObjectsEntity = hasObjectsEntity;
	}

	public boolean getHasObjectsReference()
	{
		return hasObjectsReference;
	}

	public void setHasObjectsReference(boolean hasObjectsReference)
	{
		this.hasObjectsReference = hasObjectsReference;
	}

	/**
	 * Makes the pits and cameras (their segment and the segments they're used
	 * from and to) that refer to segment oldName refer to newName, so renaming a
	 * segment doesn't leave them pointing at a name no segment has: the game
	 * would put such a camera on the first segment.
	 */
	public void renameSegmentReferences(String oldName, String newName)
	{
		if (oldName == null || oldName.equals(newName))
			return;

		utils.circuit.Pits pits = mainTrack.getPits();
		if (oldName.equals(pits.getEntry()))
			pits.setEntry(newName);
		if (oldName.equals(pits.getStart()))
			pits.setStart(newName);
		if (oldName.equals(pits.getStartBuildings()))
			pits.setStartBuildings(newName);
		if (oldName.equals(pits.getStopBuildings()))
			pits.setStopBuildings(newName);
		if (oldName.equals(pits.getEnd()))
			pits.setEnd(newName);
		if (oldName.equals(pits.getExit()))
			pits.setExit(newName);

		for (Camera camera : getCameras())
		{
			if (oldName.equals(camera.getSegment()))
				camera.setSegment(newName);
			if (oldName.equals(camera.getFovStart()))
				camera.setFovStart(newName);
			if (oldName.equals(camera.getFovEnd()))
				camera.setFovEnd(newName);
		}
	}

	/**
	 * @return the pit and camera references that are to the segment name now, so a
	 *         rename can change those and no others
	 */
	public SegmentReferences findSegmentReferences(String name)
	{
		return new SegmentReferences(name);
	}

	/**
	 * The pit and camera references to a segment, found when a rename starts. While
	 * the name is typed, a reference left over from a deleted segment can match one
	 * of the names on the way; renaming by name would take it along.
	 *
	 * Cameras are kept by their place in the list, not as objects: undoing a camera
	 * change replaces them with copies, in the same order.
	 */
	public class SegmentReferences
	{
		// the pit references: entry, start, start buildings, stop buildings, end, exit
		private final boolean[]				pits	= new boolean[6];
		// {camera index, 0 segment, 1 fov start, 2 fov end}
		private final java.util.List<int[]>	cameras	= new java.util.ArrayList<int[]>();

		private SegmentReferences(String name)
		{
			if (name == null)
				return;

			utils.circuit.Pits p = mainTrack.getPits();
			String[] names = { p.getEntry(), p.getStart(), p.getStartBuildings(), p.getStopBuildings(), p.getEnd(), p.getExit() };
			for (int i = 0; i < names.length; i++)
				pits[i] = name.equals(names[i]);

			for (int i = 0; i < cameras().size(); i++)
			{
				Camera camera = cameras().get(i);
				if (name.equals(camera.getSegment()))
					this.cameras.add(new int[] { i, 0 });
				if (name.equals(camera.getFovStart()))
					this.cameras.add(new int[] { i, 1 });
				if (name.equals(camera.getFovEnd()))
					this.cameras.add(new int[] { i, 2 });
			}
		}

		private Vector<Camera> cameras()
		{
			return getCameras();
		}

		/** Makes these references refer to the name. */
		public void rename(String name)
		{
			utils.circuit.Pits p = mainTrack.getPits();
			if (pits[0])
				p.setEntry(name);
			if (pits[1])
				p.setStart(name);
			if (pits[2])
				p.setStartBuildings(name);
			if (pits[3])
				p.setStopBuildings(name);
			if (pits[4])
				p.setEnd(name);
			if (pits[5])
				p.setExit(name);

			for (int[] reference : cameras)
			{
				if (reference[0] >= cameras().size())
					continue;
				Camera camera = cameras().get(reference[0]);
				if (reference[1] == 0)
					camera.setSegment(name);
				else if (reference[1] == 1)
					camera.setFovStart(name);
				else
					camera.setFovEnd(name);
			}
		}
	}
    /**
     * @param mainTrack The mainTrack to set.
     */
	public void setMainTrack(MainTrack data)
	{
		mainTrack = data;
	}

    /**
     * @return Returns the trackData.
     */
    public SegmentVector getSegments()
    {
        return segments;
    }
    /**
     * @param trackData The trackData to set.
     */
    public void setSegments(SegmentVector segments)
    {
        this.segments = segments;
    }

	public Vector<TrackObject> getTrackObjects()
	{
		return trackObjects;
	}
	public void setTrackObjects(Vector<TrackObject> trackObjects)
	{
		this.trackObjects = trackObjects;
	}

	public Vector<Sector> getSectors()
	{
		return sectors;
	}
	public void setSectors(Vector<Sector> sectors)
	{
		this.sectors = sectors;
	}

	public TerrainGeneration getTerrainGeneration()
	{
		return getGraphic().getTerrainGeneration();
	}

	public Vector<ObjectMap> getObjectMaps()
	{
		return getGraphic().getTerrainGeneration().getObjectMaps();
	}

	public Reliefs getReliefs()
	{
		return getGraphic().getTerrainGeneration().getReliefs();
	}

	public Vector<GraphicObject> getGraphicObjects()
	{
		return getGraphic().getTerrainGeneration().getGraphicObjects();
	}

    public void calculateSegmentValues()
    {
    	double width = mainTrack.getWidth();
    	boolean hasGrade = false;
		// like the game, a segment without an end height uses the last explicit grade
		double lastGrade = 0;

		for (int i = 0; i < segments.size(); i++)
		{
			Segment segment = segments.get(i);
			// like the game, the Main Track profil isn't used
			boolean hasSpline = segment.isSpline();

			if (i == 0)
			{
				segment.setCalculatedHeightStart(0);
				segment.setCalculatedHeightStartLeft(0);
				segment.setCalculatedHeightStartRight(0);
				segment.setCalculatedHeightEnd(0);
				segment.setCalculatedHeightEndLeft(0);
				segment.setCalculatedHeightEndRight(0);
				segment.setCalculatedGrade(0);
				segment.setCalculatedBankingStart(0);
				segment.setCalculatedBankingEnd(0);
				segment.setCalculatedStartTangent(0);
				segment.setCalculatedEndTangent(0);
				segment.setCalculatedStartTangentLeft(0);
				segment.setCalculatedEndTangentLeft(0);
				segment.setCalculatedStartTangentRight(0);
				segment.setCalculatedEndTangentRight(0);
			}
			else
			{
				Segment previous = segment.getPreviousShape();

				segment.setCalculatedHeightStartLeft(previous.getCalculatedHeightEndLeft());
				segment.setCalculatedHeightStartRight(previous.getCalculatedHeightEndRight());
				segment.setCalculatedHeightEndLeft(previous.getCalculatedHeightEndLeft());
				segment.setCalculatedHeightEndRight(previous.getCalculatedHeightEndRight());
				segment.setCalculatedHeightStart(previous.getCalculatedHeightEnd());
				segment.setCalculatedHeightEnd(previous.getCalculatedHeightEnd());
				segment.setCalculatedGrade(previous.getCalculatedGrade());
				segment.setCalculatedBankingStart(previous.getCalculatedBankingEnd());
				segment.setCalculatedBankingEnd(previous.getCalculatedBankingEnd());

				segment.setCalculatedStartTangent(previous.getCalculatedEndTangent());
				segment.setCalculatedStartTangentLeft(previous.getCalculatedEndTangentLeft());
				segment.setCalculatedStartTangentRight(previous.getCalculatedEndTangentRight());

				if (hasSpline)
				{
					segment.setCalculatedEndTangent(previous.getCalculatedEndTangent());
					segment.setCalculatedEndTangentLeft(previous.getCalculatedEndTangentLeft());
					segment.setCalculatedEndTangentRight(previous.getCalculatedEndTangentRight());
				}
			}

			double length;
			if (segment.getType().equals("str"))
			{
				length = segment.getLength();
			}
			else
			{
				Curve curve = (Curve) segment;
	            length = ((curve.getRadiusStart() + curve.getValidRadiusEnd()) / 2.0 * curve.getArcRad());
			}

			if (segment.hasHeightStartLeft())
				segment.setCalculatedHeightStartLeft(segment.getHeightStartLeft());

			if (segment.hasHeightStartRight())
				segment.setCalculatedHeightStartRight(segment.getHeightStartRight());

			if (segment.hasHeightEndLeft())
				segment.setCalculatedHeightEndLeft(segment.getHeightEndLeft());

			if (segment.hasHeightEndRight())
				segment.setCalculatedHeightEndRight(segment.getHeightEndRight());

			if (segment.hasGrade())
			{
				hasGrade = true;
				lastGrade = segment.getGrade();
				segment.setCalculatedGrade(segment.getGrade());
			}

			if (segment.hasHeightStart())
			{
				segment.setCalculatedHeightStart(segment.getHeightStart());
				segment.setCalculatedHeightStartLeft(segment.getHeightStart());
				segment.setCalculatedHeightStartRight(segment.getHeightStart());
			}
			else
			{
				segment.setCalculatedHeightStart((segment.getCalculatedHeightStartLeft() + segment.getCalculatedHeightStartRight()) / 2);
			}

			if (segment.hasHeightEnd())
			{
				segment.setCalculatedHeightEnd(segment.getHeightEnd());
				segment.setCalculatedHeightEndLeft(segment.getHeightEnd());
				segment.setCalculatedHeightEndRight(segment.getHeightEnd());
				segment.setCalculatedGrade(((segment.getCalculatedHeightEnd() - segment.getCalculatedHeightStart()) / length) * 100);
			}
			else if (hasGrade)
			{
				segment.setCalculatedGrade(lastGrade);
				segment.setCalculatedHeightEnd(segment.getCalculatedHeightStart() + (length * (lastGrade / 100)));
			}
			else
			{
				segment.setCalculatedHeightEnd((segment.getCalculatedHeightEndLeft() + segment.getCalculatedHeightEndRight()) / 2);
			}

			if (segment.hasBankingStart())
			{
				segment.setCalculatedBankingStart(segment.getBankingStart());
			}
			else
			{
			    segment.setCalculatedBankingStart(Math.toDegrees(Math.atan2(segment.getCalculatedHeightStartLeft() - segment.getCalculatedHeightStartRight(), width)));
			}

			if (segment.hasBankingEnd())
			{
				segment.setCalculatedBankingEnd(segment.getBankingEnd());
			}
			else
			{
				segment.setCalculatedBankingEnd(Math.toDegrees(Math.atan2(segment.getCalculatedHeightEndLeft() - segment.getCalculatedHeightEndRight(), width)));
			}

			double dz = Math.tan(Math.toRadians(segment.getCalculatedBankingStart())) * width / 2;
			segment.setCalculatedHeightStartLeft(segment.getCalculatedHeightStart() + dz);
			segment.setCalculatedHeightStartRight(segment.getCalculatedHeightStart() - dz);
			dz = Math.tan(Math.toRadians(segment.getCalculatedBankingEnd())) * width / 2;
			segment.setCalculatedHeightEndLeft(segment.getCalculatedHeightEnd() + dz);
			segment.setCalculatedHeightEndRight(segment.getCalculatedHeightEnd() - dz);

			if (hasSpline)
			{
				if (segment.hasProfilStartTangentLeft())
					segment.setCalculatedStartTangentLeft(segment.getProfilStartTangentLeft());
				if (segment.hasProfilEndTangentLeft())
					segment.setCalculatedEndTangentLeft(segment.getProfilEndTangentLeft());
				if (segment.hasProfilStartTangentRight())
					segment.setCalculatedStartTangentRight(segment.getProfilStartTangentRight());
				if (segment.hasProfilEndTangentRight())
					segment.setCalculatedEndTangentRight(segment.getProfilEndTangentRight());

				if (segment.hasProfilStartTangent())
				{
					segment.setCalculatedStartTangent(segment.getProfilStartTangent());
					segment.setCalculatedStartTangentLeft(segment.getProfilStartTangent());
					segment.setCalculatedStartTangentRight(segment.getProfilStartTangent());
				}
				if (segment.hasProfilEndTangent())
				{
					segment.setCalculatedEndTangent(segment.getProfilEndTangent());
					segment.setCalculatedEndTangentLeft(segment.getProfilEndTangent());
					segment.setCalculatedEndTangentRight(segment.getProfilEndTangent());
				}
			}
			else
			{
				// tangents are in %, like the profil tangent attributes
				segment.setCalculatedStartTangentLeft((segment.getCalculatedHeightEndLeft() - segment.getCalculatedHeightStartLeft()) / length * 100);
				segment.setCalculatedEndTangentLeft((segment.getCalculatedHeightEndLeft() - segment.getCalculatedHeightStartLeft()) / length * 100);

				segment.setCalculatedStartTangentRight((segment.getCalculatedHeightEndRight() - segment.getCalculatedHeightStartRight()) / length * 100);
				segment.setCalculatedEndTangentRight((segment.getCalculatedHeightEndRight() - segment.getCalculatedHeightStartRight()) / length * 100);

				segment.setCalculatedStartTangent((segment.getCalculatedStartTangentLeft() + segment.getCalculatedStartTangentRight()) / 2);
				segment.setCalculatedEndTangent((segment.getCalculatedEndTangentLeft() + segment.getCalculatedEndTangentRight()) / 2);
			}
		}

		//segments.dumpCalculated("");
    }
}
