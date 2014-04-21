package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class StructureNodeBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5476004136898238892L;
	private long ID;
	private long pID;
	private String name;
	private boolean isLeaf;
	private ArrayList<StructureNodeBean> childenList;
	private StructureNodeBean parantNode;
	private boolean isFirstChild;
	private boolean isRoot;
	private boolean isLastChild;
	private String path;
	private boolean removeFromSrc;
	private String decisionDate;
	private boolean accepted;
	private boolean decided;
	
	public String getDecisionDate() {
		return decisionDate;
	}
	public void setDecisionDate(String date) {
		this.decisionDate = date;
	}

	
	public boolean removeFromSrc() {
		return removeFromSrc;
	}
	
	public void setRemoveFromSrc(boolean remove) {
		this.removeFromSrc = remove;
	}
	
	public long getPID() {
		return pID;
	}
	
	public void setPID(long pid) {
		this.pID = pid;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public boolean IsFirstChild() {
		return isFirstChild;
	}
	
	public void setIsFirstChild(boolean isFirstChild) {
		this.isFirstChild = isFirstChild;
	}
	
	public boolean IsLastChild() {
		return isLastChild;
	}
	
	public void setIsLastChild(boolean isLastChild) {
		this.isLastChild = isLastChild;
	}
	
	public boolean IsRoot() {
		return isRoot;
	}
	
	public void setIsRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	
	public StructureNodeBean getParentNode() {
		return this.parantNode;
	}
	
	public void setParentNode(StructureNodeBean parentNode) {
		this.parantNode = parentNode;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getID(){
		return ID;
	}
	
	public void setID(long ID) {
		this.ID = ID;
	}
	
	public boolean getIsLeaf() {
		return isLeaf;
	}
	
	public void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	
	public ArrayList<StructureNodeBean> getChildenList() {
		return this.childenList;
	}
	
	public void setChildenList(ArrayList<StructureNodeBean> childenList) {
		this.childenList = childenList;
	}
	
	public StructureNodeBean(long id, String name) {
		this.ID = id;
		this.name = name;
	}
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
	public boolean isAccepted() {
		return accepted;
	}
	public void setDecided(boolean de) {
		this.decided = de;
	}
	public boolean isDecided() {
		return decided;
	}

}
