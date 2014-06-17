package edu.arizona.biosemantics.oto.steps.shared.beans.orders;

import java.io.Serializable;
import java.util.ArrayList;


public class Order implements Serializable {
	/**
	 * An order
	 */
	private static final long serialVersionUID = -1105139363017892863L;
	private String orderID;
	private String orderName;
	private String orderDescription;
	private boolean isNewlyCreated = false;
	private ArrayList<TermInOrder> termsInOrder;

	/**
	 * must have this non-argument constructor to be serializable
	 */
	public Order() {
		// has to be here for GWT serializabl
	}

	public Order(String orderName, String orderDescription) {
		this.orderName = orderName;
		this.orderDescription = orderDescription;
	}

	public Order(String orderID, String orderName, String orderDescription) {
		this.setOrderID(orderID);
		this.orderName = orderName;
		this.orderDescription = orderDescription;
	}

	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	public ArrayList<TermInOrder> getTermsInOrder() {
		return termsInOrder;
	}

	public void setTermsInOrder(ArrayList<TermInOrder> termsInOrder) {
		this.termsInOrder = termsInOrder;
	}

	public String getOrderDescription() {
		return orderDescription;
	}

	public void setOrderDescription(String orderDescription) {
		this.orderDescription = orderDescription;
	}

	public boolean isNewlyCreated() {
		return isNewlyCreated;
	}

	public void setNewlyCreated(boolean isNewlyCreated) {
		this.isNewlyCreated = isNewlyCreated;
	}

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

}
