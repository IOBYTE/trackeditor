package utils.circuit;

public final class TrackObject {
    private String name				= null;
    private String object			= null;
    private int color				= Integer.MAX_VALUE;
    private String orientationType	= null;
    private double orientation		= Double.NaN;
    private double deltaHeight		= Double.NaN;
    private double deltaVert		= Double.NaN;

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getObject() {
		return object;
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

	public void dump(String indent)
    {
		System.out.println(indent + "TrackObject");
		System.out.println(indent + "  name            : " + name);
		System.out.println(indent + "  object          : " + object);
		System.out.println(indent + "  color           : 0x" + Integer.toHexString(color).toUpperCase());
		System.out.println(indent + "  orientationType : " + orientationType);
		System.out.println(indent + "  orientation     : " + orientation);
		System.out.println(indent + "  deltaHeight     : " + deltaHeight);
		System.out.println(indent + "  deltaVert       : " + deltaVert);
    }
}
