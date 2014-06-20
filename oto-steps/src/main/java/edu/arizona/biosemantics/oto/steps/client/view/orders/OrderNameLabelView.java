package edu.arizona.biosemantics.oto.steps.client.view.orders;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.orders.OrderNameLabelPresenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.orders.Order;

public class OrderNameLabelView extends Composite implements
		OrderNameLabelPresenter.Display {

	private Label orderNameLabel;
	private OrderTblView orderContent;
	public static String TAG = "div";

	public OrderNameLabelView(Order order, OrderTblView orderContent) {
		this.orderContent = orderContent;
		orderNameLabel = new Label(order.getOrderName() + ": ");
		orderNameLabel.setTitle(order.getOrderDescription());
		orderNameLabel.getElement().setAttribute("order_name",
				order.getOrderName());
		orderNameLabel.getElement()
				.setAttribute("order_id", order.getOrderID());
		orderNameLabel.getElement().setAttribute("order_description",
				order.getOrderDescription());

		orderNameLabel.addStyleName("ORDERS_order_name_label");
		initWidget(orderNameLabel);
	}

	@Override
	public Label getOrderNameLabel() {
		return orderNameLabel;
	}

	public Widget asWidget() {
		return this;
	}

	@Override
	public OrderTblView getOrderContent() {
		return orderContent;
	}
}
