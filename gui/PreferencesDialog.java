package gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
	private JCheckBox			carsSportsRacingCheckBox	= null;
	private JButton				okButton					= null;
	private JButton				cancelButton				= null;

	public PreferencesDialog(EditorFrame editorFrame)
	{
		super();
		this.editorFrame = editorFrame;
		initialize();
	}
	
	private void initialize()
	{
		this.setSize(600, 334);
		this.setContentPane(getJPanel());
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
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.add(dataDirectoryLabel, null);
			jPanel.add(binDirectoryLabel, null);
			jPanel.add(libDirectoryLabel, null);
			jPanel.add(recentFilesMaxLabel, null);
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
			jPanel.add(getCarsSportsRacingCheckBox(), null);
			jPanel.add(getOkButton(), null);
			jPanel.add(getCancelButton(), null);
		}
		return jPanel;
	}

	private JTextField getDataDirectoryTextField()
	{
		if (dataDirectoryTextField == null)
		{
			dataDirectoryTextField = new JTextField();
			dataDirectoryTextField.setBounds(200, 10, 290, 23);
			dataDirectoryTextField.setText(editorFrame.getDataDirectory());
		}
		return dataDirectoryTextField;
	}

	private JButton getDataDirectoryButton()
	{
		if (dataDirectoryButton == null)
		{
			dataDirectoryButton = new JButton();
			dataDirectoryButton.setBounds(495, 10, 80, 25);
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
		fc.setDialogTitle("Speed Dreams Data Directory selection");
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
			binDirectoryTextField.setBounds(200, 43, 290, 23);
			binDirectoryTextField.setText(editorFrame.getBinDirectory());
		}
		return binDirectoryTextField;
	}

	private JButton getBinDirectoryButton()
	{
		if (binDirectoryButton == null)
		{
			binDirectoryButton = new JButton();
			binDirectoryButton.setBounds(495, 43, 80, 25);
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
		fc.setDialogTitle("Speed Dreams Bin Directory selection");
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
			libDirectoryTextField.setBounds(200, 76, 290, 23);
			libDirectoryTextField.setText(editorFrame.getLibDirectory());
		}
		return libDirectoryTextField;
	}

	private JButton getLibDirectoryButton()
	{
		if (libDirectoryButton == null)
		{
			libDirectoryButton = new JButton();
			libDirectoryButton.setBounds(495, 76, 80, 25);
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
		fc.setDialogTitle("Speed Dreams Lib Directory selection");
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
			recentFilesMaxTextField.setBounds(200, 109, 290, 23);
			recentFilesMaxTextField.setText(editorFrame.getRecentFilesMax()+"");
		}
		return recentFilesMaxTextField;
	}

	private JCheckBox getInteractiveFixesCheckBox()
	{
		if (interactiveFixesCheckBox == null)
		{
			interactiveFixesCheckBox = new JCheckBox();
			interactiveFixesCheckBox.setBounds(200, 139, 290, 23);
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
			cursorCoordinatesCheckBox.setBounds(200, 161, 290, 23);
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
			cursorNamesCheckBox.setBounds(200, 183, 290, 23);
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
			checkDefaultObjectsCheckBox.setBounds(200, 205, 290, 23);
			checkDefaultObjectsCheckBox.setText("Check Default Objects");
			checkDefaultObjectsCheckBox.setSelected(editorFrame.getCheckDefaultObjects());
		}
		return checkDefaultObjectsCheckBox;
	}

	private JCheckBox getCarsSportsRacingCheckBox()
	{
		if (carsSportsRacingCheckBox == null)
		{
			carsSportsRacingCheckBox = new JCheckBox();
			carsSportsRacingCheckBox.setBounds(200, 227, 290, 23);
			carsSportsRacingCheckBox.setText("Cars Sports Racing Support");
			carsSportsRacingCheckBox.setSelected(editorFrame.getCarsSportsRacing());
		}
		return carsSportsRacingCheckBox;
	}

	private JButton getOkButton()
	{
		if (okButton == null)
		{
			okButton = new JButton();
			okButton.setBounds(160, 259, 78, 25);
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
			cancelButton.setBounds(350, 259, 78, 25);
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
		try
		{
			Integer.parseInt(getRecentFilesMaxTextField().getText());
		
			APPROVE = true;
			cancel();
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this, "Invalid Recent Files Maximum : " + getRecentFilesMaxTextField().getText(), "Recent Files Maximum", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void cancel()
	{
		editorFrame.getProject().setPreferencesDialogX(this.getX());
		editorFrame.getProject().setPreferencesDialogY(this.getY());
		this.dispose();
	}
	
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
		return Integer.parseInt(getRecentFilesMaxTextField().getText());
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

	public boolean getCarsSportsRacing()
	{
		return getCarsSportsRacingCheckBox().isSelected();
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
