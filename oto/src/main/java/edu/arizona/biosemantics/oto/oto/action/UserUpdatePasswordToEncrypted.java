package edu.arizona.biosemantics.oto.oto.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.oto.db.UserDataAccess;

public class UserUpdatePasswordToEncrypted  extends ParserAction {

	private static final Logger LOGGER = Logger
            .getLogger(UserRegistrationAction.class);
	

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			UserDataAccess uds = new UserDataAccess();
			uds.updatePlainPassword();
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Error in updating password", exe);
		}			
		return null;
	}

}
