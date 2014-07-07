package edu.arizona.biosemantics.oto.common.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Category {
	
	private String category;
	private String definition;
	
	public Category() {
		
	}
	
	public Category(String category, String definition) {
		super();
		this.category = category;
		this.definition = definition;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	
	public String toString() {
		return "category: "  + this.category + "\ndefinition: " + this.definition;
	}
}
