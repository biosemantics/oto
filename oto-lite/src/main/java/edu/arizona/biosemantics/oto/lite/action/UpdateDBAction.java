package edu.arizona.biosemantics.oto.lite.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.lite.db.UpdateDBAccess;

public class UpdateDBAction extends ParserAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UpdateDBAccess.getInstance().updateDB_secret();
		return null;
	}

}
