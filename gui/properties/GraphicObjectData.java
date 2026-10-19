package gui.properties;

public class GraphicObjectData
{
	public String	name;
	public String	comment;
	public Integer	color;
	public Double	trackX;
	public Double	trackY;
	public Double	orientation;
	public Double	height;

	GraphicObjectData(String name, String comment, Integer color, double trackX, double trackY, double orientation, double height)
	{
		this.name = name;
		this.comment = comment;
		this.color = color;
		this.trackX = trackX;
		this.trackY = trackY;
		this.orientation = orientation;
		this.height = height;
	}
}
