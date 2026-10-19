package gui.properties;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import gui.EditorFrame;
import utils.MutableString;
import utils.circuit.ObjShapeRelief;
import utils.circuit.Reliefs;

public class ReliefProperties extends PropertyPanel
{
	private JTabbedPane		tabbedPane			= null;
	private JButton			addReliefButton		= null;
	private JButton			deleteReliefButton	= null;
	private Reliefs			reliefs				= null;

	ReliefProperties(EditorFrame editorFrame)
	{
		super(editorFrame);
		reliefs = getEditorFrame().getReliefs();
		initialize();
	}

	ReliefProperties(EditorFrame editorFrame, Reliefs reliefs)
	{
		super(editorFrame);
		this.reliefs = reliefs;
		initialize();
	}

	private void initialize()
	{
		setLayout(null);

		add(getTabbedPane(), null);
		add(getAddReliefButton(), null);
		add(getDeleteReliefButton(), null);
	}

	private JTabbedPane getTabbedPane()
	{
		if (tabbedPane == null)
		{
			tabbedPane = new JTabbedPane();
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			tabbedPane.setBounds(10, 10, 487, 280);

			for (int i = 0; i < reliefs.getReliefs().size(); i++)
			{
				tabbedPane.addTab("relief " + String.valueOf(i + 1), null, new ReliefPanel(reliefs.getReliefs().get(i)), null);
			}
		}
		return tabbedPane;
	}

	private JButton getAddReliefButton()
	{
		if (addReliefButton == null)
		{
			addReliefButton = new JButton();
			addReliefButton.setBounds(10, 300, 130, 25);
			addReliefButton.setText("Add Releif");
			addReliefButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					ObjShapeRelief relief = new ObjShapeRelief();

					// add points
					Rectangle2D.Double boundingRectangle = getEditorFrame().getBoundingRectangle();
					double center[] = { boundingRectangle.width / 2, boundingRectangle.height / 2 };
					double offset = tabbedPane.getTabCount() * 50;
					relief.setReliefType(ObjShapeRelief.ReliefType.Interior);
					relief.setLineType(ObjShapeRelief.LineType.Open);
					relief.addVertex(new double[] { center[0] - 50, 0, -(center[1] + offset) });
					relief.addVertex(new double[] { center[0] + 50, 0, -(center[1] + offset) });

					tabbedPane.addTab("relief " + String.valueOf(reliefs.getReliefs().size() + 1), null, new ReliefPanel(relief), null);
					tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
				}
			});
		}
		return addReliefButton;
	}

	private JButton getDeleteReliefButton()
	{
		if (deleteReliefButton == null)
		{
			deleteReliefButton = new JButton();
			deleteReliefButton.setBounds(155, 300, 140, 25);
			deleteReliefButton.setText("Delete Relief");
			deleteReliefButton.addActionListener(new java.awt.event.ActionListener()
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
		return deleteReliefButton;
	}

	private class ReliefPanel extends JPanel
	{
		private JLabel				reliefTypeLabel			= new JLabel();
		private JComboBox<String>	reliefTypeComboBox		= null;
		private JLabel				lineTypeLabel			= new JLabel();
		private JComboBox<String>	lineTypeComboBox		= null;
		private ReliefTablePanel	reliefTablePanel		= null;

		private final String[] 		reliefTypes 			= {"interior", "exterior"};
		private int					reliefTypeIndex			= 0;
		private final String[] 		lineTypes 				= {"closed", "open"};
		private int					lineTypeIndex			= 0;
		private Vector<double[]>	vertices				= new Vector<double[]>();

		public ReliefPanel(ObjShapeRelief reliefShape)
		{
			super();
			reliefTypeIndex = reliefShape.isInterior() ? 0 : 1;
			lineTypeIndex = reliefShape.isClosed() ? 0 : 1;
			for (int i = 0; i < reliefShape.getVertices().size(); i++)
			{
				vertices.add(new double[] { reliefShape.getVertices().get(i)[0], reliefShape.getVertices().get(i)[1], reliefShape.getVertices().get(i)[2]});
			}
			initialize();
		}

		private void initialize()
		{
			setLayout(null);

			reliefTypeLabel.setText("Relief Type");
			reliefTypeLabel.setBounds(10, 10, 120, 23);

			reliefTypeComboBox = new JComboBox<String>(reliefTypes);
			reliefTypeComboBox.setBounds(120, 10, 125, 23);
			reliefTypeComboBox.setSelectedIndex(reliefTypeIndex);

			reliefTypeComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
//					reliefTypeComboBoxChanged();
				}
			});

			lineTypeLabel.setText("Line Type");
			lineTypeLabel.setBounds(10, 37, 120, 23);

			lineTypeComboBox = new JComboBox<String>(lineTypes);
			lineTypeComboBox.setBounds(120, 37, 125, 23);
			lineTypeComboBox.setSelectedIndex(lineTypeIndex);

			lineTypeComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
//					lineTypeComboBoxChanged();
				}
			});

			add(reliefTypeLabel);
			add(reliefTypeComboBox);
			add(lineTypeLabel);
			add(lineTypeComboBox);
			add(getReliefTablePanel(vertices));
		}

		private ReliefTablePanel getReliefTablePanel(Vector<double[]> vertices)
		{
			if (reliefTablePanel == null)
			{
				reliefTablePanel = new ReliefTablePanel(vertices);
				reliefTablePanel.setBounds(10, 64, 465, 207);
			}
			return reliefTablePanel;
		}

		class ReliefTableModel extends AbstractTableModel
		{
			private final String[] 		columnNames = { null, "X", "Y", "Z" };
			private final Class<?>[] 	columnClass = new Class[]
					{
							Integer.class, Double.class, Double.class, Double.class
					};
			private Vector<double[]> vertices = null;

			ReliefTableModel(Vector<double[]> vertices)
			{
				this.vertices = vertices;
			}

			public int getRowCount()
			{
				return vertices.size();
			}

			public int getColumnCount()
			{
				return columnNames.length;
			}

			public String getColumnName(int columnIndex)
			{
				return columnNames[columnIndex];
			}

			public Class<?> getColumnClass(int columnIndex)
			{
				return columnClass[columnIndex];
			}

			public boolean isCellEditable(int row, int columnIndex)
			{
				if (columnIndex == 1 || columnIndex == 2 || columnIndex == 3)
				{
					return true;
				}
				return false;
			}

			public Object getValueAt(int rowIndex, int columnIndex)
			{
				switch (columnIndex)
				{
				case 0:
					return rowIndex + 1;
				case 1:
					return vertices.get(rowIndex)[0];
				case 2:
					return -vertices.get(rowIndex)[2];
				case 3:
					return vertices.get(rowIndex)[1];
				}
				return null;
			}

			public void setValueAt(Object value, int rowIndex, int columnIndex)
			{
				switch (columnIndex)
				{
				case 1:
					vertices.get(rowIndex)[0] = (Double) value;
					fireTableCellUpdated(rowIndex, columnIndex);
					break;
				case 2:
					vertices.get(rowIndex)[2] = -(Double) value;
					fireTableCellUpdated(rowIndex, columnIndex);
					break;
				case 3:
					vertices.get(rowIndex)[1] = (Double) value;
					fireTableCellUpdated(rowIndex, columnIndex);
					break;
				}
			}

			public void removeRowAt(int row)
			{
				vertices.removeElementAt(row);
				fireTableRowsDeleted(row - 1, vertices.size() - 1);
			}
		}

		class ReliefTablePanel extends JPanel
		{
			JTable 				table		= null;
			JScrollPane 		scrollPane	= null;
			ReliefTableModel	model		= null;

			public ReliefTablePanel(Vector<double[]> vertices)
			{
				super(new GridLayout(1,0));

				model = new ReliefTableModel(vertices);
				table = new JTable(model);
				scrollPane = new JScrollPane(table);
				table.getColumnModel().getColumn(0).setPreferredWidth(25);
				table.setAutoCreateRowSorter(true);
				table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

				JPopupMenu popupMenu = new JPopupMenu();
				JMenuItem deleteItem = new JMenuItem("Delete Row");
				table.addMouseListener(new MouseAdapter()
				{
					public void mouseClicked(MouseEvent me)
					{
						if (SwingUtilities.isRightMouseButton(me) == true)
						{
							int row = table.rowAtPoint(me.getPoint());
							if (table.getSelectedRow() != row)
							{
								table.setRowSelectionInterval(row, row);
							}
						}
					}
				});
				deleteItem.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						int row = table.getSelectedRow();
						if (row != -1)
						{
							if (JOptionPane.showConfirmDialog(null, "Delete this row?", "Delete Row", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
							{
								model.removeRowAt(table.convertRowIndexToModel(row));
							}
						}
					}
				});
				popupMenu.add(deleteItem);
				table.setComponentPopupMenu(popupMenu);

				add(scrollPane);
			}
		}
	}

	public void exit()
	{
		MutableString stringResult = new MutableString();
		Reliefs reliefs = getEditorFrame().getReliefs();
		int minReliefCount = Math.min(reliefs.getReliefs().size(), tabbedPane.getTabCount());
		if (reliefs.getReliefs().size() != tabbedPane.getTabCount())
		{
			getEditorFrame().documentIsModified = true;
			reliefs.setChanged(true);
		}
		for (int i = 0; i < minReliefCount; i++)
		{
			ObjShapeRelief relief = reliefs.getReliefs().elementAt(i);
			ReliefPanel panel = (ReliefPanel) tabbedPane.getComponentAt(i);
			String reliefType = panel.reliefTypes[panel.reliefTypeIndex];
			if (isDifferent(panel.reliefTypeComboBox.getSelectedItem().toString(), reliefType, stringResult))
			{
				relief.setReliefType(stringResult.getValue().equals(panel.reliefTypes[0]) ? ObjShapeRelief.ReliefType.Interior : ObjShapeRelief.ReliefType.Exterior);
				reliefs.setChanged(true);
			}
			String lineType = panel.lineTypes[panel.lineTypeIndex];
			if (isDifferent(panel.lineTypeComboBox.getSelectedItem().toString(), lineType, stringResult))
			{
				relief.setLineType(stringResult.getValue().equals(panel.lineTypes[0]) ? ObjShapeRelief.LineType.Closed : ObjShapeRelief.LineType.Open);
				reliefs.setChanged(true);
				getEditorFrame().documentIsModified = true;
			}

			int minVertexCount = Math.min(panel.vertices.size(), relief.getVertices().size());

			if (panel.vertices.size() != relief.getVertices().size())
			{
				getEditorFrame().documentIsModified = true;
				reliefs.setChanged(true);
			}
			for (int j = 0; j < minVertexCount; j++)
			{
				for (int k = 0; k < 3; k++)
				{
					if (panel.vertices.get(j)[k] != relief.getVertices().get(j)[k])
					{
						relief.getVertices().get(j)[k] = panel.vertices.get(j)[k];
						getEditorFrame().documentIsModified = true;
						reliefs.setChanged(true);
					}
				}
			}
			if (panel.vertices.size() < relief.getVertices().size())
			{
				// need to trim vertices
				while (relief.getVertices().size() > panel.vertices.size())
				{
					relief.deleteVertexAt(relief.getVertices().size() - 1);
				}
			}
			else if (relief.getVertices().size() < panel.vertices.size())
			{
				// need to add to vertices
				while (relief.getVertices().size() < panel.vertices.size())
				{
					relief.addVertex(panel.vertices.get(relief.getVertices().size()));
				}
			}
		}
		if (reliefs.getReliefs().size() > tabbedPane.getTabCount())
		{
			// need to trim reliefs
			while (reliefs.getReliefs().size() > tabbedPane.getTabCount())
			{
				reliefs.getReliefs().remove(reliefs.getReliefs().size() - 1);
			}
		}
		else if (reliefs.getReliefs().size() < tabbedPane.getTabCount())
		{
			// need to add to reliefs
			while (reliefs.getReliefs().size() < tabbedPane.getTabCount())
			{
				ReliefPanel panel = (ReliefPanel) tabbedPane.getComponentAt(reliefs.getReliefs().size());
				ObjShapeRelief shape = new ObjShapeRelief();
				shape.setReliefType(panel.reliefTypeComboBox.getSelectedItem().toString().equals("interior") ? ObjShapeRelief.ReliefType.Interior : ObjShapeRelief.ReliefType.Exterior);
				shape.setLineType(panel.lineTypeComboBox.getSelectedItem().toString().equals("closed") ? ObjShapeRelief.LineType.Closed : ObjShapeRelief.LineType.Open);
				shape.setVertices(panel.vertices);
				reliefs.getReliefs().add(shape);
			}
		}
	}
}
