package edu.arizona.biosemantics.oto.steps.client.event.hierarchy;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.TreeItem;

public class DragTreeNodeStartEvent extends
		GwtEvent<DragTreeNodeStartEventHandler> {
	public static Type<DragTreeNodeStartEventHandler> TYPE = new Type<DragTreeNodeStartEventHandler>();

	private TreeItem treeNode;

	public DragTreeNodeStartEvent(TreeItem treeNode) {
		this.treeNode = treeNode;
	}

	@Override
	public Type<DragTreeNodeStartEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DragTreeNodeStartEventHandler handler) {
		handler.onDragStart(this);
	}

	public TreeItem getTreeNode() {
		return treeNode;
	}

	public void setTreeNode(TreeItem treeNode) {
		this.treeNode = treeNode;
	}

}
