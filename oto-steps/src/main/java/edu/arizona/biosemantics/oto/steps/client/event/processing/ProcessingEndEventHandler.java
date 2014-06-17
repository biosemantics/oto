package edu.arizona.biosemantics.oto.steps.client.event.processing;

import com.google.gwt.event.shared.EventHandler;

public interface ProcessingEndEventHandler extends EventHandler {
	void onProcessingEnd(ProcessingEndEvent event);

}
