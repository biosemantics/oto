package edu.arizona.biosemantics.oto.steps.client.event.hierarchy;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.TreeItem;

public class DropOnToTreeNodeEvent extends
		GwtEvent<DropOnToTreeNodeEventHandler> {

	public static Type<DropOnToTreeNodeEventHandler> TYPE = new Type<DropOnToTreeNodeEventHandler>();

	private TreeItem treeNode;

	public DropOnToTreeNodeEvent(TreeItem treeNode) {
		this.treeNode = treeNode;
	}

	@Override
	public Type<DropOnToTreeNodeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DropOnToTreeNodeEventHandler handler) {
		handler.onDrop(this);
	}

	public TreeItem getTreeNode() {
		return treeNode;
	}

	public void setTreeNode(TreeItem treeNode) {
		this.treeNode = treeNode;
	}

}
