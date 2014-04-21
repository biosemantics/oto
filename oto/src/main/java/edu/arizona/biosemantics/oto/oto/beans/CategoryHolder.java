package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoryHolder implements Serializable {

	/**
	 * hold the the terms and reviewed status when loading grouping page
	 */
	private static final long serialVersionUID = -6774186333395704149L;
	private ArrayList<Term> terms;
	private boolean finishedReviewing = false;

	public ArrayList<Term> getTerms() {
		return terms;
	}

	public void setTerms(ArrayList<Term> terms) {
		this.terms = terms;
	}

	public boolean isFinishedReviewing() {
		return finishedReviewing;
	}

	public void setFinishedReviewing(boolean finishedReviewing) {
		this.finishedReviewing = finishedReviewing;
	}

}
