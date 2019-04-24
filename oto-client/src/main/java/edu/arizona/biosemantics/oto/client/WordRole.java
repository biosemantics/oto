package edu.arizona.biosemantics.oto.client;

public class WordRole {

	private String word;
	private String semanticRole;
	private String savedid;
	
	public WordRole() { }
	
	public WordRole(String word, String semanticRole, String savedid) {
		this.word = word;
		this.semanticRole = semanticRole;
		this.savedid = savedid;
	}


	public String getWord() {
		return word;
	}


	public void setWord(String word) {
		this.word = word;
	}


	public String getSemanticRole() {
		return semanticRole;
	}


	public void setSemanticRole(String semanticRole) {
		this.semanticRole = semanticRole;
	}


	public String getSavedid() {
		return savedid;
	}


	public void setSavedid(String savedid) {
		this.savedid = savedid;
	}

}