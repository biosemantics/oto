package edu.arizona.biosemantics.oto.common.model.lite;

public class Decision {

	private String id;
	private String term;
	private String category;
	private boolean hasSynonym;
	private String sourceDataset;
	
	public Decision() { }

	public Decision(String id, String term, String category, boolean hasSynonym, String sourceDataset) {
		super();
		this.id = id;
		this.term = term;
		this.category = category;
		this.hasSynonym = hasSynonym;
		this.sourceDataset = sourceDataset;
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
	
	public boolean isHasSynonym() {
		return hasSynonym;
	}

	public void setHasSynonym(boolean hasSynonym) {
		this.hasSynonym = hasSynonym;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSourceDataset() {
		return sourceDataset;
	}

	public void setSourceDataset(String sourceDataset) {
		this.sourceDataset = sourceDataset;
	}

}
