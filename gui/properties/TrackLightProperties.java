/*
 *   TrackLightProperties.java
 *   Created on 15 June 2022
 *
 *    The TrackLightProperties.java is part of TrackEditor-0.7.0.
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.EditorFrame;
import utils.Editor;
import utils.MutableDouble;
import utils.MutableInteger;
import utils.MutableString;
import utils.circuit.TrackLight;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TrackLightProperties extends PropertyPanel
{
	private JButton				addTrackLightButton		= null;
	private JButton				deleteTrackLightButton	= null;
	private JTabbedPane			tabbedPane				= null;

	/**
	 *
	 */
	public TrackLightProperties(EditorFrame editorFrame)
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
		this.add(getAddTrackLightButton(), null);
		this.add(getDeleteTrackLightButton(), null);
	}

	/**
	 * This method initializes addTrackLightpingButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getAddTrackLightButton()
	{
		if (addTrackLightButton == null)
		{
			addTrackLightButton = new JButton();
			addTrackLightButton.setBounds(10, 435, 140, 25);
			addTrackLightButton.setText("Add Track Light");
			addTrackLightButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					String name = uniqueName(tabbedPane, "light");
					TrackLight light = new TrackLight();

					light.setName(name);

					tabbedPane.addTab(name, null, new TrackLightPanel(light), null);
					tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
				}
			});
		}
		return addTrackLightButton;
	}

	/**
	 * This method initializes deleteTrackLightButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteTrackLightButton()
	{
		if (deleteTrackLightButton == null)
		{
			deleteTrackLightButton = new JButton();
			deleteTrackLightButton.setBounds(170, 435, 140, 25);
			deleteTrackLightButton.setText("Delete Track Light");
			deleteTrackLightButton.addActionListener(new java.awt.event.ActionListener()
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
		return deleteTrackLightButton;
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
			tabbedPane.setBounds(10, 10, 510, 415);

			Vector<TrackLight> lights = getEditorFrame().getTrackData().getTrackLights();

			for (int i = 0; i < lights.size(); i++)
	        {
				TrackLight light = lights.elementAt(i);
				tabbedPane.addTab(light.getName(), null, new TrackLightPanel(light), null);
			}
			SwingUtilities.invokeLater( new Runnable()
			{
				public void run()
				{
					int lastTab = getEditorFrame().getProject().getPropertiesEditorLightTab();
					if (lastTab == -1 && tabbedPane.getTabCount() > 0)
					{
						lastTab = 0;
					}
					if (lastTab < tabbedPane.getTabCount())
					{
						tabbedPane.setSelectedIndex(lastTab);
					}
				}
			});
		}
		return tabbedPane;
	}

	private class TrackLightPanel extends JPanel
	{
		private final TrackLight	item;	// what this tab shows and Ok changes
		private JLabel				nameLabel				= new JLabel();
		private JTextField 			nameTextField			= new JTextField();
		private JLabel				roleLabel				= new JLabel();
		private JTextField			roleTextField			= new JTextField();
		private JLabel				topLeftXLabel			= new JLabel();
		private JTextField			topLeftXTextField		= new JTextField();
		private JLabel				topLeftYLabel			= new JLabel();
		private JTextField			topLeftYTextField		= new JTextField();
		private JLabel				topLeftZLabel			= new JLabel();
		private JTextField			topLeftZTextField		= new JTextField();
		private JLabel				bottomRightXLabel		= new JLabel();
		private JTextField			bottomRightXTextField	= new JTextField();
		private JLabel				bottomRightYLabel		= new JLabel();
		private JTextField			bottomRightYTextField	= new JTextField();
		private JLabel				bottomRightZLabel		= new JLabel();
		private JTextField			bottomRightZTextField	= new JTextField();
		private JLabel				textureOnLabel			= new JLabel();
		private JTextField			textureOnTextField		= new JTextField();
		private JButton				textureOnButton			= null;
		private JLabel				textureOffLabel			= new JLabel();
		private JTextField			textureOffTextField		= new JTextField();
		private JButton				textureOffButton		= null;
		private JLabel				indexLabel				= new JLabel();
		private JTextField			indexTextField			= new JTextField();
		private JLabel				redLabel				= new JLabel();
		private JTextField			redTextField			= new JTextField();
		private JLabel				greenLabel				= new JLabel();
		private JTextField			greenTextField			= new JTextField();
		private JLabel				blueLabel				= new JLabel();
		private JTextField			blueTextField			= new JTextField();

		private final String sep = System.getProperty("file.separator");

		/**
		 *
		 */
		public TrackLightPanel(TrackLight light)
		{
			super();
			this.item = light;
			initialize(light);
		}

		/**
		 *
		 */
		private void initialize(TrackLight light)
		{
			setLayout(null);

			addLabel(this, 0, nameLabel, "Name", 160);
			addLabel(this, 1, roleLabel, "Role", 160);
			addLabel(this, 2, topLeftXLabel, "Top Left X", 160);
			addLabel(this, 3, topLeftYLabel, "Top Left Y", 160);
			addLabel(this, 4, topLeftZLabel, "Top Left Z", 160);
			addLabel(this, 5, bottomRightXLabel, "Bottom Right X", 160);
			addLabel(this, 6, bottomRightYLabel, "Bottom Right Y", 160);
			addLabel(this, 7, bottomRightZLabel, "Bottom Right Z", 160);
			addLabel(this, 8, textureOnLabel, "Texture On", 160);
			addLabel(this, 9, textureOffLabel, "Texture Off", 160);
			addLabel(this, 10, indexLabel, "Index", 160);
			addLabel(this, 11, redLabel, "Red", 160);
			addLabel(this, 12, greenLabel, "Green", 160);
			addLabel(this, 13, blueLabel, "Blue", 160);

			addTextField(this, 0, nameTextField, light.getName(), 120, 125);
			markItemName(nameTextField);
			addTextField(this, 1, roleTextField, light.getRole(), 120, 125);
			addTextField(this, 2, topLeftXTextField, light.getTopLeftX(), 120, 125);
			addTextField(this, 3, topLeftYTextField, light.getTopLeftY(), 120, 125);
			addTextField(this, 4, topLeftZTextField, light.getTopLeftZ(), 120, 125);
			addTextField(this, 5, bottomRightXTextField, light.getBottomRightX(), 120, 125);
			addTextField(this, 6, bottomRightYTextField, light.getBottomRightY(), 120, 125);
			addTextField(this, 7, bottomRightZTextField, light.getBottomRightZ(), 120, 125);
			addTextField(this, 8, textureOnTextField, light.getTextureOn(), 120, 290);
			addTextField(this, 9, textureOffTextField, light.getTextureOff(), 120, 290);
			addTextField(this, 10, indexTextField, light.getIndex(), 120, 125);
			addTextField(this, 11, redTextField, light.getRed(), 120, 125);
			addTextField(this, 12, greenTextField, light.getGreen(), 120, 125);
			addTextField(this, 13, blueTextField, light.getBlue(), 120, 125);

			add(getTextureOnButton(), null);
			add(getTextureOffButton(), null);
		}

		private JButton getTextureOnButton()
		{
			if (textureOnButton == null)
			{
				textureOnButton = new JButton();
				textureOnButton.setBounds(420, 225, 80, 25);
				textureOnButton.setText("Browse");
				textureOnButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						textureOnFile();
					}
				});
			}
			return textureOnButton;
		}

		private JButton getTextureOffButton()
		{
			if (textureOffButton == null)
			{
				textureOffButton = new JButton();
				textureOffButton.setBounds(420, 252, 80, 25);
				textureOffButton.setText("Browse");
				textureOffButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						textureOffFile();
					}
				});
			}
			return textureOffButton;
		}

		protected void textureOnFile()
		{
			Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
			UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFiles(null);
			fc.setSelectedFile(null);
			fc.rescanCurrentDirectory();
			fc.setApproveButtonMnemonic(0);
			fc.setDialogTitle("Texture On image file selection");
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
				textureOnTextField.setText(fileName);
			}
		}

		protected void textureOffFile()
		{
			Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
			UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFiles(null);
			fc.setSelectedFile(null);
			fc.rescanCurrentDirectory();
			fc.setApproveButtonMnemonic(0);
			fc.setDialogTitle("Texture Off image file selection");
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
				textureOffTextField.setText(fileName);
			}
		}
	}

	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();
		MutableInteger integerResult = new MutableInteger();

		Vector<TrackLight> lights = getEditorFrame().getTrackData().getTrackLights();
		// each tab keeps the item it shows, so deleting a tab can't move values the
		// tab doesn't show (like the unit of a camera's "to start") to another item
		Vector<TrackLight> tabItems = new Vector<TrackLight>();
		for (int i = 0; i < tabbedPane.getTabCount(); i++)
        {
            TrackLightPanel panel = (TrackLightPanel) tabbedPane.getComponentAt(i);
            TrackLight light = panel.item;

            if (isDifferent(panel.nameTextField.getText(), light.getName(), stringResult))
            {
                light.setName(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.roleTextField.getText(), light.getRole(), stringResult))
            {
                light.setRole(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.topLeftXTextField.getText(), light.getTopLeftX(), doubleResult))
            {
                light.setTopLeftX(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.topLeftYTextField.getText(), light.getTopLeftY(), doubleResult))
            {
                light.setTopLeftY(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.topLeftZTextField.getText(), light.getTopLeftZ(), doubleResult))
            {
                light.setTopLeftZ(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.bottomRightXTextField.getText(), light.getBottomRightX(), doubleResult))
            {
                light.setBottomRightX(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.bottomRightYTextField.getText(), light.getBottomRightY(), doubleResult))
            {
                light.setBottomRightY(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.bottomRightZTextField.getText(), light.getBottomRightZ(), doubleResult))
            {
                light.setBottomRightZ(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.textureOnTextField.getText(), light.getTextureOn(), stringResult))
            {
                light.setTextureOn(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.textureOffTextField.getText(), light.getTextureOff(), stringResult))
            {
                light.setTextureOff(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.indexTextField.getText(), light.getIndex(), integerResult))
            {
                light.setIndex(integerResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.redTextField.getText(), light.getRed(), doubleResult))
            {
                light.setRed(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.greenTextField.getText(), light.getGreen(), doubleResult))
            {
                light.setGreen(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.blueTextField.getText(), light.getBlue(), doubleResult))
            {
                light.setBlue(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }
            tabItems.add(light);
		}
		if (replaceItems(lights, tabItems))
		{
			getEditorFrame().documentIsModified = true;
		}

		getEditorFrame().getProject().setPropertiesEditorLightTab(this.tabbedPane.getSelectedIndex());	
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
