package edu.arizona.biosemantics.oto.steps.client.presenter.toontologies;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.DeleteSubmissionEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.EditSubmissionEvent;
import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologySubmission;

public class SubmissionDetailPresenter implements Presenter {

	public static interface Display {
		OntologySubmission getData();

		Button getEditBtn();

		Button getDeleteBtn();

		Widget asWidget();
	}

	private final Display display;
	private final HandlerManager eventBus;

	public SubmissionDetailPresenter(Display display, HandlerManager eventBus) {
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
		display.getEditBtn().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new EditSubmissionEvent(display.getData()));
			}
		});

		display.getDeleteBtn().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new DeleteSubmissionEvent(display.getData()));
			}
		});

	}

}
