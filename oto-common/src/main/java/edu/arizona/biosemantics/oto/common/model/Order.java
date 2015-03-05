package edu.arizona.biosemantics.oto.common.model;

import java.util.ArrayList;

public class Order {
	private String orderName;
	private ArrayList<String> terms;

	public Order(String orderName, ArrayList<String> terms) {
		this.setOrderName(orderName);
		this.setTerms(terms);
	}

	public Order(String orderName) {
		this.setOrderName(orderName);
	}

	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	public ArrayList<String> getTerms() {
		return terms;
	}

	public void setTerms(ArrayList<String> terms) {
		this.terms = terms;
	}
}
