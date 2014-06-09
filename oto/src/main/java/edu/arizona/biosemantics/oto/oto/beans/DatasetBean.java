package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;

/**
 * get dataset information
 * 
 * @author Huang
 * 
 */
public class DatasetBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6185640261313527600L;
	private String name;
	private int glossaryID;
	private String glossaryName;
	private boolean categorizationFinalized;
	private String note;
	private boolean hasBeenMerged;
	private String mergedInto;
	private int numTermsInCategorizePage;
	private int numTemrsReviewedInCategorizePage;
	private boolean isSystemReserved = false;
	private boolean isPrivate = false;

	public DatasetBean() {

	}

	public DatasetBean(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCategorizationFinalized() {
		return categorizationFinalized;
	}

	public void setCategorizationFinalized(boolean categorizationFinalized) {
		this.categorizationFinalized = categorizationFinalized;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isHasBeenMerged() {
		return hasBeenMerged;
	}

	public void setHasBeenMerged(boolean hasBeenMerged) {
		this.hasBeenMerged = hasBeenMerged;
	}

	public String getMergedInto() {
		return mergedInto;
	}

	public void setMergedInto(String mergedInto) {
		this.mergedInto = mergedInto;
	}

	public int getNumTermsInCategorizePage() {
		return numTermsInCategorizePage;
	}

	public void setNumTermsInCategorizePage(int numTermsInCategorizePage) {
		this.numTermsInCategorizePage = numTermsInCategorizePage;
	}

	public int getNumTemrsReviewedInCategorizePage() {
		return numTemrsReviewedInCategorizePage;
	}

	public void setNumTemrsReviewedInCategorizePage(
			int numTemrsReviewedInCategorizePage) {
		this.numTemrsReviewedInCategorizePage = numTemrsReviewedInCategorizePage;
	}

	public int getGlossaryID() {
		return glossaryID;
	}

	public void setGlossaryID(int glossaryID) {
		this.glossaryID = glossaryID;
		this.glossaryName = GlossaryNameMapper.getInstance().getGlossaryName(
				glossaryID);
	}

	public String getGlossaryName() {
		return glossaryName;
	}

	public void setGlossaryName(String glossaryName) {
		this.glossaryName = glossaryName;
	}

	public boolean isSystemReserved() {
		return isSystemReserved;
	}

	public void setSystemReserved(boolean isSystemReserved) {
		this.isSystemReserved = isSystemReserved;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

}
