package edu.arizona.biosemantics.oto.lite.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3991188422262670516L;

	private String groupName;
	private int groupID;
	private ArrayList<String> terms;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getGroupID() {
		return groupID;
	}

	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}

	public ArrayList<String> getTerms() {
		return terms;
	}

	public void setTerms(ArrayList<String> terms) {
		this.terms = terms;
	}

}
