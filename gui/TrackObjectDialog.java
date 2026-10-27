package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import gui.properties.GraphicObjectData;
import utils.circuit.GraphicObject;
import utils.circuit.ObjShapeObject;
import utils.circuit.ObjectData;
import utils.circuit.TrackObject;

public class TrackObjectDialog extends JDialog
{
	private EditorFrame					editorFrame				= null;
	private GraphicObject				graphicObject			= null;
	private ObjShapeObject				objectShape				= null;
	private ObjectData					objectData				= null;
	private GraphicObjectData			graphicObjectDatum		= null;
	private Vector<GraphicObjectData>	graphicObjectData		= null;
	private Vector<TrackObject> 		trackObjects			= null;
	private boolean						ignoreActions			= true;
	private boolean						changed					= false;
	private boolean						isGraphicObject			= false;

	private int							rgb						= 0;
	
	private JLabel						objectMapLabel			= null;
	private JComboBox<String>			objectMapComboBox		= null;

	private JCheckBox					defaultCheckBox			= new JCheckBox();

	private JLabel						nameLabel				= null;
	private JTextField					nameTextField			= null;

	private JLabel						objectLabel				= new JLabel();
	private JComboBox<String>			objectComboBox			= null;

	private JLabel						colorLabel				= new JLabel();
	private JTextField					colorTextField			= new JTextField();

	private JLabel						trackLocationLabel		= new JLabel();
	private JTextField					trackLocationTextField	= new JTextField();

	private JLabel						imageLocationLabel		= new JLabel();
	private JTextField					imageLocationTextField	= new JTextField();

	private JLabel						orientationLabel		= new JLabel();
	private JTextField					orientationTextField	= new JTextField();

	private JLabel						heightLabel				= new JLabel();
	private JTextField					heightTextField			= new JTextField();

	private JLabel						commentLabel			= new JLabel();
	private JTextArea					commentTextArea			= new JTextArea();

	private JButton						applyButton				= new JButton();
	private JButton						cancelButton			= new JButton();

	public TrackObjectDialog(EditorFrame editorFrame, ObjShapeObject objectShape, int x, int y)
	{
		super();
		this.objectShape = objectShape;
		setTitle("Add Object");
		this.editorFrame = editorFrame;
		initialize(x, y);
	}

	public TrackObjectDialog(EditorFrame editorFrame, GraphicObject graphicObject, int x, int y)
	{
		super();
		this.graphicObject = graphicObject;
		this.objectShape = graphicObject.getShape();
		setTitle("Add Object");
		this.editorFrame = editorFrame;
		initialize(x, y);
	}

	public TrackObjectDialog(EditorFrame editorFrame, boolean all, ObjShapeObject objectShape, int x, int y)
	{
		super();
		this.objectShape = objectShape;
		if (all)
			setTitle("Edit All Objects");
		else
			setTitle("Edit Object");
		this.editorFrame = editorFrame;
		initialize(x, y);
	}

	public TrackObjectDialog(EditorFrame editorFrame, boolean all, GraphicObject graphicObject, int x, int y)
	{
		super();
		this.graphicObject = graphicObject;
		this.objectShape = graphicObject.getShape();
		if (all)
			setTitle("Edit All Objects");
		else
			setTitle("Edit Object");
		this.editorFrame = editorFrame;
		initialize(x, y);
	}

	public TrackObjectDialog(EditorFrame editorFrame, boolean all, ObjectData objectData)
	{
		super();
		this.objectData = objectData;
		setTitle("Add Object");
		this.editorFrame = editorFrame;
		initialize(0, 0);
	}

	public TrackObjectDialog(EditorFrame editorFrame, boolean all, GraphicObjectData graphicObjectDatum, Vector<GraphicObjectData> graphicObjectData)
	{
		super();
		this.graphicObjectDatum = graphicObjectDatum;
 		this.graphicObjectData = graphicObjectData;
 		setTitle("Edit Object");
		this.editorFrame = editorFrame;
		initialize(0, 0);
	}

	public boolean isChanged()
	{
		return changed;
	}

	private int getRGB()
	{
		if (objectShape != null)
		{
			return objectShape.getRGB();
		}

		if (graphicObjectDatum != null)
		{
			return graphicObjectDatum.color;
		}

		return objectData.color;
	}
	private void setRGB(int rgb)
	{
		if (objectShape != null)
		{
			objectShape.setRGB(rgb);
			return;
		}

		if (graphicObjectDatum != null)
		{
			graphicObjectDatum.color = rgb;
			return;
		}

		objectData.color = rgb;
	}

	private String getObjectName()
	{
		if (objectShape != null)
		{
			return objectShape.getName();
		}

		if (graphicObjectDatum != null)
		{
			return graphicObjectDatum.name;
		}
		
		return objectData.name;
	}

	private Point2D.Double getTrackLocation()
	{
		if (objectShape != null)
		{
			return objectShape.getTrackLocation();
		}

		if (graphicObjectDatum != null)
		{
			return new Point2D.Double(graphicObjectDatum.trackX, graphicObjectDatum.trackY);
		}

		return new Point2D.Double(objectData.trackX, objectData.trackY);
	}

	private double getImageX()
	{
		if (objectShape != null)
		{
			return objectShape.getImageX();
		}

		return objectData.imageX;
	}

	private double getImageY()
	{
		if (objectShape != null)
		{
			return objectShape.getImageY();
		}

		return objectData.imageY;
	}
	
	private double getOrientation()
	{
		if (graphicObject != null)
		{
			return graphicObject.getOrientation();
		}
		else if (graphicObjectDatum != null)
		{
			if (graphicObjectDatum.orientation != null)
			{
				return graphicObjectDatum.orientation;
			}
		}
		
		return Double.NaN;
	}
	
	private double getObjectHeight()
	{
		if (graphicObject != null)
		{
			return graphicObject.getHeight();
		}
		else if (graphicObjectDatum != null)
		{
			if (graphicObjectDatum.height != null)
			{
				return graphicObjectDatum.height;
			}
		}
		
		return Double.NaN;
	}
	
	private String getComment()
	{
		if (graphicObject != null)
		{
			return graphicObject.getComment();
		}
		else if (graphicObjectDatum != null)
		{
			if (graphicObjectDatum.comment != null)
			{
				return graphicObjectDatum.comment;
			}
		}
		
		return null;
	}

	private void initialize(int x, int y)
	{
		isGraphicObject = (objectShape != null && objectShape.getType().equals("graphic object")) || graphicObjectDatum != null;

		setLayout(null);
		setSize(320, 333);
		setResizable(false);
		if (objectData == null && graphicObjectDatum == null)
		{
			setLocation(x, y);
		}
		else
		{
			setLocationRelativeTo(getParent());
		}
		
		defaultCheckBox.setText("Default Objects");
		defaultCheckBox.setBounds(120, 10, 150, 23);
		defaultCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				defaultCheckBoxChanged();
			}
		});

		objectLabel.setText("Object");
		objectLabel.setBounds(10, 64, 120, 23);

		objectComboBox = new JComboBox<String>();
		objectComboBox.setBounds(120, 64, 170, 23);
		objectComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				objectComboBoxChanged();
			}
		});

		// search for color in track objects first
		trackObjects = editorFrame.getTrackData().getTrackObjects();
		int objectIndex	= -1;
		for (int i = 0; i < trackObjects.size(); i++)
		{
			if (trackObjects.get(i).getColor() == getRGB())
			{
				defaultCheckBox.setSelected(false);
				objectIndex = i;
				break;
			}
		}

		// the track only has the default objects when its Objects section includes
		// &default-objects; (Object Properties > Default Objects)
		boolean defaults = editorFrame.getTrackData().getHasObjectsReference();
		defaultCheckBox.setEnabled(defaults);
		if (!defaults)
			defaultCheckBox.setToolTipText("The track doesn't include the default objects (Object Properties > Default Objects)");

		// search in default objects if not found
		if (objectIndex == -1 && defaults)
		{
			trackObjects = editorFrame.getDefaultObjects();
			for (int i = 0; i < trackObjects.size(); i++)
			{
				if (trackObjects.get(i).getColor() == getRGB())
				{
					defaultCheckBox.setSelected(true);
					objectIndex = i;
					break;
				}
			}
		}

		if (objectIndex == -1 && defaults)
		{
			// no object has this color so show default objects
			defaultCheckBox.setSelected(true);
		}

		for (int i = 0; i < trackObjects.size(); i++)
		{
			objectComboBox.addItem(trackObjects.get(i).getName());
		}

		if (isGraphicObject)
		{
			nameLabel = new JLabel("Name");
			nameLabel.setBounds(10, 37, 120, 23);

			nameTextField = new JTextField(getObjectName());
			nameTextField.setBounds(120, 37, 170, 23);
		}
		else
		{
			if (editorFrame.getObjectMaps().size() > 0)
			{
				objectMapLabel = new JLabel("Object Map");
				objectMapLabel.setBounds(10, 37, 120, 23);
				
				objectMapComboBox = new JComboBox<String>();
				objectMapComboBox.setBounds(120, 37, 170, 23);
				for (int i = 0; i < editorFrame.getObjectMaps().size(); i++)
				{
					objectMapComboBox.addItem(editorFrame.getObjectMaps().get(i).getName());
				}
				objectMapComboBox.setSelectedIndex(editorFrame.getCurrentObjectMap());			
				objectMapComboBox.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						objectMapComboBoxChanged();
					}
				});
			}			
		}

		colorLabel.setText("Color");
		colorLabel.setBounds(10, 91, 120, 23);

		colorTextField.setBounds(120, 91, 170, 23);
		colorTextField.setEnabled(false);

		trackLocationLabel.setText("Track Location");
		trackLocationLabel.setBounds(10, 118, 120, 23);

		trackLocationTextField.setText(String.format("%.3f", getTrackLocation().x) + ", " + String.format("%.3f", getTrackLocation().y));
		trackLocationTextField.setBounds(120, 118, 170, 23);
		trackLocationTextField.setEnabled(false);

		if (!isGraphicObject)
		{
			imageLocationLabel.setText("Image Location");
			imageLocationLabel.setBounds(10, 145, 120, 23);

			imageLocationTextField.setText(getImageX() + ", " + getImageY());
			imageLocationTextField.setBounds(120, 145, 170, 23);
			imageLocationTextField.setEnabled(false);
		}
		else
		{
			orientationLabel.setText("Orientation");
			orientationLabel.setBounds(10, 145, 120, 23);

			if (!Double.isNaN(getOrientation()))
				orientationTextField.setText("" + getOrientation());
			orientationTextField.setBounds(120, 145, 170, 23);
			
			heightLabel.setText("Height");
			heightLabel.setBounds(10, 172, 120, 23);

			if (!Double.isNaN(getObjectHeight()))
				heightTextField.setText("" + getObjectHeight());
			heightTextField.setBounds(120, 172, 170, 23);
			
			commentLabel.setText("Comment");
			commentLabel.setBounds(10, 199, 120, 23);
			
			commentTextArea.setText(getComment());
			commentTextArea.setBounds(120, 199, 170, 50);
			commentTextArea.setLineWrap(true);
			commentTextArea.setWrapStyleWord(true);
		}

		applyButton.setBounds(50, 258, 70, 25);
		applyButton.setText("Apply");
		applyButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				applyButtonChanged();
			}
		});

		cancelButton.setBounds(185, 258, 70, 25);
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				cancelButtonChanged();
			}
		});

		if (isGraphicObject)
		{
			add(nameLabel);
			add(nameTextField);
		}
		else
		{
			add(objectMapLabel);
			add(objectMapComboBox);
		}

		add(objectLabel);
		add(objectComboBox);
		add(defaultCheckBox);
		add(colorLabel);
		add(colorTextField);
		add(trackLocationLabel);
		add(trackLocationTextField);

		if (!isGraphicObject)
		{
			add(imageLocationLabel);
			add(imageLocationTextField);
		}
		else
		{
			add(orientationLabel);
			add(orientationTextField);
			add(heightLabel);
			add(heightTextField);
			add(commentLabel);
			add(commentTextArea);
		}

		add(applyButton);
		add(cancelButton);

		ignoreActions = false;

		objectComboBox.setSelectedIndex(objectIndex);
	}

	private void objectMapComboBoxChanged()
	{
		if (ignoreActions)
			return;
		if (objectMapComboBox.getSelectedIndex() == -1)
		{
			JOptionPane.showMessageDialog(this, "No object map selected!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		editorFrame.setCurrentObjectMap(objectMapComboBox.getSelectedIndex());
	}
	
	private void objectComboBoxChanged()
	{
		if (ignoreActions)
			return;
		
		if (objectComboBox.getSelectedIndex() == -1)
		{
			colorTextField.setBackground(Color.WHITE);
			colorTextField.setForeground(Color.BLACK);
			colorTextField.setText("");
			rgb = 0;
			return;
		}

		if (!isGraphicObject || objectData != null)
		{
			imageLocationTextField.setText(getImageX() + ", " + getImageY());
		}

		trackLocationTextField.setText(String.format("%.3f", getTrackLocation().x) + ", " + String.format("%.3f", getTrackLocation().y));

		rgb = trackObjects.get(objectComboBox.getSelectedIndex()).getColor();
		
		Color backgroundColor = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);
		colorTextField.setBackground(backgroundColor);
		if ((backgroundColor.getRed()*0.299 + backgroundColor.getGreen()*0.587 + backgroundColor.getBlue()*0.114) > 186)
		{
			colorTextField.setForeground(Color.BLACK);
		}
		else
		{
			colorTextField.setForeground(Color.WHITE);
		}
		colorTextField.setText(String.format("0x%06X", rgb));
	}

	private void defaultCheckBoxChanged()
	{
		if (ignoreActions)
			return;
		
		if (defaultCheckBox.isSelected())
		{
			trackObjects = editorFrame.getDefaultObjects();
		}
		else
		{
			trackObjects = editorFrame.getTrackData().getObjects();
		}

		ignoreActions = true;
		objectComboBox.removeAllItems();
		int objectIndex = -1;

		for (int i = 0; i < trackObjects.size(); i++)
		{
			objectComboBox.addItem(trackObjects.get(i).getName());

			if (trackObjects.get(i).getColor() == getRGB())
			{
				objectIndex = i;
			}
		}

		ignoreActions = false;
		
		objectComboBox.setSelectedIndex(objectIndex);
	}

	private void applyButtonChanged()
	{
		if (objectComboBox.getSelectedIndex() == -1)
		{
			JOptionPane.showMessageDialog(this, "No object selected!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		setRGB(rgb);

		if (objectShape != null)
		{
			if (nameTextField != null)
			{
				String newName = nameTextField.getText();
	
				for (GraphicObject object : editorFrame.getGraphicObjects())
				{
					if (object.getShape() != objectShape)
					{
						if (object.getName().equals(newName))
						{
							JOptionPane.showMessageDialog(this, "Object name already used!", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
				}
				objectShape.setName(newName);
			}
			else
			{
				String newName = (String) objectComboBox.getSelectedItem();
				
				objectShape.setName(newName);
				editorFrame.getObjectMaps().get(editorFrame.getCurrentObjectMap()).setChanged(true);
			}
			
			if (graphicObject == null)
			{
				graphicObject = new GraphicObject(objectShape);
			}

			String orientationText = orientationTextField.getText();
			if (orientationText == null || orientationText.isEmpty())
			{
				graphicObject.setOrientation(Double.NaN);
			}
			else
			{
				try
				{
					graphicObject.setOrientation(Double.parseDouble(orientationText));
				}
				catch (NumberFormatException e)
				{
					JOptionPane.showMessageDialog(this, "Invalid orientation!\n\n" + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			
			String heightText = heightTextField.getText();
			if (heightText == null || heightText.isEmpty())
			{
				graphicObject.setHeight(Double.NaN);
			}
			else
			{
				try
				{
					graphicObject.setHeight(Double.parseDouble(heightText));
				}
				catch (NumberFormatException e)
				{
					JOptionPane.showMessageDialog(this, "Invalid height!\n\n" + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			
			graphicObject.setComment(new String(commentTextArea.getText()));
		}
		else if (graphicObjectDatum != null)
		{
			String newName = nameTextField.getText();

			for (GraphicObjectData objectData : graphicObjectData)
			{
				if (objectData != graphicObjectDatum)
				{
					if (objectData.name.equals(newName))
					{
						JOptionPane.showMessageDialog(this, "Object name already used!", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
			graphicObjectDatum.name = newName;

			String orientationText = orientationTextField.getText();
			if (orientationText == null || orientationText.isEmpty())
			{
				graphicObjectDatum.orientation = null;
			}
			else
			{
				try
				{
					graphicObjectDatum.orientation = Double.parseDouble(orientationText);
				}
				catch (NumberFormatException e)
				{
					JOptionPane.showMessageDialog(this, "Invalid orientation!\n\n" + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			
			String heightText = heightTextField.getText();
			if (heightText == null || heightText.isEmpty())
			{
				graphicObjectDatum.height = null;
			}
			else
			{
				try
				{
					graphicObjectDatum.height = Double.parseDouble(heightText);
				}
				catch (NumberFormatException e)
				{
					JOptionPane.showMessageDialog(this, "Invalid height!\n\n" + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}	
			
			graphicObjectDatum.comment = new String(commentTextArea.getText());
		}

		changed = true;
		dispose();
	}

	private void cancelButtonChanged()
	{
		dispose();
	}
}
