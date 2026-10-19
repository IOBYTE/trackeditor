package utils.circuit;

public final class TrackObject {
    private String name				= null;
    private String object			= null;
    private String comment			= null;
    private int color				= Integer.MAX_VALUE;
    private String orientationType	= null;
    private double borderDistance	= Double.NaN;
    private double orientation		= Double.NaN;
    private double deltaHeight		= Double.NaN;
    private double deltaVert		= Double.NaN;
    private String scaleType		= null;
    private double scale			= Double.NaN;
    private double scaleMin			= Double.NaN;
    private double scaleMax			= Double.NaN;

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getObject() {
		return object;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public String getOrientationType() {
		return orientationType;
	}

	public void setOrientationType(String orientationType) {
		this.orientationType = orientationType;
	}

	public double getBorderDistance() {
		return borderDistance;
	}

	public void setBorderDistance(double borderDistance) {
		this.borderDistance = borderDistance;
	}

	public double getOrientation() {
		return orientation;
	}

	public void setOrientation(double orientation) {
		this.orientation = orientation;
	}

	public double getDeltaHeight() {
		return deltaHeight;
	}

	public void setDeltaHeight(double deltaHeight) {
		this.deltaHeight = deltaHeight;
	}

	public double getDeltaVert() {
		return deltaVert;
	}

	public void setDeltaVert(double deltaVert) {
		this.deltaVert = deltaVert;
	}

	public String getScaleType() {
		return scaleType;
	}

	public void setScaleType(String scaleType) {
		this.scaleType = scaleType;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public double getScaleMin() {
		return scaleMin;
	}

	public void setScaleMin(double scaleMin) {
		this.scaleMin = scaleMin;
	}

	public double getScaleMax() {
		return scaleMax;
	}

	public void setScaleMax(double scaleMax) {
		this.scaleMax = scaleMax;
	}

	public void dump(String indent)
    {
		System.out.println(indent + "TrackObject");
		System.out.println(indent + "  name            : " + name);
		System.out.println(indent + "  comment         : " + comment);
		System.out.println(indent + "  object          : " + object);
		System.out.println(indent + "  color           : " + String.format("0x%06X", color));
		System.out.println(indent + "  orientationType : " + orientationType);
		System.out.println(indent + "  borderDistance  : " + borderDistance);
		System.out.println(indent + "  orientation     : " + orientation);
		System.out.println(indent + "  deltaHeight     : " + deltaHeight);
		System.out.println(indent + "  deltaVert       : " + deltaVert);
		System.out.println(indent + "  scaleType       : " + scaleType);
		System.out.println(indent + "  scale           : " + scale);
		System.out.println(indent + "  scaleMin        : " + scaleMin);
		System.out.println(indent + "  scaleMax        : " + scaleMax);
    }
}
