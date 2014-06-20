package edu.arizona.biosemantics.oto.steps.shared.beans.toontologies;

import java.io.Serializable;

public class OntologyRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3964212136774172255L;
	private String id;
	private String term;
	private String category;
	private String ontology;
	private String parent; // either parent term in match or super class in
							// submission
	private String definition;
	private boolean isSelected;
	private OntologyRecordType type;

	public OntologyRecord() {

	}

	public OntologyRecord(String term, String category) {
		this.term = term;
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public OntologyRecordType getType() {
		return type;
	}

	public void setType(OntologyRecordType type) {
		this.type = type;
	}

}
