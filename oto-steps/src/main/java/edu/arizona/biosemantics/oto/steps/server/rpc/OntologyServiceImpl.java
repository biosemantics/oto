/**
 * 
 */
package edu.arizona.biosemantics.oto.steps.server.rpc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import edu.arizona.biosemantics.oto.steps.server.Configuration;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyInfo;
import edu.arizona.biosemantics.oto.steps.shared.rpc.OntologyService;

/**
 * @author Hong Cui
 *
 */
public class OntologyServiceImpl implements OntologyService {

	/**
	 * 
	 */
	public OntologyServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see edu.arizona.biosemantics.oto.steps.shared.rpc.OntologyService#exists(org.semanticweb.owlapi.model.OWLOntology, org.semanticweb.owlapi.model.OWLClass)
	 */
	@Override
	public boolean exists(OWLOntology ont, OWLClass clas) {
		return ont.getClassesInSignature(true).contains(clas); //include all imported ontos
	}

	/* (non-Javadoc)
	 * @see edu.arizona.biosemantics.oto.steps.shared.rpc.OntologyService#isA(org.semanticweb.owlapi.model.OWLClass, org.semanticweb.owlapi.model.OWLClass)
	 */
	@Override
	public boolean isA(OWLOntology ont, OWLClass subclass, OWLClass superclass) {
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        OWLReasonerConfiguration config = new SimpleConfiguration(
                progressMonitor);
        OWLReasoner reasoner = reasonerFactory.createReasoner(ont, config);
        reasoner.precomputeInferences();
        return reasoner.getSuperClasses(subclass, false).containsEntity(superclass); //false: retrieval all ancestors.
	}

	/**
	 * Find in ont, classes that are descendant of entityRoot and are not the object of any partOf relation
	 * @param ont
	 * @return null if ont is not consistent, empty list if nothing meets the requirements, or a populated list
	 * @throws Exception 
	 */
	public Set<OWLClass> getMajorOrgans(OWLOntology ont, OWLClass entityRoot, OWLObjectProperty partOf) throws Exception{
		
		HashSet<OWLClass> majorOrgans = new HashSet<OWLClass> ();
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		
		//prep arguments
		ont = manager.loadOntologyFromOntologyDocument(new File(Configuration.ontology_dir,"po.owl"));
	
		entityRoot = factory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/PO_0025131")); //plant anatomical entity
		partOf = factory.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/BFO_0000050")); 
		
		//logic
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        OWLReasonerConfiguration config = new SimpleConfiguration(
                progressMonitor);
        OWLReasoner reasoner = reasonerFactory.createReasoner(ont, config);
        reasoner.precomputeInferences();
        if(!reasoner.isConsistent()) return null;
        
        Set<OWLClass> pool = reasoner.getSubClasses(entityRoot, false).getFlattened(); //false:retrieve all descendants
        Iterator<OWLClass> it1 = pool.iterator();
        while(it1.hasNext()){
        	OWLClass clz1 = it1.next();
        	Iterator<OWLClass> it2 = pool.iterator();
        	boolean collect = true;
        	while(it2.hasNext()){
        		OWLClass clz2 = it2.next();
        		OWLAxiom c1PartofC2 = factory.getOWLSubClassOfAxiom(clz1, factory.getOWLObjectSomeValuesFrom(partOf, clz2)); //c1 subclass_of part_of some c2
        		if(reasoner.isEntailed(c1PartofC2)){
        			collect = false;
        			break;
        		}
        	}
        	String label = getLabel(clz1, ont, factory);
        	if(collect){
        		System.out.println("collected: "+label);
        		majorOrgans.add(clz1);
        	}else{
        		System.out.println("not collected: "+label);
        	}
        }
        
       
        
		return majorOrgans;
	}


	private String getLabel(OWLClass cls, OWLOntology ont, OWLDataFactory factory) throws Exception {
		OWLAnnotationProperty label = factory
				.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		for (OWLAnnotation annotation : cls.getAnnotations(ont, label)) {
			if (annotation.getValue() instanceof OWLLiteral) {
				OWLLiteral val = (OWLLiteral) annotation.getValue();
				//if (val.hasLang("en")) {
				return val.getLiteral();
				//}
			}
		}
		return null;
	}
	
	public boolean markDeprecated(OWLClass cls, OWLOntology ont, OWLDataFactory factory, OWLOntologyManager man) throws Exception{
		OWLAxiom dep = factory.getDeprecatedOWLAnnotationAssertionAxiom(cls.getIRI());
		man.applyChange(new AddAxiom(ont, dep));
		return true;
	}
	
	public static void main (String[] argv){
		OntologyServiceImpl ontologyService = new OntologyServiceImpl(); 
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        IRI ontologyIRI = IRI.create("http://example.com/owlapi/families");
        try {
			OWLOntology ont = manager.createOntology(ontologyIRI);
	        OWLDataFactory factory = manager.getOWLDataFactory();
	        OWLClass man = factory.getOWLClass(IRI
	                .create(ontologyIRI + "#man"));
	        OWLClass boy = factory.getOWLClass(IRI
	                .create(ontologyIRI + "#boy"));
	        manager.addAxiom(ont, factory.getOWLSubClassOfAxiom(boy, man));
	        ontologyService.markDeprecated(man, ont, factory, manager);
	        System.out.println("RDF/XML: ");
	        manager.saveOntology(ont, new StreamDocumentTarget(System.out));
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
        /*OntologyServiceImpl ontologyService = new OntologyServiceImpl(); 
		try {
			ontologyService.getMajorOrgans(null, null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
