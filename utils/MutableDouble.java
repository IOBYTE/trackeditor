package utils;

public class MutableDouble
{
	private double value;

	public MutableDouble()
	{
		this.value = Double.NaN;
	}

	public double getValue()
	{
		return value;
	}

	public void setValue(double value)
	{
		this.value = value;
	}
}
