package utils.circuit;

public final class Surface {
    private String name						= null;
    private double colorR1					= Double.NaN;
    private double colorG1					= Double.NaN;
    private double colorB1					= Double.NaN;
    private double colorR2					= Double.NaN;
    private double colorG2					= Double.NaN;
    private double colorB2					= Double.NaN;
    private String textureName				= null;
    private String textureType				= null;
    private double textureSize				= Double.NaN;
    private String textureLinkWithPrevious	= null;
    private String textureStartOnBoundary	= null;
    private double textureMipMap			= Double.NaN;	// TODO int
    private double friction					= Double.NaN;
    private double rollingResistance		= Double.NaN;
    private String bumpName					= null;
    private double bumpSize					= Double.NaN;
    private double roughness				= Double.NaN;
    private double roughnessWavelength		= Double.NaN;
    private String racelineName				= null;
    private double dammage					= Double.NaN;
    private double rebound					= Double.NaN;
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getColorR1() {
		return colorR1;
	}

	public void setColorR1(double colorR1) {
		this.colorR1 = colorR1;
	}

	public double getColorG1() {
		return colorG1;
	}

	public void setColorG1(double colorG1) {
		this.colorG1 = colorG1;
	}

	public double getColorB1() {
		return colorB1;
	}

	public void setColorB1(double colorB1) {
		this.colorB1 = colorB1;
	}

	public double getColorR2() {
		return colorR2;
	}

	public void setColorR2(double colorR2) {
		this.colorR2 = colorR2;
	}

	public double getColorG2() {
		return colorG2;
	}

	public void setColorG2(double colorG2) {
		this.colorG2 = colorG2;
	}

	public double getColorB2() {
		return colorB2;
	}

	public void setColorB2(double colorB2) {
		this.colorB2 = colorB2;
	}

	public String getTextureName() {
		return textureName;
	}

	public void setTextureName(String textureName) {
		this.textureName = textureName;
	}

	public String getTextureType() {
		return textureType;
	}

	public void setTextureType(String textureType) {
		this.textureType = textureType;
	}

	public double getTextureSize() {
		return textureSize;
	}

	public void setTextureSize(double textureSize) {
		this.textureSize = textureSize;
	}

	public String getTextureLinkWithPrevious() {
		return textureLinkWithPrevious;
	}

	public void setTextureLinkWithPrevious(String textureLinkWithPrevious) {
		this.textureLinkWithPrevious = textureLinkWithPrevious;
	}

	public String getTextureStartOnBoundary() {
		return textureStartOnBoundary;
	}

	public void setTextureStartOnBoundary(String textureStartOnBoundary) {
		this.textureStartOnBoundary = textureStartOnBoundary;
	}

	public double getTextureMipMap() {
		return textureMipMap;
	}

	public void setTextureMipMap(double textureMipMap) {
		this.textureMipMap = textureMipMap;
	}

	public double getFriction() {
		return friction;
	}

	public void setFriction(double friction) {
		this.friction = friction;
	}

	public double getRollingResistance() {
		return rollingResistance;
	}

	public void setRollingResistance(double rollingResistance) {
		this.rollingResistance = rollingResistance;
	}

	public String getBumpName() {
		return bumpName;
	}

	public void setBumpName(String bumpName) {
		this.bumpName = bumpName;
	}

	public double getBumpSize() {
		return bumpSize;
	}

	public void setBumpSize(double bumpSize) {
		this.bumpSize = bumpSize;
	}

	public double getRoughness() {
		return roughness;
	}

	public void setRoughness(double roughness) {
		this.roughness = roughness;
	}

	public double getRoughnessWavelength() {
		return roughnessWavelength;
	}

	public void setRoughnessWavelength(double roughnessWavelength) {
		this.roughnessWavelength = roughnessWavelength;
	}

	public String getRacelineName() {
		return racelineName;
	}

	public void setRacelineName(String racelineName) {
		this.racelineName = racelineName;
	}

	public double getDammage() {
		return dammage;
	}

	public void setDammage(double dammage) {
		this.dammage = dammage;
	}

	public double getRebound() {
		return rebound;
	}

	public void setRebound(double rebound) {
		this.rebound = rebound;
	}

    public void dump()
    {
        System.out.println("name                      : "+name);
        System.out.println("  colorR1                 : "+colorR1);
        System.out.println("  colorG1                 : "+colorG1);
        System.out.println("  colorB1                 : "+colorB1);
        System.out.println("  colorR2                 : "+colorR2);
        System.out.println("  colorG2                 : "+colorG2);
        System.out.println("  colorB2                 : "+colorB2);
        System.out.println("  textureName             : "+textureName);
        System.out.println("  textureSize             : "+textureSize);
        System.out.println("  textureType             : "+textureType);
        System.out.println("  textureLinkWithPrevious : "+textureLinkWithPrevious);
        System.out.println("  textureStartOnBoundary  : "+textureStartOnBoundary);
        System.out.println("  textureMipMap           : "+textureMipMap);
        System.out.println("  friction                : "+friction);
        System.out.println("  rollingResistance       : "+rollingResistance);
        System.out.println("  bumpName                : "+bumpName);
        System.out.println("  bumpSize                : "+bumpSize);
        System.out.println("  roughness               : "+roughness);
        System.out.println("  roughnessWavelength     : "+roughnessWavelength);
        System.out.println("  racelineName            : "+racelineName);
        System.out.println("  dammage                 : "+dammage);
        System.out.println("  rebound                 : "+rebound);
      }
}
