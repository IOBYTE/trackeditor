package utils.circuit;

public class TurnMarks {
	private double				width			= Double.NaN;
	private double				height			= Double.NaN;
	private double				verticalSpace	= Double.NaN;
	private double				horizontalSpace	= Double.NaN;

	public void setDefault()
	{
		width = 1;
		height = 1;
		verticalSpace = 0;
		horizontalSpace	= 2;
	}

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
	 * @return Returns the height.
	 */
	public double getHeight()
	{
		return height;
	}
	/**
	 * @param height
	 *            The height to set.
	 */
	public void setHeight(double height)
	{
		this.height = height;
	}
	/**
	 * @return Returns the verticalSpace.
	 */
	public double getVerticalSpace()
	{
		return verticalSpace;
	}
	/**
	 * @param verticalSpace
	 *            The verticalSpace to set.
	 */
	public void setVerticalSpace(double verticalSpace)
	{
		this.verticalSpace = verticalSpace;
	}
	/**
	 * @return Returns the horizontalSpace.
	 */
	public double getHorizontalSpace()
	{
		return horizontalSpace;
	}
	/**
	 * @param horizontalSpace
	 *            The horizontalSpace to set.
	 */
	public void setHorizontalSpace(double horizontalSpace)
	{
		this.horizontalSpace = horizontalSpace;
	}

	public void dump(String indent)
    {
		System.out.println(indent + "Turn Marks");
		System.out.println(indent + "  width           : " + width);
		System.out.println(indent + "  height          : " + height);
		System.out.println(indent + "  verticalSpace   : " + verticalSpace);
		System.out.println(indent + "  horizontalSpace : " + horizontalSpace);
    }
}
