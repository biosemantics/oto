package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * This bean will hold the decision related information
 * @author Partha
 *
 */
public class DecisionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7115848622780009171L;
	private int decisionId;
	private User user;
	private Term term;
	private Date decisionDate;
	private boolean accepted;
	private String decision;
	private String syns;
	/**
	 * @return the accepted
	 */
	public boolean isAccepted() {
		return accepted;
	}
	/**
	 * @param accepted the accepted to set
	 */
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (accepted ? 1231 : 1237);
		result = prime * result
				+ ((decision == null) ? 0 : decision.hashCode());
		result = prime * result
				+ ((decisionDate == null) ? 0 : decisionDate.hashCode());
		result = prime * result + decisionId;
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		if (!(obj instanceof DecisionBean))
			return false;
		DecisionBean other = (DecisionBean) obj;
		if (accepted != other.accepted)
			return false;
		if (decision == null) {
			if (other.decision != null)
				return false;
		} else if (!decision.equals(other.decision))
			return false;
		if (decisionDate == null) {
			if (other.decisionDate != null)
				return false;
		} else if (!decisionDate.equals(other.decisionDate))
			return false;
		if (decisionId != other.decisionId)
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	/**
	 * @return the decisionId
	 */
	public int getDecisionId() {
		return decisionId;
	}
	/**
	 * @param decisionId the decisionId to set
	 */
	public void setDecisionId(int decisionId) {
		this.decisionId = decisionId;
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * @return the term
	 */
	public Term getTerm() {
		return term;
	}
	/**
	 * @param term the term to set
	 */
	public void setTerm(Term term) {
		this.term = term;
	}
	/**
	 * @return the decisionDate
	 */
	public Date getDecisionDate() {
		return decisionDate;
	}
	/**
	 * @param decisionDate the decisionDate to set
	 */
	public void setDecisionDate(Date decisionDate) {
		this.decisionDate = decisionDate;
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
	public void setSyns(String syns) {
		this.syns = syns;
	}
	public String getSyns() {
		return syns;
	}
}
