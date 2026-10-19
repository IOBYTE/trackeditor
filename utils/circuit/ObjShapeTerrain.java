package utils.circuit;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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
	public ObjShapeTerrain()
	{
		super("terrain");
	}

	public void calcShape(EditorFrame editorFrame, Rectangle2D.Double boundingRectangle)
	{
		// TODO get border and holes from relief file someday
		if (points == null)
		{
			points = new Point3D[4]; // 4 points in 2D
			for (int i = 0; i < points.length; i++)
				points[i] = new Point3D();

			trPoints = new Point2D.Double[4];
			for (int i = 0; i < trPoints.length; i++)
				trPoints[i] = new Point2D.Double();
		}

		double border = editorFrame.getTrackData().getGraphic().getTerrainGeneration().getBorderMargin();
		
		points[0].x = boundingRectangle.getX() - border;
		points[0].y = boundingRectangle.getY() - border;

		points[1].x = boundingRectangle.getX() + boundingRectangle.getWidth() + border;
		points[1].y = boundingRectangle.getY() - border;

		points[2].x = boundingRectangle.getX() + boundingRectangle.getWidth() + border;
		points[2].y = boundingRectangle.getY() + boundingRectangle.getHeight() + border;

		points[3].x = boundingRectangle.getX() - border;
		points[3].y = boundingRectangle.getY() + boundingRectangle.getHeight() + border;
	}
}