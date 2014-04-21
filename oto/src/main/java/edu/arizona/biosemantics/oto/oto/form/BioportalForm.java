package edu.arizona.biosemantics.oto.oto.form;

import org.apache.struts.action.ActionForm;

public class BioportalForm extends ActionForm {

	/**
	 * The bioportal submission/update form
	 */
	private static final long serialVersionUID = 3627559894519570707L;
	private String termName;
	private String definition;
	private String syns;// synonyms
	private String ontology;
	private String superClassID;
	private String source;
	private String category;
	private String dataset;
	private String glossaryType;
	private String action;
	private String localID;
	private String tmpID;
	private String from; // from term/submission, will decide where to forward

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getSyns() {
		return syns;
	}

	public void setSyns(String syns) {
		this.syns = syns;
	}

	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getSuperClassID() {
		return superClassID;
	}

	public void setSuperClassID(String superClassID) {
		this.superClassID = superClassID;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public String getGlossaryType() {
		return glossaryType;
	}

	public void setGlossaryType(String glossaryType) {
		this.glossaryType = glossaryType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getLocalID() {
		return localID;
	}

	public void setLocalID(String localID) {
		this.localID = localID;
	}

	public String getTmpID() {
		return tmpID;
	}

	public void setTmpID(String tmpID) {
		this.tmpID = tmpID;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
