package edu.arizona.biosemantics.oto.steps.client.view.toontologies;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;

import edu.arizona.biosemantics.oto.steps.client.presenter.toontologies.SubmissionDetailPresenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologySubmission;

public class SubmissionDetailView extends Composite implements
		SubmissionDetailPresenter.Display {
	private OntologySubmission submission;
	private Button editBtn;
	private Button deleteBtn;

	public SubmissionDetailView(OntologySubmission submission,
			boolean hasBioportalInfo) {
		this.submission = submission;
		DecoratorPanel decPanel = new DecoratorPanel();
		decPanel.setSize("100%", "100%");
		initWidget(decPanel);

		FlexTable layout = new FlexTable();
		layout.setSize("100%", "100%");
		decPanel.setWidget(layout);
		FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();
		ColumnFormatter columnFormatter = layout.getColumnFormatter();

		layout.setHTML(0, 0, "Ontology Submission Detail");
		cellFormatter.setColSpan(0, 0, 2);
		cellFormatter.addStyleName(0, 0, "tbl_title");
		columnFormatter.setWidth(0, "30%");
		columnFormatter.setWidth(1, "70%");

		int row = 1;
		boolean accepted = true;
		layout.setHTML(row, 0, "Status: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		if (submission.getPermanentID() == null
				|| submission.getPermanentID().equals("")) {
			accepted = false;
			layout.setHTML(row, 1, "Pending");
		} else {
			layout.setHTML(row, 1, "Accepted");
			cellFormatter.addStyleName(row, 1, "accepted");
		}

		row++;
		layout.setHTML(row, 0, "Term: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setHTML(row, 1, submission.getTerm());

		row++;
		layout.setHTML(row, 0, "Category: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setHTML(row, 1, submission.getCategory());

		row++;
		layout.setHTML(row, 0, "Ontology: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setHTML(row, 1, submission.getOntologyID());

		row++;
		layout.setHTML(row, 0, "Super Class ID: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setHTML(row, 1, submission.getSuperClass());

		row++;
		layout.setHTML(row, 0, "Definition: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setHTML(row, 1, submission.getDefinition());

		row++;
		layout.setHTML(row, 0, "Local UUID: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setHTML(row, 1, submission.getLocalID());

		row++;
		layout.setHTML(row, 0, "Temporary ID: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setHTML(row, 1, submission.getTmpID());

		if (accepted) {
			row++;
			layout.setHTML(row, 0, "Permanent ID: ");
			cellFormatter.addStyleName(row, 0, "tbl_field_label");
			layout.setHTML(row, 1, submission.getDefinition());
		}

		row++;
		layout.setHTML(row, 0, "Source: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setHTML(row, 1, submission.getSource());

		row++;
		layout.setHTML(row, 0, "Sample Sentence: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setHTML(row, 1, submission.getSampleSentence());

		row++;
		layout.setHTML(row, 0, "Submitted By: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setHTML(row, 1, submission.getSubmittedBy());

		row++;
		HorizontalPanel btnRow = new HorizontalPanel();
		editBtn = new Button("Edit");
		btnRow.add(editBtn);
		deleteBtn = new Button("Delete");
		btnRow.add(deleteBtn);

		if (hasBioportalInfo) {
			layout.setWidget(row, 0, btnRow);
			cellFormatter.setColSpan(row, 0, 2);
			cellFormatter.addStyleName(row, 0, "tbl_btn_row");
			btnRow.setSpacing(10);
		}
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public OntologySubmission getData() {
		return this.submission;
	}

	@Override
	public Button getEditBtn() {
		return this.editBtn;
	}

	@Override
	public Button getDeleteBtn() {
		return this.deleteBtn;
	}

}
