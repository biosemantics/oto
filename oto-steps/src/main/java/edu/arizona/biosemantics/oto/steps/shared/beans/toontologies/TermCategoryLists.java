package edu.arizona.biosemantics.oto.steps.shared.beans.toontologies;

import java.io.Serializable;
import java.util.ArrayList;

public class TermCategoryLists implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3119700508647537853L;
	private ArrayList<TermCategoryPair> regularStructures;
	private ArrayList<TermCategoryPair> regularCharacters;
	private ArrayList<TermCategoryPair> removedStructures;
	private ArrayList<TermCategoryPair> removedCharacters;

	public TermCategoryLists() {
		// costructor with no argument, must be here for GWT Serializable
	}

	public TermCategoryLists(ArrayList<TermCategoryPair> regularStructures) {
		this.regularStructures = regularStructures;
	}

	public ArrayList<TermCategoryPair> getRegularStructures() {
		return regularStructures;
	}

	public void setRegularStructures(
			ArrayList<TermCategoryPair> regularStructures) {
		this.regularStructures = regularStructures;
	}

	public ArrayList<TermCategoryPair> getRegularCharacters() {
		return regularCharacters;
	}

	public void setRegularCharacters(
			ArrayList<TermCategoryPair> regularCharacters) {
		this.regularCharacters = regularCharacters;
	}

	public ArrayList<TermCategoryPair> getRemovedStructures() {
		return removedStructures;
	}

	public void setRemovedStructures(
			ArrayList<TermCategoryPair> removedStructures) {
		this.removedStructures = removedStructures;
	}

	public ArrayList<TermCategoryPair> getRemovedCharacters() {
		return removedCharacters;
	}

	public void setRemovedCharacters(
			ArrayList<TermCategoryPair> removedCharacters) {
		this.removedCharacters = removedCharacters;
	}
}
