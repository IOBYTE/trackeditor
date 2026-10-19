package utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
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
						tooltipText = getTextureName(surfaceName, surfaces, Editor.getProperties().getPath());

						if (tooltipText == null && editorFrame.getDataDirectory() != null)
						{
							// look for texture in default textures directory if available
							tooltipText = getTextureName(surfaceName, defaultSurfaces, editorFrame.getDataDirectory() + sep + "data" + sep + "textures");
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
					if (file.canRead())
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
								int width = 256;
								int height = 256;

								try
								{
									InputStream stream = url.openStream();
									ImageInputStream input = ImageIO.createImageInputStream(stream);
									ImageReader reader = ImageIO.getImageReaders(input).next();
									reader.setInput(input);
									width = reader.getWidth(0);
									height = reader.getHeight(0);
									reader.dispose();
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
									input.close();
									stream.close();
								}
								catch (NoSuchElementException e)
								{
									// sgi rgb files are not supported
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}

								tooltipText = "<html><img src=\"" + url.toString() + "\" width=\"" + width + "\" height=\"" + height + "\" ><br>" + textureName + "</html>";
								tooltipMap.put(url.toString(), tooltipText);
							}
						}
						catch (MalformedURLException e)
						{
							System.out.println("tooltipText " + tooltipText);
						}
					}
				}
				break;
			}
		}
		return tooltipText;
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
				tooltipText = getTextureName(surfaceName, surfaces, Editor.getProperties().getPath());
			}
			tooltips.add(tooltipText);
		}
		return tooltips;
	}
}
