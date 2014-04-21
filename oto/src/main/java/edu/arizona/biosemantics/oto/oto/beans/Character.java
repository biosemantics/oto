package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;

/**
 * This class is to hold the data of term in the terms order page. 
 * use character instead of term in the terms order page
 * @author Fengqiong
 *
 */
public class Character implements Serializable{
	private static final long serialVersionUID = 8000746907878776626L;
	private int id;
	private String name;
	private boolean isBase;
	private int distance;
	private boolean accepted;
	
	public Character(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Character(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id * prime;
		result = prime * result + distance * prime;
		result = prime * result + ((isBase == false) ? 0 : 1);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TagBean))
			return false;
		Character other = (Character) obj;
		if (id != other.id)
			return false;
		if (distance != other.distance)
			return false;
		if (isBase != other.isBase)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isBase() {
		return isBase;
	}
	public void setIsBase(boolean isBase) {
		this.isBase = isBase;
	}
	
	public int getDistance() {
		return distance;
	}
	public void setDistance(int d) {
		this.distance = d;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public boolean accepted() {
		return accepted;
	}
	
}
