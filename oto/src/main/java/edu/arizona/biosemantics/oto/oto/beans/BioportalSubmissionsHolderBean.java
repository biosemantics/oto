package edu.arizona.biosemantics.oto.oto.beans;

import java.util.ArrayList;

/**
 * The bean to hold the submissions and related info
 * 
 * @author Huang
 * 
 */
public class BioportalSubmissionsHolderBean {
	private ArrayList<BioportalSubmissionBean> submissions;
	private int numOfPending = 0;

	public ArrayList<BioportalSubmissionBean> getSubmissions() {
		return submissions;
	}

	public void setSubmissions(ArrayList<BioportalSubmissionBean> submissions) {
		this.submissions = submissions;
	}

	public int getNumOfPending() {
		return numOfPending;
	}

	public void setNumOfPending(int numOfPending) {
		this.numOfPending = numOfPending;
	}

	public int getNumOfAdopted() {
		return submissions.size() - numOfPending;
	}
}
