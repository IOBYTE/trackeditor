/*
 *   NewProjectDialog.java
 *   Created on 27 ��� 2005
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

import java.awt.Frame;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utils.Editor;
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

	private EditorFrame			parent;
	private JLabel 				authorLabel 				= null;
	private JTextField 			authorTextField 			= null;
	private JLabel 				descriptionLabel 			= null;
	private JTextField 			descriptionTextField		= null;
	
	private final String sep = System.getProperty("file.separator");
	
	/**
	 *  
	 */
	public NewProjectDialog(Frame parent)
	{
		super();
		this.parent = (EditorFrame) parent;
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		this.setSize(440, 455);
		this.setContentPane(getJPanel());
		this.setModal(true);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setTitle("New Project");
		this.getContentPane().setSize(447, 321);
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
			descriptionLabel = new JLabel();
			pathLabel = new JLabel();
			projectNameLabel = new JLabel();
			trackCategoryLabel = new JLabel();
			trackSubcategoryLabel = new JLabel();
			trackVersionLabel = new JLabel();
			jPanel = new JPanel();
			jPanel.setLayout(null);
			projectNameLabel.setBounds(15, 15, 100, 30);
			projectNameLabel.setText("Track Name");
			trackCategoryLabel.setBounds(15, 60, 100, 30);
			trackCategoryLabel.setText("Track Category");
			trackSubcategoryLabel.setBounds(15, 105, 100, 30);
			trackSubcategoryLabel.setText("Track Subcategory");
			trackVersionLabel.setBounds(15, 150, 100, 30);
			trackVersionLabel.setText("Track Version");
			pathLabel.setBounds(15, 195, 60, 30);
			pathLabel.setText("Path");
			authorLabel.setBounds(15, 240, 60, 30);
			authorLabel.setText("Author");
			descriptionLabel.setBounds(15, 285, 80, 30);
			descriptionLabel.setText("Description");
			jPanel.add(getPathTextField(), null);
			jPanel.add(getBrowseButton(), null);
			jPanel.add(getOkButton(), null);
			jPanel.add(getCancelButton(), null);
			jPanel.add(getProjectNameTextField(), null);
			jPanel.add(projectNameLabel, null);
			jPanel.add(pathLabel, null);
			jPanel.add(authorLabel, null);
			jPanel.add(getAuthorTextField(), null);
			jPanel.add(getDescriptionTextField(), null);
			jPanel.add(descriptionLabel, null);
			jPanel.add(trackCategoryLabel, null);
			jPanel.add(getTrackCategoryComboBox(), null);
			jPanel.add(trackVersionLabel, null);
			jPanel.add(getTrackVersionComboBox(), null);
			jPanel.add(trackSubcategoryLabel, null);
			jPanel.add(getTrackSubcategoryComboBox(), null);
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
			projectNameTextField.setBounds(135, 15, 170, 30);
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
			String[] items =
			{"circuit", "development", "dirt", "gprix", "karting", "oval", "road", "speedway", "test"};
			trackCategoryComboBox = new JComboBox<String>(items);
			trackCategoryComboBox.setSelectedItem(Editor.getProperties().getHeader().getCategory());
			trackCategoryComboBox.setBounds(135, 60, 170, 30);
			trackCategoryComboBox.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{

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
			String subcategory = Editor.getProperties().getHeader().getSubcategory();
			if (subcategory == null)
				subcategory = "none";
			trackSubcategoryComboBox.setSelectedItem(subcategory);
			trackSubcategoryComboBox.setBounds(135, 105, 170, 30);
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
			trackVersionComboBox.setSelectedItem(Editor.getProperties().getHeader().getVersion() + "");
			trackVersionComboBox.setBounds(135, 150, 170, 30);
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
			pathTextField.setBounds(65, 195, 240, 30);
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
			browseButton.setBounds(320, 195, 95, 30);
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
			authorTextField.setBounds(65, 240, 240, 30);
		}
		return authorTextField;
	}
	/**
	 * This method initializes descriptionTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getDescriptionTextField() {
		if (descriptionTextField == null) {
			descriptionTextField = new JTextField();
			descriptionTextField.setBounds(85, 285, 330, 30);
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
		fc.setCurrentDirectory(new File(System.getProperty("user.dir") +sep+ "tracks"));
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
			okButton.setBounds(105, 375, 78, 30);
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
			cancelButton.setBounds(250, 375, 78, 30);
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
		String tmpPath = getPathTextField().getText();
		String tmpName = getProjectNameTextField().getText();
		Editor.getProperties().getHeader().setName(tmpName);
		Editor.getProperties().setPath(tmpPath + sep + tmpName);
		int index = tmpPath.lastIndexOf(sep) + 1;
		String category = tmpPath.substring(index);

		if (category == getTrackCategoryComboBox().getSelectedItem())
			Editor.getProperties().getHeader().setCategory((String) getTrackCategoryComboBox().getSelectedItem());

		File path = new File(tmpPath + sep + tmpName);
		if (!path.exists())
		{
			path.mkdirs();
		}
		String subcategory = (String) getTrackSubcategoryComboBox().getSelectedItem();
		if (subcategory != "none")
			Editor.getProperties().getHeader().setSubcategory(subcategory);
		else
			Editor.getProperties().getHeader().setSubcategory(null);
		Editor.getProperties().getHeader().setAuthor(this.getAuthorTextField().getText());
		Editor.getProperties().getHeader().setDescription(this.getDescriptionTextField().getText());
		Editor.getProperties().getHeader().setVersion(Integer.parseInt((String) getTrackVersionComboBox().getSelectedItem()));
		APPROVE = true;
		cancel();
	}
	/**
	 *  
	 */
	protected void cancel()
	{
		this.dispose();
	}
  } //  @jve:decl-index=0:visual-constraint="6,6"