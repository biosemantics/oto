package edu.arizona.biosemantics.oto.steps.client.presenter.processing;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.event.processing.ProcessingEndEvent;
import edu.arizona.biosemantics.oto.steps.client.event.processing.ProcessingEndEventHandler;
import edu.arizona.biosemantics.oto.steps.client.event.processing.ProcessingStartEvent;
import edu.arizona.biosemantics.oto.steps.client.event.processing.ProcessingStartEventHandler;
import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;

public class ProcessingMsgPresenter implements Presenter {

	public static interface Display {
		void hideMsgPanel();

		void displayMsg(String text);

		Widget asWidget();
	}

	private final Display display;
	private final HandlerManager globalEventBus;

	public ProcessingMsgPresenter(Display display, HandlerManager globalEventBus) {
		this.display = display;
		this.globalEventBus = globalEventBus;
	}

	@Override
	public void go(HasWidgets container) {
		//container.add(display.asWidget());
		bindEvents();
	}

	@Override
	public void bindEvents() {
		globalEventBus.addHandler(ProcessingStartEvent.TYPE,
				new ProcessingStartEventHandler() {

					@Override
					public void onProcessingStart(ProcessingStartEvent event) {
						display.displayMsg(event.getProcessingMsg());
					}
				});

		globalEventBus.addHandler(ProcessingEndEvent.TYPE,
				new ProcessingEndEventHandler() {

					@Override
					public void onProcessingEnd(ProcessingEndEvent event) {
						display.hideMsgPanel();
					}
				});

	}

}
