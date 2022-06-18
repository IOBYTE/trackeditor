package gui.properties;

import javax.swing.JLabel;
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

	public boolean isDifferent(String newValue, String oldValue, MutableString result)
	{
		boolean hasNewValue = !(newValue == null || newValue.isEmpty() || newValue == "none" || newValue == String.valueOf(Integer.MAX_VALUE));
		boolean hasOldValue = !(oldValue == null || oldValue.isEmpty());

		if (!hasNewValue && !hasOldValue)
		{
			result.setValue(null);
			return false;
		}

		if (!hasNewValue && hasOldValue)
		{
			result.setValue(null);
			return true;
		}

		if (hasNewValue && !hasOldValue)
		{
			result.setValue(newValue);
			return true;
		}

		if (newValue.equals(oldValue))
		{
			result.setValue(null);
			return false;
		}

		result.setValue(newValue);
		return true;
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
			int value = Integer.decode(newValue);
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

	protected void addLabel(JPanel panel, int index, JLabel label, String text, int length)
	{
		label.setText(text);
		label.setBounds(10, 10 + (index * 25), length, 20);
		panel.add(label);
	}

	protected void addLabel(JPanel panel, int index, JLabel label, String text, int offset, int length)
	{
		label.setText(text);
		label.setBounds(offset, 10 + (index * 25), length, 20);
		panel.add(label);
	}

	protected void addTextField(JPanel panel, int index, JTextField textField, String text, int offset, int length)
	{
		textField.setBounds(offset, 10 + (index * 25), length, 20);
		setTextField(textField, text);
		panel.add(textField);
	}

	protected void addTextField(JPanel panel, int index, JTextField textField, double value, int offset, int length)
	{
		textField.setBounds(offset, 10 + (index * 25), length, 20);
		setTextField(textField, value);
		panel.add(textField);
	}

	protected void addTextField(JPanel panel, int index, JTextField textField, int value, int offset, int length)
	{
		textField.setBounds(offset, 10 + (index * 25), length, 20);
		setTextField(textField, value);
		panel.add(textField);
	}
}
