package edu.arizona.biosemantics.oto.steps.server.rpc;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto.steps.client.rpc.ToOntologiesService;
import edu.arizona.biosemantics.oto.steps.client.view.toontologies.OperationType;
import edu.arizona.biosemantics.oto.steps.server.bioportal.TermsToOntologiesClient;
import edu.arizona.biosemantics.oto.steps.server.db.GeneralDAO;
import edu.arizona.biosemantics.oto.steps.server.db.ToOntologiesDAO;
import edu.arizona.biosemantics.oto.steps.server.oto.QueryOTO;
import edu.arizona.biosemantics.oto.steps.server.utilities.Utilities;
import edu.arizona.biosemantics.oto.steps.shared.beans.UploadInfo;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyMatch;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyRecord;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyRecordType;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologySubmission;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.TermCategoryLists;

public class ToOntologiesServiceImpl extends RemoteServiceServlet implements
		ToOntologiesService {

	private static final long serialVersionUID = 8235809276166612584L;

	@Override
	public void moveTermCategoryPair(String uploadID,
			String termCategoryPairID, boolean isRemove) throws Exception {
		ToOntologiesDAO.getInstance().moveTermCategoryPair(
				Integer.parseInt(uploadID),
				Integer.parseInt(termCategoryPairID), isRemove);
	}

	@Override
	public TermCategoryLists getTermCategoryLists(String uploadID)
			throws Exception {
		return ToOntologiesDAO.getInstance().getTermCategoryPairsLists(
				Integer.parseInt(uploadID));
	}

	@Override
	public ArrayList<OntologyRecord> getOntologyRecords(String uploadID,
			String term, String category) throws Exception {
		return ToOntologiesDAO.getInstance().getOntologyRecords(
				Integer.parseInt(uploadID), term, category);
	}

	@Override
	public void updateSelectedOntologyRecord(String uploadID, String term,
			String category, String recordID, OntologyRecordType recordType)
			throws Exception {
		ToOntologiesDAO.getInstance().updateSelectedOntologyRecord(
				Integer.parseInt(uploadID), term, category, recordType,
				Integer.parseInt(recordID));
	}

	@Override
	public OntologyMatch getMatchDetail(String matchID) throws Exception {
		return ToOntologiesDAO.getInstance().getOntologyMatchByID(
				Integer.parseInt(matchID));
	}

	@Override
	public OntologySubmission getSubmissionDetail(String submissionID)
			throws Exception {
		return ToOntologiesDAO.getInstance().getOntologySubmissionByID(
				Integer.parseInt(submissionID));
	}

	@Override
	public void deleteSubmission(OntologySubmission submission, String uploadID)
			throws Exception {
		UploadInfo info = GeneralDAO.getInstance().getUploadInfo(
				Integer.parseInt(uploadID));
		TermsToOntologiesClient sendToOntologyClient = new TermsToOntologiesClient(
				info.getBioportalUserID(), info.getBioportalApiKey());
		sendToOntologyClient.deleteTerm(submission);
		ToOntologiesDAO.getInstance().deleteSubmission(
				Integer.parseInt(submission.getSubmissionID()));
	}

	@Override
	public void submitSubmission(OntologySubmission submission,
			String uploadID, OperationType type) throws Exception {
		UploadInfo info = GeneralDAO.getInstance().getUploadInfo(
				Integer.parseInt(uploadID));
		TermsToOntologiesClient sendToOntologyClient = new TermsToOntologiesClient(
				info.getBioportalUserID(), info.getBioportalApiKey());
		if (type.equals(OperationType.NEW_SUBMISSION)) {
			// get uuid first
			submission.setLocalID(QueryOTO.getInstance().getUUID(
					submission.getTerm(), submission.getCategory(),
					Utilities.getGlossaryNameByID(info.getGlossaryType()),
					submission.getDefinition()));

			// submit to bioportal
			String tmpID = sendToOntologyClient.submitTerm(submission);
			submission.setTmpID(tmpID);

			// insert record to database
			ToOntologiesDAO.getInstance().addSubmission(submission,
					Integer.parseInt(uploadID));
		} else {
			sendToOntologyClient.updateTerm(submission);
			ToOntologiesDAO.getInstance().updateSubmission(submission);
		}
	}

	@Override
	public OntologySubmission getDefaultDataForNewSubmission(String uploadID,
			String term, String category) throws Exception {
		return ToOntologiesDAO.getInstance().getDefaultDataForNewSubmission(
				Integer.parseInt(uploadID), term, category);
	}

	@Override
	public void clearSelection(String glossaryType, String term, String category)
			throws Exception {
		ToOntologiesDAO.getInstance().clearSelection(
				Integer.parseInt(glossaryType), term, category);

	}

	@Override
	public void refreshOntologyStatus(String uploadID) throws Exception {
		/**
		 * update matches
		 */
		ToOntologiesDAO.getInstance().refreshStatusOfMatches(
				Integer.parseInt(uploadID));

		/**
		 * update submissions if has bioportal info associated with this upload
		 */
		// check if has bioportal info
		UploadInfo info = GeneralDAO.getInstance().getUploadInfo(
				Integer.parseInt(uploadID));
		String bioportalUser = info.getBioportalUserID();
		String bioportalApiKey = info.getBioportalApiKey();
		if (info.getBioportalApiKey() != null && !bioportalApiKey.equals("")
				&& info.getBioportalUserID() != null
				&& !bioportalUser.equals("")) {
			TermsToOntologiesClient bioportalClient = new TermsToOntologiesClient(
					info.getBioportalUserID(), info.getBioportalApiKey());
			bioportalClient.refreshSubmissionsStatus(
					Integer.parseInt(uploadID), true);
		}
	}

}
