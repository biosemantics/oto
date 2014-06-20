package edu.arizona.biosemantics.oto.steps.client.event.orders;

import com.google.gwt.event.shared.GwtEvent;

public class ClickTermEvent extends GwtEvent<ClickTermEventHandler> {
	public static Type<ClickTermEventHandler> TYPE = new Type<ClickTermEventHandler>();

	private String termName;

	public ClickTermEvent(String termName) {
		this.termName = termName;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ClickTermEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ClickTermEventHandler handler) {
		handler.onClick(this);
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

}
