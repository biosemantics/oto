package edu.arizona.biosemantics.oto.oto.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.oto.db.UpdateDatabase;

public class UpdateServerDatabaseAction extends ParserAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UpdateDatabase.getInstance().cleanupCategoryNames();
		UpdateDatabase.getInstance().cleanup_hymenoptera_glossary();
		return null;
	}
}
