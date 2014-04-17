package edu.arizona.biosemantics.oto.lite;
import edu.arizona.biosemantics.oto.lite.beans.Download;
import edu.arizona.biosemantics.oto.lite.beans.Upload;
import edu.arizona.biosemantics.oto.lite.beans.UploadResult;


public interface IOTOLiteClient {
	
	public UploadResult upload(Upload upload);
	
	public Download download(UploadResult uploadResult);
	
}
