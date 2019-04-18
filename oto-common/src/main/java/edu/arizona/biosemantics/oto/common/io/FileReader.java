package edu.arizona.biosemantics.oto.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class FileReader {

	private static final Logger LOGGER = Logger.getLogger(FileReader.class);
	
	/**
	 * This method gets the file information from the transformed folder
	 */
	public String getFileInfo(String fileName) {
		File file = new File(fileName);
		StringBuilder text = new StringBuilder();
		String NL = System.getProperty("line.separator");
		Scanner scanner = null;

		try {
			scanner = new Scanner(new FileInputStream(file));
			while (scanner.hasNextLine()) {
				text.append(scanner.nextLine() + NL);
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("error reading file ", exe);
		} finally {
			scanner.close();
		}

		return text.toString();
	}
	
}
