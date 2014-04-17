package edu.arizona.sirls.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import edu.arizona.sirls.beans.SessionDataManager;
import edu.arizona.sirls.beans.User;
import edu.arizona.sirls.bioportal.TermsToOntologiesClient;

public class BioportalCheckApprovedtermsAction extends ParserAction{
	private static final Logger LOGGER = Logger.getLogger(ApproveRevokeAction.class);
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(checkSessionValidity(request)) {
			try {
				SessionDataManager sessionData = getSessionManager(request);
				User user = sessionData.getUser();
				TermsToOntologiesClient bioportalClient = new TermsToOntologiesClient(
						user.getBioportalUserId(), user.getBioportalApiKey());
				int count = bioportalClient.getPermanentIDs();
				
				response.setContentType("text/xml");
				if (count >= 0) {
					response.getWriter().write("<response>" + count + "</response>");	
				} else {
					response.getWriter().write("<response>Error in updating approved terms. Please try again later. </response>");
				}
			
			} catch(Exception exe) {
				exe.printStackTrace();
				LOGGER.error("unable to update adopted terms", exe);
				response.getWriter().write("<response>Error: The application encountered an error while processing your request</response>");
			}
		} else {
			response.getWriter().write("<response>Error: Your session has expired</response>");
		}
		return null;
	}

}
