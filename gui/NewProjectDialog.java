/*
 *   NewProjectDialog.java
 *   Created on 27 ??? 2005
 *
 *    The NewProjectDialog.java is part of TrackEditor-0.3.1.
 *
 *    TrackEditor-0.3.1 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.3.1 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.3.1; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package gui;

import java.awt.Point;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * @author babis
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class NewProjectDialog extends JDialog
{
	public static boolean		APPROVE						= false;
	private JPanel				jPanel						= null;
	private JTextField			projectNameTextField		= null;
	private JLabel				projectNameLabel			= null;
	private JComboBox<String>	trackCategoryComboBox		= null;
	private JLabel				trackCategoryLabel			= null;
	private JComboBox<String>	trackSubcategoryComboBox	= null;
	private JLabel				trackSubcategoryLabel		= null;
	private JComboBox<String>	trackVersionComboBox		= null;
	private JLabel				trackVersionLabel			= null;
	private JLabel				pathLabel					= null;
	private JTextField			pathTextField				= null;
	private JButton				browseButton				= null;
	private JButton				okButton					= null;
	private JButton				cancelButton				= null;

	private EditorFrame			editorFrame;
	private JLabel 				authorLabel 				= null;
	private JTextField 			authorTextField 			= null;
	private JLabel 				emailLabel 					= null;
	private JTextField 			emailTextField 				= null;
	private JLabel 				copyrightLabel 				= null;
	private JTextField 			copyrightTextField 			= null;
	private JLabel 				descriptionLabel 			= null;
	private JTextField 			descriptionTextField		= null;
	
	private EditorFrame.NewProjectInfo	newProjectInfo;
	
	private final String sep = System.getProperty("file.separator");
	
	/**
	 *  
	 */
	public NewProjectDialog(EditorFrame editorFrame, EditorFrame.NewProjectInfo newProjectInfo)
	{
		super();
		this.editorFrame = editorFrame;
		this.newProjectInfo = newProjectInfo;
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		this.setSize(540, 329);
		this.setContentPane(getJPanel());
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		Point p = new Point();
		p.x = editorFrame.getProject().getNewProjectDialogX();
		p.y = editorFrame.getProject().getNewProjectDialogY();
		this.setLocation(p);
		this.setTitle("New Project");
		this.getContentPane().setSize(547, 335);
	}
	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel()
	{
		if (jPanel == null)
		{
			authorLabel = new JLabel();
			emailLabel = new JLabel();
			copyrightLabel = new JLabel();
			descriptionLabel = new JLabel();
			pathLabel = new JLabel();
			projectNameLabel = new JLabel();
			trackCategoryLabel = new JLabel();
			trackSubcategoryLabel = new JLabel();
			trackVersionLabel = new JLabel();
			jPanel = new JPanel();
			jPanel.setLayout(null);
			projectNameLabel.setBounds(10, 10, 120, 23);
			projectNameLabel.setText("Track Name");
			trackCategoryLabel.setBounds(10, 37, 120, 23);
			trackCategoryLabel.setText("Track Category");
			trackSubcategoryLabel.setBounds(10, 64, 120, 23);
			trackSubcategoryLabel.setText("Track Subcategory");
			trackVersionLabel.setBounds(10, 91, 120, 23);
			trackVersionLabel.setText("Track Version");
			pathLabel.setBounds(10, 118, 60, 23);
			pathLabel.setText("Path");
			authorLabel.setBounds(10, 145, 60, 23);
			authorLabel.setText("Author");
			emailLabel.setBounds(10, 172, 60, 23);
			emailLabel.setText("Email");
			copyrightLabel.setBounds(10, 199, 60, 23);
			copyrightLabel.setText("Copyright");
			descriptionLabel.setBounds(10, 226, 80, 23);
			descriptionLabel.setText("Description");
			jPanel.add(getBrowseButton(), null);
			jPanel.add(getOkButton(), null);
			jPanel.add(getCancelButton(), null);
			jPanel.add(getProjectNameTextField(), null);
			jPanel.add(projectNameLabel, null);
			jPanel.add(pathLabel, null);
			jPanel.add(authorLabel, null);
			jPanel.add(getAuthorTextField(), null);
			jPanel.add(emailLabel, null);
			jPanel.add(getEmailTextField(), null);
			jPanel.add(copyrightLabel, null);
			jPanel.add(getCopyrightTextField(), null);
			jPanel.add(getDescriptionTextField(), null);
			jPanel.add(descriptionLabel, null);
			jPanel.add(trackCategoryLabel, null);
			jPanel.add(getTrackCategoryComboBox(), null);
			jPanel.add(trackVersionLabel, null);
			jPanel.add(getTrackVersionComboBox(), null);
			jPanel.add(trackSubcategoryLabel, null);
			jPanel.add(getTrackSubcategoryComboBox(), null);
			jPanel.add(getPathTextField(), null);
		}
		return jPanel;
	}
	/**
	 * This method initializes projectNameTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getProjectNameTextField()
	{
		if (projectNameTextField == null)
		{
			projectNameTextField = new JTextField();
			projectNameTextField.setBounds(145, 10, 170, 23);
			projectNameTextField.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{

				}
			});
		}
		return projectNameTextField;
	}
	/**
	 * This method initializes trackCategoryComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	public JComboBox<String> getTrackCategoryComboBox()
	{
		if (trackCategoryComboBox == null)
		{
			String[] items;
			if (editorFrame.getCarsSportsRacing())
				items = new String[] {"circuit", "development", "dirt", "grandprix", "road", "speedway"};
			else
				items = new String[] {"circuit", "development", "dirt", "gprix", "karting", "road", "speedway"};			
			trackCategoryComboBox = new JComboBox<String>(items);
			trackCategoryComboBox.setSelectedItem("road");
			trackCategoryComboBox.setBounds(145, 37, 170, 23);
			trackCategoryComboBox.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if ("speedway".equals(trackCategoryComboBox.getSelectedItem()))
					{
						trackSubcategoryComboBox.setSelectedItem("none");
						trackSubcategoryComboBox.setEnabled(true);
					}
					else
					{
						trackSubcategoryComboBox.setSelectedItem("none");
						trackSubcategoryComboBox.setEnabled(false);
					}
				}
			});
		}
		return trackCategoryComboBox;
	}
	/**
	 * This method initializes trackSubcategoryComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	public JComboBox<String> getTrackSubcategoryComboBox()
	{
		if (trackSubcategoryComboBox == null)
		{
			String[] items =
			{"none", "short", "long"};
			trackSubcategoryComboBox = new JComboBox<String>(items);
			trackSubcategoryComboBox.setEnabled(false);
			trackSubcategoryComboBox.setSelectedItem("none");
			trackSubcategoryComboBox.setBounds(145, 64, 170, 23);
			trackSubcategoryComboBox.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{

				}
			});
		}
		return trackSubcategoryComboBox;
	}
	/**
	 * This method initializes trackVersionComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	public JComboBox<String> getTrackVersionComboBox()
	{
		if (trackVersionComboBox == null)
		{
			String[] items =
			{"3", "4", "5"};
			trackVersionComboBox = new JComboBox<String>(items);
			trackVersionComboBox.setSelectedItem("4");
			trackVersionComboBox.setBounds(145, 91, 170, 23);
			trackVersionComboBox.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{

				}
			});
		}
		return trackVersionComboBox;
	}
	/**
	 * This method initializes pathTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getPathTextField()
	{
		if (pathTextField == null)
		{
			pathTextField = new JTextField();
			pathTextField.setBounds(95, 118, 331, 23);
			
			String path = null;
			File dataDir = new File(editorFrame.getDataDirectory());
			if (dataDir.isDirectory() && dataDir.canWrite())
			{
				File tracksDir = new File(dataDir.getAbsolutePath() + sep + "tracks");
				if (tracksDir.exists() && tracksDir.isDirectory() && tracksDir.canWrite())
				{
					File categoryDir = new File(tracksDir.getAbsolutePath() + sep + trackCategoryComboBox.getSelectedItem().toString());
					if (categoryDir.exists() && categoryDir.isDirectory() && categoryDir.canWrite())
					{
						path = categoryDir.getAbsolutePath();
					}
				}
			}
			
			if (path == null)
			{
				String defaultPath = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
				
				File userTracksDir = new File(defaultPath + sep + "tracks");
				
				if (userTracksDir.exists() && userTracksDir.isDirectory() && userTracksDir.canWrite())
					path = userTracksDir.getAbsolutePath();
				else
				{
					File userDir = new File(defaultPath);

					if (userDir.exists() && userDir.isDirectory() && userDir.canWrite())
						path = userDir.getAbsolutePath();
				}
			}
			pathTextField.setText(path);
			pathTextField.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					System.out.println("actionPerformed()"); // TODO
															 // Auto-generated
															 // Event stub
															 // actionPerformed()
				}
			});
		}
		return pathTextField;
	}
	/**
	 * This method initializes browseButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBrowseButton()
	{
		if (browseButton == null)
		{
			browseButton = new JButton();
			browseButton.setBounds(433, 117, 80, 25);
			browseButton.setText("Browse");
			browseButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					selectPath();
				}
			});
		}
		return browseButton;
	}
	/**
	 * This method initializes authorTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getAuthorTextField() {
		if (authorTextField == null) {
			authorTextField = new JTextField();
			authorTextField.setBounds(95, 145, 418, 23);
		}
		return authorTextField;
	}
	/**
	 * This method initializes emailTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEmailTextField() {
		if (emailTextField == null) {
			emailTextField = new JTextField();
			emailTextField.setBounds(95, 172, 418, 23);
		}
		return emailTextField;
	}
	/**
	 * This method initializes copyrightTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getCopyrightTextField() {
		if (copyrightTextField == null) {
			copyrightTextField = new JTextField();
			copyrightTextField.setBounds(95, 199, 418, 23);
		}
		return copyrightTextField;
	}
	/**
	 * This method initializes descriptionTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getDescriptionTextField() {
		if (descriptionTextField == null) {
			descriptionTextField = new JTextField();
			descriptionTextField.setBounds(95, 226, 418, 23);
		}
		return descriptionTextField;
	}
	/**
	 *  
	 */
	protected void selectPath()
	{
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle("Project path selection");
		fc.setVisible(true);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		String path = pathTextField.getText();
		fc.setCurrentDirectory(new File(path));
		int result = fc.showDialog(this, "Ok");
		if (result == JFileChooser.APPROVE_OPTION)
		{
			getPathTextField().setText(fc.getSelectedFile().toString());
		}
	}
	/**
	 * This method initializes okButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton()
	{
		if (okButton == null)
		{
			okButton = new JButton();
			okButton.setBounds(155, 254, 78, 25);
			okButton.setText("Ok");
			okButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					exit();
				}
			});
		}
		return okButton;
	}
	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton()
	{
		if (cancelButton == null)
		{
			cancelButton = new JButton();
			cancelButton.setBounds(290, 254, 78, 25);
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					cancel();
				}
			});
		}
		return cancelButton;
	}
	/**
	 *  
	 */
	protected void exit()
	{
		String tmpName = getProjectNameTextField().getText();	
		String tmpPath = getPathTextField().getText();

		File projectPath = new File(tmpPath + sep + tmpName);
		if (!projectPath.exists())
		{
			projectPath.mkdirs();
		}

		newProjectInfo.name = tmpName;
		newProjectInfo.category = (String) getTrackCategoryComboBox().getSelectedItem();
		String subcategory = (String) getTrackSubcategoryComboBox().getSelectedItem();
		if (!"none".equals(subcategory))
			newProjectInfo.subcategory = subcategory;
		newProjectInfo.version = Integer.parseInt((String) getTrackVersionComboBox().getSelectedItem());
		newProjectInfo.path = tmpPath + sep + tmpName;
		newProjectInfo.author = getAuthorTextField().getText();
		newProjectInfo.email = getEmailTextField().getText();
		newProjectInfo.copyright = getCopyrightTextField().getText();
		newProjectInfo.description = getDescriptionTextField().getText();

		APPROVE = true;
		cancel();
	}
	/**
	 *  
	 */
	protected void cancel()
	{
		editorFrame.getProject().setNewProjectDialogX(this.getX());
		editorFrame.getProject().setNewProjectDialogY(this.getY());
		this.dispose();
	}

	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			editorFrame.getProject().setNewProjectDialogX(this.getX());
			editorFrame.getProject().setNewProjectDialogY(this.getY());
		}
	}
} //  @jve:decl-index=0:visual-constraint="6,6"
