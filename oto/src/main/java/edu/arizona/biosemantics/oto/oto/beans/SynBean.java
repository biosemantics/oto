package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.Date;

public class SynBean implements Serializable {

	/**
	 * this is the synonym decisions for categorizing page
	 */
	private static final long serialVersionUID = 8275479255355684411L;
	private String syns;
	private Date decisionDate;
	
	public SynBean(String name) {
		this.syns = name;
	}
	
	public void setDecisionDate(Date decisionDate) {
		this.decisionDate = decisionDate;
	}
	public Date getDecisionDate() {
		return decisionDate;
	}
	public void setSyns(String syns) {
		this.syns = syns;
	}
	public String getSyns() {
		return syns;
	}

}
