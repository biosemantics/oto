/**
 * 
 */
package edu.arizona.biosemantics.oto.steps.shared.beans.toontologies;


import java.io.Serializable;


/**
 * @author Hong Cui
 * Information about an ontology
 *
 */
public class OntologyInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5071233259292738775L;
	private String ontologyFileName;
	private String ontologyPrefix;
	private String type; //local or external
	private String taxonGroup;
	/**
	 * 
	 */
	public OntologyInfo(){
		//need to be here
	}
	
	public OntologyInfo(String fileName, String prefix, String type, String taxonGroup) {
		this.ontologyFileName = fileName;
		this.ontologyPrefix = prefix;
		this.type = type;
		this.taxonGroup = taxonGroup;
	}

	public String getOntologyFileName(){
		return this.ontologyFileName;
	}
	
	
	public void setOntologyFileName(String ontologyFileName){
		this.ontologyFileName = ontologyFileName;
	}
	
	public String getOntologyPrefix(){
		return this.ontologyPrefix;
	}
	
	public void setOntologyPrefix(String ontologyPrefix){
		this.ontologyPrefix = ontologyPrefix;
	}
	
	public String getOntologyType(){
		return type;
	}
	
	public void setOntologyType(String type){
		this.type = type;
	}

	public String getTaxonGroup() {
		return this.taxonGroup;
	}
	
	public void setTaxonGroup(String taxonGroup) {
		this.taxonGroup = taxonGroup;
	}

}
