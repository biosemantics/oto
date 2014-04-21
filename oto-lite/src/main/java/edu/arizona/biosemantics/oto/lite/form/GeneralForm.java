package edu.arizona.biosemantics.oto.lite.form;

import org.apache.struts.action.ActionForm;

/**
 * This is a form that is for general use throughout the application
 * @author Partha
 *
 */
public class GeneralForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6499438479514125010L;
	private String value;
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	

}
