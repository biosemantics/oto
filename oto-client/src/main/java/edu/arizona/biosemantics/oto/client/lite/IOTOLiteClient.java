package edu.arizona.biosemantics.oto.client.lite;

import edu.arizona.biosemantics.oto.common.model.lite.Download;
import edu.arizona.biosemantics.oto.common.model.lite.Upload;
import edu.arizona.biosemantics.oto.common.model.lite.UploadResult;

public interface IOTOLiteClient {
	
	public UploadResult upload(Upload upload);
	
	public Download download(UploadResult uploadResult);
	
}
