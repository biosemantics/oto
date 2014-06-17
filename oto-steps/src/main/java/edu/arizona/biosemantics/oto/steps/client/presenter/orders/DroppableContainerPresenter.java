package edu.arizona.biosemantics.oto.steps.client.presenter.orders;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

import edu.arizona.biosemantics.oto.steps.client.event.orders.DropTermToBoxEvent;
import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;
import edu.arizona.biosemantics.oto.steps.client.view.orders.DraggableTermView;
import edu.arizona.biosemantics.oto.steps.client.view.orders.DroppableContainerView;
import edu.arizona.biosemantics.oto.steps.client.view.orders.OrderTblView;

public class DroppableContainerPresenter implements Presenter {

	public static interface Display {
		FlowPanel getContainer();

		OrderTblView getParentOrder();

		int getBoxIndex();

		ArrayList<String> getTermsInBox();

		void addTermToBox(String termName);

		void removeTermFromBox(String termName);

		Widget asWidget();
	}

	private final Display display;
	private HandlerManager eventBus;
	private final HandlerManager globalEventBus;

	public DroppableContainerPresenter(Display display,
			HandlerManager eventBus, HandlerManager globalEventBus) {
		this.display = display;
		this.eventBus = eventBus;
		this.globalEventBus = globalEventBus;
		bindEvents();
		addTermsToBox();
	}

	private void addTermsToBox() {
		for (String term : display.getTermsInBox()) {
			new DraggableTermPresenter(new DraggableTermView(term, false,
					display.getParentOrder()), eventBus, globalEventBus)
					.go(display.getContainer());
			display.getParentOrder().addTerm(term);
		}
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
	}

	public void goWithFlexTable(FlexTable table, int row, int column) {
		table.setWidget(row, column, display.asWidget());
		CellFormatter cellFormatter = table.getCellFormatter();
		cellFormatter.addStyleName(row, column, "ORDERS_order_box_cell");
	}

	@Override
	public void bindEvents() {
		display.getContainer().addDomHandler(new DragOverHandler() {

			@Override
			public void onDragOver(DragOverEvent event) {
				// onDragOver is required here
				display.getContainer().addStyleName("ORDERS_drag_over");
			}
		}, DragOverEvent.getType());

		display.getContainer().addDomHandler(new DragLeaveHandler() {

			@Override
			public void onDragLeave(DragLeaveEvent event) {
				display.getContainer().removeStyleName("ORDERS_drag_over");
			}
		}, DragLeaveEvent.getType());

		display.getContainer().addDomHandler(new DropHandler() {

			@Override
			public void onDrop(DropEvent event) {
				display.getContainer().removeStyleName("ORDERS_drag_over");
				eventBus.fireEvent(new DropTermToBoxEvent(
						(DroppableContainerView) display));
			}
		}, DropEvent.getType());
	}

}
