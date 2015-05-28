package edu.arizona.biosemantics.oto.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import edu.arizona.biosemantics.common.biology.TaxonGroup;

@XmlRootElement
public class CreateDataset implements Serializable {

	private String name;
	private TaxonGroup taxonGroup;
	private String authenticationToken;

	public CreateDataset() { }
	
	public CreateDataset(String name, TaxonGroup taxonGroup, String authenticationToken) {
		this.name = name;
		this.taxonGroup = taxonGroup;
		this.authenticationToken = authenticationToken;
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

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	public void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}
	
	
}
