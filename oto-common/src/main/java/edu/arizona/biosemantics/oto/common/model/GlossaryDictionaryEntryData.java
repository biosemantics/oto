package edu.arizona.biosemantics.oto.common.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GlossaryDictionaryEntryData {
	private Authentication authentication;
	private String termDescription;
	
	public GlossaryDictionaryEntryData(){}
	
	public GlossaryDictionaryEntryData(Authentication authentication, String termDescription){
		this.authentication = authentication;
		this.termDescription = termDescription;
	}
	
	public Authentication getAuthentication(){
		return authentication;
	}
	
	public String getTermDescription(){
		return termDescription;
	}
	
	public void setAuthentication(Authentication authentication){
		this.authentication = authentication;
	}
	
	public void setTermDescription(String termDescription){
		this.termDescription = termDescription;
	}
}
