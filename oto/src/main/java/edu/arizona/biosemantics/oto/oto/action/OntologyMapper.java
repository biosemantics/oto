package edu.arizona.biosemantics.oto.oto.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OntologyMapper {

	private Map<String, String> ontologiesToIds = new HashMap<String, String>();
	private Map<String, String> idsToOntologies = new HashMap<String, String>();
	private List<String> ontologies = new ArrayList<String>();
	
	private static OntologyMapper instance;
	
	public static OntologyMapper getInstance() {
		if(instance == null)
			instance = new OntologyMapper();
		return instance;
	}
	
	private OntologyMapper() {
		ontologiesToIds.put("PO", "http://data.bioontology.org/ontologies/PO");
		ontologiesToIds.put("PATO", "http://data.bioontology.org/ontologies/PATO");
		ontologiesToIds.put("HAO", "http://data.bioontology.org/ontologies/HAO");
		ontologiesToIds.put("PORO", "http://data.bioontology.org/ontologies/PORO");
		
		idsToOntologies.put("http://data.bioontology.org/ontologies/PO", "PO");
		idsToOntologies.put("http://data.bioontology.org/ontologies/PATO", "PATO");
		idsToOntologies.put("http://data.bioontology.org/ontologies/HAO", "HAO");	
		idsToOntologies.put("http://data.bioontology.org/ontologies/PORO", "PORO");
		
		ontologies.add("PATO");
		ontologies.add("PO");
		ontologies.add("HAO");
		ontologies.add("PORO");
	}
	
	public String getOntology(String id) {
		return idsToOntologies.get(id);
	}
	
	public String getOntologyId(String ontology) {
		return ontologiesToIds.get(ontology);
	}
	
	public List<String> getOntologies() {
		return ontologies;
	}
}
