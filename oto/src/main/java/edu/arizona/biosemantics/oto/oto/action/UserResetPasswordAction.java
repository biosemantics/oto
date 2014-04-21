package edu.arizona.biosemantics.oto.oto.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.db.UserDataAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class UserResetPasswordAction  extends ParserAction {
	private static final Logger LOGGER = Logger
            .getLogger(UserRegistrationAction.class);
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			GeneralForm gform = (GeneralForm) form;
			String responseText = "";
			String email = gform.getValue();
			
			UserDataAccess uds = new UserDataAccess();
			boolean rv = uds.resetPassword(email);
			
			if (rv) {
				responseText = "The system has reset your password. You should receive the new password in your email. ";
			} else {
				responseText = "The system encountered a problem when resetting your password. Please try again later. ";
			}
			
			response.setContentType("text/xml");
	    	response.getWriter().write("<response>" + responseText + "</response>");	
	    	
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Error in resetting password", exe);
			request.setAttribute(Forwardable.ERROR, exe.getCause());
			response.setContentType("text/xml");
        	response.getWriter().write("<response>Problem encountered in resetting password: " 
				+ exe.getMessage()+ "</response>");
		}			
		return null;
	}

}
