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

import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.UserDataAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class ApproveRevokeAction extends ParserAction {

	private static final Logger LOGGER = Logger.getLogger(ApproveRevokeAction.class);
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		if(checkSessionValidity(request)) {
			GeneralForm gform = (GeneralForm) form;
			String [] requestParameters = gform.getValue().split(" ");
			User user = new User();
			user.setUserId(Integer.parseInt(requestParameters[0]));
			user.setActive(requestParameters[1].equals("false")?false:true);
			UserDataAccess  uda = new UserDataAccess();
			response.setContentType("text/xml");
			try {
				uda.updateUserStatus(user);
				response.getWriter().write("<response>"+user.getUserId()+"</response>");
			} catch(Exception exe) {
				exe.printStackTrace();
				LOGGER.error("unable to update user status", exe);
				response.getWriter().write("<response>Error: The application encountered an error while processing your request</response>");
			}
		} else {
			response.getWriter().write("<response>Error: Your session has expired</response>");
		}

		return null;
	}

}
