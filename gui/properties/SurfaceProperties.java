/*
 *   SurfaceProperties.java
 *   Created on 14 June 2022
 *
 *    The SurfaceProperties.java is part of TrackEditor-0.7.0.
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
import javax.swing.JComboBox;
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
import utils.MutableString;
import utils.circuit.Surface;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SurfaceProperties extends PropertyPanel
{
	static private Surface	surfaceCopy			= null;
	
	private	Boolean			defaultSurfaces		= false;
	private	JButton			addSurfaceButton	= null;
	private	JButton			deleteSurfaceButton	= null;
	private	JButton			copySurfaceButton	= null;
	private	JButton			pasteSurfaceButton	= null;
	private	JTabbedPane		tabbedPane			= null;

	/**
	 *
	 */
	public SurfaceProperties(EditorFrame editorFrame, Boolean defaultSurfaces)
	{
		super(editorFrame);
		this.defaultSurfaces = defaultSurfaces;
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
		if (!defaultSurfaces)
		{
			this.add(getAddSurfaceButton(), null);
			this.add(getDeleteSurfaceButton(), null);
			this.add(getPasteSurfaceButton(), null);
		}
		this.add(getCopySurfaceButton(), null);
	}

	/**
	 * This method initializes addSurfacepingButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getAddSurfaceButton()
	{
		if (addSurfaceButton == null)
		{
			addSurfaceButton = new JButton();
			addSurfaceButton.setBounds(10, 758, 120, 25);
			addSurfaceButton.setText("Add Surface");
			addSurfaceButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					String name = "surface" + (tabbedPane.getTabCount() + 1);
					Surface surface = new Surface();

					surface.setName(name);

					tabbedPane.addTab(name, null, new SurfacePanel(surface), null);
					tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
				}
			});
		}
		return addSurfaceButton;
	}

	/**
	 * This method initializes deleteSurfaceButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteSurfaceButton()
	{
		if (deleteSurfaceButton == null)
		{
			deleteSurfaceButton = new JButton();
			deleteSurfaceButton.setBounds(140, 758, 120, 25);
			deleteSurfaceButton.setText("Delete Surface");
			deleteSurfaceButton.addActionListener(new java.awt.event.ActionListener()
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
		return deleteSurfaceButton;
	}

	/**
	 * This method initializes copySurfaceButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCopySurfaceButton()
	{
		if (copySurfaceButton == null)
		{
			copySurfaceButton = new JButton();
			copySurfaceButton.setBounds(270, 758, 120, 25);
			copySurfaceButton.setText("Copy Surface");
			copySurfaceButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					surfaceCopy = new Surface();
					setSurfaceFromPanel(surfaceCopy, (SurfacePanel) getTabbedPane().getSelectedComponent());
				}
			});
		}
		return copySurfaceButton;
	}

	/**
	 * This method initializes pasteSurfaceButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getPasteSurfaceButton()
	{
		if (pasteSurfaceButton == null)
		{
			pasteSurfaceButton = new JButton();
			pasteSurfaceButton.setBounds(400, 758, 120, 25);
			pasteSurfaceButton.setText("Paste Surface");
			pasteSurfaceButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (surfaceCopy != null)
					{
						if (getTabbedPane().getSelectedIndex() == -1)
						{
							String name = "surface1";
							Surface surface = new Surface();

							surface.setName(name);

							tabbedPane.addTab(name, null, new SurfacePanel(surface), null);
							tabbedPane.setSelectedIndex(0);						
						}
						
						setPanelFromSurface(surfaceCopy, (SurfacePanel) getTabbedPane().getSelectedComponent());
					}
				}
			});
		}
		return pasteSurfaceButton;
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
			tabbedPane.setBounds(10, 10, 510, 740);

			Vector<Surface> surfaces;
			if (defaultSurfaces)
			{
				surfaces = getEditorFrame().getDefaultSurfaces();
			}
			else
			{
				surfaces = getEditorFrame().getTrackData().getSurfaces();
			}

			for (int i = 0; i < surfaces.size(); i++)
	        {
				Surface surface = surfaces.elementAt(i);
				tabbedPane.addTab(surface.getName(), null, new SurfacePanel(surface), null);
			}
			SwingUtilities.invokeLater( new Runnable()
			{
				public void run()
				{
					int lastTab = getEditorFrame().getProject().getPropertiesEditorSurfaceTab();					
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

	private class SurfacePanel extends JPanel
	{
		private JLabel				nameLabel						= new JLabel();
		private JTextField 			nameTextField					= new JTextField();
		private JLabel				commentLabel					= new JLabel();
		private JTextField 			commentTextField				= new JTextField();
		private JLabel				colorR1Label					= new JLabel();
		private JTextField			colorR1TextField				= new JTextField();
		private JLabel				colorG1Label					= new JLabel();
		private JTextField			colorG1TextField				= new JTextField();
		private JLabel				colorB1Label					= new JLabel();
		private JTextField			colorB1TextField				= new JTextField();
		private JLabel				colorR2Label					= new JLabel();
		private JTextField			colorR2TextField				= new JTextField();
		private JLabel				colorG2Label					= new JLabel();
		private JTextField			colorG2TextField				= new JTextField();
		private JLabel				colorB2Label					= new JLabel();
		private JTextField			colorB2TextField				= new JTextField();
		private JLabel				textureNameLabel				= new JLabel();
		private JTextField			textureNameTextField			= new JTextField();
		private JLabel				textureTypeLabel				= new JLabel();
		private JComboBox<String>	textureTypeComboBox				= null;
		private JLabel				textureSizeLabel				= new JLabel();
		private JTextField			textureSizeTextField			= new JTextField();
		private JLabel				textureRandomnessLabel			= new JLabel();
		private JTextField			textureRandomnessTextField		= new JTextField();
		private JLabel				textureLinkWithPreviousLabel	= new JLabel();
		private JComboBox<String>	textureLinkWithPreviousComboBox	= null;
		private JLabel				textureStartOnBoundaryLabel		= new JLabel();
		private JComboBox<String>	textureStartOnBoundaryComboBox	= null;
		private JLabel				textureMipMapLabel				= new JLabel();
		private JTextField			textureMipMapTextField			= new JTextField();
		private JLabel				frictionLabel					= new JLabel();
		private JTextField 			frictionTextField				= new JTextField();
		private JLabel				frictionRainLabel				= new JLabel();
		private JTextField 			frictionRainTextField			= new JTextField();
		private JLabel				rollingResistanceLabel			= new JLabel();
		private JTextField 			rollingResistanceTextField		= new JTextField();
		private JLabel				rollingResistanceRainLabel		= new JLabel();
		private JTextField 			rollingResistanceRainTextField	= new JTextField();
		private JLabel				bumpNameLabel					= new JLabel();
		private JTextField 			bumpNameTextField				= new JTextField();
		private JLabel				bumpSizeLabel					= new JLabel();
		private JTextField 			bumpSizeTextField				= new JTextField();
		private JLabel				roughnessLabel					= new JLabel();
		private JTextField 			roughnessTextField				= new JTextField();
		private JLabel				roughnessWavelengthLabel		= new JLabel();
		private JTextField 			roughnessWavelengthTextField	= new JTextField();
		private JLabel				racelineNameLabel				= new JLabel();
		private JTextField 			racelineNameTextField			= new JTextField();
		private JLabel				damageLabel						= new JLabel();
		private JTextField 			damageTextField					= new JTextField();
		private JLabel				reboundLabel					= new JLabel();
		private JTextField 			reboundTextField				= new JTextField();
		private JButton				textureNameButton				= null;
		private JButton				racelineNameButton				= null;
		private JButton				bumpNameButton					= null;

		private final String sep = System.getProperty("file.separator");

		/**
		 *
		 */
		public SurfacePanel(Surface surface)
		{
			super();
			initialize(surface);
		}

		/**
		 *
		 */
		private void initialize(Surface surface)
		{
			setLayout(null);

			addLabel(this, 0, nameLabel, "Name", 180);
			addLabel(this, 1, commentLabel, "Comment", 180);
			addLabel(this, 2, colorR1Label, "Color R1", 180);
			addLabel(this, 3, colorG1Label, "Color G1", 180);
			addLabel(this, 4, colorB1Label, "Color B1", 180);
			addLabel(this, 5, colorR2Label, "Color R2", 180);
			addLabel(this, 6, colorG2Label, "Color G2", 180);
			addLabel(this, 7, colorB2Label, "Color B2", 180);
			addLabel(this, 8, textureNameLabel, "Texture Name", 180);
			addLabel(this, 9, textureTypeLabel, "Texture Type", 180);
			addLabel(this, 10, textureSizeLabel, "Texture Size", 180);
			addLabel(this, 11, textureRandomnessLabel, "Texture Randomness", 180);
			addLabel(this, 12, textureLinkWithPreviousLabel, "Texture Link With Previous", 180);
			addLabel(this, 13, textureStartOnBoundaryLabel, "Texture Start On Boundary", 180);
			addLabel(this, 14, textureMipMapLabel, "Texture MipMap", 180);
			addLabel(this, 15, frictionLabel, "Friction", 180);
			addLabel(this, 16, frictionRainLabel, "Friction Rain", 180);
			addLabel(this, 17, rollingResistanceLabel, "Rolling Resistance", 180);
			addLabel(this, 18, rollingResistanceRainLabel, "Rolling Resistance Rain", 180);
			addLabel(this, 19, bumpNameLabel, "Bump Name", 180);
			addLabel(this, 20, bumpSizeLabel, "Bump Size", 180);
			addLabel(this, 21, roughnessLabel, "Roughness", 180);
			addLabel(this, 22, roughnessWavelengthLabel, "Roughness Wavelength", 180);
			addLabel(this, 23, racelineNameLabel, "Raceline Name", 180);
			addLabel(this, 24, damageLabel, "Damage", 180);
			addLabel(this, 25, reboundLabel, "Rebound", 180);

			addTextField(this, 0, nameTextField, surface.getName(), 190, 125);
			addTextField(this, 1, commentTextField, surface.getComment(), 190, 310);
			addTextField(this, 2, colorR1TextField, surface.getColorR1(), 190, 125);
			addTextField(this, 3, colorG1TextField, surface.getColorG1(), 190, 125);
			addTextField(this, 4, colorB1TextField, surface.getColorB1(), 190, 125);
			addTextField(this, 5, colorR2TextField, surface.getColorR2(), 190, 125);
			addTextField(this, 6, colorG2TextField, surface.getColorG2(), 190, 125);
			addTextField(this, 7, colorB2TextField, surface.getColorB2(), 190, 125);
			addTextField(this, 8, textureNameTextField, surface.getTextureName(), 190, 220);

			add(getTextureTypeComboBox(), null);
			getTextureTypeComboBox().setSelectedItem(toNoneString(surface.getTextureType()));

			addTextField(this, 10, textureSizeTextField, surface.getTextureSize(), 190, 125);
			addTextField(this, 11, textureRandomnessTextField, surface.getTextureRandomness(), 190, 125);

			add(getTextureLinkWithPreviousComboBox(), null);
			getTextureLinkWithPreviousComboBox().setSelectedItem(toNoneString(surface.getTextureLinkWithPrevious()));
			add(getTextureStartOnBoundaryComboBox(), null);
			getTextureStartOnBoundaryComboBox().setSelectedItem(toNoneString(surface.getTextureStartOnBoundary()));

			addTextField(this, 14, textureMipMapTextField, surface.getTextureMipMap(), 190, 125);
			addTextField(this, 15, frictionTextField, surface.getFriction(), 190, 125);
			addTextField(this, 16, frictionRainTextField, surface.getFrictionRain(), 190, 125);
			addTextField(this, 17, rollingResistanceTextField, surface.getRollingResistance(), 190, 125);
			addTextField(this, 18, rollingResistanceRainTextField, surface.getRollingResistanceRain(), 190, 125);
			addTextField(this, 19, bumpNameTextField, surface.getBumpName(), 190, 220);
			addTextField(this, 20, bumpSizeTextField, surface.getBumpSize(), 190, 125);
			addTextField(this, 21, roughnessTextField, surface.getRoughness(), 190, 125);
			addTextField(this, 22, roughnessWavelengthTextField, surface.getRoughnessWavelength(), 190, 125);
			addTextField(this, 23, racelineNameTextField, surface.getRacelineName(), 190, 220);
			addTextField(this, 24, damageTextField, surface.getDammage(), 190, 125);
			addTextField(this, 25, reboundTextField, surface.getRebound(), 190, 125);

			if (defaultSurfaces)
			{
				nameTextField.setEnabled(false);
				commentTextField.setEnabled(false);
				colorR1TextField.setEnabled(false);
				colorG1TextField.setEnabled(false);
				colorB1TextField.setEnabled(false);
				colorR2TextField.setEnabled(false);
				colorG2TextField.setEnabled(false);
				colorB2TextField.setEnabled(false);
				textureNameTextField.setEnabled(false);
				textureTypeComboBox.setEnabled(false);
				textureSizeTextField.setEnabled(false);
				textureRandomnessTextField.setEnabled(false);
				textureLinkWithPreviousComboBox.setEnabled(false);
				textureStartOnBoundaryComboBox.setEnabled(false);
				textureMipMapTextField.setEnabled(false);
				frictionTextField.setEnabled(false);
				frictionRainTextField.setEnabled(false);
				rollingResistanceTextField.setEnabled(false);
				rollingResistanceRainTextField.setEnabled(false);
				bumpNameTextField.setEnabled(false);
				bumpSizeTextField.setEnabled(false);
				roughnessTextField.setEnabled(false);
				roughnessWavelengthTextField.setEnabled(false);
				racelineNameTextField.setEnabled(false);
				damageTextField.setEnabled(false);
				reboundTextField.setEnabled(false);
			}
			else
			{
				add(getTextureNameButton(), null);
				add(getRacelineNameButton(), null);
				add(getBumpNameButton(), null);
			}
		}

		public JComboBox<String> getTextureTypeComboBox()
		{
			if (textureTypeComboBox == null)
			{
				String[] items = {"none", "discrete", "continuous"};
				textureTypeComboBox = new JComboBox<String>(items);
				textureTypeComboBox.setBounds(190, 253, 125, 23);
			}
			return textureTypeComboBox;
		}

		public JComboBox<String> getTextureLinkWithPreviousComboBox()
		{
			if (textureLinkWithPreviousComboBox == null)
			{
				String[] items = {"none", "yes", "no"};
				textureLinkWithPreviousComboBox = new JComboBox<String>(items);
				textureLinkWithPreviousComboBox.setBounds(190, 334, 125, 23);
			}
			return textureLinkWithPreviousComboBox;
		}

		public JComboBox<String> getTextureStartOnBoundaryComboBox()
		{
			if (textureStartOnBoundaryComboBox == null)
			{
				String[] items = {"none", "yes", "no"};
				textureStartOnBoundaryComboBox = new JComboBox<String>(items);
				textureStartOnBoundaryComboBox.setBounds(190, 361, 125, 23);
			}
			return textureStartOnBoundaryComboBox;
		}

		/**
		 * This method initializes textureNameButton
		 *
		 * @return javax.swing.JButton
		 */
		private JButton getTextureNameButton()
		{
			if (textureNameButton == null)
			{
				textureNameButton = new JButton();
				textureNameButton.setBounds(420, 225, 80, 25);
				textureNameButton.setText("Browse");
				textureNameButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						textureNameFile();
					}
				});
			}
			return textureNameButton;
		}

		protected void textureNameFile()
		{
			Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
			UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFiles(null);
			fc.setSelectedFile(null);
			fc.rescanCurrentDirectory();
			fc.setApproveButtonMnemonic(0);
			fc.setDialogTitle("Surface image file selection");
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
				textureNameTextField.setText(fileName);
			}
		}
		
		/**
		 * This method initializes bumpNameButton
		 *
		 * @return javax.swing.JButton
		 */
		private JButton getBumpNameButton()
		{
			if (bumpNameButton == null)
			{
				bumpNameButton = new JButton();
				bumpNameButton.setBounds(420, 522, 80, 25);
				bumpNameButton.setText("Browse");
				bumpNameButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						bumpNameFile();
					}
				});
			}
			return bumpNameButton;
		}

		protected void bumpNameFile()
		{
			Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
			UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFiles(null);
			fc.setSelectedFile(null);
			fc.rescanCurrentDirectory();
			fc.setApproveButtonMnemonic(0);
			fc.setDialogTitle("Surface bump image file selection");
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
				bumpNameTextField.setText(fileName);
			}
		}
		
		/**
		 * This method initializes racelineNameButton
		 *
		 * @return javax.swing.JButton
		 */
		private JButton getRacelineNameButton()
		{
			if (racelineNameButton == null)
			{
				racelineNameButton = new JButton();
				racelineNameButton.setBounds(420, 630, 80, 25);
				racelineNameButton.setText("Browse");
				racelineNameButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						racelineNameFile();
					}
				});
			}
			return racelineNameButton;
		}

		protected void racelineNameFile()
		{
			Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
			UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFiles(null);
			fc.setSelectedFile(null);
			fc.rescanCurrentDirectory();
			fc.setApproveButtonMnemonic(0);
			fc.setDialogTitle("Raceline image file selection");
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
				racelineNameTextField.setText(fileName);
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

		Vector<Surface> surfaces = getEditorFrame().getTrackData().getSurfaces();
		int minCount = Math.min(surfaces.size(), tabbedPane.getTabCount());
		if (surfaces.size() != tabbedPane.getTabCount())
		{
			getEditorFrame().documentIsModified = true;
		}
		for (int i = 0; i < minCount; i++)
        {
            Surface surface = surfaces.elementAt(i);
            SurfacePanel panel = (SurfacePanel) tabbedPane.getComponentAt(i);

            if (isDifferent(panel.nameTextField.getText(), surface.getName(), stringResult))
            {
                surface.setName(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.commentTextField.getText(), surface.getComment(), stringResult))
            {
                surface.setComment(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.colorR1TextField.getText(), surface.getColorR1(), doubleResult))
            {
                surface.setColorR1(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.colorG1TextField.getText(), surface.getColorG1(), doubleResult))
            {
                surface.setColorG1(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.colorB1TextField.getText(), surface.getColorB1(), doubleResult))
            {
                surface.setColorB1(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.colorR2TextField.getText(), surface.getColorR2(), doubleResult))
            {
                surface.setColorR2(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.colorG2TextField.getText(), surface.getColorG2(), doubleResult))
            {
                surface.setColorG2(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.colorB2TextField.getText(), surface.getColorB2(), doubleResult))
            {
                surface.setColorB2(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.textureNameTextField.getText(), surface.getTextureName(), stringResult))
            {
                surface.setTextureName(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.getTextureTypeComboBox().getSelectedItem().toString(), surface.getTextureType(), stringResult))
            {
                surface.setTextureType(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.textureSizeTextField.getText(), surface.getTextureSize(), doubleResult))
            {
                surface.setTextureSize(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.textureRandomnessTextField.getText(), surface.getTextureRandomness(), doubleResult))
            {
                surface.setTextureRandomness(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.getTextureLinkWithPreviousComboBox().getSelectedItem().toString(), surface.getTextureLinkWithPrevious(), stringResult))
            {
                surface.setTextureLinkWithPrevious(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.getTextureStartOnBoundaryComboBox().getSelectedItem().toString(), surface.getTextureStartOnBoundary(), stringResult))
            {
                surface.setTextureStartOnBoundary(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.textureMipMapTextField.getText(), surface.getTextureMipMap(), doubleResult))
            {
                surface.setTextureMipMap(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.frictionTextField.getText(), surface.getFriction(), doubleResult))
            {
                surface.setFriction(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.frictionRainTextField.getText(), surface.getFrictionRain(), doubleResult))
            {
                surface.setFrictionRain(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.rollingResistanceTextField.getText(), surface.getRollingResistance(), doubleResult))
            {
                surface.setRollingResistance(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.rollingResistanceRainTextField.getText(), surface.getRollingResistanceRain(), doubleResult))
            {
                surface.setRollingResistanceRain(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.bumpNameTextField.getText(), surface.getBumpName(), stringResult))
            {
                surface.setBumpName(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.bumpSizeTextField.getText(), surface.getBumpSize(), doubleResult))
            {
                surface.setBumpSize(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.roughnessTextField.getText(), surface.getRoughness(), doubleResult))
            {
                surface.setRoughness(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.roughnessWavelengthTextField.getText(), surface.getRoughnessWavelength(), doubleResult))
            {
                surface.setRoughnessWavelength(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.racelineNameTextField.getText(), surface.getRacelineName(), stringResult))
            {
                surface.setRacelineName(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.damageTextField.getText(), surface.getDammage(), doubleResult))
            {
                surface.setDammage(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.reboundTextField.getText(), surface.getRebound(), doubleResult))
            {
                surface.setRebound(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }
		}
		if (surfaces.size() > tabbedPane.getTabCount())
		{
			// need to trim envMaps
			while (surfaces.size() > tabbedPane.getTabCount())
			{
				surfaces.remove(surfaces.size() - 1);
			}
		}
		else if (surfaces.size() < tabbedPane.getTabCount())
		{
			// need to add to environmentMaps
			while (surfaces.size() < tabbedPane.getTabCount())
			{
	            SurfacePanel panel = (SurfacePanel) tabbedPane.getComponentAt(surfaces.size());
				Surface surface = new Surface();
				setSurfaceFromPanel(surface, panel);
				surfaces.add(surface);
			}
		}

		getEditorFrame().getProject().setPropertiesEditorSurfaceTab(this.tabbedPane.getSelectedIndex());
	}

	private void setSurfaceFromPanel(Surface surface, SurfacePanel panel)
	{
		surface.setName(panel.nameTextField.getText());
		surface.setComment(panel.commentTextField.getText());
		surface.setColorR1(getDouble(panel.colorR1TextField.getText()));
		surface.setColorG1(getDouble(panel.colorG1TextField.getText()));
		surface.setColorB1(getDouble(panel.colorB1TextField.getText()));
		surface.setColorR2(getDouble(panel.colorR2TextField.getText()));
		surface.setColorG2(getDouble(panel.colorG2TextField.getText()));
		surface.setColorB2(getDouble(panel.colorB2TextField.getText()));
		surface.setTextureName(panel.textureNameTextField.getText());
		surface.setTextureType(getString(panel.getTextureTypeComboBox().getSelectedItem().toString()));
		surface.setTextureSize(getDouble(panel.textureSizeTextField.getText()));
		surface.setTextureLinkWithPrevious(getString(panel.getTextureLinkWithPreviousComboBox().getSelectedItem().toString()));
		surface.setTextureStartOnBoundary(getString(panel.getTextureStartOnBoundaryComboBox().getSelectedItem().toString()));
		surface.setTextureMipMap(getDouble(panel.textureMipMapTextField.getText()));
		surface.setFriction(getDouble(panel.frictionTextField.getText()));
		surface.setRollingResistance(getDouble(panel.rollingResistanceTextField.getText()));
		surface.setBumpName(panel.bumpNameTextField.getText());
		surface.setBumpSize(getDouble(panel.bumpSizeTextField.getText()));
		surface.setRoughness(getDouble(panel.roughnessTextField.getText()));
		surface.setRoughnessWavelength(getDouble(panel.roughnessWavelengthTextField.getText()));
		surface.setRacelineName(panel.racelineNameTextField.getText());
		surface.setDammage(getDouble(panel.damageTextField.getText()));
		surface.setRebound(getDouble(panel.reboundTextField.getText()));
	}

	private void setPanelFromSurface(Surface surface, SurfacePanel panel)
	{
		panel.nameTextField.setText(new String(surface.getName()));
		panel.commentTextField.setText(new String(surface.getComment()));
		setTextField(panel.colorR1TextField, surface.getColorR1());
		setTextField(panel.colorG1TextField, surface.getColorG1());
		setTextField(panel.colorB1TextField, surface.getColorB1());
		setTextField(panel.colorR2TextField, surface.getColorR2());
		setTextField(panel.colorG2TextField, surface.getColorG2());
		setTextField(panel.colorB2TextField, surface.getColorB2());
		panel.textureNameTextField.setText(new String(surface.getTextureName()));		
		panel.textureTypeComboBox.setSelectedItem(toNoneString(surface.getTextureType()));		
		setTextField(panel.textureSizeTextField, surface.getTextureSize());
		panel.textureLinkWithPreviousComboBox.setSelectedItem(toNoneString(surface.getTextureLinkWithPrevious()));
		panel.getTextureStartOnBoundaryComboBox().setSelectedItem(toNoneString(surface.getTextureStartOnBoundary()));
		setTextField(panel.textureMipMapTextField, surface.getTextureMipMap());
		setTextField(panel.frictionTextField, surface.getFriction());
		setTextField(panel.rollingResistanceTextField, surface.getRollingResistance());
		panel.bumpNameTextField.setText(new String(surface.getBumpName()));
		setTextField(panel.bumpSizeTextField, surface.getBumpSize());
		setTextField(panel.roughnessTextField, surface.getRoughness());
		setTextField(panel.roughnessWavelengthTextField, surface.getRoughnessWavelength());
		panel.racelineNameTextField.setText(new String(surface.getRacelineName()));
		setTextField(panel.damageTextField, surface.getDammage());
		setTextField(panel.reboundTextField, surface.getRebound());
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
