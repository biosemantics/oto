package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;

public class BioportalSubmissionBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8893550782253404494L;
	private long localID;
	private String tmpID;
	private String termName;
	private String permanentID;
	private String superClass;
	private String submittedBy;
	private String definition;
	private String ontologyIDs;
	private String preferredName;
	private String source;
	private String temType;
	private String temCategory;
	private String dataset;
	private boolean adopted;
	private String status;
	private String synonyms;
	private int userid;
	private String username;
	private int glossaryType;
	private String deleteTime;
	private String deletedBy;

	public BioportalSubmissionBean(String termName) {
		this.termName = termName;
	}

	public BioportalSubmissionBean() {

	}

	public long getLocalID() {
		return localID;
	}

	public void setLocalID(long localID) {
		this.localID = localID;
	}

	public String getTmpID() {
		return tmpID;
	}

	public void setTmpID(String tmpID) {
		this.tmpID = tmpID;
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
		if (permanentID == null || permanentID.equals("")) {
			setAdopted(false);
		} else {
			setAdopted(true);
		}
	}

	public String getSuperClass() {
		return superClass;
	}

	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(String submittedBy) {
		this.submittedBy = submittedBy;
	}

	public String getOntologyIDs() {
		return ontologyIDs;
	}

	public void setOntologyIDs(String ontologyIDs) {
		this.ontologyIDs = ontologyIDs;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTemType() {
		return temType;
	}

	public void setTemType(String temType) {
		this.temType = temType;
	}

	public String getTemCategory() {
		return temCategory;
	}

	public void setTemCategory(String temCategory) {
		this.temCategory = temCategory;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public boolean isAdopted() {
		return adopted;
	}

	public void setAdopted(boolean adopted) {
		this.adopted = adopted;
		if (adopted) {
			setStatus("Adopted");
		} else {
			setStatus("Pending");
		}
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(String synonyms) {
		this.synonyms = synonyms;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getGlossaryType() {
		return glossaryType;
	}

	public void setGlossaryType(int glossaryType) {
		this.glossaryType = glossaryType;
	}

	public String getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(String deleteTime) {
		this.deleteTime = deleteTime;
	}

	public String getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(String deletedBy) {
		this.deletedBy = deletedBy;
	}

}
