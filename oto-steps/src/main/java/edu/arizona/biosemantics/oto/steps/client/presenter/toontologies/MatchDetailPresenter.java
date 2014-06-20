package edu.arizona.biosemantics.oto.steps.client.presenter.toontologies;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;

public class MatchDetailPresenter implements Presenter {

	public static interface Display {

		Widget asWidget();
	}

	private final Display display;

	public MatchDetailPresenter(Display display) {
		this.display = display;
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		bindEvents();
		container.add(display.asWidget());
	}

	@Override
	public void bindEvents() {
		// TODO Auto-generated method stub

	}

}
