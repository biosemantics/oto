package edu.arizona.biosemantics.oto.lite.beans;

public class Decision {
	
	private String term;
	private boolean isMainTerm;
	private String category;
	
	public Decision() { }

	public Decision(String term, boolean isMainTerm, String category) {
		super();
		this.term = term;
		this.isMainTerm = isMainTerm;
		this.category = category;
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

	public boolean isMainTerm() {
		return isMainTerm;
	}

	public void setMainTerm(boolean isMainTerm) {
		this.isMainTerm = isMainTerm;
	}

}
