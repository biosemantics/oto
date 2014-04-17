package edu.arizona.sirls.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.sirls.beans.SessionDataManager;
import edu.arizona.sirls.beans.User;
import edu.arizona.sirls.beans.StructureNodeBean;

import edu.arizona.sirls.db.CharacterDBAccess;
import edu.arizona.sirls.form.GeneralForm;
import edu.arizona.sirls.util.Forwardable;
import edu.arizona.sirls.util.Utilities;
/**
 * save hierarchy tree action - directly save to database and reload page.
 * @author Fengqiong
 *
 */
public class SaveTreeAction extends ParserAction {
	private static final Logger LOGGER = Logger.getLogger(SaveTreeAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (checkSessionValidity(request)) {
			GeneralForm gform = (GeneralForm) form;
			String requestXML = gform.getValue();
			SessionDataManager sessionDataMgr = getSessionManager(request);
			String dataPrefix = sessionDataMgr.getDataset();
			Utilities utilities = new Utilities();
			User user = sessionDataMgr.getUser();
			try {
				//parse requestXML and save to db
				ArrayList<StructureNodeBean> nodes = utilities
						.parseTreeXML(requestXML);
				CharacterDBAccess cdba = new CharacterDBAccess();
				
				boolean isResend = cdba.isResendingTree(dataPrefix, user, nodes);
				if (!isResend) {
					cdba.saveHierarchyTree(nodes, dataPrefix, user);
				}
				request.setAttribute("message", "Data saved successfully!");

			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in saving the hierarchy tree", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				return mapping.findForward(Forwardable.ERROR);
			}

			return mapping.findForward(Forwardable.RELOAD);
		} else {
			return mapping.findForward(Forwardable.LOGON);
		}
	}

}
