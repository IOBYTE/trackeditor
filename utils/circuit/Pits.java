package utils.circuit;

public class Pits {
	private String				side				= "right";
	private String				entry;
	private String				start;
	private String				startBuildings		= null;
	private String				stopBuildings		= null;
	private int					maxPits				= 0;
	private String				end;
	private String				exit;
	private double				width;
	private double				length;
	private int					style				= 1;
	private int					indicator;
	private double				speedLimit;

	/**
	 * @return Returns the side.
	 */
	public String getSide()
	{
		return side;
	}
	/**
	 * @param side
	 *            The side to set.
	 */
	public void setSide(String side)
	{
		this.side = side;
	}

	/**
	 * @return Returns the entry.
	 */
	public String getEntry()
	{
		return entry;
	}
	/**
	 * @param entry
	 *            The entry to set.
	 */
	public void setEntry(String entry)
	{
		this.entry = entry;
	}

	/**
	 * @return Returns the start.
	 */
	public String getStart()
	{
		return start;
	}
	/**
	 * @param start
	 *            The start to set.
	 */
	public void setStart(String start)
	{
		this.start = start;
	}

	/**
	 * @return Returns the startBuildings.
	 */
	public String getStartBuildings()
	{
		return startBuildings;
	}
	/**
	 * @param startBuildings
	 *            The startBuildings to set.
	 */
	public void setStartBuildings(String startBuildings)
	{
		this.startBuildings = startBuildings;
	}

	/**
	 * @return Returns the stopBuildings.
	 */
	public String getStopBuildings()
	{
		return stopBuildings;
	}
	/**
	 * @param stopBuildings
	 *            The stopBuildings to set.
	 */
	public void setStopBuildings(String stopBuildings)
	{
		this.stopBuildings = stopBuildings;
	}

	/**
	 * @return Returns the maxPits.
	 */
	public int getMaxPits()
	{
		return maxPits;
	}
	/**
	 * @param maxPits
	 *            The maxPits to set.
	 */
	public void setMaxPits(int maxPits)
	{
		this.maxPits = maxPits;
	}

	/**
	 * @return Returns the end.
	 */
	public String getEnd()
	{
		return end;
	}
	/**
	 * @param end
	 *            The end to set.
	 */
	public void setEnd(String end)
	{
		this.end = end;
	}

	/**
	 * @return Returns the exit.
	 */
	public String getExit()
	{
		return exit;
	}
	/**
	 * @param exit
	 *            The exit to set.
	 */
	public void setExit(String exit)
	{
		this.exit = exit;
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
	 * @return Returns the length.
	 */
	public double getLength()
	{
		return length;
	}
	/**
	 * @param length
	 *            The length to set.
	 */
	public void setLength(double length)
	{
		this.length = length;
	}

	/**
	 * @return Returns the style.
	 */
	public int getStyle()
	{
		return style;
	}
	/**
	 * @param style
	 *            The style to set.
	 */
	public void setStyle(int style)
	{
		this.style = style;
	}

	/**
	 * @return Returns the indicator.
	 */
	public int getIndicator()
	{
		return indicator;
	}
	/**
	 * @param indicator
	 *            The indicator to set.
	 */
	public void setIndicator(int indicator)
	{
		this.indicator = indicator;
	}

	/**
	 * @return Returns the speedLimit.
	 */
	public double getSpeedLimit()
	{
		return speedLimit;
	}
	/**
	 * @param speedLimit
	 *            The speedLimit to set.
	 */
	public void setSpeedLimit(double speedLimit)
	{
		this.speedLimit = speedLimit;
	}

	public void dump()
    {
		System.out.println("side           : " + side);
		System.out.println("entry          : " + entry);
		System.out.println("start          : " + start);
		System.out.println("startBuildings : " + startBuildings);
		System.out.println("stopBuildings  : " + stopBuildings);
		System.out.println("maxPits        : " + maxPits);
		System.out.println("end            : " + end);
		System.out.println("exit           : " + exit);
		System.out.println("width          : " + width);
		System.out.println("length         : " + length);
		System.out.println("style          : " + style);
		System.out.println("indicator      : " + indicator);
		System.out.println("speedLimit     : " + speedLimit);
    }
}
