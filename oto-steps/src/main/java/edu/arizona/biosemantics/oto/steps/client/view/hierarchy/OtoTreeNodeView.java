package edu.arizona.biosemantics.oto.steps.client.view.hierarchy;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;

import edu.arizona.biosemantics.oto.steps.client.presenter.hierarchy.OtoTreeNodePresenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.hierarchy.Structure;

public class OtoTreeNodeView extends Composite implements
		OtoTreeNodePresenter.Display {

	private HorizontalPanel layout;
	private Structure data;
	private Image deleteIcon;
	private Image addChildIcon;
	private Label termLbl;
	private TreeItem treeItem;

	public OtoTreeNodeView(Structure data, boolean toSave) {
		this.data = data;

		layout = new HorizontalPanel();
		initWidget(layout);
		layout.setSpacing(5);
		layout.addStyleName("HIERARCHY_tree_node");
		layout.getElement().setAttribute("term_id", data.getId());
		layout.getElement().setAttribute("term_name", data.getTermName());

		termLbl = new Label(data.getTermName());
		if (toSave) {
			termLbl.addStyleName("to_save");
		}
		layout.add(termLbl);

		addChildIcon = new Image("images/add_child.png");
		addChildIcon.setHeight("14px");
		layout.add(addChildIcon);
		addChildIcon.setVisible(false);
		addChildIcon
				.setTitle("Add a new structure term as a child of this node. ");

		deleteIcon = new Image("images/cross.png");
		deleteIcon.setHeight("12px");
		layout.add(deleteIcon);
		deleteIcon.setVisible(false);
		deleteIcon
				.setTitle("Delete this node and its children nodes from tree.");
	}

	@Override
	public Structure getNodeData() {
		return data;
	}

	@Override
	public Image getDeleteIcon() {
		return deleteIcon;
	}

	@Override
	public Image getAddNodeIcon() {
		return addChildIcon;
	}

	@Override
	public Label getTermLbl() {
		return termLbl;
	}

	@Override
	public HorizontalPanel getLayout() {
		return layout;
	}

	@Override
	public TreeItem getTreeItem() {
		return treeItem;
	}

	@Override
	public void setTreeItem(TreeItem treeItem) {
		this.treeItem = treeItem;
	}

}
