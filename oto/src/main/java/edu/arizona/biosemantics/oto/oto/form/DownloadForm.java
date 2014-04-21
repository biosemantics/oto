package edu.arizona.biosemantics.oto.oto.form;

import org.apache.struts.action.ActionForm;

public class DownloadForm extends ActionForm {
	private static final long serialVersionUID = -9156540879042440814L;
	private String dataset;
	private String downloadType;
	private String fileType;

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public String getDownloadType() {
		return downloadType;
	}

	public void setDownloadType(String downloadType) {
		this.downloadType = downloadType;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}
