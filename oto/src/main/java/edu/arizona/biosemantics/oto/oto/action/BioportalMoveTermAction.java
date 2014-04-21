package edu.arizona.biosemantics.oto.oto.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class BioportalMoveTermAction extends ParserAction{

	private static final Logger LOGGER = Logger.getLogger(ApproveRevokeAction.class);
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(checkSessionValidity(request)) {
			GeneralForm gform = (GeneralForm) form;
			String term = gform.getValue();
			String glossaryType = request.getParameter("glossaryType");
			try {
				//move term 
				new CharacterDBAccess().bioportalMoveTerm(term, Integer.parseInt(glossaryType));
			
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
