package edu.arizona.biosemantics.oto.lite.rest.db;

import edu.arizona.biosemantics.oto.common.model.lite.Download;

public class DownloadDAO extends AbstractDAO {

	private DownloadDAO() throws Exception {
		super();
	}

	private static DownloadDAO instance;
	
	public static DownloadDAO getInstance() throws Exception {
		if(instance == null)
			instance = new DownloadDAO();
		return instance;
	}

	public Download getDownload(int uploadId) throws Exception {
		Download download = new Download();
		download.setFinalized(UploadDAO.getInstance().isFinalized(uploadId));
		download.setDecisions(DecisionDAO.getInstance().getDecisions(uploadId));
		download.setSynonyms(SynonymDAO.getInstance().getSynonyms(uploadId));
		
		return download;
	}

}
