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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;

import gui.EditorFrame;
import utils.DoubleValue;
import utils.Editor;
import utils.TrackEditor;
import utils.SegmentVector;
import utils.circuit.Camera;
import utils.circuit.Curve;
import utils.circuit.EnvironmentMapping;
import utils.circuit.Georeference;
import utils.circuit.GraphicObject;
import utils.circuit.MainTrack;
import utils.circuit.ObjectMap;
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
	private EditorFrame		editorFrame;
	private List<String>	fileErrors		= new ArrayList<String>();

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

	public XmlWriter(EditorFrame editorFrame)
	{
		this.editorFrame = editorFrame;
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
		boolean torcs = editorFrame.getGameType() == EditorFrame.GameType.TORCS;

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
		boolean extensions = editorFrame.getGameType().hasSpeedDreamsExtensions();
		if (extensions)
			root.addContent(getLights());
		root.addContent(getSurfaces());
		root.addContent(getObjects());
		root.addContent(getHeader());
		// only TORCS reads it; its track manual puts it after the header
		if (torcs && !editorFrame.getTrackData().getGeoreference().isEmpty())
			root.addContent(getGeoreference());
		if (extensions)
			root.addContent(getLocal());
		root.addContent(getGraphic());
		if (!torcs)
			root.addContent(getGrid());
		root.addContent(getTrack());
		// TORCS trackgen doesn't like empty cameras
		if (!torcs || !editorFrame.getTrackData().getCameras().isEmpty())
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
				XMLOutput op = new XMLOutput(Format.getPrettyFormat());
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
		addContent(track, "width", "m", editorFrame.getTrackData().getMainTrack().getWidth());
		addContent(track, "profil", editorFrame.getTrackData().getMainTrack().getProfil());
		addContent(track, "profil steps length", "m", editorFrame.getTrackData().getMainTrack().getProfilStepsLength());
		addContent(track, "surface", editorFrame.getTrackData().getMainTrack().getSurface());
		addContent(track, "raceline widthscale", null, editorFrame.getTrackData().getMainTrack().getRacelineWidthscale());
		addContent(track, "raceline int", null, editorFrame.getTrackData().getMainTrack().getRacelineInt());
		addContent(track, "raceline ext", null, editorFrame.getTrackData().getMainTrack().getRacelineExt());

		// only the version 5 loader reads these
		if (editorFrame.getTrackData().getHeader().getVersion() == 5)
		{
			MainTrack main = editorFrame.getTrackData().getMainTrack();
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

		if (editorFrame.getTrackData().getHeader().getVersion() == 3)
		{
			getSideV3(track, editorFrame.getTrackData().getMainTrack().getLeft(), "l");
			getSideV3(track, editorFrame.getTrackData().getMainTrack().getRight(), "r");
			getPitsV3(track);
		}
		else
		{
			com = new Comment("Left part of track");
			track.addContent(com);
			if (editorFrame.getTrackData().getMainTrack().getLeft().getHasSide())
				track.addContent(getSide(editorFrame.getTrackData().getMainTrack().getLeft(), "Left", true));
			if (editorFrame.getTrackData().getMainTrack().getLeft().getHasBorder())
				track.addContent(getBorder(editorFrame.getTrackData().getMainTrack().getLeft(), "Left"));
			if (editorFrame.getTrackData().getMainTrack().getLeft().getHasBarrier())
				track.addContent(getBarrier(editorFrame.getTrackData().getMainTrack().getLeft(), "Left"));
			com = new Comment("End of left part");
			track.addContent(com);
			com = new Comment("Right part of track");
			track.addContent(com);
			if (editorFrame.getTrackData().getMainTrack().getRight().getHasSide())
				track.addContent(getSide(editorFrame.getTrackData().getMainTrack().getRight(), "Right", true));
			if (editorFrame.getTrackData().getMainTrack().getRight().getHasBorder())
				track.addContent(getBorder(editorFrame.getTrackData().getMainTrack().getRight(), "Right"));
			if (editorFrame.getTrackData().getMainTrack().getRight().getHasBarrier())
				track.addContent(getBarrier(editorFrame.getTrackData().getMainTrack().getRight(), "Right"));
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
		boolean options = editorFrame.getGameType().hasPitOptions();
		if (options)
			addContent(pits, "pit style", null, editorFrame.getPits().getStyle());
		else if (editorFrame.getPits().getStyle() == 1)
			// TORCS track files name the pit type, though the game doesn't read it
			addContent(pits, "type", "track side");
		addContent(pits, "side", editorFrame.getPits().getSide());
		addContent(pits, "entry", editorFrame.getPits().getEntry());
		addContent(pits, "start", editorFrame.getPits().getStart());
		if (options)
		{
			addContent(pits, "start buildings", editorFrame.getPits().getStartBuildings());
			addContent(pits, "stop buildings", editorFrame.getPits().getStopBuildings());
			addContent(pits, "max pits", null, editorFrame.getPits().getMaxPits());
		}
		addContent(pits, "end", editorFrame.getPits().getEnd());
		addContent(pits, "exit", editorFrame.getPits().getExit());
		addContent(pits, "length", "m", editorFrame.getPits().getLength());
		addContent(pits, "width", "m", editorFrame.getPits().getWidth());
		if (options)
			addContent(pits, "pit indicator", null, editorFrame.getPits().getIndicator());
		addContent(pits, "speed limit", editorFrame.getPits().getSpeedLimit());

		return pits;
	}

	/**
	 * @return
	 */
	private synchronized void getPitsV3(Element pits)
	{
		if (editorFrame.getPits().getStyle() == 0)
		{
			addContent(pits, "pit type", "no pit");
		}
		else if (editorFrame.getPits().getStyle() == 1)
		{
			addContent(pits, "pit type", "track side");
		}
		else if (editorFrame.getPits().getStyle() == 2)
		{
			addContent(pits, "pit type", "seperate path");
		}

		addContent(pits, "pit side", editorFrame.getPits().getSide());
		addContent(pits, "pit entry", editorFrame.getPits().getEntry());
		addContent(pits, "pit start", editorFrame.getPits().getStart());
		addContent(pits, "start buildings", editorFrame.getPits().getStartBuildings());
		addContent(pits, "stop buildings", editorFrame.getPits().getStopBuildings());
		addContent(pits, "max pits", null, editorFrame.getPits().getMaxPits());
		addContent(pits, "pit end", editorFrame.getPits().getEnd());
		addContent(pits, "pit exit", editorFrame.getPits().getExit());
		addContent(pits, "pit length", "m", editorFrame.getPits().getLength());
		addContent(pits, "pit width", "m", editorFrame.getPits().getWidth());
		addContent(pits, "speed limit", editorFrame.getPits().getSpeedLimit());
	}

	/**
	 * @return
	 */
	private synchronized Element getSegments()
	{
		SegmentVector segments = editorFrame.getTrackData().getSegments();
		Segment prev = null;
		Attribute name = null;
		if (editorFrame.getTrackData().getHeader().getVersion() == 3)
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
		if (editorFrame.getGameType().hasSegmentPenalty())
			addContent(segment, "penalty", null, shape.getPenalty());
		addContent(segment, "env map index", null, shape.getEnvMapIndex());
		if (editorFrame.getGameType().hasSpeedDreamsExtensions())
			addContent(segment, "DoV factor", null, shape.getDovFactor());

		if (editorFrame.getTrackData().getHeader().getVersion() == 3)
		{
			getSideV3(segment, shape.getLeft(), "l");
			getSideV3(segment, shape.getRight(), "r");
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
	private synchronized void getSideV3(Element side, SegmentSide part, String sPart)
	{
		if (!Double.isNaN(part.getSideStartWidth()) &&
			!Double.isNaN(part.getSideEndWidth()) &&
			part.getSideStartWidth() == part.getSideEndWidth())
		{
			addContent(side, sPart + "side width", "m", part.getSideStartWidth());
		}
		else
		{
			addContent(side, sPart + "side start width", "m", part.getSideStartWidth());
			addContent(side, sPart + "side end width", "m", part.getSideEndWidth());
		}
		addContent(side, sPart + "side surface", part.getSideSurface());
		addContent(side, sPart + "side type", part.getSideBankingType());

		addContent(side, sPart + "border width", "m", part.getBorderWidth());
		addContent(side, sPart + "border height", "m", part.getBorderHeight());
		addContent(side, sPart + "border surface", part.getBorderSurface());
		addContent(side, sPart + "border style", part.getBorderStyle());

		addContent(side, sPart + "barrier width", "m", part.getBarrierWidth());
		addContent(side, sPart + "barrier height", "m", part.getBarrierHeight());
		addContent(side, sPart + "barrier surface", part.getBarrierSurface());
		addContent(side, sPart + "barrier style", barrierStyle(part.getBarrierStyle()));
	}

	/**
	 * @param left
	 * @return
	 */
	private synchronized Element getSide(SegmentSide part, String sPart, boolean mainTrack)
	{
		Element side = new Element("section");
		side.setAttribute(new Attribute("name", sPart + " Side"));

		if (!mainTrack && !editorFrame.getGameType().widthSetsBothSideWidths())
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

		if (editorFrame.getTrackData().getHeader().getVersion() == 3)
		{
			surfaces = new Element("section");
			surfaces.setAttribute(new Attribute("name", "List"));
			root.addContent(surfaces);
		}

		if (editorFrame.getTrackData().getHasSurfacesReference())
			surfaces.setText("&default-surfaces;");

		Vector<Surface> surfaceData = editorFrame.getTrackData().getSurfaces();

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
			if (editorFrame.getGameType().hasTorcsTrackgenKeys())
				addContent(el, "texture size v", "m", surface.getTextureSizeV());
			addContent(el, "texture randomness", "m", surface.getTextureRandomness());
			addContent(el, "texture link with previous", surface.getTextureLinkWithPrevious());
			addContent(el, "texture start on boundary", surface.getTextureStartOnBoundary());
			addContent(el, "texture mipmap", "m", surface.getTextureMipMap());
			addContent(el, "friction", null, surface.getFriction());
			if (editorFrame.getGameType().hasSpeedDreamsExtensions())
				addContent(el, "friction rain", null, surface.getFrictionRain());
			addContent(el, "rolling resistance", null, surface.getRollingResistance());
			if (editorFrame.getGameType().hasSpeedDreamsExtensions())
				addContent(el, "rolling resistance rain", null, surface.getRollingResistanceRain());
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

		Vector<Camera> cameraData = editorFrame.getTrackData().getCameras();

		if (cameraData == null)
			return cameras;

		if (editorFrame.getTrackData().getHeader().getVersion() == 3)
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
			Segment segment = editorFrame.getSegment(camera.getSegment());
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

		Vector<TrackLight> lightData = editorFrame.getTrackData().getTrackLights();

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

		Georeference data = editorFrame.getTrackData().getGeoreference();

		addContent(element, "latitude", "deg", data.getLatitude());
		addContent(element, "longitude", "deg", data.getLongitude());
		addContent(element, "altitude", "m", data.getAltitude());
		addContent(element, "heading", "deg", data.getHeading());

		return element;
	}

	private synchronized Element getSectors()
	{
		Element sectors = new Element("section");
		sectors.setAttribute(new Attribute("name", "Sectors"));

		Vector<Sector> sectorData = editorFrame.getTrackData().getSectors();

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
		if (editorFrame.getTrackData().getHasObjectsReference())
			objects.setText("&default-objects;");

		Vector<TrackObject> objectData = editorFrame.getTrackData().getObjects();

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
			boolean speedDreams = editorFrame.getGameType().hasSpeedDreamsTrackgenKeys();
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
			if (editorFrame.getGameType().hasUseMaterial())
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
		if (!editorFrame.getGameType().hasFence1Fence2() && style != null && (style.equals("fence1") || style.equals("fence2")))
			return "fence";

		return style;
	}

	private synchronized String getCredit()
	{
		String sep = System.getProperty("file.separator");
		String filename = Editor.getProperties().getPath();
		String trackname = filename.substring(filename.lastIndexOf(sep) + 1);
		String tmp = "\n";
		tmp += "file                : " + trackname + ".xml\n";
		tmp += "auto generated      : by " + TrackEditor.getAppName() + "\n";
		tmp += "version             : " + Editor.getProperties().version + "\n";
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

		// TORCS reads none of subcategory, sky version, copyright and email; only CSR has a short name
		boolean extensions = editorFrame.getGameType().hasSpeedDreamsExtensions();
		addContent(header, "name", editorFrame.getTrackData().getHeader().getName());
		if (editorFrame.getGameType().hasShortName())
			addContent(header, "short name", editorFrame.getTrackData().getHeader().getShortName());

		if (editorFrame.getTrackData().getHeader().getCategory() != null)
		{
			tmp = editorFrame.getTrackData().getHeader().getCategory();
		} else
		{
			tmp = "road";
		}
		addContent(header, "category", tmp);
		if (extensions)
			addContent(header, "subcategory", editorFrame.getTrackData().getHeader().getSubcategory());
		if (editorFrame.getGameType().hasCarCategory())
			addContent(header, "car category", editorFrame.getTrackData().getHeader().getCarCategory());
		if (editorFrame.getGameType().hasDisabledPenalties())
			addContent(header, "disabled penalties", editorFrame.getTrackData().getHeader().getDisabledPenalties());
		addContent(header, "version", null, editorFrame.getTrackData().getHeader().getVersion());
		if (extensions)
			addContent(header, "sky version", null, editorFrame.getTrackData().getHeader().getSkyVersion());

		if (editorFrame.getTrackData().getHeader().getAuthor() != null)
		{
			tmp = editorFrame.getTrackData().getHeader().getAuthor();
		} else
		{
			tmp = "Anonymous";
		}
		addContent(header, "author", tmp);

		if (extensions && editorFrame.getTrackData().getHeader().getCopyright() != null)
		{
			tmp = editorFrame.getTrackData().getHeader().getCopyright();
			addContent(header, "copyright", tmp);
		}

		if (extensions && editorFrame.getTrackData().getHeader().getEmail() != null)
		{
			tmp = editorFrame.getTrackData().getHeader().getEmail();
			addContent(header, "email", tmp);
		}

		if (editorFrame.getTrackData().getHeader().getDescription() != null)
		{
			tmp = editorFrame.getTrackData().getHeader().getDescription();
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

		addContent(element, "station", editorFrame.getTrackData().getLocalInfo().getStation());
		addContent(element, "timezone", null, editorFrame.getTrackData().getLocalInfo().getTimezone());
		addContent(element, "overall rain likelyhood", "%", editorFrame.getTrackData().getLocalInfo().getOverallRainLikelyhood());
		addContent(element, "little rain likelyhood", "%", editorFrame.getTrackData().getLocalInfo().getLittleRainLikelyhood());
		addContent(element, "medium rain likelyhood", "%", editorFrame.getTrackData().getLocalInfo().getMediumRainLikelyhood());
		addContent(element, "time of day", "hour", editorFrame.getTrackData().getLocalInfo().getTimeOfDay());
		addContent(element, "sun ascension", "deg", editorFrame.getTrackData().getLocalInfo().getSunAscension());
		addContent(element, "altitude", "m", editorFrame.getTrackData().getLocalInfo().getAltitude());
		// Speed Dreams reads latitude/longitude without a unit and expects plain degrees,
		// so only write a unit if the track file had one
		addContent(element, "latitude", editorFrame.getTrackData().getLocalInfo().getLatitudeUnits(), editorFrame.getTrackData().getLocalInfo().getLatitude());
		addContent(element, "longitude", editorFrame.getTrackData().getLocalInfo().getLongitudeUnits(), editorFrame.getTrackData().getLocalInfo().getLongitude());
		addContent(element, "climat", null, editorFrame.getTrackData().getLocalInfo().getClimat());
		addContent(element, "precipitation", null, editorFrame.getTrackData().getLocalInfo().getPrecipitation());
		if (editorFrame.getGameType().hasBestLap())
			addContent(element, "best lap", null, editorFrame.getTrackData().getLocalInfo().getBestLap());

		return element;
	}

	private synchronized Element getGrid()
	{
		Element element = new Element("section");
		element.setAttribute(new Attribute("name", "Starting Grid"));

		addContent(element, "rows", null, editorFrame.getTrackData().getStartingGrid().getRows());
		addContent(element, "pole position side", editorFrame.getTrackData().getStartingGrid().getPolePositionSide());
		addContent(element, "distance to start", "m", editorFrame.getTrackData().getStartingGrid().getDistanceToStart());
		addContent(element, "distance between columns", "m", editorFrame.getTrackData().getStartingGrid().getDistanceBetweenColumns());
		addContent(element, "offset within a column", "m", editorFrame.getTrackData().getStartingGrid().getOffsetWithinAColumn());
		addContent(element, "initial height", "m", editorFrame.getTrackData().getStartingGrid().getInitialHeight());

		return element;
	}

	/**
	 * @return
	 */
	private synchronized Element getGraphic()
	{
		String sep = System.getProperty("file.separator");
		String filename = Editor.getProperties().getPath();
		String trackname = filename.substring(filename.lastIndexOf(sep) + 1);

		Element element = new Element("section");
		element.setAttribute(new Attribute("name", "Graphic"));

		if (editorFrame.getTrackData().getGraphic().getDescription() == null)
			addContent(element, "3d description", trackname + ".ac");
		else
			addContent(element, "3d description", editorFrame.getTrackData().getGraphic().getDescription());
		if (editorFrame.getGameType().hasSpeedDreamsExtensions())
		{
			addContent(element, "3d description night", editorFrame.getTrackData().getGraphic().getDescriptionNight());
			addContent(element, "3d description rain+night", editorFrame.getTrackData().getGraphic().getDescriptionRainNight());
		}

		// the per-type 3d object files are only used by CSR
		if (editorFrame.getGameType().hasTypeObjectFiles())
		{
			addContent(element, "3d separate objects", editorFrame.getTrackData().getGraphic().getSeparateObjects());
			addContent(element, "3d terrain", editorFrame.getTrackData().getGraphic().getTerrainObjects());
			addContent(element, "3d road", editorFrame.getTrackData().getGraphic().getRoadObjects());
			addContent(element, "3d road2", editorFrame.getTrackData().getGraphic().getRoad2Objects());
			addContent(element, "3d trees", editorFrame.getTrackData().getGraphic().getTreesObjects());
			addContent(element, "3d building", editorFrame.getTrackData().getGraphic().getBuildingObjects());
			addContent(element, "3d house", editorFrame.getTrackData().getGraphic().getHouseObjects());
			addContent(element, "3d cars", editorFrame.getTrackData().getGraphic().getCarsObjects());
			addContent(element, "3d truck", editorFrame.getTrackData().getGraphic().getTruckObjects());
			addContent(element, "3d tribunes", editorFrame.getTrackData().getGraphic().getTribunesObjects());
			addContent(element, "3d pub", editorFrame.getTrackData().getGraphic().getPubObjects());
			addContent(element, "3d decors", editorFrame.getTrackData().getGraphic().getDecorsObjects());
			addContent(element, "3d water", editorFrame.getTrackData().getGraphic().getWaterObjects());
			addContent(element, "3d grass", editorFrame.getTrackData().getGraphic().getGrassObjects());
			addContent(element, "3d sand", editorFrame.getTrackData().getGraphic().getSandObjects());
			addContent(element, "3d rock", editorFrame.getTrackData().getGraphic().getRockObjects());
			addContent(element, "3d curb", editorFrame.getTrackData().getGraphic().getCurbObjects());
			addContent(element, "3d fence", editorFrame.getTrackData().getGraphic().getFenceObjects());
			addContent(element, "3d barrier", editorFrame.getTrackData().getGraphic().getBarrierObjects());
			addContent(element, "3d wall", editorFrame.getTrackData().getGraphic().getWallObjects());
			addContent(element, "3d environment", editorFrame.getTrackData().getGraphic().getEnvironmentObjects());
		}

		addContent(element, "background image", editorFrame.getTrackData().getGraphic().getBackgroundImage());
		addContent(element, "background type", null, editorFrame.getTrackData().getGraphic().getBackgroundType());
		addContent(element, "background color R", null, editorFrame.getTrackData().getGraphic().getBackgroundColorR());
		addContent(element, "background color G", null, editorFrame.getTrackData().getGraphic().getBackgroundColorG());
		addContent(element, "background color B", null, editorFrame.getTrackData().getGraphic().getBackgroundColorB());
		addContent(element, "ambient color R", null, editorFrame.getTrackData().getGraphic().getAmbientColorR());
		addContent(element, "ambient color G", null, editorFrame.getTrackData().getGraphic().getAmbientColorG());
		addContent(element, "ambient color B", null, editorFrame.getTrackData().getGraphic().getAmbientColorB());
		addContent(element, "diffuse color R", null, editorFrame.getTrackData().getGraphic().getDiffuseColorR());
		addContent(element, "diffuse color G", null, editorFrame.getTrackData().getGraphic().getDiffuseColorG());
		addContent(element, "diffuse color B", null, editorFrame.getTrackData().getGraphic().getDiffuseColorB());
		addContent(element, "specular color R", null, editorFrame.getTrackData().getGraphic().getSpecularColorR());
		addContent(element, "specular color G", null, editorFrame.getTrackData().getGraphic().getSpecularColorG());
		addContent(element, "specular color B", null, editorFrame.getTrackData().getGraphic().getSpecularColorB());
		addContent(element, "light position x", "m", editorFrame.getTrackData().getGraphic().getLightPositionX());
		addContent(element, "light position y", "m", editorFrame.getTrackData().getGraphic().getLightPositionY());
		addContent(element, "light position z", "m", editorFrame.getTrackData().getGraphic().getLightPositionZ());
		addContent(element, "shininess", null, editorFrame.getTrackData().getGraphic().getShininess());
		addContent(element, "fov factor", null, editorFrame.getTrackData().getGraphic().getFovFactor());

		boolean torcs = editorFrame.getGameType() == EditorFrame.GameType.TORCS;
		if (!torcs ||
			(!editorFrame.getTrackData().getGraphic().getTurnMarks().isEmpty() ||
			 !editorFrame.getTrackData().getGraphic().getTurnMarks().isDefault()))
			element.addContent(getTurnMarks());
		element.addContent(getTerrainGeneration());
		element.addContent(getEnvironmentMapping());

		return element;
	}

	private synchronized Element getTurnMarks()
	{
		Element element = new Element("section");
		element.setAttribute(new Attribute("name", "Turn Marks"));

		addContent(element, "width", "m", editorFrame.getTrackData().getGraphic().getTurnMarks().getWidth());
		addContent(element, "height", "m", editorFrame.getTrackData().getGraphic().getTurnMarks().getHeight());
		addContent(element, "vertical space", "m", editorFrame.getTrackData().getGraphic().getTurnMarks().getVerticalSpace());
		addContent(element, "horizontal space", "m", editorFrame.getTrackData().getGraphic().getTurnMarks().getHorizontalSpace());

		return element;
	}

	private synchronized Element getTerrainGeneration()
	{
		Element element = new Element("section");
		element.setAttribute(new Attribute("name", "Terrain Generation"));

		addContent(element, "track step", "m", editorFrame.getTerrainGeneration().getTrackStep());
		addContent(element, "border margin", "m", editorFrame.getTerrainGeneration().getBorderMargin());
		addContent(element, "border step", "m", editorFrame.getTerrainGeneration().getBorderStep());
		boolean torcs = editorFrame.getGameType().hasTorcsTrackgenKeys();
		if (torcs)
			addContent(element, "terrain transition", "m", editorFrame.getTerrainGeneration().getTerrainTransition());
		addContent(element, "border height", "m", editorFrame.getTerrainGeneration().getBorderHeight());
		addContent(element, "orientation", editorFrame.getTerrainGeneration().getOrientation());
		addContent(element, "maximum altitude", "m", editorFrame.getTerrainGeneration().getMaximumAltitude());
		addContent(element, "minimum altitude", "m", editorFrame.getTerrainGeneration().getMinimumAltitude());
		addContent(element, "group size", "m", editorFrame.getTerrainGeneration().getGroupSize());
		addContent(element, "elevation map", editorFrame.getTerrainGeneration().getElevationMap());
		// reliefs drawn without a relief file go to <track>-relief.ac
		if (!editorFrame.getReliefs().getReliefs().isEmpty() &&
			(editorFrame.getTerrainGeneration().getReliefFile() == null || editorFrame.getTerrainGeneration().getReliefFile().isEmpty()))
		{
			String path = Editor.getProperties().getPath();
			editorFrame.getReliefs().setNewFileName(path.substring(path.lastIndexOf(System.getProperty("file.separator")) + 1) + "-relief.ac");
		}

		addContent(element, "relief file", editorFrame.getTerrainGeneration().getReliefFile());

		if (editorFrame.getReliefs().getChanged())
		{
			try
			{
				editorFrame.getReliefs().writeFile();
			}
			catch (Exception ex)
			{
				addFileError("Relief file", editorFrame.getTerrainGeneration().getReliefFile(), ex);
			}
		}

		addContent(element, "relief border", editorFrame.getTerrainGeneration().getReliefBorder());
		addContent(element, "surface", editorFrame.getTerrainGeneration().getSurface());
		addContent(element, "random seed", null, editorFrame.getTerrainGeneration().getRandomSeed());
		if (editorFrame.getGameType().hasSpeedDreamsTrackgenKeys())
			addContent(element, "use object materials", editorFrame.getTerrainGeneration().getUseObjectMaterials());
		if (torcs)
		{
			addContent(element, "crossing mode", editorFrame.getTerrainGeneration().getCrossingMode());
			addContent(element, "crossing bridge socket gap", "m", editorFrame.getTerrainGeneration().getCrossingBridgeSocketGap());
			addContent(element, "crossing bridge thickness", "m", editorFrame.getTerrainGeneration().getCrossingBridgeThickness());
			for (int i = 0; i < TerrainGeneration.CROSSING_BRIDGE_SURFACE_KEYS.length; i++)
				addContent(element, TerrainGeneration.CROSSING_BRIDGE_SURFACE_KEYS[i], editorFrame.getTerrainGeneration().getCrossingBridgeSurface(i));
		}
		if (editorFrame.getGameType().hasTiledFile())
			addContent(element, "tiled file", editorFrame.getTerrainGeneration().getTiledFile());

		if (!torcs || !editorFrame.getTerrainGeneration().getObjectMaps().isEmpty())
			element.addContent(getObjectMaps());
		// only Speed Dreams trackgen places individual graphic objects
		if (editorFrame.getGameType().hasGraphicObjects())
			element.addContent(getGraphicObjects());

		return element;
	}

	private synchronized Element getEnvironmentMapping()
	{
		Element element = new Element("section");
		element.setAttribute(new Attribute("name", "Environment Mapping"));

		Vector<EnvironmentMapping> environment = editorFrame.getTrackData().getGraphic().getEnvironmentMapping();

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

		for (GraphicObject graphicObject : editorFrame.getGraphicObjects())
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

		Vector<ObjectMap> objMaps = editorFrame.getObjectMaps();

		for (int i = 0; i < objMaps.size(); i++)
		{
			ObjectMap data = objMaps.get(i);

			Element el = new Element("section");
			el.setAttribute(new Attribute("name", data.getName()));

			addContent(el, "object map", data.getObjectMap());

			element.addContent(el);

			if (data.getChanged())
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
			section.addContent(attstrElement(attribute, string.replace("\"", "&quot;")));
		}
	}
}
