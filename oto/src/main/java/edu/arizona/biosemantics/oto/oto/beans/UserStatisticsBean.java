package edu.arizona.biosemantics.oto.oto.beans;

public class UserStatisticsBean {
	private int userid;
	private String userName;
	private int count;
	private int count_decidedTerms;

	public UserStatisticsBean(int userid, String username, int count) {
		this.userid = userid;
		this.userName = username;
		this.count = count;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount_decidedTerms() {
		return count_decidedTerms;
	}

	public void setCount_decidedTerms(int count_decidedTerms) {
		this.count_decidedTerms = count_decidedTerms;
	}
}
