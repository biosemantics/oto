package edu.arizona.biosemantics.oto.oto.form;

import org.apache.struts.action.ActionForm;

/**
 * This form is a part of the view that is used to support user login
 * @author Partha
 *
 */
public class UserLoginForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5257129346957856416L;
	private String userEmail;
	private String userPassword;
	/**
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}
	/**
	 * @param userEmail the userEmail to set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	/**
	 * @return the userPassword
	 */
	public String getUserPassword() {
		return userPassword;
	}
	/**
	 * @param userPassword the userPassword to set
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

}
