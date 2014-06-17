package edu.arizona.biosemantics.oto.steps.client.widget;

import edu.arizona.biosemantics.oto.steps.client.widget.presenter.AlertDialogCallback;
import edu.arizona.biosemantics.oto.steps.client.widget.presenter.ConfirmBoxPresenter;
import edu.arizona.biosemantics.oto.steps.client.widget.presenter.ConfirmDialogCallback;
import edu.arizona.biosemantics.oto.steps.client.widget.view.ConfirmBoxView;

/**
 * Use this class to replace Window.confirm since Window.confirm causes drag-n-drop
 * problem in firefox
 * 
 * copied from: http://stackoverflow.com/questions/3162399
 * /gwt-confirmation-dialog-box/3163042#3163042
 * 
 */
public class OtoDialog {
	private static ConfirmBoxView view = null;
	private static ConfirmBoxPresenter presenter = null;

	public static ConfirmBoxPresenter confirm(String header, String dialogText,
			String cancelButtonText, String affirmativeButtonText,
			ConfirmDialogCallback callback) {
		view = new ConfirmBoxView();
		presenter = new ConfirmBoxPresenter(view, header, dialogText,
				cancelButtonText, affirmativeButtonText, callback);

		presenter.init();

		return presenter;
	}

	public static ConfirmBoxPresenter confirm(String header, String dialogText,
			ConfirmDialogCallback callback) {
		return OtoDialog.confirm(header, dialogText, "Cancel", "OK", callback);
	}

	public static ConfirmBoxPresenter alert(String header, String dialogText,
			String affirmativeButtonText, AlertDialogCallback callback) {
		view = new ConfirmBoxView();
		presenter = new ConfirmBoxPresenter(view, header, dialogText,
				affirmativeButtonText, callback);

		presenter.init();

		return presenter;
	}

	public static ConfirmBoxPresenter alert(String header, String dialogText,
			AlertDialogCallback callback) {
		return OtoDialog.alert(header, dialogText, "OK", callback);
	}

	protected OtoDialog() {
		//
	}
}
