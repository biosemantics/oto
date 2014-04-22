/**
 * 
 */
package edu.arizona.biosemantics.oto.common.ontologylookup.search.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;


import org.semanticweb.owlapi.model.OWLClass;


import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.SimpleEntity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.owlaccessor.OWLAccessorImpl;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.utilities.Utilities;

/**
 * @author Hong Cui
 *
 */
public class SearchPartOfChain {
	private ArrayList<SimpleEntity> chain = new ArrayList<SimpleEntity>();
	private static Hashtable<OWLClass, OWLClass> partofcache = new Hashtable<OWLClass, OWLClass>();
	private OWLAccessorImpl api;
	
	/**
	 * 
	 */
	public SearchPartOfChain(String ontologyIRI, String ontoFilePath) {		
		if(Utilities.ping(ontologyIRI, 200)){
			api = new OWLAccessorImpl(ontologyIRI, new ArrayList<String>());
		}else{
			api = new OWLAccessorImpl(new File(ontoFilePath), new ArrayList<String>());
		}
	}

	public void search(String partIRI){
		if(api!=null){
			Set<OWLClass> parents = api.getClassesWithPart(partIRI);
			//take the first class at this time
			if(parents.size()>0){
				OWLClass p = parents.iterator().next();
				SimpleEntity e = new SimpleEntity();
				e.setClassIRI(p.getIRI().toString());
				e.setLabel(api.getLabel(p));
				e.setDef(api.getDefinition(p));
				chain.add(e);
				search(p.getIRI().toString());
			}else{
				return;
			}
			
		}
	}
	
	public ArrayList<SimpleEntity> getParentChain(){
		return chain;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String ontologyiri = "http://purl.obolibrary.org/obo/po.owl";
		String ontofilepath = "C:/Users/updates/CharaParserTest/Ontologies/po.owl";
		SearchPartOfChain spoc = new SearchPartOfChain(ontologyiri, ontofilepath);
		String partIRI = "http://purl.obolibrary.org/obo/PO_0009032"; //petal
		spoc.search(partIRI);
		ArrayList<SimpleEntity> chain = spoc.getParentChain();
		System.out.println("parent organ in order: ");
		for(SimpleEntity e: chain){
			System.out.println(e.getLabel());
			System.out.println(e.getClassIRI());
			System.out.println(e.getDef());
		}
		
		/*parent organ in order: 
corolla
http://purl.obolibrary.org/obo/PO_0009059
A collective phyllome structure (PO:0025023) that is composed of one or more petals (PO:0009032), comprising the inner whorl of non-reproductive floral organs (PO:0025395) and surrounds the androecium (PO:0009061) and the gynoecium (PO:0009062).
perianth
http://purl.obolibrary.org/obo/PO_0009058
A collective phyllome structure (PO:0025023) that includes as parts the corolla (PO:0009059) and/or the calyx (PO:0009060); or one or more tepals (PO:0009033).
flower
http://purl.obolibrary.org/obo/PO_0009046
A determinate reproductive shoot system that has as part at least one carpel or at least one stamen and does not contain any other determinate shoot system as a part.
*/
	}

}
