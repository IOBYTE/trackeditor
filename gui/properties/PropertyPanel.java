package gui.properties;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.function.IntSupplier;

import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

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
	 * Selects the tabs that hold component, outer tabs first, and returns where it
	 * is, like "Terrain > Reliefs > relief 1 > ".
	 */
	private static String showComponent(Container container, Component component)
	{
		StringBuilder where = new StringBuilder();
		java.util.List<Component> path = new java.util.ArrayList<Component>();
		for (Component c = component; c != null && c != container; c = c.getParent())
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
		return where.toString();
	}

	private static JTable findBadTableEdit(Container container)
	{
		for (Component component : container.getComponents())
		{
			if (component instanceof JTable)
			{
				JTable table = (JTable) component;
				// a cell still being edited: keep a good value, report a bad one
				if (table.isEditing() && !table.getCellEditor().stopCellEditing())
					return table;
			}
			else if (component instanceof Container)
			{
				JTable table = findBadTableEdit((Container) component);
				if (table != null)
					return table;
			}
		}
		return null;
	}

	/**
	 * Checks that every number field in container holds a number or nothing, and
	 * that no table cell is left holding a value it can't take.
	 * Otherwise shows the bad field, tells the user and returns false.
	 */
	public static boolean checkNumbers(Component parent, Container container)
	{
		JTable table = findBadTableEdit(container);
		if (table != null)
		{
			String where = showComponent(container, table);
			String problem = table.getCellEditor() instanceof NumberCellEditor ?
				((NumberCellEditor) table.getCellEditor()).problem() : "isn't a valid value";
			// the row number shown in the table's first column is the model row
			int row = table.convertRowIndexToModel(table.getEditingRow()) + 1;
			JOptionPane.showMessageDialog(parent,
				where + "row " + row + ", " + table.getColumnName(table.getEditingColumn()) + ": " + problem + ".\n\n" +
				"Fix it, or press Escape in the cell to keep the old value.",
				"Properties", JOptionPane.ERROR_MESSAGE);
			Component editor = table.getEditorComponent();
			if (editor != null)
				editor.requestFocusInWindow();
			return false;
		}

		JTextField field = findBadNumber(container);
		if (field == null)
			return true;

		StringBuilder where = new StringBuilder(showComponent(container, field));
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

	// list items (cameras, lights, surfaces...) are sections named after them, so their
	// names must be set, unique in the list and can't hold '/' (the parameter path separator)
	private static final String	ITEM_NAME	= "trackeditor.itemName";

	/** Marks field as the name of the list item shown in its tab */
	protected static void markItemName(JTextField field)
	{
		field.putClientProperty(ITEM_NAME, Boolean.TRUE);
	}

	/** Implemented by panels whose item names aren't in text fields (a table) */
	public interface NameChecked
	{
		/** A message about a missing or duplicate name, after showing it, or null */
		String checkNames();
	}

	private static JTextField findItemName(Container container)
	{
		for (Component component : container.getComponents())
		{
			if (component instanceof JTextField && ((JTextField) component).getClientProperty(ITEM_NAME) != null)
				return (JTextField) component;
			if (component instanceof Container && !(component instanceof JTabbedPane))
			{
				JTextField field = findItemName((Container) component);
				if (field != null)
					return field;
			}
		}
		return null;
	}

	private static java.util.List<JTextField> itemNames(JTabbedPane tabs)
	{
		java.util.List<JTextField> names = new java.util.ArrayList<JTextField>();
		for (int i = 0; i < tabs.getTabCount(); i++)
		{
			Component tab = tabs.getComponentAt(i);
			if (tab instanceof Container)
			{
				JTextField field = findItemName((Container) tab);
				if (field != null)
					names.add(field);
			}
		}
		return names;
	}

	/** A name for a new item: prefix followed by the first number no tab uses */
	protected static String uniqueName(JTabbedPane tabs, String prefix)
	{
		java.util.Set<String> used = new java.util.HashSet<String>();
		for (int i = 0; i < tabs.getTabCount(); i++)
			used.add(tabs.getTitleAt(i));
		for (JTextField field : itemNames(tabs))
			used.add(field.getText().trim());
		int number = tabs.getTabCount() + 1;
		while (used.contains(prefix + number))
			number++;
		return prefix + number;
	}

	private static String nameProblem(JTextField field, java.util.List<JTextField> names)
	{
		String name = field.getText();
		if (name == null || name.trim().isEmpty())
			return "needs a name";
		if (name.contains("/"))
			return "the name \"" + name + "\" can't contain /";
		// report the later one of two, usually the one just added or renamed
		for (JTextField other : names)
		{
			if (other == field)
				break;
			if (name.equals(other.getText()))
				return "the name \"" + name + "\" is already used, and the game would only keep one of them";
		}
		return null;
	}

	private static JTextField findBadName(Container container, String[] problem)
	{
		for (Component component : container.getComponents())
		{
			if (component instanceof JTabbedPane)
			{
				java.util.List<JTextField> names = itemNames((JTabbedPane) component);
				for (JTextField field : names)
				{
					problem[0] = nameProblem(field, names);
					if (problem[0] != null)
						return field;
				}
			}
			if (component instanceof Container)
			{
				JTextField field = findBadName((Container) component, problem);
				if (field != null)
					return field;
			}
		}
		return null;
	}

	private static NameChecked findNameChecked(Container container)
	{
		for (Component component : container.getComponents())
		{
			if (component instanceof NameChecked)
				return (NameChecked) component;
			if (component instanceof Container)
			{
				NameChecked checked = findNameChecked((Container) component);
				if (checked != null)
					return checked;
			}
		}
		return null;
	}

	/**
	 * Checks the names of list items: a missing name makes the track impossible to
	 * save, and of two items with one name the game keeps only one.
	 * Otherwise shows the bad name, tells the user and returns false.
	 */
	public static boolean checkNames(Component parent, Container container)
	{
		String[] problem = new String[1];
		JTextField field = findBadName(container, problem);
		if (field != null)
		{
			String where = showComponent(container, field);
			if (where.endsWith(" > "))
				where = where.substring(0, where.length() - 3);
			JOptionPane.showMessageDialog(parent, where + ": " + problem[0] + ".", "Properties", JOptionPane.ERROR_MESSAGE);
			field.requestFocusInWindow();
			field.selectAll();
			return false;
		}

		NameChecked checked = findNameChecked(container);
		if (checked != null)
		{
			String where = showComponent(container, (Component) checked);
			String message = checked.checkNames();
			if (message != null)
			{
				JOptionPane.showMessageDialog(parent, where + message + ".", "Properties", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return true;
	}

	/**
	 * Replaces the items of list with items (the items the tabs show, in tab order).
	 *
	 * @return true if that changed the list
	 */
	protected static <T> boolean replaceItems(java.util.List<T> list, java.util.List<T> items)
	{
		boolean same = list.size() == items.size();
		for (int i = 0; same && i < items.size(); i++)
			same = list.get(i) == items.get(i);
		if (same)
			return false;
		list.clear();
		list.addAll(items);
		return true;
	}

	/**
	 * Selects value in a combo box of fixed choices, adding it when the list doesn't have
	 * it, so a value from the track file isn't replaced by the first choice (and then
	 * removed from the track when Ok is pressed).
	 */
	public static void selectItem(javax.swing.JComboBox<String> comboBox, String value)
	{
		javax.swing.ComboBoxModel<String> model = comboBox.getModel();
		if (value == null || value.isEmpty())
		{
			// unset: "none" where the list has it, otherwise nothing selected
			comboBox.setSelectedItem(null);
			for (int i = 0; i < model.getSize(); i++)
				if ("none".equals(model.getElementAt(i)))
					comboBox.setSelectedItem("none");
			return;
		}
		boolean found = false;
		for (int i = 0; i < model.getSize() && !found; i++)
			found = value.equals(model.getElementAt(i));
		if (!found && model instanceof javax.swing.MutableComboBoxModel)
			((javax.swing.MutableComboBoxModel<String>) model).addElement(value);
		comboBox.setSelectedItem(value);
	}

	/**
	 * Table cell editor for numbers. Unlike the default one it refuses NaN and
	 * infinity, can refuse an empty cell, and can limit a pixel to the image size.
	 * A refused value gets a red border and a tool tip saying what is wrong.
	 */
	public static class NumberCellEditor extends DefaultCellEditor
	{
		private final boolean		whole;
		private final boolean		allowEmpty;
		private final IntSupplier	size;	// pixels must be from 0 to size - 1
		private Object				value;

		private NumberCellEditor(boolean whole, boolean allowEmpty, IntSupplier size)
		{
			super(new JTextField());
			this.whole = whole;
			this.allowEmpty = allowEmpty;
			this.size = size;
			((JTextField) getComponent()).setHorizontalAlignment(JTextField.RIGHT);
		}

		/** A decimal number; allowEmpty for a value that may be unset */
		public static NumberCellEditor decimal(boolean allowEmpty)
		{
			return new NumberCellEditor(false, allowEmpty, null);
		}

		/** A pixel position in an image that is size pixels wide or high */
		public static NumberCellEditor pixel(IntSupplier size)
		{
			return new NumberCellEditor(true, false, size);
		}

		/** Why the text can't be used, or null if it can (and the value is set) */
		public String problem()
		{
			String text = ((JTextField) getComponent()).getText();
			text = text == null ? "" : text.trim();
			if (text.isEmpty())
			{
				value = null;
				return allowEmpty ? null : "a value is needed";
			}
			try
			{
				if (whole)
				{
					int number = Integer.parseInt(text);
					if (size != null && (number < 0 || number >= size.getAsInt()))
					{
						return size.getAsInt() > 0 ?
							"\"" + text + "\" is outside the image, use 0 to " + (size.getAsInt() - 1) :
							"the image couldn't be read, so positions in it can't be changed";
					}
					value = number;
				}
				else
				{
					double number = Double.parseDouble(text);
					if (!Double.isFinite(number))
						return "\"" + text + "\" isn't a number";
					value = number;
				}
				return null;
			}
			catch (NumberFormatException e)
			{
				return "\"" + text + "\" isn't " + (whole ? "a whole number" : "a number");
			}
		}

		@Override
		public boolean stopCellEditing()
		{
			JTextField field = (JTextField) getComponent();
			String problem = problem();
			if (problem != null)
			{
				field.setBorder(new LineBorder(Color.RED));
				field.setToolTipText(problem);
				return false;
			}
			return super.stopCellEditing();
		}

		@Override
		public Object getCellEditorValue()
		{
			return value;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			JTextField field = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
			field.setBorder(new LineBorder(Color.BLACK));
			field.setToolTipText(null);
			field.setText(value == null ? "" : value.toString());
			this.value = value;
			return field;
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
