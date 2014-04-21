package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class TermDecision implements Serializable {

	/**
	 * This is for decision management by admins
	 */
	private static final long serialVersionUID = -7614586147240143882L;

	private String termName;
	private String category;
	private int distance;
	private ArrayList<String> accepedDecisions;
	private ArrayList<String> declinedDecisions;
	private ArrayList<String> unconfirmedDecisions;
	private ArrayList<AdminDecisionBean> unconfirmedDecisionsbeans;
	private ArrayList<AdminDecisionBean> confirmedDecisionBeans;
	private String date;
	private boolean confirmed;
	private String termID;
	private ArrayList<String> synonyms;
	private boolean hasConflict;

	public TermDecision(String term) {
		termName = term;
	}

	public TermDecision(int dist) {
		distance = dist;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public void setUnconfirmedDecisions(ArrayList<String> unconfirmedDecisions) {
		this.unconfirmedDecisions = unconfirmedDecisions;
	}

	public ArrayList<String> getUnconfirmedDecisions() {
		return unconfirmedDecisions;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getTermName() {
		return termName;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setTermID(String termID) {
		this.termID = termID;
	}

	public String getTermID() {
		return termID;
	}

	public void setAccepedDecisions(ArrayList<String> accepedDecisions) {
		this.accepedDecisions = accepedDecisions;
	}

	public ArrayList<String> getAccepedDecisions() {
		return accepedDecisions;
	}

	public void setDeclinedDecisions(ArrayList<String> declinedDecisions) {
		this.declinedDecisions = declinedDecisions;
	}

	public ArrayList<String> getDeclinedDecisions() {
		return declinedDecisions;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}

	public void setSynonyms(ArrayList<String> synonyms) {
		this.synonyms = synonyms;
	}

	public ArrayList<String> getSynonyms() {
		return synonyms;
	}

	public void setHasConflict(boolean hasConflict) {
		this.hasConflict = hasConflict;
	}

	public boolean hasConflict() {
		return hasConflict;
	}

	public ArrayList<AdminDecisionBean> getUnconfirmedDecisionsbeans() {
		return unconfirmedDecisionsbeans;
	}

	public void setUnconfirmedDecisionsbeans(
			ArrayList<AdminDecisionBean> unconfirmedDecisionsbeans) {
		this.unconfirmedDecisionsbeans = unconfirmedDecisionsbeans;
	}

	public ArrayList<AdminDecisionBean> getConfirmedDecisionBeans() {
		return confirmedDecisionBeans;
	}

	public void setConfirmedDecisionBeans(
			ArrayList<AdminDecisionBean> confirmedDecisionBeans) {
		this.confirmedDecisionBeans = confirmedDecisionBeans;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
