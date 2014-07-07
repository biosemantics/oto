package edu.arizona.biosemantics.oto.oto.beans;

/**
 * this bean must be changed when the glossary table changes
 */

import java.util.ArrayList;

public class GlossaryNameMapper {
	private static GlossaryNameMapper instance;

	public static GlossaryNameMapper getInstance() {
		if (instance == null) {
			instance = new GlossaryNameMapper();
		}
		return instance;
	}

	/**
	 * map glossary id to glossary name
	 * 
	 * @param glossaryID
	 * @return
	 */
	public String getGlossaryName(int glossaryID) {
		String glossaryName = "";
		switch (glossaryID) {
		case 1:
			glossaryName = "Plant";
			break;
		case 2:
			glossaryName = "Hymenoptera";
			break;
		case 3:
			glossaryName = "Algea";
			break;
		case 4:
			glossaryName = "Porifera";
			break;
		case 5:
			glossaryName = "Fossil";
			break;
		default:
			break;
		}
		return glossaryName;
	}

	public int getGlossaryIDByName(String glossaryName) {

		if (glossaryName.equals("Plant")) {
			return 1;
		}

		if (glossaryName.equals("Hymenoptera")) {
			return 2;
		}

		if (glossaryName.equals("Algea")) {
			return 3;
		}

		if (glossaryName.equals("Porifera")) {
			return 4;
		}

		if (glossaryName.equals("Fossil")) {
			return 5;
		}
		return 0;
	}

	/**
	 * get all glossaries as an array list: no need to access database since
	 * this is pretty stable
	 * 
	 * @return
	 */
	public ArrayList<String> getGlossaryNames() {
		ArrayList<String> glosses = new ArrayList<String>();
		glosses.add("Plant");
		glosses.add("Hymenoptera");
		glosses.add("Algea");
		glosses.add("Porifera");
		glosses.add("Fossil");
		return glosses;
	}

	/**
	 * check if the dataset is system reserved
	 * 
	 * @param dataset
	 * @return
	 */
	public boolean isSystemReservedDataset(String dataset) {
		return dataset.toLowerCase().equals("oto_demo")
				|| dataset.toLowerCase().equals("plant_glossary")
				|| dataset.toLowerCase().equals("hymenoptera_glossary")
				|| dataset.toLowerCase().equals("algea_glossary")
				|| dataset.toLowerCase().equals("porifera_glossary")
				|| dataset.toLowerCase().equals("fossil_glossary");
	}

	/**
	 * check if this dataset is for glossary download (usually the biggest one)
	 * will be used when insert glossary version
	 * 
	 * @param dataset
	 * @return
	 */
	public boolean isGlossaryReservedDataset(String dataset) {
		return dataset.toLowerCase().equals("plant_glossary")
				|| dataset.toLowerCase().equals("hymenoptera_glossary")
				|| dataset.toLowerCase().equals("algea_glossary")
				|| dataset.toLowerCase().equals("porifera_glossary")
				|| dataset.toLowerCase().equals("fossil_glossary");
	}

}
