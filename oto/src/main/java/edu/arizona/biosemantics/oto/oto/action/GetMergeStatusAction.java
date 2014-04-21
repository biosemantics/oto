package edu.arizona.biosemantics.oto.oto.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class GetMergeStatusAction extends ParserAction {
	private static final Logger LOGGER = Logger
			.getLogger(GetUserReportAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (checkSessionValidity(request)) {
			GeneralForm gform = (GeneralForm) form;
			String param = gform.getValue();
			try {

				HttpSession session = request.getSession();
				String responseText = "null";
				if (session.getAttribute(param) != null) {
					responseText = session.getAttribute(param).toString();
				}
				response.setContentType("text/xml");
				response.getWriter().write(
						"<response>" + responseText + "</response>");

			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in getting " + param, exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				response.setContentType("text/xml");
				response.getWriter().write(
						"<response>Problem encountered in getting " + param
								+ ": " + exe.getMessage() + "</response>");
			}
			return null;
		} else {
			return null;
		}
	}

}
