package bimExtraction;

import java.util.List;
import java.util.ArrayList;


/*
 * This class manages every line of the IFC file
 * 
 */
public class IfcRawRecord {
     int id;
     String entityName;
     String rawParamsLine;
     List<String> params;

    public IfcRawRecord(int id, String entityName, String rawParamsLine) {
        this.id = id;
        this.entityName = entityName;
        this.rawParamsLine = rawParamsLine;
        this.params = parseParams(rawParamsLine);
    }


    private List<String> parseParams(String raw) {
        List<String> result = new ArrayList<>();
        if (raw == null || raw.isEmpty()) return result;

        StringBuilder sb = new StringBuilder();
        int depth = 0;
        boolean inString = false;

        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);

         
            if (c == '\'') {
                inString = !inString;
            }

          
            if (!inString) {
                if (c == '(') depth++;
                else if (c == ')') depth--;
            }

         
            if (c == ',' && depth == 0 && !inString) {
                result.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }

        result.add(sb.toString().trim());
        return result;
    }

    public int getId() { return id; }
    public String getEntityName() { return entityName; }
    public String getRawParamsLine() { return rawParamsLine; }
    public List<String> getParams() { return params; }


    public String getParam(int index) {
        if (index >= 0 && index < params.size()) {
            return params.get(index);
        }
        return null;
    }

    @Override
    public String toString() {
        return "#" + id + " = " + entityName + "(" + params.size() + " params)";
    }
}
