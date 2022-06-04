package utils.circuit;

import java.util.Vector;

public class TerrainGeneration {

	private double				trackStep		= 20;
	private double				borderMargin	= 50;
	private double				borderStep		= 30;
	private double				borderHeight	= 15;
	private String				orientation		= "clockwise";
	private double				maximumAltitude	= Double.NaN;
	private double				minimumAltitude	= Double.NaN;
	private double				groupSize		= Double.NaN;
	private String				elevationMap	= null;
	private String				reliefFile		= null;
	private String				surface			= null;
	private Vector<ObjectMap>	objectMaps		= new Vector<ObjectMap>();

	/**
	 * @return Returns the terrainTrackStep.
	 */
	public double getTrackStep()
	{
		return trackStep;
	}
	/**
	 * @param trackStep
	 *            The trackStep to set.
	 */
	public void setTrackStep(double trackStep)
	{
		this.trackStep = trackStep;
	}
	/**
	 * @return Returns the borderMargin.
	 */
	public double getBorderMargin()
	{
		return borderMargin;
	}
	/**
	 * @param borderMargin
	 *            The borderMargin to set.
	 */
	public void setBorderMargin(double borderMargin)
	{
		this.borderMargin = borderMargin;
	}
	/**
	 * @return Returns the borderStep.
	 */
	public double getBorderStep()
	{
		return borderStep;
	}
	/**
	 * @param borderStep
	 *            The pitMaxPits to set.
	 */
	public void setBorderStep(double borderStep)
	{
		this.borderStep = borderStep;
	}
	/**
	 * @return Returns the borderHeight.
	 */
	public double getBorderHeight()
	{
		return borderHeight;
	}
	/**
	 * @param borderHeight
	 *            The borderHeight to set.
	 */
	public void setBorderHeight(double borderHeight)
	{
		this.borderHeight = borderHeight;
	}
	/**
	 * @return Returns the orientation.
	 */
	public String getOrientation()
	{
		return orientation;
	}
	/**
	 * @param orientation
	 *            The orientation to set.
	 */
	public void setOrientation(String orientation)
	{
		this.orientation = orientation;
	}
	/**
	 * @return Returns the maximumAltitude.
	 */
	public double getMaximumAltitude()
	{
		return maximumAltitude;
	}
	/**
	 * @param maximumAltitude
	 *            The maximumAltitude to set.
	 */
	public void setMaximumAltitude(double maximumAltitude)
	{
		this.maximumAltitude = maximumAltitude;
	}
	/**
	 * @return Returns the minimumAltitude.
	 */
	public double getMinimumAltitude()
	{
		return minimumAltitude;
	}
	/**
	 * @param minimumAltitude
	 *            The minimumAltitude to set.
	 */
	public void setMinimumAltitude(double minimumAltitude)
	{
		this.minimumAltitude = minimumAltitude;
	}
	/**
	 * @return Returns the groupSize.
	 */
	public double getGroupSize()
	{
		return groupSize;
	}
	/**
	 * @param groupSize
	 *            The groupSize to set.
	 */
	public void setGroupSize(double groupSize)
	{
		this.groupSize = groupSize;
	}
	/**
	 * @return Returns the elevationMap.
	 */
	public String getElevationMap()
	{
		return elevationMap;
	}
	/**
	 * @param elevationMap
	 *            The elevationMap to set.
	 */
	public void setElevationMap(String elevationMap)
	{
		this.elevationMap = elevationMap;
	}
	/**
	 * @return Returns the reliefFile.
	 */
	public String getReliefFile()
	{
		return reliefFile;
	}
	/**
	 * @param reliefFile
	 *            The reliefFile to set.
	 */
	public void setReliefFile(String reliefFile)
	{
		this.reliefFile = reliefFile;
	}
	/**
	 * @return Returns the surface.
	 */
	public String getSurface()
	{
		return surface;
	}
	/**
	 * @param surface
	 *            The surface to set.
	 */
	public void setSurface(String surface)
	{
		this.surface = surface;
	}

	public Vector<ObjectMap> getObjectMaps() {
		return objectMaps;
	}
	public void setObjectMaps(Vector<ObjectMap> objectMaps) {
		this.objectMaps = objectMaps;
	}

	public void dump(String indent)
    {
		System.out.println(indent + "TerrainGeneration");
		System.out.println(indent + "  trackStep       : " + trackStep);
		System.out.println(indent + "  borderMargin    : " + borderMargin);
		System.out.println(indent + "  borderStep      : " + borderStep);
		System.out.println(indent + "  borderHeight    : " + borderHeight);
		System.out.println(indent + "  orientation     : " + orientation);
		System.out.println(indent + "  maximumAltitude : " + maximumAltitude);
		System.out.println(indent + "  minimumAltitude : " + minimumAltitude);
		System.out.println(indent + "  groupSize       : " + groupSize);
		System.out.println(indent + "  elevationMap    : " + elevationMap);
		System.out.println(indent + "  reliefFile      : " + reliefFile);
		System.out.println(indent + "  surface         : " + surface);
		System.out.println(indent + "  objectMaps[" + objectMaps.size() + "]");
		for (int i = 0; i < objectMaps.size(); i++)
		{
			objectMaps.get(i).dump(indent + "    ");
		}
    }
}
