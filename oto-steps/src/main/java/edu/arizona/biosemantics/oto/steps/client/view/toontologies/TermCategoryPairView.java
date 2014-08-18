package edu.arizona.biosemantics.oto.steps.client.view.toontologies;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.toontologies.TermCategoryPairPresenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.MappingStatus;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.TermCategoryPair;

public class TermCategoryPairView extends Composite implements
		TermCategoryPairPresenter.Display {
	private HorizontalPanel layout;
	private TermCategoryPair tcPair;
	private Image actionImg;
	private Label nameLabel;

	public TermCategoryPairView(TermCategoryPair tcPair) {
		this.tcPair = tcPair;

		layout = new HorizontalPanel();
		layout.setSize("100%", "20px");
		initWidget(layout);

		SimplePanel namePart = new SimplePanel();
		namePart.setSize("80%", "100%");
		nameLabel = new Label(tcPair.getTerm() + " (" + tcPair.getCategory()
				+ ")");
		nameLabel.addStyleName("clickable");
		namePart.add(nameLabel);
		if (tcPair.getStatus().equals(MappingStatus.MAPPED_TO_MATCH)) {
			nameLabel.addStyleName("TO_ONTOLOGY_mapped_to_match");
		} else if (tcPair.getStatus()
				.equals(MappingStatus.MAPPED_TO_SUBMISSION)) {
			nameLabel.addStyleName("TO_ONTOLOGY_mapped_to_submission");
		} else {
			nameLabel.addStyleName("TO_ONTOLOGY_not_mapped");
		}
		layout.add(namePart);

		if (tcPair.isRemoved()) {
			actionImg = new Image("images/correct.png");
			actionImg.setHeight("12px");
		} else {
			actionImg = new Image("images/cross.png");
			actionImg.setHeight("10px");
		}
		actionImg.addStyleName("img_btn right");

		SimplePanel actionPart = new SimplePanel();
		actionPart.addStyleName("right_container");
		actionPart.setSize("20%", "100%");
		actionPart.add(actionImg);
		layout.add(actionPart);
	}

	@Override
	public Image getActionBtn() {
		return actionImg;
	}

	public Widget asWidget() {
		return this;
	}

	@Override
	public TermCategoryPair getTermCategoryPair() {
		return tcPair;
	}

	@Override
	public Label getNameLabel() {
		return nameLabel;
	}

}
