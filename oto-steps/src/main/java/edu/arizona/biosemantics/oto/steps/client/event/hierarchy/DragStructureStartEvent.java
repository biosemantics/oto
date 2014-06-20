package edu.arizona.biosemantics.oto.steps.client.event.hierarchy;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class DragStructureStartEvent extends
		GwtEvent<DragStructureStartEventHandler> {

	public static Type<DragStructureStartEventHandler> TYPE = new Type<DragStructureStartEventHandler>();

	private Widget structureWidget;

	public DragStructureStartEvent(Widget structureWidget) {
		this.setStructureWidget(structureWidget);
	}

	@Override
	public Type<DragStructureStartEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DragStructureStartEventHandler handler) {
		handler.onDragStart(this);

	}

	public Widget getStructureWidget() {
		return structureWidget;
	}

	public void setStructureWidget(Widget structureWidget) {
		this.structureWidget = structureWidget;
	}

}
