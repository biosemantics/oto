package edu.arizona.biosemantics.oto.steps.client.event.hierarchy;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.TreeItem;

public class DeleteNodeEvent extends GwtEvent<DeleteNodeEventHandler> {

	public static Type<DeleteNodeEventHandler> TYPE = new Type<DeleteNodeEventHandler>();

	private TreeItem node;

	public DeleteNodeEvent(TreeItem node) {
		this.setNode(node);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DeleteNodeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DeleteNodeEventHandler handler) {
		handler.onClick(this);
	}

	public TreeItem getNode() {
		return node;
	}

	public void setNode(TreeItem node) {
		this.node = node;
	}

}
