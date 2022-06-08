package utils.circuit;

public class MainTrack
{
	public static final	double	DEFAULT_WIDTH				= 10;
	public static final	String	DEFAULT_SURFACE				= "asphalt2-lines";
	public static final	double	DEFAULT_PROFIL_STEPS_LENGTH	= 4;

	private double				width						= 10;
	private String				surface						= null;
	private double				racelineWidthscale			= Double.NaN;
	private double				racelineInt					= Double.NaN;
	private double				racelinExt					= Double.NaN;
	private double				profilStepsLength			= Double.NaN;
	private Pits				pits						= new Pits();
	private SegmentSide			left						= new SegmentSide();
	private SegmentSide			right						= new SegmentSide();

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
	 * @return Returns the profilStepsLength.
	 */
	public double getProfilStepsLength()
	{
		return profilStepsLength;
	}
	/**
	 * @param profilStepsLength The profilStepsLength to set.
	 */
	public void setProfilStepsLength(double profilStepsLength)
	{
		this.profilStepsLength = profilStepsLength;
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
		System.out.println(indent + "  profilStepsLength  : " + profilStepsLength);
		pits.dump(indent + "    ");
    }
}
