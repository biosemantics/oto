package edu.arizona.sirls.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.sirls.beans.GlossaryNameMapper;
import edu.arizona.sirls.db.CharacterDBAccess;

public class CreateSystemReservedDatasetsAction  extends ParserAction{

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CharacterDBAccess cdba = new CharacterDBAccess();
		ArrayList<String> glosses = new GlossaryNameMapper().getGlossaryNames();
		for (int i = 0; i < glosses.size(); i++) {
			cdba.createDatasetIfNotExist(glosses.get(i) + "_glossary", "", 23, i + 1);
		}
		
		return null;
	}

}
