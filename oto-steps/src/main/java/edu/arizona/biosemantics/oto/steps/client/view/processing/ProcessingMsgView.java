package edu.arizona.biosemantics.oto.steps.client.view.processing;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

import edu.arizona.biosemantics.oto.steps.client.presenter.processing.ProcessingMsgPresenter;

public class ProcessingMsgView extends Composite implements
		ProcessingMsgPresenter.Display {
	private PopupPanel msgPanel;

	public ProcessingMsgView() {
		msgPanel = new PopupPanel(false, true);
		msgPanel.hide();
	}

	@Override
	public void hideMsgPanel() {
		msgPanel.hide();
	}

	@Override
	public void displayMsg(String text) {
		msgPanel.setWidget(new Label(text));
		msgPanel.show();
		msgPanel.center();

	}
}
