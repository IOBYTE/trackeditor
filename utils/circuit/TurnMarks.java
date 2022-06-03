package utils.circuit;

public class TurnMarks {
	private double				turnMarksWidth			= 1;
	private double				turnMarksHeight			= 1;
	private double				turnMarksVerticalSpace	= 0;
	private double				turnMarksHorizontalSpace= 2;

	/**
	 * @return Returns the turnMarksWidth.
	 */
	public double getTurnMarksWidth()
	{
		return turnMarksWidth;
	}
	/**
	 * @param turnMarksWidth
	 *            The turnMarksWidth to set.
	 */
	public void setTurnMarksWidth(double turnMarksWidth)
	{
		this.turnMarksWidth = turnMarksWidth;
	}
	/**
	 * @return Returns the turnMarksHeight.
	 */
	public double getTurnMarksHeight()
	{
		return turnMarksHeight;
	}
	/**
	 * @param turnMarksHeight
	 *            The turnMarksHeight to set.
	 */
	public void setTurnMarksHeight(double turnMarksHeight)
	{
		this.turnMarksHeight = turnMarksHeight;
	}
	/**
	 * @return Returns the turnMarksVerticalSpace.
	 */
	public double getTurnMarksVerticalSpace()
	{
		return turnMarksVerticalSpace;
	}
	/**
	 * @param turnMarksVerticalSpace
	 *            The turnMarksVerticalSpace to set.
	 */
	public void setTurnMarksVerticalSpace(double turnMarksVerticalSpace)
	{
		this.turnMarksVerticalSpace = turnMarksVerticalSpace;
	}
	/**
	 * @return Returns the turnMarksHorizontalSpace.
	 */
	public double getTurnMarksHorizontalSpace()
	{
		return turnMarksHorizontalSpace;
	}
	/**
	 * @param turnMarksHorizontalSpace
	 *            The turnMarksHorizontalSpace to set.
	 */
	public void setTurnMarksHorizontalSpace(double turnMarksHorizontalSpace)
	{
		this.turnMarksHorizontalSpace = turnMarksHorizontalSpace;
	}

	public void dump()
    {
		System.out.println("turnMarksWidth           : " + turnMarksWidth);
		System.out.println("turnMarksHeight          : " + turnMarksHeight);
		System.out.println("turnMarksVerticalSpace   : " + turnMarksVerticalSpace);
		System.out.println("turnMarksHorizontalSpace : " + turnMarksHorizontalSpace);
    }
}
