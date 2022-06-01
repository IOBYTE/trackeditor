/*
 *   Properties.java
 *   Created on 27 ��� 2005
 *
 *    The Properties.java is part of TrackEditor-0.3.1.
 *
 *    TrackEditor-0.3.1 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.3.1 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.3.1; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package utils;

import java.awt.event.ActionListener;
import java.util.Vector;

import utils.circuit.SegmentSide;


/**
 * @author babis
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public  class Properties
{
	private static Properties	instance				= new Properties();
	private Vector				propertiesListeners		= new Vector();
	//private Vector				trackData;
	public final String			title					= "Track Editor";
	public final String			version					= "v0.7.0 alpha";
	private String				category				= "road";
	private int					trackVersion			= 4;
	private String				path;
	private String				trackName;
	private String				author;
	private String				description;

	private double				trackWidth				= 10;
	private String				surface					= "asphalt2-lines";
	
	private SegmentSide			left					= new SegmentSide();
	private SegmentSide			right					= new SegmentSide();

	private String				pitSide					= "right";
	private String				pitEntry;
	private String				pitStart;
	private String				pitStartBuildings		= null;
	private String				pitStopBuildings		= null;
	private int					pitMaxPits				= 0;
	private String				pitEnd;
	private String				pitExit;
	private double				pitWidth;
	private double				pitLength;
	private int					pitStyle				= 1;

	private double				imageScale				= 1;
	private String				image					= "";
	private EditorPoint			imgOffset				= new EditorPoint();
	private double				initx;
	private double				inity;
	
	private double				currentX				= 0;
	private double				currentY				= 0;
	private double				currentZ				= 0;
	private double				currentA				= 0;
	private double				currentBanking			= 0;

	private double				showArrows				= 0;
	private double				trackStartDist			= 0;
	private double				profileStepLength		= 4;
	private int					curveNameCount			= 0;
	private int					straightNameCount		= 0;

	private double				terrainTrackStep		= 20;
	private double				terrainBorderMargin		= 50;
	private double				terrainBorderStep		= 30;
	private double				terrainBorderHeight		= 15;
	private String				terrainOrientation		= "clockwise";

	private double				turnMarksWidth			= 1;
	private double				turnMarksHeight			= 1;
	private double				turnMarksVerticalSpace	= 0;
	private double				turnMarksHorizontalSpace= 2;


	/**
	 *  
	 */
	public Properties()
	{
//		System.out.println("Properties constructor");
	}

	public static Properties getInstance()
	{
		return instance;
	}

	/**
	 * @param instance The instance to set.
	 */
	public static void setInstance(Properties instance)
	{
		Properties.instance = instance;
	}
//	/**
//	 * @return Returns the trackData.
//	 */
//	public Vector getTrackData()
//	{
//		return trackData;
//	}
//	/**
//	 * @param trackData The trackData to set.
//	 */
//	public void setTrackData(Vector trackData)
//	{
//		this.trackData = trackData;
//		this.valueChanged();
//	}
	/**
	 * @return Returns the author.
	 */
	public String getAuthor()
	{
		return author;
	}
	/**
	 * @param author
	 *            The author to set.
	 */
	public void setAuthor(String author)
	{
		this.author = author;
	}
	/**
	 * @return Returns the category.
	 */
	public String getCategory()
	{
		return category;
	}
	/**
	 * @param category
	 *            The category to set.
	 */
	public void setCategory(String category)
	{
		this.category = category;
	}
	/**
	 * @return Returns the track version.
	 */
	public int getTrackVersion()
	{
		return trackVersion;
	}
	/**
	 * @param trackVersion
	 *            The track version to set.
	 */
	public void setTrackVersion(int trackVersion)
	{
		this.trackVersion = trackVersion;
	}
	/**
	 * @return Returns the description.
	 */
	public String getDescription()
	{
		return description;
	}
	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	/**
	 * @return Returns the path.
	 */
	public String getPath()
	{
		return path;
	}
	/**
	 * @param path
	 *            The path to set.
	 */
	public void setPath(String path)
	{
		this.path = path;
	}
	/**
	 * @return Returns the trackName.
	 */
	public String getTrackName()
	{
		return trackName;
	}
	/**
	 * @param trackName
	 *            The trackName to set.
	 */
	public void setTrackName(String trackName)
	{
		this.trackName = trackName;
	}
	/**
	 * @return Returns the image.
	 */
	public String getImage()
	{
		return image;
	}
	/**
	 * @param image
	 *            The image to set.
	 */
	public void setImage(String image)
	{
		this.image = image;
	}
	/**
	 * @return Returns the imageScale.
	 */
	public double getImageScale()
	{
		return imageScale;
	}
	/**
	 * @param imageScale
	 *            The imageScale to set.
	 */
	public void setImageScale(double imageScale)
	{
		this.imageScale = imageScale;
	}
	/**
	 * @return Returns the initx.
	 */
	public double getInitx()
	{
		return initx;
	}
	/**
	 * @param initx
	 *            The initx to set.
	 */
	public void setInitx(double initx)
	{
		this.initx = initx;
	}
	/**
	 * @return Returns the inity.
	 */
	public double getInity()
	{
		return inity;
	}
	/**
	 * @param inity
	 *            The inity to set.
	 */
	public void setInity(double inity)
	{
		this.inity = inity;
	}

	/**
	 * @return Returns the pitLength.
	 */
	public double getPitLength()
	{
		return pitLength;
	}
	/**
	 * @param pitLength
	 *            The pitLength to set.
	 */
	public void setPitLength(double pitLength)
	{
		this.pitLength = pitLength;
	}
	/**
	 * @return Returns the pitSide.
	 */
	public String getPitSide()
	{
		return pitSide;
	}
	/**
	 * @param pitSide
	 *            The pitSide to set.
	 */
	public void setPitSide(String pitSide)
	{
		this.pitSide = pitSide;
	}

	/**
	 * @return Returns the pitWidth.
	 */
	public double getPitWidth()
	{
		return pitWidth;
	}
	/**
	 * @param pitWidth
	 *            The pitWidth to set.
	 */
	public void setPitWidth(double pitWidth)
	{
		this.pitWidth = pitWidth;
	}
	/**
	 * @return Returns the pitStyle.
	 */
	public int getPitStyle()
	{
		return pitStyle;
	}
	/**
	 * @param pitStyle
	 *            The pitStyle to set.
	 */
	public void setPitStyle(int pitStyle)
	{
		this.pitStyle = pitStyle;
	}
	/**
	 * @return Returns the trackWidth.
	 */
	public double getTrackWidth()
	{
		return trackWidth;
	}
	/**
	 * @param trackWidth
	 *            The trackWidth to set.
	 */
	public void setTrackWidth(double trackWidth)
	{
		this.trackWidth = trackWidth;
	}
	/**
	 * @return Returns the surface.
	 */
	public String getSurface()
	{
		return surface;
	}
	/**
	 * @param surface The surface to set.
	 */
	public void setSurface(String surface)
	{
		this.surface = surface;
	}
	/**
	 * @return Returns the pitEnd.
	 */
	public String getPitEnd()
	{
		return pitEnd;
	}
	/**
	 * @param pitEnd
	 *            The pitEnd to set.
	 */
	public void setPitEnd(String pitEnd)
	{
		this.pitEnd = pitEnd;
	}
	/**
	 * @return Returns the pitEntry.
	 */
	public String getPitEntry()
	{
		return pitEntry;
	}
	/**
	 * @param pitEntry
	 *            The pitEntry to set.
	 */
	public void setPitEntry(String pitEntry)
	{
		this.pitEntry = pitEntry;
	}
	/**
	 * @return Returns the pitExit.
	 */
	public String getPitExit()
	{
		return pitExit;
	}
	/**
	 * @param pitExit
	 *            The pitExit to set.
	 */
	public void setPitExit(String pitExit)
	{
		this.pitExit = pitExit;
	}
	/**
	 * @return Returns the pitStart.
	 */
	public String getPitStart()
	{
		return pitStart;
	}
	/**
	 * @param pitStart
	 *            The pitStart to set.
	 */
	public void setPitStart(String pitStart)
	{
		this.pitStart = pitStart;
	}
	/**
	 * @return Returns the pitStartBuildings.
	 */
	public String getPitStartBuildings()
	{
		return pitStartBuildings;
	}
	/**
	 * @param pitStartBuildings
	 *            The pitStartBuildings to set.
	 */
	public void setPitStartBuildings(String pitStartBuildings)
	{
		this.pitStartBuildings = pitStartBuildings;
	}
	/**
	 * @return Returns the pitStopBuildings.
	 */
	public String getPitStopBuildings()
	{
		return pitStopBuildings;
	}
	/**
	 * @param pitStopBuildings
	 *            The pitStopBuildings to set.
	 */
	public void setPitStopBuildings(String pitStopBuildings)
	{
		this.pitStopBuildings = pitStopBuildings;
	}
	/**
	 * @return Returns the pitMaxPits.
	 */
	public int getPitMaxPits()
	{
		return pitMaxPits;
	}
	/**
	 * @param pitMaxPits
	 *            The pitMaxPits to set.
	 */
	public void setPitMaxPits(int pitMaxPits)
	{
		this.pitMaxPits = pitMaxPits;
	}
	/**
	 * @return Returns the terrainTrackStep.
	 */
	public double getTerrainTrackStep()
	{
		return terrainTrackStep;
	}
	/**
	 * @param terrainTrackStep
	 *            The terrainTrackStep to set.
	 */
	public void setTerrainTrackStep(double terrainTrackStep)
	{
		this.terrainTrackStep = terrainTrackStep;
	}
	/**
	 * @return Returns the terrainBorderMargin.
	 */
	public double getTerrainBorderMargin()
	{
		return terrainBorderMargin;
	}
	/**
	 * @param terrainBorderMargin
	 *            The terrainBorderMargin to set.
	 */
	public void setTerrainBorderMargin(double terrainBorderMargin)
	{
		this.terrainBorderMargin = terrainBorderMargin;
	}
	/**
	 * @return Returns the terrainBorderStep.
	 */
	public double getTerrainBorderStep()
	{
		return terrainBorderStep;
	}
	/**
	 * @param terrainBorderStep
	 *            The pitMaxPits to set.
	 */
	public void setTerrainBorderStep(double terrainBorderStep)
	{
		this.terrainBorderStep = terrainBorderStep;
	}
	/**
	 * @return Returns the terrainBorderHeight.
	 */
	public double getTerrainBorderHeight()
	{
		return terrainBorderHeight;
	}
	/**
	 * @param terrainBorderHeight
	 *            The terrainBorderHeight to set.
	 */
	public void setTerrainBorderHeight(double terrainBorderHeight)
	{
		this.terrainBorderHeight = terrainBorderHeight;
	}
	/**
	 * @return Returns the terrainOrientation.
	 */
	public String getTerrainOrientation()
	{
		return terrainOrientation;
	}
	/**
	 * @param terrainOrientation
	 *            The terrainOrientation to set.
	 */
	public void setTerrainOrientation(String terrainOrientation)
	{
		this.terrainOrientation = terrainOrientation;
	}
	/**
	 * @return Returns the turnMarksWidth.
	 */
	public double getTurnMarksWidth()
	{
		return turnMarksWidth;
	}
	/**
	 * @param turnMarksWidth
	 *            The turnMarksWidth to set.
	 */
	public void setTurnMarksWidth(double turnMarksWidth)
	{
		this.turnMarksWidth = turnMarksWidth;
	}
	/**
	 * @return Returns the turnMarksHeight.
	 */
	public double getTurnMarksHeight()
	{
		return turnMarksHeight;
	}
	/**
	 * @param turnMarksHeight
	 *            The turnMarksHeight to set.
	 */
	public void setTurnMarksHeight(double turnMarksHeight)
	{
		this.turnMarksHeight = turnMarksHeight;
	}
	/**
	 * @return Returns the turnMarksVerticalSpace.
	 */
	public double getTurnMarksVerticalSpace()
	{
		return turnMarksVerticalSpace;
	}
	/**
	 * @param turnMarksVerticalSpace
	 *            The turnMarksVerticalSpace to set.
	 */
	public void setTurnMarksVerticalSpace(double turnMarksVerticalSpace)
	{
		this.turnMarksVerticalSpace = turnMarksVerticalSpace;
	}
	/**
	 * @return Returns the turnMarksHorizontalSpace.
	 */
	public double getTurnMarksHorizontalSpace()
	{
		return turnMarksHorizontalSpace;
	}
	/**
	 * @param turnMarksHorizontalSpace
	 *            The turnMarksHorizontalSpace to set.
	 */
	public void setTurnMarksHorizontalSpace(double turnMarksHorizontalSpace)
	{
		this.turnMarksHorizontalSpace = turnMarksHorizontalSpace;
	}

	public synchronized void removePropertiesListener(ActionListener l)
	{

	}

	public synchronized void addPropertiesListener(ActionListener l)
	{
		Vector v = propertiesListeners == null ? new Vector(2) : (Vector) propertiesListeners.clone();
		if (!v.contains(l))
		{
			v.addElement(l);
			propertiesListeners = v;
		}
	}

	public void valueChanged()
	{
		if (propertiesListeners != null)
		{
			Vector listeners = propertiesListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++)
			{
				((ActionListener) listeners.elementAt(i)).actionPerformed(null);
			}
		}
	}
	/**
	 * @return Returns the currentA.
	 */
	public double getCurrentA()
	{
		return currentA;
	}
	/**
	 * @param currentA The currentA to set.
	 */
	public void setCurrentA(double currentA)
	{
		this.currentA = currentA;
	}
	/**
	 * @return Returns the currentBanking.
	 */
	public double getCurrentBanking()
	{
		return currentBanking;
	}
	/**
	 * @param currentBanking The currentBanking to set.
	 */
	public void setCurrentBanking(double currentBanking)
	{
		this.currentBanking = currentBanking;
	}
	/**
	 * @return Returns the currentX.
	 */
	public double getCurrentX()
	{
		return currentX;
	}
	/**
	 * @param currentX The currentX to set.
	 */
	public void setCurrentX(double currentX)
	{
		this.currentX = currentX;
	}
	/**
	 * @return Returns the currentY.
	 */
	public double getCurrentY()
	{
		return currentY;
	}
	/**
	 * @param currentY The currentY to set.
	 */
	public void setCurrentY(double currentY)
	{
		this.currentY = currentY;
	}
	/**
	 * @return Returns the currentZ.
	 */
	public double getCurrentZ()
	{
		return currentZ;
	}
	/**
	 * @param currentZ The currentZ to set.
	 */
	public void setCurrentZ(double currentZ)
	{
		this.currentZ = currentZ;
	}
	/**
	 * @return Returns the profileStepLength.
	 */
	public double getProfileStepLength()
	{
		return profileStepLength;
	}
	/**
	 * @param profileStepLength The profileStepLength to set.
	 */
	public void setProfileStepLength(double profileStepLength)
	{
		this.profileStepLength = profileStepLength;
	}
	/**
	 * @return Returns the showArrows.
	 */
	public double getShowArrows()
	{
		return showArrows;
	}
	/**
	 * @param showArrows The showArrows to set.
	 */
	public void setShowArrows(double showArrows)
	{
		this.showArrows = showArrows;
	}
	/**
	 * @return Returns the trackStartDist.
	 */
	public double getTrackStartDist()
	{
		return trackStartDist;
	}
	/**
	 * @param trackStartDist The trackStartDist to set.
	 */
	public void setTrackStartDist(double trackStartDist)
	{
		this.trackStartDist = trackStartDist;
	}
	/**
	 * @return Returns the left.
	 */
	public SegmentSide getLeft()
	{
		return left;
	}
	/**
	 * @param left The left to set.
	 */
	public void setLeft(SegmentSide left)
	{
		this.left = left;
	}
	/**
	 * @return Returns the right.
	 */
	public SegmentSide getRight()
	{
		return right;
	}
	/**
	 * @param right The right to set.
	 */
	public void setRight(SegmentSide right)
	{
		this.right = right;
	}

 
    /**
     * @return Returns the imgOffset.
     */
    public EditorPoint getImgOffset()
    {
        return imgOffset;
    }
    /**
     * @param imgOffset The imgOffset to set.
     */
    public void setImgOffset(EditorPoint imgOffset)
    {
        this.imgOffset = imgOffset;
    }
    /**
     * @return Returns the curveNameCount.
     */
    public int getCurveNameCount()
    {
        return curveNameCount;
    }
    /**
     * @param curveNameCount The curveNameCount to set.
     */
    public void setCurveNameCount(int curveNameCount)
    {
        this.curveNameCount = curveNameCount;
    }
    /**
     * @return Returns the straightNameCount.
     */
    public int getStraightNameCount()
    {
        return straightNameCount;
    }
    /**
     * @param straightNameCount The straightNameCount to set.
     */
    public void setStraightNameCount(int straightNameCount)
    {
        this.straightNameCount = straightNameCount;
    }
}
