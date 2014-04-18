package edu.arizona.biosemantics.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.beans.ManagerDecisionBean;
import edu.arizona.biosemantics.db.CharacterDBAccess;
import edu.arizona.biosemantics.form.GeneralForm;
import edu.arizona.biosemantics.util.Utilities;

public class saveDecisionByManager extends ParserAction {
	private static final Logger LOGGER = Logger
			.getLogger(saveDecisionByManager.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		// TODO Auto-generated method stub
		if (checkSessionValidity(request)) {
			GeneralForm gform = (GeneralForm) form;
			String requestXML = gform.getValue();
			Utilities utilities = new Utilities();
			try {
				String type = utilities.parseType(requestXML);
				CharacterDBAccess cdba = new CharacterDBAccess();
				ManagerDecisionBean mdb = utilities.parseManagerDecision(
						requestXML, type);
				boolean success = false;
				// parse type
				if (type.equals("1")) {
					// save category into fnaglossary (accepted ones) or
					// confirmed_category (declined ones)
					success = cdba.confirmCategory(mdb);
				} else if (type.equals("2")) {
					// save path into confirmed_paths

					success = cdba.confirmPath(mdb);
				} else if (type.equals("3")) {
					// save path into confirmed_orders
					success = cdba.confirmOrder(mdb);
				} else if (type.equals("4")) {
					// confirm synonym
					success = cdba.confirmSynonym(mdb);
				} else {
					// do nothing
				}

				response.setContentType("text/xml");
				if (success) {
					response.getWriter().write(
							"<response>Saved successfully!</response>");
				} else {
					response.getWriter()
							.write("<response>ERROR: Problem encountered in saving the decision. Plese try again later. </response>");
				}

			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in saving decision by manager", exe);
				response.setContentType("text/xml");
				response.getWriter()
						.write("<response>"
								+ "ERROR: Problem encountered in saving the decision : "
								+ exe + "</response>");
			}
			return null;
		} else
			return null;
	}

}
