package edu.arizona.biosemantics.oto.steps.client.presenter.toontologies;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.BackToDetailViewEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.SubmitSubmissionEvent;
import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;
import edu.arizona.biosemantics.oto.steps.client.view.toontologies.OperationType;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologySubmission;

public class EditSubmissionPresenter implements Presenter {
	public static interface Display {
		OperationType getType();
		Button getSubmitBtn();
		Button getBackBtn();
		String getOntologyValue();
		OntologySubmission getDataToSubmit();
		OntologySubmission getOriginalData();
		Widget asWidget();
		//Button getUpdateLocalBtn();
		Button getBrowseOntologyIcon1();
		Button getBrowseOntologyIcon2();
	}

	private final Display display;
	private final HandlerManager eventBus;

	public EditSubmissionPresenter(Display display, HandlerManager eventBus) {
		this.display = display;
		this.eventBus = eventBus;
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		bindEvents();
		container.add(display.asWidget());
	}

	@Override
	public void bindEvents() {
		display.getBackBtn().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new BackToDetailViewEvent(display
						.getOriginalData()));
			}
		});

		display.getSubmitBtn().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new SubmitSubmissionEvent(display
						.getDataToSubmit(), display.getType()));
			}
		});
		
		/*display.getUpdateLocalBtn().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new UpdateLocalEvent());
			}
		});*/
		
		display.getBrowseOntologyIcon1().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Window.open(
						BrowseSuperClassURL.get(display.getOntologyValue()),
						"_blank", "");
			}
		});
		
		display.getBrowseOntologyIcon2().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Window.open(
						BrowseSuperClassURL.get(display.getOntologyValue()),
						"_blank", "");
			}
		});
	}
	
	

}
