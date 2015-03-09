package edu.arizona.biosemantics.oto.client.oto;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.oto.common.model.Authentication;
import edu.arizona.biosemantics.oto.common.model.CreateDataset;
import edu.arizona.biosemantics.oto.common.model.GlossaryDictionaryEntry;
import edu.arizona.biosemantics.oto.common.model.GroupTerms;
import edu.arizona.biosemantics.oto.common.model.Order;
import edu.arizona.biosemantics.oto.common.model.StructureHierarchy;
import edu.arizona.biosemantics.oto.common.model.TermContext;
import edu.arizona.biosemantics.oto.common.model.TermOrder;
import edu.arizona.biosemantics.oto.common.model.User;
import edu.arizona.biosemantics.oto.common.model.TermContext;


public class Main {

	/**
	 * @param args
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		OTOClient otoClient = new OTOClient("http://localhost:8080/otoOld2/");

		otoClient.open();
		
		String email = "thomas.rodenhausen@gmail.com";
		String token = "/jGE2LudoledEgsX/ccLe3fe4Ek=";
		
		/*User user = new User();
		user.setUserEmail(email);
		user.setPassword(pass);
		user.setAffiliation("UA");
		user.setFirstName("Thomas");
		user.setLastName("Rodenhausen");
		user.setBioportalUserId("asdf");
		user.setBioportalApiKey("asdf2");
		
		Future<String> result = otoClient.postUser(user);
		*/
		List<TermContext> termContexts = new LinkedList<TermContext>();
		termContexts.add(new TermContext("a", "a sentence"));
		
		//GroupTerms groupTerms = new GroupTerms(termContexts, new Authentication(email, token));
		//Future<String> result = otoClient.postGroupTerms("thomas_tester123", groupTerms);
		//Future<String> result  = otoClient.postStructureHierarchy("thomas_tester123", new StructureHierarchy(termContexts, new Authentication(email, token)));
		
		List<Order> orders = new LinkedList<Order>();
		Future<String> result = otoClient.postTermOrder("thomas_tester123", new TermOrder(orders, new Authentication(email, token)));
		//Future<String> result = otoClient.postDataset(new CreateDataset("thomas_tester123", TaxonGroup.PLANT, new Authentication(email, token)));
		
		System.out.println(result.get());
		
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
