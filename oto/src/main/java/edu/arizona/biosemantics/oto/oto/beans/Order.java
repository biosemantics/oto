package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class Order  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6160828661900502038L;
	private int id;
	private String name;
	private int baseOrderID;
	private String baseTermName;
	private ArrayList<Character> terms;
	private ArrayList<Order> subOrders; //will be used in when parsing requestXML
	private int termsNumber;
	private boolean savedBefore;
	private String explanation;
	private boolean hasConflict;
	
	public String getExplanation() {
		return explanation;
	}
	
	public void setExplanation(String exp) {
		if (exp == null) {
			this.explanation = "";
		} else if (exp.equals("null")) {
			this.explanation = "";
		} else {
			this.explanation = exp;
		}
	}
	
	public Order(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public boolean savedBefore() {
		return savedBefore;
	}
	public void setSavedBefore(boolean savedBefore) {
		this.savedBefore = savedBefore;
	}
	
	public int getTermsNumber() {
		return termsNumber;
	}
	public void setTermsNumber(int number) {
		this.termsNumber = number;
	}
	
	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getBaseOrderID() {
		return baseOrderID;
	}
	public void setbaseOrderID(int bid) {
		this.baseOrderID = bid;
	}
	
	public String getBaseTermName() {
		return baseTermName;
	}
	public void setBaseTermName(String bname) {
		this.baseTermName = bname;
	}
	
	public ArrayList<Character> getTerms() {
		return terms;
	}
	
	public void setTerms(ArrayList<Character> terms){
		this.terms = terms;
	}

	public void setSubOrders(ArrayList<Order> subOrders) {
		this.subOrders = subOrders;
	}

	public ArrayList<Order> getSubOrders() {
		return subOrders;
	}

	public void setHasConflict(boolean hasConflict) {
		this.hasConflict = hasConflict;
	}

	public boolean hasConflict() {
		return hasConflict;
	}

}
