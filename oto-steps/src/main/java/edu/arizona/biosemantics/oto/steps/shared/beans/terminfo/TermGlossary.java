package edu.arizona.biosemantics.oto.steps.shared.beans.terminfo;

import java.io.Serializable;

public class TermGlossary implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2191681473010354133L;
	private String id;
	private String category;
	private String definition;

	public TermGlossary() {

	}

	public TermGlossary(String id, String category, String definition) {
		this.id = id;
		this.category = category;
		this.definition = definition;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
