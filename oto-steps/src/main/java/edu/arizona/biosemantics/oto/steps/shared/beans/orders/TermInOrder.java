package edu.arizona.biosemantics.oto.steps.shared.beans.orders;

import java.io.Serializable;

public class TermInOrder implements Serializable {
	/**
	 * a term in a order
	 */
	private static final long serialVersionUID = 663684299805998604L;
	private String termName;
	private int position;

	/**
	 * must have this non-argument constructor to be serializable
	 */
	public TermInOrder() {
		// has to be here for GWT serializabl
	}

	public TermInOrder(String termName, int position) {
		this.termName = termName;
		this.position = position;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
