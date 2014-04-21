package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class is to hold the data of a category
 * 
 * @author Fengqiong
 * 
 */
public class CategoryBean implements Serializable {
	private static final long serialVersionUID = -225751981230348851L;
	private String name;
	private String def;
	private ArrayList<String> terms;
	private ArrayList<Term> changed_terms;

	public String getDef() {
		return def;
	}

	public void setDef(String definition) {
		this.def = definition;
	}

	public CategoryBean(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getTerms() {
		return terms;
	}

	public void setTerms(ArrayList<String> terms) {
		this.terms = terms;
	}

	public void setChanged_terms(ArrayList<Term> changed_terms) {
		this.changed_terms = changed_terms;
	}

	public ArrayList<Term> getChanged_terms() {
		return changed_terms;
	}
}
