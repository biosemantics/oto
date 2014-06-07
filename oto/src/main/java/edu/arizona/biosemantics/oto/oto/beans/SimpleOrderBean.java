package edu.arizona.biosemantics.oto.oto.beans;

import java.util.ArrayList;

public class SimpleOrderBean {
	private String orderName;
	private ArrayList<String> terms;

	public SimpleOrderBean(String orderName, ArrayList<String> terms) {
		this.setOrderName(orderName);
		this.setTerms(terms);
	}

	public SimpleOrderBean(String orderName) {
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
