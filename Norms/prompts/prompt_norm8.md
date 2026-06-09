# Sequence of prompts for SmartNorms4BIM
Sequence of prompts for norms **type 8** (subjective norms).

# Prompt 1: 
Write < norm> "Bridges with a long length"</ norm> in OWL Functional Syntax. You should create a full ontology.

> Answer 1: ontology1.owl

# Prompt 2: Fuzzy datatypes
The datatype MinLength_100 was inferred, but this specification is not explicitly defined in the ontology. We need define a linguistic label.

 ### Example
<user_query id="example-1">  
Create a linguistic label where "Long" = unkwo , "Bridge"=class name, and "Length"=data property. This label must be modeled as a datatype with an appropriate prefix declaration (e.g., sid:LongBridgeLength). Its range may be xsd:string, xsd:float, xsd:decimal, or unspecified.  
</user_query>

<expert_response>  
Datatype: sid:LongBridgeLength  
Class: LongBridge EquivalentTo: Bridge and hasLength some sid:LongBridgeLength   
</expert_response> 

> Answer 2: ontology2.owl

# Prompt 3: core & support  
You identify a <ontology_element>"class like LongBridge" </ontology_element>. <description> Now redefine the ontology using two classes: LongBridgeCore and LongBridgeSupport, with cardinality constraints >=490 and >215, respectively.</description>

### Example
<user_query id="example-2">  
Define a class LongBridgeCore with cardinality constraints >=490.  
</user_query>

<expert_response>  
 Class: LongBridgeCore EquivalentTo: Bridge and hasLength some xsd:decimal[>= "490"]   
</expert_response>

>Answer 3: ontology3.owl

# Prompt 4:
A correction: Using LongBridgeCore AND LongBridgeSupport, the reasoner do not found some instances of LongBridge because they should be in both classes in Core and Support. LongBridge instances should belong to LongBridgeCore or LongBridgeSupport.

>Answer 4: ontology4.owl
