package edu.arizona.biosemantics.oto.steps.client.presenter.orders;

import java.util.ArrayList;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.MainPresenter;
import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;
import edu.arizona.biosemantics.oto.steps.shared.rpc.OrderService;
import edu.arizona.biosemantics.oto.steps.shared.rpc.OrderServiceAsync;
import edu.arizona.biosemantics.oto.steps.client.view.orders.SingleOrderSetView;
import edu.arizona.biosemantics.oto.steps.shared.beans.orders.OrderCategory;

public class OrdersPagePresenter implements Presenter {

	public static interface Display {
		VerticalPanel getPanel();

		Widget asWidget();
	}

	private final Display display;
	private OrderServiceAsync rpcService = GWT.create(OrderService.class);
	private final HandlerManager globalEventBus;

	public OrdersPagePresenter(Display display, HandlerManager globalEventBus) {
		this.display = display;
		this.globalEventBus = globalEventBus;

	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		bindEvents();
		fetchOrderCategories();
		container.add(display.asWidget());
	}

	@Override
	public void bindEvents() {
		// nothing to bind here
	}

	private void fetchOrderCategories() {
		Label loading = new Label("Loading orders ...");
		display.getPanel().add(loading);

		rpcService.getOrderCategories(MainPresenter.uploadID,
				new AsyncCallback<ArrayList<OrderCategory>>() {

					@Override
					public void onSuccess(ArrayList<OrderCategory> result) {
						display.getPanel().clear();
						for (OrderCategory category : result) {
							new SingleOrderSetPresenter(new SingleOrderSetView(
									category), rpcService, globalEventBus)
									.go(display.getPanel());
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Server Error: failed to get order categories. \n\n"
								+ caught.getMessage());
					}
				});
	}

}
