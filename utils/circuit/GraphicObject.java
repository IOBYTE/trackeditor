package utils.circuit;

import java.awt.geom.Point2D;

public class GraphicObject
{
	private ObjShapeObject	shape		= null;
	private String			comment		= null;

	// TODO add more overrides here
	private double 			orientation = Double.NaN;
	private double			height		= Double.NaN;

	public GraphicObject(String name, int rgb, Point2D.Double location)
	{
		shape = new ObjShapeObject(name, rgb, location);
	}
	
	public GraphicObject(ObjShapeObject shape)
	{
		this.shape = shape;
	}
	
	public Object clone()
	{
		GraphicObject object = new GraphicObject((ObjShapeObject) this.shape.clone());
		object.comment = this.comment;
		object.orientation = this.orientation;
		object.height = this.height;
		return object;
	}

	public ObjShapeObject getShape()
	{
		return shape;
	}

	public void setShape(ObjShapeObject shape)
	{
		this.shape = shape;
	}

	public String getName()
	{
		return shape.getName();
	}

	public void setName(String name)
	{
		shape.setName(name);
	}

	public int getColor()
	{
		return shape.getRGB();
	}

	public void setColor(int rgb)
	{
		shape.setRGB(rgb);
	}

	public double getX()
	{
		return shape.getTrackLocation().x;
	}

	public void setX(double x)
	{
		shape.setTrackLocationX(x);
	}

	public double getY()
	{
		return shape.getTrackLocation().y;
	}

	public void setY(double y)
	{
		shape.setTrackLocationY(y);
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public double getOrientation()
	{
		return orientation;
	}

	public void setOrientation(double orientation)
	{
		this.orientation = orientation;
	}

	public double getHeight()
	{
		return height;
	}

	public void setHeight(double height)
	{
		this.height = height;
	}

	public void dump(String indent)
	{
		System.out.println(indent + "name        : " + getName());
		System.out.println(indent + "color       : " + getColor());
		System.out.println(indent + "x           : " + getX());
		System.out.println(indent + "y           : " + getY());
		System.out.println(indent + "orientation : " + getOrientation());
		System.out.println(indent + "height      : " + getHeight());
	}
}
