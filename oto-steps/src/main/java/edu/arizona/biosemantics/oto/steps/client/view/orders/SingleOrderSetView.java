package edu.arizona.biosemantics.oto.steps.client.view.orders;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.orders.SingleOrderSetPresenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.orders.OrderCategory;

public class SingleOrderSetView extends Composite implements
		SingleOrderSetPresenter.Display {

	private String categoryID;
	private DisclosurePanel panel;
	private VerticalPanel disclosureContent;

	public SingleOrderSetView(OrderCategory category) {
		this.categoryID = category.getCategoryID();
		panel = new DisclosurePanel(category.getCategoryName());
		panel.setWidth("100%");
		panel.setAnimationEnabled(true);

		disclosureContent = new VerticalPanel();
		disclosureContent.setWidth("100%");
		panel.setContent(disclosureContent);

		initWidget(panel);
	}

	@Override
	public VerticalPanel getContentPanel() {
		return disclosureContent;
	}

	@Override
	public DisclosurePanel getDisclosurePanel() {
		return panel;
	}

	@Override
	public String getCategoryID() {
		return categoryID;
	}

	@Override
	public Widget asWidget() {
		return this;
	}

}
