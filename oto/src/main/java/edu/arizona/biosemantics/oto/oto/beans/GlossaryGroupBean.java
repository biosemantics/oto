package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class GlossaryGroupBean implements Serializable {

	/**
	 * the bean that holds a list of datasets that belongs to the same glossary
	 */
	private static final long serialVersionUID = 7585155278495640902L;
	private int glossaryID;
	private String glossaryName;
	private ArrayList<DatasetBean> datasets;

	public int getGlossaryID() {
		return glossaryID;
	}

	public void setGlossaryID(int glossaryID) {
		this.glossaryID = glossaryID;
		this.glossaryName = GlossaryNameMapper.getInstance().getGlossaryName(glossaryID);
	}

	public String getGlossaryName() {
		return glossaryName;
	}

	public void setGlossaryName(String glossaryName) {
		this.glossaryName = glossaryName;
	}

	public ArrayList<DatasetBean> getDatasets() {
		return datasets;
	}

	public void setDatasets(ArrayList<DatasetBean> datasets) {
		this.datasets = datasets;
	}

}
