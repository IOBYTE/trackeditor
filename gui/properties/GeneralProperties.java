/*
 *   GeneralProperties.java
 *   Created on 27 ??? 2005
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

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.Editor;
import utils.MutableString;

/**
 * @author babis
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class GeneralProperties extends PropertyPanel
{
	private JLabel				nameLabel				= new JLabel();
	private JTextField			nameTextField			= new JTextField();
	private JLabel				shortNameLabel			= new JLabel();
	private JTextField			shortNameTextField		= new JTextField();
	private JLabel				categoryLabel			= new JLabel();
	private JComboBox<String>	categoryComboBox		= null;
	private JLabel				subcategoryLabel		= new JLabel();
	private JComboBox<String>	subcategoryComboBox		= null;
	private JLabel				versionLabel			= new JLabel();
	private JComboBox<String>	versionComboBox			= null;
	private JLabel				skyVersionLabel			= new JLabel();
	private JComboBox<String>	skyVersionComboBox		= null;
	private JLabel				authorLabel				= new JLabel();
	private JTextField			authorTextField			= new JTextField();
	private JLabel				emailLabel				= new JLabel();
	private JTextField			emailTextField			= new JTextField();
	private JLabel				copyrightLabel			= new JLabel();
	private JTextField			copyrightTextField		= new JTextField();
	private JLabel				descriptionLabel		= new JLabel();
	private JTextField			descriptionTextField	= new JTextField();
	private JLabel				carCategoryLabel		= new JLabel();
	private JTextField			carCategoryTextField	= new JTextField();
	private JLabel				disabledPenaltiesLabel	= new JLabel();
	private JCheckBox			slowdownCheckBox		= new JCheckBox("Slowdown");
	private JCheckBox			wallHitCheckBox			= new JCheckBox("Wall hit invalidates lap time");

	private static final String	SLOWDOWN				= "slowdown";
	private static final String	WALL_HIT				= "wallhittimeinvalidate";

	private String				lastSubcategory			= null;
	private final String 		sep						= System.getProperty("file.separator");

	/**
	 *
	 */
	public GeneralProperties(EditorFrame editorFrame)
	{
		super(editorFrame);
		initialize();
	}

	/**
	 *
	 */
	private void initialize()
	{
		setLayout(null);
		setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));

		// TORCS has no short name, subcategory, sky version, email or copyright
		boolean extensions = getEditorFrame().getGameType().hasSpeedDreamsExtensions();
		int row = 0;

		addLabel(this, row, nameLabel, "Name", 110);
		addTextField(this, row++, nameTextField, getEditorFrame().getTrackData().getHeader().getName(), 130, 200);
		if (extensions)
		{
			addLabel(this, row, shortNameLabel, "Short Name", 110);
			addTextField(this, row++, shortNameTextField, getEditorFrame().getTrackData().getHeader().getShortName(), 130, 200);
		}
		addLabel(this, row, categoryLabel, "Category", 110);
		add(getCategoryComboBox(), null);
		getCategoryComboBox().setBounds(130, rowY(row++), 125, 23);
		if (extensions)
		{
			addLabel(this, row, subcategoryLabel, "Subcategory", 110);
			add(getSubcategoryComboBox(), null);
			getSubcategoryComboBox().setBounds(130, rowY(row++), 125, 23);
		}
		addLabel(this, row, versionLabel, "Version", 110);
		add(getVersionComboBox(), null);
		getVersionComboBox().setBounds(130, rowY(row++), 125, 23);
		if (extensions)
		{
			addLabel(this, row, skyVersionLabel, "Sky Version", 110);
			add(getSkyVersionComboBox(), null);
			getSkyVersionComboBox().setBounds(130, rowY(row++), 125, 23);
		}
		addLabel(this, row, authorLabel, "Author", 80);
		addTextField(this, row++, authorTextField, getEditorFrame().getTrackData().getHeader().getAuthor(), 85, 435);
		if (extensions)
		{
			addLabel(this, row, emailLabel, "Email", 80);
			addTextField(this, row++, emailTextField, getEditorFrame().getTrackData().getHeader().getEmail(), 85, 435);
			addLabel(this, row, copyrightLabel, "Copyright", 80);
			addTextField(this, row++, copyrightTextField, getEditorFrame().getTrackData().getHeader().getCopyright(), 85, 435);
		}
		addLabel(this, row, descriptionLabel, "Description", 80);
		addTextField(this, row++, descriptionTextField, getEditorFrame().getTrackData().getHeader().getDescription(), 85, 435);
		if (getEditorFrame().getGameType().hasCarCategory())
		{
			addLabel(this, 10, carCategoryLabel, "Car Category", 110);
			addTextField(this, 10, carCategoryTextField, getEditorFrame().getTrackData().getHeader().getCarCategory(), 130, 390);
			carCategoryTextField.setToolTipText("Car category this track is meant for (empty = any)");
		}
		else if (getEditorFrame().getGameType().hasDisabledPenalties())
		{
			addLabel(this, 10, disabledPenaltiesLabel, "Disabled Penalties", 120);
			slowdownCheckBox.setBounds(130, 280, 100, 23);
			slowdownCheckBox.setSelected(getEditorFrame().getTrackData().getHeader().hasDisabledPenalty(SLOWDOWN));
			slowdownCheckBox.setToolTipText("No slowdown penalty for cutting corners on this track");
			add(slowdownCheckBox);
			wallHitCheckBox.setBounds(240, 280, 220, 23);
			wallHitCheckBox.setSelected(getEditorFrame().getTrackData().getHeader().hasDisabledPenalty(WALL_HIT));
			wallHitCheckBox.setToolTipText("Hitting a wall doesn't invalidate the lap time on this track");
			add(wallHitCheckBox);
		}
	}

	private static int rowY(int row)
	{
		return 10 + row * 27;
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
			String[] items = getEditorFrame().getGameType().getTrackCategories();
			categoryComboBox = new JComboBox<String>(items);
			categoryComboBox.setBounds(130, 64, 125, 23);
			selectItem(categoryComboBox, getEditorFrame().getTrackData().getHeader().getCategory());
			categoryComboBox.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if ("speedway".equals(categoryComboBox.getSelectedItem()))
					{
						if (lastSubcategory != null)
						{
							subcategoryComboBox.setSelectedItem(lastSubcategory);
						}
						else
						{
							subcategoryComboBox.setSelectedItem("none");
						}
						subcategoryComboBox.setEnabled(true);
					}
					else
					{
						subcategoryComboBox.setEnabled(false);
						subcategoryComboBox.setSelectedItem("none");
					}
				}
			});
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
			subcategoryComboBox.setBounds(130, 91, 125, 23);
			String subcategory = getEditorFrame().getTrackData().getHeader().getSubcategory();
			if (subcategory == null)
				subcategory = "none";
			selectItem(subcategoryComboBox, subcategory);
			lastSubcategory = new String(subcategory);
			subcategoryComboBox.setEnabled("speedway".equals(getEditorFrame().getTrackData().getHeader().getCategory()));
			subcategoryComboBox.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (subcategoryComboBox.isEnabled())
					{
						lastSubcategory = new String(subcategoryComboBox.getSelectedItem().toString());
					}
				}
			});
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
			versionComboBox.setBounds(130, 118, 125, 23);
			versionComboBox.setSelectedItem(getEditorFrame().getTrackData().getHeader().getVersion() + "");
		}
		return versionComboBox;
	}

	/**
	 * This method initializes skyVersionComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	public JComboBox<String> getSkyVersionComboBox()
	{
		if (skyVersionComboBox == null)
		{
			String[] items = {"none", "1"};
			skyVersionComboBox = new JComboBox<String>(items);
			skyVersionComboBox.setBounds(130, 145, 125, 23);
			int version = getEditorFrame().getTrackData().getHeader().getSkyVersion();
			String stringVersion;
			if (version == Integer.MAX_VALUE)
				stringVersion = "none";
			else
				stringVersion = version + "";
			selectItem(skyVersionComboBox, stringVersion);
		}
		return skyVersionComboBox;
	}

	public void exit()
	{
		MutableString stringResult = new MutableString();

		// the path is something/category/track
		String tmpPath = Editor.getProperties().getPath();

		if (isDifferent(nameTextField.getText(), getEditorFrame().getTrackData().getHeader().getName(), stringResult))
		{
			getEditorFrame().getTrackData().getHeader().setName(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		// the fields TORCS doesn't have aren't shown, so they are left as they are
		boolean extensions = getEditorFrame().getGameType().hasSpeedDreamsExtensions();

		if (extensions && isDifferent(shortNameTextField.getText(), getEditorFrame().getTrackData().getHeader().getShortName(), stringResult))
		{
			getEditorFrame().getTrackData().getHeader().setShortName(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent((String) getCategoryComboBox().getSelectedItem(), getEditorFrame().getTrackData().getHeader().getCategory(), stringResult))
		{
			getEditorFrame().getTrackData().getHeader().setCategory(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		// (the category used to be compared with the track's folder here, only to print a
		// note; it threw for a path without a folder separator, so Ok applied nothing)

		File path = new File(tmpPath);
		if (!path.exists())
		{
			path.mkdirs();
		}

		if (extensions && isDifferent((String) getSubcategoryComboBox().getSelectedItem(),
			getEditorFrame().getTrackData().getHeader().getSubcategory(), stringResult))
		{
			getEditorFrame().getTrackData().getHeader().setSubcategory(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		int version = Integer.parseInt((String) getVersionComboBox().getSelectedItem());
		if (version != getEditorFrame().getTrackData().getHeader().getVersion())
		{
			getEditorFrame().getTrackData().getHeader().setVersion(version);
			getEditorFrame().documentIsModified = true;
		}

		String skyVersionString = (String) getSkyVersionComboBox().getSelectedItem();
		int skyVersion;
		if ("none".equals(skyVersionString))
			skyVersion = Integer.MAX_VALUE;
		else
			skyVersion = Integer.parseInt(skyVersionString);
		if (extensions && skyVersion != getEditorFrame().getTrackData().getHeader().getSkyVersion())
		{
			getEditorFrame().getTrackData().getHeader().setSkyVersion(skyVersion);
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(authorTextField.getText(),
			getEditorFrame().getTrackData().getHeader().getAuthor(), stringResult))
		{
			getEditorFrame().getTrackData().getHeader().setAuthor(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (extensions && isDifferent(emailTextField.getText(),
			getEditorFrame().getTrackData().getHeader().getEmail(), stringResult))
		{
			getEditorFrame().getTrackData().getHeader().setEmail(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (extensions && isDifferent(copyrightTextField.getText(),
			getEditorFrame().getTrackData().getHeader().getCopyright(), stringResult))
		{
			getEditorFrame().getTrackData().getHeader().setCopyright(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(descriptionTextField.getText(),
			getEditorFrame().getTrackData().getHeader().getDescription(), stringResult))
		{
			getEditorFrame().getTrackData().getHeader().setDescription(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (getEditorFrame().getGameType().hasCarCategory())
		{
			if (isDifferent(carCategoryTextField.getText(),
				getEditorFrame().getTrackData().getHeader().getCarCategory(), stringResult))
			{
				getEditorFrame().getTrackData().getHeader().setCarCategory(stringResult.getValue());
				getEditorFrame().documentIsModified = true;
			}
		}
		else if (getEditorFrame().getGameType().hasDisabledPenalties())
		{
			String oldPenalties = getEditorFrame().getTrackData().getHeader().getDisabledPenalties();
			getEditorFrame().getTrackData().getHeader().setDisabledPenalty(SLOWDOWN, slowdownCheckBox.isSelected());
			getEditorFrame().getTrackData().getHeader().setDisabledPenalty(WALL_HIT, wallHitCheckBox.isSelected());
			String newPenalties = getEditorFrame().getTrackData().getHeader().getDisabledPenalties();
			if (oldPenalties == null ? newPenalties != null : !oldPenalties.equals(newPenalties))
				getEditorFrame().documentIsModified = true;
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
