package utils;

public class MutableString
{
	private String value;

	public MutableString()
	{
		this.value = null;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
}
