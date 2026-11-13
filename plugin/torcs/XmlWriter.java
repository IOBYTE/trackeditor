/*
 *   XmlWriter.java
 *   Created on 14 ??? 2005
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jdom2.Attribute;
import org.jdom2.Comment;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.support.AbstractXMLOutputProcessor;
import org.jdom2.output.support.FormatStack;
import org.jdom2.util.NamespaceStack;

import utils.DoubleValue;
import utils.GameType;
import utils.Properties;
import utils.TrackEditor;
import utils.SegmentVector;
import utils.check.TrackContext;
import utils.circuit.Camera;
import utils.circuit.Curve;
import utils.circuit.EnvironmentMapping;
import utils.circuit.Georeference;
import utils.circuit.GraphicObject;
import utils.circuit.MainTrack;
import utils.circuit.ObjectMap;
import utils.circuit.Reliefs;
import utils.circuit.Sector;
import utils.circuit.Segment;
import utils.circuit.SegmentSide;
import utils.circuit.Straight;
import utils.circuit.Surface;
import utils.circuit.TerrainGeneration;
import utils.circuit.TrackLight;
import utils.circuit.TrackObject;

/**
 * @author Charalampos Alexopoulos
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class XmlWriter
{
	private TrackContext	trackContext;
	private List<String>	fileErrors		= new ArrayList<String>();
	// false while only making the document (getDocument): the relief file and object
	// maps aren't written then
	private boolean			writeFiles		= true;

	/**
	 * The track xml was written, but relief or object map files it refers to were not.
	 */
	public static class ReferencedFileException extends IOException
	{
		private final List<String>	errors;

		public ReferencedFileException(List<String> errors)
		{
			super(String.join("\n", errors));
			this.errors = errors;
		}

		public List<String> getErrors()
		{
			return errors;
		}
	}

	public XmlWriter(TrackContext trackContext)
	{
		this.trackContext = trackContext;
	}

	public void writeXml(String fileName, String trackName) throws FileNotFoundException, IOException, SecurityException
	{
		Document doc = new Document();

		fileErrors.clear();
		getXml(doc, trackName);
		writeToFile(fileName, doc);

		if (!fileErrors.isEmpty())
			throw new ReferencedFileException(new ArrayList<String>(fileErrors));
	}

	/**
	 * @return the track file the writer would write, without writing it or the files it
	 *         refers to (the relief file and object maps)
	 */
	public Document getDocument(String trackName)
	{
		Document doc = new Document();
		writeFiles = false;
		try
		{
			getXml(doc, trackName);
		}
		finally
		{
			writeFiles = true;
		}
		return doc;
	}

	private void addFileError(String what, String file, Exception e)
	{
		String reason = e.getLocalizedMessage();
		if (reason == null || reason.isEmpty())
			reason = e.getClass().getSimpleName();
		fileErrors.add(what + " " + file + " : " + reason);
	}

	/**
	 * @param segments
	 * @return
	 */
	private synchronized void getXml(Document doc, String trackName)
	{
		Comment com;
		Element root = getRoot(trackName);
		boolean torcs = trackContext.getGameType() == GameType.TORCS;

		DocType type = new DocType("params", "../../../src/libs/tgf/params.dtd");
		String entity = "<!--  general definitions for tracks  -->\n";
		entity += "<!ENTITY default-surfaces SYSTEM \"../../../data/tracks/surfaces.xml\">\n";
		entity += "<!ENTITY default-objects SYSTEM \"../../../data/tracks/objects.xml\">\n";
		type.setInternalSubset(entity);
		com = new Comment(getCredit());
		doc.addContent(com);
		com = new Comment(getLicence());
		doc.addContent(com);
		doc.addContent(type);
		doc.setRootElement(root);
		// TORCS reads none of Track Lights, Local Info and Sectors
		boolean extensions = trackContext.getGameType().hasSpeedDreamsExtensions();
		if (extensions)
			root.addContent(getLights());
		root.addContent(getSurfaces());
		root.addContent(getObjects());
		root.addContent(getHeader());
		// only TORCS reads it; its track manual puts it after the header
		if (torcs && !trackContext.getTrackData().getGeoreference().isEmpty())
			root.addContent(getGeoreference());
		if (extensions)
			root.addContent(getLocal());
		root.addContent(getGraphic());
		if (!torcs)
			root.addContent(getGrid());
		root.addContent(getTrack());
		// TORCS trackgen doesn't like empty cameras
		if (!torcs || !trackContext.getTrackData().getCameras().isEmpty())
			root.addContent(getCameras());
		if (extensions)
			root.addContent(getSectors());
	}

	private synchronized void writeToFile(String fileName, Document doc) throws FileNotFoundException, IOException, SecurityException
	{
		// write a temporary file next to the track and only replace the track
		// once it is complete, so a failure can't leave a truncated track
		Path target = Paths.get(fileName).toAbsolutePath();
		Path temp = target.resolveSibling(target.getFileName() + ".tmp");
		try
		{
			try (FileOutputStream out = new FileOutputStream(temp.toFile()))
			{
				// escapes &, <, > and " in names and values; the entity references are EntityRefs
				XMLOutputter op = new XMLOutputter(Format.getPrettyFormat(), new TrackOutputProcessor());
				op.output(doc, out);
				out.flush();
			}
			try
			{
				Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			}
			catch (IOException e)
			{
				// not every file system can replace atomically
				Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
			}
		}
		finally
		{
			Files.deleteIfExists(temp);
		}
	}

	/**
	 * @return
	 */
	private synchronized Element getRoot(String trackName)
	{
		Attribute name = new Attribute("name", trackName);
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
	private synchronized Element getTrack()
	{
		Attribute name = new Attribute("name", "Main Track");
		Element track = new Element("section");
		Comment com = null;
		track.setAttribute(name);
		addContent(track, "width", "m", trackContext.getTrackData().getMainTrack().getWidth());
		addContent(track, "profil", trackContext.getTrackData().getMainTrack().getProfil());
		addContent(track, "profil steps length", "m", trackContext.getTrackData().getMainTrack().getProfilStepsLength());
		addContent(track, "surface", trackContext.getTrackData().getMainTrack().getSurface());
		addContent(track, "raceline widthscale", null, trackContext.getTrackData().getMainTrack().getRacelineWidthscale());
		addContent(track, "raceline int", null, trackContext.getTrackData().getMainTrack().getRacelineInt());
		addContent(track, "raceline ext", null, trackContext.getTrackData().getMainTrack().getRacelineExt());

		// only the version 5 loader reads these
		if (trackContext.getTrackData().getHeader().getVersion() == 5)
		{
			MainTrack main = trackContext.getTrackData().getMainTrack();
			addContent(track, "original bearing", "deg", main.getStartBearing());
			addContent(track, "start X", "m", main.getStartX());
			addContent(track, "start Y", "m", main.getStartY());
			addContent(track, "start Z", "m", main.getStartZ());
			addContent(track, "minimum X", "m", main.getMinimumX());
			addContent(track, "minimum Y", "m", main.getMinimumY());
			addContent(track, "minimum Z", "m", main.getMinimumZ());
			addContent(track, "extent X", "m", main.getExtentX());
			addContent(track, "extent Y", "m", main.getExtentY());
			addContent(track, "extent Z", "m", main.getExtentZ());
		}

		if (trackContext.getTrackData().getHeader().getVersion() == 3)
		{
			getSideV3(track, trackContext.getTrackData().getMainTrack().getLeft(), "l", true);
			getSideV3(track, trackContext.getTrackData().getMainTrack().getRight(), "r", true);
			getPitsV3(track);
		}
		else
		{
			com = new Comment("Left part of track");
			track.addContent(com);
			if (trackContext.getTrackData().getMainTrack().getLeft().getHasSide())
				track.addContent(getSide(trackContext.getTrackData().getMainTrack().getLeft(), "Left", true));
			if (trackContext.getTrackData().getMainTrack().getLeft().getHasBorder())
				track.addContent(getBorder(trackContext.getTrackData().getMainTrack().getLeft(), "Left"));
			if (trackContext.getTrackData().getMainTrack().getLeft().getHasBarrier())
				track.addContent(getBarrier(trackContext.getTrackData().getMainTrack().getLeft(), "Left"));
			com = new Comment("End of left part");
			track.addContent(com);
			com = new Comment("Right part of track");
			track.addContent(com);
			if (trackContext.getTrackData().getMainTrack().getRight().getHasSide())
				track.addContent(getSide(trackContext.getTrackData().getMainTrack().getRight(), "Right", true));
			if (trackContext.getTrackData().getMainTrack().getRight().getHasBorder())
				track.addContent(getBorder(trackContext.getTrackData().getMainTrack().getRight(), "Right"));
			if (trackContext.getTrackData().getMainTrack().getRight().getHasBarrier())
				track.addContent(getBarrier(trackContext.getTrackData().getMainTrack().getRight(), "Right"));
			com = new Comment("End of right part");
			track.addContent(com);
			track.addContent(getPits());
		}
		track.addContent(getSegments());

		return track;
	}

	/**
	 * @return
	 */
	private synchronized Element getPits()
	{
		Element pits = new Element("section");
		pits.setAttribute(new Attribute("name", "Pits"));

		// TORCS only has track side pits, without buildings, max pits or an indicator
		boolean options = trackContext.getGameType().hasPitOptions();
		if (options)
			addContent(pits, "pit style", null, trackContext.getPits().getStyle());
		else if (trackContext.getPits().getStyle() == 1)
			// TORCS track files name the pit type, though the game doesn't read it
			addContent(pits, "type", "track side");
		addContent(pits, "side", trackContext.getPits().getSide());
		addContent(pits, "entry", trackContext.getPits().getEntry());
		addContent(pits, "start", trackContext.getPits().getStart());
		if (options)
		{
			addContent(pits, "start buildings", trackContext.getPits().getStartBuildings());
			addContent(pits, "stop buildings", trackContext.getPits().getStopBuildings());
			addContent(pits, "max pits", null, trackContext.getPits().getMaxPits());
		}
		addContent(pits, "end", trackContext.getPits().getEnd());
		addContent(pits, "exit", trackContext.getPits().getExit());
		addContent(pits, "length", "m", trackContext.getPits().getLength());
		addContent(pits, "width", "m", trackContext.getPits().getWidth());
		if (options)
			addContent(pits, "pit indicator", null, trackContext.getPits().getIndicator());
		addContent(pits, "speed limit", trackContext.getPits().getSpeedLimit());

		return pits;
	}

	/**
	 * @return
	 */
	private synchronized void getPitsV3(Element pits)
	{
		if (trackContext.getPits().getStyle() == 0)
		{
			addContent(pits, "pit type", "no pit");
		}
		else if (trackContext.getPits().getStyle() == 1)
		{
			addContent(pits, "pit type", "track side");
		}
		else if (trackContext.getPits().getStyle() == 2)
		{
			// the games' spelling (TRK_VAL_PIT_TYPE_SEP_PATH)
			addContent(pits, "pit type", "seperate path");
		}

		addContent(pits, "pit side", trackContext.getPits().getSide());
		addContent(pits, "pit entry", trackContext.getPits().getEntry());
		addContent(pits, "pit start", trackContext.getPits().getStart());
		addContent(pits, "start buildings", trackContext.getPits().getStartBuildings());
		addContent(pits, "stop buildings", trackContext.getPits().getStopBuildings());
		addContent(pits, "max pits", null, trackContext.getPits().getMaxPits());
		addContent(pits, "pit end", trackContext.getPits().getEnd());
		addContent(pits, "pit exit", trackContext.getPits().getExit());
		addContent(pits, "pit length", "m", trackContext.getPits().getLength());
		addContent(pits, "pit width", "m", trackContext.getPits().getWidth());
		addContent(pits, "speed limit", trackContext.getPits().getSpeedLimit());
	}

	/**
	 * @return
	 */
	private synchronized Element getSegments()
	{
		SegmentVector segments = trackContext.getTrackData().getSegments();
		Segment prev = null;
		Attribute name = null;
		if (trackContext.getTrackData().getHeader().getVersion() == 3)
			name = new Attribute("name", "segments");
		else
			name = new Attribute("name", "Track Segments");
		Comment com = null;
		Element trackSegs = new Element("section");
		trackSegs.setAttribute(name);

		for (int i = 0; i < segments.size(); i++)
		{
			Segment shape = segments.get(i);
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
	private synchronized Element getSegment(Segment shape)
	{
		Attribute name = null;
		Comment com = null;
		Element el = null;
		Element segment = new Element("section");
		name = new Attribute("name", shape.getName());
		segment.setAttribute(name);
		addContent(segment, "comment", shape.getComment());
		el = attstrElement("type", shape.getType());
		segment.addContent(el);
		if (shape.getType().equals("str"))
		{
			addContent(segment, "lg", "m", ((Straight) shape).getLength());
		} else
		{
			double arc = ((Curve) shape).getArcDeg();
			addContent(segment, "arc", "deg", arc);

			double radStart = ((Curve) shape).getRadiusStart();
			addContent(segment, "radius", "m", radStart);

			double radEnd = ((Curve) shape).getRadiusEnd();
			if (radStart != radEnd)
			{
				addContent(segment, "end radius", "m", radEnd);
			}
			addContent(segment, "marks", ((Curve) shape).getMarks());
		}

		addContent(segment, "z start", "m", shape.getHeightStart());
		addContent(segment, "z start left", "m", shape.getHeightStartLeft());
		addContent(segment, "z start right", "m", shape.getHeightStartRight());

		addContent(segment, "z end", "m", shape.getHeightEnd());
		addContent(segment, "z end left", "m", shape.getHeightEndLeft());
		addContent(segment, "z end right", "m", shape.getHeightEndRight());

		addContent(segment, "grade", "%", shape.getGrade());
		addContent(segment, "banking start", "deg", shape.getBankingStart());
		addContent(segment, "banking end", "deg", shape.getBankingEnd());
		addContent(segment, "profil", shape.getProfil());
		addContent(segment, "profil steps", null, shape.getProfilSteps());
		addContent(segment, "profil steps length", "m", shape.getProfilStepsLength());
		addContent(segment, "profil start tangent", "%", shape.getProfilStartTangent());
		addContent(segment, "profil end tangent", "%", shape.getProfilEndTangent());
		addContent(segment, "profil start tangent left", "%", shape.getProfilStartTangentLeft());
		addContent(segment, "profil end tangent left", "%", shape.getProfilEndTangentLeft());
		addContent(segment, "profil start tangent right", "%", shape.getProfilStartTangentRight());
		addContent(segment, "profil end tangent right", "%", shape.getProfilEndTangentRight());
		addContent(segment, "surface", shape.getSurface());
		if (trackContext.getGameType().hasSegmentPenalty())
			addContent(segment, "penalty", null, shape.getPenalty());
		addContent(segment, "env map index", null, shape.getEnvMapIndex());
		// the version 3 loaders don't read it
		if (trackContext.getGameType().hasSpeedDreamsExtensions() && trackContext.getTrackData().getHeader().getVersion() != 3)
			addContent(segment, "DoV factor", null, shape.getDovFactor());

		if (trackContext.getTrackData().getHeader().getVersion() == 3)
		{
			getSideV3(segment, shape.getLeft(), "l", false);
			getSideV3(segment, shape.getRight(), "r", false);
		}
		else
		{
			com = new Comment("Left part of segment");
			segment.addContent(com);
			if (shape.getLeft().getHasSide())
				segment.addContent(getSide(shape.getLeft(), "Left", false));
			if (shape.getLeft().getHasBorder())
				segment.addContent(getBorder(shape.getLeft(), "Left"));
			if (shape.getLeft().getHasBarrier())
				segment.addContent(getBarrier(shape.getLeft(), "Left"));
			com = new Comment("End of left part");
			segment.addContent(com);
			com = new Comment("Right part of segment");
			segment.addContent(com);
			if (shape.getRight().getHasSide())
				segment.addContent(getSide(shape.getRight(), "Right", false));
			if (shape.getRight().getHasBorder())
			    segment.addContent(getBorder(shape.getRight(), "Right"));
			if (shape.getRight().getHasBarrier())
			    segment.addContent(getBarrier(shape.getRight(), "Right"));
			com = new Comment("End of right part");
			segment.addContent(com);
		}

		return segment;
	}

	/**
	 * @param left
	 * @param string
	 * @return
	 */
	private synchronized Element getBorder(SegmentSide part, String sPart)
	{
		Element side = new Element("section");
		side.setAttribute(new Attribute("name", sPart + " Border"));

		addContent(side, "width", "m", part.getBorderWidth());
		addContent(side, "height", "m", part.getBorderHeight());
		addContent(side, "surface", part.getBorderSurface());
		addContent(side, "style", part.getBorderStyle());

		return side;
	}

	/**
	 * @param left
	 * @param string
	 * @return
	 */
	private synchronized Element getBarrier(SegmentSide part, String sPart)
	{
		Element side = new Element("section");
		side.setAttribute(new Attribute("name", sPart + " Barrier"));

		addContent(side, "width", "m", part.getBarrierWidth());
		addContent(side, "height", "m", part.getBarrierHeight());
		addContent(side, "surface", part.getBarrierSurface());
		addContent(side, "style", barrierStyle(part.getBarrierStyle()));

		return side;
	}

	/**
	 * @param left
	 * @return
	 */
	/**
	 * Writes a side the way the version 3 loaders read it: the Main Track has only
	 * "side width", and segments have only "side start width" and "side end width".
	 * The banking type ("side type") and the barrier are read only for the Main Track.
	 */
	private synchronized void getSideV3(Element side, SegmentSide part, String sPart, boolean mainTrack)
	{
		if (mainTrack)
		{
			// the segments start from it, like from a previous segment's end
			addContent(side, sPart + "side width", "m", part.getMainTrackSideWidth());
		}
		else
		{
			addContent(side, sPart + "side start width", "m", part.getSideStartWidth());
			addContent(side, sPart + "side end width", "m", part.getSideEndWidth());
		}
		addContent(side, sPart + "side surface", part.getSideSurface());
		if (mainTrack)
			addContent(side, sPart + "side type", part.getSideBankingType());

		addContent(side, sPart + "border width", "m", part.getBorderWidth());
		addContent(side, sPart + "border height", "m", part.getBorderHeight());
		addContent(side, sPart + "border surface", part.getBorderSurface());
		addContent(side, sPart + "border style", part.getBorderStyle());

		if (mainTrack)
		{
			addContent(side, sPart + "barrier width", "m", part.getBarrierWidth());
			addContent(side, sPart + "barrier height", "m", part.getBarrierHeight());
			addContent(side, sPart + "barrier surface", part.getBarrierSurface());
			addContent(side, sPart + "barrier style", barrierStyle(part.getBarrierStyle()));
		}
	}

	/**
	 * @param left
	 * @return
	 */
	private synchronized Element getSide(SegmentSide part, String sPart, boolean mainTrack)
	{
		Element side = new Element("section");
		side.setAttribute(new Attribute("name", sPart + " Side"));

		if (mainTrack)
		{
			// the games read only "width" for the Main Track: the segments start from it
			addContent(side, "width", "m", part.getMainTrackSideWidth());
		}
		else if (!trackContext.getGameType().widthSetsBothSideWidths())
		{
			// TORCS reads "width" as the end width only, so equal widths can't become "width"
			addContent(side, "start width", "m", part.getSideStartWidth());
			addContent(side, part.getEndWidthAsWidth() ? "width" : "end width", "m", part.getSideEndWidth());
		}
		else if (!Double.isNaN(part.getSideStartWidth()) &&
			!Double.isNaN(part.getSideEndWidth()) &&
			part.getSideStartWidth() == part.getSideEndWidth())
		{
			addContent(side, "width", "m", part.getSideStartWidth());
		}
		else
		{
			addContent(side, "start width", "m", part.getSideStartWidth());
			addContent(side, "end width", "m", part.getSideEndWidth());
		}
		addContent(side, "surface", part.getSideSurface());
		// the games only read the Main Track's banking type
		if (mainTrack)
			addContent(side, "banking type", part.getSideBankingType());

		return side;
	}

	private synchronized Element getSurfaces()
	{
		Element surfaces = new Element("section");
		surfaces.setAttribute(new Attribute("name", "Surfaces"));
		Element root = surfaces;

		if (trackContext.getTrackData().getHeader().getVersion() == 3)
		{
			surfaces = new Element("section");
			surfaces.setAttribute(new Attribute("name", "List"));
			root.addContent(surfaces);
		}

		if (trackContext.getTrackData().getHasSurfacesReference())
			surfaces.addContent(new EntityRef("default-surfaces"));

		Vector<Surface> surfaceData = trackContext.getTrackData().getSurfaces();

		if (surfaceData == null)
			return surfaces;

		for (int i = 0; i < surfaceData.size(); i++)
		{
			Surface surface = surfaceData.get(i);

			Element el = new Element("section");
			el.setAttribute(new Attribute("name", surface.getName()));

			addContent(el, "comment", surface.getComment());
			addContent(el, "color R1", null, surface.getColorR1());
			addContent(el, "color G1", null, surface.getColorG1());
			addContent(el, "color B1", null, surface.getColorB1());
			addContent(el, "color R2", null, surface.getColorR2());
			addContent(el, "color G2", null, surface.getColorG2());
			addContent(el, "color B2", null, surface.getColorB2());
			addContent(el, "texture name", surface.getTextureName());
			addContent(el, "texture type", surface.getTextureType());
			addContent(el, "texture size", "m", surface.getTextureSize());
			if (trackContext.getGameType().hasTorcsTrackgenKeys())
				addContent(el, "texture size v", "m", surface.getTextureSizeV());
			addContent(el, "texture randomness", "m", surface.getTextureRandomness());
			addContent(el, "texture link with previous", surface.getTextureLinkWithPrevious());
			addContent(el, "texture start on boundary", surface.getTextureStartOnBoundary());
			addContent(el, "texture mipmap", null, surface.getTextureMipMap());
			addContent(el, "friction", null, surface.getFriction());
			addContent(el, "rolling resistance", null, surface.getRollingResistance());
			addContent(el, "bump name", surface.getBumpName());
			addContent(el, "bump size", "m", surface.getBumpSize());
			addContent(el, "roughness", "m", surface.getRoughness());
			addContent(el, "roughness wavelength", "m", surface.getRoughnessWavelength());
			addContent(el, "raceline name", surface.getRacelineName());
			addContent(el, "dammage", null, surface.getDammage());
			addContent(el, "rebound", null, surface.getRebound());

			surfaces.addContent(el);
		}

		return root;
	}

	private synchronized Element getCameras()
	{
		Element cameras = new Element("section");
		cameras.setAttribute(new Attribute("name", "Cameras"));
		Element root = cameras;

		Vector<Camera> cameraData = trackContext.getTrackData().getCameras();

		if (cameraData == null)
			return cameras;

		if (trackContext.getTrackData().getHeader().getVersion() == 3)
		{
			cameras = new Element("section");
			cameras.setAttribute(new Attribute("name", "list"));
			root.addContent(cameras);
		}

		for (int i = 0; i < cameraData.size(); i++)
		{
			Camera camera = cameraData.get(i);

			Element el = new Element("section");
			el.setAttribute(new Attribute("name", camera.getName()));

			addContent(el, "comment", camera.getComment());
			addContent(el, "segment", camera.getSegment());
			addContent(el, "to right", null, camera.getToRight());
			// units can be meters for straights or degrees for curves
			Segment segment = trackContext.getSegment(camera.getSegment());
			if (camera.isToStartUnitsKnown())
			{
				// keep the unit the track file used: Speed Dreams converts deg to rad,
				// so adding or dropping it would change the camera position
				addContent(el, "to start", camera.getToStartUnits(), camera.getToStart());
			}
			else if (segment != null && !segment.getType().equals("str"))
			{
				addContent(el, "to start", "deg", camera.getToStart());
			}
			else
			{
				addContent(el, "to start", null, camera.getToStart());
			}
			addContent(el, "height", null, camera.getHeight());
			addContent(el, "fov start", camera.getFovStart());
			addContent(el, "fov end", camera.getFovEnd());

			cameras.addContent(el);
		}

		return root;
	}

	private synchronized Element getLights()
	{
		Element lights = new Element("section");
		lights.setAttribute(new Attribute("name", "Track Lights"));

		Vector<TrackLight> lightData = trackContext.getTrackData().getTrackLights();

		if (lightData == null)
			return lights;

		for (int i = 0; i < lightData.size(); i++)
		{
			TrackLight light = lightData.get(i);

			Element el = new Element("section");
			el.setAttribute(new Attribute("name", light.getName()));

			addContent(el, "role", light.getRole());

			if (isSet(light.getTopLeft()))
			{
				Element corner = new Element("section");
				corner.setAttribute(new Attribute("name", "topleft"));

				addContent(corner, "x", null, light.getTopLeft().x);
				addContent(corner, "y", null, light.getTopLeft().y);
				addContent(corner, "z", null, light.getTopLeft().z);

				el.addContent(corner);
			}

			if (isSet(light.getBottomRight()))
			{
				Element corner = new Element("section");
				corner.setAttribute(new Attribute("name", "bottomright"));

				addContent(corner, "x", null, light.getBottomRight().x);
				addContent(corner, "y", null, light.getBottomRight().y);
				addContent(corner, "z", null, light.getBottomRight().z);

				el.addContent(corner);
			}

			addContent(el, "texture on", light.getTextureOn());
			addContent(el, "texture off", light.getTextureOff());
			addContent(el, "index", null, light.getIndex());
			addContent(el, "red", null, light.getRed());
			addContent(el, "green", null, light.getGreen());
			addContent(el, "blue", null, light.getBlue());

			lights.addContent(el);
		}

		return lights;
	}
	/** A light corner the track sets: the light always has both, but they can be empty */
	private static boolean isSet(TrackLight.Corner corner)
	{
		return corner != null && !(Double.isNaN(corner.x) && Double.isNaN(corner.y) && Double.isNaN(corner.z));
	}

	private synchronized Element getGeoreference()
	{
		Element element = new Element("section");
		element.setAttribute(new Attribute("name", "Georeference"));

		Georeference data = trackContext.getTrackData().getGeoreference();

		// TORCS keeps them as floats (good to about a meter), but the file keeps every
		// digit given, for other tools and for a game that reads them more precisely
		addPreciseContent(element, "latitude", "deg", data.getLatitude());
		addPreciseContent(element, "longitude", "deg", data.getLongitude());
		addContent(element, "altitude", "m", data.getAltitude());
		addContent(element, "heading", "deg", data.getHeading());

		return element;
	}

	private synchronized Element getSectors()
	{
		Element sectors = new Element("section");
		sectors.setAttribute(new Attribute("name", "Sectors"));

		Vector<Sector> sectorData = trackContext.getTrackData().getSectors();

		if (sectorData == null)
			return sectors;

		for (int i = 0; i < sectorData.size(); i++)
		{
			Sector sector = sectorData.get(i);

			Element el = new Element("section");
			el.setAttribute(new Attribute("name", sector.getName()));
			addContent(el, "comment", sector.getComment());
			addContent(el, "distance from start", "m", sector.getDistanceFromStart());

			sectors.addContent(el);
		}

		return sectors;
	}

	private synchronized Element getObjects()
	{
		Element objects = new Element("section");
		objects.setAttribute(new Attribute("name", "Objects"));
		if (trackContext.getTrackData().getHasObjectsReference())
			objects.addContent(new EntityRef("default-objects"));

		Vector<TrackObject> objectData = trackContext.getTrackData().getObjects();

		if (objectData == null)
			return objects;

		for (int i = 0; i < objectData.size(); i++)
		{
			TrackObject object = objectData.get(i);

			Element el = new Element("section");
			el.setAttribute(new Attribute("name", object.getName()));

			addContent(el, "object", object.getObject());
			addContent(el, "comment", object.getComment());
			addHexContent(el, "color", null, object.getColor());
			addContent(el, "orientation type", object.getOrientationType());
			// TORCS trackgen reads neither the border distance nor the scale settings
			boolean speedDreams = trackContext.getGameType().hasSpeedDreamsTrackgenKeys();
			if (speedDreams)
				addContent(el, "border distance", "m", object.getBorderDistance());
			addContent(el, "orientation", "deg", object.getOrientation());
			addContent(el, "delta height",null,  object.getDeltaHeight());
			addContent(el, "delta vert", null, object.getDeltaVert());
			if (speedDreams)
			{
				addContent(el, "scale type", object.getScaleType());
				addContent(el, "scale", null, object.getScale());
				addContent(el, "scale min", null, object.getScaleMin());
				addContent(el, "scale max", null, object.getScaleMax());
			}
			if (trackContext.getGameType().hasUseMaterial())
				addContent(el, "use material", object.getUseMaterial());

			objects.addContent(el);
		}

		return objects;
	}

	/**
	 * Only Speed Dreams has the fence1/fence2 barrier styles (the others would build a
	 * wall), so for the others write them as fence.
	 */
	private String barrierStyle(String style)
	{
		if (!trackContext.getGameType().hasFence1Fence2() && style != null && (style.equals("fence1") || style.equals("fence2")))
			return "fence";

		return style;
	}

	private synchronized String getCredit()
	{
		String sep = System.getProperty("file.separator");
		String filename = Properties.getInstance().getPath();
		String trackname = filename.substring(filename.lastIndexOf(sep) + 1);
		String tmp = "\n";
		tmp += "file                : " + trackname + ".xml\n";
		tmp += "auto generated      : by " + TrackEditor.getAppName() + "\n";
		tmp += "version             : " + Properties.getInstance().version + "\n";
		tmp += "copyright           : (C) 2005 by Charalampos Alexopoulos\n";

		return tmp;
	}

	private synchronized String getLicence()
	{
		String tmp = "\n";
		tmp += "This program is free software; you can redistribute it and/or modify it\n";
		tmp += "under the terms of the GNU General Public License as published by\n";
		tmp += "the Free Software Foundation; either version 2 of the License, or\n";
		tmp += "(at your option) any later version.";
		tmp += "\n";

		return tmp;
	}

	private synchronized Element getHeader()
	{
		Attribute name = new Attribute("name", "Header");
		String tmp = "";

		Element header = new Element("section");
		header.setAttribute(name);

		// TORCS reads none of subcategory, copyright and email; only CSR has a short name
		boolean extensions = trackContext.getGameType().hasSpeedDreamsExtensions();
		addContent(header, "name", trackContext.getTrackData().getHeader().getName());
		if (trackContext.getGameType().hasShortName())
			addContent(header, "short name", trackContext.getTrackData().getHeader().getShortName());

		if (trackContext.getTrackData().getHeader().getCategory() != null)
		{
			tmp = trackContext.getTrackData().getHeader().getCategory();
		} else
		{
			tmp = "road";
		}
		addContent(header, "category", tmp);
		if (extensions)
			addContent(header, "subcategory", trackContext.getTrackData().getHeader().getSubcategory());
		if (trackContext.getGameType().hasCarCategory())
			addContent(header, "car category", trackContext.getTrackData().getHeader().getCarCategory());
		if (trackContext.getGameType().hasDisabledPenalties())
			addContent(header, "disabled penalties", trackContext.getTrackData().getHeader().getDisabledPenalties());
		addContent(header, "version", null, trackContext.getTrackData().getHeader().getVersion());

		if (trackContext.getTrackData().getHeader().getAuthor() != null)
		{
			tmp = trackContext.getTrackData().getHeader().getAuthor();
		} else
		{
			tmp = "Anonymous";
		}
		addContent(header, "author", tmp);

		if (extensions && trackContext.getTrackData().getHeader().getCopyright() != null)
		{
			tmp = trackContext.getTrackData().getHeader().getCopyright();
			addContent(header, "copyright", tmp);
		}

		if (extensions && trackContext.getTrackData().getHeader().getEmail() != null)
		{
			tmp = trackContext.getTrackData().getHeader().getEmail();
			addContent(header, "email", tmp);
		}

		if (trackContext.getTrackData().getHeader().getDescription() != null)
		{
			tmp = trackContext.getTrackData().getHeader().getDescription();
		} else
		{
			tmp = "No description provided";
		}
		addContent(header, "description", tmp);

		return header;
	}

	/**
	 * @return
	 */
	private synchronized Element getLocal()
	{
		Element element = new Element("section");
		element.setAttribute(new Attribute("name", "Local Info"));

		addContent(element, "station", trackContext.getTrackData().getLocalInfo().getStation());
		addContent(element, "timezone", null, trackContext.getTrackData().getLocalInfo().getTimezone());
		addContent(element, "time of day", "hour", trackContext.getTrackData().getLocalInfo().getTimeOfDay());
		addContent(element, "sun ascension", "deg", trackContext.getTrackData().getLocalInfo().getSunAscension());
		addContent(element, "altitude", "m", trackContext.getTrackData().getLocalInfo().getAltitude());
		// Speed Dreams reads latitude/longitude without a unit and expects plain degrees,
		// so only write a unit if the track file had one
		addContent(element, "latitude", trackContext.getTrackData().getLocalInfo().getLatitudeUnits(), trackContext.getTrackData().getLocalInfo().getLatitude());
		addContent(element, "longitude", trackContext.getTrackData().getLocalInfo().getLongitudeUnits(), trackContext.getTrackData().getLocalInfo().getLongitude());
		addContent(element, "climat", null, trackContext.getTrackData().getLocalInfo().getClimat());
		addContent(element, "precipitation", null, trackContext.getTrackData().getLocalInfo().getPrecipitation());
		if (trackContext.getGameType().hasBestLap())
			addContent(element, "best lap", null, trackContext.getTrackData().getLocalInfo().getBestLap());

		return element;
	}

	private synchronized Element getGrid()
	{
		Element element = new Element("section");
		boolean torcs = trackContext.getGameType() == GameType.TORCS;
		if (!torcs || !trackContext.getTrackData().getStartingGrid().isEmpty())
			element.setAttribute(new Attribute("name", "Starting Grid"));
		addContent(element, "rows", null, trackContext.getTrackData().getStartingGrid().getRows());
		addContent(element, "pole position side", trackContext.getTrackData().getStartingGrid().getPolePositionSide());
		addContent(element, "distance to start", "m", trackContext.getTrackData().getStartingGrid().getDistanceToStart());
		addContent(element, "distance between columns", "m", trackContext.getTrackData().getStartingGrid().getDistanceBetweenColumns());
		addContent(element, "offset within a column", "m", trackContext.getTrackData().getStartingGrid().getOffsetWithinAColumn());
		addContent(element, "initial height", "m", trackContext.getTrackData().getStartingGrid().getInitialHeight());

		return element;
	}

	/**
	 * @return
	 */
	private synchronized Element getGraphic()
	{
		String sep = System.getProperty("file.separator");
		String filename = Properties.getInstance().getPath();
		String trackname = filename.substring(filename.lastIndexOf(sep) + 1);

		Element element = new Element("section");
		element.setAttribute(new Attribute("name", "Graphic"));

		if (trackContext.getTrackData().getGraphic().getDescription() == null)
			addContent(element, "3d description", trackname + ".ac");
		else
			addContent(element, "3d description", trackContext.getTrackData().getGraphic().getDescription());

		// the per-type 3d object files are only used by CSR
		if (trackContext.getGameType().hasTypeObjectFiles())
		{
			addContent(element, "3d separate objects", trackContext.getTrackData().getGraphic().getSeparateObjects());
			addContent(element, "3d terrain", trackContext.getTrackData().getGraphic().getTerrainObjects());
			addContent(element, "3d road", trackContext.getTrackData().getGraphic().getRoadObjects());
			addContent(element, "3d road2", trackContext.getTrackData().getGraphic().getRoad2Objects());
			addContent(element, "3d trees", trackContext.getTrackData().getGraphic().getTreesObjects());
			addContent(element, "3d building", trackContext.getTrackData().getGraphic().getBuildingObjects());
			addContent(element, "3d house", trackContext.getTrackData().getGraphic().getHouseObjects());
			addContent(element, "3d cars", trackContext.getTrackData().getGraphic().getCarsObjects());
			addContent(element, "3d truck", trackContext.getTrackData().getGraphic().getTruckObjects());
			addContent(element, "3d tribunes", trackContext.getTrackData().getGraphic().getTribunesObjects());
			addContent(element, "3d pub", trackContext.getTrackData().getGraphic().getPubObjects());
			addContent(element, "3d decors", trackContext.getTrackData().getGraphic().getDecorsObjects());
			addContent(element, "3d water", trackContext.getTrackData().getGraphic().getWaterObjects());
			addContent(element, "3d grass", trackContext.getTrackData().getGraphic().getGrassObjects());
			addContent(element, "3d sand", trackContext.getTrackData().getGraphic().getSandObjects());
			addContent(element, "3d rock", trackContext.getTrackData().getGraphic().getRockObjects());
			addContent(element, "3d curb", trackContext.getTrackData().getGraphic().getCurbObjects());
			addContent(element, "3d fence", trackContext.getTrackData().getGraphic().getFenceObjects());
			addContent(element, "3d barrier", trackContext.getTrackData().getGraphic().getBarrierObjects());
			addContent(element, "3d wall", trackContext.getTrackData().getGraphic().getWallObjects());
			addContent(element, "3d environment", trackContext.getTrackData().getGraphic().getEnvironmentObjects());
		}

		addContent(element, "background image", trackContext.getTrackData().getGraphic().getBackgroundImage());
		addContent(element, "background type", null, trackContext.getTrackData().getGraphic().getBackgroundType());
		addContent(element, "background color R", null, trackContext.getTrackData().getGraphic().getBackgroundColorR());
		addContent(element, "background color G", null, trackContext.getTrackData().getGraphic().getBackgroundColorG());
		addContent(element, "background color B", null, trackContext.getTrackData().getGraphic().getBackgroundColorB());
		addContent(element, "ambient color R", null, trackContext.getTrackData().getGraphic().getAmbientColorR());
		addContent(element, "ambient color G", null, trackContext.getTrackData().getGraphic().getAmbientColorG());
		addContent(element, "ambient color B", null, trackContext.getTrackData().getGraphic().getAmbientColorB());
		addContent(element, "diffuse color R", null, trackContext.getTrackData().getGraphic().getDiffuseColorR());
		addContent(element, "diffuse color G", null, trackContext.getTrackData().getGraphic().getDiffuseColorG());
		addContent(element, "diffuse color B", null, trackContext.getTrackData().getGraphic().getDiffuseColorB());
		addContent(element, "specular color R", null, trackContext.getTrackData().getGraphic().getSpecularColorR());
		addContent(element, "specular color G", null, trackContext.getTrackData().getGraphic().getSpecularColorG());
		addContent(element, "specular color B", null, trackContext.getTrackData().getGraphic().getSpecularColorB());
		addContent(element, "light position x", "m", trackContext.getTrackData().getGraphic().getLightPositionX());
		addContent(element, "light position y", "m", trackContext.getTrackData().getGraphic().getLightPositionY());
		addContent(element, "light position z", "m", trackContext.getTrackData().getGraphic().getLightPositionZ());
		addContent(element, "shininess", null, trackContext.getTrackData().getGraphic().getShininess());
		addContent(element, "fov factor", null, trackContext.getTrackData().getGraphic().getFovFactor());

		// TORCS tracks leave the section out when it has nothing to say: no values,
		// or only the values the game uses anyway
		boolean torcs = trackContext.getGameType() == GameType.TORCS;
		if (!torcs ||
			(!trackContext.getTrackData().getGraphic().getTurnMarks().isEmpty() &&
			 !trackContext.getTrackData().getGraphic().getTurnMarks().isDefault()))
			element.addContent(getTurnMarks());
		element.addContent(getTerrainGeneration());
		element.addContent(getEnvironmentMapping());

		return element;
	}

	private synchronized Element getTurnMarks()
	{
		Element element = new Element("section");
		element.setAttribute(new Attribute("name", "Turn Marks"));

		addContent(element, "width", "m", trackContext.getTrackData().getGraphic().getTurnMarks().getWidth());
		addContent(element, "height", "m", trackContext.getTrackData().getGraphic().getTurnMarks().getHeight());
		addContent(element, "vertical space", "m", trackContext.getTrackData().getGraphic().getTurnMarks().getVerticalSpace());
		addContent(element, "horizontal space", "m", trackContext.getTrackData().getGraphic().getTurnMarks().getHorizontalSpace());

		return element;
	}

	private synchronized Element getTerrainGeneration()
	{
		Element element = new Element("section");
		element.setAttribute(new Attribute("name", "Terrain Generation"));

		addContent(element, "track step", "m", trackContext.getTerrainGeneration().getTrackStep());
		addContent(element, "border margin", "m", trackContext.getTerrainGeneration().getBorderMargin());
		addContent(element, "border step", "m", trackContext.getTerrainGeneration().getBorderStep());
		boolean torcs = trackContext.getGameType().hasTorcsTrackgenKeys();
		if (torcs)
			addContent(element, "terrain transition", "m", trackContext.getTerrainGeneration().getTerrainTransition());
		addContent(element, "border height", "m", trackContext.getTerrainGeneration().getBorderHeight());
		addContent(element, "orientation", trackContext.getTerrainGeneration().getOrientation());
		addContent(element, "maximum altitude", "m", trackContext.getTerrainGeneration().getMaximumAltitude());
		addContent(element, "minimum altitude", "m", trackContext.getTerrainGeneration().getMinimumAltitude());
		addContent(element, "group size", "m", trackContext.getTerrainGeneration().getGroupSize());
		addContent(element, "elevation map", trackContext.getTerrainGeneration().getElevationMap());
		// Add Relief names the file; reliefs left without one (the name was
		// cleared) still go to <track folder>-relief.ac
		if (!trackContext.getReliefs().getReliefs().isEmpty() &&
			(trackContext.getTerrainGeneration().getReliefFile() == null || trackContext.getTerrainGeneration().getReliefFile().isEmpty()))
		{
			String fileName = Reliefs.newFileName();
			if (fileName != null)
				trackContext.getReliefs().setNewFileName(fileName);
		}

		addContent(element, "relief file", trackContext.getTerrainGeneration().getReliefFile());

		if (writeFiles && trackContext.getReliefs().getChanged())
		{
			try
			{
				trackContext.getReliefs().writeFile();
			}
			catch (Exception ex)
			{
				addFileError("Relief file", trackContext.getTerrainGeneration().getReliefFile(), ex);
			}
		}

		addContent(element, "relief border", trackContext.getTerrainGeneration().getReliefBorder());
		addContent(element, "surface", trackContext.getTerrainGeneration().getSurface());
		addContent(element, "random seed", null, trackContext.getTerrainGeneration().getRandomSeed());
		if (trackContext.getGameType().hasSpeedDreamsTrackgenKeys())
			addContent(element, "use object materials", trackContext.getTerrainGeneration().getUseObjectMaterials());
		if (torcs)
		{
			addContent(element, "crossing mode", trackContext.getTerrainGeneration().getCrossingMode());
			addContent(element, "crossing bridge socket gap", "m", trackContext.getTerrainGeneration().getCrossingBridgeSocketGap());
			addContent(element, "crossing bridge thickness", "m", trackContext.getTerrainGeneration().getCrossingBridgeThickness());
			for (int i = 0; i < TerrainGeneration.CROSSING_BRIDGE_SURFACE_KEYS.length; i++)
				addContent(element, TerrainGeneration.CROSSING_BRIDGE_SURFACE_KEYS[i], trackContext.getTerrainGeneration().getCrossingBridgeSurface(i));
		}
		if (trackContext.getGameType().hasTiledFile())
			addContent(element, "tiled file", trackContext.getTerrainGeneration().getTiledFile());
		// accc's settings, for every game (TORCS and CSR make .acc tracks with accc)
		for (int i = 0; i < TerrainGeneration.ACC_GROUP_DISTANCE_KEYS.length; i++)
			addContent(element, TerrainGeneration.ACC_GROUP_DISTANCE_KEYS[i], "m", trackContext.getTerrainGeneration().getAccGroupDistance(i));
		addContent(element, TerrainGeneration.ACC_SPLIT_DISTANCE_KEY, "m", trackContext.getTerrainGeneration().getAccSplitDistance());
		addContent(element, TerrainGeneration.ACC_EXTENDED_STRIPS_KEY, trackContext.getTerrainGeneration().getAccExtendedStrips());

		if (!torcs || !trackContext.getTerrainGeneration().getObjectMaps().isEmpty())
			element.addContent(getObjectMaps());
		// only Speed Dreams trackgen places individual graphic objects
		if (trackContext.getGameType().hasGraphicObjects())
			element.addContent(getGraphicObjects());

		return element;
	}

	private synchronized Element getEnvironmentMapping()
	{
		Element element = new Element("section");
		element.setAttribute(new Attribute("name", "Environment Mapping"));

		Vector<EnvironmentMapping> environment = trackContext.getTrackData().getGraphic().getEnvironmentMapping();

		for (int i = 0; i < environment.size(); i++)
		{
			EnvironmentMapping data = environment.get(i);

			Element el = new Element("section");
			el.setAttribute(new Attribute("name", data.getName()));

			addContent(el, "env map image", data.getEnvMapImage());

			element.addContent(el);
		}

		return element;
	}

	private synchronized Element getGraphicObjects()
	{
		Element element = new Element("section");
		element.setAttribute(new Attribute("name", "Objects"));

		for (GraphicObject graphicObject : trackContext.getGraphicObjects())
		{
			Element el = new Element("section");
			el.setAttribute(new Attribute("name", graphicObject.getName()));

			addContent(el, "comment", graphicObject.getComment());
			addHexContent(el, "color", null, graphicObject.getColor());
			addContent(el, "x", "m", graphicObject.getX());
			addContent(el, "y", "m", graphicObject.getY());
			addContent(el, "orientation", "deg", graphicObject.getOrientation());
			addContent(el, "height", "m", graphicObject.getHeight());

			element.addContent(el);
		}

		return element;
	}

	private synchronized Element getObjectMaps()
	{
		Element element = new Element("section");
		element.setAttribute(new Attribute("name", "Object Maps"));

		Vector<ObjectMap> objMaps = trackContext.getObjectMaps();

		for (int i = 0; i < objMaps.size(); i++)
		{
			ObjectMap data = objMaps.get(i);

			Element el = new Element("section");
			el.setAttribute(new Attribute("name", data.getName()));

			addContent(el, "object map", data.getObjectMap());

			element.addContent(el);

			if (writeFiles && data.getChanged())
			{
				try
				{
					data.writeImage();
				}
				catch (Exception e)
				{
					addFileError("Object map", data.getObjectMap(), e);
				}
			}
		}

		return element;
	}

	private synchronized Element attstrElement(String attname, String attval)
	{
		Attribute name = null;
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

	private synchronized Element attnumElement(String attname, String attunit, String attval)
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

	private synchronized void addContent(Element section, String attribute, String units, double value)
	{
		if (!Double.isNaN(value))
		{
			section.addContent(attnumElement(attribute, units, number(value)));
		}
	}

	/**
	 * Like addContent, but with all of the value's digits instead of only those a float keeps.
	 */
	private synchronized void addPreciseContent(Element section, String attribute, String units, double value)
	{
		if (!Double.isNaN(value))
		{
			section.addContent(attnumElement(attribute, units, Double.toString(value)));
		}
	}

	private synchronized void addContent(Element section, String attribute, DoubleValue value)
	{
		if (value != null && !Double.isNaN(value.value))
		{
			section.addContent(attnumElement(attribute, value.units, number(value.value)));
		}
	}

	/**
	 * The game keeps every number as a float, so write the shortest text that gives the
	 * same float: no digits the game can't use (1.7453292519943295 becomes 1.7453293,
	 * 350.52000000000004 becomes 350.52), and nothing it uses is lost.
	 */
	static String number(double value)
	{
		if (Double.isInfinite(value) || Math.abs(value) > Float.MAX_VALUE)
			return Double.toString(value);

		return Float.toString((float) value);
	}

	private synchronized void addContent(Element section, String attribute, String units, int value)
	{
		if (value != Integer.MAX_VALUE)
		{
			section.addContent(attnumElement(attribute, units, value + ""));
		}
	}

	private synchronized void addHexContent(Element section, String attribute, String units, int value)
	{
		if (value != Integer.MAX_VALUE)
		{
			section.addContent(attnumElement(attribute, units, String.format("0x%06X", value)));
		}
	}

	private synchronized void addContent(Element section, String attribute, String string)
	{
		// an empty value is written too: for some keys the game uses its default only when
		// the key is missing ("background image" is then background.png)
		if (string != null)
		{
			// the outputter escapes it
			section.addContent(attstrElement(attribute, string));
		}
	}

	/**
	 * Lays the file out like the editor always has: &amp;default-surfaces; and
	 * &amp;default-objects; on a line of their own before a section's sections
	 * (pretty printing would put the first section on the same line), and an
	 * empty line after the DOCTYPE and at the end.
	 */
	public static class TrackOutputProcessor extends AbstractXMLOutputProcessor
	{
		@Override
		protected void printDocType(Writer out, FormatStack fstack, DocType docType) throws IOException
		{
			super.printDocType(out, fstack, docType);
			if (fstack.getLineSeparator() != null)
				write(out, fstack.getLineSeparator());
		}

		@Override
		protected void printDocument(Writer out, FormatStack fstack, NamespaceStack nstack, Document doc) throws IOException
		{
			super.printDocument(out, fstack, nstack, doc);
			if (fstack.getLineSeparator() != null)
				write(out, fstack.getLineSeparator());
		}

		@Override
		protected void printEntityRef(Writer out, FormatStack fstack, EntityRef entity) throws IOException
		{
			super.printEntityRef(out, fstack, entity);

			org.jdom2.Parent parent = entity.getParent();
			if (fstack.getLineSeparator() == null || fstack.getLevelIndent() == null || parent == null)
				return;
			int next = parent.indexOf(entity) + 1;
			if (next < parent.getContentSize() && parent.getContent(next) instanceof Element)
				write(out, fstack.getLineSeparator() + fstack.getLevelIndent());
		}
	}
}
