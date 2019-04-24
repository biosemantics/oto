package edu.arizona.biosemantics.oto.client.oto;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.oto.model.CategorizeTerms;
import edu.arizona.biosemantics.oto.model.CategoryBean;
import edu.arizona.biosemantics.oto.model.CreateDataset;
import edu.arizona.biosemantics.oto.model.CreateUserResult;
import edu.arizona.biosemantics.oto.model.DecisionHolder;
import edu.arizona.biosemantics.oto.model.GlossaryDictionaryEntry;
import edu.arizona.biosemantics.oto.model.GroupTerms;
import edu.arizona.biosemantics.oto.model.Order;
import edu.arizona.biosemantics.oto.model.StructureHierarchy;
import edu.arizona.biosemantics.oto.model.Term;
import edu.arizona.biosemantics.oto.model.TermContext;
import edu.arizona.biosemantics.oto.model.TermOrder;
import edu.arizona.biosemantics.oto.model.User;
import edu.arizona.biosemantics.oto.model.TermContext;

public class Main {

	/**
	 * @param args
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		OTOClient otoClient = new OTOClient("http://localhost:8080/oto/");

		otoClient.open();
		
		String email = "thomas.rodenhausen@gmail.com";
		/*String pass = "termspassword";
		String firstName = "Thomas";
		String lastName = "Rodenhausen";*/
		//String token = "9ImelotCiHj2FMQ8fK5B0w==";
		//String token = "TA+QdXcpz+AUi1iOjZDvZw==";
		String token = "4hbYwF7HCNljBGkHL38uRA==";
		
		/*User user = new User();
		user.setUserEmail(email);
		user.setPassword(pass);
		user.setAffiliation("UA");
		user.setFirstName("Thomas");
		user.setLastName("Rodenhausen");
		user.setBioportalUserId("asdf");
		user.setBioportalApiKey("asdf2");
		
		Future<CreateUserResult> result = otoClient.postUser(user);
		System.out.println("token " + result.get());
		
		Future<String> result2 = otoClient.getUserAuthenticationToken(user);
		System.out.println("token " + result2.get());
		*/
		
		//List<TermContext> termContexts = new LinkedList<TermContext>();
		//termContexts.add(new TermContext("a", "a sentence"));
		
		//GroupTerms groupTerms = new GroupTerms(termContexts, new Authentication(email, token));
		//Future<String> result = otoClient.postGroupTerms("thomas_tester123", groupTerms);
		//Future<String> result  = otoClient.postStructureHierarchy("thomas_tester123", new StructureHierarchy(termContexts, new Authentication(email, token)));
		
		//List<Order> orders = new LinkedList<Order>();
		//Future<String> result = otoClient.postTermOrder("thomas_tester123", new TermOrder(orders, new Authentication(email, token)));
		
		//User user = new User();
		//user.setUserEmail(email);
		//user.setPassword(firstName + lastName);
		
		//Future<String> tokenResult = otoClient.getUserAuthenticationToken(user);
		//String token = tokenResult.get();
		//Future<String> result = otoClient.postDataset(new CreateDataset("acb", TaxonGroup.PLANT, token));
		
		String datasetName = otoClient.postDataset(new CreateDataset("th133", TaxonGroup.PLANT, token)).get();
		System.out.println(datasetName);
		
		List<TermContext> termContexts = new LinkedList<TermContext>();
		termContexts.add(new TermContext("d", "d sentence"));
		termContexts.add(new TermContext("e", "e sentence"));
		termContexts.add(new TermContext("c", "c sentence"));
		
		GroupTerms groupTerms = new GroupTerms(termContexts, token, true);
		Future<GroupTerms.Result> result = otoClient.postGroupTerms(datasetName, groupTerms);
		System.out.println(result.get().getCount());
		
		CategorizeTerms categorizeTerms = new CategorizeTerms();
		categorizeTerms.setAuthenticationToken(token);
		DecisionHolder decisionHolder = new DecisionHolder();
		ArrayList<CategoryBean> regularCategories = new ArrayList<CategoryBean>();
		CategoryBean categoryBean = new CategoryBean();
		categoryBean.setName("taste");
		ArrayList<Term> changedTerms = new ArrayList<Term>();
		Term term = new Term();
		term.setAdditional(false);
		term.setHasSyn(false);
		term.setTerm("c");
		
		Term synTerm = new Term();
		synTerm.setAdditional(false);
		synTerm.setHasSyn(false);
		synTerm.setTerm("d");
		
		changedTerms.add(term);
		//term = new Term();
		//term.setAdditional(true);
		//term.set
		changedTerms.add(synTerm);
		categoryBean.setChanged_terms(changedTerms);		
		
		regularCategories.add(categoryBean);
		decisionHolder.setRegular_categories(regularCategories);
		categorizeTerms.setDecisionHolder(decisionHolder);
		otoClient.postGroupTermsCategorization(datasetName, categorizeTerms);
				
		
		CategorizeTerms categorizeTerms2 = new CategorizeTerms();
		categorizeTerms2.setAuthenticationToken(token);
		DecisionHolder decisionHolder2 = new DecisionHolder();
		ArrayList<CategoryBean> regularCategories2 = new ArrayList<CategoryBean>();
		CategoryBean categoryBean2 = new CategoryBean();
		categoryBean2.setName("taste");
		ArrayList<Term> changedTerms2 = new ArrayList<Term>();
		Term term2 = new Term();
		term2.setAdditional(false);
		term2.setHasSyn(true);
		term2.setTerm("c");
		term2.setRelatedTerms("'d'");
		ArrayList<Term> syns2 = new ArrayList<Term>();
		Term synTerm2 = new Term();
		synTerm2.setAdditional(false);
		synTerm2.setHasSyn(false);
		synTerm2.setTerm("d");
		syns2.add(synTerm2);
		term2.setSyns(syns2);
		//term = new Term();
		//term.setAdditional(true);
		//term.set
		Term oldTerm = new Term();
		oldTerm.setAdditional(true);
		oldTerm.setHasSyn(false);
		oldTerm.setRelatedTerms("synonym of 'c'");
		oldTerm.setTerm("d");
		changedTerms2.add(oldTerm);
		changedTerms2.add(term2);
		categoryBean2.setChanged_terms(changedTerms2);
				
		regularCategories2.add(categoryBean2);
		decisionHolder2.setRegular_categories(regularCategories2);
		
		categorizeTerms2.setDecisionHolder(decisionHolder2);
		otoClient.postGroupTermsCategorization(datasetName, categorizeTerms2);
		
		
		/*CategorizeTerms categorizeTerms = new CategorizeTerms();
		categorizeTerms.setAuthenticationToken(token);
		DecisionHolder decisionHolder = new DecisionHolder();
		ArrayList<CategoryBean> regularCategories = new ArrayList<CategoryBean>();
		CategoryBean categoryBean = new CategoryBean();
		categoryBean.setName("taste");
		ArrayList<Term> changedTerms = new ArrayList<Term>();
		Term term = new Term();
		term.setAdditional(false);
		term.setHasSyn(true);
		term.setTerm("c");
		ArrayList<Term> syns = new ArrayList<Term>();
		Term synTerm = new Term();
		synTerm.setAdditional(true);
		synTerm.setHasSyn(false);
		synTerm.setTerm("d");
		syns.add(synTerm);
		term.setSyns(syns);
		changedTerms.add(term);
		//term = new Term();
		//term.setAdditional(true);
		//term.set
		changedTerms.add(synTerm);
		categoryBean.setChanged_terms(changedTerms);
		ArrayList<String> terms = new ArrayList<String>();
		terms.add("c");
		terms.add("d");
		categoryBean.setTerms(terms);
		
		
		regularCategories.add(categoryBean);
		decisionHolder.setRegular_categories(regularCategories);
		
		categorizeTerms.setDecisionHolder(decisionHolder);
		otoClient.postGroupTermsCategorization("th123_rodenhausen_20150521094833", categorizeTerms);*/
		
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
