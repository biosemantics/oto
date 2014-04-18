package edu.arizona.biosemantics.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.arizona.biosemantics.beans.CategoryBean;
import edu.arizona.biosemantics.beans.Character;
import edu.arizona.biosemantics.beans.DecisionBean;
import edu.arizona.biosemantics.beans.DecisionHolder;
import edu.arizona.biosemantics.beans.ManagerDecisionBean;
import edu.arizona.biosemantics.beans.Order;
import edu.arizona.biosemantics.beans.StructureNodeBean;
import edu.arizona.biosemantics.beans.Term;
import edu.arizona.biosemantics.beans.TermRelationBean;
import edu.arizona.biosemantics.beans.User;
import edu.arizona.biosemantics.db.CharacterDBAccess;
import edu.arizona.biosemantics.db.ReportingDBAccess;
import edu.arizona.biosemantics.db.UserDataAccess;

public class Utilities {
	private static Properties properties = null;
	private static final Logger LOGGER = Logger.getLogger(Utilities.class);
	private static FileInputStream fstream = null;

	public Utilities() throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
	}

	/**
	 * This method pulls all the application specific datasets
	 * 
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<String> getDataSets() throws Exception {

		CharacterDBAccess cdba = new CharacterDBAccess();
		ArrayList<String> datasets = null;
		try {
			datasets = cdba.getDataSets();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datasets;
	}

	/**
	 * This method will get All the decisions made by the user
	 * 
	 * @param dataPrefix
	 * @return
	 * @throws Exception
	 */
	public ArrayList<CategoryBean> getDecisions(String dataPrefix)
			throws Exception {
		CharacterDBAccess cdba = new CharacterDBAccess();
		ArrayList<CategoryBean> decisions = null;
		try {
			decisions = cdba.getAllCategory(dataPrefix);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("unable to load decisions", e);
		}
		return decisions;
	}

	/**
	 * delete a file from disk
	 * 
	 * @param fileName
	 *            : absolute path + file name
	 * @return
	 */
	public boolean deleteFile(String fileName) {
		String cmmd = "rm " + fileName;
		ExecCommmand ec = new ExecCommmand();
		ec.execShellCmd(cmmd);
		return true;
	}

	/**
	 * rename a file
	 * 
	 * @param oldFileName
	 *            : absolute path + filename
	 * @param newFileName
	 *            : absolute path + filename
	 * @return
	 */
	public boolean renameFile(String oldFileName, String newFileName) {
		String cmmd = "mv " + oldFileName + " " + newFileName;
		ExecCommmand ec = new ExecCommmand();
		int executeExit = ec.execShellCmd(cmmd);
		if (executeExit == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * insert meta data into file
	 * 
	 * @param file
	 *            : file path
	 * @param metadata
	 *            : a list of meata data lines
	 * @return
	 * @throws IOException
	 */
	public boolean insertMetaDataIntoCsvFile(String file,
			ArrayList<String> metadata) throws IOException {
		boolean rv = false;
		RandomAccessFile f = null;
		try {
			f = new RandomAccessFile(new File(file), "rw");
			f.seek(0); // to the beginning

			for (String eachLine : metadata) {
				f.writeBytes(eachLine);
				f.writeBytes("\r\n");
			}
			f.writeBytes("\r\n");
			f.close();
			rv = true;
		} catch (Exception e) {
			System.out.println("insertMetaDataIntoCsvFile failure: " + e);
		} finally {
			if (f != null) {
				f.close();
			}
		}
		return rv;

	}

	/**
	 * This method gets the file information from the transformed folder
	 */
	public String getFileInfo(String fileName) {
		File file = new File(properties.getProperty("SOURCE_FILE_PATH")
				+ fileName);
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

	public ArrayList<String> getProceccedCategories(User user, String dataset) {
		ArrayList<String> processedCategories = null;
		if (user != null && dataset != null) {
			try {
				processedCategories = new CharacterDBAccess()
						.getProcessedCategories(user, dataset);
			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("unable to get processed categories/decisions",
						exe);
			}
		}
		return processedCategories;
	}

	public ArrayList<String> getProcessedGroups(User user, String dataset) {

		ArrayList<String> processedGroups = null;
		if (user != null && dataset != null) {
			try {
				processedGroups = new CharacterDBAccess().getProcessedGroups(
						user, dataset);
			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("unable to get processed groups", exe);
			}
		}
		return processedGroups;
	}

	/**
	 * This function gets all the groups the user has not applied his decision
	 * to
	 * 
	 * @param dataset
	 * @param userid
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getGroups(String dataset, int userid, boolean flag)
			throws Exception {
		if (dataset != null) {
			CharacterDBAccess cdba = new CharacterDBAccess();
			ArrayList<String> savedGroups = null;
			try {
				savedGroups = cdba.getAllGroups(dataset, userid);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("unable to load savedGroups", e);
			}
			return savedGroups;
		}
		return null;
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
		ArrayList<CategoryBean> new_cats = new ArrayList<CategoryBean>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(requestXML));
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();

			// parse reviewed terms
			ArrayList<String> reviewedTerms = new ArrayList<String>();
			NodeList termNodes = doc.getElementsByTagName("reviewHistory");
			if (termNodes.getLength() > 0) {
				Element e_term = (Element) termNodes.item(0);
				if (e_term != null) {
					String str_reviewedTerms = e_term.getFirstChild()
							.getNodeValue();
					String[] rTerms = str_reviewedTerms.split(";");
					for (String term : rTerms) {
						if (!term.equals("")) {
							reviewedTerms.add(term.trim());
						}
					}
				}
			}
			dh.setReviewed_terms(reviewedTerms);

			// parse new categories
			NodeList new_categories = doc.getElementsByTagName("new_category");
			for (int i = 0; i < new_categories.getLength(); i++) {
				Element e = (Element) new_categories.item(i);
				if (e != null) {
					String name = e.getFirstChild().getFirstChild()
							.getNodeValue();
					String def = e.getFirstChild().getNextSibling()
							.getFirstChild().getNodeValue();
					CategoryBean new_cat = new CategoryBean(name);
					new_cat.setDef(def);
					new_cats.add(new_cat);
				}
			}
			dh.setNew_categories(new_cats);

			// parse regular categories, including uncategorized terms
			ArrayList<CategoryBean> categories = new ArrayList<CategoryBean>();
			int i = 0, j = 0;
			// categories part
			NodeList categories_list = doc.getElementsByTagName("category");
			if (categories_list.getLength() > 0) {
				for (i = 0; i < categories_list.getLength(); i++) {
					// get the category information
					Element e_category = (Element) categories_list.item(i);
					String category_name = e_category.getFirstChild()
							.getFirstChild().getNodeValue();
					CategoryBean category = new CategoryBean(category_name);
					// ArrayList<String> termsList = new ArrayList<String>();
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
							index_e = (Element) index_e.getNextSibling();
							if (term.hasSyn()) {
								// get <syn> elements, construct syns and
								// relatedTerms
								String relatedTerms = "";
								NodeList synList = index_e.getChildNodes();
								ArrayList<Term> syns = new ArrayList<Term>();
								for (int k = 0; k < synList.getLength(); k++) {
									Element syn = (Element) synList.item(k);
									if (syn != null) {
										String synName = syn.getFirstChild()
												.getNodeValue();
										syns.add(new Term(synName));
										if (relatedTerms.equals("")) {
											relatedTerms = "'" + synName + "'";
										} else {
											relatedTerms += ",'" + synName
													+ "'";
										}
									}
								}
								term.setSyns(syns);
								term.setRelatedTerms(relatedTerms);
							} else if (term.isAdditional()) {
								// set relatedTerms
								term.setRelatedTerms(index_e.getFirstChild()
										.getNodeValue());
							} else {
								term.setRelatedTerms("");
							}

							//
							// if (term.hasSyn() || term.isAdditional()) {
							// term.setRelatedTerms(index_e.getFirstChild()
							// .getNodeValue());
							// } else {
							// term.setRelatedTerms("");
							// }
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
				CategoryBean emptyCat = new CategoryBean("");
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

	public ArrayList<CategoryBean> parseNewCategory(String requestXML)
			throws Exception {
		ArrayList<CategoryBean> new_cats = new ArrayList<CategoryBean>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(requestXML));
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();

			NodeList new_categories = doc.getElementsByTagName("new_category");
			for (int i = 0; i < new_categories.getLength(); i++) {
				Element e = (Element) new_categories.item(i);
				if (e != null) {
					String name = e.getFirstChild().getFirstChild()
							.getNodeValue();
					String def = e.getFirstChild().getNextSibling()
							.getFirstChild().getNodeValue();
					CategoryBean new_cat = new CategoryBean(name);
					new_cat.setDef(def);
					new_cats.add(new_cat);
				}
			}

		} catch (Exception exe) {
			LOGGER.error("Unable to parse the new category: parseNewCategory",
					exe);
			exe.printStackTrace();
		}
		return new_cats;
	}

	/**
	 * This method will parse the requestXML to get the terms relationships has
	 * to be stored in database Fengqiong 20120125: no longer in use
	 * 
	 * @param requestXML
	 * @return list of TermRelationBean
	 * @throws Exception
	 */
	public ArrayList<TermRelationBean> parseRelationXml(String requestXML)
			throws Exception {
		ArrayList<TermRelationBean> tRbList = new ArrayList<TermRelationBean>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		int i = 0;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(requestXML));
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();

			NodeList relation_list = doc.getElementsByTagName("relation");
			if (relation_list.getLength() > 0) {
				for (i = 0; i < relation_list.getLength(); i++) {
					Element e_relation = (Element) relation_list.item(i);
					Element e_mainTerm = (Element) e_relation.getFirstChild();
					Element e_addTerm = (Element) e_mainTerm.getNextSibling();
					Element e_action = (Element) e_addTerm.getNextSibling();
					// Element e_type = (Element) e_action.getNextSibling();
					// //only deal with synonyms for now
					int action = 1, type = 1;
					if (e_action.getFirstChild().getNodeValue()
							.equals("remove")) {
						type = 0;
					}
					TermRelationBean trb = new TermRelationBean(e_mainTerm
							.getFirstChild().getNodeValue(), e_addTerm
							.getFirstChild().getNodeValue(), type);
					trb.setAction(action);
					tRbList.add(trb);
				}
			}
		} catch (Exception exe) {
			LOGGER.error(
					"Unable to parse the XMLStringToRelations: parseRelationXml",
					exe);
			exe.printStackTrace();
		}
		return tRbList;
	}

	/**
	 * This method return a single element value from requestXML
	 * 
	 * @param requestXML
	 * @param tag
	 * @return
	 * @throws Exception
	 */
	public String getElementFromXML(String requestXML, String tag)
			throws Exception {
		String returnValue = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(requestXML));
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName(tag);
			if (nodes.getLength() > 0) {
				Element e = (Element) nodes.item(0);
				if (e != null) {
					returnValue = e.getFirstChild().getNodeValue();
				}
			}
		} catch (Exception exe) {
			LOGGER.error("Unable to get element from XMLString", exe);
			exe.printStackTrace();
		}

		return returnValue;
	}

	public Order getOrderFromParsingXML(String requestXML) throws Exception {
		int baseOrderID = 0;
		Order order = new Order(baseOrderID, "");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(requestXML));
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();
			// get baseOrderID
			NodeList nList = doc.getElementsByTagName("baseOrderID");
			if (nList.getLength() > 0) {
				Element base = (Element) nList.item(0);
				if (base != null) {
					String id_str = base.getFirstChild().getNodeValue();
					if (id_str != null && id_str != "") {
						baseOrderID = Integer.parseInt(id_str);
						order.setbaseOrderID(baseOrderID);
					}
				}
			}
			// get new_terms
			nList = doc.getElementsByTagName("new_term");
			ArrayList<Character> new_terms = new ArrayList<Character>();
			for (int i = 0; i < nList.getLength(); i++) {
				Element term = (Element) nList.item(i);
				if (term != null) {
					new_terms.add(new Character(term.getFirstChild()
							.getNodeValue()));
				}
			}
			order.setTerms(new_terms);

			// get subOrders
			ArrayList<Order> subOrders = new ArrayList<Order>();
			nList = doc.getElementsByTagName("order");
			for (int i = 0; i < nList.getLength(); i++) {
				// get order id
				int orderID = 0;
				Element e_order = (Element) nList.item(i);
				Element e_orderid = (Element) e_order.getFirstChild();
				if (e_orderid.getFirstChild() != null) {
					System.out.println("null");
					String id_str = e_orderid.getFirstChild().getNodeValue();
					if (id_str != null && id_str != "") {
						orderID = Integer.parseInt(id_str);
					}
				}

				// get order name
				Element e_orderName = (Element) e_orderid.getNextSibling();
				// create sub-order
				Order subOrder = new Order(orderID, e_orderName.getFirstChild()
						.getNodeValue());
				// get explanation
				if (orderID == 0) {
					Element e_exp = (Element) e_orderName.getNextSibling();
					subOrder.setExplanation(e_exp.getFirstChild()
							.getNodeValue());
				}

				// get terms list
				ArrayList<Character> terms = new ArrayList<Character>();
				NodeList n_terms = e_order.getElementsByTagName("term");
				for (int j = 0; j < n_terms.getLength(); j++) {
					Element e_term = (Element) n_terms.item(j);
					Element e_name = (Element) e_term.getFirstChild();
					Element e_position = (Element) e_name.getNextSibling();
					String term_name = "";
					if (e_name != null) {
						term_name = e_name.getFirstChild().getNodeValue();
					}
					Character term = new Character(term_name);

					if (e_position != null) {
						String position = e_position.getFirstChild()
								.getNodeValue();
						if (position != null && position != "") {
							term.setDistance(Integer.parseInt(position));
						}
					}
					terms.add(term);
				}
				subOrder.setTerms(terms);
				subOrders.add(subOrder);
			}
			order.setSubOrders(subOrders);
		} catch (Exception exe) {
			LOGGER.error("Unable to get order from requestXMLString", exe);
			exe.printStackTrace();
		}
		return order;
	}

	/**
	 * This method will parse the order xml submitted by the client when saving
	 * order
	 * 
	 * @param requestXML
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> parseOrderXML(String requestXML, String tag)
			throws Exception {
		ArrayList<String> termList = new ArrayList<String>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		int i = 0;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(requestXML));
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList xmlNodes = doc.getElementsByTagName(tag);
			if (xmlNodes.getLength() > 0) {
				for (i = 0; i < xmlNodes.getLength(); i++) {
					Element e = (Element) xmlNodes.item(i);
					if (e != null) {
						String name = e.getFirstChild().getNodeValue();
						termList.add(name);
					}
				}
			}
		} catch (Exception exe) {
			LOGGER.error("Unable to parse the order XMLString", exe);
			exe.printStackTrace();
		}

		return termList;
	}

	/**
	 * This method will parse the hierarchy tree xml submitted by the client
	 * when saving the hierarchy tree
	 * 
	 * @param requestXML
	 * @return
	 * @throws Exception
	 */
	public ArrayList<StructureNodeBean> parseTreeXML(String requestXML)
			throws Exception {
		ArrayList<StructureNodeBean> nodes = new ArrayList<StructureNodeBean>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		int i = 0;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(requestXML));
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList xmlNodes = doc.getElementsByTagName("node");
			if (xmlNodes.getLength() > 0) {
				for (i = 0; i < xmlNodes.getLength(); i++) {
					Element e = (Element) xmlNodes.item(i);
					if (e != null) {
						// String test =
						// e.getFirstChild().getFirstChild().getNodeValue();
						long id = Long.parseLong(e.getFirstChild()
								.getFirstChild().getNodeValue());
						long pid = Long.parseLong(e.getFirstChild()
								.getNextSibling().getFirstChild()
								.getNodeValue());
						String name = e.getFirstChild().getNextSibling()
								.getNextSibling().getFirstChild()
								.getNodeValue();
						String keep = e.getFirstChild().getNextSibling()
								.getNextSibling().getNextSibling()
								.getFirstChild().getNodeValue();

						StructureNodeBean snb = new StructureNodeBean(id, name);
						snb.setPID(pid);
						if (keep.equals("yes")) {
							snb.setRemoveFromSrc(false);
						} else {
							snb.setRemoveFromSrc(true);
						}
						nodes.add(snb);
					}
				}
			}
		} catch (Exception exe) {
			LOGGER.error("Unable to parse the hierarchy tree XMLString", exe);
			exe.printStackTrace();
		}

		return nodes;
	}

	/**
	 * This method will parse the xml of categorizing page submitted by the
	 * client
	 * 
	 * @param requestXML
	 * @return list of CharacterGroupBean
	 * @throws Exception
	 */
	public ArrayList<CategoryBean> parseCategorizeXml(String requestXML)
			throws Exception {
		ArrayList<CategoryBean> categories = new ArrayList<CategoryBean>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		int i = 0, j = 0;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(requestXML));
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();

			// categories part
			NodeList categories_list = doc.getElementsByTagName("category");
			if (categories_list.getLength() > 0) {
				for (i = 0; i < categories_list.getLength(); i++) {
					// get the category information
					Element e_category = (Element) categories_list.item(i);
					String category_name = e_category.getFirstChild()
							.getFirstChild().getNodeValue();
					CategoryBean category = new CategoryBean(category_name);
					// ArrayList<String> termsList = new ArrayList<String>();
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
							index_e = (Element) index_e.getNextSibling();

							if (term.hasSyn() || term.isAdditional()) {
								term.setRelatedTerms(index_e.getFirstChild()
										.getNodeValue());
							} else {
								term.setRelatedTerms("");
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
				CategoryBean emptyCat = new CategoryBean("");
				Element e_emptyCategory = (Element) emptyCategories.item(0);
				NodeList terms = e_emptyCategory
						.getElementsByTagName("termName");
				;

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
		} catch (Exception exe) {
			LOGGER.error(
					"Unable to parse the Categorize XML: parseCategorizeXml",
					exe);
			exe.printStackTrace();
		}

		return categories;
	}

	public ManagerDecisionBean parseManagerDecision(String requestXML,
			String type) throws Exception {
		ManagerDecisionBean mdb = new ManagerDecisionBean();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(requestXML));
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();
			// get term name
			NodeList nodes = doc.getElementsByTagName("term");
			if (nodes.getLength() > 0) {
				Element e = (Element) nodes.item(0);
				if (e != null) {
					mdb.setTerm(e.getFirstChild().getNodeValue());
				}
			}

			if (type.equals("1") || type.equals("2") || type.equals("4")) { // category
				// get decision
				nodes = doc.getElementsByTagName("decision");
				if (nodes.getLength() > 0) {
					Element e = (Element) nodes.item(0);
					if (e != null) {
						mdb.setDecision(e.getFirstChild().getNodeValue());
					}
				}
			} else if (type.equals("3")) { // order
				// get distance
				nodes = doc.getElementsByTagName("distance");
				if (nodes.getLength() > 0) {
					Element e = (Element) nodes.item(0);
					if (e != null) {
						mdb.setDecision(e.getFirstChild().getNodeValue());
					}
				}
				// get orderID
				nodes = doc.getElementsByTagName("orderID");
				if (nodes.getLength() > 0) {
					Element e = (Element) nodes.item(0);
					if (e != null) {
						mdb.setOrderID(Integer.parseInt(e.getFirstChild()
								.getNodeValue()));
					}
				}
			}

			if (type.equals("4")) {
				nodes = doc.getElementsByTagName("category");
				if (nodes.getLength() > 0) {
					Element e = (Element) nodes.item(0);
					if (e != null) {
						mdb.setCategory(e.getFirstChild().getNodeValue());
					}
				}
			}

			// get dataset name
			nodes = doc.getElementsByTagName("dataset");
			if (nodes.getLength() > 0) {
				Element e = (Element) nodes.item(0);
				if (e != null) {
					mdb.setDataset(e.getFirstChild().getNodeValue());
				}
			}

			// get accept name
			nodes = doc.getElementsByTagName("accept");
			if (nodes.getLength() > 0) {
				Element e = (Element) nodes.item(0);
				if (e != null) {
					if (e.getFirstChild().getNodeValue().equals("y")) {
						mdb.setAccept(true);
					} else {
						mdb.setAccept(false);
					}
				}
			}
		} catch (Exception exe) {
			LOGGER.error("Unable to get element from parseManagerDecision", exe);
			exe.printStackTrace();
		}

		return mdb;
	}

	/**
	 * get type from requestXML
	 * 
	 * @param requestXML
	 * @return
	 * @throws Exception
	 */
	public String parseType(String requestXML) throws Exception {
		return getElementFromXML(requestXML, "type");
	}

	public String parseTerm(String requestXML) throws Exception {
		return getElementFromXML(requestXML, "term");
	}

	public boolean parseAccept(String requestXML) throws Exception {
		String accept = getElementFromXML(requestXML, "accept");
		if (accept.equals("y"))
			return true;
		else
			return false;
	}

	public String parseDecision(String requestXML) throws Exception {
		return getElementFromXML(requestXML, "decision");
	}

	public int parseDistance(String requestXML) throws Exception {
		String distance = getElementFromXML(requestXML, "distance");
		return Integer.parseInt(distance);
	}

	public int parseOrderID(String requestXML) throws Exception {
		String orderID = getElementFromXML(requestXML, "orderID");
		return Integer.parseInt(orderID);
	}

	public ArrayList<DecisionBean> getUserSpecificReport(User user,
			String dataset) throws SQLException, IOException {
		ReportingDBAccess rdba = new ReportingDBAccess();
		return rdba.getUserSpecificReport(user, dataset);
	}

	public static ArrayList<User> getAllUsers() throws IOException {
		ArrayList<User> users = null;
		UserDataAccess uda = new UserDataAccess();
		try {
			users = uda.getAllUsers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}

	public static String getProperty(String key) {

		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(fstream);
			} catch (IOException e) {
				LOGGER.error(
						"couldn't open file in ApplicationUtilities:getProperty",
						e);
				e.printStackTrace();
			}
		}
		return properties.getProperty(key);
	}

	/*
	 * public static void main(String[] args) { Utilities ut = new Utilities();
	 * System.out.println();
	 * 
	 * }
	 */
}
