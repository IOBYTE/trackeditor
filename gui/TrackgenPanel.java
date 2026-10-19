/*
 *   Trackgen.java
 *   Created on 27 ??? 2005
 *
 *    The Trackgen.java is part of TrackEditor-0.3.1.
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
package gui;

import java.awt.Point;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import utils.Editor;

/**
 * @author babis
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TrackgenPanel extends JDialog implements Runnable
{
	private final static String newline 			= "\n";
	private final static String sep					= System.getProperty("file.separator");
	private EditorFrame			editorFrame;
	private String				additionalArgs		= null;
	private Thread 				ac3d 				= new Thread(this);

	private JPanel				panel				= null;
	private JLabel				nameLabel			= null;
	private JLabel				authorLabel			= null;
	private JLabel				fileNameLabel		= null;
	private JLabel				lengthLabel			= null;
	private JLabel				widthLabel			= null;
	private JLabel				xSizeLabel			= null;
	private JLabel				ySizeLabel			= null;
	private JScrollPane			nodesScrollPane		= null;
	private JTextArea			nodesTextArea		= null;
	private JPanel				infoPanel			= null;
	private JLabel				trackgenLabel		= null;
	private JLabel				waitLabel			= null;
	private JScrollPane			errorsScrollPane	= null;
	private JTextArea			errorsTextArea		= null;
	
	public TrackgenPanel(EditorFrame editorFrame, String additionalArgs)
	{
		super();
		this.editorFrame = editorFrame;
		this.additionalArgs = additionalArgs;
		initialize();
		ac3d.start();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		this.setContentPane(getPanel());
		this.setTitle("Trackgen");
		this.setSize(800, 670);
		this.setResizable(false);
		Point p = new Point();
		p.x = editorFrame.getProject().getTrackgenDialogX();
		p.y = editorFrame.getProject().getTrackgenDialogY();
		this.setLocation(p);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public void run()
	{
		String path = Editor.getProperties().getPath();
		String trackName = path.substring(path.lastIndexOf(sep) + 1);
		String category = " -c " + editorFrame.getTrackData().getHeader().getCategory();
		String name = " -n " + trackName;
		String args = category + name;
		
		if (additionalArgs != null)
		{
			args = args + additionalArgs;

			// don't create everything for race line
			if (!args.contains("-r"))
			{
				args += " -a";
			}
		}
		else
		{
			args += " -a";
		}

		System.out.println(args);

		try
		{
			String ls_str;
			String tmp = "";
			String trackgen = editorFrame.getCarsSportsRacing() ? "csr-trackgen" : "sd2-trackgen";
			if (editorFrame.getBinDirectory() != null && !editorFrame.getBinDirectory().isEmpty())
			{
				trackgen = editorFrame.getBinDirectory() + sep + trackgen;
			}
			
			Process ls_proc = Runtime.getRuntime().exec(trackgen + args);
			// get its output (your input) stream
			BufferedReader ls_in = new BufferedReader(new InputStreamReader(ls_proc.getInputStream()));
			BufferedReader ls_err = new BufferedReader(new InputStreamReader(ls_proc.getErrorStream()));

			try
			{
				while (true)
				{
					// done when process terminated and nothing to read
					if (!ls_proc.isAlive() && !ls_in.ready() && !ls_err.ready())
						break;
					
					if (ls_err.ready())
					{
						String str = ls_err.readLine();
						int index = str.indexOf("Error");
						if (index != -1)
						{
							if (!str.contains("not released"))
							{
								append(errorsTextArea, str.substring(index));
							}
						}
						index = str.indexOf("Warning");
						if (index != -1)
						{
							append(errorsTextArea, str.substring(index));
						}
						index = str.indexOf("FATAL:");
						if (index != -1)
						{
							append(errorsTextArea, str.substring(index));
						}
						index = str.indexOf("WARNING:");
						if (index != -1)
						{
							append(errorsTextArea, str.substring(index));
						}
						index = str.indexOf("libpng warning:");
						if (index != -1)
						{
							append(errorsTextArea, str.substring(index));
						}
					}
				
					if (ls_in.ready()) 
					{
						ls_str = ls_in.readLine();
						if (ls_str.indexOf(" ") != -1)
						{
							tmp = ls_str.substring(0, ls_str.indexOf(" "));
							if (tmp.equals("name"))
							{
								nameLabel.setText(ls_str);
							} else if (tmp.equals("authors"))
							{
								this.authorLabel.setText(ls_str);
							} else if (tmp.equals("filename"))
							{
								this.fileNameLabel.setText(ls_str);
							}else if (tmp.equals("length"))
							{
								this.lengthLabel.setText(ls_str);
							}else if (tmp.equals("width"))
							{
								this.widthLabel.setText(ls_str);
							}
							else if (tmp.equals("XSize"))
							{
								this.xSizeLabel.setText(ls_str);
							}else if (tmp.equals("YSize"))
							{
								this.ySizeLabel.setText(ls_str);
							}else if (tmp.equals("FATAL:"))
							{
								append(errorsTextArea, ls_str);
							}else if (tmp.equals("WARNING:"))
							{
								append(errorsTextArea, ls_str);
							}else
							{
								if (ls_str.endsWith(" Nodes"))
								{
									if (nodesTextArea.getText().endsWith(" Nodes"))
									{
										int lastLineBreak = nodesTextArea.getText().lastIndexOf('\n');
										nodesTextArea.replaceRange(ls_str, lastLineBreak + 1, nodesTextArea.getText().length());
									}
									else
									{
										append(nodesTextArea, ls_str);
									}
								}
								else
								{
									append(nodesTextArea, ls_str);
								}
								
								nodesTextArea.setCaretPosition(nodesTextArea.getText().length());
							}
						}
					}
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		} catch (IOException e1)
		{
			JOptionPane.showMessageDialog(this, e1.getLocalizedMessage(), "Export AC3D", JOptionPane.ERROR_MESSAGE);
		}
		this.waitLabel.setText("Track finished");
	}

	private void append(JTextArea textArea, String text)
	{
		if (textArea.getText().length() > 0)
		{
			textArea.append(newline);
		}

		textArea.append(text);		
	}
	
	/**
	 * This method initializes panel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPanel()
	{
		if (panel == null)
		{
			trackgenLabel = new JLabel();
			waitLabel = new JLabel();
			panel = new JPanel();
			panel.setLayout(null);
			trackgenLabel.setBounds(350, 10, 200, 20);
			trackgenLabel.setText("Track data");
			trackgenLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
			waitLabel.setBounds(10, 412, 290, 25);
			waitLabel.setText("Constructing the .ac file. Please wait...");
			panel.add(trackgenLabel, null);
			panel.add(waitLabel, null);
	
			panel.add(getInfoPanel(), null);
			panel.add(getNodesScrollPane(), null);
			panel.add(getErrorsScrollPane(), null);
		}
		return panel;
	}
	/**
	 * This method initializes infoPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getInfoPanel() {
		if (infoPanel == null) {
			infoPanel = new JPanel();
			infoPanel.setLayout(null);
			infoPanel.setBounds(10, 40, 760, 180);
			infoPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
			nameLabel = new JLabel();
			authorLabel = new JLabel();
			fileNameLabel = new JLabel();
			lengthLabel = new JLabel();
			widthLabel = new JLabel();
			xSizeLabel = new JLabel();
			ySizeLabel = new JLabel();
			nameLabel.setText("");
			nameLabel.setBounds(5, 5, 740, 20);
			authorLabel.setText("");
			authorLabel.setBounds(5, 30, 740, 20);
			fileNameLabel.setText("");
			fileNameLabel.setBounds(5, 55, 740, 20);
			lengthLabel.setText("");
			lengthLabel.setBounds(5, 80, 740, 20);
			widthLabel.setText("");
			widthLabel.setBounds(5, 105, 740, 20);
			xSizeLabel.setText("");
			xSizeLabel.setBounds(5, 130, 740, 20);
			ySizeLabel.setText("");
			ySizeLabel.setBounds(5, 155, 740, 20);
			infoPanel.add(ySizeLabel, null);
			infoPanel.add(xSizeLabel, null);
			infoPanel.add(widthLabel, null);
			infoPanel.add(lengthLabel, null);
			infoPanel.add(fileNameLabel, null);
			infoPanel.add(authorLabel, null);
			infoPanel.add(nameLabel, null);
		}
		return infoPanel;
	}

	/**
	 * This method initializes nodesScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getNodesScrollPane()
	{
		if (nodesScrollPane == null)
		{
			nodesScrollPane = new JScrollPane(getNodesTextArea());
			nodesScrollPane.setBounds(10, 230, 760, 180);
			nodesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			nodesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
		    return nodesScrollPane;
		}
		return nodesScrollPane;
	}

	/**
	 * This method initializes nodesTextArea
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextArea getNodesTextArea()
	{
		if (nodesTextArea == null)
		{
			nodesTextArea = new JTextArea();
			nodesTextArea.setLineWrap(false);
			nodesTextArea.setEditable(false);
			nodesTextArea.setVisible(true);
		}
		return nodesTextArea;
	}

	/**
	 * This method initializes errorsScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getErrorsScrollPane()
	{
		if (errorsScrollPane == null)
		{
		    errorsScrollPane = new JScrollPane(getErrorsTextArea());
		    errorsScrollPane.setBounds(10, 440, 760, 180);
		    errorsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    errorsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		}
		return errorsScrollPane;
	}

	/**
	 * This method initializes errorsTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getErrorsTextArea()
	{
		if (errorsTextArea == null)
		{
			errorsTextArea = new JTextArea();
			errorsTextArea.setLineWrap(false);
			errorsTextArea.setEditable(false);
			errorsTextArea.setText("");
		}
		return errorsTextArea;
	}

	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			editorFrame.getProject().setTrackgenDialogX(this.getX());
			editorFrame.getProject().setTrackgenDialogY(this.getY());
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"

