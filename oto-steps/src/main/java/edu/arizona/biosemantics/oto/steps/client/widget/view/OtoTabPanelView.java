package edu.arizona.biosemantics.oto.steps.client.widget.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.widget.presenter.OtoTabPanelPresenter;

public class OtoTabPanelView extends Composite implements
		OtoTabPanelPresenter.Display {

	private FlexTable layout;
	private int currentTabIndex; // 0-based
	private int numTabs;// how many tabs are there
	private LayoutPanel contentPanel;
	private HorizontalPanel tabCardsPanel;

	public OtoTabPanelView() {
		currentTabIndex = -1;
		numTabs = 0;

		layout = new FlexTable();
		layout.setSize("100%", "100%"); //Hong added 6/18/14
		initWidget(layout);

		// mimic appearance of gwt panel
		tabCardsPanel = new HorizontalPanel();
		layout.setWidget(0, 0, tabCardsPanel);
		layout.getFlexCellFormatter().addStyleName(0, 0, "oto-tab-header");

		// has only one content panel
		contentPanel = new LayoutPanel();
		contentPanel.setSize("100%", "100%");
		layout.setWidget(1, 0, contentPanel);
		contentPanel.addStyleName("oto-tab-content-panel");
		layout.getFlexCellFormatter().setHeight(1, 0, "100%");
		layout.getFlexCellFormatter().setWidth(1, 0, "100%");
	}

	@Override
	public void setSize(String width, String height) {
		layout.setSize(width, height);
	}
	
	@Override
	public int getCurrentTabIndex() {
		return currentTabIndex;
	}

	@Override
	public Label addTab(String tabName) {
		Label newLabel = new Label(tabName);
		tabCardsPanel.add(newLabel);
		newLabel.addStyleName("oto-tab-card");
		numTabs++;
		newLabel.getElement().setAttribute("tab_index",
				Integer.toString(numTabs - 1));
		return newLabel;
	}

	@Override
	public void setContent(Widget widget) {
		contentPanel.add(widget);
	}

	@Override
	public void setContentSize(String width, String height) {
		layout.getFlexCellFormatter().setHeight(1, 0, height);
		layout.getFlexCellFormatter().setWidth(1, 0, width);
	}

	@Override
	public void setContentWidth(String width) {
		layout.getFlexCellFormatter().setWidth(1, 0, width);
	}

	@Override
	public void setContentHeight(String height) {
		layout.getFlexCellFormatter().setHeight(1, 0, height);
	}

	@Override
	public HorizontalPanel getTabCardsPanel() {
		return tabCardsPanel;
	}

	@Override
	public void setCurrentTabIndex(int index) {
		this.currentTabIndex = index;
	}

	@Override
	public LayoutPanel getContentPanel() {
		return contentPanel;
	}

}
