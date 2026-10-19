package gui.properties;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.MutableDouble;
import utils.MutableString;
import utils.circuit.Sector;

/**
 * @author Robert Reif
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SectorProperties extends PropertyPanel
{
	private JButton				addSectorButton		= null;
	private JButton				deleteSectorButton	= null;
	private JTabbedPane			tabbedPane			= null;

	/**
	 *
	 */
	public SectorProperties(EditorFrame editorFrame)
	{
		super(editorFrame);
		initialize();
    }

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize()
	{
		this.setLayout(null);
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		this.add(getTabbedPane(), null);
		this.add(getAddSectorButton(), null);
		this.add(getDeleteSectorButton(), null);
	}

	/**
	 * This method initializes addSectorpingButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getAddSectorButton()
	{
		if (addSectorButton == null)
		{
			addSectorButton = new JButton();
			addSectorButton.setBounds(10, 147, 120, 25);
			addSectorButton.setText("Add Sector");
			addSectorButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					Sector sector = new Sector();
					sector.setName("" + (tabbedPane.getTabCount() + 1));

					tabbedPane.addTab(sector.getName(), null, new SectorPanel(sector), null);
					tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
				}
			});
		}
		return addSectorButton;
	}

	/**
	 * This method initializes deleteSectorButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteSectorButton()
	{
		if (deleteSectorButton == null)
		{
			deleteSectorButton = new JButton();
			deleteSectorButton.setBounds(140, 147, 130, 25);
			deleteSectorButton.setText("Delete Sector");
			deleteSectorButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (tabbedPane.getTabCount() > 0)
					{
						tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
					}
				}
			});
		}
		return deleteSectorButton;
	}

	/**
	 * This method initializes tabbedPane
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getTabbedPane()
	{
		if (tabbedPane == null)
		{
			tabbedPane = new JTabbedPane();
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			tabbedPane.setBounds(10, 10, 510, 127);

			Vector<Sector> sectors = getEditorFrame().getTrackData().getSectors();

			for (int i = 0; i < sectors.size(); i++)
	        {
                Sector sector = sectors.elementAt(i);
				tabbedPane.addTab(sector.getName(), null, new SectorPanel(sector), null);
			}
		}
		return tabbedPane;
	}

	private class SectorPanel extends JPanel
	{
		private JLabel		nameLabel					= new JLabel();
		private JTextField 	nameTextField				= new JTextField();
		private JLabel		commentLabel				= new JLabel();
		private JTextField 	commentTextField			= new JTextField();
		private JLabel		distanceFromStartLabel		= new JLabel();
		private JTextField	distanceFromStartTextField	= new JTextField();

		/**
		 *
		 */
		public SectorPanel(Sector sector)
		{
			super();
			initialize(sector);
		}

		/**
		 *
		 */
		private void initialize(Sector sector)
		{
			setLayout(null);

			addLabel(this, 0, nameLabel, "Name", 150);
			addLabel(this, 1, commentLabel, "Comment", 150);
			addLabel(this, 2, distanceFromStartLabel, "Distance From Start", 150);

			addTextField(this, 0, nameTextField, sector.getName(), 160, 125);
			addTextField(this, 1, commentTextField, sector.getComment(), 160, 335);
			addTextField(this, 2, distanceFromStartTextField, sector.getDistanceFromStart(), 160, 125);
		}
	}

	/**
	 *
	 */
	public void exit()
	{
		MutableString stringResult = new MutableString();
		MutableDouble doubleResult = new MutableDouble();
		Vector<Sector> sectors = getEditorFrame().getTrackData().getSectors();
		int minCount = Math.min(sectors.size(), tabbedPane.getTabCount());
		if (sectors.size() != tabbedPane.getTabCount())
		{
			getEditorFrame().documentIsModified = true;
		}
		for (int i = 0; i < minCount; i++)
        {
            Sector sector = sectors.elementAt(i);
            SectorPanel panel = (SectorPanel) tabbedPane.getComponentAt(i);
            if (isDifferent(panel.nameTextField.getText(), sector.getName(), stringResult))
            {
                sector.setName(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }
            if (isDifferent(panel.commentTextField.getText(), sector.getComment(), stringResult))
            {
                sector.setComment(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }
            if (isDifferent(panel.distanceFromStartTextField.getText(), sector.getDistanceFromStart(), doubleResult))
            {
                sector.setDistanceFromStart(doubleResult.getValue());
                getEditorFrame().documentIsModified = true;
            }
		}
		if (sectors.size() > tabbedPane.getTabCount())
		{
			// need to trim Sectors
			while (sectors.size() > tabbedPane.getTabCount())
			{
				sectors.remove(sectors.size() - 1);
			}
		}
		else if (sectors.size() < tabbedPane.getTabCount())
		{
			// need to add to sectors
			while (sectors.size() < tabbedPane.getTabCount())
			{
	            SectorPanel panel = (SectorPanel) tabbedPane.getComponentAt(sectors.size());
				Sector sector = new Sector();
				sector.setName(panel.nameTextField.getText());
				sector.setComment(panel.commentTextField.getText());
				sector.setDistanceFromStart(getDouble(panel.distanceFromStartTextField.getText()));
				sectors.add(sector);
			}
		}
	}
 } //  @jve:decl-index=0:visual-constraint="10,10"
