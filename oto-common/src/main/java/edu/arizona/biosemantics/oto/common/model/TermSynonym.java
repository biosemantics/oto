package edu.arizona.biosemantics.oto.common.model;

public class TermSynonym {

	private String term;
	private String category;
	private String synonym;
	private String termID;
	
	public TermSynonym() { }

	public TermSynonym(String term, String category, String synonym, String termID) {
		this.term = term;
		this.category = category;
		this.synonym = synonym;
		this.termID = termID;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTermID() {
		return termID;
	}

	public void setTermID(String termID) {
		this.termID = termID;
	}

}
