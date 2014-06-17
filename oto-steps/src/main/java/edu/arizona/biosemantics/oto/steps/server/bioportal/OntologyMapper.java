package edu.arizona.biosemantics.oto.steps.server.bioportal;

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
		ontologiesToIds.put("PO", "1587");
		ontologiesToIds.put("PATO", "1107");
		ontologiesToIds.put("HAO", "1362");
		ontologiesToIds.put("PORO", "49868");
		
		idsToOntologies.put("1587", "PO");
		idsToOntologies.put("1107", "PATO");
		idsToOntologies.put("1362", "HAO");	
		idsToOntologies.put("49868", "PORO");
		
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
