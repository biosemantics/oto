package edu.arizona.biosemantics.oto.lite.db;

public class GlossaryIDConverter {
	/**
	 * The use of this should be reconsidered: What if we have a new glossary
	 * type, do we want to change the code to accomodate this?
	 * 
	 * @param glossaryType
	 * @return
	 */
	public static String getGlossaryNameByID(int glossaryType) {
		switch (glossaryType) {
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
		default:
			return "Plant";
		}
	}

	/**
	 * The use of this should be reconsidered: What if we have a new glossary
	 * type, do we want to change the code to accomodate this?
	 * 
	 * @param glossaryType
	 * @return
	 */
	public static int getGlossaryIDByName(String glossaryName) {
		if (glossaryName.equals("Plant"))
			return 1;
		if (glossaryName.equals("Hymenoptera"))
			return 2;
		if (glossaryName.equals("Algea"))
			return 3;
		if (glossaryName.equals("Porifera"))
			return 4;
		if (glossaryName.equals("Fossil"))
			return 5;
		return 1;
	}
}
