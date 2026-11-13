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
import utils.GameType;
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

	/**
	 * @return a text field that shows a hint in gray while it's empty, such as the
	 *         value the game uses then; the hint is only shown, never written
	 */
	public static JTextField hintTextField(final String hint)
	{
		return hintTextField(() -> hint);
	}

	/**
	 * @return a text field that shows a hint in gray while it's empty, one that
	 *         can change (what the game uses can depend on another choice)
	 */
	public static JTextField hintTextField(final java.util.function.Supplier<String> hints)
	{
		return new JTextField()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(java.awt.Graphics g)
			{
				super.paintComponent(g);
				String hint = hints.get();
				if (hint == null || !getText().isEmpty())
					return;
				java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
				g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.setColor(Color.GRAY);
				g2.setFont(getFont());
				java.awt.Insets insets = getInsets();
				java.awt.FontMetrics metrics = g2.getFontMetrics();
				int y = insets.top + (getHeight() - insets.top - insets.bottom - metrics.getHeight()) / 2 + metrics.getAscent();
				g2.drawString(hint, insets.left + 2, y);
				g2.dispose();
			}
		};
	}

	/**
	 * @param name    a file name
	 * @param folders where a name is looked for, in order; null ones are skipped
	 * @return the file the name is: the name itself when it has a folder, or
	 *         else the first of the folders that has it; null when none has it
	 */
	public static java.io.File findFile(String name, String... folders)
	{
		return utils.check.TrackChecker.findFile(name, folders);
	}

	/**
	 * Shows a file name field in red while there is no such file, with a tool
	 * tip saying where it was looked for, and keeps it up to date as the name
	 * changes. An empty field is never red. When the file is found, the tool tip
	 * says where.
	 *
	 * @param field   the file name field
	 * @param where   where a name is looked for, for the tool tip, such as "in
	 *                the track's folder"
	 * @param folders where a name is looked for, in order; a null one isn't
	 *                known (the game's data folder isn't set), so a name that
	 *                could be there isn't red
	 */
	public static void showFileFound(final JTextField field, final String where, final String... folders)
	{
		showFileFound(field, where, null, folders);
	}

	/**
	 * As showFileFound, and a file whose name's format the game can't use, as
	 * problem says (null when it can), is shown in red too
	 */
	public static void showFileFound(final JTextField field, final String where,
		final java.util.function.Function<String, String> problem, final String... folders)
	{
		final Runnable show = () ->
		{
			String name = field.getText().trim();
			java.io.File file = name.isEmpty() ? null : findFile(name, folders);
			boolean unknown = java.util.Arrays.asList(folders).contains(null);
			boolean found = name.isEmpty() || file != null || unknown;
			String format = name.isEmpty() || problem == null ? null : problem.apply(name);
			field.setForeground(found && format == null ? javax.swing.UIManager.getColor("TextField.foreground") : Color.RED);
			if (name.isEmpty())
				field.setToolTipText(null);
			else if (format != null && found)
				field.setToolTipText(name + ": " + format);
			else if (file != null)
				field.setToolTipText("Found at " + file.getAbsolutePath());
			else if (unknown)
				field.setToolTipText(name + " isn't in the track's folder. Set the game's data folder in Preferences so the editor can look there too.");
			else
				field.setToolTipText((name + " wasn't found " + where).trim());
		};
		field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener()
		{
			public void insertUpdate(javax.swing.event.DocumentEvent e)
			{
				show.run();
			}

			public void removeUpdate(javax.swing.event.DocumentEvent e)
			{
				show.run();
			}

			public void changedUpdate(javax.swing.event.DocumentEvent e)
			{
				show.run();
			}
		});
		show.run();
	}

	/**
	 * Shows the name of a texture of the track's own, like an environment map
	 * or the background image, in red while there is no such texture, where the
	 * game looks for it: the track's folder, then the game's data/textures
	 * folder. Until the game's data folder is set, only the track's folder is
	 * looked in, as Check does.
	 */
	protected void showTextureFound(JTextField field)
	{
		String dataDirectory = getEditorFrame().getDataDirectory();
		String sep = System.getProperty("file.separator");
		String track = getEditorFrame().getTrackFoldersWords();
		if (dataDirectory == null || dataDirectory.isEmpty())
			showFileFound(field, "in " + track + " (set the game's data folder in Preferences to look in data/textures too)",
				textureFormat(), getEditorFrame().getTrackFolders());
		else
			showFileFound(field, "in " + track + " or the game's data/textures folder",
				textureFormat(), getEditorFrame().getTrackFolders(dataDirectory + sep + "data" + sep + "textures"));
	}

	/** @return why the game can't load a texture of a name's format, as Check reports */
	private java.util.function.Function<String, String> textureFormat()
	{
		GameType game = getEditorFrame().getGameType();
		return game::textureFormatProblem;
	}

	/**
	 * Shows an object's model (AC3D file) name field in red while there is no
	 * such file, where Check looks for it: the track's folder, then the game's
	 * data/objects folder. Until the game's data folder is set, only the track's
	 * folder is looked in, as Check does.
	 */
	protected void showObjectFileFound(JTextField field)
	{
		String dataDirectory = getEditorFrame().getDataDirectory();
		String sep = System.getProperty("file.separator");
		if (dataDirectory == null || dataDirectory.isEmpty())
			showFileFound(field, "in the track's folder (set the game's data folder in Preferences to look in data/objects too)",
				utils.Properties.getInstance().getPath());
		else
			showFileFound(field, "in the track's folder or the game's data/objects folder",
				utils.Properties.getInstance().getPath(), dataDirectory + sep + "data" + sep + "objects");
	}

	/**
	 * Shows a shared texture's name field, like a surface's texture, bump or
	 * raceline, in red while there is no such texture, where Check looks for
	 * it: the track's folder, then the game's data/textures folder, where most
	 * of them are. Until the game's data folder is set, a name isn't red, and
	 * its tool tip says why.
	 */
	protected void showSharedTextureFound(JTextField field)
	{
		String dataDirectory = getEditorFrame().getDataDirectory();
		String sep = System.getProperty("file.separator");
		showFileFound(field, "in " + getEditorFrame().getTrackFoldersWords() + " or the game's data/textures folder", textureFormat(),
			getEditorFrame().getTrackFolders(dataDirectory == null || dataDirectory.isEmpty() ? null : dataDirectory + sep + "data" + sep + "textures"));
	}

	/**
	 * Shows a light's texture name field in red while there is no such texture,
	 * where Check looks for it: the track's folder, the game's data folder (the
	 * game's tracks name them like data/textures/start-light-red.png), then
	 * the game's data/textures folder. Until the game's data folder is set, a
	 * name isn't red.
	 */
	protected void showLightTextureFound(JTextField field)
	{
		String dataDirectory = getEditorFrame().getDataDirectory();
		boolean unknown = dataDirectory == null || dataDirectory.isEmpty();
		String sep = System.getProperty("file.separator");
		showFileFound(field, "in " + getEditorFrame().getTrackFoldersWords() + " or the game's data folder", textureFormat(),
			getEditorFrame().getTrackFolders(unknown ? null : dataDirectory, unknown ? null : dataDirectory + sep + "data" + sep + "textures"));
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

	// a number field showing the text the file has for it, which the game reads as
	// another number (see showFileText)
	private static final String	FILE_TEXT	= "trackeditor.fileText";

	/**
	 * Shows the text the file has for the key instead of the number read from it, in
	 * red with why in its tool tip, while the field has the number the game reads
	 * (see TrackData.FileText), as the segment dialog does: typing replaces it, and
	 * Ok keeps the number while the field has the file's text, typed again too (see
	 * numberText).
	 *
	 * @param item the list's item the key is in (a light), null for none
	 * @param path the key's path in the item, or in the file when there is no item
	 *        (see TrackData.getFileText)
	 */
	protected void showFileText(final JTextField field, Object item, String path)
	{
		utils.TrackData trackData = getEditorFrame().getTrackData();
		final utils.TrackData.FileText fileText = trackData == null ? null : trackData.getFileText(item, path);
		String text = field.getText();
		if (fileText == null || text == null || text.trim().isEmpty() || Double.compare(getDouble(text.trim()), fileText.value) != 0)
			return;

		field.setText(fileText.text);
		// not a typo to catch on Ok (see checkNumbers)
		field.putClientProperty(NUMBER_TEXT, fileText.text);
		field.putClientProperty(FILE_TEXT, fileText);
		field.setCaretPosition(0);
		final Color foreground = field.getForeground();
		final String tip = field.getToolTipText();
		final String fileTip = "<html><body style='width: 300px'><font color=red>"
			+ fileText.message.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
			+ "</font><br>Type a value to replace it.</body></html>";
		field.setForeground(Color.RED);
		field.setToolTipText(fileTip);
		field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener()
		{
			public void insertUpdate(javax.swing.event.DocumentEvent e)
			{
				changed();
			}

			public void removeUpdate(javax.swing.event.DocumentEvent e)
			{
				changed();
			}

			public void changedUpdate(javax.swing.event.DocumentEvent e)
			{
				changed();
			}

			// red while it has the file's text, typed again too
			private void changed()
			{
				boolean shown = fileText.text.equals(field.getText());
				field.setForeground(shown ? Color.RED : foreground);
				field.setToolTipText(shown ? fileTip : tip);
			}
		});
	}

	/**
	 * @return the field's text, or the number the game reads while it shows the text
	 *         the file has (see showFileText)
	 */
	protected static String numberText(JTextField field)
	{
		Object shown = field.getClientProperty(FILE_TEXT);
		if (!(shown instanceof utils.TrackData.FileText) || !((utils.TrackData.FileText) shown).text.equals(field.getText()))
			return field.getText();
		double value = ((utils.TrackData.FileText) shown).value;
		// a whole number as one, for a whole number field
		return value == Math.rint(value) && Math.abs(value) < 1e15 ? String.valueOf((long) value) : String.valueOf(value);
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
				return "the name \"" + name + "\" is already used, and the game can't load a track with two of the same name";
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
	 * save, and the game can't load a track with two items of the same name.
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
			String message = checked.checkNames();
			if (message != null)
			{
				// only show the tab when there's something wrong in it
				String where = showComponent(container, (Component) checked);
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
	 * @return whether a choice is "none", or "none (what the game does then)": no
	 *         value in the track
	 */
	public static boolean isNone(Object item)
	{
		return item != null && (item.equals("none") || item.toString().startsWith("none ("));
	}

	/** @return the combo box's choice, or null for none */
	public static String chosenItem(javax.swing.JComboBox<?> comboBox)
	{
		Object item = comboBox.getSelectedItem();
		return item == null || isNone(item) ? null : item.toString();
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
				if (isNone(model.getElementAt(i)))
				{
					comboBox.setSelectedItem(model.getElementAt(i));
					break;
				}
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

	/**
	 * Asks which segments to use for references to segments that are missing or
	 * aren't segments (the game finds segments by name): for each, a list of the
	 * track's segments with a guess picked, or none.
	 *
	 * @param problems what's wrong with each reference
	 * @param guesses the segment to pick for each, or null for none
	 * @return the segment picked for each ("" for none), an empty array when
	 *         ignored, or null when cancelled
	 */
	public static String[] pickSegments(Component parent, String title, java.util.List<String> problems,
		java.util.List<String> guesses, utils.SegmentVector segments, boolean canIgnore)
	{
		JPanel panel = new JPanel(new java.awt.GridBagLayout());
		java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
		c.insets = new java.awt.Insets(3, 3, 3, 3);
		c.anchor = java.awt.GridBagConstraints.WEST;
		c.gridwidth = 2;
		panel.add(new JLabel("Pick the segments to use:"), c);
		c.gridwidth = 1;
		java.util.List<javax.swing.JComboBox<String>> choices = new java.util.ArrayList<javax.swing.JComboBox<String>>();
		for (int i = 0; i < problems.size(); i++)
		{
			javax.swing.JComboBox<String> choice = new javax.swing.JComboBox<String>();
			choice.addItem(NO_SEGMENT);
			for (int j = 0; j < segments.size(); j++)
				choice.addItem(segments.get(j).getName());
			choice.setSelectedItem(guesses.get(i) == null ? NO_SEGMENT : guesses.get(i));
			c.gridy = i + 1;
			c.gridx = 0;
			panel.add(new JLabel(problems.get(i)), c);
			c.gridx = 1;
			panel.add(choice, c);
			choices.add(choice);
		}
		String[] options = canIgnore ? new String[] { "Ok", "Ignore", "Cancel" } : new String[] { "Ok", "Cancel" };
		int answer = JOptionPane.showOptionDialog(parent, panel, title, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
			null, options, options[0]);
		if (answer < 0 || options[answer].equals("Cancel"))
			return null;
		if (options[answer].equals("Ignore"))
			return new String[0];
		String[] picks = new String[choices.size()];
		for (int i = 0; i < picks.length; i++)
		{
			Object picked = choices.get(i).getSelectedItem();
			picks[i] = picked == null || NO_SEGMENT.equals(picked) ? "" : picked.toString();
		}
		return picks;
	}

	private static final String	NO_SEGMENT	= "(none)";

	/**
	 * @return the segment name most like the name: the fewest letters to change,
	 *         ignoring case; null when none is like it
	 */
	public static String closestSegmentName(String name, utils.SegmentVector segments)
	{
		String best = null;
		int bestDistance = Integer.MAX_VALUE;
		String a = name.toLowerCase();
		for (int k = 0; k < segments.size(); k++)
		{
			String candidate = segments.get(k).getName();
			if (candidate == null)
				continue;
			String b = candidate.toLowerCase();
			int[] previous = new int[b.length() + 1], current = new int[b.length() + 1];
			for (int j = 0; j <= b.length(); j++)
				previous[j] = j;
			for (int i = 1; i <= a.length(); i++)
			{
				current[0] = i;
				for (int j = 1; j <= b.length(); j++)
					current[j] = Math.min(Math.min(current[j - 1] + 1, previous[j] + 1),
						previous[j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1));
				int[] swap = previous;
				previous = current;
				current = swap;
			}
			int distance = previous[b.length()];
			if (distance < bestDistance)
			{
				bestDistance = distance;
				best = candidate;
			}
		}
		// nothing like it: leave it to the user
		return bestDistance <= Math.max(2, name.length() / 2) ? best : null;
	}
}
