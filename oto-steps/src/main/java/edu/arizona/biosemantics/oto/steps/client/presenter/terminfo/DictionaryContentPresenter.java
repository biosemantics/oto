package edu.arizona.biosemantics.oto.steps.client.presenter.terminfo;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.terminfo.TermDictionary;

public class DictionaryContentPresenter implements Presenter {

	public static interface Display {
		CellTable<TermDictionary> getTbl();

		Widget asWidget();
	}

	private final Display display;

	public DictionaryContentPresenter(Display display) {
		this.display = display;
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
		bindEvents();
	}

	@Override
	public void bindEvents() {
		// TODO Auto-generated method stub

	}
}
