package edu.arizona.biosemantics.oto.oto.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import edu.arizona.biosemantics.oto.oto.beans.SimpleOrderBean;

/**
 * this class is to parse csv files for importing
 * 
 * @author Huang
 * 
 */
public class CsvParser {
	private InputStream fileStream;
	private BufferedReader br = null;
	private String line = "";
	private String splitBy = ",";
	private ArrayList<String> termList;
	private ArrayList<SimpleOrderBean> orderList;
	private ArrayList<String> sentences;

	public CsvParser(InputStream fileStream, String splitBy) {
		this.fileStream = fileStream;
		this.splitBy = splitBy;
	}

	public CsvParser(InputStream fileStream) {
		this.fileStream = fileStream;
	}

	/**
	 * parse sentence for each line
	 * 
	 * format of line: 1st column is term, second column is sentence
	 * 
	 * @param term
	 * @param line
	 * @return
	 */
	private void parseSentence(String term, String line) {
		// remove term from line
		String sentence = line.substring(term.length() + 1, line.length());
		if (sentence.startsWith("\"")) {// sentence has comma inside
			sentence = sentence.substring(1, sentence.length());// remove first
																// "
			if (sentence.indexOf("\"") > 0) {
				sentence = sentence.substring(0, sentence.indexOf("\""));
			}
		} else {// sentence has no comma inside
			sentence = sentence.trim().replaceAll(",", "");
		}

		if (!sentence.equals("")) {
			sentences.add(sentence);
		}
	}

	/**
	 * for the categorization page
	 * 
	 * @throws IOException
	 */
	private void parseTermList() throws IOException {
		termList = new ArrayList<String>();
		sentences = new ArrayList<String>();

		br = new BufferedReader(new InputStreamReader(fileStream));
		while ((line = br.readLine()) != null) {
//			byte[] utf8Bytes = line.getBytes("UTF-8");
//			String test = new String(utf8Bytes, "UTF-8");
			String[] terms = line.split(splitBy);
			if (terms.length > 0) {
				// get term
				String term = terms[0].trim().toLowerCase()
						.replaceAll("\"", "");
				if (!term.equals("")) {
					termList.add(term);
				}

				// get sentence
				if (terms.length > 1) {
					parseSentence(terms[0], line);
				}
			}
		}

		removeDuplicates(termList);
		removeDuplicates(sentences);
	}

	/**
	 * for the hierarchy page: exclude 7 default structures
	 * 
	 * @throws IOException
	 */
	private void parseStructureList() throws IOException {
		termList = new ArrayList<String>();
		sentences = new ArrayList<String>();

		// use map to remove duplicates
		HashMap<String, String> map = new HashMap<String, String>();

		// get structures from the input
		br = new BufferedReader(new InputStreamReader(fileStream));
		while ((line = br.readLine()) != null) {
			String[] terms = line.split(splitBy);
			if (terms.length > 0) {
				String term = terms[0].trim().toLowerCase()
						.replaceAll("\"", "");
				if (!term.equals("")) {
					map.put(term, "");
				}

				// get sentence
				if (terms.length > 1) {
					parseSentence(terms[0], line);
				}
			}
		}

		// remove the 7 default structures
		map.remove("plant");
		map.remove("root");
		map.remove("stem");
		map.remove("leaf");
		map.remove("fruit");
		map.remove("seed");
		map.remove("flower");

		for (String key : map.keySet()) {
			termList.add(key);
		}

		removeDuplicates(sentences);
	}

	/**
	 * for the order page
	 * 
	 * @throws IOException
	 */
	private void parseOrderList() throws IOException {
		orderList = new ArrayList<SimpleOrderBean>();
		br = new BufferedReader(new InputStreamReader(fileStream));
		while ((line = br.readLine()) != null) {
			String[] words = line.split(splitBy);
			if (words.length > 0) {
				/*
				 * read in the order group name, order group name can be
				 * duplicates
				 */
				SimpleOrderBean order = new SimpleOrderBean(words[0].trim()
						.replaceAll("\"", ""));
				if (words.length > 1) {
					// read in the terms in this order group
					ArrayList<String> terms = new ArrayList<String>();

					for (int i = 1; i < words.length; i++) {
						String term = words[i].trim().toLowerCase()
								.replaceAll("\"", "");
						if (!term.equals("")) {
							terms.add(term);
						}
					}
					removeDuplicates(terms);
					order.setTerms(terms);
				}
				orderList.add(order);
			}
		}
	}

	public ArrayList<String> getTermList() throws IOException {
		parseTermList();
		return termList;
	}

	public ArrayList<String> getStructureList() throws IOException {
		parseStructureList();
		return termList;
	}

	public ArrayList<SimpleOrderBean> getOrderList() throws IOException {
		parseOrderList();
		return orderList;
	}

	public ArrayList<String> getSentences() {
		return sentences;
	}

	/**
	 * Remove duplicate words from a list
	 * 
	 * @param words
	 * @return
	 */
	private void removeDuplicates(ArrayList<String> words) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (int i = words.size() - 1; i >= 0; i--) {
			String word = words.get(i);
			if (map.get(word) == null) {
				map.put(word, 1);
			} else {
				// duplicate, remove it from list
				words.remove(i);
			}
		}
	}
}
