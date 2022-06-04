package utils.circuit;

public class LocalInfo {

	private String station					= null;
	private double timezone					= Double.NaN;
	private double overallRainLikelyhood	= Double.NaN;
	private double littleRainLikelyhood		= Double.NaN;
	private double mediumRainLikelyhood		= Double.NaN;
	private double timeOfDay				= Double.NaN;
	private double sunAscension				= Double.NaN;
	private double altitude					= Double.NaN;

	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public double getTimezone() {
		return timezone;
	}
	public void setTimezone(double timezone) {
		this.timezone = timezone;
	}
	public double getOverallRainLikelyhood() {
		return overallRainLikelyhood;
	}
	public void setOverallRainLikelyhood(double overallRainLikelyhood) {
		this.overallRainLikelyhood = overallRainLikelyhood;
	}
	public double getLittleRainLikelyhood() {
		return littleRainLikelyhood;
	}
	public void setLittleRainLikelyhood(double littleRainLikelyhood) {
		this.littleRainLikelyhood = littleRainLikelyhood;
	}
	public double getMediumRainLikelyhood() {
		return mediumRainLikelyhood;
	}
	public void setMediumRainLikelyhood(double mediumRainLikelyhood) {
		this.mediumRainLikelyhood = mediumRainLikelyhood;
	}
	public double getTimeOfDay() {
		return timeOfDay;
	}
	public void setTimeOfDay(double timeOfDay) {
		this.timeOfDay = timeOfDay;
	}
	public double getSunAscension() {
		return sunAscension;
	}
	public void setSunAscension(double sunAscension) {
		this.sunAscension = sunAscension;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public void dump(String indent)
    {
		System.out.println(indent + "LocalInfo");
		System.out.println(indent + "  station               : " + station);
		System.out.println(indent + "  timezone              : " + timezone);
		System.out.println(indent + "  overallRainLikelyhood : " + overallRainLikelyhood);
		System.out.println(indent + "  littleRainLikelyhood  : " + littleRainLikelyhood);
		System.out.println(indent + "  mediumRainLikelyhood  : " + mediumRainLikelyhood);
		System.out.println(indent + "  timeOfDay             : " + timeOfDay);
		System.out.println(indent + "  sunAscension          : " + sunAscension);
		System.out.println(indent + "  altitude              : " + altitude);
    }
}
