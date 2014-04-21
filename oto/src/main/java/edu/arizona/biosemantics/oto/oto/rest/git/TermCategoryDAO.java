package edu.arizona.biosemantics.oto.oto.rest.git;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import edu.arizona.biosemantics.gitclient.GitClient;
import edu.arizona.biosemantics.oto.common.model.TermCategory;
import edu.arizona.biosemantics.oto.oto.Configuration;

public class TermCategoryDAO {

	private static TermCategoryDAO instance;
	private GitClient gitClient;
	
	public static TermCategoryDAO getInstance() throws IOException {
		if(instance == null) 
			instance = new TermCategoryDAO();
		return instance;
	}
	
	private TermCategoryDAO() throws IOException {
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

	public List<TermCategory> getTermCategories(String glossaryType, String version) throws Exception {
		List<TermCategory> result = new ArrayList<TermCategory>();
		File file = this.gitClient.getFile(glossaryType + File.separator + version + File.separator + glossaryType + "_glossary_term_category.csv", "master");
		if(file.exists()) {
			CSVReader reader = new CSVReader(
					new InputStreamReader(new FileInputStream(file)),
					',', '"', 9);
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				boolean hasSyn = nextLine[2].equals("1");
				result.add(new TermCategory(nextLine[0], nextLine[1], hasSyn, nextLine[3], nextLine[4]));
			}
			reader.close();
		} else {
			throw new Exception("Latest downloadable glossary of this type not found");
		}
		return result;
	}

}
