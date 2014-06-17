package edu.arizona.biosemantics.oto.steps.server.utilities;

import java.io.IOException;
import java.util.Properties;

public class Utilities {
	private static Utilities instance;

	public static Utilities getInstance() throws IOException {
		if (instance == null) {
			instance = new Utilities();
		}
		return instance;
	}

	public Utilities() throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
	}

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

	public static String getOntologyNameByGlossaryType(int glossaryType) {
		String defaultOntology = "po";
		switch (glossaryType) {
		case 1:
			return "po";
		case 2:
			return "hao";
		case 3:
			return defaultOntology;
		case 4:
			return "poro";
		case 5:
			return defaultOntology;
		default:
			return defaultOntology;
		}
	}
}
