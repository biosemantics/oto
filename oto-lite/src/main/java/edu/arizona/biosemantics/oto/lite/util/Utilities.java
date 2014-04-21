package edu.arizona.biosemantics.oto.lite.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.arizona.biosemantics.oto.lite.beans.Category;
import edu.arizona.biosemantics.oto.lite.beans.DecisionHolder;
import edu.arizona.biosemantics.oto.lite.beans.Term;

/**
 * This is an Utility class
 * 
 * @author Partha
 * 
 */
public class Utilities {
	private static final Logger LOGGER = Logger.getLogger(Utilities.class);
	private static Utilities instance;

	public static Utilities getInstance() {
		if (instance == null) {
			instance = new Utilities();
		}
		return instance;
	}
	
	private Utilities() {
		
	}
	
	/**
	 * The use of this should be reconsidered: What if we have a new glossary type, do we want to change the code to accomodate this?
	 * @param glossaryType
	 * @return
	 */
	public static String getGlossaryNameByID(int glossaryType) {
        switch (glossaryType) {
        case 1:
                return "Plant";
        case 2:
                return "Hymenoptera";
        case 3:
                return "Algea";
        case 4:
                return "Porifera";
        case 5:
                return "Fossil";
        default:
                return "Plant";
        }
	}
	
	/**
	 * The use of this should be reconsidered: What if we have a new glossary type, do we want to change the code to accomodate this?
	 * @param glossaryType
	 * @return
	 */
	public static int getGlossaryIDByName(String glossaryName) {
        switch (glossaryName) {
        case "Plant":
                return 1;
        case "Hymenoptera":
                return 2;
        case "Algea":
                return 3;
        case "Porifera":
                return 4;
        case "Fossil":
                return 5;
        default:
                return 1;
        }
	}

	/**
	 * This method gets the file information from the transformed folder
	 */
	public String getFileInfo(String fileName) {
		File file = new File(fileName);
		StringBuilder text = new StringBuilder();
		String NL = System.getProperty("line.separator");
		Scanner scanner = null;

		try {
			scanner = new Scanner(new FileInputStream(file));
			while (scanner.hasNextLine()) {
				text.append(scanner.nextLine() + NL);
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("error reading file ", exe);
		} finally {
			scanner.close();
		}

		return text.toString();
	}

	/**
	 * parse the requestXML of grouping page parse newCategory, reviewedTerms,
	 * regular categories at one time
	 * 
	 * @param requestXML
	 * @return
	 * @throws Exception
	 */
	public DecisionHolder parseGroupingXML(String requestXML) throws Exception {
		DecisionHolder dh = new DecisionHolder();
		ArrayList<Category> new_cats = new ArrayList<Category>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(requestXML));
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();

			// parser uploadID
			NodeList uploadID = doc.getElementsByTagName("uploadID");
			if (uploadID.getLength() > 0) {
				Element e = (Element) uploadID.item(0);
				if (e != null) {
					String uid = e.getFirstChild().getNodeValue();
					dh.setUploadID(Integer.parseInt(uid));
				}
			}

			// parse new categories
			NodeList new_categories = doc.getElementsByTagName("new_category");
			for (int i = 0; i < new_categories.getLength(); i++) {
				Element e = (Element) new_categories.item(i);
				if (e != null) {
					String name = e.getFirstChild().getFirstChild()
							.getNodeValue();
					String def = e.getFirstChild().getNextSibling()
							.getFirstChild().getNodeValue();
					Category new_cat = new Category(name);
					new_cat.setDef(def);
					new_cats.add(new_cat);
				}
			}
			dh.setNew_categories(new_cats);

			// parse regular categories, including uncategorized terms
			ArrayList<Category> categories = new ArrayList<Category>();
			int i = 0, j = 0;
			// categories part
			NodeList categories_list = doc.getElementsByTagName("category");
			if (categories_list.getLength() > 0) {
				for (i = 0; i < categories_list.getLength(); i++) {
					// get the category information
					Element e_category = (Element) categories_list.item(i);
					String category_name = e_category.getFirstChild()
							.getFirstChild().getNodeValue();
					Category category = new Category(category_name);
					ArrayList<Term> changedTermList = new ArrayList<Term>();

					// new decisions part
					Element e_newTerms = (Element) e_category.getFirstChild()
							.getNextSibling();
					if (e_newTerms != null
							&& (e_newTerms.getTagName().equals("new_terms"))) {
						NodeList terms = e_newTerms.getChildNodes();

						for (j = 0; j < terms.getLength(); j++) {
							Element e_term = (Element) terms.item(j);
							Term term = new Term(e_term.getFirstChild()
									.getNodeValue());
							term.setHasSyn(false);
							term.setIsAdditional(false);
							term.setRelatedTerms("");
							changedTermList.add(term);
						}
						category.setChanged_terms(changedTermList);
					}

					// changed decisions part
					Element changedTerms = (Element) e_category.getLastChild();
					if (changedTerms != null
							&& (changedTerms.getTagName()
									.equals("changed_terms"))) {
						NodeList terms = changedTerms
								.getElementsByTagName("term");
						for (j = 0; j < terms.getLength(); j++) {
							Element e_term = (Element) terms.item(j);
							// term name
							Element index_e = (Element) e_term.getFirstChild();
							Term term = new Term(index_e.getFirstChild()
									.getNodeValue());

							// hasSyn
							index_e = (Element) index_e.getNextSibling();
							if (index_e.getFirstChild().getNodeValue()
									.equals("1")) {
								term.setHasSyn(true);
							} else {
								term.setHasSyn(false);
							}

							// isAdditional
							index_e = (Element) index_e.getNextSibling();
							if (index_e.getFirstChild().getNodeValue()
									.equals("1")) {
								term.setIsAdditional(true);
							} else {
								term.setIsAdditional(false);
							}

							// SynList
							if (term.hasSyn() || term.isAdditional()) {
								NodeList nl_syns = e_term
										.getElementsByTagName("syn");
								ArrayList<String> syns = new ArrayList<String>();
								for (i = 0; i < nl_syns.getLength(); i++) {
									Element e_syn = (Element) nl_syns.item(i);
									if (e_syn != null) {
										syns.add(e_syn.getFirstChild()
												.getNodeValue());
									}
								}
								term.setSyns(syns);
							}

							changedTermList.add(term);
						}
						category.setChanged_terms(changedTermList);
					}
					categories.add(category);
				}
			}

			// deleted decisions part
			// <removed_decisions><term></term></removed_decisions>
			NodeList emptyCategories = doc
					.getElementsByTagName("removed_decisions");
			if (emptyCategories.getLength() > 0) {
				Category emptyCat = new Category("");
				Element e_emptyCategory = (Element) emptyCategories.item(0);
				NodeList terms = e_emptyCategory
						.getElementsByTagName("termName");
				ArrayList<Term> removed_terms = new ArrayList<Term>();
				for (j = 0; j < terms.getLength(); j++) {
					Element e_term = (Element) terms.item(j);
					Term term = new Term(e_term.getFirstChild().getNodeValue());
					term.setComment(e_term.getNextSibling().getFirstChild()
							.getNodeValue());
					term.setRelatedTerms("");
					removed_terms.add(term);
				}
				emptyCat.setChanged_terms(removed_terms);
				categories.add(emptyCat);
			}
			dh.setRegular_categories(categories);

		} catch (Exception exe) {
			LOGGER.error("Unable to parse the new category: parseNewCategory",
					exe);
			exe.printStackTrace();
		}
		return dh;
	}

}
