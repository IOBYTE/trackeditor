/*
 *   XmlWriter.java
 *   Created on 14 ��� 2005
 *
 *    The XmlWriter.java is part of TrackEditor-0.6.0.
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.ProcessingInstruction;
import org.jdom.output.Format;

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
public class XmlWriter
{
	//private static Properties	properties	= Properties.getInstance();
	static Document					doc;
	private boolean				optimize	= true;
	static boolean job;
	private static String sep = System.getProperty("file.separator");
	
	public static void writeXml()
	{
		job = false;
		getXml();
		writeToFile();
	}

	/**
	 * @param segments
	 * @return
	 */
	private static void getXml()
	{
		Comment com;
		Element root = getRoot();

		DocType type = new DocType("params", "../../../src/libs/tgf/params.dtd");
		String entity = "<!--  general definitions for tracks  -->\n";
		entity += "<!ENTITY default-surfaces SYSTEM \"../../../data/tracks/surfaces.xml\">\n";
		entity += "<!ENTITY default-objects SYSTEM \"../../../data/tracks/objects.xml\">\n";
		type.setInternalSubset(entity);
		doc = new Document();
		com = new Comment(getCredit());
		doc.addContent(com);
		com = new Comment(getLicence());
		doc.addContent(com);
		doc.addContent(type);
		doc.setRootElement(root);
		root.addContent(getSurfaces());
		root.addContent(getObjects());
		root.addContent(getHeader());
		root.addContent(getGraphic());
		root.addContent(getTrack());

//		try
//		{
//			XMLOutput outputter = new XMLOutput(Format.getPrettyFormat());
//			outputter.output(doc, System.out);
//		} catch (java.io.IOException e)
//		{
//			e.printStackTrace();
//		}
	}
	/**
	 * @return
	 */
	private synchronized static Element getRoot()
	{
		Attribute name = new Attribute("name", "test");
		Attribute val = new Attribute("type", "param");
		Attribute mode = new Attribute("mode", "mw");

		Element root = new Element("params");
		root.setAttribute(name);
		root.setAttribute(val);
		root.setAttribute(mode);

		return root;
	}

	/**
	 * @return
	 */
	private synchronized static Element getTrack()
	{
		Attribute name = new Attribute("name", "Main Track");
		Element track = new Element("section");
		Comment com = null;
		track.setAttribute(name);
		Element el = attnumElement("width", "m", Editor.getProperties().getTrackWidth() + "");
		track.addContent(el);
		el = attnumElement("profil steps length", "m", Editor.getProperties().getProfileStepLength() + "");
		track.addContent(el);
		el = attstrElement("surface", Editor.getProperties().getSurface());
		track.addContent(el);

		com = new Comment("Left part of track");
		track.addContent(com);
		if (Editor.getProperties().getLeft().getHasSide())
			track.addContent(getSide(Editor.getProperties().getLeft(), "Left"));
		if (Editor.getProperties().getLeft().getHasBorder())
			track.addContent(getBorder(Editor.getProperties().getLeft(), "Left"));
		if (Editor.getProperties().getLeft().getHasBarrier())
			track.addContent(getBarrier(Editor.getProperties().getLeft(), "Left"));
		com = new Comment("End of left part");
		track.addContent(com);
		com = new Comment("Right part of track");
		track.addContent(com);
		if (Editor.getProperties().getRight().getHasSide())
			track.addContent(getSide(Editor.getProperties().getRight(), "Right"));
		if (Editor.getProperties().getRight().getHasBorder())
			track.addContent(getBorder(Editor.getProperties().getRight(), "Right"));
		if (Editor.getProperties().getRight().getHasBarrier())
			track.addContent(getBarrier(Editor.getProperties().getRight(), "Right"));
		com = new Comment("End of right part");
		track.addContent(com);

		track.addContent(getPits());
		track.addContent(getSegments());

		return track;
	}

	/**
	 * @return
	 */
	private synchronized static Element getPits()
	{
		Attribute name = new Attribute("name", "Pits");
		String tmp = "";

		Element pits = new Element("section");
		pits.setAttribute(name);

		int style = Editor.getProperties().getPitStyle();
		if (style != 1)
		{
			Element el = attnumElement("type", null,  "" + style);
			pits.addContent(el);
		}
		if (Editor.getProperties().getPitSide() != null)
		{
			tmp = Editor.getProperties().getPitSide();
		} else
		{
			tmp = "right";
		}
		Element el = attstrElement("side", tmp);
		pits.addContent(el);

		if (Editor.getProperties().getPitEntry() != null)
		{
			tmp = Editor.getProperties().getPitEntry();
		} else
		{
			tmp = "";
		}
		el = attstrElement("entry", tmp);
		pits.addContent(el);

		if (Editor.getProperties().getPitStart() != null)
		{
			tmp = Editor.getProperties().getPitStart();
		} else
		{
			tmp = "";
		}
		el = attstrElement("start", tmp);
		pits.addContent(el);

		if (Editor.getProperties().getPitStartBuildings() != null &&
			!Editor.getProperties().getPitStartBuildings().isEmpty())
		{
			tmp = Editor.getProperties().getPitStartBuildings();
			el = attstrElement("start buildings", tmp);
			pits.addContent(el);
		}

		if (Editor.getProperties().getPitStopBuildings() != null &&
			!Editor.getProperties().getPitStopBuildings().isEmpty())
		{
			tmp = Editor.getProperties().getPitStopBuildings();
			el = attstrElement("stop buildings", tmp);
			pits.addContent(el);
		}

		if (Editor.getProperties().getPitMaxPits() > 0)
		{
			el = attnumElement("max pits", null, Editor.getProperties().getPitMaxPits() + "");
			pits.addContent(el);
		}

		if (Editor.getProperties().getPitEnd() != null)
		{
			tmp = Editor.getProperties().getPitEnd();
		} else
		{
			tmp = "";
		}
		el = attstrElement("end", tmp);
		pits.addContent(el);

		if (Editor.getProperties().getPitExit() != null)
		{
			tmp = Editor.getProperties().getPitExit();
		} else
		{
			tmp = "";
		}
		el = attstrElement("exit", tmp);
		pits.addContent(el);

		el = attnumElement("length", "m", Editor.getProperties().getPitLength() + "");
		pits.addContent(el);

		el = attnumElement("width", "m", Editor.getProperties().getPitWidth() + "");
		pits.addContent(el);

		return pits;
	}

	/**
	 * @return
	 */
	private synchronized static Element getSegments()
	{
		Vector segments = TrackData.getTrackData();
		Segment prev = null;
		Attribute name = new Attribute("name", "Track Segments");
		Comment com = null;
		Element trackSegs = new Element("section");
		trackSegs.setAttribute(name);

		for (int i = 0; i < segments.size(); i++)
		{
			Segment shape = (Segment) segments.get(i);
			shape.previousShape = prev;
			com = new Comment("******************************");
			trackSegs.addContent(com);
			com = new Comment("     Segment " + (i + 1) + "                ");
			trackSegs.addContent(com);
			com = new Comment("******************************");
			trackSegs.addContent(com);
			trackSegs.addContent(getSegment(shape));
			prev = shape;
		}

		return trackSegs;
	}

	/**
	 * @param shape
	 * @return
	 */
	private synchronized static Element getSegment(Segment shape)
	{
		Attribute name = null;
		Attribute unit = null;
		Attribute val = null;
		Comment com = null;
		Element el = null;
		Element segment = new Element("section");
		name = new Attribute("name", shape.getName());
		segment.setAttribute(name);
		el = attstrElement("type", shape.getType());
		segment.addContent(el);
		if (shape.getType().equals("str"))
		{
			el = attnumElement("lg", "m", ((Straight) shape).getLength() + "");
			segment.addContent(el);
		} else
		{
			double arc = ((Curve) shape).getArc();
			arc = (arc * 180) / Math.PI;
			el = attnumElement("arc", "deg", arc + "");
			segment.addContent(el);

			double radStart = ((Curve) shape).getRadiusStart();
			el = attnumElement("radius", "m", radStart + "");
			segment.addContent(el);

			double radEnd = ((Curve) shape).getRadiusEnd();
			if (radStart != radEnd)
			{
				el = attnumElement("end radius", "m", radEnd + "");
				segment.addContent(el);
			}
		}
		if (!Double.isNaN(shape.getHeightStartLeft()) &&
			!Double.isNaN(shape.getHeightStartRight()) &&
			shape.getHeightStartLeft() == shape.getHeightStartRight())
		{
			el = attnumElement("z start", "m", shape.getHeightStartLeft() + "");
			segment.addContent(el);
		}
		else
		{
			if (!Double.isNaN(shape.getHeightStartLeft()))
			{
				el = attnumElement("z start left", "m", shape.getHeightStartLeft() + "");
				segment.addContent(el);
			}
			if (!Double.isNaN(shape.getHeightStartRight()))
			{
				el = attnumElement("z start right", "m", shape.getHeightStartRight() + "");
				segment.addContent(el);
			}
		}
		if (!Double.isNaN(shape.getHeightEndLeft()) &&
			!Double.isNaN(shape.getHeightEndRight()) &&
			shape.getHeightEndLeft() == shape.getHeightEndRight())
		{
			el = attnumElement("z end", "m", shape.getHeightEndLeft() + "");
			segment.addContent(el);
		}
		else
		{
			if (!Double.isNaN(shape.getHeightEndLeft()))
			{
				el = attnumElement("z end left", "m", shape.getHeightEndLeft() + "");
				segment.addContent(el);
			}
			if (!Double.isNaN(shape.getHeightEndRight()))
			{
				el = attnumElement("z end right", "m", shape.getHeightEndRight() + "");
				segment.addContent(el);
			}
		}
		if (!Double.isNaN(shape.getGrade()))
		{
			el = attnumElement("grade", "%", shape.getGrade() + "");
			segment.addContent(el);
		}
		if (!Double.isNaN(shape.getBankingStart()))
		{
			el = attnumElement("banking start", "deg", shape.getBankingStart() + "");
			segment.addContent(el);
		}
		if (!Double.isNaN(shape.getBankingEnd()))
		{
			el = attnumElement("banking end", "deg", shape.getBankingEnd() + "");
			segment.addContent(el);
		}
		if (shape.getProfil() != null)
		{
			el = attstrElement("profil", shape.getProfil());
			segment.addContent(el);
		}
		if (!Double.isNaN(shape.getProfilSteps()))
		{
			el = attnumElement("profil steps", "m", shape.getProfilSteps() + "");
			segment.addContent(el);
		}
		if (!Double.isNaN(shape.getProfilStepsLength()))
		{
			el = attnumElement("profil steps length", "m", shape.getProfilStepsLength() + "");
			segment.addContent(el);
		}
		if (!Double.isNaN(shape.getProfilStartTangent()))
		{
			el = attnumElement("profil start tangent", "m", shape.getProfilStartTangent() + "");
			segment.addContent(el);
		}
		if (!Double.isNaN(shape.getProfilEndTangent()))
		{
			el = attnumElement("profil end tangent", "%", shape.getProfilEndTangent() + "");
			segment.addContent(el);
		}
		if (!Double.isNaN(shape.getProfilStartTangentLeft()))
		{
			el = attnumElement("profil start tangent left", "m", shape.getProfilStartTangentLeft() + "");
			segment.addContent(el);
		}
		if (!Double.isNaN(shape.getProfilEndTangentLeft()))
		{
			el = attnumElement("profil end tangent left", "m", shape.getProfilEndTangentLeft() + "");
			segment.addContent(el);
		}
		if (!Double.isNaN(shape.getProfilStartTangentRight()))
		{
			el = attnumElement("profil start tangent right", "m", shape.getProfilStartTangentRight() + "");
			segment.addContent(el);
		}
		if (!Double.isNaN(shape.getProfilEndTangentRight()))
		{
			el = attnumElement("profil end tangent right", "m", shape.getProfilEndTangentRight() + "");
			segment.addContent(el);
		}

		el = attstrElement("surface", shape.getSurface());
		segment.addContent(el);
		com = new Comment("Left part of segment");
		segment.addContent(com);
		if (shape.getLeft().getHasSide())
			segment.addContent(getSide(shape.getLeft(), "Left"));
		if (shape.getLeft().getHasBorder())
			segment.addContent(getBorder(shape.getLeft(), "Left"));
		if (shape.getLeft().getHasBarrier())
			segment.addContent(getBarrier(shape.getLeft(), "Left"));
		com = new Comment("End of left part");
		segment.addContent(com);
		com = new Comment("Right part of segment");
		segment.addContent(com);
		if (shape.getRight().getHasSide())
			segment.addContent(getSide(shape.getRight(), "Right"));
		if (shape.getRight().getHasBorder())
		    segment.addContent(getBorder(shape.getRight(), "Right"));
		if (shape.getRight().getHasBarrier())
		    segment.addContent(getBarrier(shape.getRight(), "Right"));
		com = new Comment("End of right part");
		segment.addContent(com);

		return segment;
	}

	/**
	 * @param left
	 * @param string
	 * @return
	 */
	private synchronized static Element getBorder(SegmentSide part, String sPart)
	{
		Element el = null;
		Element side = new Element("section");
		Attribute name = new Attribute("name", sPart + " Border");
		side.setAttribute(name);
		if (!Double.isNaN(part.getBorderWidth()))
		{
			el = attnumElement("width", "m", part.getBorderWidth() + "");
			side.addContent(el);
		}
		if (!Double.isNaN(part.getBorderHeight()))
		{
			el = attnumElement("height", "m", part.getBorderHeight() + "");
			side.addContent(el);
		}
		if (part.getBorderSurface() != null && !part.getBorderSurface().isEmpty())
		{
			el = attstrElement("surface", part.getBorderSurface());
			side.addContent(el);
		}
		if (part.getBorderStyle() != null && !part.getBorderStyle().isEmpty())
		{
			el = attstrElement("style", part.getBorderStyle());
			side.addContent(el);
		}

		return side;
	}

	/**
	 * @param left
	 * @param string
	 * @return
	 */
	private synchronized static Element getBarrier(SegmentSide part, String sPart)
	{
		Element el = null;
		Element side = new Element("section");
		Attribute name = new Attribute("name", sPart + " Barrier");
		side.setAttribute(name);
		if (!Double.isNaN(part.getBarrierWidth()))
		{
			el = attnumElement("width", "m", part.getBarrierWidth() + "");
			side.addContent(el);
		}
		if (!Double.isNaN(part.getBarrierHeight()))
		{
			el = attnumElement("height", "m", part.getBarrierHeight() + "");
			side.addContent(el);
		}
		if (part.getBarrierSurface() != null && !part.getBarrierSurface().isEmpty())
		{
			el = attstrElement("surface", part.getBarrierSurface());
			side.addContent(el);
		}
		if (part.getBarrierSurface() != null && !part.getBarrierStyle().isEmpty())
		{
			el = attstrElement("style", part.getBarrierStyle());
			side.addContent(el);
		}

		return side;
	}

	/**
	 * @param left
	 * @return
	 */
	private synchronized static Element getSide(SegmentSide part, String sPart)
	{
		Element el = null;
		Element side = new Element("section");
		Attribute name = new Attribute("name", sPart + " Side");
		side.setAttribute(name);
		if (!Double.isNaN(part.getSideStartWidth()) && !Double.isNaN(part.getSideEndWidth()) &&
			part.getSideStartWidth() == part.getSideEndWidth())
		{
			el = attnumElement("width", "m", part.getSideStartWidth() + "");
			side.addContent(el);
		}
		else
		{
			if (!Double.isNaN(part.getSideStartWidth()))
			{
				el = attnumElement("start width", "m", part.getSideStartWidth() + "");
				side.addContent(el);
			}
			if (!Double.isNaN(part.getSideEndWidth()))
			{
				el = attnumElement("end width", "m", part.getSideEndWidth() + "");
				side.addContent(el);
			}
		}
		if (part.getSideSurface() != null && !part.getSideSurface().isEmpty())
		{
			el = attstrElement("surface", part.getSideSurface());
			side.addContent(el);
		}
		if (part.getSideBankingType() != null && !part.getSideBankingType().isEmpty())
		{
			el = attstrElement("banking type", part.getSideBankingType());
			side.addContent(el);
		}

		return side;
	}

	private synchronized static Element getSurfaces()
	{
		Element surfaces = new Element("section");
		Attribute name = new Attribute("name", "Surfaces");
		surfaces.setAttribute(name);
		surfaces.setText("&default-surfaces;");

		return surfaces;
	}

	private synchronized static Element getObjects()
	{
		Element objects = new Element("section");
		Attribute name = new Attribute("name", "Objects");
		objects.setAttribute(name);
		objects.setText("&default-objects;");

		return objects;
	}

	private synchronized static String getCredit()
	{
		String tmp = "\n";
		tmp += "file                : " + Editor.getProperties().getTrackName() + ".xml\n";
		tmp += "auto generated      : by Track Editor\n";
		tmp += "version             : " + Editor.getProperties().version + "\n";
		tmp += "copyright           : (C) 2005 by Charalampos Alexopoulos\n";
		tmp += "email               : \n";

		return tmp;
	}

	private synchronized static String getLicence()
	{
		String tmp = "\n";
		tmp += "This program is free software; you can redistribute it and/or modify it\n";
		tmp += "under the terms of the GNU General Public License as published by\n";
		tmp += "the Free Software Foundation; either version 2 of the License, or\n";
		tmp += "(at your option) any later version.";
		tmp += "\n";

		return tmp;
	}

	private synchronized static Element getHeader()
	{
		Attribute name = new Attribute("name", "Header");
		Attribute val = null;
		String tmp = "";

		Element header = new Element("section");
		header.setAttribute(name);

		Element el = attstrElement("name", Editor.getProperties().getTrackName());
		header.addContent(el);

		if (Editor.getProperties().getCategory() != null)
		{
			tmp = Editor.getProperties().getCategory();
		} else
		{
			tmp = "road";
		}
		el = attstrElement("category", tmp);
		header.addContent(el);

		el = attnumElement("version", null, Editor.getProperties().getTrackVersion() + "");
		header.addContent(el);

		if (Editor.getProperties().getAuthor() != null)
		{
			tmp = Editor.getProperties().getAuthor();
		} else
		{
			tmp = "Anonymous";
		}
		el = attstrElement("author", tmp);
		header.addContent(el);

		if (Editor.getProperties().getDescription() != null)
		{
			tmp = Editor.getProperties().getDescription();
		} else
		{
			tmp = "No description provided";
		}
		el = attstrElement("description", tmp);
		header.addContent(el);

		return header;
	}

	/**
	 * @return
	 */
	private synchronized static Element getGraphic()
	{
		Attribute name = new Attribute("name", "Graphic");

		Element graphic = new Element("section");
		graphic.setAttribute(name);

		Element el = attstrElement("3d description", Editor.getProperties().getTrackName() + ".ac");
		graphic.addContent(el);

		Element terrain = new Element("section");
		name = new Attribute("name", "Terrain Generation");
		terrain.setAttribute(name);
		graphic.addContent(terrain);

		el = attnumElement("track step", "m", "20");
		terrain.addContent(el);

		el = attnumElement("border margin", "m", "50");
		terrain.addContent(el);

		el = attnumElement("border step", "m", "30");
		terrain.addContent(el);

		el = attnumElement("border height", "m", "15");
		terrain.addContent(el);

		el = attstrElement("orientation", "clockwise");
		terrain.addContent(el);

		return graphic;
	}

	private synchronized static Element attstrElement(String attname, String attval)
	{
		Attribute name = null;
		Attribute unit = null;
		Attribute val = null;

		Element el = new Element("attstr");
		name = new Attribute("name", attname);
		if (attval == null)
		{
			attval = "";
		}
		val = new Attribute("val", attval);
		el.setAttribute(name);
		el.setAttribute(val);

		return el;
	}

	private synchronized static Element attnumElement(String attname, String attunit, String attval)
	{
		Attribute name = null;
		Attribute unit = null;
		Attribute val = null;

		Element el = new Element("attnum");
		name = new Attribute("name", attname);
		el.setAttribute(name);
		if (attunit != null)
		{
			unit = new Attribute("unit", attunit);
			el.setAttribute(unit);
		}
		val = new Attribute("val", attval);
		el.setAttribute(val);

		return el;
	}

	private synchronized static void writeToFile()
	{
		String fileName = Editor.getProperties().getPath() + sep + Editor.getProperties().getTrackName() + ".xml";
		try
		{
			FileOutputStream out = new FileOutputStream(fileName);
			XMLOutput op = new XMLOutput(Format.getPrettyFormat());
			op.output(doc, out);
			out.flush();
			out.close();
		} catch (IOException e)
		{
			System.err.println(e);
		}
	}
}