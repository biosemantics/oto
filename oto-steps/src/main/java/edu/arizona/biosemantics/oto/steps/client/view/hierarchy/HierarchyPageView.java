package edu.arizona.biosemantics.oto.steps.client.view.hierarchy;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.biosemantics.oto.steps.client.presenter.hierarchy.HierarchyPagePresenter;

public class HierarchyPageView extends Composite implements
		HierarchyPagePresenter.Display {

	private VerticalPanel leftListPanel;
	private VerticalPanel treePanel;
	private Tree tree;
	private TreeItem root;
	private Button prepopulateBtn;
	private Button saveBtn;
	private Button resetBtn;
	private LayoutPanel layout;

	public HierarchyPageView() {
		layout = new LayoutPanel();
		initWidget(layout);

		// FlexTable layout = new FlexTable();
		// layout.setSize("100%", "100%");
		// initWidget(layout);

		// titles
		Label title1 = new Label("Structures: ");
		layout.add(title1);
		layout.setWidgetLeftRight(title1, 0, Unit.PCT, 80, Unit.PCT);
		layout.setWidgetTopHeight(title1, 0, Unit.PCT, 37, Unit.PX);
		LayoutPanel.setStyleName(title1.getElement(), "HIERARCHY_title", true);
		LayoutPanel.setStyleName(title1.getElement(),
				"HIERARCHY_title_structure", true);

		FlexTable rightTitleTbl = new FlexTable();
		rightTitleTbl.setText(0, 0, "Hierarchy: ");
		prepopulateBtn = new Button("Prepopulate Tree");
		prepopulateBtn
				.setTitle("Pre-build the tree with part-of relations from Ontology. ");
		resetBtn = new Button("Reset Tree");
		resetBtn.setTitle("Reset the tree to be empty.");
		saveBtn = new Button("Save Tree");
		saveBtn.setTitle("Save the tree to database");
		HorizontalPanel btns = new HorizontalPanel();
		btns.setSpacing(1);
		btns.add(prepopulateBtn);
		btns.add(resetBtn);
		btns.add(saveBtn);
		rightTitleTbl.setWidget(0, 1, btns);
		rightTitleTbl.getColumnFormatter().setWidth(0, "50%");
		rightTitleTbl.getColumnFormatter().setWidth(1, "50%");
		rightTitleTbl.getFlexCellFormatter().setAlignment(0, 1,
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_MIDDLE);
		prepopulateBtn.setVisible(false); // default not visible
		resetBtn.setVisible(false);

		layout.add(rightTitleTbl);
		layout.setWidgetLeftRight(rightTitleTbl, 20.2, Unit.PCT, 0, Unit.PCT);
		layout.setWidgetTopHeight(rightTitleTbl, 0, Unit.PX, 37, Unit.PX);
		LayoutPanel.setStyleName(rightTitleTbl.getElement(), "HIERARCHY_title");

		// left list panel
		ScrollPanel leftPanel = new ScrollPanel();
		leftPanel.addStyleName("HIERARCHY_left_structure_list");
		layout.add(leftPanel);
		layout.setWidgetTopBottom(leftPanel, 38, Unit.PX, 0, Unit.PX);
		layout.setWidgetLeftWidth(leftPanel, 0, Unit.PCT, 20, Unit.PCT);

		leftListPanel = new VerticalPanel();
		leftListPanel.setSize("100%", "100%");
		leftListPanel.setSpacing(5);
		leftPanel.add(leftListPanel);

		// right tree panel
		ScrollPanel rightPanel = new ScrollPanel();
		layout.add(rightPanel);
		layout.setWidgetTopBottom(rightPanel, 38, Unit.PX, 0, Unit.PX);
		layout.setWidgetLeftRight(rightPanel, 20.2, Unit.PCT, 0, Unit.PCT);

		treePanel = new VerticalPanel();
		rightPanel.add(treePanel);

		tree = new Tree();
		treePanel.add(tree);

	}
	
	@Override
	public void setSize(String width, String height) {
		this.layout.setSize(width, height);
	}

	@Override
	public VerticalPanel getStructureListPanel() {
		return leftListPanel;
	}

	@Override
	public VerticalPanel getTreePanel() {
		return treePanel;
	}

	@Override
	public Button getPrepopulateBtn() {
		return prepopulateBtn;
	}

	@Override
	public TreeItem getTreeRoot() {
		return root;
	}

	@Override
	public Tree getTree() {
		return tree;
	}

	@Override
	public void setTreeRoot(TreeItem root) {
		this.root = root;
		tree.addItem(root);

	}

	@Override
	public Button getSaveBtn() {
		return saveBtn;
	}

	@Override
	public Button getResetBtn() {
		return resetBtn;
	}

}
