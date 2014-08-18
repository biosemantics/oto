package edu.arizona.biosemantics.oto.oto.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.db.GeneralDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class preCheckCleanupAction extends ParserAction {
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
				String requestText = gform.getValue();
				String[] dss = requestText.split(";");
				boolean hasSynTable = false;
				String clean_synonyms_table = "";
				String dataset = dss[0];
				String clean_term_category_table = dss[1];
				GeneralDBAccess gdba = GeneralDBAccess.getInstance();
				int numTermCategory = 0;
				int numSyns = 0;
				if (dss.length > 2) {
					hasSynTable = true;
					clean_synonyms_table = dss[2];
				}

				// check requirements
				if (gdba.validateDatasetPrefix(dataset)) {
					if (gdba.checkGlossaryTable(clean_term_category_table,
							"termCategory")) {
						numTermCategory = gdba
								.getNumRecords(clean_term_category_table);
						responseText = "There are "
								+ Integer.toString(numTermCategory)
								+ " term-category records ";
						if (hasSynTable) {
							if (gdba.checkGlossaryTable(clean_synonyms_table,
									"synonym")) {
								numSyns = gdba
										.getNumRecords(clean_synonyms_table);
								responseText += " and "
										+ Integer.toString(numSyns)
										+ " synonyms records";
							} else {
								responseText = "Error: table '"
										+ clean_synonyms_table
										+ "' doesn't exist or not in valid format.";
							}
						}
					} else {
						responseText = "Error: table '"
								+ clean_term_category_table
								+ "' doesn't exist or not in valid format.";
					}

				} else {
					responseText = "Error: Dataset '" + dataset
							+ "' doesn't exist.";
				}

				response.setContentType("text");
				response.getWriter().write(responseText);

			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in precheck of cleaning up dataset: ", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				response.setContentType("text");
				response.getWriter().write(
						"Error: Problem encountered in precheck of cleaning up dataset: "
								+ exe.getMessage());
			}
			return null;
		} else {
			return null;
		}
	}

}
