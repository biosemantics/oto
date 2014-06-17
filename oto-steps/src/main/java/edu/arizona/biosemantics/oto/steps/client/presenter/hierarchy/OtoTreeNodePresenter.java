package edu.arizona.biosemantics.oto.steps.client.presenter.hierarchy;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.event.context.ViewTermInfoEvent;
import edu.arizona.biosemantics.oto.steps.client.event.hierarchy.AddStructureAsChildEvent;
import edu.arizona.biosemantics.oto.steps.client.event.hierarchy.DeleteNodeEvent;
import edu.arizona.biosemantics.oto.steps.client.event.hierarchy.DragTreeNodeStartEvent;
import edu.arizona.biosemantics.oto.steps.client.event.hierarchy.DropOnToTreeNodeEvent;
import edu.arizona.biosemantics.oto.steps.client.event.hierarchy.SetCopyDragEvent;
import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;
import edu.arizona.biosemantics.oto.steps.client.widget.OtoDialog;
import edu.arizona.biosemantics.oto.steps.client.widget.presenter.ConfirmDialogCallback;
import edu.arizona.biosemantics.oto.steps.shared.beans.hierarchy.Structure;

public class OtoTreeNodePresenter implements Presenter {
	public static interface Display {
		HorizontalPanel getLayout();

		Structure getNodeData();

		Image getDeleteIcon();

		Image getAddNodeIcon();

		Label getTermLbl();

		TreeItem getTreeItem();

		void setTreeItem(TreeItem treeItem);

		Widget asWidget();
	}

	private final Display display;
	private final HandlerManager globalEventBus;
	private final HandlerManager eventBus;
	private boolean isRoot;

	public OtoTreeNodePresenter(Display display, HandlerManager globalEventBus,
			HandlerManager eventBus) {
		this.display = display;
		this.globalEventBus = globalEventBus;
		this.eventBus = eventBus;
	}

	@Override
	public void go(HasWidgets container) {
		//
	}

	public TreeItem addRoot(Tree tree) {
		tree.clear();
		TreeItem root = new TreeItem(display.asWidget());
		display.setTreeItem(root);
		tree.addItem(root);
		isRoot = true;
		bindEvents();
		return root;
	}

	public TreeItem addChild(TreeItem parentNode) {
		TreeItem node = new TreeItem(display.asWidget());
		display.setTreeItem(node);
		parentNode.addItem(node);
		isRoot = false;
		bindEvents();
		return node;
	}

	@Override
	public void bindEvents() {
		// when mouse over, display action icons
		display.getLayout().addDomHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				if (!isRoot) {
					display.getDeleteIcon().setVisible(true);
				}
				display.getAddNodeIcon().setVisible(true);
			}
		}, MouseOverEvent.getType());

		// when mouse out, hide action icons
		display.getLayout().addDomHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				display.getDeleteIcon().setVisible(false);
				display.getAddNodeIcon().setVisible(false);
			}
		}, MouseOutEvent.getType());

		// when clicked, display term info
		display.getTermLbl().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!isRoot) {
					globalEventBus.fireEvent(new ViewTermInfoEvent(display
							.getNodeData().getTermName()));
				}
			}
		});

		display.getTermLbl().addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (event.isControlKeyDown()) {
					eventBus.fireEvent(new SetCopyDragEvent(true));
				} else {
					eventBus.fireEvent(new SetCopyDragEvent(false));
				}
			}
		});

		// remove node from tree
		display.getDeleteIcon().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// confirm if delete node and children
				if (display.getTreeItem().getChildCount() > 0) {
					OtoDialog
							.confirm(
									"Confirm",
									"This node has children. Delete it will also delete all its children. \n"
											+ "Are you sure you want to delete the entire branch? ",
									new ConfirmDialogCallback() {

										@Override
										public void onCancel() {
											// do nothing
										}

										@Override
										public void onAffirmative() {
											eventBus.fireEvent(new DeleteNodeEvent(
													display.getTreeItem()));
										}
									});
				} else {
					eventBus.fireEvent(new DeleteNodeEvent(display
							.getTreeItem()));
				}

			}
		});

		// add a new term to tree
		display.getAddNodeIcon().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new AddStructureAsChildEvent(display
						.getTreeItem()));

			}
		});

		if (!isRoot) {// root is not draggable
			display.getTermLbl().getElement()
					.setDraggable(Element.DRAGGABLE_TRUE);

			// drag start
			display.getTermLbl().addDragStartHandler(new DragStartHandler() {

				@Override
				public void onDragStart(DragStartEvent event) {
					// required for firefox
					event.setData("dummyData", "dummyData");

					eventBus.fireEvent(new DragTreeNodeStartEvent(display
							.getTreeItem()));

					globalEventBus.fireEvent(new ViewTermInfoEvent(display
							.getNodeData().getTermName()));
				}
			});

			// drag end
			display.getTermLbl().addDragEndHandler(new DragEndHandler() {

				@Override
				public void onDragEnd(DragEndEvent event) {
					// TODO Auto-generated method stub

				}
			});
		}

		// drag over
		display.asWidget().addDomHandler(new DragOverHandler() {
			// drag over handler is required here

			@Override
			public void onDragOver(DragOverEvent event) {
				display.getTermLbl().addStyleName("HIERARCHY_node_drag_over");
			}
		}, DragOverEvent.getType());

		// drag leave
		display.asWidget().addDomHandler(new DragLeaveHandler() {

			@Override
			public void onDragLeave(DragLeaveEvent event) {
				display.getTermLbl()
						.removeStyleName("HIERARCHY_node_drag_over");

			}
		}, DragLeaveEvent.getType());

		// on drop
		display.asWidget().addDomHandler(new DropHandler() {

			@Override
			public void onDrop(DropEvent event) {
				// TODO drop source onto this
				display.getTermLbl()
						.removeStyleName("HIERARCHY_node_drag_over");
				TreeItem test = display.getTreeItem();
				eventBus.fireEvent(new DropOnToTreeNodeEvent(test));
			}
		}, DropEvent.getType());
	}

}
