package utils.circuit;

public class Pits {
	private String				side				= null;
	private String				entry				= null;
	private String				start				= null;
	private String				startBuildings		= null;
	private String				stopBuildings		= null;
	private int					maxPits				= Integer.MAX_VALUE;
	private String				end					= null;
	private String				exit				= null;
	private double				width				= Double.NaN;
	private double				length				= Double.NaN;
	private int					style				= Integer.MAX_VALUE;
	private int					indicator			= Integer.MAX_VALUE;
	private double				speedLimit			= Double.NaN;

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

	public void dump(String indent)
    {
		System.out.println(indent + "Pits");
		System.out.println(indent + "  side           : " + side);
		System.out.println(indent + "  entry          : " + entry);
		System.out.println(indent + "  start          : " + start);
		System.out.println(indent + "  startBuildings : " + startBuildings);
		System.out.println(indent + "  stopBuildings  : " + stopBuildings);
		System.out.println(indent + "  maxPits        : " + maxPits);
		System.out.println(indent + "  end            : " + end);
		System.out.println(indent + "  exit           : " + exit);
		System.out.println(indent + "  width          : " + width);
		System.out.println(indent + "  length         : " + length);
		System.out.println(indent + "  style          : " + style);
		System.out.println(indent + "  indicator      : " + indicator);
		System.out.println(indent + "  speedLimit     : " + speedLimit);
    }
}
