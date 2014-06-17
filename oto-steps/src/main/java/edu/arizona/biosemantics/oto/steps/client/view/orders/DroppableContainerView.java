package edu.arizona.biosemantics.oto.steps.client.view.orders;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.orders.DroppableContainerPresenter;

public class DroppableContainerView extends Composite implements
		DroppableContainerPresenter.Display {
	private FlowPanel panel;
	private int index;
	private ArrayList<String> terms = new ArrayList<String>();
	private OrderTblView parentOrder;

	public DroppableContainerView(int index, ArrayList<String> terms,
			OrderTblView orderTblView) {
		if (terms != null) {
			this.terms = terms;
		}
		this.index = index;
		this.parentOrder = orderTblView;

		panel = new FlowPanel();
		panel.addStyleName("ORDERS_droppable_container");
		initWidget(panel);

	}

	@Override
	public FlowPanel getContainer() {
		return panel;
	}

	public Widget asWidget() {
		return this;
	}

	@Override
	public OrderTblView getParentOrder() {
		return parentOrder;
	}

	@Override
	public int getBoxIndex() {
		return index;
	}

	@Override
	public ArrayList<String> getTermsInBox() {
		return terms;
	}

	@Override
	public void addTermToBox(String termName) {
		if (terms.indexOf(termName) < 0) {
			terms.add(termName);
		}
	}

	@Override
	public void removeTermFromBox(String termName) {
		if (terms.indexOf(termName) >= 0) {
			terms.remove(termName);
		}
	}
}
