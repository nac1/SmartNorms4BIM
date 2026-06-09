package bimExtraction;

import java.util.*;
import java.util.regex.*;

/*
 * This class represents a extraction of a given IFC class
 * 
 */

public class BimReferenceExtractor {

    public static Map<Integer, Integer> extractIdReference(Map<Integer, IfcRawRecord> model, String entityName, int paramIndex) {
        Map<Integer, Integer> results = new LinkedHashMap<>();

        for (IfcRawRecord record : model.values()) {
            if (record.getEntityName().equals(entityName)) {
                String param = record.getParam(paramIndex);
                
                Integer refId = parseSingleId(param);
                if (refId != null) {
                    results.put(record.getId(), refId);
                }
            }
        }
        return results;
    }

    private static Integer parseSingleId(String text) {
        if (text == null || text.equals("$")) return null;
        Matcher m = Pattern.compile("#(\\d+)").matcher(text);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return null;
    }
}