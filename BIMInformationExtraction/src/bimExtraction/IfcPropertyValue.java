package bimExtraction;

public class IfcPropertyValue {

    private final String ifcType;
    private final Object value;
    private final IfcJavaType javaType;

    public IfcPropertyValue(String ifcType, Object value, IfcJavaType javaType) {
        this.ifcType = ifcType;
        this.value = value;
        this.javaType = javaType;
    }

    public String getIfcType() {
        return ifcType;
    }

    public Object getValue() {
        return value;
    }

    public IfcJavaType getJavaType() {
        return javaType;
    }

    public Double asDouble() {
        return (Double) value;
    }

    public Integer asInteger() {
        return (Integer) value;
    }

    public Boolean asBoolean() {
        return (Boolean) value;
    }

    public String asString() {
        return (String) value;
    }
}