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

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.Editor;
import utils.circuit.TrackObject;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ObjectProperties extends PropertyPanel
{
	private JButton				addObjectButton		= null;
	private JButton				deleteObjectButton	= null;
	private JTabbedPane			tabbedPane			= null;

	/**
	 *
	 */
	public ObjectProperties(EditorFrame frame)
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
		this.add(getAddObjectButton(), null);
		this.add(getDeleteObjectButton(), null);
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
			addObjectButton.setBounds(10, 230, 100, 25);
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
			deleteObjectButton.setBounds(130, 230, 120, 25);
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
	 * This method initializes tabbedPane
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getTabbedPane()
	{
		if (tabbedPane == null)
		{
			tabbedPane = new JTabbedPane();
			tabbedPane.setBounds(10, 10, 460, 210);

			Vector<TrackObject> objects = Editor.getProperties().getObjects();

			for (int i = 0; i < objects.size(); i++)
	        {
				TrackObject object = objects.elementAt(i);
				tabbedPane.addTab(object.getName(), null, new ObjectPanel(object), null);
			}
		}
		return tabbedPane;
	}

	private class ObjectPanel extends JPanel
	{
		private JLabel				nameLabel				= null;
		private JTextField 			nameTextField			= null;
		private JLabel				objectLabel				= null;
		private JTextField			objectTextField			= null;
		private JLabel				colorLabel				= null;
		private JTextField			colorTextField			= null;
		private JLabel				orientationTypeLabel	= null;
		private JComboBox<String>	orientationTypeComboBox	= null;
		private JLabel				orientationLabel		= null;
		private JTextField			orientationTextField	= null;
		private JLabel				deltaHeightLabel		= null;
		private JTextField			deltaHeightTextField	= null;
		private JLabel				deltaVertLabel			= null;
		private JTextField			deltaVertTextField		= null;

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
			nameLabel = new JLabel();
			objectLabel = new JLabel();
			colorLabel = new JLabel();
			orientationTypeLabel = new JLabel();
			orientationLabel = new JLabel();
			deltaHeightLabel = new JLabel();
			deltaVertLabel = new JLabel();
			nameLabel.setBounds(10, 10, 160, 20);
			nameLabel.setText("Name");
			objectLabel.setBounds(10, 35, 160, 20);
			objectLabel.setText("Object");
			colorLabel.setBounds(10, 60, 160, 20);
			colorLabel.setText("Color");
			orientationTypeLabel.setBounds(10, 85, 160, 20);
			orientationTypeLabel.setText("Orientation Type");
			orientationLabel.setBounds(10, 110, 160, 20);
			orientationLabel.setText("Orientation");
			deltaHeightLabel.setBounds(10, 135, 160, 20);
			deltaHeightLabel.setText("Delta Height");
			deltaVertLabel.setBounds(10, 160, 160, 20);
			deltaVertLabel.setText("Delta Vert");
			add(nameLabel);
			add(objectLabel);
			add(colorLabel);
			add(orientationTypeLabel);
			add(orientationLabel);
			add(deltaHeightLabel);
			add(deltaVertLabel);
			setLayout(null);
			add(getNameTextField(), null);
			add(getObjectTextField(), null);
			add(getColorTextField(), null);
			add(getOrientationTypeComboBox(), null);
			add(getOrientationTextField(), null);
			add(getDeltaHeightTextField(), null);
			add(getDeltaVertTextField(), null);

			getNameTextField().setText(object.getName());
			getObjectTextField().setText(object.getObject());
			getColorTextField().setText(getString(object.getColor()));
			getOrientationTypeComboBox().setSelectedItem(getString(object.getOrientationType()));
			getOrientationTextField().setText(getString(object.getOrientation()));
			getDeltaHeightTextField().setText(getString(object.getDeltaHeight()));
			getDeltaVertTextField().setText(getString(object.getDeltaVert()));
		}

		private String getString(double value)
		{
			if (!Double.isNaN(value))
				return String.valueOf(value);

			return null;
		}
		private String getString(int value)
		{
			if (value != (Integer.MAX_VALUE))
				return "0x" + Integer.toHexString(value).toUpperCase();

			return null;
		}
		private String getString(String string)
		{
			if (string == null || string.isEmpty())
				return "none";

			return string;
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
				nameTextField.setBounds(120, 10, 100, 20);
			}
			return nameTextField;
		}

		public JTextField getObjectTextField()
		{
			if (objectTextField == null)
			{
				objectTextField = new JTextField();
				objectTextField.setBounds(120, 35, 100, 20);
			}
			return objectTextField;
		}

		public JTextField getColorTextField()
		{
			if (colorTextField == null)
			{
				colorTextField = new JTextField();
				colorTextField.setBounds(120, 60, 100, 20);
			}
			return colorTextField;
		}

		public JComboBox<String> getOrientationTypeComboBox()
		{
			if (orientationTypeComboBox == null)
			{
				String[] items = {"none", "random", "standard"};
				orientationTypeComboBox = new JComboBox<String>(items);
				orientationTypeComboBox.setBounds(120, 85, 120, 20);
			}
			return orientationTypeComboBox;
		}

		public JTextField getOrientationTextField()
		{
			if (orientationTextField == null)
			{
				orientationTextField = new JTextField();
				orientationTextField.setBounds(120, 110, 100, 20);
			}
			return orientationTextField;
		}

		public JTextField getDeltaHeightTextField()
		{
			if (deltaHeightTextField == null)
			{
				deltaHeightTextField = new JTextField();
				deltaHeightTextField.setBounds(120, 135, 100, 20);
			}
			return deltaHeightTextField;
		}

		public JTextField getDeltaVertTextField()
		{
			if (deltaVertTextField == null)
			{
				deltaVertTextField = new JTextField();
				deltaVertTextField.setBounds(120, 160, 100, 20);
			}
			return deltaVertTextField;
		}
	}

	/**
	 *
	 */
	public void exit()
	{
		String stringResult = null;
		MutableDouble doubleResult = new MutableDouble(Double.NaN);
		MutableInteger integerResult = new MutableInteger(Integer.MAX_VALUE);
		Vector<TrackObject> objects = Editor.getProperties().getObjects();
		int minCount = Integer.min(objects.size(), tabbedPane.getTabCount());
		if (objects.size() != tabbedPane.getTabCount())
		{
			frame.documentIsModified = true;
		}
		for (int i = 0; i < minCount; i++)
        {
            TrackObject object = objects.elementAt(i);
            ObjectPanel panel = (ObjectPanel) tabbedPane.getComponentAt(i);

            if (isDifferent(panel.getNameTextField().getText(), object.getName(), stringResult))
            {
                object.setName(panel.getNameTextField().getText());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getObjectTextField().getText(), object.getObject(), stringResult))
            {
                object.setObject(panel.getObjectTextField().getText());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getColorTextField().getText(), object.getColor(), integerResult))
            {
                object.setColor(integerResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getOrientationTypeComboBox().getSelectedItem().toString(), object.getOrientationType(), stringResult))
            {
                object.setOrientationType(stringResult);
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getOrientationTextField().getText(), object.getOrientation(), doubleResult))
            {
                object.setOrientation(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getDeltaHeightTextField().getText(), object.getDeltaHeight(), doubleResult))
            {
                object.setDeltaHeight(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getDeltaVertTextField().getText(), object.getDeltaVert(), doubleResult))
            {
                object.setDeltaVert(doubleResult.getValue());
                frame.documentIsModified = true;
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

				object.setName(panel.getNameTextField().getText());
				object.setObject(panel.getObjectTextField().getText());
				object.setColor(getInteger(panel.getColorTextField().getText()));
				object.setOrientationType(getString(panel.getOrientationTypeComboBox().getSelectedItem().toString()));
				object.setOrientation(getDouble(panel.getOrientationTextField().getText()));
				object.setDeltaHeight(getDouble(panel.getDeltaHeightTextField().getText()));
				object.setDeltaVert(getDouble(panel.getDeltaVertTextField().getText()));

				objects.add(object);
			}
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
