package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * This class represents an entity in the TermsGroup table
 * @author Partha
 *
 */
public class TermsGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7119691694739848239L;
	private int group;
	private ArrayList<Term> termsInGroup; //added by huangfq: termsGroup only need the group id and terms list
	
	public ArrayList<Term> GetTermsInGroup() {
		return termsInGroup;
	}
	public void setTermsInGroup(ArrayList<Term> terms) {
		this.termsInGroup = terms;
	}
	
	/**
	 * @return the group
	 */
	public long getGroup() {
		return group;
	}
	
	
	/**
	 * @param group the group to set
	 */
	public void setGroup(int group) {
		this.group = group;
	}
	
	/**
	 * @param group
	 */
	public TermsGroup(int group) {
		super();
		this.group = group;
	}
}
