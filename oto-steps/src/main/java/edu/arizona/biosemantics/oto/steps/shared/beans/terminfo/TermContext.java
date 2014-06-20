package edu.arizona.biosemantics.oto.steps.shared.beans.terminfo;

import java.io.Serializable;

public class TermContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2446934367710935529L;
	private String source;
	private String sentence;

	public TermContext() {

	}

	public TermContext(String src, String sentence) {
		this.sentence = sentence;
		this.source = src;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
}
