package edu.arizona.biosemantics.oto.oto.action;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.oto.Configuration;
import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class DownloadAction extends ParserAction {
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GeneralForm gform = (GeneralForm) form;
		String dataset = gform.getValue();
		CharacterDBAccess cdba = new CharacterDBAccess();
		String merged = cdba.getDownloadRedirection(dataset);
		
		
		String filePath = Configuration.getInstance().getGlossaryFilePath();
		response.setContentType("text/xml");
		StringBuffer responseString = new StringBuffer("<response>");

		// merged into info
		if (!merged.equals("")) {
			responseString.append("<mergedInto>" + merged + "</mergedInto>");
		}

		// url
		String url = Configuration.getInstance().getDownloadUrl().replaceAll("/", "SLASH");
		responseString.append("<url>" + url + "</url>");

		// zipped sql file
		try {
			File doc = new File(filePath + dataset + ".zip");
			if (doc.isFile() && doc.canRead()) {
				// cdba.generateDownloadingFiles_as_sql_and_zip(dataset);

				responseString.append("<zipfile>" + dataset + ".zip"
						+ "</zipfile>");
			}
		} catch (Throwable t) {
			responseString.append("<msg>error</msg>");
		}

		// versions
		String versions = cdba.generateResponseXMLForDownload(dataset);
		responseString.append(versions);

		responseString.append("</response>");
		response.getWriter().write(responseString.toString());
		return null;
	}

}
