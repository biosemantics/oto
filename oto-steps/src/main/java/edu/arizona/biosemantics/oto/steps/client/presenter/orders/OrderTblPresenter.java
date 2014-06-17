package edu.arizona.biosemantics.oto.steps.client.presenter.orders;

import java.util.ArrayList;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;
import edu.arizona.biosemantics.oto.steps.client.view.orders.DroppableContainerView;
import edu.arizona.biosemantics.oto.steps.client.view.orders.OrderTblView;
import edu.arizona.biosemantics.oto.steps.shared.beans.orders.Order;
import edu.arizona.biosemantics.oto.steps.shared.beans.orders.TermInOrder;

public class OrderTblPresenter implements Presenter {

	public static interface Display {
		Widget asWidget();

		// terms in this order
		ArrayList<String> getTermsList();

		void removeTerm(String term);

		void addTerm(String term);

		ArrayList<DroppableContainerView> getBoxes();

		void addBoxToList(DroppableContainerView box);

		boolean isTermExist(String term);

		Order getData();

		int getNumBoxes();

		FlexTable getOrderTbl();
	}

	private final Display display;
	private final HandlerManager eventBus;
	private final HandlerManager globalEventBus;

	public OrderTblPresenter(Display display, HandlerManager eventBus,
			HandlerManager globalEventBus) {
		this.display = display;
		this.eventBus = eventBus;
		this.globalEventBus = globalEventBus;
		bindEvents();
		addBoxesWithData();
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
	}

	public void goWithFlexTable(FlexTable table, int row, int column) {
		table.setWidget(row, column, display.asWidget());
	}

	@Override
	public void bindEvents() {
		// TODO Auto-generated method stub

	}

	private void addBoxesWithData() {
		for (int boxIndex = 0; boxIndex < display.getNumBoxes(); boxIndex++) {
			ArrayList<TermInOrder> terms = display.getData().getTermsInOrder();
			// get term list for this box
			ArrayList<String> termsInBox = new ArrayList<String>();
			if (terms != null) {
				for (TermInOrder term : terms) {
					if (term.getPosition() == boxIndex) {
						termsInBox.add(term.getTermName());
					}
				}
			}

			// create the box and fill in the table
			DroppableContainerView box = new DroppableContainerView(boxIndex,
					termsInBox, (OrderTblView) display);
			new DroppableContainerPresenter(box, eventBus, globalEventBus)
					.goWithFlexTable(display.getOrderTbl(), 0, boxIndex);
			display.addBoxToList(box);
		}
	}
}
