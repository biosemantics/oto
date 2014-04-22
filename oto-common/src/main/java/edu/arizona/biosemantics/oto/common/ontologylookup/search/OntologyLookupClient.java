package edu.arizona.biosemantics.oto.common.ontologylookup.search;

import java.util.ArrayList;
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
	private String ontologyURL;
	private String ontologyLocalPath;
	public TermOutputerUtilities ontoutil;
	public String entityonto;
	public String eonto;
	public String bspo;
	public String pato;
	public static String dictdir;
	public Hashtable<String, String> ontoURLs = new Hashtable<String, String>();
	

	public OntologyLookupClient(String ontologyName, String ontologyDir,
			String dictDir) {
		entityonto = ontologyName;
		eonto = ontologyDir + "/" + ontologyName + ".owl";
		this.ontologyLocalPath = ontologyDir + "/" + ontologyName + ".owl";
		bspo = ontologyDir + "/bspo.owl";
		pato = ontologyDir + "/pato.owl";
		dictdir = dictDir;

		if (ontologyName.compareToIgnoreCase("po") == 0) {
			this.ontologyURL = "http://purl.obolibrary.org/obo/po.owl";
			ontoURLs.put(eonto,
					"http://purl.obolibrary.org/obo/po.owl");
		} else if (ontologyName.compareToIgnoreCase("hao") == 0) {
			this.ontologyURL = "http://purl.obolibrary.org/obo/hao.owl";
			ontoURLs.put(eonto,
					"http://purl.obolibrary.org/obo/hao.owl");
		} else if (ontologyName.compareToIgnoreCase("poro") == 0) {
			this.ontologyURL = "http://purl.obolibrary.org/obo/poro.owl";
			ontoURLs.put(eonto,
					"http://purl.obolibrary.org/obo/poro.owl");
		} else if (ontologyName.compareToIgnoreCase("ext") == 0) {
			this.ontologyURL = "purl.obolibrary.org/obo/uberon/ext.owl";
			ontoURLs.put(eonto,
					"purl.obolibrary.org/obo/uberon/ext.owl");
		}
		ontoURLs.put(bspo,
				"http://purl.obolibrary.org/obo/bspo.owl");
		ontoURLs.put(pato,
				"http://purl.obolibrary.org/obo/pato.owl");

		// now load ontologies
		ontoutil = new TermOutputerUtilities(eonto, bspo, pato, ontoURLs);
	}

	public ArrayList<FormalConcept> searchCharacter(String term) {
		TermSearcher ts = new TermSearcher(this);
		return ts.searchTerm(term, "quality");
	}

	public ArrayList<EntityProposals> searchStrucutre(String term) {
		EntitySearcherOriginal eso = new EntitySearcherOriginal(this);
		return eso.searchEntity(term, "", term + "+" + "", rel);
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
		// System.out.println("parent organ in order: ");
		// for (SimpleEntity e : chain) {
		// System.out.println(e.getLabel());
		// System.out.println(e.getClassIRI());
		// System.out.println(e.getDef());
		// }

		return chain;
	}

	public static void main(String[] args) {
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
				System.out.println(term + ": ");
				System.out.println("\tClassIRI: " + fc.getClassIRI());
				System.out.println("\tId: " + fc.getId());
				System.out.println("\tLabel: " + fc.getLabel());
				System.out.println("\tSearchString: " + fc.getSearchString());
				System.out.println("\tString: " + fc.getString());
				System.out.println("\tDef: " + fc.getDef());
				System.out.println("\tParent label: " + fc.getPLabel());
			}

		}

		term = "condyle of femur";
		ArrayList<EntityProposals> eps = client2.searchStrucutre(term);
		if (eps != null) {
			for (EntityProposals ep : eps) {
				for (Entity e : ep.getProposals()) {
					System.out.println(term + ": ");
					System.out.println("\tClassIRI: " + e.getClassIRI());
					System.out.println("\tId: " + e.getId());
					System.out.println("\tLabel: " + e.getLabel());
					System.out
							.println("\tSearchString: " + e.getSearchString());
					System.out.println("\tString: " + e.getString());
					System.out.println("\tDef: " + e.getDef());
					System.out.println("\tParent label: " + e.getPLabel());
				}
			}
		}
	}
}
