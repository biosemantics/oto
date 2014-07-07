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
import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;
/**
 * This class checks if the dataset chosen has co-occured 
 * terms associated with it
 * @author Partha
 *
 */
public class CheckParsingAction extends ParserAction {

    /** Getting the instance of logger. */
    private static final Logger LOGGER = Logger
            .getLogger(CheckParsingAction.class);
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
        throws Exception {

    	if(checkSessionValidity(request)) {
        	GeneralForm gform = (GeneralForm) form;    	
        	String dataset = gform.getValue();
        	StringBuffer responseString = new StringBuffer("<response>");
        	CharacterDBAccess cdba = new CharacterDBAccess();
        	response.setContentType("text/xml");
        	try {
        		if(!cdba.checkIfDataSetTableExist(dataset)){
        			/* This hardcoded comment should be moved to ApplicationProperties*/
        			responseString.append("Dataset " + dataset + " has no co-occurred terms associated with it. " +
        			"Please choose another dataset.");
        		} else {
        			responseString.append("present");
        		}
        	} catch (Exception exe){
        		LOGGER.error("unable to check if the table exists", exe);
        	}
        	responseString.append("</response>");
        	
        	response.getWriter().write(responseString.toString());
        	return null;
    	} else {
    		return mapping.findForward(Forwardable.LOGON);
    	}

    }
}
