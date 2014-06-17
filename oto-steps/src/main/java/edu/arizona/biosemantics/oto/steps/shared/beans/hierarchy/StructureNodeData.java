package edu.arizona.biosemantics.oto.steps.shared.beans.hierarchy;

import java.io.Serializable;
import java.util.ArrayList;

public class StructureNodeData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6574914759741466603L;

	private String termID;
	private String termName;
	private String pID;
	private ArrayList<StructureNodeData> children;

	public StructureNodeData() {
		// required for gwt serializable
	}

	public StructureNodeData(String termID, String pID, String termName) {
		this.setTermID(termID);
		this.setpID(pID);
		this.termName = termName;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public ArrayList<StructureNodeData> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<StructureNodeData> children) {
		this.children = children;
	}

	public String getTermID() {
		return termID;
	}

	public void setTermID(String termID) {
		this.termID = termID;
	}

	public String getpID() {
		return pID;
	}

	public void setpID(String pID) {
		this.pID = pID;
	}

}
