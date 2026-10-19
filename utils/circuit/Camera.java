package utils.circuit;

public final class Camera {
	private String name		= null;
	private String comment	= null;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

    public void dump(String indent)
    {
    	System.out.println(indent + "name        : "+name);
		System.out.println(indent + "  comment   : "+comment);
		System.out.println(indent + "  segment   : "+segment);
		System.out.println(indent + "  toRight   : "+toRight);
		System.out.println(indent + "  toLeft    : "+toStart);
		System.out.println(indent + "  height    : "+height);
		System.out.println(indent + "  fovStart  : "+fovStart);
		System.out.println(indent + "  fovEnd    : "+fovEnd);
    }
}
