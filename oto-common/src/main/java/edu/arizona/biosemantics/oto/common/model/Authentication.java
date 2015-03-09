package edu.arizona.biosemantics.oto.common.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Authentication implements Serializable {
	
	private String email;
	private String token;
	
	public Authentication() {}
	
	public Authentication(String email, String token){
		this.email = email;
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	

}