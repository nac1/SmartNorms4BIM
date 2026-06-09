# Sequence of prompts for SmartNorms4BIM
Sequence of prompts for norms  **type 6** (geometric norms).

# Prompt 1:

Write < norm> "All Valid practicable spaces should be possible to inscribe a circle
of 1.20 m in diameter, free of the impact of the rotation of doors.
The interior routes of these spaces must have a minimum passage width of 0.80 m." </ norm> in OWL Functional Syntax. You should create a full ontology.

> Answer 1: Ontology1.owl

# Prompt 2:

Yes, please create the extension of the ontology.

> Answer 2: Ontology2.owl 

# Prompt 3 (integration with geometric algorithms): 
Check the <ontology_element> "object property " </ontology_element> there is/are <number_of_elements> "a error" </number_of_elements> in the <description > "hasCircle  property" </description>.

<detail_description >  
"In this case, we need to treat it as a data property because a numerical value for circle diameter is computed externally. Change the property name, type and range"  
</detail_description>.

### Example (if necessary)
<user_query id="example-1">  
"add query"  
<user_query>  

<expert_response>  
 "add example"  
</expert_response> 

> Answer 3: Ontology3.owl 

