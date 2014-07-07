package edu.arizona.biosemantics.oto.oto.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class CheckDatasetExistenceAction extends ParserAction {
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
				String datasetName = gform.getValue();

				CharacterDBAccess cdba = new CharacterDBAccess();
				if (cdba.isPrefixExist(datasetName)) {
					SessionDataManager sessionData = getSessionManager(request);
					User user = sessionData.getUser();
					if (cdba.isConfirmed(datasetName, 1)) {
						responseText = "yes-rename";
					} else {
						if (user.getRole().equals("A")) {
							responseText = "yes-merge"; //can merge into
						} else {
							if (cdba.hasRightToDataset(datasetName, user)) {
								responseText = "yes-merge"; //can merge into
							} else {
								responseText = "yes-rename"; //don't have modify right to dataset, has to rename
							}
						}	
					}
									
				} else {
					responseText = "no";
				}

				response.setContentType("text/xml");
				response.getWriter().write(
						"<response>" + responseText + "</response>");

			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in checking dataset existence", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				response.setContentType("text/xml");
				response.getWriter().write(
						"<response>Problem encountered in checking dataset existence: "
								+ exe.getMessage() + "</response>");
			}
			return null;
		} else {
			return null;
		}
	}

}
