package edu.arizona.biosemantics.oto.oto.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

public class ImportForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5454956517218672810L;

	private FormFile file;
	private String datasetName;
	private String taskIndex; // 1-categorization; 2-hierarchical; 3-orders

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public String getTaskIndex() {
		return taskIndex;
	}

	public void setTaskIndex(String taskIndex) {
		this.taskIndex = taskIndex;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if (getFile() == null) {
			return errors;
		}

		if (getFile().getFileSize() == 0) {
			errors.add("common.file.err", new ActionMessage(
					"error.common.file.required"));
			return errors;
		}

		System.out.println(getFile().getContentType());

		// only allow textfile to upload
		// if (!"cvs".equals(getFile().getContentType())) {
		// errors.add("common.file.err.ext", new ActionMessage(
		// "error.common.file.textfile.only"));
		// return errors;
		// }

		// // file size cant larger than 10kb
		// System.out.println(getFile().getFileSize());
		// if (getFile().getFileSize() > 10240) { // 10kb
		// errors.add("common.file.err.size", new ActionMessage(
		// "error.common.file.size.limit", 10240));
		// return errors;
		// }

		return errors;
	}

}
