package edu.arizona.biosemantics.oto.oto.action;
/**
 * @author Partha Pratim Sanyal
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.UserDataAccess;
import edu.arizona.biosemantics.oto.oto.form.UserLoginForm;

/**
 * This class will take care of user login as appropriate
 * @author Partha
 *
 */
public class UserLoginAction extends ParserAction{

    /** Getting the instance of logger. */
    private static final Logger LOGGER = Logger
            .getLogger(UserLoginAction.class);
    
    
    @SuppressWarnings("finally")
	public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
        throws Exception {
    	
    	HttpSession session = null;
    	String forwardString = null;
    	/*User Login will be handled here*/
    	UserLoginForm uForm = (UserLoginForm) form;
    	User user = new User();
    	user.setUserEmail(uForm.getUserEmail());
    	user.setPassword(uForm.getUserPassword());
    	
    	UserDataAccess uaccess = new UserDataAccess();
    	try {
        	if(uaccess.validateUser(user)) {
        		session = request.getSession(true);
        		/*Initiate the SessionDataManager*/
            	SessionDataManager sessionDataMgr = new SessionDataManager(user);
            	/* Set the SessionDataManager to the session */
            	setSessionManager(sessionDataMgr, session);            	
            	//session.setAttribute(SessionVariables.BIOPORTAL_USER_ID.toString(), user.getBioportalUserId());
				//session.setAttribute(SessionVariables.BIOPORTAL_API_KEY.toString(), user.getBioportalApiKey());
            	forwardString = Forwardable.HOME;
        	} else { 
        		forwardString = Forwardable.RELOAD;
        		/* This hard coded values can be placed on the ApplicationProperties file later*/
        		request.setAttribute("message", "Invalid email or password! Please try again");
        	}
    	} catch (Exception exe) {
    		LOGGER.error("Unable to login", exe);
    		exe.printStackTrace();
    		forwardString = Forwardable.ERROR;
    	} finally {
    		return mapping.findForward(forwardString);
    	}
    }
}
