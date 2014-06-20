package edu.arizona.biosemantics.oto.steps.client.event.hierarchy;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.TreeItem;

public class AddStructureAsChildEvent extends
		GwtEvent<AddStructureAsChildEventHandler> {

	public static Type<AddStructureAsChildEventHandler> TYPE = new Type<AddStructureAsChildEventHandler>();

	private TreeItem node;

	public AddStructureAsChildEvent(TreeItem node) {
		this.node = node;
	}

	@Override
	public Type<AddStructureAsChildEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddStructureAsChildEventHandler handler) {
		handler.onClick(this);
	}

	public TreeItem getNode() {
		return node;
	}

	public void setNode(TreeItem node) {
		this.node = node;
	}

}
