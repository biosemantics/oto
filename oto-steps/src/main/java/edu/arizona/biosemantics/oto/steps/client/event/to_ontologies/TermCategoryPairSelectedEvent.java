package edu.arizona.biosemantics.oto.steps.client.event.to_ontologies;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.TermCategoryPair;

public class TermCategoryPairSelectedEvent extends
		GwtEvent<TermCategoryPairSelectedEventHandler> {

	public static Type<TermCategoryPairSelectedEventHandler> TYPE = new Type<TermCategoryPairSelectedEventHandler>();
	private TermCategoryPair data;
	private Widget widget;

	public TermCategoryPairSelectedEvent(TermCategoryPair data, Widget widget) {
		this.data = data;
		this.setWidget(widget);
	}

	@Override
	public Type<TermCategoryPairSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TermCategoryPairSelectedEventHandler handler) {
		handler.onTermCategoryPairSelected(this);
	}

	public TermCategoryPair getData() {
		return data;
	}

	public void setData(TermCategoryPair data) {
		this.data = data;
	}

	public Widget getWidget() {
		return widget;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

}
