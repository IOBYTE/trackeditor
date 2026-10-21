package gui;

import java.awt.Point;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import gui.properties.ObjectProperties;

public class DefaultObjectsDialog extends JDialog
{
	private EditorFrame				editorFrame;
	private ObjectProperties		objectProperties		= null;
	
	public DefaultObjectsDialog(EditorFrame editorFrame)
	{
		super();
		this.editorFrame = editorFrame;
		initialize();
	}

	private void initialize()
	{
		this.setContentPane(getObjectProperties());
		this.setSize(550, 489);
		Point p = new Point();
		p.x = editorFrame.getProject().getDefaultObjectsDialogX();
		p.y = editorFrame.getProject().getDefaultObjectsDialogY();
		this.setLocation(p);
		this.setModal(false);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setTitle("Default Objects");
	}
	
	private ObjectProperties getObjectProperties()
	{
		if (objectProperties == null)
		{
			objectProperties = new ObjectProperties(editorFrame, true);
		}
		return objectProperties;
	}

	/**
	 * Shows the default objects again, after they were read again because the game
	 * or its data directory changed.
	 */
	public void refresh()
	{
		objectProperties = null;
		this.setContentPane(getObjectProperties());
		this.validate();
		this.repaint();
	}

	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			editorFrame.getProject().setDefaultObjectsDialogX(this.getX());
			editorFrame.getProject().setDefaultObjectsDialogY(this.getY());
			editorFrame.clearDefaultObjectsDialog();
		}
	}
}
