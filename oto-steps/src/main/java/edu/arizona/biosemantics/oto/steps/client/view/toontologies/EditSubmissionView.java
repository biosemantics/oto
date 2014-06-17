package edu.arizona.biosemantics.oto.steps.client.view.toontologies;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;

import edu.arizona.biosemantics.oto.steps.client.presenter.toontologies.EditSubmissionPresenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.AvailableOntologies;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologySubmission;

public class EditSubmissionView extends Composite implements
		EditSubmissionPresenter.Display {
	private Button submitBtn;
	private Button backBtn;
	private OperationType type;
	private OntologySubmission submission;

	private ListBox ontologyBox;
	private TextBox superClassBox;
	private TextArea definitionArea;
	private TextArea synonymsArea;
	private TextBox sourceBox;
	private TextArea sampleSentenceArea;
	private Image browserOntologyIcon;

	public void setDefaultData() {
		synonymsArea.setText(submission.getSynonyms());
		sourceBox.setText(submission.getSource());

		if (type.equals(OperationType.UPDATE_SUBMISSION)) {
			// ontology
			int i = 0;
			for (AvailableOntologies ont : AvailableOntologies.values()) {
				if (ont.toString().equals(submission.getOntologyID())) {
					ontologyBox.setSelectedIndex(i);
					break;
				}
				i++;
			}

			superClassBox.setText(submission.getSuperClass());
			definitionArea.setText(submission.getDefinition());
			sampleSentenceArea.setText(submission.getSampleSentence());
		}
	}

	public EditSubmissionView(OntologySubmission submission, OperationType type) {
		this.submission = submission;
		this.type = type;

		DecoratorPanel decPanel = new DecoratorPanel();
		decPanel.setSize("100%", "100%");
		initWidget(decPanel);

		FlexTable layout = new FlexTable();
		layout.setSize("100%", "100%");
		decPanel.setWidget(layout);
		FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();
		ColumnFormatter columnFormatter = layout.getColumnFormatter();

		if (type.equals(OperationType.NEW_SUBMISSION)) {
			layout.setHTML(0, 0, "New Ontology Submission: ");
		} else {
			layout.setHTML(0, 0, "Edit Ontology Submission: ");
		}
		cellFormatter.setColSpan(0, 0, 3);
		cellFormatter.addStyleName(0, 0, "tbl_title");
		columnFormatter.setWidth(0, "30%");
		columnFormatter.setWidth(1, "60%");
		columnFormatter.setWidth(2, "10%");

		int row = 1;
		layout.setHTML(row, 0, "Term: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setText(row, 1, submission.getTerm());

		row++;
		layout.setHTML(row, 0, "Category: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setText(row, 1, submission.getCategory());

		row++;
		ontologyBox = new ListBox();
		layout.setHTML(row, 0, "Ontology *: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label required");
		layout.setWidget(row, 1, ontologyBox);

		// fill in options
		for (AvailableOntologies ont : AvailableOntologies.values()) {
			ontologyBox.addItem(ont.name());
		}

		row++;
		superClassBox = new TextBox();
		superClassBox.setWidth("90%");
		layout.setHTML(row, 0, "Super Class ID *: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label required");
		layout.setWidget(row, 1, superClassBox);
		browserOntologyIcon = new Image("images/locator.png");
		browserOntologyIcon.setHeight("15px");
		layout.setWidget(row, 2, browserOntologyIcon);

		row++;
		definitionArea = new TextArea();
		layout.setHTML(row, 0, "Definition *: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label required");
		layout.setWidget(row, 1, definitionArea);
		definitionArea.setWidth("90%");

		row++;
		synonymsArea = new TextArea();
		layout.setHTML(row, 0, "Synonyms: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setWidget(row, 1, synonymsArea);
		synonymsArea.setWidth("90%");
		// layout.setHTML(row, 2, "Comma separated list");

		row++;
		layout.setText(row, 1, "synonyms format: comma separated list");
		cellFormatter.addStyleName(row, 1, "TO_ONTOLOGY_hint");

		row++;
		sourceBox = new TextBox();
		layout.setHTML(row, 0, "Source: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setWidget(row, 1, sourceBox);
		sourceBox.setWidth("90%");

		row++;
		sampleSentenceArea = new TextArea();
		layout.setHTML(row, 0, "Sample Sentence: ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setWidget(row, 1, sampleSentenceArea);
		sampleSentenceArea.setWidth("90%");

		row++;
		HorizontalPanel btnRow = new HorizontalPanel();
		btnRow.setSpacing(10);
		layout.setWidget(row, 0, btnRow);
		cellFormatter.setColSpan(row, 0, 3);
		cellFormatter.addStyleName(row, 0, "tbl_btn_row");

		submitBtn = new Button("Submit");
		btnRow.add(submitBtn);

		backBtn = new Button("Back");
		if (type.equals(OperationType.UPDATE_SUBMISSION)) {
			btnRow.add(backBtn);
		}

		setDefaultData();
	}

	@Override
	public OperationType getType() {
		return type;
	}

	@Override
	public Button getSubmitBtn() {
		return submitBtn;
	}

	public Widget asWidget() {
		return this;
	}

	@Override
	public Button getBackBtn() {
		return backBtn;
	}

	@Override
	public OntologySubmission getDataToSubmit() {
		OntologySubmission data = new OntologySubmission();

		if (type.equals(OperationType.UPDATE_SUBMISSION)) {
			data.setSubmissionID(submission.getSubmissionID());
			data.setTmpID(submission.getTmpID());
		}

		data.setTerm(submission.getTerm());
		data.setCategory(submission.getCategory());
		data.setDefinition(definitionArea.getText());
		data.setOntologyID(ontologyBox.getItemText(ontologyBox
				.getSelectedIndex()));
		data.setSource(sourceBox.getText());
		data.setSuperClass(superClassBox.getText());
		data.setSynonyms(synonymsArea.getText());
		data.setSampleSentence(sampleSentenceArea.getText());

		return data;
	}

	@Override
	public OntologySubmission getOriginalData() {
		return this.submission;
	}

	@Override
	public String getOntologyValue() {
		return ontologyBox.getValue(ontologyBox.getSelectedIndex());
	}

	@Override
	public Image getBrowseOntologyIcon() {
		return browserOntologyIcon;
	}
}
