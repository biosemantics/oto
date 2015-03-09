package edu.arizona.biosemantics.oto.common.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import edu.arizona.biosemantics.common.biology.TaxonGroup;

@XmlRootElement
public class CreateDataset implements Serializable {

	private String name;
	private TaxonGroup taxonGroup;
	private Authentication Authentication;

	public CreateDataset() { }
	
	public CreateDataset(String name, TaxonGroup taxonGroup, Authentication authentication) {
		this.name = name;
		this.taxonGroup = taxonGroup;
		this.Authentication = authentication;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TaxonGroup getTaxonGroup() {
		return taxonGroup;
	}

	public void setTaxonGroup(TaxonGroup taxonGroup) {
		this.taxonGroup = taxonGroup;
	}

	public Authentication getAuthentication() {
		return Authentication;
	}

	public void setAuthentication(Authentication authentication) {
		Authentication = authentication;
	}
	
	
}
