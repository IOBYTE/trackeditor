/*
 *   SegmentSlider.java
 *   Created on 28 ??? 2005
 *
 *    The SegmentSlider.java is part of TrackEditor-0.3.1.
 *
 *    TrackEditor-0.3.1 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.3.1 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.3.1; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package gui.segment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import utils.SegmentSliderLayout;
/**
 * @author Charalampos Alexopoulos
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SegmentSlider extends JPanel
{
	private Vector<SliderListener>	sliderListeners	= new Vector<SliderListener>();
	private JLabel			sectionLabel	= null;
	private JTextField		textField		= null;
	private JLabel			attLabel		= null;
	private JSlider			slider			= null;
	private JCheckBox		checkBox		= null;
	private boolean			enabled			= true;
	private boolean			optional		= false;

	private String			section;
	private String			attr;
	private double			min;
	private double			max;
	private double			defaultValue;
	private double			resolution;
	private String			method;
	private double			value			= Double.NaN;
	private NumberFormat	nf;
	private boolean			integerFormat	= false;

	/**
	 *  
	 */
	public SegmentSlider(double min, double max, double defaultValue, double resolution)
	{
		super();
		this.min = min;
		this.max = max;
		this.defaultValue = defaultValue;
		this.resolution = resolution;
		initialize();
		new SliderLink();
	}

	public SegmentSlider(double min, double max, double defaultValue, double resolution, double value, String section, String attr, String method, boolean optional, boolean integerFormat)
	{
		super();
		this.min = min;
		this.max = max;
		this.defaultValue = defaultValue;
		this.resolution = resolution;
		this.value = value;
		this.section = section;
		this.attr = attr;
		this.method = method;
		this.optional = optional;
		this.integerFormat = integerFormat;
		initialize();
		new SliderLink();
		setValue(value);
		setOptional(optional);
	}

	/**
	 *  
	 */
	private void initialize()
	{
		nf = NumberFormat.getNumberInstance(Locale.US);
		nf.setGroupingUsed(false);
		if (integerFormat)
		{
			nf.setMaximumFractionDigits(0);
			nf.setMinimumFractionDigits(0);
		}
		else
		{
			nf.setMaximumFractionDigits(3);
			nf.setMinimumFractionDigits(1);			
		}
		
		attLabel = new JLabel();
		sectionLabel = new JLabel();
		this.setLayout(new SegmentSliderLayout());
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
		this.setSize(50, 250);
		sectionLabel.setText(section);
		sectionLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
		sectionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		sectionLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		attLabel.setText(attr);
		attLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
		attLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
		attLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
		this.add(sectionLabel, null);
		this.add(getCheckBox(), null);
		this.add(attLabel, null);
		this.add(getTextField(), null);
		this.add(getSlider(), null);
	}

	public boolean getIntegerFormat()
	{
		return integerFormat;
	}

	private void setSliderValue(double value)
	{
		getSlider().setValue((int) Math.round(value / resolution));
	}

	private double getSliderValue()
	{
		return getSlider().getValue() * resolution;
	}

	/**
	 * This method initializes textField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTextField()
	{
		if (textField == null)
		{
			textField = new JTextField();
			textField.setHorizontalAlignment(JTextField.LEFT);			
			textField.getDocument().addDocumentListener(new DocumentListener()
			{
				public void changedUpdate(DocumentEvent e)
				{
					textChanged(e);
				}
				public void insertUpdate(DocumentEvent e)
				{
					textChanged(e);
				}
				public void removeUpdate(DocumentEvent e)
				{
					textChanged(e);
				}
				private void textChanged(DocumentEvent e)
				{
					Runnable doHighlight = new Runnable()
					{
						@Override
						public void run()
						{
							Document document = e.getDocument();
							try
							{
								String s = document.getText(0, document.getLength());
								if (s.equals("-") || s.equals(""))
								{
									double oldValue = value;
									setValueInternal(defaultValue);
									if (Math.abs(oldValue - value) >= resolution)
									{
										valueChanged();
									}
								}
								else if (!s.equals(""))
								{
									try
									{
										double oldValue = value;
										double tmp = Double.parseDouble(getTextField().getText());
										setValueInternal(tmp);
										if (Math.abs(oldValue - value) >= resolution)
										{
											valueChanged();
										}
									}
									catch (Exception ex)
									{
										// restore the old value
										getTextField().setText(nf.format(value));
									}
								}
							}
							catch (BadLocationException ex)
							{
								ex.printStackTrace();
							}
						}
					};
					SwingUtilities.invokeLater(doHighlight);
				}
			});
			textField.addKeyListener(new KeyAdapter()
			{
				public void keyTyped(KeyEvent e)
				{
					char c = e.getKeyChar();

					// check for valid digit or a single decimal point or a single minus when negative number allowed
					if (!(Character.isDigit(c) || (min < 0 && c == '-' && !textField.getText().contains("-")) || (!integerFormat && (c == '.' && !textField.getText().contains(".")))))
					{
						e.consume();
						return;
					}
				}
			});
		}
		return textField;
	}
	/**
	 * This method initializes slider
	 * 
	 * @return javax.swing.JSlider
	 */
	private JSlider getSlider()
	{
		if (slider == null)
		{
			slider = new JSlider();
			slider.setOrientation(JSlider.VERTICAL);
			slider.setMinimum((int) Math.floor(min / resolution));
			slider.setMaximum((int) Math.ceil(max / resolution));
		}
		return slider;
	}
	/**
	 * This method initializes checkBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCheckBox()
	{
		if (checkBox == null)
		{
			checkBox = new JCheckBox();
			checkBox.setEnabled(false);
		}
		return checkBox;
	}
	/**
	 * @return Returns the attr.
	 */
	public String getAttr()
	{
		return attr;
	}
	/**
	 * @param attr
	 *            The attr to set.
	 */
	public void setAttr(String attr)
	{
		this.attr = attr;
		this.attLabel.setText(attr);
	}
	/**
	 * @return Returns the section.
	 */
	public String getSection()
	{
		return section;
	}
	/**
	 * @param section
	 *            The section to set.
	 */
	public void setSection(String section)
	{
		this.section = section;
		this.sectionLabel.setText(section);
	}

	/**
	 * @return Returns the attLabel.
	 */
	public JLabel getAttLabel()
	{
		return attLabel;
	}
	/**
	 * @param attLabel
	 *            The attLabel to set.
	 */
	public void setAttLabel(JLabel attLabel)
	{
		this.attLabel = attLabel;
	}

	/**
	 * @return Returns the max.
	 */
	private double getMax()
	{
		return max;
	}
	/**
	 * @param max
	 *            The max to set.
	 */
	private void setMax(double max)
	{
		getSlider().setMaximum((int) Math.ceil(max / resolution));
		this.max = max;
	}
	/**
	 * @return Returns the min.
	 */
	private double getMin()
	{
		return min;
	}
	/**
	 * @param min
	 *            The min to set.
	 */
	private void setMin(double min)
	{
		getSlider().setMinimum((int) Math.floor(min / resolution));
		this.min = min;
	}

	public void setEnabled(boolean value)
	{
		this.enabled = value;
		if (this.optional)
			this.checkBox.setEnabled(value);
		this.getTextField().setEnabled(value);
		this.getSlider().setEnabled(value);
		this.sectionLabel.setEnabled(value);
		this.attLabel.setEnabled(value);
		if (!value)
		{
			this.getTextField().setText("");
			if (this.optional)
				this.checkBox.setSelected(false);
		}
	}

	public void setOptional(boolean value)
	{
		this.optional = value;
		this.checkBox.setEnabled(value);
		if (!value)
		{
			this.checkBox.setSelected(false);
		}
	}

	public boolean isCheckBoxSelected()
	{
		return checkBox.isSelected();
	}

	public void setCheckBoxSelected(boolean state)
	{
		checkBox.setSelected(state);
	}

	/**
	 * @return Returns the method.
	 */
	public String getMethod()
	{
		return method;
	}
	/**
	 * @param method
	 *            The method to set.
	 */
	public void setMethod(String method)
	{
		this.method = method;
	}
	/**
	 * @return Returns the enabled.
	 */
	public boolean isEnabled()
	{
		return enabled;
	}
	/**
	 * @return Returns the value.
	 */
	public double getValue()
	{
		if (integerFormat && Double.isNaN(value))
		{
			return Integer.MAX_VALUE;
		}

		return value;
	}
	/**
	 * @param value
	 *            The value to set.
	 */
	public void setValue(double val)
	{
		if (integerFormat && val == Integer.MAX_VALUE)
		{
			value = Double.NaN;
		}
		else
		{
			value = val;
		}

		if (Double.isNaN(value))
		{
			getTextField().setText("");
			getTextField().setEnabled(false);
			setSliderValue(defaultValue);
			getSlider().setEnabled(false);
			if (optional)
				checkBox.setSelected(false);
		}
		else
		{
			if (value > getMax())
			{
				int newMaximum = (int) Math.ceil(value);
				System.out.println("Increasing slider maximum to " + newMaximum + " was " + getMax());
				setMax(newMaximum);
			}
			else if (value < getMin())
			{
				int newMinimum = (int) Math.floor(value);
				System.out.println("Increasing slider minimum to " + newMinimum + " was " + getMin());
				setMin(newMinimum);
			}
			getTextField().setText(nf.format(value));
			getTextField().setCaretPosition(0);
			if (isEnabled())
			{
				getTextField().setEnabled(true);
				setSliderValue(value);
				getSlider().setEnabled(true);
				if (optional)
					checkBox.setSelected(true);
			}
		}
	}

	/**
	 * @param value
	 *            The frozen value to set.
	 */
	public void setValueFrozen(double val)
	{
		value = val;
		checkBox.setEnabled(false);
		getTextField().setEnabled(false);
		getSlider().setEnabled(false);
		sectionLabel.setEnabled(false);
		attLabel.setEnabled(false);

		if (Double.isNaN(val))
		{
			getTextField().setText("");
			setSliderValue(defaultValue);
			if (optional)
				checkBox.setSelected(false);
		}
		else
		{
			if (value > getMax())
			{
				int newMaximum = (int) Math.ceil(value);
				System.out.println("Increasing slider maximum to " + newMaximum + " was " + getMax());
				setMax(newMaximum);
			}
			else if (value < getMin())
			{
				int newMinimum = (int) Math.floor(value);
				System.out.println("Increasing slider minimum to " + newMinimum + " was " + getMin());
				setMin(newMinimum);
			}
			getTextField().setText(nf.format(value));
			getTextField().setCaretPosition(0);
			if (isEnabled())
			{
				setSliderValue(value);
				if (optional)
					checkBox.setSelected(true);
			}
		}
	}

	private void setValueInternal(double val)
	{
		value = val;
		setSliderValue(value);
		double textValue = 0;
		if (!getTextField().getText().equals("") && !getTextField().getText().equals("-"))
		{
			try
			{
				textValue = Double.parseDouble(getTextField().getText());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if (Math.abs(textValue - value) >= 0.001)
		{
			getTextField().setText(nf.format(value));
			getTextField().setCaretPosition(0);
			valueChanged();
		}
	}

	public synchronized void removeSliderListener(SliderListener l)
	{

	}

	public synchronized void addSliderListener(SliderListener l)
	{
		Vector<SliderListener> v = sliderListeners == null ? new Vector<SliderListener>(2) : (Vector<SliderListener>) sliderListeners.clone();
		if (!v.contains(l))
		{
			v.addElement(l);
			sliderListeners = v;
		}
	}

	private void valueChanged()
	{
		if (sliderListeners != null)
		{
			Vector<SliderListener> listeners = sliderListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).sliderChanged(this);
			}
		}
	}

	private void checkboxChanged()
    {
		if (sliderListeners != null)
		{
			Vector<SliderListener> listeners = sliderListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				listeners.elementAt(i).checkBoxChanged(this);
			}
		}
	}

	/** *** Inner class SliderLink****** */
	class SliderLink
	{
		SliderLink()
		{
			checkBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent actionEvent)
				{
					checkBoxChanged();
				}
			});

			getTextField().setEnabled(true);
			getTextField().addKeyListener(new KeyAdapter()
			{
				public void keyReleased(KeyEvent e)
				{
					textFieldChanged();
				}
			});

			getSlider().addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
				{
					if (slider.getValueIsAdjusting())
					{
						sliderChanged();
					}
				}
			});
		}

		public void checkBoxChanged()
		{
			checkboxChanged();
			
			double	oldValue = value;
			if (checkBox.isSelected())
			{
				if (Double.isNaN(value))
				{
					value = defaultValue;
				}
				if (oldValue != value)
				{
					getTextField().setText(nf.format(value));
					getTextField().setCaretPosition(0);
					getTextField().setEnabled(true);
					setSliderValue(value);
					getSlider().setEnabled(true);
					valueChanged();
				}
			}
			else
			{
				value = Double.NaN;
				if (!Double.isNaN(oldValue))
				{
					getTextField().setText("");
					getTextField().setEnabled(false);
					setSliderValue(defaultValue);
					getSlider().setEnabled(false);
					valueChanged();
				}
			}
		}

		public void sliderChanged()
		{
			if (!getTextField().getText().equals(""))
			{
				double tmp1 = getSliderValue();
				try
				{
					double tmp2 = Double.parseDouble(getTextField().getText());
					if (tmp1 != tmp2 && !(tmp2 >= tmp1 && tmp2 < (tmp1 + resolution)))
					{
						setValueInternal(tmp1);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		/**
		 *  
		 */
		protected void textFieldChanged()
		{
			if (!getTextField().getText().equals(""))
			{
				if (getTextField().getText().equals("-"))
				{
					setValueInternal(defaultValue);
				}
				else
				{
					try
					{
						double tmp = Double.parseDouble(getTextField().getText());
						setValueInternal(tmp);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
