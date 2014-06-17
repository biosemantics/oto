package edu.arizona.biosemantics.oto.steps.client.view.hierarchy;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto.steps.client.presenter.hierarchy.StructureTermPresenter;
import edu.arizona.biosemantics.oto.steps.shared.beans.hierarchy.Structure;

public class StructureTermView extends Composite implements
		StructureTermPresenter.Display {

	private Label lbl;
	private Structure structure;

	public StructureTermView(Structure structure) {
		this.structure = structure;
		lbl = new Label(structure.getTermName());
		lbl.addStyleName("HIERARCHY_structure_label");
		lbl.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		lbl.getElement().setAttribute("term_name", structure.getTermName());
		initWidget(lbl);
	}

	@Override
	public Label getStructureLabel() {
		return lbl;
	}

	@Override
	public Structure getData() {
		return structure;
	}

	public Widget asWidget() {
		return this;
	}

}
