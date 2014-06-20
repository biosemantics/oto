package edu.arizona.biosemantics.oto.steps.client.event.orders;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.steps.client.view.orders.DraggableTermView;

public class DragTermStartEvent extends GwtEvent<DragTermStartEventHandler> {
	public static Type<DragTermStartEventHandler> TYPE = new Type<DragTermStartEventHandler>();

	private DraggableTermView widget;

	public DragTermStartEvent(DraggableTermView w) {
		this.setWidget(w);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DragTermStartEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DragTermStartEventHandler handler) {
		handler.onDragTermStart(this);

	}

	public DraggableTermView getWidget() {
		return widget;
	}

	public void setWidget(DraggableTermView widget) {
		this.widget = widget;
	}

}
