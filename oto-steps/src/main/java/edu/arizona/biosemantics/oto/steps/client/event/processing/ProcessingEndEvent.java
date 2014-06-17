package edu.arizona.biosemantics.oto.steps.client.event.processing;

import com.google.gwt.event.shared.GwtEvent;

public class ProcessingEndEvent extends GwtEvent<ProcessingEndEventHandler> {

	public static Type<ProcessingEndEventHandler> TYPE = new Type<ProcessingEndEventHandler>();

	@Override
	public Type<ProcessingEndEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ProcessingEndEventHandler handler) {
		handler.onProcessingEnd(this);
	}

}
