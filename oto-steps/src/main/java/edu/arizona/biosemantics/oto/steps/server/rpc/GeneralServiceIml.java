package edu.arizona.biosemantics.oto.steps.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto.steps.client.rpc.GeneralService;
import edu.arizona.biosemantics.oto.steps.server.db.GeneralDAO;
import edu.arizona.biosemantics.oto.steps.shared.beans.UploadInfo;

public class GeneralServiceIml extends RemoteServiceServlet implements
		GeneralService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2193065585421857808L;

	@Override
	public UploadInfo getUploadInfo(String uploadID, String secret) throws Exception {
		return GeneralDAO.getInstance().getUploadInfo(
				Integer.parseInt(uploadID), secret);
	}

}
