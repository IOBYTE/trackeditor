/*
 *   GroupButton.java
 *   Created on 27 ??? 2005
 *
 *    The GroupButton.java is part of TrackEditor-0.6.0.
 *
 *    TrackEditor-0.6.0 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.6.0 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.6.0; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package gui.segment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
/**
 * @author Charalampos Alexopoulos
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class GroupButton extends JPanel
{
	private JRadioButton		leftButton	= null;
	private JRadioButton		rightButton	= null;
	Action						leftAction	= null;
	Action						rightAction	= null;
	ButtonGroup					group		= null;
	private boolean				enabled 	= true;
	private String				selected;
	private SegmentEditorDlg	parent;

	/**
	 *  
	 */
	public GroupButton()
	{
		initialize();
	}

	/**
	 *  
	 */
	private void initialize()
	{
		setLayout(null);
		setBounds(0, 0, 70, 32);
		setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		add(getLeftButton(), null);
		add(getRightButton(), null);
		group = new ButtonGroup();
		group.add(rightButton);
		group.add(leftButton);
	}

	private JRadioButton getLeftButton()
	{
		if (leftButton == null)
		{
			leftButton = new JRadioButton("Left");
			leftButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					selected = "lft";
					parent.groupChanged();
				}

			});
			leftButton.setBounds(5, 16, 50, 15);
		}
		return leftButton;
	}

	private JRadioButton getRightButton()
	{
		if (rightButton == null)
		{
			rightButton = new JRadioButton("Right");
			rightButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					selected = "rgt";
					parent.groupChanged();
				}

			});
			rightButton.setBounds(5, 2, 50, 15);
		}
		return rightButton;
	}

	/**
	 * @return Returns the selected.
	 */
	public String getSelected()
	{
		return selected;
	}
	/**
	 * @param selected
	 *            The selected to set.
	 */
	public void setSelected(String selected)
	{
		this.selected = selected;
		if (selected.equals("rgt"))
		{
			group.setSelected(getRightButton().getModel(),true);
		} else
		{
			group.setSelected(getLeftButton().getModel(),true);
		}
	}
	/**
	 * @param parent The parent to set.
	 */
	public void setParent(SegmentEditorDlg parent)
	{
		this.parent = parent;
	}
	/**
	 * @return Returns the enabled.
	 */
	public boolean isEnabled()
	{
		return enabled;
	}
	/**
	 * @param enabled The enabled to set.
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		getRightButton().setEnabled(enabled);
		getLeftButton().setEnabled(enabled);
		if (!enabled)
			group.clearSelection();
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
