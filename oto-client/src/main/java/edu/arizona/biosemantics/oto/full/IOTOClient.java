package edu.arizona.biosemantics.oto.full;
import java.util.List;

import edu.arizona.biosemantics.oto.model.Category;
import edu.arizona.biosemantics.oto.model.GlossaryDictionaryEntry;
import edu.arizona.biosemantics.oto.model.GlossaryDownload;



public interface IOTOClient {
		
	public GlossaryDownload download(String glossaryType);
	
	public GlossaryDownload download(String glossaryType, String version);

	public List<Category> getCategories();
	
	public GlossaryDictionaryEntry getGlossaryDictionaryEntry(String glossaryType, String term, String category);

	public GlossaryDictionaryEntry insertAndGetGlossaryDictionaryEntry(String glossaryType, String term, String category, String definition);

	public List<GlossaryDictionaryEntry> getGlossaryDictionaryEntries(String glossaryType, String term);
	
	
	
}
