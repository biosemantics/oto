/**
 * 
 */
package edu.arizona.biosemantics.oto.common.ontologylookup.search.search;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.arizona.biosemantics.oto.common.ontologylookup.search.OntologyLookupClient;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.CompositeEntity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.Entity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.EntityProposals;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.FormalConcept;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.REntity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.SimpleEntity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.knowledge.Dictionary;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.knowledge.TermOutputerUtilities;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.utilities.Utilities;

/**
 * @author Hong Cui
 * it searches different variations of the E/EL compositions using all the elements.
 * 
 * For examples: 
 * input: e:postaxial process, el:modifier fibula
 * generate variations like:
 * 1. (postaxial|syn_ring) (process|crest|syn_ring) of modifier (fibula|fibular|adj)
 * 2. modifier (fibula|fibular|adj) (postaxial|syn_ring) (process|crest|syn_ring) 
 * 3. (postaxial|syn_ring) modifier (fibula|fibular|adj) (process|crest|syn_ring)
 * 4. modifier (postaxial|syn_ring) (fibula|fibular|adj) (process|crest|syn_ring)
 * 
 */
public class EntitySearcher1 extends EntitySearcher {
	private static final Logger LOGGER = Logger.getLogger(EntitySearcher1.class);   
	private static boolean debug_permutation = false;
	private static Hashtable<String, ArrayList<EntityProposals>> cache = new Hashtable<String, ArrayList<EntityProposals>>();
	private static ArrayList<String> nomatchcache = new ArrayList<String>();
	private static final float partial = 0.8f;
	public static float discount = 1.0f;
	private boolean printMatchingDetails = false;
	//boolean debug = true;
	//private boolean useCache=true;

	/**
	 * 
	 */
	public EntitySearcher1(OntologyLookupClient OLC, boolean useCache){
		super(OLC, useCache);
	}
	//TODO patterns s0fd16381: maxillae, anterior end of 
	//entityphrase could be reg exp such as (?:A of B| B A) of (?: C D | D of C) or a simple string
	/* (non-Javadoc)
	 * @see EntitySearcher#searchEntity(org.jdom.Element, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public ArrayList<EntityProposals> searchEntity(
			String entityphrase, String elocatorphrase,
			String originalentityphrase, String prep, float discount) {
		
		if(printMatchingDetails) System.out.println("EntitySearcher1: search '"+entityphrase+"[orig="+originalentityphrase+"]'");

		//search cache
		if(useCache){
			if(EntitySearcher1.nomatchcache.contains(entityphrase+"+"+elocatorphrase)) return null;
			if(EntitySearcher1.cache.get(entityphrase+"+"+elocatorphrase)!=null) return EntitySearcher1.cache.get(entityphrase+"+"+elocatorphrase);
		}
		ArrayList<EntityProposals> entities = null;
		EntityProposals ep = new EntityProposals(); //search results
		//entityphrase =  "posterior radials";
		//elocatorphrase = "anterior dorsal fin";
		//save phrases as components

		EntityComponents ecs = new EntityComponents(entityphrase, elocatorphrase);
		ArrayList<EntityComponent> components = ecs.getComponents(); //each component is an entity or an entity locator

		//construct pre-composed variations: selected permutations without repetition 
		ArrayList<String> variations  = new ArrayList<String>(); 
		//permutation on synrings that are results of subcomponents permutation
		permutation(components, variations); 
		if(printMatchingDetails) System.out.println("...created permutation variations");
		if(printMatchingDetails) System.out.println("'"+entityphrase+" , "+elocatorphrase+"' generated "+variations.size()+" variations:");
		for(String variation : variations){
			if(printMatchingDetails) System.out.println("....."+variation);
		}
		//search variations for pre-composed terms one by one, return all the results
		boolean found = false;
		if(printMatchingDetails) System.out.println("search variations one by one...");
		for(String variation: variations){
			if(printMatchingDetails) System.out.println("...search variation '"+variation+"'");
			//ArrayList<FormalConcept> entityfcs = new TermSearcher().regexpSearchTerm(variation, "entity"); //remove indexes from variation before search
			ArrayList<FormalConcept> entityfcs = new TermSearcher(OLC, useCache).searchTerm(variation, "entity", discount); //remove indexes from variation before search
			//check for the strength of the match: related synonyms: (?:(?:crista) (?:parotica)) entity=>tegmen tympani
			if(entityfcs!=null){
				for(FormalConcept entity:entityfcs){
					if(entity!=null){
						found = true;
						//ep.setPhrase(entityphrase);
						ep.setPhrase(originalentityphrase);
						ep.add((Entity)entity); //all variations are alternative entities (i.e. proposals) for the phrase
					}
				}
				if(printMatchingDetails) System.out.println("...found match: "+ep.toString());
			}
		}
		if(found){
			//entities.add(ep);
			if(entities==null) entities = new ArrayList<EntityProposals>();
			Utilities.addEntityProposals(entities, ep);
			
			if(printMatchingDetails) System.out.println("EntitySearcher1 found matched variations, returns:");
			//for(EntityProposals aep: entities){
			//	if(printMatchingDetails) System.out.println("..:"+aep.toString());
			//}
			
			//caching
			if(useCache){
				if(entities==null) EntitySearcher1.nomatchcache.add(entityphrase+"+"+elocatorphrase);
				else EntitySearcher1.cache.put(entityphrase+"+"+elocatorphrase, entities);
			}
			return entities;
		}else{
			return entities;
		}
		/* disable post-composition 01/19/22
		ArrayList<EntityProposals> bestpartialresults = null; 
		//failed to find pre-composed terms, try to post-compose using part_of
		//call on EntityEntityLocatorStrategy on expressions without spatial terms:
		//(the attachment of spatial terms to parent entity is different from attachement of a child entity to parent entity)
		//like EntitySearch2, but more flexible: may call the strategy on different entity/entity locator combinations
		//TODO: need more work: what's entityphrase and elocatorphrase?
		boolean startwithspatial = false;
		Pattern p = Pattern.compile("^("+Dictionary.spatialtermptn+")\\b\\s*\\b("+Dictionary.allSpatialHeadNouns()+")?\\b");
		Matcher m = p.matcher(entityphrase);
		if(m.find()) startwithspatial = true;
		//boolean hasspatial = ecs.containsSpatial();
		//if(elocatorphrase.trim().length()>0 && !hasspatial){//call EELS strategy when there is an entity locator to avoid infinite loop. 
		if(!startwithspatial){//call EELS strategy when there is an entity locator to avoid infinite loop. 
			//ep.setPhrase(entityphrase);
			if(printMatchingDetails) System.out.println(System.getProperty("line.separator")+"EntitySearcher1 calls EntityEntityLocatorStrategy");
			if(components.size()==1){
				if(printMatchingDetails) System.out.println("find components size = 1");
				//has one component only, split the component into entity and entitylocator
				ArrayList<String> perms = components.get(0).getPermutations(); //perms are not reg exps
				for(String perm : perms){
					if(perm.indexOf(" of ")<0) continue; //there must be another variation with " of " that is equivalent to this variation
					if(this.debug_permutation) System.err.println("variation to split: "+perm);
					String[] parts = perm.split("\\s+of\\s+");
					if(parts.length>1){
						for(int l = 0; l < parts.length-1; l++){ //length of entity
							String aentityphrase = Utilities.join(parts, 0, l, " of ");	
							String aelocatorphrase =  Utilities.join(parts, l+1, parts.length-1, " of ");
							if(printMatchingDetails) System.out.println("..EEL search: entity '"+aentityphrase+"' and locator '"+aelocatorphrase+"'");
							EntityEntityLocatorStrategy eels = new EntityEntityLocatorStrategy(aentityphrase, aelocatorphrase, originalentityphrase, prep, OLC, useCache);
							eels.handle(discount);
							ArrayList<EntityProposals> entity = eels.getEntities(); //a list of different entities: both sexes => female and male
							if(entity != null){
								found = true;
								//ep.add(entity);
								//entities.add(ep);
								//entities.addAll(entity);	
								if(entities==null) entities = new ArrayList<EntityProposals>();
								for(EntityProposals aep: entity){
									Utilities.addEntityProposals(entities, aep);
									if(printMatchingDetails) System.out.println("..EEL adds proposals:"+aep);
								}
							}else{
								
								ArrayList<EntityProposals> eresult = eels.getEntityResult();
								ArrayList<EntityProposals> elresult = eels.getEntityLocatorResult();
								ArrayList<EntityProposals> best = null;
								if(eresult!=null) best = removeRedundancy(eresult);
								else if(elresult!=null) best = removeRedundancy(elresult);
								if(best!=null){
									if(bestpartialresults==null){
										bestpartialresults = new ArrayList<EntityProposals>();
										bestpartialresults.addAll(best);
									}else{
										bestpartialresults.addAll(best);
										bestpartialresults = removeRedundancy(bestpartialresults);
									}
								}
								if(bestpartialresults!=null){
									if(printMatchingDetails) System.out.println("..EEL return partial matches");
									for(EntityProposals aep: bestpartialresults){
										if(printMatchingDetails) System.out.println("..:"+aep.toString());
									}
								}
							}
						}
					}
				}
			}else{
				if(printMatchingDetails) System.out.println("find components size > 1");
				//has multiple components
				//use the first n as entity, the remaining as entity locator
				//form simple string, not reg exp for entity and locator
				for(int n = 1; n < components.size(); n++){
					String aentityphrase="", aelocatorphrase="";
					for(int i = 0; i < n; i++){ //
						String var = components.get(i).getPhrase().split("\\|")[0];
						var = var.replaceAll("[(:?)]", "");
						aentityphrase += var+" of ";
						//ArrayList<String> perms = components.get(i).getPermutations();
						//String vars = "";
						//for(String perm : perms){
						//	vars += perm+"|"; //include all perms in search
						//}
						//vars = vars.replaceFirst("\\|$", "");
						//aentityphrase +="(?:"+vars+") of ";
						
					}
					aentityphrase = aentityphrase.replaceFirst(" of $", ""); // (?:A of B| B A) of (?: C D | D of C)
					//use the rest as entity locators
					for(int i = n; i < components.size(); i++){ //
						String var = components.get(i).getPhrase().split("\\|")[0];
						var = var.replaceAll("[(:?)]", "");
						aelocatorphrase += var+" of ";
						/*
						ArrayList<String> perms = components.get(i).getPermutations();
						String vars = "";
						for(String perm : perms){
							vars += perm+"|";
						}
						vars = vars.replaceFirst("\\|$", "");
						aelocatorphrase +="(?:"+vars+") of ";
					}
					aelocatorphrase = aelocatorphrase.replaceFirst(" of $", "").trim();//similar to aentityphrase: (?:A of B| B A) of (?: C D | D of C)

					//if(printMatchingDetails) System.out.println("..EEL search: entity '"+entityphrase+"' and locator '"+elocatorphrase+"'");
					//entityphrase = entityphrase.replaceFirst("(\\(\\?:|\\)|\\|)", "");
					//elocatorphrase = elocatorphrase.replaceFirst("(\\(\\?:|\\)|\\|)", "");
					if(printMatchingDetails) System.out.println("ES1->EEL...entity:'"+aentityphrase+"' entitylocator:'"+aelocatorphrase+"'");
					if(elocatorphrase.length()>0){
						EntityEntityLocatorStrategy eels = new EntityEntityLocatorStrategy(aentityphrase, aelocatorphrase, originalentityphrase, prep, OLC, useCache);
						eels.handle(discount);
						ArrayList<EntityProposals> entity = eels.getEntities(); //a list of different entities: both sexes => female and male
						if(entity != null){
							found = true;
							//ep.add(entity);
							//entities.add(ep);
							//entities.addAll(entity);	
							if(entities==null) entities = new ArrayList<EntityProposals>();
							for(EntityProposals aep: entity){
								Utilities.addEntityProposals(entities, aep);
								if(printMatchingDetails) System.out.println("..EEL adds proposals:"+aep);
							}
						}else{
							if(printMatchingDetails) System.out.println("..EEL didn't return composed entity");
							ArrayList<EntityProposals> eresult = eels.getEntityResult();
							ArrayList<EntityProposals> elresult = eels.getEntityLocatorResult();
							ArrayList<EntityProposals> best = null;
							if(eresult!=null) best = removeRedundancy(eresult);
							else if(elresult!=null) best = removeRedundancy(elresult);
							if(best!=null){
								if(bestpartialresults==null){
									bestpartialresults = new ArrayList<EntityProposals>();
									bestpartialresults.addAll(best);
								}else{
									bestpartialresults.addAll(best);
									bestpartialresults = removeRedundancy(bestpartialresults);
								}
							}
							if(bestpartialresults!=null){
								if(printMatchingDetails) System.out.println("..EEL return partial matches");
								for(EntityProposals aep: bestpartialresults){
									if(printMatchingDetails) System.out.println("..:"+aep.toString());
								}
							}
						}
					}
				}
			}
		}
		//if(found) return entities;

		//deal with spatial expressions
		if(startwithspatial){
			if(printMatchingDetails) System.out.println(System.getProperty("line.separator")+"EntitySearcher1 calls SpatialModifiedEntityStrategy");

			//TODO: need more work: what's entityphrase and elocatorphrase?
			SpatialModifiedEntityStrategy smes = new SpatialModifiedEntityStrategy(entityphrase, elocatorphrase, originalentityphrase, prep, OLC, useCache);
			smes.handle(discount);
			ArrayList<EntityProposals> entity = smes.getEntities();
			if(entity != null){
				found = true;
				//ep.add(entity);
				//entities.add(ep);
				//entities.addAll(entity); //add a list of different entities: both sexes => female and male
				if(entities==null) entities = new ArrayList<EntityProposals>();
				for(EntityProposals aep: entity){
					if(printMatchingDetails) System.out.println("..SME adds proposals: "+aep.toString());
					Utilities.addEntityProposals(entities, aep);
				}
			}else{
				if(printMatchingDetails) System.out.println("..SME didn't return composed entity");
				ArrayList<EntityProposals> eresult = smes.getEntityResult();
				ArrayList<EntityProposals> elresult = smes.getEntityLocatorResult();
				ArrayList<EntityProposals> best = null;
				if(eresult!=null) best = removeRedundancy(eresult);
				else if(elresult!=null) best = removeRedundancy(elresult);
				if(best!=null){
					if(bestpartialresults==null){
						bestpartialresults = new ArrayList<EntityProposals>();
						bestpartialresults.addAll(best);
					}else{
						bestpartialresults.addAll(best);
						bestpartialresults = removeRedundancy(bestpartialresults);
					}
				}
				if(bestpartialresults!=null){
					if(printMatchingDetails) System.out.println("..SME return partial matches");
					for(EntityProposals aep: bestpartialresults){
						if(printMatchingDetails) System.out.println("..:"+aep.toString());
					}
				}
			}
		}
		//if(found) return entities;
		
		if(printMatchingDetails) System.out.println(System.getProperty("line.separator")+"EntitySearcher1 calls EntitySearcher4");
		ArrayList<EntityProposals> entity = new EntitySearcher4(OLC, useCache).searchEntity(entityphrase, elocatorphrase, originalentityphrase, prep, discount*EntitySearcher4.discount);
		//proximal tarsal element:
		//SpaticalModifiedEntity: phrase=proximal region entity=proximal region score=1.0 and (part_of some phrase=tarsal\b.* entity=tarsal bone score=0.5) 
		//EntitySearcher5: phrase=proximal tarsal\b.* entity=proximal tarsal bone score=0.5
		//TODO: save both or select one? 
		if(entity!=null){
			//entities.addAll(entity);
			if(entities==null) entities = new ArrayList<EntityProposals>();
			for(EntityProposals aep: entity){
				if(printMatchingDetails) System.out.println("..ES4 adds proposals: "+aep.toString());
				Utilities.addEntityProposals(entities, aep);
			}
		}else{
			if(printMatchingDetails) System.out.println("ES4.. found no match");
		}

		if(entities == null || isOriginatedFromPartialResults(entities)){	
			if(bestpartialresults!=null){
				if(printMatchingDetails) System.out.println("..no better match, use bestpartialresults:");	
				if(printMatchingDetails) System.out.println("..no better match, use bestpartialresults:");
				bestpartialresults = removeRedundancy(bestpartialresults);
				bestpartialresults =lowerscore(bestpartialresults);
				if(entities==null) entities = new ArrayList<EntityProposals>();
				entities.addAll(bestpartialresults);
				for(EntityProposals aep: entities){
					if(printMatchingDetails) System.out.println("..:"+aep.toString());
					if(printMatchingDetails) System.out.println("..:"+aep.toString());
				}
			}
		}
		//logging
		if(entities!=null){
			if(printMatchingDetails) System.out.println(System.getProperty("line.separator")+"EntitySearcher1 completed search for '"+entityphrase+"[orig="+originalentityphrase+"]' and returns:");
			for(EntityProposals aep: entities){
				if(printMatchingDetails) System.out.println("..:"+aep.toString());
			}
		}
		
		//caching
		if(useCache){
			if(entities==null) EntitySearcher1.nomatchcache.add(entityphrase+"+"+elocatorphrase);
			else EntitySearcher1.cache.put(entityphrase+"+"+elocatorphrase, entities);
		}
		
		return entities;
		//return new EntitySearcher5().searchEntity(root, structid, entityphrase, elocatorphrase, originalentityphrase, prep);
		 disable post-composition 01/19/22 */
	}

	/**
	 * when entities are based on partial results, their confidence score is less than this.partial
	 * @param entities
	 * @return true if all entities are from partial
	 */
	private boolean isOriginatedFromPartialResults(
			ArrayList<EntityProposals> entities) {
		for(EntityProposals ep: entities){
			for(Entity e: ep.getProposals()){
				if(e.getConfidenceScore()>this.partial){
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * partial results should have a lower score
	 * @param bestpartialresults
	 * @return
	 */
	private ArrayList<EntityProposals> lowerscore(
			ArrayList<EntityProposals> bestpartialresults) {
		for(EntityProposals ep: bestpartialresults){
			for(Entity e: ep.getProposals()){
				e.setConfidenceScore(e.getConfidenceScore()*partial);
			}
		}
		return bestpartialresults;
	}
	/**
	 * remove redundant matches of parts
	 * @param bestpartialresults
	 * @return the unique, longest matches
	 */
	private ArrayList<EntityProposals> removeRedundancy(
			ArrayList<EntityProposals> bestpartialresults) {
		ArrayList<EntityProposals> tobecleaned = new ArrayList<EntityProposals>();
		for(int i = 0; i<bestpartialresults.size(); i++){
			for(int j = i+1; j<bestpartialresults.size(); j++){
				EntityProposals ep1 = bestpartialresults.get(i);
				EntityProposals ep2 = bestpartialresults.get(j);
				if(ep1.equals(ep2) || ep1.content().contains(ep2.content())){
					tobecleaned.add(ep2);
				}else if (ep2.content().contains(ep1.content())){
					tobecleaned.add(ep1);
				}
			}
		}
		
		for(EntityProposals ep: tobecleaned){
			bestpartialresults.remove(ep);
		}
		return bestpartialresults;
	}
	/**
	 * find the best (covers the most concepts in the original search phrases) proposals
	 * P1:phrase=(?:lobe) entity=lobe score=1.0 and (part_of some phrase=(?:(?:caudal) (?:fin)) entity=caudal fin score=1.0)
	 * P2:phrase=(?:caudal) (?:fin) .*? (?:lobe) entity=caudal fin upper lobe score=1.0
	   P3:phrase=(?:caudal) (?:fin) .*? (?:lobe) entity=caudal fin lower lobe score=1.0
	 * @param proposals
	 * @param elocatorphrase 
	 * @param entityphrase 
	 * @return a set of best proposals
	 */
	/*private ArrayList<EntityProposals> best(ArrayList<EntityProposals> proposals) {
		Hashtable<String, Set<Entity>> scores = new Hashtable<String, Set<Entity>>();
		int max = -1;
		for(EntityProposals ep: proposals){
			for(Entity e: ep.getProposals()){
				String tokens = getTokens(e, "");				
				String[] covered = tokens.trim().split("\\s+");
				int c = (new HashSet<String>(Arrays.asList(covered))).size();
				Set<Entity> ps = scores.get(c+"");
				if(ps==null){
					ps = new HashSet<Entity>();
				}
				ps.add(e);
				scores.put(c+"", ps);
				if(c>max) max = c;
			}
		}
		EntityProposals best = new EntityProposals();
		for(Entity e: scores.get(max+""))
			best.add(e);
		
		ArrayList<EntityProposals> results = new ArrayList<EntityProposals>();
		results.add(best);
		return results;
	}*/
	
	/**
	 * 
	 * @param e
	 * @param tokens
	 * @return may contain trailing space and multiple spaces in tokens
	 */
	private String getTokens(Entity e, String tokens) {	
		if(e instanceof SimpleEntity) return e.getString().substring(0, e.getString().indexOf("|")).replaceAll("(\\(\\?:|\\)|\\.\\*\\?)", "")+" ";
		else if(e instanceof CompositeEntity){
			ArrayList<Entity> es = ((CompositeEntity) e).getEntities();
			for(Entity e1: es){
				tokens +=getTokens(e1, tokens); //
			}
		}else if(e instanceof REntity){
			Entity e1 = ((REntity) e).getEntity();
			return getTokens(e1, tokens);
		}
		return tokens;
	}
	/**
	 * 'posterior radials,anterior dorsal fin' generated 2 variations:
		..(?:(?:posterior|posterior side) (?:radials)) of (?:(?:fin) of (?:anterior|anterior side) (?:dorsal|dorsal side)|(?:anterior|anterior side) (?:dorsal|dorsal side) (?:fin))
		..(?:(?:fin) of (?:anterior|anterior side) (?:dorsal|dorsal side)|(?:anterior|anterior side) (?:dorsal|dorsal side) (?:fin)) (?:(?:posterior|posterior side) (?:radials))

	 * 'ventral radial crest,' generated 1 variations
	 *..(?:(?:process|crest|ridge|tentacule|shelf|flange|ramus) of (?:ventral|ventral side) (?:radial|radius)|(?:ventral|ventral side) (?:radial|radius) (?:process|crest|ridge|tentacule|shelf|flange|ramus))
	 * 
	 * 'posterior postfrontal,'  generated 1 variations:
	 * 1. (?:(?:posterior|posterior side) (?:postfrontal))
	 * @param components
	 * @param variations
	 */
	public  static void permutation(ArrayList<EntityComponent> components, ArrayList<String> variations) { 
		//if(printMatchingDetails) System.out.println("round 0: i=-1 "+ "components size="+components.size()+" prefix=''");
		permutation("", components, variations, clone(components), -1); 
		//remove indexes 
		for(int i = 0; i < variations.size(); i++){
			variations.set(i, variations.get(i).replaceAll("\\(-?\\d+\\)", "").trim());
		}		
	}


	/**
	 * collect permutations of a list of components
	 * @param prefix: current output sequence
	 * @param components: remaining components to be added to the sequence
	 * @param variations: permutations collected so far
	 * @param clone: the original components
	 * @param lastindex: index of the last component in the original components(clone) that was added to the prefix. 
	 */
	private static void permutation(String prefix, ArrayList<EntityComponent> components, ArrayList<String> variations,  ArrayList<EntityComponent> clone, int lastindex) {
		int n = components.size();
		if (n == 0){
			if(!clone.get(lastindex).isSpatial() || lastindex == 0){ //the last component can not be a spatial term, if there are multiple components (lastindex!=0)
				variations.add(prefix+"("+lastindex+")");
				if(debug_permutation) System.err.println("variation: "+prefix+"("+lastindex+")");
			}
		}
		else {
			for (int i = 0; i < n; i++){
				ArrayList<EntityComponent> reducedcomps = new ArrayList<EntityComponent>();
				for(int j = 0; j < n; j++){
					if(j!=i) reducedcomps.add(components.get(j)); //reducedcomps = components - element_i
				}
				if(debug_permutation) System.err.println("prefix="+prefix+" new round: i="+i+ " components size="+reducedcomps.size());
				String newprefix = newPrefix(prefix, lastindex, clone, clone.indexOf(components.get(i)), i, components);
				if(debug_permutation) System.err.println("newprefix="+newprefix+" new round: i="+i+ " components size="+reducedcomps.size());
				permutation(/*(prefix+" "+components.get(i).getSynRing()).trim()*/newprefix, reducedcomps, variations, clone, clone.indexOf(components.get(i)));
			}
		}
	}

	/**
	 * decide whether to concatenate oldprefix and components.get(i).getSynRing() directly or to add " of " between them.
	 * add "of" after a structure when the lastindex < newindex (i.e., putting a child before a parent organ)
	 * @param oldprefix: the current prefix 
	 * @param lastindex: index of the last component in the original components(clone) that was added to prefix. 
	 * @param newindex:  index of the component i in the original clone
	 * @param i: index of the to-be-added component in current components
	 * @param components
	 * @return
	 */

	private static String newPrefix(String oldprefix, int lastindex, ArrayList<EntityComponent> clone, int newindex, int i,
			ArrayList<EntityComponent> components) {
		//if(lastindex>=0 && lastindex<components.size() && clone.get(lastindex).isStructure() && lastindex < newindex){
		if(lastindex>=0 && clone.get(lastindex).isStructure() && lastindex < newindex){
			return (oldprefix+"("+lastindex+") of "+components.get(i).getPhrase()).trim();
		}
		return (oldprefix+"("+lastindex+") "+components.get(i).getPhrase()).trim();
	}

	private static ArrayList<EntityComponent> clone(
			ArrayList<EntityComponent> components) {
		ArrayList<EntityComponent> clone = new ArrayList<EntityComponent>();
		for(int i = 0 ; i < components.size(); i++){
			clone.add(components.get(i));
		}
		return clone;
	}


	/**
	 * private class
	 * @author Hong Cui
	 *
	 */
	private class EntityComponents{
		ArrayList<EntityComponent> components = new ArrayList<EntityComponent>(); //the order of the elements indicate the part of relation, 0 part of 1 part of 2 ...

		public EntityComponents(String entity, String locator){
			//1. join entityphrase and elocatorphrase, then split them into entity components, sorted from child to parent organ
			components = joinAndSplit(entity, locator);

			//2. create syn_ring for each component
			//setSynRings(components);
		}

		/**
		 * turn entityphrase + elocatorphrases to a list of EntityComponents, each EntityComponent represents one structure, e.g. 'dorsal', 'fin', 'dorsal region', 'long tooth'
		 * @param entityphrase: entities separated by ',', later entities are parent organs of the earlier ones
		 * @param elocatorphrase: entity locators separated by ',', later entities are parent organs of the earlier ones
		 * @return
		 */
		private ArrayList<EntityComponent> joinAndSplit(String entityphrase,
				String elocatorphrase) {
			ArrayList<EntityComponent> components = new ArrayList<EntityComponent>();
			entityphrase = entityphrase+","+elocatorphrase; //join

			//split: separate adjective organs ('nasal') and modified organ ('bone');
			//keep spatial term  ('dorsal') and modified organ ('fin') together, keep "dorsal margin" as one part, separate them from other parts
			//split on " of ".
			//String spatialphraseptn = "(?:"+Dictionary.singlewordspatialtermptn +")?\\s*"
			//		+ "\\b(?:(?:"+Dictionary.allSpatialHeadNouns()+")\\b|\\b(?:"+TermOutputerUtilities.adjectiveorganptn+"))";
			String singleptn = "((?:"+Dictionary.singlewordspatialtermptn +")\\b\\s*\\b(?:"+Dictionary.allSpatialHeadNouns()+")?\\b\\s*)|"
					+ "\\b("+OLC.ontoutil.adjectiveorganptn+")\\b\\s*";
			String spatialphrasesptn = "((?:"+singleptn+")+)"; //allow selection of either single spatial term, spatial phrase, or organadjective, or combination of spatial and organadjs
			String[] entityphrases = entityphrase.split("\\s*(,| of )\\s*");
			//order of the phrases matters
			for(String phrase: entityphrases){
				phrase = phrase.trim();
				if(phrase.length()==0) continue;
				String phrasecp = phrase;
				//phrase = "medioventral axis radial element";
				//Pattern p = Pattern.compile("(.*?)\\b("+Dictionary.spatialtermptn+"|"+TermOutputerUtilities.adjectiveorganptn+")\\b(.*)"); //this splits on single-word spatial term also
				Pattern p = Pattern.compile("(.*?)\\b"+spatialphrasesptn+"\\b(.*)");
				Matcher m = p.matcher(phrase);
				String temp = ""+"";
				while(m.matches()){
					//temp += m.group(1)+"#"+m.group(2)+"#";
					//temp += m.group(1)+m.group(2)+"#";
					//phrase = m.group(3);
					temp += m.group(1);
					phrase = m.group(5);
					String matched = m.group(2);
					Pattern p1 = Pattern.compile(singleptn);
					Matcher m1 = p1.matcher(matched);
					while(m1.find()){
						if(m1.group(1)!=null && m1.group(1).length()>0){ //spatial
							if(m1.group(1).trim().indexOf(" ")>0) temp +="#"+m1.group(1)+"#";
							else temp +="#"+m1.group(1)+" ";
							matched = matched.substring(m1.end(1)).trim();
						}
						if(m1.group(2)!=null && m1.group(2).length()>0){
							temp +="#"+m1.group(2)+"#";
							matched = matched.substring(m1.end(2)).trim();
						}
						m1 = p1.matcher(matched);
					}
					m = p.matcher(phrase);
				}
				temp +=phrase.trim();//appending the original string to the tokens separated by #
				temp = temp.trim();
				if(debug_permutation) System.err.println("split&join: '"+phrasecp+"' =>'"+temp+"'");
				temp =  temp.replaceAll("\\s+",  " ").replaceAll("(^#+|#+$)", "");
				String[] temps =temp.split("\\s*#+\\s*");
				if(temps.length==1){ //if the split didn't split, force split on spaces
					ArrayList<FormalConcept> test = new TermSearcher(OLC, useCache).searchTerm(phrasecp, "entity", discount); 
					if(test==null) temps = temp.split("\\s+"); 
				}
				ArrayList<EntityComponent> thiscomponents = new ArrayList<EntityComponent>();
				//for(String part: temps){
				for(int i = temps.length-1; i>=0; i--){
					String part = temps[i];
					part = part.trim();
					if(part.length()>0){
						//parts.add(t);
						EntityComponent ec = new EntityComponent(part);
						//ec.setSynRing(this.getSynRing4Phrase(part));
						if(part.indexOf(" ")<0 && part.matches(Dictionary.singlewordspatialtermptn)){
							ec.isSpatial(true);
							ec.isStructure(false);
						}
						else{
							ec.isStructure(true);
							ec.isSpatial(false);
						}
						thiscomponents.add(ec);
					}
				}

				//permute parts in the phrase
				ArrayList<String> permutations = new ArrayList<String>();
				EntitySearcher1.permutation(thiscomponents, permutations); 
				//save EntityComponent
				String thephrase = "";
				for(String permu : permutations){ //A B; B of A
					thephrase += permu+"|"; //A B|B of A
				}
				thephrase = "(?:"+thephrase.replaceFirst("\\|$", "").trim()+")";
				EntityComponent ec = new EntityComponent(thephrase);
				ec.isStructure(true); //each phrase representing a structure
				ec.setPermutations(permutations);
				components.add(ec);

			}
			return components;
		}

		/**
		 * sort organs so parent organs come later
		 * turn 'ventral radial process' to 'process, ventral radial'
		 * turn 'radial ventral region' to 'ventral region, radial'
		 * @param phrases: phrases without comma or 'of'
		 * @return sorted list of strings
		 */
		/*private ArrayList<String> sort(String[] phrases){
			ArrayList<String> sorted = new ArrayList<String>();
			for(String phrase: phrases){
				Pattern p = Pattern.compile("(.*?)\\b("+Dictionary.spatialtermptn+"|"+TermOutputerUtilities.adjectiveorganptn+")\\b(.*)");
				Matcher m = p.matcher(phrase);
				String temp = "";
				while(m.matches()){
					temp += m.group(1)+"#"+m.group(2)+"#";
					phrase = m.group(3);
					m = p.matcher(phrase);
				}
				temp +=phrase.trim();//appending the original string to the tokens separated by #
				String[] temps = temp.split("\\s*#\\s*");
				for(int)
			}
			return sorted;
		}*/

		/**
		 * Set the syn ring for each component. Treat syn rings for different permutations the alternatives in the syn ring
		 * @param components
		 */
		/*private void setSynRings(ArrayList<EntityComponent> components) {
			for(EntityComponent component: components){
				String synring = "";
				ArrayList<String> permus = component.getPermutations();
				for(String permu : permus){ //A B; B A
					//synring += getSynRing4Phrase(permu)+"|"; //(A|A1|A2) (B|B1)|(B|B1) (A|A1|A2)
					synring += permu+"|"; //(A|A1|A2) (B|B1)|(B|B1) (A|A1|A2)
				}
				component.setSynRing("(?:"+synring.replaceFirst("\\|$", "").trim()+")");
			}
		}*/

		/**
		 * dorsal fin
		 * @param phrase: (?:(?:shoulder) (?:girdle)) or dorsal fin
		 * @return (?:dorsal|dorsal side) (?:fin)
		 */
		
		public  ArrayList<EntityComponent> getComponents(){
			return this.components;
		}

		public EntityComponent getComponent(int index){
			return this.components.get(index);
		}

		public int indexOf(EntityComponent c){
			return this.components.indexOf(c);
		}

		/**
		 * whether this set of entitycomponents contain a spatial term
		 * @return
		 */
		public boolean containsSpatial(){
			//[dorsal radials, posterior dorsal fin] => true
			//[anterior process, maxilla] => true
			//for(EntityComponent cp: components){
				if(components.get(0).getPhrase().matches(".*?\\b("+Dictionary.spatialtermptn+")\\b.*"))
					return true;
			//}
			return false;
		}
	}

	/**
	 * private class
	 * @author Hong Cui
	 *
	 */
	private class EntityComponent{
		//String synring; //for the component and is the permutations concatenated as alternatives
		String phrase; //e.g. posterior dorsal fin, or fin
		ArrayList<String> permutations; // permutations of the parts (represented as synrings) in the phrase
		boolean spatial = false;
		boolean structure = false;

		public EntityComponent(String phrase){ this.phrase = phrase;}

		public String getPhrase(){return this.phrase;}
		
		public void setPermutations(ArrayList<String> permutations) {
			this.permutations = permutations;					
		}

		public ArrayList<String> getPermutations() {
			return this.permutations;				
		}

		/**
		 * 
		 * @param synring
		 */
		//public void setSynRing(String synring) {this.synring = synring;}

		/**
		 * used only for one-word spatial terms
		 * @return
		 */
		public void isSpatial(boolean isspatial) {this.spatial = isspatial;}

		/**
		 * used for one-word or n-word phrases
		 * @return
		 */
		public void isStructure(boolean isstructure) {this.structure = isstructure;}

		//public String getSynRing(){	return this.synring;}

		/**
		 * used only for one-word spatial terms
		 * @return
		 */
		public boolean isSpatial(){return spatial;} 

		/**
		 * used for one-word or n-word phrases
		 * @return
		 */
		public boolean isStructure(){return structure;}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
	}
}




/*if((entityphrase.split("\\s").length>=2)&&(elocatorphrase=="")){
	//try out the variations
	SynRingVariation entityvariation = new SynRingVariation(entityphrase);
	SynRingVariation elocatorvariation = null;
	if(elocatorphrase==null || elocatorphrase.length()==0){
		//elocatorvariation =  new SynRingVariation(elocatorphrase);
	}

	if(elocatorvariation == null){ //try entityvariation alone
		String spatial = entityvariation.getLeadSpaticalTermVariation(); //TODO
		String head = entityvariation.getHeadNounVariation(); //TODO remove duplicates

		// the below code passes all the spatial and entity variations to termsearcher and get all the matching entities.
		ArrayList<FormalConcept> matches = TermSearcher.entityvariationtermsearch(spatial,head);
		if(matches.size()>0)
		{
			EntityProposals entities = new EntityProposals();
			for(int i =0; i <matches.size(); i++){
				entities.add((Entity)matches.get(i));					
			}
			return entities;
		}
	}
}*/
