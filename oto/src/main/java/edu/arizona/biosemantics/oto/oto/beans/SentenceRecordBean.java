package edu.arizona.biosemantics.oto.oto.beans;

public class SentenceRecordBean {
	private int sentID;
	private String source;
	private String sentence;
	private String originalSent;
	private String status;
	private String tag;

	public SentenceRecordBean(int sentID, String source, String sentence,
			String originalSent, String status, String tag) {
		this.sentID = sentID;
		this.source = source;
		this.sentence = sentence;
		this.originalSent = originalSent;
		this.status = status;
		this.tag = tag;
	}

	public int getSentID() {
		return sentID;
	}

	public void setSentID(int sentID) {
		this.sentID = sentID;
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

	public String getOriginalSent() {
		return originalSent;
	}

	public void setOriginalSent(String originalSent) {
		this.originalSent = originalSent;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
