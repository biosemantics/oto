package edu.arizona.biosemantics.oto.common.model.iplant;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import edu.arizona.biosemantics.oto.common.model.iplant.TermCategory;
import edu.arizona.biosemantics.oto.common.model.iplant.TermSynonym;


@XmlRootElement
public class GlossaryDownload {

	private String version;
	private List<TermCategory> termCategories = new ArrayList<TermCategory>();
	private List<TermSynonym> termSynonyms = new ArrayList<TermSynonym>();
	
	public GlossaryDownload() { }
	
	public GlossaryDownload(List<TermCategory> termCategories,
			List<TermSynonym> termSynonyms, String version) {
		super();
		this.version = version;
		this.termCategories = termCategories;
		this.termSynonyms = termSynonyms;
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String toString() {
		return "version: " + this.version + "\ntermCategories: " + this.termCategories.size() + "\ntermSyns: " + this.termSynonyms.size();
		
	}
}
