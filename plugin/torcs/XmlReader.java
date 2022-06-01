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
    private static List segments;

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
        Element header = getChildWithName(root, "Header");
        setHeader(header);
        Element graphic = getChildWithName(root, "Graphic");
        setGraphic(graphic);
        Element mainTrack = getChildWithName(root, "Main Track");
        Editor.getProperties().setTrackWidth(
                getAttrNumValue(mainTrack, "width"));
        Editor.getProperties().setSurface(
                getAttrStrValue(mainTrack, "surface"));
        Editor.getProperties().setProfileStepLength(
                getAttrNumValue(mainTrack, "profil steps length"));
        setSide(mainTrack, Editor.getProperties().getLeft(), "Left");
        setSide(mainTrack, Editor.getProperties().getRight(), "Right");
        setPits(mainTrack);
        segments = getChildWithName(mainTrack, "Track Segments").getChildren();
        setSegments();
    }

    /**
     * @param header
     */
    private static void setHeader(Element header)
    {
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
     *
     */
    private static void setGraphic(Element graphic)
    {
        Element terrain = getChildWithName(graphic, "Terrain Generation");

        double val = getAttrNumValue(terrain, "track step");
        Editor.getProperties().setTerrainTrackStep(val);

        val = getAttrNumValue(terrain, "border margin");
        Editor.getProperties().setTerrainBorderMargin(val);

        val = getAttrNumValue(terrain, "border step");
        Editor.getProperties().setTerrainBorderStep(val);

        val = getAttrNumValue(terrain, "border height");
        Editor.getProperties().setTerrainBorderHeight(val);

        String orientation = getAttrStrValue(terrain, "orientation");
        Editor.getProperties().setTerrainOrientation(orientation);
    }

    /**
     *  
     */
    private static void setPits(Element track)
    {
        Element pits = getChildWithName(track, "Pits");
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
    }

    private synchronized static void setSegments()
    {
        Vector trackData = new Vector();
        Iterator it;
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
        List all = element.getChildren();
        Iterator it;
        it = all.iterator();

        while (it.hasNext()
                && !((Element) it.next()).getAttribute("name").getValue()
                        .equals(name))
        {
            count++;
        }
        if (count < all.size())
        {
            out = (Element) all.get(count);
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
    public List getSegments()
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