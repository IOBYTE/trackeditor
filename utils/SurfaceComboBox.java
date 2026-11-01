package utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;

import gui.EditorFrame;
import utils.circuit.Surface;

public class SurfaceComboBox extends JComboBox<String>
{
	static private HashMap<String, String>	tooltipMap = new HashMap<>();
	
	private EditorFrame						editorFrame;
	private Vector<String>					surfaceVector;

	private String 							sep	= System.getProperty("file.separator");

	public SurfaceComboBox(EditorFrame editorFrame, Vector<String> surfaceVector)
	{
		super(surfaceVector);
		
		this.editorFrame = editorFrame;
		this.surfaceVector = surfaceVector;
		
		initialize();
	}

	private void initialize()
	{
		ComboboxToolTipRenderer	renderer = new ComboboxToolTipRenderer();
		renderer.setTooltips(getSurfaceImages());
		setRenderer(renderer);
		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (getSelectedIndex() != -1)
				{
					String surfaceName = getSelectedItem().toString();
					if (surfaceName != null && !surfaceName.isEmpty())
					{
						String tooltipText = null;
						Vector<Surface> defaultSurfaces = editorFrame.getDefaultSurfaces();
						Vector<Surface>	surfaces = editorFrame.getTrackData().getSurfaces();

						// look for texture in track directory first
						tooltipText = getTextureName(surfaceName, surfaces, Properties.getInstance().getPath());

						if (tooltipText == null && editorFrame.getDataDirectory() != null)
						{
							// look for texture in default textures directory if available
							tooltipText = getTextureName(surfaceName, defaultSurfaces, editorFrame.getDataDirectory() + sep + "data" + sep + "textures");
						}
						if (tooltipText == null)
						{
							tooltipText = textureNotFound(surfaceName, surfaces, defaultSurfaces);
						}
						setToolTipText(tooltipText);
					}
				}
			}
		});
	}

	private class ComboboxToolTipRenderer extends DefaultListCellRenderer
	{
		List<String> tooltips;

		@Override
		public JComponent getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus)
		{
			JComponent comp = (JComponent) super.getListCellRendererComponent(
				list, value, index, isSelected, cellHasFocus);

			if (-1 < index && null != value && null != tooltips && index < tooltips.size())
			{
				list.setToolTipText(tooltips.get(index));
			}
			return comp;
		}

		public void setTooltips(List<String> tooltips)
		{
			this.tooltips = tooltips;
		}
	};

	private String getTextureName(String surfaceName, Vector<Surface> surfaces, String path)
	{
		String tooltipText = null;

		for (int j = 0; j < surfaces.size(); j++)
		{
			Surface surface = surfaces.get(j);
			if (surface.getName().equals(surfaceName))
			{
				String textureName = surface.getTextureName();
				if (textureName != null && !textureName.isEmpty())
				{
					String fileName = path + sep + textureName;
					File file = new File(fileName);
					if (!file.canRead())
					{
						// return null so the caller can try another directory
					}
					else
					{
						try
						{
							URL url = file.toURI().toURL();

							if (tooltipMap.containsKey(url.toString()))
							{
								tooltipText = tooltipMap.get(url.toString());
							}
							else
							{
								URL imgUrl = url;
								int width = 256;
								int height = 256;

								try
								{
									BufferedImage image;
									if (SgiReader.isSgiFile(file.getName()))
									{
										image = SgiReader.read(file);
									}
									else
									{
										image = ImageIO.read(file);
									}
									if (image != null)
									{
										width = image.getWidth();
										height = image.getHeight();

										if (SgiReader.isSgiFile(file.getName()))
										{
											// Swing's HTML renderer can't display SGI files,
											// so write a cached PNG for the tooltip
											File pngFile = new File(file.getParentFile(),
												file.getName() + ".preview.png");
											if (!pngFile.exists())
												ImageIO.write(image, "png", pngFile);
											imgUrl = pngFile.toURI().toURL();
										}
									}
									if (width > 256 || height > 256)
									{
										if (width == height)
										{
											width = 256;
											height = 256;
										}
										else
										{
											height = height * 256 / width;
											width = 256;
										}
									}
								}
								catch (Exception e)
								{
									// can't read image dimensions or convert
								}

								tooltipText = "<html><img src=\"" + imgUrl.toString() + "\" width=\"" + width + "\" height=\"" + height + "\" ><br>" + textureName + "</html>";
								tooltipMap.put(url.toString(), tooltipText);
							}
						}
						catch (MalformedURLException e)
						{
						}
					}
				}
				break;
			}
		}
		return tooltipText;
	}

	private String textureNotFound(String surfaceName, Vector<Surface> surfaces, Vector<Surface> defaultSurfaces)
	{
		// find the texture name from either surface list
		for (Vector<Surface> list : new Vector[] { surfaces, defaultSurfaces })
		{
			for (int i = 0; i < list.size(); i++)
			{
				Surface surface = (Surface) list.get(i);
				if (surface.getName().equals(surfaceName))
				{
					String textureName = surface.getTextureName();
					if (textureName != null && !textureName.isEmpty())
						return "Texture not found: " + textureName;
				}
			}
		}
		return null;
	}

	private List<String> getSurfaceImages()
	{
		List<String> tooltips = new ArrayList<String>();
		Vector<Surface> defaultSurfaces = editorFrame.getDefaultSurfaces();
		Vector<Surface>	surfaces = editorFrame.getTrackData().getSurfaces();
		for (int i = 0; i < surfaceVector.size(); i++)
		{
			String tooltipText = null;
			String surfaceName = surfaceVector.get(i);
			if (editorFrame.getDataDirectory() != null)
			{
				tooltipText = getTextureName(surfaceName, defaultSurfaces, editorFrame.getDataDirectory() + sep + "data" + sep + "textures");
			}
			if (tooltipText == null)
			{
				tooltipText = getTextureName(surfaceName, surfaces, Properties.getInstance().getPath());
			}
			if (tooltipText == null)
			{
				tooltipText = textureNotFound(surfaceName, surfaces, defaultSurfaces);
			}
			tooltips.add(tooltipText);
		}
		return tooltips;
	}
}
