package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;

public class TermGlossaryBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3357830326327676880L;
	private String term;
	private String category;
	private String definition;
	private String note; // from where: glossary? ontology? locally accepted

	public TermGlossaryBean(String term) {
		this.term = term;
	}

	public TermGlossaryBean(String term, String category) {
		this.term = term;
		this.category = category;
	}

	public TermGlossaryBean(String term, String category, String def,
			String note) {
		this.term = term;
		this.category = category;
		this.definition = def;
		this.note = note;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}
}
