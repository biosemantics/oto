package edu.arizona.biosemantics.oto.oto.beans;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import edu.arizona.biosemantics.bioportal.model.ProvisionalClass;


public class ProvisionalEntry {
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private String localId;
	private String termType;
	private String termCategory;
	private String source;
	private int glossaryType;
	private String dataset;
	private ProvisionalClass provisionalClass;
	
	public ProvisionalEntry(String localId, String termType,
			String termCategory, String source, int glossaryType,
			String dataset, ProvisionalClass provisionalClass) {
		super();
		this.localId = localId;
		this.termType = termType;
		this.termCategory = termCategory;
		this.source = source;
		this.glossaryType = glossaryType;
		this.dataset = dataset;
		this.provisionalClass = provisionalClass;
	}

	public ProvisionalEntry(String localId, String termType, String termCategory, String source, int glossaryType) {
		super();
		this.localId = localId;
		this.termType = termType;
		this.termCategory = termCategory;
		this.source = source;
		this.glossaryType = glossaryType;
	}

	public boolean hasLocalId() {
		return this.localId != null && !this.localId.isEmpty();
	}
	
	public String getLocalId() {
		return localId;
	}

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public boolean hasTermType() {
		return this.termType != null && !this.termType.isEmpty();
	}
	
	public String getTermType() {
		return termType;
	}

	public void setTermType(String termType) {
		this.termType = termType;
	}

	public boolean hasTermCategory() {
		return this.termCategory != null && !this.termCategory.isEmpty();
	}
	
	public String getTermCategory() {
		return termCategory;
	}

	public void setTermCategory(String termCategory) {
		this.termCategory = termCategory;
	}

	public boolean hasSource() {
		return this.source != null && !this.source.isEmpty();
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getGlossaryType() {
		return glossaryType;
	}

	public void setGlossaryType(int glossaryType) {
		this.glossaryType = glossaryType;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public ProvisionalClass getProvisionalClass() {
		return provisionalClass;
	}

	public void setProvisionalClass(ProvisionalClass provisionalClass) {
		this.provisionalClass = provisionalClass;
	}
		
	public String toString() {
		try {
			return objectMapper.writeValueAsString(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getSynonymsString() {
		String result = "";
		for(String synonym : getProvisionalClass().getSynonym()) {
			result += synonym + ",";
		}
		if(result.isEmpty())
			return result;
		return result.substring(0, result.length() - 1);
	}
	
	public String getOntologiesString() {
		String result = "";
		for(String ontology : getProvisionalClass().getOntology()) {
			result += ontology + ",";
		}
		if(result.isEmpty())
			return result;
		return result.substring(0, result.length() - 1);
	}

	public String getDefinitionsString() {
		String result = "";
		for(String definition : getProvisionalClass().getDefinition()) {
			result += definition + ";";
		}
		if(result.isEmpty())
			return result;
		return result.substring(0, result.length() - 1);
	}

	public List<String> getSynonymsFromString(String synonyms) {
		List<String> result = new LinkedList<String>();
		for(String synonym : synonyms.split(","))
			result.add(synonym);
		return result;
	}
	
	public void setSynonymsFromString(String synonyms) {
		this.provisionalClass.setSynonym(this.getSynonymsFromString(synonyms));
	}
	
	public List<String> getDefinitionsFromString(String definitions) {
		List<String> result = new LinkedList<String>();
		for(String definition : definitions.split(";"))
			result.add(definition);
		return result;
	}
	
	public void setDefinitionsFromString(String definitions) {
		this.provisionalClass.setDefinition(this.getDefinitionsFromString(definitions));
	}
	
	public List<String> getOntologiesFromString(String ontologies) {
		List<String> result = new LinkedList<String>();
		for(String ontology : ontologies.split(";"))
			result.add(ontology);
		return result;
	}
	
	public void setOntologiesFromString(String ontologies) {
		this.provisionalClass.setDefinition(this.getOntologiesFromString(ontologies));
	}
}
