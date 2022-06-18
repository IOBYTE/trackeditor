/*
 *   ImageProperties.java
 *   Created on 27 ��� 2005
 *
 *    The ImageProperties.java is part of TrackEditor-0.3.1.
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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import gui.EditorFrame;
import utils.Editor;

/**
 * @author babis
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ImageProperties extends PropertyPanel
{
	private JLabel		pathLabel				= new JLabel();
	private JTextField	pathTextField			= new JTextField();
	private JButton		browseButton			= null;
	private JLabel 		imageScaleLabel			= new JLabel();
	private JTextField	imageScaleTextField		= new JTextField();

	private final String sep = System.getProperty("file.separator");

	/**
	 *
	 */
	public ImageProperties(EditorFrame frame)
	{
		super(frame);
		initialize();
	}

	/**
	 *
	 */
	private void initialize()
	{
		setLayout(null);
		setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));

		addLabel(this, 0, imageScaleLabel, "Path", 60);
		addLabel(this, 1, pathLabel, "Image scale", 60);

		addTextField(this, 0, pathTextField, Editor.getProperties().getImage(), 90, 295);
		addTextField(this, 1, imageScaleTextField, Editor.getProperties().getImageScale(), 90, 50);

		this.add(getBrowseButton(), null);
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
			browseButton.setBounds(390, 7, 80, 25);
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
	 *
	 */
	protected void selectPath()
	{
		String tmp = "";
		String filename = Editor.getProperties().getImage();
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle("Background image selection");
		fc.setVisible(true);
		if(Editor.getProperties().getImage().equals(""))
		{
			fc.setCurrentDirectory(new File(System.getProperty("user.dir") +sep+ "tracks"));
		}
		else
		{
			String tmpFile = Editor.getProperties().getImage().substring(0,Editor.getProperties().getImage().lastIndexOf(sep));
			File file = new File(tmpFile);
			fc.setCurrentDirectory(file);
		}
		int result = fc.showDialog(this, "Ok");
		if (result == JFileChooser.APPROVE_OPTION)
		{
			pathTextField.setText(fc.getSelectedFile().toString());
		}
	}

	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();

		if (isDifferent(pathTextField.getText(), Editor.getProperties().getImage(), stringResult))
		{
			Editor.getProperties().setImage(stringResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(imageScaleTextField.getText(),
			Editor.getProperties().getImageScale(), doubleResult))
		{
			Editor.getProperties().setImageScale(doubleResult.getValue());
			frame.documentIsModified = true;
		}
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
