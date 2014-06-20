package edu.arizona.biosemantics.oto.steps.client.view.terminfo;

import java.util.ArrayList;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.terminfo.GlossaryContentPresenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.terminfo.TermGlossary;

public class GlossaryContentView extends Composite implements
		GlossaryContentPresenter.Display {

	public GlossaryContentView(ArrayList<TermGlossary> glossaries, String term) {
		if (glossaries.size() > 0) {
			CellTable<TermGlossary> table = new CellTable<TermGlossary>();
			table.setSize("100%", "100%");
			initWidget(table);

			// first column: id
			TextColumn<TermGlossary> idColumn = new TextColumn<TermGlossary>() {

				@Override
				public String getValue(TermGlossary object) {
					return object.getId();
				}
			};
			table.addColumn(idColumn, "ID of '" + term + "'");
			table.setColumnWidth(idColumn, "20%");

			// second column: category
			TextColumn<TermGlossary> categoryColumn = new TextColumn<TermGlossary>() {

				@Override
				public String getValue(TermGlossary object) {
					return object.getCategory();
				}
			};
			table.addColumn(categoryColumn, "Category");
			table.setColumnWidth(categoryColumn, "10%");

			// third column: sentence
			TextColumn<TermGlossary> definitionColumn = new TextColumn<TermGlossary>() {

				@Override
				public String getValue(TermGlossary object) {
					return object.getDefinition();
				}
			};
			table.addColumn(definitionColumn, "Definition of '" + term + "'");
			table.setColumnWidth(definitionColumn, "70%");

			// fill in data
			table.setRowCount(glossaries.size());
			table.setRowData(glossaries);
		} else {
			// label
			Label noRecordLbl = new Label("No glossary record found for term '"
					+ term + "'");
			initWidget(noRecordLbl);
		}
	}

	public Widget asWidget() {
		return this;
	}

}
