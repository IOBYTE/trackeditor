package utils.circuit;

public class Header {
	private String	name		= null;
	private String	shortName	= null;
	private String	category	= "road";
	private String	subcategory	= null;
	private String	carCategory	= null;
	private int		version		= 4;
	private int		skyVersion	= Integer.MAX_VALUE;
	private String	author		= null;
	private String	email		= null;
	private String	copyright	= null;
	private String	description	= null;
	private String	disabledPenalties	= null;	// Speed Dreams only: ';' separated list

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	public String getShortName()
	{
		return shortName;
	}
	public void setShortName(String shortName)
	{
		this.shortName = shortName;
	}

	/**
	 * @return Returns the category.
	 */
	public String getCategory()
	{
		return category;
	}
	/**
	 * @param category
	 *            The category to set.
	 */
	public void setCategory(String category)
	{
		this.category = category;
	}

	/**
	 * @return Returns the subcategory.
	 */
	public String getSubcategory()
	{
		return subcategory;
	}
	/**
	 * @param subcategory
	 *            The subcategory to set.
	 */
	public void setSubcategory(String subcategory)
	{
		this.subcategory = subcategory;
	}

	public String getCarCategory()
	{
		return carCategory;
	}
	public void setCarCategory(String carCategory)
	{
		this.carCategory = carCategory;
	}

	/**
	 * @return Returns the version.
	 */
	public int getVersion()
	{
		return version;
	}
	/**
	 * @param version
	 *            The version to set.
	 */
	public void setVersion(int version)
	{
		this.version = version;
	}

	/**
	 * @return Returns the skyVersion.
	 */
	public int getSkyVersion()
	{
		return skyVersion;
	}
	/**
	 * @param skyVersion
	 *            The skyVersion to set.
	 */
	public void setSkyVersion(int skyVersion)
	{
		this.skyVersion = skyVersion;
	}

	/**
	 * @return Returns the author.
	 */
	public String getAuthor()
	{
		return author;
	}
	/**
	 * @param author
	 *            The author to set.
	 */
	public void setAuthor(String author)
	{
		this.author = author;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription()
	{
		return description;
	}
	/**
	 * @param author
	 *            The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getCopyright()
	{
		return copyright;
	}

	public void setCopyright(String copyright)
	{
		this.copyright = copyright;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getDisabledPenalties()
	{
		return disabledPenalties;
	}

	public void setDisabledPenalties(String disabledPenalties)
	{
		this.disabledPenalties = disabledPenalties;
	}

	/**
	 * @return true if penalty (e.g. "slowdown") is in the disabled penalties list.
	 */
	public boolean hasDisabledPenalty(String penalty)
	{
		if (disabledPenalties == null)
			return false;

		for (String token : disabledPenalties.split(";"))
		{
			if (token.equals(penalty))
				return true;
		}

		return false;
	}

	/**
	 * Adds or removes one penalty, keeping any other entries in the list.
	 */
	public void setDisabledPenalty(String penalty, boolean disabled)
	{
		if (hasDisabledPenalty(penalty) == disabled)
			return;

		java.util.List<String> tokens = new java.util.ArrayList<String>();

		if (disabledPenalties != null)
		{
			for (String token : disabledPenalties.split(";"))
			{
				if (!token.isEmpty() && !token.equals(penalty))
					tokens.add(token);
			}
		}

		if (disabled)
			tokens.add(penalty);

		disabledPenalties = tokens.isEmpty() ? null : String.join(";", tokens);
	}

	public void dump(String indent)
    {
		System.out.println(indent + "Header");
		System.out.println(indent + "  name        : " + name);
		System.out.println(indent + "  shortName   : " + shortName);
		System.out.println(indent + "  category    : " + category);
		System.out.println(indent + "  subcategory : " + subcategory);
		System.out.println(indent + "  carCategory : " + carCategory);
		System.out.println(indent + "  version     : " + version);
		System.out.println(indent + "  skyVersion  : " + skyVersion);
		System.out.println(indent + "  author      : " + author);
		System.out.println(indent + "  email       : " + email);
		System.out.println(indent + "  copyright   : " + copyright);
		System.out.println(indent + "  description : " + description);
		System.out.println(indent + "  disabledPenalties : " + disabledPenalties);
    }
}
