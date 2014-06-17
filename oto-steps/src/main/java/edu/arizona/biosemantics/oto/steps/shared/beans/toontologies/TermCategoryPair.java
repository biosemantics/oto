package edu.arizona.biosemantics.oto.steps.shared.beans.toontologies;

import java.io.Serializable;

public class TermCategoryPair implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8293873138488019531L;
	private String pairID;
	private String term;
	private String category;
	private boolean isRemoved;
	private MappingStatus status;
	private boolean isStructure;

	public TermCategoryPair() {
		// has to be here for GWT serializabl
	}

	public TermCategoryPair(String pairID, String term, String category) {
		this.pairID = pairID;
		this.term = term;
		this.category = category;
	}

	public String getPairID() {
		return pairID;
	}

	public void setPairID(String pairID) {
		this.pairID = pairID;
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

	public boolean isRemoved() {
		return isRemoved;
	}

	public void setRemoved(boolean isRemoved) {
		this.isRemoved = isRemoved;
	}

	public boolean isStructure() {
		return isStructure;
	}

	public void setIsStructure(boolean isStructure) {
		this.isStructure = isStructure;
	}

	public MappingStatus getStatus() {
		return status;
	}

	public void setStatus(MappingStatus status) {
		this.status = status;
	}
}
