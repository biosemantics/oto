package edu.arizona.biosemantics.oto.steps.client.view.toontologies;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * A {@link Cell} used to render a radio button. The value of the radio button
 * may be toggled using the ENTER key as well as via mouse click.
 */
public class RadioBtnCell extends AbstractEditableCell<Boolean, Boolean> {

	/**
	 * An html string representation of a checked radio button.
	 */
	private static SafeHtml INPUT_CHECKED = SafeHtmlUtils
			.fromSafeConstant("<input type=\"radio\" tabindex=\"-1\" checked=\"checked\"/>");

	/**
	 * An html string representation of an unchecked radio button.
	 */
	private static SafeHtml INPUT_UNCHECKED = SafeHtmlUtils
			.fromSafeConstant("<input type=\"radio\" tabindex=\"-1\"/>");

	private boolean dependsOnSelection, handlesSelection;

	/**
	 * Constructs a new {@link RadioButtonCell} that does not depend on
	 * selection and does not handle selection.
	 */
	public RadioBtnCell() {
		super("change", "keydown");
	}

	/**
	 * Constructs a new {@link RadioButtonCell} that can be configured to depend
	 * and/or handle selection
	 * 
	 * @param groupName
	 *            HTML name attribute of the radiobutton
	 * @param dependsOnSelection
	 *            true if the cell depends on the selection state
	 * @param handlesSelection
	 *            true if the cell modifies the selection state
	 */
	public RadioBtnCell(String groupName, boolean dependsOnSelection,
			boolean handlesSelection) {
		this();
		this.dependsOnSelection = dependsOnSelection;
		this.handlesSelection = handlesSelection;

		INPUT_CHECKED = SafeHtmlUtils
				.fromSafeConstant("<input type=\"radio\" name=\"" + groupName
						+ "\" tabindex=\"-1\" checked=\"checked\"/>");
		INPUT_UNCHECKED = SafeHtmlUtils
				.fromSafeConstant("<input type=\"radio\" name=\"" + groupName
						+ "\" tabindex=\"-1\"/>");
	}

	@Override
	public boolean dependsOnSelection() {
		return dependsOnSelection;
	}

	@Override
	public boolean handlesSelection() {
		return handlesSelection;
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, Boolean value,
			NativeEvent event, ValueUpdater<Boolean> valueUpdater) {
		Object key = context.getKey();
		String type = event.getType();

		boolean enterPressed = "keydown".equals(type)
				&& event.getKeyCode() == KeyCodes.KEY_ENTER;
		if ("change".equals(type) || enterPressed) {
			InputElement input = parent.getFirstChild().cast();
			Boolean isChecked = input.isChecked();

			/**
			 * Check the radio button if the enter key was pressed and the cell
			 * handles selection or doesn't depend on selection. If the cell
			 * depends on selection but doesn't handle selection, then ignore
			 * the enter key and let the SelectionEventManager determine which
			 * keys will trigger a change.
			 */
			if (enterPressed && (handlesSelection() || !dependsOnSelection())) {
				isChecked = true;
				input.setChecked(isChecked);
			}

			/**
			 * Save the new value. However, if the cell depends on the
			 * selection, then do not save the value because we can get into an
			 * inconsistent state.
			 */
			if (value != isChecked && !dependsOnSelection()) {
				setViewData(key, isChecked);
			} else {
				clearViewData(key);
			}

			if (valueUpdater != null) {
				valueUpdater.update(isChecked);
			}
		}
	}

	@Override
	public boolean isEditing(Context context, Element parent, Boolean value) {
		return false;
	}

	@Override
	public void render(Context context, Boolean value, SafeHtmlBuilder sb) {
		Object key = context.getKey();
		Boolean viewData = getViewData(key);
		if (viewData != null && viewData.equals(value)) {
			clearViewData(key);
			viewData = null;
		}

		if (value != null && ((viewData != null) ? viewData : value)) {
			sb.append(INPUT_CHECKED);
		} else {
			sb.append(INPUT_UNCHECKED);
		}
	}

}