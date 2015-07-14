package edu.arizona.biosemantics.oto.common.model.iplant;

public class TermSynonym {

	private String term;
	private String synonym;
	
	public TermSynonym() { }

	public TermSynonym(String term, String synonym) {
		this.term = term;
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
}
