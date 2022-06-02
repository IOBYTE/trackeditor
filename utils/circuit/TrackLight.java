package utils.circuit;

import java.awt.geom.Point2D;
//import javafx.geometry.Point3D;

public final class TrackLight {
	private String name						= null;
	private String role						= null;
//	private Point3D.Double	topLeft			= null;
//	private Point3D.Double	bottomRight		= null;
	private double x						= Double.NaN;
	private double y						= Double.NaN;
	private double z						= Double.NaN;
	private String textureOn				= null;
	private String textureOff				= null;
	private int index						= Integer.MAX_VALUE;
	private double red						= Double.NaN;
	private double green					= Double.NaN;
	private double blue						= Double.NaN;

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getTextureOn() {
		return textureOn;
	}

	public void setTextureOn(String textureOn) {
		this.textureOn = textureOn;
	}

	public String getTextureOff() {
		return textureOff;
	}

//	public Point3D.Double getTopLeft() {
//		return topLeft;
//	}

//	public void setTopLeft(Point3D.Double topLeft) {
//		this.topLeft = topLeft;
//	}

//	public Point3D.Double getBottomRight() {
//		return bottomRight;
//	}

//	public void setBottomRight(Point3D.Double bottomRight) {
//		this.bottomRight = bottomRight;
//	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getRed() {
		return red;
	}

	public void setRed(double red) {
		this.red = red;
	}

	public double getGreen() {
		return green;
	}

	public void setGreen(double green) {
		this.green = green;
	}

	public double getBlue() {
		return blue;
	}

	public void setBlue(double blue) {
		this.blue = blue;
	}

	public void setTextureOff(String textureOff) {
		this.textureOff = textureOff;
	}
}
