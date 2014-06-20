package edu.arizona.biosemantics.oto.steps.client.presenter.toontologies;

import static com.google.gwt.query.client.GQuery.$;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.event.context.ViewTermInfoEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.MoveTermCategoryPairEvent;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.TermCategoryPairSelectedEvent;
import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;
import edu.arizona.biosemantics.oto.steps.client.view.toontologies.TermCategoryPairView;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.TermCategoryPair;

public class TermCategoryPairPresenter implements Presenter {

	public static interface Display {
		TermCategoryPair getTermCategoryPair();

		Image getActionBtn();

		Label getNameLabel();

		Widget asWidget();
	}

	private final Display display;
	private final HandlerManager eventBus;
	private final HandlerManager globalEventBus;

	/**
	 * constructer
	 * 
	 * @param view
	 */
	public TermCategoryPairPresenter(Display view, HandlerManager eventBus,
			HandlerManager globalEventBus) {
		this.display = view;
		this.eventBus = eventBus;
		this.globalEventBus = globalEventBus;
	}

	@Override
	public void go(HasWidgets container) {
		bindEvents();
		// container.clear();
		container.add(display.asWidget());
	}

	@Override
	public void bindEvents() {
		display.getNameLabel().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				$(".TO_ONTOLOGY_currentPair").removeClass(
						"TO_ONTOLOGY_currentPair");
				display.getNameLabel().addStyleName("TO_ONTOLOGY_currentPair");

				eventBus.fireEvent(new TermCategoryPairSelectedEvent(display
						.getTermCategoryPair(), display.getNameLabel()));

				globalEventBus.fireEvent(new ViewTermInfoEvent(display
						.getTermCategoryPair().getTerm()));
			}
		});

		display.getActionBtn().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new MoveTermCategoryPairEvent(display
						.getTermCategoryPair(), (TermCategoryPairView) display));
			}
		});

	}
}
