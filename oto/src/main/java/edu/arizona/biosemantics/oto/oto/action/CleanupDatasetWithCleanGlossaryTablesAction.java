package edu.arizona.biosemantics.oto.oto.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.db.GeneralDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class CleanupDatasetWithCleanGlossaryTablesAction extends ParserAction {
	private static final Logger LOGGER = Logger
			.getLogger(SetDatasetPrivacyAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if (checkSessionValidity(request)) {
			try {
				GeneralForm gform = (GeneralForm) form;
				String responseText = "";

				// get request variables
				SessionDataManager sdm = getSessionManager(request);
				int userid = sdm.getUser().getUserId();
				String clean_syns_table = "";
				String requestText = gform.getValue();
				String[] dss = requestText.split(";");
				String dataset = dss[0];
				String clean_term_category_table = dss[1];
				if (dss.length > 2) {
					clean_syns_table = dss[2];
				}

				// clen up
				if (GeneralDBAccess.getInstance()
						.cleanupDatasetWithGivenGlossaryTables(dataset,
								clean_term_category_table, clean_syns_table,
								userid)) {
					responseText = "success";
				} else {
					responseText = "error";
				}

				response.setContentType("text");
				response.getWriter().write(responseText);

			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in cleaning up dataset: ", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				response.setContentType("text");
				response.getWriter().write(
						"Error: Problem encountered in cleaning up dataset: "
								+ exe.getMessage());
			}
			return null;
		} else {
			return null;
		}
	}

}
