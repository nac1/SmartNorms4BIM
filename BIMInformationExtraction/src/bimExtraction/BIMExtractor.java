package bimExtraction;

import java.util.*;
import java.util.regex.*;

/*
 * This class extracts the Information from BIM model.
 * Reference : "Extracting Information from Building Information Models to Support Automated Value Analysis"
 * By: Lu Zhang and Nora El-Gohary and A.M.ASCE 
 * Year: 2016
 * URL: http://www.see.eng.osaka-u.ac.jp/seeit/icccbe2016/Proceedings/Full_Papers/067-268.pdf
 */

public class BIMExtractor {

    private Map<Integer, BimObject> allObjects = new HashMap<>();
    private Map<Integer, Integer> instanceToTypeMap = new HashMap<>();

    public Map<Integer, BimObject> performExtraction(Map<Integer, IfcRawRecord> model) {
        
        // 1. Extract all direct properties (of instances and Types)
        for (IfcRawRecord rel : model.values()) {
            if (rel.getEntityName().equals("IFCRELDEFINESBYPROPERTIES")) {
                processPropertyRelation(rel, model);
            }
        }

        //  2. Map inheritance relationships (Instance - Type)
        for (IfcRawRecord rel : model.values()) {
            if (rel.getEntityName().equals("IFCRELDEFINESBYTYPE")) {
                processTypeRelation(rel, model);
            }
        }

        // 3. Resolve and copy properties of the Type to the Instance
        resolveInheritance();

        return allObjects;
    }

    private void processPropertyRelation(IfcRawRecord rel, Map<Integer, IfcRawRecord> model) {
        // Related Objects (Instances or Types)
        List<Integer> targetIds = extractAllIds(rel.getParam(4)); 
        // Relating Property Definition
        List<Integer> psetIds = extractAllIds(rel.getParam(5));

        if (psetIds.isEmpty()) return;

        IfcRawRecord psetRecord = model.get(psetIds.get(0));
        if (psetRecord != null && psetRecord.getEntityName().equals("IFCPROPERTYSET")) {
            List<Integer> propIds = extractAllIds(psetRecord.getParam(4));

            for (Integer targetId : targetIds) {
                BimObject bimObj = getOrCreateObject(targetId, model);
                for (Integer pId : propIds) {
                    IfcRawRecord prop = model.get(pId);
                    if (prop != null) {
                        // We save in the object's directProperties (whether instance or type)
                        bimObj.addDirectProperty(prop.getParam(0), cleanIfcValue(prop.getParam(2)));
                    }
                }
            }
        }
    }

    private void processTypeRelation(IfcRawRecord rel, Map<Integer, IfcRawRecord> model) {
              
        List<Integer> instanceIds = extractAllIds(rel.getParam(4));
        List<Integer> typeIds = extractAllIds(rel.getParam(5));

        if (!typeIds.isEmpty()) {
            int typeId = typeIds.get(0);
            
            getOrCreateObject(typeId, model);

            for (Integer instId : instanceIds) {
                getOrCreateObject(instId, model);
                instanceToTypeMap.put(instId, typeId);
            }
        }
    }

    private void resolveInheritance() {
        for (Map.Entry<Integer, Integer> entry : instanceToTypeMap.entrySet()) {
            int instId = entry.getKey();
            int typeId = entry.getValue();

            BimObject instanceObj = allObjects.get(instId);
            BimObject typeObj = allObjects.get(typeId);

            if (instanceObj != null && typeObj != null) {
                // We copy the direct properties of the TYPE to those inherited from the INSTANCE
                for (Map.Entry<String, String> prop : typeObj.getDirectProperties().entrySet()) {
                    instanceObj.addInheritedProperty(prop.getKey(), prop.getValue());
                }
            }
        }
    }

    private List<Integer> extractAllIds(String text) {
        List<Integer> ids = new ArrayList<>();
        if (text == null || text.isEmpty() || text.equals("$")) return ids;
        Matcher m = Pattern.compile("#(\\d+)").matcher(text);
        while (m.find()) {
            ids.add(Integer.parseInt(m.group(1)));
        }
        return ids;
    }
    
 
    private BimObject getOrCreateObject(int id, Map<Integer, IfcRawRecord> model) {
        return allObjects.computeIfAbsent(id, k -> {
            IfcRawRecord raw = model.get(k);
            String type = (raw != null) ? raw.getEntityName() : "UNKNOWN";
            BimObject bimObj = new BimObject(k, type);

            
            if (raw != null && raw.getParams().size() > 0) {
                String firstParam = raw.getParam(0); // GUI
                if (isLikelyGuid(firstParam)) {
                    bimObj.setGuid(cleanString(firstParam));
                }
                
                String fivetParam = raw.getParam(5);
               
                if (isLikelyId(fivetParam)) {
                	int pid=Integer.parseInt( fivetParam.replace("#", "")); // ID for placement
                    bimObj.setplaceId(pid);
                   }
            }
            
            return bimObj;
        });
    }

    private boolean isLikelyId(String param) {
    	if (param == null) return false;
    	return param.startsWith("#");
	}


    private boolean isLikelyGuid(String param) {
        if (param == null) return false;
        return param.startsWith("'") && param.endsWith("'") && param.length() == 24;
    }


    private String cleanString(String text) {
        if (text == null) return "";
        return text.replace("'", "");
    }

    private String cleanIfcValue(String raw) {
        if (raw == null || raw.equals("$")) return "";
        /*Pattern p = Pattern.compile("\\w+\\((.*)\\)");
        Matcher m = p.matcher(raw);
        if (m.find()) {
            return cleanString(m.group(1));
        }*/
        return cleanString(raw);
    }
    
    
    
}