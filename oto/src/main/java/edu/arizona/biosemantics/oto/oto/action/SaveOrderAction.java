package edu.arizona.biosemantics.oto.oto.action;

import java.io.StringReader;
import java.sql.SQLException;
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
import edu.arizona.biosemantics.oto.oto.beans.Character;
import edu.arizona.biosemantics.oto.oto.beans.Order;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

/**
 * save order action - AJAX
 * 
 * @author Fengqiong
 * 
 */
public class SaveOrderAction extends ParserAction {
	private static final Logger LOGGER = Logger
			.getLogger(SaveOrderAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception, SQLException {
		// TODO Auto-generated method stub
		if (checkSessionValidity(request)) {
			GeneralForm gform = (GeneralForm) form;
			String requestXML = gform.getValue();
			SessionDataManager sessionDataMgr = getSessionManager(request);
			String dataPrefix = sessionDataMgr.getDataset();
			User user = sessionDataMgr.getUser();
			try {
				CharacterDBAccess cdba = new CharacterDBAccess();
				Order order = getOrderFromParsingXML(requestXML);
				String rValue = cdba.saveOrder(order, dataPrefix, user);
				response.setContentType("text/xml");
				response.getWriter().write(
						"<server-response>" + rValue + "</server-response>");
			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in saving order", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				// return mapping.findForward(Forwardable.ERROR);
				response.setContentType("text/xml");
				response.getWriter().write(
						"<server-response>Problem encountered in saving the order : "
								+ exe.getMessage() + "</server-response>");
			}

			// return mapping.findForward(Forwardable.LOGON);
			// return mapping.findForward(Forwardable.RELOAD);
			return null;
		} else {
			// return mapping.findForward(Forwardable.LOGON);
			return null;
		}
	}

	private Order getOrderFromParsingXML(String requestXML) throws Exception {
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
}
