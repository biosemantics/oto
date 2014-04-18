package edu.arizona.biosemantics.oto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.arizona.biosemantics.oto.full.OTOClient;
import edu.arizona.biosemantics.oto.lite.OTOLiteClient;
import edu.arizona.biosemantics.oto.model.GlossaryDownload;
import edu.arizona.biosemantics.oto.model.LocalGlossary;
import edu.arizona.biosemantics.oto.model.TermCategory;
import edu.arizona.biosemantics.oto.model.TermSynonym;
import edu.arizona.biosemantics.oto.model.WordRole;
import edu.arizona.biosemantics.oto.model.lite.Decision;
import edu.arizona.biosemantics.oto.model.lite.Download;
import edu.arizona.biosemantics.oto.model.lite.Sentence;
import edu.arizona.biosemantics.oto.model.lite.Synonym;
import edu.arizona.biosemantics.oto.model.lite.Term;
import edu.arizona.biosemantics.oto.model.lite.Upload;
import edu.arizona.biosemantics.oto.model.lite.UploadResult;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LocalGlossary localGlossary = new LocalGlossary();
		List<TermCategory> termCategories = new ArrayList<TermCategory>();
		List<WordRole> wordRoles = new ArrayList<WordRole>();
		List<TermSynonym> termSynonyms = new ArrayList<TermSynonym>();

		/** get permanent glossary **/
		OTOClient otoClient = new OTOClient("http://localhost:8080/OTO5/");
		GlossaryDownload glossaryDownload = otoClient.download("ant_agosti");
		
		termCategories.addAll(glossaryDownload.getTermCategories());
		termSynonyms.addAll(glossaryDownload.getTermSynonyms());

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
		
		UploadResult uploadResult = otoLiteClient.upload(upload);
		Download download = otoLiteClient.download(uploadResult);
		List<Decision> decisions = download.getDecisions();
		List<Synonym> synonyms = download.getSynonyms();

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
