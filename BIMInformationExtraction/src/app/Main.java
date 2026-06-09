package app;

import java.util.Map;

import bimExtraction.BIMExtractor;
import bimExtraction.BimObject;
import bimExtraction.BimReferenceExtractor;
import bimExtraction.IfcFile;
import bimExtraction.IfcPropertyValue;
import bimExtraction.IfcRawRecord;
import bimExtraction.IfcValueParser;
import classficationExtractor.ClassificationFile;
import classficationExtractor.ClassificationInfo;
import ontologyOwl2.OntologyDataTask;
import ontologyOwl2.OntologyReasoner;

/*
 * This MAIN class to populate a norm ontology.
 * Here an example of the norm type 1.
 */

public class Main {
   	public static  String CLASSIFIER_FILE1="Uniclass2015_SL_v1_32_singular.csv";
   	public static  String CLASSIFIER_FILE2="GuBIMClass v.1.2_CA.csv";
   	public static  String IFC_FILE="7_dwelling_building_IFC2x3.ifc";
   	public static  String ONTOLOGY_FILE="resources/ontology2.owl";
   	public static  String NEW_ONTOLOGY_FILE="resources/new_ontology.owl";
   	public static  OntologyDataTask normOntology= new OntologyDataTask();

	public static void main(String[] args) {
	    try {

	    	// 0. Load classifier files
	    	ClassificationFile classSystem = new  ClassificationFile();
	        Map<String, String> uniclass2015 = classSystem.loadFile(CLASSIFIER_FILE1);
	        
	        classSystem = new  ClassificationFile();
		    Map<String, String> guBIMClass = classSystem.loadFile(CLASSIFIER_FILE2);
		 
	        // 1. Load IFC file
	        IfcFile parser = new IfcFile();
	        Map<Integer, IfcRawRecord> rawModel = parser.loadFile(IFC_FILE);
	        
	        // 2. Extraction of Information from BIM model, Zhang et al.'s (2016) algorithm 
	        BIMExtractor extractor = new BIMExtractor();
	        Map<Integer, BimObject> finalObjects = extractor.performExtraction(rawModel);
	        
	        // 3. Load placement
	        Map<Integer, Integer> placements = BimReferenceExtractor.extractIdReference(rawModel, "IFCLOCALPLACEMENT",0);

	        // 4. Load norm ontology
	        normOntology.readOntology(ONTOLOGY_FILE);
	               
	        // 5. IFCtoOWL : iterates objects to populate an norm ontology
	        for (BimObject obj : finalObjects.values()) {
	            //System.out.println("Processing: " + obj.getIfcType() + " ID: " + obj.getId()+ " place ID: "+ obj.getPlaceId());
	            
	            if (obj.getDirectProperties().containsKey("ACO_Class_GuBIMclass")) {
	            	
	            	ClassificationInfo classifier =ClassificationInfo.parse(obj.getDirectProperties().get("ACO_Class_GuBIMclass"));
	    	                 	
	            	String indName=obj.getDirectProperties().get("GlobalId");
	            	String clss="";
	            	
	            	 switch (classifier.getClassification()) {
		                 case "Uniclass":
			                 if(obj.getIfcType().equals("IFCSPACE"))
			                 	{
			                	  clss=CamelCase(uniclass2015.get(classifier.getCode()),false);
			                	 
			                	 // assertions
			                	 normOntology.addDataPropertyAssertion(indName,"classification_name_simple_property", clss);
			                	 
			                	 normOntology.classAssertion(indName, clss);
			                	 
				            	 addDirectProperties(obj,indName);
			                 	}
		                	 break; 
		                case "GuBIMclass":
		                	if(obj.getIfcType().equals("IFCFLOWTERMINAL"))
		                 	{
		                	 clss=guBIMClass.get(classifier.getCode());
		                	 
		                	 // assertions 
		                	 normOntology.addDataPropertyAssertion(indName,"classification_name_simple_property", clss);
		                	 
		                	 normOntology.classAssertion(indName, "IfcElement"); 
		                	 
			            	 addDirectProperties(obj, indName);
		                 	}
		                	 break;
						default:
							System.out.println("No classification system, use other method");
	            	 }		               
	            } 

	        }
	      
	     // assertions
	     addObjectProperties(finalObjects,placements);

	     
	     // 6. Save new norm ontology
	     normOntology.saveOntology(NEW_ONTOLOGY_FILE);
	     
	     // 7. Verify ontology: consistency and valid class
	     OntologyReasoner newOntology= new OntologyReasoner();
	     newOntology.reasoningOntology(NEW_ONTOLOGY_FILE);
	     
	     System.out.println("ValidBathroom");
	     newOntology.showInstancesOfClass("ValidBathroom");
	     
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	public static void addDirectProperties(BimObject obj, String indName)
	{
     	 Map<String, String> directs = obj.getDirectProperties();
     	 
     	 for (Map.Entry<String, String> entry : directs.entrySet()) {
     		 
     		 IfcPropertyValue property =IfcValueParser.parse(entry.getValue());
     		 Boolean lower=true;
     		 String  roleName=CamelCase(entry.getKey(),lower);
     		 
     		 normOntology.addDataPropertyAssertion(indName, roleName, property);                		 
	        }
	}
	

	public static void addObjectProperties( Map<Integer, BimObject> finalObjects, Map<Integer, Integer> placements  )
	{
		 // object property assertion
        for (BimObject obj1 : finalObjects.values()) {
        	if(obj1.getIfcType().equals("IFCSPACE"))
        	{
             for (BimObject obj2 : finalObjects.values()) {
            	Integer parentPlaceId = placements.get(obj2.getPlaceId());
            	
            		if (parentPlaceId != null && obj1.getPlaceId() != obj2.getPlaceId() && obj1.getPlaceId() == parentPlaceId) {
	                	 String indName1=obj1.getGuid();
	                	 String indName2=obj2.getGuid();
	                	 
	                	 normOntology.addObjectPropertyAssertion(indName1,"containsElement", indName2);
            		}
            	}
            }
        }
	}
	
	public static String CamelCase(String text, Boolean lower) {

	    String[] words = text.replaceAll("[^a-zA-Z0-9_ ]", " ").replace('_', ' ').trim().split("\\s+");

	    if (words.length == 0) {
	        return "";
	    }

	    StringBuilder result= new StringBuilder(words[0]);
	    if (lower)
	    	 result= new StringBuilder(words[0].toLowerCase());
	    	

	    for (int i = 1; i < words.length; i++) {
	        result.append( Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1).toLowerCase());
	    }

	    return result.toString();
	}
	
	
	public static void printClassification(Map<String, String> classification)
	{
		 for (Map.Entry<String, String> entry : classification.entrySet()) {
	            System.out.println(entry.getKey() + " -> " + entry.getValue());
	        }
	}

}
