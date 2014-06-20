package edu.arizona.biosemantics.oto.steps.client.widget.presenter;

import static com.google.gwt.query.client.GQuery.$;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;

public class OtoTabPanelPresenter implements Presenter {
	public static interface Display {

		void setSize(String width, String height);

		void setContentSize(String width, String height);

		void setContentWidth(String width);

		void setContentHeight(String height);

		int getCurrentTabIndex();

		void setCurrentTabIndex(int index);

		Label addTab(String text);

		void setContent(Widget widget);

		LayoutPanel getContentPanel();

		HorizontalPanel getTabCardsPanel();

		Widget asWidget();
	}

	private final Display display;
	private OtoTabPanelTabSelectionHandler selectTabCallback;

	public OtoTabPanelPresenter(Display display, ArrayList<String> tabNames) {
		this.display = display;
		initializeTabs(tabNames);
	}

	public void setSelectTabCallback(OtoTabPanelTabSelectionHandler callback) {
		this.selectTabCallback = callback;
	}

	/**
	 * according to index, get the label and do switch tab
	 * 
	 * @param index
	 */
	public void selectTab(int index) {
		if (display.getCurrentTabIndex() == index) {
			return;
		}

		display.setCurrentTabIndex(index);
		GQuery tabs = $(display.getTabCardsPanel().getElement()).find(
				"[tab_index='" + Integer.toString(index) + "']");
		if (tabs.length() > 0) {
			Element tab = (Element) tabs.get(0);

			$(display.getTabCardsPanel().getElement()).find(".oto-current-tab")
					.removeClass("oto-current-tab");
			tab.addClassName("oto-current-tab");
			doTabSwitch();
		}
	}

	/**
	 * add tabs and add click handler for each tab name label
	 * 
	 * @param tabNames
	 * @param defaultTabIndex
	 *            : 0 based
	 */
	private void initializeTabs(ArrayList<String> tabNames) {
		for (String tabName : tabNames) {
			final Label tab = display.addTab(tabName);
			tab.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					int tabIndex = Integer.parseInt(tab.getElement()
							.getAttribute("tab_index"));
					selectTab(tabIndex);
				}
			});
		}
	}

	/**
	 * change to a different tab
	 */
	private void doTabSwitch() {
		if (selectTabCallback != null) {
			selectTabCallback.onSelect(display.getCurrentTabIndex());
		}
	}

	@Override
	public void go(HasWidgets container) {
		// not used

	}

	@Override
	public void bindEvents() {
		// not used

	}
}
