package edu.arizona.biosemantics.oto.client.oto;




public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//IOTOClient otoClient = new OTOClient("http://biosemantics.arizona.edu/ONTNEW/");
		IOTOClient otoClient = new OTOClient("http://biosemantics.arizona.edu:8080/OTO/");	
		
		System.out.println(otoClient.getGlossaryDictionaryEntries("Hymenoptera", "abundance"));
		
		//GlossaryDictionaryEntry result = otoClient.getGlossaryDictionaryEntry("Plant", "round22", "shape");
		
		//System.out.println(result.getTermID());
		//System.out.println(result.getDefinition());
		//otoClient.getCategories();
		
		
		/*List<Category> categories = otoClient.getCategories();
		System.out.println(categories);*/
		//"plant_gloss_for_iplant", "Plant"
		//GlossaryDownload download = otoClient.download("Plant");
		//System.out.println(download.toString());
		/*GlossaryDownload download = otoClient.download("Plant", "latest");
		System.out.println(download.toString());
		for(TermCategory termCategory : download.getTermCategories()) {
			System.out.println(
					termCategory.getCategory() + " " + termCategory.getTerm() + " " + 
			termCategory.isHasSyn());
		}*/
	}

}
