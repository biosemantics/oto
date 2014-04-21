package edu.arizona.biosemantics.oto.oto.action;
/**
 * @author Partha Pratim Sanyal
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.Configuration;
import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.UserDataAccess;
import edu.arizona.biosemantics.oto.oto.form.RegistrationForm;

public class UserRegistrationAction extends ParserAction {

    /** Getting the instance of logger. */
    private static final Logger LOGGER = Logger
            .getLogger(UserRegistrationAction.class);
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		boolean errorFlag = false;
		UserDataAccess uda = new UserDataAccess();
		StringBuffer message = new StringBuffer("You have some missing information or errors : ");
		RegistrationForm regForm = (RegistrationForm) form;
		User user = new User();
		if(regForm.getEmailId() != null && !regForm.getEmailId().trim().equals("")) {
			user.setUserEmail(regForm.getEmailId());
			if (uda.doesEmailIdExist(user)) {
				message.append("This email already exists in our system.");
				request.setAttribute("message", message.toString());
				request.setAttribute("show", "show");
				request.setAttribute("registrationForm", regForm);
				return mapping.findForward(Forwardable.RELOAD);
			}
		}
		if(regForm.getFirstName() == null || regForm.getFirstName().trim().equals("")) {
			errorFlag = true;
			message.append("First Name, ");
		}
		if (regForm.getLastName() == null || regForm.getLastName().trim().equals("")) {
			errorFlag = true;
			message.append("Last Name, ");
		}
		
		if (regForm.getEmailId() == null || regForm.getEmailId().trim().equals("")) {
			errorFlag = true;
			message.append("Email, ");
		}
		
		if (regForm.getConfirmEmailId() == null || regForm.getConfirmEmailId().trim().equals("")) {
			errorFlag = true;
			message.append("Confirmation Email, ");
		}
		
		if(regForm.getEmailId() != null 
				&& regForm.getConfirmEmailId() != null 
				&& !regForm.getConfirmEmailId().trim().equals(regForm.getEmailId().trim())) {
			errorFlag = true;
			message.append("Email and Confirmation Email don't match, ");
		}
		
		if(regForm.getRegPassword() == null || regForm.getRegPassword().trim().equals("")) {
			errorFlag = true;
			message.append("Password, ");
		}
		
		if(regForm.getConfirmPassword() == null || regForm.getConfirmPassword().trim().equals("")) {
			errorFlag = true;
			message.append("Confirmation Password, ");
		}
		
		if(regForm.getAffiliation() == null || regForm.getAffiliation().trim().equals("")) {
			errorFlag = true;
			message.append("Affiliation. ");
		}
		if(regForm.getConfirmPassword() != null 
				&& regForm.getRegPassword() != null 
				&& !regForm.getConfirmPassword().trim().equals(regForm.getRegPassword().trim())) {
			errorFlag = true;
			message.append("Password and confirmation password don't match, ");
		}
		
		Configuration config = Configuration.getInstance();
		if(regForm.getRegPassword() != null && regForm.getRegPassword().length() < config.getMinPasswordLength()) {
			errorFlag = true;
			message.append("The minimum password length is "+ config.getMinPasswordLength() + " characters, ");
		}

		if (errorFlag) {
			message.append(" Please try again!");
			request.setAttribute("message", message.toString());
			request.setAttribute("show", "show");
			request.setAttribute("registrationForm", regForm);
			return mapping.findForward(Forwardable.RELOAD);
		} else {
			user.setUserEmail(regForm.getEmailId());
			user.setAffiliation(regForm.getAffiliation());
			user.setPassword(regForm.getRegPassword());
			user.setFirstName(regForm.getFirstName());
			user.setLastName(regForm.getLastName());
			try {
				uda.registerUser(user);
				request.setAttribute("message", user.getFirstName() + " " + user.getLastName());
				return mapping.findForward(Forwardable.SUCCESS);
			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Unable to register user", exe);
				request.setAttribute("message", "Unable to register : " + exe.getMessage());
				return mapping.findForward(Forwardable.ERROR);
			}
			
		}
	}

}
