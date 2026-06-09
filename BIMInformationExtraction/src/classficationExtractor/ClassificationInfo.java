package classficationExtractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassificationInfo{
	String classification;
    String code;
        
public ClassificationInfo(String classification, String code) {
	this.classification =classification;
    this.code = code;
    }

public ClassificationInfo() {
	
}

public String getClassification() { return classification; }
public String getCode() { return code; }
       
@Override
public String toString() {
	return  classification +" : "+ code;
        }
        
	
public static ClassificationInfo parse(String value) {

    Pattern pattern =
        Pattern.compile("\\[([^\\]]+)\\]\\s+([^:]+)\\s*:");

    Matcher matcher = pattern.matcher(value);

    if (!matcher.find()) {
        return null;
    }

    String source = matcher.group(1);
    String code = matcher.group(2).trim();

    String classification =
        source.split("\\s+")[0];

    return new ClassificationInfo(
            classification,
            code);
   }

}
	
	