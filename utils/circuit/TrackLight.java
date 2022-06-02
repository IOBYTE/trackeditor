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
	private Corner topLeft			= null;
	private Corner bottomRight		= null;
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

	public void setTopLeft(double x, double y, double z) {
		this.topLeft = new Corner();

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

	public void setBottomRight(double x, double y, double z) {
		this.bottomRight = new Corner();

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

	public void dump()
    {
		System.out.println("name            : " + name);
		System.out.println("  role          : " + role);
		if (topLeft != null)
			System.out.println("  topLeft       : " + topLeft.x + " " + topLeft.y + " " + topLeft.z);
		else
			System.out.println("  topLeft       : null");
		if (bottomRight != null)
			System.out.println("  bottomRight   : " + bottomRight.x + " " + bottomRight.y + " " + bottomRight.z);
		else
			System.out.println("  bottomRight   : null");
		System.out.println("  textureOn     : " + textureOn);
		System.out.println("  textureOff    : " + textureOff);
		System.out.println("  index         : " + index);
		System.out.println("  red           : " + red);
		System.out.println("  green         : " + green);
		System.out.println("  blue          : " + blue);
    }
}
