package edu.arizona.biosemantics.oto.steps.client.event.to_ontologies;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologySubmission;

public class BackToDetailViewEvent extends
		GwtEvent<BackToDetailViewEventHandler> {
	public static Type<BackToDetailViewEventHandler> TYPE = new Type<BackToDetailViewEventHandler>();

	private OntologySubmission submission;

	public BackToDetailViewEvent(OntologySubmission subission) {
		this.setSubmission(subission);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<BackToDetailViewEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(BackToDetailViewEventHandler handler) {
		handler.onClick(this);
	}

	public OntologySubmission getSubmission() {
		return submission;
	}

	public void setSubmission(OntologySubmission submission) {
		this.submission = submission;
	}

}
