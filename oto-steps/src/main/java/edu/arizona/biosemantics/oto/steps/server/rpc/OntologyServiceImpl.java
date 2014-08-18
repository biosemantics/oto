/**
 * 
 */
package edu.arizona.biosemantics.oto.steps.server.rpc;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

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
		return ont.getClassesInSignature(true).contains(clas);
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
        return reasoner.getSuperClasses(subclass, true).containsEntity(superclass);
	}



}
