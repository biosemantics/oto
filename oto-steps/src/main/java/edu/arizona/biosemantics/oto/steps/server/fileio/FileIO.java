package edu.arizona.biosemantics.oto.steps.server.fileio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import edu.arizona.biosemantics.oto.steps.server.Configuration;

public class FileIO {
	private static FileIO insatnce;

	private FileIO() {

	}

	public static FileIO getInstance() {
		if (insatnce == null) {
			insatnce = new FileIO();
		}
		return insatnce;
	}

	public String getContextFileContent(String uploadID, String sourceName)
			throws IOException {
		String content = "";
		// parse out source file name
		String fileName = sourceName;
		if (sourceName.indexOf(".") >= 0) {
			fileName = sourceName.substring(0, sourceName.indexOf(".") + 1)
					+ "txt";
		}

		// get folder path from configuration
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		/*Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		String folderPath = properties.getProperty("src_file_dir");*/
		String folderPath = Configuration.src_file_dir;
	
		folderPath = folderPath + File.separator + uploadID + File.separator;
	

		String filePath = folderPath + fileName;

		// check if file exist
		File test = new File(filePath);
		if (test.exists()) {
			// read in content
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			try {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					sb.append(line);
					sb.append('\n');
					line = br.readLine();
				}
				content = sb.toString();
			} finally {
				br.close();
			}
		} else {
			// fill in error content
			content = "File '" + fileName + "' doesn't exist. ";
		}

		return content;
	}
}
