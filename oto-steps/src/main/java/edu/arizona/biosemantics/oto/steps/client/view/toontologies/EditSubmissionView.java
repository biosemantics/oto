package edu.arizona.biosemantics.oto.steps.client.view.toontologies;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;

import edu.arizona.biosemantics.oto.steps.client.presenter.MainPresenter;
import edu.arizona.biosemantics.oto.steps.client.presenter.toontologies.EditSubmissionPresenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.AvailableOntologies;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologySubmission;

public class EditSubmissionView extends Composite implements
		EditSubmissionPresenter.Display {
	private Button submitBtn;
	private Button backBtn;
	private OperationType type;
	private OntologySubmission submission;

	private TextBox termBox;
	private CheckBox asSynBox;
	private TextBox categoryBox;
	private ListBox ontologyBox;
	private TextBox superClassBox;
	private TextArea definitionArea;
	private TextBox synonymsArea;
	private TextBox sourceBox;
	private TextBox sampleSentenceArea;
	//private Image browserOntologyIcon;
	//private Button updateLocalBtn;
	private TextBox partOfClassBox;
	private TextBox otherIDBox;
	private ListBox localOntologyBox;
	private Button browserOntologyIcon1;
	private Button browserOntologyIcon2;

	public void setDefaultData() {
		synonymsArea.setText(submission.getSynonyms());
		sourceBox.setText(submission.getSource());

		if (type.equals(OperationType.UPDATE_SUBMISSION)) {
			// ontology
			int i = 0;
			//for (AvailableOntologies ont : AvailableOntologies.values()) {  
			for (String ont : MainPresenter.availableOntologies) {
				if (ont.toString().equals(submission.getOntologyID())) {
					ontologyBox.setSelectedIndex(i);
					break;
				}
				i++;
			}

			superClassBox.setText(submission.getSuperClass());
			partOfClassBox.setText(submission.getPartOfClass());
			definitionArea.setText(submission.getDefinition());
			sampleSentenceArea.setText(submission.getSampleSentence());
		}
	}

	public EditSubmissionView(OntologySubmission submission, OperationType type) {
		this.submission = submission;
		this.type = type;

		DecoratorPanel vetPanel = new DecoratorPanel();
		vetPanel.setSize("100%", "100%");
		initWidget(vetPanel);

		FlexTable layout = new FlexTable();
		layout.setSize("100%", "100%");
		vetPanel.add(layout);
		//decPanel.setWidget(layout);
		FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();
		ColumnFormatter columnFormatter = layout.getColumnFormatter();

		if (type.equals(OperationType.NEW_SUBMISSION)) {
			layout.setHTML(0, 0, "Submit Term (use ',' to separate multiple entries)");
		} else {
			layout.setHTML(0, 0, "Edit Term Submission");
		}
		cellFormatter.setColSpan(0, 0, 3);
		cellFormatter.addStyleName(0, 0, "tbl_title");
		columnFormatter.setWidth(0, "30%");
		columnFormatter.setWidth(1, "60%");
		columnFormatter.setWidth(2, "10%");
		columnFormatter.setStyleName(0, "align_right");

		int row = 1;
		//the term to be submitted, if a noun, use singular form, e.g. cauline leaf
		layout.setHTML(row, 0, "*Term  ");
		termBox = new TextBox();
		termBox.setText(submission.getTerm());
		termBox.setHeight("10px");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setWidget(row, 1, termBox);
		
		//synonym submission
		row++;
		asSynBox = new CheckBox();
		layout.setWidget(row, 0, asSynBox);
		layout.setHTML(row, 1, "submit as a synonym");

		row++;
		//etc internal category the term belongs to, e.g. structure
		layout.setHTML(row, 0, "*Category  ");
		categoryBox = new TextBox();
		categoryBox.setText(submission.getCategory());
		categoryBox.setHeight("10px");
		//categoryBox.setEnabled(false);
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setWidget(row, 1, categoryBox);

		row++;
		//an external ontology the term will be submitted to. Optional.
		//to the external ontology, the term is a new term but it could exist in other ontologies.
		ontologyBox = new ListBox();
		ontologyBox.addItem("");
		ontologyBox.setHeight("20px");
		layout.setHTML(row, 0, "Submit to ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label required");
		layout.setWidget(row, 1, ontologyBox);

		row++;
		//an internal ontology the term will be added to. Required.
		//to the internal ontology, the term is a new term but it could exist in other ontologies.
		localOntologyBox = new ListBox();
		localOntologyBox.addItem("");
		localOntologyBox.setHeight("20px");
		layout.setHTML(row, 0, "*Also add to ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label required");
		layout.setWidget(row, 1, localOntologyBox);

		// fill in options
		for (String ont : MainPresenter.availableOntologies) {
			if(ont.startsWith("ETC_")) localOntologyBox.addItem(ont);
			else ontologyBox.addItem(ont);
		}
		
		row++;
		//IDs the term has in 'other' ontologies.
		//this is useful to create xref to 'other' ontologies on the new term
		otherIDBox = new TextBox();
		otherIDBox.setHeight("10px");
		otherIDBox.setWidth("90%");
		Label otherID = new Label("Known class ID if any ");
		otherID.setTitle("fill in the full class ID (IRI) of the term");
		otherID.addStyleName("tbl_field_label");
		layout.setWidget(row, 0, otherID);
		layout.setWidget(row, 1, otherIDBox);

		row++;
		//ID of the superClass for the new term in the target ontology.
		//what if a term is to be submitted to both an external and an internal ontology and their superclasses in two ontologies are different?
		superClassBox = new TextBox();
		superClassBox.setHeight("10px");
		superClassBox.setWidth("90%");
		Label isALbl = new Label("*Is a ");
		isALbl.setTitle("fill in the full class ID (IRI) of the term's superclass");
		isALbl.addStyleName("tbl_field_label");
		layout.setWidget(row, 0, isALbl);
		//cellFormatter.addStyleName(row, 0, "tbl_field_label required");
		layout.setWidget(row, 1, superClassBox);
		browserOntologyIcon1 = new Button(); 
		//new Image("images/locator.png");
		browserOntologyIcon1.setHeight("20px");
		browserOntologyIcon1.addStyleName("TO_ONTOLOGY_search_button");
		layout.setWidget(row, 2, browserOntologyIcon1);

		row++;
		//ID of the parentClass for the new term in the target ontology.
		//what if a term is to be submitted to both an external and an internal ontology and their parentClasses in two ontologies are different?
		partOfClassBox = new TextBox();
		partOfClassBox.setHeight("10px");
		partOfClassBox.setWidth("90%");
		Label partOfLbl = new Label("Part of  ");
		partOfLbl.setTitle("fill in the full class ID (IRI) of which the term is a part");
		partOfLbl.addStyleName("tbl_field_label");
		layout.setWidget(row, 0, partOfLbl);
		//cellFormatter.addStyleName(row, 0, "tbl_field_label required");
		layout.setWidget(row, 1, partOfClassBox);
		browserOntologyIcon2 = new Button(); 
		//new Image("images/locator.png");
		browserOntologyIcon2.setHeight("20px");
		browserOntologyIcon2.addStyleName("TO_ONTOLOGY_search_button");
		layout.setWidget(row, 2, browserOntologyIcon2);
		
		row++;
		//definition of the new term
		definitionArea = new TextArea();
		layout.setHTML(row, 0, "*Definition ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label required");
		layout.setWidget(row, 1, definitionArea);
		definitionArea.setWidth("90%");

		row++;
		//synonyms of the new term
		synonymsArea = new TextBox();
		layout.setHTML(row, 0, "Synonyms  ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setWidget(row, 1, synonymsArea);
		synonymsArea.setWidth("90%");


		/*row++;
		layout.setText(row, 1, "synonyms format: comma separated list");
		cellFormatter.addStyleName(row, 1, "TO_ONTOLOGY_hint");*/

		row++;
		//source document where the term is collected
		sourceBox = new TextBox();
		sourceBox.setHeight("10px");
		layout.setHTML(row, 0, "Source  ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setWidget(row, 1, sourceBox);
		sourceBox.setWidth("90%");

		row++;
		//a sample sentence showing the usage of the new term in the source
		sampleSentenceArea = new TextBox();
		layout.setHTML(row, 0, "Sample Sentence  ");
		cellFormatter.addStyleName(row, 0, "tbl_field_label");
		layout.setWidget(row, 1, sampleSentenceArea);
		sampleSentenceArea.setWidth("90%");

		row++;
		HorizontalPanel btnRow = new HorizontalPanel();
		btnRow.setSpacing(10);
		layout.setWidget(row, 0, btnRow);
		cellFormatter.setColSpan(row, 0, 3);
		//cellFormatter.addStyleName(row, 0, "tbl_btn_row");


		submitBtn = new Button("Submit");
		btnRow.add(submitBtn);
		
		/*updateLocalBtn = new Button("Update Local Ontology");
		updateLocalBtn.setTitle("update any local ontology affected");
		btnRow.add(updateLocalBtn);*/
		
		backBtn = new Button("Back");
		if (type.equals(OperationType.UPDATE_SUBMISSION)) {
			btnRow.add(backBtn);
		}

		//setDefaultData();
		//setDebuggingDataEntity();
		//setDebuggingDataQuality();
		//setDebuggingDataSynonym();
	}

	private void setDebuggingDataQuality() {
		//debugging
		termBox.setText("blue");
		categoryBox.setText("coloration");
		definitionArea.setText("A color hue with low wavelength of that portion of the visible spectrum lying between green and indigo, evoked in the human observer by radiant energy with wavelengths of approximately 420 to 490 nanometers");
		otherIDBox.setText("http://purl.obolirary.org/obo/pato_blue");
		ontologyBox.setSelectedIndex(0);
		localOntologyBox.setSelectedIndex(1);
		sourceBox.setText("fna");
		superClassBox.setText("coloration");;
		synonymsArea.setText("blueish, light blue, dark blue");
		sampleSentenceArea.setText("stems blue");
		
	}

	private void setDebuggingDataEntity() {
		//debugging
		termBox.setText("cauline_leaf");
		categoryBox.setText("structure");
		definitionArea.setText("leaves on stems");
		otherIDBox.setText("http://purl.obolirary.org/po_cauline_leaf");
		ontologyBox.setSelectedIndex(0);
		localOntologyBox.setSelectedIndex(1);
		sourceBox.setText("fna");
		superClassBox.setText("leaf");;
		partOfClassBox.setText("plant");
		synonymsArea.setText("cauline leaves");
		sampleSentenceArea.setText("cauline leaves smaller");
	}
	
	private void setDebuggingDataSynonym() {
		//debugging
		termBox.setText("stems");
		categoryBox.setText("structure");
		definitionArea.setText("a structure");
		otherIDBox.setText("http://purl.obolirary.org/fake_stem, http://purl.obolibrary.org/obo/PO_0009047");
		ontologyBox.setSelectedIndex(0);
		localOntologyBox.setSelectedIndex(1);
		sourceBox.setText("fna");
		synonymsArea.setText("chunks, stalks");
		sampleSentenceArea.setText("stems 2 cm in diameter");
	}

	@Override
	public OperationType getType() {
		return type;
	}

	@Override
	public Button getSubmitBtn() {
		return submitBtn;
	}


	/*@Override
	public Button getUpdateLocalBtn() {
		return updateLocalBtn;
	}*/
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
			data.setSubmissionID(submission.getSubmissionID().trim());
			data.setTmpID(submission.getTmpID().trim());
		}
		
		data.setTerm(termBox.getText().trim());
		data.setSubmitAsSynonym(asSynBox.getValue());
		data.setCategory(categoryBox.getText().trim());
		data.setDefinition(definitionArea.getText().trim());
		data.setOtherID(otherIDBox.getText().trim());
		data.setOntologyID(ontologyBox.getItemText(ontologyBox
				.getSelectedIndex()).trim());
		data.setLocalOntologyID(localOntologyBox.getItemText(localOntologyBox
				.getSelectedIndex()).trim());
		data.setSource(sourceBox.getText().trim());
		data.setSuperClass(superClassBox.getText().trim());
		data.setPartOfClass(partOfClassBox.getText().trim());
		data.setSynonyms(synonymsArea.getText().trim());
		data.setSampleSentence(sampleSentenceArea.getText().trim());
		data.setSubmittedBy("hong"); //TODO: replace hardcoded value with userID variable

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
	public Button getBrowseOntologyIcon1() {
		return browserOntologyIcon1;
	}
	
	@Override
	public Button getBrowseOntologyIcon2() {
		return browserOntologyIcon2;
	}
}
