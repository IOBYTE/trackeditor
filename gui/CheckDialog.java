package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import utils.check.CheckMessage;
import utils.check.Subject;
import utils.check.TrackChecker;
import utils.circuit.Segment;

public class CheckDialog extends JDialog
{
	private JScrollPane			scrollPane		= null;
	// the messages, and what each is about
	private final DefaultListModel<CheckMessage>	messages	= new DefaultListModel<CheckMessage>();
	// all of them, also the ones about the objects' models that are hidden
	private final java.util.List<CheckMessage>	allMessages	= new java.util.ArrayList<CheckMessage>();
	// show/hide buttons: each says what clicking it does, so all look alike, on or off
	private final JButton		objectFilesButton	= showHideButton(false);
	private final JButton		pngButton			= showHideButton(false);
	private final JButton		savingButton		= showHideButton(false);
	// shown at first: a missing texture is a real problem, unless the data folder isn't set
	private final JButton		textureButton		= showHideButton(true);
	private final JList<CheckMessage>	list			= new JList<CheckMessage>(messages);
	private EditorFrame			editorFrame		= null;

	public CheckDialog(EditorFrame editorFrame)
	{
		// owned by the editor, so it stays in front of it while the track is edited
		super(editorFrame);
		this.editorFrame = editorFrame;
		// not blocked by the Properties dialog: a message can show its tab while it is open
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize()
	{
		this.setTitle("Check Track");
		this.setSize(editorFrame.getProject().getCheckDialogWidth(), editorFrame.getProject().getCheckDialogHeight());
		this.setResizable(true);
		Point p = new Point();
		p.x = editorFrame.getProject().getCheckDialogX();
		p.y = editorFrame.getProject().getCheckDialogY();
		this.setLocation(p);

		// looks like the read-only text area the dialog showed before: its font, colors
		// and line spacing
		JTextArea text = new JTextArea();
		text.setEditable(false);
		list.setFont(text.getFont());
		list.setForeground(text.getForeground());
		list.setBackground(text.getBackground());
		list.setSelectionForeground(text.getSelectedTextColor());
		list.setSelectionBackground(text.getSelectionColor());
		list.setFixedCellHeight(text.getFontMetrics(text.getFont()).getHeight());
		list.setCellRenderer(new MessageRenderer());
		// lines are copied as the text area's were: one or more selected (shift or
		// ctrl click), with ctrl C or the right-click menu
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setToolTipText("");
		JPopupMenu menu = new JPopupMenu();
		JMenuItem copyLines = new JMenuItem("Copy");
		copyLines.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
		copyLines.addActionListener(e -> copySelected());
		JMenuItem copyAll = new JMenuItem("Copy All");
		copyAll.addActionListener(e -> copy());
		menu.add(copyLines);
		menu.add(copyAll);
		list.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				popup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				popup(e);
			}

			/** shows the menu, for the line clicked on unless it is one of those selected */
			private void popup(MouseEvent e)
			{
				if (!e.isPopupTrigger())
					return;
				int index = list.locationToIndex(e.getPoint());
				if (index >= 0 && list.getCellBounds(index, index).contains(e.getPoint()) && !list.isSelectedIndex(index))
					list.setSelectedIndex(index);
				copyLines.setEnabled(!list.isSelectionEmpty());
				copyAll.setEnabled(!messages.isEmpty());
				menu.show(list, e.getX(), e.getY());
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e))
				{
					int index = list.locationToIndex(e.getPoint());
					if (index >= 0 && list.getCellBounds(index, index).contains(e.getPoint()))
						show(messages.get(index));
				}
			}
		});
		list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "show");
		// the lines as Copy All has them, with the system's line ends
		list.getActionMap().put("copy", new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				copySelected();
			}
		});
		list.getActionMap().put("show", new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (list.getSelectedValue() != null)
					show(list.getSelectedValue());
			}
		});

		scrollPane = new JScrollPane(list);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JButton recheck = new JButton("Recheck");
		recheck.addActionListener(e -> recheck());
		JButton copy = new JButton("Copy");
		copy.setToolTipText("Copies the messages shown as text");
		copy.addActionListener(e -> copy());
		// an object's model is often from elsewhere: its problems are hidden unless asked for
		objectFilesButton.setToolTipText("Shows or hides the messages about the objects' models: their AC3D files and textures");
		objectFilesButton.addActionListener(e -> showMessages());
		// the game reads .rgb textures too: converting them is only a suggestion
		pngButton.setToolTipText("Shows or hides the messages about .rgb textures that should be converted to png");
		pngButton.addActionListener(e -> showMessages());
		// keys the editor doesn't keep are often only left over from an older version
		savingButton.setToolTipText("Shows or hides messages about items in the file that saving will remove");
		savingButton.addActionListener(e -> showMessages());
		textureButton.setToolTipText("Shows or hides the messages about textures that aren't found");
		textureButton.addActionListener(e -> showMessages());
		JButton close = new JButton("Close");
		close.addActionListener(e -> close());
		JPanel buttons = new JPanel(new BorderLayout());
		// the show/hide buttons have a row of their own, so the rest fits beside Close
		JPanel shown = new JPanel(new FlowLayout(FlowLayout.LEFT));
		shown.add(objectFilesButton);
		shown.add(pngButton);
		shown.add(savingButton);
		shown.add(textureButton);
		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
		left.add(recheck);
		left.add(copy);
		left.add(new JLabel("Double-click a message to show what it is about, right-click to copy it"));
		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		right.add(close);
		buttons.add(shown, BorderLayout.NORTH);
		buttons.add(left, BorderLayout.CENTER);
		buttons.add(right, BorderLayout.EAST);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(buttons, BorderLayout.SOUTH);

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent e)
			{
				recheck();
			}
		});
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**
	 * Runs all the checks again, on the track as it is now: the dialog stays open
	 * while the track is edited.
	 */
	public void recheck()
	{
		messages.clear();
		allMessages.clear();
		// the same track (another one closes the dialog), maybe with other default
		// surfaces and objects or data folder since
		if (editorFrame.getTrackData() == null)
			return;
		allMessages.addAll(new TrackChecker(editorFrame).check());
		showMessages();
	}
	private void showMessages()
	{
		JButton[] buttons = { objectFilesButton, pngButton, savingButton, textureButton };
		String[] names = { "object messages", "convert to png messages", "saving removes messages", "texture not found messages" };
		int[] counts = new int[buttons.length];
		messages.clear();
		for (CheckMessage message : allMessages)
		{
			boolean[] kinds = { message.isObjectFile(), message.isPng(), message.isSaving(), message.isTexture() };
			// the kinds that hide the message
			int hiding = 0;
			int hidden = -1;
			for (int i = 0; i < kinds.length; i++)
			{
				if (kinds[i] && !buttons[i].isSelected())
				{
					hiding++;
					hidden = i;
				}
			}
			// a button counts what it adds: the messages only it hides, or that it shows
			for (int i = 0; i < kinds.length; i++)
			{
				if (kinds[i] && (hiding == 0 || (hiding == 1 && hidden == i)))
					counts[i]++;
			}
			if (hiding == 0)
				messages.addElement(message);
		}
		for (int i = 0; i < buttons.length; i++)
			showCount(buttons[i], names[i], counts[i]);
	}

	/**
	 * Names what a show/hide button does, with how many messages it shows or
	 * hides; with none it has nothing to do, shown or not
	 */
	private static void showCount(JButton button, String what, int count)
	{
		boolean shown = button.isSelected();
		button.setText((shown ? "Hide " : "Show ") + what + " (" + count + ")");
		button.setEnabled(count > 0);
	}

	/**
	 * @return a button that stays on or off like a toggle button, but isn't drawn
	 *         pressed while on: its text says whether it shows or hides
	 */
	private static JButton showHideButton(boolean shown)
	{
		JButton button = new JButton();
		button.setModel(new JToggleButton.ToggleButtonModel());
		button.setSelected(shown);
		return button;
	}

	private void show(CheckMessage message)
	{
		if (message.getSubject() == null)
			return;
		String problem = show(message.getSubject());
		if (problem != null)
			JOptionPane.showMessageDialog(this, problem, "Check Track", JOptionPane.INFORMATION_MESSAGE);
	}

	/** @return null once the subject is shown, or why it can't be */
	private String show(Subject subject)
	{
		switch (subject.getKind())
		{
		case SEGMENT:
		{
			Segment segment = subject.getSegment();
			// it blocks the track view and the segment dialog
			if (editorFrame.isPropertiesOpen())
				return "Close the Properties dialog to show segment " + segment.getName();
			return editorFrame.getCircuitView().showSegment(segment, subject.getSegmentTab()) ? null
				: "Segment " + segment.getName() + " isn't part of the track any more: Recheck to update the messages";
		}
		case PROPERTIES:
			editorFrame.showProperties(subject.getTabs());
			return null;
		case CALCULATE_CLOSURE:
			DeltaPanel.showAndCalculate(editorFrame);
			return null;
		case LAST_CLOSURE:
			DeltaPanel.showLastClosure(editorFrame);
			return null;
		default:
			return null;
		}
	}

	/** Copies the selected messages, one per line */
	private void copySelected()
	{
		StringBuilder text = new StringBuilder();
		for (CheckMessage message : list.getSelectedValuesList())
			text.append(message.getText()).append(System.lineSeparator());
		if (text.length() > 0)
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text.toString()), null);
	}

	/** Copies all the messages, one per line */
	private void copy()
	{
		StringBuilder text = new StringBuilder();
		for (int i = 0; i < messages.size(); i++)
			text.append(messages.get(i).getText()).append(System.lineSeparator());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text.toString()), null);
	}

	private void close()
	{
		storeBounds();
		dispose();
	}

	/** Messages that show something when double-clicked say so in their tool tip */
	private static class MessageRenderer extends DefaultListCellRenderer
	{
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus)
		{
			Component component = super.getListCellRendererComponent(list, value, index, selected, focus);
			// no room around the text, as in a text area
			((JComponent) component).setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
			boolean link = value instanceof CheckMessage && ((CheckMessage) value).getSubject() != null;
			((JComponent) component).setToolTipText(link ? "Double-click to show it" : null);
			return component;
		}
	}

	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
			storeBounds();
	}

	private void storeBounds()
	{
		editorFrame.getProject().setCheckDialogX(this.getX());
		editorFrame.getProject().setCheckDialogY(this.getY());
		editorFrame.getProject().setCheckDialogWidth(this.getWidth());
		editorFrame.getProject().setCheckDialogHeight(this.getHeight());
	}
}
