package edu.arizona.biosemantics.oto.steps.client.event.to_ontologies;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyRecord;

public class OntologyRecordSelectChangedEvent extends
		GwtEvent<OntologyRecordSelectChangedEventHandler> {
	public static Type<OntologyRecordSelectChangedEventHandler> TYPE = new Type<OntologyRecordSelectChangedEventHandler>();
	private OntologyRecord selectedRecord;

	public OntologyRecordSelectChangedEvent(OntologyRecord selectedRecord) {
		this.selectedRecord = selectedRecord;
	}

	@Override
	public Type<OntologyRecordSelectChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OntologyRecordSelectChangedEventHandler handler) {
		handler.onSelect(this);
	}

	public OntologyRecord getSelectedRecord() {
		return selectedRecord;
	}

	public void setSelectedRecord(OntologyRecord selectedRecord) {
		this.selectedRecord = selectedRecord;
	}

}
