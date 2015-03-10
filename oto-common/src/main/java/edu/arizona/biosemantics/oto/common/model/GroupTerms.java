package edu.arizona.biosemantics.oto.common.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GroupTerms {

	private List<TermContext> termContexts;
	private String authenticationToken;
	private boolean replace = true;
	
	public GroupTerms() { }
	
	public GroupTerms(List<TermContext> termContexts, String authenticationToken, boolean replace) {
		super();
		this.termContexts = termContexts;
		this.authenticationToken = authenticationToken;
		this.replace = replace;
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

	public boolean isReplace() {
		return replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}	
	
}
