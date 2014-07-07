package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This is the term bean for bioportal ontology lookup
 * 
 * @author Huang
 * 
 */
public class TermForBioportalBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6029176985384974181L;
	private String termName;
	private String source; // source of the term, from sentence table
	private ArrayList<String> availableCategories; // decision not submitted yet
	private String syns = ""; // synonyms separated by comma
	private ArrayList<BioportalSubmissionBean> existingSubmissions;

	public TermForBioportalBean(String termName) {
		this.termName = termName;
	}
	
	public TermForBioportalBean() {
		
	}
	
	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public ArrayList<String> getAvailableCategories() {
		return availableCategories;
	}

	public void setAvailableCategories(ArrayList<String> availableCategories) {
		this.availableCategories = availableCategories;
	}

	public String getSyns() {
		return syns;
	}

	public void setSyns(String syns) {
		this.syns = syns;
	}

	public ArrayList<BioportalSubmissionBean> getExistingSubmissions() {
		return existingSubmissions;
	}

	public void setExistingSubmissions(ArrayList<BioportalSubmissionBean> existingSubmissions) {
		this.existingSubmissions = existingSubmissions;
	}
}
