package edu.arizona.biosemantics.oto.lite.beans;

public class TermGlossaryBean {

	private String id;
	private String category;
	private String definition;

	public TermGlossaryBean() {

	}

	public TermGlossaryBean(String id, String category, String definition) {
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
