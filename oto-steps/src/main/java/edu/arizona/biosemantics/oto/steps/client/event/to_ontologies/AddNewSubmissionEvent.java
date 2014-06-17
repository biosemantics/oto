package edu.arizona.biosemantics.oto.steps.client.event.to_ontologies;

import com.google.gwt.event.shared.GwtEvent;

public class AddNewSubmissionEvent extends
		GwtEvent<AddNewSubmissionEventHandler> {

	public static Type<AddNewSubmissionEventHandler> TYPE = new Type<AddNewSubmissionEventHandler>();
	private String term;
	private String category;

	public AddNewSubmissionEvent(String term, String category) {
		this.term = term;
		this.category = category;
	}

	@Override
	public Type<AddNewSubmissionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddNewSubmissionEventHandler handler) {
		handler.onClick(this);
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
