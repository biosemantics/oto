package edu.arizona.biosemantics.oto.lite.beans;

import java.io.Serializable;

public class Upload implements Serializable {
	private static final long serialVersionUID = -7920176254732654835L;
	private long uploadID;
	private String uploadTime;
	private boolean isFinalized;
	private boolean sentToOTO;
	private int numberTerms;

	public Upload(long id) {
		uploadID = id;
	}

	public long getUploadID() {
		return uploadID;
	}

	public void setUploadID(long uploadID) {
		this.uploadID = uploadID;
	}

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}

	public boolean isFinalized() {
		return isFinalized;
	}

	public void setFinalized(boolean isFinalized) {
		this.isFinalized = isFinalized;
	}

	public boolean isSentToOTO() {
		return sentToOTO;
	}

	public void setSentToOTO(boolean sentToOTO) {
		this.sentToOTO = sentToOTO;
	}

	public int getNumberTerms() {
		return numberTerms;
	}

	public void setNumberTerms(int numberTerms) {
		this.numberTerms = numberTerms;
	}
}
