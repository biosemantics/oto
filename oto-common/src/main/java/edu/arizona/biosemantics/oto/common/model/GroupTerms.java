package edu.arizona.biosemantics.oto.common.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GroupTerms {

	private List<TermContext> termContexts;
	private Authentication authentication;
	
	public GroupTerms() { }
	
	public GroupTerms(List<TermContext> termContexts, Authentication authentication) {
		super();
		this.termContexts = termContexts;
		this.authentication = authentication;
	}

	public List<TermContext> getTermContexts() {
		return termContexts;
	}

	public void setTermContexts(List<TermContext> termContexts) {
		this.termContexts = termContexts;
	}

	public Authentication getAuthentication() {
		return authentication;
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}
	
	
	
}
