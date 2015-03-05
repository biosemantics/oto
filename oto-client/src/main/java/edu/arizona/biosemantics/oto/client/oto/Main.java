package edu.arizona.biosemantics.oto.client.oto;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.arizona.biosemantics.oto.common.model.GlossaryDictionaryEntry;
import edu.arizona.biosemantics.oto.common.model.User;


public class Main {

	/**
	 * @param args
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		OTOClient otoClient = new OTOClient("http://localhost:8080/otoOld2/");

		otoClient.open();
		
		User user = new User();
		user.setUserEmail("thomas.rodenhausen@gmail.com");
		user.setPassword("myPass");
		user.setAffiliation("UA");
		user.setFirstName("Thomas");
		user.setLastName("Rodenhausen");
		user.setBioportalUserId("asdf");
		user.setBioportalApiKey("asdf2");
		
		Future<String> result = otoClient.postUser(user);
		
		//OTOClient otoClient = new OTOClient("http://biosemantics.arizona.edu/OTO/");
		//OTOClient otoClient = new OTOClient("http://localhost:9090/oto/");	
		//otoClient.open();
		
		//otoClient.getGlossaryDownload("Plant").get();
		//otoClient.getCategories().get();
		//otoClient.getGlossaryDictionaryEntry("Plant", "abaxial", "position").get();
		//otoClient.getGlossaryDictionaryEntries("Plant", "abaxial").get();
		
		/*Future<List<GlossaryDictionaryEntry>> entries = otoClient.getGlossaryDictionaryEntries("Hymenoptera", "abundance");
		
		System.out.println(entries.get());*/
		//otoClient.close();
		
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
