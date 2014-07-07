package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * hold the 4 lists of terms
 * 
 * @author Huang
 * 
 */
public class TermsForBioportalBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9034577577469775118L;
	private ArrayList<String> regStructures; // Structures in the regular list
	private ArrayList<String> regCharacters; // Characters in the regular list
	private ArrayList<String> removedStructures;
	private ArrayList<String> removedCharacters;
	
	//the construction func
	public TermsForBioportalBean(ArrayList<String> regS, ArrayList<String> regC, 
			ArrayList<String> removedS, ArrayList<String> removedC) {
		this.regStructures = regS;
		this.regCharacters = regC;
		this.removedStructures = removedS;
		this.removedCharacters = removedC;
	}
	
	public TermsForBioportalBean () {
		
	}

	public ArrayList<String> getRemovedCharacters() {
		return removedCharacters;
	}

	public void setRemovedCharacters(ArrayList<String> removedCharacters) {
		this.removedCharacters = removedCharacters;
	}

	public ArrayList<String> getRemovedStructures() {
		return removedStructures;
	}

	public void setRemovedStructures(ArrayList<String> removedStructures) {
		this.removedStructures = removedStructures;
	}

	public ArrayList<String> getRegCharacters() {
		return regCharacters;
	}

	public void setRegCharacters(ArrayList<String> regCharacters) {
		this.regCharacters = regCharacters;
	}

	public ArrayList<String> getRegStructures() {
		return regStructures;
	}

	public void setRegStructures(ArrayList<String> regStructures) {
		this.regStructures = regStructures;
	}
}
