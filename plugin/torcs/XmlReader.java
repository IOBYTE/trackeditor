/*
 *   XmlReader.java
 *   Created on 24 ??? 2005
 *
 *    The XmlReader.java is part of TrackEditor-0.6.0.
 *
 *    TrackEditor-0.6.0 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.6.0 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.6.0; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package plugin.torcs;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.input.SAXHandler;
import org.xml.sax.InputSource;

import gui.EditorFrame;
import utils.DoubleValue;
import utils.Editor;
import utils.SegmentVector;
import utils.ac3d.Ac3dException;
import utils.circuit.Camera;
import utils.circuit.Curve;
import utils.circuit.EnvironmentMapping;
import utils.circuit.Georeference;
import utils.circuit.Graphic;
import utils.circuit.GraphicObject;
import utils.circuit.LocalInfo;
import utils.circuit.MainTrack;
import utils.circuit.Segment;
import utils.circuit.SegmentSide;
import utils.circuit.StartingGrid;
import utils.circuit.Straight;
import utils.circuit.Surface;
import utils.circuit.TrackLight;
import utils.circuit.TrackObject;
import utils.circuit.ObjectMap;
import utils.circuit.Sector;

/**
 * @author Charalampos Alexopoulos
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class XmlReader
{
	public class MyElement extends Element {
		int lineNumber;

		public MyElement(final String name, final Namespace namespace) {
			super(name, namespace);
		}

		public int getLineNumber() {
			return lineNumber;
		}

		public void setLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}
	}

	public class MySAXBuilder extends SAXBuilder {
		public MySAXBuilder(boolean validate) {
			super(validate);
			setFactory(new MyFactory());
		}

		protected void configureContentHandler(SAXHandler contentHandler) {
			super.configureContentHandler(contentHandler);
			((MyFactory) getFactory()).setSAXHandler(contentHandler);
		}
	}

	public class MyFactory extends DefaultJDOMFactory {
		private SAXHandler saxHandler;

		public void setSAXHandler(SAXHandler sh) {
			this.saxHandler = sh;
		}

		public Element element(String name) {
			return this.element(name, (Namespace) null);
		}

		public Element element(String name, Namespace namespace) {
			MyElement e = new MyElement(name, namespace);

			org.xml.sax.Locator loc = this.saxHandler.getDocumentLocator();
			e.setLineNumber(loc.getLineNumber());

			return e;
		}
	}

	private EditorFrame		editorFrame = null;
	private String			filename	= null;
	private List<String>	warnings	= new java.util.ArrayList<String>();

	public XmlReader(EditorFrame editorFrame)
	{
		this.editorFrame = editorFrame;
	}

    public void readXml(String filename) throws JDOMException, IOException
    {
    	this.filename = filename;
		warnings.clear();

        Document doc = readFromFile(filename);
        Element element = doc.getRootElement();
        checkStructure(element);
        setTrackData(element);
    }

    /**
     * Rejects what the game's parameter parser rejects (it then can't load the track at all):
     * a params, section, attnum or attstr element without a name, an attnum or attstr
     * without a value, or an attnum or attstr that isn't inside a section.
     */
    private void checkStructure(Element root) throws JDOMException
    {
        try
        {
            if (root.getName().equals("params") && root.getAttribute("name") == null)
                throw new JDOMException(where(root) + "the <params> element has no name.");

            checkChildren(root, true);
        }
        catch (JDOMException e)
        {
            throw new JDOMException(e.getMessage() + "\n\nThe game can't load a track file like this either.");
        }
    }

    private void checkChildren(Element element, boolean isRoot) throws JDOMException
    {
        for (Object o : element.getChildren())
        {
            Element child = (Element) o;
            String tag = child.getName();

            if (tag.equals("section"))
            {
                if (child.getAttribute("name") == null)
                    throw new JDOMException(where(child) + "a section" + in(element, isRoot) + " has no name.");
                checkChildren(child, false);
            }
            else if (tag.equals("attnum") || tag.equals("attstr"))
            {
                if (isRoot)
                    throw new JDOMException(where(child) + "<" + tag + "> \"" + child.getAttributeValue("name") + "\" isn't inside a section.");
                if (child.getAttribute("name") == null)
                    throw new JDOMException(where(child) + "an <" + tag + ">" + in(element, isRoot) + " has no name.");
                if (child.getAttribute("val") == null)
                    throw new JDOMException(where(child) + "<" + tag + "> \"" + child.getAttributeValue("name") + "\"" + in(element, isRoot) + " has no value.");
            }
        }
    }

    private static String where(Element e)
    {
        String line = e instanceof MyElement ? "line " + ((MyElement) e).getLineNumber() + ": " : "";
        return line;
    }

    private static String in(Element parent, boolean isRoot)
    {
        return isRoot ? "" : " in section \"" + parent.getAttributeValue("name") + "\"";
    }

	private void checkDefaultEntity(String text)
	{
	    boolean hasSurfacesEntity = text.contains("<!ENTITY default-surfaces ");
	    boolean hasSurfacesReference = false;
	    java.util.regex.Matcher surfacesMatcher =
	        java.util.regex.Pattern
	            .compile("(?s)<section\\s+name=\"Surfaces\"[^>]*>(.*?)</section>")
	            .matcher(text);

	    if (surfacesMatcher.find())
	        hasSurfacesReference = surfacesMatcher.group(1).contains("&default-surfaces;");

	    boolean hasObjectsEntity = text.contains("<!ENTITY default-objects ");
	    boolean hasObjectsReference = false;
	    java.util.regex.Matcher objectsMatcher =
	        java.util.regex.Pattern
	            .compile("(?s)<section\\s+name=\"Objects\"[^>]*>(.*?)</section>")
	            .matcher(text);

	    if (objectsMatcher.find())
	        hasObjectsReference = objectsMatcher.group(1).contains("&default-objects;");

		editorFrame.getTrackData().setHasSurfacesEntity(hasSurfacesEntity);
		editorFrame.getTrackData().setHasSurfacesReference(hasSurfacesReference);
		editorFrame.getTrackData().setHasObjectsEntity(hasObjectsEntity);
		editorFrame.getTrackData().setHasObjectsReference(hasObjectsReference);
	}

    private Document readFromFile(String fname) throws JDOMException, IOException
    {
        String text = Files.readString(
                Paths.get(fname),
                StandardCharsets.UTF_8
            );

        checkDefaultEntity(text);

        Document d = null;
        MySAXBuilder sxb = new MySAXBuilder(false);

        sxb.setEntityResolver(new NoOpEntityResolver());
        d = sxb.build(new File(fname));
        return d;
    }

    public void readDefaultSurfaces(String filename, Vector<Surface> surfaceData) throws JDOMException, IOException
    {
		Element surfaces = readDefaultFile(filename, "Surfaces");

    	if (surfaces != null)
    	{
    		getSurfaces(surfaces, surfaceData);
    	}
    }

    public void readDefaultObjects(String filename, Vector<TrackObject> objectData) throws JDOMException, IOException
    {
		Element objects = readDefaultFile(filename, "Objects");

		if (objects != null)
    	{
			getObjects(objects, objectData);
    	}
    }

	/**
	 * Reads a default surfaces or objects file (sections a track includes with
	 * &amp;default-surfaces; or &amp;default-objects;) into a section with the given name.
	 */
	private Element readDefaultFile(String filename, String section) throws JDOMException, IOException
	{
		String xml =
			"<params name=\"test\" type=\"param\" mode=\"mw\">" +
			"<section name=\"" + section + "\">" +
			readFragment(filename) +
			"</section>" +
			"</params>";
		SAXBuilder sxb = new SAXBuilder(false);
		sxb.setValidation(false);
		sxb.setFeature("http://xml.org/sax/features/validation", false);
		sxb.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		sxb.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		Document doc;
		try
		{
			doc = sxb.build(new InputSource(new StringReader(xml)));
		}
		catch (JDOMException e)
		{
			throw new JDOMException(filename + " : " + e.getLocalizedMessage(), e);
		}
		return getChildWithName(doc.getRootElement(), section);
	}

	/**
	 * @return the text of a file that is included in a track, without its XML
	 * declaration. TORCS's objects.xml says it is UTF-8 but has a Latin-1 character
	 * in a comment, which the games' parser lets through, so a file that isn't
	 * valid UTF-8 is read as Latin-1.
	 */
	private static String readFragment(String filename) throws IOException
	{
		byte[] bytes;
		try
		{
			bytes = java.nio.file.Files.readAllBytes(Paths.get(filename));
		}
		catch (java.nio.file.NoSuchFileException e)
		{
			throw new IOException(filename + " (No such file or directory)", e);
		}

		String text;
		try
		{
			text = java.nio.charset.StandardCharsets.UTF_8.newDecoder().decode(java.nio.ByteBuffer.wrap(bytes)).toString();
		}
		catch (java.nio.charset.CharacterCodingException e)
		{
			text = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
		}

		if (text.startsWith("\uFEFF"))
			text = text.substring(1);

	    return text.replaceFirst("^\\s*<\\?xml[^>]*\\?>", "");
	}

    private synchronized void setTrackData(Element root)
    {
        setTrackLights(root);
        setHeader(root);
        setLocalInfo(root);
        setSurfaces(root);
        setObjects(root);
        setStartingGrid(root);
        setGraphic(root);
        setCameras(root);
        setMainTrack(root);
        setSectors(root);
        setGeoreference(root);
        editorFrame.getTrackData().calculateSegmentValues();
    }

	/**
	 * The TORCS Georeference section (degrees, and metres for the altitude, like TORCS).
	 */
	private void setGeoreference(Element root)
	{
		Element element = getChildWithName(root, "Georeference");

		if (element == null)
			return;

		Georeference data = new Georeference();

		data.setLatitude(getAttrNumValue(element, "latitude", "deg"));
		data.setLongitude(getAttrNumValue(element, "longitude", "deg"));
		data.setAltitude(getAttrNumValue(element, "altitude", "m"));
		data.setHeading(getAttrNumValue(element, "heading", "deg"));

		editorFrame.getTrackData().setGeoreference(data);
	}

    private synchronized void setMainTrack(Element root)
    {
        Element mainTrack = getChildWithName(root, "Main Track");

        if (mainTrack == null)
            return;

        editorFrame.getTrackData().getMainTrack().setWidth(getAttrNumValue(mainTrack, "width", "m"));
        editorFrame.getTrackData().getMainTrack().setSurface(getAttrStrValue(mainTrack, "surface"));
        editorFrame.getTrackData().getMainTrack().setProfil(getAttrStrValue(mainTrack, "profil"));
        editorFrame.getTrackData().getMainTrack().setProfilStepsLength(getAttrNumValue(mainTrack, "profil steps length", "m"));
        editorFrame.getTrackData().getMainTrack().setRacelineWidthscale(getAttrNumValue(mainTrack, "raceline widthscale"));
        editorFrame.getTrackData().getMainTrack().setRacelineInt(getAttrNumValue(mainTrack, "raceline int"));
        editorFrame.getTrackData().getMainTrack().setRacelineExt(getAttrNumValue(mainTrack, "raceline ext"));

		// only the version 5 loader reads these
		if (editorFrame.getTrackData().getHeader().getVersion() == 5)
		{
			MainTrack main = editorFrame.getTrackData().getMainTrack();
			main.setStartBearing(getAttrNumValue(mainTrack, "original bearing", "deg"));
			main.setStartX(getAttrNumValue(mainTrack, "start X", "m"));
			main.setStartY(getAttrNumValue(mainTrack, "start Y", "m"));
			main.setStartZ(getAttrNumValue(mainTrack, "start Z", "m"));
			main.setMinimumX(getAttrNumValue(mainTrack, "minimum X", "m"));
			main.setMinimumY(getAttrNumValue(mainTrack, "minimum Y", "m"));
			main.setMinimumZ(getAttrNumValue(mainTrack, "minimum Z", "m"));
			main.setExtentX(getAttrNumValue(mainTrack, "extent X", "m"));
			main.setExtentY(getAttrNumValue(mainTrack, "extent Y", "m"));
			main.setExtentZ(getAttrNumValue(mainTrack, "extent Z", "m"));
		}

        if (editorFrame.getTrackData().getHeader().getVersion() == 3)
        {
			setSideV3(mainTrack, editorFrame.getTrackData().getMainTrack().getLeft(), "l", true);
			setSideV3(mainTrack, editorFrame.getTrackData().getMainTrack().getRight(), "r", true);
            setPitsV3(mainTrack);
        }
        else
        {
			setSide(mainTrack, editorFrame.getTrackData().getMainTrack().getLeft(), "Left", true);
			setSide(mainTrack, editorFrame.getTrackData().getMainTrack().getRight(), "Right", true);
            setPits(mainTrack);
        }

        setSegments(mainTrack);
    }

    /**
     * @param header
     */
    private void setHeader(Element root)
    {
        Element header = getChildWithName(root, "Header");

        if (header == null)
            return;

        editorFrame.getTrackData().getHeader().setName(getAttrStrValue(header, "name"));
        editorFrame.getTrackData().getHeader().setShortName(getAttrStrValue(header, "short name"));
        editorFrame.getTrackData().getHeader().setCategory(getAttrStrValue(header, "category"));
        editorFrame.getTrackData().getHeader().setSubcategory(getAttrStrValue(header, "subcategory"));
        editorFrame.getTrackData().getHeader().setCarCategory(getAttrStrValue(header, "car category"));
        editorFrame.getTrackData().getHeader().setVersion(getAttrIntValue(header, "version"));
        editorFrame.getTrackData().getHeader().setSkyVersion(getAttrIntValue(header, "sky version"));
        editorFrame.getTrackData().getHeader().setAuthor(getAttrStrValue(header, "author"));
        editorFrame.getTrackData().getHeader().setEmail(getAttrStrValue(header, "email"));
        editorFrame.getTrackData().getHeader().setCopyright(getAttrStrValue(header, "copyright"));
        editorFrame.getTrackData().getHeader().setDescription(getAttrStrValue(header, "description"));
        editorFrame.getTrackData().getHeader().setDisabledPenalties(getAttrStrValue(header, "disabled penalties"));
    }

    /**
     * @param root
     */
    private void setCameras(Element root)
    {
        Element cameras = getChildWithName(root, "Cameras");

        if (cameras == null)
            return;

        if (editorFrame.getTrackData().getHeader().getVersion() == 3)
        	cameras = getChildWithName(cameras, "list");

        if (cameras == null)
            return;

        Vector<Camera> cameraData = new Vector<Camera>();
        List<?> sections = cameras.getChildren();
        Iterator<?> it = sections.iterator();
        while (it.hasNext())
        {
            Camera cam = new Camera();

            Element camera = (Element) it.next();
            cam.setName(camera.getAttribute("name").getValue());
            cam.setComment(getAttrStrValue(camera, "comment"));
            cam.setSegment(getAttrStrValue(camera, "segment"));
            cam.setToRight(getAttrNumValue(camera, "to right"));
            // units can be meters for straights or degrees for curves
            DoubleValue toStart = getAttrNumUnitsValue(camera, "to start");
            if (toStart != null)
            {
                cam.setToStart(toStart.value);
                cam.setToStartUnits(toStart.units);
            }
            cam.setHeight(getAttrNumValue(camera, "height"));
            cam.setFovStart(getAttrStrValue(camera, "fov start"));
            cam.setFovEnd(getAttrStrValue(camera, "fov end"));

            cameraData.add(cam);
        }
        editorFrame.getTrackData().setCameras(cameraData);
    }

    /**
     * @param root
     */
    private void setTrackLights(Element root)
    {
        Element lights = getChildWithName(root, "Track Lights");

        if (lights == null)
            return;

        Vector<TrackLight> lightData = new Vector<TrackLight>();
        List<?> sections = lights.getChildren();
        Iterator<?> it = sections.iterator();
        while (it.hasNext())
        {
            TrackLight lit = new TrackLight();

            Element light = (Element) it.next();
            lit.setName(light.getAttribute("name").getValue());

            Element corner = getChildWithName(light, "topleft");

            if (corner != null)
            {
                lit.setTopLeft(getAttrNumValue(corner, "x"),
                               getAttrNumValue(corner, "y"),
                               getAttrNumValue(corner, "z"));
            }

            corner = getChildWithName(light, "bottomright");

            if (corner != null)
            {
                lit.setBottomRight(getAttrNumValue(corner, "x"),
                                   getAttrNumValue(corner, "y"),
                                   getAttrNumValue(corner, "z"));
            }

            lit.setRole(getAttrStrValue(light, "role"));
            lit.setTextureOn(getAttrStrValue(light, "texture on"));
            lit.setTextureOff(getAttrStrValue(light, "texture off"));
            lit.setIndex(getAttrIntValue(light, "index"));
            lit.setRed(getAttrNumValue(light, "red"));
            lit.setGreen(getAttrNumValue(light, "green"));
            lit.setBlue(getAttrNumValue(light, "blue"));

            lightData.add(lit);
        }
        editorFrame.getTrackData().setTrackLights(lightData);
    }

    /**
     * @param root
     */
    private void setSectors(Element root)
    {
        Element sectors = getChildWithName(root, "Sectors");

        if (sectors == null)
            return;

        Vector<Sector> sectorData = new Vector<Sector>();
        List<?> sections = sectors.getChildren();
        Iterator<?> it = sections.iterator();
        while (it.hasNext())
        {
            Sector sector = new Sector();

            Element element = (Element) it.next();
            sector.setName(element.getAttribute("name").getValue());
            sector.setComment(getAttrStrValue(element, "comment"));
            sector.setDistanceFromStart(getAttrNumValue(element, "distance from start", "m"));

            sectorData.add(sector);
        }
        editorFrame.getTrackData().setSectors(sectorData);
    }

    /**
     * @param root
     */
    private void setStartingGrid(Element root)
    {
        Element element = getChildWithName(root, "Starting Grid");

        if (element == null)
            return;

        StartingGrid data = new StartingGrid();

        data.setRows(getAttrIntValue(element, "rows"));
        data.setPolePositionSide(getAttrStrValue(element, "pole position side"));
        data.setDistanceToStart(getAttrNumValue(element, "distance to start", "m"));
        data.setDistanceBetweenColumns(getAttrNumValue(element, "distance between columns", "m"));
        data.setOffsetWithinAColumn(getAttrNumValue(element, "offset within a column", "m"));
        data.setInitialHeight(getAttrNumValue(element, "initial height", "m"));

        editorFrame.getTrackData().setStartingGrid(data);
    }

    /**
     * @param root
     */
    private void setLocalInfo(Element root)
    {
        Element element = getChildWithName(root, "Local Info");

        if (element == null)
            return;

        LocalInfo data = new LocalInfo();

        data.setStation(getAttrStrValue(element, "station"));
        data.setTimezone(getAttrNumValue(element, "timezone"));
        data.setOverallRainLikelyhood(getAttrNumValue(element, "overall rain likelyhood", "%"));
        data.setLittleRainLikelyhood(getAttrNumValue(element, "little rain likelyhood", "%"));
        data.setMediumRainLikelyhood(getAttrNumValue(element, "medium rain likelyhood", "%"));
        data.setTimeOfDay(getAttrNumValue(element, "time of day", "hour"));
        data.setSunAscension(getAttrNumValue(element, "sun ascension", "deg"));
        data.setAltitude(getAttrNumValue(element, "altitude", "m"));
        DoubleValue latitude = getAttrNumUnitsValue(element, "latitude");
        if (latitude != null)
        {
            data.setLatitude(latitude.value);
            data.setLatitudeUnits(latitude.units);
        }
        DoubleValue longitude = getAttrNumUnitsValue(element, "longitude");
        if (longitude != null)
        {
            data.setLongitude(longitude.value);
            data.setLongitudeUnits(longitude.units);
        }
        data.setClimat(getAttrIntValue(element, "climat"));
        data.setPrecipitation(getAttrNumValue(element, "precipitation"));
        data.setBestLap(getAttrNumValue(element, "best lap"));

        editorFrame.getTrackData().setLocalInfo(data);
    }

    /**
     * @param root
     */
    private void setSurfaces(Element root)
    {
        Element surfaces = getChildWithName(root, "Surfaces");

        if (surfaces == null)
            return;

        if (editorFrame.getTrackData().getHeader().getVersion() == 3)
            surfaces = getChildWithName(surfaces, "List");

        if (surfaces == null)
            return;

        Vector<Surface> surfaceData = new Vector<Surface>();
        getSurfaces(surfaces, surfaceData);
        editorFrame.getTrackData().setSurfaces(surfaceData);
    }

    private void getSurfaces(Element surfaces, Vector<Surface> surfaceData)
    {
        List<?> sections = surfaces.getChildren();
        Iterator<?> it = sections.iterator();
        while (it.hasNext())
        {
            Surface surf = new Surface();

            Element surface = (Element) it.next();
            surf.setName(surface.getAttribute("name").getValue());
            surf.setComment(getAttrStrValue(surface, "comment"));
            surf.setColorR1(getAttrNumValue(surface, "color R1"));
            surf.setColorG1(getAttrNumValue(surface, "color G1"));
            surf.setColorB1(getAttrNumValue(surface, "color B1"));
            surf.setColorR2(getAttrNumValue(surface, "color R2"));
            surf.setColorG2(getAttrNumValue(surface, "color G2"));
            surf.setColorB2(getAttrNumValue(surface, "color B2"));
            surf.setTextureName(getAttrStrValue(surface, "texture name"));
            surf.setTextureType(getAttrStrValue(surface, "texture type"));
            surf.setTextureSize(getAttrNumValue(surface, "texture size", "m"));
			surf.setTextureSizeV(getAttrNumValue(surface, "texture size v", "m"));
            surf.setTextureRandomness(getAttrNumValue(surface, "texture randomness", "m"));
            surf.setTextureLinkWithPrevious(getAttrStrValue(surface, "texture link with previous"));
            surf.setTextureStartOnBoundary(getAttrStrValue(surface, "texture start on boundary"));
            surf.setTextureMipMap(getAttrNumValue(surface, "texture mipmap", "m"));
            surf.setFriction(getAttrNumValue(surface, "friction"));
            surf.setFrictionRain(getAttrNumValue(surface, "friction rain"));
            surf.setRollingResistance(getAttrNumValue(surface, "rolling resistance"));
            surf.setRollingResistanceRain(getAttrNumValue(surface, "rolling resistance rain"));
            surf.setBumpName(getAttrStrValue(surface, "bump name"));
            surf.setBumpSize(getAttrNumValue(surface, "bump size", "m"));
            surf.setRoughness(getAttrNumValue(surface, "roughness", "m"));
            surf.setRoughnessWavelength(getAttrNumValue(surface, "roughness wavelength", "m"));
            surf.setRacelineName(getAttrStrValue(surface, "raceline name"));
            surf.setDammage(getAttrNumValue(surface, "dammage"));
            surf.setRebound(getAttrNumValue(surface, "rebound"));

            surfaceData.add(surf);
        }
    }

    /**
     * @param root
     */
    private void setObjects(Element root)
    {
        Element objects = getChildWithName(root, "Objects");

        if (objects == null)
            return;

        Vector<TrackObject> objectData = new Vector<TrackObject>();
        getObjects(objects, objectData);
        editorFrame.getTrackData().setObjects(objectData);
    }

    /**
     * @param root
     */
    private void getObjects(Element objects, Vector<TrackObject> objectData)
    {
        List<?> sections = objects.getChildren();
        Iterator<?> it = sections.iterator();
        while (it.hasNext())
        {
            TrackObject obj = new TrackObject();

            Element object = (Element) it.next();
            obj.setName(object.getAttribute("name").getValue());
            obj.setObject(getAttrStrValue(object, "object"));
            obj.setComment(getAttrStrValue(object, "comment"));
            obj.setColor(getAttrIntValue(object, "color"));
            obj.setOrientationType(getAttrStrValue(object, "orientation type"));
            obj.setBorderDistance(getAttrNumValue(object, "border distance", "m"));
            obj.setOrientation(getAttrNumValue(object, "orientation", "deg"));
            obj.setDeltaHeight(getAttrNumValue(object, "delta height"));
            obj.setDeltaVert(getAttrNumValue(object, "delta vert"));
            obj.setScaleType(getAttrStrValue(object, "scale type"));
            obj.setScale(getAttrNumValue(object, "scale"));
            obj.setScaleMin(getAttrNumValue(object, "scale min"));
            obj.setScaleMax(getAttrNumValue(object, "scale max"));
            obj.setUseMaterial(getAttrStrValue(object, "use material"));

            objectData.add(obj);
        }
    }

    /**
     *
     */
    private void setGraphic(Element root)
    {
        Element graphic = getChildWithName(root, "Graphic");

        if (graphic == null)
            return;

        Graphic	data = new Graphic();

        data.setDescription(getAttrStrValue(graphic, "3d description"));
        data.setDescriptionNight(getAttrStrValue(graphic, "3d description night"));
        data.setDescriptionRainNight(getAttrStrValue(graphic, "3d description rain+night"));

        data.setSeparateObjects(getAttrStrValue(graphic, "3d separate objects"));
        data.setTerrainObjects(getAttrStrValue(graphic, "3d terrain"));
        data.setRoadObjects(getAttrStrValue(graphic, "3d road"));
        data.setRoad2Objects(getAttrStrValue(graphic, "3d road2"));
        data.setTreesObjects(getAttrStrValue(graphic, "3d trees"));
        data.setBuildingObjects(getAttrStrValue(graphic, "3d building"));
        data.setHouseObjects(getAttrStrValue(graphic, "3d house"));
        data.setCarsObjects(getAttrStrValue(graphic, "3d cars"));
        data.setTruckObjects(getAttrStrValue(graphic, "3d truck"));
        data.setTribunesObjects(getAttrStrValue(graphic, "3d tribunes"));
        data.setPubObjects(getAttrStrValue(graphic, "3d pub"));
        data.setDecorsObjects(getAttrStrValue(graphic, "3d decors"));
        data.setWaterObjects(getAttrStrValue(graphic, "3d water"));
        data.setGrassObjects(getAttrStrValue(graphic, "3d grass"));
        data.setSandObjects(getAttrStrValue(graphic, "3d sand"));
        data.setRockObjects(getAttrStrValue(graphic, "3d rock"));
        data.setCurbObjects(getAttrStrValue(graphic, "3d curb"));
        data.setFenceObjects(getAttrStrValue(graphic, "3d fence"));
        data.setBarrierObjects(getAttrStrValue(graphic, "3d barrier"));
        data.setWallObjects(getAttrStrValue(graphic, "3d wall"));
        data.setEnvironmentObjects(getAttrStrValue(graphic, "3d environment"));

        data.setBackgroundImage(getAttrStrValue(graphic, "background image"));
        data.setBackgroundType(getAttrIntValue(graphic, "background type"));
        data.setBackgroundColorR(getAttrNumValue(graphic, "background color R"));
        data.setBackgroundColorG(getAttrNumValue(graphic, "background color G"));
        data.setBackgroundColorB(getAttrNumValue(graphic, "background color B"));
        data.setAmbientColorR(getAttrNumValue(graphic, "ambient color R"));
        data.setAmbientColorG(getAttrNumValue(graphic, "ambient color G"));
        data.setAmbientColorB(getAttrNumValue(graphic, "ambient color B"));
        data.setDiffuseColorR(getAttrNumValue(graphic, "diffuse color R"));
        data.setDiffuseColorG(getAttrNumValue(graphic, "diffuse color G"));
        data.setDiffuseColorB(getAttrNumValue(graphic, "diffuse color B"));
        data.setSpecularColorR(getAttrNumValue(graphic, "specular color R"));
        data.setSpecularColorG(getAttrNumValue(graphic, "specular color G"));
        data.setSpecularColorB(getAttrNumValue(graphic, "specular color B"));
        data.setLightPositionX(getAttrNumValue(graphic, "light position x"));
        data.setLightPositionY(getAttrNumValue(graphic, "light position y"));
        data.setLightPositionZ(getAttrNumValue(graphic, "light position z"));
        data.setShininess(getAttrNumValue(graphic, "shininess"));
        data.setFovFactor(getAttrNumValue(graphic, "fov factor"));

        Element marks = getChildWithName(graphic, "Turn Marks");

        if (marks != null)
        {
            data.getTurnMarks().setWidth(getAttrNumValue(marks, "width", "m"));
            data.getTurnMarks().setHeight(getAttrNumValue(marks, "height", "m"));
            data.getTurnMarks().setVerticalSpace(getAttrNumValue(marks, "vertical space", "m"));
            data.getTurnMarks().setHorizontalSpace(getAttrNumValue(marks, "horizontal space", "m"));
        }

        Element terrain = getChildWithName(graphic, "Terrain Generation");

        if (terrain != null)
        {
	        data.getTerrainGeneration().setTrackStep(getAttrNumValue(terrain, "track step", "m"));
	        data.getTerrainGeneration().setBorderMargin(getAttrNumValue(terrain, "border margin", "m"));
	        data.getTerrainGeneration().setBorderStep(getAttrNumValue(terrain, "border step", "m"));
	        data.getTerrainGeneration().setBorderHeight(getAttrNumValue(terrain, "border height", "m"));
	        data.getTerrainGeneration().setOrientation(getAttrStrValue(terrain, "orientation"));
	        data.getTerrainGeneration().setMaximumAltitude(getAttrNumValue(terrain, "maximum altitude", "m"));
	        data.getTerrainGeneration().setMinimumAltitude(getAttrNumValue(terrain, "minimum altitude", "m"));
	        data.getTerrainGeneration().setGroupSize(getAttrNumValue(terrain, "group size", "m"));
	        data.getTerrainGeneration().setElevationMap(getAttrStrValue(terrain, "elevation map"));

	        String reliefFile = getAttrStrValue(terrain, "relief file");
	        if (reliefFile != null)
	        {
	    		Path path = Paths.get(reliefFile);
	        	try
	        	{
	        		data.getTerrainGeneration().getReliefs().setFileName(reliefFile);
	    		}
	    		catch (IOException e)
	    		{
	        		String msg = path.getFileName() + " : " + e.getLocalizedMessage();
	            	JOptionPane.showMessageDialog(editorFrame, msg, "Relief File", JOptionPane.ERROR_MESSAGE);
	    		}
	    		catch (Ac3dException e)
	    		{
	        		String msg = path.getFileName() + " : " + e.getLocalizedMessage();
	            	JOptionPane.showMessageDialog(editorFrame, msg, "Relief File", JOptionPane.ERROR_MESSAGE);
	    		}
	    		catch (Exception e)
	    		{
	        		String msg = path.getFileName() + " : " + e.getLocalizedMessage();
	            	JOptionPane.showMessageDialog(editorFrame, msg, "Relief File", JOptionPane.ERROR_MESSAGE);
	    		}
	        }

	        data.getTerrainGeneration().setReliefBorder(getAttrStrValue(terrain, "relief border"));
	        data.getTerrainGeneration().setSurface(getAttrStrValue(terrain, "surface"));
	        data.getTerrainGeneration().setRandomSeed(getAttrIntValue(terrain, "random seed"));
	        data.getTerrainGeneration().setUseObjectMaterials(getAttrStrValue(terrain, "use object materials"));
	        data.getTerrainGeneration().setTiledFile(getAttrStrValue(terrain, "tiled file"));
			// TORCS
	        data.getTerrainGeneration().setCrossingMode(getAttrStrValue(terrain, "crossing mode"));
			data.getTerrainGeneration().setTerrainTransition(getAttrNumValue(terrain, "terrain transition", "m"));
			data.getTerrainGeneration().setCrossingBridgeSocketGap(getAttrNumValue(terrain, "crossing bridge socket gap", "m"));
			data.getTerrainGeneration().setCrossingBridgeThickness(getAttrNumValue(terrain, "crossing bridge thickness", "m"));
			for (int i = 0; i < utils.circuit.TerrainGeneration.CROSSING_BRIDGE_SURFACE_KEYS.length; i++)
				data.getTerrainGeneration().setCrossingBridgeSurface(i, getAttrStrValue(terrain, utils.circuit.TerrainGeneration.CROSSING_BRIDGE_SURFACE_KEYS[i]));

	        Element objects = getChildWithName(terrain, "Objects");

	        if (objects != null)
	        {
	        	Vector<GraphicObject>	graphicObjects = new Vector<GraphicObject>();
	        	Iterator<?> it = objects.getChildren().iterator();
	        	while (it.hasNext())
	        	{
	        		Element el = (Element) it.next();
	        		GraphicObject object = new GraphicObject(el.getAttribute("name").getValue(),
	        				getAttrIntValue(el, "color"),
	        				new Point2D.Double(getAttrNumValue(el, "x"), getAttrNumValue(el, "y")));
	        		object.setComment(getAttrStrValue(el, "comment"));
	        		object.setOrientation(getAttrNumValue(el, "orientation", "deg"));
	        		object.setHeight(getAttrNumValue(el, "height", "m"));
	        		graphicObjects.add(object);
	        	}
	        	data.getTerrainGeneration().setGraphicObjects(graphicObjects);
	        }

	        Element objectMaps = getChildWithName(terrain, "Object Maps");

	        if (objectMaps != null)
	        {
		        Vector<ObjectMap> objMap = new Vector<ObjectMap>();
		        Iterator<?> it = objectMaps.getChildren().iterator();
		        while (it.hasNext())
		        {
                    ObjectMap obj = new ObjectMap();

		            Element el = (Element) it.next();
		            obj.setName(el.getAttribute("name").getValue());

		            try
		            {
		            	obj.setObjectMap(getAttrStrValue(el, "object map"));
		            }
		            catch (IOException e)
		            {
		            }

		            if (obj.getObjectMap() != null && !obj.getObjectMap().isEmpty())
		            {
		            	try
		            	{
		            		obj.readImage();
		            	}
		            	catch (IOException e)
		            	{
		            		String msg = obj.getObjectMap() + " : " + e.getLocalizedMessage();
		                	JOptionPane.showMessageDialog(editorFrame, msg, "Object Map File", JOptionPane.ERROR_MESSAGE);
		            	}
		            }

		            objMap.add(obj);
		        }
		        data.getTerrainGeneration().setObjectMaps(objMap);
	        }
        }

        Element environment = getChildWithName(graphic, "Environment Mapping");

        if (environment != null)
        {
            Vector<EnvironmentMapping> envMap = new Vector<EnvironmentMapping>();
	        Iterator<?> it = environment.getChildren().iterator();
	        while (it.hasNext())
	        {
	            EnvironmentMapping env = new EnvironmentMapping();

	            Element el = (Element) it.next();
	            env.setName(el.getAttribute("name").getValue());

	            env.setEnvMapImage(getAttrStrValue(el, "env map image"));

	            envMap.add(env);
	        }
	        data.setEnvironmentMapping(envMap);
        }

	    editorFrame.getTrackData().setGraphic(data);
    }

    /**
     *
     */
    private void setPits(Element track)
    {
        Element pits = getChildWithName(track, "Pits");

        if (pits == null)
            return;

        editorFrame.getPits().setStyle(getAttrIntValue(pits, "pit style"));
        // try alternate representation used by some TORCS tracks
        if (editorFrame.getPits().getStyle() == Integer.MAX_VALUE)
        {
        	String pitStyle = getAttrStrValue(pits, "type");

            if (pitStyle != null)
            {
         	   if (pitStyle.equals("no pit"))
         	   {
                    editorFrame.getPits().setStyle(0);
         	   }
         	   else if (pitStyle.equals("track side"))
         	   {
                    editorFrame.getPits().setStyle(1);
         	   }
         	   else if (pitStyle.equals("seperate path"))
         	   {
                    editorFrame.getPits().setStyle(2);
         	   }
            }
        }

        editorFrame.getPits().setSide(getAttrStrValue(pits, "side"));
        editorFrame.getPits().setEntry(getAttrStrValue(pits, "entry"));
        editorFrame.getPits().setStart(getAttrStrValue(pits, "start"));
        editorFrame.getPits().setStartBuildings(getAttrStrValue(pits, "start buildings"));
        editorFrame.getPits().setStopBuildings(getAttrStrValue(pits, "stop buildings"));
        editorFrame.getPits().setMaxPits(getAttrIntValue(pits, "max pits"));
        editorFrame.getPits().setEnd(getAttrStrValue(pits, "end"));
        editorFrame.getPits().setExit(getAttrStrValue(pits, "exit"));
        editorFrame.getPits().setLength(getAttrNumValue(pits, "length", "m"));
        editorFrame.getPits().setWidth(getAttrNumValue(pits, "width", "m"));
        editorFrame.getPits().setIndicator(getAttrIntValue(pits, "pit indicator"));
        editorFrame.getPits().setSpeedLimit(getAttrNumUnitsValue(pits, "speed limit"));
    }

    /**
    *
    */
   private void setPitsV3(Element mainTrack)
   {
       String pitStyle = getAttrStrValue(mainTrack, "pit type");

       if (pitStyle != null)
       {
    	   if (pitStyle.equals("no pit"))
    	   {
               editorFrame.getPits().setStyle(0);
    	   }
    	   else if (pitStyle.equals("track side"))
    	   {
               editorFrame.getPits().setStyle(1);
    	   }
    	   else if (pitStyle.equals("seperate path"))
    	   {
               editorFrame.getPits().setStyle(2);
    	   }
       }

       editorFrame.getPits().setSide(getAttrStrValue(mainTrack, "pit side"));
       editorFrame.getPits().setEntry(getAttrStrValue(mainTrack, "pit entry"));
       editorFrame.getPits().setStart(getAttrStrValue(mainTrack, "pit start"));
       editorFrame.getPits().setStartBuildings(getAttrStrValue(mainTrack, "start buildings"));
       editorFrame.getPits().setStopBuildings(getAttrStrValue(mainTrack, "stop buildings"));
       editorFrame.getPits().setMaxPits(getAttrIntValue(mainTrack, "max pits"));
       editorFrame.getPits().setEnd(getAttrStrValue(mainTrack, "pit end"));
       editorFrame.getPits().setExit(getAttrStrValue(mainTrack, "pit exit"));
       editorFrame.getPits().setLength(getAttrNumValue(mainTrack, "pit length", "m"));
       editorFrame.getPits().setWidth(getAttrNumValue(mainTrack, "pit width", "m"));
       editorFrame.getPits().setSpeedLimit(getAttrNumUnitsValue(mainTrack, "speed limit"));
   }

	/**
	 * Reads a v3 side the way the games' v3 loader (track3.cpp) reads it, so
	 * the track keeps its meaning when it is saved as v4:
	 * <ul>
	 * <li>the Main Track has only "side width"; segments have only "side start
	 * width" and "side end width".</li>
	 * <li>"side type" (banking type) is read only for the Main Track.</li>
	 * <li>barriers are read only for the Main Track, and its barrier height
	 * defaults to 1 m (v4 defaults to 0.6 m).</li>
	 * <li>in TORCS, a segment without a border height has a height of 0 (v4
	 * inherits it from the previous segment).</li>
	 * </ul>
	 */
	private void setSideV3(Element seg, SegmentSide part, String sPart, boolean mainTrack)
	{
		int side = sPart.equals("l") ? 0 : 1;

		if (mainTrack)
		{
			double width = getAttrNumValue(seg, sPart + "side width", "m");
			part.setSideStartWidth(width);
			part.setSideEndWidth(width);
		}
		else
		{
			part.setSideStartWidth(getAttrNumValue(seg, sPart + "side start width", "m"));
			part.setSideEndWidth(getAttrNumValue(seg, sPart + "side end width", "m"));
		}
		part.setSideSurface(getAttrStrValue(seg, sPart + "side surface"));
		if (mainTrack)
			part.setSideBankingType(getAttrStrValue(seg, sPart + "side type"));
		part.setBorderStyle(getAttrStrValue(seg, sPart + "border style"));
		part.setBorderWidth(getAttrNumValue(seg, sPart + "border width", "m"));
		double borderHeight = getAttrNumValue(seg, sPart + "border height", "m");
		if (mainTrack)
		{
			v3BorderHeight[side] = Double.isNaN(borderHeight) ? 0.0 : borderHeight;
		}
		else if (!Double.isNaN(borderHeight))
		{
			v3BorderHeight[side] = borderHeight;
		}
		else if (editorFrame.getGameType() == EditorFrame.GameType.TORCS && v3BorderHeight[side] != 0.0)
		{
			borderHeight = 0.0;
			v3BorderHeight[side] = 0.0;
		}
		part.setBorderHeight(borderHeight);
		part.setBorderSurface(getAttrStrValue(seg, sPart + "border surface"));
		if (mainTrack)
		{
			part.setBarrierStyle(getAttrStrValue(seg, sPart + "barrier style"));
			part.setBarrierWidth(getAttrNumValue(seg, sPart + "barrier width", "m"));
			double barrierHeight = getAttrNumValue(seg, sPart + "barrier height", "m");
			part.setBarrierHeight(Double.isNaN(barrierHeight) ? 1.0 : barrierHeight);
			part.setBarrierSurface(getAttrStrValue(seg, sPart + "barrier surface"));
		}
	}

	// the border height a v3 segment inherits, per side (left, right)
	private double[] v3BorderHeight = new double[2];

    private synchronized void setSegments(Element mainTrack)
    {
		Element segmentsSection;

        if (editorFrame.getTrackData().getHeader().getVersion() == 3)
            segmentsSection = getChildWithName(mainTrack, "segments");
        else
            segmentsSection = getChildWithName(mainTrack, "Track Segments");

        if (segmentsSection == null)
        {
            // keep an empty track rather than none, so the editor doesn't crash
            editorFrame.getTrackData().setSegments(new SegmentVector());
            JOptionPane.showMessageDialog(editorFrame, "The track has no \"" + (editorFrame.getTrackData().getHeader().getVersion() == 3 ? "segments" : "Track Segments") +
                "\" section in its Main Track.", "Read Track", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<?> segments = segmentsSection.getChildren();
        int skipped = 0;

        SegmentVector trackData = new SegmentVector();
        Iterator<?> it;
        Segment prev = null;
        Segment shape = null;

        it = segments.iterator();
        while (it.hasNext())
        {
            Element e = (Element) it.next();
            String type = getAttrStrValue(e, "type");
            // like the game, skip a segment without a type (or one it doesn't know)
            if (type == null || !(type.equals("str") || type.equals("lft") || type.equals("rgt")))
            {
                skipped++;
                continue;
            }
            if (type.equals("str"))
            {
                shape = new Straight();
            } else
            {
                shape = new Curve(type, null);
            }
            shape = setSegment(e, shape, prev);
            trackData.add(shape);
            prev = shape;
        }

        if (skipped > 0)
        {
            JOptionPane.showMessageDialog(editorFrame, skipped + " segment" + (skipped == 1 ? " has" : "s have") +
                " no type (str, lft or rgt) and " + (skipped == 1 ? "was" : "were") + " left out.\n" +
                "The game skips segments without a type.", "Read Track", JOptionPane.WARNING_MESSAGE);
        }

        editorFrame.getTrackData().setSegments(trackData);
        editorFrame.getTrackData().calculateSegmentValues();

        for (Segment segment : editorFrame.getTrackData().getSegments())
        {
            try
            {
                segment.calcShape(editorFrame);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private synchronized Segment setSegment(Element seg, Segment shape,
            Segment prev)
    {
        SegmentSide left = shape.getLeft();
        SegmentSide right = shape.getRight();

        if (shape.getType().equals("str"))
        {
            shape.setLength(getAttrNumValue(seg, "lg", "m"));
        } else
        {
            double arc = getAttrNumValue(seg, "arc", "deg");
            ((Curve) shape).setArcDeg(arc);
            double startRad = getAttrNumValue(seg, "radius", "m");
            ((Curve) shape).setRadiusStart(startRad);
            double endRad = getAttrNumValue(seg, "end radius", "m");
            if (Double.isNaN(endRad))
            {
                ((Curve) shape).setRadiusEnd(startRad);
            } else
            {
                ((Curve) shape).setRadiusEnd(endRad);
            }
            ((Curve) shape).setMarks(getAttrStrValue(seg, "marks"));
        }
        shape.setComment(getAttrStrValue(seg, "comment"));
        String name = getSegmentName(seg);
        shape.setName(name);
        if (name.startsWith("curve "))
        {
            String tmp = name.substring(6);
            try
            {
                Integer tmpInt = Integer.parseInt(tmp);
                int i = tmpInt.intValue();
                if (i > Editor.getProperties().getCurveNameCount())
                {
                    Editor.getProperties().setCurveNameCount(i);
                }
            } catch (NumberFormatException e)
            {
                /* If what follows the word curve
                 * is not a number just ignore it */
            }
        }
        if (name.startsWith("straight "))
        {
            String tmp = name.substring(9);
            try
            {
                Integer tmpInt = Integer.parseInt(tmp);
                int i = tmpInt.intValue();
                if (i > Editor.getProperties().getStraightNameCount())
                {
                    Editor.getProperties().setStraightNameCount(i);
                }
            } catch (NumberFormatException e)
            {
                /* If what follows the word straight
                 * is not a number just ignore it */
            }
        }

        shape.setSurface(getAttrStrValue(seg, "surface"));
        shape.setPenalty(getAttrIntValue(seg, "penalty"));
        shape.setEnvMapIndex(getAttrIntValue(seg, "env map index"));
        shape.setDovFactor(getAttrNumValue(seg, "DoV factor"));

        shape.setHeightStart(getAttrNumValue(seg, "z start", "m"));
        shape.setHeightStartLeft(getAttrNumValue(seg, "z start left", "m"));
        shape.setHeightStartRight(getAttrNumValue(seg, "z start right", "m"));

        shape.setHeightEnd(getAttrNumValue(seg, "z end", "m"));
        shape.setHeightEndLeft(getAttrNumValue(seg, "z end left", "m"));
        shape.setHeightEndRight(getAttrNumValue(seg, "z end right", "m"));

        shape.setGrade(getAttrNumValue(seg, "grade", "%"));
        shape.setBankingStart(getAttrNumValue(seg, "banking start", "deg"));
        shape.setBankingEnd(getAttrNumValue(seg, "banking end", "deg"));
        shape.setProfil(getAttrStrValue(seg, "profil"));
        shape.setProfilSteps(getAttrIntValue(seg, "profil steps"));
        shape.setProfilStepsLength(getAttrNumValue(seg, "profil steps length", "m"));
        shape.setProfilStartTangent(getAttrNumValue(seg, "profil start tangent", "%"));
        shape.setProfilEndTangent(getAttrNumValue(seg, "profil end tangent", "%"));
        shape.setProfilStartTangentLeft(getAttrNumValue(seg, "profil start tangent left", "%"));
        shape.setProfilEndTangentLeft(getAttrNumValue(seg, "profil end tangent left", "%"));
        shape.setProfilStartTangentRight(getAttrNumValue(seg, "profil start tangent right", "%"));
        shape.setProfilEndTangentRight(getAttrNumValue(seg, "profil end tangent right", "%"));

        if (editorFrame.getTrackData().getHeader().getVersion() == 3)
        {
			setSideV3(seg, left, "l", false);
			setSideV3(seg, right, "r", false);
        }
        else
        {
			setSide(seg, left, "Left", false);
			setSide(seg, right, "Right", false);
        }

        return shape;
    }

    /**
     * @param el
     * @param left
     * @param string
     */
    private synchronized void setSide(Element seg, SegmentSide part,
			String sPart, boolean mainTrack)
    {
        Element el = getChildWithName(seg, sPart + " Side");
        if (el != null)
        {
            double width = getAttrNumValue(el, "width", "m");
			if (!mainTrack && !editorFrame.getGameType().widthSetsBothSideWidths())
			{
				// TORCS: the start width is "start width" (or else the previous end width),
				// the end width is "end width", or else "width", or else the start width
				part.setSideStartWidth(getAttrNumValue(el, "start width", "m"));
				double endWidth = getAttrNumValue(el, "end width", "m");
				if (Double.isNaN(endWidth) && !Double.isNaN(width))
				{
					endWidth = width;
					part.setEndWidthAsWidth(true);
				}
				part.setSideEndWidth(endWidth);
			}
			else if (Double.isNaN(width))
            {
                part.setSideStartWidth(getAttrNumValue(el, "start width", "m"));
                part.setSideEndWidth(getAttrNumValue(el, "end width", "m"));
            }
            else
            {
                part.setSideStartWidth(width);
                part.setSideEndWidth(width);
            }
            part.setSideSurface(getAttrStrValue(el, "surface"));
			// the games only read the Main Track's banking type, not a segment's
			if (mainTrack)
				part.setSideBankingType(getAttrStrValue(el, "banking type"));
			// (some TORCS tracks have a side "type", but no game reads it: reading it as the
			// banking type would make the game use it once the track is saved)
        }

        el = getChildWithName(seg, sPart + " Border");
        if (el != null)
        {
            part.setBorderWidth(getAttrNumValue(el, "width", "m"));
            part.setBorderHeight(getAttrNumValue(el, "height", "m"));
            part.setBorderSurface(getAttrStrValue(el, "surface"));
            part.setBorderStyle(getAttrStrValue(el, "style"));
        }

        el = getChildWithName(seg, sPart + " Barrier");
        if (el != null)
        {
            part.setBarrierWidth(getAttrNumValue(el, "width", "m"));
            part.setBarrierHeight(getAttrNumValue(el, "height", "m"));
            part.setBarrierSurface(getAttrStrValue(el, "surface"));
            part.setBarrierStyle(getAttrStrValue(el, "style"));
        }
    }

    private synchronized Element getChildWithName(Element element,
            String name)
    {
		// the games' parameter parser keeps every value of a duplicate name, but finds the
		// first one (libs/tgf: GfHashGetStr), so use the first; the others are reported
		// once and not saved
		Element out = null;
		for (Object o : element.getChildren())
		{
			Element child = (Element) o;
			if (name.equals(child.getAttributeValue("name")))
			{
				if (out == null)
					out = child;
				else if (!child.getName().equals("section") && reportedDuplicates.add(child))
					warn(child, "is in its section more than once: like the game, the first one is used, so this one isn't saved.");
			}
		}
		return out;
    }

    public synchronized String getSegmentName(Element element)
    {
        String out = null;

        if (element == null || element.getParent() == null)
        {
            return out;
        }

        String tmp = element.getParentElement().getAttribute("name").getValue();
        if (editorFrame.getTrackData().getHeader().getVersion() == 3 && tmp.equals("segments"))
        {
            out = element.getAttribute("name").getValue();
        }
        else if (tmp.equals("Track Segments"))
        {
            out = element.getAttribute("name").getValue();
        }
        return out;
    }

    public synchronized String getAttrStrValue(Element element,
            String name)
    {
        String out = null;
        Element el = getChildWithName(element, name);

        try
        {
            if (el.getName().equals("attstr"))
            {
                out = el.getAttributeValue("val");
            }
        } catch (NullPointerException e)
        {

        }
        return out;
    }

    /**
     * Problems found while reading numbers: values the game reads differently than they
     * are written, or units it doesn't know. Shown to the user once the track is read.
     */
	// duplicate values already reported
	private final java.util.Set<Element>	reportedDuplicates	=
		java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<Element, Boolean>());

    public List<String> getWarnings()
    {
        return warnings;
    }

    private void warn(Element e, String message)
    {
        String where = e instanceof MyElement ? "line " + ((MyElement) e).getLineNumber() + ": " : "";
        warnings.add(where + "\"" + e.getAttributeValue("name") + "\" " + message);
    }

    /**
     * Reads an attnum value the way the game does. A value that isn't a valid number is
     * read as far as it is one (like the game) and reported.
     */
    private double readNumber(Element e)
    {
        String text = e.getAttributeValue("val");
        boolean[] clean = { false };
        double value = GfParmNumbers.parse(text, clean);

        if (!clean[0])
        {
            warn(e, "has the value \"" + (text == null ? "" : text) + "\", which the game reads as " + format(value) +
                ". It is saved as " + format(value) + ".");
        }

        return value;
    }

    private static String format(double value)
    {
        if (value == Math.rint(value) && !Double.isInfinite(value))
            return String.valueOf((long) value);
        return String.valueOf(value);
    }

    /**
     * Converts a value from the file's unit to the unit the editor keeps it in, through SI
     * like the game. No unit means SI, as it does for the game.
     */
    private double toEditorUnit(Element e, double value, String expectedUnit)
    {
        String unit = e.getAttributeValue("unit");
        List<String> unknown = new java.util.ArrayList<String>();
		double out = GfParmNumbers.convert(value, unit, expectedUnit, editorFrame.getGameType(), unknown);

        for (String u : unknown)
        {
            warn(e, "uses the unit \"" + u + "\", which the game doesn't know, so like the game it is taken as SI.");
        }

        return out;
    }

    /**
     * @return the value in SI, or NaN when there is no such attnum
     */
    public synchronized double getAttrNumValue(Element element, String name)
    {
        return getAttrNumValue(element, name, null);
    }

    /**
     * @return the value and the file's unit, without converting it, or null when there is no such attnum
     */
    public synchronized DoubleValue getAttrNumUnitsValue(Element element, String name)
    {
        Element e = getChildWithName(element, name);
        if (e != null && e.getName().equals("attnum"))
        {
            double out = readNumber(e);

            if (!Double.isNaN(out))
            {
                return new DoubleValue(out, e.getAttributeValue("unit"));
            }
        }

        return null;
    }

    /**
     * @return the value converted to expectedUnit (SI when null), or NaN when there is no such attnum
     */
    public synchronized double getAttrNumValue(Element element, String name, String expectedUnit)
    {
        double out = Double.NaN;
        Element e = getChildWithName(element, name);
        if (e != null && e.getName().equals("attnum"))
        {
            out = readNumber(e);

            if (!Double.isNaN(out))
            {
                out = toEditorUnit(e, out, expectedUnit);
            }
        }
        return out;
    }

    public synchronized int getAttrIntValue(Element element, String name)
    {
    	int out = Integer.MAX_VALUE;
    	Element e = getChildWithName(element, name);
		if (e != null && e.getName().equals("attnum"))
    	{
			// the game reads every attnum as a number (decimal, or hexadecimal after "0x",
			// so "010" is 10, not octal 8), converts its unit and the code casts it
			double value = toEditorUnit(e, readNumber(e), null);

			if (Double.isNaN(value) || Double.isInfinite(value))
    		{
				out = 0;
    		}
			else
    		{
				if (Math.floor(value) != value)
    			{
					warn(e, "should be a whole number; the game uses " + (int) value + ".");
    			}
				out = (int) value;
    		}
    	}
    	return out;
    }
}
