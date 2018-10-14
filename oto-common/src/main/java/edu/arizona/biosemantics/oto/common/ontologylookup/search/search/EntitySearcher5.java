package edu.arizona.biosemantics.oto.common.ontologylookup.search.search;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;



import edu.arizona.biosemantics.oto.common.ontologylookup.search.OntologyLookupClient;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.EntityProposals;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.FormalConcept;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.SimpleEntity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.utilities.Utilities;

/**
 * 
 * @author Hong Cui
 * strategy for phrases that omitted the head noun, for example epibranchial, which
 * may match Epibranchial bone, Epibranchial element, or Epibranchial cartilage
 *
 */
public class EntitySearcher5 extends EntitySearcher {
	private static final Logger LOGGER = Logger.getLogger(EntitySearcher5.class);   
	private static Hashtable<String, ArrayList<EntityProposals>> cache = new Hashtable<String, ArrayList<EntityProposals>>();
	private static ArrayList<String> nomatchcache = new ArrayList<String>();
	public static float discount = 0.7f;
	private boolean useCache = true;
	
	public EntitySearcher5(OntologyLookupClient OLC, boolean useCache){
		super(OLC, useCache);
		
	}

	@Override
	public ArrayList<EntityProposals> searchEntity(
			String entityphrase, String elocatorphrase,
			String originalentityphrase, String prep, float discount) {
		System.out.println("EntitySearcher5: search '"+entityphrase+"[orig="+originalentityphrase+"]'");
		
		//search cache
		if(useCache){
		if(EntitySearcher5.nomatchcache.contains(entityphrase+"+"+elocatorphrase)) return null;
		if(EntitySearcher5.cache.get(entityphrase+"+"+elocatorphrase)!=null) return EntitySearcher5.cache.get(entityphrase+"+"+elocatorphrase);
		}
		//TODO take care of elocatorphrase
		
		//bone, cartilage,  element
		//Epibranchial 1: (0) present and ossified E: Epibranchial 1 bone, Q: present
		//Epibranchial 1: (1) present and cartilaginous E: Epibranchial 1 cartilage, Q: present
		//Epibranchial 1: (2) absent E: Epibranchial 1 cartilage, Q: absent E: Epibranchial 1 bone, Q: absent
		//The curator should use both the cartilage and bone terms to annotate state 2 because the author clearly differentiates between the two.
		 

		//search with regular expression  "epibranchial .*" to find possible missing headnouns 
		String aentityphrase = entityphrase.replaceFirst("^\\(\\?:", "").replaceFirst("\\)$", "");	
		//if(entityphrase.indexOf(" ")<0 && entityphrase.compareTo(originalentityphrase)==0){
		if(aentityphrase.indexOf(" ")<0){
			Hashtable<String, String> headnouns = new Hashtable<String, String>();
			//ArrayList<FormalConcept> regexpresults = TermSearcher.regexpSearchTerm(entityphrase+" .*", "entity");
			ArrayList<FormalConcept> regexpresults = new TermSearcher(OLC, useCache).searchTerm(aentityphrase+" .*", "entity", discount);
			String nouns = null;
			if(regexpresults!=null){
				System.out.println("...search entity '"+aentityphrase+" .*' found match");
				for(FormalConcept regexpresult: regexpresults){
					//regexpresult.setSearchString(originalentityphrase+"["+regexpresult.getSearchString()+"]"); //record originalentityphrase for grouping entity proposals later
					headnouns.put(regexpresult.getLabel().replace(aentityphrase, ""), regexpresult.getId()+"###"
					+regexpresult.getClassIRI()+"###"
							+regexpresult.getPLabel()+"###"
							+regexpresult.getDef()); //don't trim headnoun
				}	
				
				if(regexpresults.size()<10){
					//search headnouns in the context: coronoid .* => coronoid process of ulna
					//headnouns may have leading or trailing spaces, perserve them: hindlimb intermedium; intermedium (fore)
						nouns= searchContext (headnouns); //bone, cartilaginous
				}
			}else{
				System.out.println("...search entity '"+aentityphrase+" .*' found no match");
			}
			
			if(nouns != null){
				System.out.println("...found candidate headnouns '"+nouns+"', forming proposals...");
				EntityProposals ep = new EntityProposals();
				ArrayList<EntityProposals> entities = null;
				//ep.setPhrase(entityphrase+" .*");
				ep.setPhrase(originalentityphrase);
				String[] choices = nouns.split(",");
				float score = discount*(1.0f/regexpresults.size());
				boolean found = false;
				for(String noun: choices){
					String[] idiri = headnouns.get(noun).split("###");
					SimpleEntity sentity = new SimpleEntity();
					sentity.setSearchString(aentityphrase+" .*");
					sentity.setString(aentityphrase);
					sentity.setLabel(noun.startsWith(" ")? aentityphrase+noun: noun+aentityphrase);
					sentity.setId(idiri[0]);
					sentity.setConfidenceScore(score);
					sentity.setClassIRI(idiri[1]);
					sentity.setPLabel(idiri[2]);
					sentity.setDef(idiri.length>3? idiri[3]: "");
					ep.add(sentity);
					System.out.println(".....add a proposal:"+sentity);
					found = true;
				}
				//entities.add(ep);
				if(found){
					if(entities==null) entities = new ArrayList<EntityProposals>();
					Utilities.addEntityProposals(entities, ep);

					//logging
					System.out.println("EntitySearcher5 completed search for '"+aentityphrase+"[orig="+originalentityphrase+"]' and returns:");
					for(EntityProposals aep: entities){
						System.out.println("..: "+aep.toString());
					}	
					
					if(useCache){
					if(entities==null) EntitySearcher5.nomatchcache.add(entityphrase+"+"+elocatorphrase);
					else EntitySearcher5.cache.put(entityphrase+"+"+elocatorphrase, entities);
					}
					return entities;
				}
			}else{
				System.out.println("...candidate headnouns is null, search failed");
			}
				/*else{
			
				//text::Caudal fin
				//text::heterocercal  (heterocercal tail is a subclass of caudal fin, search "heterocercal *")
				//return all matches as candidates
				if(regexpresults!=null){
					EntityProposals entities = new EntityProposals();
					for(FormalConcept regexpresult: regexpresults){
						Entity e = (Entity) regexpresult;
						entities.add(e);
					}			
					return entities;
				}
				
			}*/
			//caching
		}
		if(useCache)
			EntitySearcher5.nomatchcache.add(entityphrase+"+"+elocatorphrase);
		
		System.out.println("...search for entity '"+entityphrase+"' found no match");
		System.out.println("EntitySearcher5 calls EntitySearcher6");
		return new EntitySearcher6(OLC, useCache).searchEntity(entityphrase, elocatorphrase, originalentityphrase, prep, discount*EntitySearcher6.discount);
			
	}

	/**
	 * look into text context for statements containing structid 
	 * to determin the target the context is most close to. for example
	 *  //bone, cartilage,  element
		//Epibranchial 1: (0) present and ossified E: Epibranchial 1 bone, Q: present
		//Epibranchial 1: (1) present and cartilaginous E: Epibranchial 1 cartilage, Q: present
		//Epibranchial 1: (2) absent E: Epibranchial 1 cartilage, Q: absent E: Epibranchial 1 bone, Q: absent
		//The curator should use both the cartilage and bone terms to annotate state 2 because the author clearly differentiates between the two.
	 * 	//could perform a content similarity measure between the definitions associated with the targets in ontology and the text of the statement
	 * @param root
	 * @param structid
	 * @param target
	 * @return
	 */
	private static String searchContext(Hashtable<String, String> targets){
		try{
			//filter other cases: prefer phrases one-word longer than the original phrase 
			String result = "";
			Enumeration<String> keys = targets.keys();
			while(keys.hasMoreElements()){
				String noun = keys.nextElement();
				if(noun.indexOf(" of ")>=0 || noun.indexOf(" and ")>=0) continue; //coronoids 'proccess of ulna': coronoids can't possibility be used to represent a complex concept that require the use of "of"
				if(noun.trim().indexOf(" ")<= 0){
						result += noun+","; //don't trim noun
				}
			}
			if(result.trim().length()>0){
				return result.replaceFirst(",$", "");
			}			
		}catch(Exception e){
			LOGGER.error("", e);
		}		
		return null;
	}
		
	/**
	 * 
	 * @param noun: the headnoun candidate
	 * @param structid: the structure that in need of the headnoun
	 * @param root: the root of the xml file
	 * @return whether the headnoun and the structure is connected via a <relation> chain in xml
	 */
	/*@SuppressWarnings("unchecked")
	private static boolean related(String noun, String structid, Element root) {
		try{
			XPath xpath = XPath.newInstance("//relation[@from='"+structid+"']");
			List<Element> relations = xpath.selectNodes(root);
			for(Element relation: relations){
				String toid = relation.getAttributeValue("to");
				Element related = (Element) XPath.selectSingleNode(root, "//structure[@id='"+toid+"']");
				if(related.getAttributeValue("name").compareTo(noun)==0){
					return true;
				}else{
					return related(noun, toid, root);
				}
			}
		}catch(Exception e){
			LOGGER.error("", e);
		}
		return false;
	}*/

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
