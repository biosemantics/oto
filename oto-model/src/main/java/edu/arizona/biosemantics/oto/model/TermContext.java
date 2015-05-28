package edu.arizona.biosemantics.oto.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TermContext implements Serializable {

	private String term;
	private String context;
	
	public TermContext(){
		
	}
	
	public TermContext(String term, String context){
		this.term = term;
		this.context = context;
	}
	
	public String getTerm(){
		return term;
	}
	
	public void setTerm(String term){
		this.term = term;
	}
	
	public String getContext(){
		return context;
	}
	
	public void setContext(String context){
		this.context = context;
	}
	
	public String toString(){
		return term + ": " + context;
	}
}