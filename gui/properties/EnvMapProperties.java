/*
 *   EnvMapProperties.java
 *   Created on 14 June 2022
 *
 *    The EnvMapProperties.java is part of TrackEditor-0.7.0.
 *
 *    TrackEditor-0.7.0 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.7.0 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.7.0; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package gui.properties;

import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.EditorFrame;
import utils.Editor;
import utils.MutableString;
import utils.circuit.EnvironmentMapping;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class EnvMapProperties extends PropertyPanel
{
	private JButton				addEnvMapButton		= null;
	private JButton				deleteEnvMapButton	= null;
	private JTabbedPane			tabbedPane			= null;

	/**
	 *
	 */
	public EnvMapProperties(EditorFrame editorFrame)
	{
		super(editorFrame);
		initialize();
    }

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize()
	{
		this.setLayout(null);
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		this.add(getTabbedPane(), null);
		this.add(getAddEnvMapButton(), null);
		this.add(getDeleteEnvMapButton(), null);
	}

	/**
	 * This method initializes addEnvMappingButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getAddEnvMapButton()
	{
		if (addEnvMapButton == null)
		{
			addEnvMapButton = new JButton();
			addEnvMapButton.setBounds(10, 120, 120, 25);
			addEnvMapButton.setText("Add Env Map");
			addEnvMapButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					String name = uniqueName(tabbedPane, "map ");
					EnvironmentMapping envMap = new EnvironmentMapping();
					envMap.setName(name);

					tabbedPane.addTab(name, null, new EnvMapPanel(envMap), null);
					tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
				}
			});
		}
		return addEnvMapButton;
	}

	/**
	 * This method initializes deleteEnvMapButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteEnvMapButton()
	{
		if (deleteEnvMapButton == null)
		{
			deleteEnvMapButton = new JButton();
			deleteEnvMapButton.setBounds(140, 120, 130, 25);
			deleteEnvMapButton.setText("Delete Env Map");
			deleteEnvMapButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (tabbedPane.getTabCount() > 0)
					{
						tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
					}
				}
			});
		}
		return deleteEnvMapButton;
	}

	/**
	 * This method initializes tabbedPane
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getTabbedPane()
	{
		if (tabbedPane == null)
		{
			tabbedPane = new JTabbedPane();
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			tabbedPane.setBounds(10, 10, 510, 100);

			Vector<EnvironmentMapping> envMaps = getEditorFrame().getTrackData().getGraphic().getEnvironmentMapping();

			for (int i = 0; i < envMaps.size(); i++)
	        {
                EnvironmentMapping envMap = envMaps.elementAt(i);
				tabbedPane.addTab(envMap.getName(), null, new EnvMapPanel(envMap), null);
			}
		}
		return tabbedPane;
	}

	private class EnvMapPanel extends JPanel
	{
		private final EnvironmentMapping	item;	// what this tab shows and Ok changes
		private JLabel		nameLabel		= new JLabel();
		private JTextField 	nameTextField	= new JTextField();
		private JLabel		envMapLabel		= new JLabel();
		private JTextField	envMapTextField	= new JTextField();
		private JButton		envMapButton	= null;

		private final String sep = System.getProperty("file.separator");

		/**
		 *
		 */
		public EnvMapPanel(EnvironmentMapping envMap)
		{
			super();
			this.item = envMap;
			initialize(envMap.getName(), envMap.getEnvMapImage());
		}

		/**
		 *
		 */
		private void initialize(String name, String environmentMap)
		{
			setLayout(null);

			addLabel(this, 0, nameLabel, "Name", 120);
			addLabel(this, 1, envMapLabel, "Environment Map", 120);

			addTextField(this, 0, nameTextField, name, 130, 100);
			markItemName(nameTextField);
			addTextField(this, 1, envMapTextField, environmentMap, 130, 280);

			add(getEnvMapButton(), null);
		}

		/**
		 * This method initializes envMapButton
		 *
		 * @return javax.swing.JButton
		 */
		private JButton getEnvMapButton()
		{
			if (envMapButton == null)
			{
				envMapButton = new JButton();
				envMapButton.setBounds(420, 36, 80, 25);
				envMapButton.setText("Browse");
				envMapButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						envMapFile();
					}
				});
			}
			return envMapButton;
		}

		protected void envMapFile()
		{
			Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
			UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFiles(null);
			fc.setSelectedFile(null);
			fc.rescanCurrentDirectory();
			fc.setApproveButtonMnemonic(0);
			fc.setDialogTitle("Environment Mapping image file selection");
			fc.setFileHidingEnabled(false);
			fc.setVisible(true);
			fc.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("RGB and PNG images", "rgb", "png");
			fc.addChoosableFileFilter(filter);
			fc.setCurrentDirectory(new File(Editor.getProperties().getPath()));
			int result = fc.showOpenDialog(this);
			UIManager.put("FileChooser.readOnly", old);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				String fileName = fc.getSelectedFile().toString();
				int index = fileName.lastIndexOf(sep);
				String pathToFile = fileName.substring(0, index);
				if (pathToFile.equals(Editor.getProperties().getPath()))
					fileName = fileName.substring(index + 1);
				envMapTextField.setText(fileName);
			}
		}
	}

	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		Vector<EnvironmentMapping> envMaps = getEditorFrame().getTrackData().getGraphic().getEnvironmentMapping();
		// each tab keeps the item it shows, so deleting a tab can't move values the
		// tab doesn't show (like the unit of a camera's "to start") to another item
		Vector<EnvironmentMapping> tabItems = new Vector<EnvironmentMapping>();
		for (int i = 0; i < tabbedPane.getTabCount(); i++)
        {
            EnvMapPanel panel = (EnvMapPanel) tabbedPane.getComponentAt(i);
            EnvironmentMapping envMap = panel.item;
            if (isDifferent(panel.nameTextField.getText(), envMap.getName(), stringResult))
            {
                envMap.setName(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }
            if (isDifferent(panel.envMapTextField.getText(), envMap.getEnvMapImage(), stringResult))
            {
                envMap.setEnvMapImage(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }
            tabItems.add(envMap);
		}
		if (replaceItems(envMaps, tabItems))
		{
			getEditorFrame().documentIsModified = true;
		}
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
