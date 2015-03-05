package edu.arizona.biosemantics.oto.common.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GlossaryDictionaryEntryData {
	private Login loginData;
	private String termDescription;
	
	public GlossaryDictionaryEntryData(){}
	
	public GlossaryDictionaryEntryData(Login loginData, String termDescription){
		this.loginData = loginData;
		this.termDescription = termDescription;
	}
	
	public Login getLoginData(){
		return loginData;
	}
	
	public String getTermDescription(){
		return termDescription;
	}
	
	public void setLoginData(Login loginData){
		this.loginData = loginData;
	}
	
	public void setTermDescription(String termDescription){
		this.termDescription = termDescription;
	}
}
