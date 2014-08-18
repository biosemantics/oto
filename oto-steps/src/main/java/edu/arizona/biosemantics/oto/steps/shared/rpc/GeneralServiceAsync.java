package edu.arizona.biosemantics.oto.steps.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.oto.steps.shared.beans.UploadInfo;

@RemoteServiceRelativePath("general")
public interface GeneralServiceAsync {

	void getUploadInfo(String uploadID, String secret, AsyncCallback<UploadInfo> callback);

}
