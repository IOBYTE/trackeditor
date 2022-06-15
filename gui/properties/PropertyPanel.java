package gui.properties;

import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.EditorFrame;

public class PropertyPanel extends JPanel
{
	public EditorFrame			frame;

	public PropertyPanel(EditorFrame frame)
	{
		super();
		this.frame = frame;
	}

	public void setTextField(JTextField field, int value)
	{
		if (value != Integer.MAX_VALUE)
		{
			field.setText(value + "");
		}
		else
		{
			field.setText(null);
		}
	}

	public void setTextField(JTextField field, double value)
	{
		if (!Double.isNaN(value))
		{
			field.setText(value + "");
		}
		else
		{
			field.setText(null);
		}
	}

	public void setTextField(JTextField field, String value)
	{
		if (value != null && !value.isEmpty())
		{
			field.setText(value);
		}
		else
		{
			field.setText(null);
		}
	}

	public boolean isDifferent(String newValue, String oldValue, String result)
	{
		if ((newValue == null || newValue.isEmpty() || newValue == "none" || newValue == String.valueOf(Integer.MAX_VALUE)) &&
			(oldValue == null || oldValue.isEmpty()))
		{
			return false;
		}

		if (newValue == null && !oldValue.isEmpty())
		{
			result = newValue;
			return true;
		}

		boolean different = !newValue.equals(oldValue);

		if (different)
		{
			if (newValue.equals("none") || newValue == String.valueOf(Integer.MAX_VALUE))
				result = null;
			else
				result = newValue;
		}

		return different;
	}

	public boolean isDifferent(String newValue, double oldValue, MutableDouble result)
	{
		boolean changed = false;
		try
		{
			double value = Double.parseDouble(newValue);
			if (value != oldValue)
			{
				result.setValue(value);
				changed = true;
			}
		} catch (NumberFormatException e)
		{
			if (!Double.isNaN(oldValue))
			{
				result.setValue(Double.NaN);
				changed = true;
			}
		}
		return changed;
	}

	public boolean isDifferent(String newValue, int oldValue, MutableInteger result)
	{
		boolean changed = false;
		try
		{
			int value = Integer.parseInt(newValue);
			if (value != oldValue)
			{
				result.setValue(value);
				changed = true;
			}
		} catch (NumberFormatException e)
		{
			if (oldValue != Integer.MAX_VALUE)
			{
				result.setValue(Integer.MAX_VALUE);
				changed = true;
			}
		}
		return changed;
	}

	protected double getDouble(String string)
	{
		if (string == null || string.isEmpty())
			return Double.NaN;

		double value = Double.NaN;

		try
		{
			value = Double.parseDouble(string);
		}
		catch (NumberFormatException e)
		{
		}

		return value;
	}

	protected int getInteger(String string)
	{
		if (string == null || string.isEmpty())
			return Integer.MAX_VALUE;

		int value = Integer.MAX_VALUE;

		try
		{
			value = Integer.decode(string);
		}
		catch (NumberFormatException e)
		{
		}

		return value;
	}

	protected String getString(String string)
	{
		if (string == "none")
			return null;

		return string;
	}

	public class MutableDouble
	{
		private double value;

		public MutableDouble(double value)
		{
			this.value = value;
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

	public class MutableInteger
	{
		private int value;

		public MutableInteger(int value)
		{
			this.value = value;
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
}
