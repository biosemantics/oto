package edu.arizona.biosemantics.oto.steps.server.beans;

public class TermForTreePopulation {
	private String termID;
	private String termName;
	private String permanentID;
	private String treeID;

	public TermForTreePopulation(String termID, String termName,
			String permanentID) {
		this.termID = termID;
		this.termName = termName;
		this.permanentID = permanentID;
	}

	public String getTermID() {
		return termID;
	}

	public void setTermID(String termID) {
		this.termID = termID;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getPermanentID() {
		return permanentID;
	}

	public void setPermanentID(String permanentID) {
		this.permanentID = permanentID;
	}

	public String getTreeID() {
		return treeID;
	}

	public void setTreeID(String treeID) {
		this.treeID = treeID;
	}
}
