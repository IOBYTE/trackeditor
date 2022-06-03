package utils.circuit;

public class TerrainGeneration {
	private double				terrainTrackStep		= 20;
	private double				terrainBorderMargin		= 50;
	private double				terrainBorderStep		= 30;
	private double				terrainBorderHeight		= 15;
	private String				terrainOrientation		= "clockwise";
	private double				terrainMaximumAltitude	= Double.NaN;
	private double				terrainMinimumAltitude	= Double.NaN;
	private double				terrainGroupSize		= Double.NaN;
	private String				terrainElevationMap		= null;
	private String				terrainReliefFile		= null;
	private String				terrainSurface			= null;

	/**
	 * @return Returns the terrainTrackStep.
	 */
	public double getTerrainTrackStep()
	{
		return terrainTrackStep;
	}
	/**
	 * @param terrainTrackStep
	 *            The terrainTrackStep to set.
	 */
	public void setTerrainTrackStep(double terrainTrackStep)
	{
		this.terrainTrackStep = terrainTrackStep;
	}
	/**
	 * @return Returns the terrainBorderMargin.
	 */
	public double getTerrainBorderMargin()
	{
		return terrainBorderMargin;
	}
	/**
	 * @param terrainBorderMargin
	 *            The terrainBorderMargin to set.
	 */
	public void setTerrainBorderMargin(double terrainBorderMargin)
	{
		this.terrainBorderMargin = terrainBorderMargin;
	}
	/**
	 * @return Returns the terrainBorderStep.
	 */
	public double getTerrainBorderStep()
	{
		return terrainBorderStep;
	}
	/**
	 * @param terrainBorderStep
	 *            The pitMaxPits to set.
	 */
	public void setTerrainBorderStep(double terrainBorderStep)
	{
		this.terrainBorderStep = terrainBorderStep;
	}
	/**
	 * @return Returns the terrainBorderHeight.
	 */
	public double getTerrainBorderHeight()
	{
		return terrainBorderHeight;
	}
	/**
	 * @param terrainBorderHeight
	 *            The terrainBorderHeight to set.
	 */
	public void setTerrainBorderHeight(double terrainBorderHeight)
	{
		this.terrainBorderHeight = terrainBorderHeight;
	}
	/**
	 * @return Returns the terrainOrientation.
	 */
	public String getTerrainOrientation()
	{
		return terrainOrientation;
	}
	/**
	 * @param terrainOrientation
	 *            The terrainOrientation to set.
	 */
	public void setTerrainOrientation(String terrainOrientation)
	{
		this.terrainOrientation = terrainOrientation;
	}
	/**
	 * @return Returns the terrainMaximumAltitude.
	 */
	public double getTerrainMaximumAltitude()
	{
		return terrainMaximumAltitude;
	}
	/**
	 * @param terrainMaximumAltitude
	 *            The terrainMaximumAltitude to set.
	 */
	public void setTerrainMaximumAltitude(double terrainMaximumAltitude)
	{
		this.terrainMaximumAltitude = terrainMaximumAltitude;
	}
	/**
	 * @return Returns the terrainMinimumAltitude.
	 */
	public double getTerrainMinimumAltitude()
	{
		return terrainMinimumAltitude;
	}
	/**
	 * @param terrainMinimumAltitude
	 *            The terrainMinimumAltitude to set.
	 */
	public void setTerrainMinimumAltitude(double terrainMinimumAltitude)
	{
		this.terrainMinimumAltitude = terrainMinimumAltitude;
	}
	/**
	 * @return Returns the terrainGroupSize.
	 */
	public double getTerrainGroupSize()
	{
		return terrainGroupSize;
	}
	/**
	 * @param terrainGroupSize
	 *            The terrainGroupSize to set.
	 */
	public void setTerrainGroupSize(double terrainGroupSize)
	{
		this.terrainGroupSize = terrainGroupSize;
	}
	/**
	 * @return Returns the terrainElevationMap.
	 */
	public String getTerrainElevationMap()
	{
		return terrainElevationMap;
	}
	/**
	 * @param terrainElevationMap
	 *            The terrainElevationMap to set.
	 */
	public void setTerrainElevationMap(String terrainElevationMap)
	{
		this.terrainElevationMap = terrainElevationMap;
	}
	/**
	 * @return Returns the terrainReliefFile.
	 */
	public String getTerrainReliefFile()
	{
		return terrainReliefFile;
	}
	/**
	 * @param terrainReliefFile
	 *            The terrainReliefFile to set.
	 */
	public void setTerrainReliefFile(String terrainReliefFile)
	{
		this.terrainReliefFile = terrainReliefFile;
	}
	/**
	 * @return Returns the terrainSurface.
	 */
	public String getTerrainSurface()
	{
		return terrainSurface;
	}
	/**
	 * @param terrainSurface
	 *            The terrainSurface to set.
	 */
	public void setTerrainSurface(String terrainSurface)
	{
		this.terrainSurface = terrainSurface;
	}

	public void dump()
    {
		System.out.println("terrainTrackStep       : " + terrainTrackStep);
		System.out.println("terrainBorderMargin    : " + terrainBorderMargin);
		System.out.println("terrainBorderStep      : " + terrainBorderStep);
		System.out.println("terrainBorderHeight    : " + terrainBorderHeight);
		System.out.println("terrainOrientation     : " + terrainOrientation);
		System.out.println("terrainMaximumAltitude : " + terrainMaximumAltitude);
		System.out.println("terrainMinimumAltitude : " + terrainMinimumAltitude);
		System.out.println("terrainGroupSize       : " + terrainGroupSize);
		System.out.println("terrainElevationMap    : " + terrainElevationMap);
		System.out.println("terrainReliefFile      : " + terrainReliefFile);
		System.out.println("terrainSurface         : " + terrainSurface);
    }
}