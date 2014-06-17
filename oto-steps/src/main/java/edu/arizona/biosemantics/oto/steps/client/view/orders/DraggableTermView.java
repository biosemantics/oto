package edu.arizona.biosemantics.oto.steps.client.view.orders;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.orders.DraggableTermPresenter;

public class DraggableTermView extends Composite implements
		DraggableTermPresenter.Display {

	private Label termLabel;
	private boolean isFromBase;
	private OrderTblView parentOrder;
	private String termName;

	public static String TAG = "DIV";

	public DraggableTermView(String termName, boolean isFromBase,
			OrderTblView orderTblView) {
		this.termName = termName;
		this.isFromBase = isFromBase;
		this.parentOrder = orderTblView;

		termLabel = new Label(termName);
		termLabel.getElement().setAttribute("term_name", termName);
		initWidget(termLabel);
	}

	@Override
	public Label getTermLabel() {
		return termLabel;
	}

	public Widget asWidget() {
		return this;
	}

	@Override
	public String getTermName() {
		return termName;
	}

	@Override
	public boolean isFromBase() {
		return isFromBase;
	}

	@Override
	public OrderTblView getParentOrder() {
		return parentOrder;
	}
}
