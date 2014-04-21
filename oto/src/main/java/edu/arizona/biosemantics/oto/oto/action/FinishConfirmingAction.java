package edu.arizona.biosemantics.oto.oto.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class FinishConfirmingAction extends ParserAction {
	private static final Logger LOGGER = Logger
			.getLogger(GetUserReportAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (checkSessionValidity(request)) {
			try {
				GeneralForm gform = (GeneralForm) form;
				SessionDataManager sessionData = getSessionManager(request);
				int userID = sessionData.getUser().getUserId();
				String responseText = "";
				String value = gform.getValue();
				String[] info = value.split("::");
				boolean success = false;
				if (info.length == 2) {
					HttpSession session = request.getSession();
					session.setAttribute("finalizeStatus", "processing");

					String dataset = info[0];
					String type = info[1];
					success = new CharacterDBAccess().finalizeDataset(dataset,
							type, userID);

					if (success)
						responseText = "success";
					else {
						responseText = "Merge datasets failed. Please try again later. ";
					}

					session.setAttribute("finalizeStatus", responseText);
				}

				response.setContentType("text/xml");
				if (!success) {
					responseText = "<response>"
							+ "Problem encountered in finalizing dataset. Please try again later. "
							+ "</response>";
				} else {
					responseText = "<response>"
							+ "Dataset finalized SUCCESSFULLY." + "</response>";
				}
				response.getWriter().write(responseText);

			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in finalizing dataset", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				response.setContentType("text/xml");
				response.getWriter().write(
						"<server-response>Problem encountered in finalizing dataset: "
								+ exe.getMessage() + "</server-response>");
			}
			return null;
		} else {
			return mapping.findForward(Forwardable.LOGON);
		}
	}

}
