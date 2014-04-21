package edu.arizona.biosemantics.oto.oto.beans;
import java.io.Serializable;

/**
 * This class is to hold the term data for the hierarchy page
 * use Tag instead of term in the hierarchy page
 * @author Fengqiong
 *
 */
public class TagBean implements Serializable {
	private static final long serialVersionUID = -7409959263450109469L;
	private int id;
	private String name;
	private boolean decided;
	private boolean hasConflict;
	
	public boolean isDecided() {
		return decided;
	}
	
	public void setDecided(boolean decided) {
		this.decided = decided;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	public long getID() {
		return id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public TagBean(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id * prime;
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
		TagBean other = (TagBean) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public void setHasConflict(boolean hasConflict) {
		this.hasConflict = hasConflict;
	}

	public boolean hasConflict() {
		return hasConflict;
	}
}
