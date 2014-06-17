package edu.arizona.biosemantics.oto.steps.client.view.terminfo;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.terminfo.TermInfoPresenter;
import edu.arizona.biosemantics.oto.steps.client.widget.OtoTabPanel;

public class TermInfoView extends Composite implements
		TermInfoPresenter.Display {
	private OtoTabPanel tabPanel;
	private ScrollPanel termInfoPanel;
	private String term;

	public TermInfoView() {
		// tab names
		ArrayList<String> tabNames = new ArrayList<String>();
		tabNames.add("Context");
		tabNames.add("Ontology");
		tabNames.add("Dictionary");

		tabPanel = new OtoTabPanel(tabNames);
		tabPanel.selectTab(0);
		
		termInfoPanel = new ScrollPanel();
		termInfoPanel.setSize("100%", "100%");
		tabPanel.getContentPanel().add(termInfoPanel);
		setInitialMsg();

		initWidget(tabPanel.asWidget());
	}

	@Override
	public OtoTabPanel getTabPanel() {
		return tabPanel;
	}
	
	public Widget asWidget() {
		return this;
	}

	@Override
	public void setTerm(String term) {
		this.term = term;
	}

	@Override
	public String getTerm() {
		return term;
	}

	@Override
	public void clearTerm() {
		termInfoPanel.clear();
		setInitialMsg();
	}

	private void setInitialMsg() {
		Label initialMsg = new Label(
				"Click on a term to display detail information.");
		termInfoPanel.clear();
		termInfoPanel.add(initialMsg);
	}

	@Override
	public ScrollPanel getTermInfoPanel() {
		return termInfoPanel;
	}

}
