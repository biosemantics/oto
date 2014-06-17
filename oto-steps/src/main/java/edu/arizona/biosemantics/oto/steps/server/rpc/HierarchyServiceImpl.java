package edu.arizona.biosemantics.oto.steps.server.rpc;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto.steps.client.rpc.HierarchyService;
import edu.arizona.biosemantics.oto.steps.server.db.HierarchyDAO;
import edu.arizona.biosemantics.oto.steps.server.oto.QueryOTO;
import edu.arizona.biosemantics.oto.steps.shared.beans.hierarchy.Structure;
import edu.arizona.biosemantics.oto.steps.shared.beans.hierarchy.StructureNodeData;

public class HierarchyServiceImpl extends RemoteServiceServlet implements
		HierarchyService {

	private static final long serialVersionUID = -8088784352943361785L;

	@Override
	public ArrayList<Structure> getStructureList(String uploadID)
			throws Exception {
		return HierarchyDAO.getInstance().getStructuresList(
				Integer.parseInt(uploadID));
	}

	@Override
	public ArrayList<StructureNodeData> getNodeList(String uploadID)
			throws Exception {
		return HierarchyDAO.getInstance().getNodeList(
				Integer.parseInt(uploadID));
	}

	@Override
	public void saveTree(String uploadID,
			ArrayList<StructureNodeData> nodeDataList) throws Exception {
		HierarchyDAO.getInstance().saveTree(uploadID, nodeDataList);
	}

	@Override
	public boolean isStructureExistInOTO(String termName, String glossaryType)
			throws Exception {
		boolean result = QueryOTO.getInstance().isTripleExistInOTO(termName,
				"structure", glossaryType);
		return result;
	}

	@Override
	public Structure addStructure(String termName, String uploadID)
			throws Exception {
		return HierarchyDAO.getInstance().addStructure(uploadID, termName);
	}

	@Override
	public Structure addStructureToOTOAndDB(String termName, String uploadID,
			String glossaryType, String definition) throws Exception {
		QueryOTO.getInstance().insertTripleToOTO(termName, "structure",
				glossaryType, definition);
		return HierarchyDAO.getInstance().addStructure(uploadID, termName);
	}

	@Override
	public void prepopulateTree(String uploadID, String glossaryType)
			throws Exception {
		HierarchyDAO.getInstance().prepopulateTree(uploadID,
				Integer.parseInt(glossaryType));

	}

}
