package edu.arizona.biosemantics.oto.lite.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.lite.db.CategorizationDBAccess;
import edu.arizona.biosemantics.oto.lite.form.GeneralForm;

public class DeleteCategoryAction extends ParserAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GeneralForm gform = (GeneralForm) form;
		String requestStr = gform.getValue();
		String category = requestStr.substring(0, requestStr.indexOf(","));
		String uploadID = requestStr.substring(requestStr.indexOf(",") + 1, requestStr.length());
		HttpSession session = request.getSession();
		session.setAttribute("uploadID", uploadID);
		CategorizationDBAccess.getInstance().deleteCategory(category, Integer.parseInt(uploadID));
		
		return null;
	}
}
