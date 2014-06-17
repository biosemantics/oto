package edu.arizona.biosemantics.oto.steps.shared.beans.toontologies;

import java.io.Serializable;

public class OntologyMatch implements Serializable {
	/**
	 * ontology matches are glossary global in OTOLite
	 */
	private static final long serialVersionUID = -2646362199278635153L;
	private String matchID;
	private String term;
	private String ontologyID;
	private String permanentID;
	private String parentTerm;
	private String definition;

	public OntologyMatch() {
		// has to be here for GWT serializabl
	}

	public OntologyMatch(String term) {
		this.term = term;
	}

	public void setMatchingInfo(String ontologyID, String permanentID,
			String parentTerm, String definition) {
		this.ontologyID = ontologyID;
		this.permanentID = permanentID;
		this.parentTerm = parentTerm;
		this.definition = definition;
	}

	public String getOntologyID() {
		return ontologyID;
	}

	public void setOntologyID(String ontologyID) {
		this.ontologyID = ontologyID;
	}

	public String getMatchID() {
		return matchID;
	}

	public void setMatchID(String matchID) {
		this.matchID = matchID;
	}

	public String getPermanentID() {
		return permanentID;
	}

	public void setPermanentID(String permanentID) {
		this.permanentID = permanentID;
	}

	public String getParentTerm() {
		return parentTerm;
	}

	public void setParentTerm(String parentTerm) {
		this.parentTerm = parentTerm;
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
}
