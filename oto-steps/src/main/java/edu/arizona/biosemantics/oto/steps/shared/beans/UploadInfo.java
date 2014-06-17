package edu.arizona.biosemantics.oto.steps.shared.beans;

import java.io.Serializable;
import java.util.Date;

public class UploadInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7818463451261970209L;
	private int uploadID;
	private Date uploadTime;
	private boolean hasSentToOTO;
	private boolean isFinalized;
	private String prefixForOTO;
	private boolean readyToDelete;
	private int glossaryType;
	private String glossaryTypeName;
	private String bioportalUserID;
	private String bioportalApiKey;
	private String EtcUserName;
	private boolean hasBioportalInfo;
	private String source;

	public UploadInfo() {

	}

	public UploadInfo(int uploadID) {
		this.uploadID = uploadID;
	}

	public int getUploadID() {
		return uploadID;
	}

	public void setUploadID(int uploadID) {
		this.uploadID = uploadID;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public boolean isHasSentToOTO() {
		return hasSentToOTO;
	}

	public void setHasSentToOTO(boolean hasSentToOTO) {
		this.hasSentToOTO = hasSentToOTO;
	}

	public boolean isFinalized() {
		return isFinalized;
	}

	public void setFinalized(boolean isFinalized) {
		this.isFinalized = isFinalized;
	}

	public String getPrefixForOTO() {
		return prefixForOTO;
	}

	public void setPrefixForOTO(String prefixForOTO) {
		this.prefixForOTO = prefixForOTO;
	}

	public boolean isReadyToDelete() {
		return readyToDelete;
	}

	public void setReadyToDelete(boolean readyToDelete) {
		this.readyToDelete = readyToDelete;
	}

	public int getGlossaryType() {
		return glossaryType;
	}

	public void setGlossaryType(int glossaryType) {
		this.glossaryType = glossaryType;
	}

	public String getBioportalUserID() {
		return bioportalUserID;
	}

	public void setBioportalUserID(String bioportalUserID) {
		this.bioportalUserID = bioportalUserID;
		setHasBioportalInfo();
	}

	public String getEtcUserName() {
		return EtcUserName;
	}

	public void setEtcUserName(String etcUserName) {
		EtcUserName = etcUserName;
	}

	public String getBioportalApiKey() {
		return bioportalApiKey;
	}

	public void setBioportalApiKey(String bioportalApiKey) {
		this.bioportalApiKey = bioportalApiKey;
		setHasBioportalInfo();
	}

	public boolean isHasBioportalInfo() {
		return hasBioportalInfo;
	}

	public void setHasBioportalInfo(boolean hasBioportalInfo) {
		this.hasBioportalInfo = hasBioportalInfo;
	}

	private void setHasBioportalInfo() {
		if (bioportalUserID != null && !bioportalUserID.equals("")
				&& !bioportalUserID.equals("null") && bioportalApiKey != null
				&& !bioportalApiKey.equals("")
				&& !bioportalApiKey.equals("null")) {
			this.hasBioportalInfo = true;
		}
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getGlossaryTypeName() {
		return glossaryTypeName;
	}

	public void setGlossaryTypeName(String glossaryTypeName) {
		this.glossaryTypeName = glossaryTypeName;
	}

}
