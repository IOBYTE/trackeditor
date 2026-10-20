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
	private Header				header					= new Header();
    private LocalInfo			localInfo				= new LocalInfo();
    private StartingGrid		startingGrid			= new StartingGrid();
    private Graphic				graphic					= new Graphic();
    private Vector<Surface> 	surfaces				= new Vector<Surface>();
    private Vector<Camera> 		cameras					= new Vector<Camera>();
    private Vector<TrackObject> trackObjects			= new Vector<TrackObject>();
    private Vector<TrackLight> 	trackLights				= new Vector<TrackLight>();
    private MainTrack			mainTrack				= new MainTrack();
    private SegmentVector		segments				= null;
    private Vector<Sector>		sectors					= new Vector<Sector>();
   
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
				double radiusEnd;

				if (curve.hasRadiusEnd())
					radiusEnd = curve.getRadiusEnd();
				else
					radiusEnd = curve.getRadiusStart();

	            length = ((curve.getRadiusStart() + radiusEnd) / 2.0 * curve.getArcRad());
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
