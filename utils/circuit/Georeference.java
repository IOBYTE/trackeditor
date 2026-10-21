package utils.circuit;

/**
 * The TORCS track's geographic reference: where the main road centre is at the
 * start of the first segment (WGS84 latitude and longitude in degrees, altitude
 * in metres above mean sea level) and the heading there in degrees clockwise
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
