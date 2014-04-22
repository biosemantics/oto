/**
 * 
 */
package edu.arizona.biosemantics.oto.common.ontologylookup.search.search;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;
//import org.jdom.Element;



import edu.arizona.biosemantics.oto.common.ontologylookup.search.OntologyLookupClient;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.CompositeEntity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.Entity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.EntityProposals;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.FormalRelation;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.REntity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.SimpleEntity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.knowledge.Dictionary;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.utilities.Utilities;

/**
 * @author Hong Cui
 * = EntitySearcher2
 */
public class EntityEntityLocatorStrategy implements SearchStrategy {
	ArrayList<EntityProposals> entities;
	private String elocatorphrase;
	private String entityphrase;
	private String prep;
	private String originalentityphrase;
	private static final Logger LOGGER = Logger.getLogger(EntityEntityLocatorStrategy.class);   
	
	//search results:
	private ArrayList<EntityProposals> entitylps; 
	private ArrayList<EntityProposals> sentityps;
	
	private static Hashtable<String, ArrayList<EntityProposals>> cache = new Hashtable<String, ArrayList<EntityProposals>>();
	private static ArrayList<String> nomatchcache = new ArrayList<String>();
	public OntologyLookupClient OLC;
	/**
	 * 
	 */
	public EntityEntityLocatorStrategy(String entityphrase, String elocatorphrase,
			String originalentityphrase, String prep, OntologyLookupClient OLC) {
		this.elocatorphrase = elocatorphrase;
		this.entityphrase = entityphrase;
		this.prep = prep;
		this.originalentityphrase = originalentityphrase;
		this.OLC = OLC;
		LOGGER.debug("EntityEntityLocatorStrategy: search '"+entityphrase+"[orig="+originalentityphrase+"]'");

	}

	/* anterior margin of maxilla => anterior margin^part_of(maxilla)): entity = anterior margin, locator = maxilla
	 */
	@Override
	public void handle() {
		//search cache
		if(EntityEntityLocatorStrategy.nomatchcache.contains(entityphrase+"+"+elocatorphrase)){
			entities = null;
			return;
		}
		if(EntityEntityLocatorStrategy.cache.get(entityphrase+"+"+elocatorphrase)!=null){
			entities = EntityEntityLocatorStrategy.cache.get(entityphrase+"+"+elocatorphrase);
			return;
		}
		
		
		//search entity and entity locator separately

		String[] entitylocators = null;
		if(elocatorphrase.length()>0) entitylocators = elocatorphrase.split("\\s*,\\s*");

		//SimpleEntity entityl = new SimpleEntity();
		//entityl.setString(elocatorphrase);
		//ArrayList<EntityProposals> entitylps = new ArrayList<EntityProposals>();
		//entitylp.setPhrase(elocatorphrase);
		if(entitylocators!=null) {
			//SimpleEntity result = (SimpleEntity) new TermSearcher().searchTerm(elocatorphrase, "entity");
			ArrayList<EntityProposals> result = new EntitySearcherOriginal(OLC).searchEntity(elocatorphrase, "", originalentityphrase, prep); //advanced search
			if(result!=null){
				entitylps = result;
				LOGGER.debug("EEL...searched locator '"+elocatorphrase+"[orig="+originalentityphrase+"]':");
				for(EntityProposals ep: result){
					LOGGER.debug("....."+ep.toString());
				}
			}else{ //entity locator not matched
				LOGGER.debug("EEL...no results from searching '"+elocatorphrase+"[orig="+originalentityphrase+"]':");
				//TODO
			}
		}
		//SimpleEntity sentity = (SimpleEntity)new TermSearcher().searchTerm(entityphrase, "entity");
		
		sentityps = new EntitySearcherOriginal(OLC).searchEntity(entityphrase, "", originalentityphrase, prep); //advanced search
		if(sentityps!=null && entitylps!=null){//if entity matches
			//entity
			for(EntityProposals entitylp: entitylps){
				if(entitylp.getPhrase().length()>0){
					//LOGGER.debug("entity locator phrase is not empty, constructing composite entity...");
					for(Entity entityl: entitylp.getProposals()){
						//relation & entity locator
						FormalRelation rel = Dictionary.partof;
						rel.setConfidenceScore((float)1.0);
						REntity rentity = new REntity(rel, entityl);
						boolean confirmed = false;
						EntityProposals confirmedcentityp = null;
						EntityProposals centityp = null;
						LOGGER.debug("EEL searched entity'"+entityphrase+"[orig="+originalentityphrase+"]':");
						for(EntityProposals sentityp: sentityps){
							LOGGER.debug("....."+sentityp.toString());
							for(Entity sentity: sentityp.getProposals()){
								SimpleEntity bspo = null;
								//sentityps could be a spatial entity, so the final E need to be composed using the spatial convention of SpatialModifiedEntity.
								if(sentity instanceof CompositeEntity && sentity.getPrimaryEntityID().contains("BSPO:")){
									bspo = ((CompositeEntity)sentity).getTheSimpleEntity();
									sentity = ((CompositeEntity)sentity).getEntityLocator().getEntity();
								}
								if(bspo!=null){//composition with a spatial entity
									CompositeEntity ce = new CompositeEntity();
									ce.addEntity(bspo);
									ce.addEntity(rentity);
									rentity = new REntity(rel, ce);
								}
								//check elk: can sentity be part of entityl? 'intermedium (fore)' is part of 'manus'
								//if true for any proposal, return it
								//otherwise, return all
								//if(SearchMain.elk!=null && SearchMain.elk.isPartOf(sentity.getClassIRI(),entityl.getClassIRI())){
								//	confirmed = true;
								//}
								//composite entity
								CompositeEntity centity = new CompositeEntity();
								centity.addEntity(sentity);
								centity.addEntity(rentity);
								centity.setString(this.originalentityphrase);
								//EntityProposals entities = new EntityProposals();
								//entities.setPhrase(sentityp.getPhrase());
								//EntityProposals centityp = new EntityProposals();
								//centityp.setPhrase(sentityp.getPhrase());
								if(confirmed){
									if(confirmedcentityp==null) confirmedcentityp = new EntityProposals();
									confirmedcentityp.setPhrase(this.originalentityphrase);
									//confirmedcentityp.add(centity); //no explicit composition needed
									confirmedcentityp.add(sentity);
									sentity.setConfidenceScore(1f);
									confirmed = false;
									LOGGER.debug("...confirmed:"+confirmedcentityp);
								}else{
									if(centityp==null) centityp = new EntityProposals();
									centityp.setPhrase(this.originalentityphrase);
									centityp.add(centity);
								}

							}
						}
						if(confirmedcentityp!=null){
							entities = new ArrayList<EntityProposals>();
							Utilities.addEntityProposals(entities, confirmedcentityp);
							//LOGGER.debug("..composite entityproposals ="+confirmedcentityp.toString());
						}
						else if(centityp!=null){
							entities = new ArrayList<EntityProposals>();
							Utilities.addEntityProposals(entities, centityp);
							//LOGGER.debug("..composite entityproposals ="+centityp.toString());
						}	
					}

					//caching
					if(entities==null) EntityEntityLocatorStrategy.nomatchcache.add(entityphrase+"+"+elocatorphrase);
					else EntityEntityLocatorStrategy.cache.put(entityphrase+"+"+elocatorphrase, entities);
					
					if(entities!=null){
						LOGGER.debug("EEL: results:");

						for(EntityProposals ep: entities){
							LOGGER.debug(".."+ep.toString());
						}
						return;
					}
				}else{
					//LOGGER.debug("entity locator phrase is empty, saving simple entity as EntityProposals...");
					//EntityProposals entities = new EntityProposals();
					//entities = new EntityProposals();
					//entities.setPhrase(sentityp.getPhrase());
					//entities.add(sentityp.getProposals());
					for(EntityProposals ep: sentityps){
						ep.setPhrase(this.originalentityphrase+"["+ep.getPhrase()+"]");
						if(entities==null) entities = new ArrayList<EntityProposals>();
						Utilities.addEntityProposals(entities, ep);
						//LOGGER.debug("..entityproposals ="+ep.toString());
					}					

					//caching
					if(entities==null) EntityEntityLocatorStrategy.nomatchcache.add(entityphrase+"+"+elocatorphrase);
					else EntityEntityLocatorStrategy.cache.put(entityphrase+"+"+elocatorphrase, entities);
					
					if(entities!=null){
						LOGGER.debug("EEL: results:");
						for(EntityProposals ep: entities){
							LOGGER.debug(".."+ep.toString());
						}
						return;
					}
				}
			}
		}else{
			LOGGER.debug("EntityEntityLocatorStrategy: no results for '"+entityphrase+"[orig="+originalentityphrase+"]':");
		}
	}

	/**
	 * search result for entity (partial)
	 * @return
	 */
	public ArrayList<EntityProposals> getEntityResult() {
		return this.sentityps;
	}

	/**
	 * search result for entity locator (partial)
	 * @return
	 */
	public ArrayList<EntityProposals> getEntityLocatorResult() {
		return this.entitylps;
	}
	
	/**
	 * composite entities with entity and locator
	 * @return
	 */
	public ArrayList<EntityProposals> getEntities() {
		return entities;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


}
