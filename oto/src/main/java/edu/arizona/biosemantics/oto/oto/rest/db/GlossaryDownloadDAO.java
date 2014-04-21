package edu.arizona.biosemantics.oto.oto.rest.db;

import edu.arizona.biosemantics.oto.common.model.GlossaryDownload;

public class GlossaryDownloadDAO extends AbstractDAO {

	private GlossaryDownloadDAO() throws Exception {
		super();
	}

	private static GlossaryDownloadDAO instance;
	
	public static GlossaryDownloadDAO getInstance() throws Exception {
		if(instance == null)
			instance = new GlossaryDownloadDAO();
		return instance;
	}

	public GlossaryDownload getGlossaryDownload(String glossaryType, String version) throws Exception {
		GlossaryDownload glossaryDownload = new GlossaryDownload();
		
		version = getVersion(glossaryType, version);
		glossaryDownload.setVersion(version);
		glossaryDownload.setTermCategories(TermCategoryDAO.getInstance().getTermCategories(glossaryType, version));
		glossaryDownload.setTermSynonyms(TermSynonymDAO.getInstance().getTermSynonyms(glossaryType, version));
		
		return glossaryDownload;
	}

	private String getVersion(String glossaryType, String version) throws Exception {
		GlossaryVersionDAO glossaryVersionDAO = GlossaryVersionDAO.getInstance();
		if(version.equals("latest")) {
			return glossaryVersionDAO.getLatestVersion(glossaryType);
		} else {
			if(glossaryVersionDAO.existsVersion(glossaryType, version))
				return version;
			else 
				throw new Exception("Version not found");
		}
	}
}
