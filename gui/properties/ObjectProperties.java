/*
 *   ObjectProperties.java
 *   Created on 14 June 2022
 *
 *    The ObjectProperties.java is part of TrackEditor-0.7.0.
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.EditorFrame;
import utils.Editor;
import utils.MutableDouble;
import utils.MutableInteger;
import utils.MutableString;
import utils.circuit.TrackObject;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ObjectProperties extends PropertyPanel
{
	static private TrackObject	objectCopy			= null;
	
	private	Boolean				defaultObjects		= false;
	private JButton				addObjectButton		= null;
	private JButton				deleteObjectButton	= null;
	private	JButton				copyObjectButton	= null;
	private	JButton				pasteObjectButton	= null;
	private JTabbedPane			tabbedPane			= null;

	/**
	 *
	 */
	public ObjectProperties(EditorFrame editorFrame, Boolean defaultObjects)
	{
		super(editorFrame);
		this.defaultObjects = defaultObjects;
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
		
		if (!defaultObjects)
		{
			this.add(getAddObjectButton(), null);
			this.add(getDeleteObjectButton(), null);
			this.add(getPasteObjectButton(), null);
		}
		this.add(getCopyObjectButton(), null);
	}

	/**
	 * This method initializes addObjectpingButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getAddObjectButton()
	{
		if (addObjectButton == null)
		{
			addObjectButton = new JButton();
			addObjectButton.setBounds(10, 414, 120, 25);
			addObjectButton.setText("Add Object");
			addObjectButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					String name = "object" + (tabbedPane.getTabCount() + 1);
					TrackObject object = new TrackObject();

					object.setName(name);

					tabbedPane.addTab(name, null, new ObjectPanel(object), null);
					tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
				}
			});
		}
		return addObjectButton;
	}

	/**
	 * This method initializes deleteObjectButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteObjectButton()
	{
		if (deleteObjectButton == null)
		{
			deleteObjectButton = new JButton();
			deleteObjectButton.setBounds(140, 414, 120, 25);
			deleteObjectButton.setText("Delete Object");
			deleteObjectButton.addActionListener(new java.awt.event.ActionListener()
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
		return deleteObjectButton;
	}

	/**
	 * This method initializes copyObjectButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCopyObjectButton()
	{
		if (copyObjectButton == null)
		{
			copyObjectButton = new JButton();
			copyObjectButton.setBounds(270, 414, 120, 25);
			copyObjectButton.setText("Copy Object");
			copyObjectButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					objectCopy = new TrackObject();
					setObjectFromPanel(objectCopy, (ObjectPanel) getTabbedPane().getSelectedComponent());
				}
			});
		}
		return copyObjectButton;
	}

	/**
	 * This method initializes pasteObjectButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getPasteObjectButton()
	{
		if (pasteObjectButton == null)
		{
			pasteObjectButton = new JButton();
			pasteObjectButton.setBounds(400, 414, 120, 25);
			pasteObjectButton.setText("Paste Object");
			pasteObjectButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (objectCopy != null)
					{
						if (getTabbedPane().getSelectedIndex() == -1)
						{
							String name = "object1";
							TrackObject object = new TrackObject();

							object.setName(name);

							tabbedPane.addTab(name, null, new ObjectPanel(object), null);
							tabbedPane.setSelectedIndex(0);
						}
							
						setPanelFromObject(objectCopy, (ObjectPanel) getTabbedPane().getSelectedComponent());
					}
				}
			});
		}
		return pasteObjectButton;
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
			tabbedPane.setBounds(10, 10, 510, 394);

			Vector<TrackObject> objects = null;
			if (defaultObjects)
			{
				objects = getEditorFrame().getDefaultObjects();
			}
			else
			{
				objects = getEditorFrame().getTrackData().getObjects();	
			}

			for (int i = 0; i < objects.size(); i++)
	        {
				TrackObject object = objects.elementAt(i);
				tabbedPane.addTab(object.getName(), null, new ObjectPanel(object), null);
			}
			SwingUtilities.invokeLater( new Runnable()
			{
				public void run()
				{
					int lastTab = getEditorFrame().getProject().getPropertiesEditorObjectTab();
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

	private class ObjectPanel extends JPanel
	{
		private JLabel				nameLabel				= new JLabel();
		private JTextField 			nameTextField			= new JTextField();
		private JLabel				objectLabel				= new JLabel();
		private JTextField			objectTextField			= new JTextField();
		private JLabel				commentLabel			= new JLabel();
		private JTextField			commentTextField		= new JTextField();
		private JLabel				colorLabel				= new JLabel();
		private JTextField			colorTextField			= new JTextField();
		private JLabel				orientationTypeLabel	= new JLabel();
		private JComboBox<String>	orientationTypeComboBox	= null;
		private JLabel				orientationLabel		= new JLabel();
		private JTextField			borderDistanceTextField	= new JTextField();
		private JLabel				borderDistanceLabel		= new JLabel();
		private JTextField			orientationTextField	= new JTextField();
		private JLabel				deltaHeightLabel		= new JLabel();
		private JTextField			deltaHeightTextField	= new JTextField();
		private JLabel				deltaVertLabel			= new JLabel();
		private JTextField			deltaVertTextField		= new JTextField();
		private JLabel				scaleTypeLabel			= new JLabel();
		private JComboBox<String>	scaleTypeComboBox		= null;
		private JLabel				scaleLabel				= new JLabel();
		private JTextField			scaleTextField			= new JTextField();
		private JLabel				scaleMinLabel			= new JLabel();
		private JTextField			scaleMinTextField		= new JTextField();
		private JLabel				scaleMaxLabel			= new JLabel();
		private JTextField			scaleMaxTextField		= new JTextField();		
		private JLabel				useMaterialLabel		= new JLabel();
		private JComboBox<String>	useMaterialComboBox		= null;
		private JButton				objectButton			= null;
		private JButton				colorButton				= null;
		
		private double				lastScale				= 1.0;
		private double				lastScaleMin			= 0.5;
		private double				lastScaleMax			= 2.0;

		private final String sep = System.getProperty("file.separator");

		/**
		 *
		 */
		public ObjectPanel(TrackObject object)
		{
			super();
			initialize(object);
		}

		/**
		 *
		 */
		private void initialize(TrackObject object)
		{
			if (object.getScaleType() != null && object.getScaleType().equals("random"))
			{
				lastScaleMin = object.getScaleMin();
				lastScaleMax = object.getScaleMax();
			}
			else if (object.getScaleType() != null && object.getScaleType().equals("fixed"))
			{
				lastScale = object.getScale();
			}

			setLayout(null);

			addLabel(this, 0, nameLabel, "Name", 160);
			addLabel(this, 1, objectLabel, "Object", 160);			
			addLabel(this, 2, commentLabel, "Comment", 160);
			addLabel(this, 3, colorLabel, "Color", 160);
			addLabel(this, 4, orientationTypeLabel, "Orientation Type", 160);
			addLabel(this, 5, borderDistanceLabel, "Border Distance", 160);
			addLabel(this, 6, orientationLabel, "Orientation", 160);
			addLabel(this, 7, deltaHeightLabel, "Delta Height", 160);
			addLabel(this, 8, deltaVertLabel, "Delta Vert", 160);
			addLabel(this, 9, scaleTypeLabel, "Scale Type", 160);
			addLabel(this, 10, scaleLabel, "Scale", 160);
			addLabel(this, 11, scaleMinLabel, "Scale Min", 160);
			addLabel(this, 12, scaleMaxLabel, "Scale Max", 160);

			addTextField(this, 0, nameTextField, object.getName(), 120, 125);
			addTextField(this, 1, objectTextField, object.getObject(), 120, 290);
			addTextField(this, 2, commentTextField, object.getComment(), 120, 380);
			addTextField(this, 3, colorTextField, toHexString(object.getColor()), 120, 125);

			add(getOrientationTypeComboBox(), null);
			getOrientationTypeComboBox().setSelectedItem(toNoneString(object.getOrientationType()));

			addTextField(this, 5, borderDistanceTextField, object.getBorderDistance(), 120, 125);
			addTextField(this, 6, orientationTextField, object.getOrientation(), 120, 125);
			addTextField(this, 7, deltaHeightTextField, object.getDeltaHeight(), 120, 125);
			addTextField(this, 8, deltaVertTextField, object.getDeltaVert(), 120, 125);

			add(getScaleTypeComboBox(), null);
			getScaleTypeComboBox().setSelectedItem(toNoneString(object.getScaleType()));

			addTextField(this, 10, scaleTextField, object.getScale(), 120, 125);
			addTextField(this, 11, scaleMinTextField, object.getScaleMin(), 120, 125);
			addTextField(this, 12, scaleMaxTextField, object.getScaleMax(), 120, 125);

			// only Speed Dreams trackgen reads "use material"
			if (!getEditorFrame().getCarsSportsRacing())
			{
				addLabel(this, 4, useMaterialLabel, "Use Material", 265, 90);
				add(getUseMaterialComboBox(), null);
				getUseMaterialComboBox().setSelectedItem(toNoneString(object.getUseMaterial()));
			}

			if (defaultObjects)
			{
				nameTextField.setEnabled(false);
				objectTextField.setEnabled(false);
				commentTextField.setEnabled(false);
				colorTextField.setEnabled(false);
				orientationTypeComboBox.setEnabled(false);
				orientationTextField.setEnabled(false);
				deltaHeightTextField.setEnabled(false);
				deltaVertTextField.setEnabled(false);
				scaleTypeComboBox.setEnabled(false);
				scaleTextField.setEnabled(false);
				scaleMinTextField.setEnabled(false);
				scaleMaxTextField.setEnabled(false);		
				getUseMaterialComboBox().setEnabled(false);
			}
			else
			{
				add(getObjectButton(), null);
				add(getColorButton(object), null);
			}
			setColorTextFieldColor(new Color(object.getColor()));
			colorTextField.getDocument().addDocumentListener(new DocumentListener()
			{
				public void changedUpdate(DocumentEvent ev)
				{
					changed();
				}
				public void removeUpdate(DocumentEvent ev)
				{
					changed();
				}
				public void insertUpdate(DocumentEvent ev)
				{
					changed();
				}

				public void changed()
				{
					try
					{
						int rgb = Integer.decode(colorTextField.getText());
						Color color = new Color(rgb);
						colorTextField.setBackground(color);
						if ((color.getRed()*0.299 + color.getGreen()*0.587 + color.getBlue()*0.114) > 186)
						{
							colorTextField.setForeground(Color.BLACK);
						}
						else
						{
							colorTextField.setForeground(Color.WHITE);
						}
					}
					catch (Exception ex)
					{
					}
				}
			});
			colorTextField.addKeyListener(new KeyAdapter()
			{
				public void keyTyped(KeyEvent e)
				{
					String text = colorTextField.getText();
					char c = e.getKeyChar();

					// check length
					if (text.length() >= 8)
					{
						e.consume();
						return;
					}

					// check is starts with '0'
					if (text.length() == 0 && c != '0')
					{
						e.consume();
						return;
					}

					// check for valid use of 'x'
					if (text.length() == 1)
					{
						if (!(c == 'x' || c == 'X'))
						{
							e.consume();
						}
						return;
					}

					// check for valid hex digit
					if (!(Character.isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')))
					{
						e.consume();
						return;
					}
				}
			});
		}

		public JComboBox<String> getOrientationTypeComboBox()
		{
			if (orientationTypeComboBox == null)
			{
				String[] items = {"none", "random", "standard", "track", "terrain", "border"};
				orientationTypeComboBox = new JComboBox<String>(items);
				orientationTypeComboBox.setBounds(120, 118, 125, 23);
			}
			return orientationTypeComboBox;
		}

		public JComboBox<String> getUseMaterialComboBox()
		{
			if (useMaterialComboBox == null)
			{
				String[] items = {"none", "yes", "no"};
				useMaterialComboBox = new JComboBox<String>(items);
				useMaterialComboBox.setBounds(360, 118, 80, 23);
				useMaterialComboBox.setToolTipText("Use the object's own material instead of the terrain's");
			}
			return useMaterialComboBox;
		}

		public JComboBox<String> getScaleTypeComboBox()
		{
			if (scaleTypeComboBox == null)
			{
				String[] items = {"none", "random", "fixed"};
				scaleTypeComboBox = new JComboBox<String>(items);
				scaleTypeComboBox.setBounds(120, 255, 125, 23);
				scaleTypeComboBox.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						scaleTypeChanged();
					}
				});
			}
			return scaleTypeComboBox;
		}

		private void scaleTypeChanged()
		{
			if (scaleTextField.isEnabled())
			{
				String text = scaleTextField.getText();
				if (text != null && !text.isEmpty())
				{
					lastScale = getDouble(text);
				}
			}
			else if (scaleMinTextField.isEnabled())
			{
				String text = scaleMinTextField.getText();
				if (text != null && !text.isEmpty())
				{
					lastScaleMin = getDouble(text);
				}
				text = scaleMaxTextField.getText();
				if (text != null && !text.isEmpty())
				{
					lastScaleMax = getDouble(text);			
				}
			}
			
			String type = scaleTypeComboBox.getSelectedItem().toString();
			if (type.equals("none"))
			{
				scaleTextField.setEnabled(false);
				scaleTextField.setText(null);
				scaleMinTextField.setEnabled(false);
				scaleMinTextField.setText(null);
				scaleMaxTextField.setEnabled(false);
				scaleMaxTextField.setText(null);
			}
			else if (type.equals("random"))
			{
				scaleTextField.setEnabled(false);
				scaleTextField.setText(null);
				scaleMinTextField.setEnabled(true);
				setTextField(scaleMinTextField, lastScaleMin);
				scaleMaxTextField.setEnabled(true);					
				setTextField(scaleMaxTextField, lastScaleMax);
			}
			else if (type.equals("fixed"))
			{
				scaleTextField.setEnabled(true);
				setTextField(scaleTextField, lastScale);
				scaleMinTextField.setEnabled(false);
				scaleMinTextField.setText(null);
				scaleMaxTextField.setEnabled(false);
				scaleMaxTextField.setText(null);
			}
		}
		
		/**
		 * This method initializes objectButton
		 *
		 * @return javax.swing.JButton
		 */
		private JButton getObjectButton()
		{
			if (objectButton == null)
			{
				objectButton = new JButton();
				objectButton.setBounds(420, 36, 80, 25);
				objectButton.setText("Browse");
				objectButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						objectFile();
					}
				});
			}
			return objectButton;
		}

		protected void objectFile()
		{
			Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
			UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFiles(null);
			fc.setSelectedFile(null);
			fc.rescanCurrentDirectory();
			fc.setApproveButtonMnemonic(0);
			fc.setDialogTitle("Object file selection");
			fc.setVisible(true);
			fc.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("AC models", "ac");
			fc.addChoosableFileFilter(filter);
			fc.setCurrentDirectory(new File(Editor.getProperties().getPath()));
			int result = fc.showOpenDialog(this);
			UIManager.put("FileChooser.readOnly", old);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				String fileName = fc.getSelectedFile().toString();
				int index = fileName.lastIndexOf(sep);
				String pathToFile = fileName.substring(0, index);
				String pathToObjects = getEditorFrame().getDataDirectory() + sep + "data" + sep + "objects";
				if (pathToFile.equals(Editor.getProperties().getPath()))
					fileName = fileName.substring(index + 1);
				else if (pathToFile.equals(pathToObjects))
					fileName = fileName.substring(pathToObjects.length() + 1);
				objectTextField.setText(fileName);
			}
		}

		class MyColorTracker implements ActionListener
		{
			JColorChooser chooser;
			Color color;

			public MyColorTracker(JColorChooser c)
			{
				chooser = c;
			}

			public void actionPerformed(ActionEvent e)
			{
				color = chooser.getColor();
			}

			public Color getColor()
			{
				return color;
			}
		}

		/**
		 * This method initializes colorButton
		 *
		 * @return javax.swing.JButton
		 */
		private JButton getColorButton(TrackObject object)
		{
			if (colorButton == null)
			{
				colorButton = new JButton();
				colorButton.setBounds(420, 90, 80, 25);
				colorButton.setText("Browse");
				colorButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						JColorChooser colorChooser = new JColorChooser(getColorTextFieldColor());

						// remove all panels except "RGB"
						AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();
						for (AbstractColorChooserPanel panel : panels)
						{
							if (!panel.getDisplayName().equals("RGB"))
							{
								colorChooser.removeChooserPanel(panel);
							}
							else
							{
								panel.setColorTransparencySelectionEnabled(false);
							}
						}

						MyColorTracker ok = new MyColorTracker(colorChooser);
						JDialog dialog = JColorChooser.createDialog(null, "Choose a color", true, colorChooser, ok, null);
						dialog.setVisible(true);

						Color newColor = ok.getColor();
						if (newColor != null)
						{
							setColorTextFieldColor(newColor);
						}
					}
				});
			}
			return colorButton;
		}

		private Color getColorTextFieldColor()
		{
			int rgb = 0;
			try
			{
				rgb = Integer.decode(colorTextField.getText());
			}
			catch (Exception e)
			{
			}
			return new Color(rgb);
		}

		private void setColorTextFieldColor(Color color)
		{
			if (color == null)
			{
				colorTextField.setBackground(Color.WHITE);
				colorTextField.setForeground(Color.BLACK);
				colorTextField.setText(null);
				return;
			}

			colorTextField.setBackground(color);
			if ((color.getRed()*0.299 + color.getGreen()*0.587 + color.getBlue()*0.114) > 186)
			{
				colorTextField.setForeground(Color.BLACK);
			}
			else
			{
				colorTextField.setForeground(Color.WHITE);
			}
			colorTextField.setText(toHexString(color.getRGB() & 0x00FFFFFF));
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

		Vector<TrackObject> objects = getEditorFrame().getTrackData().getObjects();
		int minCount = Math.min(objects.size(), tabbedPane.getTabCount());
		if (objects.size() != tabbedPane.getTabCount())
		{
			getEditorFrame().documentIsModified = true;
		}
		for (int i = 0; i < minCount; i++)
        {
            TrackObject object = objects.elementAt(i);
            ObjectPanel panel = (ObjectPanel) tabbedPane.getComponentAt(i);

            if (isDifferent(panel.nameTextField.getText(), object.getName(), stringResult))
            {
                object.setName(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.objectTextField.getText(), object.getObject(), stringResult))
            {
                object.setObject(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.commentTextField.getText(), object.getComment(), stringResult))
            {
                object.setComment(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.colorTextField.getText(), object.getColor(), integerResult))
            {
                object.setColor(integerResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.getOrientationTypeComboBox().getSelectedItem().toString(), object.getOrientationType(), stringResult))
            {
                object.setOrientationType(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.borderDistanceTextField.getText(), object.getBorderDistance(), doubleResult))
            {
                object.setBorderDistance(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.orientationTextField.getText(), object.getOrientation(), doubleResult))
            {
                object.setOrientation(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.deltaHeightTextField.getText(), object.getDeltaHeight(), doubleResult))
            {
                object.setDeltaHeight(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.deltaVertTextField.getText(), object.getDeltaVert(), doubleResult))
            {
                object.setDeltaVert(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.getScaleTypeComboBox().getSelectedItem().toString(), object.getScaleType(), stringResult))
            {
                object.setScaleType(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.scaleTextField.getText(), object.getScale(), doubleResult))
            {
                object.setScale(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.scaleMinTextField.getText(), object.getScaleMin(), doubleResult))
            {
                object.setScaleMin(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (isDifferent(panel.scaleMaxTextField.getText(), object.getScaleMax(), doubleResult))
            {
                object.setScaleMax(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }

            if (!getEditorFrame().getCarsSportsRacing() &&
                isDifferent(panel.getUseMaterialComboBox().getSelectedItem().toString(), object.getUseMaterial(), stringResult))
            {
                object.setUseMaterial(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }
        }
		if (objects.size() > tabbedPane.getTabCount())
		{
			// need to trim envMaps
			while (objects.size() > tabbedPane.getTabCount())
			{
				objects.remove(objects.size() - 1);
			}
		}
		else if (objects.size() < tabbedPane.getTabCount())
		{
			// need to add to environmentMaps
			while (objects.size() < tabbedPane.getTabCount())
			{
	            ObjectPanel panel = (ObjectPanel) tabbedPane.getComponentAt(objects.size());
				TrackObject object = new TrackObject();
				setObjectFromPanel(object, panel);
				objects.add(object);
			}
		}

		getEditorFrame().getProject().setPropertiesEditorObjectTab(this.tabbedPane.getSelectedIndex());
	}
	
	private void setObjectFromPanel(TrackObject object, ObjectPanel panel)
	{
		object.setName(panel.nameTextField.getText());
		object.setObject(panel.objectTextField.getText());
		object.setColor(getInteger(panel.colorTextField.getText()));
		object.setOrientationType(getString(panel.orientationTypeComboBox.getSelectedItem().toString()));
		object.setBorderDistance(getDouble(panel.borderDistanceTextField.getText()));
		object.setOrientation(getDouble(panel.orientationTextField.getText()));
		object.setDeltaHeight(getDouble(panel.deltaHeightTextField.getText()));
		object.setDeltaVert(getDouble(panel.deltaVertTextField.getText()));
		object.setScaleType(getString(panel.scaleTypeComboBox.getSelectedItem().toString()));
		object.setScale(getDouble(panel.scaleTextField.getText()));
		object.setScaleMin(getDouble(panel.scaleMinTextField.getText()));
		object.setScaleMax(getDouble(panel.scaleMaxTextField.getText()));
		if (!getEditorFrame().getCarsSportsRacing())
			object.setUseMaterial(getString(panel.getUseMaterialComboBox().getSelectedItem().toString()));
	}

	private void setPanelFromObject(TrackObject object, ObjectPanel panel)
	{
		setTextField(panel.nameTextField, object.getName());
		setTextField(panel.objectTextField, object.getObject());
		setTextField(panel.colorTextField, toHexString(object.getColor()));
		panel.orientationTypeComboBox.setSelectedItem(toNoneString(object.getOrientationType()));		
		setTextField(panel.orientationTextField, object.getOrientation());
		setTextField(panel.deltaHeightTextField, object.getDeltaHeight());
		setTextField(panel.deltaVertTextField, object.getDeltaVert());
		panel.scaleTypeComboBox.setSelectedItem(toNoneString(object.getScaleType()));		
		setTextField(panel.scaleTextField, object.getScale());
		setTextField(panel.scaleMinTextField, object.getScaleMin());
		setTextField(panel.scaleMaxTextField, object.getScaleMax());
		panel.getUseMaterialComboBox().setSelectedItem(toNoneString(object.getUseMaterial()));
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
