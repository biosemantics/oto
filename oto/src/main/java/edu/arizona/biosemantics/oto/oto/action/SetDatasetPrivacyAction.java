package edu.arizona.biosemantics.oto.oto.action;

/**
 * this action is creating a dataset in manage datasets page
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.db.GeneralDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class SetDatasetPrivacyAction extends ParserAction {
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
				String value = gform.getValue();
				String[] dss = value.split(";");

				String datasetName = dss[0];
				boolean isPrivate = (dss[1].equals("1") ? true : false);
				if (GeneralDBAccess.getInstance().setDatasetPrivacy(
						datasetName, isPrivate)) {
					responseText = "success";
				} else {
					responseText = "error";
				}

				response.setContentType("text");
				response.getWriter().write(responseText);

			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in setting dataset privacy", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				response.setContentType("text/xml");
				response.getWriter().write(
						"<response>error: Problem encountered in setting dataset privacy: "
								+ exe.getMessage() + "</response>");
			}
			return null;
		} else {
			return null;
		}
	}

}
