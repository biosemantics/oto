package edu.arizona.biosemantics.oto.oto.action;
/**
 * @author Partha Pratim Sanyal
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.UserDataAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class CheckDuplicateEmailAction extends ParserAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		
		GeneralForm gform = (GeneralForm) form;
		String emailId = gform.getValue();
		User user = new User();
		user.setUserEmail(emailId);
		boolean exists = false;
		
		if(checkSessionValidity(request)) {
			User currentUser = getSessionManager(request).getUser();
			if(currentUser.getUserEmail().equals(user.getUserEmail())) {
				return null;
			}
		}

		StringBuffer responseString = new StringBuffer("<response>");
		try {
			exists = new UserDataAccess().doesEmailIdExist(user);
		} catch (Exception exe) {
			exe.printStackTrace();
		}
		response.setContentType("text/xml");
		if(exists){
			responseString.append("Email - " + user.getUserEmail() + " already exists in our system. " +
					"Please try another one!</response>");
			
		} else {
			responseString.append("Available</response>");
		}
		response.getWriter().write(responseString.toString());
		
		return null;
	}

}
