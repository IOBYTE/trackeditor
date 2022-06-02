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
import utils.circuit.Curve;
import utils.circuit.Segment;
import utils.circuit.SegmentSide;
import utils.circuit.Straight;

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
        setEnvironmentMapping(root);
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

        String tmp = getAttrStrValue(header, "author");
        Editor.getProperties().setAuthor(tmp);
        tmp = getAttrStrValue(header, "description");
        Editor.getProperties().setDescription(tmp);
        tmp = getAttrStrValue(header, "category");
        Editor.getProperties().setCategory(tmp);
        double version = getAttrNumValue(header, "version");
        Editor.getProperties().setTrackVersion((int) version);
    }

    /**
     * @param root
     */
    private static void setCameras(Element root)
    {
        class Camera
        {
            String name;
            String segment;
            double toRight;
            double toStart;
            double height;
            String fovStart;
            String fovEnd;

            void dump()
            {
              System.out.println("name        : "+name);
              System.out.println("  segment   : "+segment);
              System.out.println("  toRight   : "+toRight);
              System.out.println("  toLeft    : "+toStart);
              System.out.println("  height    : "+height);
              System.out.println("  fovStart  : "+fovStart);
              System.out.println("  fovEnd    : "+fovEnd);
            }
        }

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
            cam.name = camera.getAttribute("name").getValue();
            cam.segment = getAttrStrValue(camera, "segment");
            cam.toRight = getAttrNumValue(camera, "to right");
            cam.toStart = getAttrNumValue(camera, "to start");
            cam.height = getAttrNumValue(camera, "height");
            cam.fovStart = getAttrStrValue(camera, "fov start");
            cam.fovEnd = getAttrStrValue(camera, "fov end");

            cameraData.add(cam);

            //cam.dump();
        }
    }

    /**
     * @param root
     */
    private static void setTrackLights(Element root)
    {
        Element lights = getChildWithName(root, "Track Lights");

        if (lights == null)
            return;

        // TODO
    }

    /**
     * @param root
     */
    private static void setEnvironmentMapping(Element root)
    {
        Element environment = getChildWithName(root, "Environment Map");

        if (environment == null)
            return;

        // TODO
    }

    /**
     * @param root
     */
    private static void setStartingGrid(Element root)
    {
        Element grid = getChildWithName(root, "Starting Grid");

        if (grid == null)
            return;

        // TODO
    }

    /**
     * @param root
     */
    private static void setLocalInfo(Element root)
    {
        Element local = getChildWithName(root, "Local Info");

        if (local == null)
            return;

        // TODO
    }

    /**
     * @param root
     */
    private static void setSurfaces(Element root)
    {
        Element surfaces = getChildWithName(root, "Surfaces");

        if (surfaces == null)
            return;

        class Surface
        {
            String name;
            double colorR1;
            double colorG1;
            double colorB1;
            double colorR2;
            double colorG2;
            double colorB2;
            String textureName;
            String textureType;
            double textureSize;
            String textureLinkWithPrevious;
            String textureStartOnBoundary;
            double textureMipMap;	// TODO int
            double friction;
            double rollingResistance;
            String bumpName;
            double bumpSize;
            double roughness;
            double roughnessWavelength;
            String racelineName;
            double dammage;
            double rebound;

            void dump()
            {
              System.out.println("name                      : "+name);
              System.out.println("  colorR1                 : "+colorR1);
              System.out.println("  colorG1                 : "+colorG1);
              System.out.println("  colorB1                 : "+colorB1);
              System.out.println("  colorR2                 : "+colorR2);
              System.out.println("  colorG2                 : "+colorG2);
              System.out.println("  colorB2                 : "+colorB2);
              System.out.println("  textureName             : "+textureName);
              System.out.println("  textureSize             : "+textureSize);
              System.out.println("  textureType             : "+textureType);
              System.out.println("  textureLinkWithPrevious : "+textureLinkWithPrevious);
              System.out.println("  textureStartOnBoundary  : "+textureStartOnBoundary);
              System.out.println("  textureMipMap           : "+textureMipMap);
              System.out.println("  friction                : "+friction);
              System.out.println("  rollingResistance       : "+rollingResistance);
              System.out.println("  bumpName                : "+bumpName);
              System.out.println("  bumpSize                : "+bumpSize);
              System.out.println("  roughness               : "+roughness);
              System.out.println("  roughnessWavelength     : "+roughnessWavelength);
              System.out.println("  racelineName            : "+racelineName);
              System.out.println("  dammage                 : "+dammage);
              System.out.println("  rebound                 : "+rebound);
            }
        }

        Vector<Surface> surfaceData = new Vector<Surface>();
        List<Element> sections = surfaces.getChildren();
        Iterator<Element> it = sections.iterator();
        while (it.hasNext())
        {
            Surface surf = new Surface();

            Element surface = it.next();
            surf.name = surface.getAttribute("name").getValue();
            surf.colorR1 = getAttrNumValue(surface, "color R1");
            surf.colorG1 = getAttrNumValue(surface, "color G1");
            surf.colorB1 = getAttrNumValue(surface, "color B1");
            surf.colorR2 = getAttrNumValue(surface, "color R2");
            surf.colorG2 = getAttrNumValue(surface, "color G2");
            surf.colorB2 = getAttrNumValue(surface, "color B2");
            surf.textureName = getAttrStrValue(surface, "texture name");
            surf.textureSize = getAttrNumValue(surface, "texture size");
            surf.textureType = getAttrStrValue(surface, "texture type");
            surf.textureLinkWithPrevious = getAttrStrValue(surface, "texture link with previous");
            surf.textureStartOnBoundary = getAttrStrValue(surface, "texture start on boundary");
            surf.textureMipMap = getAttrNumValue(surface, "texture mipmap");
            surf.friction = getAttrNumValue(surface, "friction");
            surf.rollingResistance = getAttrNumValue(surface, "rolling resistance");
            surf.bumpName = getAttrStrValue(surface, "bump name");
            surf.bumpSize = getAttrNumValue(surface, "bump size");
            surf.roughness = getAttrNumValue(surface, "roughness");
            surf.roughnessWavelength = getAttrNumValue(surface, "roughness wavelength");
            surf.racelineName = getAttrStrValue(surface, "raceline name");
            surf.dammage = getAttrNumValue(surface, "dammage");
            surf.rebound = getAttrNumValue(surface, "rebound");

            surfaceData.add(surf);

            //surf.dump();
        }
    }

    /**
     * @param root
     */
    private static void setObjects(Element root)
    {
        Element objects = getChildWithName(root, "Objects");

        if (objects == null)
            return;

        // TODO
    }

    /**
     *
     */
    private static void setGraphic(Element root)
    {
        Element graphic = getChildWithName(root, "Graphic");

        if (graphic == null)
            return;

        double val;
        String str;
        Element marks = getChildWithName(graphic, "Turn Marks");

        if (marks != null)
        {
            val = getAttrNumValue(marks, "width");
            Editor.getProperties().setTurnMarksWidth(val);

            val = getAttrNumValue(marks, "height");
            Editor.getProperties().setTurnMarksHeight(val);

            val = getAttrNumValue(marks, "vertical space");
            Editor.getProperties().setTurnMarksVerticalSpace(val);

            val = getAttrNumValue(marks, "horizontal space");
            Editor.getProperties().setTurnMarksHorizontalSpace(val);
        }

        Element terrain = getChildWithName(graphic, "Terrain Generation");

        if (terrain != null)
        {
	        val = getAttrNumValue(terrain, "track step");
	        Editor.getProperties().setTerrainTrackStep(val);

	        val = getAttrNumValue(terrain, "border margin");
	        Editor.getProperties().setTerrainBorderMargin(val);

	        val = getAttrNumValue(terrain, "border step");
	        Editor.getProperties().setTerrainBorderStep(val);

	        val = getAttrNumValue(terrain, "border height");
	        Editor.getProperties().setTerrainBorderHeight(val);

	        str = getAttrStrValue(terrain, "orientation");
	        Editor.getProperties().setTerrainOrientation(str);

	        val = getAttrNumValue(terrain, "maximum altitude");
	        Editor.getProperties().setTerrainMaximumAltitude(val);

	        val = getAttrNumValue(terrain, "minimum altitude");
	        Editor.getProperties().setTerrainMinimumAltitude(val);

	        val = getAttrNumValue(terrain, "group size");
	        Editor.getProperties().setTerrainGroupSize(val);

	        str = getAttrStrValue(terrain, "elevation map");
	        Editor.getProperties().setTerrainElevationMap(str);

	        str = getAttrStrValue(terrain, "relief file");
	        Editor.getProperties().setTerrainReliefFile(str);

	        str = getAttrStrValue(terrain, "surface");
	        Editor.getProperties().setTerrainSurface(str);
        }
    }

    /**
     *  
     */
    private static void setPits(Element track)
    {
        Element pits = getChildWithName(track, "Pits");

        if (pits == null)
            return;

        double style = getAttrNumValue(pits, "style");
        if (!Double.isNaN(style))
            Editor.getProperties().setPitStyle((int) style);
        else
            Editor.getProperties().setPitStyle(1);

        String tmp = getAttrStrValue(pits, "side");
        Editor.getProperties().setPitSide(tmp);

        tmp = getAttrStrValue(pits, "entry");
        Editor.getProperties().setPitEntry(tmp);

        tmp = getAttrStrValue(pits, "start");
        Editor.getProperties().setPitStart(tmp);

        tmp = getAttrStrValue(pits, "start buildings");
        Editor.getProperties().setPitStartBuildings(tmp);

        tmp = getAttrStrValue(pits, "stop buildings");
        Editor.getProperties().setPitStopBuildings(tmp);

        double maxPits = getAttrNumValue(pits, "max pits");
        if (!Double.isNaN(maxPits) && maxPits == Math.floor(maxPits) && maxPits > 0)
            Editor.getProperties().setPitMaxPits((int) maxPits);

        tmp = getAttrStrValue(pits, "end");
        Editor.getProperties().setPitEnd(tmp);

        tmp = getAttrStrValue(pits, "exit");
        Editor.getProperties().setPitExit(tmp);

        double val = getAttrNumValue(pits, "length");
        Editor.getProperties().setPitLength(val);

        val = getAttrNumValue(pits, "width");
        Editor.getProperties().setPitWidth(val);

        val = getAttrNumValue(pits, "pit indicator");
        if (!Double.isNaN(val) && val >= 0)
            Editor.getProperties().setPitIndicator((int)val);
        else
            Editor.getProperties().setPitIndicator(-1);

        val = getAttrNumValue(pits, "speed limit");
        Editor.getProperties().setPitSpeedLimit(val);
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