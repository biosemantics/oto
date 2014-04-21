package edu.arizona.biosemantics.oto.lite.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class is to hold the data of a category
 * 
 * @author Fengqiong
 * 
 */
public class Category implements Serializable {
	private static final long serialVersionUID = -225751981230348851L;
	private String name;
	private String def;
	private ArrayList<Term> changed_terms; 	//hold all the decisions 
	private boolean userCreated = false; //default false

	public String getDef() {
		return def;
	}

	public void setDef(String definition) {
		this.def = definition;
	}

	public Category(String name) {
		this.name = name;
	}
	
	public Category () {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setChanged_terms(ArrayList<Term> changed_terms) {
		this.changed_terms = changed_terms;
	}

	public ArrayList<Term> getChanged_terms() {
		return changed_terms;
	}

	public boolean isUserCreated() {
		return userCreated;
	}

	public void setUserCreated(boolean userCreated) {
		this.userCreated = userCreated;
	}
}
