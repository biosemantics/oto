package edu.arizona.biosemantics.oto.steps.client.presenter.orders;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.event.context.ViewTermInfoEvent;
import edu.arizona.biosemantics.oto.steps.client.event.orders.ClickTermEvent;
import edu.arizona.biosemantics.oto.steps.client.event.orders.DragTermEndEvent;
import edu.arizona.biosemantics.oto.steps.client.event.orders.DragTermStartEvent;
import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;
import edu.arizona.biosemantics.oto.steps.client.view.orders.DraggableTermView;
import edu.arizona.biosemantics.oto.steps.client.view.orders.OrderTblView;

public class DraggableTermPresenter implements Presenter {

	public static interface Display {
		Label getTermLabel();

		String getTermName();

		boolean isFromBase();

		OrderTblView getParentOrder();

		Widget asWidget();
	}

	private final Display display;
	private HandlerManager eventBus;
	private final HandlerManager globalEventBus;

	public DraggableTermPresenter(Display display, HandlerManager eventBus,
			HandlerManager globalEventBus) {
		this.display = display;
		this.eventBus = eventBus;
		this.globalEventBus = globalEventBus;
		bindEvents();
	}

	@Override
	public void go(HasWidgets container) {
		container.add(display.asWidget());
	}

	public void goWithFlexTable(FlexTable table, int row, int column) {
		table.setWidget(row, column, display.asWidget());
	}

	@Override
	public void bindEvents() {
		display.getTermLabel().getElement()
				.setDraggable(Element.DRAGGABLE_TRUE);

		display.getTermLabel().addDragStartHandler(new DragStartHandler() {

			@Override
			public void onDragStart(DragStartEvent event) {
				// has to setData here in order for firefox to work
				event.setData("dummyData", "dummyData");

				eventBus.fireEvent(new DragTermStartEvent(
						(DraggableTermView) display.asWidget()));

			}
		});

		display.getTermLabel().addDragEndHandler(new DragEndHandler() {

			@Override
			public void onDragEnd(DragEndEvent event) {
				eventBus.fireEvent(new DragTermEndEvent(
						(DraggableTermView) display.asWidget()));
			}
		});

		display.getTermLabel().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new ClickTermEvent(display.getTermName()));
				globalEventBus.fireEvent(new ViewTermInfoEvent(display
						.getTermName()));
			}
		});

	}

}
