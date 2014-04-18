package edu.arizona.biosemantics.oto.lite;
import edu.arizona.biosemantics.oto.model.lite.Download;
import edu.arizona.biosemantics.oto.model.lite.Upload;
import edu.arizona.biosemantics.oto.model.lite.UploadResult;


public interface IOTOLiteClient {
	
	public UploadResult upload(Upload upload);
	
	public Download download(UploadResult uploadResult);
	
}
