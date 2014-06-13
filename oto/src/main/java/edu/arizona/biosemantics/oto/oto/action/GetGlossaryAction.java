package edu.arizona.biosemantics.oto.oto.action;

/**
 * Get glossaries from db and ontology lookup
 * @author Fengqiong
 */

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.ontologylookup.webservice.OlsClient;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.TermGlossaryBean;
import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class GetGlossaryAction extends ParserAction {
	/** Getting the instance of logger. */
    private static final Logger LOGGER = Logger
            .getLogger(GetGlossaryAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (checkSessionValidity(request)) {
    		try {
    			
            	GeneralForm gform = (GeneralForm) form;
            	String term = gform.getValue();
            	
            	ArrayList<TermGlossaryBean> glossaries = new ArrayList<TermGlossaryBean>();
            	CharacterDBAccess cdba = new CharacterDBAccess();
            	SessionDataManager sessionData = getSessionManager(request);
            	String dataset = sessionData.getDataset();
            	try {
            		glossaries = cdba.getGlossaryFromDB(term, dataset);
            	} catch(Exception exe){
            		exe.printStackTrace();
            		LOGGER.error("Error in retrieving glossary from db", exe);
            	}
            	
            	response.setContentType("text/xml");
            	StringBuffer responseString = new StringBuffer("<glossaries>");
            	for (TermGlossaryBean gloss : glossaries) {
            		responseString.append("<glossary>");
                	responseString.append("<category>" + gloss.getNote() + gloss.getCategory() + "</category>");
                	responseString.append("<definition>"+ gloss.getDefinition() + "</definition>");
                	responseString.append("</glossary>");
            	}
            	
            	OlsClient olsC = new OlsClient(term, "PATO");
        		if (olsC.hasData()) {
        			responseString.append("<glossary>");
                	responseString.append("<category>In 'PATO': " + olsC.getParent() + "</category>");
                	responseString.append("<definition>In 'PATO': "+ olsC.getDefinition() + "</definition>");
                	responseString.append("</glossary>");
            		
        		}
            	responseString.append("</glossaries>");
            	
            	response.getWriter().write(responseString.toString());
    		} catch (Exception exe) {
    			LOGGER.error("unable to get glossaries for term", exe);
    			exe.printStackTrace();
    		}
        	return null;
    	} else {
			System.out.println("Session invalid!");
		}
		// TODO Auto-generated method stub
		return null;
	}

}
