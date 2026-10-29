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
	}

	/**
	 * @return what TORCS can't use, or null. TORCS ignores a latitude or longitude
	 * out of range, or one without the other.
	 */
	public String problem()
	{
		double latitude = getDouble(latitudeTextField.getText().trim());
		double longitude = getDouble(longitudeTextField.getText().trim());

		if (!Double.isNaN(latitude) && Math.abs(latitude) > 90)
			return "Georeference: the latitude must be from -90 to 90 degrees.";
		if (!Double.isNaN(longitude) && Math.abs(longitude) > 180)
			return "Georeference: the longitude must be from -180 to 180 degrees.";
		if (Double.isNaN(latitude) != Double.isNaN(longitude))
			return "Georeference: set both the latitude and the longitude, or neither (TORCS needs both).";

		return null;
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
