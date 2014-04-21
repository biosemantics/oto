package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class will be used to create Term objects
 * 
 * @author Partha
 * 
 */
public class Term implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 870875636646563970L;

	public Term(String term) {
		super();
		this.term = term;
	}

	private String term;
	private boolean removed;
	private boolean isAdditional;
	private boolean hasSyn;
	private String relatedTerms;
	private boolean hasConflict;
	private ArrayList<Term> syns;
	private String comment;
	private boolean reviewed;

	public boolean hasConflict() {
		return hasConflict;
	}

	public void setConflict(boolean conflict) {
		this.hasConflict = conflict;
	}

	public Term(String term, boolean removed) {
		super();
		this.term = term;
		this.removed = removed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (removed ? 1231 : 1237);
		result = prime * result + (isAdditional ? 1231 : 1237);
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		result = prime * result
				+ ((relatedTerms == null) ? 0 : relatedTerms.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Term))
			return false;
		Term other = (Term) obj;
		if (removed != other.removed)
			return false;
		if (isAdditional != other.isAdditional)
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		if (relatedTerms == null) {
			if (other.relatedTerms != null)
				return false;
		} else if (!relatedTerms.equals(other.relatedTerms))
			return false;
		return true;
	}

	public String getRelatedTerms() {
		return relatedTerms;
	}

	public void setRelatedTerms(String relatedTerms) {
		this.relatedTerms = relatedTerms;
	}

	public boolean isAdditional() {
		return isAdditional;
	}

	public void setIsAdditional(boolean b) {
		this.isAdditional = b;
	}

	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}
	
	public String getOriginalTermName() {
		//remove leading and tailing _ and tailing copy number (1-99)
		String t = this.term.replaceAll("(^_+)|(_+$)|(_\\d\\d?$)", "");
		return t;
	}

	/**
	 * @param term
	 *            the term to set
	 */
	public void setTerm(String term) {
		this.term = term;
	}

	/**
	 * @return the removed
	 */
	public boolean isRemoved() {
		return removed;
	}

	/**
	 * @param removed
	 *            the removed to set
	 */
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public void setHasSyn(boolean hasSyn) {
		this.hasSyn = hasSyn;
	}

	public boolean hasSyn() {
		return hasSyn;
	}

	public void setSyns(ArrayList<Term> syns) {
		this.syns = syns;
	}

	public ArrayList<Term> getSyns() {
		return syns;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isReviewed() {
		return reviewed;
	}

	public void setReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}
}
