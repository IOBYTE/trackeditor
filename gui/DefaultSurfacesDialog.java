package gui;

import java.awt.Point;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import gui.properties.SurfaceProperties;

public class DefaultSurfacesDialog extends JDialog
{
	private EditorFrame				editorFrame;
	private SurfaceProperties		surfaceProperties		= null;
	
	public DefaultSurfacesDialog(EditorFrame editorFrame)
	{
		super();
		this.editorFrame = editorFrame;
		initialize();
	}

	private void initialize()
	{
		this.setContentPane(getSurfaceProperties());
		this.setSize(550, 831);
		Point p = new Point();
		p.x = editorFrame.getProject().getDefaultSurfacesDialogX();
		p.y = editorFrame.getProject().getDefaultSurfacesDialogY();
		this.setLocation(p);
		this.setModal(false);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setTitle("Default Surfaces");
	}
	
	private SurfaceProperties getSurfaceProperties()
	{
		if (surfaceProperties == null)
		{
			surfaceProperties = new SurfaceProperties(editorFrame, true);
		}
		return surfaceProperties;
	}

	/**
	 * Shows the default surfaces again, after they were read again because the game
	 * or its data directory changed.
	 */
	public void refresh()
	{
		surfaceProperties = null;
		this.setContentPane(getSurfaceProperties());
		this.validate();
		this.repaint();
	}

	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			editorFrame.getProject().setDefaultSurfacesDialogX(this.getX());
			editorFrame.getProject().setDefaultSurfacesDialogY(this.getY());
			editorFrame.clearDefaultSurfacesDialog();
		}
	}
}
