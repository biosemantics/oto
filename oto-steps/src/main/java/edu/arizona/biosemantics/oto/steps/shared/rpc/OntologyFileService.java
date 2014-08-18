/**
 * 
 */
package edu.arizona.biosemantics.oto.steps.shared.rpc;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyInfo;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologySubmission;

/**
 * @author Hong Cui
 *
 */
@RemoteServiceRelativePath("ontologyFile")
public interface OntologyFileService extends RemoteService{
	RPCResult<ArrayList<OntologyInfo>> getOntologyInfo(String userID) throws Exception;
	RPCResult<OntologyInfo> newEmptyOntologyFile(String userID, String uploadID, String fileName, String prefix, String taxonGroup) throws Exception;
	RPCResult<String> updateOntologyFile(OntologySubmission submission) throws Exception;
}
