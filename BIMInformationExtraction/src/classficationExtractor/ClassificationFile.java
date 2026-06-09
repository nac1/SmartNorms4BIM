package classficationExtractor;
import java.io.*;
import java.util.*;
/*
 * This class loads a IFC file
 * 
 */
public class ClassificationFile {

    public Map<String, String> loadFile(String filePath) throws IOException {
    	Map<String, String> rawClassification = new HashMap<>();
        
        InputStream is = ClassificationFile.class.getClassLoader().getResourceAsStream(filePath);
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
            	 String[] parts = line.split(",", 2);
            	 rawClassification.put(parts[0].trim(), parts[1].trim());
            	 
            }
        }
        return rawClassification;
    }
  
}

