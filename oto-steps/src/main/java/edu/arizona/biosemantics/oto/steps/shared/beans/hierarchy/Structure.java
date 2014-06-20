package edu.arizona.biosemantics.oto.steps.shared.beans.hierarchy;

import java.io.Serializable;

public class Structure implements Serializable {

	private static final long serialVersionUID = 2829643559543281759L;
	private String termName;
	private String id;
	private String pid;

	public Structure() {
		// has to be here for GWT serializabl
	}

	public Structure(String id, String pid, String name) {
		this.id = id;
		this.pid = pid;
		this.termName = name;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

}
