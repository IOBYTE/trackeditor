package utils.ac3d;

import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Vector;

public class Ac3dSurface
{
	public class Ref
	{
		public int		index;
		public double[]	coord = new double[2];

		public Ref(int index, double i, double j)
		{
			this.index = index;
			this.coord[0] = i;
			this.coord[1] = j;
		}

		public Ref(String[] tokens)
		{
			index = Integer.parseInt(tokens[0]);
			coord[0] = Double.parseDouble(tokens[1]);
			coord[1] = Double.parseDouble(tokens[2]);
		}
	}

	public static final int SURFACE_TYPE_MASK			= 0x0f;
	public static final int SURFACE_TYPE_POLYGON		= 0x00;
	public static final int SURFACE_TYPE_CLOSED_LINE	= 0x01;
	public static final int SURFACE_TYPE_OPEN_LINE		= 0x02;

	public static final int	SHADED_MASK					= 0x10;
	public static final int SHADED_FLAT					= 0x00;
	public static final int SHADED_SMOOTH				= 0x10;

	public static final int SIDED_MASK					= 0x20;
	public static final int SIDED_ONE					= 0x00;
	public static final int SIDED_TWO					= 0x20;

	private int			linenum							= 0;
	private Integer 	surf;
	private Integer 	mat;
	private Vector<Ref>	refs = new Vector<Ref>();
	
	public boolean isPolygon()
	{
		return (surf & SURFACE_TYPE_MASK) == SURFACE_TYPE_POLYGON;
	}
	
	public void setPolygon()
	{
		if (surf == null)
		{
			surf = SURFACE_TYPE_POLYGON;
		}
		else
		{
			surf = (surf & ~SURFACE_TYPE_MASK) | SURFACE_TYPE_POLYGON;
		}
	}
	
	public boolean isClosedLine()
	{
		return (surf & SURFACE_TYPE_MASK) == SURFACE_TYPE_CLOSED_LINE;
	}
	
	public void setClosedLine()
	{
		if (surf == null)
		{
			surf = SURFACE_TYPE_CLOSED_LINE;
		}
		else
		{
			surf = (surf & ~SURFACE_TYPE_MASK) | SURFACE_TYPE_CLOSED_LINE;
		}
	}
	
	public boolean isOpenLine()
	{
		return (surf & SURFACE_TYPE_MASK) == SURFACE_TYPE_OPEN_LINE;
	}

	public void setOpenLine()
	{
		if (surf == null)
		{
			surf = SURFACE_TYPE_OPEN_LINE;
		}
		else
		{
			surf = (surf & ~SURFACE_TYPE_MASK) | SURFACE_TYPE_OPEN_LINE;
		}
	}

	public boolean isFlatShaded()
	{
		return (surf & SHADED_MASK) == SHADED_FLAT;
	}
	
	public void setFlatShaded()
	{
		if (surf == null)
		{
			surf = SHADED_FLAT;
		}
		else
		{
			surf = (surf & ~SHADED_MASK) | SHADED_FLAT;
		}
	}

	public boolean isSmoothShaded()
	{
		return (surf & SHADED_MASK) == SHADED_SMOOTH;
	}
	
	public void setSmoothShaded()
	{
		if (surf == null)
		{
			surf = SHADED_SMOOTH;
		}
		else
		{
			surf = (surf & ~SHADED_MASK) | SHADED_SMOOTH;
		}
	}

	public boolean isSingleSided()
	{
		return (surf & SIDED_MASK) == SIDED_ONE;
	}
	
	public void setSingleSided()
	{
		if (surf == null)
		{
			surf = SIDED_ONE;
		}
		else
		{
			surf = (surf & ~SIDED_MASK) | SIDED_ONE;
		}
	}

	public boolean isDoubleSided()
	{
		return (surf & SIDED_MASK) == SIDED_TWO;
	}
	
	public void setDoubleSided()
	{
		if (surf == null)
		{
			surf = SIDED_TWO;
		}
		else
		{
			surf = (surf & ~SIDED_MASK) | SIDED_TWO;
		}
	}
	
	public void write(FileWriter file) throws IOException
	{
		NumberFormat	nf;
		nf = NumberFormat.getNumberInstance(Locale.US);
		nf.setMaximumFractionDigits(6);
		nf.setMinimumFractionDigits(0);
		nf.setGroupingUsed(false);

		if (surf != null)
		{
			file.write("SURF " + String.format("0x%02X", surf) + "\n");
		}

		if (mat != null)
		{
			file.write("mat " + mat + "\n");
		}

		file.write("refs " + refs.size() + "\n");

		for (int i = 0; i < refs.size(); i++)
		{
			Ref ref = refs.get(i);

			file.write(ref.index + " " + nf.format(ref.coord[0]) + " " + nf.format(ref.coord[1]) + "\n");
		}
	}

	public int getSurf()
	{
		return surf;
	}

	public void setSurf(int surf)
	{
		this.surf = surf;
	}

	public int getMat()
	{
		return mat;
	}

	public void setMat(int mat)
	{
		this.mat = mat;
	}

	public Vector<Ref> getRefs()
	{
		return refs;
	}

	public void setRefs(Vector<Ref> refs)
	{
		this.refs = refs;
	}

	public void addRef(String[] tokens)
	{
		refs.add(new Ref(tokens));
	}

	public void addRef(int index, double i, double j)
	{
		refs.add(new Ref(index, i, j));
	}

	public int getLinenum()
	{
		return linenum;
	}

	public void setLinenum(int linenum)
	{
		this.linenum = linenum;
	}
}
