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
			field.setText("");
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
			field.setText("");
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
			field.setText("");
		}
	}
}
