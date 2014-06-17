package edu.arizona.biosemantics.oto.steps.client.widget.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.Presenter;

public class ConfirmBoxPresenter implements Presenter {

	public interface Display {

		Label getDialogText();

		Button getAffirmativeButton();

		Button getCancelButton();

		Widget asWidget();

		public void center();

		public void hide();

		public void setHeader(String text);
	}

	private Display display;
	private String header;
	private String dialogText;
	private String cancelButtonText;
	private String affirmativeButtonText;
	private ConfirmDialogCallback confirmCallback;
	private AlertDialogCallback alertCallback;

	public ConfirmBoxPresenter(Display display, String header,
			String dialogText, String cancelButtonText,
			String affirmativeButtonText, ConfirmDialogCallback callback) {
		this.display = display;
		this.header = header;
		this.dialogText = dialogText;
		this.cancelButtonText = cancelButtonText;
		this.affirmativeButtonText = affirmativeButtonText;
		this.confirmCallback = callback;

		bindEvents();
	}

	public ConfirmBoxPresenter(Display display, String header,
			String dialogText, String affirmativeButtonText,
			AlertDialogCallback callback) {
		this.display = display;
		this.header = header;
		this.dialogText = dialogText;
		this.affirmativeButtonText = affirmativeButtonText;
		this.alertCallback = callback;

		this.display.getCancelButton().setVisible(false);

		bindEvents();
	}

	private void addClickHandlers() {
		this.display.getAffirmativeButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doAffirmative();
			}
		});

		this.display.getCancelButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doCancel();
			}
		});
	}

	private void doAffirmative() {
		if (confirmCallback != null) {
			confirmCallback.onAffirmative();
		} else {
			alertCallback.onAffirmative();
		}
		display.hide();
	}

	private void doCancel() {
		confirmCallback.onCancel();
		display.hide();
	}

	public void init() {
		display.center();
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
	}

	@Override
	public void bindEvents() {
		this.display.getDialogText().setText(dialogText);
		this.display.getAffirmativeButton().setText(affirmativeButtonText);
		this.display.getCancelButton().setText(cancelButtonText);
		this.display.setHeader(header);

		addClickHandlers();
	}
}