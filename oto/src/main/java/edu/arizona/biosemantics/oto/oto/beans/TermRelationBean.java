package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;

public class TermRelationBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2371334829228148809L;
	
	private String term1;
	private String term2;
	private int relation;
	private String decision;
	private int action; // 0-to remove; 1-to add
	
	public TermRelationBean(String term1, String term2, int relation) {
		super();
		this.term1 = term1;
		this.term2 = term2;
		this.relation = relation;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (relation == 1 ? 1231 : 1237);
		result = prime * result + ((term1 == null) ? 0 : term1.hashCode());
		result = prime * result + ((term2 == null) ? 0 : term2.hashCode());
		result = prime * result + ((decision == null) ? 0 : decision.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TermRelationBean))
			return false;
		TermRelationBean other = (TermRelationBean) obj;
		if (relation != other.relation) 
			return false;
		
		if (term1 == null) {
			if (other.term1 != null)
				return false;
		} else if (!term1.equals(other.term1))
			return false;
		
		if (term2 == null) {
			if (other.term2 != null)
				return false;
		} else if (!term2.equals(other.term2))
			return false;
		
		if (decision == null) {
			if (other.decision != null)
				return false;
		} else if (!decision.equals(other.decision))
			return false;
		return true;
	}
	
	public String getDecision() {
		return decision;
	}
	
	public void setDecision(String decision) {
		this.decision = decision;
	}
	
	public String getTerm1(){
		return term1;
	}
	
	public void setTerm1(String term) {
		this.term1 = term;
	}
	
	public String getTerm2() {
		return term2;
	}
	
	public void setTerm2(String term) {
		this.term2 = term;
	}
	
	public int getRelation() {
		return relation;
	}
	
	public void setRelation(int r) {
		this.relation = r;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getAction() {
		return action;
	}
}
