package edu.arizona.biosemantics.oto.oto.action;
/**
 * @author Partha Pratim Sanyal
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts2.ServletActionContext;
import org.json.JSONException;
import org.json.JSONObject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.Configuration;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.common.model.User;
import edu.arizona.biosemantics.oto.oto.db.UserDataAccess;
import edu.arizona.biosemantics.oto.oto.form.UserLoginForm;
import edu.arizona.biosemantics.oto.oto.form.UserLoginGoogleForm;

/**
 * Unfortunately Struts seems to cut off the #access_token=.... parameters passed by google,
 * probably because it doesnt' follow the ?param=value&param2= ... scheme.
 * There's no trace of this data anymore in HttpServletRequest. For this reason redirect to googleLoginLanding.jsp
 * which redirects with the respective info to this action.
 */
public class UserLoginGoogleAction extends ParserAction {

    /** Getting the instance of logger. */
    private static final Logger LOGGER = Logger
            .getLogger(UserLoginGoogleAction.class);
    
    private String access_token;
    
    @SuppressWarnings("finally")
	public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse servletResponse)
        throws Exception {    	
    	
    	UserLoginGoogleForm loginForm = (UserLoginGoogleForm) form;

    	URL url;
    	HttpURLConnection connection = null;
    	try {
    		url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + loginForm.getAccessToken());
    		connection = (HttpURLConnection)url.openConnection();
    		connection.setRequestMethod("GET");
    	} catch (MalformedURLException e) {
    		LOGGER.error("Malformed url", e);
    	} catch (IOException e) {
    		LOGGER.error("Couldn't read from url", e);
    	}
    	
    	if(connection != null) {
    		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = reader.readLine()) != null){
				response.append(line);
			}
			//System.out.println("Got the result: \n" + response.toString());
			
			String firstName = null;
			String lastName = null;
			String openIdProviderId = null;
			try {
				JSONObject elements = new JSONObject(response.toString());
				firstName = elements.getString("given_name");
				lastName = elements.getString("family_name");
				openIdProviderId = elements.getString("email");
			} catch(JSONException e) {
				LOGGER.error("Couldn't parse JSON", e);
			}
			
			if(firstName != null && lastName != null && openIdProviderId != null) {
				//create an account for this user if they do not have one yet.	
				String dummyPassword = firstName + lastName;
				
				User user = new User();
				user.setUserEmail(openIdProviderId);
				user.setFirstName(firstName);
				user.setLastName(lastName);
				user.setPassword(dummyPassword);

				UserDataAccess uda = new UserDataAccess();
				
				if (uda.doesEmailIdExist(user)) {
					if(uda.validateUser(user)) {
						HttpSession session = request.getSession(true);
						
						SessionDataManager sessionDataMgr = new SessionDataManager(user);
		            	setSessionManager(sessionDataMgr, session);
		            	return mapping.findForward(Forwardable.HOME);
		        	} else { 
		        		
		        		//TODO: this could also be a case where somebody was already registred with OTO with the same email
		        		// and used it now for google login. Then it will fail. What to do?
		        		request.setAttribute("message", "Validation failed, please try again!");
		        		return mapping.findForward(Forwardable.RELOAD);
		        	}
				} else {
					try {
						uda.registerUser(user);
						user = uda.getUser(user.getUserEmail());
						user.setActive(true);
						uda.updateUserStatus(user); //set active
						request.setAttribute("message", user.getFirstName() + " " + user.getLastName());
						
						HttpSession session = request.getSession(true);
		            	SessionDataManager sessionDataMgr = new SessionDataManager(user);
		            	setSessionManager(sessionDataMgr, session);            	
						
						return mapping.findForward(Forwardable.HOME);
					} catch (Exception exe) {
						exe.printStackTrace();
						LOGGER.error("Unable to register user", exe);
						request.setAttribute("message", "Unable to register : " + exe.getMessage());
						return mapping.findForward(Forwardable.RELOAD);
					}
				}
			}
    	}
    	request.setAttribute("message", "Validation failed, please try again!");
		return mapping.findForward(Forwardable.RELOAD);
	}	
    
    
}
