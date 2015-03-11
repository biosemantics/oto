package edu.arizona.biosemantics.oto.common.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateUserResult implements Serializable {

	private boolean result;

	public CreateUserResult(boolean result) {
		this.result = result;
	}
	
	public CreateUserResult() { }
	
	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
	
	
	
}
