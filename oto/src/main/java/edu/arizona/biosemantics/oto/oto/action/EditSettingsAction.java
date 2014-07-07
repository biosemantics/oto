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
import edu.arizona.biosemantics.oto.common.security.Encryptor;
import edu.arizona.biosemantics.oto.oto.Configuration;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.UserDataAccess;
import edu.arizona.biosemantics.oto.oto.form.RegistrationForm;

public class EditSettingsAction extends ParserAction {

    /** Getting the instance of logger. */
    private static final Logger LOGGER = Logger
            .getLogger(EditSettingsAction.class);
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		
		boolean errorFlag = false;
		StringBuffer message = new StringBuffer("There are some missing information or errors in your update information: ");
		UserDataAccess uda = new UserDataAccess();
		
		if(checkSessionValidity(request)) {
			RegistrationForm regForm = (RegistrationForm) form;
			SessionDataManager sessionDataMgr = getSessionManager(request);
			User user = sessionDataMgr.getUser();
			String oldPass = Encryptor.getInstance().encrypt(regForm.getRegPassword());
			
			//validate old password
			if(!oldPass.equals(user.getPassword())) {
				message.append("Current password entered doesn't match");
				request.setAttribute("message", message.toString());
				return mapping.findForward(Forwardable.RELOAD);
			}
			
			//validate name and affiliation
			if(regForm.getFirstName() == null || regForm.getFirstName().equals("")) {
				message.append("First Name can't be blank, ");
			}
			
			if(regForm.getAffiliation() == null || regForm.getAffiliation().trim().equals("")) {
				errorFlag = true;
				message.append("Affiliation can't be blank ");
			}
			
			if (regForm.getLastName() == null || regForm.getLastName().trim().equals("")) {
				errorFlag = true;
				message.append("Last Name can't be blank, ");
			}
			
			//validate email
			if (regForm.getEmailId() == null || regForm.getEmailId().trim().equals("")) {
				errorFlag = true;
				message.append("Email can't be blank, ");
			}
			
			//validate new password
			if(regForm.getRegPassword() == null || regForm.getRegPassword().trim().equals("")) {
				errorFlag = true;
				message.append("Current Password can't be blank, ");
			}
			
			String newPassword = regForm.getNewPassword();
			String confirmPassword = regForm.getConfirmPassword();
			if(newPassword != null && !newPassword.equals("") 
					&& newPassword.length() < Configuration.getInstance().getMinPasswordLength()) {
				errorFlag = true;
				message.append("The minimum password length is "+ Configuration.getInstance().getMinPasswordLength() + " characters, ");
			}
			
			if(!newPassword.equals(confirmPassword)) {
				errorFlag = true;
				message.append("New Password doesn't match Re-type New Password, ");
			}
			
			if (errorFlag) { //generate validation error msg
				request.setAttribute("message", message.toString());
				return mapping.findForward(Forwardable.RELOAD);
			} else {
				//update new user
				User updatedUser = new User();
				updatedUser.setUserId(user.getUserId());
				
				updatedUser.setAffiliation(regForm.getAffiliation());
				
				boolean passChanged = false;
				if (newPassword != null && !newPassword.equals("")) {
					updatedUser.setPassword(newPassword);	
					passChanged = true;
				} else {
					updatedUser.setPassword(regForm.getRegPassword());
				}
				
				updatedUser.setFirstName(regForm.getFirstName());
				updatedUser.setLastName(regForm.getLastName());
				updatedUser.setUserEmail(regForm.getEmailId());
				updatedUser.setBioportalUserId(regForm.getBioportalUserId());
				updatedUser.setBioportalApiKey(regForm.getBioportalApiKey());
				try {
					
					uda.updateUserDetails(updatedUser);
					
					user.setAffiliation(updatedUser.getAffiliation());
					user.setFirstName(updatedUser.getFirstName());
					user.setLastName(updatedUser.getLastName());
					if (passChanged) {
						user.setPassword(Encryptor.getInstance().encrypt(newPassword));	
					}
					user.setUserEmail(updatedUser.getUserEmail());
					user.setBioportalUserId(updatedUser.getBioportalUserId());
					user.setBioportalApiKey(updatedUser.getBioportalApiKey());
					
					sessionDataMgr.setUser(user);
					setSessionManager(sessionDataMgr, request);
					
					request.setAttribute("message", "Your details have been updated.");
					return mapping.findForward(Forwardable.RELOAD);
				} catch(Exception exe) {
					LOGGER.error("Unable to update the user details", exe);
					exe.printStackTrace();
					request.setAttribute("message", "Unable to update user details - " + exe.getMessage());
					return mapping.findForward(Forwardable.ERROR);
				}
			}
			
		} else {
			
		}
		return mapping.findForward(Forwardable.LOGON);
	}

}
