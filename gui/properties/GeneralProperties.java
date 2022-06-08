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
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.Editor;

/**
 * @author babis
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class GeneralProperties extends PropertyPanel
{
	private JTextField			nameTextField			= null;
	private JLabel				nameLabel				= null;
	private JComboBox<String>	categoryComboBox		= null;
	private JLabel				categoryLabel			= null;
	private JComboBox<String>	subcategoryComboBox		= null;
	private JLabel				subcategoryLabel		= null;
	private JComboBox<String>	versionComboBox			= null;
	private JLabel				versionLabel			= null;
	private JLabel				pathLabel				= null;
	private JTextField			pathTextField			= null;
	private JButton				browseButton			= null;
	private JLabel				authorLabel				= null;
	private JTextField			authorTextField			= null;
	private JLabel				descriptionLabel		= null;
	private JTextField			descriptionTextField	= null;

	private final String sep = System.getProperty("file.separator");

	/**
	 *
	 */
	public GeneralProperties(EditorFrame frame)
	{
		super(frame);
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
		nameLabel = new JLabel();
		categoryLabel = new JLabel();
		subcategoryLabel = new JLabel();
		versionLabel = new JLabel();
		this.setLayout(null);
		nameLabel.setBounds(15, 15, 100, 30);
		nameLabel.setText("Track Name");
		categoryLabel.setBounds(15, 60, 100, 30);
		categoryLabel.setText("Track Category");
		subcategoryLabel.setBounds(15, 105, 100, 30);
		subcategoryLabel.setText("Track Subcategory");
		versionLabel.setBounds(15, 150, 100, 30);
		versionLabel.setText("Track Version");
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
		this.add(getNameTextField(), null);
		this.add(nameLabel, null);
		this.add(pathLabel, null);
		this.add(authorLabel, null);
		this.add(getAuthorTextField(), null);
		this.add(getDescriptionTextField(), null);
		this.add(descriptionLabel, null);
		this.add(categoryLabel, null);
		this.add(getCategoryComboBox(), null);
		this.add(subcategoryLabel, null);
		this.add(getSubcategoryComboBox(), null);
		this.add(versionLabel, null);
		this.add(getVersionComboBox(), null);
	}

	/**
	 * This method initializes nameTextField
	 *
	 * @return javax.swing.JTextField
	 */
	public JTextField getNameTextField()
	{
		if (nameTextField == null)
		{
			nameTextField = new JTextField();
			nameTextField.setBounds(135, 15, 170, 30);
			nameTextField.setText(Editor.getProperties().getHeader().getName());
		}
		return nameTextField;
	}

	/**
	 * This method initializes categoryComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	public JComboBox<String> getCategoryComboBox()
	{
		if (categoryComboBox == null)
		{
			String[] items = {"circuit", "development", "dirt", "gprix", "karting", "oval", "road", "speedway", "test"};
			categoryComboBox = new JComboBox<String>(items);
			categoryComboBox.setBounds(135, 60, 170, 30);
			categoryComboBox.setSelectedItem(Editor.getProperties().getHeader().getCategory());
		}
		return categoryComboBox;
	}

	/**
	 * This method initializes subcategoryComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	public JComboBox<String> getSubcategoryComboBox()
	{
		if (subcategoryComboBox == null)
		{
			String[] items = {"none", "short", "long"};
			subcategoryComboBox = new JComboBox<String>(items);
			subcategoryComboBox.setBounds(135, 105, 170, 30);
			String subcategory = Editor.getProperties().getHeader().getSubcategory();
			if (subcategory == null)
				subcategory = "none";
			subcategoryComboBox.setSelectedItem(subcategory);
		}
		return subcategoryComboBox;
	}

	/**
	 * This method initializes versionComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	public JComboBox<String> getVersionComboBox()
	{
		if (versionComboBox == null)
		{
			String[] items = {"3", "4", "5"};
			versionComboBox = new JComboBox<String>(items);
			versionComboBox.setBounds(135, 150, 170, 30);
			versionComboBox.setSelectedItem(Editor.getProperties().getHeader().getVersion() + "");
		}
		return versionComboBox;
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
			pathTextField.setText(Editor.getProperties().getPath());
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
		fc.setCurrentDirectory(new File(System.getProperty("user.dir") + sep + "tracks"));
		int result = fc.showDialog(this, "Ok");
		if (result == JFileChooser.APPROVE_OPTION)
		{
			getPathTextField().setText(fc.getSelectedFile().toString());
		}
	}

	public void exit()
	{
		// the path is something/category/track
		String tmpPath = getPathTextField().getText();
		String tmpName = getNameTextField().getText();
		String tmpCategory = (String) getCategoryComboBox().getSelectedItem();
		String result = null;

		if (isDifferent(tmpName, Editor.getProperties().getHeader().getName(), result))
		{
			Editor.getProperties().getHeader().setName(result);
			frame.documentIsModified = true;
		}

		if (isDifferent(tmpPath, Editor.getProperties().getPath(), result))
		{
			Editor.getProperties().setPath(result);
			frame.documentIsModified = true;
		}

		if (isDifferent(tmpCategory, Editor.getProperties().getHeader().getCategory(), result))
		{
			Editor.getProperties().getHeader().setCategory(result);
			frame.documentIsModified = true;
		}

		// get the track name from the path
		int index = tmpPath.lastIndexOf(sep);
		String nameFromPath = tmpPath.substring(index + 1);
		if (!nameFromPath.equals(tmpName))
		{
			// TODO Which one should we use?
			System.out.println("different!!! name from path: " + nameFromPath + " name: " + tmpName);
		}

		// remove the track name from the path
		String pathToCategory = tmpPath.substring(0, index);

		// get the category from the path
		index = pathToCategory.lastIndexOf(sep);
		String categoryFromPath = pathToCategory.substring(index + 1);
		if (!categoryFromPath.equals(tmpCategory))
		{
			// TODO  Which one should we use?
			System.out.println("category from path : " + categoryFromPath + " category : " + tmpCategory);
		}

		File path = new File(tmpPath);
		if (!path.exists())
		{
			path.mkdirs();
		}

		if (isDifferent((String) getSubcategoryComboBox().getSelectedItem(),
			Editor.getProperties().getHeader().getSubcategory(), result))
		{
			Editor.getProperties().getHeader().setSubcategory(result);
			frame.documentIsModified = true;
		}

		int version = Integer.parseInt((String) getVersionComboBox().getSelectedItem());
		if (version != Editor.getProperties().getHeader().getVersion())
		{
			Editor.getProperties().getHeader().setVersion(version);
			frame.documentIsModified = true;
		}

		if (isDifferent(getAuthorTextField().getText(),
			Editor.getProperties().getHeader().getAuthor(), result))
		{
			Editor.getProperties().getHeader().setAuthor(result);
			frame.documentIsModified = true;
		}

		if (isDifferent(getDescriptionTextField().getText(),
			Editor.getProperties().getHeader().getDescription(), result))
		{
			Editor.getProperties().getHeader().setDescription(result);
			frame.documentIsModified = true;
		}
	}

} //  @jve:decl-index=0:visual-constraint="10,10"
