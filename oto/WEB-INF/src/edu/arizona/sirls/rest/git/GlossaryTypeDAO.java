package edu.arizona.sirls.rest.git;

import gitClient.Branch;
import gitClient.GitClient;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class GlossaryTypeDAO {

	private static GlossaryTypeDAO instance;
	private GitClient gitClient;
	
	public static GlossaryTypeDAO getInstance() throws IOException {
		if(instance == null) 
			instance = new GlossaryTypeDAO();
		return instance;
	}
	
	private GlossaryTypeDAO() throws IOException {
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
	
	public List<String> getGlossaryTypes() throws Exception {
		List<String> result = new LinkedList<String>();
		List<File> directories = gitClient.getDirectories("", Branch.master.toString());
		for(File directory : directories) 
			result.add(directory.getName());
		return result;
	}
	
}
