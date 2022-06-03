package utils.circuit;

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

//	<section name="Object Maps">
//		<section name="map 1">
//			<attstr name="object map" val="objects-map1.png" />
//		</section>
//	</section>

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

	public void dump()
    {
		System.out.println("trackStep       : " + trackStep);
		System.out.println("borderMargin    : " + borderMargin);
		System.out.println("borderStep      : " + borderStep);
		System.out.println("borderHeight    : " + borderHeight);
		System.out.println("orientation     : " + orientation);
		System.out.println("maximumAltitude : " + maximumAltitude);
		System.out.println("minimumAltitude : " + minimumAltitude);
		System.out.println("groupSize       : " + groupSize);
		System.out.println("elevationMap    : " + elevationMap);
		System.out.println("reliefFile      : " + reliefFile);
		System.out.println("surface         : " + surface);
    }
}