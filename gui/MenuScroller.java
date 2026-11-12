/*
 *   MenuScroller.java
 *
 *    This file is part of the track editor.
 *
 *    The track editor is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    The track editor is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 */
package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * Fills a menu each time it opens, and when its items don't fit on the screen
 * shows as many as fit between a scroll up and a scroll down item: resting the
 * mouse on one scrolls, clicking it scrolls one item, and so does the mouse
 * wheel anywhere on the menu. The footer (like a separator and Clear History)
 * is always shown below.
 */
public class MenuScroller
{
	// how often an arrow scrolls while the mouse rests on it
	private static final int		SCROLL_DELAY	= 80;

	private final JMenu				menu;
	private final Supplier<List<JMenuItem>>	itemsSupplier;
	private final List<Component>	footer;
	private final ArrowItem			up				= new ArrowItem(true);
	private final ArrowItem			down			= new ArrowItem(false);
	private List<JMenuItem>			items			= new ArrayList<JMenuItem>();
	// the first item shown and how many are shown
	private int						first;
	private int						shown;

	/**
	 * @param menu   the menu, whose contents this makes
	 * @param items  makes the items each time the menu opens
	 * @param footer what is always below them
	 */
	public MenuScroller(JMenu menu, Supplier<List<JMenuItem>> items, Component... footer)
	{
		this.menu = menu;
		this.itemsSupplier = items;
		this.footer = java.util.Arrays.asList(footer);
		menu.addMenuListener(new MenuListener()
		{
			public void menuSelected(MenuEvent e)
			{
				fill();
			}

			public void menuDeselected(MenuEvent e)
			{
				up.stop();
				down.stop();
			}

			public void menuCanceled(MenuEvent e)
			{
				up.stop();
				down.stop();
			}
		});
		menu.getPopupMenu().addMouseWheelListener(e -> scroll(e.getWheelRotation()));
		fill();
	}

	/** Makes the items and shows the first ones */
	public void fill()
	{
		items = itemsSupplier.get();
		first = 0;
		shown = items.size();
		int itemHeight = items.isEmpty() ? 0 : items.get(0).getPreferredSize().height;
		if (itemHeight > 0)
		{
			int footerHeight = 0;
			for (Component component : footer)
				footerHeight += component.getPreferredSize().height;
			int room = screenHeight() - footerHeight - 2 * itemHeight;
			if (items.size() * itemHeight > room)
			{
				// the arrows take a row each
				room -= up.getPreferredSize().height + down.getPreferredSize().height;
				shown = Math.max(1, room / itemHeight);
			}
		}
		layout();
	}

	/** Scrolls by rows, down when more than 0 */
	private void scroll(int rows)
	{
		int to = Math.max(0, Math.min(items.size() - shown, first + rows));
		if (to == first)
			return;
		first = to;
		layout();
	}

	private boolean scrolls()
	{
		return shown < items.size();
	}

	private void layout()
	{
		menu.removeAll();
		boolean scrolls = scrolls();
		if (scrolls)
		{
			up.setEnabled(first > 0);
			menu.add(up);
		}
		for (int i = first; i < first + shown && i < items.size(); i++)
			menu.add(items.get(i));
		if (scrolls)
		{
			down.setEnabled(first + shown < items.size());
			menu.add(down);
		}
		for (Component component : footer)
			menu.getPopupMenu().add(component);
		if (menu.getPopupMenu().isVisible())
		{
			menu.getPopupMenu().revalidate();
			menu.getPopupMenu().repaint();
		}
	}

	/** @return the height of the screen the menu is on, without the task bar */
	private int screenHeight()
	{
		GraphicsConfiguration gc = menu.getGraphicsConfiguration();
		if (gc == null)
			return Toolkit.getDefaultToolkit().getScreenSize().height;
		Rectangle bounds = gc.getBounds();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
		return bounds.height - insets.top - insets.bottom;
	}

	/** An item that scrolls while the mouse rests on it, and one row when clicked, without closing the menu */
	private class ArrowItem extends JMenuItem
	{
		private final boolean	upwards;
		private final Timer		timer;

		ArrowItem(boolean upwards)
		{
			this.upwards = upwards;
			setIcon(new ArrowIcon(upwards));
			setHorizontalAlignment(SwingConstants.CENTER);
			setToolTipText(upwards ? "Scroll up" : "Scroll down");
			timer = new Timer(SCROLL_DELAY, e -> scroll(this.upwards ? -1 : 1));
			getModel().addChangeListener(e ->
			{
				if (getModel().isArmed() && isEnabled())
					timer.start();
				else
					timer.stop();
			});
		}

		void stop()
		{
			timer.stop();
		}

		@Override
		protected void processMouseEvent(MouseEvent e)
		{
			// a click scrolls instead of choosing an item, which would close the menu
			if (e.getID() == MouseEvent.MOUSE_RELEASED || e.getID() == MouseEvent.MOUSE_CLICKED)
			{
				if (e.getID() == MouseEvent.MOUSE_RELEASED && isEnabled())
					scroll(upwards ? -1 : 1);
				e.consume();
				return;
			}
			super.processMouseEvent(e);
		}

		@Override
		public Dimension getPreferredSize()
		{
			Dimension size = super.getPreferredSize();
			// as narrow as it can be: the menu is as wide as its widest item
			return new Dimension(Math.min(size.width, 40), size.height);
		}
	}

	/** A small triangle */
	private static class ArrowIcon implements Icon
	{
		private final boolean	upwards;

		ArrowIcon(boolean upwards)
		{
			this.upwards = upwards;
		}

		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			Color color = c.isEnabled() ? c.getForeground() : UIManager.getColor("MenuItem.disabledForeground");
			g.setColor(color != null ? color : Color.GRAY);
			int w = getIconWidth(), h = getIconHeight();
			int[] xs = { x, x + w / 2, x + w };
			int[] ys = upwards ? new int[] { y + h, y, y + h } : new int[] { y, y + h, y };
			g.fillPolygon(xs, ys, 3);
		}

		public int getIconWidth()
		{
			return 10;
		}

		public int getIconHeight()
		{
			return 5;
		}
	}
}
