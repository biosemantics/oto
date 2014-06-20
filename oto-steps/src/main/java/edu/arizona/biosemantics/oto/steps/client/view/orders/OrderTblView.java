package edu.arizona.biosemantics.oto.steps.client.view.orders;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import edu.arizona.biosemantics.oto.steps.client.presenter.orders.OrderTblPresenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.orders.Order;

public class OrderTblView extends Composite implements
		OrderTblPresenter.Display {

	private ArrayList<String> termList = new ArrayList<String>();
	private ArrayList<DroppableContainerView> boxes = new ArrayList<DroppableContainerView>();
	private FlexTable layout;
	private Order order;
	private int numBoxes;

	public OrderTblView(Order order, int numTerms) {
		this.order = order;
		this.numBoxes = numTerms;
		layout = new FlexTable();
		initWidget(layout);
	}

	@Override
	public ArrayList<String> getTermsList() {
		return termList;
	}

	@Override
	public ArrayList<DroppableContainerView> getBoxes() {
		return boxes;
	}

	@Override
	public void removeTerm(String term) {
		if (termList.indexOf(term) >= 0) {
			termList.remove(term);
		}

	}

	@Override
	public void addTerm(String term) {
		if (termList.indexOf(term) < 0) {
			termList.add(term);
		}
	}

	@Override
	public void addBoxToList(DroppableContainerView box) {
		boxes.add(box);

	}

	@Override
	public boolean isTermExist(String term) {
		return termList.indexOf(term) >= 0;
	}

	@Override
	public Order getData() {
		return order;
	}

	@Override
	public int getNumBoxes() {
		return numBoxes;
	}

	@Override
	public FlexTable getOrderTbl() {
		return layout;
	}

}
