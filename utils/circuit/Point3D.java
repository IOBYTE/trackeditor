package utils.circuit;

import java.awt.geom.Point2D;

public class Point3D extends Point2D.Double
{
	public double z;

	public Point3D()
	{
		super();
	}

	public Point3D(double x, double y, double z)
	{
		super(x, y);
		this.z = z;
	}

	public void set(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void sub(Point3D p0, Point3D p1)
	{
		x = p0.x - p1.x;
		y = p0.y - p1.y;
		z = p0.z - p1.z;
	}

	public void cross(Point3D p0, Point3D p1)
	{
		x = -p0.z * p1.y + p0.y * p1.z;
		y =  p0.z * p1.x - p0.x * p1.z;
		z = -p0.y * p1.x + p0.x * p1.y;
	}

	public double dot(Point3D p)
	{
		return x * p.x + y * p.y + z * p.z;
	}

	public void scaleAdd(double scale, Point3D p0, Point3D p1)
	{
		x = scale * p0.x + p1.x;
		y = scale * p0.y + p1.y;
		z = scale * p0.z + p1.z;
	}

	public String toString()
	{
		return "Point3D["+x+", "+y+", "+z+"]";
	}
}
