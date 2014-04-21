package edu.arizona.biosemantics.oto.common.model.lite;

public class Sentence {

	private int sentId;
	private String source;
	private String sentence;
	private String originalSentence;
	
	public Sentence() { }
	
	public Sentence(int sentId, String source, String sentence,	String originalSentence) {
		this.sentId = sentId;
		this.source = source;
		this.sentence = sentence;
		this.originalSentence = originalSentence;
	}

	public int getSentId() {
		return sentId;
	}

	public void setSentId(int sentId) {
		this.sentId = sentId;
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

	public String getOriginalSentence() {
		return originalSentence;
	}

	public void setOriginalSentence(String originalSentence) {
		this.originalSentence = originalSentence;
	}	
}
