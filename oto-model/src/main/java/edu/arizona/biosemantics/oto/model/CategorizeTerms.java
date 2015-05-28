package edu.arizona.biosemantics.oto.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CategorizeTerms implements Serializable {

	private DecisionHolder decisionHolder;
	private String authenticationToken;
	
	public CategorizeTerms() { }
	
	public CategorizeTerms(DecisionHolder decisionHolder, String authenticationToken) {
		super();
		this.decisionHolder = decisionHolder;
		this.authenticationToken = authenticationToken;
	}

	
	public DecisionHolder getDecisionHolder() {
		return decisionHolder;
	}

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	public void setDecisionHolder(DecisionHolder decisionHolder) {
		this.decisionHolder = decisionHolder;
	}

	public void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}
	
}
