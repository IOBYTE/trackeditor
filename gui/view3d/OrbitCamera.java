/*
 *   OrbitCamera.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package gui.view3d;

/**
 * A camera that looks at a center point from a direction (yaw around the up axis,
 * pitch above the ground) and a distance. Rotating turns it around the center,
 * panning moves the center, zooming changes the distance. Heights are in the
 * view's units, that is with the height exaggeration applied.
 */
public class OrbitCamera
{
	public static final double	FOV_DEGREES	= 50;

	double	targetX, targetY, targetZ;
	double	yaw;
	double	pitch;
	double	distance	= 100;
	// the vertical field of view in degrees: wider to see what a track camera films
	double	fov			= FOV_DEGREES;
	// the track's size, for the near and far planes
	double	radius		= 100;

	/** Looks at the whole track, from above (top) or at an angle. */
	public void fit(RoadMesh mesh, double heightScale, boolean top)
	{
		yaw = top ? -Math.PI / 2 : -Math.PI / 2 + 0.6;
		pitch = top ? Math.toRadians(89) : 0.62;
		frame(mesh, heightScale);
	}

	/**
	 * Looks from eye in a direction (yaw around the up axis, pitch above the
	 * horizon), turning around the point at distance that way, with a field of view.
	 */
	public void lookFrom(double[] eye, double viewYaw, double viewPitch, double distance, double fov, RoadMesh mesh)
	{
		viewPitch = Math.max(Math.toRadians(-89), Math.min(Math.toRadians(85), viewPitch));
		this.distance = Math.max(0.5, distance);
		this.fov = fov;
		yaw = viewYaw + Math.PI;
		pitch = -viewPitch;
		double c = Math.cos(viewPitch);
		targetX = eye[0] + this.distance * c * Math.cos(viewYaw);
		targetY = eye[1] + this.distance * c * Math.sin(viewYaw);
		targetZ = eye[2] + this.distance * Math.sin(viewPitch);
		radius = mesh.getRadius();
	}

	/** Looks at the whole track from the current direction. */
	public void frame(RoadMesh mesh, double heightScale)
	{
		fov = FOV_DEGREES;
		targetX = (mesh.minX + mesh.maxX) / 2;
		targetY = (mesh.minY + mesh.maxY) / 2;
		targetZ = (mesh.minZ + mesh.maxZ) / 2 * heightScale;
		radius = mesh.getRadius();
		// looking down, the track's width fills the window: it needs a little less room
		boolean down = pitch > Math.toRadians(80);
		distance = radius / Math.tan(Math.toRadians(FOV_DEGREES / 2)) * (down ? 0.95 : 1.05);
	}

	public double[] eye()
	{
		double c = Math.cos(pitch);
		return new double[] { targetX + distance * c * Math.cos(yaw), targetY + distance * c * Math.sin(yaw), targetZ + distance * Math.sin(pitch) };
	}

	/** @return the screen's right direction in the world */
	double[] right()
	{
		return new double[] { -Math.sin(yaw), Math.cos(yaw), 0 };
	}

	/** @return the screen's up direction in the world */
	double[] up()
	{
		double c = Math.cos(pitch), s = Math.sin(pitch);
		// right x forward, forward = -(cos p cos y, cos p sin y, sin p)
		return new double[] { -s * Math.cos(yaw), -s * Math.sin(yaw), c };
	}

	/** Turns around the center by a mouse movement in pixels. */
	public void orbit(double dx, double dy)
	{
		yaw -= dx * 0.008;
		pitch = Math.max(Math.toRadians(-85), Math.min(Math.toRadians(89), pitch + dy * 0.008));
	}

	/** Moves the center so the track follows a mouse movement in pixels. */
	public void pan(double dx, double dy, int viewportHeight)
	{
		double perPixel = 2 * distance * Math.tan(Math.toRadians(fov / 2)) / Math.max(1, viewportHeight);
		double[] r = right(), u = up();
		targetX += (-r[0] * dx + u[0] * dy) * perPixel;
		targetY += (-r[1] * dx + u[1] * dy) * perPixel;
		targetZ += (-r[2] * dx + u[2] * dy) * perPixel;
	}

	/** Zooms by factor (below 1 is closer) toward a point, or the center when point is null. */
	public void zoom(double factor, double[] point)
	{
		if (point != null)
		{
			double k = 1 - factor;
			targetX += (point[0] - targetX) * k;
			targetY += (point[1] - targetY) * k;
			targetZ += (point[2] - targetZ) * k;
		}
		distance = Math.max(0.5, distance * factor);
	}

	public double near()
	{
		return Math.max(0.05, distance / 1000);
	}

	public double far()
	{
		return distance * 2 + radius * 4;
	}

	/**
	 * @return the direction (yaw, pitch), the distance, the point looked at with its
	 *         height without the exaggeration, and the field of view: what
	 *         {@link #setState} takes
	 */
	public double[] state(double heightScale)
	{
		return new double[] { Math.IEEEremainder(yaw, 2 * Math.PI), pitch, distance, targetX, targetY, targetZ / heightScale, fov };
	}

	/** Looks from a {@link #state}, at a track the mesh's size. */
	public void setState(double[] state, RoadMesh mesh, double heightScale)
	{
		fov = Math.max(10, Math.min(120, state[6]));
		yaw = state[0];
		pitch = Math.max(Math.toRadians(-85), Math.min(Math.toRadians(89), state[1]));
		distance = Math.max(0.5, state[2]);
		targetX = state[3];
		targetY = state[4];
		targetZ = state[5] * heightScale;
		radius = mesh.getRadius();
	}

	/** Changes the height exaggeration, keeping the view on the same part of the track. */
	public void scaleHeights(double factor)
	{
		targetZ *= factor;
	}
}
