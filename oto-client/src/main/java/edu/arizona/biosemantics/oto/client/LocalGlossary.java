package edu.arizona.biosemantics.oto.client;

import java.util.ArrayList;
import java.util.List;

import edu.arizona.biosemantics.oto.common.model.TermCategory;
import edu.arizona.biosemantics.oto.common.model.TermSynonym;

public class LocalGlossary {

	private List<TermCategory> termCategories = new ArrayList<TermCategory>();
	private List<TermSynonym> termSynonyms = new ArrayList<TermSynonym>();
	private List<WordRole> wordRoles = new ArrayList<WordRole>();
	
	public LocalGlossary() { };
	
	public LocalGlossary(List<TermCategory> termCategories,
			List<TermSynonym> termSynonyms, List<WordRole> wordRoles) {
		super();
		this.termCategories = termCategories;
		this.termSynonyms = termSynonyms;
		this.wordRoles = wordRoles;
	}


	public List<TermCategory> getTermCategories() {
		return termCategories;
	}


	public void setTermCategories(List<TermCategory> termCategories) {
		this.termCategories = termCategories;
	}


	public List<TermSynonym> getTermSynonyms() {
		return termSynonyms;
	}


	public void setTermSynonyms(List<TermSynonym> termSynonyms) {
		this.termSynonyms = termSynonyms;
	}


	public List<WordRole> getWordRoles() {
		return wordRoles;
	}


	public void setWordRoles(List<WordRole> wordRoles) {
		this.wordRoles = wordRoles;
	}
	
	public String toString() {
		return termCategories.toString() + " " + termSynonyms.toString() + " " + wordRoles.toString();
	}
	
}
