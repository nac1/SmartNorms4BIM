<div align="center">
<h1>SmartNorms4BIM</h1>
</div>

## About
This GitHub repository contains prompts, full prompt chains, norms, geometric algorithms, a BIM extraction algorithm, and an OWL2 ontology (IFCtoOWL) population algorithm.

## 🤖 Prompt chains

| # | Item | Description | Resource|
|---:|---|---|---|
| 1 | **Prompts** | Contains the prompts in markdown format. |  [`Norms/prompts`](Norms/prompts) |
| 2 | **First prompt** | The initial prompt indicates to LLM how to work with a specific profile, files, steps, and examples.|  [`prompts/first_Prompt.md`](Norms/prompts/first_Prompt.md) |
| 3 | **Prompts for norms 1-5** | Sequence of prompts used for norms of type 1 (Object relation), type 2 (Cardinality), type 3 (Data type), type 4 (Logical), and type 5 (Value and unit).| [`prompts/prompt_norms1_5.md`](Norms/prompts/prompt_norms1_5.md) |
| 4 | **Prompts for geometric norms** | Sequence of prompts used for norms of type 6 (geometric norms).|  [`prompts/prompt_norm6.md`](Norms/prompts/prompt_norm6.md) |
| 5 | **Prompts for subjective norms** | Sequence of prompts used for norm type 8 (subjective norms).|  [`prompts/prompt_norm8.md`](Norms/prompts/prompt_norm8.md) |
| 6 | **LLM examples** | Contains 8 folders, one for each type of norm. Each folder includes a file named prompt.md, which contains the sequence of prompts used to generate the corresponding resulting ontologies. ChatGPT-5 was used for ontology generation.|  [`Norms/LLM_examples`](Norms/LLM_examples) |
| 7 | **ACE examples** | Contains 8 folders, one for each type of norm. Each folder includes a file named ACE_grammar.md, which contains the norm formalized in Attempto Controlled English (ACE) and its corresponding OWL ontology.|  [`Norms/ACE_examples`](Norms/ACE_examples)|

## ✅Geometric algorithms

| # | Item | Description | Resource|
|---:|---|---|---|
| 1 | **Circle inscription**|  Contains a C++ implementation of a circle inscription algorithm. | [`SkeletonCircle/SkeletonCircle.cpp`](Geometric/SkeletonCircle/SkeletonCircle/SkeletonCircle.cpp) |
| 2 | **Internal route**|  Contains a C++ implementation of an internal route algorithm. | [`InternalRoute/InternalRoute.cpp`](Geometric/InternalRoute/InternalRoute/InternalRoute.cpp) |

## 🏗️ BIM and Ontology implementations

| # | Item | Description | Resource|
|---:|---|---|---|
| 1 | **BIM information extraction**| Contains a Java implementation of <a href=http://www.see.eng.osaka-u.ac.jp/seeit/icccbe2016/Proceedings/Full_Papers/067-268.pdf>Zhang et al.'s (2016) algorithm </a> for BIM information extraction.| [`bimExtraction/BIMExtractor.java`](BIMInformationExtraction/src/bimExtraction/BIMExtractor.java) |
| 2 | **IFCtoOWL**| Contains a Java implementation of an ontology population algorithm. Example for norm type 1.| [`app/Main.java`](BIMInformationExtraction/src/app/Main.java) 

## 📚 Resources
| # | Item | Description | Resource|
|---:|---|---|---|
| 1 | **Resources**| Constains the files used to test  BIM information extraction and  ontology population algorithm.  |[`BIMInformationExtraction/resources`](BIMInformationExtraction/resources) |
<!--| 2 | **BIM model**| Constains the IFC file.  |[`resources/7_dwelling_building_IFC2x3.ifc`](BIMInformationExtraction/resources/7_dwelling_building_IFC2x3.ifc) | -->



## 📁 Structure
```text
.
|-- Norms              # Prompt chains
	|-- prompts        # Templates of prompts
	|-- LLM_examples   # Full prompt chains of ChatGPT-5
	|-- ACE_examples   # Examples of ACE grammar
|-- Geometric          # Geometric algorithms
	|-- SkeletonCircle # C++ project: Circle inscription
	|-- InternalRoute  # C++ project: Internal route
|-- BIMExtraction      # Java project : BIM information extraction and IFCtoOWL
	|-- lib            # Java libraries 
	|-- resources      # owl and csv files
	|-- src            # Packages and Java classes
```


## 📝 Citation
```
@misc{huitzil2026smartnorms4bim,
	title        = {Semi-Automated Code Compliance Cheking based on Semantic Reasoning},
    author       = {Ignacio Huitzil and  Luc{\'i}a Pitarch and Marco Schorlemmer and Nardine Osman and Josep Coll and Fernando Bobillo},
  	year         = {2026},
  	publisher    = {SSRN},
 	doi          = {10.2139/ssrn.6329621},
 	url          = {https://ssrn.com/abstract=6329621},
}
```
<!-- ## ⚖️ Licence -->
