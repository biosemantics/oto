package edu.arizona.biosemantics.oto.steps.client.presenter.orders;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.event.orders.ClickOrderNameEvent;
import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;
import edu.arizona.biosemantics.oto.steps.client.view.orders.OrderTblView;

public class OrderNameLabelPresenter implements Presenter {

	public static interface Display {
		Label getOrderNameLabel();

		OrderTblView getOrderContent();

		Widget asWidget();
	}

	private Display display;
	private HandlerManager eventBus;

	public OrderNameLabelPresenter(Display display, HandlerManager eventBus) {
		this.display = display;
		this.eventBus = eventBus;

		bindEvents();
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
	}

	public void goWithFlexTable(FlexTable table, int row, int column) {
		table.setWidget(row, column, display.asWidget());
		table.getCellFormatter().addStyleName(row, column,
				"ORDERS_order_name_cell");
	}

	@Override
	public void bindEvents() {
		display.getOrderNameLabel().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new ClickOrderNameEvent(display
						.getOrderContent().getTermsList()));
			}
		});
	}

}
