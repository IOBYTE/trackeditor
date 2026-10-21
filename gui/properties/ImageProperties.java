/*
 *   ImageProperties.java
 *   Created on 27 ??? 2005
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
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.EditorFrame;
import utils.Editor;
import utils.MutableDouble;
import utils.MutableString;

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
	private JButton		pathButton				= null;
	private JLabel 		imageScaleLabel			= new JLabel();
	private JTextField	imageScaleTextField		= new JTextField();
	private JLabel 		imageOffsetXLabel		= new JLabel();
	private JTextField	imageOffsetXTextField	= new JTextField();
	private JLabel 		imageOffsetYLabel		= new JLabel();
	private JTextField	imageOffsetYTextField	= new JTextField();

	// what an empty field means
	private static final String	DEFAULT_SCALE	= "1";
	private static final String	DEFAULT_OFFSET	= "0";

	/**
	 *
	 */
	public ImageProperties(EditorFrame editorFrame)
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

		addLabel(this, 0, pathLabel, "Path", 80);
		addLabel(this, 1, imageScaleLabel, "Image scale", 80);
		addLabel(this, 2, imageOffsetXLabel, "Image Offset X", 80);
		addLabel(this, 3, imageOffsetYLabel, "Image Offset Y", 80);

		addTextField(this, 0, pathTextField, Editor.getProperties().getImage(), 100, 335);
		addTextField(this, 1, imageScaleTextField, Editor.getProperties().getImageScale(), 100, 125);
		addTextField(this, 2, imageOffsetXTextField, Editor.getProperties().getImgOffset().getX(), 100, 125);
		addTextField(this, 3, imageOffsetYTextField, Editor.getProperties().getImgOffset().getY(), 100, 125);

		this.add(getPathButton(), null);
	}

	/**
	 * This method initializes pathButton
	 *
	 * @return javax.swing.JButton
	 */
	public JButton getPathButton()
	{
		if (pathButton == null)
		{
			pathButton = new JButton();
			pathButton.setBounds(440, 9, 80, 25);
			pathButton.setText("Browse");
			pathButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					selectPath();
				}
			});
		}
		return pathButton;
	}

	/**
	 *
	 */
	protected void selectPath()
	{
		Path trackPath = Paths.get(Editor.getProperties().getPath());
		Path filename;
		try
		{
			filename = Paths.get(pathTextField.getText());
		}
		catch (InvalidPathException e)
		{
			// a name with characters the file system doesn't allow: start in the track directory
			filename = trackPath.resolve("image.png");
		}
		Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFiles(null);
		fc.setSelectedFile(null);
		fc.rescanCurrentDirectory();
		fc.setApproveButtonMnemonic(0);
		fc.setDialogTitle("Background image file selection");
		fc.setFileHidingEnabled(false);
		fc.setVisible(true);
		// check parent directory
		if (filename.getParent() == null || filename.getParent().toString().equals(trackPath.toString()))
		{
			// use track directory
			fc.setCurrentDirectory(new File(trackPath.toString()));
		}
		else
		{
			// try to use actual directory
			File file = new File(filename.getParent().toString());
			if (file.exists())
			{
				// use file directory
				fc.setCurrentDirectory(file);
			}
			else
			{
				// directory doesn't exist so use track directory
				fc.setCurrentDirectory(new File(trackPath.toString()));
			}
		}
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG images", "png");
		fc.addChoosableFileFilter(filter);
		int result = fc.showOpenDialog(this);
		UIManager.put("FileChooser.readOnly", old);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			Path selectedFile = Paths.get(fc.getSelectedFile().toString());

			// remove directory if same as track directory
			if (selectedFile.getParent().toString().equals(trackPath.toString()))
			{
				selectedFile = selectedFile.getFileName();
			}

			pathTextField.setText(selectedFile.toString());
		}
	}

	private static String orDefault(String text, String value)
	{
		if (text == null || text.trim().isEmpty())
			return value;

		return text.trim();
	}

	/**
	 * Checks that the image scale is greater than 0 (empty means 1).
	 * Otherwise shows the field, tells the user and returns false.
	 */
	public boolean checkScale(java.awt.Component parent, JTabbedPane tabs)
	{
		double scale = getDouble(orDefault(imageScaleTextField.getText(), DEFAULT_SCALE));
		if (scale > 0)
			return true;

		tabs.setSelectedComponent(this);
		JOptionPane.showMessageDialog(parent,
			"Image > Image scale: \"" + imageScaleTextField.getText().trim() + "\" must be greater than 0.\n\n" +
			"Fix it or clear the field to use 1.",
			"Properties", JOptionPane.ERROR_MESSAGE);
		imageScaleTextField.requestFocusInWindow();
		imageScaleTextField.selectAll();
		return false;
	}

	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();
		boolean reload = false;

		if (isDifferent(pathTextField.getText(), Editor.getProperties().getImage(), stringResult))
		{
			Editor.getProperties().setImage(stringResult.getValue());
			getEditorFrame().documentIsModified = true;
			reload = true;
		}

		if (isDifferent(orDefault(imageScaleTextField.getText(), DEFAULT_SCALE),
			Editor.getProperties().getImageScale(), doubleResult))
		{
			Editor.getProperties().setImageScale(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
			reload = true;
		}

		if (isDifferent(orDefault(imageOffsetXTextField.getText(), DEFAULT_OFFSET),
			Editor.getProperties().getImgOffset().getX(), doubleResult))
		{
			Editor.getProperties().getImgOffset().setX(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(orDefault(imageOffsetYTextField.getText(), DEFAULT_OFFSET),
			Editor.getProperties().getImgOffset().getY(), doubleResult))
		{
			Editor.getProperties().getImgOffset().setY(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		// the view loads the image and works out its size from the scale
		if (reload)
			getEditorFrame().getCircuitView().setBackgroundImage(Editor.getProperties().getImage());
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
