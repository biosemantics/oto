package edu.arizona.biosemantics.oto.steps.client.presenter;

import java.util.ArrayList;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.toontologies.ToOntologyPresenter;
import edu.arizona.biosemantics.oto.steps.client.presenter.hierarchy.HierarchyPagePresenter;
import edu.arizona.biosemantics.oto.steps.client.presenter.orders.OrdersPagePresenter;
import edu.arizona.biosemantics.oto.steps.shared.rpc.GeneralService;
import edu.arizona.biosemantics.oto.steps.shared.rpc.GeneralServiceAsync;
import edu.arizona.biosemantics.oto.steps.client.view.hierarchy.HierarchyPageView;
import edu.arizona.biosemantics.oto.steps.client.view.orders.OrdersPageView;
import edu.arizona.biosemantics.oto.steps.client.view.toontologies.ToOntologyView;
import edu.arizona.biosemantics.oto.steps.client.widget.OtoTabPanel;
import edu.arizona.biosemantics.oto.steps.client.widget.presenter.OtoTabPanelTabSelectionHandler;
import edu.arizona.biosemantics.oto.steps.shared.beans.UploadInfo;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.AvailableOntologies;

public class MainPresenter implements Presenter {

	public interface Display {
		OtoTabPanel getTabPanel();

		Widget asWidget();
	}

	private final Display display;
	private final HasWidgets container;
	private HandlerManager globalEventBus;
	private String secret;
	public static String uploadID;
	public static UploadInfo uploadInfo;
	private GeneralServiceAsync rpcService = GWT.create(GeneralService.class);
	public static ArrayList<String> availableOntologies = new ArrayList<String> ();
	
	public MainPresenter(Display view, HandlerManager globalEventBus,
			HasWidgets container) throws Exception {
		this.display = view;
		this.globalEventBus = globalEventBus;
		this.container = container;
		uploadID = Validator.validateUploadID();
		secret = Validator.validateSecret();
		for (AvailableOntologies ont : AvailableOntologies.values()) {
			availableOntologies.add(ont.toString());
		}
	}

	public static void addLocalOntologies(ArrayList<String> localOntoPrefix){
		availableOntologies.addAll(localOntoPrefix);
	}
	
	public void bindEvents() {
		display.getTabPanel().addSelectionHandler(
				new OtoTabPanelTabSelectionHandler() {

					@Override
					public void onSelect(int tabIndex) {
						fillInTabContent();
					}
				});
	}

	private void fillInTabContent() {
		switch (display.getTabPanel().getCurrentTabIndex()) {
		case 0: // to_ontology page
			new ToOntologyPresenter(new ToOntologyView(), globalEventBus)
					.go(display.getTabPanel().getContentPanel());
			break;
		case 1: // hierarchy page
			new HierarchyPagePresenter(new HierarchyPageView(), globalEventBus)
					.go(display.getTabPanel().getContentPanel());
			break;
		case 2:// orders page
			new OrdersPagePresenter(new OrdersPageView(), globalEventBus)
					.go(display.getTabPanel().getContentPanel());
			break;
		default:
			break;
		}
	}

	@Override
	public void go(HasWidgets dummyContainer) {
		fetchUploadInfo();
		container.add(new Label("Loading ..."));
	}

	private void fetchUploadInfo() {
		rpcService.getUploadInfo(uploadID, secret,
				new AsyncCallback<UploadInfo>() {

					@Override
					public void onSuccess(UploadInfo result) {
						uploadInfo = result;
						// get uploadInfo before the page is functional
						MainPresenter.uploadInfo = result;

						bindEvents();
						// select tab has to be after bindEvents()
						display.getTabPanel().selectTab(0);
						container.clear();
						container.add(display.asWidget());
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Server Error: failed in getting upload info. \n\n"
								+ caught.getMessage());
					}
				});
	}

	public UploadInfo getUploadInfo() {
		return uploadInfo;
	}

}
