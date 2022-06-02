package utils.circuit;

public final class TrackObject {
    private String name				= null;
    private String object			= null;
    private int color				= Integer.MAX_VALUE;
    private String orientationType	= null;
    private double orientation		= Double.NaN;

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

	public void dump()
    {
      System.out.println("name              : "+name);
      System.out.println("  object          : "+object);
      System.out.println("  color           : 0x"+Integer.toHexString(color).toUpperCase());
      System.out.println("  orientationType : "+orientationType);
      System.out.println("  orientation     : "+orientation);
    }
}
