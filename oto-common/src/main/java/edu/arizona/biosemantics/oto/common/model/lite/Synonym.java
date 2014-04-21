package edu.arizona.biosemantics.oto.common.model.lite;

public class Synonym {

	private String id;
	private String term;
	private String category;
	private String synonym;
	
	public Synonym() { }

	public Synonym(String id, String term, String category, String synonym) {
		this.id = id;
		this.term = term;
		this.category = category;
		this.synonym = synonym;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
}

