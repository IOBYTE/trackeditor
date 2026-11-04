package utils.circuit;

import utils.DoubleValue;

public class Pits {
	/**
	 * The pit segments have to come in order going round the track from the
	 * entry: start, start buildings, stop buildings, end and exit; otherwise the
	 * pit lane goes the long way round, from the start to the end.
	 *
	 * @param buildings whether the game reads the start and stop buildings (Speed
	 *        Dreams and CSR)
	 * @return what's out of order, or null; names that are empty or aren't
	 *         segments are left out (they're reported on their own)
	 */
	public static String orderProblem(utils.SegmentVector segments, String entry, String start, String startBuildings,
		String stopBuildings, String end, String exit, boolean buildings)
	{
		int n = segments.size();
		int from = indexOf(segments, entry);
		if (n == 0 || from < 0)
			return null;
		String[] labels = { "Start", "Start Buildings", "Stop Buildings", "End", "Exit" };
		String[] names = { start, buildings ? startBuildings : null, buildings ? stopBuildings : null, end, exit };
		String previousLabel = "Entry", previousName = entry;
		int previous = 0;
		for (int i = 0; i < names.length; i++)
		{
			int index = indexOf(segments, names[i]);
			if (index < 0)
				continue;
			int along = (index - from + n) % n;
			if (along < previous)
				return String.format("The pit segments are out of order: going round the track from the entry %s, %s %s comes before %s %s. "
					+ "They have to come in this order: Entry, Start, %sEnd, Exit",
					entry, labels[i], names[i], previousLabel, previousName, buildings ? "Start Buildings, Stop Buildings, " : "");
			previous = along;
			previousLabel = labels[i];
			previousName = names[i];
		}
		return null;
	}

	private static int indexOf(utils.SegmentVector segments, String name)
	{
		if (name == null || name.trim().isEmpty())
			return -1;
		Segment segment = segments.getSegmentFromName(name.trim());
		return segment == null ? -1 : segments.indexOf(segment);
	}

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
	private DoubleValue			speedLimit			= null;
	
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

	/** The width the game uses: 5 m when the track doesn't set one */
	public double getValidWidth()
	{
		return Double.isNaN(width) ? 5.0 : width;
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
	public DoubleValue getSpeedLimit()
	{
		return speedLimit;
	}

	/**
	 * @param speedLimit
	 *            The speedLimit to set.
	 */
	public void setSpeedLimit(DoubleValue speedLimit)
	{
		this.speedLimit = speedLimit;
	}

	public double getSpeedLimitValue()
	{
		if (speedLimit == null)
		{
			return Double.NaN;
		}
		return speedLimit.value;
	}

	public void setSpeedLimitValue(double speedLimit)
	{
		if (this.speedLimit == null)
		{
			if (Double.isNaN(speedLimit))
			{
				return;
			}
			this.speedLimit = new DoubleValue();
		}
		this.speedLimit.value = speedLimit;
	}

	public String getSpeedLimitUnits()
	{
		if (speedLimit == null)
		{
			return null;
		}
		return speedLimit.units;
	}

	public void setSpeedLimitUnits(String units)
	{
		if (this.speedLimit == null)
		{
			if (units == null)
			{
				return;
			}
			
			this.speedLimit = new DoubleValue();
		}		
		this.speedLimit.units = units;
	}

	/**
	 * The pit generator's settings for one part: the entry, the pits or the exit.
	 */
	public static class GeneratorPart
	{
		private String	surface			= null;
		private String	borderType		= null;
		private String	borderSurface	= null;
		private double	borderWidth		= Double.NaN;
		private double	borderHeight	= Double.NaN;

		public GeneratorPart copy()
		{
			GeneratorPart part = new GeneratorPart();
			part.surface = surface;
			part.borderType = borderType;
			part.borderSurface = borderSurface;
			part.borderWidth = borderWidth;
			part.borderHeight = borderHeight;
			return part;
		}

		public String getSurface()
		{
			return surface;
		}
		public void setSurface(String surface)
		{
			this.surface = surface;
		}
		public String getBorderType()
		{
			return borderType;
		}
		public void setBorderType(String borderType)
		{
			this.borderType = borderType;
		}
		public String getBorderSurface()
		{
			return borderSurface;
		}
		public void setBorderSurface(String borderSurface)
		{
			this.borderSurface = borderSurface;
		}
		public double getBorderWidth()
		{
			return borderWidth;
		}
		public void setBorderWidth(double borderWidth)
		{
			this.borderWidth = borderWidth;
		}
		public double getBorderHeight()
		{
			return borderHeight;
		}
		public void setBorderHeight(double borderHeight)
		{
			this.borderHeight = borderHeight;
		}
	}

	/**
	 * The pit generator's settings: one part each for the entry, the pits and
	 * the exit. A bean, so the project file keeps them in a section of their own.
	 */
	public static class Generator
	{
		private GeneratorPart	entry	= null;
		private GeneratorPart	pits	= null;
		private GeneratorPart	exit	= null;
		// a key of the pit segments as the generator left them, to tell when
		// they were changed since; null when not known
		private Integer			segmentsKey	= null;

		public Generator()
		{
		}

		public Generator(GeneratorPart entry, GeneratorPart pits, GeneratorPart exit)
		{
			this.entry = entry;
			this.pits = pits;
			this.exit = exit;
		}

		/** @return true when it has all three parts */
		public boolean isComplete()
		{
			return entry != null && pits != null && exit != null;
		}

		public Generator copy()
		{
			Generator copy = new Generator(entry == null ? null : entry.copy(),
				pits == null ? null : pits.copy(), exit == null ? null : exit.copy());
			copy.segmentsKey = segmentsKey;
			return copy;
		}

		public Integer getSegmentsKey()
		{
			return segmentsKey;
		}
		public void setSegmentsKey(Integer segmentsKey)
		{
			this.segmentsKey = segmentsKey;
		}

		public GeneratorPart getEntry()
		{
			return entry;
		}
		public void setEntry(GeneratorPart entry)
		{
			this.entry = entry;
		}
		public GeneratorPart getPits()
		{
			return pits;
		}
		public void setPits(GeneratorPart pits)
		{
			this.pits = pits;
		}
		public GeneratorPart getExit()
		{
			return exit;
		}
		public void setExit(GeneratorPart exit)
		{
			this.exit = exit;
		}
	}

	// the pit generator's settings, kept with the pits so undo and redo bring
	// them back; the games don't read them, so they aren't written to the track
	// file, but the project file keeps them
	private Generator			generator			= null;

	/**
	 * @return the pit generator's last settings, or null
	 */
	public Generator getGenerator()
	{
		return generator;
	}

	public void setGenerator(Generator generator)
	{
		this.generator = generator;
	}

	/** Copies the pit generator's settings from other pits */
	public void copyGeneratorFrom(Pits other)
	{
		generator = other.generator == null ? null : other.generator.copy();
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
		System.out.println(indent + "  speedLimit     : " + (speedLimit == null ? "null" : speedLimit.value + " " + (speedLimit.units != null ? speedLimit.units : "")));
    }
}
