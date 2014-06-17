package edu.arizona.biosemantics.oto.steps.client.event.hierarchy;

import com.google.gwt.event.shared.GwtEvent;

public class SetCopyDragEvent extends GwtEvent<SetCopyDragEventHandler> {

	public static Type<SetCopyDragEventHandler> TYPE = new Type<SetCopyDragEventHandler>();

	private boolean isCopyDrag;

	public SetCopyDragEvent(boolean isCopyDrag) {
		this.setCopyDrag(isCopyDrag);
	}

	@Override
	public Type<SetCopyDragEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetCopyDragEventHandler handler) {
		handler.onSetCopyDrag(this);
	}

	public boolean isCopyDrag() {
		return isCopyDrag;
	}

	public void setCopyDrag(boolean isCopyDrag) {
		this.isCopyDrag = isCopyDrag;
	}

}
