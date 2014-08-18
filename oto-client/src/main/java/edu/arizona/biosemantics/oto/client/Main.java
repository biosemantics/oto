package edu.arizona.biosemantics.oto.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.arizona.biosemantics.oto.client.lite.OTOLiteClient;
import edu.arizona.biosemantics.oto.client.oto.OTOClient;
import edu.arizona.biosemantics.oto.common.model.GlossaryDownload;
import edu.arizona.biosemantics.oto.common.model.TermCategory;
import edu.arizona.biosemantics.oto.common.model.TermSynonym;
import edu.arizona.biosemantics.oto.common.model.lite.Decision;
import edu.arizona.biosemantics.oto.common.model.lite.Download;
import edu.arizona.biosemantics.oto.common.model.lite.Sentence;
import edu.arizona.biosemantics.oto.common.model.lite.Synonym;
import edu.arizona.biosemantics.oto.common.model.lite.Term;
import edu.arizona.biosemantics.oto.common.model.lite.Upload;
import edu.arizona.biosemantics.oto.common.model.lite.UploadResult;

/**
 * 
 * @author 
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		LocalGlossary localGlossary = new LocalGlossary();
		List<TermCategory> termCategories = new ArrayList<TermCategory>();
		List<WordRole> wordRoles = new ArrayList<WordRole>();
		List<TermSynonym> termSynonyms = new ArrayList<TermSynonym>();

		/** get permanent glossary **/
		OTOClient otoClient = new OTOClient("http://localhost:8080/OTO5/");
		Future<GlossaryDownload> glossaryDownload = otoClient.getGlossaryDownload("ant_agosti");
		GlossaryDownload gdl = glossaryDownload.get();
		termCategories.addAll(gdl.getTermCategories());
		termSynonyms.addAll(gdl.getTermSynonyms());

		/** get temporary glossary **/
		OTOLiteClient otoLiteClient = new OTOLiteClient("http://localhost:8080/OTOLite/");
		
		Upload upload = new Upload();
		upload.setGlossaryType("ant_agosti");
		List<Sentence> sentences = new ArrayList<Sentence>();
		sentences.add(new Sentence(1, "1.txt", "some", "example"));
		upload.setSentences(sentences);
		List<Term> possStr = new ArrayList<Term>();
		List<Term> possCh = new ArrayList<Term>();
		List<Term> possOt = new ArrayList<Term>();
		possStr.add(new Term("possstr"));
		possCh.add(new Term("possCh"));
		possOt.add(new Term("possOth"));
		upload.setPossibleCharacters(possCh);
		upload.setPossibleOtherTerms(possOt);
		upload.setPossibleStructures(possStr);
		
		Future<UploadResult> uploadResult = otoLiteClient.putUpload(upload);
		Future<Download> download = otoLiteClient.getDownload(uploadResult.get());
		Download d = download.get();
		List<Decision> decisions = d.getDecisions();
		List<Synonym> synonyms = d.getSynonyms();

		HashSet<String> hasSynSet = new HashSet<String>();
		for(Synonym synonym : synonyms) {
			TermSynonym termSynonym = new TermSynonym();
			termSynonym.setTerm(synonym.getTerm());
			termSynonym.setSynonym(synonym.getSynonym());
			termSynonyms.add(termSynonym);
			hasSynSet.add(synonym.getTerm());
		}
		
		for(Decision decision : decisions) {
			TermCategory termCategory = new TermCategory();
			termCategory.setTerm(decision.getTerm());
			termCategory.setCategory(decision.getCategory());
			termCategory.setHasSyn(hasSynSet.contains(decision.getTerm()));
			termCategories.add(termCategory);
		}
		
		/** generate the wordroles from termcategories **/
		for(TermCategory termCategory : termCategories) {
			WordRole wordRole = new WordRole();
			wordRole.setWord(termCategory.getTerm());
			String semanticRole = "c";
			if(termCategory.getCategory().equalsIgnoreCase("structure")) {
				semanticRole = "op";
			}
			wordRole.setSemanticRole(semanticRole);
			wordRole.setSavedid(""); //not really needed, is a left over from earlier charaparser times
			wordRoles.add(wordRole);
		}
		
		/** set the combined glossaries **/
		localGlossary.setTermCategories(termCategories);
		localGlossary.setWordRoles(wordRoles);
		localGlossary.setTermSynonyms(termSynonyms);
		
		System.out.println(localGlossary);
	}

}
