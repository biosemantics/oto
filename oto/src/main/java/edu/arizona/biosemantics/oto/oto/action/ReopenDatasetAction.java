package edu.arizona.biosemantics.oto.oto.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class ReopenDatasetAction  extends ParserAction{
	private static final Logger LOGGER = Logger
			.getLogger(GetUserReportAction.class);
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (checkSessionValidity(request)) {
			try {
				GeneralForm gform = (GeneralForm) form;
				String responseText = "";
				String value = gform.getValue();
				String[] info = value.split("::");
				if (info.length == 2) {
					String dataset = info[0];
					String type = info[1];
					new CharacterDBAccess().reopenDataset(dataset, type); 
				}	
				response.setContentType("text/xml");
		    	response.getWriter().write(responseText);
		    	
			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in reopenning the dataset", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				response.setContentType("text/xml");
            	response.getWriter().write("<server-response>Problem encountered in reopenning the dataset : " 
    				+ exe.getMessage()+ "</server-response>");
			}			
			return null;
		} else {
			return mapping.findForward(Forwardable.LOGON);
		}
	}

}
