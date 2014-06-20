package edu.arizona.biosemantics.oto.steps.client.event.context;

import com.google.gwt.event.shared.GwtEvent;

public class ViewTermInfoEvent extends GwtEvent<ViewTermInfoEventHandler> {
	public static Type<ViewTermInfoEventHandler> TYPE = new Type<ViewTermInfoEventHandler>();

	private String term;

	public ViewTermInfoEvent(String term) {
		this.term = term;
	}

	@Override
	public Type<ViewTermInfoEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ViewTermInfoEventHandler handler) {
		handler.onViewTermInfo(this);
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

}
