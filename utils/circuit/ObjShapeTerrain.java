package utils.circuit;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import gui.EditorFrame;



/**
 * <p>
 * Titre : Torcs Tune
 * </p>
 * <p>
 * Description : Torcs tuning
 * </p>
 * <p>
 * Copyright : Copyright (c) 2002 Patrice Espie
 * </p>
 * <p>
 * Societe
 * </p>
 * 
 * @author Patrice Espie
 * @version 0.1a
 */

public class ObjShapeTerrain extends Segment
{
	/** where a point is: on the terrain, outside its outer edge, or in a hole */
	public enum Place { ON, OUTSIDE, IN_HOLE };

	// the terrain's outer edges and its holes, in track coordinates
	private final List<Path2D.Double>	outers	= new ArrayList<Path2D.Double>();
	private final List<Path2D.Double>	holes	= new ArrayList<Path2D.Double>();

	public ObjShapeTerrain()
	{
		super("terrain");
	}

	/**
	 * Finds where trackgen makes terrain, like its EasyMesh triangulation:
	 * <ul>
	 * <li>With the border (Relief border isn't "yes"), the outer edge is the track's
	 * bounds and the border margin around them.</li>
	 * <li>Without it (Relief border "yes", trackgen -B), the triangulation fills the
	 * convex hull of the track and the relief lines, unless a closed exterior relief
	 * line that runs counterclockwise goes around the whole track: then that line is the
	 * outer edge (erase() removes what's between it and the hull).</li>
	 * <li>A closed relief line, interior or exterior, that runs clockwise is a hole:
	 * EasyMesh's erase() removes the triangles whose corners are all on one closed
	 * line and wind backwards.</li>
	 * </ul>
	 */
	public void calcShape(EditorFrame editorFrame, Rectangle2D.Double boundingRectangle)
	{
		outers.clear();
		holes.clear();
		if (boundingRectangle == null || editorFrame.getTrackData() == null)
			return;

		TerrainGeneration terrain = editorFrame.getTrackData().getGraphic().getTerrainGeneration();
		boolean border = !"yes".equals(terrain.getReliefBorder());
		List<Point2D.Double> hullPoints = new ArrayList<Point2D.Double>();
		List<Point2D.Double> trackPoints = new ArrayList<Point2D.Double>();
		if (!border)
		{
			for (Segment segment : editorFrame.getTrackData().getSegments())
			{
				Point3D[] segmentPoints = segment.getPoints();
				if (segmentPoints == null)
					continue;
				for (Point3D point : segmentPoints)
				{
					if (point != null)
						trackPoints.add(new Point2D.Double(point.x, point.y));
				}
			}
			hullPoints.addAll(trackPoints);
		}

		Reliefs reliefs = editorFrame.getReliefs();
		if (reliefs != null)
		{
			for (ObjShapeRelief relief : reliefs.getReliefs())
			{
				// relief lines are relative to the track's bounds (see ObjShapeRelief.calcShape)
				List<Point2D.Double> ring = new ArrayList<Point2D.Double>();
				for (double[] vertex : relief.getVertices())
					ring.add(new Point2D.Double(vertex[0] + boundingRectangle.x, -vertex[2] + boundingRectangle.y));
				hullPoints.addAll(ring);

				if (!relief.isClosed() || ring.size() < 3)
					continue;
				double area = signedArea(ring);
				if (area < 0)
					holes.add(path(ring));
				else if (area > 0 && !border && relief.isExterior())
				{
					Path2D.Double outer = path(ring);
					if (containsAll(outer, trackPoints))
						outers.add(outer);
				}
			}
		}

		if (border)
		{
			double margin = terrain.getBorderMargin();
			// trackgen uses this when the track doesn't set one
			if (Double.isNaN(margin))
				margin = TerrainGeneration.DEFAULT_BORDER_MARGIN;

			List<Point2D.Double> ring = new ArrayList<Point2D.Double>();
			ring.add(new Point2D.Double(boundingRectangle.getMinX() - margin, boundingRectangle.getMinY() - margin));
			ring.add(new Point2D.Double(boundingRectangle.getMaxX() + margin, boundingRectangle.getMinY() - margin));
			ring.add(new Point2D.Double(boundingRectangle.getMaxX() + margin, boundingRectangle.getMaxY() + margin));
			ring.add(new Point2D.Double(boundingRectangle.getMinX() - margin, boundingRectangle.getMaxY() + margin));
			outers.add(path(ring));
		}
		else if (outers.isEmpty())
		{
			List<Point2D.Double> hull = convexHull(hullPoints);
			if (hull.size() >= 3)
				outers.add(path(hull));
		}
	}

	/**
	 * @return where the point is (see calcShape): trackgen only makes terrain, so an
	 *         object there only finds a height, inside an outer edge and not in a hole
	 */
	public Place place(double x, double y)
	{
		boolean inside = false;
		for (Path2D.Double outer : outers)
		{
			if (outer.contains(x, y))
			{
				inside = true;
				break;
			}
		}
		if (!inside)
			return Place.OUTSIDE;
		for (Path2D.Double hole : holes)
		{
			if (hole.contains(x, y))
				return Place.IN_HOLE;
		}
		return Place.ON;
	}

	public void draw(Graphics g, AffineTransform affineTransform)
	{
		Graphics2D g2 = (Graphics2D) g;
		for (Path2D.Double outer : outers)
			g2.draw(affineTransform.createTransformedShape(outer));
		for (Path2D.Double hole : holes)
			g2.draw(affineTransform.createTransformedShape(hole));
	}

	private static boolean containsAll(Path2D.Double path, List<Point2D.Double> points)
	{
		for (Point2D.Double point : points)
		{
			if (!path.contains(point))
				return false;
		}
		return true;
	}

	/** @return twice the area, positive when the ring runs counterclockwise */
	private static double signedArea(List<Point2D.Double> ring)
	{
		double area = 0;
		for (int i = 0; i < ring.size(); i++)
		{
			Point2D.Double p = ring.get(i);
			Point2D.Double q = ring.get((i + 1) % ring.size());
			area += p.x * q.y - q.x * p.y;
		}
		return area;
	}

	private static Path2D.Double path(List<Point2D.Double> ring)
	{
		Path2D.Double path = new Path2D.Double();
		path.moveTo(ring.get(0).x, ring.get(0).y);
		for (int i = 1; i < ring.size(); i++)
			path.lineTo(ring.get(i).x, ring.get(i).y);
		path.closePath();
		return path;
	}

	/** @return the convex hull, counterclockwise (Andrew's monotone chain) */
	private static List<Point2D.Double> convexHull(List<Point2D.Double> points)
	{
		List<Point2D.Double> sorted = new ArrayList<Point2D.Double>(points);
		sorted.sort((a, b) -> a.x != b.x ? Double.compare(a.x, b.x) : Double.compare(a.y, b.y));
		List<Point2D.Double> hull = new ArrayList<Point2D.Double>();
		if (sorted.size() < 3)
			return hull;
		for (int pass = 0; pass < 2; pass++)
		{
			int start = hull.size();
			for (int i = 0; i < sorted.size(); i++)
			{
				Point2D.Double p = sorted.get(pass == 0 ? i : sorted.size() - 1 - i);
				while (hull.size() >= start + 2 && cross(hull.get(hull.size() - 2), hull.get(hull.size() - 1), p) <= 0)
					hull.remove(hull.size() - 1);
				hull.add(p);
			}
			// the last point starts the other half
			hull.remove(hull.size() - 1);
		}
		return hull;
	}

	private static double cross(Point2D.Double o, Point2D.Double a, Point2D.Double b)
	{
		return (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x);
	}
}
