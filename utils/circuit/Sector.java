package utils.circuit;

public class Sector
{
	private String	name				= null;
	private String	comment				= null;
	private double	distanceFromStart	= Double.NaN;
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}

	public String getComment()
	{
		return comment;
	}
	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public double getDistanceFromStart()
	{
		return distanceFromStart;
	}
	public void setDistanceFromStart(double distanceFromStart)
	{
		this.distanceFromStart = distanceFromStart;
	}

	public void dump(String indent)
    {
		System.out.println(indent + "Sector");
		System.out.println(indent + "  name              : " + name);
		System.out.println(indent + "  comment           : " + comment);
		System.out.println(indent + "  distanceFromStart : " + distanceFromStart);
    }
}
