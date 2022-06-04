package utils.circuit;

public class Header {
	private String	name		= null;
	private String	category	= "road";
	private String	subcategory	= null;
	private int	version			= 4;
	private int skyVersion		= Integer.MAX_VALUE;
	private String	author		= null;
	private String	description	= null;

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

	public void dump(String indent)
    {
		System.out.println(indent + "Header");
		System.out.println(indent + "  name        : " + name);
		System.out.println(indent + "  category    : " + category);
		System.out.println(indent + "  subcategory : " + subcategory);
		System.out.println(indent + "  version     : " + version);
		System.out.println(indent + "  author      : " + author);
		System.out.println(indent + "  description : " + description);
    }
}
