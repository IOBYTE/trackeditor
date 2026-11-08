package gui.properties;

import utils.circuit.GraphicObject;

public class GraphicObjectData
{
	public String	name;
	public String	comment;
	public Integer	color;
	public Double	trackX;
	public Double	trackY;
	public Double	orientation;
	public Double	height;
	public final Integer	originalColor;	// what "Unknown" goes back to
	// the object the row was read from, null for a row added in the dialog:
	// exit() writes each row back to its own object, whatever rows were deleted
	public final GraphicObject	object;

	GraphicObjectData(String name, String comment, Integer color, double trackX, double trackY, double orientation, double height)
	{
		this(null, name, comment, color, trackX, trackY, orientation, height);
	}

	GraphicObjectData(GraphicObject object, String name, String comment, Integer color, double trackX, double trackY, double orientation, double height)
	{
		this.object = object;
		this.name = name;
		this.comment = comment;
		this.color = color;
		this.originalColor = color;
		this.trackX = trackX;
		this.trackY = trackY;
		this.orientation = orientation;
		this.height = height;
	}
}
