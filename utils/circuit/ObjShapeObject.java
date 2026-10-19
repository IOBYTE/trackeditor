package utils.circuit;

import java.awt.Color;
import java.awt.geom.Point2D;

public class ObjShapeObject extends Segment
{
	static public final double	defaultSize	= 10;

	// color
	private int					rgb;
	private Color				color;
	
	// position
	private int					imageX = Integer.MAX_VALUE;
	private int					imageY = Integer.MAX_VALUE;
	
	// shape info
	private Point2D.Double		location 	= new Point2D.Double(0, 0);
	private double				width		= defaultSize;
	private double				height		= defaultSize;
	
	public ObjShapeObject(int rgb, int imageX, int imageY)
	{
		super("object");
		this.rgb = rgb & 0x00ffffff;
		this.color = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);
		this.imageX = imageX;
		this.imageY = imageY;
	}
			
	public ObjShapeObject(String name, int rgb, int imageX, int imageY)
	{
		super(name, "object");
		this.rgb = rgb & 0x00ffffff;
		this.color = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);
		this.imageX = imageX;
		this.imageY = imageY;
	}

	public ObjShapeObject(String name, int rgb, Point2D.Double location)
	{
		super(name, "graphic object");
		this.rgb = rgb & 0x00ffffff;
		this.color = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);
		calcShape(location);
	}

	@Override
	public void set(Segment segment)
	{
		super.set(segment);
		ObjShapeObject object = (ObjShapeObject) segment;
		rgb = object.rgb;
		color = new Color(object.color.getRGB());
		imageX = object.imageX;
		imageY = object.imageY;
		location = new Point2D.Double(object.location.x, object.location.y);
		width = object.width;
		height = object.height;
	}

	public Object clone()
	{
		ObjShapeObject object = (ObjShapeObject) super.clone();
		object.rgb = this.rgb;
		object.color = new Color(this.color.getRGB());
		object.imageX = this.imageX;
		object.imageY = this.imageY;
		object.location = (Point2D.Double) this.location.clone();
		object.width = this.width;
		object.height = this.height;
		return object;
	}

	public int getRGB() {
		return rgb;
	}

	public void setRGB(int rgb) {
		this.rgb = rgb;
		this.color = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		this.rgb = color.getRGB();
	}

	public int getImageX() {
		return imageX;
	}

	public void setImageX(int imageX) {
		this.imageX = imageX;
	}

	public int getImageY() {
		return imageY;
	}

	public void setImageY(int imageY) {
		this.imageY = imageY;
	}

	public void setImageXY(int x, int y)
	{
		imageX = x;
		imageY = y;
	}
	
	public final Point2D.Double getTrackLocation() {
		return location;
	}

	public void setLocation(Point2D.Double location) {
		this.location = location;
		calcShape(location);
	}

	public void setTrackLocationX(double x) {
		location.x = x;
		calcShape(location);
	}

	public void setTrackLocationY(double y) {
		location.y = y;
		calcShape(location);
	}

	public void setTrackLocation(double x, double y) {
		location.x = x;
		location.y = y;
		calcShape(location);
	}

	public boolean contains(Point2D.Double point)
	{
		return boundingRectangle.contains(point.x, point.y);
	}

	public void calcShape(Point2D.Double location)
	{
		this.location.x = location.x;
		this.location.y = location.y;
		
		if (points == null)
		{
			points = new Point3D[4]; // 4 points in 2D
			for (int i = 0; i < points.length; i++)
				points[i] = new Point3D();

			trPoints = new Point2D.Double[4];
			for (int i = 0; i < trPoints.length; i++)
				trPoints[i] = new Point2D.Double();
		}

		points[0].x = this.location.getX() - width / 2;
		points[0].y = this.location.getY() - height / 2;

		points[1].x = this.location.getX() + width / 2;
		points[1].y = this.location.getY() - height / 2;

		points[2].x = this.location.getX() + width / 2;
		points[2].y = this.location.getY() + height / 2;

		points[3].x = this.location.getX() - width / 2;
		points[3].y = this.location.getY() + height / 2;

		setBounds();
	}
	
	public void dump(String indent)
	{
		super.dump(indent);
		
		System.out.println(indent + "  ObjShapeObject");	
		System.out.println(indent + "    rgb         : " + String.format("0x%06X", rgb));
		System.out.println(indent + "    color       : " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue());
		System.out.println(indent + "    imageX      : " + imageX);
		System.out.println(indent + "    imageY      : " + imageY);
		System.out.println(indent + "    location    : " + location.x + ", " + location.y);
		System.out.println(indent + "    width       : " + width);
		System.out.println(indent + "    height      : " + height);
	}
}
