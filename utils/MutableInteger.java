package utils;

public class MutableInteger
{
	private int value;

	public MutableInteger()
	{
		this.value = Integer.MAX_VALUE;
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}
}
