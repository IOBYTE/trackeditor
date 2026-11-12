package utils.circuit;

/**
 * The TORCS track's geographic reference: where the main road center is at the
 * start of the first segment (WGS84 latitude and longitude in degrees, altitude
 * in meters above mean sea level) and the heading there in degrees clockwise
 * from true north. Values not set are NaN.
 */
public class Georeference
{
	private double	latitude	= Double.NaN;
	private double	longitude	= Double.NaN;
	private double	altitude	= Double.NaN;
	private double	heading		= Double.NaN;

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public double getAltitude()
	{
		return altitude;
	}

	public void setAltitude(double altitude)
	{
		this.altitude = altitude;
	}

	public double getHeading()
	{
		return heading;
	}

	public void setHeading(double heading)
	{
		this.heading = heading;
	}

	/** @return what TORCS can't use in these values, or null */
	public String problem()
	{
		return problem(latitude, longitude);
	}

	/**
	 * @return what TORCS can't use, or null (NaN is not set): TORCS ignores a
	 *         latitude or longitude out of range, or one without the other
	 */
	public static String problem(double latitude, double longitude)
	{
		if (!Double.isNaN(latitude) && !(Math.abs(latitude) <= 90))
			return "the latitude must be from -90 to 90 degrees";
		if (!Double.isNaN(longitude) && !(Math.abs(longitude) <= 180))
			return "the longitude must be from -180 to 180 degrees";
		if (Double.isNaN(latitude) != Double.isNaN(longitude))
			return "set both the latitude and the longitude, or neither (TORCS needs both)";
		return null;
	}

	/** @return true when nothing is set */
	public boolean isEmpty()
	{
		return Double.isNaN(latitude) && Double.isNaN(longitude) && Double.isNaN(altitude) && Double.isNaN(heading);
	}

	public void dump(String indent)
	{
		System.out.println(indent + "Georeference");
		System.out.println(indent + "  latitude  : " + latitude);
		System.out.println(indent + "  longitude : " + longitude);
		System.out.println(indent + "  altitude  : " + altitude);
		System.out.println(indent + "  heading   : " + heading);
	}
}
