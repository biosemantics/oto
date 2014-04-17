package edu.arizona.biosemantics.oto.lite;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;


import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import edu.arizona.biosemantics.oto.lite.beans.Download;
import edu.arizona.biosemantics.oto.lite.beans.Upload;
import edu.arizona.biosemantics.oto.lite.beans.UploadResult;


public class OTOLiteClient implements IOTOLiteClient {

	private String url;
	private Client client;
	
	/**
	 * @param url
	 */
	@Inject
	public OTOLiteClient(@Named("OTOLiteClient_Url")String url) {
		this.url = url;
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		client = Client.create(clientConfig);
		//client.addFilter(new LoggingFilter(System.out));
	}
	
	public UploadResult upload(Upload upload) {
		String url = this.url + "rest/glossary/upload";
	    WebResource webResource = client.resource(url);
	    try {
		    UploadResult uploadResult = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).put(UploadResult.class, upload);
		    return uploadResult;
		} catch(Exception e) {
			e.printStackTrace();
			return new UploadResult(-1, "");
		}
	}

	public Download download(UploadResult uploadResult) {
		String url = this.url + "rest/glossary/download";
	    WebResource webResource = client.resource(url);
	    MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
	    queryParams.add("uploadId", String.valueOf(uploadResult.getUploadId()));
	    queryParams.add("secret", uploadResult.getSecret());
	    try {
		    Download download = webResource.queryParams(queryParams).get(Download.class);
			return download;
		} catch(Exception e) {
			e.printStackTrace();
			return new Download();
		}
	}

}
