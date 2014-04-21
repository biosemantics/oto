package edu.arizona.biosemantics.oto.lite.action;

import java.io.StringReader;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
import edu.arizona.biosemantics.oto.lite.beans.Category;
import edu.arizona.biosemantics.oto.lite.beans.DecisionHolder;
import edu.arizona.biosemantics.oto.lite.beans.Term;
import edu.arizona.biosemantics.oto.lite.db.CategorizationDBAccess;
import edu.arizona.biosemantics.oto.lite.form.GeneralForm;

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

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GeneralForm gform = (GeneralForm) form;
		String requestXML = gform.getValue();

		try {
			DecisionHolder dh = parseGroupingXML(requestXML);
			HttpSession session = request.getSession();
			session.setAttribute("uploadID", dh.getUploadID());
			request.setAttribute("uploadID", dh.getUploadID());

			if (dh.getNew_categories().size() > 0) {
				CategorizationDBAccess.getInstance().addNewCategory(
						dh.getNew_categories(), dh.getUploadID());
			}

			// save categorizing decisions
			boolean success = CategorizationDBAccess.getInstance()
					.saveCategorizingDecisions(dh.getRegular_categories(),
							dh.getUploadID());
			response.setContentType("text/xml");
			if (success) {
				response.getWriter().write("success");
			} else {
				response.getWriter().write("failed");
			}

			// this can be done at uploading part
			CategorizationDBAccess.getInstance().cleanUpUploads();
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Error in saving the decisions", exe);
			request.setAttribute(Forwardable.ERROR, exe.getCause());
			return mapping.findForward(Forwardable.ERROR);
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
