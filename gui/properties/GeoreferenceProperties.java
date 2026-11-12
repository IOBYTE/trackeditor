package gui.properties;

import javax.swing.JLabel;
import javax.swing.JTextField;

import gui.EditorFrame;
import utils.MutableDouble;
import utils.circuit.Georeference;

/**
 * The TORCS Georeference section: where the main road center is at the start of
 * the first segment, and the heading there.
 */
public class GeoreferenceProperties extends PropertyPanel
{
	private JLabel		latitudeLabel		= new JLabel();
	private JTextField	latitudeTextField	= new JTextField();
	private JLabel		longitudeLabel		= new JLabel();
	private JTextField	longitudeTextField	= new JTextField();
	private JLabel		altitudeLabel		= new JLabel();
	private JTextField	altitudeTextField	= new JTextField();
	private JLabel		headingLabel		= new JLabel();
	private JTextField	headingTextField	= new JTextField();
	private JLabel		noteLabel			= new JLabel();

	public GeoreferenceProperties(EditorFrame editorFrame)
	{
		super(editorFrame);
		initialize();
	}

	private Georeference georeference()
	{
		return getEditorFrame().getTrackData().getGeoreference();
	}

	private void initialize()
	{
		setLayout(null);
		setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));

		addLabel(this, 0, latitudeLabel, "Latitude (deg)", 150);
		addLabel(this, 1, longitudeLabel, "Longitude (deg)", 150);
		addLabel(this, 2, altitudeLabel, "Altitude (m)", 150);
		addLabel(this, 3, headingLabel, "Heading (deg)", 150);
		addLabel(this, 5, noteLabel, "<html>The main road center at the start of the first segment: north latitude, " +
			"east longitude, altitude above mean sea level, and heading clockwise from true north.</html>", 10, 480);
		noteLabel.setSize(480, 50);

		addTextField(this, 0, latitudeTextField, georeference().getLatitude(), 170, 125);
		addTextField(this, 1, longitudeTextField, georeference().getLongitude(), 170, 125);
		addTextField(this, 2, altitudeTextField, georeference().getAltitude(), 170, 125);
		addTextField(this, 3, headingTextField, georeference().getHeading(), 170, 125);

		// red with why while TORCS can't use them, as Check reports
		javax.swing.event.DocumentListener listener = new javax.swing.event.DocumentListener()
		{
			public void insertUpdate(javax.swing.event.DocumentEvent e)
			{
				showProblems();
			}

			public void removeUpdate(javax.swing.event.DocumentEvent e)
			{
				showProblems();
			}

			public void changedUpdate(javax.swing.event.DocumentEvent e)
			{
				showProblems();
			}
		};
		latitudeTextField.getDocument().addDocumentListener(listener);
		longitudeTextField.getDocument().addDocumentListener(listener);
		showProblems();
	}

	/**
	 * Shows the latitude or longitude TORCS can't use in red, with why in its
	 * tool tip: out of range, or set without the other
	 */
	private void showProblems()
	{
		double latitude = getDouble(latitudeTextField.getText().trim());
		double longitude = getDouble(longitudeTextField.getText().trim());
		String latitudeProblem = null;
		String longitudeProblem = null;
		if (!Double.isNaN(latitude) && !(Math.abs(latitude) <= 90))
			latitudeProblem = Georeference.problem(latitude, 0);
		if (!Double.isNaN(longitude) && !(Math.abs(longitude) <= 180))
			longitudeProblem = Georeference.problem(0, longitude);
		// the one that is set is red: the other is empty
		if (latitudeProblem == null && longitudeProblem == null && Double.isNaN(latitude) != Double.isNaN(longitude))
		{
			String problem = Georeference.problem(latitude, longitude);
			if (Double.isNaN(longitude))
				latitudeProblem = problem;
			else
				longitudeProblem = problem;
		}
		show(latitudeTextField, latitudeProblem);
		show(longitudeTextField, longitudeProblem);
	}

	private static void show(JTextField field, String problem)
	{
		field.setForeground(problem == null ? javax.swing.UIManager.getColor("TextField.foreground") : java.awt.Color.RED);
		field.setToolTipText(problem == null ? null : "TORCS ignores the latitude and longitude: " + problem);
	}

	/**
	 * @return what TORCS can't use, or null. TORCS ignores a latitude or longitude
	 * out of range, or one without the other.
	 */
	public String problem()
	{
		double latitude = getDouble(latitudeTextField.getText().trim());
		double longitude = getDouble(longitudeTextField.getText().trim());

		// as Check reports one read from the file
		String problem = Georeference.problem(latitude, longitude);
		return problem == null ? null : "Georeference: " + problem + ".";
	}

	public void exit()
	{
		MutableDouble doubleResult = new MutableDouble();

		if (isDifferent(latitudeTextField.getText(), georeference().getLatitude(), doubleResult))
		{
			georeference().setLatitude(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(longitudeTextField.getText(), georeference().getLongitude(), doubleResult))
		{
			georeference().setLongitude(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(altitudeTextField.getText(), georeference().getAltitude(), doubleResult))
		{
			georeference().setAltitude(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}

		if (isDifferent(headingTextField.getText(), georeference().getHeading(), doubleResult))
		{
			georeference().setHeading(doubleResult.getValue());
			getEditorFrame().documentIsModified = true;
		}
	}
}
