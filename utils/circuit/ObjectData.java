package utils.circuit;

public class ObjectData
{
	public String	name;
	public Integer	color;
	public Integer	imageX;
	public Integer	imageY;
	public Double	trackX;
	public Double	trackY;
	public final Integer	originalColor;	// what "Unknown" goes back to
	// the object the row was read from, null for a row read from the image:
	// exit() writes each row back to its own object, whatever rows were deleted
	public final ObjShapeObject	object;

	public ObjectData(String name, Integer color, Integer imageX, Integer imageY, double trackX, double trackY)
	{
		this(null, name, color, imageX, imageY, trackX, trackY);
	}

	public ObjectData(ObjShapeObject object, String name, Integer color, Integer imageX, Integer imageY, double trackX, double trackY)
	{
		this.object = object;
		this.name = name;
		this.color = color;
		this.originalColor = color;
		this.imageX = imageX;
		this.imageY = imageY;
		this.trackX = trackX;
		this.trackY = trackY;
	}
}
