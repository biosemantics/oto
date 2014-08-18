package edu.arizona.biosemantics.oto.steps.shared.rpc;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto.steps.shared.beans.hierarchy.Structure;
import edu.arizona.biosemantics.oto.steps.shared.beans.hierarchy.StructureNodeData;

public interface HierarchyServiceAsync {

	void getStructureList(String uploadID,
			AsyncCallback<ArrayList<Structure>> callback);

	void getNodeList(String uploadID,
			AsyncCallback<ArrayList<StructureNodeData>> callback);

	void saveTree(String uploadID, ArrayList<StructureNodeData> nodeDataList,
			AsyncCallback<Void> callback);

	void isStructureExistInOTO(String termName, String glossaryType,
			AsyncCallback<Boolean> callback);

	void addStructure(String termName, String uploadID,
			AsyncCallback<Structure> callback);

	void addStructureToOTOAndDB(String termName, String uploadID,
			String glossaryType, String definition,
			AsyncCallback<Structure> callback);

	void prepopulateTree(String uploadID, String glossaryType,
			AsyncCallback<Void> callback);

}
