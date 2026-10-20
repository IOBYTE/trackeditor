package utils.circuit;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;

import utils.AtomicFile;
import utils.Editor;

public class ObjectMap
{
	// XML attributes
	private String					name		= null;
	private String					objectMap	= null;
	
	// image information
	private Vector<ObjShapeObject>	objects		= new Vector<ObjShapeObject>();
	private Set<Integer>			colors		= new HashSet<Integer>();
	private int 					imageWidth	= 0;
	private int 					imageHeight	= 0;

	private Boolean					changed		= false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getObjectMap() {
		return objectMap;
	}

	public void setObjectMap(String objectMap) throws IOException {
		this.objectMap = objectMap;
		readImage();
	}

	public Vector<ObjShapeObject> getObjects() {
		return objects;
	}

	public void addObject(ObjShapeObject object)
	{
		objects.add(object);
		colors.add(object.getRGB());
		changed = true;
	}

	public void addObjectAt(ObjShapeObject object, int index)
	{
		objects.insertElementAt(object, index);
		colors.add(object.getRGB());
		changed = true;
	}

	public void setObjectAt(int index, ObjShapeObject object)
	{
		objects.set(index, object);
		colors.add(object.getRGB());
		changed = true;
	}

	public ObjShapeObject getObjectAt(int index)
	{
		return objects.get(index);
	}

	public boolean removeObject(ObjShapeObject object)
	{
		if (objects.remove(object))
		{
			changed = true;
			return true;
		}
		return false;
	}
	
	public int getObjectIndex(ObjShapeObject object)
	{
		int index = objects.indexOf(object);
		
		return index;
	}
	
	public boolean hasObject(ObjShapeObject object)
	{
		return getObjectIndex(object) != -1;
	}
	
	public void addColor(int color)
	{
		colors.add(color);
	}	

	public Set<Integer> getColors() {
		return colors;
	}
	
	public void recalculateColors() {
		colors.clear();
		for (int i = 0; i < objects.size(); i++)
		{
			colors.add(objects.get(i).getRGB());
		}
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public Boolean getChanged() {
		return changed;
	}

	public void setChanged(Boolean changed) {
		this.changed = changed;
	}

	public void readImage() throws IOException
	{
		objects.clear();
		colors.clear();
		imageWidth = 0;
		imageHeight = 0;

		Path filename = Paths.get(objectMap);
		Path trackPath = Paths.get(Editor.getProperties().getPath());

		// check for parent directory
		if (filename.getParent() == null)
		{
			// use track directory
			filename = Paths.get(trackPath.toString(), filename.toString());
		}

		BufferedImage image = ImageIO.read(new File(filename.toString()));
		if (image == null)
			throw new IOException("not an image format the editor can read (use PNG)");
		imageWidth = image.getWidth();
		imageHeight = image.getHeight();

		for (int x = 0; x < imageWidth; x++)
		{
			for (int y = 0; y < imageHeight; y++)
			{
				int rgb = image.getRGB(x, y) & 0x00ffffff;

				if (rgb != 0x0)
				{
					colors.add(rgb);
					objects.add(new ObjShapeObject(rgb, x, y));
				}
			}
		}
	}

	public void writeImage() throws IOException
	{
		if (!changed)
			return;

		if (objectMap == null || objectMap.isEmpty())
			return;

		// no size means the image couldn't be read; writing would replace it with an empty one
		if (imageWidth <= 0 || imageHeight <= 0)
			throw new IOException("it couldn't be read when the track was opened, so it wasn't overwritten");

		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphic = image.createGraphics();
		graphic.setColor(new Color(0x00000000));
		graphic.fillRect(0, 0, imageWidth, imageHeight);

		for (int i = 0; i < objects.size(); i++)
		{
			ObjShapeObject object = objects.get(i);

			graphic.setColor(object.getColor());
			graphic.drawLine(object.getImageX(), object.getImageY(), object.getImageX(), object.getImageY());
		}
		graphic.dispose();

		Path filename = Paths.get(objectMap);
		Path trackPath = Paths.get(Editor.getProperties().getPath());

		// check for parent directory
		if (filename.getParent() == null)
		{
			// use track directory
			filename = Paths.get(trackPath.toString(), filename.toString());
		}

		writePng(filename, image);

		// written, so the next save doesn't have to write it again
		changed = false;
	}

	/**
	 * Creates a new object map image: black (no objects), width x height pixels.
	 */
	public static void createImage(Path filename, int width, int height) throws IOException
	{
		if (width <= 0 || height <= 0)
			throw new IOException("the image would be " + width + " x " + height + " pixels");

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphic = image.createGraphics();
		graphic.setColor(new Color(0x00000000));
		graphic.fillRect(0, 0, width, height);
		graphic.dispose();

		writePng(filename, image);
	}

	/**
	 * Writes a PNG to a temporary file first, so a failure leaves the old image as it was.
	 */
	private static void writePng(Path filename, BufferedImage image) throws IOException
	{
		// ImageIO only reports "Can't create an ImageOutputStream!" for a bad folder
		Path folder = filename.toAbsolutePath().getParent();
		if (folder != null && !java.nio.file.Files.isDirectory(folder))
			throw new IOException("folder doesn't exist: " + folder);

		AtomicFile.write(filename, file -> {
			if (!ImageIO.write(image, "png", file))
				throw new IOException("no PNG image writer available");
		});
	}

	public void dump(String indent)
    {
		System.out.println(indent + "ObjectMap");
		System.out.println(indent + "  name        : " + name);
		System.out.println(indent + "  objectMap   : " + objectMap);
		System.out.println(indent + "  imageWidth  : " + imageWidth);
		System.out.println(indent + "  imageHeight : " + imageHeight);
		System.out.println(indent + "  colors[" + colors.size() + "]");
		Iterator<Integer> colorsIterator = colors.iterator();
		while (colorsIterator.hasNext())
		{
			System.out.println(indent + "    " + String.format("0x%06X", colorsIterator.next()));
		}
		System.out.println(indent + "  objects[" + objects.size() + "]");
		for (int i = 0; i < objects.size(); i++)
		{
			ObjShapeObject object = objects.get(i);
			System.out.println(indent + "    " + String.format("0x%06X", object.getRGB()) + " " + object.getImageX() + " " + object.getImageY());
		}
    }
}
