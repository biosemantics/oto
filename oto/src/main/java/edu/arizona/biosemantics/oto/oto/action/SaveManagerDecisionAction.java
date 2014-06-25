package edu.arizona.biosemantics.oto.oto.action;

import java.io.StringReader;

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

import edu.arizona.biosemantics.oto.oto.beans.ManagerDecisionBean;
import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class SaveManagerDecisionAction extends ParserAction {
	private static final Logger LOGGER = Logger
			.getLogger(SetDatasetPrivacyAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (checkSessionValidity(request)) {
			GeneralForm gform = (GeneralForm) form;
			String requestXML = gform.getValue();
			try {
				String type = parseType(requestXML);
				CharacterDBAccess cdba = new CharacterDBAccess();
				ManagerDecisionBean mdb = parseManagerDecision(requestXML, type);
				boolean success = false;
				// parse type
				if (type.equals("1")) {
					success = cdba.confirmCategory(mdb);
				} else if (type.equals("2")) {
					success = cdba.confirmPath(mdb);
				} else if (type.equals("3")) {
					success = cdba.confirmOrder(mdb);
				} else if (type.equals("4")) {
					success = cdba.confirmSynonym(mdb);
				} else {
					// do nothing
				}

				response.setContentType("text/xml");
				if (success) {
					response.getWriter().write(
							"<response>Saved successfully!</response>");
				} else {
					response.getWriter().write(
							"<response>ERROR: Problem encountered in saving the decision. "
									+ "Plese try again later. </response>");
				}

			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in saving decision by manager", exe);
				response.setContentType("text/xml");
				response.getWriter()
						.write("<response>"
								+ "ERROR: Problem encountered in saving the decision : "
								+ exe + "</response>");
			}
			return null;
		} else {
			return null;
		}
	}
	
	
	private ManagerDecisionBean parseManagerDecision(String requestXML,
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

	private String parseType(String xml) {
		return getElementFromXML(xml, "type");
	}

	private String getElementFromXML(String xml, String tag) {
		String returnValue = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
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


}
