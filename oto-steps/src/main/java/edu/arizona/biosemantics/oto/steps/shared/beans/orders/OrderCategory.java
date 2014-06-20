package edu.arizona.biosemantics.oto.steps.shared.beans.orders;

import java.io.Serializable;

public class OrderCategory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6070672738530516212L;
	private String categoryID;
	private String categoryName;

	public OrderCategory() {

	}

	public OrderCategory(String id, String name) {
		this.categoryID = id;
		this.categoryName = name;
	}

	public String getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(String categoryID) {
		this.categoryID = categoryID;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
}
