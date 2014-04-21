package edu.arizona.biosemantics.oto.lite.action;

/**
 * @author Partha Pratim Sanyal
 */

/**
 * This class is used to retrieve the contextual sentences for a particular
 * set of coOccured Terms
 * @author Partha
 */
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.lite.beans.ContextBean;
import edu.arizona.biosemantics.oto.lite.db.CategorizationDBAccess;
import edu.arizona.biosemantics.oto.lite.form.GeneralForm;

public class GetContextAction extends ParserAction {

	/** Getting the instance of logger. */
	private static final Logger LOGGER = Logger
			.getLogger(GetContextAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			GeneralForm gform = (GeneralForm) form;
			String term = gform.getValue();
			int uploadID = Integer.parseInt(request.getParameter("uploadID").toString());
			ArrayList<ContextBean> contexts = new ArrayList<ContextBean>();
			try {
				contexts = CategorizationDBAccess.getInstance().getContextForTerm(term, uploadID);
			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in retrieving context", exe);
			}

			response.setContentType("text/xml");
			StringBuffer responseString = new StringBuffer("<contexts>");
			for (ContextBean context : contexts) {
				responseString.append("<context>");
				String sourceFile = context.getSourceText();
				String sourceToShow = sourceFile.substring(0,
						sourceFile.lastIndexOf("-"));
				String sentence = context.getSentence();
				responseString.append("<source>" + sourceToShow + "</source>");
				responseString.append("<sentence>" + sentence + "</sentence>");
				responseString.append("</context>");
			}
			responseString.append("</contexts>");
			/* "±" cannot be recognized in xml file, replace it with "+-" */
			String str = responseString.toString().replaceAll("±", "+-");
			str = str.replaceAll("×", "x");
			str = str.replaceAll("–", "-");

			response.getWriter().write(str);
		} catch (Exception exe) {
			LOGGER.error("unable to get context for term", exe);
			exe.printStackTrace();
		}
		return null;

	}
}
