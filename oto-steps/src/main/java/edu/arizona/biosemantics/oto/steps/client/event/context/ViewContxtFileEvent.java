package edu.arizona.biosemantics.oto.steps.client.event.context;

import com.google.gwt.event.shared.GwtEvent;

public class ViewContxtFileEvent extends GwtEvent<ViewContextFileEventHandler> {
	public static Type<ViewContextFileEventHandler> TYPE = new Type<ViewContextFileEventHandler>();
	private String sourceName;

	public ViewContxtFileEvent(String source) {
		this.setSourceName(source);
	}

	@Override
	public Type<ViewContextFileEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ViewContextFileEventHandler handler) {
		handler.onClick(this);
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

}
