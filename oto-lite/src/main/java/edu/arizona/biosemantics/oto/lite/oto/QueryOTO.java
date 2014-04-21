package edu.arizona.biosemantics.oto.lite.oto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.arizona.biosemantics.oto.client.oto.OTOClient;
import edu.arizona.biosemantics.oto.common.model.GlossaryDictionaryEntry;
import edu.arizona.biosemantics.oto.lite.beans.TermGlossaryBean;

public class QueryOTO extends AbstractOTOAccessObject {

	private static QueryOTO instance;

	public static QueryOTO getInstance() throws IOException {
		if (instance == null) {
			instance = new QueryOTO();
		}
		return instance;
	}

	private QueryOTO() throws IOException {
		super();
	}

	/**
	 * given a triple with optional definition, get existing or generated uuid
	 * from OTO
	 * 
	 * @param term
	 * @param category
	 * @param glossaryType
	 *            : in string
	 * @param definition
	 * @return
	 * @throws IOException
	 */
	public String getUUID(String term, String category, String glossaryType,
			String definition) throws IOException {
		OTOClient otoClient = createOTOClient();
		return otoClient.insertAndGetGlossaryDictionaryEntry(glossaryType,
				term, category, definition).getTermID();
	}

	/**
	 * get the <category, definition> list for a given term in a given glossary
	 * type
	 * 
	 * @param term
	 * @param glossaryType: must be string value
	 * @return
	 * @throws IOException
	 */
	public ArrayList<TermGlossaryBean> getGlossaryInfo(String term,
			String glossaryType) throws IOException {
		ArrayList<TermGlossaryBean> glossaries = new ArrayList<TermGlossaryBean>();

		OTOClient otoClient = createOTOClient();

		List<GlossaryDictionaryEntry> entryList = otoClient
				.getGlossaryDictionaryEntries(glossaryType, term);
		for (GlossaryDictionaryEntry entry : entryList) {
			TermGlossaryBean glossary = new TermGlossaryBean(entry.getTermID(), entry.getCategory(),
					entry.getDefinition());
			glossaries.add(glossary);
		}

		return glossaries;
	}

}