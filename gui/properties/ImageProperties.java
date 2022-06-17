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
	private JLabel		pathLabel				= null;
	private JTextField	pathTextField			= null;
	private JButton		browseButton			= null;
	private JLabel 		imageScaleLabel			= null;
	private JTextField	imageScaleTextField		= null;

	private String sep = System.getProperty("file.separator");

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
		imageScaleLabel = new JLabel();
		pathLabel = new JLabel();
		this.setLayout(null);
		pathLabel.setBounds(10, 10, 60, 30);
		pathLabel.setText("Path");
		imageScaleLabel.setBounds(10, 50, 90, 30);
		imageScaleLabel.setText("Image scale");
		this.setSize(420, 230);
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		this.add(getPathTextField(), null);
		this.add(getBrowseButton(), null);
		this.add(pathLabel, null);
		this.add(imageScaleLabel, null);
		this.add(getImageScaleTextField(), null);
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
			pathTextField.setBounds(75, 10, 240, 30);
			pathTextField.setText(Editor.getProperties().getImage());
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
			browseButton.setBounds(320, 10, 95, 30);
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
		}else
		{
			String tmpFile = Editor.getProperties().getImage().substring(0,Editor.getProperties().getImage().lastIndexOf(sep));
			File file = new File(tmpFile);
			fc.setCurrentDirectory(file);
		}
		int result = fc.showDialog(this, "Ok");
		if (result == JFileChooser.APPROVE_OPTION)
		{
			getPathTextField().setText(fc.getSelectedFile().toString());
		}
	}

	/**
	 * This method initializes imageScaleTextField
	 *
	 * @return javax.swing.JTextField
	 */
	public JTextField getImageScaleTextField() {
		if (imageScaleTextField == null) {
			imageScaleTextField = new JTextField();
			imageScaleTextField.setBounds(105, 50, 90, 30);
			imageScaleTextField.setText(Double.toString(Editor.getProperties().getImageScale()));
		}
		return imageScaleTextField;
	}
	/**
	 * @param imageScaleTextField The imageScaleTextField to set.
	 */
	public void setImageScaleTextField(JTextField imageScaleTextField)
	{
		this.imageScaleTextField = imageScaleTextField;
	}
	/**
	 * @param pathTextField The pathTextField to set.
	 */
	public void setPathTextField(JTextField pathTextField)
	{
		this.pathTextField = pathTextField;
	}

	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();

		if (isDifferent(getPathTextField().getText(), Editor.getProperties().getImage(), stringResult))
		{
			Editor.getProperties().setImage(stringResult.getValue());
			frame.documentIsModified = true;
		}

		if (isDifferent(getImageScaleTextField().getText(),
			Editor.getProperties().getImageScale(), doubleResult))
		{
			Editor.getProperties().setImageScale(doubleResult.getValue());
			frame.documentIsModified = true;
		}
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
