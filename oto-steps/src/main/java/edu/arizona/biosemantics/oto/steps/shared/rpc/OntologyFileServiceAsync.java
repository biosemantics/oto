/**
 * 
 */
package edu.arizona.biosemantics.oto.steps.shared.rpc;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.*;
/**
 * @author Hong Cui
 *
 */
public interface OntologyFileServiceAsync {

	void getOntologyInfo(String userID, AsyncCallback<RPCResult<ArrayList<OntologyInfo>>> callback);
	void newEmptyOntologyFile(String userID, String uploadID,  String fileName,  String prefix, String taxonGroup, AsyncCallback<RPCResult<OntologyInfo>> callback);
	void updateOntologyFile(OntologySubmission submission, AsyncCallback<RPCResult<String>> callback);

}
