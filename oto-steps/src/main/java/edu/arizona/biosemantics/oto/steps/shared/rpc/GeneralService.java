package edu.arizona.biosemantics.oto.steps.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.oto.steps.shared.beans.UploadInfo;

@RemoteServiceRelativePath("general")
public interface GeneralService extends RemoteService {
	UploadInfo getUploadInfo(String uploadID, String secret) throws Exception;
}
