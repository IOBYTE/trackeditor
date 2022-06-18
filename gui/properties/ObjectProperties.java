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
		private JLabel				nameLabel				= new JLabel();
		private JTextField 			nameTextField			= new JTextField();
		private JLabel				objectLabel				= new JLabel();
		private JTextField			objectTextField			= new JTextField();
		private JLabel				colorLabel				= new JLabel();
		private JTextField			colorTextField			= new JTextField();
		private JLabel				orientationTypeLabel	= new JLabel();
		private JComboBox<String>	orientationTypeComboBox	= null;
		private JLabel				orientationLabel		= new JLabel();
		private JTextField			orientationTextField	= new JTextField();
		private JLabel				deltaHeightLabel		= new JLabel();
		private JTextField			deltaHeightTextField	= new JTextField();
		private JLabel				deltaVertLabel			= new JLabel();
		private JTextField			deltaVertTextField		= new JTextField();

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
			setLayout(null);

			addLabel(this, 0, nameLabel, "Name", 160);
			addLabel(this, 1, objectLabel, "Object", 160);
			addLabel(this, 2, colorLabel, "Color", 160);
			addLabel(this, 3, orientationTypeLabel, "Orientation Type", 160);
			addLabel(this, 4, orientationLabel, "Orientation", 160);
			addLabel(this, 5, deltaHeightLabel, "Delta Height", 160);
			addLabel(this, 6, deltaVertLabel, "Delta Vert", 160);

			addTextField(this, 0, nameTextField, object.getName(), 120, 100);
			addTextField(this, 1, objectTextField, object.getObject(), 120, 100);
			addTextField(this, 2, colorTextField, getString(object.getColor()), 120, 100);

			add(getOrientationTypeComboBox(), null);
			getOrientationTypeComboBox().setSelectedItem(getString(object.getOrientationType()));

			addTextField(this, 4, orientationTextField, getString(object.getOrientation()), 120, 100);
			addTextField(this, 5, deltaHeightTextField, getString(object.getDeltaHeight()), 120, 100);
			addTextField(this, 6, deltaVertTextField, getString(object.getDeltaVert()), 120, 100);
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
	}

	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();
		MutableInteger integerResult = new MutableInteger();

		Vector<TrackObject> objects = Editor.getProperties().getObjects();
		int minCount = Math.min(objects.size(), tabbedPane.getTabCount());
		if (objects.size() != tabbedPane.getTabCount())
		{
			frame.documentIsModified = true;
		}
		for (int i = 0; i < minCount; i++)
        {
            TrackObject object = objects.elementAt(i);
            ObjectPanel panel = (ObjectPanel) tabbedPane.getComponentAt(i);

            if (isDifferent(panel.nameTextField.getText(), object.getName(), stringResult))
            {
                object.setName(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.objectTextField.getText(), object.getObject(), stringResult))
            {
                object.setObject(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.colorTextField.getText(), object.getColor(), integerResult))
            {
                object.setColor(integerResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.getOrientationTypeComboBox().getSelectedItem().toString(), object.getOrientationType(), stringResult))
            {
                object.setOrientationType(stringResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.orientationTextField.getText(), object.getOrientation(), doubleResult))
            {
                object.setOrientation(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.deltaHeightTextField.getText(), object.getDeltaHeight(), doubleResult))
            {
                object.setDeltaHeight(doubleResult.getValue());
                frame.documentIsModified = true;
            }

            if (isDifferent(panel.deltaVertTextField.getText(), object.getDeltaVert(), doubleResult))
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

				object.setName(panel.nameTextField.getText());
				object.setObject(panel.objectTextField.getText());
				object.setColor(getInteger(panel.colorTextField.getText()));
				object.setOrientationType(getString(panel.orientationTypeComboBox.getSelectedItem().toString()));
				object.setOrientation(getDouble(panel.orientationTextField.getText()));
				object.setDeltaHeight(getDouble(panel.deltaHeightTextField.getText()));
				object.setDeltaVert(getDouble(panel.deltaVertTextField.getText()));

				objects.add(object);
			}
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
