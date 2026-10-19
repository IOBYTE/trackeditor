package utils.circuit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import utils.Editor;
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
	private Boolean					changed		= false;


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) throws IOException, Ac3dException, Exception {
		this.fileName = fileName;
		readFile();
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

	public void readFile() throws IOException, Ac3dException, Exception
	{
		Path filename = Paths.get(fileName);
		Path trackPath = Paths.get(Editor.getProperties().getPath());

		// check for parent directory
		if (filename.getParent() == null)
		{
			// use track directory
			filename = Paths.get(trackPath.toString(), filename.toString());
		}

		Ac3d ac3dFile = new Ac3d();

		ac3dFile.read(new File(filename.toString()));

		materials = ac3dFile.getMaterials();

		Ac3dObject root = ac3dFile.getRoot();

		root.flatten();

		if (root != null && "world".equals(root.getType()))
		{
			for (int i = 0; i < root.getKids().size(); i++)
			{
				Ac3dObject object = root.getKids().get(i);

				if ("poly".equals(object.getType()))
				{
					String data = object.getData();

					if (data == null)
					{
						continue;
					}
					else if (!(data.equals("interior") || data.equals("exterior")))
					{
						continue;
					}

					ObjShapeRelief.ReliefType type = data.equals("interior") ? ObjShapeRelief.ReliefType.Interior : ObjShapeRelief.ReliefType.Exterior;

					for (int j = 0; j < object.getSurfaces().size(); j++)
					{
						Ac3dSurface	surface = object.getSurfaces().get(j);
						ObjShapeRelief.LineType lineType = surface.isOpenLine() ? ObjShapeRelief.LineType.Open : ObjShapeRelief.LineType.Closed;
						Vector<double[]> vertices = new Vector<double[]>();
							
						for (int k = 0; k < surface.getRefs().size(); k++)
						{
							vertices.add(object.getVertices().get(surface.getRefs().get(k).index));
						}
							
						reliefs.add(new ObjShapeRelief(object.getName(), type, lineType, vertices));
					}
				}
			}
		}
	}
	
	public void writeFile() throws IOException
	{
		if (!changed)
			return;

		if (fileName == null || fileName.isEmpty())
			return;

		Path filename = Paths.get(fileName);
		Path trackPath = Paths.get(Editor.getProperties().getPath());

		// check for parent directory
		if (filename.getParent() == null)
		{
			// use track directory
			filename = Paths.get(trackPath.toString(), filename.toString());
		}

		Ac3d ac3dFile = new Ac3d();

		Ac3dObject	world = new Ac3dObject("world", 0);

		ac3dFile.setRoot(world);

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
