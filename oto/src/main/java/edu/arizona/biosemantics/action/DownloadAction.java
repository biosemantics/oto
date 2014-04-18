package edu.arizona.biosemantics.action;

import java.io.File;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.db.CharacterDBAccess;
import edu.arizona.biosemantics.form.GeneralForm;

public class DownloadAction extends ParserAction {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GeneralForm gform = (GeneralForm) form;
		String dataset = gform.getValue();
		CharacterDBAccess cdba = new CharacterDBAccess();
		String merged = cdba.getDownloadRedirection(dataset);
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		String filePath = properties.get("GLOSSARY_FILE_PATH").toString();
		response.setContentType("text/xml");
		StringBuffer responseString = new StringBuffer("<response>");

		// merged into info
		if (!merged.equals("")) {
			responseString.append("<mergedInto>" + merged + "</mergedInto>");
		}

		// url
		String url = properties.get("DOWNLOAD_URL").toString()
				.replaceAll("/", "SLASH");
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
