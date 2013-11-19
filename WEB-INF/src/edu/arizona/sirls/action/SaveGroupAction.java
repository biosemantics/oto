package edu.arizona.sirls.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.sirls.beans.DecisionHolder;
import edu.arizona.sirls.db.CategorizationDBAccess;
import edu.arizona.sirls.form.GeneralForm;
import edu.arizona.sirls.util.Forwardable;
import edu.arizona.sirls.util.Utilities;

/**
 * This class saves the group information along with the decision to the
 * database
 * 
 * @author Partha
 * 
 */
public class SaveGroupAction extends ParserAction {

	private static final Logger LOGGER = Logger
			.getLogger(SaveGroupAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GeneralForm gform = (GeneralForm) form;
		String requestXML = gform.getValue();

		try {
			DecisionHolder dh = Utilities.getInstance().parseGroupingXML(
					requestXML);
			HttpSession session = request.getSession();
			session.setAttribute("uploadID", dh.getUploadID());
			request.setAttribute("uploadID", dh.getUploadID());

			if (dh.getNew_categories().size() > 0) {
				CategorizationDBAccess.getInstance().addNewCategory(
						dh.getNew_categories(), dh.getUploadID());
			}

			// save categorizing decisions
			boolean success = CategorizationDBAccess.getInstance()
					.saveCategorizingDecisions(dh.getRegular_categories(),
							dh.getUploadID());
			response.setContentType("text/xml");
			if (success) {
				response.getWriter().write("success");
			} else {
				response.getWriter().write("failed");
			}

			// this can be done at uploading part
			CategorizationDBAccess.getInstance().cleanUpUploads();
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Error in saving the decisions", exe);
			request.setAttribute(Forwardable.ERROR, exe.getCause());
			return mapping.findForward(Forwardable.ERROR);
		}
		return null;
	}

	public static void main(String[] args) throws Exception {

	}

}
