package ontologyOwl2;

import java.io.File;


import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/*
 * This class check the ontology
 * 
 */
public class OntologyReasoner {
	static IRI ontologyIRI;
	static OWLOntology ontology;
	static OWLOntologyManager manager= OWLManager.createOWLOntologyManager();
	static OWLDataFactory factory= manager.getOWLDataFactory();
	OWLReasoner reasoner;
	
	public static IRI getOntologyIRI() {
		return ontologyIRI;
	}

	public static void setOntologyIRI(IRI ontologyIRI) {
		OntologyReasoner.ontologyIRI = ontologyIRI;
	}

	public static OWLOntology getOntology() {
		return ontology;
	}

	public static void setOntology(OWLOntology ontology) {
		OntologyReasoner.ontology = ontology;
	}

	public static OWLOntologyManager getManager() {
		return manager;
	}

	public static void setManager(OWLOntologyManager manager) {
		OntologyReasoner.manager = manager;
	}

	public static OWLDataFactory getFactory() {
		return factory;
	}

	public static void setFactory(OWLDataFactory factory) {
		OntologyReasoner.factory = factory;
	}
	
	public void reasoningOntology(String fileName) throws OWLOntologyCreationException {
		
		ontology = manager.loadOntologyFromOntologyDocument(new File(fileName));	
		ontologyIRI = ontology.getOntologyID().getOntologyIRI(); 
		//System.out.println(ontologyIRI.toString());
		
		reasoner=new Reasoner.ReasonerFactory().createReasoner(ontology);
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY,InferenceType.DATA_PROPERTY_ASSERTIONS);
		
		System.out.println("Reasoner: " + reasoner.toString());
		System.out.println("Ontology is consistent?: " + reasoner.isConsistent());
				
	}
	
		public void showInstancesOfClass(String className)
	{
		OWLClass claseInteres = factory.getOWLClass(IRI.create("http://example.org/norms#"+className));
		NodeSet<OWLNamedIndividual> instanceSet = reasoner.getInstances(claseInteres, true);
		
		for (OWLNamedIndividual ins : instanceSet.getFlattened()) {
		    System.out.println("Instance found: " + ins.getIRI().toString());
		}
	}
	

}
