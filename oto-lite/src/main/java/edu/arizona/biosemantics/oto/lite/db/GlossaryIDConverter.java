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
		switch (glossaryName) {
		case "Plant":
			return 1;
		case "Hymenoptera":
			return 2;
		case "Algea":
			return 3;
		case "Porifera":
			return 4;
		case "Fossil":
			return 5;
		default:
			return 1;
		}
	}
}
