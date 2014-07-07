package edu.arizona.biosemantics.oto.lite.action;

/**
 * Get glossaries from db and ontology lookup
 * @author Fengqiong
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.client.oto.OTOClient;
import edu.arizona.biosemantics.oto.common.model.GlossaryDictionaryEntry;
import edu.arizona.biosemantics.oto.lite.Configuration;
import edu.arizona.biosemantics.oto.lite.beans.TermGlossaryBean;
import edu.arizona.biosemantics.oto.lite.db.GeneralDBAccess;
import edu.arizona.biosemantics.oto.lite.db.GlossaryIDConverter;
import edu.arizona.biosemantics.oto.lite.db.OntologyDBAccess;
import edu.arizona.biosemantics.oto.lite.form.GeneralForm;

public class GetGlossaryAction extends ParserAction {
	
	/** Getting the instance of logger. */
	private static final Logger LOGGER = Logger
			.getLogger(GetGlossaryAction.class);
	private OTOClient otoClient;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {

			GeneralForm gform = (GeneralForm) form;
			String term = gform.getValue();
			String uploadID = "1";
			int glossaryType = GeneralDBAccess.getInstance().getGlossaryType(
					Integer.parseInt(uploadID));

			response.setContentType("text/xml");
			StringBuffer responseString = new StringBuffer("<glossaries>");

			// get from PATO
			// OlsClient olsC = new OlsClient(term, "PATO");
			// if (olsC.hasData()) {
			// responseString.append("<glossary>");
			// responseString.append("<id>In 'PATO': " + olsC.getTermID() +
			// "</id>");
			// responseString.append("<category>In 'PATO': " + olsC.getParent()
			// + "</category>");
			// responseString.append("<definition>In 'PATO': "+
			// olsC.getDefinition() + "</definition>");
			// responseString.append("</glossary>");
			// }

			// get from OTO
			otoClient = new OTOClient(Configuration.getInstance().getOtoUrl());
			otoClient.open();
			ArrayList<TermGlossaryBean> glosses = new ArrayList<TermGlossaryBean>();
			Future<List<GlossaryDictionaryEntry>> entryList = otoClient.getGlossaryDictionaryEntries(GlossaryIDConverter.getGlossaryNameByID(glossaryType),
					term);
			for (GlossaryDictionaryEntry entry : entryList.get()) {
				TermGlossaryBean glossary = new TermGlossaryBean(entry.getTermID(), entry.getCategory(),
						entry.getDefinition());
				glosses.add(glossary);
			}
			otoClient.close();
			
			for (TermGlossaryBean gloss : glosses) {
				responseString.append("<glossary>");
				responseString
						.append("<id>OTO ID : " + gloss.getId() + "</id>");
				responseString.append("<category>" + gloss.getCategory()
						+ "</category>");
				responseString.append("<definition>" + gloss.getDefinition()
						+ "</definition>");
				responseString.append("</glossary>");
			}

			// get from ontology match
			glosses = OntologyDBAccess.getInstance().getOntologyMatchForTerm(
					term, glossaryType);
			for (TermGlossaryBean gloss : glosses) {
				responseString.append("<glossary>");
				responseString.append("<id>" + gloss.getId() + "</id>");
				responseString.append("<category>" + gloss.getCategory()
						+ "</category>");
				responseString.append("<definition>" + gloss.getDefinition()
						+ "</definition>");
				responseString.append("</glossary>");
			}

			responseString.append("</glossaries>");

			response.getWriter().write(responseString.toString());
		} catch (Exception exe) {
			LOGGER.error("unable to get glossaries for term", exe);
			exe.printStackTrace();
		} finally {
			otoClient.close();
		}
		return null;
	}

}
