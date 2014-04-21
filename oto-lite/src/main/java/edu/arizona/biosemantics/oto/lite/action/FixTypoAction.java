package edu.arizona.biosemantics.oto.lite.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.lite.db.CategorizationDBAccess;
import edu.arizona.biosemantics.oto.lite.form.GeneralForm;

public class FixTypoAction extends ParserAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GeneralForm gform = (GeneralForm) form;
		String requestStr = gform.getValue();

		// get uploadID
		String vals[] = requestStr.split(";");
		int uploadID = Integer.parseInt(vals[0]);
		String term = vals[1];
		String replacement = vals[2];
		boolean success = CategorizationDBAccess.getInstance().fixTypo(uploadID, term,
				replacement);

		response.setContentType("text/xml");
		if (success) {
			response.getWriter().write("success");
		} else {
			response.getWriter().write("failed");
		}

		return null;
	}

}
