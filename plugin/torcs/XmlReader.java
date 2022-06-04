/*
 *   XmlReader.java
 *   Created on 24 ��� 2005
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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import utils.Editor;
import utils.TrackData;
import utils.circuit.Camera;
import utils.circuit.Curve;
import utils.circuit.EnvironmentMapping;
import utils.circuit.Graphic;
import utils.circuit.LocalInfo;
import utils.circuit.Segment;
import utils.circuit.SegmentSide;
import utils.circuit.StartingGrid;
import utils.circuit.Straight;
import utils.circuit.Surface;
import utils.circuit.TerrainGeneration;
import utils.circuit.TrackLight;
import utils.circuit.TrackObject;
import utils.circuit.ObjectMap;

/**
 * @author Charalampos Alexopoulos
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class XmlReader
{
    //private static Properties properties = Properties.getInstance();
    private static List<Element> segments;

    public static void readXml(String filename)
    {
        Document doc;
        try
        {
            doc = readFromFile(filename);
            Element element = doc.getRootElement();
            setTrackData(element);
        } catch (JDOMException e)
        {
            e.printStackTrace();
        }
    }

    private static synchronized void setTrackData(Element root)
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
    }

    private static synchronized void setMainTrack(Element root)
    {
        Element mainTrack = getChildWithName(root, "Main Track");

        if (mainTrack == null)
            return;

        Editor.getProperties().setTrackWidth(getAttrNumValue(mainTrack, "width"));
        Editor.getProperties().setSurface(getAttrStrValue(mainTrack, "surface"));
        Editor.getProperties().setProfileStepLength(getAttrNumValue(mainTrack, "profil steps length"));
        setSide(mainTrack, Editor.getProperties().getLeft(), "Left");
        setSide(mainTrack, Editor.getProperties().getRight(), "Right");
        setPits(mainTrack);
        setSegments(mainTrack);
    }

    /**
     * @param header
     */
    private static void setHeader(Element root)
    {
        Element header = getChildWithName(root, "Header");

        if (header == null)
            return;

        Editor.getProperties().setAuthor(getAttrStrValue(header, "author"));
        Editor.getProperties().setDescription(getAttrStrValue(header, "description"));
        Editor.getProperties().setCategory(getAttrStrValue(header, "category"));
        Editor.getProperties().setTrackVersion(getAttrIntValue(header, "version"));
    }

    /**
     * @param root
     */
    private static void setCameras(Element root)
    {
        Element cameras = getChildWithName(root, "Cameras");

        if (cameras == null)
            return;

        Vector<Camera> cameraData = new Vector<Camera>();
        List<Element> sections = cameras.getChildren();
        Iterator<Element> it = sections.iterator();
        while (it.hasNext())
        {
            Camera cam = new Camera();

            Element camera = it.next();
            cam.setName(camera.getAttribute("name").getValue());
            cam.setSegment(getAttrStrValue(camera, "segment"));
            cam.setToRight(getAttrNumValue(camera, "to right"));
            cam.setToStart(getAttrNumValue(camera, "to start"));
            cam.setHeight(getAttrNumValue(camera, "height"));
            cam.setFovStart(getAttrStrValue(camera, "fov start"));
            cam.setFovEnd(getAttrStrValue(camera, "fov end"));

            cameraData.add(cam);
        }
        Editor.getProperties().setCameras(cameraData);
    }

    /**
     * @param root
     */
    private static void setTrackLights(Element root)
    {
        Element lights = getChildWithName(root, "Track Lights");

        if (lights == null)
            return;

        Vector<TrackLight> lightData = new Vector<TrackLight>();
        List<Element> sections = lights.getChildren();
        Iterator<Element> it = sections.iterator();
        while (it.hasNext())
        {
            TrackLight lit = new TrackLight();

            Element light = it.next();
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
        Editor.getProperties().setTrackLights(lightData);
    }

    /**
     * @param root
     */
    private static void setStartingGrid(Element root)
    {
        Element element = getChildWithName(root, "Starting Grid");

        if (element == null)
            return;

        StartingGrid data = new StartingGrid();

        data.setRows(getAttrIntValue(element, "rows"));
        data.setPolePositionSide(getAttrStrValue(element, "pole position side"));
        data.setDistanceToStart(getAttrNumValue(element, "distance to start"));
        data.setDistanceBetweenColumns(getAttrNumValue(element, "distance between columns"));
        data.setOffsetWithinAColumn(getAttrNumValue(element, "offset within a column"));
        data.setInitialHeight(getAttrNumValue(element, "initial height"));

        Editor.getProperties().setStartingGrid(data);
    }

    /**
     * @param root
     */
    private static void setLocalInfo(Element root)
    {
        Element element = getChildWithName(root, "Local Info");

        if (element == null)
            return;

        LocalInfo data = new LocalInfo();

        data.setStation(getAttrStrValue(element, "station"));
        data.setTimezone(getAttrNumValue(element, "timezone"));
        data.setOverallRainLikelyhood(getAttrNumValue(element, "overall rain likelyhood"));
        data.setLittleRainLikelyhood(getAttrNumValue(element, "little rain likelyhood"));
        data.setMediumRainLikelyhood(getAttrNumValue(element, "medium rain likelyhood"));
        data.setTimeOfDay(getAttrNumValue(element, "time of day"));
        data.setSunAscension(getAttrNumValue(element, "sun ascension"));
        data.setAltitude(getAttrNumValue(element, "altitude"));

        Editor.getProperties().setLocalInfo(data);
    }

    /**
     * @param root
     */
    private static void setSurfaces(Element root)
    {
        Element surfaces = getChildWithName(root, "Surfaces");

        if (surfaces == null)
            return;

        Vector<Surface> surfaceData = new Vector<Surface>();
        List<Element> sections = surfaces.getChildren();
        Iterator<Element> it = sections.iterator();
        while (it.hasNext())
        {
            Surface surf = new Surface();

            Element surface = it.next();
            surf.setName(surface.getAttribute("name").getValue());
            surf.setColorR1(getAttrNumValue(surface, "color R1"));
            surf.setColorG1(getAttrNumValue(surface, "color G1"));
            surf.setColorB1(getAttrNumValue(surface, "color B1"));
            surf.setColorR2(getAttrNumValue(surface, "color R2"));
            surf.setColorG2(getAttrNumValue(surface, "color G2"));
            surf.setColorB2(getAttrNumValue(surface, "color B2"));
            surf.setTextureName(getAttrStrValue(surface, "texture name"));
            surf.setTextureSize(getAttrNumValue(surface, "texture size"));
            surf.setTextureType(getAttrStrValue(surface, "texture type"));
            surf.setTextureLinkWithPrevious(getAttrStrValue(surface, "texture link with previous"));
            surf.setTextureStartOnBoundary(getAttrStrValue(surface, "texture start on boundary"));
            surf.setTextureMipMap(getAttrNumValue(surface, "texture mipmap"));
            surf.setFriction(getAttrNumValue(surface, "friction"));
            surf.setRollingResistance(getAttrNumValue(surface, "rolling resistance"));
            surf.setBumpName(getAttrStrValue(surface, "bump name"));
            surf.setBumpSize(getAttrNumValue(surface, "bump size"));
            surf.setRoughness(getAttrNumValue(surface, "roughness"));
            surf.setRoughnessWavelength(getAttrNumValue(surface, "roughness wavelength"));
            surf.setRacelineName(getAttrStrValue(surface, "raceline name"));
            surf.setDammage(getAttrNumValue(surface, "dammage"));
            surf.setRebound(getAttrNumValue(surface, "rebound"));

            surfaceData.add(surf);
        }
        Editor.getProperties().setSurfaces(surfaceData);
    }

    /**
     * @param root
     */
    private static void setObjects(Element root)
    {
        Element objects = getChildWithName(root, "Objects");

        if (objects == null)
            return;

        Vector<TrackObject> objectData = new Vector<TrackObject>();
        List<Element> sections = objects.getChildren();
        Iterator<Element> it = sections.iterator();
        while (it.hasNext())
        {
            TrackObject obj = new TrackObject();

            Element surface = it.next();
            obj.setName(surface.getAttribute("name").getValue());
            obj.setObject(getAttrStrValue(surface, "object"));
            obj.setColor(getAttrIntValue(surface, "color"));
            obj.setOrientationType(getAttrStrValue(surface, "orientation type"));
            obj.setOrientation(getAttrNumValue(surface, "orientation"));
            obj.setDeltaHeight(getAttrNumValue(surface, "delta height"));
            obj.setDeltaVert(getAttrNumValue(surface, "delta vert"));

            objectData.add(obj);
        }
        Editor.getProperties().setObjects(objectData);
    }

    /**
     *
     */
    private static void setGraphic(Element root)
    {
        Element graphic = getChildWithName(root, "Graphic");

        if (graphic == null)
            return;

        Graphic	data = new Graphic();

        data.setDescription(getAttrStrValue(graphic, "3d description"));
        data.setDescriptionNight(getAttrStrValue(graphic, "3d description night"));
        data.setDescriptionRainNight(getAttrStrValue(graphic, "3d description rain+night"));
        data.setBackgroundImage(getAttrStrValue(graphic, "background image"));
        data.setBackgroundType(getAttrIntValue(graphic, "background type"));
        data.setBackgroundColorR(getAttrNumValue(graphic, "background color R"));
        data.setBackgroundColorG(getAttrNumValue(graphic, " background color G"));
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

        Element marks = getChildWithName(graphic, "Turn Marks");

        if (marks != null)
        {
            data.getTurnMarks().setWidth(getAttrNumValue(marks, "width"));
            data.getTurnMarks().setHeight(getAttrNumValue(marks, "height"));
            data.getTurnMarks().setVerticalSpace(getAttrNumValue(marks, "vertical space"));
            data.getTurnMarks().setHorizontalSpace(getAttrNumValue(marks, "horizontal space"));
        }

        Element terrain = getChildWithName(graphic, "Terrain Generation");

        if (terrain != null)
        {
	        data.getTerrainGeneration().setTrackStep(getAttrNumValue(terrain, "track step"));
	        data.getTerrainGeneration().setBorderMargin(getAttrNumValue(terrain, "border margin"));
	        data.getTerrainGeneration().setBorderStep(getAttrNumValue(terrain, "border step"));
	        data.getTerrainGeneration().setBorderHeight(getAttrNumValue(terrain, "border height"));
	        data.getTerrainGeneration().setOrientation(getAttrStrValue(terrain, "orientation"));
	        data.getTerrainGeneration().setMaximumAltitude(getAttrNumValue(terrain, "maximum altitude"));
	        data.getTerrainGeneration().setMinimumAltitude(getAttrNumValue(terrain, "minimum altitude"));
	        data.getTerrainGeneration().setGroupSize(getAttrNumValue(terrain, "group size"));
	        data.getTerrainGeneration().setElevationMap(getAttrStrValue(terrain, "elevation map"));
	        data.getTerrainGeneration().setReliefFile(getAttrStrValue(terrain, "relief file"));
	        data.getTerrainGeneration().setSurface(getAttrStrValue(terrain, "surface"));

	        Element objects = getChildWithName(terrain, "Object Maps");

	        if (objects != null)
	        {
		        Vector<ObjectMap> objMap = new Vector<ObjectMap>();
		        Iterator<Element> it = objects.getChildren().iterator();
		        while (it.hasNext())
		        {
                    ObjectMap obj = new ObjectMap();

		            Element el = it.next();
		            obj.setName(el.getAttribute("name").getValue());

		            obj.setObjectMap(getAttrStrValue(el, "object map"));

		            objMap.add(obj);
		        }
		        data.getTerrainGeneration().setObjectMaps(objMap);
	        }
        }

        Element environment = getChildWithName(graphic, "Environment Mapping");

        if (environment != null)
        {
            Vector<EnvironmentMapping> envMap = new Vector<EnvironmentMapping>();
	        Iterator<Element> it = environment.getChildren().iterator();
	        while (it.hasNext())
	        {
	            EnvironmentMapping env = new EnvironmentMapping();

	            Element el = it.next();
	            env.setName(el.getAttribute("name").getValue());

	            env.setEnvMapImage(getAttrStrValue(el, "env map image"));

	            envMap.add(env);
	        }
	        data.setEnvironmentMapping(envMap);
        }

	    Editor.getProperties().setGraphic(data);
    }

    /**
     *
     */
    private static void setPits(Element track)
    {
        Element pits = getChildWithName(track, "Pits");

        if (pits == null)
            return;

        Editor.getProperties().getPits().setStyle(getAttrIntValue(pits, "style"));
        Editor.getProperties().getPits().setSide(getAttrStrValue(pits, "side"));
        Editor.getProperties().getPits().setEntry(getAttrStrValue(pits, "entry"));
        Editor.getProperties().getPits().setStart(getAttrStrValue(pits, "start"));
        Editor.getProperties().getPits().setStartBuildings(getAttrStrValue(pits, "start buildings"));
        Editor.getProperties().getPits().setStopBuildings(getAttrStrValue(pits, "stop buildings"));
        Editor.getProperties().getPits().setMaxPits(getAttrIntValue(pits, "max pits"));
        Editor.getProperties().getPits().setEnd(getAttrStrValue(pits, "end"));
        Editor.getProperties().getPits().setExit(getAttrStrValue(pits, "exit"));
        Editor.getProperties().getPits().setLength(getAttrNumValue(pits, "length"));
        Editor.getProperties().getPits().setWidth(getAttrNumValue(pits, "width"));
        Editor.getProperties().getPits().setIndicator(getAttrIntValue(pits, "pit indicator"));
        Editor.getProperties().getPits().setSpeedLimit(getAttrNumValue(pits, "speed limit"));
    }

    private synchronized static void setSegments(Element mainTrack)
    {
        segments = getChildWithName(mainTrack, "Track Segments").getChildren();

        Vector<Segment> trackData = new Vector<Segment>();
        Iterator<Element> it;
        Segment prev = null;
        Segment shape = null;

        it = segments.iterator();
        while (it.hasNext())
        {
            Element e = it.next();
            String type = getAttrStrValue(e, "type");
            if (type.equals("str"))
            {
                shape = new Straight();
            } else
            {
                shape = new Curve(getAttrStrValue(e, "type"), null);
            }
            shape = setSegment(e, shape, prev);
            shape.setProfilStepLength(4);
            try
            {
                shape.calcShape(prev);
            } catch (Exception e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            trackData.add(shape);
            prev = shape;
        }
        TrackData.setTrackData(trackData);
    }

    private synchronized static Segment setSegment(Element seg, Segment shape,
            Segment prev)
    {
        SegmentSide left = shape.getLeft();
        SegmentSide right = shape.getRight();
        if (prev != null)
        {
            SegmentSide prevLeft = prev.getLeft();
            SegmentSide prevRight = prev.getRight();
        }

        if (shape.getType().equals("str"))
        {
            shape.setLength(getAttrNumValue(seg, "lg"));
        } else
        {
            double arc = getAttrNumValue(seg, "arc");
            arc = (arc * Math.PI) / 180;
            ((Curve) shape).setArc(arc);
            double startRad = getAttrNumValue(seg, "radius");
            ((Curve) shape).setRadiusStart(startRad);
            double endRad = getAttrNumValue(seg, "end radius");
            if (Double.isNaN(endRad))
            {
                ((Curve) shape).setRadiusEnd(startRad);
            } else
            {
                ((Curve) shape).setRadiusEnd(endRad);
            }
            ((Curve) shape).setMarks(getAttrStrValue(seg, "marks"));
        }
        String name = getSegmentName(seg);
        shape.setName(name);
        if (name.startsWith("curve "))
        {
            String tmp = name.substring(6);
            try
            {
                Integer tmpInt = new Integer(tmp);
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
                Integer tmpInt = new Integer(tmp);
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

        double z = getAttrNumValue(seg, "z start");
        if (Double.isNaN(z))
        {
            shape.setHeightStartLeft(getAttrNumValue(seg, "z start left"));
            shape.setHeightStartRight(getAttrNumValue(seg, "z start right"));
        }
        else
        {
            shape.setHeightStartLeft(z);
            shape.setHeightStartRight(z);
        }
        z = getAttrNumValue(seg, "z end");
        if (Double.isNaN(z))
        {
            shape.setHeightEndLeft(getAttrNumValue(seg, "z end left"));
            shape.setHeightEndRight(getAttrNumValue(seg, "z end right"));
        }
        else
        {
            shape.setHeightEndLeft(z);
            shape.setHeightEndRight(z);
        }

        shape.setGrade(getAttrNumValue(seg, "grade"));
        shape.setBankingStart(getAttrNumValue(seg, "banking start"));
        shape.setBankingEnd(getAttrNumValue(seg, "banking end"));
        shape.setProfil(getAttrStrValue(seg, "profil"));
        shape.setProfilSteps(getAttrNumValue(seg, "profil steps"));
        shape.setProfilStepsLength(getAttrNumValue(seg, "profil steps length"));
        shape.setProfilStartTangent(getAttrNumValue(seg, "profil start tangent"));
        shape.setProfilEndTangent(getAttrNumValue(seg, "profil end tangent"));
        shape.setProfilStartTangentLeft(getAttrNumValue(seg, "profil start tangent left"));
        shape.setProfilEndTangentLeft(getAttrNumValue(seg, "profil end tangent left"));
        shape.setProfilStartTangentRight(getAttrNumValue(seg, "profil start tangent right"));
        shape.setProfilEndTangentRight(getAttrNumValue(seg, "profil end tangent right"));

        setSide(seg, left, "Left");
        setSide(seg, right, "Right");

        return shape;
    }

    /**
     * @param el
     * @param left
     * @param string
     */
    private synchronized static void setSide(Element seg, SegmentSide part,
            String sPart)
    {
        Element el = getChildWithName(seg, sPart + " Side");
        if (el != null)
        {
            part.setHasSide(true);
            double width = getAttrNumValue(el, "width");
            if (Double.isNaN(width))
            {
                part.setSideStartWidth(getAttrNumValue(el, "start width"));
                part.setSideEndWidth(getAttrNumValue(el, "end width"));
            }
            else
            {
                part.setSideStartWidth(width);
                part.setSideEndWidth(width);
            }
            part.setSideSurface(getAttrStrValue(el, "surface"));
            part.setSideBankingType(getAttrStrValue(el, "banking type"));
        }
        else
            part.setHasSide(false);

        el = getChildWithName(seg, sPart + " Border");
        if (el != null)
        {
            part.setHasBorder(true);
            part.setBorderWidth(getAttrNumValue(el, "width"));
            part.setBorderHeight(getAttrNumValue(el, "height"));
            part.setBorderSurface(getAttrStrValue(el, "surface"));
            part.setBorderStyle(getAttrStrValue(el, "style"));
        }
        else
            part.setHasBorder(false);

        el = getChildWithName(seg, sPart + " Barrier");
        if (el != null)
        {
            part.setHasBarrier(true);
            part.setBarrierWidth(getAttrNumValue(el, "width"));
            part.setBarrierHeight(getAttrNumValue(el, "height"));
            part.setBarrierSurface(getAttrStrValue(el, "surface"));
            part.setBarrierStyle(getAttrStrValue(el, "style"));
        }
        else
            part.setHasBarrier(false);
    }

    private synchronized static Element getChildWithName(Element element,
            String name)
    {
        Element out = null;
        int count = 0;
        List<Element> all = element.getChildren();
        Iterator<Element> it;
        it = all.iterator();

        while (it.hasNext()
                && !it.next().getAttribute("name").getValue()
                        .equals(name))
        {
            count++;
        }
        if (count < all.size())
        {
            out = all.get(count);
        }
        return out;
    }

    public synchronized static String getSegmentName(Element element)
    {
        String out = null;

        if (element == null || element.getParent() == null)
        {
            return out;
        }

        String tmp = element.getParentElement().getAttribute("name").getValue();
        if (tmp.equals("Track Segments"))
        {
            out = element.getAttribute("name").getValue();
        }
        return out;
    }

    public synchronized static String getAttrStrValue(Element element,
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

    public synchronized static double getAttrNumValue(Element element,
            String name)
    {
        double out = Double.NaN;
        Element e = getChildWithName(element, name);
        if (e != null)
        {
            if (e.getName().equals("attnum"))
            {
                out = Double.parseDouble(e.getAttributeValue("val"));
            }
        }
        return out;
    }

    public synchronized static int getAttrIntValue(Element element,
            String name)
    {
        int out = Integer.MAX_VALUE;
        Element e = getChildWithName(element, name);
        if (e != null)
        {
            if (e.getName().equals("attnum"))
            {
                out = Integer.decode(e.getAttributeValue("val"));
            }
        }
        return out;
    }

    /**
     * @return Returns the segments.
     */
    public List<Element> getSegments()
    {
        return segments;
    }

    private static Document readFromFile(String fname) throws JDOMException
    {
        Document d = null;
        SAXBuilder sxb = new SAXBuilder(false);

        try
        {
            sxb.setEntityResolver(new NoOpEntityResolver());
            //sxb.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
            // false);
            d = sxb.build(new File(fname));
        } catch (JDOMException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return d;
    }

}