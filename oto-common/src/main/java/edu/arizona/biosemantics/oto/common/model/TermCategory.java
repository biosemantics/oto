package edu.arizona.biosemantics.oto.common.model;

public class TermCategory {

	private String term;
	private String category;
	private boolean hasSyn; 
	private String sourceDataset;
	private String termID;
	
	public TermCategory() { }
	
	public TermCategory(String term, String category, boolean hasSyn, String sourceDataset, String termID) {
		this.term = term;
		this.category = category;
		this.hasSyn = hasSyn;
		this.sourceDataset = sourceDataset;
		this.termID = termID;
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

	public String getSourceDataset() {
		return sourceDataset;
	}

	public void setSourceDataset(String sourceDataset) {
		this.sourceDataset = sourceDataset;
	}

	public String getTermID() {
		return termID;
	}

	public void setTermID(String termID) {
		this.termID = termID;
	}
	
	
}
