package edu.arizona.biosemantics.oto.common.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GlossaryDictionaryEntry {

	private String termID;
	private String term;
	private String category;
	private String glossaryType;
	private String definition;
	
	public GlossaryDictionaryEntry() {
		
	}

	public GlossaryDictionaryEntry(String termID, String term, String category, String glossaryType, String definition) {
		super();
		this.termID = termID;
		this.term = term;
		this.category = category;
		this.glossaryType = glossaryType;
		this.definition = definition;
	}

	public String getTermID() {
		return termID;
	}

	public void setTermID(String termID) {
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

	public String getGlossaryType() {
		return glossaryType;
	}

	public void setGlossaryType(String glossaryType) {
		this.glossaryType = glossaryType;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

}