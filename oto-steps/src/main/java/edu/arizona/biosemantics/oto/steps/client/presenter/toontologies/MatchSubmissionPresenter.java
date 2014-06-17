package edu.arizona.biosemantics.oto.steps.client.presenter.toontologies;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.AddNewSubmissionEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.ClearSelectionEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.OntologyRecordSelectChangedEvent;
import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyRecord;

public class MatchSubmissionPresenter implements Presenter {

	public static interface Display {
		CellTable<OntologyRecord> getOntologyRecordTbl();

		OntologyRecord getInitialSelectedRecord();

		CellTable<OntologyRecord> getCellTbl();

		void clearInitialSelected();

		Button getNewSubmissionBtn();

		Button getClearSubmissionBtn();

		SingleSelectionModel<OntologyRecord> getSelectionModel();

		Widget asWidget();
	}

	private final Display display;
	private final HandlerManager eventBus;
	private final String term;
	private final String category;

	public MatchSubmissionPresenter(Display display, HandlerManager eventBus,
			String term, String category) {
		this.display = display;
		this.eventBus = eventBus;
		this.term = term;
		this.category = category;
	}

	@Override
	public void go(HasWidgets container) {
		bindEvents();
		container.clear();
		container.add(display.asWidget());
	}

	@Override
	public void bindEvents() {
		display.getNewSubmissionBtn().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new AddNewSubmissionEvent(term, category));
			}
		});

		display.getSelectionModel().addSelectionChangeHandler(
				new SelectionChangeEvent.Handler() {

					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						OntologyRecord selected = display.getSelectionModel()
								.getSelectedObject();
						eventBus.fireEvent(new OntologyRecordSelectChangedEvent(
								selected));
					}
				});

		display.getClearSubmissionBtn().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new ClearSelectionEvent());
			}
		});
	}
}
