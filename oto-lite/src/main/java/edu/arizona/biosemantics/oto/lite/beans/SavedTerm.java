package edu.arizona.biosemantics.oto.lite.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class SavedTerm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7756082093277361806L;
	private String termName;
	private boolean isAdditional;
	private boolean hasSyns; // has synonyms
	private ArrayList<String> syns;

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public ArrayList<String> getSyns() {
		return syns;
	}

	public void setSyns(ArrayList<String> syns) {
		this.syns = syns;
	}

	public boolean isAdditional() {
		return isAdditional;
	}

	public void setAdditional(boolean isAdditional) {
		this.isAdditional = isAdditional;
	}

	public boolean isHasSyns() {
		return hasSyns;
	}

	public void setHasSyns(boolean hasSyns) {
		this.hasSyns = hasSyns;
	}
}
