package edu.arizona.biosemantics.oto.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GroupTerms implements Serializable  {

	@XmlRootElement
	public static class Result implements Serializable {
		private int count;
		
		public Result() { 
			
		}
		
		public Result(int count) {
			this.count = count;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}
	}
	
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
