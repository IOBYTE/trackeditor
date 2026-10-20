package gui.properties;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import gui.EditorFrame;
import gui.TrackObjectDialog;
import utils.Editor;
import utils.MutableString;
import utils.circuit.ObjShapeObject;
import utils.circuit.ObjectData;
import utils.circuit.ObjectMap;

public class ObjectMapProperties extends PropertyPanel
{
	private JTabbedPane		tabbedPane				= null;
	private JButton			addObjectMapButton		= null;
	private JButton			deleteObjectMapButton	= null;
	private final String 	sep						= System.getProperty("file.separator");

	ObjectMapProperties(EditorFrame editorFrame)
	{
		super(editorFrame);
		initialize();
	}

	private void initialize()
	{
		setLayout(null);

		add(getTabbedPane(), null);
		add(getAddObjectMapButton(), null);
		add(getDeleteObjectMapButton(), null);
	}

	private JTabbedPane getTabbedPane()
	{
		if (tabbedPane == null)
		{
			tabbedPane = new JTabbedPane();
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			tabbedPane.setBounds(10, 10, 487, 280);

			Vector<ObjectMap> objectMaps = getEditorFrame().getObjectMaps();

			for (int i = 0; i < objectMaps.size(); i++)
	        {
                ObjectMap objectMap = objectMaps.elementAt(i);
				tabbedPane.addTab(objectMap.getName(), null, new ObjectMapPanel(objectMap), null);
			}
		}
		return tabbedPane;
	}

	private JButton getAddObjectMapButton()
	{
		if (addObjectMapButton == null)
		{
			addObjectMapButton = new JButton();
			addObjectMapButton.setBounds(10, 300, 130, 25);
			addObjectMapButton.setText("Add Object Map");
			addObjectMapButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					String		name = "map " + (tabbedPane.getTabCount() + 1);
					ObjectMap	objectMap = new ObjectMap();
					objectMap.setName(name);

					tabbedPane.addTab(name, null, new ObjectMapPanel(objectMap), null);
					tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
				}
			});
		}
		return addObjectMapButton;
	}

	private JButton getDeleteObjectMapButton()
	{
		if (deleteObjectMapButton == null)
		{
			deleteObjectMapButton = new JButton();
			deleteObjectMapButton.setBounds(155, 300, 140, 25);
			deleteObjectMapButton.setText("Delete Object Map");
			deleteObjectMapButton.addActionListener(new java.awt.event.ActionListener()
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
		return deleteObjectMapButton;
	}

	private class ObjectMapPanel extends JPanel
	{
		private final int 			ROW_INDEX				= 0;
		private final int 			OBJECT_INDEX 			= 1;
		private final int 			COLOR_INDEX				= 2;
		private final int 			IMAGE_X_INDEX			= 3;
		private final int 			IMAGE_Y_INDEX			= 4;
		private final int 			TRACK_X_INDEX			= 5;
		private final int 			TRACK_Y_INDEX			= 6;

		private JLabel				nameLabel				= new JLabel();
		private JTextField 			nameTextField			= new JTextField();
		private JLabel				objectMapLabel			= new JLabel();
		private JTextField			objectMapTextField		= new JTextField();
		private JButton				objectMapCreateButton 	= null;
		private JButton				objectMapButton			= null;
		private ObjectTablePanel	objectTablePanel		= null;		
		private ObjectMap			objectMap				= null;

		private Vector<ObjectData> data = new Vector<ObjectData>();

		public Vector<ObjectData> getData()
		{
			return data;
		}

		/**
		 *
		 */
		public ObjectMapPanel(ObjectMap objectMap)
		{
			super();
			this.objectMap = objectMap;
			initialize();
		}

		/**
		 *
		 */
		private void initialize()
		{
			setLayout(null);

			addLabel(this, 0, nameLabel, "Name", 120);
			addLabel(this, 1, objectMapLabel, "Object Map", 120);

			addTextField(this, 0, nameTextField, objectMap.getName(), 115, 100);
			addTextField(this, 1, objectMapTextField, objectMap.getObjectMap(), 115, 275);

			add(getObjectMapCreateButton(), null);
			add(getObjectMapButton(), null);
			add(getObjectTablePanel(objectMap), null);

			objectMapCreateButton.setEnabled(objectMap.getObjectMap() == null || objectMap.getObjectMap().isEmpty());

			objectMapTextField.getDocument().addDocumentListener(new DocumentListener()
			{
				public void changedUpdate(DocumentEvent ev)
				{
					changed();
				}
				public void removeUpdate(DocumentEvent ev)
				{
					changed();
				}
				public void insertUpdate(DocumentEvent ev)
				{
					changed();
				}

				public void changed()
				{
					// update table with new data
					try
					{
						getDataFromImage(Editor.getProperties().getPath() + sep + objectMapTextField.getText());
						objectTablePanel.dataChanged();
					}
					catch (IOException ex)
					{
					}
				}
			});
		}

 		/**
		 * This method initializes objectMapCreateButton
		 *
		 * @return javax.swing.JButton
		 */
		private JButton getObjectMapCreateButton()
		{
			if (objectMapCreateButton == null)
			{
				objectMapCreateButton = new JButton();
				objectMapCreateButton.setBounds(395, 6, 80, 25);
				objectMapCreateButton.setText("Create");
				objectMapCreateButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent ev)
					{
						try
						{
							objectMapCreateFile();
						}
						catch (IOException ex)
						{
							JOptionPane.showMessageDialog(ObjectMapPanel.this, "Couldn't create the object map\n\n" + ex.getLocalizedMessage(),
									"Create Object Map", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
			}
			return objectMapCreateButton;
		}

		protected void objectMapCreateFile() throws IOException
		{
			int index = tabbedPane.getSelectedIndex() + 1;

			Path filename = Paths.get(Editor.getProperties().getPath() + sep + "object-map" + index + ".png");

			if (filename.toFile().exists())
			{
				String[] options = {"Overwrite", "Use Existing", "Cancel"};

				int option = JOptionPane.showOptionDialog(this, filename.getFileName().toString() + " already exists!",
						"Create Object Map", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

				if (option == 1)
				{
					objectMapTextField.setText(filename.getFileName().toString());

					getDataFromImage(filename.toString());
					objectTablePanel.dataChanged();
					return;
				}
				else if (option != 0)
				{
					// Cancel, or the dialog was closed: leave the file alone
					return;
				}
			}

			Rectangle2D.Double boundingRectangle = getEditorFrame().getBoundingRectangle();

			int imageWidth = 1024;
			int imageHeight = (int)(1024 * (boundingRectangle.height / boundingRectangle.width));

			ObjectMap.createImage(filename, imageWidth, imageHeight);

			objectMapTextField.setText(filename.getFileName().toString());

			getDataFromImage(filename.toString());
			objectTablePanel.dataChanged();
		}

		/**
		 * This method initializes objectMapButton
		 *
		 * @return javax.swing.JButton
		 */
		private JButton getObjectMapButton()
		{
			if (objectMapButton == null)
			{
				objectMapButton = new JButton();
				objectMapButton.setBounds(395, 36, 80, 25);
				objectMapButton.setText("Browse");
				objectMapButton.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						objectMapFile();
					}
				});
			}
			return objectMapButton;
		}

		protected void objectMapFile()
		{
			Boolean old = UIManager.getBoolean("FileChooser.readOnly");
			UIManager.put("FileChooser.readOnly", Boolean.TRUE);
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFiles(null);
			fc.setSelectedFile(null);
			fc.rescanCurrentDirectory();
			fc.setApproveButtonMnemonic(0);
			fc.setDialogTitle("Object Map image file selection");
			fc.setFileHidingEnabled(false);
			fc.setVisible(true);
			fc.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("RGB and PNG images", "rgb", "png");
			fc.addChoosableFileFilter(filter);
			fc.setCurrentDirectory(new File(Editor.getProperties().getPath()));
			int result = fc.showOpenDialog(this);
			UIManager.put("FileChooser.readOnly", old);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				String selectedFile = fc.getSelectedFile().toString();
				String fileName = selectedFile;
				int index = selectedFile.lastIndexOf(sep);
				String pathToFile = selectedFile.substring(0, index);
				if (pathToFile.equals(Editor.getProperties().getPath()))
					fileName = selectedFile.substring(index + 1);
				objectMapTextField.setText(fileName);

				// update table with new data
				try
				{
					getDataFromImage(selectedFile);
					objectTablePanel.dataChanged();
				}
				catch (IOException e)
				{
				}
			}
		}

    	public void getDataFromImage(String fileName) throws IOException
    	{
    		data.clear();

			File file = new File(fileName);

			if (file.exists())
			{
				BufferedImage image = ImageIO.read(file);
				int imageWidth = image.getWidth();
				int imageHeight = image.getHeight();

				for (int x = 0; x < imageWidth; x++)
				{
					for (int y = 0; y < imageHeight; y++)
					{
						int rgb = image.getRGB(x, y) & 0x00ffffff;

						if (rgb != 0x0)
						{
							String name = getEditorFrame().getObjectColorName(rgb);
							if (name == null)
							{
								name = new String("Unknown");
							}
							Point2D.Double real = new Point2D.Double();
							getEditorFrame().getCircuitView().imageToReal(x, y, imageWidth, imageHeight, real);
							data.add(new ObjectData(name, rgb, x, y, real.x, real.y));
						}
					}
				}
			}
    	}

    	public void getDataFromObjectMap(ObjectMap objectMap)
    	{
    		data.clear();

			for (ObjShapeObject object : objectMap.getObjects())
			{
				String name = getEditorFrame().getObjectColorName(object.getRGB());

				if (name == null)
				{
					name = new String("Unknown");
				}

				Point2D.Double real = new Point2D.Double();
				getEditorFrame().getCircuitView().imageToReal(object.getImageX(), object.getImageY(), objectMap.getImageWidth(), objectMap.getImageHeight(), real);
				data.add(new ObjectData(name, object.getRGB(), object.getImageX(), object.getImageY(), real.x, real.y));
			}
    	}

		private ObjectTablePanel getObjectTablePanel(ObjectMap objectMap)
		{
			if (objectTablePanel == null)
			{
				objectTablePanel = new ObjectTablePanel(objectMap);
				objectTablePanel.setBounds(10, 64, 465, 207);
			}
			return objectTablePanel;
		}

		public class ColorRenderer extends DefaultTableCellRenderer
		{
		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		        boolean hasFocus, int row, int column)
		    {
		        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		        JLabel label = (JLabel)c;

		        if (column == COLOR_INDEX)
		    	{
		    		int rgb = Integer.decode(value.toString());
		    		Color color = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);
		    		label.setBackground(color);
		    		if ((color.getRed()*0.299 + color.getGreen()*0.587 + color.getBlue()*0.114) > 186)
		    		{
			        	label.setForeground(Color.BLACK);
		    		}
		    		else
		    		{
			        	label.setForeground(Color.WHITE);
		    		}
		    	}
		        else
		        {
		        	label.setBackground(Color.WHITE);
		        	label.setForeground(Color.BLACK);
		        }

		        return label;
		    }
		}

		class ObjectTableModel extends AbstractTableModel
	    {
	        private final String[] 		columnNames = { null, "Object", "Color", "Image X", "Image Y", "Track X", "Track Y" };
	        private final Class<?>[] 	columnClass = new Class[]
	        {
	        	Integer.class, String.class, Integer.class, Integer.class, Integer.class, Double.class, Double.class
	        };
			private ObjectMap 			objectMap = null;
			private Point2D.Double 		real = new Point2D.Double();

	        ObjectTableModel(ObjectMap objectMap)
			{
	        	this.objectMap = objectMap;
	        	getDataFromObjectMap(objectMap);
	        }

			public int getRowCount()
			{
				return data.size();
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
	        	if (columnIndex == OBJECT_INDEX || columnIndex == IMAGE_X_INDEX || columnIndex == IMAGE_Y_INDEX)
	        	{
	        		return true;
	        	}
	        	return false;
	        }

			public Object getValueAt(int rowIndex, int columnIndex)
			{
				ObjectData datum = data.get(rowIndex);

				switch (columnIndex)
				{
				case ROW_INDEX:
					return rowIndex + 1;
				case OBJECT_INDEX:
					return datum.name;
				case COLOR_INDEX:
					return String.format("0x%06X", datum.color);
				case IMAGE_X_INDEX:
					return datum.imageX;
				case IMAGE_Y_INDEX:
					return datum.imageY;
				case TRACK_X_INDEX:
					return datum.trackX;
				case TRACK_Y_INDEX:
					return datum.trackY;
				}
				return null;
			}

			public void setValueAt(Object value, int rowIndex, int columnIndex)
			{
				ObjectData datum = data.get(rowIndex);

				switch (columnIndex)
				{
				case OBJECT_INDEX:
					datum.name = (String) value;
			        fireTableCellUpdated(rowIndex, columnIndex);

			        if (value.equals("Unknown"))
			        {
			        	datum.color = objectMap.getObjects().get(rowIndex).getRGB();
			        }
			        else
			        {
			        	datum.color = getEditorFrame().getObjectColor(datum.name);
			        }
			        fireTableCellUpdated(rowIndex, COLOR_INDEX);
			        break;
				case COLOR_INDEX:
					datum.color = (Integer) value;
					fireTableCellUpdated(rowIndex, columnIndex);
					datum.name = getEditorFrame().getObjectColorName(datum.color);
					fireTableCellUpdated(rowIndex, OBJECT_INDEX);
					break;
				case IMAGE_X_INDEX:
					datum.imageX = (Integer) value;
					fireTableCellUpdated(rowIndex, columnIndex);			        
					getEditorFrame().getCircuitView().imageToReal(datum.imageX, datum.imageY, objectMap.getImageWidth(), objectMap.getImageHeight(), real);
					datum.trackX = real.x;
					fireTableCellUpdated(rowIndex, TRACK_X_INDEX);			        
					break;
				case IMAGE_Y_INDEX:
					datum.imageY = (Integer) value;
					fireTableCellUpdated(rowIndex, columnIndex);			        
					getEditorFrame().getCircuitView().imageToReal(datum.imageX, datum.imageY, objectMap.getImageWidth(), objectMap.getImageHeight(), real);
					datum.trackY = real.y;
					fireTableCellUpdated(rowIndex, TRACK_Y_INDEX);			        
					break;
				case TRACK_X_INDEX:
					datum.trackX = (Double) value;
					fireTableCellUpdated(rowIndex, columnIndex);
					break;
				case TRACK_Y_INDEX:
					datum.trackY = (Double) value;
					fireTableCellUpdated(rowIndex, columnIndex);
					break;
				}
		    }

			public void removeRowAt(int row)
			{
				data.removeElementAt(row);
				fireTableRowsDeleted(row - 1, data.size() - 1);
			}
	    }

	    public void setUpNameColumn(JTable table, TableColumn nameColumn, Set<String> names)
	    {
	    	//Set up the editor for the name cells.
	    	JComboBox<String> comboBox = new JComboBox<String>();

	    	Iterator<String> it = names.iterator();
			while (it.hasNext())
			{
				comboBox.addItem(it.next());
			}

			comboBox.addItem("Unknown");

			nameColumn.setCellEditor(new DefaultCellEditor(comboBox));

	    	//Set up tool tips for the name cells.
	    	DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
	    	//renderer.setToolTipText("Click to change object name");
	    	nameColumn.setCellRenderer(renderer);
	    }

		class ObjectTablePanel extends JPanel
		{
	        JTable 				table		= null;
	        JScrollPane 		scrollPane	= null;
	        ObjectTableModel	model		= null;

	        public ObjectTablePanel(ObjectMap objectMap)
			{
		        super(new GridLayout(1,0));

		        model = new ObjectTableModel(objectMap);
		        table = new JTable(model);
		        scrollPane = new JScrollPane(table);
		        table.getColumnModel().getColumn(ROW_INDEX).setPreferredWidth(35);
		        table.getColumnModel().getColumn(OBJECT_INDEX).setPreferredWidth(120);
		        table.setDefaultRenderer(Integer.class, new ColorRenderer());
		        table.setAutoCreateRowSorter(true);
		        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		        Set<String> names = getEditorFrame().getObjectColorNames();

		        setUpNameColumn(table, table.getColumnModel().getColumn(OBJECT_INDEX), names);

		        table.addMouseListener(new MouseAdapter()
		        {
		            @Override
		        	public void mouseClicked(MouseEvent me)
		        	{
		            	if (SwingUtilities.isRightMouseButton(me) == true)
		        		{
		        			int row = table.rowAtPoint(me.getPoint());
			        		if (row != -1)
		        			{
		        				table.setRowSelectionInterval(row, row);
		        		        if (me.getComponent() instanceof JTable )
		        		        {
		        		            JPopupMenu popup = createPopupMenu(objectTablePanel);
		        		            popup.show(me.getComponent(), me.getX(), me.getY());
		        		        }
		        			}
		        		}
		        	}
		        });

		        add(scrollPane);
		    }

	        void dataChanged()
	        {
	        	model.fireTableDataChanged();
	        }
		}

		public JPopupMenu createPopupMenu(ObjectTablePanel panel)
		{
			if (panel.table.isEditing())
			{
				int row = panel.table.getEditingRow();
				int col = panel.table.getEditingColumn();
				if (row < panel.table.getRowCount())
				{
					TableCellEditor cellEditor = panel.table.getCellEditor(row, col);
					cellEditor.stopCellEditing();
				}
			}

			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem editItem = new JMenuItem("Edit Object");
			JMenuItem editAllColorItem = new JMenuItem("Edit All Objects With This Color");
			JMenuItem deleteItem = new JMenuItem("Delete Object");
			JMenuItem deleteAllColorItem = new JMenuItem("Delete All Objects With This Color");
			JMenuItem deleteAllNameItem = new JMenuItem("Delete All Objects With This Name");
			JMenuItem moveToObjects = new JMenuItem("Move To Objects");
			JMenuItem moveAllToObjects = new JMenuItem("Move All To Objects");

			editItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int row = panel.table.getSelectedRow();
					if (row != -1)
					{
						ObjectData datum = data.elementAt(panel.table.convertRowIndexToModel(row));

						TrackObjectDialog editObjectDialog = new TrackObjectDialog(getEditorFrame(), false, datum);

						editObjectDialog.setModal(true);
						editObjectDialog.setVisible(true);

						if (editObjectDialog.isChanged())
						{
							panel.model.setValueAt(datum.name, row, OBJECT_INDEX);
							panel.model.setValueAt(datum.color, row, COLOR_INDEX);

							getEditorFrame().documentIsModified = true;
						}
					}
				}
			});
			editAllColorItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int row = panel.table.getSelectedRow();
					if (row != -1)
					{
						ObjectData datum = data.elementAt(panel.table.convertRowIndexToModel(row));
						int	oldColor = datum.color;

						TrackObjectDialog editObjectDialog = new TrackObjectDialog(getEditorFrame(), false, datum);

						editObjectDialog.setModal(true);
						editObjectDialog.setVisible(true);

						if (editObjectDialog.isChanged())
						{
							for (int i = 0; i < data.size(); i++)
							{
								if (data.elementAt(i).color == oldColor)
								{
									panel.model.setValueAt(datum.name, i, OBJECT_INDEX);
									panel.model.setValueAt(datum.color, i, COLOR_INDEX);
								}
							}

							getEditorFrame().documentIsModified = true;
						}
					}
				}
			});
		    deleteItem.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent e)
	            {
	            	int row = panel.table.getSelectedRow();
	            	if (row != -1)
	            	{
	            		if (JOptionPane.showConfirmDialog(null, "Delete this object?", "Delete Object", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
	            		{
	            			panel.model.removeRowAt(panel.table.convertRowIndexToModel(row));
	            		}
	            	}
	            }
	        });
	        deleteAllColorItem.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent e)
	            {
	            	int row = panel.table.getSelectedRow();
	            	if (row != -1)
	            	{
	            		if (JOptionPane.showConfirmDialog(null, "Delete all objects with this color?", "Delete Objects With Color", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
	            		{
	            			int color = data.elementAt(panel.table.convertRowIndexToModel(row)).color;
	            			Vector<Integer> toDelete = new Vector<Integer>();
	            			for (int i = 0; i < data.size(); i++)
	            			{
	            				if (color == data.elementAt(i).color)
	            				{
	            					toDelete.add(i);
	            				}
	            			}
	            			Collections.sort(toDelete, new Comparator<Integer>()
	            			{
	                            @Override
	                            public int compare(Integer o1, Integer o2)
	                            {
	                                // Changing the order of the elements
	                                return o2 - o1;
	                            }
	                        });
	            			for (int i = 0; i < toDelete.size(); i++)
	            			{
	            				panel.model.removeRowAt(toDelete.elementAt(i));
	            			}
	            		}
	            	}
	            }
	        });
	        deleteAllNameItem.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent e)
	            {
	            	int row = panel.table.getSelectedRow();
	            	if (row != -1)
	            	{
	            		if (JOptionPane.showConfirmDialog(null, "Delete all objects with this name?", "Delete Objects with Name", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
	            		{
	            			String name = new String(data.elementAt(panel.table.convertRowIndexToModel(row)).name);
	            			Vector<Integer> toDelete = new Vector<Integer>();
	            			for (int i = 0; i < data.size(); i++)
	            			{
	            				if (name.equals(data.elementAt(i).name))
	            				{
	            					toDelete.add(i);
	            				}
	            			}
	            			Collections.sort(toDelete, new Comparator<Integer>()
	            			{
	                            @Override
	                            public int compare(Integer o1, Integer o2)
	                            {
	                                // Changing the order of the elements
	                                return o2 - o1;
	                            }
	                        });
	            			for (int i = 0; i < toDelete.size(); i++)
	            			{
	            				panel.model.removeRowAt(toDelete.elementAt(i));
	            			}
	            		}
	            	}
	            }
	        });
		    moveToObjects.addActionListener(new ActionListener()
	        {
				public void actionPerformed(ActionEvent e)
				{
	            	int row = panel.table.getSelectedRow();
	            	if (row != -1)
	            	{
	            		if (JOptionPane.showConfirmDialog(null, "Move this object?", "Move Object", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
	            		{
	            			ObjectData datum = data.elementAt(panel.table.convertRowIndexToModel(row));											
							String name = getEditorFrame().getObjectColorName(datum.color) + "-" + data.size();
		            		GraphicObjectData	graphicObjectData = new GraphicObjectData(name, null, datum.color, datum.trackX, datum.trackY, Double.NaN, Double.NaN);
		            		getEditorFrame().getGraphicObjectProperties().addData(graphicObjectData);
	            			panel.model.removeRowAt(panel.table.convertRowIndexToModel(row));
	            		}
					}
				}
	        });
		    moveAllToObjects.addActionListener(new ActionListener()
		    {
				public void actionPerformed(ActionEvent e)
				{
	            	int row = panel.table.getSelectedRow();
	            	if (row != -1)
	            	{
	            		if (JOptionPane.showConfirmDialog(null, "Move all objects with this name?", "Move Objects with Name", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
	            		{
	            			ObjectData datum = data.elementAt(panel.table.convertRowIndexToModel(row));
	            			Vector<Integer> toMove = new Vector<Integer>();
	            			for (int i = 0; i < data.size(); i++)
	            			{
	            				if (datum.name.equals(data.elementAt(i).name))
	            				{
	            					toMove.add(i);
	            				}
	            			}
	            			Collections.sort(toMove, new Comparator<Integer>()
	            			{
	                            @Override
	                            public int compare(Integer o1, Integer o2)
	                            {
	                                // Changing the order of the elements
	                                return o2 - o1;
	                            }
	                        });
	            			int size = data.size();
	            			for (int i = 0; i < toMove.size(); i++)
	            			{
	            				ObjectData datum1 = data.elementAt(i);											
								String name = getEditorFrame().getObjectColorName(datum.color) + "-" + size++;
			            		GraphicObjectData	graphicObjectData = new GraphicObjectData(name, null, datum.color, datum1.trackX, datum1.trackY, Double.NaN, Double.NaN);
			            		getEditorFrame().getGraphicObjectProperties().addData(graphicObjectData);
	            			}	            			
	            			for (int i = 0; i < toMove.size(); i++)
	            			{
	            				panel.model.removeRowAt(toMove.elementAt(i));
	            			}	            			
	            		}
	            	}
				}
		    });

	        popupMenu.add(editItem);
	        popupMenu.add(editAllColorItem);
	        popupMenu.addSeparator();
	        popupMenu.add(deleteItem);
	        popupMenu.add(deleteAllColorItem);
	        popupMenu.add(deleteAllNameItem);
		    //popupMenu.addSeparator();
	        //popupMenu.add(moveToObjects);
	        //popupMenu.add(moveAllToObjects);

	        return popupMenu;
	    }
	}

	public void exit()
	{
		MutableString stringResult = new MutableString();
        Vector<ObjectMap> objectMaps = getEditorFrame().getObjectMaps();
		int minCount = Math.min(objectMaps.size(), tabbedPane.getTabCount());
		if (objectMaps.size() != tabbedPane.getTabCount())
		{
			getEditorFrame().documentIsModified = true;
		}
		for (int i = 0; i < minCount; i++)
        {
            ObjectMap objectMap = objectMaps.elementAt(i);
            ObjectMapPanel panel = (ObjectMapPanel) tabbedPane.getComponentAt(i);
            if (isDifferent(panel.nameTextField.getText(), objectMap.getName(), stringResult))
            {
                objectMap.setName(stringResult.getValue());
                getEditorFrame().documentIsModified = true;
            }
            if (isDifferent(panel.objectMapTextField.getText(), objectMap.getObjectMap(), stringResult))
            {
            	try
            	{
            		objectMap.setObjectMap(stringResult.getValue());
            	}
            	catch (IOException e)
            	{
            	}
                getEditorFrame().documentIsModified = true;
            }

            Vector<ObjectData> data = panel.getData();
            Vector<ObjShapeObject>	objects = objectMap.getObjects();
    		int minDataCount = Math.min(data.size(), objects.size());

    		if (data.size() != objects.size())
    		{
    			getEditorFrame().documentIsModified = true;
    			objectMap.setChanged(true);
    		}
            for (int j = 0; j < minDataCount; j++)
            {
            	ObjectData datum = data.get(j);
            	ObjShapeObject object = objects.get(j);

            	if (!datum.color.equals(object.getRGB()))
            	{
            		object.setRGB(datum.color);
        			getEditorFrame().documentIsModified = true;
            	}

            	if (!datum.imageX.equals(object.getImageX()))
            	{
            		object.setImageX(datum.imageX);
            		object.setTrackLocation(datum.trackX, datum.trackY);
        			getEditorFrame().documentIsModified = true;
            	}

            	if (!datum.imageY.equals(object.getImageY()))
            	{
            		object.setImageY(datum.imageY);
            		object.setTrackLocation(datum.trackX, datum.trackY);
        			getEditorFrame().documentIsModified = true;
            	}

            	if (getEditorFrame().documentIsModified)
            	{
            		objectMap.setChanged(true);
            		
            		// force recalculation of points
            		object.points = null;
            	}
            }
            // recalculate colors
            if (getEditorFrame().documentIsModified)
            {
            	objectMap.recalculateColors();
            }
            if (data.size() < objects.size())
            {
            	// need to trim objects
            	while (objects.size() > data.size())
            	{
            		objects.remove(objects.size() - 1);
            	}
            }
            else if (objects.size() < data.size())
            {
    			// need to add to objects
    			while (objects.size() < data.size())
    			{
    				ObjectData datum = data.get(objects.size());

    				objects.add(new ObjShapeObject(datum.color, datum.imageX, datum.imageY));
    			}
    		}
		}
		if (objectMaps.size() > tabbedPane.getTabCount())
		{
			// need to trim objectMaps
			while (objectMaps.size() > tabbedPane.getTabCount())
			{
				objectMaps.remove(objectMaps.size() - 1);
			}
		}
		else if (objectMaps.size() < tabbedPane.getTabCount())
		{
			// need to add to objectMaps
			while (objectMaps.size() < tabbedPane.getTabCount())
			{
	            ObjectMapPanel panel = (ObjectMapPanel) tabbedPane.getComponentAt(objectMaps.size());
				ObjectMap objectMap = new ObjectMap();
				objectMap.setName(panel.nameTextField.getText());
				try
				{
					objectMap.setObjectMap(panel.objectMapTextField.getText());
            	}
            	catch (IOException e)
            	{
            	}
				objectMaps.add(objectMap);
			}
		}
	}
}
