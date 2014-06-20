package edu.arizona.biosemantics.oto.steps.client.event.orders;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.steps.client.view.orders.DraggableTermView;

public class DragTermEndEvent extends GwtEvent<DragTermEndEventHandler> {

	public static Type<DragTermEndEventHandler> TYPE = new Type<DragTermEndEventHandler>();

	private DraggableTermView widget;

	public DragTermEndEvent(DraggableTermView w) {
		this.setWidget(w);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DragTermEndEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DragTermEndEventHandler handler) {
		handler.onDragTermEnd(this);
	}

	public DraggableTermView getWidget() {
		return widget;
	}

	public void setWidget(DraggableTermView widget) {
		this.widget = widget;
	}

}
