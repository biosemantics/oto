package edu.arizona.biosemantics.action;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.beans.Order;
import edu.arizona.biosemantics.beans.SessionDataManager;
import edu.arizona.biosemantics.beans.User;
import edu.arizona.biosemantics.db.CharacterDBAccess;
import edu.arizona.biosemantics.form.GeneralForm;
import edu.arizona.biosemantics.util.Forwardable;
import edu.arizona.biosemantics.util.Utilities;

/**
 * save order action - AJAX
 * 
 * @author Fengqiong
 * 
 */
public class SaveOrderAction extends ParserAction {
	private static final Logger LOGGER = Logger
			.getLogger(SaveOrderAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception, SQLException {
		// TODO Auto-generated method stub
		if (checkSessionValidity(request)) {
			GeneralForm gform = (GeneralForm) form;
			String requestXML = gform.getValue();
			SessionDataManager sessionDataMgr = getSessionManager(request);
			String dataPrefix = sessionDataMgr.getDataset();
			Utilities utilities = new Utilities();
			User user = sessionDataMgr.getUser();
			try {
				CharacterDBAccess cdba = new CharacterDBAccess();
				Order order = utilities.getOrderFromParsingXML(requestXML);
				String rValue = cdba.saveOrder(order, dataPrefix, user);
				response.setContentType("text/xml");
				response.getWriter().write(
						"<server-response>" + rValue + "</server-response>");
			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in saving order", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				// return mapping.findForward(Forwardable.ERROR);
				response.setContentType("text/xml");
				response.getWriter().write(
						"<server-response>Problem encountered in saving the order : "
								+ exe.getMessage() + "</server-response>");
			}

			// return mapping.findForward(Forwardable.LOGON);
			// return mapping.findForward(Forwardable.RELOAD);
			return null;
		} else {
			// return mapping.findForward(Forwardable.LOGON);
			return null;
		}
	}
}
