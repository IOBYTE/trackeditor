package gui.properties;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.MutableDouble;
import utils.MutableInteger;
import utils.MutableString;

public class PropertyPanel extends JPanel
{
	private EditorFrame			editorFrame;
	
	protected EditorFrame getEditorFrame()
	{
		return editorFrame;
	}

	public PropertyPanel(EditorFrame editorFrame)
	{
		super();
		this.editorFrame = editorFrame;
	}

	// number fields remember their type and the text they were given, so that a typo can be
	// caught when Ok is pressed instead of silently removing the value
	private static final String	NUMBER_TYPE	= "trackeditor.numberType";
	private static final String	NUMBER_TEXT	= "trackeditor.numberText";

	private static void markNumber(JTextField field, Class<?> type)
	{
		field.putClientProperty(NUMBER_TYPE, type);
		field.putClientProperty(NUMBER_TEXT, field.getText());
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
		markNumber(field, Integer.class);
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
		markNumber(field, Double.class);
	}

	public void setHexTextField(JTextField field, int value)
	{
		setTextField(field, toHexString(value));
		markNumber(field, Integer.class);
	}

	private static boolean isNumber(String text, Class<?> type)
	{
		if (text == null || text.trim().isEmpty())
			return true;	// not set

		try
		{
			if (type == Integer.class)
			{
				Integer.decode(text.trim());
				return true;
			}
			return Double.isFinite(Double.parseDouble(text));
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	private static JTextField findBadNumber(Container container)
	{
		for (Component component : container.getComponents())
		{
			if (component instanceof JTextField)
			{
				JTextField field = (JTextField) component;
				Class<?> type = (Class<?>) field.getClientProperty(NUMBER_TYPE);
				String text = field.getText();
				if (type != null && field.isEnabled() &&
					!(text != null && text.equals(field.getClientProperty(NUMBER_TEXT))) &&
					!isNumber(text, type))
				{
					return field;
				}
			}
			else if (component instanceof Container)
			{
				JTextField field = findBadNumber((Container) component);
				if (field != null)
					return field;
			}
		}
		return null;
	}

	private static String labelFor(JTextField field)
	{
		// the panels place a label to the left of each field on the same row
		JLabel best = null;
		for (Component component : field.getParent().getComponents())
		{
			if (component instanceof JLabel && component.isVisible() &&
				Math.abs(component.getY() - field.getY()) < 5 && component.getX() < field.getX() &&
				(best == null || component.getX() > best.getX()))
			{
				best = (JLabel) component;
			}
		}
		if (best == null || best.getText() == null || best.getText().trim().isEmpty())
			return null;

		return best.getText().trim().replaceAll(":$", "");
	}

	/**
	 * Checks that every number field in container holds a number or nothing.
	 * Otherwise shows the bad field, tells the user and returns false.
	 */
	public static boolean checkNumbers(Component parent, Container container)
	{
		JTextField field = findBadNumber(container);
		if (field == null)
			return true;

		// bring the field into view, outer tabs first
		StringBuilder where = new StringBuilder();
		java.util.List<Component> path = new java.util.ArrayList<Component>();
		for (Component c = field; c != null && c != container; c = c.getParent())
			path.add(0, c);
		Container outer = container;
		for (Component c : path)
		{
			if (outer instanceof JTabbedPane)
			{
				JTabbedPane tabs = (JTabbedPane) outer;
				int index = tabs.indexOfComponent(c);
				if (index != -1)
				{
					tabs.setSelectedIndex(index);
					where.append(tabs.getTitleAt(index)).append(" > ");
				}
			}
			if (c instanceof Container)
				outer = (Container) c;
		}
		String label = labelFor(field);
		if (label != null)
			where.append(label);
		else if (where.length() > 0)
			where.setLength(where.length() - 3);

		Class<?> type = (Class<?>) field.getClientProperty(NUMBER_TYPE);
		JOptionPane.showMessageDialog(parent,
			(where.length() > 0 ? where + ": " : "") + "\"" + field.getText().trim() + "\" isn't " +
			(type == Integer.class ? "a whole number" : "a number") + ".\n\n" +
			"Fix it or clear the field to leave the value unset.",
			"Properties", JOptionPane.ERROR_MESSAGE);
		field.requestFocusInWindow();
		field.selectAll();
		return false;
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
		boolean hasNewValue = !(newValue == null || newValue.isEmpty() || newValue.equals("none") || newValue.equals(String.valueOf(Integer.MAX_VALUE)));
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
			int value = Integer.decode(newValue.trim());
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
			value = Integer.decode(string.trim());
		}
		catch (NumberFormatException e)
		{
		}

		return value;
	}

	protected String getString(String string)
	{
		if (string == null || string.equals("none") || string.isEmpty())
			return null;

		return string;
	}

	public String toNoneString(String string)
	{
		if (string == null || string.isEmpty())
			return "none";

		return string;
	}

	public String toHexString(int value)
	{
		if (value != Integer.MAX_VALUE)
			return String.format("0x%06X", value);

		return null;
	}



	protected void addLabel(JPanel panel, int index, JLabel label, String text, int length)
	{
		label.setText(text);
		label.setBounds(10, 10 + (index * 27), length, 23);
		panel.add(label);
	}

	protected void addLabel(JPanel panel, int index, JLabel label, String text, int offset, int length)
	{
		label.setText(text);
		label.setBounds(offset, 10 + (index * 27), length, 23);
		panel.add(label);
	}

	protected void addTextField(JPanel panel, int index, JTextField textField, String text, int offset, int length)
	{
		textField.setBounds(offset, 10 + (index * 27), length, 23);
		setTextField(textField, text);
		panel.add(textField);
	}

	protected void addTextField(JPanel panel, int index, JTextField textField, double value, int offset, int length)
	{
		textField.setBounds(offset, 10 + (index * 27), length, 23);
		setTextField(textField, value);
		panel.add(textField);
	}

	protected void addTextField(JPanel panel, int index, JTextField textField, int value, int offset, int length)
	{
		textField.setBounds(offset, 10 + (index * 27), length, 23);
		setTextField(textField, value);
		panel.add(textField);
	}
}
