package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;

public class AdminDecisionBean implements Serializable {
	/**
	 * 
	 */

	private static final long serialVersionUID = -8521934966413336035L;
	private String category;
	private String synonym;
	private int decisionType; // 1-approve, 0-decline
	private String decidedBy; // who made this decision, maybe a list of people

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getDecisionType() {
		return decisionType;
	}

	public void setDecisionType(int decisionType) {
		this.decisionType = decisionType;
	}

	public String getDecidedBy() {
		return decidedBy;
	}

	public void setDecidedBy(String decidedBy) {
		this.decidedBy = decidedBy;
	}

	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}
}
