package utils.circuit;

public class EnvironmentMapping {
	private String name				= null;
	private String envMapImage		= null;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEnvMapImage() {
		return envMapImage;
	}
	public void setEnvMapImage(String envMapImage) {
		this.envMapImage = envMapImage;
	}

	public void dump(String indent)
    {
		System.out.println(indent + "EnvironmentMapping");
		System.out.println(indent + "  name        : " + name);
		System.out.println(indent + "  envMapImage : " + envMapImage);
    }
}
