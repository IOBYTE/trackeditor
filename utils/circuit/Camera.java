package utils.circuit;

public class Camera {
	private String name		= null;
	private String segment	= null;
	private double toRight	= Double.NaN;
	private double toStart	= Double.NaN;
	private double height	= Double.NaN;
	private String fovStart	= null;
	private String fovEnd	= null;

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public double getToRight() {
		return toRight;
	}

	public void setToRight(double toRight) {
		this.toRight = toRight;
	}

	public double getToStart() {
		return toStart;
	}

	public void setToStart(double toStart) {
		this.toStart = toStart;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public String getFovStart() {
		return fovStart;
	}

	public void setFovStart(String fovStart) {
		this.fovStart = fovStart;
	}

	public String getFovEnd() {
		return fovEnd;
	}

	public void setFovEnd(String fovEnd) {
		this.fovEnd = fovEnd;
	}

    public void dump()
    {
      System.out.println("name        : "+name);
      System.out.println("  segment   : "+segment);
      System.out.println("  toRight   : "+toRight);
      System.out.println("  toLeft    : "+toStart);
      System.out.println("  height    : "+height);
      System.out.println("  fovStart  : "+fovStart);
      System.out.println("  fovEnd    : "+fovEnd);
    }

}
