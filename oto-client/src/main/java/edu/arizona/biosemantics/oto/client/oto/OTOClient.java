package edu.arizona.biosemantics.oto.client.oto;

import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.oto.common.model.Category;
import edu.arizona.biosemantics.oto.common.model.GlossaryDictionaryEntry;
import edu.arizona.biosemantics.oto.common.model.GlossaryDownload;
import edu.arizona.biosemantics.oto.common.model.lite.Download;
import edu.arizona.biosemantics.oto.common.model.lite.Upload;
import edu.arizona.biosemantics.oto.common.model.lite.UploadResult;

public class OTOClient {

	private String url;
	private Client client;
	private WebTarget target;		
	
	/**
	 * @param url
	 */
	@Inject
	public OTOClient(@Named("OTOClient_Url")String url) {
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
		
	public Future<GlossaryDownload> getGlossaryDownload(String glossaryType, String version) {
		return this.getGlossaryDownloadInvoker(glossaryType, version).get(GlossaryDownload.class);
	}
	
	public void getGlossaryDownload(String glossaryType, String version, InvocationCallback<GlossaryDownload> callback) {
		this.getGlossaryDownloadInvoker(glossaryType, version).get(callback);
	}
	
	public Future<GlossaryDownload> getGlossaryDownload(String glossaryType) {
		return this.getGlossaryDownloadInvoker(glossaryType).get(GlossaryDownload.class);
	}
	
	public void getGlossaryDownload(String glossaryType, InvocationCallback<GlossaryDownload> callback) {
		this.getGlossaryDownloadInvoker(glossaryType).get(callback);
	}
	
	public Future<List<GlossaryDictionaryEntry>> getGlossaryDictionaryEntries(String glossaryType, String term) {
		return this.getGlossaryDictionaryEntriesInvoker(glossaryType, term).get(new GenericType<List<GlossaryDictionaryEntry>>() {});
	}
	
	public void getGlossaryDictionaryEntries(String glossaryType, String term, InvocationCallback<List<GlossaryDictionaryEntry>> callback) {
		this.getGlossaryDictionaryEntriesInvoker(glossaryType, term).get(callback);
	}

	public Future<GlossaryDictionaryEntry> putAndGetGlossaryDictionaryEntry(String glossaryType, String term, String category, String definition) {
		return this.getGlossaryDictionaryEntryInvoker(glossaryType, term, category).put(Entity.entity(definition, MediaType.APPLICATION_JSON), GlossaryDictionaryEntry.class);
	}
	
	public void putAndGetGlossaryDictionaryEntry(String glossaryType, String term, String category, String definition, InvocationCallback<GlossaryDictionaryEntry> callback) {
		this.getGlossaryDictionaryEntryInvoker(glossaryType, term, category).put(Entity.entity(definition, MediaType.APPLICATION_JSON), callback);
	}
	
	public Future<GlossaryDictionaryEntry> getGlossaryDictionaryEntry(String glossaryType, String term, String category) {
		return this.getGlossaryDictionaryEntryInvoker(glossaryType, term, category).get(GlossaryDictionaryEntry.class);
	}
	
	public void getGlossaryDictionaryEntry(String glossaryType, String term, String category, InvocationCallback<GlossaryDictionaryEntry> callback) {
		this.getGlossaryDictionaryEntryInvoker(glossaryType, term, category).get(callback);
	}
	
	public Future<List<Category>> getCategories() {
		return this.getCategoriesInvoker().get(new GenericType<List<Category>>() {});
	}
	
	public void getCategories(InvocationCallback<List<Category>> callback) {
		this.getCategoriesInvoker().get(callback);
	}
		
	private AsyncInvoker getGlossaryDownloadInvoker(String glossaryType) {
		return target.path("rest").path("glossaries").path(glossaryType).request(MediaType.APPLICATION_JSON).async();
	}	
	
	private AsyncInvoker getGlossaryDownloadInvoker(String glossaryType, String version) {
		return target.path("rest").path("glossaries").path(glossaryType).queryParam("version", version).request(MediaType.APPLICATION_JSON).async();
	}
	
	private AsyncInvoker getCategoriesInvoker() {
		return target.path("rest").path("categories").request(MediaType.APPLICATION_JSON).async();
	}
	
	private AsyncInvoker getGlossaryDictionaryEntriesInvoker(String glossaryType, String term) {
		return target.path("rest").path("termCategories").path(glossaryType).path(term).request(MediaType.APPLICATION_JSON).async();
	}
	
	private AsyncInvoker getGlossaryDictionaryEntryInvoker(String glossaryType, String term, String category) {
		return target.path("rest").path("termCategories").path(glossaryType).path(term).path(category).request(MediaType.APPLICATION_JSON).async();
	}

}
