package edu.arizona.biosemantics.oto.steps.client.view.toontologies;

import java.util.ArrayList;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

import edu.arizona.biosemantics.oto.steps.client.presenter.toontologies.MatchSubmissionPresenter;
import edu.arizona.biosemantics.oto.steps.client.event.to_ontologies.OntologyRecordClickEvent;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyRecord;

public class MatchSubmissionView extends Composite implements
		MatchSubmissionPresenter.Display {
	private CellTable<OntologyRecord> ontologyRecordsTbl;
	private Button newSubmissionBtn;
	private Button clearSelectionBtn;
	private SingleSelectionModel<OntologyRecord> selectionModel;
	private OntologyRecord initialSelected = null;

	public MatchSubmissionView(ArrayList<OntologyRecord> records, String term,
			String category, boolean hasBioportalInfo, HandlerManager eventBus) {
		final HandlerManager myEventBus = eventBus;
		VerticalPanel layout = new VerticalPanel();
		layout.setSize("100%", "100%");
		initWidget(layout);
		final ArrayList<OntologyRecord> ontRecords = records;

		ontologyRecordsTbl = new CellTable<OntologyRecord>();
		selectionModel = new SingleSelectionModel<OntologyRecord>();
		ontologyRecordsTbl.setSelectionModel(selectionModel);

		// matches
		if (ontRecords.size() > 0) {

			// first column: radio btn
			Column<OntologyRecord, Boolean> radioColumn = new Column<OntologyRecord, Boolean>(
					new RadioBtnCell("radioGroup", true, false)) {

				@Override
				public Boolean getValue(OntologyRecord object) {
					return object.isSelected();
				}
			};
			radioColumn
					.setFieldUpdater(new FieldUpdater<OntologyRecord, Boolean>() {

						@Override
						public void update(int index, OntologyRecord object,
								Boolean value) {
							// set all to be false, then set this to be true
							for (OntologyRecord record : ontRecords) {
								record.setSelected(false);
							}

							object.setSelected(true);

							// fire a event to do it
							myEventBus.fireEvent(new OntologyRecordClickEvent(
									object.getId(), object.getType()));
						}
					});
			ontologyRecordsTbl.addColumn(radioColumn, "Select");

			// second column: ontology column
			TextColumn<OntologyRecord> ontologyColumn = new TextColumn<OntologyRecord>() {

				@Override
				public String getValue(OntologyRecord object) {
					return object.getOntology();
				}
			};
			ontologyRecordsTbl.addColumn(ontologyColumn, "Ontology");
			
			// 3th column: definition
			TextColumn<OntologyRecord> definitionColumn = new TextColumn<OntologyRecord>() {

				@Override
				public String getValue(OntologyRecord object) {
					return object.getDefinition();
				}
			};
			ontologyRecordsTbl.addColumn(definitionColumn, "Definition");

			// 4rd column: parent term / super class
			TextColumn<OntologyRecord> parentColumn = new TextColumn<OntologyRecord>() {

				@Override
				public String getValue(OntologyRecord object) {
					return object.getParent();
				}
			};
			ontologyRecordsTbl.addColumn(parentColumn,
					"Superclass");

			// set column width
			ontologyRecordsTbl.setColumnWidth(radioColumn, "10%");
			ontologyRecordsTbl.setColumnWidth(ontologyColumn, "20%");
			ontologyRecordsTbl.setColumnWidth(parentColumn, "20%");
			ontologyRecordsTbl.setColumnWidth(definitionColumn, "50%");

			// set data
			ontologyRecordsTbl.setRowCount(records.size(), true);
			ontologyRecordsTbl.setRowData(0, ontRecords);

			// set selected
			for (OntologyRecord r : ontRecords) {
				if (r.isSelected()) {
					initialSelected = r;
					break;
				}
			}

			if (initialSelected != null) {
				selectionModel.setSelected(initialSelected, true);
			}

			layout.add(ontologyRecordsTbl);

		} else {
			Label label = new Label("'" + term + "' as a "
					+ category + " does not exist in the ontologies");
			label.addStyleName("TO_ONTOLOGY_content_title");
			layout.add(label);
		}

		HorizontalPanel btnRow = new HorizontalPanel();
		btnRow.setSpacing(10);
		layout.add(btnRow);

		// new submission button
		newSubmissionBtn = new Button("Add to Ontologies");
		newSubmissionBtn.addStyleName("center");

		if (hasBioportalInfo) {
			btnRow.add(newSubmissionBtn);
		}

		// clear selected button
		clearSelectionBtn = new Button("Clear Selection");
		if (initialSelected != null) {
			btnRow.add(clearSelectionBtn);
		}
	}

	@Override
	public Button getNewSubmissionBtn() {
		return newSubmissionBtn;
	}

	public Widget asWidget() {
		return this;
	}

	@Override
	public CellTable<OntologyRecord> getOntologyRecordTbl() {
		return ontologyRecordsTbl;
	}

	@Override
	public SingleSelectionModel<OntologyRecord> getSelectionModel() {
		return this.selectionModel;
	}

	@Override
	public OntologyRecord getInitialSelectedRecord() {
		return initialSelected;
	}

	@Override
	public void clearInitialSelected() {
		initialSelected = null;
	}

	@Override
	public CellTable<OntologyRecord> getCellTbl() {
		return this.ontologyRecordsTbl;
	}

	@Override
	public Button getClearSubmissionBtn() {
		return this.clearSelectionBtn;
	}

}
