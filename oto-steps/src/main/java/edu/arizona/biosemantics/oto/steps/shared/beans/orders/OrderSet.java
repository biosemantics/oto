package edu.arizona.biosemantics.oto.steps.shared.beans.orders;

import java.io.Serializable;
import java.util.ArrayList;


public class OrderSet implements Serializable {
	/**
	 * An order set that for an category with a list of terms
	 */
	private static final long serialVersionUID = 8180698422977414717L;
	private String categoryID;
	private String categoryName;
	private String defaultTerm;// the one whose position is always 0
	private ArrayList<String> terms;
	private ArrayList<Order> orders;

	/**
	 * must have this non-argument constructor to be serializable
	 */
	public OrderSet() {
		// has to be here for GWT serializabl
	}

	public OrderSet(String categoryName) {
		this.categoryName = categoryName;
	}

	public OrderSet(String categoryID, String categoryName) {
		this.setCategoryID(categoryID);
		this.categoryName = categoryName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getDefaultTerm() {
		return defaultTerm;
	}

	public void setDefaultTerm(String defaultTerm) {
		this.defaultTerm = defaultTerm;
	}

	public ArrayList<String> getTerms() {
		return terms;
	}

	public void setTerms(ArrayList<String> terms) {
		this.terms = terms;
	}

	public ArrayList<Order> getOrders() {
		return orders;
	}

	public void setOrders(ArrayList<Order> orders) {
		this.orders = orders;
	}

	public String getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(String categoryID) {
		this.categoryID = categoryID;
	}

}
