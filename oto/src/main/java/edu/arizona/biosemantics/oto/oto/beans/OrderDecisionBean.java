package edu.arizona.biosemantics.oto.oto.beans;

import java.util.ArrayList;
import java.util.HashMap;

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
