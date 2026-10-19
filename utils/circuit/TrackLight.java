package utils.circuit;

public final class TrackLight {

	public class Corner
	{
		public double x				= Double.NaN;
		public double y				= Double.NaN;
		public double z				= Double.NaN;
	}

	private String name				= null;
	private String role				= null;
	private Corner topLeft			= new Corner();
	private Corner bottomRight		= new Corner();
	private String textureOn		= null;
	private String textureOff		= null;
	private int index				= Integer.MAX_VALUE;
	private double red				= Double.NaN;
	private double green			= Double.NaN;
	private double blue				= Double.NaN;

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

	public Corner getTopLeft() {
		return topLeft;
	}

	public double getTopLeftX() {
		return topLeft.x;
	}

	public double getTopLeftY() {
		return topLeft.y;
	}

	public double getTopLeftZ() {
		return topLeft.z;
	}

	public void setTopLeftX(double x) {
		this.topLeft.x = x;
	}

	public void setTopLeftY(double y) {
		this.topLeft.y = y;
	}

	public void setTopLeftZ(double z) {
		this.topLeft.z = z;
	}

	public void setTopLeft(double x, double y, double z) {
		this.topLeft.x = x;
		this.topLeft.y = y;
		this.topLeft.z = z;
	}

	public void setTopLeft(Corner topLeft) {
		this.topLeft = topLeft;
	}

	public Corner getBottomRight() {
		return bottomRight;
	}

	public double getBottomRightX() {
		return bottomRight.x;
	}

	public double getBottomRightY() {
		return bottomRight.y;
	}

	public double getBottomRightZ() {
		return bottomRight.z;
	}

	public void setBottomRightX(double x) {
		this.bottomRight.x = x;
	}

	public void setBottomRightY(double y) {
		this.bottomRight.y = y;
	}

	public void setBottomRightZ(double z) {
		this.bottomRight.z = z;
	}

	public void setBottomRight(double x, double y, double z) {
		this.bottomRight.x = x;
		this.bottomRight.y = y;
		this.bottomRight.z = z;
	}

	public void setBottomRight(Corner bottomRight) {
		this.bottomRight = bottomRight;
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

	public void dump(String indent)
    {
		System.out.println(indent + "TrackLight");
		System.out.println(indent + "  name          : " + name);
		System.out.println(indent + "  role          : " + role);
		System.out.println(indent + "  topLeft       : " + topLeft.x + " " + topLeft.y + " " + topLeft.z);
		System.out.println(indent + "  bottomRight   : " + bottomRight.x + " " + bottomRight.y + " " + bottomRight.z);
		System.out.println(indent + "  textureOn     : " + textureOn);
		System.out.println(indent + "  textureOff    : " + textureOff);
		System.out.println(indent + "  index         : " + index);
		System.out.println(indent + "  red           : " + red);
		System.out.println(indent + "  green         : " + green);
		System.out.println(indent + "  blue          : " + blue);
    }
}
