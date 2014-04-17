package edu.arizona.biosemantics.oto.full;
import java.util.List;

import com.sun.jersey.api.client.WebResource;

import edu.arizona.biosemantics.oto.full.beans.Category;
import edu.arizona.biosemantics.oto.full.beans.GlossaryDictionaryEntry;
import edu.arizona.biosemantics.oto.full.beans.GlossaryDownload;
import edu.arizona.biosemantics.oto.lite.beans.Download;
import edu.arizona.biosemantics.oto.lite.beans.Upload;



public interface IOTOClient {
		
	public GlossaryDownload download(String glossaryType);
	
	public GlossaryDownload download(String glossaryType, String version);

	public List<Category> getCategories();
	
	public GlossaryDictionaryEntry getGlossaryDictionaryEntry(String glossaryType, String term, String category);

	public GlossaryDictionaryEntry insertAndGetGlossaryDictionaryEntry(String glossaryType, String term, String category, String definition);

	public List<GlossaryDictionaryEntry> getGlossaryDictionaryEntries(String glossaryType, String term);
	
	
	
}
