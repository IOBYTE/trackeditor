package utils.circuit;

public class ObjectMap {
	private String	name		= null;
	private String	objectMap	= null;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getObjectMap() {
		return objectMap;
	}

	public void setObjectMap(String objectMap) {
		this.objectMap = objectMap;
	}

	public void dump(String indent)
    {
		System.out.println(indent + "ObjectMap");
		System.out.println(indent + "  name      : " + name);
		System.out.println(indent + "  objectMap : " + objectMap);
    }	
}
