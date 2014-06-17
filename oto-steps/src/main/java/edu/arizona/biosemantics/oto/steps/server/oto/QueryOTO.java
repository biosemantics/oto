package edu.arizona.biosemantics.oto.steps.server.oto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.arizona.biosemantics.oto.client.oto.OTOClient;
import edu.arizona.biosemantics.oto.common.model.GlossaryDictionaryEntry;
import edu.arizona.biosemantics.oto.steps.shared.beans.terminfo.TermGlossary;

public class QueryOTO extends AbstractOTOAccessObject {

	private static QueryOTO instance;

	public static QueryOTO getInstance() throws IOException {
		if (instance == null) {
			instance = new QueryOTO();
		}
		return instance;
	}

	public QueryOTO() throws IOException {
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
	 */
	public String getUUID(String term, String category, String glossaryType,
			String definition) {
		OTOClient otoClient = createOTOClient();
		return otoClient.insertAndGetGlossaryDictionaryEntry(glossaryType,
				term, category, definition).getTermID();
	}

	/**
	 * insert the triple with definition into OTO dictionary
	 * 
	 * @param term
	 * @param category
	 * @param glossaryType
	 * @param definition
	 */
	public void insertTripleToOTO(String term, String category,
			String glossaryType, String definition) {
		OTOClient otoClient = createOTOClient();
		otoClient.insertAndGetGlossaryDictionaryEntry(glossaryType, term,
				category, definition).getTermID();
	}

	/**
	 * check if a given triple exisit in OTO
	 * 
	 * @param term
	 * @param category
	 * @param glossaryType
	 * @return
	 */
	public boolean isTripleExistInOTO(String term, String category,
			String glossaryType) {
		OTOClient otoClient = createOTOClient();
		GlossaryDictionaryEntry entry = otoClient.getGlossaryDictionaryEntry(
				glossaryType, term, category);
		if (entry == null) {
			return false;
		}

		return true;
	}

	/**
	 * get the <category, definition> list for a given term in a given glossary
	 * type
	 * 
	 * @param term
	 * @param glossaryType
	 * @return
	 */
	public ArrayList<TermGlossary> getGlossaryInfo(String term,
			String glossaryType) {
		ArrayList<TermGlossary> glossaries = new ArrayList<TermGlossary>();

		OTOClient otoClient = createOTOClient();

		List<GlossaryDictionaryEntry> entryList = otoClient
				.getGlossaryDictionaryEntries(glossaryType, term);
		for (GlossaryDictionaryEntry entry : entryList) {
			TermGlossary glossary = new TermGlossary("OTO ID: "
					+ entry.getTermID(), entry.getCategory(),
					entry.getDefinition());
			glossaries.add(glossary);
		}

		return glossaries;
	}

}
