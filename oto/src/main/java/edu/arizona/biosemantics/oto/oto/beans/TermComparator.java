package edu.arizona.biosemantics.oto.oto.beans;

import java.util.Comparator;

public class TermComparator implements Comparator<Term>{

	@Override
	public int compare(Term term0, Term term1) { 
		return term0.getTerm().compareTo(term1.getTerm());
	}

}
