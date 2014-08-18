/**
 * 
 */
package edu.arizona.biosemantics.oto.steps.client.event.to_ontologies;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyInfo;

/**
 * @author Hong Cui
 *
 */
public class CreateOntologyEvent extends GwtEvent<CreateOntologyEventHandler> {
	private String userID;
	private OntologyInfo ontoInfo;
	private String uploadID;
	public static Type<CreateOntologyEventHandler> TYPE = new Type<CreateOntologyEventHandler>();

	/**
	 * 
	 */
	public CreateOntologyEvent(String userID, String uploadID, OntologyInfo ontoInfo) {
		this.userID = userID;
		this.ontoInfo = ontoInfo;
		this.uploadID = uploadID;
	}

	public OntologyInfo getOntologyInfo(){
		return this.ontoInfo;
	}
	
	public String getUserID(){
		return this.userID;
	}
	
	@Override
	protected void dispatch(CreateOntologyEventHandler handler) {
		handler.onSubmit(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CreateOntologyEventHandler> getAssociatedType() {
		return TYPE;
	}

	public String getUploadID() {
		return this.uploadID;
	}



}
