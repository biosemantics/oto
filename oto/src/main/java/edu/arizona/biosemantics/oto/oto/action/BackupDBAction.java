package edu.arizona.biosemantics.oto.oto.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class BackupDBAction extends ParserAction {
	private static final Logger LOGGER = Logger.getLogger(DataSetAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (checkSessionValidity(request)) {
			GeneralForm gform = (GeneralForm) form;
			String value = gform.getValue();
			String responseText = "";
			CharacterDBAccess cdba = new CharacterDBAccess();
			boolean success = false;
			if (value.equals("beforeMerge")) {
				success = cdba.backupDatabase(true);
			} else {
				success = cdba.backupDatabase(false);
			}

			if (success)
				responseText = "success";
			else {
				LOGGER.error("Backup datasets failed.");
				responseText = "Backup datasets failed. Please try again later. ";
			}
			response.setContentType("text/xml");
			response.getWriter().write(
					"<response>" + responseText + "</response>");
		} else {
			return mapping.findForward(Forwardable.LOGON);
		}
		return null;
	}
}
