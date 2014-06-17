package edu.arizona.biosemantics.oto.steps.shared.beans.toontologies;

import java.io.Serializable;

public class OntologySubmission implements Serializable {
	/**
	 * submissions are global in OTOLite
	 */
	private static final long serialVersionUID = -5938989384592171693L;

	// Fields for bioportal
	private String tmpID;
	private String permanentID;
	private String term;
	private String definition;
	private String synonyms;
	private String superClass;
	private String ontologyID;
	private String submittedBy;

	// fields for our DB
	private String submissionID;
	private String localID = "uuid";
	private String category;
	private String source;
	private String sampleSentence;

	public OntologySubmission() {
		// has to be here for GWT serializable
	}

	public String getLocalID() {
		return localID;
	}

	public void setLocalID(String localID) {
		this.localID = localID;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSubmissionID() {
		return submissionID;
	}

	public void setSubmissionID(String submissionID) {
		this.submissionID = submissionID;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTmpID() {
		return tmpID;
	}

	public void setTmpID(String tmpID) {
		this.tmpID = tmpID;
	}

	public String getPermanentID() {
		return permanentID;
	}

	public void setPermanentID(String permanentID) {
		this.permanentID = permanentID;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(String synonyms) {
		this.synonyms = synonyms;
	}

	public String getOntologyID() {
		return ontologyID;
	}

	public void setOntologyID(String ontologyID) {
		this.ontologyID = ontologyID;
	}

	public String getSuperClass() {
		return superClass;
	}

	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

	public String getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(String submittedBy) {
		this.submittedBy = submittedBy;
	}

	public String getSampleSentence() {
		return sampleSentence;
	}

	public void setSampleSentence(String sampleSentence) {
		this.sampleSentence = sampleSentence;
	}

}
