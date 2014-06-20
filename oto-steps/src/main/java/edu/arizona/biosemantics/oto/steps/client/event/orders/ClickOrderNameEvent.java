package edu.arizona.biosemantics.oto.steps.client.event.orders;

import java.util.ArrayList;

import com.google.gwt.event.shared.GwtEvent;

public class ClickOrderNameEvent extends GwtEvent<ClickOrderNameEventHandler> {

	public static Type<ClickOrderNameEventHandler> TYPE = new Type<ClickOrderNameEventHandler>();

	private ArrayList<String> terms;

	public ClickOrderNameEvent(ArrayList<String> terms) {
		this.setTerms(terms);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ClickOrderNameEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ClickOrderNameEventHandler handler) {
		handler.onClick(this);

	}

	public ArrayList<String> getTerms() {
		return terms;
	}

	public void setTerms(ArrayList<String> terms) {
		this.terms = terms;
	}

}
