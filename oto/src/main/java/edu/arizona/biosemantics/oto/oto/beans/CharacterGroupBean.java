package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/** This is a screenshot of one group
 * @author Partha Pratim Sanyal
 *  */


public class CharacterGroupBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2281130171606373012L;
	private ArrayList <TermsGroup> cooccurrences;// to be deleted
	private ArrayList<Term> availableTerms; //replace the coocurrances
	private String groupName;
	private boolean isSaved;
	private String decision;
	private HashMap<String, Double> entropyScores;
	
	public ArrayList<Term> getAvailableTerms() {
		return availableTerms;
	}
	
	public void setAvailableTerms(ArrayList<Term> availableTerms) {
		this.availableTerms = availableTerms;
	}
	
	/**
	 * @return the entropyScores
	 */
	public HashMap<String, Double> getEntropyScores() {
		return entropyScores;
	}
	/**
	 * @param entropyScores the entropyScores to set
	 */
	public void setEntropyScores(HashMap<String, Double> entropyScores) {
		this.entropyScores = entropyScores;
	}
	/**
	 * @return the cooccurrences
	 */
	public ArrayList<TermsGroup> getCooccurrences() {
		return cooccurrences;
	}
	/**
	 * @param cooccurrences the cooccurrences to set
	 */
	public void setCooccurrences(ArrayList<TermsGroup> cooccurrences) {
		this.cooccurrences = cooccurrences;
	}
	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	/**
	 * @return the isSaved
	 */
	public boolean isSaved() {
		return isSaved;
	}
	/**
	 * @param isSaved the isSaved to set
	 */
	public void setSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}

	/**
	 * @return the decision
	 */
	public String getDecision() {
		return decision;
	}
	/**
	 * @param decision the decision to set
	 */
	public void setDecision(String decision) {
		this.decision = decision;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cooccurrences == null) ? 0 : cooccurrences.hashCode());
		result = prime * result
				+ ((decision == null) ? 0 : decision.hashCode());
		result = prime * result
				+ ((groupName == null) ? 0 : groupName.hashCode());
		result = prime * result + (isSaved ? 1231 : 1237);
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CharacterGroupBean))
			return false;
		final CharacterGroupBean other = (CharacterGroupBean) obj;
		if (cooccurrences == null) {
			if (other.cooccurrences != null)
				return false;
		} else if (!cooccurrences.equals(other.cooccurrences))
			return false;
		if (decision == null) {
			if (other.decision != null)
				return false;
		} else if (!decision.equals(other.decision))
			return false;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		if (isSaved != other.isSaved)
			return false;
		return true;
	}
	public CharacterGroupBean(ArrayList<TermsGroup> cooccurrences,
			String groupName, boolean isSaved) {
		this.cooccurrences = cooccurrences;
		this.groupName = groupName;
		this.isSaved = isSaved;
	}
	
	
}
