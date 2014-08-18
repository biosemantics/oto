package edu.arizona.biosemantics.oto.steps.shared.rpc;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto.steps.client.view.toontologies.OperationType;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyMatch;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyRecord;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyRecordType;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologySubmission;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.TermCategoryLists;

public interface ToOntologiesServiceAsync {

	void moveTermCategoryPair(String uploadID, String termCategoryPairID,
			boolean isRemove, AsyncCallback<Void> callback);

	void getTermCategoryLists(String uploadID,
			AsyncCallback<TermCategoryLists> callback);

	void getOntologyRecords(String uploadID, String term, String category,
			AsyncCallback<ArrayList<OntologyRecord>> callback);

	void updateSelectedOntologyRecord(String uploadID, String term,
			String category, String recordID, OntologyRecordType recordType,
			AsyncCallback<Void> callback);

	void getMatchDetail(String matchID, AsyncCallback<OntologyMatch> callback);

	void getSubmissionDetail(String submissionID,
			AsyncCallback<OntologySubmission> callback);

	void deleteSubmission(OntologySubmission submission, String uploadID,
			AsyncCallback<Void> callback);

	void submitSubmission(OntologySubmission submission, String uploadID,
			OperationType type, AsyncCallback<RPCResult<Boolean>> callback);

	void getDefaultDataForNewSubmission(String uploadID, String term,
			String category, AsyncCallback<OntologySubmission> callback);

	void clearSelection(String glossaryType, String term, String category,
			AsyncCallback<Void> callback);

	void refreshOntologyStatus(String uploadID, AsyncCallback<Void> callback);


}
