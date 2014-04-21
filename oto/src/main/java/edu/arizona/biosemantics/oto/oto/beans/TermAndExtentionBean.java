package edu.arizona.biosemantics.oto.oto.beans;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TermAndExtentionBean {

	private String termWithIndex;
	private boolean hasIndex;
	private String term;
	private int index = 0;
	private String category;
	private int userid;
	private String old_term;

	/**
	 * to parse term and index
	 * @param termWithIndex
	 */
	public TermAndExtentionBean(String termWithIndex) {
		this.termWithIndex = termWithIndex;
		Pattern p = Pattern.compile("^(.*)_(\\d+)$");
		Matcher m = p.matcher(termWithIndex);
		if (m.matches()) {
			this.hasIndex = true;
			this.term = m.group(1);
			this.index = Integer.parseInt(m.group(2));
		} else {
			this.hasIndex = false;
			this.term = termWithIndex;
		}
	}

	/**
	 * to map an old term to a new term with a new index
	 * @param term
	 * @param index
	 * @param category
	 * @param userid
	 */
	public TermAndExtentionBean(String term, int index, String category, int userid, String oldTerm) {
		this.index = index;
		this.category = category;
		this.term = term;
		this.hasIndex = true;
		this.termWithIndex = term + "_" + Integer.toString(index);
		this.userid = userid;
		this.old_term = oldTerm;
	}

	public boolean hasIndex() {
		return hasIndex;
	}

	public void setHasIndex(boolean hasIndex) {
		this.hasIndex = hasIndex;
	}

	public String getTermWithIndex() {
		return termWithIndex;
	}

	public void setTermWithIndex(String termWithIndex) {
		this.termWithIndex = termWithIndex;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getOld_term() {
		return old_term;
	}

	public void setOld_term(String old_term) {
		this.old_term = old_term;
	}

}
