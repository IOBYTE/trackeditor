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
import utils.circuit.LocalInfo;
import utils.circuit.Segment;
import utils.circuit.Surface;
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
    private static Vector<Segment>				trackData;
    private static Vector<Surface> 				surfaceData;
    private static Vector<Camera> 				cameraData;
    private static Vector<TrackObject> 			objectData;
    private static Vector<TrackLight> 			lightData;
    private static LocalInfo					localData;
	/**
     * @return Returns the trackData.
     */
    public static Vector<Segment> getTrackData()
    {
        return trackData;
    }
    /**
     * @param trackData The trackData to set.
     */
    public static void setTrackData(Vector<Segment> data)
    {
        trackData = data;
    }

    /**
     * @return Returns the surfaceData.
     */
    public static Vector<Surface> getSurfaceData()
    {
        return surfaceData;
    }
    /**
     * @param trackData The surfaceData to set.
     */
    public static void setSurfaceData(Vector<Surface> data)
    {
        surfaceData = data;
    }

    /**
     * @return Returns the cameraData.
     */
    public static Vector<Camera> getCameraData()
    {
        return cameraData;
    }
    /**
     * @param trackData The cameraData to set.
     */
    public static void setCameraData(Vector<Camera> data)
    {
        cameraData = data;
    }

    /**
     * @return Returns the objectData.
     */
    public static Vector<TrackObject> getObjectData()
    {
        return objectData;
    }
    /**
     * @param trackData The objectData to set.
     */
    public static void setObjectData(Vector<TrackObject> data)
    {
        objectData = data;
    }

    /**
     * @return Returns the lightData.
     */
    public static Vector<TrackLight> getLightData()
    {
        return lightData;
    }
    /**
     * @param lightData The lightData to set.
     */
    public static void setLightData(Vector<TrackLight> data)
    {
        lightData = data;
    }

    /**
     * @return Returns the localData.
     */
    public static LocalInfo getLocalData()
    {
		return localData;
	}
    /**
     * @param localData The localData to set.
     */
	public static void setLocalData(LocalInfo data)
	{
		localData = data;
	}

	/**
     * 
     */
    public TrackData()
    {
        super();
        // TODO Auto-generated constructor stub
    }
}
