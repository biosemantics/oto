package edu.arizona.biosemantics.oto.oto.action;
/**
 * @author Partha Pratim Sanyal
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.User;
/**
 * This abstract class serves as the base class of all the Action classes.
 * It contains a set of methods that could be used to access the session related 
 * information of the user
 * @author Partha
 *
 */
public abstract class ParserAction extends Action {

	/* This variable holds the name of the SessionDataManager used as a session identifier*/
	private static final String sessionDataMgrName = "sessionDataMgr";
	/**
	 * This method checks if the user session is valid
	 * @param request
	 * @return
	 */
	protected final boolean checkSessionValidity(HttpServletRequest request) {
		
		boolean returnValue = false;
		HttpSession session = request.getSession(false);
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute(sessionDataMgrName));
		if (sessionDataManager != null) {
			returnValue = true;
		}
		return returnValue;
	}

	/**
	 * This method is a one stop destination of getting the SessdionDataManager from the session
	 * @param request
	 * @return
	 */
	protected final SessionDataManager getSessionManager(
			HttpServletRequest request) {
		SessionDataManager sessionDataMgr = (SessionDataManager) request
				.getSession(false).getAttribute(sessionDataMgrName);
		return sessionDataMgr;
	}
	
	/**
	 * This method will set the SessionDataManager to the request's session
	 * @param sessionDataMgr
	 * @param request
	 */
	protected final void setSessionManager(SessionDataManager sessionDataMgr , HttpServletRequest request) {
		HttpSession session  = request.getSession(false);
		session.setAttribute(sessionDataMgrName, sessionDataMgr);
	}
	
	/**
	 * This method sets the SessionDataManager to the session
	 * @param sessionDataMgr
	 * @param session
	 */
	protected final void setSessionManager(SessionDataManager sessionDataMgr , HttpSession session) {
		session.setAttribute(sessionDataMgrName, sessionDataMgr);
	}
	
	/**
	 * This method will return the user information for the current session
	 * @param request
	 * @return
	 */
	protected final User getUser(HttpServletRequest request) {
		SessionDataManager sessionDataMgr = (SessionDataManager) request
		.getSession(false).getAttribute(sessionDataMgrName);
		return sessionDataMgr.getUser();
	}

	/**
	 * This is an abstract method that has to be implemented by the subclasses as per requirement
	 */
	public abstract ActionForward execute(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception;
}
