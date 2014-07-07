package edu.arizona.biosemantics.oto.common.model.lite;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Upload {

	private List<Term> possibleStructures = new LinkedList<Term>();
	private List<Term> possibleCharacters = new LinkedList<Term>();
	private List<Term> possibleOtherTerms = new LinkedList<Term>();
	private List<Sentence> sentences = new LinkedList<Sentence>();
	private String glossaryType;
	private String source;
	private String user;
	private String bioportalUserId;
	private String bioportalAPIKey;
	
	public Upload() {
		
	}

	public Upload(List<Term> possibleStructures, List<Term> possibleCharacters,
			List<Term> possibleOtherTerms, List<Sentence> sentences,
			String glossaryType, String source, String user, String bioportalUserId, String bioportalAPIKey) {
		this.possibleStructures = possibleStructures;
		this.possibleCharacters = possibleCharacters;
		this.possibleOtherTerms = possibleOtherTerms;
		this.sentences = sentences;
		this.glossaryType = glossaryType;
		this.source = source;
		this.user = user;
		this.bioportalUserId = bioportalUserId;
		this.bioportalAPIKey = bioportalAPIKey;
	}

	public List<Term> getPossibleStructures() {
		return possibleStructures;
	}

	public void setPossibleStructures(List<Term> possibleStructures) {
		this.possibleStructures = possibleStructures;
	}

	public List<Term> getPossibleCharacters() {
		return possibleCharacters;
	}

	public void setPossibleCharacters(List<Term> possibleCharacters) {
		this.possibleCharacters = possibleCharacters;
	}

	public List<Term> getPossibleOtherTerms() {
		return possibleOtherTerms;
	}

	public void setPossibleOtherTerms(List<Term> possibleOtherTerms) {
		this.possibleOtherTerms = possibleOtherTerms;
	}

	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(List<Sentence> sentences) {
		this.sentences = sentences;
	}

	public String getGlossaryType() {
		return glossaryType;
	}

	public void setGlossaryType(String glossaryType) {
		this.glossaryType = glossaryType;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getBioportalUserId() {
		return bioportalUserId;
	}

	public void setBioportalUserId(String bioportalUserId) {
		this.bioportalUserId = bioportalUserId;
	}

	public String getBioportalAPIKey() {
		return bioportalAPIKey;
	}

	public void setBioportalAPIKey(String bioportalAPIKey) {
		this.bioportalAPIKey = bioportalAPIKey;
	}
	
}
