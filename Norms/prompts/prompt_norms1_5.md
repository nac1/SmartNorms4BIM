# Sequence of prompts for SmartNorms4BIM
Sequence of prompts used for norms of **types 1 to 5**, based on zero-shot technique.

# Prompt 1:
Write < norm> "All the valid bathrooms must have hygienic equipment that consists of at least one sink, one toilet and one shower" </ norm> 
in OWL Functional Syntax. You should create a full ontology.

>  Answer 1: Ontology1.owl

# Prompt 2:
Yes, please create the extension of the ontology.

>  Answer 2: Ontology2.owl

# Prompt 3 (for errors or to improve the ontology):  
Check the <ontology_element> "data property " </ontology_element> there is/are <number_of_elements> "a error" </number_of_elements> in the <description> "URI name..." </description>.

<detail_description >  
"It should conform to the OWL 2, Use obj:data_property_name ..."  
</detail_description>.

>  Answer 3: Ontology3.owl