package edu.arizona.biosemantics.oto.steps.client.presenter.orders;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;
import edu.arizona.biosemantics.oto.steps.client.rpc.OrderServiceAsync;
import edu.arizona.biosemantics.oto.steps.client.view.orders.OrderSetContentView;
import edu.arizona.biosemantics.oto.steps.client.view.orders.Styles;
import edu.arizona.biosemantics.oto.steps.shared.beans.orders.OrderSet;

public class SingleOrderSetPresenter implements Presenter {

	public static interface Display {
		VerticalPanel getContentPanel();

		DisclosurePanel getDisclosurePanel();

		String getCategoryID();

		Widget asWidget();

	}

	private final Display display;
	private OrderServiceAsync rpcService;
	HandlerRegistration preventClose = null;
	private final HandlerManager globalEventBus;

	public SingleOrderSetPresenter(Display display,
			OrderServiceAsync rpcService, HandlerManager globalEventBus) {
		this.display = display;
		this.rpcService = rpcService;
		this.globalEventBus = globalEventBus;
	}

	@Override
	public void go(HasWidgets container) {
		bindEvents();
		container.add(display.asWidget());
	}

	@Override
	public void bindEvents() {
		display.getDisclosurePanel().addOpenHandler(
				new OpenHandler<DisclosurePanel>() {

					@Override
					public void onOpen(OpenEvent<DisclosurePanel> event) {
						getOrderContent(display.getCategoryID());
						preventClose();
					}
				});
	}

	private void getOrderContent(String categoryID) {
		display.getContentPanel().clear();
		rpcService.getOrderSetByID(categoryID, new AsyncCallback<OrderSet>() {

			@Override
			public void onSuccess(OrderSet result) {
				new OrderSetContentPresenter(new OrderSetContentView(result),
						rpcService, globalEventBus).go(display
						.getContentPanel());
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Server Error: failed to get the detail of this order category. \n\n"
						+ caught.getMessage());
			}
		});
	}

	/**
	 * use addNativePreviewHandler to replace CloseHandler to check if there is
	 * unsaved change
	 */
	private void preventClose() {
		if (preventClose == null) {
			preventClose = Event
					.addNativePreviewHandler(new NativePreviewHandler() {
						@Override
						public void onPreviewNativeEvent(
								NativePreviewEvent event) {
							if (event.getTypeInt() == Event.ONCLICK) {
								// check if clicked_header equal
								// to this disclosure header
								GQuery this_header = GQuery.$(display
										.getDisclosurePanel().getHeader()
										.getElement().getParentElement());
								GQuery clicked_header = GQuery.$(event
										.getNativeEvent().getEventTarget());
								for (int i = 0; i < 5; i++) {
									if (clicked_header.hasClass("header")) {
										break;
									} else {
										clicked_header = clicked_header
												.parent();
									}
								}

								if (clicked_header.toString().equals(
										this_header.toString())) {
									if (GQuery
											.$(display.asWidget().getElement())
											.find("." + Styles.TO_SAVE)
											.length() > 0) {
										if (Window
												.confirm("You have unsaved changes in this order category. "
														+ "You'll lose the changes when you close this category. "
														+ "\n\nDo you want to close anyway? ")) {
											preventClose.removeHandler();
											preventClose = null;
										} else {
											event.cancel();											
										}
									}
								}
							}
						}
					});
		}
	}

}
