package utils.circuit;

import java.util.Vector;

public class Graphic {
    private String 						description				= null;

    private String 						separateObjects			= null;
    private String 						terrainObjects			= null;
    private String 						roadObjects				= null;
    private String 						road2Objects			= null;
    private String 						treesObjects			= null;
    private String 						buildingObjects			= null;
    private String 						houseObjects			= null;
    private String 						carsObjects				= null;
    private String 						truckObjects			= null;
    private String 						tribunesObjects			= null;
    private String 						pubObjects				= null;
    private String 						decorsObjects			= null;
    private String 						waterObjects			= null;
    private String 						grassObjects			= null;
    private String 						sandObjects				= null;
    private String 						rockObjects				= null;
    private String 						curbObjects				= null;
    private String 						fenceObjects			= null;
    private String 						barrierObjects			= null;
    private String 						wallObjects				= null;
    private String 						environmentObjects		= null;

    private String 						backgroundImage			= null;
    private int							backgroundType			= Integer.MAX_VALUE;
    private double 						backgroundColorR		= Double.NaN;
    private double 						backgroundColorG		= Double.NaN;
    private double 						backgroundColorB		= Double.NaN;
    private double 						ambientColorR			= Double.NaN;
    private double 						ambientColorG			= Double.NaN;
    private double 						ambientColorB			= Double.NaN;
    private double 						diffuseColorR			= Double.NaN;
    private double 						diffuseColorG			= Double.NaN;
    private double 						diffuseColorB			= Double.NaN;
    private double 						specularColorR			= Double.NaN;
    private double 						specularColorG			= Double.NaN;
    private double 						specularColorB			= Double.NaN;
    private double 						lightPositionX			= Double.NaN;
    private double 						lightPositionY			= Double.NaN;
    private double						lightPositionZ			= Double.NaN;
    private double						shininess				= Double.NaN;
    private double						fovFactor				= Double.NaN;
    private TurnMarks					turnMarks				= new TurnMarks();
    private TerrainGeneration			terrainGeneration		= new TerrainGeneration();
    private Vector<EnvironmentMapping>	environmentMapping		= new Vector<EnvironmentMapping>();
    
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSeparateObjects() {
		return separateObjects;
	}
	public void setSeparateObjects(String separateObjects) {
		this.separateObjects = separateObjects;
	}
	public String getTerrainObjects() {
		return terrainObjects;
	}
	public void setTerrainObjects(String terrainObjects) {
		this.terrainObjects = terrainObjects;
	}
	public String getRoadObjects() {
		return roadObjects;
	}
	public void setRoadObjects(String roadObjects) {
		this.roadObjects = roadObjects;
	}
	public String getRoad2Objects() {
		return road2Objects;
	}
	public void setRoad2Objects(String road2Objects) {
		this.road2Objects = road2Objects;
	}
	public String getTreesObjects() {
		return treesObjects;
	}
	public void setTreesObjects(String treesObjects) {
		this.treesObjects = treesObjects;
	}
	public String getBuildingObjects() {
		return buildingObjects;
	}
	public void setBuildingObjects(String buildingObjects) {
		this.buildingObjects = buildingObjects;
	}
	public String getHouseObjects() {
		return houseObjects;
	}
	public void setHouseObjects(String houseObjects) {
		this.houseObjects = houseObjects;
	}
	public String getCarsObjects() {
		return carsObjects;
	}
	public void setCarsObjects(String carsObjects) {
		this.carsObjects = carsObjects;
	}
	public String getTruckObjects() {
		return truckObjects;
	}
	public void setTruckObjects(String truckObjects) {
		this.truckObjects = truckObjects;
	}
	public String getTribunesObjects() {
		return tribunesObjects;
	}
	public void setTribunesObjects(String tribunesObjects) {
		this.tribunesObjects = tribunesObjects;
	}
	public String getPubObjects() {
		return pubObjects;
	}
	public void setPubObjects(String pubObjects) {
		this.pubObjects = pubObjects;
	}
	public String getDecorsObjects() {
		return decorsObjects;
	}
	public void setDecorsObjects(String decorsObjects) {
		this.decorsObjects = decorsObjects;
	}
	public String getWaterObjects() {
		return waterObjects;
	}
	public void setWaterObjects(String waterObjects) {
		this.waterObjects = waterObjects;
	}
	public String getGrassObjects() {
		return grassObjects;
	}
	public void setGrassObjects(String grassObjects) {
		this.grassObjects = grassObjects;
	}
	public String getSandObjects() {
		return sandObjects;
	}
	public void setSandObjects(String sandObjects) {
		this.sandObjects = sandObjects;
	}
	public String getRockObjects() {
		return rockObjects;
	}
	public void setRockObjects(String rockObjects) {
		this.rockObjects = rockObjects;
	}
	public String getCurbObjects() {
		return curbObjects;
	}
	public void setCurbObjects(String curbObjects) {
		this.curbObjects = curbObjects;
	}
	public String getFenceObjects() {
		return fenceObjects;
	}
	public void setFenceObjects(String fenceObjects) {
		this.fenceObjects = fenceObjects;
	}
	public String getBarrierObjects() {
		return barrierObjects;
	}
	public void setBarrierObjects(String barrierObjects) {
		this.barrierObjects = barrierObjects;
	}
	public String getWallObjects() {
		return wallObjects;
	}
	public void setWallObjects(String wallObjects) {
		this.wallObjects = wallObjects;
	}
	public String getEnvironmentObjects() {
		return environmentObjects;
	}
	public void setEnvironmentObjects(String environmentObjects) {
		this.environmentObjects = environmentObjects;
	}
	public String getBackgroundImage() {
		return backgroundImage;
	}
	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}
	public int getBackgroundType() {
		return backgroundType;
	}
	public void setBackgroundType(int backgroundType) {
		this.backgroundType = backgroundType;
	}
	public double getBackgroundColorR() {
		return backgroundColorR;
	}
	public void setBackgroundColorR(double backgroundColorR) {
		this.backgroundColorR = backgroundColorR;
	}
	public double getBackgroundColorG() {
		return backgroundColorG;
	}
	public void setBackgroundColorG(double backgroundColorG) {
		this.backgroundColorG = backgroundColorG;
	}
	public double getBackgroundColorB() {
		return backgroundColorB;
	}
	public void setBackgroundColorB(double backgroundColorB) {
		this.backgroundColorB = backgroundColorB;
	}
	public double getAmbientColorR() {
		return ambientColorR;
	}
	public void setAmbientColorR(double ambientColorR) {
		this.ambientColorR = ambientColorR;
	}
	public double getAmbientColorG() {
		return ambientColorG;
	}
	public void setAmbientColorG(double ambientColorG) {
		this.ambientColorG = ambientColorG;
	}
	public double getAmbientColorB() {
		return ambientColorB;
	}
	public void setAmbientColorB(double ambientColorB) {
		this.ambientColorB = ambientColorB;
	}
	public double getDiffuseColorR() {
		return diffuseColorR;
	}
	public void setDiffuseColorR(double diffuseColorR) {
		this.diffuseColorR = diffuseColorR;
	}
	public double getDiffuseColorG() {
		return diffuseColorG;
	}
	public void setDiffuseColorG(double diffuseColorG) {
		this.diffuseColorG = diffuseColorG;
	}
	public double getDiffuseColorB() {
		return diffuseColorB;
	}
	public void setDiffuseColorB(double diffuseColorB) {
		this.diffuseColorB = diffuseColorB;
	}
	public double getSpecularColorR() {
		return specularColorR;
	}
	public void setSpecularColorR(double specularColorR) {
		this.specularColorR = specularColorR;
	}
	public double getSpecularColorG() {
		return specularColorG;
	}
	public void setSpecularColorG(double specularColorG) {
		this.specularColorG = specularColorG;
	}
	public double getSpecularColorB() {
		return specularColorB;
	}
	public void setSpecularColorB(double specularColorB) {
		this.specularColorB = specularColorB;
	}
	public double getLightPositionX() {
		return lightPositionX;
	}
	public void setLightPositionX(double lightPositionX) {
		this.lightPositionX = lightPositionX;
	}
	public double getLightPositionY() {
		return lightPositionY;
	}
	public void setLightPositionY(double lightPositionY) {
		this.lightPositionY = lightPositionY;
	}
	public double getLightPositionZ() {
		return lightPositionZ;
	}
	public void setLightPositionZ(double lightPositionZ) {
		this.lightPositionZ = lightPositionZ;
	}
	public TurnMarks getTurnMarks() {
		return turnMarks;
	}
	public void setTurnMarks(TurnMarks turnMarks) {
		this.turnMarks = turnMarks;
	}
	public TerrainGeneration getTerrainGeneration() {
		return terrainGeneration;
	}
	public void setTerrainGeneration(TerrainGeneration terrainGeneration) {
		this.terrainGeneration = terrainGeneration;
	}
	public Vector<EnvironmentMapping> getEnvironmentMapping() {
		return environmentMapping;
	}
	public void setEnvironmentMapping(Vector<EnvironmentMapping> environmentMapping) {
		this.environmentMapping = environmentMapping;
	}
    public double getShininess() {
		return shininess;
	}
	public void setShininess(double shininess) {
		this.shininess = shininess;
	}
	public double getFovFactor() {
		return fovFactor;
	}
	public void setFovFactor(double fovFactor) {
		this.fovFactor = fovFactor;
	}

	public void dump(String indent)
    {
		System.out.println(indent + "Graphic");
		System.out.println(indent + "  description          : " + description);
		System.out.println(indent + "  separateObjects      : " + separateObjects);
		System.out.println(indent + "  terrainObjects       : " + terrainObjects);
		System.out.println(indent + "  roadObjects          : " + roadObjects);
		System.out.println(indent + "  road2Objects         : " + road2Objects);
		System.out.println(indent + "  treesObjects         : " + treesObjects);
		System.out.println(indent + "  buildingObjects      : " + buildingObjects);
		System.out.println(indent + "  houseObjects         : " + houseObjects);
		System.out.println(indent + "  carsObjects          : " + carsObjects);
		System.out.println(indent + "  truckObjects         : " + truckObjects);
		System.out.println(indent + "  tribunesObjects      : " + tribunesObjects);
		System.out.println(indent + "  pubObjects           : " + pubObjects);
		System.out.println(indent + "  decorsObjects        : " + decorsObjects);
		System.out.println(indent + "  waterObjects         : " + waterObjects);
		System.out.println(indent + "  grassObjects         : " + grassObjects);
		System.out.println(indent + "  sandObjects          : " + sandObjects);
		System.out.println(indent + "  rockObjects          : " + rockObjects);
		System.out.println(indent + "  curbObjects          : " + curbObjects);
		System.out.println(indent + "  fenceObjects         : " + fenceObjects);
		System.out.println(indent + "  barrierObjects       : " + barrierObjects);
		System.out.println(indent + "  wallObjects          : " + wallObjects);
		System.out.println(indent + "  environmentObjects   : " + environmentObjects);
		System.out.println(indent + "  backgroundImage      : " + backgroundImage);
		System.out.println(indent + "  backgroundType       : " + backgroundType);
		System.out.println(indent + "  backgroundColorR     : " + backgroundColorR);
		System.out.println(indent + "  backgroundColorG     : " + backgroundColorG);
		System.out.println(indent + "  backgroundColorB     : " + backgroundColorB);
		System.out.println(indent + "  ambientColorR        : " + ambientColorR);
		System.out.println(indent + "  ambientColorG        : " + ambientColorG);
		System.out.println(indent + "  ambientColorB        : " + ambientColorB);
		System.out.println(indent + "  diffuseColorR        : " + diffuseColorR);
		System.out.println(indent + "  diffuseColorG        : " + diffuseColorG);
		System.out.println(indent + "  diffuseColorB        : " + diffuseColorB);
		System.out.println(indent + "  specularColorR       : " + specularColorR);
		System.out.println(indent + "  specularColorG       : " + specularColorG);
		System.out.println(indent + "  specularColorB       : " + specularColorB);
		System.out.println(indent + "  lightPositionX       : " + lightPositionX);
		System.out.println(indent + "  lightPositionY       : " + lightPositionY);
		System.out.println(indent + "  lightPositionZ       : " + lightPositionZ);
		System.out.println(indent + "  shininess            : " + shininess);
		System.out.println(indent + "  fovFactor            : " + fovFactor);
		System.out.println(indent + "  turnMarks            : ");
		turnMarks.dump(indent + "    ");
		System.out.println(indent + "  terrainGeneration    : ");
		terrainGeneration.dump(indent + "    ");
		System.out.println(indent + "  environmentMapping[" + environmentMapping.size() + "]");
		for (int i = 0; i < environmentMapping.size(); i++)
		{
			environmentMapping.get(i).dump(indent + "    ");
		}
    }
}
