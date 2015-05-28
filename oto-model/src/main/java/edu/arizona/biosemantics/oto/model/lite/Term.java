package edu.arizona.biosemantics.oto.model.lite;

import java.io.Serializable;

public class Term implements Serializable {

	private String term;
	
	public Term() { }

	public Term(String term) {
		this.term = term;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
}
