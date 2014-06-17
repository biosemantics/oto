package edu.arizona.biosemantics.oto.steps.client.presenter.hierarchy;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.event.context.ViewTermInfoEvent;
import edu.arizona.biosemantics.oto.steps.client.event.hierarchy.DragStructureStartEvent;
import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.hierarchy.Structure;

public class StructureTermPresenter implements Presenter {
	public static interface Display {
		Label getStructureLabel();

		Structure getData();

		Widget asWidget();
	}

	private final Display display;
	private final HandlerManager globalEventBus;
	private final HandlerManager eventBus;

	public StructureTermPresenter(Display display,
			HandlerManager globalEventBus, HandlerManager eventBus) {
		this.display = display;
		this.globalEventBus = globalEventBus;
		this.eventBus = eventBus;
		bindEvents();
	}

	@Override
	public void go(HasWidgets container) {
		container.add(display.asWidget());

	}

	@Override
	public void bindEvents() {
		// when clicked, display term info
		display.getStructureLabel().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				globalEventBus.fireEvent(new ViewTermInfoEvent(display
						.getData().getTermName()));

			}
		});

		// structure label should be draggable
		display.getStructureLabel().addDragStartHandler(new DragStartHandler() {

			@Override
			public void onDragStart(DragStartEvent event) {
				// required for firefox
				event.setData("dummyData", "dummyData");
				eventBus.fireEvent(new DragStructureStartEvent(display
						.asWidget()));
				globalEventBus.fireEvent(new ViewTermInfoEvent(display
						.getData().getTermName()));
			}
		});

		display.getStructureLabel().addDragEndHandler(new DragEndHandler() {

			@Override
			public void onDragEnd(DragEndEvent event) {
				// TODO Auto-generated method stub

			}
		});
	}
}
