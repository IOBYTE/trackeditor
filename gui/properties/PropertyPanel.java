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
}
