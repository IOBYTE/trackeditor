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

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import utils.Properties;
import utils.SegmentVector;
import utils.check.Closure;
import utils.check.TrackgenOutput;
import utils.circuit.Curve;
import utils.circuit.Pits;
import utils.circuit.Segment;
import utils.circuit.Straight;
import utils.undo.Undo;
import utils.undo.UndoPitGeneration;

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

	// how close trackgen's deltas have to be for the track to close (see Closure)
	static final double			TARGET_DX		= Closure.TARGET_DX;
	static final double			TARGET_DY		= Closure.TARGET_DY;
	static final double			TARGET_DZ		= Closure.TARGET_DZ;
	static final double			TARGET_ANGLE	= Closure.TARGET_ANGLE;

	private static final Color	PASS_COLOR		= new Color(0, 128, 0);
	private static final Color	FAIL_COLOR		= new Color(204, 0, 0);
	private static final Color	ROUNDING_COLOR	= new Color(204, 120, 0);

	// the search's tries without getting closer after which it stops
	private static final int	SEARCH_TRIES		= 9;

	// how much Auto changes a lever to measure its effect on the deltas
	private static final double	ARC_PROBE		= 0.1;		// degrees
	private static final double	LENGTH_PROBE	= 0.5;		// meters
	// the most trackgen runs Auto makes to close the track with its levers
	private static final int	MAX_PASSES		= 12;
	private static final int	MAX_HEIGHT_PASSES	= 6;
	// how often Auto measures the levers' effects again when a pass doesn't halve the miss
	private static final int	MAX_REMEASURES	= 2;
	// changes bigger than these are confirmed first
	private static final double	BIG_LENGTH_CHANGE	= 30;	// meters
	private static final double	BIG_ARC_CHANGE		= 5;	// degrees
	// what a lever can't go below
	private static final double	MIN_LENGTH		= 0.1;		// meters
	private static final double	MIN_ARC			= 0.01;		// degrees
	// straights shorter than this aren't used as levers
	private static final double	MIN_LEVER_LENGTH	= 2;	// meters
	// the most trackgen runs of the search for tiny changes that close the track,
	// when the deltas are down to trackgen's rounding
	private static final int	MAX_SEARCH_RUNS		= 30;
	// a miss (see miss) below which trackgen's rounding may be what's left, so the
	// levers' effects aren't measured again
	private static final double	ROUNDING_MISS		= 20;
	// the smallest step of the search, in meters
	private static final double	MIN_SEARCH_STEP		= 0.0001;
	// the search's changes to the two straights, in steps, nearest first
	private static final int[][]	GRID			= { { 0, 0 }, { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
		{ 1, 1 }, { -1, -1 }, { 1, -1 }, { -1, 1 }, { 2, 0 }, { -2, 0 }, { 0, 2 }, { 0, -2 },
		{ 2, 1 }, { -2, -1 }, { 1, 2 }, { -1, -2 }, { 2, -1 }, { -2, 1 }, { 1, -2 }, { -1, 2 },
		{ 2, 2 }, { -2, -2 }, { 2, -2 }, { -2, 2 } };
    private EditorFrame editorFrame;
    private Thread worker = null;
    // the trackgen process running now, so Cancel can stop it
    private volatile Process process = null;
    private volatile boolean cancelled = false;
    // whether the running adjustment has changed the track
    private volatile boolean changed = false;

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
	private double dZ = Double.NaN;
	// the track's largest coordinate, from XSize and YSize
	private double size = Double.NaN;
	private double angle = Double.NaN;

	// a summary of what Auto did, shown when it finishes
	private volatile String result = null;
	private JLabel deltaZLabel = null;
	private Color labelColor = null;

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
        this.setSize(379, 440);
        java.awt.Point location = new java.awt.Point(editorFrame.getProject().getDeltaDialogX(), editorFrame.getProject().getDeltaDialogY());
        if (isOnScreen(location))
            this.setLocation(location);
    }

    /** @return whether the top of a dialog there, where it's dragged, is on a screen */
    private static boolean isOnScreen(java.awt.Point location)
    {
		java.awt.Rectangle title = new java.awt.Rectangle(location.x, location.y, 100, 20);
		for (java.awt.GraphicsDevice device : java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
			if (device.getDefaultConfiguration().getBounds().intersects(title))
				return true;
		return false;
    }

    @Override
    protected void processWindowEvent(java.awt.event.WindowEvent e)
    {
		super.processWindowEvent(e);
		if (e.getID() == java.awt.event.WindowEvent.WINDOW_CLOSING)
		{
			editorFrame.getProject().setDeltaDialogX(getX());
			editorFrame.getProject().setDeltaDialogY(getY());
		}

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
		final List<String> folderArgs = new ArrayList<String>();
		final boolean[] exported = new boolean[1];
		final String[] problem = new String[1];
		final String[] trackKey = new String[1];
		// the lines shown, in the order of labels()
		final String[] lines = new String[labels().length];

		onEdt(() -> {
			for (JLabel label : labels())
			{
				label.setText(null);
				label.setForeground(labelColor);
				label.setToolTipText(null);
			}
			waitLabel.setText("Calculating track data. Please wait...");

			String path = Properties.getInstance().getPath();
			trackName[0] = path.substring(path.lastIndexOf(sep) + 1);
			trackgen[0] = editorFrame.getTrackgenPath();
			category[0] = editorFrame.getTrackData().getHeader().getCategory();
			// where the track is: without it CSR and Speed Dreams trackgen read the
			// installed track of that name (see EditorFrame.getTrackgenFolderArgs)
			folderArgs.addAll(editorFrame.getTrackgenFolderArgs());

			// don't run trackgen on an old or missing track file
			exported[0] = editorFrame.exportTrack();
			trackKey[0] = Closure.key(editorFrame);
			if (exported[0])
				problem[0] = editorFrame.getTrackgenProblem();
		});

		if (!exported[0])
			throw new TrackgenException(null);	// exportTrack already showed why
		if (problem[0] != null)
			throw new TrackgenException(problem[0]);

		length = Double.NaN;
		size = Double.NaN;
		dX = Double.NaN;
		dY = Double.NaN;
		dZ = Double.NaN;
		angle = Double.NaN;

		// the track data read, and the last lines that weren't, to show if trackgen fails
		TrackgenOutput output = new TrackgenOutput();

		Process proc;
		try
		{
			List<String> command = new ArrayList<String>(Arrays.asList(trackgen[0], "-z", "-c", category[0], "-n", trackName[0]));
			command.addAll(folderArgs);
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(true);
			// TORCS trackgen reads the track from the directory it runs in
			pb.directory(editorFrame.getTrackgenDirectory());
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
				TrackgenOutput.Field field = output.read(line);
				if (field != null)
				{
					final JLabel l = labels()[field.ordinal()];
					final String text = line;
					// the target for a delta, and the delta
					final double t = TrackgenOutput.target(field);
					final double v = output.value(field);
					final double a = field == TrackgenOutput.Field.DELTA_X || field == TrackgenOutput.Field.DELTA_Y
						? allowed(t, output.getSize()) : t;
					lines[field.ordinal()] = line;
					SwingUtilities.invokeLater(() -> showLine(l, text, t, v, a));
				}
			}

			int exitValue = proc.waitFor();

			if (cancelled)
				throw new TrackgenException(null);

			if (exitValue != 0)
				throw new TrackgenException(utils.check.Trackgen.failed(trackgen[0], exitValue) + output.otherOutput());
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
			length = output.getLength();
			size = output.getSize();
			dX = output.getDX();
			dY = output.getDY();
			dZ = output.getDZ();
			angle = output.getAngle();
		}

		if (Double.isNaN(length) || Double.isNaN(dX) || Double.isNaN(dY) || Double.isNaN(angle))
			throw new TrackgenException(trackgen[0] + " didn't report the track length and deltas" + output.otherOutput());

		Closure.setLast(new Closure(trackKey[0], dX, dY, dZ, angle, size, lines));
    }

    /** @return the labels of the track data, from the top */
    private JLabel[] labels()
    {
		return new JLabel[] { nameLabel, authorLabel, fileNameLabel, lengthLabel, widthLabel,
			xSizeLabel, ySizeLabel, deltaXLabel, deltaYLabel, deltaZLabel, deltaAngLabel };
    }

    /**
     * Shows a line of trackgen's output, and for a delta whether it's within its target.
     * Called on the event dispatch thread.
     *
     * @param target the delta's target, NaN when it isn't a delta
     * @param allowed how far it can be within trackgen's rounding (see allowed)
     */
    private void showLine(JLabel label, String text, double target, double value, double allowed)
    {
		label.setText(text);
		fitText();
		if (Double.isNaN(target))
			return;
		String units = target == TARGET_ANGLE ? " degrees" : " m";
		if (Math.abs(value) <= target)
		{
			label.setForeground(PASS_COLOR);
			label.setToolTipText("Within the target of \u00b1" + plain(target) + units);
		}
		else if (Math.abs(value) <= allowed)
		{
			label.setForeground(ROUNDING_COLOR);
			label.setToolTipText("More than the target of \u00b1" + plain(target) + units + ", but within trackgen's rounding on a track this size (\u00b1"
				+ String.format("%.4f", allowed) + units + ")");
		}
		else
		{
			label.setForeground(FAIL_COLOR);
			label.setToolTipText("More than the target of \u00b1" + plain(target) + units + ": Auto closes the track");
		}
    }

    /** @return how far Delta X or Delta Y can be from 0 (see Closure.allowed) */
    private static double allowed(double target, double size)
    {
		return Closure.allowed(target, size);
    }

    /**
     * Opens the dialog and runs Calc. Called on the event dispatch thread.
     */
    static void showAndCalculate(EditorFrame editorFrame)
    {
		DeltaPanel panel = new DeltaPanel(editorFrame, "", false);
		panel.setVisible(true);
		// after the dialog is shown, as Calc waits until it's done
		SwingUtilities.invokeLater(() -> panel.startCalculation(() -> panel.runTrackgen()));
    }

    /**
     * Opens the dialog showing trackgen's figures from the last run, which are still
     * the track's (see lastClosure). Called on the event dispatch thread.
     */
    static void showLastClosure(EditorFrame editorFrame)
    {
		Closure closure = Closure.last(editorFrame);
		DeltaPanel panel = new DeltaPanel(editorFrame, "", false);
		panel.setVisible(true);
		if (closure == null)
		{
			panel.waitLabel.setText("The track has changed: Calc again");
			return;
		}
		JLabel[] labels = panel.labels();
		for (int i = 0; i < labels.length && i < closure.lines.length; i++)
		{
			if (closure.lines[i] == null)
				continue;
			JLabel label = labels[i];
			double target = label == panel.deltaXLabel ? TARGET_DX : label == panel.deltaYLabel ? TARGET_DY
				: label == panel.deltaZLabel ? TARGET_DZ : label == panel.deltaAngLabel ? TARGET_ANGLE : Double.NaN;
			double value = label == panel.deltaXLabel ? closure.dX : label == panel.deltaYLabel ? closure.dY
				: label == panel.deltaZLabel ? closure.dZ : label == panel.deltaAngLabel ? closure.angle : Double.NaN;
			double allowed = label == panel.deltaXLabel || label == panel.deltaYLabel ? allowed(target, closure.size) : target;
			panel.showLine(label, closure.lines[i], target, value, allowed);
		}
		panel.waitLabel.setText("From the last calculation");
    }

    /**
     * Widens the track data, and the dialog, when a line doesn't fit, up to the
     * width of the screen. It doesn't get narrower again, so it doesn't jump about.
     */
    private void fitText()
    {
		int width = 0;
		for (java.awt.Component label : jPanel1.getComponents())
			width = Math.max(width, label.getPreferredSize().width);
		java.awt.Rectangle screen = getGraphicsConfiguration() != null ? getGraphicsConfiguration().getBounds()
			: new java.awt.Rectangle(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
		int frame = getInsets().left + getInsets().right;
		// the panel's margins and the label's
		int most = screen.width - frame - 2 * jPanel1.getX() - 10;
		width = Math.min(width, most);
		if (width <= jPanel1.getWidth() - 10)
			return;
		for (java.awt.Component label : jPanel1.getComponents())
			label.setSize(width, label.getHeight());
		jPanel1.setSize(width + 10, jPanel1.getHeight());
		setSize(Math.max(getWidth(), 2 * jPanel1.getX() + jPanel1.getWidth() + frame), getHeight());
		// keep it on the screen
		if (getX() + getWidth() > screen.x + screen.width)
			setLocation(Math.max(screen.x, screen.x + screen.width - getWidth()), getY());
		validate();
		repaint();
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
		result = null;

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
					waitLabel.setText("Calculation canceled");
				else
					waitLabel.setText(f ? "Calculation failed" : "Calculation finished");
				setButtonsEnabled(true);
				if (message != null && !cancelled)
					JOptionPane.showMessageDialog(this, message, "Delta Dialog", JOptionPane.ERROR_MESSAGE);
				else if (result != null && !f && !cancelled)
					JOptionPane.showMessageDialog(this, result, "Auto", JOptionPane.INFORMATION_MESSAGE);
				// changes made before a cancel or failure are kept, so show them
				editorFrame.refresh();
				// an open Check dialog shows trackgen's deltas when they're the track's
				for (java.awt.Window window : java.awt.Window.getWindows())
					if (window instanceof CheckDialog && window.isShowing())
						((CheckDialog) window).recheck();
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
            waitLabel.setBounds(15, 380, 290, 25);
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
            deltaZLabel = new JLabel();
            deltaYLabel = new JLabel();
            deltaXLabel = new JLabel();
            jPanel1 = new JPanel();
            jPanel1.setLayout(null);
            jPanel1.setBounds(29, 60, 310, 280);
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
            deltaZLabel.setBounds(5, 230, 300, 20);
            deltaZLabel.setText("");
            deltaAngLabel.setBounds(5, 255, 300, 20);
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
            jPanel1.add(deltaZLabel, null);
            jPanel1.add(deltaAngLabel, null);
            labelColor = deltaXLabel.getForeground();
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
            calcButton.setBounds(12, 345, 60, 25);
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
			adjustButton.setBounds(82, 345, 65, 25);
			adjustButton.setText("Auto");
			adjustButton.setToolTipText("Closes the track by changing a curve's arc and two straights' lengths, and the last segment's end height");
			adjustButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) 
				{  
				    int options = JOptionPane.OK_CANCEL_OPTION;
				    String msg = "This option will change your track.\n"
				        +"The result is written to the track's xml file (Undo takes it back).\n\n"
				        +"Auto closes the track by changing one curve's arc and two straights'\n"
				        +"lengths, away from the pits, and the last segment's end height when\n"
				        +"the heights don't meet. It runs trackgen until the deltas are within\n"
				        +"their targets, so it may take a while.";
				    int out = JOptionPane.showConfirmDialog(DeltaPanel.this,msg,"Warning",options);
				    if(out != JOptionPane.OK_OPTION)
				        return;
				    startCalculation(() -> undoable(() -> adjust(), false));
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
			lengthButton.setBounds(169, 345, 85, 25);
			lengthButton.setText("Length");
			lengthButton.setToolTipText("Adjusts the track's length to match the value in the box on the right");
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
				    String msg = "This option will change your track.\n"
				        +"The result is written to the track's xml file (Undo takes it back).\n\n"
				        +"If you decide to continue keep in mind that the adjust method is\n"
				        +"experimental and not propertly tested.";
				    int out = JOptionPane.showConfirmDialog(DeltaPanel.this,msg,"Warning",options);
				    if(out != JOptionPane.OK_OPTION)
				        return;

				    startCalculation(() -> undoable(() -> adjustLength(newLength), true));
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
			jTextField.setBounds(266, 345, 85, 25);
			jTextField.setToolTipText("The length the Length button adjusts the track to");
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
		changed = true;
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
	 * Runs an adjustment as one undo step. Called on the worker thread.
	 *
	 * @param keepOnFailure whether changes made before a cancel or a failure are kept
	 *        (see startCalculation), as part of the step; if not, the track is put back
	 *        as it was, and written again
	 */
	private void undoable(Runnable adjustment, boolean keepOnFailure)
	{
		final UndoPitGeneration[] undo = new UndoPitGeneration[1];
		changed = false;
		// UndoPitGeneration keeps every segment's values (and the pits, cameras and
		// name counters, which an adjustment doesn't change)
		onEdt(() -> undo[0] = new UndoPitGeneration(editorFrame));
		final boolean[] finished = new boolean[1];
		try
		{
			adjustment.run();
			finished[0] = true;
		}
		finally
		{
			// not onEdt: that refuses to run once Cancel was pressed
			Runnable add = () -> {
				if (!changed)
					return;
				if (!finished[0] && !keepOnFailure)
				{
					undo[0].undo();
					changed = false;
					// the file has the values trackgen was last run with
					editorFrame.exportTrack();
					return;
				}
				undo[0].captureAfter();
				Undo.add(undo[0]);
				editorFrame.documentIsModified = true;
			};
			try
			{
				SwingUtilities.invokeAndWait(add);
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * A value Auto changes to close the track: a curve's arc, in degrees, or a straight's length
	 */
	private static class Lever
	{
		final Segment	segment;
		final double	original;

		Lever(Segment segment)
		{
			this.segment = segment;
			this.original = get();
		}

		boolean isArc()
		{
			return segment instanceof Curve;
		}

		double get()
		{
			return isArc() ? ((Curve) segment).getArcDeg() : segment.getLength();
		}

		void set(double value)
		{
			if (isArc())
				((Curve) segment).setArcDeg(value);
			else
				segment.setLength(value);
		}

		double probe()
		{
			return isArc() ? ARC_PROBE : LENGTH_PROBE;
		}

		double min()
		{
			return isArc() ? MIN_ARC : MIN_LENGTH;
		}

		/** @return whether changing it by change is big enough to confirm first */
		boolean isBig(double change)
		{
			if (isArc())
				return Math.abs(change) > BIG_ARC_CHANGE;
			return Math.abs(change) > BIG_LENGTH_CHANGE || original + change < original / 2;
		}

		String describe(double from, double to)
		{
			String kind = isArc() ? "curve" : "straight";
			// not "curve curve 1"
			String name = segment.getName().toLowerCase().startsWith(kind) ? segment.getName() : kind + " " + segment.getName();
			if (isArc())
				return name + " arc " + String.format("%.6f", from) + "\u00b0 to " + String.format("%.6f", to) + "\u00b0";
			return name + " length " + String.format("%.6f", from) + " m to " + String.format("%.6f", to) + " m";
		}
	}

	/** @return the number without an exponent or trailing zeros */
	private static String plain(double value)
	{
		return Closure.plain(value);
	}

	/** @return how far the deltas are from closing, as a multiple of their targets: closed when at most 1 */
	private double miss()
	{
		return Math.max(Math.abs(dX) / TARGET_DX, Math.max(Math.abs(dY) / TARGET_DY, Math.abs(angle) / TARGET_ANGLE));
	}

	private double[] deltas()
	{
		return new double[] { dX, dY, angle };
	}

	/**
	 * Closes the track by changing a curve's arc and two straights' lengths (the levers),
	 * running trackgen until its deltas are within their targets, then the last segment's
	 * end height. Like the TORCS tracktools, it measures how each lever moves the end of
	 * the track and solves for the changes. Called on the worker thread.
	 */
	private void adjust()
	{
		runTrackgen();
		StringBuilder summary = new StringBuilder();

		if (miss() > 1)
		{
			Lever[] levers = new Lever[3];
			String[] problem = new String[1];
			onEdt(() -> {
				// the straights' directions and the curves' centers of the track as it is
				editorFrame.refresh();
				problem[0] = chooseLevers(levers);
			});
			if (problem[0] == null)
				problem[0] = closeWithLevers(levers, summary);
			if (problem[0] != null)
			{
				if (!scaleAllInstead(problem[0]))
					throw new TrackgenException(null);
				summary.setLength(0);
				scaleAll();
				summary.append("Scaled all the curves and straights.\n");
			}
		}
		else
		{
			summary.append("The track already closes in x, y and angle.\n");
		}

		closeHeight(summary);
		onEdt(() -> editorFrame.refresh());

		summary.append("\n").append(String.format("Delta X = %.6f, Delta Y = %.6f, Delta Ang = %.6f\u00b0", dX, dY, angle));
		if (!Double.isNaN(dZ))
			summary.append(String.format(", Delta Z = %.6f", dZ));
		boolean heights = Double.isNaN(dZ) || Math.abs(dZ) <= TARGET_DZ;
		if (miss() <= 1 && heights)
			summary.append("\nThe track closes within the targets.");
		else if (Math.abs(dX) <= allowed(TARGET_DX, size) && Math.abs(dY) <= allowed(TARGET_DY, size)
			&& Math.abs(angle) <= TARGET_ANGLE && heights)
			summary.append("\nThe track closes within trackgen's rounding on a track this size (\u00b1"
				+ String.format("%.4f", allowed(TARGET_DX, size)) + " m), though not within the targets.");
		else
			summary.append("\nThe track doesn't close within the targets. Within a few millimeters trackgen's deltas depend on how its\n"
				+ "floats round, which is coarser on a big track: this is as close as Auto got.");
		result = summary.toString();
	}

	/**
	 * Asks whether to scale all the curves and straights instead, like Auto used to.
	 *
	 * @return true to scale them
	 */
	private boolean scaleAllInstead(String problem)
	{
		boolean[] yes = new boolean[1];
		onEdt(() -> {
			Object[] options = { "Scale All", "Cancel" };
			int choice = JOptionPane.showOptionDialog(DeltaPanel.this, problem + "\n\nScale all the curves and straights instead?\n"
				+ "That changes the whole track, so it may need running two or three times.",
				"Auto", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			yes[0] = choice == 0;
		});
		return yes[0];
	}

	/**
	 * @return which segments are part of the pits, from the pit entry to the pit exit
	 *         (or the pit start to the pit end when they aren't set)
	 */
	private boolean[] pitSegments(SegmentVector track)
	{
		boolean[] pit = new boolean[track.size()];
		Pits pits = editorFrame.getTrackData().getMainTrack().getPits();
		if (pits == null)
			return pit;
		int from = index(track, pits.getEntry());
		if (from == -1)
			from = index(track, pits.getStart());
		int to = index(track, pits.getExit());
		if (to == -1)
			to = index(track, pits.getEnd());
		if (from == -1 || to == -1)
			return pit;
		for (int i = from; ; i = (i + 1) % track.size())
		{
			pit[i] = true;
			if (i == to)
				break;
		}
		return pit;
	}

	private static int index(SegmentVector track, String name)
	{
		if (name == null || name.isEmpty())
			return -1;
		for (int i = 0; i < track.size(); i++)
			if (name.equals(track.get(i).getName()))
				return i;
		return -1;
	}

	/**
	 * Chooses the levers, away from the pits: the curve closest to the end of the track,
	 * as turning a curve moves the end around its center, and the two straights that can
	 * move the end where it has to go with the smallest changes for their lengths. Called
	 * on the event dispatch thread.
	 *
	 * @return why it can't, or null when it has
	 */
	private String chooseLevers(Lever[] levers)
	{
		SegmentVector track = editorFrame.getTrackData().getSegments();
		if (track.size() < 3)
			return "The track has too few segments for Auto to close it.";
		boolean[] pit = pitSegments(track);
		Point2D.Double end = track.get(track.size() - 1).endTrackCenter;
		if (end == null)
			return "The track's shape isn't known.";

		// the turn needed, left positive: Delta Ang is the start's heading minus the end's
		double turn = Math.toRadians(angle);

		Curve curve = null;
		double bestCurve = Double.POSITIVE_INFINITY;
		for (int i = 0; i < track.size(); i++)
		{
			if (pit[i] || !(track.get(i) instanceof Curve))
				continue;
			Curve candidate = (Curve) track.get(i);
			double arc = candidate.getArcDeg();
			// the arc mustn't drop below half of it
			if (!(arc > 0) || Math.abs(angle) > arc / 2)
				continue;
			double score = candidate.center.distance(end) * (1 + Math.abs(angle) / arc);
			if (score < bestCurve)
			{
				bestCurve = score;
				curve = candidate;
			}
		}
		if (curve == null)
			return "There's no curve away from the pits that Auto can turn by " + String.format("%.4f", angle) + "\u00b0.";

		// turning the curve swings the end around its center: the straights move it the rest of the way
		double needX = dX + turn * (end.y - curve.center.y);
		double needY = dY - turn * (end.x - curve.center.x);

		List<Straight> straights = new ArrayList<Straight>();
		for (int i = 0; i < track.size(); i++)
		{
			Segment segment = track.get(i);
			if (!pit[i] && segment instanceof Straight && segment.getLength() >= MIN_LEVER_LENGTH
				&& Math.hypot(segment.getDx(), segment.getDy()) > 0)
				straights.add((Straight) segment);
		}

		Straight first = null, second = null;
		double bestStraights = Double.POSITIVE_INFINITY;
		for (int a = 0; a < straights.size(); a++)
		{
			Straight sa = straights.get(a);
			double la = Math.hypot(sa.getDx(), sa.getDy());
			double ax = sa.getDx() / la, ay = sa.getDy() / la;
			for (int b = a + 1; b < straights.size(); b++)
			{
				Straight sb = straights.get(b);
				double lb = Math.hypot(sb.getDx(), sb.getDy());
				double bx = sb.getDx() / lb, by = sb.getDy() / lb;
				double cross = ax * by - ay * bx;
				// nearly parallel straights would need big opposite changes
				if (Math.abs(cross) < 0.2)
					continue;
				double changeA = (needX * by - needY * bx) / cross;
				double changeB = (ax * needY - ay * needX) / cross;
				double score = Math.max(Math.abs(changeA) / sa.getLength(), Math.abs(changeB) / sb.getLength())
					+ 0.01 / (Math.abs(cross) * Math.min(sa.getLength(), sb.getLength()));
				if (score < bestStraights)
				{
					bestStraights = score;
					first = sa;
					second = sb;
				}
			}
		}
		if (first == null)
			return "There aren't two straights away from the pits, at an angle to each other, that Auto can lengthen or shorten.";

		levers[0] = new Lever(curve);
		levers[1] = new Lever(first);
		levers[2] = new Lever(second);
		return null;
	}

	/**
	 * Measures how much each lever moves the deltas, by changing it a little and running
	 * trackgen. The levers and deltas are left as they were.
	 *
	 * @return effect[delta][lever]: the change in the delta for a change of 1 in the lever
	 */
	private double[][] measure(Lever[] levers)
	{
		double[] base = deltas();
		double[][] effect = new double[3][levers.length];
		for (int k = 0; k < levers.length; k++)
		{
			Lever lever = levers[k];
			double value = lever.get();
			double probe = lever.probe();
			onEdt(() -> lever.set(value + probe));
			changed = true;
			try
			{
				runTrackgen();
			}
			finally
			{
				// not onEdt: that refuses to run once Cancel was pressed, and the
				// track mustn't be left with the probe
				setOnEdt(lever, value);
			}
			double[] probed = deltas();
			for (int r = 0; r < 3; r++)
				effect[r][k] = (probed[r] - base[r]) / probe;
		}
		dX = base[0];
		dY = base[1];
		angle = base[2];
		return effect;
	}

	private void setOnEdt(Lever lever, double value)
	{
		Runnable set = () -> lever.set(value);
		if (SwingUtilities.isEventDispatchThread())
		{
			set.run();
			return;
		}
		try
		{
			SwingUtilities.invokeAndWait(set);
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Solves effect * change = -deltas.
	 *
	 * @return the changes, or null when the levers can't move the deltas independently
	 */
	static double[] solve(double[][] effect, double[] deltas)
	{
		int n = deltas.length;
		double[][] m = new double[n][n + 1];
		// each row scaled to its largest effect, so meters and degrees compare
		for (int r = 0; r < n; r++)
		{
			double scale = 0;
			for (int c = 0; c < n; c++)
				scale = Math.max(scale, Math.abs(effect[r][c]));
			if (scale == 0)
				return null;
			for (int c = 0; c < n; c++)
				m[r][c] = effect[r][c] / scale;
			m[r][n] = -deltas[r] / scale;
		}
		for (int c = 0; c < n; c++)
		{
			int pivot = c;
			for (int r = c + 1; r < n; r++)
				if (Math.abs(m[r][c]) > Math.abs(m[pivot][c]))
					pivot = r;
			if (Math.abs(m[pivot][c]) < 1e-9)
				return null;
			double[] row = m[c];
			m[c] = m[pivot];
			m[pivot] = row;
			for (int r = 0; r < n; r++)
			{
				if (r == c)
					continue;
				double factor = m[r][c] / m[c][c];
				for (int j = c; j <= n; j++)
					m[r][j] -= factor * m[c][j];
			}
		}
		double[] change = new double[n];
		for (int r = 0; r < n; r++)
			change[r] = m[r][n] / m[r][r];
		return change;
	}

	/**
	 * Changes the levers until trackgen's deltas are within their targets, or as close
	 * as it gets. Called on the worker thread.
	 *
	 * @return why it can't, or null when it has
	 */
	private String closeWithLevers(Lever[] levers, StringBuilder summary)
	{
		double[][] effect = measure(levers);
		double bestMiss = miss();
		double[] best = new double[levers.length];
		for (int k = 0; k < levers.length; k++)
			best[k] = levers[k].get();
		int remeasures = 0;
		// passes in a row that didn't get closer
		int stalled = 0;

		for (int pass = 0; pass < MAX_PASSES && miss() > 1; pass++)
		{
			double[] change = solve(effect, deltas());
			if (change == null)
				return "Auto can't close the track with " + levers[0].segment.getName() + ", " + levers[1].segment.getName()
					+ " and " + levers[2].segment.getName() + ": they don't move its end independently.";

			StringBuilder changes = new StringBuilder();
			boolean big = false;
			for (int k = 0; k < levers.length; k++)
			{
				Lever lever = levers[k];
				double to = lever.get() + change[k];
				if (to < lever.min())
					return "Closing the track would make " + lever.describe(lever.get(), to) + ".";
				big |= lever.isBig(to - lever.original);
				changes.append("\n    ").append(lever.describe(lever.original, to));
			}
			if (big && pass == 0 && !confirm(changes.toString()))
				throw new TrackgenException(null);

			double before = miss();
			onEdt(() -> {
				for (int k = 0; k < levers.length; k++)
					levers[k].set(levers[k].get() + change[k]);
			});
			changed = true;
			runTrackgen();

			if (miss() < bestMiss)
			{
				bestMiss = miss();
				for (int k = 0; k < levers.length; k++)
					best[k] = levers[k].get();
				stalled = 0;
			}
			else
			{
				stalled++;
			}
			// down to trackgen's rounding solving doesn't get closer: the search below may
			if (stalled >= 2)
				break;
			// far from the start the effects aren't the same: measure them again
			if (miss() > ROUNDING_MISS && miss() > before / 2 && remeasures < MAX_REMEASURES)
			{
				effect = measure(levers);
				remeasures++;
			}
		}

		if (miss() > bestMiss)
		{
			onEdt(() -> {
				for (int k = 0; k < levers.length; k++)
					levers[k].set(best[k]);
			});
			runTrackgen();
		}

		// Within a few millimeters trackgen's deltas don't follow the levers smoothly
		// any more: its floats can only hold the track's points in steps (about a
		// quarter of a millimeter at 2 km), and how they round adds up segment by
		// segment. They're the same for the same values though, so the straights are
		// tried a grid of those steps around where the levers' effects say the deltas
		// cancel, keeping the closest, until one closes the track.
		double step = Math.max(MIN_SEARCH_STEP, Double.isNaN(size) ? 0 : Math.ulp((float) size));
		double[] bestDeltas = deltas();
		java.util.Set<String> tried = new java.util.HashSet<String>();
		int runs = 0;
		boolean improved = true;
		// how far apart the deltas came out
		double minX = bestDeltas[0], maxX = bestDeltas[0], minY = bestDeltas[1], maxY = bestDeltas[1];
		while (bestMiss > 1 && runs < MAX_SEARCH_RUNS && improved)
		{
			improved = false;
			double[] center = best.clone();
			if (Math.abs(bestDeltas[2]) <= TARGET_ANGLE)
			{
				// the heading closes: a change to the curve changes how the heading rounds
				// in every segment after it, which can move the end by centimeters
				double[] change = solve(new double[][] { { effect[0][1], effect[0][2] }, { effect[1][1], effect[1][2] } },
					new double[] { bestDeltas[0], bestDeltas[1] });
				if (change == null)
					break;
				center[1] += change[0];
				center[2] += change[1];
			}
			else
			{
				double[] change = solve(effect, bestDeltas);
				if (change == null)
					break;
				for (int k = 0; k < levers.length; k++)
					center[k] += change[k];
			}
			int tries = 0;
			for (int[] offset : GRID)
			{
				if (runs >= MAX_SEARCH_RUNS)
					break;
				// none of the nearest got closer: the rest won't be any likelier to
				if (tries >= SEARCH_TRIES && !improved)
					break;
				double[] values = center.clone();
				values[1] += offset[0] * step;
				values[2] += offset[1] * step;
				// the track file has floats: values they keep the same give the same deltas
				String key = (float) values[0] + "," + (float) values[1] + "," + (float) values[2];
				if (!tried.add(key))
					continue;
				onEdt(() -> {
					for (int k = 0; k < levers.length; k++)
						levers[k].set(values[k]);
				});
				runTrackgen();
				runs++;
				tries++;
				minX = Math.min(minX, dX);
				maxX = Math.max(maxX, dX);
				minY = Math.min(minY, dY);
				maxY = Math.max(maxY, dY);
				if (miss() < bestMiss)
				{
					bestMiss = miss();
					bestDeltas = deltas();
					System.arraycopy(values, 0, best, 0, values.length);
					improved = true;
					if (bestMiss <= 1)
						break;
				}
			}
		}
		if (miss() > bestMiss)
		{
			onEdt(() -> {
				for (int k = 0; k < levers.length; k++)
					levers[k].set(best[k]);
			});
			runTrackgen();
		}

		if (runs > 0 && bestMiss > 1)
			summary.append(String.format("Changes of a fraction of a millimeter moved trackgen's deltas over %.1f mm in x and %.1f mm in y\n"
				+ "on this track: closer than that is down to how its floats round.\n", (maxX - minX) * 1000, (maxY - minY) * 1000));

		boolean unchanged = true;
		for (Lever lever : levers)
			unchanged &= (float) lever.get() == (float) lever.original;
		if (unchanged)
		{
			onEdt(() -> {
				for (Lever lever : levers)
					lever.set(lever.original);
			});
			summary.append("Kept the track as it was: no change Auto tried got it closer.\n");
			return null;
		}

		summary.append("Changed:");
		for (Lever lever : levers)
			summary.append("\n    ").append(lever.describe(lever.original, lever.get()));
		summary.append("\n");
		return null;
	}

	/**
	 * Asks whether to make big changes.
	 *
	 * @return true to make them
	 */
	private boolean confirm(String changes)
	{
		boolean[] yes = new boolean[1];
		onEdt(() -> {
			Object[] options = { "Change Them", "Cancel" };
			int choice = JOptionPane.showOptionDialog(DeltaPanel.this, "Closing the track needs big changes, about:" + changes,
				"Auto", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			yes[0] = choice == 0;
		});
		return yes[0];
	}

	/**
	 * Raises or lowers the end of the last segment until Delta Z is within its target.
	 * Called on the worker thread.
	 */
	private void closeHeight(StringBuilder summary)
	{
		if (Double.isNaN(dZ) || Math.abs(dZ) <= TARGET_DZ)
			return;
		Segment[] last = new Segment[1];
		String[] from = new String[1];
		onEdt(() -> {
			SegmentVector track = editorFrame.getTrackData().getSegments();
			last[0] = track.get(track.size() - 1);
			from[0] = endHeight(last[0]);
		});

		// how much Delta Z falls for a rise of 1 in the end: 1 when the end's height
		// is set, otherwise about that
		double rate = 1;
		for (int pass = 0; pass < MAX_HEIGHT_PASSES && Math.abs(dZ) > TARGET_DZ; pass++)
		{
			double before = dZ;
			double rise = before / rate;
			String[] problem = new String[1];
			onEdt(() -> problem[0] = raiseEnd(last[0], rise));
			if (problem[0] != null)
			{
				summary.append(problem[0]).append("\n");
				return;
			}
			changed = true;
			runTrackgen();
			if (Double.isNaN(dZ))
				return;
			double fell = before - dZ;
			if (Math.abs(fell) > 1e-9)
				rate = Math.max(0.1, Math.min(10, fell / rise));
		}
		summary.append("Changed the end height of segment ").append(last[0].getName()).append(": ").append(from[0])
			.append(" to ").append(endHeight(last[0])).append("\n");
	}

	private static String endHeight(Segment segment)
	{
		if (segment.hasHeightEnd())
			return String.format("%.5f m", segment.getHeightEnd());
		if (segment.hasHeightEndLeft() || segment.hasHeightEndRight())
			return String.format("%.5f m left, %.5f m right", segment.getHeightEndLeft(), segment.getHeightEndRight());
		if (segment.hasGrade())
			return String.format("grade %.5f%%", segment.getGrade());
		return "not set";
	}

	/**
	 * Raises the end of a segment: the height it has, or its grade, or else its calculated height.
	 * Called on the event dispatch thread.
	 *
	 * @return why it can't, or null when it has
	 */
	private static String raiseEnd(Segment segment, double rise)
	{
		if (segment.hasHeightEnd())
		{
			segment.setHeightEnd(segment.getHeightEnd() + rise);
		}
		else if (segment.hasHeightEndLeft() || segment.hasHeightEndRight())
		{
			double left = segment.hasHeightEndLeft() ? segment.getHeightEndLeft() : segment.getCalculatedHeightEndLeft();
			double right = segment.hasHeightEndRight() ? segment.getHeightEndRight() : segment.getCalculatedHeightEndRight();
			if (Double.isNaN(left) || Double.isNaN(right))
				return "Auto can't set the end height of segment " + segment.getName() + ": its height isn't known.";
			segment.setHeightEndLeft(left + rise);
			segment.setHeightEndRight(right + rise);
		}
		else if (segment.hasGrade() && segment.getLength() > 0)
		{
			// the grade is a percentage
			segment.setGrade(segment.getGrade() + rise / segment.getLength() * 100);
		}
		else
		{
			double height = segment.getCalculatedHeightEnd();
			if (Double.isNaN(height))
				return "Auto can't set the end height of segment " + segment.getName() + ": its height isn't known.";
			segment.setHeightEnd(height + rise);
		}
		return null;
	}

	/**
	 * Scales all the curves and straights so the track closes: what Auto did before it
	 * had levers. Called on the worker thread.
	 */
	private void scaleAll()
	{
	    runTrackgen();
	    adjustArcs(angle);
	    runTrackgen();
	    adjustStraights(false, dY);
	    runTrackgen();
	    adjustStraights(true, dX);
	    runTrackgen();
	}

	/**
	 * Scales every curve's arc so the track's turns add up to a whole turn.
	 *
	 * @param angle the angle trackgen reports, in degrees: the first segment's
	 *        start angle minus the last segment's end angle, so the turns have to
	 *        change by it. The track's turns (left positive) are N, and should be
	 *        N + angle, so the arcs are scaled by (N + angle) / N: this works for
	 *        tracks turning either way (N about +360 or -360)
	 */
	private void adjustArcs(double angle)
	{
	    onEdt(() -> {
		SegmentVector track = editorFrame.getTrackData().getSegments();
		int size = track.size();

		double turns = 0;
		for (Segment obj : track)
		{
			if (obj instanceof Curve)
			{
				Curve curve = (Curve) obj;
				turns += "lft".equals(curve.getType()) ? curve.getArcDeg() : -curve.getArcDeg();
			}
		}

		// turns that cancel out (a figure eight) can't be closed by scaling them all
		double target = turns + angle;
		if (Math.round(target / 360) == 0 || turns == 0)
			return;
		double co = target / turns;
		changed = true;

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
	 * Lengthens and shortens the straights that run mostly along one axis so the
	 * track's end moves by delta along it. Which way a straight heads doesn't
	 * matter, only how much of it is along the axis: one heading -x runs along x
	 * as much as one heading +x.
	 *
	 * Straights heading opposite ways change in opposite directions, each by the
	 * same fraction of its length: lengthening every straight by one factor would
	 * hardly move the end of a closed track, whose straights going one way about
	 * cancel those going the other way.
	 */
	private void adjustStraights(boolean alongX, double delta)
	{
	    onEdt(() -> {
		// redraw so the straights' directions reflect the previous pass
		editorFrame.refresh();

		SegmentVector track = editorFrame.getTrackData().getSegments();
		java.util.List<Straight> straights = new java.util.ArrayList<Straight>();
		double total = 0;

		for (Segment obj : track)
		{
			if (obj instanceof Straight)
			{
			    Straight str = (Straight) obj;
			    double along = alongX ? str.getDx() : str.getDy();
			    double across = alongX ? str.getDy() : str.getDx();
			    if (Math.abs(along) > Math.abs(across))
			    {
			        straights.add(str);
			        total += Math.abs(along);
			    }
			}
		}

		if (total == 0 || delta == 0 || Double.isNaN(delta))
		    return;

		// each straight's length changes by this fraction: the ones heading the
		// way the end has to move get longer, the others shorter
		double fraction = delta / total;
		if (Math.abs(fraction) >= 1)
		    throw new TrackgenException("The straights along " + (alongX ? "x" : "y")
		        + " are too short to close a gap of " + delta + " m.");
		changed = true;

		for (Straight str : straights)
		{
		    double along = alongX ? str.getDx() : str.getDy();
		    str.setLength(str.getLength() * (1 + fraction * Math.signum(along)));
		}
	    });
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
