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
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.StructureNodeBean;
import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

/**
 * save hierarchy tree action - directly save to database and reload page.
 * @author Fengqiong
 *
 */
public class SaveTreeAction extends ParserAction {
	private static final Logger LOGGER = Logger.getLogger(SaveTreeAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (checkSessionValidity(request)) {
			GeneralForm gform = (GeneralForm) form;
			String requestXML = gform.getValue();
			SessionDataManager sessionDataMgr = getSessionManager(request);
			String dataPrefix = sessionDataMgr.getDataset();
			User user = sessionDataMgr.getUser();
			try {
				//parse requestXML and save to db
				if (requestXML != null) {
					ArrayList<StructureNodeBean> nodes = parseTreeXML(requestXML);
					CharacterDBAccess cdba = new CharacterDBAccess();
					
					boolean isResend = cdba.isResendingTree(dataPrefix, user, nodes);
					if (!isResend) {
						cdba.saveHierarchyTree(nodes, dataPrefix, user);
					}	
				}
				
				request.setAttribute("message", "Data saved successfully!");

			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in saving the hierarchy tree", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				return mapping.findForward(Forwardable.ERROR);
			}

			return mapping.findForward(Forwardable.RELOAD);
		} else {
			return mapping.findForward(Forwardable.LOGON);
		}
	}
	
	private ArrayList<StructureNodeBean> parseTreeXML(String requestXML)
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

}
