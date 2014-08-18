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
import edu.arizona.biosemantics.oto.common.model.TermSynonym;
import edu.arizona.biosemantics.oto.oto.Configuration;

public class TermSynonymDAO {
	
	private static TermSynonymDAO instance;
	private GitClient gitClient;
	
	public static TermSynonymDAO getInstance() throws IOException {
		if(instance == null) 
			instance = new TermSynonymDAO();
		return instance;
	}
	
	private TermSynonymDAO() throws IOException {
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

	public List<TermSynonym> getTermSynonyms(String glossaryType, String version) throws Exception {
		List<TermSynonym> result = new ArrayList<TermSynonym>();
		
		File file = this.gitClient.getFile(glossaryType + File.separator + version + File.separator + glossaryType + "_glossary_syns.csv", "master");
		if(file.exists()) {
			CSVReader reader = new CSVReader(
					new InputStreamReader(new FileInputStream(file)),
					',', '"', 9);
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				try {
					result.add(new TermSynonym(nextLine[0], nextLine[1], nextLine[2], nextLine[3]));
				} catch(ArrayIndexOutOfBoundsException e) {
					// there is a line that does not conform to the csv schema
					// maybe there is an extra line at the file end?
					// we'll just ignore this line to be more robust
				}
			}
			reader.close();
		} else {
			throw new Exception("Latest downloadable glossary of this type not found");
		}
		return result;
	}
}
