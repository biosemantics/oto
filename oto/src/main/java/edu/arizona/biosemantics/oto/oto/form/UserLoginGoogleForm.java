package edu.arizona.biosemantics.oto.oto.form;

import org.apache.struts.action.ActionForm;

public class UserLoginGoogleForm extends ActionForm {

	private static final long serialVersionUID = -4811163680006707923L;
	private String accessToken;
	private String tokenType;
	private String expiresIn;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}
	
	

}
