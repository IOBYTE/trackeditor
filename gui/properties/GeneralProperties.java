/*
 *   GeneralProperties.java
 *   Created on 27 ��� 2005
 *
 *    The GeneralProperties.java is part of TrackEditor-0.3.1.
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
package gui.properties;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
public class GeneralProperties extends JPanel
{
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
	private JLabel				authorLabel					= null;
	private JTextField			authorTextField				= null;
	private JLabel				descriptionLabel			= null;
	private JTextField			descriptionTextField		= null;
	
	private final String sep = System.getProperty("file.separator");
	
	/**
	 *  
	 */
	public GeneralProperties()
	{
		super();
		initialize();
	}

	/**
	 *  
	 */
	private void initialize()
	{
		authorLabel = new JLabel();
		descriptionLabel = new JLabel();
		pathLabel = new JLabel();
		projectNameLabel = new JLabel();
		trackCategoryLabel = new JLabel();
		trackSubcategoryLabel = new JLabel();
		trackVersionLabel = new JLabel();
		this.setLayout(null);
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
		this.setSize(420, 230);
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		this.add(getPathTextField(), null);
		this.add(getBrowseButton(), null);
		this.add(getProjectNameTextField(), null);
		this.add(projectNameLabel, null);
		this.add(pathLabel, null);
		this.add(authorLabel, null);
		this.add(getAuthorTextField(), null);
		this.add(getDescriptionTextField(), null);
		this.add(descriptionLabel, null);
		this.add(trackCategoryLabel, null);
		this.add(getTrackCategoryComboBox(), null);
		this.add(trackSubcategoryLabel, null);
		this.add(getTrackSubcategoryComboBox(), null);
		this.add(trackVersionLabel, null);
		this.add(getTrackVersionComboBox(), null);
	}

	/**
	 * This method initializes projectNameTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getProjectNameTextField()
	{
		if (projectNameTextField == null)
		{
			projectNameTextField = new JTextField();
			projectNameTextField.setBounds(135, 15, 170, 30);
			projectNameTextField.setText(Editor.getProperties().getHeader().getName());
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
	public JTextField getPathTextField()
	{
		if (pathTextField == null)
		{
			pathTextField = new JTextField();
			pathTextField.setBounds(65, 195, 240, 30);
			pathTextField.setText(Editor.getProperties().getPath().substring(0, Editor.getProperties().getPath().lastIndexOf(sep)));
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
	public JButton getBrowseButton()
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
	public JTextField getAuthorTextField()
	{
		if (authorTextField == null)
		{
			authorTextField = new JTextField();
			authorTextField.setBounds(65, 240, 240, 30);
			authorTextField.setText(Editor.getProperties().getHeader().getAuthor());
		}
		return authorTextField;
	}
	/**
	 * This method initializes descriptionTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getDescriptionTextField()
	{
		if (descriptionTextField == null)
		{
			descriptionTextField = new JTextField();
			descriptionTextField.setBounds(85, 285, 330, 30);
			descriptionTextField.setText(Editor.getProperties().getHeader().getDescription());
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
		fc.setCurrentDirectory(new File(System.getProperty("user.dir") + sep+"tracks"));
		int result = fc.showDialog(this, "Ok");
		if (result == JFileChooser.APPROVE_OPTION)
		{
			getPathTextField().setText(fc.getSelectedFile().toString());
		}
	}

	public void exit()
	{
		String tmpPath = getPathTextField().getText();
		String tmpName = getProjectNameTextField().getText();
		Editor.getProperties().getHeader().setName(tmpName);
		Editor.getProperties().setPath(tmpPath + sep + tmpName);
		int index = tmpPath.lastIndexOf(sep) + 1;
		String category = tmpPath.substring(index);
		
		File path = new File(tmpPath + sep + tmpName);
		if (!path.exists())
		{
			path.mkdirs();
		}
		Editor.getProperties().getHeader().setAuthor(getAuthorTextField().getText());
		Editor.getProperties().getHeader().setDescription(getDescriptionTextField().getText());

		if (category == getTrackCategoryComboBox().getSelectedItem())
			Editor.getProperties().getHeader().setCategory((String) getTrackCategoryComboBox().getSelectedItem());

		String subcategory = (String) getTrackSubcategoryComboBox().getSelectedItem();
		if (subcategory != "none")
			Editor.getProperties().getHeader().setSubcategory(subcategory);
		else
			Editor.getProperties().getHeader().setSubcategory(null);

		Editor.getProperties().getHeader().setVersion(Integer.parseInt((String) getTrackVersionComboBox().getSelectedItem()));

		Editor.getProperties().valueChanged();
	}

} //  @jve:decl-index=0:visual-constraint="10,10"
