/**
 * 
 */
package edu.arizona.biosemantics.oto.common.ontologylookup.search.search;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import edu.arizona.biosemantics.oto.common.ontologylookup.search.OntologyLookupClient;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.knowledge.Dictionary;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.knowledge.TermOutputerUtilities;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.owlaccessor.OWLAccessorImpl;

/**
 * @author Hong Cui
 * This is a utility class with static methods that 
 * construct regex patterns of alternatives for an input word (spatial or structure)
 * 
 * Examples:
 *
 * input: postaxial
 * output:
 * synring: postaxial|syn_ring_from_dictionary|syn_from_onto
 * 
 * 
 * input: nose
 * output:
 * synring: nose|nasal|syn_ring_from_dictionary|syn_from_onto|relational_adj_from_onto
 */
public class SynRingVariation {
	//private String leadspatialtermvariation="";
	//private String headnounvariation="";
	private static final Logger LOGGER = Logger.getLogger(SynRingVariation.class);   
	private static Hashtable<String, String> cache = new Hashtable<String, String>();
	private static boolean useCache = true;
	/**
	 * 
	 * @param phrase
	 */
	/*public SynRingVariation(String phrase) {
		//TODO create variation
		getSynRing4Spatial(phrase);
		extractheadnounvariation(phrase);
		this.leadspatialtermvariation=this.leadspatialtermvariation!=""?this.leadspatialtermvariation.substring(1):"";
		this.headnounvariation = this.headnounvariation!=""?this.headnounvariation.substring(1):"";
		
	}*/
	public SynRingVariation(boolean useCache) {
		this.useCache = useCache;
	}
	
	
	/**
	 * 
	 * @param searchtype 
	 * @param structure: one word representing a structure
	 * @return
	 */
	//TODO check duplicates: (?:(?:opening|foramina|foramen|foramens|perforation|orifice|opening|foramina|bone foramen|foramen|foramens|bone foramen|perforation|orifice|orifice))
	public static String getSynRing4Word(String word, OntologyLookupClient OLC, String searchtype) {
		if(word.length()==0) return "";
		String synring = null;
		if(useCache){
			synring = cache.get(word);
			if(synring!=null) return synring;
		}
		synring = word;
		OWLAccessorImpl owlapi=null;
		ArrayList<String> ontosynonyms;
		//grab a synring from Dictionary
		Enumeration<String> ptn = Dictionary.synrings.keys();
		while(ptn.hasMoreElements()){
			//if(structure.matches("\\b("+Dictionary.process+")\\b"))
			//	synring = "anatomical projection";
			//if(structure.matches("\\b("+Dictionary.opening+")\\b"))
			//	synring = Dictionary.opening;
			String syn = ptn.nextElement();
			if(word.matches("\\b("+syn+")\\b"))
				synring = Dictionary.synrings.get(syn);
		}
		
		//find owlapi
		if(searchtype.equals("entity")){
			for(OWLAccessorImpl temp:OLC.ontoutil.OWLentityOntoAPIs){
				for(String entityOntoName: OLC.entityOntologyNames){
					if(temp.getSource().indexOf(entityOntoName)>1){
						owlapi=temp;
						break;
					}
				}
			}
		}
		
		if(searchtype.equals("quality")){
			for(OWLAccessorImpl temp:OLC.ontoutil.OWLqualityOntoAPIs){
				for(String entityOntoName: OLC.qualityOntologyNames){
					if(temp.getSource().indexOf(entityOntoName)>1){
						owlapi=temp;
						break;
					}
				}
			}
		}
		
		//expanding the synring with synonyms
		for(String form:synring.split("\\|"))
		{
			if(!form.matches("\\b("+synring+")\\b")) synring+="|"+form; //don't add duplicates
			if(owlapi!=null){
				ontosynonyms = owlapi.getSynonymLabelsbyPhrase(form,"entity");
				for(String syn:ontosynonyms)
					if(!form.matches("\\b("+synring+")\\b")) synring+="|"+syn;
			}
		}
		
		//fetch adjective organs
		ArrayList<String> adjectives = new ArrayList<String> ();
		if(OWLAccessorImpl.organadjectives.get(word)!=null) adjectives.addAll(OWLAccessorImpl.organadjectives.get(word));
		if(OWLAccessorImpl.adjectiveorgans.get(word)!=null) adjectives.addAll(OWLAccessorImpl.adjectiveorgans.get(word).values());
		if(Dictionary.organadjectives.get(word)!=null) adjectives.addAll(Dictionary.organadjectives.get(word)); //those not covered in ontologies
		if(Dictionary.adjectiveorgans.get(word)!=null) adjectives.addAll(Dictionary.adjectiveorgans.get(word));
		
		for(String adjectiveform: adjectives){
			if(!adjectiveform.matches("\\b("+synring+")\\b")) synring+="|"+adjectiveform;
		}
		if(useCache)
			cache.put(word, synring);
		return synring;
	}

	/**
	 * Gets all the synonyms of the spatial term and forms a string like "anterior|syn1|syn2"
	 * @param spatial: one word spatial term such as 'anterior'
	 * @return: a string of synonym ring like "anterior|syn1|syn2"
	 */
	public static String getSynRing4Spatial(String spatial, OntologyLookupClient OLC) {
		if(spatial.length()==0) return "";
		String synring;
		if(useCache){
			synring = cache.get(spatial);
			if(synring!=null) return synring;
		}
		//String forms = prefixSpatial(spatial);
		OWLAccessorImpl owlapi=null;

		for(OWLAccessorImpl temp:OLC.ontoutil.OWLentityOntoAPIs){
			if(temp.getSource().indexOf("bspo")>=1){
				owlapi=temp;
				break;
			}
			
		}
		
		if(owlapi == null){
			if(useCache) cache.put(spatial, spatial);
			return spatial;
		}
	
		synring = spatial;
		ArrayList<String> ontosynonyms = owlapi.getSynonymLabelsbyPhrase(spatial,"spatial");
		for(String syn:ontosynonyms){
			if(!synring.matches("\\b("+syn+")\\b"))
				synring +="|"+syn;
		}
		
		if(useCache) cache.put(spatial, synring);
		return synring;
		
	}

	

	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	//	getsynonym("anatomical section");
	/*	SynRingVariation sv= new SynRingVariation("postaxial process");
		//System.out.println("sv.leadspatialtermvariation = "+sv.leadspatialtermvariation);
		//System.out.println("sv.headnounvariation ="+sv.headnounvariation);
	*/
	////System.out.println(SynRingVariation.getSynRing4Spatial("basal"));
//	//System.out.println(SynRingVariation.getSynRing4Structure("radial"));
		
	}

}
