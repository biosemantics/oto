package edu.arizona.biosemantics.oto.common.model.iplant;

public class TermCategory {

	private String term;
	private String category;
	private boolean hasSyn; 
	
	public TermCategory() { }
	
	public TermCategory(String term, String category, boolean hasSyn) {
		this.term = term;
		this.category = category;
		this.hasSyn = hasSyn;
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

	public boolean isHasSyn() {
		return hasSyn;
	}

	public void setHasSyn(boolean hasSyn) {
		this.hasSyn = hasSyn;
	}
}
