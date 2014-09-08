/**
 * 
 */
package edu.arizona.biosemantics.oto.steps.server.utilities;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import edu.arizona.biosemantics.oto.steps.server.db.ToOntologiesDAO;

/**
 * @author updates
 *
 */
public class InputTermsPreparation {

	private static XPathExpression<?> entity = null;
	private static XPathExpression<?> character = null;
	private static XPathExpression<?> sentence = null;

	static{
		XPathFactory xpfac = XPathFactory.instance();
		//entity = xpfac.compile("//entity[@type="structure|subtance"], Filters.elements());
		character = xpfac.compile("//character", Filters.element());
		entity = xpfac.compile("//structure|//substance", Filters.element()); //this may change if we use 'structure'/'substance' as entity type
		sentence = xpfac.compile("//statement/text", Filters.element()); //this may change if we use 'structure'/'substance' as entity type
	}
	
	/**
	 * 
	 * @param xmldir
	 * @return 
	 * @throws  
	 * @throws Exception 
	 */
	public boolean prepareCandidateTerms(String xmldirstr, int uploadID) throws Exception{
		boolean success = false;
		File xmldir = new File(xmldirstr);
		//grab entity, character, and state terms 
		HashSet<String> states = new HashSet<String>();
		HashSet<String> entities = new HashSet<String>();
		HashSet<String> characters = new HashSet<String>();
		for(File file: xmldir.listFiles()){
			SAXBuilder sax = new SAXBuilder();
			Document doc = sax.build(file);
			Element root = doc.getRootElement();
			for (Object astructure : entity.evaluate(root)) {
				String name = ((Element)astructure).getAttributeValue("name_original");
				String constraint = ((Element)astructure).getAttribute("constraint")!=null? ((Element)astructure).getAttributeValue("constraint")+" ": " ";
				String structModifier = "";
				for(Object acharacter : character.evaluate(astructure)){
					if(((Element)acharacter).getAttribute("is_modifier")!=null){
						if(((Element)acharacter).getAttribute("value")!=null){
							structModifier += ((Element)acharacter).getAttributeValue("value")+ " ";
						}  
					}

					if(((Element)acharacter).getAttribute("value")!=null && ((Element)acharacter).getAttribute("name")!=null){
						String state = ((Element)acharacter).getAttributeValue("value").replaceAll("[_\\W]+$", "");
						if(! state.matches("^\\d.*?") || ! state.matches(".*?\\d$"))
							states.add(state+"#"+((Element)acharacter).getAttributeValue("name"));  //term#category
					}
					if(((Element)acharacter).getAttribute("name")!=null && !((Element)acharacter).getAttributeValue("name").contains("_or_")){
						characters.add(((Element)acharacter).getAttributeValue("name"));  
					}
				}
				if(name.length()>0) entities.add((structModifier+ constraint+name).replaceAll("\\s+",  " ").trim());
			}
		}
		states.addAll(characters);
		
		//store them in : term_category_pair_for_ontology
		//term
		//INSERT INTO `term_category_pair` (`term`, `category`, `uploadID`) VALUES ('ID', 'term', 'category', 'synonyms', 'uploadID', 'removed');
		ToOntologiesDAO dao = ToOntologiesDAO.getInstance();
		if(uploadID>0){
			Hashtable<String, String> syns = dao.getSynonymsFromUpload(uploadID);
			dao.createInputForToOntology(uploadID, syns, entities, states);
			success = true;
		}/*else{
			//no term_category_pair table. This case will not happen when the code is hooked up
			uploadID = dao.getLargestUploadIDFromInputForToOntologies();
			Hashtable<String, String> syns = new Hashtable<String, String> ();
			dao.createInputForToOntology(uploadID, syns, entities, states);		
			success = true;
		}*/
		
		
		//for standing-alone test only: add sentences to sentences table
		int count = 1;
		for(File file: xmldir.listFiles()){
			SAXBuilder sax = new SAXBuilder();
			Document doc = sax.build(file);
			Element root = doc.getRootElement();
			for (Object asentence : sentence.evaluate(root)) {
				String sent = ((Element)asentence).getTextNormalize();
				if(sent.length()>0)
					dao.addSentence(uploadID, count++, file.getName(), sent);
			}
		}
		return success;
	}

	/**
	 * @param args
	 * @throws  
	 */
	public static void main(String[] args) {
		//first do this, then run toontology
		InputTermsPreparation itp = new InputTermsPreparation();
		String xmlDirString = "C:/Users/updates/CharaParserTest/ETC-workshop-RubusSubsets/Rubus33_Rubus33_TC";
		try {
			itp.prepareCandidateTerms(xmlDirString, 162);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
