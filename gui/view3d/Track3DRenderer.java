/*
 *   Track3DRenderer.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 */
package gui.view3d;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.fixedfunc.GLPointerFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

/**
 * Draws the road with OpenGL (the fixed-function pipeline, which every desktop
 * driver has): filled and lit, or as a wireframe of the triangles' edges.
 */
public class Track3DRenderer implements GLEventListener
{
	public enum Coloring { TEXTURE, SURFACE, HEIGHT, PLAIN };

	// what to draw and how; set on the event thread, read when drawing
	private volatile RoadMesh		mesh;
	private volatile boolean		wireframe	= false;
	private volatile boolean		lighting	= true;
	private volatile boolean		culling		= false;
	private volatile boolean		raceline	= true;
	private volatile Coloring		coloring	= Coloring.TEXTURE;
	private volatile double			heightScale	= 1;
	private volatile boolean		buffersStale	= true;
	final OrbitCamera				camera		= new OrbitCamera();

	// a zoom toward the road under the cursor, done after the next frame (it needs its depth)
	private volatile int			zoomX, zoomY;
	private volatile double			zoomFactor	= 0;

	private final Track3DWindow		window;
	private final GLU				glu			= new GLU();
	private FloatBuffer				vertices, normals, colors, texCoords, racelineCoords, lines;
	private int						drawnTriangles;
	// the triangles are sorted by texture and sidedness: the texture and raceline texture of
	// each run, its first vertex, its vertex count and whether it is two-sided (1)
	private final List<Texture>		runTextures		= new ArrayList<Texture>();
	private final List<Texture>		runRacelines	= new ArrayList<Texture>();
	private final List<int[]>		runs			= new ArrayList<int[]>();
	// the strips with textures that can be seen through, drawn one by one
	private final List<SeeThrough>	seeThrough		= new ArrayList<SeeThrough>();
	// the textures loaded so far, by file and time, and the ones that couldn't be read
	private final Map<String, Texture>	textures	= new HashMap<String, Texture>();
	// the textures with parts that can be seen through, such as a fence's gaps
	private final java.util.Set<Texture>	translucent	= new HashSet<Texture>();
	private final Map<String, String>	textureErrors	= new HashMap<String, String>();
	private float					anisotropy		= 0;
	private int						width = 1, height = 1;
	private String					renderer	= "";

	Track3DRenderer(Track3DWindow window)
	{
		this.window = window;
	}

	void setMesh(RoadMesh mesh)
	{
		this.mesh = mesh;
		buffersStale = true;
	}

	RoadMesh getMesh()
	{
		return mesh;
	}

	void setWireframe(boolean wireframe)
	{
		this.wireframe = wireframe;
	}

	void setLighting(boolean lighting)
	{
		this.lighting = lighting;
	}

	/** Hides the back of one-sided surfaces, as the game does. */
	void setCulling(boolean culling)
	{
		this.culling = culling;
	}

	/** Shows the raceline layer on the road, where its surface has one. */
	void setRaceline(boolean raceline)
	{
		this.raceline = raceline;
	}

	void setColoring(Coloring coloring)
	{
		this.coloring = coloring;
		buffersStale = true;
	}

	void setHeightScale(double heightScale)
	{
		camera.scaleHeights(heightScale / this.heightScale);
		this.heightScale = heightScale;
		buffersStale = true;
	}

	double getHeightScale()
	{
		return heightScale;
	}

	/** Zooms toward the road under a pixel (from the top left, in surface pixels) after the next frame. */
	void zoomAt(int x, int y, double factor)
	{
		zoomX = x;
		zoomY = y;
		zoomFactor = factor;
	}

	int getHeight()
	{
		return height;
	}

	int getWidth()
	{
		return width;
	}

	@Override
	public void init(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
		renderer = gl.glGetString(GL.GL_RENDERER) + ", OpenGL " + gl.glGetString(GL.GL_VERSION);
		window.rendererFound(renderer);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glShadeModel(GLLightingFunc.GL_FLAT);
		gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE);
		gl.glEnable(GLLightingFunc.GL_NORMALIZE);
		// the normals follow trackgen's winding: a back face is lit from its own side
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1);
		gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, new float[] { 0.38f, 0.38f, 0.38f, 1 }, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, new float[] { 0.7f, 0.7f, 0.68f, 1 }, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, new float[] { 0, 0, 0, 1 }, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, new float[] { 0, 0, 0, 1 }, 0);
		gl.glEnable(GLLightingFunc.GL_LIGHT0);
		gl.glEnable(GL.GL_MULTISAMPLE);
		if (gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic"))
		{
			float[] max = new float[1];
			gl.glGetFloatv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max, 0);
			anisotropy = Math.min(8, max[0]);
		}
		// a new context has none of the textures
		textures.clear();
		translucent.clear();
		textureErrors.clear();
		buffersStale = true;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		width = Math.max(1, w);
		height = Math.max(1, h);
	}

	@Override
	public void dispose(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
		for (Texture texture : textures.values())
			texture.destroy(gl);
		textures.clear();
		translucent.clear();
	}

	@Override
	public void display(GLAutoDrawable drawable)
	{
		long start = System.nanoTime();
		GL2 gl = drawable.getGL().getGL2();
		RoadMesh mesh = this.mesh;

		if (buffersStale && mesh != null)
		{
			fillBuffers(gl, mesh);
			buffersStale = false;
		}

		gl.glViewport(0, 0, width, height);
		gl.glClearColor(0.62f, 0.74f, 0.86f, 1);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		drawBackground(gl);

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(camera.fov, (double) width / height, camera.near(), camera.far());
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		double[] eye = camera.eye();
		glu.gluLookAt(eye[0], eye[1], eye[2], camera.targetX, camera.targetY, camera.targetZ, 0, 0, 1);
		// a sun from the south-west, high up; set after the camera so it stays fixed in the world
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, new float[] { -0.35f, -0.55f, 0.76f, 0 }, 0);

		if (drawnTriangles > 0)
		{
			gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			if (wireframe)
			{
				// every edge, near or far
				gl.glDisable(GLLightingFunc.GL_LIGHTING);
				gl.glDisable(GL.GL_DEPTH_TEST);
				// dark, on the light sky
				gl.glColor3f(0.16f, 0.2f, 0.25f);
				gl.glLineWidth(1);
				gl.glVertexPointer(3, GL.GL_FLOAT, 0, lines);
				gl.glDrawArrays(GL.GL_LINES, 0, drawnTriangles * 6);
				gl.glEnable(GL.GL_DEPTH_TEST);
			}
			else
			{
				if (lighting)
					gl.glEnable(GLLightingFunc.GL_LIGHTING);
				else
					gl.glDisable(GLLightingFunc.GL_LIGHTING);
				gl.glEnableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
				gl.glEnableClientState(GLPointerFunc.GL_COLOR_ARRAY);
				gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertices);
				gl.glNormalPointer(GL.GL_FLOAT, 0, normals);
				gl.glColorPointer(3, GL.GL_FLOAT, 0, colors);
				gl.glEnableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
				gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, texCoords);
				// a texture's clear parts (a fence's gaps) aren't drawn, and don't hide what is behind
				gl.glEnable(GL2.GL_ALPHA_TEST);
				gl.glAlphaFunc(GL.GL_GREATER, 0.02f);
				// the raceline layer's coordinates, for the second texture unit
				gl.glClientActiveTexture(GL.GL_TEXTURE1);
				gl.glEnableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
				gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, racelineCoords);
				gl.glClientActiveTexture(GL.GL_TEXTURE0);
				// what can't be seen through first
				for (int r = 0; r < runs.size(); r++)
				{
					if (translucent.contains(runTextures.get(r)))
						continue;
					bind(gl, runTextures.get(r), runRacelines.get(r), runs.get(r)[2]);
					gl.glDrawArrays(GL.GL_TRIANGLES, runs.get(r)[0], runs.get(r)[1]);
				}
				// then the strips with textures that can be seen through, blended as the games do
				// (a fence's wires fade with the distance instead of breaking up), from the
				// farthest from the camera to the nearest, as Speed Dreams sorts its transparent
				// objects, so a near one doesn't hide one behind it
				gl.glEnable(GL.GL_BLEND);
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				int count = seeThrough.size();
				Integer[] order = new Integer[count];
				double[] distance = new double[count];
				for (int i = 0; i < count; i++)
				{
					float[] center = seeThrough.get(i).center;
					double dx = center[0] - eye[0], dy = center[1] - eye[1], dz = center[2] - eye[2];
					distance[i] = dx * dx + dy * dy + dz * dz;
					order[i] = i;
				}
				java.util.Arrays.sort(order, (i, j) -> Double.compare(distance[j], distance[i]));
				SeeThrough bound = null;
				for (int i : order)
				{
					SeeThrough strip = seeThrough.get(i);
					// neighbors often have the same texture: bind only what changes
					if (bound == null || strip.texture != bound.texture || strip.layer != bound.layer || strip.faces != bound.faces)
						bind(gl, strip.texture, strip.layer, strip.faces);
					bound = strip;
					gl.glDrawArrays(GL.GL_TRIANGLES, strip.first, strip.count);
				}
				gl.glDisable(GL.GL_BLEND);
				gl.glActiveTexture(GL.GL_TEXTURE1);
				gl.glDisable(GL.GL_TEXTURE_2D);
				gl.glActiveTexture(GL.GL_TEXTURE0);
				gl.glClientActiveTexture(GL.GL_TEXTURE1);
				gl.glDisableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
				gl.glClientActiveTexture(GL.GL_TEXTURE0);
				gl.glDisable(GL.GL_TEXTURE_2D);
				gl.glDisable(GL2.GL_ALPHA_TEST);
				gl.glDisableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
				gl.glDisableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
				gl.glDisableClientState(GLPointerFunc.GL_COLOR_ARRAY);
				gl.glDisable(GLLightingFunc.GL_LIGHTING);
				gl.glDisable(GL.GL_CULL_FACE);
			}
			gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
		}

		// wait for the drawing, so the time shown is the real frame time
		gl.glFinish();
		double ms = (System.nanoTime() - start) / 1e6;

		if (zoomFactor != 0)
		{
			double factor = zoomFactor;
			zoomFactor = 0;
			camera.zoom(factor, roadUnder(gl, zoomX, zoomY));
			window.requestRedraw();
		}

		window.frameDrawn(ms, drawnTriangles);
	}

	/** The sky: a vertical gradient behind everything. */
	private void drawBackground(GL2 gl)
	{
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glDisable(GLLightingFunc.GL_LIGHTING);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		// the track is shaded flat: blend the sky's colors
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
		gl.glBegin(GL2.GL_QUADS);
		// a light sky, paler toward the bottom
		gl.glColor3f(0.86f, 0.9f, 0.93f);
		gl.glVertex2f(-1, -1);
		gl.glVertex2f(1, -1);
		gl.glColor3f(0.62f, 0.74f, 0.86f);
		gl.glVertex2f(1, 1);
		gl.glVertex2f(-1, 1);
		gl.glEnd();
		gl.glShadeModel(GLLightingFunc.GL_FLAT);
		gl.glEnable(GL.GL_DEPTH_TEST);
	}

	/** @return the point of the road drawn at a pixel (from the top left), or null when there is none */
	private double[] roadUnder(GL2 gl, int x, int y)
	{
		if (x < 0 || y < 0 || x >= width || y >= height)
			return null;
		FloatBuffer depth = Buffers.newDirectFloatBuffer(1);
		gl.glReadPixels(x, height - 1 - y, 1, 1, GL2.GL_DEPTH_COMPONENT, GL.GL_FLOAT, depth);
		float z = depth.get(0);
		if (z >= 1)
			return null;

		double[] modelview = new double[16], projection = new double[16];
		int[] viewport = { 0, 0, width, height };
		gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, modelview, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projection, 0);
		double[] point = new double[3];
		if (!glu.gluUnProject(x + 0.5, height - 1 - y + 0.5, z, modelview, 0, projection, 0, viewport, 0, point, 0))
			return null;
		return point;
	}

	/** Builds the vertex, normal, color, texture and edge arrays from trackgen's strips. */
	private void fillBuffers(GL2 gl, RoadMesh mesh)
	{
		List<TrackScene.Strip> strips = mesh.scene == null ? new ArrayList<TrackScene.Strip>() : mesh.scene.strips;
		int triangles = mesh.triangleCount;
		float[] v = new float[triangles * 9], n = new float[triangles * 9], c = new float[triangles * 9], t = new float[triangles * 6], rt = new float[triangles * 6], l = new float[triangles * 18];

		// the textures, and the strips sorted by texture and sidedness so each is bound and culled once
		boolean textured = coloring == Coloring.TEXTURE;
		Map<String, Texture> stripTextures = new HashMap<String, Texture>();
		List<String> missing = new ArrayList<String>();
		if (textured)
		{
			for (int i = 0; i < mesh.textureNames.length; i++)
			{
				Texture texture = texture(gl, mesh.textureFiles[i]);
				stripTextures.put(mesh.textureNames[i], texture);
				if (texture == null)
				{
					File file = mesh.textureFiles[i];
					String error = file == null ? null : textureErrors.get(textureKey(file));
					missing.add(mesh.textureNames[i] + (file == null ? ": not found" : ": " + error));
				}
			}
		}
		window.texturesMissing(missing);
		List<TrackScene.Strip> sorted = new ArrayList<TrackScene.Strip>(strips);
		if (textured)
			sorted.sort((a, b) -> faces(a) != faces(b) ? Integer.compare(faces(a), faces(b)) :
				!a.texture.equals(b.texture) ? a.texture.compareTo(b.texture) : String.valueOf(a.racelineTexture).compareTo(String.valueOf(b.racelineTexture)));
		else
			sorted.sort((a, b) -> Integer.compare(faces(a), faces(b)));

		runs.clear();
		runTextures.clear();
		runRacelines.clear();
		seeThrough.clear();
		float scale = (float) heightScale;
		double heightRange = Math.max(1e-6, mesh.maxZ - mesh.minZ);
		int tri = 0;
		for (TrackScene.Strip strip : sorted)
		{
			Texture texture = textured ? stripTextures.get(strip.texture) : null;
			Texture layer = textured && strip.racelineTexture != null ? stripTextures.get(strip.racelineTexture) : null;
			int[] order = strip.triangles();
			int last = runs.size() - 1;
			if (runs.isEmpty() || runTextures.get(last) != texture || runRacelines.get(last) != layer || runs.get(last)[2] != faces(strip))
			{
				runs.add(new int[] { tri * 3, 0, faces(strip) });
				runTextures.add(texture);
				runRacelines.add(layer);
			}
			runs.get(runs.size() - 1)[1] += order.length;
			if (translucent.contains(texture) && order.length > 0)
				seeThrough.add(new SeeThrough(strip, texture, layer, faces(strip), tri * 3, order.length, scale));
			boolean flip = texture != null && texture.getMustFlipVertically();
			boolean flipLayer = layer != null && layer.getMustFlipVertically();
			// a texture is lit as it is; a missing one shows the surface's color
			float[] plain = texture != null ? new float[] { 1, 1, 1 } : coloring == Coloring.PLAIN ? partColor(strip.part) : surfaceColor(strip.surface != null ? strip.surface : strip.texture);
			for (int k = 0; k < order.length; k += 3, tri++)
			{
				int a = order[k], b = order[k + 1], d = order[k + 2];
				float ax = strip.x(b) - strip.x(a), ay = strip.y(b) - strip.y(a), az = (strip.z(b) - strip.z(a)) * scale;
				float bx = strip.x(d) - strip.x(a), by = strip.y(d) - strip.y(a), bz = (strip.z(d) - strip.z(a)) * scale;
				float nx = ay * bz - az * by, ny = az * bx - ax * bz, nz = ax * by - ay * bx;
				if (Math.abs(nx) + Math.abs(ny) + Math.abs(nz) < 1e-12f)
				{
					// a cap's sliver: any normal
					nx = 0;
					ny = 0;
					nz = 1;
				}
				for (int corner = 0; corner < 3; corner++)
				{
					int i = order[k + corner], o = tri * 9 + corner * 3, to = tri * 6 + corner * 2;
					v[o] = strip.x(i);
					v[o + 1] = strip.y(i);
					v[o + 2] = strip.z(i) * scale;
					n[o] = nx;
					n[o + 1] = ny;
					n[o + 2] = nz;
					float[] rgb = texture == null && coloring == Coloring.HEIGHT ? heightColor((strip.z(i) - mesh.minZ) / heightRange) : plain;
					c[o] = rgb[0];
					c[o + 1] = rgb[1];
					c[o + 2] = rgb[2];
					t[to] = strip.u(i);
					t[to + 1] = flip ? 1 - strip.v(i) : strip.v(i);
					rt[to] = strip.racelineU(i);
					rt[to + 1] = flipLayer ? 1 - strip.racelineV(i) : strip.racelineV(i);
				}
				int[] edges = { a, b, b, d, d, a };
				for (int e = 0; e < 6; e++)
				{
					int o = tri * 18 + e * 3;
					l[o] = strip.x(edges[e]);
					l[o + 1] = strip.y(edges[e]);
					l[o + 2] = strip.z(edges[e]) * scale;
				}
			}
		}

		vertices = Buffers.newDirectFloatBuffer(v);
		normals = Buffers.newDirectFloatBuffer(n);
		colors = Buffers.newDirectFloatBuffer(c);
		texCoords = Buffers.newDirectFloatBuffer(t);
		racelineCoords = Buffers.newDirectFloatBuffer(rt);
		lines = Buffers.newDirectFloatBuffer(l);
		drawnTriangles = tri;
	}

	/** A strip with a texture that can be seen through: where it is in the arrays, and its middle. */
	private static final class SeeThrough
	{
		final Texture	texture, layer;
		final int		faces, first, count;
		final float[]	center;

		SeeThrough(TrackScene.Strip strip, Texture texture, Texture layer, int faces, int first, int count, float scale)
		{
			this.texture = texture;
			this.layer = layer;
			this.faces = faces;
			this.first = first;
			this.count = count;
			// the middle of its bounds
			float x0 = Float.MAX_VALUE, x1 = -Float.MAX_VALUE, y0 = Float.MAX_VALUE, y1 = -Float.MAX_VALUE, z0 = Float.MAX_VALUE, z1 = -Float.MAX_VALUE;
			for (int i = 0; i < strip.size(); i++)
			{
				x0 = Math.min(x0, strip.x(i));
				x1 = Math.max(x1, strip.x(i));
				y0 = Math.min(y0, strip.y(i));
				y1 = Math.max(y1, strip.y(i));
				z0 = Math.min(z0, strip.z(i));
				z1 = Math.max(z1, strip.z(i));
			}
			center = new float[] { (x0 + x1) / 2, (y0 + y1) / 2, (z0 + z1) / 2 * scale };
		}
	}

	/** Binds a run's texture and raceline layer, and culls as the game does. */
	private void bind(GL2 gl, Texture texture, Texture layer, int faces)
	{
		// the raceline layer is multiplied in, and doesn't repeat, as in the game
		if (!raceline)
			layer = null;
		gl.glActiveTexture(GL.GL_TEXTURE1);
		if (layer != null)
		{
			layer.enable(gl);
			layer.bind(gl);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		}
		else
			gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glActiveTexture(GL.GL_TEXTURE0);
		if (texture != null)
		{
			texture.enable(gl);
			texture.bind(gl);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
		}
		else
			gl.glDisable(GL.GL_TEXTURE_2D);
		// the game culls one-sided surfaces only; a fence's two faces always,
		// as they are in the same place and would show through each other
		if (culling && faces == ONE_SIDED || faces == BACK_TO_BACK)
			gl.glEnable(GL.GL_CULL_FACE);
		else
			gl.glDisable(GL.GL_CULL_FACE);
	}

	// how a run of strips is culled
	private static final int	ONE_SIDED		= 0;
	private static final int	DOUBLE_SIDED	= 1;
	private static final int	BACK_TO_BACK	= 2;

	private static int faces(TrackScene.Strip strip)
	{
		return strip.backToBack ? BACK_TO_BACK : strip.doubleSided ? DOUBLE_SIDED : ONE_SIDED;
	}

	/** Plain colors that tell the road, borders, sides and barriers apart. */
	private static float[] partColor(TrackScene.Part part)
	{
		switch (part == null ? TrackScene.Part.ROAD : part)
		{
		case BORDER:
			return new float[] { 0.72f, 0.7f, 0.66f };
		case SIDE:
			return new float[] { 0.5f, 0.58f, 0.48f };
		case BARRIER:
			return new float[] { 0.78f, 0.78f, 0.8f };
		case TURN_MARK:
			return new float[] { 0.86f, 0.62f, 0.24f };
		case START_BRIDGE:
			return new float[] { 0.62f, 0.5f, 0.72f };
		case PIT_BUILDING:
			return new float[] { 0.82f, 0.8f, 0.72f };
		case CROSSING:
			return new float[] { 0.7f, 0.66f, 0.6f };
		default:
			return new float[] { 0.6f, 0.64f, 0.63f };
		}
	}

	private static String textureKey(File file)
	{
		return file.getAbsolutePath() + "|" + file.lastModified();
	}

	/** @return whether some of the image can be seen through */
	private static boolean seeThrough(BufferedImage image)
	{
		if (!image.getColorModel().hasAlpha())
			return false;
		int[] row = new int[image.getWidth()];
		for (int y = 0; y < image.getHeight(); y++)
		{
			image.getRGB(0, y, row.length, 1, row, 0, row.length);
			for (int argb : row)
				if ((argb >>> 24) < 250)
					return true;
		}
		return false;
	}

	/** @return whether some of an SGI image can be seen through */
	private static boolean seeThrough(TextureData data)
	{
		if (data.getPixelFormat() != GL.GL_RGBA || !(data.getBuffer() instanceof ByteBuffer))
			return false;
		ByteBuffer pixels = ((ByteBuffer) data.getBuffer()).duplicate();
		pixels.rewind();
		int size = data.getWidth() * data.getHeight() * 4;
		for (int i = 3; i < size && i < pixels.limit(); i += 4)
			if ((pixels.get(i) & 0xff) < 250)
				return true;
		return false;
	}

	/** @return the texture of an image file, loaded once, or null when it can't be read */
	private Texture texture(GL2 gl, File file)
	{
		if (file == null)
			return null;
		String key = textureKey(file);
		Texture texture = textures.get(key);
		if (texture != null || textureErrors.containsKey(key))
			return texture;
		try
		{
			String name = file.getName().toLowerCase();
			if (name.endsWith(".rgb") || name.endsWith(".rgba") || name.endsWith(".sgi") || name.endsWith(".bw"))
			{
				TextureData data = TextureIO.newTextureData(gl.getGLProfile(), file, true, TextureIO.SGI);
				texture = TextureIO.newTexture(data);
				if (seeThrough(data))
					translucent.add(texture);
			}
			else
			{
				BufferedImage image = ImageIO.read(file);
				if (image == null)
					throw new java.io.IOException("not an image ImageIO can read");
				texture = AWTTextureIO.newTexture(gl.getGLProfile(), image, true);
				if (seeThrough(image))
					translucent.add(texture);
			}
			texture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
			texture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
			texture.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
			texture.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
			if (anisotropy > 1)
				texture.setTexParameterf(gl, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropy);
			textures.put(key, texture);
		}
		catch (Exception | LinkageError e)
		{
			textureErrors.put(key, e.getMessage() == null ? e.toString() : e.getMessage());
		}
		return texture;
	}

	/** A muted gray with a slight tint that tells surfaces apart. */
	private static float[] surfaceColor(String surface)
	{
		int h = surface == null ? 0 : surface.hashCode() & 0x7fffffff;
		float hue = (h % 360) / 360f;
		float saturation = (10 + (h >> 9) % 18) / 100f;
		float brightness = (58 + (h >> 4) % 18) / 100f;
		int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
		return new float[] { ((rgb >> 16) & 255) / 255f, ((rgb >> 8) & 255) / 255f, (rgb & 255) / 255f };
	}

	/** Low is teal, high is amber. */
	private static float[] heightColor(double t)
	{
		float[][] stops = { { 46, 110, 120 }, { 120, 150, 110 }, { 214, 170, 90 }, { 205, 110, 70 } };
		t = Math.max(0, Math.min(1, t)) * (stops.length - 1);
		int i = Math.min(stops.length - 2, (int) t);
		float f = (float) (t - i);
		float[] a = stops[i], b = stops[i + 1];
		return new float[] { (a[0] + (b[0] - a[0]) * f) / 255, (a[1] + (b[1] - a[1]) * f) / 255, (a[2] + (b[2] - a[2]) * f) / 255 };
	}
}
