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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
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
import utils.TrackData;
import utils.ac3d.Ac3dException;
import utils.circuit.Camera;
import utils.circuit.Curve;
import utils.circuit.EnvironmentMapping;
import utils.circuit.Graphic;
import utils.circuit.GraphicObject;
import utils.circuit.LocalInfo;
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
	// every element the reader looked at, so the rest can be preserved
	private Set<Element>	visited		= Collections.newSetFromMap(new IdentityHashMap<Element, Boolean>());
	
	public XmlReader(EditorFrame editorFrame)
	{
		this.editorFrame = editorFrame;
	}
	
    public void readXml(String filename) throws JDOMException, IOException
    {
    	this.filename = filename;
    	
        Document doc = readFromFile(filename);
        Element element = doc.getRootElement();
        visited.clear();
        setTrackData(element);
        editorFrame.getTrackData().setPreservedElements(collectUnvisited(element));
    }

    private Document readFromFile(String fname) throws JDOMException, IOException
    {
        Document d = null;
        MySAXBuilder sxb = new MySAXBuilder(false);

        sxb.setEntityResolver(new NoOpEntityResolver());
        d = sxb.build(new File(fname));
        return d;
    }

    public void readDefaultSurfaces(String filename, Vector<Surface> surfaceData) throws JDOMException, IOException
    {
    	String xml =
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
    		"<!DOCTYPE params SYSTEM \"../../../src/libs/tgf/params.dtd\" [" +
    		"<!ENTITY default-surfaces SYSTEM \"" + filename + "\">" +
    		"]>" +
    		"<params name=\"test\" type=\"param\" mode=\"mw\">" +
    		"<section name=\"Surfaces\">" +
    		"&default-surfaces;" +
    		"</section>" +
    		"</params>";
    	SAXBuilder sxb = new SAXBuilder(false);
    	sxb.setValidation(false);
    	sxb.setFeature("http://xml.org/sax/features/validation", false);
    	sxb.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
    	sxb.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);    	
    	Document doc = sxb.build(new InputSource(new StringReader(xml)));
    	Element root = doc.getRootElement();
    	Element surfaces = getChildWithName(root, "Surfaces");

    	if (surfaces != null)
    	{
    		getSurfaces(surfaces, surfaceData);
    	}
    }

    public void readDefaultObjects(String filename, Vector<TrackObject> objectData) throws JDOMException, IOException
    {
    	String xml =
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
    		"<!DOCTYPE params SYSTEM \"../../../src/libs/tgf/params.dtd\" [" +
    		"<!ENTITY default-objects SYSTEM \"" + filename + "\">" +
    		"]>" +
    		"<params name=\"test\" type=\"param\" mode=\"mw\">" +
    		"<section name=\"Objects\">" +
    		"&default-objects;" +
    		"</section>" +
    		"</params>";
    	SAXBuilder sxb = new SAXBuilder(false);
    	sxb.setValidation(false);
    	sxb.setFeature("http://xml.org/sax/features/validation", false);
    	sxb.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
    	sxb.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);    	
    	Document doc = sxb.build(new InputSource(new StringReader(xml)));
    	Element root = doc.getRootElement();
    	Element surfaces = getChildWithName(root, "Objects");

    	if (surfaces != null)
    	{
    		getObjects(surfaces, objectData);
    	}
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
        editorFrame.getTrackData().calculateSegmentValues();
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

        if (editorFrame.getTrackData().getHeader().getVersion() == 3)
        {
            setSideV3(mainTrack, editorFrame.getTrackData().getMainTrack().getLeft(), "l");
            setSideV3(mainTrack, editorFrame.getTrackData().getMainTrack().getRight(), "r");
            setPitsV3(mainTrack);
        }
        else
        {
            setSide(mainTrack, editorFrame.getTrackData().getMainTrack().getLeft(), "Left");
            setSide(mainTrack, editorFrame.getTrackData().getMainTrack().getRight(), "Right");
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

    private void setSideV3(Element seg, SegmentSide part, String sPart)
    {
        double width = getAttrNumValue(seg, sPart + "side width", "m");
        if (Double.isNaN(width))
        {
            part.setSideStartWidth(getAttrNumValue(seg, sPart + "side start width", "m"));
            part.setSideEndWidth(getAttrNumValue(seg, sPart + "side end width", "m"));
        }
        else
        {
            part.setSideStartWidth(width);
            part.setSideEndWidth(width);
        }
        part.setSideSurface(getAttrStrValue(seg, sPart + "side surface"));
        part.setSideBankingType(getAttrStrValue(seg, sPart + "side type"));
        part.setBorderStyle(getAttrStrValue(seg, sPart + "border style"));
        part.setBorderWidth(getAttrNumValue(seg, sPart + "border width", "m"));
        part.setBorderHeight(getAttrNumValue(seg, sPart + "border height", "m"));
        part.setBorderSurface(getAttrStrValue(seg, sPart + "border surface"));
        part.setBarrierStyle(getAttrStrValue(seg, sPart + "barrier style"));
        part.setBarrierWidth(getAttrNumValue(seg, sPart + "barrier width", "m"));
        part.setBarrierHeight(getAttrNumValue(seg, sPart + "barrier height", "m"));
        part.setBarrierSurface(getAttrStrValue(seg, sPart + "barrier surface"));
    }

    private synchronized void setSegments(Element mainTrack)
    {
    	List<?> segments;
    	
        if (editorFrame.getTrackData().getHeader().getVersion() == 3)
            segments = getChildWithName(mainTrack, "segments").getChildren();
        else
            segments = getChildWithName(mainTrack, "Track Segments").getChildren();

        if (segments == null)
            return;

        SegmentVector trackData = new SegmentVector();
        Iterator<?> it;
        Segment prev = null;
        Segment shape = null;

        it = segments.iterator();
        while (it.hasNext())
        {
            Element e = (Element) it.next();
            String type = getAttrStrValue(e, "type");
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
            setSideV3(seg, left, "l");
            setSideV3(seg, right, "r");
        }
        else
        {
        	setSide(seg, left, "Left");
        	setSide(seg, right, "Right");
        }

        return shape;
    }

    /**
     * @param el
     * @param left
     * @param string
     */
    private synchronized void setSide(Element seg, SegmentSide part,
            String sPart)
    {
        Element el = getChildWithName(seg, sPart + " Side");
        if (el != null)
        {
            double width = getAttrNumValue(el, "width", "m");
            if (Double.isNaN(width))
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
            part.setSideBankingType(getAttrStrValue(el, "banking type"));
            // try alternate representation used by some TORCS tracks
            if (part.getSideBankingType() == null)
            {
                part.setSideBankingType(getAttrStrValue(el, "type"));            	
            }
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
        // Speed Dreams' parameter parser keeps the last of duplicate names, so do the same.
        Element out = null;
        for (Object o : element.getChildren())
        {
            Element child = (Element) o;
            if (name.equals(child.getAttributeValue("name")))
            {
                out = child;
            }
        }
        if (out != null)
        {
            visited.add(out);
        }
        visited.add(element);
        return out;
    }

    /**
     * Collects the elements the reader never looked at, below sections it did
     * read, so XmlWriter can write them back unchanged.
     */
    private Vector<TrackData.PreservedElement> collectUnvisited(Element root)
    {
        Vector<TrackData.PreservedElement> result = new Vector<TrackData.PreservedElement>();
        collectUnvisited(root, new ArrayList<String>(), result);
        return result;
    }

    private void collectUnvisited(Element section, List<String> path, Vector<TrackData.PreservedElement> result)
    {
        for (Object o : section.getChildren())
        {
            Element child = (Element) o;
            String name = child.getAttributeValue("name");
            if (name == null)
                continue;

            if (!visited.contains(child))
            {
                result.add(new TrackData.PreservedElement(path.toArray(new String[path.size()]), (Element) child.clone()));
            }
            else if (child.getName().equals("section"))
            {
                path.add(name);
                collectUnvisited(child, path, result);
                path.remove(path.size() - 1);
            }
        }
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

    public synchronized double getAttrNumValue(Element element, String name)
    {
        double out = Double.NaN;
        Element e = getChildWithName(element, name);
        if (e != null)
        {
            if (e.getName().equals("attnum"))
            {
                try
                {
                    out = Double.parseDouble(e.getAttributeValue("val"));
                }
                catch (NumberFormatException exception)
                {
                	String msg = filename + " : " + ((MyElement)e).getLineNumber() + " : " + e.getAttribute("name").getValue() + " : " + e.getAttributeValue("val");
                	JOptionPane.showMessageDialog(editorFrame, msg, "Invalid number", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        return out;
    }

    public synchronized DoubleValue getAttrNumUnitsValue(Element element, String name)
    {
        double out = Double.NaN;
        Element e = getChildWithName(element, name);
        if (e != null)
        {
            if (e.getName().equals("attnum"))
            {
                try
                {
                    out = Double.parseDouble(e.getAttributeValue("val"));
                }
                catch (NumberFormatException exception)
                {
                	String msg = filename + " : " + ((MyElement)e).getLineNumber() + " : " + e.getAttribute("name").getValue() + " : " + e.getAttributeValue("val");
                	JOptionPane.showMessageDialog(editorFrame, msg, "Invalid number", JOptionPane.ERROR_MESSAGE);
                }
                
                if (!Double.isNaN(out))
                {
                	return new DoubleValue(out, e.getAttributeValue("unit"));
                }
            }
        }

        return null;
    }

    public synchronized double getAttrNumValue(Element element, String name, String expectedUnit)
    {
        double out = Double.NaN;
        Element e = getChildWithName(element, name);
        if (e != null)
        {
            if (e.getName().equals("attnum"))
            {
                try
                {
                    out = Double.parseDouble(e.getAttributeValue("val"));
                }
                catch (NumberFormatException exception)
                {
                	String msg = filename + " : " + ((MyElement)e).getLineNumber() + " : " + e.getAttribute("name").getValue() + " : " + e.getAttributeValue("val");
                	JOptionPane.showMessageDialog(editorFrame, msg, "Invalid number", JOptionPane.ERROR_MESSAGE);
                }
                
                if (!Double.isNaN(out))
                {               
                	String actualUnit = e.getAttributeValue("unit");
               
                	if (actualUnit != null && !actualUnit.isEmpty())
                	{
                		if (!actualUnit.equals(expectedUnit))
                		{
                			if (expectedUnit.equals("m") && (actualUnit.equals("ft") || actualUnit.equals("feet")))
                			{
                				out = out * 0.304801;
                			}
                			else if (expectedUnit.equals("m") && (actualUnit.equals("in") || actualUnit.equals("inch") || actualUnit.equals("inches")))
                			{
                				out = out * 0.0254;
                			}
                			else if (expectedUnit.equals("m") && actualUnit.equals("cm"))
                			{
                				out = out * 0.01;
                			}
                			else if (expectedUnit.equals("m") && actualUnit.equals("mm"))
                			{
                				out = out * 0.001;
                			}
                			else if (expectedUnit.equals("rad") && actualUnit.equals("deg"))
                			{
                				out = Math.toRadians(out);
                			}
                			else if (expectedUnit.equals("s") && (actualUnit.equals("hour") || actualUnit.equals("hours")))
                			{
                				out = out * 3600.0;
                			}
                			else if (expectedUnit.equalsIgnoreCase("mph") && actualUnit.equals("m"))
                	    	{
                	    		out = out * 0.44704;
                	        }
                			else if (expectedUnit.equalsIgnoreCase("mph") && actualUnit.equalsIgnoreCase("kph"))
                			{
                				out = out * 1.60934;
                			}
                			else if (expectedUnit.equalsIgnoreCase("kph") && actualUnit.equals("m"))
                			{
                	            out = out * 0.277778;
                			}
                			else if (expectedUnit.equalsIgnoreCase("kph") && actualUnit.equalsIgnoreCase("mph"))
                			{
                				out = out * 0.621371;
                			}
                			else
                			{
                				System.out.println("can't convert " + expectedUnit +" to " + actualUnit);
                			}
                		}
                	}
                }
            }
        }
        return out;
    }

    public synchronized int getAttrIntValue(Element element, String name)
    {
    	int out = Integer.MAX_VALUE;
    	Element e = getChildWithName(element, name);
    	if (e != null)
    	{
    		if (e.getName().equals("attnum"))
    		{
    			try
    			{
    				out = Integer.decode(e.getAttributeValue("val"));
    			}
    			catch (NumberFormatException exception1)
    			{
    				try
    				{
    					double value = Double.parseDouble(e.getAttributeValue("val"));

    					if (Math.floor(value) != value)
    					{
    						String msg = filename + " : " + ((MyElement)e).getLineNumber() + " : " + e.getAttribute("name").getValue() + " : " + e.getAttributeValue("val");
    						JOptionPane.showMessageDialog(editorFrame, msg, "Invalid number", JOptionPane.ERROR_MESSAGE);
    					}

    					out = (int) value;
    				}
    				catch (NumberFormatException exception2)
    				{
    					String msg = filename + " : " + ((MyElement)e).getLineNumber() + " : " + e.getAttribute("name").getValue() + " : " + e.getAttributeValue("val");
    					JOptionPane.showMessageDialog(editorFrame, msg, "Invalid number", JOptionPane.ERROR_MESSAGE);
    				}
    			}
    		}
    	}
    	return out;
    }
}
