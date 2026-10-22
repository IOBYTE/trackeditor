package utils.circuit;

public class MainTrack
{
	public static final	double	DEFAULT_WIDTH				= 10;
	public static final	String	DEFAULT_SURFACE				= "asphalt2-lines";
	public static final	double	DEFAULT_PROFIL_STEPS_LENGTH	= 4;
	public static final	String  DEFAULT_PROFIL				= "spline";
	// what the game's track loader uses when the track doesn't set them
	public static final	double	GAME_DEFAULT_WIDTH			= 15;
	public static final	String	GAME_DEFAULT_SURFACE		= "asphalt";

	private double				width						= Double.NaN;
	private String				surface						= null;
	private double				racelineWidthscale			= Double.NaN;
	private double				racelineInt					= Double.NaN;
	private double				racelineExt					= Double.NaN;
	private String				profil						= null;
	private double				profilStepsLength			= Double.NaN;
	// version 5 only: where the track starts (bearing in degrees) and the
	// bounding box the game uses instead of the one it computes
	private double				startBearing				= Double.NaN;
	private double				startX						= Double.NaN;
	private double				startY						= Double.NaN;
	private double				startZ						= Double.NaN;
	private double				minimumX					= Double.NaN;
	private double				minimumY					= Double.NaN;
	private double				minimumZ					= Double.NaN;
	private double				extentX						= Double.NaN;
	private double				extentY						= Double.NaN;
	private double				extentZ						= Double.NaN;
	private Pits				pits						= new Pits();
	private SegmentSide			left						= new SegmentSide(false);
	private SegmentSide			right						= new SegmentSide(true);

	/**
	 * @return Returns the width.
	 */
	public double getWidth()
	{
		return width;
	}

	/** The width the game uses: 15 m when the track doesn't set one */
	public double getValidWidth()
	{
		return Double.isNaN(width) ? GAME_DEFAULT_WIDTH : width;
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

	public double getRacelineExt() {
		return racelineExt;
	}

	public void setRacelineExt(double racelineExt) {
		this.racelineExt = racelineExt;
	}

	public String getProfil() {
		return profil;
	}

	public void setProfil(String profil) {
		this.profil = profil;
	}

	public double getStartBearing()
	{
		return startBearing;
	}

	public void setStartBearing(double startBearing)
	{
		this.startBearing = startBearing;
	}

	public double getStartX()
	{
		return startX;
	}

	public void setStartX(double startX)
	{
		this.startX = startX;
	}

	public double getStartY()
	{
		return startY;
	}

	public void setStartY(double startY)
	{
		this.startY = startY;
	}

	public double getStartZ()
	{
		return startZ;
	}

	public void setStartZ(double startZ)
	{
		this.startZ = startZ;
	}

	public double getMinimumX()
	{
		return minimumX;
	}

	public void setMinimumX(double minimumX)
	{
		this.minimumX = minimumX;
	}

	public double getMinimumY()
	{
		return minimumY;
	}

	public void setMinimumY(double minimumY)
	{
		this.minimumY = minimumY;
	}

	public double getMinimumZ()
	{
		return minimumZ;
	}

	public void setMinimumZ(double minimumZ)
	{
		this.minimumZ = minimumZ;
	}

	public double getExtentX()
	{
		return extentX;
	}

	public void setExtentX(double extentX)
	{
		this.extentX = extentX;
	}

	public double getExtentY()
	{
		return extentY;
	}

	public void setExtentY(double extentY)
	{
		this.extentY = extentY;
	}

	public double getExtentZ()
	{
		return extentZ;
	}

	public void setExtentZ(double extentZ)
	{
		this.extentZ = extentZ;
	}

	public void dump(String indent)
    {
		System.out.println(indent + "MainTrack");
		System.out.println(indent + "  width              : " + width);
		System.out.println(indent + "  surface            : " + surface);
		System.out.println(indent + "  racelineWidthscale : " + racelineWidthscale);
		System.out.println(indent + "  racelineInt        : " + racelineInt);
		System.out.println(indent + "  racelineExt        : " + racelineExt);
		System.out.println(indent + "  profil             : " + profil);
		System.out.println(indent + "  profilStepsLength  : " + profilStepsLength);
		System.out.println(indent + "  startBearing       : " + startBearing);
		System.out.println(indent + "  startX             : " + startX);
		System.out.println(indent + "  startY             : " + startY);
		System.out.println(indent + "  startZ             : " + startZ);
		System.out.println(indent + "  minimumX           : " + minimumX);
		System.out.println(indent + "  minimumY           : " + minimumY);
		System.out.println(indent + "  minimumZ           : " + minimumZ);
		System.out.println(indent + "  extentX            : " + extentX);
		System.out.println(indent + "  extentY            : " + extentY);
		System.out.println(indent + "  extentZ            : " + extentZ);
		pits.dump(indent + "    ");
    }
}
