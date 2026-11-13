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
	// > 0 while the slider changes its own text
	private int settingText = 0;

	/**
	 * Sets the text field without that counting as the user typing a value.
	 */
	private void setTextQuietly(String text)
	{
		settingText++;
		try
		{
			getTextField().setText(text);
		}
		finally
		{
			settingText--;
		}
	}

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
					// only typing is an edit: text the slider sets itself (a new value,
					// or clearing it when disabled) must not be written back to the track
					if (settingText > 0)
						return;
					// a value typed replaces the text the file had
					if (fileText != null)
					{
						fileText = null;
						showText();
					}

					Runnable doHighlight = new Runnable()
					{
						@Override
						public void run()
						{
							Document document = e.getDocument();
							try
							{
								String s = document.getText(0, document.getLength());
								// cleared, or only a minus so far: the number is still being
								// typed, so the value stays as it is (writing the default in
								// would change the segment and the text being typed); leaving
								// the field puts the value back (see the focus listener)
								if (s.equals("-") || s.equals(""))
								{
									return;
								}
								else
								{
									try
									{
										double oldValue = value;
										double tmp = Double.parseDouble(getTextField().getText());
										// a value outside the limits isn't used: the box says why,
										// and leaving it puts the value back
										String refused = refused(tmp);
										showRefused(refused);
										if (refused != null)
											return;
										setValueInternal(tmp);
										// any change typed is the segment's, even one finer than
										// the slider's steps: the box shows it, so it must apply
										if (Double.compare(oldValue, value) != 0)
										{
											valueChanged();
										}
									}
									catch (Exception ex)
									{
										// restore the old value
										setTextQuietly(nf.format(value));
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
			// left cleared or with only a minus: show the value it still has
			textField.addFocusListener(new java.awt.event.FocusAdapter()
			{
				@Override
				public void focusLost(java.awt.event.FocusEvent e)
				{
					String s = textField.getText();
					if ((s.equals("") || s.equals("-") || refusedText != null) && !Double.isNaN(value) && textField.isEnabled())
						setTextQuietly(nf.format(value));
					showRefused(null);
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

	// the check box was enabled so a value with a problem can be removed
	private boolean	problemUnlocked	= false;

	/**
	 * Shows the value in red, with why in its tool tip, or as usual again. A
	 * value that can't be changed here (frozen: the style has no such value) can
	 * still be removed, by unticking its check box.
	 *
	 * @param tip the tool tip saying what is wrong, null for none
	 */
	public void setProblem(String tip)
	{
		java.awt.Color red = java.awt.Color.RED;
		boolean frozen = !getTextField().isEnabled();
		if (tip != null && frozen && optional && !Double.isNaN(value))
		{
			checkBox.setEnabled(true);
			problemUnlocked = true;
			tip = tip.replace("</body></html>", "<br>Uncheck the box to remove it.</body></html>");
		}
		else if (problemUnlocked && (tip == null || !checkBox.isSelected()))
		{
			checkBox.setEnabled(!frozen && enabled);
			problemUnlocked = false;
		}
		problemTip = tip;
		attLabel.setForeground(tip != null ? red : javax.swing.UIManager.getColor("Label.foreground"));
		sectionLabel.setForeground(tip != null ? red : javax.swing.UIManager.getColor("Label.foreground"));
		for (javax.swing.JComponent c : new javax.swing.JComponent[] { this, attLabel, sectionLabel, getSlider() })
			c.setToolTipText(tip);
		if (checkBox != null)
			checkBox.setToolTipText(tip);
		showText();
	}

	// the warning setProblem shows, and why a typed value isn't used: the box shows
	// the second while there is one
	private String	problemTip	= null;
	private String	refusedText	= null;

	// the limits on values typed or dragged (NaN for none), and whether 0 is
	// allowed below the lowest (it means none, as for a steps length)
	private double	lowest		= Double.NaN;
	private double	highest		= Double.NaN;
	private boolean	zeroAllowed	= false;
	// what else makes a value unusable, null for nothing
	private java.util.function.DoubleFunction<String>	validator	= null;

	/**
	 * Values outside these aren't used: the box turns red and says why, and the
	 * value stays as it was. The slider's own range is only what it can be dragged
	 * over, and grows to show a value read from a file.
	 *
	 * @param zeroAllowed whether 0 is allowed below lowest
	 */
	public void setLimits(double lowest, double highest, boolean zeroAllowed)
	{
		this.lowest = lowest;
		this.highest = highest;
		this.zeroAllowed = zeroAllowed;
	}

	/** @param validator says why a value can't be used, or null when it can */
	public void setValidator(java.util.function.DoubleFunction<String> validator)
	{
		this.validator = validator;
	}

	/** @return why the value can't be used, null when it can */
	private String refused(double v)
	{
		boolean belowLowest = !Double.isNaN(lowest) && v < lowest && !(zeroAllowed && v == 0);
		boolean aboveHighest = !Double.isNaN(highest) && v > highest;
		if (belowLowest || aboveHighest)
		{
			String range = Double.isNaN(highest) ? "at least " + nf.format(lowest)
				: Double.isNaN(lowest) ? "at most " + nf.format(highest)
				: "between " + nf.format(lowest) + " and " + nf.format(highest);
			return (zeroAllowed ? "it must be 0 (none) or " : "it must be ") + range;
		}
		return validator == null ? null : validator.apply(v);
	}

	private void showRefused(String why)
	{
		if (java.util.Objects.equals(why, refusedText))
			return;
		refusedText = why;
		showText();
	}

	// what the file has for the value when the game reads it as another number
	// ("*2.5" read as 0), shown in red instead of the value while it is the one
	// read (see setFileText)
	private String	fileText	= null;
	private double	fileValue	= Double.NaN;
	private String	fileTip		= null;

	/**
	 * Shows the text the file has instead of the value, in red with the message in
	 * its tool tip, while the value is the one read from it: typing or dragging a
	 * value replaces it.
	 *
	 * @param text the file's text, null for none
	 * @param readValue the value the game reads it as
	 * @param message what is wrong with it
	 */
	public void setFileText(String text, double readValue, String message)
	{
		boolean shown = showsFileText();
		fileText = text;
		fileValue = readValue;
		fileTip = message;
		if (shown || showsFileText())
		{
			setTextQuietly(valueText());
			getTextField().setCaretPosition(0);
		}
		showText();
	}

	private boolean showsFileText()
	{
		return fileText != null && Double.compare(value, fileValue) == 0;
	}

	/** @return the text the box shows for the value */
	private String valueText()
	{
		return showsFileText() ? fileText : Double.isNaN(value) ? "" : nf.format(value);
	}

	/** Shows the text in red, with why in its tool tip, when it is refused or has a warning */
	private void showText()
	{
		java.awt.Color red = java.awt.Color.RED;
		String fileTipText = showsFileText() ? "<html><body style='width: 300px'><font color=red>"
			+ fileTip.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
			+ "</font><br>Type a value to replace it.</body></html>" : null;
		String tip = refusedText != null ? "<html><body style='width: 300px'><font color=red><b>Not used:</b> "
			+ refusedText + ".<br>The previous value is restored when you leave the box.</font></body></html>" : problemTip != null ? problemTip : fileTipText;
		getTextField().setDisabledTextColor(problemTip != null || fileTipText != null ? red : javax.swing.UIManager.getColor("TextField.inactiveForeground"));
		getTextField().setForeground(tip != null ? red : javax.swing.UIManager.getColor("TextField.foreground"));
		getTextField().setToolTipText(tip);
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
			setTextQuietly("");
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
		// a new value to show: what was refused before no longer applies
		showRefused(null);
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
			setTextQuietly("");
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
			setTextQuietly(valueText());
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
			setTextQuietly("");
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
			setTextQuietly(valueText());
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
		double before = value;
		value = val;
		setSliderValue(value);
		if (fileText != null && Double.compare(value, fileValue) != 0)
		{
			// a new value (dragged): the text the file had is no longer what it has
			fileText = null;
			showText();
			setTextQuietly(nf.format(value));
			getTextField().setCaretPosition(0);
			if (Double.compare(before, value) != 0)
				valueChanged();
			return;
		}
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
			setTextQuietly(nf.format(value));
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
					setTextQuietly(nf.format(value));
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
					setTextQuietly("");
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
						// dragged past a limit: not used, and the box says why
						String refused = refused(tmp1);
						showRefused(refused);
						if (refused == null)
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
