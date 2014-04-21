package edu.arizona.biosemantics.oto.oto.beans;

import java.util.ArrayList;

public class DatasetStatistics {

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

	/**
	 * page 2: hierarchy
	 */
	private int numTotalTags;
	private int numUnTouchedTags;
	// decision statistics
	private int numDecisionsInHierarchy;
	private ArrayList<UserStatisticsBean> userStatsInHierarchy;

	/**
	 * page 3: order
	 */
	private int numTotalOrders;
	private int numTotalTermsInOrders;
	// decision statistics
	private int numDecisionsInOrders;
	private ArrayList<UserStatisticsBean> userStatsInOrders;
	
	// comments statistics
		private int numComments;
		private ArrayList<UserStatisticsBean> userStatsInComments;

	public int getNumDecisions() {
		return numDecisions;
	}

	public void setNumDecisions(int numDecisions) {
		this.numDecisions = numDecisions;
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
	}

	public int getNumTotalOrders() {
		return numTotalOrders;
	}

	public void setNumTotalOrders(int numTotalOrders) {
		this.numTotalOrders = numTotalOrders;
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

}
