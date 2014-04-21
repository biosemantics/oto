package edu.arizona.biosemantics.oto.lite.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class DecisionHolder implements Serializable {

	/**
	 * This class is to hold the grouping decisions during parsing requestXML
	 */
	private static final long serialVersionUID = -7946277801987517084L;
	private ArrayList<Category> new_categories;
	private ArrayList<Category> regular_categories;
	private ArrayList<String> reviewed_terms;
	private int uploadID; //OTO light

	public ArrayList<Category> getNew_categories() {
		return new_categories;
	}

	public void setNew_categories(ArrayList<Category> new_categories) {
		this.new_categories = new_categories;
	}

	public ArrayList<Category> getRegular_categories() {
		return regular_categories;
	}

	public void setRegular_categories(ArrayList<Category> regular_categories) {
		this.regular_categories = regular_categories;
	}

	public ArrayList<String> getReviewed_terms() {
		return reviewed_terms;
	}

	public void setReviewed_terms(ArrayList<String> reviewed_terms) {
		this.reviewed_terms = reviewed_terms;
	}

	public int getUploadID() {
		return uploadID;
	}

	public void setUploadID(int uploadID) {
		this.uploadID = uploadID;
	}

}
