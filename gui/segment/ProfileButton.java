/*
 *   ProfileButton.java
 *   Created on 31 May 2022
 *
 *    The ProfileButton.java is part of TrackEditor-0.7.0.
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
package gui.segment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
/**
 * @author Robert Reif
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ProfileButton extends JPanel
{
	private JRadioButton		noneButton		= null;
	private JRadioButton		linearButton	= null;
	private JRadioButton		splineButton	= null;
	Action						noneAction		= null;
	Action						linearAction	= null;
	Action						splineAction	= null;
	ButtonGroup					group			= null;
	private boolean				enabled 		= true;
	private String				selected;
	private SegmentEditorDlg	parent;

	/**
	 *  
	 */
	public ProfileButton()
	{
		initialize();
	}

	/**
	 *  
	 */
	private void initialize()
	{
		setLayout(null);
		setBounds(0, 0, 80, 48);
		setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		add(getNoneButton(), null);
		add(getLinearButton(), null);
		add(getSplineButton(), null);
		group = new ButtonGroup();
		group.add(noneButton);
		group.add(linearButton);
		group.add(splineButton);
	}

	private JRadioButton getNoneButton()
	{
		if (noneButton == null)
		{
			noneButton = new JRadioButton("None");
			noneButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					selected = "none";
					parent.profileChanged();
				}

			});
			noneButton.setBounds(5, 2, 60, 15);
		}
		return noneButton;
	}

	private JRadioButton getLinearButton()
	{
		if (linearButton == null)
		{
			linearButton = new JRadioButton("Linear");
			linearButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					selected = "linear";
					parent.profileChanged();
				}

			});
			linearButton.setBounds(5, 16, 60, 15);
		}
		return linearButton;
	}

	private JRadioButton getSplineButton()
	{
		if (splineButton == null)
		{
			splineButton = new JRadioButton("Spline");
			splineButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					selected = "spline";
					parent.profileChanged();
				}

			});
			splineButton.setBounds(5, 30, 60, 15);
		}
		return splineButton;
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
		if (selected.equals("none"))
		{
			group.setSelected(getNoneButton().getModel(),true);
		} else if (selected.equals("linear"))
		{
			group.setSelected(getLinearButton().getModel(),true);
		} else
		{
			group.setSelected(getSplineButton().getModel(),true);
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
		getNoneButton().setEnabled(enabled);
		getLinearButton().setEnabled(enabled);
		getSplineButton().setEnabled(enabled);
		if (!enabled)
			group.clearSelection();
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
