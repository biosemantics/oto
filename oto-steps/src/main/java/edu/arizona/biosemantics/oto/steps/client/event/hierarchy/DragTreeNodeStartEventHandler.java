package edu.arizona.biosemantics.oto.steps.client.event.hierarchy;

import com.google.gwt.event.shared.EventHandler;

public interface DragTreeNodeStartEventHandler extends EventHandler {
	void onDragStart(DragTreeNodeStartEvent event);
}
