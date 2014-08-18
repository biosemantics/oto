package edu.arizona.biosemantics.oto.steps.shared.rpc;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.oto.steps.shared.beans.hierarchy.Structure;
import edu.arizona.biosemantics.oto.steps.shared.beans.hierarchy.StructureNodeData;

@RemoteServiceRelativePath("hierarchy")
public interface HierarchyService extends RemoteService {
	ArrayList<Structure> getStructureList(String uploadID) throws Exception;

	ArrayList<StructureNodeData> getNodeList(String uploadID) throws Exception;

	void saveTree(String uploadID, ArrayList<StructureNodeData> nodeDataList)
			throws Exception;

	boolean isStructureExistInOTO(String termName, String glossaryType)
			throws Exception;

	Structure addStructure(String termName, String uploadID) throws Exception;

	Structure addStructureToOTOAndDB(String termName, String uploadID,
			String glossaryType, String definition) throws Exception;

	void prepopulateTree(String uploadID, String glossaryType) throws Exception;
}
