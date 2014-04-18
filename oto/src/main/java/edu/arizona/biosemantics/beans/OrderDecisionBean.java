package edu.arizona.biosemantics.beans;

import java.util.HashMap;
import java.util.ArrayList;

import edu.arizona.biosemantics.beans.Character;

/**
 * for manageOrder page
 * 
 * @author Fengqiong
 * 
 */
public class OrderDecisionBean {
	private String userName;
	private HashMap<Integer, ArrayList<Character>> decisions;

	public OrderDecisionBean(String name) {
		userName = name;
	}

	
	public void setDecisions(HashMap<Integer, ArrayList<Character>> decisions) {
		this.decisions = decisions;
	}

	public HashMap<Integer, ArrayList<Character>> getDecisions() {
		return decisions;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getUserName() {
		return userName;
	}
}
