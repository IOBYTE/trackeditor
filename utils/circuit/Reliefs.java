package utils.circuit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import utils.Properties;
import utils.ac3d.Ac3d;
import utils.ac3d.Ac3dException;
import utils.ac3d.Ac3dObject;
import utils.ac3d.Ac3dSurface;
import utils.ac3d.Ac3dMaterial;

public class Reliefs
{
	private String					fileName	= null;
	private Vector<Ac3dMaterial>	materials	= new Vector<Ac3dMaterial>();
	private Vector<ObjShapeRelief>	reliefs		= new Vector<ObjShapeRelief>();
	// what the file holds besides relief lines, written back unchanged
	private Vector<Ac3dObject>		other		= new Vector<Ac3dObject>();
	private Boolean					changed		= false;
	// the file exists but couldn't be read, so writing it would lose what it holds
	private boolean					unreadable	= false;


	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the relief file and reads it, replacing the current reliefs. If it
	 * can't be read the name is still kept, so the track keeps referring to it.
	 */
	public void setFileName(String fileName) throws IOException, Ac3dException, Exception {
		this.fileName = fileName;
		try
		{
			readFile();
		}
		catch (Exception e)
		{
			unreadable = true;
			throw e;
		}
	}

	/**
	 * Changes the relief file from the editor: an existing file is read and
	 * replaces the current reliefs; a new file name keeps the current reliefs,
	 * which are written to it on the next save.
	 */
	public void useFile(String fileName) throws IOException, Ac3dException, Exception {
		if (fileName == null || fileName.isEmpty() || !resolve(fileName).toFile().exists())
		{
			this.fileName = fileName;
			unreadable = false;
			changed = true;
			return;
		}
		String oldName = this.fileName;
		boolean oldUnreadable = unreadable;
		try
		{
			setFileName(fileName);
		}
		catch (Exception e)
		{
			this.fileName = oldName;
			unreadable = oldUnreadable;
			throw e;
		}
	}

	/**
	 * Names the file new reliefs are saved to, without reading it.
	 */
	public void setNewFileName(String fileName) {
		this.fileName = fileName;
		unreadable = false;
		changed = true;
	}

	public Vector<ObjShapeRelief> getReliefs() {
		return reliefs;
	}

	public void setReliefs(Vector<ObjShapeRelief> reliefs) {
		this.reliefs = reliefs;
	}

	public Boolean getChanged() {
		return changed;
	}

	public void setChanged(Boolean changed) {
		this.changed = changed;
	}

	private Path resolve(String name)
	{
		Path filename = Paths.get(name);

		// check for parent directory
		if (filename.getParent() == null)
		{
			// use track directory
			filename = Paths.get(Properties.getInstance().getPath(), filename.toString());
		}
		return filename;
	}

	public void readFile() throws IOException, Ac3dException, Exception
	{
		Ac3d ac3dFile = new Ac3d();

		ac3dFile.read(resolve(fileName).toFile());

		Ac3dObject root = ac3dFile.getRoot();

		Vector<ObjShapeRelief>	newReliefs = new Vector<ObjShapeRelief>();
		Vector<Ac3dObject>		newOther = new Vector<Ac3dObject>();

		if (root != null)
		{
			// put every point in world coordinates
			root.flatten();

			for (Ac3dObject kid : root.getKids())
			{
				collect(kid, root.getData(), newReliefs, newOther);
			}
		}

		// only replace what we have once the whole file was read
		materials = ac3dFile.getMaterials();
		reliefs.clear();
		reliefs.addAll(newReliefs);
		other = newOther;
		changed = false;
		unreadable = false;
	}

	/**
	 * Like trackgen, an object's "interior"/"exterior" data applies to it and
	 * everything below it. Lines become reliefs; anything else is kept as is.
	 */
	private void collect(Ac3dObject object, String parentData, Vector<ObjShapeRelief> newReliefs, Vector<Ac3dObject> newOther)
	{
		String data = object.getData() != null ? object.getData() : parentData;
		boolean isRelief = data != null && (data.startsWith("interior") || data.startsWith("exterior"));

		if (!object.getVertices().isEmpty() || !object.getSurfaces().isEmpty())
		{
			Ac3dObject kept = null;

			for (Ac3dSurface surface : object.getSurfaces())
			{
				if (isRelief && (surface.isOpenLine() || surface.isClosedLine()))
				{
					ObjShapeRelief.ReliefType type = data.startsWith("interior") ? ObjShapeRelief.ReliefType.Interior : ObjShapeRelief.ReliefType.Exterior;
					ObjShapeRelief.LineType lineType = surface.isOpenLine() ? ObjShapeRelief.LineType.Open : ObjShapeRelief.LineType.Closed;
					Vector<double[]> vertices = new Vector<double[]>();

					for (int k = 0; k < surface.getRefs().size(); k++)
					{
						double[] v = object.getVertices().get(surface.getRefs().get(k).index);
						vertices.add(new double[] { v[0], v[1], v[2] });
					}

					newReliefs.add(new ObjShapeRelief(object.getName(), type, lineType, vertices));
				}
				else
				{
					if (kept == null)
					{
						kept = new Ac3dObject(object.getType(), 0);
						kept.setName(object.getName());
						kept.setData(data);
						kept.copySettings(object);
						kept.setVertices(object.getVertices());
					}
					kept.addSurface(surface);
				}
			}

			if (kept == null && object.getSurfaces().isEmpty())
			{
				// points without surfaces
				kept = new Ac3dObject(object.getType(), 0);
				kept.setName(object.getName());
				kept.setData(data);
				kept.copySettings(object);
				kept.setVertices(object.getVertices());
			}

			if (kept != null)
				newOther.add(kept);
		}

		for (Ac3dObject kid : object.getKids())
		{
			collect(kid, data, newReliefs, newOther);
		}
	}
	
	public void writeFile() throws IOException
	{
		if (!changed)
			return;

		if (fileName == null || fileName.isEmpty())
			return;

		Path filename = resolve(fileName);

		// keep a file the editor couldn't read: writing only what was drawn would lose the rest
		if (unreadable && filename.toFile().exists())
			throw new IOException("it couldn't be read when the track was opened, so it wasn't overwritten. "
				+ "Fix the file, or choose a new relief file name in the terrain properties");

		Ac3d ac3dFile = new Ac3d();

		Ac3dObject	world = new Ac3dObject("world", 0);

		ac3dFile.setRoot(world);

		for (Ac3dObject object : other)
		{
			world.addKid(object);
		}

		if (materials.isEmpty())
		{
			Ac3dMaterial material = new Ac3dMaterial("");

			material.setRgb(new double[] { 0, 0, 1 });
			material.setAmb(new double[] { 0.2, 0.2, 0.2 });
			material.setEmis(new double[] { 0, 0, 0 });
			material.setSpec(new double[] { 0.5, 0.5, 0.5 });
			material.setShi(10);
			material.setTrans(0);

			ac3dFile.getMaterials().add(material);
		}
		else
			ac3dFile.setMaterials(materials);

		for (int i = 0; i < reliefs.size(); i++)
		{
			ObjShapeRelief relief = reliefs.get(i);
			Ac3dObject kid = new Ac3dObject("poly", 0);

			kid.setName(relief.getReliefName());
			kid.setData(relief.isInterior() ? "interior" : "exterior");
			kid.setVertices(relief.getVertices());

			Ac3dSurface surface = new Ac3dSurface();

			if (relief.isClosed())
			{
				surface.setClosedLine();
			}
			else
			{
				surface.setOpenLine();
			}

			for (int j = 0; j < relief.getVertices().size(); j++)
			{
				surface.addRef(j, 0, 0);
			}

			surface.setMat(0);

			kid.addSurface(surface);

			world.addKid(kid);
		}

		ac3dFile.write(filename.toString());

		// written, so the next save doesn't have to write it again
		changed = false;
	}

	public void dump(String indent)
	{
		System.out.println(indent + "Reliefs");
		System.out.println(indent + "  fileName           : " + (fileName == null ? "null" : fileName));
		System.out.println(indent + "  objects[" + reliefs.size() + "]");
		for (int i = 0; i < reliefs.size(); i++)
		{
			reliefs.get(i).dump(indent + "    ");
		}
	}
}
