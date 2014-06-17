package edu.arizona.biosemantics.oto.steps.client.event.to_ontologies;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyRecordType;

public class OntologyRecordClickEvent extends
		GwtEvent<OntologyRecordClickEventHandler> {
	public static Type<OntologyRecordClickEventHandler> TYPE = new Type<OntologyRecordClickEventHandler>();
	private OntologyRecordType type;
	private String recordID;

	public OntologyRecordClickEvent(String recordID, OntologyRecordType type) {
		this.recordID = recordID;
		this.type = type;
	}

	@Override
	public Type<OntologyRecordClickEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OntologyRecordClickEventHandler handler) {
		handler.onClick(this);
	}

	public OntologyRecordType getType() {
		return type;
	}

	public void setType(OntologyRecordType type) {
		this.type = type;
	}

	public String getRecordID() {
		return recordID;
	}

	public void setRecordID(String recordID) {
		this.recordID = recordID;
	}

}
