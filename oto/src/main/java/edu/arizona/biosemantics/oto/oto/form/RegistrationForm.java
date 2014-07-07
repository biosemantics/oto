package edu.arizona.biosemantics.oto.oto.form;

import org.apache.struts.action.ActionForm;

/**
 * This form is for the user registration module
 * @author Partha
 *
 */
public class RegistrationForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8324445447123052506L;

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}
	/**
	 * @param emailId the emailId to set
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	/**
	 * @return the confirmEmailId
	 */
	public String getConfirmEmailId() {
		return confirmEmailId;
	}
	/**
	 * @param confirmEmailId the confirmEmailId to set
	 */
	public void setConfirmEmailId(String confirmEmailId) {
		this.confirmEmailId = confirmEmailId;
	}
	/**
	 * @return the regPassword
	 */
	public String getRegPassword() {
		return regPassword;
	}
	/**
	 * @param regPassword the regPassword to set
	 */
	public void setRegPassword(String regPassword) {
		this.regPassword = regPassword;
	}
	/**
	 * @return the confirmPassword
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}
	/**
	 * @param confirmPassword the confirmPassword to set
	 */
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	/**
	 * @return the affiliation
	 */
	public String getAffiliation() {
		return affiliation;
	}
	/**
	 * @param affiliation the affiliation to set
	 */
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getBioportalUserId() {
		return bioportalUserId;
	}
	public void setBioportalUserId(String bioportalUserId) {
		this.bioportalUserId = bioportalUserId;
	}
	public String getBioportalApiKey() {
		return bioportalApiKey;
	}
	public void setBioportalApiKey(String bioportalApiKey) {
		this.bioportalApiKey = bioportalApiKey;
	}
	private String firstName;
	private String lastName;
	private String emailId;
	private String confirmEmailId;
	private String regPassword;
	private String newPassword;
	private String confirmPassword;
	private String affiliation;
	private String bioportalUserId;
	private String bioportalApiKey;

}
