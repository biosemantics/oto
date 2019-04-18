/**
 * 
 */
package edu.arizona.biosemantics.oto.common.ontologylookup.search.search;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import edu.arizona.biosemantics.oto.common.ontologylookup.search.OntologyLookupClient;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.CompositeEntity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.EntityProposals;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.FormalConcept;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.REntity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.SimpleEntity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.knowledge.Dictionary;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.utilities.Utilities;

/**
 * @author Hong Cui
 * the strategy for handling cases such as 'otic canal' which matches 'otic sensoary canal'.
 * turn 'otic canal' to 'otic .* canal'
 * 
 *  
 *
 */
public class EntitySearcher4 extends EntitySearcher {
	private static final Logger LOGGER = Logger.getLogger(EntitySearcher4.class);  
	private static Hashtable<String, ArrayList<EntityProposals>> cache = new Hashtable<String, ArrayList<EntityProposals>>();
	private static ArrayList<String> nomatchcache = new ArrayList<String>();
	public static float discount = 0.9f;
	private boolean printMatchingDetails = false;
	//private boolean useCache = true;
	/**
	 * 
	 */
	public EntitySearcher4(OntologyLookupClient OLC, boolean useCache){
		super(OLC, useCache);
		
	}


	@Override
	public ArrayList<EntityProposals> searchEntity(String entityphrase, String elocatorphrase,
			String originalentityphrase, String prep, float discount) {

		if(printMatchingDetails) System.out.println("EntitySearcher4: search '"+entityphrase+"[orig="+originalentityphrase+"]'");
		
		//search cache
		if(useCache){
		if(EntitySearcher4.nomatchcache.contains(entityphrase+"+"+elocatorphrase)) return null;
		if(EntitySearcher4.cache.get(entityphrase+"+"+elocatorphrase)!=null) return EntitySearcher4.cache.get(entityphrase+"+"+elocatorphrase);
		}
		//still not find a match, if entityphrase is at least two words long, add wildcard * in spaces

		//search for locator first
		String[] entitylocators = null;
		if(elocatorphrase.length()>0) entitylocators = elocatorphrase.split("\\s*,\\s*");
		ArrayList<SimpleEntity> entityls = new ArrayList<SimpleEntity>();

		if(entitylocators!=null) {
			//TODO: is elocator a reg exp?
			ArrayList<FormalConcept> result = new TermSearcher(OLC, useCache).searchTerm(elocatorphrase, "entity", discount); //TODO: should it call EntitySearcherOriginal? decided not to.
			if(result!=null){
				if(printMatchingDetails) System.out.println("search for locator '"+elocatorphrase+"' found match: ");
				for(FormalConcept fc: result){
					entityls.add((SimpleEntity)fc);
					if(printMatchingDetails) System.out.println(".."+fc.toString());
				}
			}else{ //entity locator not matched
				if(printMatchingDetails) System.out.println("search for locator '"+elocatorphrase+"' found no match");
			}
		}

		
		//search entityphrase using wildcard
		//String myentityphrase = entityphrase.replaceFirst("^\\(\\?:", "").replaceFirst("\\)$", "").trim();
		String aentityphrase = entityphrase;
		
		//embryo proper => .*? embryo proper to match plant embryo proper
		//turn 'otic canal' to 'otic .* canal'
		if(entityphrase.contains(" ")) aentityphrase = ".*?\\b"+ entityphrase.replaceAll("\\s+", "\\\\b.*?\\\\b");//"\\\\" is needed to form the proper reqexp
		//ArrayList<FormalConcept> sentities = TermSearcher.regexpSearchTerm(entityphrase, "entity"); //candidate matches for the same entity
		ArrayList<FormalConcept> sentities = new TermSearcher(OLC, useCache).searchTerm(aentityphrase, "entity", discount); //candidate matches for the same entity
		
		if(sentities!=null){
			if(printMatchingDetails) System.out.println("search for entity '"+aentityphrase+"' found match, forming proposals...");
			boolean found = false;
			EntityProposals ep = new EntityProposals();
			ep.setPhrase(originalentityphrase);
			for(FormalConcept sentityfc: sentities){				
				SimpleEntity sentity = (SimpleEntity)sentityfc;
				sentity.setConfidenceScore(discount*(1f/sentities.size()));
				if(sentity!=null){//if entity matches
					if(elocatorphrase.length()>0){
						for(FormalConcept fc: entityls){
							SimpleEntity entityl = (SimpleEntity)fc;
							entityl.setConfidenceScore(discount*(1f/entityls.size()));
							//relation & entity locator
							CompositeEntity centity = new CompositeEntity();
							centity.addEntity(sentity);								
							centity.addParentEntity(new REntity(Dictionary.partof, entityl));
							centity.setString(originalentityphrase);
							ep.add(centity); //add the other	
							if(printMatchingDetails) System.out.println(".."+centity.toString());
							found = true;
						}
					}else{
						ep.add(sentity); //no locator
						if(printMatchingDetails) System.out.println(".."+sentity.toString());
						found = true;
					}
				}
			}
			if(found==true){
				ArrayList<EntityProposals> entities = new ArrayList<EntityProposals>();
				Utilities.addEntityProposals(entities, ep);
				if(printMatchingDetails) System.out.println("EntitySearcher4 returns:");
				for(EntityProposals aep: entities){
					if(printMatchingDetails) System.out.println("..EntityProposals: "+aep.toString());
				}
				//caching
				if(useCache){
				if(entities==null) EntitySearcher4.nomatchcache.add(entityphrase+"+"+elocatorphrase);
				else EntitySearcher4.cache.put(entityphrase+"+"+elocatorphrase, entities);
				}
				return entities;
			}
		}else{
			if(printMatchingDetails) System.out.println("...search for entity '"+entityphrase+"' found no match");
			if(useCache){
			EntitySearcher4.nomatchcache.add(entityphrase+"+"+elocatorphrase);
			}
		}
		if(printMatchingDetails) System.out.println("EntitySearcher4 calls EntitySearcher5");
		return  new EntitySearcher5(OLC, useCache).searchEntity(entityphrase, elocatorphrase, originalentityphrase, prep, discount*EntitySearcher5.discount);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
