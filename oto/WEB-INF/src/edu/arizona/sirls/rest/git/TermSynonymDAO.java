package edu.arizona.sirls.rest.git;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import au.com.bytecode.opencsv.CSVReader;

import edu.arizona.sirls.rest.beans.TermSynonym;
import gitClient.Branch;
import gitClient.GitClient;

public class TermSynonymDAO {
	
	private static TermSynonymDAO instance;
	private GitClient gitClient;
	
	public static TermSynonymDAO getInstance() throws IOException {
		if(instance == null) 
			instance = new TermSynonymDAO();
		return instance;
	}
	
	private TermSynonymDAO() throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		String gitUser = properties.getProperty("gitUser");
		String gitPassword = properties.getProperty("gitPassword");
		String gitAuthorName = properties.getProperty("gitAuthorName");
		String gitAuthorEmail = properties.getProperty("gitAuthorEmail");
		String gitCommitterName = properties.getProperty("gitCommitterName");
		String gitCommitterEmail = properties.getProperty("gitCommitterEmail");
		String gitRepository = properties.getProperty("gitRepository");
		String gitLocalPath = properties.getProperty("gitLocalPath");
		
		this.gitClient = new GitClient(gitRepository, gitLocalPath, gitUser, gitPassword, gitAuthorName, gitAuthorEmail, gitCommitterName, gitCommitterEmail);
	}

	public List<TermSynonym> getTermSynonyms(String glossaryType, String version) throws Exception {
		List<TermSynonym> result = new ArrayList<TermSynonym>();
		
		File file = this.gitClient.getFile(glossaryType + File.separator + version + File.separator + glossaryType + "_glossary_syns.csv", Branch.master.toString());
		if(file.exists()) {
			CSVReader reader = new CSVReader(
					new InputStreamReader(new FileInputStream(file)),
					',', '"', 9);
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				result.add(new TermSynonym(nextLine[0], nextLine[1], nextLine[2], nextLine[3]));
			}
			reader.close();
		} else {
			throw new Exception("Latest downloadable glossary of this type not found");
		}
		return result;
	}
}
