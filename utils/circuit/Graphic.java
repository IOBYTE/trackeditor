package utils.circuit;

import java.util.Vector;

public class Graphic {
    private String 						description				= null;
    private String 						descriptionNight		= null;
    private String 						descriptionRainNight	= null;
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
	public String getDescriptionNight() {
		return descriptionNight;
	}
	public void setDescriptionNight(String descriptionNight) {
		this.descriptionNight = descriptionNight;
	}
	public String getDescriptionRainNight() {
		return descriptionRainNight;
	}
	public void setDescriptionRainNight(String descriptionRaiNight) {
		this.descriptionRainNight = descriptionRaiNight;
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
		System.out.println(indent + "  descriptionNight     : " + descriptionNight);
		System.out.println(indent + "  descriptionRainNight : " + descriptionRainNight);
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
