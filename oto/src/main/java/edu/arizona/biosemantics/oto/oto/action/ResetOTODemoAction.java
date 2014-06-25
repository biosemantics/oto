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

public class ResetOTODemoAction extends ParserAction{
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
				String pageIndex = gform.getValue();
				
				if (GeneralDBAccess.getInstance().resetOTODemo(pageIndex)) {
					responseText = "success";
				} else {
					responseText = "error";
				}

				response.setContentType("text");
				response.getWriter().write(responseText);

			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in resetting oto_demo dataset: ", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				response.setContentType("text/xml");
				response.getWriter().write(
						"<response>error: Problem encountered in resetting oto_demo dataset: "
								+ exe.getMessage() + "</response>");
			}
			return null;
		} else {
			return null;
		}
	}

}
