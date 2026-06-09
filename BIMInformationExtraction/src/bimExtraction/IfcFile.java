package bimExtraction;
import java.util.regex.*;
import java.io.*;
import java.util.*;
/*
 * This class loads a IFC file
 * 
 */
public class IfcFile {
    private static final Pattern IFC_LINE_PATTERN = Pattern.compile("^#(\\d+)\\s*=\\s*(\\w+)\\s*\\((.*)\\)\\s*;\\s*$");

    public Map<Integer, IfcRawRecord> loadFile(String filePath) throws IOException {
        Map<Integer, IfcRawRecord> rawModel = new HashMap<>();
        
        InputStream is = IfcFile.class.getClassLoader().getResourceAsStream(filePath);
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = IFC_LINE_PATTERN.matcher(line.trim());
                if (matcher.find()) {
                    int id = Integer.parseInt(matcher.group(1));
                    String name = matcher.group(2);
                    String params = matcher.group(3);
                    rawModel.put(id, new IfcRawRecord(id, name, params));
                }
            }
        }
        return rawModel;
    }
  
}