package edu.arizona.biosemantics.oto.steps.server.bioportal;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

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

import edu.arizona.biosemantics.bioportal.client.BioPortalClient;
import edu.arizona.biosemantics.bioportal.client.Filter;
import edu.arizona.biosemantics.bioportal.model.ProvisionalClass;
import edu.arizona.biosemantics.oto.steps.server.db.ToOntologiesDAO;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologySubmission;

public class TermsToOntologiesClient {

	private BioPortalClient bioPortalClient;
	private String bioportalUserID;

	public TermsToOntologiesClient(String bioportalUserId,
			String bioportalAPIKey) throws IOException {
		this.bioportalUserID = bioportalUserId;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		String url = properties.getProperty("bioportalUrl");
		bioPortalClient = new BioPortalClient(url, bioportalAPIKey);
	}

	/**
	 * return how many submissions were accepted since last check
	 * 
	 * @param uploadID
	 * @param thisUploadOnly
	 * @return
	 * @throws SQLException
	 * @throws JAXBException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public int refreshSubmissionsStatus(int uploadID, boolean thisUploadOnly)
			throws SQLException, JAXBException, ClassNotFoundException,
			IOException {
		int count = 0;
		try {
			List<OntologySubmission> pendingSubmissions = ToOntologiesDAO
					.getInstance().getPendingOntologySubmissions(uploadID,
							thisUploadOnly);
			for (OntologySubmission submission : pendingSubmissions) {
				String permanentId = null;
				Future<ProvisionalClass> result = bioPortalClient.getProvisionalClass(submission.getTmpID());
				ProvisionalClass provisionalClass = result.get();
				permanentId = provisionalClass.getPermanentId();
				if (permanentId == null && !permanentId.isEmpty())
					continue;
				else {
					submission.setPermanentID(permanentId);
					ToOntologiesDAO.getInstance()
							.updatePermanentIDOfSubmission(
									Integer.parseInt(submission
											.getSubmissionID()));
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
	 * submit a term to bioportal
	 * 
	 * @param provisionalTerm
	 * @return temporary id given to the provided provisionalTerm
	 * @throws Exception
	 */
	public String submitTerm(OntologySubmission submission) throws Exception {
		String definitionToSubmit = submission.getDefinition() + " "
				+ "[this term has been used in sentence '"
				+ submission.getSampleSentence() + "' in source '"
				+ submission.getSource() + "']";
		ProvisionalClass provisionalClass = new ProvisionalClass();
		provisionalClass.setLabel(submission.getTerm());
		List<String> definitions = new LinkedList<String>();
		definitions.add(submission.getDefinition());
		provisionalClass.setDefinition(definitions);
		provisionalClass.setSubclassOf(submission.getSuperClass());
		List<String> synonyms = new LinkedList<String>();
		//need to split ?
		synonyms.add(submission.getSynonyms());
		provisionalClass.setSynonym(synonyms);
		List<String> ontologies = new LinkedList<String>();
		ontologies.add(submission.getOntologyID());
		provisionalClass.setOntology(ontologies);
		provisionalClass.setCreator(bioportalUserID);
		
		// interact with the server
		Future<ProvisionalClass> result = bioPortalClient.postProvisionalClass(provisionalClass);
		String temporaryId = result.get().getId();
		
		// modify local database
		submission.setTmpID(temporaryId);
		return temporaryId;
	}

	public void updateTerm(OntologySubmission submission) throws JAXBException,
			SQLException, ClassNotFoundException, IOException {
		String definitionToSubmit = submission.getDefinition() + " "
				+ "[this term has been used in sentence '"
				+ submission.getSampleSentence() + "' in source '"
				+ submission.getSource() + "']";
		ProvisionalClass provisionalClass = new ProvisionalClass();
		provisionalClass.setId(submission.getTmpID());
		provisionalClass.setLabel(submission.getTerm());
		List<String> definitions = new LinkedList<String>();
		definitions.add(submission.getDefinition());
		provisionalClass.setDefinition(definitions);
		provisionalClass.setSubclassOf(submission.getSuperClass());
		List<String> synonyms = new LinkedList<String>();
		//need to split ?
		synonyms.add(submission.getSynonyms());
		provisionalClass.setSynonym(synonyms);
		List<String> ontologies = new LinkedList<String>();
		ontologies.add(submission.getOntologyID());
		provisionalClass.setOntology(ontologies);
		provisionalClass.setCreator(bioportalUserID);
		
		bioPortalClient.patchProvisionalClass(provisionalClass);
	}

	public void deleteTerm(OntologySubmission submission) throws Exception {
		bioPortalClient.deleteProvisionalClass(submission.getTmpID());
	}

	public static void main(String[] args) throws IOException, JAXBException,
			SAXException, ParserConfigurationException {
		/*ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		String url = properties.getProperty("bioportalUrl");
		String userId = properties.getProperty("bioportalUserId");
		String apiKey = properties.getProperty("bioportalApiKey");
		BioPortalClient bioPortalClient = new BioPortalClient(url, apiKey);

		Filter filter = new Filter();
		filter.setSubmittedBy(userId);
		String resultXML = bioPortalClient
				.getProvisionalTermsReturnString(filter);
		System.out.println(resultXML);

		boolean deleteAll = true;
		if (deleteAll) {
			// parse xml to get all the temporaryID
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
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
		*/
	}
}
