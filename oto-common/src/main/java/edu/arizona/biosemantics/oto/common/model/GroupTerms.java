package edu.arizona.biosemantics.oto.common.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GroupTerms {

	private List<TermContext> termContexts;
	private String authenticationToken;
	
	public GroupTerms() { }
	
	public GroupTerms(List<TermContext> termContexts, String authenticationToken) {
		super();
		this.termContexts = termContexts;
		this.authenticationToken = authenticationToken;
	}

	public List<TermContext> getTermContexts() {
		return termContexts;
	}

	public void setTermContexts(List<TermContext> termContexts) {
		this.termContexts = termContexts;
	}

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	public void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}	
	
}
