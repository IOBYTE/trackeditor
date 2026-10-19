package utils;

public class DoubleValue
{
	public double 	value = Double.NaN;
	public String	units = null;

	public DoubleValue()
	{
	}
	public DoubleValue(double value)
	{
		this.value = value;
	}
	public DoubleValue(double value, String units)
	{
		this.value = value;
		this.units = units;
	}
	public double getValue()
	{
		return value;
	}
	public void setValue(double value)
	{
		this.value = value;
	}
	public String getUnits()
	{
		return units;
	}
	public void setUnits(String units)
	{
		this.units = units;
	}
}
