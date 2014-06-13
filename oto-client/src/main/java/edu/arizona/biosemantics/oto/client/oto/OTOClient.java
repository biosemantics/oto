package edu.arizona.biosemantics.oto.client.oto;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import edu.arizona.biosemantics.oto.common.model.Category;
import edu.arizona.biosemantics.oto.common.model.GlossaryDictionaryEntry;
import edu.arizona.biosemantics.oto.common.model.GlossaryDownload;

public class OTOClient implements IOTOClient {

	private String url;
	private Client client;
	
	/**
	 * @param url
	 */
	@Inject
	public OTOClient(@Named("OTOClient_Url")String url) {
		this.url = url;
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		client = Client.create(clientConfig);
		client.addFilter(new LoggingFilter(System.out));
	}

	public GlossaryDownload download(String glossaryType, String version) {
		String url = this.url + "rest/glossaries/" + glossaryType;
	    WebResource webResource = client.resource(url);
	    MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
	    queryParams.add("version", version);
	    try {
		    GlossaryDownload download = webResource.queryParams(queryParams).get(GlossaryDownload.class);
			return download;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public GlossaryDownload download(String glossaryType) {
		String url = this.url + "rest/glossaries/" + glossaryType;
	    WebResource webResource = client.resource(url);
	    MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
	    try {
		    GlossaryDownload download = webResource.queryParams(queryParams).get(GlossaryDownload.class);
			return download;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Category> getCategories() {
		String url = this.url + "rest/categories";
		WebResource webResource = client.resource(url);
		try {
			List<Category> categories = webResource.get(new GenericType<List<Category>>() {});
		    return categories;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public GlossaryDictionaryEntry getGlossaryDictionaryEntry(String glossaryType, String term, String category) {
		String url = this.url + "rest/termCategories/" + glossaryType + "/" + term + "/" + category;
		WebResource webResource = client.resource(url);
		try {
			GlossaryDictionaryEntry glossaryDictionaryEntry = webResource.get(GlossaryDictionaryEntry.class);
			return glossaryDictionaryEntry;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public GlossaryDictionaryEntry insertAndGetGlossaryDictionaryEntry(String glossaryType, String term, String category, String definition) {
		String url = this.url + "rest/termCategories/" + glossaryType + "/" + term + "/" + category;
		WebResource webResource = client.resource(url);
		try {
			GlossaryDictionaryEntry glossaryDictionaryEntry =  webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).put(GlossaryDictionaryEntry.class, definition);
			return glossaryDictionaryEntry;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<GlossaryDictionaryEntry> getGlossaryDictionaryEntries(String glossaryType, String term) {
		String url = this.url + "rest/termCategories/" + glossaryType + "/" + term;
		WebResource webResource = client.resource(url);
		try {
			List<GlossaryDictionaryEntry> glossaryDictionaryEntries = webResource.get(new GenericType<List<GlossaryDictionaryEntry>>() {});
			return glossaryDictionaryEntries;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
