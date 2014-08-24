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
				OntologySubmission submission = display.getDataToSubmit();
				String error ="The following requires a value: ";
				if(submission.getTerm().length()==0)
					error += System.getProperty("line.separator", "\n")+"Term, ";
				if(submission.getOntologyID().length()==0)
					error += System.getProperty("line.separator", "\n")+"Target ontology, ";
				if(submission.getEorQ().length()==0)
					error += System.getProperty("line.separator", "\n")+"E or Q, ";
				if(submission.getSubmitAsSynonym() && submission.getClassID().length()==0)
					error += System.getProperty("line.separator", "\n")+"Class ID, ";
				if(!submission.getSubmitAsSynonym() && submission.getClassID().length()==0 && submission.getDefinition().length()==0)
					error += System.getProperty("line.separator", "\n")+"Definition, ";
				if(!submission.getSubmitAsSynonym() && (submission.getClassID()+submission.getSuperClass()+submission.getPartOfClass()).length()==0)
					error += System.getProperty("line.separator", "\n")+"One of Class ID, Superclass, and Part of must have a value, ";
				
				error = error.replaceFirst(",\\s+$", "");
				if(error.compareTo("The following requires a value: ")==0)
					eventBus.fireEvent(new SubmitSubmissionEvent(submission, display.getType()));
				else
					Window.alert(error);
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
