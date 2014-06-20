package edu.arizona.biosemantics.oto.steps.client.widget;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.widget.presenter.OtoTabPanelPresenter;
import edu.arizona.biosemantics.oto.steps.client.widget.presenter.OtoTabPanelTabSelectionHandler;
import edu.arizona.biosemantics.oto.steps.client.widget.view.OtoTabPanelView;

public class OtoTabPanel {
	private OtoTabPanelView view;
	private OtoTabPanelPresenter presenter;

	public OtoTabPanel(ArrayList<String> tabNames) {
		view = new OtoTabPanelView();
		presenter = new OtoTabPanelPresenter(view, tabNames);
	}

	public Widget asWidget() {
		return this.view;
	}

	public void setSize(String width, String height) {
		view.setSize(width,  height);
	}
	
	/**
	 * put the panel into a container
	 * 
	 * @param widget
	 */
	public void placeOtoTabPanel(HasWidgets widget) {
		widget.add(view);
	}

	public void setContentSize(String width, String height) {
		view.setContentSize(width, height);
	}

	public void setContentWidth(String width) {
		view.setContentWidth(width);
	}

	public void setContenHeight(String height) {
		view.setContentHeight(height);
	}

	public void addSelectionHandler(OtoTabPanelTabSelectionHandler callback) {
		presenter.setSelectTabCallback(callback);
	}

	public void selectTab(int index) {
		presenter.selectTab(index);
	}

	public LayoutPanel getContentPanel() {
		return view.getContentPanel();
	}

	public int getCurrentTabIndex() {
		return view.getCurrentTabIndex();
	}
}
