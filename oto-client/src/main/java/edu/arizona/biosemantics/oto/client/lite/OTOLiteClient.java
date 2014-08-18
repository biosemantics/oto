package edu.arizona.biosemantics.oto.client.lite;

import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.oto.common.model.lite.Download;
import edu.arizona.biosemantics.oto.common.model.lite.Upload;
import edu.arizona.biosemantics.oto.common.model.lite.UploadResult;

public class OTOLiteClient {

	protected String url;
	protected Client client;
	protected WebTarget target;		
	
	/**
	 * @param url
	 */
	@Inject
	public OTOLiteClient(@Named("OTOLiteClient_Url")String url) {
		this.url = url;
	}
	
	public void open() {		
		client = ClientBuilder.newBuilder().withConfig(new ClientConfig()).register(JacksonFeature.class).build();
		client.register(new LoggingFilter(Logger.getAnonymousLogger(), true));
		
		//this doesn't seem to work for posts (among others), even though it is documented as such, use authentication header instead there
		//target = client.target(this.apiUrl).queryParam("apikey", this.apiKey);
		target = client.target(this.url);
	}
	
	public void close() {
		client.close();
	}
		
	public Future<UploadResult> putUpload(Upload upload) {
		return this.getUploadInvoker().put(Entity.entity(upload, MediaType.APPLICATION_JSON), UploadResult.class);
	}
	
	public void putUpload(Upload upload, InvocationCallback<List<UploadResult>> callback) {
		this.getUploadInvoker().put(Entity.entity(upload, MediaType.APPLICATION_JSON), callback);
	}
	
	public Future<Download> getDownload(UploadResult uploadResult) {
		return this.getDownloadInvoker(uploadResult).get(Download.class);
	}
	
	public void getDownload(UploadResult uploadResult, InvocationCallback<List<Download>> callback) {
		this.getDownloadInvoker(uploadResult).get(callback);
	}
	
	private AsyncInvoker getUploadInvoker() {
		return target.path("rest").path("glossary").path("upload").request(MediaType.APPLICATION_JSON).async();
	}
	
	private AsyncInvoker getDownloadInvoker(UploadResult uploadResult) {
		return target.path("rest").path("glossary").path("download").queryParam("uploadId", uploadResult.getUploadId())
				.queryParam("secret", uploadResult.getSecret()).request(MediaType.APPLICATION_JSON).async();
	}
	
}
