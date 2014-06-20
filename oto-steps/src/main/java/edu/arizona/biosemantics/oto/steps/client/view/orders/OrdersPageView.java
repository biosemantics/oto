package edu.arizona.biosemantics.oto.steps.client.view.orders;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.orders.OrdersPagePresenter;

public class OrdersPageView extends Composite implements
		OrdersPagePresenter.Display {
	private VerticalPanel panel;
	private ScrollPanel layout;

	public OrdersPageView() {
		layout = new ScrollPanel();
		initWidget(layout);
		
		panel = new VerticalPanel();
		panel.setWidth("100%");
		layout.add(panel);
	}
	
	@Override
	public void setSize(String width, String height) {
		this.layout.setSize(width, height);
	}

	@Override
	public VerticalPanel getPanel() {
		return panel;
	}

	@Override
	public Widget asWidget() {
		return this;
	}

}
