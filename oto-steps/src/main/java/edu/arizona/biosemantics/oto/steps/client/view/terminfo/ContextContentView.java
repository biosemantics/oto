package edu.arizona.biosemantics.oto.steps.client.view.terminfo;

import java.util.ArrayList;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.event.context.ViewContxtFileEvent;
import edu.arizona.biosemantics.oto.steps.client.presenter.terminfo.ContextContentPresenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.terminfo.TermContext;

public class ContextContentView extends Composite implements
		ContextContentPresenter.Display {

	public ContextContentView(ArrayList<TermContext> contexts, String term,
			final HandlerManager globalEventBus) {

		if (contexts.size() > 0) {
			ScrollPanel layout = new ScrollPanel();
			layout.setSize("100%", "100%");
			initWidget(layout);

			CellTable<TermContext> table = new CellTable<TermContext>();
			table.setSize("100%", "100%");
			layout.add(table);

			// first column: source
			Column<TermContext, String> sourceColumn = new Column<TermContext, String>(
					new ClickableTextCell()) {

				@Override
				public String getValue(TermContext object) {
					return object.getSource();
				}
			};
			
			sourceColumn.setCellStyleNames("Clickable-context-filename");

			sourceColumn
					.setFieldUpdater(new FieldUpdater<TermContext, String>() {

						@Override
						public void update(int index, TermContext object,
								String value) {
							globalEventBus.fireEvent(new ViewContxtFileEvent(object
									.getSource()));
						}
					});

			table.addColumn(sourceColumn, "Source of '" + term + "'");
			table.setColumnWidth(sourceColumn, "20%");

			// second column: sentence
			TextColumn<TermContext> sentenceColumn = new TextColumn<TermContext>() {

				@Override
				public String getValue(TermContext object) {
					return object.getSentence();
				}
			};
			table.addColumn(sentenceColumn, "Sentence of '" + term + "'");
			table.setColumnWidth(sentenceColumn, "80%");

			// fill in data
			table.setRowCount(contexts.size());
			table.setRowData(contexts);
		} else {
			// label
			Label noRecordLbl = new Label("No context record found for term '"
					+ term + "'");
			initWidget(noRecordLbl);
		}
	}

	public Widget asWidget() {
		return this;
	}

}
