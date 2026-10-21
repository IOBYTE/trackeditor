package utils.circuit;

import java.io.IOException;
import java.util.Vector;

public class TerrainGeneration
{
	public static final double 		DEFAULT_TRACK_STEP				= 10;
	public static final double		DEFAULT_BORDER_MARGIN			= 100;
	public static final double		DEFAULT_BORDER_STEP				= 10;
	public static final double		DEFAULT_BORDER_HEIGHT			= 0;
	public static final String		DEFAULT_ORIENTATION				= "clockwise";
	public static final double		DEFAULT_MAXIMUM_ALTITUDE		= Double.NaN;
	public static final double		DEFAULT_MINIMUM_ALTITUDE		= Double.NaN;
	public static final double		DEFAULT_GROUP_SIZE				= 100;
	public static final String		DEFAULT_ELEVATION_MAP			= null;
	public static final String		DEFAULT_RELIEF_FILE				= null;
	public static final String		DEFAULT_RELIEF_BORDER			= "no";
	public static final String		DEFAULT_SURFACE					= "grass";
	public static final int			DEFAULT_RANDOM_SEED				= 1;
	public static final String		DEFAULT_USE_OBJECT_MATERIALS	= "no";
	public static final String		DEFAULT_TILED_FILE				= null;	
	// TORCS trackgen: the defaults it uses for a missing value
	public static final double		DEFAULT_CROSSING_BRIDGE_SOCKET_GAP	= 0.5;
	public static final double		DEFAULT_CROSSING_BRIDGE_THICKNESS	= 1.0;
	// the crossing bridge surfaces: underside, side, socket and floor
	public static final String[]	CROSSING_BRIDGE_SURFACE_KEYS	=
		{"crossing bridge underside surface", "crossing bridge side surface",
		 "crossing bridge socket surface", "crossing bridge floor surface"};
	public static final String[]	CROSSING_BRIDGE_SURFACE_NAMES	= {"Underside", "Side", "Socket", "Floor"};
	public static final String[]	DEFAULT_CROSSING_BRIDGE_SURFACES	=
		{"bridge-underside", "bridge-side", "bridge-socket", "bridge-floor"};

	private double					trackStep						= Double.NaN;
	private double					borderMargin					= Double.NaN;
	private double					borderStep						= Double.NaN;
	private double					borderHeight					= Double.NaN;
	private String					orientation						= null;
	private double					maximumAltitude					= Double.NaN;
	private double					minimumAltitude					= Double.NaN;
	private double					groupSize						= Double.NaN;
	private String					elevationMap					= null;
	private Reliefs					reliefs							= new Reliefs();
	private String					reliefBorder					= null;
	private String					surface							= null;
	private int						randomSeed						= Integer.MAX_VALUE;
	private String					useObjectMaterials				= null;
	private String					tiledFile						= null;
	private double					terrainTransition				= Double.NaN;
	private double					crossingBridgeSocketGap			= Double.NaN;
	private double					crossingBridgeThickness			= Double.NaN;
	private String[]				crossingBridgeSurfaces			= new String[CROSSING_BRIDGE_SURFACE_KEYS.length];
	private Vector<ObjectMap>		objectMaps						= new Vector<ObjectMap>();
	private Vector<GraphicObject>	graphicObjects					= new Vector<GraphicObject>();

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
		return reliefs.getFileName();
	}
	/**
	 * @param reliefFile
	 *            The reliefFile to set.
	 */
	public void setReliefFile(String reliefFile) throws Exception, IOException
	{
		this.reliefs.useFile(reliefFile);
	}
	public Reliefs getReliefs()
	{
		return reliefs;
	}
	public void setReliefs(Reliefs reliefs)
	{
		this.reliefs = reliefs;
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

	public Vector<ObjectMap> getObjectMaps()
	{
		return objectMaps;
	}
	public void setObjectMaps(Vector<ObjectMap> objectMaps)
	{
		this.objectMaps = objectMaps;
	}
	public String getReliefBorder()
	{
		return reliefBorder;
	}
	public void setReliefBorder(String reliefBorder)
	{
		this.reliefBorder = reliefBorder;
	}
	
	public int getRandomSeed()
	{
		return randomSeed;
	}
	public void setRandomSeed(int randomSeed)
	{
		this.randomSeed = randomSeed;
	}

	public String getUseObjectMaterials()
	{
		return useObjectMaterials;
	}
	public void setUseObjectMaterials(String useObjectMaterials)
	{
		this.useObjectMaterials = useObjectMaterials;
	}

	public String getTiledFile()
	{
		return tiledFile;
	}
	public void setTiledFile(String tiledFile)
	{
		this.tiledFile = tiledFile;
	}

	public double getTerrainTransition()
	{
		return terrainTransition;
	}
	public void setTerrainTransition(double terrainTransition)
	{
		this.terrainTransition = terrainTransition;
	}

	public double getCrossingBridgeSocketGap()
	{
		return crossingBridgeSocketGap;
	}
	public void setCrossingBridgeSocketGap(double crossingBridgeSocketGap)
	{
		this.crossingBridgeSocketGap = crossingBridgeSocketGap;
	}

	public double getCrossingBridgeThickness()
	{
		return crossingBridgeThickness;
	}
	public void setCrossingBridgeThickness(double crossingBridgeThickness)
	{
		this.crossingBridgeThickness = crossingBridgeThickness;
	}

	/**
	 * @param index the surface's index in CROSSING_BRIDGE_SURFACE_KEYS
	 */
	public String getCrossingBridgeSurface(int index)
	{
		return crossingBridgeSurfaces[index];
	}
	public void setCrossingBridgeSurface(int index, String surface)
	{
		crossingBridgeSurfaces[index] = surface;
	}

	public Vector<GraphicObject> getGraphicObjects()
	{
		return graphicObjects;
	}
	public void setGraphicObjects(Vector<GraphicObject> graphicObjects)
	{
		this.graphicObjects = graphicObjects;
	}

	public void dump(String indent)
    {
		System.out.println(indent + "TerrainGeneration");
		System.out.println(indent + "  trackStep            : " + trackStep);
		System.out.println(indent + "  borderMargin         : " + borderMargin);
		System.out.println(indent + "  borderStep           : " + borderStep);
		System.out.println(indent + "  borderHeight         : " + borderHeight);
		System.out.println(indent + "  orientation          : " + orientation);
		System.out.println(indent + "  maximumAltitude      : " + maximumAltitude);
		System.out.println(indent + "  minimumAltitude      : " + minimumAltitude);
		System.out.println(indent + "  groupSize            : " + groupSize);
		System.out.println(indent + "  elevationMap         : " + elevationMap);
		reliefs.dump(indent + "  ");
		System.out.println(indent + "  reliefBorder         : " + reliefBorder);
		System.out.println(indent + "  surface              : " + surface);
		System.out.println(indent + "  random seed          : " + randomSeed);
		System.out.println(indent + "  use object materials : " + useObjectMaterials);
		System.out.println(indent + "  tiled file           : " + tiledFile);		
		System.out.println(indent + "  terrain transition   : " + terrainTransition);
		System.out.println(indent + "  bridge socket gap    : " + crossingBridgeSocketGap);
		System.out.println(indent + "  bridge thickness     : " + crossingBridgeThickness);
		for (int i = 0; i < crossingBridgeSurfaces.length; i++)
			System.out.println(indent + "  " + CROSSING_BRIDGE_SURFACE_KEYS[i] + " : " + crossingBridgeSurfaces[i]);
		System.out.println(indent + "  objectMaps           : " + objectMaps.size());
		for (int i = 0; i < objectMaps.size(); i++)
		{
			System.out.println(indent + "  objectMaps[" + i + "]");
			objectMaps.get(i).dump(indent + "    ");
		}
		System.out.println(indent + "  graphicObjects       : " + graphicObjects.size());
		for (int i = 0; i < graphicObjects.size(); i++)
		{
			System.out.println(indent + "  graphicObjects[" + i + "]");
			graphicObjects.get(i).dump(indent + "    ");
		}
    }
}
