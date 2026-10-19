package utils.ac3d;

import java.io.FileWriter;
import java.io.IOException;

public class Ac3dMaterial
{
	private boolean	versionC;
	private String	name;
	private double	rgb[];
	private double	amb[];
	private double	emis[];
	private double	spec[];
	private int		shi;
	private	double	trans;
	private String	data;

	public Ac3dMaterial(String name)
	{
		versionC = false;

		this.name = name;
	}

	public Ac3dMaterial(String[] tokens, int lineNumber) throws Ac3dException
	{
		versionC = false;

		if (tokens.length != 22)
		{
			throw new Ac3dException("invalid material", lineNumber);
		}

		if (!"rgb".equals(tokens[2]) || !"amb".equals(tokens[6]) || !"emis".equals(tokens[10]) ||
			!"spec".equals(tokens[14]) || !"shi".equals(tokens[18]) || !"trans".equals(tokens[20]))
		{
			throw new Ac3dException("invalid material", lineNumber);
		}

		name = tokens[1];

		rgb = new double[3];

		rgb[0] = Double.parseDouble(tokens[3]);
		rgb[1] = Double.parseDouble(tokens[4]);
		rgb[2] = Double.parseDouble(tokens[5]);

		amb = new double[3];

		amb[0] = Double.parseDouble(tokens[7]);
		amb[1] = Double.parseDouble(tokens[8]);
		amb[2] = Double.parseDouble(tokens[9]);

		emis = new double[3];

		emis[0] = Double.parseDouble(tokens[11]);
		emis[1] = Double.parseDouble(tokens[12]);
		emis[2] = Double.parseDouble(tokens[13]);

		spec = new double[3];

		spec[0] = Double.parseDouble(tokens[15]);
		spec[1] = Double.parseDouble(tokens[16]);
		spec[2] = Double.parseDouble(tokens[17]);

		shi = Integer.parseInt(tokens[19]);
		trans = Double.parseDouble(tokens[21]);
	}

	public void write(FileWriter file) throws IOException
	{
		if (versionC)
		{
			file.write("MAT \"" + name + "\"\n");
			file.write("rgb " + rgb[0] + " " + rgb[1] + " " + rgb[2] + "\n");
			file.write("amb " + amb[0] + " " + amb[1] + " " + amb[2] + "\n");
			file.write("emis " + emis[0] + " " + emis[1] + " " + emis[2] + "\n");
			file.write("spec " + spec[0] + " " + spec[1] + " " + spec[2] + "\n");
			file.write("shi " + shi + "\n");
			file.write("trans " + trans + "\n");
			// TODO write data
			file.write("ENDMAT\n");
		}
		else
		{
			file.write("MATERIAL \"" + name + "\"" +
				" rgb " + rgb[0] + " " + rgb[1] + " " + rgb[2] +
				"  amb " + amb[0] + " " + amb[1] + " " + amb[2] +
				"  emis " + emis[0] + " " + emis[1] + " " + emis[2] +
				"  spec " + spec[0] + " " + spec[1] + " " + spec[2] +
				"  shi " + shi +
				"  trans " + trans + "\n");
		}
	}

	public boolean isVersionC()
	{
		return versionC;
	}

	public void setVersionC(boolean versionC)
	{
		this.versionC = versionC;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public double[] getRgb()
	{
		return rgb;
	}

	public void setRgb(String[] tokens)
	{
		rgb = new double[3];

		rgb[0] = Double.parseDouble(tokens[1]);
		rgb[1] = Double.parseDouble(tokens[2]);
		rgb[2] = Double.parseDouble(tokens[3]);
	}

	public void setRgb(double[] rgb)
	{
		this.rgb = rgb;
	}

	public double[] getAmb()
	{
		return amb;
	}

	public void setAmb(String[] tokens)
	{
		amb = new double[3];

		amb[0] = Double.parseDouble(tokens[1]);
		amb[1] = Double.parseDouble(tokens[2]);
		amb[2] = Double.parseDouble(tokens[3]);
	}

	public void setAmb(double[] amb)
	{
		this.amb = amb;
	}

	public double[] getEmis()
	{
		return emis;
	}

	public void setEmis(String[] tokens)
	{
		emis = new double[3];

		emis[0] = Double.parseDouble(tokens[1]);
		emis[1] = Double.parseDouble(tokens[2]);
		emis[2] = Double.parseDouble(tokens[3]);
	}

	public void setEmis(double[] emis)
	{
		this.emis = emis;
	}

	public double[] getSpec()
	{
		return spec;
	}

	public void setSpec(String[] tokens)
	{
		spec = new double[3];

		spec[0] = Double.parseDouble(tokens[1]);
		spec[1] = Double.parseDouble(tokens[2]);
		spec[2] = Double.parseDouble(tokens[3]);
	}

	public void setSpec(double[] spec)
	{
		this.spec = spec;
	}

	public int getShi()
	{
		return shi;
	}

	public void setShi(String[] tokens)
	{
		this.shi = Integer.parseInt(tokens[1]);
	}

	public void setShi(int shi)
	{
		this.shi = shi;
	}

	public double getTrans()
	{
		return trans;
	}

	public void setTrans(String[] tokens)
	{
		this.trans = Double.parseDouble(tokens[1]);
	}

	public void setTrans(double trans)
	{
		this.trans = trans;
	}

	public String getData()
	{
		return data;
	}

	public void setData(String data)
	{
		this.data = data;
	}
}
