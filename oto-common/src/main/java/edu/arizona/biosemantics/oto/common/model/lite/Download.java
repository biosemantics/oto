package edu.arizona.biosemantics.oto.common.model.lite;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Download {

	private boolean finalized = false;
	private List<Decision> decisions;
	private List<Synonym> synonyms;
	
	public Download() {
		
	}

	public Download(boolean finalized, List<Decision> decisions, List<Synonym> synonyms) {
		super();
		this.finalized = finalized;
		this.decisions = decisions;
		this.synonyms = synonyms;
	}

	public List<Decision> getDecisions() {
		return decisions;
	}

	public void setDecisions(List<Decision> decisions) {
		this.decisions = decisions;
	}

	public List<Synonym> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<Synonym> synonyms) {
		this.synonyms = synonyms;
	}
	
	@Override
	public String toString() {
		return decisions.toString() + " " + synonyms.toString();
	}

	public boolean isFinalized() {
		return finalized;
	}

	public void setFinalized(boolean finalized) {
		this.finalized = finalized;
	}
}
