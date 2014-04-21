package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class DecisionHolder implements Serializable {

	/**
	 * This class is to hold the grouping decisions during parsing requestXML
	 */
	private static final long serialVersionUID = -7946277801987517084L;
	private ArrayList<CategoryBean> new_categories;
	private ArrayList<CategoryBean> regular_categories;
	private ArrayList<String> reviewed_terms;

	public ArrayList<CategoryBean> getNew_categories() {
		return new_categories;
	}

	public void setNew_categories(ArrayList<CategoryBean> new_categories) {
		this.new_categories = new_categories;
	}

	public ArrayList<CategoryBean> getRegular_categories() {
		return regular_categories;
	}

	public void setRegular_categories(ArrayList<CategoryBean> regular_categories) {
		this.regular_categories = regular_categories;
	}

	public ArrayList<String> getReviewed_terms() {
		return reviewed_terms;
	}

	public void setReviewed_terms(ArrayList<String> reviewed_terms) {
		this.reviewed_terms = reviewed_terms;
	}

}
