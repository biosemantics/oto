package edu.arizona.biosemantics.oto.oto.beans;

/**
 * this bean must be changed when the glossary table changes
 */

import java.util.ArrayList;

import edu.arizona.biosemantics.common.biology.TaxonGroup;

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
			return "Plant";
		case 2:
			return "Hymenoptera";
		case 3:
			return "Algea";
		case 4:
			return "Porifera";
		case 5:
			return "Fossil";
		case 7:
			return "Cnidaria";
		case 8:
			return "Coleoptera";
		case 9:
			return "Gastropods";
		case 10:
			return "Spider";
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
		
		if (glossaryName.equals("Cnidaria")) {
			return 6;
		}
		
		if (glossaryName.equals("Coleoptera")) {
			return 7;
		}
		
		if (glossaryName.equals("Gastropods")) {
			return 8;
		}
		
		if (glossaryName.equals("Spider")) {
			return 9;
		}
		return 0;
	}

	public int getGlossaryIDByName(TaxonGroup taxonGroup) {
		String glossaryName = taxonGroup.getDisplayName();
		
		// QUICK and dirty FIX until OTO/github glossaries repository and also OTO database "glossaryTypes" table spelling error is corrected
		if(glossaryName.equalsIgnoreCase("Algae")) {
			glossaryName = "Algea";
		}
		return this.getGlossaryIDByName(glossaryName);
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
		glosses.add("Cnidaria");
		glosses.add("Coleoptera");
		glosses.add("Gastropods");
		glosses.add("Spider");
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
				|| dataset.toLowerCase().equals("fossil_glossary")
				|| dataset.toLowerCase().equals("cnidaria_glossary")
				|| dataset.toLowerCase().equals("coleoptera_glossary")
				|| dataset.toLowerCase().equals("gastropods_glossary")
				|| dataset.toLowerCase().equals("spider_glossary");
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
				|| dataset.toLowerCase().equals("fossil_glossary")
				|| dataset.toLowerCase().equals("cnidaria_glossary")
				|| dataset.toLowerCase().equals("coleoptera_glossary")
				|| dataset.toLowerCase().equals("gastropods_glossary")
				|| dataset.toLowerCase().equals("spider_glossary")
				;
	}

}
