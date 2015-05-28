package edu.arizona.biosemantics.oto.model.lite;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UploadResult implements Serializable {

	private int uploadId;
	private String secret;
	
	public UploadResult() {
		
	}
	
	public UploadResult(int uploadId, String secret) {
		this.uploadId = uploadId;
		this.secret = secret;
	}

	public int getUploadId() {
		return uploadId;
	}

	public void setUploadId(int uploadId) {
		this.uploadId = uploadId;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
}
