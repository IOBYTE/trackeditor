/*
 *   Project.java
 *   Created on 1 ??? 2005
 *
 *    The Project.java is part of TrackEditor-0.6.0.
 *
 *    TrackEditor-0.6.0 is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    TrackEditor-0.6.0 is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with TrackEditor-0.6.0; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package utils;

/**
 * @author Charalampos Alexopoulos
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Project
{
	private int frameX = 0;
	private int frameY = 0;
	private int segmentEditorX = 0;
	private int segmentEditorY = 0;
	private int propertiesEditorX = 0;
	private int propertiesEditorY = 0;
	private int propertiesEditorTab = 0;
	private int defaultSurfacesDialogX = 0;
	private int defaultSurfacesDialogY = 0;
	private int defaultObjectsDialogX = 0;
	private int defaultObjectsDialogY = 0;
	private int trackgenDialogX = 0;
	private int trackgenDialogY = 0;
	private int preferencesDialogX = 0;
	private int preferencesDialogY = 0;
	private int newProjectDialogX = 0;
	private int newProjectDialogY = 0;
	private int checkDialogX = 0;
	private int checkDialogY = 0;
	private int checkDialogWidth = 0;
	private int checkDialogHeight = 0;
	private int propertiesEditorSurfaceTab = 0;
	private int propertiesEditorObjectTab = 0;
	private int propertiesEditorCameraTab = 0;
	private int propertiesEditorLightTab = 0;
	private int propertiesEditorTerrainTab = 0;
	
	public Project()
	{
		
	}

	/**
	 * @return Returns the frameX.
	 */
	public int getFrameX()
	{
		return frameX;
	}
	/**
	 * @param frameX The frameX to set.
	 */
	public void setFrameX(int frameX)
	{
		this.frameX = frameX;
	}
	/**
	 * @return Returns the frameY.
	 */
	public int getFrameY()
	{
		return frameY;
	}
	/**
	 * @param frameY The frameY to set.
	 */
	public void setFrameY(int frameY)
	{
		this.frameY = frameY;
	}
	/**
	 * @return Returns the segmentEditorX.
	 */
	public int getSegmentEditorX()
	{
		return segmentEditorX;
	}
	/**
	 * @param segmentEditorX The segmentEditorX to set.
	 */
	public void setSegmentEditorX(int segmentEditorX)
	{
		this.segmentEditorX = segmentEditorX;
	}
	/**
	 * @return Returns the segmentEditorY.
	 */
	public int getSegmentEditorY()
	{
		return segmentEditorY;
	}
	/**
	 * @param segmentEditorY The segmentEditorY to set.
	 */
	public void setSegmentEditorY(int segmentEditorY)
	{
		this.segmentEditorY = segmentEditorY;
	}

	public int getPropertiesEditorX()
	{
		return propertiesEditorX;
	}

	public void setPropertiesEditorX(int propertiesEditorX)
	{
		this.propertiesEditorX = propertiesEditorX;
	}

	public int getPropertiesEditorY()
	{
		return propertiesEditorY;
	}

	public void setPropertiesEditorY(int propertiesEditorY)
	{
		this.propertiesEditorY = propertiesEditorY;
	}

	public int getDefaultSurfacesDialogX()
	{
		return defaultSurfacesDialogX;
	}

	public void setDefaultSurfacesDialogX(int defaultSurfacesDialogX)
	{
		this.defaultSurfacesDialogX = defaultSurfacesDialogX;
	}

	public int getDefaultSurfacesDialogY()
	{
		return defaultSurfacesDialogY;
	}

	public void setDefaultSurfacesDialogY(int defaultSurfacesDialogY)
	{
		this.defaultSurfacesDialogY = defaultSurfacesDialogY;
	}

	public int getDefaultObjectsDialogX()
	{
		return defaultObjectsDialogX;
	}

	public void setDefaultObjectsDialogX(int defaultObjectsDialogX)
	{
		this.defaultObjectsDialogX = defaultObjectsDialogX;
	}

	public int getDefaultObjectsDialogY()
	{
		return defaultObjectsDialogY;
	}

	public void setDefaultObjectsDialogY(int defaultObjectsDialogY)
	{
		this.defaultObjectsDialogY = defaultObjectsDialogY;
	}

	public int getTrackgenDialogX()
	{
		return trackgenDialogX;
	}

	public void setTrackgenDialogX(int trackgenDialogX)
	{
		this.trackgenDialogX = trackgenDialogX;
	}

	public int getTrackgenDialogY()
	{
		return trackgenDialogY;
	}

	public void setTrackgenDialogY(int trackgenDialogY)
	{
		this.trackgenDialogY = trackgenDialogY;
	}

	public int getPreferencesDialogX() 
	{
		return preferencesDialogX;
	}

	public void setPreferencesDialogX(int preferencesDialogX) 
	{
		this.preferencesDialogX = preferencesDialogX;
	}

	public int getPreferencesDialogY() 
	{
		return preferencesDialogY;
	}

	public void setPreferencesDialogY(int preferencesDialogY) 
	{
		this.preferencesDialogY = preferencesDialogY;
	}

	public int getNewProjectDialogX() 
	{
		return newProjectDialogX;
	}

	public void setNewProjectDialogX(int newProjectDialogX) 
	{
		this.newProjectDialogX = newProjectDialogX;
	}

	public int getNewProjectDialogY() 
	{
		return newProjectDialogY;
	}

	public void setNewProjectDialogY(int newProjectDialogY) 
	{
		this.newProjectDialogY = newProjectDialogY;
	}

	public int getPropertiesEditorTab()
	{
		return propertiesEditorTab;
	}

	public void setPropertiesEditorTab(int propertiesEditorTab)
	{
		this.propertiesEditorTab = propertiesEditorTab;
	}

	public int getCheckDialogX()
	{
		return checkDialogX;
	}

	public void setCheckDialogX(int checkDialogX)
	{
		this.checkDialogX = checkDialogX;
	}

	public int getCheckDialogY()
	{
		return checkDialogY;
	}

	public void setCheckDialogY(int checkDialogY)
	{
		this.checkDialogY = checkDialogY;
	}

	public int getCheckDialogWidth()
	{
		return checkDialogWidth;
	}

	public void setCheckDialogWidth(int checkDialogWidth)
	{
		this.checkDialogWidth = checkDialogWidth;
	}

	public int getCheckDialogHeight()
	{
		return checkDialogHeight;
	}

	public void setCheckDialogHeight(int checkDialogHeight)
	{
		this.checkDialogHeight = checkDialogHeight;
	}

	public int getPropertiesEditorSurfaceTab()
	{
		return propertiesEditorSurfaceTab;
	}

	public void setPropertiesEditorSurfaceTab(int propertiesEditorSurfaceTab)
	{
		this.propertiesEditorSurfaceTab = propertiesEditorSurfaceTab;
	}

	public int getPropertiesEditorObjectTab()
	{
		return propertiesEditorObjectTab;
	}

	public void setPropertiesEditorObjectTab(int propertiesEditorObjectTab)
	{
		this.propertiesEditorObjectTab = propertiesEditorObjectTab;
	}

	public int getPropertiesEditorCameraTab()
	{
		return propertiesEditorCameraTab;
	}

	public void setPropertiesEditorCameraTab(int propertiesEditorCameraTab)
	{
		this.propertiesEditorCameraTab = propertiesEditorCameraTab;
	}

	public int getPropertiesEditorLightTab()
	{
		return propertiesEditorLightTab;
	}

	public void setPropertiesEditorLightTab(int propertiesEditorLightTab)
	{
		this.propertiesEditorLightTab = propertiesEditorLightTab;
	}

	public int getPropertiesEditorTerrainTab()
	{
		return propertiesEditorTerrainTab;
	}

	public void setPropertiesEditorTerrainTab(int propertiesEditorTerrainTab)
	{
		this.propertiesEditorTerrainTab = propertiesEditorTerrainTab;
	}
}
