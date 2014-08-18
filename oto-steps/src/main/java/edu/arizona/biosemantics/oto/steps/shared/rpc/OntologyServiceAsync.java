/**
 * 
 */
package edu.arizona.biosemantics.oto.steps.shared.rpc;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author Hong Cui
 *
 */
public interface OntologyServiceAsync {
	void exists(OWLOntology ont, OWLClass clas, AsyncCallback<Boolean> callback);
	void isA(OWLOntology ont, OWLClass subclass, OWLClass superclass, AsyncCallback<Boolean> callback);
}
