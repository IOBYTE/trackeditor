package utils.ac3d;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Ac3d
{
	private BufferedReader			br;
	private String					line;
	private int 					linenum		= 0;
	private String[]				tokens;
	private boolean 				versionC	= false;
	private Vector<Ac3dMaterial>	materials	= new Vector<Ac3dMaterial>();
	private Ac3dObject				root		= null;

    private String[] tokenizeLine(String text)  throws Ac3dException
    {
        List<String> result;
        StringBuilder str;
        int i;
        String token;

        result = new ArrayList<>();
        str = new StringBuilder(text);

        while (true)
        {
            // trimLeft
            i = 0;
            while ((i < str.length()) && (Character.isWhitespace(str.charAt(i))))
            {
                i++;
            }
            str = str.delete(0, i);

            // stop when str is empty
            if (str.length() == 0)
            {
                break;
            }

            // if str start with a double quote
            if (str.charAt(0) == '"')
            {
                // find the next double quote
                i = 1;
                while (i < str.length())
                {
                    if (str.charAt(i) == str.charAt(0))
                    {
                        break;
                    }
                    i += 1;
                }

                if (i >= str.length())
                {
                    throw new Ac3dException("Quote parse error.", linenum);
                }

                // add the found string to the token vector without the quotes
                token = str.substring(1, i);
                result.add(token);
                str = str.delete(0, i + 1);
            }
            else
            {
                // find first whiteSpace
                i = 0;
                while ((i < str.length()) && (!Character.isWhitespace(str.charAt(i))))
                {
                    i++;
                }

                // add the found string to the token vector
                token = str.substring(0, i);
                result.add(token);
                str = str.delete(0, i);
            }
        }

        return result.toArray(new String[result.size()]);
    }

	private boolean readLine() throws IOException
	{
		line = br.readLine();

		if (line != null)
		{
			tokens = tokenizeLine(line);
			linenum++;
		}

		return line != null;
	}

	private void parseMaterial() throws Ac3dException
	{
		Ac3dMaterial material = new Ac3dMaterial(tokens, linenum);

		materials.add(material);
	}

	private void parseMaterial(String name) throws IOException, Ac3dException
	{
		Ac3dMaterial material = new Ac3dMaterial(name);

		while (readLine())
		{
			if (tokens.length == 0)
			{
				continue;
			}
			else if (tokens[0].equals("rgb"))
			{
				material.setRgb(tokens);
			}
			else if (tokens[0].equals("amb"))
			{
				material.setAmb(tokens);
			}
			else if (tokens[0].equals("emis"))
			{
				material.setEmis(tokens);
			}
			else if (tokens[0].equals("spec"))
			{
				material.setSpec(tokens);
			}
			else if (tokens[0].equals("shi"))
			{
				material.setShi(tokens);
			}
			else if (tokens[0].equals("trans"))
			{
				material.setTrans(tokens);
			}
			else if (tokens[0].equals("ENDMAT"))
			{
				materials.add(material);
				break;
			}
			else
			{
				throw new Ac3dException("unexpected token: " + tokens[0], linenum);
			}
		}
	}

	private void parseObject(Ac3dObject parent) throws Ac3dException, IOException
	{
		Ac3dObject	object = new Ac3dObject(tokens[1], linenum);

		if (parent == null)
			root = object;
		else
			parent.addKid(object);

		while (readLine())
		{
			if (tokens.length == 0)
			{
				continue;
			}
			else if (tokens[0].equals("name"))
			{
				object.setName(tokens[1]);
			}
			else if (tokens[0].equals("data"))
			{
				int toRead = Integer.parseInt(tokens[1]);

				int wasRead = 0;
				String data = new String();

				while (wasRead < toRead)
				{
					readLine();

					wasRead += line.length();
					data = data + line;
				}

				object.setData(data);
			}
			else if (tokens[0].equals("texture"))
			{
				object.setTexture(tokens[1]);
			}
			else if (tokens[0].equals("texrep"))
			{
				object.setTexrep(tokens);
			}
			else if (tokens[0].equals("texoff"))
			{
				object.setTexoff(tokens);
			}
			else if (tokens[0].equals("subdiv"))
			{
				object.setSubdiv(tokens);
			}
			else if (tokens[0].equals("crease"))
			{
				object.setCrease(tokens);
			}
			else if (tokens[0].equals("rot"))
			{
				object.setRot(tokens);
			}
			else if (tokens[0].equals("loc"))
			{
				object.setLoc(tokens);
			}
			else if (tokens[0].equals("url"))
			{
				object.setUrl(tokens[1]);
			}
			else if (tokens[0].equals("hidden"))
			{
				object.setHidden(true);
			}
			else if (tokens[0].equals("locked"))
			{
				object.setLocked(true);
			}
			else if (tokens[0].equals("folded"))
			{
				object.setFolded(true);
			}
			else if (tokens[0].equals("numvert"))
			{
				int numvert = Integer.parseInt(tokens[1]);

				for (int i = 0; i < numvert; i++)
				{
					readLine();

					object.addVertex(tokens);
				}
			}
			else if (tokens[0].equals("numsurf"))
			{
				int numsurf = Integer.parseInt(tokens[1]);

				for (int i = 0; i < numsurf; i++)
				{
					Ac3dSurface surface = new Ac3dSurface();

					while (readLine())
					{
						if (tokens.length == 0)
						{
							continue;
						}
						else if (tokens[0].equals("SURF"))
						{
							surface.setSurf(Integer.decode(tokens[1]));
							surface.setLinenum(linenum);
						}
						else if (tokens[0].equals("mat"))
						{
							surface.setMat(Integer.parseInt(tokens[1]));
						}
						else if (tokens[0].equals("refs"))
						{
							int refs = Integer.parseInt(tokens[1]);

							for (int j = 0; j < refs; j++)
							{
								readLine();

								surface.addRef(tokens);
							}

							object.addSurface(surface);

							break;
						}
						else
						{
							throw new Ac3dException("unexpected token: " + tokens[0], linenum);
						}
					}
				}
			}
			else if (tokens[0].equals("kids"))
			{
				int kids = Integer.parseInt(tokens[1]);

				if (kids == 0)
					break;
				else
				{
					while (readLine())
					{
						if (tokens.length == 0 || (tokens.length == 1 && tokens[0].trim().isEmpty()))
						{
							continue;
						}
						else if (tokens[0].equals("OBJECT"))
						{
							parseObject(object);
						}
						else
						{
							throw new Ac3dException("unexpected token: " + tokens[0], linenum);
						}
					}
				}
			}
			else
			{
				throw new Ac3dException("unexpected token: " + tokens[0], linenum);
			}
		}
	}

	public Ac3d()
	{
	}
	
	public void read(File file) throws Exception, IOException
	{
		try
		{
			br = new BufferedReader(new FileReader(file));

			readLine();

			if ("AC3Db".equals(tokens[0]))
			{
				versionC = false;
			}
			else if ("AC3Dc".equals(tokens[0]))
			{
				versionC = true;
			}
			else
			{
				throw new Ac3dException("unsupported version: " + line, linenum);
			}

			while (readLine())
			{
				if (tokens.length == 0)
				{
					continue;
				}
				else if (tokens[0].equals("MATERIAL"))
				{
					parseMaterial();
				}
				else if (versionC && tokens[0].equals("MAT"))
				{
					parseMaterial(tokens[1]);
				}
				else if (tokens[0].equals("OBJECT"))
				{
					parseObject(null);
				}
				else
				{
					throw new Ac3dException("unexpected token: " + tokens[0], linenum);
				}
			}

			br.close();
		}
		catch (Ac3dException e)
		{
			br.close();
			throw new Exception("line " + e.getLineNumber() + " " + e.getLocalizedMessage());
		}
	}

	public void write(String fileName) throws IOException
	{
		FileWriter  file = new FileWriter (fileName);

		if (versionC)
			file.write("AC3Dc\n");
		else
			file.write("AC3Db\n");

		for (int i = 0; i < materials.size(); ++i)
		{
			materials.get(i).write(file);
		}

		root.write(file);

		file.close();
	}

	public Vector<Ac3dMaterial> getMaterials()
	{
		return materials;
	}

	public void setMaterials(Vector<Ac3dMaterial> materials)
	{
		this.materials = materials;
	}

	public Ac3dObject getRoot()
	{
		return root;
	}

	public void setRoot(Ac3dObject root)
	{
		this.root = root;
	}
}
