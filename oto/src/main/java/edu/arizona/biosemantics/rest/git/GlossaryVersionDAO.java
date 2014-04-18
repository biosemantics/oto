package edu.arizona.biosemantics.rest.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.arizona.biosemantics.gitclient.GitClient;

public class GlossaryVersionDAO {

	private static GlossaryVersionDAO instance;
	private GitClient gitClient;
	
	public static GlossaryVersionDAO getInstance() throws IOException {
		if(instance == null) 
			instance = new GlossaryVersionDAO();
		return instance;
	}
	
	private GlossaryVersionDAO() throws IOException {
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
		List<String> branches = new LinkedList<String>();
		branches.add("master");
		branches.add("development");
		
		this.gitClient = new GitClient(gitRepository, branches, gitLocalPath, gitUser, gitPassword, gitAuthorName, gitAuthorEmail, gitCommitterName, gitCommitterEmail);
	}
	
	public String getLatestVersion(String glossaryType) throws Exception {
		File file = this.gitClient.getFile(glossaryType + File.separator + "latest" + File.separator + glossaryType + "_glossary_term_category.csv", "master");
		if(file.exists()) {
			//read and parse version number
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = reader.readLine()) != null) {
				if(line.startsWith("#Version:")) {
					String[] versionParts = line.split("Version:");
					reader.close();
					return versionParts[1].trim();
				}
			}
			reader.close();
			throw new Exception("Latest downloadable glossary does not contain version information");
		} else {
			throw new Exception("Latest downloadable glossary of this type not found");
		}
	}
	
	public boolean existsVersion(String glossaryType, String version) throws Exception {
		File file = this.gitClient.getFile(glossaryType + File.separator + version, "master");
		if(file.exists() && file.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}
	
}
