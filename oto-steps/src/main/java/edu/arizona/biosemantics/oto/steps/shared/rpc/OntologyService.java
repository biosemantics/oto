package edu.arizona.biosemantics.oto.steps.shared.rpc;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import com.google.gwt.user.client.rpc.RemoteService;

public interface OntologyService extends RemoteService {
	boolean exists(OWLOntology ont, OWLClass clas);
	boolean isA(OWLOntology ont, OWLClass subclass, OWLClass superclass);
}
