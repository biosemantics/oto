package edu.arizona.biosemantics.oto.oto.rest.git;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.gitclient.GitClient;
import edu.arizona.biosemantics.oto.oto.Configuration;

public class GlossaryTypeDAO {

	private static GlossaryTypeDAO instance;
	private GitClient gitClient;
	
	public static GlossaryTypeDAO getInstance() throws IOException {
		if(instance == null) 
			instance = new GlossaryTypeDAO();
		return instance;
	}
	
	private GlossaryTypeDAO() throws IOException {
		Configuration configuration = Configuration.getInstance();
		String gitUser = configuration.getGitUser();
		String gitPassword = configuration.getGitPassword();
		String gitAuthorName = configuration.getGitAuthorName();
		String gitAuthorEmail = configuration.getGitAuthorEmail();
		String gitCommitterName = configuration.getGitCommitterName();
		String gitCommitterEmail = configuration.getGitCommitterEmail();
		String gitRepository = configuration.getGitRepository();
		String gitLocalPath = configuration.getGitLocalPath();
		List<String> branches = new LinkedList<String>();
		branches.add("master");
		branches.add("development");
		
		this.gitClient = new GitClient(gitRepository, branches, gitLocalPath, gitUser, gitPassword, gitAuthorName, gitAuthorEmail, gitCommitterName, gitCommitterEmail);
	}
	
	public List<String> getGlossaryTypes() throws Exception {
		List<String> result = new LinkedList<String>();
		List<File> directories = gitClient.getDirectories("", "master");
		for(File directory : directories) 
			result.add(directory.getName());
		return result;
	}
	
}
