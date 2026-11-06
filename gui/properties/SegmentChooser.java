/*
 *   SegmentChooser.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package gui.properties;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import utils.SegmentVector;

/**
 * A segment name typed or picked from the track's segments, around the text
 * field that holds it. A name that isn't a segment stays as it is: red, saying
 * so at the end of the field, and first in the list in red until a segment is
 * picked, with the most likely segment under it. The name the field had stays
 * in the list, to put it back. None is "none (required)" in gray, or a blank
 * line where none is allowed. Typing lists the segments with it in their name.
 */
public class SegmentChooser extends javax.swing.JComboBox<String>
{
	private static final String	NOT_A_SEGMENT	= " (not a segment)";
	private static final String	REQUIRED		= "none (required)";
	private static final String	BLANK			= " ";
	// none: a blank line, or "none (required)"
	private final String		none;
	private final JTextField	field;
	private final java.util.function.Supplier<SegmentVector>	segments;
	private final java.util.function.Supplier<String>			guess;
	private boolean				adjusting;
	// the name when the tab opened, to go back to even when it isn't a segment
	private final String		original;

	/**
	 * @param blank whether none is allowed: a blank line in the list
	 * @param guess the most likely segment for a name that isn't one, or null
	 */
	public SegmentChooser(JTextField field, boolean blank, java.util.function.Supplier<SegmentVector> segments,
		java.util.function.Supplier<String> guess)
	{
		this.none = blank ? BLANK : REQUIRED;
		this.field = field;
		this.segments = segments;
		this.guess = guess;
		this.original = field.getText().trim();
		// the field's font, as the other fields have, not the list's bold one
		setFont(field.getFont());
		setEditable(true);
		setMaximumRowCount(16);
		setEditor(new javax.swing.ComboBoxEditor()
		{
			public java.awt.Component getEditorComponent()
			{
				return field;
			}

			public void setItem(Object item)
			{
				if (adjusting || item == null)
					return;
				String text = value(item.toString());
				if (!text.equals(field.getText()))
					field.setText(text);
			}

			public Object getItem()
			{
				return field.getText();
			}

			public void selectAll()
			{
				field.selectAll();
			}

			public void addActionListener(java.awt.event.ActionListener l)
			{
				field.addActionListener(l);
			}

			public void removeActionListener(java.awt.event.ActionListener l)
			{
				field.removeActionListener(l);
			}
		});
		setRenderer(new javax.swing.DefaultListCellRenderer()
		{
			public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
				boolean selected, boolean focus)
			{
				java.awt.Component c = super.getListCellRendererComponent(list, value, index, selected, focus);
				String text = String.valueOf(value);
				// only the name that isn't a segment in red, whatever color the field has
				if (text.endsWith(NOT_A_SEGMENT))
					c.setForeground(java.awt.Color.RED);
				else if (text.equals(REQUIRED))
					c.setForeground(java.awt.Color.GRAY);
				else
					c.setForeground(selected ? list.getSelectionForeground() : UIManager.getColor("List.foreground"));
				c.setFont(field.getFont());
				return c;
			}
		});
		fill(false);
		mark();
		field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener()
		{
			public void insertUpdate(javax.swing.event.DocumentEvent e)
			{
				mark();
			}

			public void removeUpdate(javax.swing.event.DocumentEvent e)
			{
				mark();
			}

			public void changedUpdate(javax.swing.event.DocumentEvent e)
			{
				mark();
			}
		});
		addPopupMenuListener(new javax.swing.event.PopupMenuListener()
		{
			public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e)
			{
				if (!typing)
					fill(false);
			}

			public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e)
			{
			}

			public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e)
			{
			}
		});
		// typing lists the segments with what is typed in their name
		field.addKeyListener(new java.awt.event.KeyAdapter()
		{
			public void keyReleased(java.awt.event.KeyEvent e)
			{
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER || e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE
					|| e.getKeyCode() == java.awt.event.KeyEvent.VK_UP || e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN)
					return;
				typing = true;
				fill(true);
				if (field.isShowing())
				{
					hidePopup();
					if (getItemCount() > 0)
						showPopup();
				}
				typing = false;
			}
		});
	}

	private boolean	typing;

	/** A name that isn't a segment in red, saying so at the end of the field */
	private void mark()
	{
		String text = field.getText().trim();
		boolean bad = !text.isEmpty() && segments.get().getSegmentFromName(text) == null;
		field.setForeground(bad ? java.awt.Color.RED : UIManager.getColor("TextField.foreground"));
		NotASegmentBorder.show(field, bad);
	}

	/** @return the name an item stands for */
	private static String value(String item)
	{
		if (item.equals(REQUIRED) || item.equals(BLANK))
			return "";
		if (item.endsWith(NOT_A_SEGMENT))
			return item.substring(0, item.length() - NOT_A_SEGMENT.length());
		return item;
	}

	/**
	 * The segments, in the order of the lap, after the name when it isn't one
	 * (or none); only those with the typed text in their name while typing.
	 * The most likely one shows first in the list when the name isn't one.
	 */
	private void fill(boolean filter)
	{
		adjusting = true;
		try
		{
			String text = field.getText().trim();
			SegmentVector all = segments.get();
			javax.swing.DefaultComboBoxModel<String> model = new javax.swing.DefaultComboBoxModel<String>();
			boolean known = all.getSegmentFromName(text) != null;
			// none first (always, when it is allowed), a name that isn't a segment, and the
			// name there was, so it can be put back
			if (none == BLANK || text.isEmpty() || original.isEmpty())
				model.addElement(none);
			if (!known && !text.isEmpty())
				model.addElement(text + NOT_A_SEGMENT);
			if (!original.equals(text) && !original.isEmpty() && all.getSegmentFromName(original) == null)
				model.addElement(original + NOT_A_SEGMENT);
			String lower = text.toLowerCase();
			// for a name that isn't a segment, the lap from the most likely one, so it shows under it
			int from = 0;
			String likely = !filter && !known && !text.isEmpty() ? guess.get() : null;
			for (int i = 0; likely != null && i < all.size(); i++)
				if (likely.equals(all.get(i).getName()))
					from = i;
			for (int k = 0; k < all.size(); k++)
			{
				String name = all.get((from + k) % all.size()).getName();
				if (name == null || (filter && !lower.isEmpty() && !name.toLowerCase().contains(lower)))
					continue;
				model.addElement(name);
			}
			model.setSelectedItem(known ? text : text.isEmpty() ? none : text + NOT_A_SEGMENT);
			setModel(model);
			if (likely != null)
			{
				// the most likely segment ready to pick
				final String pick = likely;
				SwingUtilities.invokeLater(() -> {
					Object child = getUI().getAccessibleChild(this, 0);
					if (child instanceof javax.swing.plaf.basic.ComboPopup)
					{
						javax.swing.JList<?> list = ((javax.swing.plaf.basic.ComboPopup) child).getList();
						int i = ((javax.swing.DefaultComboBoxModel<String>) getModel()).getIndexOf(pick);
						if (i >= 0)
						{
							// the name that isn't one stays in sight, above it when it can
							list.ensureIndexIsVisible(0);
							list.setSelectedIndex(i);
							list.ensureIndexIsVisible(i);
						}
					}
				});
			}
		}
		finally
		{
			adjusting = false;
		}
	}

	/** "not a segment" at the right end of a field, after the name */
	private static class NotASegmentBorder extends javax.swing.border.AbstractBorder
	{
		private static final String	TEXT	= "not a segment";
		private static final String	KEY		= "notASegmentBorder";

		static void show(JTextField field, boolean on)
		{
			Object shown = field.getClientProperty(KEY);
			if (on == (shown != null))
				return;
			if (on)
			{
				javax.swing.border.Border old = field.getBorder();
				field.putClientProperty(KEY, old == null ? javax.swing.BorderFactory.createEmptyBorder() : old);
				field.setBorder(javax.swing.BorderFactory.createCompoundBorder(old, new NotASegmentBorder()));
			}
			else
			{
				field.setBorder((javax.swing.border.Border) shown);
				field.putClientProperty(KEY, null);
			}
			field.revalidate();
			field.repaint();
		}

		private static java.awt.Font font(java.awt.Component c)
		{
			return c.getFont().deriveFont(java.awt.Font.ITALIC);
		}

		@Override
		public java.awt.Insets getBorderInsets(java.awt.Component c, java.awt.Insets insets)
		{
			insets.set(0, 0, 0, c.getFontMetrics(font(c)).stringWidth(TEXT) + 6);
			return insets;
		}

		@Override
		public void paintBorder(java.awt.Component c, java.awt.Graphics g, int x, int y, int width, int height)
		{
			java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
			g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setFont(font(c));
			java.awt.FontMetrics metrics = g2.getFontMetrics();
			g2.setColor(new java.awt.Color(200, 60, 60));
			g2.drawString(TEXT, x + width - metrics.stringWidth(TEXT) - 3, y + (height - metrics.getHeight()) / 2 + metrics.getAscent());
			g2.dispose();
		}
	}
}
