package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.ArrayList;

import edu.arizona.biosemantics.oto.oto.db.ReportingDBAccess;

/**
 * This bean represents all the attributes pertaining to a term specific report
 * It also has private methods to pull out data.
 * 
 * @author Partha
 * 
 */
public class SpecificReportBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 191055270176590713L;
	private Term term; // to be deleted - by huangfq
	private int type; // 1: categorizing; 2: hierarchy tree; 3: order
	private String idOrName;
	private ArrayList<CommentBean> userComments;
	private User user;
	private ArrayList<DecisionBean> decisions;
	private ReportingDBAccess rdba;
	private String dataset;

	public SpecificReportBean(String idOrName, User user, String dataset,
			int type) throws Exception {
		super();
		this.idOrName = idOrName;
		this.user = user;
		this.dataset = dataset;
		this.type = type;
		rdba = new ReportingDBAccess();
		// get the comments
		userComments = getComments();
		// get the categorizing decisions
		decisions = getUserDecisions();
	}

	public String getIdOrName() {
		return idOrName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setIdOrName(String idOrName) {
		this.idOrName = idOrName;
	}

	/**
	 * @return the term
	 */
	public Term getTerm() {
		return term;
	}

	/**
	 * @param term
	 *            the term to set
	 */
	public void setTerm(Term term) {
		this.term = term;
	}

	/**
	 * @return the userComments
	 */
	public ArrayList<CommentBean> getUserComments() {
		return userComments;
	}

	/**
	 * @param userComments
	 *            the userComments to set
	 */
	public void setUserComments(ArrayList<CommentBean> userComments) {
		this.userComments = userComments;
	}

	/**
	 * @return the userId
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(User user) {
		this.user = user;
	}

	/**
	 * @return the decision
	 */
	public ArrayList<DecisionBean> getDecisions() {
		return decisions;
	}

	/**
	 * @param decision
	 *            the decision to set
	 */
	public void setDecisions(ArrayList<DecisionBean> decision) {
		this.decisions = decision;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataset == null) ? 0 : dataset.hashCode());
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result
				+ ((idOrName == null) ? 0 : idOrName.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SpecificReportBean))
			return false;
		SpecificReportBean other = (SpecificReportBean) obj;
		if (dataset == null) {
			if (other.dataset != null)
				return false;
		} else if (!dataset.equals(other.dataset))
			return false;
		if (idOrName == null) {
			if (other.idOrName != null)
				return false;
		} else if (!idOrName.equals(other.idOrName))
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
	 * This method gets all comments related to the current user
	 * 
	 * @return
	 */
	private ArrayList<CommentBean> getComments() {
		try {
			return rdba.getUserComments(new CommentBean(user), dataset, type,
					idOrName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method pulls all decisions made by the user on this term
	 * 
	 * @return
	 */
	private ArrayList<DecisionBean> getUserDecisions() {
		try {
			return rdba.getUserDecisions(user, idOrName, dataset, type);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<SynBean> getUserSynonyms() {
		try {
			return rdba.getSynonyms(user, idOrName, dataset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
