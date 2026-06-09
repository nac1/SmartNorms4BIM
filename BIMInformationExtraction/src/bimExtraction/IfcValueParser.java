package bimExtraction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IfcValueParser {

    private static final Pattern PATTERN =
            Pattern.compile("^(\\w+)\\((.*)\\)$");

    public static IfcPropertyValue parse(String raw) {

        Matcher matcher = PATTERN.matcher(raw);
        
        String ifcType;
        String value ;

        if (matcher.matches()) {
        	ifcType = matcher.group(1);
        	value = matcher.group(2);
            
        }else {
           	ifcType = "NOTIFC";
        	value =  raw;
        	
        }

        switch (ifcType) {

            case "IFCINTEGER":
                return new IfcPropertyValue(
                        ifcType,
                        Integer.parseInt(value),
                        IfcJavaType.INTEGER);

            case "IFCREAL":
            case "IFCLENGTHMEASURE":
            case "IFCAREAMEASURE":
            case "IFCVOLUMEMEASURE":
            case "IFCPOSITIVELENGTHMEASURE":
            case "IFCMASSMEASURE":
                return new IfcPropertyValue(
                        ifcType,
                        Double.parseDouble(value),
                        IfcJavaType.DOUBLE);

            case "IFCBOOLEAN":

                return new IfcPropertyValue(
                        ifcType,
                        ".T.".equals(value),
                        IfcJavaType.BOOLEAN);

            default:

                return new IfcPropertyValue(
                        ifcType,
                        value,
                        IfcJavaType.STRING);
        }
    }
}