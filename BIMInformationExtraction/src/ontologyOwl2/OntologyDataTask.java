package ontologyOwl2;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import bimExtraction.IfcPropertyValue;

/*
 * This class offers the assertions to populate an ontology
 * 
 */
public class OntologyDataTask {
	static IRI ontologyIRI;
	static OWLOntology ontology;
	static OWLOntologyManager manager= OWLManager.createOWLOntologyManager();
	static OWLDataFactory factory= manager.getOWLDataFactory();
	OWLReasoner reasoner;
	
	private Map<String, IRI> classMap = new HashMap<>();
	private Map<String, IRI> objectPropMap = new HashMap<>();
	private Map<String, IRI> dataPropMap = new HashMap<>();
	
	public OWLReasoner getReasoner() {
		return reasoner;
	}

	public void setReasoner(OWLReasoner reasoner) {
		this.reasoner = reasoner;
	}

	public static IRI getOntologyIRI() {
		return ontologyIRI;
	}

	public static void setOntologyIRI(IRI ontologyIRI) {
		OntologyDataTask.ontologyIRI = ontologyIRI;
	}

	public static OWLOntology getOntology() {
		return ontology;
	}

	public static void setOntology(OWLOntology ontology) {
		OntologyDataTask.ontology = ontology;
	}

	public static OWLOntologyManager getManager() {
		return manager;
	}

	public static void setManager(OWLOntologyManager manager) {
		OntologyDataTask.manager = manager;
	}

	public static OWLDataFactory getFactory() {
		return factory;
	}

	public static void setFactory(OWLDataFactory factory) {
		OntologyDataTask.factory = factory;
	}


	public void saveOntology(String name) throws OWLOntologyStorageException
	{
		 File destinationFile = new File(name);
	     OWLOntologyFormat format = manager.getOntologyFormat(ontology);
	     manager.saveOntology(ontology, format, IRI.create(destinationFile.toURI()));
	}
	
	
	public void readOntology(String fileName) throws OWLOntologyCreationException, IOException {
	
        String cleanedContent = getCleanedOntology(fileName); 
		ontology = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(cleanedContent));
		ontologyIRI = ontology.getOntologyID().getOntologyIRI(); 
		//System.out.println(ontologyIRI.toString());
		
		for (OWLClass cls : ontology.getClassesInSignature()) {
		    classMap.put(cls.getIRI().getFragment(),cls.getIRI());
		}
				
		for (OWLObjectProperty p : ontology.getObjectPropertiesInSignature()) {
			objectPropMap.put(p.getIRI().getFragment(), p.getIRI());
		}
		
		
		for (OWLDataProperty p : ontology.getDataPropertiesInSignature()) {
			dataPropMap.put(p.getIRI().getFragment(), p.getIRI());
		}
	}
	
	public OWLClass getClass(String className) {
	    IRI iri = classMap.get(className);

	    if (iri == null) {
	    	IRI iriTemp=IRI.create("http://example.org/norms#"+className);
	    	return factory.getOWLClass(iriTemp);
	    }

	    return factory.getOWLClass(iri);
	}
	
	
	public OWLDataProperty getDataProperty(String role) {
		IRI iri = dataPropMap.get(role);
		if (iri == null) {
		    	IRI iriTemp=IRI.create("http://example.org/norms#"+role);
		    	return factory.getOWLDataProperty(iriTemp);
		    }
		
		return factory.getOWLDataProperty(iri);
     }


	public OWLObjectProperty getObjectProperty(String role) {
	    IRI iri = objectPropMap.get(role);

	    if (iri == null) {
	    	IRI iriTemp=IRI.create("http://example.org/norms#"+role);
	    	return factory.getOWLObjectProperty(iriTemp);
	    }

	    return factory.getOWLObjectProperty(iri);
	}
	
	public static IRI iri(Object o) {
		return IRI.create( ontologyIRI.toString() + "#" + o.toString());
	}

	public static OWLNamedIndividual getIndividual(String ind) {
		IRI iriTemp=IRI.create("http://example.org/norms#"+ind);
		return factory.getOWLNamedIndividual(iriTemp);
	}

	
	public void classAssertion(String indName, String className) {
	    OWLNamedIndividual ind = getIndividual(indName);
	    OWLClass cls = getClass(className);
	    OWLClassAssertionAxiom axiom =factory.getOWLClassAssertionAxiom(cls, ind);
	    manager.addAxiom(ontology, axiom);
	}

	public void addObjectPropertyAssertion(String indName1, String roleName, String indName2) {
		OWLNamedIndividual i1 = getIndividual(indName1);
		OWLNamedIndividual i2 = getIndividual(indName2);
		OWLObjectProperty op = getObjectProperty(roleName);
		OWLObjectPropertyAssertionAxiom axiom = factory.getOWLObjectPropertyAssertionAxiom(op, i1, i2);
		 manager.addAxiom(ontology, axiom);
	}

	public void addDataPropertyAssertion(String indName, String roleName, String lit) {
		OWLNamedIndividual i = getIndividual(indName);
		OWLDataProperty dp = getDataProperty(roleName);
		OWLLiteral l = factory.getOWLLiteral(lit);
		OWLDataPropertyAssertionAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(dp, i, l);
		manager.addAxiom(ontology, axiom);
	}
		
	public void addDataPropertyAssertion(String indName, String roleName, Boolean lit) {
		OWLNamedIndividual i = getIndividual(indName);
		OWLDataProperty dp = getDataProperty(roleName);
		OWLLiteral l = factory.getOWLLiteral(lit);
		OWLDataPropertyAssertionAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(dp, i, l);
		manager.addAxiom(ontology, axiom);
	}
	
	public void addDataPropertyAssertion(String indName, String roleName, int lit) {
		//if (lit < 0)
		//	return;
		OWLNamedIndividual i = getIndividual(indName);
		OWLDataProperty dp = getDataProperty(roleName);
		OWLLiteral l = factory.getOWLLiteral(lit);
		OWLDataPropertyAssertionAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(dp, i, l);
		manager.addAxiom(ontology, axiom);
	}
	
	public  void addDataPropertyAssertion(String indName, String roleName, double lit) {
		OWLNamedIndividual i = getIndividual(indName);
		OWLDataProperty dp = getDataProperty(roleName);
		OWLLiteral l = factory.getOWLLiteral(Double.toString(lit),
		factory.getOWLDatatype(OWL2Datatype.XSD_DECIMAL.getIRI()));
		OWLDataPropertyAssertionAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(dp, i, l);
		manager.addAxiom(ontology, axiom);
	}
	
	
	public void addDataPropertyAssertion(String indName,String roleName,IfcPropertyValue value) {

	    switch (value.getJavaType()) {

	        case STRING ->
	            addDataPropertyAssertion(indName,roleName,value.asString());

	        case DOUBLE ->
	            addDataPropertyAssertion(indName,roleName, value.asDouble());

	        case INTEGER ->
	            addDataPropertyAssertion(indName,roleName,value.asInteger());

	        case BOOLEAN ->
	            addDataPropertyAssertion(indName,roleName, value.asBoolean());
	    }
	}

	
	private String getCleanedOntology(String fileName) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            String cleanedLine = removeCommentsFromLine(line);
	            
	            if (!cleanedLine.isEmpty()) {
	                sb.append(cleanedLine).append("\n");
	            }
	        }
	    }
	    return sb.toString().trim();
	}

	private String removeCommentsFromLine(String line) {
	    if (line == null || line.trim().isEmpty()) return "";

	    boolean inString = false;
	    boolean inIri = false;
	    int commentStartIndex = -1;

	    for (int i = 0; i < line.length(); i++) {
	        char c = line.charAt(i);

	
	        if (c == '"' && (i == 0 || line.charAt(i - 1) != '\\')) {
	            inString = !inString;
	        } 
	        
	
	        else if (!inString && c == '<') {
	            inIri = true;
	        } 
	        else if (!inString && c == '>') {
	            inIri = false;
	        }

	     
	        else if (c == '#' && !inString && !inIri) {
	            commentStartIndex = i;
	            break; 
	        }
	    }

	
	    if (commentStartIndex != -1) {
	        return line.substring(0, commentStartIndex).trim();
	    }

	    return line.trim();
	}
}
