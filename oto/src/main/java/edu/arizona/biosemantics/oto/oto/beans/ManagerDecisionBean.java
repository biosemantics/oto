package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;

/**
 * This is the manager decision bean for manager acceepting or declining users'
 * decisions
 * 
 * @author Fengqiong
 * 
 */
public class ManagerDecisionBean implements Serializable {
	private static final long serialVersionUID = 7628150775844199250L;
	private String term;
	private String category;
	private String decision;
	private boolean isAccept;
	private String dataset;
	private int orderID;

	public ManagerDecisionBean(String term, String decision, String dataset) {
		this.term = term;
		this.decision = decision;
		this.dataset = dataset;
	}

	public ManagerDecisionBean() {

	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getTerm() {
		return term;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

	public String getDecision() {
		return decision;
	}

	public void setAccept(boolean isAccept) {
		this.isAccept = isAccept;
	}

	public boolean isAccept() {
		return isAccept;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public String getDataset() {
		return dataset;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	public int getOrderID() {
		return orderID;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
