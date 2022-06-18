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
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.EditorFrame;
import utils.Editor;
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
	public EnvMapProperties(EditorFrame frame)
	{
		super(frame);
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
		this.setSize(362, 251);
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
					String name = "map " + (tabbedPane.getTabCount() + 1);

					tabbedPane.addTab(name, null, new EnvMapPanel(name, ""), null);
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
			tabbedPane.setBounds(10, 10, 460, 100);

			Vector<EnvironmentMapping> envMaps = Editor.getProperties().getGraphic().getEnvironmentMapping();

			for (int i = 0; i < envMaps.size(); i++)
	        {
                EnvironmentMapping envMap = envMaps.elementAt(i);
				tabbedPane.addTab(envMap.getName(), null, new EnvMapPanel(envMap.getName(), envMap.getEnvMapImage()), null);
			}
		}
		return tabbedPane;
	}

	private class EnvMapPanel extends JPanel
	{
		private JLabel		nameLabel		= null;
		private JTextField 	nameTextField	= null;
		private JLabel		envMapLabel		= null;
		private JTextField	envMapTextField	= null;
		private JButton		browseButton	= null;

		private final String sep = System.getProperty("file.separator");

		/**
		 *
		 */
		public EnvMapPanel(String name, String envMap)
		{
			super();
			initialize(name, envMap);
		}

		/**
		 *
		 */
		private void initialize(String name, String environmentMap)
		{
			nameLabel = new JLabel();
			envMapLabel = new JLabel();
			nameLabel.setBounds(10, 15, 100, 20);
			nameLabel.setText("Name");
			envMapLabel.setBounds(10, 40, 100, 20);
			envMapLabel.setText("Environment Map");
			add(nameLabel);
			add(envMapLabel);
			setLayout(null);
			add(getNameTextField(), null);
			add(getEnvironmentMapTextField(), null);
			add(getBrowseButton(), null);

			getNameTextField().setText(name);
			getEnvironmentMapTextField().setText(environmentMap);
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
				nameTextField.setBounds(115, 15, 60, 20);
			}
			return nameTextField;
		}

		/**
		 * This method initializes envMapTextField
		 *
		 * @return javax.swing.JTextField
		 */
		public JTextField getEnvironmentMapTextField()
		{
			if (envMapTextField == null)
			{
				envMapTextField = new JTextField();
				envMapTextField.setBounds(115, 40, 250, 20);
			}
			return envMapTextField;
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
				browseButton.setBounds(370, 37, 80, 25);
				browseButton.setText("Browse");
				browseButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						selectFile();
					}
				});
			}
			return browseButton;
		}

		protected void selectFile()
		{
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFiles(null);
			fc.setSelectedFile(null);
			fc.rescanCurrentDirectory();
			fc.setApproveButtonMnemonic(0);
			fc.setDialogTitle("Enfironment Mapping image file selection");
			fc.setVisible(true);
			fc.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG images", "png");
			fc.addChoosableFileFilter(filter);
			fc.setCurrentDirectory(new File(Editor.getProperties().getPath()));
			int result = fc.showDialog(this, "Ok");
			if (result == JFileChooser.APPROVE_OPTION)
			{
				String fileName = fc.getSelectedFile().toString();
				int index = fileName.lastIndexOf(sep);
				String pathToFile = fileName.substring(0, index);
				if (pathToFile.equals(Editor.getProperties().getPath()))
					fileName = fileName.substring(index + 1);
				getEnvironmentMapTextField().setText(fileName);
			}
		}
	}

	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		Vector<EnvironmentMapping> envMaps = Editor.getProperties().getGraphic().getEnvironmentMapping();
		int minCount = Math.min(envMaps.size(), tabbedPane.getTabCount());
		if (envMaps.size() != tabbedPane.getTabCount())
		{
			frame.documentIsModified = true;
		}
		for (int i = 0; i < minCount; i++)
        {
            utils.circuit.EnvironmentMapping envMap = envMaps.elementAt(i);
            EnvMapPanel panel = (EnvMapPanel) tabbedPane.getComponentAt(i);
            if (isDifferent(panel.getNameTextField().getText(), envMap.getName(), stringResult))
            {
                envMap.setName(stringResult.getValue());
                frame.documentIsModified = true;
            }
            if (isDifferent(panel.getEnvironmentMapTextField().getText(), envMap.getEnvMapImage(), stringResult))
            {
                envMap.setEnvMapImage(stringResult.getValue());
                frame.documentIsModified = true;
            }
		}
		if (envMaps.size() > tabbedPane.getTabCount())
		{
			// need to trim envMaps
			while (envMaps.size() > tabbedPane.getTabCount())
			{
				envMaps.remove(envMaps.size() - 1);
			}
		}
		else if (envMaps.size() < tabbedPane.getTabCount())
		{
			// need to add to environmentMaps
			while (envMaps.size() < tabbedPane.getTabCount())
			{
	            EnvMapPanel panel = (EnvMapPanel) tabbedPane.getComponentAt(envMaps.size());
				EnvironmentMapping envMap = new EnvironmentMapping();
				envMap.setName(panel.getNameTextField().getText());
				envMap.setEnvMapImage(panel.getEnvironmentMapTextField().getText());
				envMaps.add(envMap);
			}
		}
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
