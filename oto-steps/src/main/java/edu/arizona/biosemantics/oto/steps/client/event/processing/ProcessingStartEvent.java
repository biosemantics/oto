package edu.arizona.biosemantics.oto.steps.client.event.processing;

import com.google.gwt.event.shared.GwtEvent;

public class ProcessingStartEvent extends GwtEvent<ProcessingStartEventHandler> {

	public static Type<ProcessingStartEventHandler> TYPE = new Type<ProcessingStartEventHandler>();

	private String processingMsg;

	public ProcessingStartEvent(String processingMsg) {
		this.setProcessingMsg(processingMsg);
	}

	@Override
	public Type<ProcessingStartEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ProcessingStartEventHandler handler) {
		handler.onProcessingStart(this);
	}

	public String getProcessingMsg() {
		return processingMsg;
	}

	public void setProcessingMsg(String processingMsg) {
		this.processingMsg = processingMsg;
	}

}
