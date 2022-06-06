package utils.circuit;

public class MainTrack {
	private double				width				= 10;
	private String				surface				= "asphalt2-lines";
	private double				racelineWidthscale	= Double.NaN;
	private double				racelineInt			= Double.NaN;
	private double				racelinExt			= Double.NaN;
	private double				profileStepLength	= 4;
    private Pits				pits				= new Pits();

	private SegmentSide			left				= new SegmentSide();
	private SegmentSide			right				= new SegmentSide();

	/**
	 * @return Returns the width.
	 */
	public double getWidth()
	{
		return width;
	}
	/**
	 * @param width
	 *            The width to set.
	 */
	public void setWidth(double width)
	{
		this.width = width;
	}
	/**
	 * @return Returns the surface.
	 */
	public String getSurface()
	{
		return surface;
	}
	/**
	 * @param surface The surface to set.
	 */
	public void setSurface(String surface)
	{
		this.surface = surface;
	}
	/**
	 * @return Returns the profileStepLength.
	 */
	public double getProfileStepLength()
	{
		return profileStepLength;
	}
	/**
	 * @param profileStepLength The profileStepLength to set.
	 */
	public void setProfileStepLength(double profileStepLength)
	{
		this.profileStepLength = profileStepLength;
	}

    /**
     * @return Returns the pits.
     */
    public Pits getPits()
    {
        return pits;
    }
    /**
     * @param pits The pits to set.
     */
    public void setPits(Pits pits)
    {
        this.pits = pits;
    }

	/**
	 * @return Returns the left.
	 */
	public SegmentSide getLeft()
	{
		return left;
	}
	/**
	 * @param left The left to set.
	 */
	public void setLeft(SegmentSide left)
	{
		this.left = left;
	}
	/**
	 * @return Returns the right.
	 */
	public SegmentSide getRight()
	{
		return right;
	}
	/**
	 * @param right The right to set.
	 */
	public void setRight(SegmentSide right)
	{
		this.right = right;
	}

	public double getRacelineWidthscale() {
		return racelineWidthscale;
	}

	public void setRacelineWidthscale(double racelineWidthscale) {
		this.racelineWidthscale = racelineWidthscale;
	}

	public double getRacelineInt() {
		return racelineInt;
	}

	public void setRacelineInt(double racelineInt) {
		this.racelineInt = racelineInt;
	}

	public double getRacelinExt() {
		return racelinExt;
	}

	public void setRacelinExt(double racelinExt) {
		this.racelinExt = racelinExt;
	}

	public void dump(String indent)
    {
		System.out.println(indent + "MainTrack");
		System.out.println(indent + "  width              : " + width);
		System.out.println(indent + "  surface            : " + surface);
		System.out.println(indent + "  racelineWidthscale : " + racelineWidthscale);
		System.out.println(indent + "  racelineInt        : " + racelineInt);
		System.out.println(indent + "  racelinExt         : " + racelinExt);
		System.out.println(indent + "  profileStepLength  : " + profileStepLength);
		pits.dump(indent + "    ");
    }
}
