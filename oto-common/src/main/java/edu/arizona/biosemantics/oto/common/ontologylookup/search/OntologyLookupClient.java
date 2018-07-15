package edu.arizona.biosemantics.oto.common.ontologylookup.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.Entity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.EntityProposals;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.FormalConcept;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.SimpleEntity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.knowledge.TermOutputerUtilities;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.search.EntitySearcherOriginal;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.search.SearchPartOfChain;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.search.TermSearcher;

public class OntologyLookupClient {
	private String rel = "part_of";

	// the following variables are for search parent chain
	@Deprecated
	private String ontologyURL;
	@Deprecated
	private String ontologyLocalPath;
	public String[] entityOntologyFilepaths = new String[]{};
	public String[] qualityOntologyFilepaths = new String[]{};
	public HashSet<String> entityOntologyNames = new HashSet<String>();
	public HashSet<String> qualityOntologyNames = new HashSet<String>();
	public TermOutputerUtilities ontoutil;
	@Deprecated
	private String entityonto;
	@Deprecated
	private String eonto;
	@Deprecated
	private String bspo;
	@Deprecated
	private String pato;
	@Deprecated
	private String ro;
	@Deprecated
	private String spd;
	public static String dictdir;
	public Hashtable<String, String> ontoURLs = new Hashtable<String, String>();
	
    /**
     * @deprecated use the other constructor instead.
     * @param entityOntologyName
     * @param ontologyDir
     * @param dictDir
     */
	@Deprecated
	public OntologyLookupClient(String entityOntologyName, String ontologyDir,
			String dictDir) {
		
		entityonto = entityOntologyName;
		entityOntologyNames.add(entityOntologyName);
		entityOntologyNames.add("bspo");
		qualityOntologyNames.add("ro");
		qualityOntologyNames.add("pato");
		eonto = ontologyDir + "/" + entityOntologyName + ".owl";
		this.ontologyLocalPath = ontologyDir + "/" + entityOntologyName + ".owl";
		bspo = ontologyDir + "/bspo.owl";
		pato = ontologyDir + "/pato.owl";
		ro = ontologyDir + "/ro.owl";
		spd = ontologyDir + "/spd.owl";
		dictdir = dictDir;

		if (entityOntologyName.compareToIgnoreCase("po") == 0) {
			this.ontologyURL = "http://purl.obolibrary.org/obo/po.owl";
			ontoURLs.put(eonto,
					"http://purl.obolibrary.org/obo/po.owl");
		} else if (entityOntologyName.compareToIgnoreCase("hao") == 0) {
			this.ontologyURL = "http://purl.obolibrary.org/obo/hao.owl";
			ontoURLs.put(eonto,
					"http://purl.obolibrary.org/obo/hao.owl");
		} else if (entityOntologyName.compareToIgnoreCase("poro") == 0) {
			this.ontologyURL = "http://purl.obolibrary.org/obo/poro.owl";
			ontoURLs.put(eonto,
					"http://purl.obolibrary.org/obo/poro.owl");
		} else if (entityOntologyName.compareToIgnoreCase("ext") == 0) {
			this.ontologyURL = "purl.obolibrary.org/obo/uberon/ext.owl";
			ontoURLs.put(eonto,
					"purl.obolibrary.org/obo/uberon/ext.owl");
		} else if(entityOntologyName.compareToIgnoreCase("spd") == 0) {
			this.ontologyURL = "http://purl.obolibrary.org/obo/spd.owl";
			ontoURLs.put(spd,
					"http://purl.obolibrary.org/obo/spd.owl");
		}
		ontoURLs.put(bspo,
				"http://purl.obolibrary.org/obo/bspo.owl");
		ontoURLs.put(pato,
				"http://purl.obolibrary.org/obo/pato.owl");
		ontoURLs.put(ro, 
				"http://purl.obolibrary.org/obo/ro.owl");

		// now load ontologies
		ontoutil = new TermOutputerUtilities(eonto, bspo, pato, ro, ontoURLs);
	}
	
	/**
	 * 
	 * @param entityOntologyNames ontology filename without ".owl". ontology files should be named with their obo acronym, plant ontology = po
	 * @param qualityOntologyNames
	 * @param ontologyDir
	 * @param dictDir
	 */
	public OntologyLookupClient(HashSet<String> entityOntologyNames, HashSet<String> qualityOntologyNames, String ontologyDir,
			String dictDir) {

		dictdir = dictDir;
		int en = entityOntologyNames.size();
		int qn = qualityOntologyNames.size();
		int i = 0;
		//get local filepaths: all ontologies must be in the file systems
		for(String entityonto: entityOntologyNames){		
			this.entityOntologyFilepaths[i++] = ontologyDir + "/" + entityonto + ".owl";
		}
		i = 0;
		for(String qualityonto: qualityOntologyNames){		
			this.qualityOntologyFilepaths[i++] = ontologyDir + "/" + qualityonto + ".owl";
		}
		
		//get URL, some URL may not work, and the system will use the local file
		i = 0;
		for(String entityonto: entityOntologyNames){		
			ontoURLs.put(entityonto,
					"http://purl.obolibrary.org/obo/"+entityonto+".owl");
		}
		i = 0;
		for(String qualityonto: qualityOntologyNames){		
			ontoURLs.put(qualityonto,
					"http://purl.obolibrary.org/obo/"+qualityonto+".owl");
		}
		// now load ontologies
		ontoutil = new TermOutputerUtilities(entityOntologyFilepaths, qualityOntologyFilepaths, ontoURLs);
	}
	

	public ArrayList<FormalConcept> searchCharacter(String term) {
		TermSearcher ts = new TermSearcher(this);
		return ts.searchTerm(term, "quality", 1.0f);
	}

	/*public ArrayList<EntityProposals> searchStructure(String term) {
		EntitySearcherOriginal eso = new EntitySearcherOriginal(this);
		return eso.searchEntity(term, "", term + "+" + "", rel);
	}*/
	
	public ArrayList<EntityProposals> searchStructure(String term, String locator, String rel) {
		EntitySearcherOriginal eso = new EntitySearcherOriginal(this);
		return eso.searchEntity(term, locator, term + "+" + "", rel, 1.0f);
	}

	/**
	 * 
	 * @param termIRI
	 *            sample: http://purl.obolibrary.org/obo/PO_0009032
	 * @return
	 */
	public ArrayList<SimpleEntity> searchParentChain(String termIRI) {
		SearchPartOfChain spoc = new SearchPartOfChain(this.ontologyURL,
				this.ontologyLocalPath);
		spoc.search(termIRI);
		ArrayList<SimpleEntity> chain = spoc.getParentChain();
		// //System.out.println("parent organ in order: ");
		// for (SimpleEntity e : chain) {
		// //System.out.println(e.getLabel());
		// //System.out.println(e.getClassIRI());
		// //System.out.println(e.getDef());
		// }

		return chain;
	}

	public static void main(String[] args) {
		

			HashSet<String> entityOntologies = new HashSet<String>();
			entityOntologies.add("PO");
			HashSet<String> qualityOntologies = new HashSet<String>();
			OntologyLookupClient client = new OntologyLookupClient(entityOntologies, qualityOntologies, "ontologies", "wordnet/wn31/dict");
			client.searchStructure("leaf", "", "");
		}
		
		
		/*//try search: scattered broad-based lanceolate - lance-subulate prickle
		OntologyLookupClient client = new OntologyLookupClient("po",
				"/home/sbs0457/workspace/OTOLiteForETC/OntologyOwlFiles",
				"/home/sbs0457/workspace/OTOLiteForETC/DictFiles");
		client.searchParentChain("http://purl.obolibrary.org/obo/PO_0009032");

		OntologyLookupClient client2 = new OntologyLookupClient("ext",
				"/home/sbs0457/workspace/OTOLiteForETC/OntologyOwlFiles",
				"/home/sbs0457/workspace/OTOLiteForETC/DictFiles");
		String term = "round";
		ArrayList<FormalConcept> fcs = client2.searchCharacter(term);
		if (fcs != null) {
			for (FormalConcept fc : fcs) {
				//System.out.println(term + ": ");
				//System.out.println("\tClassIRI: " + fc.getClassIRI());
				//System.out.println("\tId: " + fc.getId());
				//System.out.println("\tLabel: " + fc.getLabel());
				//System.out.println("\tSearchString: " + fc.getSearchString());
				//System.out.println("\tString: " + fc.getString());
				//System.out.println("\tDef: " + fc.getDef());
				//System.out.println("\tParent label: " + fc.getPLabel());
			}

		}

		term = "condyle of femur";
		ArrayList<EntityProposals> eps = client2.searchStructure(term, "", "");
		if (eps != null) {
			for (EntityProposals ep : eps) {
				for (Entity e : ep.getProposals()) {
					//System.out.println(term + ": ");
					//System.out.println("\tClassIRI: " + e.getClassIRI());
					//System.out.println("\tId: " + e.getId());
					//System.out.println("\tLabel: " + e.getLabel());
					System.out
							.println("\tSearchString: " + e.getSearchString());
					//System.out.println("\tString: " + e.getString());
					//System.out.println("\tDef: " + e.getDef());
					//System.out.println("\tParent label: " + e.getPLabel());
				}
			}
		}*/
	
}
