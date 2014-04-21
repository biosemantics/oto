package edu.arizona.biosemantics.oto.oto.beans;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionBean implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7440633863007023254L;
	private int primaryVersion;
	private int secondaryVersion;

	public int getPrimaryVersion () {
		return this.primaryVersion;
	}
	
	public void setPrimaryVersion ( int primary) {
		this.primaryVersion = primary;
	}
	
	public int getSecondaryVersion () {
		return this.secondaryVersion;
	}
	
	public void setSecondaryVersion(int second) {
		this.secondaryVersion = second;
	}
	
	public VersionBean(int primary, int secondary) {
		this.primaryVersion = primary;
		this.secondaryVersion = secondary;
	}

	/**
	 * construct version from version number in string
	 * @param versionString
	 */
	public VersionBean(String versionString) {
		Pattern p = Pattern.compile("^v?(\\d+)\\.(\\d+)$");
		Matcher m = p.matcher(versionString);
		if (m.matches()) {
			this.primaryVersion = Integer.parseInt(m.group(1));
			this.secondaryVersion = Integer.parseInt(m.group(2));
		}
	}

	public String toString() {
		return "v" + primaryVersion + "." + secondaryVersion;
	}
}
