/*
 *   SegmentEditorDlg.java
 *   Created on 28 ??? 2005
 *
 *    The SegmentEditorDlg.java is part of TrackEditor-0.3.1.
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

import gui.EditorFrame;
import gui.view.CircuitView;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import utils.SurfaceComboBox;
import utils.circuit.Curve;
import utils.circuit.MainTrack;
import utils.circuit.Segment;
import utils.circuit.Surface;
import bsh.EvalError;
import bsh.Interpreter;

/**
 * @author babis
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SegmentEditorDlg extends JDialog implements SliderListener
{
	private Segment					shape;
	CircuitView						view;
	EditorFrame						editorFrame;
	public boolean					dirty						= false;

	private JPanel					jContentPane				= null;	//  @jve:decl-index=0:visual-constraint="377,10"

	private JTabbedPane				jTabbedPane					= null;
	private JPanel					centerPanel					= null;	//  @jve:decl-index=0:visual-constraint="394,34"
	private SegmentSlider			radiusStartSlider			= null;
	private SegmentSlider			radiusEndSlider				= null;
	private SegmentSlider			arcSlider					= null;
	private SegmentSlider			lengthSlider				= null;
	private JLabel					nameLabel					= null;
	private JTextField				nameTextField				= null;
	private JLabel					surfaceLabel				= null;
	private SurfaceComboBox			surfaceComboBox				= null;

	private SegmentSlider			gradeSlider					= null;
	private SegmentSlider			startTangentSlider			= null;
	private SegmentSlider			startTangentLeftSlider		= null;
	private SegmentSlider			startTangentRightSlider		= null;
	private SegmentSlider			endTangentSlider			= null;
	private SegmentSlider			endTangentLeftSlider		= null;
	private SegmentSlider			endTangentRightSlider		= null;
	private SegmentSlider			bankingStartSlider			= null;
	private SegmentSlider			bankingEndSlider			= null;
	private SegmentSlider			heightStartSlider			= null;
	private SegmentSlider			heightStartLeftSlider		= null;
	private SegmentSlider			heightStartRightSlider		= null;
	private SegmentSlider			heightEndSlider				= null;
	private SegmentSlider			heightEndLeftSlider			= null;
	private SegmentSlider			heightEndRightSlider		= null;
	private SegmentSlider			profilStepsSlider			= null;
	private SegmentSlider			profilStepsLengthSlider		= null;

	private SegmentSideProperties	rightPanel					= null;
	private SegmentSideProperties	leftPanel					= null;

	private GroupButton				groupButton					= null;
	private ProfileButton			profileButton				= null;

	private JLabel					marksLabel					= null;
	private JTextField				marksTextField				= null;
	private JLabel					commentLabel				= null;
	private JTextField				commentTextField			= null;
	private JLabel					penaltyLabel				= null;
	private JTextField				penaltyTextField			= null;
	private JLabel					envMapIndexLabel			= null;
	private JTextField				envMapIndexTextField		= null;
	private JLabel					dovFactorLabel				= null;
	private JTextField				dovFactorTextField			= null;

	private String[]				roadSurfaceItems			=
	{"asphalt-lines", "asphalt-l-left", "asphalt-l-right",
     "asphalt-l-both", "asphalt-pits", "asphalt", "dirt", "dirt-b", "asphalt2", "road1", "road1-pits",
     "road1-asphalt", "asphalt-road1", "b-road1", "b-road1-l2", "b-road1-l2p", "concrete", "concrete2",
     "concrete3", "b-asphalt", "b-asphalt-l1", "b-asphalt-l1p", "asphalt2-lines", "asphalt2-l-left",
     "asphalt2-l-right", "asphalt2-l-both", "grass", "grass3", "grass5", "grass6", "grass7", "gravel", "sand3",
     "sand", "curb-5cm-r", "curb-5cm-l", "curb-l", "tar-grass3-l", "tar-grass3-r", "tar-sand", "b-road1-grass6",
     "b-road1-grass6-l2", "b-road1-gravel-l2", "b-road1-sand3", "b-road1-sand3-l2", "b-asphalt-grass7",
     "b-asphalt-grass7-l1", "b-asphalt-grass6", "b-asphalt-grass6-l1", "b-asphalt-sand3", "b-asphalt-sand3-l1",
     "barrier", "barrier2", "barrier-turn", "barrier-grille", "wall", "wall2", "tire-wall"};
	private Vector<String>			roadSurfaceVector			= new Vector<String>();

	/**
	 *
	 */
	public SegmentEditorDlg()
	{
		super((Frame) null, "", true);
	}

	public SegmentEditorDlg(CircuitView view, EditorFrame editorFrame, String title, boolean modal, Segment shape)
	{
		super(editorFrame, title, modal);

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		try
		{
			this.view = view;
			this.editorFrame = editorFrame;

			// add new surfaces from Surfaces
			addDefaultSurfaces(roadSurfaceVector, roadSurfaceItems);
			
			setShape(shape);

			initialize();
			//			pack();
			this.setVisible(true);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void refresh()
	{
		roadSurfaceVector.clear();
		addDefaultSurfaces(roadSurfaceVector, roadSurfaceItems);
		centerPanel.remove(surfaceComboBox);
		surfaceComboBox = null;
		centerPanel.add(getSurfaceComboBox(), null);

		rightPanel.refresh();
		leftPanel.refresh();
	}

	private void addDefaultSurfaces(Vector<String> surfaceVector, String[] fallback)
	{
		// use the default surfaces if available
		Vector<Surface> defaultSurfaces = editorFrame.getDefaultSurfaces();

		if (defaultSurfaces.isEmpty())
		{
			// add the ones found
			surfaceVector.addAll(Arrays.asList(fallback));
		}
		else
		{
			// add the ones that should be there
	        for (int i = 0; i < defaultSurfaces.size(); i++)
	        {
	        	surfaceVector.add(defaultSurfaces.get(i).getName());
	        }
		}

        Vector<Surface> surfaces = editorFrame.getTrackData().getSurfaces();
        for (int i = 0; i < surfaces.size(); i++)
        {
			String surface = surfaces.elementAt(i).getName();
			if (surface != null)
			{
				boolean found = false;
				for (int j = 0; j < surfaceVector.size(); j++)
				{
					if (surfaceVector.elementAt(i).equals(surfaces.elementAt(i).getName()))
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					surfaceVector.add(surface);
				}
			}
        }
		Collections.sort(surfaceVector);
	}

	private void addSurface(Vector<String> surfaceVector, String surface)
	{
		// add this surface if it's not found in default list
		if (surface != null)
		{
			boolean found = false;
			for (int i = 0; i < surfaceVector.size(); i++)
			{
				if (surfaceVector.elementAt(i).equals(surface))
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				surfaceVector.add(surface);
				Collections.sort(surfaceVector);
			}
		}
	}

	/**
	 *
	 */
	private void initialize()
	{
		this.setSize(1180, 536);
		Point p = editorFrame.getLocation();
		p.x = editorFrame.getProject().getSegmentEditorX();
		p.y = editorFrame.getProject().getSegmentEditorY();
		this.setLocation(p);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane()
	{
		if (jContentPane == null)
		{
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJTabbedPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTabbedPane
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane()
	{
		if (jTabbedPane == null)
		{
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("Left", null, getLeftPanel(), null);
			jTabbedPane.addTab("Center", null, getCenterPanel(), null);
			jTabbedPane.addTab("Right", null, getRightPanel(), null);
			jTabbedPane.setSelectedIndex(1);
		}
		return jTabbedPane;
	}
	/**
	 * This method initializes centerPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getCenterPanel()
	{
		if (centerPanel == null)
		{
			nameLabel = new JLabel();
			surfaceLabel = new JLabel();
			marksLabel = new JLabel();
			commentLabel = new JLabel();
			centerPanel = new JPanel();
			centerPanel.setLayout(null);
			nameLabel.setBounds(10, 5, 55, 23);
			nameLabel.setText("Name");
			surfaceLabel.setBounds(10, 33, 55, 23);
			surfaceLabel.setText("Surface");
	        marksLabel.setText("Marks");
	        marksLabel.setBounds(440, 5, 60, 23);
	        commentLabel.setText("Comment");
	        commentLabel.setBounds(440, 33, 60, 23);
	        centerPanel.add(nameLabel, null);
	        centerPanel.add(surfaceLabel, null);
			centerPanel.add(marksLabel, null);
			centerPanel.add(commentLabel, null);
			penaltyLabel = new JLabel("Penalty");
			penaltyLabel.setBounds(820, 5, 70, 23);
			penaltyLabel.setToolTipText("CSR 'penalty' (empty = not set)");
			if (editorFrame.getCarsSportsRacing())
				centerPanel.add(penaltyLabel, null);
			envMapIndexLabel = new JLabel("Env Map");
			envMapIndexLabel.setBounds(950, 5, 60, 23);
			envMapIndexLabel.setToolTipText("'env map index' (empty = not set)");
			centerPanel.add(envMapIndexLabel, null);
			dovFactorLabel = new JLabel("DoV Factor");
			dovFactorLabel.setBounds(820, 33, 70, 23);
			dovFactorLabel.setToolTipText("'DoV factor' (empty = not set)");
			centerPanel.add(dovFactorLabel, null);
			centerPanel.add(getNameTextField(), null);
			centerPanel.add(getMarksTextField(), null);
			centerPanel.add(getCommentTextField(), null);
			if (editorFrame.getCarsSportsRacing())
				centerPanel.add(getPenaltyTextField(), null);
			centerPanel.add(getEnvMapIndexTextField(), null);
			centerPanel.add(getDovFactorTextField(), null);
			centerPanel.add(getSurfaceComboBox(), null);
			centerPanel.add(getRadiusStartSlider(), null);
			centerPanel.add(getRadiusEndSlider(), null);
			centerPanel.add(getArcSlider(), null);
			centerPanel.add(getLengthSlider(), null);
			centerPanel.add(getGradeSlider(), null);
			centerPanel.add(getStartTangentSlider(), null);
			centerPanel.add(getStartTangentLeftSlider(), null);
			centerPanel.add(getStartTangentRightSlider(), null);
			centerPanel.add(getBankingStartSlider(), null);
			centerPanel.add(getBankingEndSlider(), null);
			centerPanel.add(getEndTangentSlider(), null);
			centerPanel.add(getEndTangentLeftSlider(), null);
			centerPanel.add(getEndTangentRightSlider(), null);
			centerPanel.add(getHeightStartSlider(), null);
			centerPanel.add(getHeightStartLeftSlider(), null);
			centerPanel.add(getHeightStartRightSlider(), null);
			centerPanel.add(getHeightEndSlider(), null);
			centerPanel.add(getHeightEndLeftSlider(), null);
			centerPanel.add(getHeightEndRightSlider(), null);
			centerPanel.add(getGroupButton(), null);
			centerPanel.add(getProfileButton(), null);
			centerPanel.add(getProfileStepsSlider(), null);
			centerPanel.add(getProfileStepsLengthSlider(), null);
		}
		return centerPanel;
	}
	/**
	 * This method initializes radiusStartSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getRadiusStartSlider()
	{
		if (radiusStartSlider == null)
		{
			radiusStartSlider = new SegmentSlider(1, 1000, Double.NaN, 0.001);
			radiusStartSlider.setBounds(115, 64, 50, 390);
			radiusStartSlider.setSection("Radius");
			radiusStartSlider.setAttr("Start");
			if (!shape.getType().equals("str"))
			{
				radiusStartSlider.setMethod("RadiusStart");
			}
			radiusStartSlider.addSliderListener(this);
		}
		return radiusStartSlider;
	}
	/**
	 * This method initializes radiusEndSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getRadiusEndSlider()
	{
		if (radiusEndSlider == null)
		{
			radiusEndSlider = new SegmentSlider(1, 1000, Double.NaN, 0.001);
			radiusEndSlider.setBounds(170, 64, 50, 390);
			radiusEndSlider.setSection("Radius");
			radiusEndSlider.setAttr("End");
			if (!shape.getType().equals("str"))
			{
				radiusEndSlider.setMethod("RadiusEnd");
			}
			radiusEndSlider.addSliderListener(this);
		}
		return radiusEndSlider;
	}
	/**
	 * This method initializes arcSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getArcSlider()
	{
		if (arcSlider == null)
		{
			arcSlider = new SegmentSlider(1, 360, Double.NaN, 0.001);
			arcSlider.setBounds(60, 64, 50, 390);
			arcSlider.setSection("Arc");
			arcSlider.setAttr("");
			if (!shape.getType().equals("str"))
			{
				arcSlider.setMethod("ArcDeg");
			}
			arcSlider.addSliderListener(this);
		}
		return arcSlider;
	}
	/**
	 * This method initializes lengthSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getLengthSlider()
	{
		if (lengthSlider == null)
		{
			lengthSlider = new SegmentSlider(0.001, 1000, 0.001, 0.001, shape.getLength(), "Length", "", "Length", false, false);
			lengthSlider.setBounds(5, 64, 50, 390);
			lengthSlider.addSliderListener(this);
		}
		return lengthSlider;
	}
	/**
	 * This method initializes gradeSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getGradeSlider()
	{
		if (gradeSlider == null)
		{
			gradeSlider = new SegmentSlider(-45, 45, 0, 0.001, shape.getGrade(), "Grade", "", "Grade", true, false);
			gradeSlider.setBounds(225, 64, 50, 390);
			gradeSlider.addSliderListener(this);
		}
		return gradeSlider;
	}
	/**
	 * This method initializes startTangentSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getStartTangentSlider()
	{
		if (startTangentSlider == null)
		{
			startTangentSlider = new SegmentSlider(-45, 45, 0, 0.001, shape.getProfilStartTangent(), "Tangent", "Start", "ProfilStartTangent", true, false);
			startTangentSlider.setBounds(280, 64, 50, 390);
			startTangentSlider.addSliderListener(this);
		}
		return startTangentSlider;
	}
	/**
	 * This method initializes startTangentLeftSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getStartTangentLeftSlider()
	{
		if (startTangentLeftSlider == null)
		{
			startTangentLeftSlider = new SegmentSlider(-45, 45, 0, 0.001, shape.getProfilStartTangentLeft(), "L Tan", "Start", "ProfilStartTangentLeft", true, false);
			startTangentLeftSlider.setBounds(335, 64, 50, 390);
			startTangentLeftSlider.addSliderListener(this);
		}
		return startTangentLeftSlider;
	}
	/**
	 * This method initializes startTangentRightSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getStartTangentRightSlider()
	{
		if (startTangentRightSlider == null)
		{
			startTangentRightSlider = new SegmentSlider(-45, 45, 0, 0.001, shape.getProfilStartTangentRight(), "R Tan", "Start", "ProfilStartTangentRight", true, false);
			startTangentRightSlider.setBounds(390, 64, 50, 390);
			startTangentRightSlider.addSliderListener(this);
		}
		return startTangentRightSlider;
	}
	/**
	 * This method initializes endTangentSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getEndTangentSlider()
	{
		if (endTangentSlider == null)
		{
			endTangentSlider = new SegmentSlider(-45, 45, 0, 0.001, shape.getProfilEndTangent(), "Tangent", "End", "ProfilEndTangent", true, false);
			endTangentSlider.setBounds(445, 64, 50, 390);
			endTangentSlider.addSliderListener(this);
		}
		return endTangentSlider;
	}
	/**
	 * This method initializes endTangentSLeftlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getEndTangentLeftSlider()
	{
		if (endTangentLeftSlider == null)
		{
			endTangentLeftSlider = new SegmentSlider(-45, 45, 0, 0.001, shape.getProfilEndTangentLeft(), "L Tan", "End", "ProfilEndTangentLeft", true, false);
			endTangentLeftSlider.setBounds(500, 64, 50, 390);
			endTangentLeftSlider.addSliderListener(this);
		}
		return endTangentLeftSlider;
	}
	/**
	 * This method initializes endTangentRightSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getEndTangentRightSlider()
	{
		if (endTangentRightSlider == null)
		{
			endTangentRightSlider = new SegmentSlider(-45, 45, 0, 0.001, shape.getProfilEndTangentRight(), "R Tan", "End", "ProfilEndTangentRight", true, false);
			endTangentRightSlider.setBounds(555, 64, 50, 390);
			endTangentRightSlider.addSliderListener(this);
		}
		return endTangentRightSlider;
	}
	/**
	 * This method initializes profilStepsSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getProfileStepsSlider()
	{
		if (profilStepsSlider == null)
		{
			int minSteps = 1;
			if (!shape.getType().equals("str"))
				minSteps = 2;
			profilStepsSlider = new SegmentSlider(minSteps, 100, Double.NaN, 1, shape.getProfilSteps(), "Steps", "", "ProfilSteps", true, true);
			profilStepsSlider.setBounds(610, 64, 50, 390);
			profilStepsSlider.addSliderListener(this);
		}
		return profilStepsSlider;
	}
	/**
	 * This method initializes profilStepsLengthSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getProfileStepsLengthSlider()
	{
		if (profilStepsLengthSlider == null)
		{
			profilStepsLengthSlider = new SegmentSlider(0, 100, 0, 0.001, shape.getProfilStepsLength(), "Steps", "Len", "ProfilStepsLength", true, false);
			profilStepsLengthSlider.setBounds(665, 64, 50, 390);
			profilStepsLengthSlider.addSliderListener(this);
		}
		return profilStepsLengthSlider;
	}
	/**
	 * This method initializes bankingStartSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getBankingStartSlider()
	{
		if (bankingStartSlider == null)
		{
			bankingStartSlider = new SegmentSlider(-45, 45, 0, 0.001, shape.getBankingStart(), "Banking", "Start", "BankingStart", true, false);
			bankingStartSlider.setBounds(720, 64, 50, 390);
			bankingStartSlider.addSliderListener(this);
		}
		return bankingStartSlider;
	}
	/**
	 * This method initializes bankingEndSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getBankingEndSlider()
	{
		if (bankingEndSlider == null)
		{
			bankingEndSlider = new SegmentSlider(-45, 45, 0, 0.001, shape.getBankingEnd(), "Banking", "End", "BankingEnd", true, false);
			bankingEndSlider.setBounds(775, 64, 50, 390);
			bankingEndSlider.addSliderListener(this);
		}
		return bankingEndSlider;
	}
	/**
	 * This method initializes heightStartSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getHeightStartSlider()
	{
		if (heightStartSlider == null)
		{
			heightStartSlider = new SegmentSlider(-200, 650, 0, 0.001, shape.getHeightStart(), "Height", "Start", "HeightStart", true, false);
			heightStartSlider.setBounds(830, 64, 50, 390);
			heightStartSlider.addSliderListener(this);
		}
		return heightStartSlider;
	}
	/**
	 * This method initializes heightStartLeftSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getHeightStartLeftSlider()
	{
		if (heightStartLeftSlider == null)
		{
			heightStartLeftSlider = new SegmentSlider(-200, 650, 0, 0.001, shape.getHeightStartLeft(), "L Height", "Start", "HeightStartLeft", true, false);
			heightStartLeftSlider.setBounds(885, 64, 50, 390);
			heightStartLeftSlider.addSliderListener(this);
		}
		return heightStartLeftSlider;
	}
	/**
	 * This method initializes heightStartRightSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getHeightStartRightSlider()
	{
		if (heightStartRightSlider == null)
		{
			heightStartRightSlider = new SegmentSlider(-200, 650, 0, 0.001, shape.getHeightStartRight(), "R Height", "Start", "HeightStartRight", true, false);
			heightStartRightSlider.setBounds(940, 64, 50, 390);
			heightStartRightSlider.addSliderListener(this);
		}
		return heightStartRightSlider;
	}
	/**
	 * This method initializes heightEndSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getHeightEndSlider()
	{
		if (heightEndSlider == null)
		{
			heightEndSlider = new SegmentSlider(-200, 650, 0, 0.001, shape.getHeightEnd(), "Height", "End", "HeightEnd", true, false);
			heightEndSlider.setBounds(995, 64, 50, 390);
			heightEndSlider.addSliderListener(this);
		}
		return heightEndSlider;
	}
	/**
	 * This method initializes heightEndLeftSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getHeightEndLeftSlider()
	{
		if (heightEndLeftSlider == null)
		{
			heightEndLeftSlider = new SegmentSlider(-200, 650, 0, 0.001, shape.getHeightEndLeft(), "L Height", "End", "HeightEndLeft", true, false);
			heightEndLeftSlider.setBounds(1050, 64, 50, 390);
			heightEndLeftSlider.addSliderListener(this);
		}
		return heightEndLeftSlider;
	}
	/**
	 * This method initializes heightEndRightSlider
	 *
	 * @return gui.SegmentSlider
	 */
	private SegmentSlider getHeightEndRightSlider()
	{
		if (heightEndRightSlider == null)
		{
			heightEndRightSlider = new SegmentSlider(-200, 650, 0, 0.001, shape.getHeightEndRight(), "R Height", "End", "HeightEndRight", true, false);
			heightEndRightSlider.setBounds(1105, 64, 50, 390);
			heightEndRightSlider.addSliderListener(this);
		}
		return heightEndRightSlider;
	}

	/**
	 * This method initializes nameTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getNameTextField()
	{
		if (nameTextField == null)
		{
			nameTextField = new JTextField();
			nameTextField.setBounds(75, 5, 180, 23);
			nameTextField.setText(shape.getName());
			nameTextField.getDocument().addDocumentListener(new DocumentListener()
			{
				public void removeUpdate(DocumentEvent e)
				{
					shape.setName(nameTextField.getText());
					updateTitle();
				}
				public void insertUpdate(DocumentEvent e)
				{
					shape.setName(nameTextField.getText());
					updateTitle();
				}
				public void changedUpdate(DocumentEvent e)
				{
				}
			});
		}
		return nameTextField;
	}
	/**
	 * This method initializes marksTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getMarksTextField()
	{
		if (marksTextField == null)
		{
			marksTextField = new JTextField();
			marksTextField.setBounds(510, 5, 300, 23);
			marksTextField.addKeyListener(new KeyAdapter()
			{
				public void keyReleased(KeyEvent e)
				{
					if (!shape.getType().equals("str"))
					{
						((Curve)shape).setMarks(marksTextField.getText());
					}
				}
			});
		}
		return marksTextField;
	}
	/**
	 * This method initializes commentTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getCommentTextField()
	{
		if (commentTextField == null)
		{
			commentTextField = new JTextField();
			commentTextField.setBounds(510, 33, 300, 23);
			commentTextField.addKeyListener(new KeyAdapter()
			{
				public void keyReleased(KeyEvent e)
				{
					shape.setComment(commentTextField.getText());
				}
			});
		}
		return commentTextField;
	}

	/**
	 * Text for an optional int: empty when not set.
	 */
	private static String intText(int value)
	{
		return value == Integer.MAX_VALUE ? "" : String.valueOf(value);
	}

	/**
	 * Text for an optional double: empty when not set.
	 */
	private static String doubleText(double value)
	{
		return Double.isNaN(value) ? "" : String.valueOf(value);
	}

	private JTextField getPenaltyTextField()
	{
		if (penaltyTextField == null)
		{
			penaltyTextField = new JTextField();
			penaltyTextField.setBounds(890, 5, 50, 23);
			penaltyTextField.addKeyListener(new KeyAdapter()
			{
				public void keyReleased(KeyEvent e)
				{
					String text = penaltyTextField.getText().trim();
					try
					{
						int value = text.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(text);
						if (value != shape.getPenalty())
						{
							shape.setPenalty(value);
							editorFrame.documentIsModified = true;
						}
					}
					catch (NumberFormatException ex)
					{
						// keep the last valid value while the user is typing
					}
				}
			});
		}
		return penaltyTextField;
	}

	private JTextField getEnvMapIndexTextField()
	{
		if (envMapIndexTextField == null)
		{
			envMapIndexTextField = new JTextField();
			envMapIndexTextField.setBounds(1010, 5, 50, 23);
			envMapIndexTextField.addKeyListener(new KeyAdapter()
			{
				public void keyReleased(KeyEvent e)
				{
					String text = envMapIndexTextField.getText().trim();
					try
					{
						int value = text.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(text);
						if (value != shape.getEnvMapIndex())
						{
							shape.setEnvMapIndex(value);
							editorFrame.documentIsModified = true;
						}
					}
					catch (NumberFormatException ex)
					{
						// keep the last valid value while the user is typing
					}
				}
			});
		}
		return envMapIndexTextField;
	}

	private JTextField getDovFactorTextField()
	{
		if (dovFactorTextField == null)
		{
			dovFactorTextField = new JTextField();
			dovFactorTextField.setBounds(890, 33, 50, 23);
			dovFactorTextField.addKeyListener(new KeyAdapter()
			{
				public void keyReleased(KeyEvent e)
				{
					String text = dovFactorTextField.getText().trim();
					try
					{
						double value = text.isEmpty() ? Double.NaN : Double.parseDouble(text);
						double old = shape.getDovFactor();
						if (!(value == old || (Double.isNaN(value) && Double.isNaN(old))))
						{
							shape.setDovFactor(value);
							editorFrame.documentIsModified = true;
						}
					}
					catch (NumberFormatException ex)
					{
						// keep the last valid value while the user is typing
					}
				}
			});
		}
		return dovFactorTextField;
	}
	/**
	 * This method initializes surfaceComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getSurfaceComboBox()
	{
		if (surfaceComboBox == null)
		{
			surfaceComboBox = new SurfaceComboBox(editorFrame, roadSurfaceVector);
			surfaceComboBox.setBounds(75, 33, 200, 23);
			surfaceComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					shape.setSurface((String) surfaceComboBox.getSelectedItem());
					try
					{
						view.redrawCircuit();
					} catch (Exception e1)
					{
						e1.printStackTrace();
					}
					editorFrame.documentIsModified = true;
				}
			});

		}
		return surfaceComboBox;
	}

	public void updateTitle()
	{
		this.setTitle("Segment " + shape.getCount() + " : " + shape.getName());
	}

	public void setShape(Segment shape)
	{
		this.shape = shape;
		addSurface(roadSurfaceVector, shape.getSurface());
		this.getRightPanel().setSide(shape, shape.getRight());
		this.getLeftPanel().setSide(shape, shape.getLeft());

		// update all fields

		try
		{
			if (!shape.getType().equals("str"))
			{
				Curve curve = (Curve)shape;
				this.getRadiusStartSlider().setEnabled(true);
				this.getRadiusEndSlider().setEnabled(true);
				this.getArcSlider().setEnabled(true);
				this.getLengthSlider().setEnabled(false);
				this.getLengthSlider().setValue(curve.getLength());
				getGroupButton().setEnabled(true);
				getGroupButton().setSelected(curve.getType());

				this.getArcSlider().setValue(curve.getArcDeg());
				this.getRadiusStartSlider().setValue(curve.getRadiusStart());
				this.getRadiusEndSlider().setValue(curve.getRadiusEnd());
				this.getMarksTextField().setEnabled(true);
				this.getMarksTextField().setText(curve.getMarks());

			} else
			{
				this.getRadiusStartSlider().setEnabled(false);
				this.getRadiusEndSlider().setEnabled(false);
				this.getArcSlider().setEnabled(false);
				this.getLengthSlider().setEnabled(true);
				getGroupButton().setEnabled(false);

				this.getLengthSlider().setValue(shape.getLength());
				this.getMarksTextField().setEnabled(false);
				this.getMarksTextField().setText("");
			}
			getNameTextField().setText(shape.getName());
			getCommentTextField().setText(shape.getComment());
			getPenaltyTextField().setText(intText(shape.getPenalty()));
			getEnvMapIndexTextField().setText(intText(shape.getEnvMapIndex()));
			getDovFactorTextField().setText(doubleText(shape.getDovFactor()));

			this.updateTitle();

			// add this surface if it's not found in default list
			String surface = shape.getSurface();
			if (surface != null)
			{
				boolean found = false;
				for (int i = 0; i < roadSurfaceVector.size(); i++)
				{
					if (roadSurfaceVector.elementAt(i).equals(surface))
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					roadSurfaceVector.add(surface);
					Collections.sort(roadSurfaceVector);
				}
			}

			if (shape.getSurface() != null)
				getSurfaceComboBox().setSelectedItem(shape.getSurface());
			else
				getSurfaceComboBox().setSelectedIndex(-1);
			
			this.getGradeSlider().setValue(shape.getGrade());
			this.getStartTangentSlider().setValue(shape.getProfilStartTangent());
			this.getStartTangentLeftSlider().setValue(shape.getProfilStartTangentLeft());
			this.getStartTangentRightSlider().setValue(shape.getProfilStartTangentRight());
			this.getEndTangentSlider().setValue(shape.getProfilEndTangent());
			this.getEndTangentLeftSlider().setValue(shape.getProfilEndTangentLeft());
			this.getEndTangentRightSlider().setValue(shape.getProfilEndTangentRight());
			this.getBankingStartSlider().setValue(shape.getBankingStart());
			this.getBankingEndSlider().setValue(shape.getBankingEnd());
			this.getHeightStartSlider().setValue(shape.getHeightStart());
			this.getHeightStartLeftSlider().setValue(shape.getHeightStartLeft());
			this.getHeightStartRightSlider().setValue(shape.getHeightStartRight());
			this.getHeightEndSlider().setValue(shape.getHeightEnd());
			this.getHeightEndLeftSlider().setValue(shape.getHeightEndLeft());
			this.getHeightEndRightSlider().setValue(shape.getHeightEndRight());
			this.getProfileStepsSlider().setValue(shape.getProfilSteps());
			this.getProfileStepsLengthSlider().setValue(shape.getProfilStepsLength());
			if (shape.getProfil() == null || shape.getProfil().isEmpty())
			{
				getProfileButton().setSelected("none");
			}
			else
			{
				getProfileButton().setSelected(shape.getProfil());
			}
			if (shape.getValidProfil(editorFrame).equals("linear"))
			{
				this.getStartTangentSlider().setEnabled(false);
				this.getStartTangentLeftSlider().setEnabled(false);
				this.getStartTangentRightSlider().setEnabled(false);
				this.getEndTangentSlider().setEnabled(false);
				this.getEndTangentLeftSlider().setEnabled(false);
				this.getEndTangentRightSlider().setEnabled(false);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		this.validate();
		this.repaint();
	}

	public void update()
	{
		try
		{
			view.redrawCircuit();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		editorFrame.documentIsModified = true;
		dirty = true;
	}
	/**
	 * This method initializes rightPanel
	 *
	 * @return gui.segment.SegmentSideProperties
	 */
	private SegmentSideProperties getRightPanel()
	{
		if (rightPanel == null)
		{
			rightPanel = new SegmentSideProperties(this, shape, shape.getRight());
			rightPanel.setTitle("Right");
		}
		return rightPanel;
	}
	/**
	 * This method initializes leftPanel
	 *
	 * @return gui.segment.SegmentSideProperties
	 */
	private SegmentSideProperties getLeftPanel()
	{
		if (leftPanel == null)
		{
			leftPanel = new SegmentSideProperties(this, shape, shape.getLeft());
			leftPanel.setTitle("Left");
		}
		return leftPanel;
	}

	/**
	 *
	 */
	public void sideChanged()
	{
		view.redrawCircuit();
		editorFrame.documentIsModified = true;
		dirty = true;
	}
	/**
	 * This method initializes groupButton
	 *
	 * @return gui.segment.GroupButton
	 */
	private GroupButton getGroupButton()
	{
		if (groupButton == null)
		{
			groupButton = new GroupButton();
			groupButton.setBounds(270, 2, 70, 33);
			groupButton.setParent(this);
		}
		return groupButton;
	}

	public void groupChanged()
	{
		shape.setType(getGroupButton().getSelected());
		editorFrame.documentIsModified = true;
		view.redrawCircuit();
	}

	/**
	 * This method initializes profileButton
	 *
	 * @return gui.segment.ProfileButton
	 */
	private ProfileButton getProfileButton()
	{
		if (profileButton == null)
		{
			profileButton = new ProfileButton();
			profileButton.setBounds(350, 2, 70, 49);
			profileButton.setParent(this);
		}
		return profileButton;
	}

	public void profileChanged()
	{
		if (getProfileButton().getSelected().isEmpty() || getProfileButton().getSelected() == "none")
		{
			shape.setProfil("");
		}
		else
		{
			shape.setProfil(getProfileButton().getSelected());
		}
		
		if (shape.getValidProfil(editorFrame).equals("linear"))
		{
			shape.setProfilStartTangent(Double.NaN);
			shape.setProfilEndTangent(Double.NaN);
			shape.setProfilEndTangentLeft(Double.NaN);
			shape.setProfilEndTangentRight(Double.NaN);
			this.getStartTangentSlider().setEnabled(false);
			this.getStartTangentLeftSlider().setEnabled(false);
			this.getStartTangentRightSlider().setEnabled(false);
			this.getEndTangentSlider().setEnabled(false);
			this.getEndTangentLeftSlider().setEnabled(false);
			this.getEndTangentRightSlider().setEnabled(false);
		}
		else
		{
			this.getStartTangentSlider().setEnabled(true);
			this.getStartTangentLeftSlider().setEnabled(true);
			this.getStartTangentRightSlider().setEnabled(true);
			this.getEndTangentSlider().setEnabled(true);
			this.getEndTangentLeftSlider().setEnabled(true);
			this.getEndTangentRightSlider().setEnabled(true);
		}
		editorFrame.documentIsModified = true;
	}

	public void windowClosing(WindowEvent anEvent)
	{
		System.out.println("JDialog is closing");
	}


	/* (non-Javadoc)
	 * @see gui.segment.SliderListener#valueChanged(gui.segment.SegmentSlider)
	 */
	public void sliderChanged(SegmentSlider slider)
	{
		// TODO I don't know if this is the best way to fix this but it works
		if (slider.getMethod() == null || slider.getMethod().isEmpty())
			return;

		Interpreter line = new Interpreter();
		String command = "";

		try
		{
			line.set("shape", shape);

			String method = slider.getMethod();

			if (slider.getIntegerFormat())
			{
				if (slider.getValue() == Integer.MAX_VALUE)
					command = "shape.set" + method + "(Integer.MAX_VALUE)";
				else
					command = "shape.set" + method + "(" + (int) slider.getValue() + ")";
			}
			else
			{
				if (Double.isNaN(slider.getValue()))
					command = "shape.set" + method + "(Double.NaN)";
				else
					command = "shape.set" + method + "(" + slider.getValue() + ")";
			}

			line.eval(command);
			shape = (Segment) line.get("shape");
		} catch (EvalError e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double length = shape.getLength();
		view.redrawCircuit();
		double newLength = shape.getLength();
		if (length != newLength)
		{
			getLengthSlider().setValue(newLength);
		}
		editorFrame.documentIsModified = true;
		dirty = true;
	}

	public void checkBoxChanged(SegmentSlider slider)
	{
		if (slider.getSection().equals("Height") && slider.getAttr().equals("Start"))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setHeightStart(shape.getCalculatedHeightStart());
				slider.setValue(shape.getHeightStart());
				
				shape.setHeightStartLeft(Double.NaN);
				heightStartLeftSlider.setValue(shape.getHeightStartLeft());
				
				shape.setHeightStartRight(Double.NaN);
				heightStartRightSlider.setValue(shape.getHeightStartRight());
			}
		}
		else if (slider.getSection().equals("L Height") && slider.getAttr().equals("Start"))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setHeightStartLeft(shape.getCalculatedHeightStartLeft());
				slider.setValue(shape.getHeightStartLeft());
				
				shape.setHeightStart(Double.NaN);
				heightStartSlider.setValue(shape.getHeightStart());
			}
		}
		else if (slider.getSection().equals("R Height") && slider.getAttr().equals("Start"))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setHeightStartRight(shape.getCalculatedHeightStartRight());
				slider.setValue(shape.getHeightStartRight());

				shape.setHeightStart(Double.NaN);
				heightStartSlider.setValue(shape.getHeightStart());
			}
		}
		else if (slider.getSection().equals("Height") && slider.getAttr().equals("End"))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setHeightEnd(shape.getCalculatedHeightEnd());
				slider.setValue(shape.getHeightEnd());
				
				shape.setHeightEndLeft(Double.NaN);
				heightEndLeftSlider.setValue(shape.getHeightEndLeft());
				
				shape.setHeightEndRight(Double.NaN);
				heightEndRightSlider.setValue(shape.getHeightEndRight());
			}
		}
		else if (slider.getSection().equals("L Height") && slider.getAttr().equals("End"))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setHeightEndLeft(shape.getCalculatedHeightEndLeft());
				slider.setValue(shape.getHeightEndLeft());
				
				shape.setHeightEnd(Double.NaN);
				heightEndSlider.setValue(shape.getHeightEnd());
			}
		}
		else if (slider.getSection().equals("R Height") && slider.getAttr().equals("End"))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setHeightEndRight(shape.getCalculatedHeightEndRight());
				slider.setValue(shape.getHeightEndRight());
				
				shape.setHeightEnd(Double.NaN);
				heightEndSlider.setValue(shape.getHeightEnd());
			}
		}
		else if (slider.getSection().equals("Grade") && slider.getAttr().equals(""))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setGrade(shape.getCalculatedGrade());
				slider.setValue(shape.getGrade());
			}
		}
		else if (slider.getSection().equals("Banking") && slider.getAttr().equals("Start"))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setBankingStart(shape.getCalculatedBankingStart());
				slider.setValue(shape.getBankingStart());
			}
		}
		else if (slider.getSection().equals("Banking") && slider.getAttr().equals("End"))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setBankingEnd(shape.getCalculatedBankingEnd());
				slider.setValue(shape.getBankingEnd());
			}
		}
		else if (slider.getSection().equals("Tangent") && slider.getAttr().equals("Start"))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setProfilStartTangent(shape.getCalculatedStartTangent());
				slider.setValue(shape.getProfilStartTangent());
				
				shape.setProfilStartTangentLeft(Double.NaN);
				startTangentLeftSlider.setValue(shape.getProfilStartTangentLeft());
				
				shape.setProfilStartTangentRight(Double.NaN);
				startTangentRightSlider.setValue(shape.getProfilStartTangentRight());
			}
		}
		else if (slider.getSection().equals("L Tan") && slider.getAttr().equals("Start"))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setProfilStartTangentLeft(shape.getCalculatedStartTangentLeft());
				slider.setValue(shape.getProfilStartTangentLeft());
				
				shape.setProfilStartTangent(Double.NaN);
				startTangentSlider.setValue(shape.getProfilStartTangent());
			}
		}
		else if (slider.getSection().equals("R Tan") && slider.getAttr().equals("Start"))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setProfilStartTangentRight(shape.getCalculatedStartTangentRight());
				slider.setValue(shape.getProfilStartTangentRight());
				
				shape.setProfilStartTangent(Double.NaN);
				startTangentSlider.setValue(shape.getProfilStartTangent());
			}
		}
		else if (slider.getSection().equals("Tangent") && slider.getAttr().equals("End"))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setProfilEndTangent(shape.getCalculatedEndTangent());
				slider.setValue(shape.getProfilEndTangent());
				
				shape.setProfilEndTangentLeft(Double.NaN);
				endTangentLeftSlider.setValue(shape.getProfilEndTangentLeft());
				
				shape.setProfilEndTangentRight(Double.NaN);
				endTangentRightSlider.setValue(shape.getProfilEndTangentRight());
			}
		}
		else if (slider.getSection().equals("L Tan") && slider.getAttr().equals("End"))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setProfilEndTangentLeft(shape.getCalculatedEndTangentLeft());
				slider.setValue(shape.getProfilEndTangentLeft());
				
				shape.setProfilEndTangent(Double.NaN);
				endTangentSlider.setValue(shape.getProfilEndTangent());
			}
		}
		else if (slider.getSection().equals("R Tan") && slider.getAttr().equals("End"))
		{
			if (slider.isCheckBoxSelected())
			{
				shape.setProfilEndTangentRight(shape.getCalculatedEndTangentRight());
				slider.setValue(shape.getProfilEndTangentRight());
				
				shape.setProfilEndTangent(Double.NaN);
				endTangentSlider.setValue(shape.getProfilEndTangent());
			}
		}
		else if (slider.getSection().equals("Steps") && slider.getAttr().equals("Len"))
		{
			if (slider.isCheckBoxSelected())
			{
				double stepsLength;
				if (shape.hasProfilSteps())
				{
					double length;
					if (!shape.getType().equals("str"))
					{
						Curve curve = (Curve) shape;
						length = curve.getArcRad() * (curve.getRadiusStart() + curve.getRadiusEnd()) / 2;
					}
					else
					{
						length = shape.getLength();
					}
					stepsLength = length / shape.getProfilSteps();
				}
				else
				{
					stepsLength = shape.getValidProfilStepsLength(editorFrame);
					if (Double.isNaN(stepsLength))
						stepsLength = MainTrack.DEFAULT_PROFIL_STEPS_LENGTH;
				}
				shape.setProfilStepsLength(stepsLength);
				slider.setValue(shape.getProfilStepsLength());

				shape.setProfilSteps(Integer.MAX_VALUE);
				profilStepsSlider.setValue(shape.getProfilSteps());
			}
		}
		else if (slider.getSection().equals("Steps") && slider.getAttr().equals(""))
		{
			if (slider.isCheckBoxSelected())
			{
				double length;
				if (!shape.getType().equals("str"))
				{
					Curve curve = (Curve) shape;
					length = curve.getArcRad() * (curve.getRadiusStart() + curve.getRadiusEnd()) / 2;
				}
				else
				{
					length = shape.getLength();
				}
				// start from the steps the game builds now
				int steps = shape.getValidSteps(editorFrame, length);
				shape.setProfilSteps(steps);
				slider.setValue(shape.getProfilSteps());

				shape.setProfilStepsLength(Double.NaN);
				profilStepsLengthSlider.setValue(shape.getProfilStepsLength());
			}
		}
	}

	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			exit();
			view.segmentParamDialog = null;
		}
	}

	private void exit()
	{
		editorFrame.getProject().setSegmentEditorX(this.getX());
		editorFrame.getProject().setSegmentEditorY(this.getY());
	}
} //  @jve:decl-index=0:visual-constraint="10,10"

