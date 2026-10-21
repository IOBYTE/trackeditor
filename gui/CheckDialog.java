package gui;

import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import utils.Editor;
import utils.SegmentVector;
import utils.TrackData;
import utils.ac3d.Ac3d;
import utils.ac3d.Ac3dException;
import utils.ac3d.Ac3dMaterial;
import utils.ac3d.Ac3dObject;
import utils.ac3d.Ac3dSurface;
import utils.circuit.Curve;
import utils.circuit.EnvironmentMapping;
import utils.circuit.GameSides;
import utils.circuit.GraphicObject;
import utils.circuit.ObjShapeObject;
import utils.circuit.ObjectMap;
import utils.circuit.Pits;
import utils.circuit.Segment;
import utils.circuit.SegmentSide;
import utils.circuit.Surface;
import utils.circuit.TerrainGeneration;
import utils.circuit.TrackLight;
import utils.circuit.TrackObject;

public class CheckDialog extends JDialog
{
	private final String 		sep 			= System.getProperty("file.separator");
	private JScrollPane			scrollPane		= null;
	private JTextArea			textArea		= null;
	private EditorFrame			editorFrame		= null;
	private Vector<Surface>		defaultSurfaces	= null;
	private Vector<TrackObject>	defaultObjects	= null;
	private Boolean				doubleSided		= false;
	private Boolean				flatShaded		= false;
	private Set<String> 		textures 		= new HashSet<String>();
	private String				dataDirectory	= null;
	private TrackData			trackData		= null;
	private Map<String,Boolean>	imageHasAlpha	= new HashMap<String, Boolean>();

	public CheckDialog(EditorFrame editorFrame)
	{
		super();
		this.editorFrame = editorFrame;
		defaultSurfaces = editorFrame.getDefaultSurfaces();
		defaultObjects = editorFrame.getDefaultObjects();
		dataDirectory = editorFrame.getDataDirectory();
		trackData = editorFrame.getTrackData();
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize()
	{
		this.setTitle("Check Track");
		this.setSize(editorFrame.getProject().getCheckDialogWidth(), editorFrame.getProject().getCheckDialogHeight());
		this.setResizable(true);
		Point p = new Point();
		p.x = editorFrame.getProject().getCheckDialogX();
		p.y = editorFrame.getProject().getCheckDialogY();
		this.setLocation(p);

		textArea = new JTextArea();
	    textArea.setLineWrap(false);
	    textArea.setEditable(false);
	    textArea.setVisible(true);

	    scrollPane = new JScrollPane (textArea);
	    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	    add(scrollPane);

	    addComponentListener(new ComponentAdapter()
	    {
	    	public void componentShown(ComponentEvent e)
	    	{
	    		checkTrackHeight();
	    		checkSurfaces();
	    		checkObjects();
	    		checkTerrainGeneration();
	    		checkEnvironmentMapping();
	    		checkTrackLights();
	    		checkGraphic();
	    		checkPits();
	    		checkTrack();
	    		checkHeader();

	    		textArea.append("Checking complete!");
	    	}
	    });
	    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	private void checkTrack()
	{
		SegmentVector segments = trackData.getSegments();

		for (int i = 0; i < segments.size(); i++)
		{
			Segment segment = segments.get(i);

			if (!segment.getType().equals("str"))
			{
				Curve curve = (Curve) segment;
				double startWidth = trackData.getMainTrack().getValidWidth() / 2.0;
				double endWidth = startWidth;

				if (segment.getType().equals("rgt"))
				{
					startWidth += curve.getValidRightBorderWidth(editorFrame);
					endWidth = startWidth;

					startWidth += curve.getValidRightSideStartWidth(editorFrame);
					endWidth += curve.getValidRightSideEndWidth(editorFrame);
				}
				else
				{
					startWidth += curve.getValidLeftBorderWidth(editorFrame);
					endWidth = startWidth;

					startWidth += curve.getValidLeftSideStartWidth(editorFrame);
					endWidth += curve.getValidLeftSideEndWidth(editorFrame);
				}

				if (startWidth > curve.getRadiusStart())
				{
					textArea.append("Segment " + curve.getName() + " Distance to barrier: " + startWidth + " greater than start radius: " + curve.getRadiusStart() + "\n");
				}
				if (endWidth > curve.getRadiusEnd())
				{
					textArea.append("Segment " + curve.getName() + " Distance to barrier: " + endWidth + " greater than end radius: " + curve.getRadiusEnd() + "\n");
				}
			}
		}
		
		// the editor shows what the game uses (see GameSides); these point out values in the
		// file that the game ignores or that it takes from an earlier segment
		for (int i = 0; i < segments.size(); i++)
		{
			Segment segment = segments.get(i);
			Segment previousSegment = i > 0 ? segments.get(i - 1) : null;

			checkSide(segment, previousSegment, segment.getLeft(), "Left", false);
			checkSide(segment, previousSegment, segment.getRight(), "Right", true);
		}
	}

	private void checkSide(Segment segment, Segment previousSegment, SegmentSide side, String name, boolean right)
	{
		GameSides.State state = GameSides.of(segment, right, editorFrame);
		String prefix = "Segment " + segment.getName() + " " + name;

		// border
		String borderStyle = side.getBorderStyle();
		if (borderStyle != null && !borderStyle.isEmpty() &&
			!borderStyle.equals("plan") && !borderStyle.equals("curb") && !borderStyle.equals("wall"))
		{
			textArea.append(prefix + " border style \"" + borderStyle + "\" isn't a border style, so the game makes it a wall\n");
		}
		// (TORCS keeps a plan border's height for the borders after it, so it isn't ignored there)
		if (editorFrame.getGameType().planBorderResetsHeight() &&
			state.borderStyle.equals("plan") && !Double.isNaN(side.getBorderHeight()) && side.getBorderHeight() != 0)
		{
			textArea.append(prefix + " border style is plan with " + side.getBorderHeight() + " height, which the game ignores (it uses 0): set border style to curb or wall or don't set a height\n");
		}

		// barrier
		String barrierStyle = side.getBarrierStyle();
		boolean fence = state.barrierStyle.startsWith("fence");
		boolean walls = editorFrame.getGameType().otherBarrierStylesAreWalls();
		if (barrierStyle != null && !barrierStyle.isEmpty() && !barrierStyle.equals("wall") &&
			!(barrierStyle.equals("no barrier") && !walls) &&
			!barrierStyle.equals("fence") && !(editorFrame.getGameType().hasFence1Fence2() && (barrierStyle.equals("fence1") || barrierStyle.equals("fence2"))))
		{
			if (walls)
				textArea.append(prefix + " barrier style \"" + barrierStyle + "\" isn't fence or wall, so the game makes it a wall\n");
			else
				textArea.append(prefix + " barrier style \"" + barrierStyle + "\" isn't one the game knows, so it keeps the " + state.barrierStyle + " from before\n");
		}
		if ((fence || state.barrierStyle.equals("no barrier")) && !Double.isNaN(side.getBarrierWidth()) && side.getBarrierWidth() != 0)
		{
			textArea.append(prefix + " barrier style is " + state.barrierStyle + " with " + side.getBarrierWidth() + " width, which the game ignores (it uses 0): set barrier style to wall or don't set a width\n");
		}
		if (state.barrierStyle.equals("no barrier") && !Double.isNaN(side.getBarrierHeight()) && side.getBarrierHeight() != 0)
		{
			textArea.append(prefix + " barrier style is no barrier with " + side.getBarrierHeight() + " height, which the game ignores (it uses 0)\n");
		}

		if (previousSegment == null)
			return;

		// a new style takes its size from the previous segment unless it is set
		GameSides.State previous = GameSides.of(previousSegment, right, editorFrame);
		if (!state.borderStyle.equals(previous.borderStyle) && !state.borderStyle.equals("plan") && Double.isNaN(side.getBorderHeight()))
		{
			textArea.append(prefix + " border style changed from " + previous.borderStyle + " to " + state.borderStyle + " but new height not set (the game uses " + state.borderHeight + ")\n");
		}
		if (!state.barrierStyle.equals(previous.barrierStyle) && state.barrierStyle.equals("wall") && Double.isNaN(side.getBarrierWidth()))
		{
			textArea.append(prefix + " barrier style changed from " + previous.barrierStyle + " to wall but new width not set (the game uses " + state.barrierWidth + ")\n");
		}
		// CSR's track loader takes a missing barrier style from a list that has no
		// "no barrier", so after one it reads past the end of the list (once per run
		// of segments: reported where the run starts)
		SegmentSide previousSide = right ? previousSegment.getRight() : previousSegment.getLeft();
		if (editorFrame.getCarsSportsRacing() && "no barrier".equals(previousSide.getBarrierStyle()) &&
			(barrierStyle == null || barrierStyle.isEmpty()))
		{
			textArea.append(prefix + " follows a no barrier side without setting its own barrier style: CSR then uses an undefined style for it and the segments after it until one sets a barrier style, and can crash\n");
		}
	}
	
	public void checkHeader()
	{
		if (trackData.getHeader().getSubcategory() != null)
		{
			if (!"speedway".equals(trackData.getHeader().getCategory()))
			{
				textArea.append("Subcategory " + trackData.getHeader().getSubcategory() + " should only be used with the speedway category\n");
			}
		}
	}

	private void checkPits()
	{
		Pits pits = trackData.getMainTrack().getPits();

		if (pits == null)
			return;

		boolean noSegments = !hasText(pits.getEntry()) && !hasText(pits.getStart()) && !hasText(pits.getEnd())
				&& !hasText(pits.getExit());

		if (!noSegments)
		{
			SegmentVector segments = trackData.getSegments();

			if (!hasText(pits.getEntry()))
				textArea.append("Missing pit entry\n");
			else if (segments.getSegmentFromName(pits.getEntry()) == null)
				textArea.append("Invalid pit entry: " + pits.getEntry() + "\n");

			if (!hasText(pits.getStart()))
				textArea.append("Missing pit start\n");
			else if (segments.getSegmentFromName(pits.getStart()) == null)
				textArea.append("Invalid pit start: " + pits.getStart() + "\n");

			if (!hasText(pits.getEnd()))
				textArea.append("Missing pit end\n");
			else if (segments.getSegmentFromName(pits.getEnd()) == null)
				textArea.append("Invalid pit end: " + pits.getEnd() + "\n");

			if (!hasText(pits.getExit()))
				textArea.append("Missing pit exit\n");
			else if (segments.getSegmentFromName(pits.getExit()) == null)
				textArea.append("Invalid pit exit: " + pits.getExit() + "\n");

			if (!hasText(pits.getSide()))
				textArea.append("Missing pit side\n");
			else if (!(pits.getSide().equals("left") || pits.getSide().equals("right")))
				textArea.append("Invalid pit side: " + pits.getSide() + "\n");
		}
	}

	private void checkTrackHeight()
	{
		SegmentVector segments = trackData.getSegments();

		double width = trackData.getMainTrack().getValidWidth();

		double firstHeightStart = Double.NaN;
		double previousHeightEnd = Double.NaN;

		double firstHeightStartLeft = Double.NaN;
		double firstHeightStartRight = Double.NaN;
		double previousHeightEndLeft = Double.NaN;
		double previousHeightEndRight = Double.NaN;

		for (int i = 0; i < segments.size(); i++)
		{
			Segment segment = segments.get(i);

			String	segmentInfo = "Segment " + segment.getName() + " : ";

			double heightStart = segment.getHeightStart();
			boolean hasHeightStart = !Double.isNaN(heightStart);

			double heightStartLeft = segment.getHeightStartLeft();
			boolean hasHeightStartLeft = !Double.isNaN(heightStartLeft);

			double heightStartRight = segment.getHeightStartRight();
			boolean hasHeightStartRight = !Double.isNaN(heightStartRight);

			boolean hasHeightStartLeftRight = hasHeightStartLeft && hasHeightStartRight;
			boolean hasBankingStartFromHeights = hasHeightStartLeftRight && heightStartLeft != heightStartRight;

			double bankingStart = segment.getBankingStart();
			boolean hasBankingStart = !Double.isNaN(bankingStart);
			
			if (hasHeightStart && (hasHeightStartLeft || hasHeightStartRight))
			{
				textArea.append(segmentInfo + "has height start and height start left or height start right\n");				
			}

			// Track should start at an elevation of 0.0.
			if (i == 0)
			{
				firstHeightStart = heightStart;
				firstHeightStartLeft = heightStartLeft;
				firstHeightStartRight = heightStartRight;

				if (hasHeightStartLeft && hasHeightStartRight)
				{
					double centerHeight = (heightStartLeft + heightStartRight) / 2.0;

					if (centerHeight != 0.0)
					{
						// disabled because many tracks don't start at 0 because of banking
						// or because they use the actual elevation
						//textArea.append(segmentInfo + "Track height at start should be 0. Actual height: " + centerHeight +"\n");
					}
				}
			}

			if (hasBankingStart && hasBankingStartFromHeights)
			{
				textArea.append(segmentInfo + "Banking start angle and banking from heights\n");

				double bankingFromHeights = Math.toDegrees(Math.atan2(heightStartLeft - heightStartRight, width));

				if (bankingStart != bankingFromHeights)
				{
					textArea.append(segmentInfo + "Banking start: " + bankingStart + " doesn't match banking from heights: " + bankingFromHeights + "\n");
				}
			}

			double heightEnd = segment.getHeightEnd();
			boolean hasHeightEnd = !Double.isNaN(heightEnd);

			double heightEndLeft = segment.getHeightEndLeft();
			boolean hasHeightEndLeft = !Double.isNaN(heightEndLeft);

			double heightEndRight = segment.getHeightEndRight();
			boolean hasHeightEndRight = !Double.isNaN(heightEndRight);

			boolean hasHeightEndLeftRight = hasHeightEndLeft && hasHeightEndRight;
			boolean hasBankingEndFromHeights = hasHeightEndLeftRight && heightEndLeft != heightEndRight;

			double bankingEnd = segment.getBankingEnd();
			boolean hasBankingEnd = !Double.isNaN(bankingEnd);

			if (hasHeightEnd && (hasHeightEndLeft || hasHeightEndRight))
			{
				textArea.append(segmentInfo + "has height end and height end left or height end right\n");				
			}

			if (hasBankingEnd && hasBankingEndFromHeights)
			{
				textArea.append(segmentInfo + "Banking end angle and banking from heights\n");

				double bankingFromHeights = Math.toDegrees(Math.atan2(heightEndLeft - heightEndRight, width));

				if (bankingEnd != bankingFromHeights)
				{
					textArea.append(segmentInfo + "Banking end: " + bankingEnd + " doesn't match banking from heights: " + bankingFromHeights + "\n");
				}
			}

			double grade = segment.getGrade();
			boolean hasGrade = !Double.isNaN(grade);

			if (hasGrade && hasHeightEndLeftRight)
			{
				if (hasBankingEndFromHeights)
				{
					textArea.append(segmentInfo + "Grade and end banking from heights\n");
				}
				else
				{
					textArea.append(segmentInfo + "Grade and end height\n");
				}
			}

			Segment previous = segment.previousShape;
			Segment next = segment.nextShape;

			if (previous != null && hasHeightStartLeftRight)
			{
				if (!Double.isNaN(previousHeightEndLeft) && previousHeightEndLeft != heightStartLeft)
				{
					textArea.append(segmentInfo + "Previous height end left : " + previousHeightEndLeft + " doesn't match " + heightStartLeft + "\n");
				}
				
				if (!Double.isNaN(previousHeightEnd) && previousHeightEnd != heightStartLeft)
				{
					textArea.append(segmentInfo + "Previous height end : " + previousHeightEnd + " doesn't match " + heightStartLeft + "\n");
				} 

				if (!Double.isNaN(previousHeightEndRight) && previousHeightEndRight != heightStartRight)
				{
					textArea.append(segmentInfo + "Previous height end right : " + previousHeightEndRight + " doesn't match " + heightStartRight + "\n");
				}
				
				if (!Double.isNaN(previousHeightEnd) && previousHeightEnd != heightStartRight)
				{
					textArea.append(segmentInfo + "Previous height end : " + previousHeightEnd + " doesn't match " + heightStartRight + "\n");
				}
			}
			else if (previous != null && hasHeightStart)
			{
				if (!Double.isNaN(previousHeightEndLeft) && previousHeightEndLeft != heightStart)
				{
					textArea.append(segmentInfo + "Previous height end left : " + previousHeightEndLeft + " doesn't match " + heightStart + "\n");
				}
				
				if (!Double.isNaN(previousHeightEndRight) && previousHeightEndRight != heightStart)
				{
					textArea.append(segmentInfo + "Previous height end right : " + previousHeightEndRight + " doesn't match " + heightStart + "\n");
				}
				
				if (!Double.isNaN(previousHeightEnd) && previousHeightEnd != heightStart)
				{
					textArea.append(segmentInfo + "Previous height end : " + previousHeightEnd + " doesn't match " + heightStart + "\n");
				}
			}
			else if (next == null && hasHeightEndLeftRight)
			{
				if (!Double.isNaN(firstHeightStartLeft) && firstHeightStartLeft != heightEndLeft)
				{
					textArea.append(segmentInfo + "Height end left : " + heightEndLeft + " doesn't match " + firstHeightStartLeft + "\n");
				}
				
				if (!Double.isNaN(firstHeightStart) && firstHeightStart != heightEndLeft)
				{
					textArea.append(segmentInfo + "Height end left : " + heightEndLeft + " doesn't match " + firstHeightStart + "\n");
				}

				if (!Double.isNaN(firstHeightStartRight) && firstHeightStartRight != heightEndRight)
				{
					textArea.append(segmentInfo + "Height end right : " + heightEndRight + " doesn't match " + firstHeightStartRight + "\n");
				}
				
				if (!Double.isNaN(firstHeightStart) && firstHeightStart != heightEndRight)
				{
					textArea.append(segmentInfo + "Height end right : " + heightEndRight + " doesn't match " + firstHeightStart + "\n");
				}
			}
			else if (next == null && hasHeightEnd)
			{
				if (!Double.isNaN(firstHeightStartLeft) && firstHeightStartLeft != heightEnd)
				{
					textArea.append(segmentInfo + "Height end : " + heightEnd + " doesn't match " + firstHeightStartLeft + "\n");
				}
				
				if (!Double.isNaN(firstHeightStartRight) && firstHeightStartRight != heightEnd)
				{
					textArea.append(segmentInfo + "Height end : " + heightEnd + " doesn't match " + firstHeightStartRight + "\n");
				}
				
				if (!Double.isNaN(firstHeightStart) && firstHeightStart != heightEnd)
				{
					textArea.append(segmentInfo + "Height end : " + heightEnd + " doesn't match " + firstHeightStart + "\n");
				}
			}
			
			previousHeightEnd = heightEnd;
			previousHeightEndLeft = heightEndLeft;
			previousHeightEndRight = heightEndRight;
		}
	}

	private void checkGraphic()
	{
		String image = trackData.getGraphic().getBackgroundImage();
		if (hasText(image))
			checkTexture("Graphic Background Image ", image);
	}

	private void checkTrackLights()
	{
		Vector<TrackLight> lightData = trackData.getTrackLights();

		for (int i = 0; i < lightData.size(); i++)
		{
			TrackLight light = lightData.get(i);

			String texture = light.getTextureOn();
			checkTexture("Track Lights " + light.getName() + " Texture On ", texture);

			texture = light.getTextureOff();
			checkTexture("Track Lights " + light.getName() + " Texture Off ", texture);
		}
	}
	private void checkEnvironmentMapping()
	{
		Vector<EnvironmentMapping>	envMaps = trackData.getGraphic().getEnvironmentMapping();

		for (int i = 0; i < envMaps.size(); i++)
		{
			String texture = envMaps.get(i).getEnvMapImage();
			checkTexture("Environment Map " + envMaps.get(i).getName(), texture);
		}
	}

	private void checkTerrainGeneration()
	{
		String reliefFile = trackData.getGraphic().getTerrainGeneration().getReliefFile();

		if (reliefFile != null && !reliefFile.isEmpty())
		{
			File file = findObjectFile(reliefFile);

			if (file == null)
			{
				textArea.append("Terrain Generation relief file " + reliefFile + " not found\n");
			}
			else
			{
				Ac3d ac3dFile = new Ac3d();

				try
				{
					ac3dFile.read(file);

					for (Ac3dMaterial material : ac3dFile.getMaterials())
					{
						String name = material.getName();
						
						if (name != null && !name.isEmpty())
						{
							if (name.contains(" "))
							{
								textArea.append("Relief file " + file.toString() + " : material \"" + name + "\" has space in name\n");					
							}
						}
					}

					Ac3dObject root = ac3dFile.getRoot();

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
									textArea.append("Terrain Generation relief file " + reliefFile + " line " + object.getLinenum() + " : missing interior or exterior data\n");									
								}
								else if (!(data.equals("interior") || data.equals("exterior")))
								{
									textArea.append("Terrain Generation relief file " + reliefFile + " line " + object.getLinenum() + " : expected interior or exterior data but found " + data + "\n");
								}

								// sd2-trackgen expects borders and holes to be closed line and generates bad terrain when using line and duplicate vertices 
								for (int j = 0; j < object.getSurfaces().size(); j++)
								{
									Ac3dSurface	surface = object.getSurfaces().get(j);

									if (surface.isOpenLine())
									{
										double[] firstVertex = object.getVertices().get(0);
										double[] lastVertex = object.getVertices().get(object.getVertices().size() - 1);
										
										if (firstVertex[0] == lastVertex[0] && firstVertex[1] == lastVertex[1] && firstVertex[2] == lastVertex[2])
										{
											textArea.append("Terrain Generation relief file " + reliefFile + " line " + object.getLinenum() + " : line with first and last vertices same\n");																				
										}
									}
								}
							}
						}
					}
				}
				catch (Ac3dException e)
				{
					textArea.append("Terrain Generation relief file " + reliefFile + " line " + e.getLineNumber() + " : " + e.getLocalizedMessage() + "\n");
				}
				catch (Exception e)
				{
					textArea.append("Terrain Generation relief file " + reliefFile + " : " + e.getLocalizedMessage() + "\n");
				}
			}
		}

		String elevationFile = trackData.getGraphic().getTerrainGeneration().getElevationMap();

		if (elevationFile != null && !elevationFile.isEmpty())
		{
			File file = new File(Editor.getProperties().getPath() + sep + elevationFile);

			if (!file.exists())
			{
				textArea.append("Terrain Generation elevation file " + elevationFile + " not found\n");
			}
		}

		// get colors from image files
		Vector<ObjectMap>	objectMaps = trackData.getObjectMaps();
		Set<Integer>		colors = new HashSet<Integer>();

		for (int i = 0; i < objectMaps.size(); i++)
		{
			ObjectMap objectMap = objectMaps.get(i);
			String objectMapName = objectMap.getName();
			String objectMapFile = objectMap.getObjectMap();

			if (!hasText(objectMapName))
			{
				textArea.append("Terrain Generation object map " + (i + 1) + " missing name\n");
				objectMapName = (i + 1) + "";
			}

			if (!hasText(objectMapFile))
			{
				textArea.append("Terrain Generation object map " + objectMapName + " missing file name\n");
			}
			else
			{
				Path filename = Paths.get(objectMapFile);
				Path trackPath = Paths.get(Editor.getProperties().getPath());

				// check for parent directory
				if (filename.getParent() == null)
				{
					// use track directory
					filename = Paths.get(trackPath.toString(), filename.toString());
				}

				File file = new File(filename.toString());

				if (!file.exists())
				{
					textArea.append("Terrain Generation object map file " + objectMapFile + " not found\n");
				}
				else
				{
					colors.addAll(objectMap.getColors());
					
					if (objectMap.getImageWidth() != 1024)
					{
						textArea.append("Terrain Generation object map " + objectMapName + " file " + objectMapFile + " image width must be 1024 but found " + objectMap.getImageWidth() + "\n");
					}
				}
			}
		}

		Set<Integer> definedColors = new HashSet<Integer>();

		// get defined colors from track objects
		for (int i = 0; i < trackData.getObjects().size(); i++)
		{
			int color = trackData.getObjects().get(i).getColor();

			if (color == Integer.MAX_VALUE)
			{
				textArea.append("Track object " + (i + 1) + " missing color\n");
			}
			else
			{
				definedColors.add(color);
			}
		}

		// get defined colors from default objects
		for (int i = 0; i < defaultObjects.size(); i++)
		{
			int color = defaultObjects.get(i).getColor();

			if (color == Integer.MAX_VALUE)
			{
				textArea.append("Default object " + (i + 1) + " missing color\n");
			}
			else
			{
				definedColors.add(color);
			}
		}

		// check for undefined image colors
		Iterator<Integer> colorsIterator = colors.iterator();

		while (colorsIterator.hasNext())
		{
			int color = colorsIterator.next();

			if (!definedColors.contains(color))
			{
				textArea.append("No object for color " + String.format("0x%06X", color) + "\n");
			}
		}

		// check for big pixels
		colorsIterator = colors.iterator();

		while (colorsIterator.hasNext())
		{
			int color = colorsIterator.next();
			Set<Point> pixels = new HashSet<Point>();

			for (int i = 0; i < objectMaps.size(); i++)
			{
				Vector<ObjShapeObject>	objects = objectMaps.get(i).getObjects();

				for (int j = 0; j < objects.size(); j++)
				{
					ObjShapeObject object = objects.get(j);

					if (object.getRGB() == color)
					{
						Point pixel = new Point(object.getImageX(), object.getImageY());

						if (pixels.contains(pixel))
						{
							textArea.append("Duplicate objects for color " + String.format("0x%06X", color) +
									" at " + object.getImageX() + ", " + object.getImageY() + "\n");
						}
						else
						{
							pixels.add(pixel);
						}
					}
				}
			}

			Iterator<Point> pixelsIterator = pixels.iterator();

			while (pixelsIterator.hasNext())
			{
				Point pixel = pixelsIterator.next();

				if (pixels.contains(new Point(pixel.x + 1, pixel.y)) || pixels.contains(new Point(pixel.x, pixel.y + 1)))
				{
					textArea.append("Adjacent pixels for object color " + String.format("0x%06X", color) + " found at " + pixel.x + ", " + pixel.y + "\n");
				}
			}
		}

		//check for objects outside terrain boundary
		SegmentVector track = editorFrame.getTrackData().getSegments();
		int size = track.size();
		Rectangle2D.Double boundingRectangle = null;

		for(int i = 0; i < size; i++)
		{
			Rectangle2D.Double r = track.get(i).getBounds();

			if (boundingRectangle == null)
				boundingRectangle = new Rectangle2D.Double(r.x, r.y, r.width, r.height);	// a copy, add() changes it
			else
				boundingRectangle.add(r);
		}
		double border = editorFrame.getTrackData().getGraphic().getTerrainGeneration().getBorderMargin();
		if (Double.isNaN(border))
		{
			// trackgen uses this when the track doesn't set one
			border = TerrainGeneration.DEFAULT_BORDER_MARGIN;
		}
		Rectangle2D.Double terrainRectangle = new Rectangle2D.Double(boundingRectangle.getMinX() - border,
				 													 boundingRectangle.getMinY() - border,
				 													 boundingRectangle.getWidth() + (border * 2),
				 													 boundingRectangle.getHeight() + (border * 2));

		for (int i = 0; i < objectMaps.size(); i++)
		{
			ObjectMap objectMap = objectMaps.get(i);
			Vector<ObjShapeObject> objects = objectMap.getObjects();
			double widthScale = terrainRectangle.getWidth() / objectMap.getImageWidth();
			double heightScale = terrainRectangle.getHeight() / objectMap.getImageHeight();

			for (int j = 0; j < objects.size(); j++)
			{
				ObjShapeObject object = objects.get(j);
				Point2D.Double location = object.getTrackLocation();

				if (location == null)
				{
					double worldX = terrainRectangle.getMinX() + (object.getImageX() * widthScale);
					double worldY = terrainRectangle.getMinY() + ((objectMap.getImageHeight() - object.getImageY()) * heightScale);

					location = new Point2D.Double(worldX, worldY);
				}

				if (!terrainRectangle.contains(location))
				{
					textArea.append("Object Map " + objectMaps.get(i).getName() + " Object " + object.getName() + " outside terrain boundary\n");
				}
			}
		}
		
		//check for graphic objects with same name
		Vector<GraphicObject> graphicObjects = trackData.getGraphic().getTerrainGeneration().getGraphicObjects();
		for (int i = 0; i < graphicObjects.size() - 1; i++)
		{
			for (int j = i + 1; j < graphicObjects.size(); j++)
			{
				if (graphicObjects.get(i).getName().equals(graphicObjects.get(j).getName()))
				{
					textArea.append("Graphic Objects has duplicate name: " + graphicObjects.get(i).getName() + "\n");					
				}
			}
		}
	}

	private File findObjectFile(String object)
	{
		// check if there is a path
		if (object.contains(sep))
		{
			File path = new File(object);
			if (!path.exists())
			{
				return null;
			}

			return path;
		}

		// try in track directory
		File file = new File(Editor.getProperties().getPath() + sep + object);

		if (!file.exists())
		{
			// try in data/objects directory
			file = new File(dataDirectory + sep + "data" + sep + "objects" + sep + object);
			if (!file.exists())
			{
				return null;
			}
		}

		return file;
	}
	private Boolean hasText(String text)
	{
		return !(text == null || text.trim().isEmpty());
	}
	private void checkTrackObject(TrackObject trackObject, String type)
	{
		doubleSided = false;
		flatShaded = false;
		textures.clear();
		
		String	object = trackObject.getObject();

		if (!hasText(object))
		{
			textArea.append(type + " object " + object + " missing model\n");
			return;
		}

		File file = findObjectFile(object);

		if (file == null)
		{
			textArea.append(type + " object " + trackObject.getName() + " model " + object + " not found\n");
			return;
		}

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
		    String line = "";

		    while ((line = br.readLine()) != null)
		    {
		        if (line.startsWith("texture"))
		        {
		        	String texture = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
		        	checkTexture(type + " object " + trackObject.getName() + " model " + object, texture);
		        }
		    }
		    br.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		Ac3d ac3dFile = new Ac3d();

		try
		{
			ac3dFile.read(file);

			for (Ac3dMaterial material : ac3dFile.getMaterials())
			{
				String name = material.getName();
				
				if (name != null && !name.isEmpty())
				{
					if (name.contains(" "))
					{
						textArea.append("Object file " + file.toString() + " : material \"" + name + "\" has space in name\n");
					}
				}
			}

			Ac3dObject root = ac3dFile.getRoot();

			if (root != null && "world".equals(root.getType()))
			{
				for (int i = 0; i < root.getKids().size(); i++)
				{
					checkKid(file, root.getKids().get(i));
				}
			}

			if (doubleSided)
			{
				textArea.append("Object file " + file.toString() + " : has double sided surface\n");
			}

			if (flatShaded)
			{
				textArea.append("Object file " + file.toString() + " : has flat shaded surface\n");
			}

			if (textures.size() > 1)
			{
				int hasAlpha = 0;
				for (String filename : textures)
				{
					File textureFile = findTextureFile(filename);
					if (filename.endsWith(".rgba"))
					{
						hasAlpha++;
					}
					else if (textureFile != null && filename.endsWith(".png"))
					{
						BufferedImage image = ImageIO.read(textureFile);

						if (image.getColorModel().hasAlpha())
						{
							hasAlpha++;
						}
					}
				}

				// check for more than one of either alpha or non-alpha textures
				if (hasAlpha == 0 || hasAlpha == textures.size() || (textures.size() - hasAlpha > 1) || hasAlpha > 1)
				{
					textArea.append("Object file " + file.toString() + " : has multiple textures\n");
				}
			}
		}
		catch (Ac3dException e)
		{
			textArea.append("Object file " + file.toString() + " line " + e.getLineNumber() + " : " + e.getLocalizedMessage() + "\n");
		}
		catch (Exception e)
		{
			textArea.append("Object file " + file.toString() + " : " + e.getLocalizedMessage() + "\n");
		}		
	}

	public void checkKid(File file, Ac3dObject object)
	{
		if ("poly".equals(object.getType()))
		{
			if (object.getTexture() == null || object.getTexture().isEmpty())
			{
				textArea.append("Object file " + file.toString() + " line " + object.getLinenum() + " : object with no texture\n");
			}

			if (object.getSurfaces().isEmpty())
			{
				textArea.append("Object file " + file.toString() + " line " + object.getLinenum() + " : object with no surfaces\n");
				return;
			}

			Set<Integer> types = new HashSet<Integer>();
			Set<Integer> mats = new HashSet<Integer>();
			String objectTexture = object.getTexture();

			if (objectTexture != null)
			{
				textures.add(objectTexture);
			}

			for (int i = 0; i < object.getSurfaces().size(); i++)
			{
				Ac3dSurface	surface = object.getSurfaces().get(i);

				if (surface.getRefs().size() != 3)
				{
					textArea.append("Object file " + file.toString() + " line " + surface.getLinenum() + " : surface with " +  surface.getRefs().size() + " vertices\n");																				
				}

				types.add(surface.getSurf());
				mats.add(surface.getMat());
				
				if (surface.isDoubleSided() && objectTexture != null)
				{
					File textureFile = findTextureFile(objectTexture);
					if (objectTexture.endsWith(".rgba"))
					{
					}
					else if (objectTexture.endsWith(".rgb"))
					{
						doubleSided = true;
					}
					else if (textureFile != null && objectTexture.endsWith(".png"))
					{
						try
						{
							if (!imageHasAlpha.containsKey(textureFile.getAbsolutePath()))
							{
								BufferedImage image = ImageIO.read(textureFile);
								imageHasAlpha.put(textureFile.getAbsolutePath(), image.getColorModel().hasAlpha());
							}

							if (imageHasAlpha.get(textureFile.getAbsolutePath()))
							{
								doubleSided = true;
							}
						}
						catch (Exception e)
						{
						}
					}
				}
				
				if (surface.isFlatShaded())
				{
					flatShaded = true;
				}
			}

			if (types.size() > 1)
			{
				textArea.append("Object file " + file.toString() + " line " + object.getLinenum() + " : object with " + types.size() + " surface types\n");																							
			}

			if (mats.size() > 1)
			{
				textArea.append("Object file " + file.toString() + " line " + object.getLinenum() + " : object with " + mats.size() + " materials\n");																							
			}
		}
		else
		{
			for (int i = 0; i < object.getKids().size(); i++)
			{
				checkKid(file, object.getKids().get(i));
			}
		}
	}

	private void checkTexture(String description, String texture)
	{
		Vector<String> messages = new Vector<String>();
		if (!hasText(texture))
		{
			messages.add(" missing texture\n");
		}
		else
		{
			File textureFile = findTextureFile(texture);
			if (textureFile == null)
			{
				messages.add(" texture " + texture + " not found\n");
			}

			if (texture.endsWith(".rgb"))
			{
				messages.add(" texture " + texture + " should be converted to png format\n");
			}

			if (texture.contains("/") || texture.contains("\\"))
			{
				messages.add(" texture " + texture + " should remove path\n");
			}
		}
    	for (int i = 0; i < messages.size(); i++)
    	{
    		textArea.append(description + messages.get(i));
    	}
	}
	private void checkObjects()
	{
		// check for duplicate object names
		for (int i = 0; i < trackData.getObjects().size(); i++)
		{
			String name = trackData.getObjects().get(i).getName();

			if (name == null || name.isEmpty())
			{
				textArea.append("Track object " + (i + 1) + " missing name\n");
			}
			else
			{
				for (int j = i + 1; j < trackData.getObjects().size(); j++)
				{
					if (name.equals(trackData.getObjects().get(j).getName()))
					{
						textArea.append("Track object " + (i + 1) + " " + name + " has same name as Track object " + (j + 1) + " " + trackData.getObjects().get(j).getName() + "\n");
					}
				}

				for (int j = 0; j < defaultObjects.size(); j++)
				{
					if (name.equals(defaultObjects.get(j).getName()))
					{
						textArea.append("Track object " + (i + 1) + " " + name + " has same name as Default object " + (j + 1) + " " + defaultObjects.get(j).getName() + "\n");
					}
				}
			}
		}

		// check for duplicate colors
		for (int i = 0; i < trackData.getObjects().size(); i++)
		{
			String name = trackData.getObjects().get(i).getName();
			int color = trackData.getObjects().get(i).getColor();

			if (color == Integer.MAX_VALUE)
			{
				textArea.append("Track object " + (i + 1) + " missing color\n");
			}
			else
			{
				for (int j = i + 1; j < trackData.getObjects().size(); j++)
				{
					if (color == trackData.getObjects().get(j).getColor())
					{
						textArea.append("Track object " + (i + 1) + " " + name + " has same color as Track object " + (j + 1) + " " + trackData.getObjects().get(j).getName() + "\n");
					}
				}

				for (int j = 0; j < defaultObjects.size(); j++)
				{
					if (color == defaultObjects.get(j).getColor())
					{
						textArea.append("Track object " + (i + 1) + " " + name + " has same color as Default object " + (j + 1) + " " + defaultObjects.get(j).getName() + "\n");
					}
				}
			}
		}

		for (int i = 0; i < trackData.getObjects().size(); i++)
		{
			checkTrackObject(trackData.getObjects().get(i), "Track");
		}

		if (editorFrame.getCheckDefaultObjects())
		{
			for (int i = 0; i < defaultObjects.size(); i++)
			{
				checkTrackObject(defaultObjects.get(i), "Default");
			}
		}
	}
	private File findTextureFile(String texture)
	{
		File file = new File(Editor.getProperties().getPath() + sep + texture);

		if (!file.exists())
		{
			if (dataDirectory != null)
			{
				file = new File(dataDirectory + sep + "data" + sep + "textures" + sep + texture);
				if (!file.exists())
				{
					return null;
				}
			}
		}
		return file;
	}
	private void checkSurface(String surface, String description)
	{
		if (surface == null || surface.isEmpty())
			return;

		String message = description + " surface " + surface;

		for (int i = 0; i < trackData.getSurfaces().size(); i++)
		{
			if (trackData.getSurfaces().get(i).getName().equals(surface))
			{
				String texture = trackData.getSurfaces().get(i).getTextureName();
				checkTexture(message, texture);

				texture = trackData.getSurfaces().get(i).getBumpName();
				if (hasText(texture))
					checkTexture(message + " Bump ", texture);

				texture = trackData.getSurfaces().get(i).getRacelineName();
				if (hasText(texture))
					checkTexture(message + " Raceline ", texture);

				return;
			}
		}
		for (int i = 0; i < defaultSurfaces.size(); i++)
		{
			if (defaultSurfaces.get(i).getName().equals(surface))
			{
				String texture = defaultSurfaces.get(i).getTextureName();
				checkTexture(message, texture);

				texture = defaultSurfaces.get(i).getBumpName();
				if (hasText(texture))
					checkTexture(message + " Bump ", texture);

				texture = defaultSurfaces.get(i).getRacelineName();
				if (hasText(texture))
					checkTexture(message + " Raceline ", texture);

				return;
			}
		}

		textArea.append(description + " surface " + surface + " not found\n");
	}
	private void checkSurfaces()
	{
		// check for duplicate surface names
		for (int i = 0; i < trackData.getSurfaces().size(); i++)
		{
			String name = trackData.getSurfaces().get(i).getName();

			if (!hasText(name))
			{
				textArea.append("Track surface " + (i + 1) + " missing name\n");
			}
			else
			{
				for (int j = i + 1; j < trackData.getSurfaces().size(); j++)
				{
					if (name.equals(trackData.getSurfaces().get(j).getName()))
					{
						textArea.append("Track surface " + (i + 1) + " " + name + " has same name as Track surface " + (j + 1) + " " + trackData.getSurfaces().get(i).getName() + "\n");
					}
				}

				for (int j = 0; j < defaultSurfaces.size(); j++)
				{
					if (name.equals(defaultSurfaces.get(j).getName()))
					{
						textArea.append("Track surface " + (i + 1) + " " + name + " has same name as Default surface " + (j + 1) + " " + defaultSurfaces.get(i).getName() + "\n");
					}
				}
			}
		}

		checkSurface(trackData.getMainTrack().getSurface(), "Main Track");
		checkSurface(trackData.getMainTrack().getLeft().getBorderSurface(), "Main Track Left Border");
		checkSurface(trackData.getMainTrack().getLeft().getSideSurface(), "Main Track Left Side");
		checkSurface(trackData.getMainTrack().getLeft().getBarrierSurface(), "Main Track Left Barrier");
		checkSurface(trackData.getMainTrack().getRight().getBorderSurface(), "Main Track Right Border");
		checkSurface(trackData.getMainTrack().getRight().getSideSurface(), "Main Track Right Side");
		checkSurface(trackData.getMainTrack().getRight().getBarrierSurface(), "Main Track Right Barrier");
		for (int i = 0; i < trackData.getSegments().size(); i++)
		{
			Segment segment = trackData.getSegments().get(i);

			checkSurface(segment.getSurface(), "Segment " + segment.getName() + " Track");
			checkSurface(segment.getLeft().getBorderSurface(), "Segment " + segment.getName() + " Left Border");
			checkSurface(segment.getLeft().getSideSurface(), "Segment " + segment.getName() + " Left Side");
			checkSurface(segment.getLeft().getBarrierSurface(), "Segment " + segment.getName() + " Left Barrier");
			checkSurface(segment.getRight().getBorderSurface(), "Segment " + segment.getName() + " Right Border");
			checkSurface(segment.getRight().getSideSurface(), "Segment " + segment.getName() + " Right Side");
			checkSurface(segment.getRight().getBarrierSurface(), "Segment " + segment.getName() + " Right Barrier");
		}
		checkSurface(trackData.getGraphic().getTerrainGeneration().getSurface(), "Terrain");
	}

	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			editorFrame.getProject().setCheckDialogX(this.getX());
			editorFrame.getProject().setCheckDialogY(this.getY());
			editorFrame.getProject().setCheckDialogWidth(this.getWidth());
			editorFrame.getProject().setCheckDialogHeight(this.getHeight());
		}
	}
}
