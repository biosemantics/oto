package edu.arizona.biosemantics.oto.steps.client.widget.view;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.widget.presenter.ConfirmBoxPresenter;

public class ConfirmBoxView extends DialogBox implements
		ConfirmBoxPresenter.Display {
	private Label dialogText;
	private Button affirmativeButton;
	private Button cancelButton;
	private VerticalPanel container;

	public ConfirmBoxView() {
		// init items
		dialogText = new Label();

		affirmativeButton = new Button();
		cancelButton = new Button();

		container = new VerticalPanel();

		setGlassEnabled(true);
		setAnimationEnabled(true);
		setModal(false);

		init();
	}

	private void init() {
		// add items
		container.add(dialogText);

		HorizontalPanel hp = new HorizontalPanel();
		hp.add(affirmativeButton);
		hp.add(cancelButton);
		hp.setSpacing(15);

		container.add(hp);
		this.add(container);
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public Label getDialogText() {
		return dialogText;
	}

	@Override
	public Button getAffirmativeButton() {
		return affirmativeButton;
	}

	@Override
	public Button getCancelButton() {
		return cancelButton;
	}

	@Override
	public void setHeader(String text) {
		this.setText(text);
	}

}
