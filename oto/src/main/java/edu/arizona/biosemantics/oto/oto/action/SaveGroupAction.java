package edu.arizona.biosemantics.oto.oto.action;
import java.io.StringReader;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.beans.CategoryBean;
import edu.arizona.biosemantics.oto.oto.beans.DecisionHolder;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.Term;
import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;


/**
 * This class saves the group information along with the decision to the
 * database
 * 
 * @author Partha
 * 
 */
public class SaveGroupAction extends ParserAction {

	private static final Logger LOGGER = Logger
			.getLogger(SaveGroupAction.class);
	/* Create the strategy hook */
	//private GroupSelectionStrategy gStrategy = new GroupSelectionStrategy();

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (checkSessionValidity(request)) {
			GeneralForm gform = (GeneralForm) form;
			String requestXML = gform.getValue();
			if (requestXML == null) {
				return mapping.findForward(Forwardable.RELOAD);
			}
			
			//testing
			//requestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><decisions><category><category_name>coloration</category_name><new_terms><term>lavender</term><term>pink</term><term>purple</term></new_terms></category><category><category_name>count</category_name><changed_terms><term>tan</term></changed_terms></category><relation><mainTerm>denticulate</mainTerm><addTerm>dentate</addTerm><action>add</action><type>syn</type></relation><removed_decisions><term>glabrous</term></removed_decisions></decisions>";
			SessionDataManager sessionDataMgr = getSessionManager(request);
			String dataPrefix = sessionDataMgr.getDataset();
			User user = sessionDataMgr.getUser();
			try {
				DecisionHolder dh = parseGroupingXML(requestXML);
				CharacterDBAccess cdba = new CharacterDBAccess();
				
				/*//check if this action is a resend
				boolean isResend = false;
				if (categories.size() > 0) {
					isResend = cdba.isResendingGroup(dataPrefix, user, categories.get(0), null);
				} else if (trbList.size() > 0) {
					isResend = cdba.isResendingGroup(dataPrefix, user, null, trbList.get(0));
				}*/
				
				if (dh.getNew_categories().size() > 0) {
					cdba.addNewCategory(dh.getNew_categories(), dataPrefix);
				}
				
				if (dh.getReviewed_terms().size() > 0) {
					cdba.saveReviewedTerms(dh.getReviewed_terms(), dataPrefix, user);
				}
				
				//save categorizing decisions
				cdba.saveCategorizingDecisions(dh.getRegular_categories(), dataPrefix, user);

				request.setAttribute("message", "Changes have been successfully saved!");
			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in saving the group", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				return mapping.findForward(Forwardable.ERROR);
			}
			return mapping.findForward(Forwardable.RELOAD);
		} else {
			return mapping.findForward(Forwardable.LOGON);
		}

	}

	private DecisionHolder parseGroupingXML(String requestXML) throws Exception {
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

}
