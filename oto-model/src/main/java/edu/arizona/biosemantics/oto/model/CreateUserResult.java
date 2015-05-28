package edu.arizona.biosemantics.oto.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateUserResult implements Serializable {

	private boolean result;
	private String message;

	public CreateUserResult(boolean result) {
		this.result = result;
	}
	
	public CreateUserResult(boolean result, String message) {
		this.result = result;
		this.message = message;
	}
	
	public CreateUserResult() { }
	
	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
