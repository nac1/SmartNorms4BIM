package bimExtraction;
import java.util.*;

/*
 * This class represents a BIM object with properties
 * 
 */
public class BimObject {
    private final int id;
    private final String ifcType;
    private String guid; 
    private int placeId; 
    private final Map<String, String> directProperties = new LinkedHashMap<>();
    private final Map<String, String> inheritedProperties = new LinkedHashMap<>();

    public BimObject(int id, String ifcType) {
        this.id = id;
        this.ifcType = ifcType;
    }

    public void setGuid(String guid) {
        this.guid = guid;
        this.addDirectProperty("GlobalId", guid);
    }
    
    public void setplaceId(int  placeId) {
        this.placeId = placeId;
     }
 
    public void addDirectProperty(String key, String value) {
        this.directProperties.put(key.replace("'", ""), value);
    }

    public void addInheritedProperty(String key, String value) {
        this.inheritedProperties.put(key.replace("'", ""), value);
    }

    public int getId() { return id; }
    public String getGuid() { return guid; }
    public int getPlaceId() {return placeId;}
    public String getIfcType() { return ifcType; }
    public Map<String, String> getDirectProperties() { return directProperties; }
    public Map<String, String> getInheritedProperties() { return inheritedProperties; }

    @Override
    public String toString() {
        return "Object #" + id 
        		+ " [GUID: " + (guid != null ? guid : "N/A")
        		+ " [PlaceID: " + (placeId != 0 ? placeId : "N/A") 
        		+ "] [" + ifcType + "]\n" +
                "   Direct:  " + directProperties + "\n" +
                "   Inherited: " + inheritedProperties;
    }
}
