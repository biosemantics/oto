package edu.arizona.sirls.bioportal;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.arizona.sirls.beans.OTOProvisionalTerm;
import edu.arizona.sirls.db.OTOProvisionalTermDAO;

import bioportal.beans.Filter;
import bioportal.beans.response.Entry;
import bioportal.beans.response.Relations;
import bioportal.beans.response.Success;
import bioportal.client.BioPortalClient;

public class TermsToOntologiesClient {

	private BioPortalClient bioPortalClient;
	private String bioportalUserId;

	/*
	 * private static TermsToOntologiesClient instance;
	 * 
	 * public static TermsToOntologiesClient getInstance() throws IOException {
	 * if(instance == null) instance = new TermsToOntologiesClient(); return
	 * instance; }
	 */

	public TermsToOntologiesClient(String bioportalUserId,
			String bioportalAPIKey) throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		String url = properties.getProperty("bioportalUrl");
		// String userId = properties.getProperty("bioportalUserId");
		// String apiKey = properties.getProperty("bioportalApiKey");
		this.bioportalUserId = bioportalUserId;
		bioPortalClient = new BioPortalClient(url, bioportalUserId,
				bioportalAPIKey);
	}

	/**
	 * Send a term to bioportal
	 * 
	 * @param provisionalTerm
	 * @return temporary id given to the provided provisionalTerm
	 * @throws SQLException
	 * @throws JAXBException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public String sendTerm(OTOProvisionalTerm provisionalTerm)
			throws SQLException, JAXBException, ClassNotFoundException,
			IOException, IllegalArgumentException {

		// interact with the server
		Success success = bioPortalClient
				.createProvisionalTerm(provisionalTerm);
		String temporaryId = getIdFromSuccessfulCreate(success, "id");
		// modify local database
		provisionalTerm.setTemporaryid(temporaryId);
		// provisionalTerm.setSubmittedby(bioportalUserId);

		OTOProvisionalTermDAO.getInstance()
				.addAwaitingAdoption(provisionalTerm);

		return temporaryId;
	}

	public int getPermanentIDs() throws SQLException, JAXBException,
			ClassNotFoundException, IOException {
		int count = 0;
		try {
			List<OTOProvisionalTerm> allPendingTerms = OTOProvisionalTermDAO
					.getInstance().getAllPendingAdoptions();
			for (OTOProvisionalTerm provisionalTerm : allPendingTerms) {
				String permanentId = null;
				Success success = bioPortalClient
						.getProvisionalTerm(provisionalTerm.getTemporaryid());
				List<Object> fullIdOrIdOrLabels = success.getData().getClassBean()
						.getFullIdOrIdOrLabel();
				for (Object fullIdOrIdOrLabel : fullIdOrIdOrLabels) {
					if (fullIdOrIdOrLabel instanceof Relations) {
						Relations relations = (Relations) fullIdOrIdOrLabel;
						List<Entry> entries = relations.getEntry();
						for (Entry entry : entries) {
							List<Object> objects = entry.getStringOrList();
							if (objects.size() >= 2
									&& objects.get(0).equals(
											"provisionalPermanentId")) {
								permanentId = (String) objects.get(1);
							}
						}
					}
				}

				if (permanentId == null)
					continue;
				else {
					provisionalTerm.setPermanentid(permanentId);
					OTOProvisionalTermDAO.getInstance().updatePermanentID(
							provisionalTerm, permanentId);
					count++;
				}
			}
		} catch (Exception e) {
			System.out.println("Error in checking adopted submissions: " + e);
			return -1;
		} 
		
		return count;
	}

	/**
	 * Refresh all the awaiting submissions: try to get permanent id for them
	 * 
	 * @return Map<Temporary ID, Permanent ID> of newly discovered adoptions
	 * @throws SQLException
	 * @throws JAXBException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Map<String, String> checkTermAdoptions() throws SQLException,
			JAXBException, ClassNotFoundException, IOException {
		Map<String, String> result = new HashMap<String, String>();
		List<OTOProvisionalTerm> allPendingTerms = OTOProvisionalTermDAO
				.getInstance().getAllPendingAdoptions();
		for (OTOProvisionalTerm provisionalTerm : allPendingTerms) {
			String permanentId = null;
			Success success = bioPortalClient
					.getProvisionalTerm(provisionalTerm.getTemporaryid());
			List<Object> fullIdOrIdOrLabels = success.getData().getClassBean()
					.getFullIdOrIdOrLabel();
			for (Object fullIdOrIdOrLabel : fullIdOrIdOrLabels) {
				if (fullIdOrIdOrLabel instanceof Relations) {
					Relations relations = (Relations) fullIdOrIdOrLabel;
					List<Entry> entries = relations.getEntry();
					for (Entry entry : entries) {
						List<Object> objects = entry.getStringOrList();
						if (objects.size() >= 2
								&& objects.get(0).equals(
										"provisionalPermanentId")) {
							permanentId = (String) objects.get(1);
						}
					}
				}
			}

			if (permanentId == null)
				continue;
			else {
				provisionalTerm.setPermanentid(permanentId);
				// OTOProvisionalTermDAO.getInstance().deleteAwaitingAdoption(provisionalTerm);
				// OTOProvisionalTermDAO.getInstance().storeAdopted(provisionalTerm);
				OTOProvisionalTermDAO.getInstance().updatePermanentID(
						provisionalTerm, permanentId);
				result.put(provisionalTerm.getTemporaryid(), permanentId);
			}
		}
		return result;
	}

	private String getIdFromSuccessfulCreate(Success createSuccess,
			String idName) {
		List<Object> fullIdOrIdOrLabel = createSuccess.getData().getClassBean()
				.getFullIdOrIdOrLabel();
		for (Object object : fullIdOrIdOrLabel) {
			if (object instanceof JAXBElement) {
				JAXBElement<String> possibleIdElement = (JAXBElement<String>) object;
				if (possibleIdElement.getName().toString().equals(idName)) {
					return possibleIdElement.getValue();
				}
			}
		}
		return null;
	}

	public boolean updateTerm(OTOProvisionalTerm provisionalTerm)
			throws JAXBException, SQLException, ClassNotFoundException,
			IOException {
		boolean rv = false;
		try {
			bioPortalClient.updateProvisionalTerm(provisionalTerm.getTemporaryid(),
				provisionalTerm);
			OTOProvisionalTermDAO.getInstance().updateAwaitingAdoption(
				provisionalTerm);
			rv = true;
		} catch (Exception e) {
			System.out.println("Error in deleting submission: " + e);
		} 
		return rv;
	}

	public boolean deleteTerm(OTOProvisionalTerm provisionalTerm, int userID)
			throws JAXBException, SQLException, ClassNotFoundException,
			IOException {
		boolean rv = false;
		try {
			bioPortalClient.deleteProvisionalTerm(provisionalTerm.getTemporaryid());
			OTOProvisionalTermDAO.getInstance().deleteAwaitingAdoption(
					provisionalTerm, userID);
			rv = true;
		} catch (Exception e){
			System.out.println("Error in deleting submission: " + e);
		}
		return rv;
		
	}

	public static void main(String[] args) throws IOException, JAXBException,
			SAXException, ParserConfigurationException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		String url = properties.getProperty("bioportalUrl");
		String userId = properties.getProperty("bioportalUserId");
		String apiKey = properties.getProperty("bioportalApiKey");
		BioPortalClient bioPortalClient = new BioPortalClient(url, userId,
				apiKey);

		Filter filter = new Filter();
		filter.setSubmittedBy(userId);
		String resultXML = bioPortalClient
				.getProvisionalTermsReturnString(filter);
		System.out.println(resultXML);
		
		// parse xml to get all the temporaryID
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(resultXML));
		Document doc = builder.parse(is);
		doc.getDocumentElement().normalize();

		// parse reviewed terms
		NodeList idNodes = doc.getElementsByTagName("id");
		if (idNodes.getLength() > 0) {
			for (int i = 0; i < idNodes.getLength(); i++) {
				Element e = (Element) idNodes.item(i);
				if (e != null) {
					String tmpID = e.getFirstChild().getNodeValue();
					bioPortalClient.deleteProvisionalTerm(tmpID);
					System.out.println("Deleted " + tmpID);
				}
			}
		}
	}
}
