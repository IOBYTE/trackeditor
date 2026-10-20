package utils.ac3d;

import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Vector;

public class Ac3dObject
{
	private int					linenum;
	private String				type;
	private String				name;
	private String				data;
	private String				texture;
	private double[]			texrep;
	private double[]			texoff;
	private Integer				subdiv;
	private Double				crease;
	private double[]			rot;
	private double[]			loc;
	private String				url;
	private Boolean				hidden;
	private Boolean				locked;
	private Boolean				folded;
	private Vector<double[]>	vertices	= new Vector<double[]>();
	private Vector<Ac3dSurface>	surfaces	= new Vector<Ac3dSurface>();
	private Vector<Ac3dObject>	kids		= new Vector<Ac3dObject>();

	public class Matrix
	{
		public double[][] data = new double[4][4];
		
		public Matrix()
		{
			data[0][0] = 1; data[0][1] = 0; data[0][2] = 0; data[0][3] = 0;
			data[1][0] = 0; data[1][1] = 1; data[1][2] = 0; data[1][3] = 0;
			data[2][0] = 0; data[2][1] = 0; data[2][2] = 1; data[2][3] = 0;
			data[3][0] = 0; data[3][1] = 0; data[3][2] = 0; data[3][3] = 1;
		}

		public Matrix(double m0,  double m1,  double m2,  double m3,
					  double m4,  double m5,  double m6,  double m7,
					  double m8,  double m9,  double m10, double m11,
					  double m12, double m13, double m14, double m15)
		{
			data[0][0] = m0;  data[0][1] = m1;  data[0][2] = m2;  data[0][3] = m3;
			data[1][0] = m4;  data[1][1] = m5;  data[1][2] = m6;  data[1][3] = m7;
			data[2][0] = m8;  data[2][1] = m9;  data[2][2] = m10; data[2][3] = m11;
			data[3][0] = m12; data[3][1] = m13; data[3][2] = m14; data[3][3] = m15;
		}

		public void setLoc(double[] loc)
		{
			data[3][0] = loc[0]; data[3][1] = loc[1]; data[3][2] = loc[2];
		}

		public void setRot(double[] rot)
		{
			data[0][0] = rot[0]; data[0][1] = rot[1]; data[0][2] = rot[2];
			data[1][0] = rot[3]; data[1][1] = rot[4]; data[1][2] = rot[5];
			data[2][0] = rot[6]; data[2][1] = rot[7]; data[2][2] = rot[8];
		}

		public void transformPoint(double[] point)
		{
		    double t0 = point[0];
		    double t1 = point[1];
		    double t2 = point[2];

		    point[0] = t0 * data[0][0] + t1 * data[1][0] + t2 * data[2][0] + data[3][0];
		    point[1] = t0 * data[0][1] + t1 * data[1][1] + t2 * data[2][1] + data[3][1];
		    point[2] = t0 * data[0][2] + t1 * data[1][2] + t2 * data[2][2] + data[3][2];
		}

		public Matrix multiply(Matrix matrix)
		{
		    Matrix dst = new Matrix();

		    for (int i = 0; i < 4; i++)
		    {
		        dst.data[0][i] = matrix.data[0][0] * data[0][i] +
		        				 matrix.data[0][1] * data[1][i] +
		        				 matrix.data[0][2] * data[2][i] +
		        				 matrix.data[0][3] * data[3][i];

		        dst.data[1][i] = matrix.data[1][0] * data[0][i] +
		        				 matrix.data[1][1] * data[1][i] +
		        				 matrix.data[1][2] * data[2][i] +
		        				 matrix.data[1][3] * data[3][i];

		        dst.data[2][i] = matrix.data[2][0] * data[0][i] +
		        				 matrix.data[2][1] * data[1][i] +
		        				 matrix.data[2][2] * data[2][i] +
		        				 matrix.data[2][3] * data[3][i];

		        dst.data[3][i] = matrix.data[3][0] * data[0][i] +
		        				 matrix.data[3][1] * data[1][i] +
		        				 matrix.data[3][2] * data[2][i] +
		        				 matrix.data[3][3] * data[3][i];
		    }
		    return dst;
		}

		public void dump()
		{
			System.out.println(data[0][0] + " " + data[0][1] + " " + data[0][2] + " " + data[0][3] + "\n" +
							   data[1][0] + " " + data[1][1] + " " + data[1][2] + " " + data[1][3] + "\n" +
							   data[2][0] + " " + data[2][1] + " " + data[2][2] + " " + data[2][3] + "\n" +
							   data[3][0] + " " + data[3][1] + " " + data[3][2] + " " + data[3][3] + "\n");
		}
	}

	public Ac3dObject(String type, int linenum)
	{
		this.type = type;
		this.linenum = linenum;
	}

	public void write(FileWriter file) throws IOException
	{
		NumberFormat	nf;
		nf = NumberFormat.getNumberInstance(Locale.US);
		nf.setMaximumFractionDigits(6);
		nf.setMinimumFractionDigits(0);
		nf.setGroupingUsed(false);

		file.write("OBJECT " + type + "\n");

		if (name != null && !name.isEmpty())
		{
			file.write("name \"" + name + "\"\n");
		}

		if (texture != null && !texture.isEmpty())
		{
			file.write("texture \"" + texture + "\"\n");
		}

		if (texrep != null && texrep.length == 2)
		{
			file.write("texrep " + nf.format(texrep[0]) + " " + nf.format(texrep[1]) + "\n");
		}

		if (texoff != null && texoff.length == 2)
		{
			file.write("texoff " + nf.format(texoff[0]) + " " + nf.format(texoff[1]) + "\n");
		}

		if (rot != null && rot.length == 9)
		{
			file.write("rot " + nf.format(rot[0]) + " " + nf.format(rot[1]) + " " + nf.format(rot[2]) +
					" " + nf.format(rot[3]) + " " + nf.format(rot[4]) + " " + nf.format(rot[5]) +
					" " + nf.format(rot[6]) + " " + nf.format(rot[7]) + " " + nf.format(rot[8]) + "\n");
		}

		if (loc != null && loc.length == 3)
		{
			file.write("loc " + nf.format(loc[0]) + " " + nf.format(loc[1]) + " " + nf.format(loc[2]) + "\n");
		}

		if (crease != null)
		{
			file.write("crease " + crease + "\n");
		}

		if (subdiv != null)
		{
			file.write("subdiv " + subdiv + "\n");
		}

		if (hidden != null && hidden == true)
		{
			file.write("hidden\n");
		}

		if (locked != null && locked == true)
		{
			file.write("locked\n");
		}

		if (folded != null && folded == true)
		{
			file.write("folded\n");
		}

		if (data != null && !data.isEmpty())
		{
			file.write("data " + data.length() + "\n" + data + "\n");
		}

		if (vertices.size() > 0)
		{
			file.write("numvert " + vertices.size() + "\n");

			for (int i = 0; i < vertices.size(); i++)
			{
				double vertex[] = vertices.get(i);

				file.write(nf.format(vertex[0]) + " " + nf.format(vertex[1]) + " " + nf.format(vertex[2]) + "\n");
			}
		}

		if (surfaces.size() > 0)
		{
			file.write("numsurf " + surfaces.size() + "\n");

			for (int i = 0; i < surfaces.size(); i++)
			{
				Ac3dSurface surface = surfaces.get(i);

				surface.write(file);
			}
		}

		file.write("kids " + kids.size() + "\n");

		for (int i = 0; i < kids.size(); i++)
		{
			kids.get(i).write(file);
		}
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getData()
	{
		return data;
	}

	public void setData(String data)
	{
		this.data = data;
	}

	public String getTexture()
	{
		return texture;
	}

	public void setTexture(String texture)
	{
		this.texture = texture;
	}

	public double[] getTexrep()
	{
		return texrep;
	}

	public void setTexrep(String[] tokens)
	{
		this.texrep = new double[2];

		texrep[0] = Double.parseDouble(tokens[1]);
		texrep[1] = Double.parseDouble(tokens[2]);
	}

	public void setTexrep(double[] texrep)
	{
		this.texrep = texrep;
	}

	public double[] getTexoff()
	{
		return texoff;
	}

	public void setTexoff(String[] tokens)
	{
		this.texoff = new double[2];

		texoff[0] = Double.parseDouble(tokens[1]);
		texoff[1] = Double.parseDouble(tokens[2]);
	}

	public void setTexoff(double[] texoff)
	{
		this.texoff = texoff;
	}

	public int getSubdiv()
	{
		return subdiv;
	}

	public void setSubdiv(String[] tokens)
	{
		this.subdiv = Integer.parseInt(tokens[1]);
	}

	public void setSubdiv(int subdiv)
	{
		this.subdiv = subdiv;
	}

	public double getCrease()
	{
		return crease;
	}

	public void setCrease(String[] tokens)
	{
		this.crease = Double.parseDouble(tokens[1]);
	}

	public void setCrease(double crease)
	{
		this.crease = crease;
	}

	public double[] getRot()
	{
		return rot;
	}

	public void setRot(String[] tokens)
	{
		rot = new double[9];

		for (int i = 0; i < 9; i++)
			rot[i] = Double.parseDouble(tokens[i + 1]);
	}

	public void setRot(double[] rot)
	{
		this.rot = rot;
	}

	public double[] getLoc()
	{
		return loc;
	}

	public void setLoc(String[] tokens)
	{
		loc = new double[3];

		for (int i = 0; i < 3; i++)
			loc[i] = Double.parseDouble(tokens[i + 1]);
	}

	public void setLoc(double[] loc)
	{
		this.loc = loc;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public Vector<double[]> getVertices()
	{
		return vertices;
	}

	public void setVertices(Vector<double[]> vertices)
	{
		this.vertices = vertices;
	}

	public void addVertex(String line)
	{
		addVertex(line.split("\\s+"));
	}

	public void addVertex(String[] tokens)
	{
		double vertex[] = new double[3];

		for (int i = 0; i < 3; i++)
			vertex[i] = Double.parseDouble(tokens[i]);

		vertices.add(vertex);
	}

	public Vector<Ac3dSurface> getSurfaces()
	{
		return surfaces;
	}

	public void setSurfaces(Vector<Ac3dSurface> surfaces)
	{
		this.surfaces = surfaces;
	}

	public void addSurface(Ac3dSurface surface)
	{
		surfaces.add(surface);
	}

	public Vector<Ac3dObject> getKids()
	{
		return kids;
	}

	public void setKids(Vector<Ac3dObject> kids)
	{
		this.kids = kids;
	}

	public void addKid(Ac3dObject kid)
	{
		kids.add(kid);
	}

	public Boolean getHidden()
	{
		return hidden;
	}

	public void setHidden(Boolean hidden)
	{
		this.hidden = hidden;
	}

	public Boolean getLocked()
	{
		return locked;
	}

	public void setLocked(Boolean locked)
	{
		this.locked = locked;
	}

	public Boolean getFolded()
	{
		return folded;
	}

	public void setFolded(Boolean folded)
	{
		this.folded = folded;
	}

	public void setSubdiv(Integer subdiv)
	{
		this.subdiv = subdiv;
	}

	public void setCrease(Double crease)
	{
		this.crease = crease;
	}

	public int getLinenum()
	{
		return linenum;
	}

	public void setLinenum(int linenum)
	{
		this.linenum = linenum;
	}
	
	public void flatten()
	{
		transform(new Matrix(1.0, 0.0, 0.0, 0.0,
							 0.0, 1.0, 0.0, 0.0,
							 0.0, 0.0, 1.0, 0.0,
							 0.0, 0.0, 0.0, 1.0));
	}

	public void transform(Matrix matrix)
	{
		Matrix	thisMatrix = new Matrix();

		if (loc != null)
		{
			thisMatrix.setLoc(loc);
			loc = null;
		}

		if (rot != null)
		{
			thisMatrix.setRot(rot);
			rot = null;
		}

		// points are row vectors: world = point * this * parent
		Matrix newMatrix = matrix.multiply(thisMatrix);

		for (int i = 0; i < vertices.size(); i++)
		{
			newMatrix.transformPoint(vertices.elementAt(i));
		}

		for (int i = 0; i < kids.size(); i++)
		{
			kids.get(i).transform(newMatrix);
		}
	}
}
