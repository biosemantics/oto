package edu.arizona.biosemantics.oto.oto.beans;

import java.util.ArrayList;

public class DatasetStatistics {
	private String datasetName;
	private boolean isPrivate = false;

	/**
	 * page 1: categorization
	 */
	private int numTotalTerms;
	private int numUnCategorizedTerms;
	private int numUnTouchedTerms;
	// decisoins statistics
	private int numDecisions;
	// reviews statistics
	private int numReviews;
	private ArrayList<UserStatisticsBean> userStatsInCategorizationWithReview;
	// boolean values
	private boolean hasTerm = false;
	private boolean hasCategorizationDecision = false;

	/**
	 * page 2: hierarchy
	 */
	private int numTotalTags;
	private int numUnTouchedTags;
	// decision statistics
	private int numDecisionsInHierarchy;
	private ArrayList<UserStatisticsBean> userStatsInHierarchy;
	// boolean values
	private boolean hasStructure = false;
	private boolean hasTreeDecision = false;

	/**
	 * page 3: order
	 */
	private int numTotalOrders;
	private int numTotalTermsInOrders;
	// decision statistics
	private int numDecisionsInOrders;
	private ArrayList<UserStatisticsBean> userStatsInOrders;
	// boolean values
	private boolean hasOrder = false;
	private boolean hasOrderDecision = false;

	// comments statistics
	private int numComments;
	private ArrayList<UserStatisticsBean> userStatsInComments;

	public int getNumDecisions() {
		return numDecisions;
	}

	public void setNumDecisions(int numDecisions) {
		this.numDecisions = numDecisions;
		if (numDecisions > 0) {
			setHasCategorizationDecision(true);
		}
	}

	public int getNumComments() {
		return numComments;
	}

	public void setNumComments(int numComments) {
		this.numComments = numComments;
	}

	public int getNumReviews() {
		return numReviews;
	}

	public void setNumReviews(int numReviews) {
		this.numReviews = numReviews;
	}

	public int getNumTotalTerms() {
		return numTotalTerms;
	}

	public void setNumTotalTerms(int numTotalTerms) {
		this.numTotalTerms = numTotalTerms;
		if (numTotalTerms > 0) {
			setHasTerm(true);
		}
	}

	public int getNumUnCategorizedTerms() {
		return numUnCategorizedTerms;
	}

	public void setNumUnCategorizedTerms(int numUnCategorizedTerms) {
		this.numUnCategorizedTerms = numUnCategorizedTerms;
	}

	public int getNumTotalTags() {
		return numTotalTags;
	}

	public void setNumTotalTags(int numTotalTags) {
		this.numTotalTags = numTotalTags;
		if (numTotalTags > 0) {
			setHasStructure(true);
		}
	}

	public int getNumUnTouchedTags() {
		return numUnTouchedTags;
	}

	public void setNumUnTouchedTags(int numUnTouchedTags) {
		this.numUnTouchedTags = numUnTouchedTags;
	}

	public int getNumDecisionsInHierarchy() {
		return numDecisionsInHierarchy;
	}

	public void setNumDecisionsInHierarchy(int numDecisionsInHierarchy) {
		this.numDecisionsInHierarchy = numDecisionsInHierarchy;
		if (numDecisionsInHierarchy > 0) {
			setHasTreeDecision(true);
		}
	}

	public int getNumTotalOrders() {
		return numTotalOrders;
	}

	public void setNumTotalOrders(int numTotalOrders) {
		this.numTotalOrders = numTotalOrders;
		if (numTotalOrders > 0) {
			setHasOrder(true);
		}
	}

	public int getNumTotalTermsInOrders() {
		return numTotalTermsInOrders;
	}

	public void setNumTotalTermsInOrders(int numTotalTermsInOrders) {
		this.numTotalTermsInOrders = numTotalTermsInOrders;
	}

	public int getNumDecisionsInOrders() {
		return numDecisionsInOrders;
	}

	public void setNumDecisionsInOrders(int numDecisionsInOrders) {
		this.numDecisionsInOrders = numDecisionsInOrders;
		if (numDecisionsInOrders > 0) {
			setHasOrderDecision(true);
		}
	}

	public ArrayList<UserStatisticsBean> getUserStatsInHierarchy() {
		return userStatsInHierarchy;
	}

	public void setUserStatsInHierarchy(
			ArrayList<UserStatisticsBean> userStatsInHierarchy) {
		this.userStatsInHierarchy = userStatsInHierarchy;
	}

	public ArrayList<UserStatisticsBean> getUserStatsInOrders() {
		return userStatsInOrders;
	}

	public void setUserStatsInOrders(
			ArrayList<UserStatisticsBean> userStatsInOrders) {
		this.userStatsInOrders = userStatsInOrders;
	}

	public ArrayList<UserStatisticsBean> getUserStatsInComments() {
		return userStatsInComments;
	}

	public void setUserStatsInComments(
			ArrayList<UserStatisticsBean> userStatsInComments) {
		this.userStatsInComments = userStatsInComments;
	}

	public ArrayList<UserStatisticsBean> getUserStatsInCategorizationWithReview() {
		return userStatsInCategorizationWithReview;
	}

	public void setUserStatsInCategorizationWithReview(
			ArrayList<UserStatisticsBean> userStatsInCategorizationWithReview) {
		this.userStatsInCategorizationWithReview = userStatsInCategorizationWithReview;
	}

	public int getNumUnTouchedTerms() {
		return numUnTouchedTerms;
	}

	public void setNumUnTouchedTerms(int numUnTouchedTerms) {
		this.numUnTouchedTerms = numUnTouchedTerms;
	}

	public boolean isHasTerm() {
		return hasTerm;
	}

	public void setHasTerm(boolean hasTerm) {
		this.hasTerm = hasTerm;
	}

	public boolean isHasCategorizationDecision() {
		return hasCategorizationDecision;
	}

	public void setHasCategorizationDecision(boolean hasCategorizationDecision) {
		this.hasCategorizationDecision = hasCategorizationDecision;
	}

	public boolean isHasStructure() {
		return hasStructure;
	}

	public void setHasStructure(boolean hasStructure) {
		this.hasStructure = hasStructure;
	}

	public boolean isHasTreeDecision() {
		return hasTreeDecision;
	}

	public void setHasTreeDecision(boolean hasTreeDecision) {
		this.hasTreeDecision = hasTreeDecision;
	}

	public boolean isHasOrder() {
		return hasOrder;
	}

	public void setHasOrder(boolean hasOrder) {
		this.hasOrder = hasOrder;
	}

	public boolean isHasOrderDecision() {
		return hasOrderDecision;
	}

	public void setHasOrderDecision(boolean hasOrderDecision) {
		this.hasOrderDecision = hasOrderDecision;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

}
