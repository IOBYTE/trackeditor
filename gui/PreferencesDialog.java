package gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class PreferencesDialog extends JDialog
{
	public static boolean		APPROVE						= false;
	private EditorFrame			editorFrame;
	private JPanel				jPanel						= null;
	private JLabel				dataDirectoryLabel			= null;
	private JTextField			dataDirectoryTextField		= null;
	private JButton				dataDirectoryButton			= null;
	private JLabel				binDirectoryLabel			= null;
	private JTextField			binDirectoryTextField		= null;
	private JButton				binDirectoryButton			= null;
	private JLabel				libDirectoryLabel			= null;
	private JTextField			libDirectoryTextField		= null;
	private JButton				libDirectoryButton			= null;
	private JLabel				recentFilesMaxLabel			= null;
	private JTextField			recentFilesMaxTextField		= null;
	private JCheckBox			interactiveFixesCheckBox	= null;
	private JCheckBox			cursorCoordinatesCheckBox	= null;
	private JCheckBox			cursorNamesCheckBox			= null;
	private JCheckBox			checkDefaultObjectsCheckBox	= null;
	private JCheckBox			speedDreamsAccCheckBox		= null;
	private JLabel				gameTypeLabel				= null;
	private JComboBox<EditorFrame.GameType>	gameTypeComboBox	= null;
	private JButton				okButton					= null;
	private JButton				cancelButton				= null;
	// each game's data, bin and lib directories, as edited in this dialog
	private java.util.EnumMap<EditorFrame.GameType, String[]>	directories	=
		new java.util.EnumMap<EditorFrame.GameType, String[]>(EditorFrame.GameType.class);

	public PreferencesDialog(EditorFrame editorFrame)
	{
		super();
		this.editorFrame = editorFrame;
		for (EditorFrame.GameType game : EditorFrame.GameType.values())
		{
			directories.put(game, new String[] {
				editorFrame.getDirectory(game, EditorFrame.DATA_DIRECTORY),
				editorFrame.getDirectory(game, EditorFrame.BIN_DIRECTORY),
				editorFrame.getDirectory(game, EditorFrame.LIB_DIRECTORY) });
		}
		initialize();
	}
	
	// the fields start right after the widest label, and the directory fields are wide
	// enough to show a whole path
	private static final int	LABEL_X			= 10;
	private static final int	GAP				= 10;
	private static final int	FIELD_WIDTH		= 560;
	private static final int	BUTTON_WIDTH	= 80;
	private int					fieldX			= 200;

	private void initialize()
	{
		this.setContentPane(getJPanel());
		// the panel's width plus the dialog's borders
		this.setSize(fieldX + FIELD_WIDTH + 5 + BUTTON_WIDTH + GAP + 16, 356);
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		Point p = new Point();
		p.x = editorFrame.getProject().getPreferencesDialogX();
		p.y = editorFrame.getProject().getPreferencesDialogY();
		if (p.x == 0 && p.y == 0)
		{
			setLocationRelativeTo(getParent());
		}
		else
		{
			this.setLocation(p);
		}
		this.setTitle("Preferences Dialog");
	}
	
	private JPanel getJPanel()
	{
		if (jPanel == null)
		{
			dataDirectoryLabel = new JLabel();
			dataDirectoryLabel.setBounds(10, 10, 190, 23);
			dataDirectoryLabel.setText("Speed Dreams Data Directory");
			binDirectoryLabel = new JLabel();
			binDirectoryLabel.setBounds(10, 43, 190, 23);
			binDirectoryLabel.setText("Speed Dreams Bin Directory");
			libDirectoryLabel = new JLabel();
			libDirectoryLabel.setBounds(10, 76, 190, 23);
			libDirectoryLabel.setText("Speed Dreams Lib Directory");
			recentFilesMaxLabel = new JLabel();
			recentFilesMaxLabel.setBounds(10, 109, 190, 23);
			recentFilesMaxLabel.setText("Recent Files Maximum");
			gameTypeLabel = new JLabel();
			gameTypeLabel.setBounds(10, 227, 190, 23);
			gameTypeLabel.setText("Game");
			layOutLabels();
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.add(dataDirectoryLabel, null);
			jPanel.add(binDirectoryLabel, null);
			jPanel.add(libDirectoryLabel, null);
			jPanel.add(recentFilesMaxLabel, null);
			jPanel.add(gameTypeLabel, null);
			jPanel.add(getDataDirectoryTextField(), null);
			jPanel.add(getDataDirectoryButton(), null);
			jPanel.add(getBinDirectoryTextField(), null);
			jPanel.add(getBinDirectoryButton(), null);
			jPanel.add(getLibDirectoryTextField(), null);
			jPanel.add(getLibDirectoryButton(), null);
			jPanel.add(getRecentFilesMaxTextField(), null);
			jPanel.add(getInteractiveFixesCheckBox(), null);
			jPanel.add(getCursorCoordinatesCheckBox(), null);
			jPanel.add(getCursorNamesCheckBox(), null);
			jPanel.add(getCheckDefaultObjectsCheckBox(), null);
			jPanel.add(getGameTypeComboBox(), null);
			jPanel.add(getSpeedDreamsAccCheckBox(), null);
			jPanel.add(getOkButton(), null);
			jPanel.add(getCancelButton(), null);
			updateGameLabels();
		}
		return jPanel;
	}

	/**
	 * Makes the labels as wide as the widest one can be (with any game's name),
	 * and puts the fields right after them.
	 */
	private void layOutLabels()
	{
		int width = Math.max(recentFilesMaxLabel.getPreferredSize().width, gameTypeLabel.getPreferredSize().width);
		// the directory labels start with the game's name, so measure them with each one
		for (EditorFrame.GameType game : EditorFrame.GameType.values())
		{
			for (String kind : new String[] { " Data Directory", " Bin Directory", " Lib Directory" })
			{
				JLabel test = new JLabel(game.getShortName() + kind);
				test.setFont(dataDirectoryLabel.getFont());
				width = Math.max(width, test.getPreferredSize().width);
			}
		}

		JLabel[] labels = { dataDirectoryLabel, binDirectoryLabel, libDirectoryLabel, recentFilesMaxLabel, gameTypeLabel };

		for (JLabel label : labels)
			label.setSize(width, label.getHeight());

		fieldX = LABEL_X + width + GAP;
	}

	/** The game the directories are for, as chosen in this dialog */
	private String gameName()
	{
		return getGameType().getName();
	}

	private void updateGameLabels()
	{
		// CSR is abbreviated, so the labels don't take up much of the width
		String game = getGameType().getShortName();
		dataDirectoryLabel.setText(game + " Data Directory");
		binDirectoryLabel.setText(game + " Bin Directory");
		libDirectoryLabel.setText(game + " Lib Directory");
	}

	private JTextField getDataDirectoryTextField()
	{
		if (dataDirectoryTextField == null)
		{
			dataDirectoryTextField = new JTextField();
			dataDirectoryTextField.setBounds(fieldX, 10, FIELD_WIDTH, 23);
			dataDirectoryTextField.setText(editorFrame.getDataDirectory());
		}
		return dataDirectoryTextField;
	}

	private JButton getDataDirectoryButton()
	{
		if (dataDirectoryButton == null)
		{
			dataDirectoryButton = new JButton();
			dataDirectoryButton.setBounds(fieldX + FIELD_WIDTH + 5, 10, BUTTON_WIDTH, 25);
			dataDirectoryButton.setText("Browse");
			dataDirectoryButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					selectDataDirectory();
				}
			});
		}
		return dataDirectoryButton;
	}
	
	protected void selectDataDirectory()
	{
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle(gameName() + " Data Directory selection");
		fc.setFileHidingEnabled(false);
		fc.setVisible(true);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		String dataDir = editorFrame.getDataDirectory();
		if (dataDir == null)
			dataDir = System.getProperty("user.dir");
		fc.setCurrentDirectory(new File(dataDir));
		int result = fc.showDialog(this, "Ok");
		if (result == JFileChooser.APPROVE_OPTION)
		{
			getDataDirectoryTextField().setText(fc.getSelectedFile().toString());
		}
	}

	private JTextField getBinDirectoryTextField()
	{
		if (binDirectoryTextField == null)
		{
			binDirectoryTextField = new JTextField();
			binDirectoryTextField.setBounds(fieldX, 43, FIELD_WIDTH, 23);
			binDirectoryTextField.setText(editorFrame.getBinDirectory());
		}
		return binDirectoryTextField;
	}

	private JButton getBinDirectoryButton()
	{
		if (binDirectoryButton == null)
		{
			binDirectoryButton = new JButton();
			binDirectoryButton.setBounds(fieldX + FIELD_WIDTH + 5, 43, BUTTON_WIDTH, 25);
			binDirectoryButton.setText("Browse");
			binDirectoryButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					selectBinDirectory();
				}
			});
		}
		return binDirectoryButton;
	}
	
	protected void selectBinDirectory()
	{
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle(gameName() + " Bin Directory selection");
		fc.setFileHidingEnabled(false);
		fc.setVisible(true);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		String binDir = editorFrame.getBinDirectory();
		if (binDir == null)
			binDir = System.getProperty("user.dir");
		fc.setCurrentDirectory(new File(binDir));
		int result = fc.showDialog(this, "Ok");
		if (result == JFileChooser.APPROVE_OPTION)
		{
			getBinDirectoryTextField().setText(fc.getSelectedFile().toString());
		}
	}

	private JTextField getLibDirectoryTextField()
	{
		if (libDirectoryTextField == null)
		{
			libDirectoryTextField = new JTextField();
			libDirectoryTextField.setBounds(fieldX, 76, FIELD_WIDTH, 23);
			libDirectoryTextField.setText(editorFrame.getLibDirectory());
		}
		return libDirectoryTextField;
	}

	private JButton getLibDirectoryButton()
	{
		if (libDirectoryButton == null)
		{
			libDirectoryButton = new JButton();
			libDirectoryButton.setBounds(fieldX + FIELD_WIDTH + 5, 76, BUTTON_WIDTH, 25);
			libDirectoryButton.setText("Browse");
			libDirectoryButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					selectLibDirectory();
				}
			});
		}
		return libDirectoryButton;
	}
	
	protected void selectLibDirectory()
	{
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle(gameName() + " Lib Directory selection");
		fc.setFileHidingEnabled(false);
		fc.setVisible(true);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		String libDir = editorFrame.getLibDirectory();
		if (libDir == null)
			libDir = System.getProperty("user.dir");
		fc.setCurrentDirectory(new File(libDir));
		int result = fc.showDialog(this, "Ok");
		if (result == JFileChooser.APPROVE_OPTION)
		{
			getLibDirectoryTextField().setText(fc.getSelectedFile().toString());
		}
	}

	private JTextField getRecentFilesMaxTextField()
	{
		if (recentFilesMaxTextField == null)
		{
			recentFilesMaxTextField = new JTextField();
			recentFilesMaxTextField.setBounds(fieldX, 109, 60, 23);
			recentFilesMaxTextField.setText(editorFrame.getRecentFilesMax()+"");
		}
		return recentFilesMaxTextField;
	}

	private JCheckBox getInteractiveFixesCheckBox()
	{
		if (interactiveFixesCheckBox == null)
		{
			interactiveFixesCheckBox = new JCheckBox();
			interactiveFixesCheckBox.setBounds(fieldX, 139, 290, 23);
			interactiveFixesCheckBox.setText("Interactive Fixes");
			interactiveFixesCheckBox.setSelected(editorFrame.getInteractiveFixes());
		}
		return interactiveFixesCheckBox;
	}

	private JCheckBox getCursorCoordinatesCheckBox()
	{
		if (cursorCoordinatesCheckBox == null)
		{
			cursorCoordinatesCheckBox = new JCheckBox();
			cursorCoordinatesCheckBox.setBounds(fieldX, 161, 290, 23);
			cursorCoordinatesCheckBox.setText("Cursor Track Coordinates");
			cursorCoordinatesCheckBox.setSelected(editorFrame.getCursorCoordinates());
		}
		return cursorCoordinatesCheckBox;
	}

	private JCheckBox getCursorNamesCheckBox()
	{
		if (cursorNamesCheckBox == null)
		{
			cursorNamesCheckBox = new JCheckBox();
			cursorNamesCheckBox.setBounds(fieldX, 183, 290, 23);
			cursorNamesCheckBox.setText("Cursor Names");
			cursorNamesCheckBox.setSelected(editorFrame.getCursorNames());
		}
		return cursorNamesCheckBox;
	}

	private JCheckBox getCheckDefaultObjectsCheckBox()
	{
		if (checkDefaultObjectsCheckBox == null)
		{
			checkDefaultObjectsCheckBox = new JCheckBox();
			checkDefaultObjectsCheckBox.setBounds(fieldX, 205, 290, 23);
			checkDefaultObjectsCheckBox.setText("Check Default Objects");
			checkDefaultObjectsCheckBox.setSelected(editorFrame.getCheckDefaultObjects());
		}
		return checkDefaultObjectsCheckBox;
	}

	/** Speed Dreams only: make acc files with accc, like TORCS and CSR, instead of trackgen -A */
	private JCheckBox getSpeedDreamsAccCheckBox()
	{
		if (speedDreamsAccCheckBox == null)
		{
			speedDreamsAccCheckBox = new JCheckBox();
			speedDreamsAccCheckBox.setBounds(fieldX, 249, 290, 23);
			speedDreamsAccCheckBox.setText("Speed Dreams: make .acc files with accc");
			speedDreamsAccCheckBox.setToolTipText("Run trackgen and then sd2-accc with the track's ACC settings " +
				"(Properties > Terrain > ACC), like TORCS and CSR, instead of trackgen -A");
			speedDreamsAccCheckBox.setSelected(editorFrame.getSpeedDreamsAccWithAccc());
		}
		return speedDreamsAccCheckBox;
	}

	private JComboBox<EditorFrame.GameType> getGameTypeComboBox()
	{
		if (gameTypeComboBox == null)
		{
			gameTypeComboBox = new JComboBox<EditorFrame.GameType>(EditorFrame.GameType.values());
			gameTypeComboBox.setBounds(fieldX, 227, 170, 23);
			gameTypeComboBox.setSelectedItem(editorFrame.getGameType());
			gameTypeComboBox.addItemListener(new java.awt.event.ItemListener()
			{
				public void itemStateChanged(java.awt.event.ItemEvent e)
				{
					// each game has its own directories: keep what was typed for the
					// game left, and show the ones of the game chosen
					EditorFrame.GameType game = (EditorFrame.GameType) e.getItem();
					if (e.getStateChange() == java.awt.event.ItemEvent.DESELECTED)
					{
						keepDirectories(game);
					}
					else
					{
						String[] values = directories.get(game);
						getDataDirectoryTextField().setText(values[EditorFrame.DATA_DIRECTORY]);
						getBinDirectoryTextField().setText(values[EditorFrame.BIN_DIRECTORY]);
						getLibDirectoryTextField().setText(values[EditorFrame.LIB_DIRECTORY]);
						if (dataDirectoryLabel != null)
							updateGameLabels();
					}
				}
			});
		}
		return gameTypeComboBox;
	}

	private JButton getOkButton()
	{
		if (okButton == null)
		{
			okButton = new JButton();
			// Ok and Cancel centred under the fields
			int middle = (fieldX + FIELD_WIDTH + 5 + BUTTON_WIDTH + GAP) / 2;
			okButton.setBounds(middle - 78 - 40, 281, 78, 25);
			okButton.setText("Ok");
			okButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					exit();
				}
			});
		}
		return okButton;
	}

	private JButton getCancelButton()
	{
		if (cancelButton == null)
		{
			cancelButton = new JButton();
			int middle = (fieldX + FIELD_WIDTH + 5 + BUTTON_WIDTH + GAP) / 2;
			cancelButton.setBounds(middle + 40, 281, 78, 25);
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					cancel();
				}
			});
		}
		return cancelButton;
	}

	protected void exit()
	{
		int max = -1;
		try
		{
			max = getRecentFilesMax();
		}
		catch (NumberFormatException e)
		{
		}

		if (max < 0 || max > EditorFrame.RECENT_FILES_LIMIT)
		{
			JOptionPane.showMessageDialog(this, "Invalid Recent Files Maximum : " + getRecentFilesMaxTextField().getText() +
				"\n\nUse a whole number from 0 to " + EditorFrame.RECENT_FILES_LIMIT + ".", "Recent Files Maximum", JOptionPane.ERROR_MESSAGE);
			return;
		}

		APPROVE = true;
		cancel();
	}

	protected void cancel()
	{
		editorFrame.getProject().setPreferencesDialogX(this.getX());
		editorFrame.getProject().setPreferencesDialogY(this.getY());
		this.dispose();
	}
	
	/** Remembers the directories shown as the given game's */
	private void keepDirectories(EditorFrame.GameType game)
	{
		directories.put(game, new String[] {
			getDataDirectoryTextField().getText(),
			getBinDirectoryTextField().getText(),
			getLibDirectoryTextField().getText() });
	}

	/** @return the game's data, bin and lib directories */
	public String[] getDirectories(EditorFrame.GameType game)
	{
		keepDirectories(getGameType());
		return directories.get(game).clone();
	}

	/** @return the data directory of the game chosen */
	public String getDataDirectory()
	{
		return getDataDirectoryTextField().getText();
	}
	
	public String getBinDirectory()
	{
		return getBinDirectoryTextField().getText();
	}
	
	public String getLibDirectory()
	{
		return getLibDirectoryTextField().getText();
	}

	public int getRecentFilesMax()
	{
		return Integer.parseInt(getRecentFilesMaxTextField().getText().trim());
	}

	public boolean getInteractiveFixes()
	{
		return getInteractiveFixesCheckBox().isSelected();
	}

	public boolean getCursorCoordinates()
	{
		return getCursorCoordinatesCheckBox().isSelected();
	}

	public boolean getCursorNames()
	{
		return getCursorNamesCheckBox().isSelected();
	}

	public boolean getCheckDefaultObjects()
	{
		return getCheckDefaultObjectsCheckBox().isSelected();
	}

	public boolean getSpeedDreamsAccWithAccc()
	{
		return getSpeedDreamsAccCheckBox().isSelected();
	}

	public EditorFrame.GameType getGameType()
	{
		return (EditorFrame.GameType) getGameTypeComboBox().getSelectedItem();
	}

	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			editorFrame.getProject().setPreferencesDialogX(this.getX());
			editorFrame.getProject().setPreferencesDialogY(this.getY());
		}
	}
}
