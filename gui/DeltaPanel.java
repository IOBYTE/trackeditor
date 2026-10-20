/*
 *   Trackgen.java
 *   Created on Aug 26, 2004
 *
 *    The Trackgen.java is part of TrackEditor-0.6.2.
 *
 *    TrackEditor-0.6.2 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.6.2 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.6.2TrackEditor; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import utils.Editor;
import utils.SegmentVector;
import utils.circuit.Curve;
import utils.circuit.Segment;
import utils.circuit.Straight;

import javax.swing.JTextField;

/**
 * @author Charalampos Alexopoulos
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DeltaPanel extends JDialog
{
	private final static String sep	= System.getProperty("file.separator");
    private EditorFrame editorFrame;
    private Thread worker = null;
    // the trackgen process running now, so Cancel can stop it
    private volatile Process process = null;
    private volatile boolean cancelled = false;

    private JPanel jPanel = null;
    private JLabel nameLabel = null;
    private JLabel authorLabel = null;
    private JLabel fileNameLabel = null;
    private JLabel lengthLabel = null;
    private JLabel widthLabel = null;
    private JLabel xSizeLabel = null;
    private JLabel ySizeLabel = null;
    private JPanel jPanel1 = null;
    private JLabel trackgenLabel = null;
    private JLabel waitLabel = null;

    private JButton calcButton = null;
    private JLabel deltaXLabel = null;
    private JLabel deltaYLabel = null;
    private JLabel deltaAngLabel = null;

	private JButton adjustButton = null;
	private JButton lengthButton = null;
	private JTextField jTextField = null;
	
	// results of the last trackgen run, only used by the worker thread
	private double length = Double.NaN;
	private double dX = Double.NaN;
	private double dY = Double.NaN;
	private double angle = Double.NaN;

	/** A failure that stops the current calculation. A null message means it was already reported. */
	private static class TrackgenException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		TrackgenException(String message)
		{
			super(message);
		}
	}
    /**
     *  
     */
    public DeltaPanel(EditorFrame editorFrame, String title, boolean modal)
    {
        super(editorFrame, title, modal);
        this.editorFrame = editorFrame;
        initialize();
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setContentPane(getJPanel());
        this.setTitle("Trackgen");
        this.setSize(379, 415);

    }

    /**
     * Saves the track and runs trackgen -z on it. Called on the worker thread.
     * The labels are updated on the event dispatch thread as the output arrives.
     *
     * @throws TrackgenException when the track can't be saved, trackgen can't be run,
     *         trackgen fails or its output doesn't contain the track data
     */
    private void runTrackgen()
    {
		final String[] trackName = new String[1];
		final String[] trackgen = new String[1];
		final String[] category = new String[1];
		final boolean[] exported = new boolean[1];

		onEdt(() -> {
			JLabel[] labels = {nameLabel, authorLabel, fileNameLabel, lengthLabel, widthLabel,
					xSizeLabel, ySizeLabel, deltaXLabel, deltaYLabel, deltaAngLabel};
			for (JLabel label : labels)
				label.setText(null);
			waitLabel.setText("Calculating track data. Please wait...");

			String path = Editor.getProperties().getPath();
			trackName[0] = path.substring(path.lastIndexOf(sep) + 1);
			trackgen[0] = editorFrame.getCarsSportsRacing() ? "csr-trackgen" : "sd2-trackgen";
			if (editorFrame.getBinDirectory() != null && !editorFrame.getBinDirectory().isEmpty())
				trackgen[0] = editorFrame.getBinDirectory() + sep + trackgen[0];
			category[0] = editorFrame.getTrackData().getHeader().getCategory();

			// don't run trackgen on an old or missing track file
			exported[0] = editorFrame.exportTrack();
		});

		if (!exported[0])
			throw new TrackgenException(null);	// exportTrack already showed why

		length = Double.NaN;
		dX = Double.NaN;
		dY = Double.NaN;
		angle = Double.NaN;

		// the last few lines that weren't track data, to show if trackgen fails
		ArrayDeque<String> otherLines = new ArrayDeque<String>();
		final int maxOtherLines = 15;

		Process proc;
		try
		{
			ProcessBuilder pb = new ProcessBuilder(trackgen[0], "-z", "-c", category[0], "-n", trackName[0]);
			pb.redirectErrorStream(true);
			proc = pb.start();
			process = proc;
			if (cancelled)
				proc.destroy();
		}
		catch (IOException e)
		{
			throw new TrackgenException("Couldn't run " + trackgen[0] + "\n\n" + e.getLocalizedMessage());
		}

		try (BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream())))
		{
			String line;

			while ((line = in.readLine()) != null)
			{
				JLabel label = null;
				int equals = line.indexOf("=");
				String key = equals == -1 ? "" : line.substring(0, equals);

				try
				{
					if (key.equals("name      ")) {
						label = nameLabel;
					} else if (key.equals("authors   ") || key.equals("author    ")) {
						label = authorLabel;
					} else if (key.equals("filename  ")) {
						label = fileNameLabel;
					} else if (key.equals("length    ")) {
						length = Double.parseDouble(line.substring(equals + 1).trim());
						label = lengthLabel;
					} else if (key.equals("width     ")) {
						label = widthLabel;
					} else if (key.equals("XSize     ")) {
						label = xSizeLabel;
					} else if (key.equals("YSize     ")) {
						label = ySizeLabel;
					} else if (key.equals("Delta X   ")) {
						dX = Double.parseDouble(line.substring(equals + 1).trim());
						label = deltaXLabel;
					} else if (key.equals("Delta Y   ")) {
						dY = Double.parseDouble(line.substring(equals + 1).trim());
						label = deltaYLabel;
					} else if (key.equals("Delta Ang ")) {
						angle = Double.parseDouble(line.substring(line.indexOf("(") + 1, line.indexOf(")")).trim());
						label = deltaAngLabel;
					}
				}
				catch (NumberFormatException | IndexOutOfBoundsException e)
				{
					label = null;
				}

				if (label != null)
				{
					final JLabel l = label;
					final String text = line;
					SwingUtilities.invokeLater(() -> l.setText(text));
				}
				else if (!key.isEmpty() || !line.trim().isEmpty())
				{
					if (otherLines.size() == maxOtherLines)
						otherLines.removeFirst();
					otherLines.addLast(line);
				}
			}

			int exitValue = proc.waitFor();

			if (cancelled)
				throw new TrackgenException(null);

			if (exitValue != 0)
				throw new TrackgenException(trackgen[0] + " failed (exit status " + exitValue + ")" + output(otherLines));
		}
		catch (IOException e)
		{
			proc.destroy();
			if (cancelled)
				throw new TrackgenException(null);
			throw new TrackgenException("Couldn't read the output of " + trackgen[0] + "\n\n" + e.getLocalizedMessage());
		}
		catch (InterruptedException e)
		{
			proc.destroy();
			Thread.currentThread().interrupt();
			throw new TrackgenException(null);
		}
		finally
		{
			process = null;
		}

		if (Double.isNaN(length) || Double.isNaN(dX) || Double.isNaN(dY) || Double.isNaN(angle))
			throw new TrackgenException(trackgen[0] + " didn't report the track length and deltas" + output(otherLines));
    }

    private static String output(ArrayDeque<String> lines)
    {
		if (lines.isEmpty())
			return "";

		StringBuilder text = new StringBuilder("\n\n");
		for (String line : lines)
			text.append(line).append("\n");
		return text.toString();
    }

    /**
     * Runs code on the event dispatch thread and waits for it.
     */
    private void onEdt(Runnable code)
    {
		// a cancel is handled on the event dispatch thread too, so checking here
		// means nothing more changes once Cancel was pressed
		Runnable checked = () -> {
			if (cancelled)
				throw new TrackgenException(null);
			code.run();
		};
		if (SwingUtilities.isEventDispatchThread())
		{
			checked.run();
			return;
		}
		try
		{
			SwingUtilities.invokeAndWait(checked);
		}
		catch (InvocationTargetException e)
		{
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException)
				throw (RuntimeException) cause;
			if (cause instanceof Error)
				throw (Error) cause;
			throw new RuntimeException(cause);
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
			throw new TrackgenException(null);
		}
    }

    private void setButtonsEnabled(boolean enabled)
    {
		getCalcButton().setEnabled(enabled);
		getAdjustButton().setEnabled(enabled);
		getLengthButton().setEnabled(enabled);
    }

    /**
     * Runs a calculation on a worker thread so the dialog keeps updating.
     * While it runs a modal "please wait" window blocks the editor, so the
     * track can't be edited, closed or replaced between trackgen runs.
     * Errors are shown on the event dispatch thread.
     */
    private void startCalculation(Runnable calculation)
    {
		if (worker != null && worker.isAlive())
			return;

		setButtonsEnabled(false);
		cancelled = false;

		JDialog busy = new JDialog(this, "Trackgen", true);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> cancelCalculation(cancelButton));
		JPanel busyPanel = new JPanel(new java.awt.BorderLayout(10, 10));
		busyPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
		busyPanel.add(new JLabel("Running trackgen. Please wait..."), java.awt.BorderLayout.CENTER);
		busyPanel.add(cancelButton, java.awt.BorderLayout.EAST);
		busy.setContentPane(busyPanel);
		busy.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		busy.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				cancelCalculation(cancelButton);
			}
		});
		busy.pack();
		busy.setLocationRelativeTo(this);

		worker = new Thread(() -> {
			String error = null;
			boolean failed = false;

			try
			{
				calculation.run();
			}
			catch (TrackgenException e)
			{
				failed = true;
				error = e.getMessage();
			}
			catch (RuntimeException e)
			{
				e.printStackTrace();
				failed = true;
				error = e.toString();
			}

			final boolean f = failed;
			final String message = error;

			SwingUtilities.invokeLater(() -> {
				busy.dispose();
				if (cancelled)
					waitLabel.setText("Calculation cancelled");
				else
					waitLabel.setText(f ? "Calculation failed" : "Calculation finished");
				setButtonsEnabled(true);
				if (message != null && !cancelled)
					JOptionPane.showMessageDialog(this, message, "Delta Dialog", JOptionPane.ERROR_MESSAGE);
				// changes made before a cancel or failure are kept, so show them
				editorFrame.refresh();
			});
		}, "trackgen deltas");

		worker.setDaemon(true);
		worker.start();

		// blocks here, while still handling events, until the worker disposes it
		busy.setVisible(true);
    }

    /**
     * Stops the calculation: the running trackgen is killed and nothing more is changed.
     */
    private void cancelCalculation(JButton cancelButton)
    {
		cancelled = true;
		cancelButton.setEnabled(false);
		Process p = process;
		if (p != null)
			p.destroy();
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel()
    {
        if (jPanel == null)
        {
            trackgenLabel = new JLabel();
            waitLabel = new JLabel();
            jPanel = new JPanel();
            jPanel.setLayout(null);
            trackgenLabel.setBounds(103, 19, 113, 20);
            trackgenLabel.setText("Track data");
            trackgenLabel.setFont(new java.awt.Font("Dialog",
                    java.awt.Font.BOLD, 18));
            waitLabel.setBounds(15, 355, 290, 25);
            waitLabel.setText("");
            jPanel.add(trackgenLabel, null);
            jPanel.add(waitLabel, null);

            jPanel.add(getJPanel1(), null);
            jPanel.add(getCalcButton(), null);
            jPanel.add(getAdjustButton(), null);
            jPanel.add(getLengthButton(), null);
            jPanel.add(getJTextField(), null);
        }
        return jPanel;
    }

    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1()
    {
        if (jPanel1 == null)
        {
            deltaAngLabel = new JLabel();
            deltaYLabel = new JLabel();
            deltaXLabel = new JLabel();
            jPanel1 = new JPanel();
            jPanel1.setLayout(null);
            jPanel1.setBounds(29, 60, 310, 255);
            jPanel1
                    .setBorder(javax.swing.BorderFactory
                            .createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
            nameLabel = new JLabel();
            authorLabel = new JLabel();
            fileNameLabel = new JLabel();
            lengthLabel = new JLabel();
            widthLabel = new JLabel();
            xSizeLabel = new JLabel();
            ySizeLabel = new JLabel();
            nameLabel.setText("");
            nameLabel.setBounds(5, 5, 300, 20);
            nameLabel.setBackground(new java.awt.Color(255,255,153));
            authorLabel.setText("");
            authorLabel.setBounds(5, 30, 300, 20);
            authorLabel.setBackground(new java.awt.Color(255,255,153));
            fileNameLabel.setText("");
            fileNameLabel.setBounds(5, 55, 300, 20);
            lengthLabel.setText("");
            lengthLabel.setBounds(5, 80, 300, 20);
            widthLabel.setText("");
            widthLabel.setBounds(5, 105, 300, 20);
            xSizeLabel.setText("");
            xSizeLabel.setBounds(5, 130, 300, 20);
            ySizeLabel.setText("");
            ySizeLabel.setBounds(5, 155, 300, 20);
            deltaXLabel.setBounds(5, 180, 300, 20);
            deltaXLabel.setText("");
            deltaYLabel.setBounds(5, 205, 300, 20);
            deltaYLabel.setText("");
            deltaAngLabel.setBounds(5, 230, 300, 20);
            deltaAngLabel.setText("");
            jPanel1.setBackground(new java.awt.Color(255,255,153));
            jPanel1.setForeground(new java.awt.Color(51,51,255));
            jPanel1.add(ySizeLabel, null);
            jPanel1.add(xSizeLabel, null);
            jPanel1.add(widthLabel, null);
            jPanel1.add(lengthLabel, null);
            jPanel1.add(fileNameLabel, null);
            jPanel1.add(authorLabel, null);
            jPanel1.add(nameLabel, null);
            jPanel1.add(deltaXLabel, null);
            jPanel1.add(deltaYLabel, null);
            jPanel1.add(deltaAngLabel, null);
        }
        return jPanel1;
    }

    /**
     * This method initializes calcButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCalcButton()
    {
        if (calcButton == null)
        {
            calcButton = new JButton();
            calcButton.setBounds(12, 320, 60, 25);
            calcButton.setText("Calc");
            calcButton.setToolTipText("Calculates the track data");
            calcButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    startCalculation(() -> runTrackgen());
                }
            });
        }
        return calcButton;
    }

	/**
	 * This method initializes adjustButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getAdjustButton() {
		if (adjustButton == null) {
			adjustButton = new JButton();
			adjustButton.setBounds(82, 320, 65, 25);
			adjustButton.setText("Auto");
			adjustButton.setToolTipText("Auto adjust the start/finish segments of the track");
			adjustButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) 
				{  
				    int options = JOptionPane.OK_CANCEL_OPTION;
				    String msg = "This option will change your track permantly.\n"
				        +"The result will be writed on xml file and no undo has implemented yet.\n\n"
				        +"If you decide to continue keep in mind that the adjust method is\n"
				        +"experimental and not propertly tested. As far as i know doing the job but\n"
				        +"need to use the adjust button two or three times";
				    int out = JOptionPane.showConfirmDialog(DeltaPanel.this,msg,"Warning",options);
				    if(out != JOptionPane.OK_OPTION)
				        return;
				    startCalculation(() -> adjust());
				}
			});
		}
		return adjustButton;
	}
	/**
	 * This method initializes lengthButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getLengthButton() {
		if (lengthButton == null) {
			lengthButton = new JButton();
			lengthButton.setBounds(169, 320, 85, 25);
			lengthButton.setText("Length");
			lengthButton.setToolTipText("Adjust the length of the track to match the value in the text field on the rigth");
			lengthButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) 
				{    
				    double parsed;
				    try
				    {
				        parsed = Double.parseDouble(getJTextField().getText().trim());
				    }
				    catch (NumberFormatException ex)
				    {
				        parsed = Double.NaN;
				    }
				    final double newLength = parsed;
				    if (!(newLength > 0) || Double.isInfinite(newLength))
				    {
				        JOptionPane.showMessageDialog(DeltaPanel.this,
				            "Enter the new track length in the field next to the Length button.",
				            "Delta Dialog", JOptionPane.ERROR_MESSAGE);
				        return;
				    }

				    int options = JOptionPane.OK_CANCEL_OPTION;
				    String msg = "This option will change your track permantly.\n"
				        +"The result will be writed on xml file and no undo has implemented yet.\n\n"
				        +"If you decide to continue keep in mind that the adjust method is\n"
				        +"experimental and not propertly tested.";
				    int out = JOptionPane.showConfirmDialog(DeltaPanel.this,msg,"Warning",options);
				    if(out != JOptionPane.OK_OPTION)
				        return;

				    startCalculation(() -> adjustLength(newLength));
				}
			});
		}
		return lengthButton;
	}
	
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setBounds(266, 320, 85, 25);
			jTextField.setToolTipText("The length value used from Length button");
		}
		return jTextField;
	}
	
	/**
	 * Scales the track to the new length. Called on the worker thread.
	 */
	private void adjustLength(double newLength)
	{
	    runTrackgen();

	    if (!(length > 0))
	        throw new TrackgenException("trackgen reported a track length of " + length);

	    final double co = newLength/length;

	    onEdt(() -> {
		SegmentVector track = editorFrame.getTrackData().getSegments();
		int size = track.size();

		for(int i=0; i<size; i++)
		{
			Segment obj = track.get(i);
			try
			{
				if(obj.getType().equals("str"))
				{
				    obj.setLength(obj.getLength()*co);
				}else
				{
				    Curve curve = (Curve) obj;
				    curve.setRadiusStart(curve.getRadiusStart()*co);
				    curve.setRadiusEnd(curve.getRadiusEnd()*co);
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	    });

		runTrackgen();
		onEdt(() -> editorFrame.refresh());
	}
	
	/**
	 * Adjusts the curves and straights so the track closes. Called on the worker thread.
	 */
	private void adjust()
	{
	    runTrackgen();
	    adjustArcs(360/(360+angle));
	    runTrackgen();
	    adjustStraights(false, dY);
	    runTrackgen();
	    adjustStraights(true, dX);
	    runTrackgen();
	    onEdt(() -> editorFrame.refresh());
	}

	private void adjustArcs(double co)
	{
	    onEdt(() -> {
		SegmentVector track = editorFrame.getTrackData().getSegments();
		int size = track.size();

		for(int i=0; i<size; i++)
		{
			Segment obj = track.get(i);
			try
			{
				if(!obj.getType().equals("str"))
				{
				    Curve curve = (Curve) obj;
				    curve.setArcRad(curve.getArcRad()*co);
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	    });
	}

	/**
	 * Scales the straights that run mostly along y (or exactly along x)
	 * so the track end moves by delta in that direction.
	 */
	private void adjustStraights(boolean alongX, double delta)
	{
	    onEdt(() -> {
		// redraw so the straights' directions reflect the previous pass
		editorFrame.refresh();

		SegmentVector track = editorFrame.getTrackData().getSegments();
		int size = track.size();
		double total = 0;

		for(int i=0; i<size; i++)
		{
			Segment obj = track.get(i);
			if(obj.getType().equals("str"))
			{
			    Straight str = (Straight) obj;
			    double x = str.getDx();
			    double y = str.getDy();
			    if(alongX ? y == 0 : y > x)
			    {
			        total += alongX ? x : y;
			    }
			}
		}

		if (total == 0)
		    return;

		double co = (total+delta)/total;

		for(int i=0; i<size; i++)
		{
			Segment obj = track.get(i);
			if(obj.getType().equals("str"))
			{
			    Straight str = (Straight) obj;
			    double x = str.getDx();
			    double y = str.getDy();
			    if(alongX ? y == 0 : y > x)
			    {
			        str.setLength(str.getLength()*co);
			    }
			}
		}
	    });
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
